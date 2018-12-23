package com.kuaixiu.shop.service;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.XLSTransformer;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.common.base.service.BaseService;
import com.common.exception.SystemException;
import com.common.importExcel.ExcelUtil;
import com.common.importExcel.ImportError;
import com.common.importExcel.ImportReport;
import com.common.paginate.Page;
import com.common.util.BaiduMapUtil;
import com.common.util.ConverterUtil;
import com.common.util.SmsSendUtil;
import com.kuaixiu.brand.entity.Brand;
import com.kuaixiu.brand.service.BrandService;
import com.kuaixiu.brand.service.NewBrandService;
import com.kuaixiu.engineer.entity.Engineer;
import com.kuaixiu.engineer.service.EngineerService;
import com.kuaixiu.oldtonew.entity.NewOrder;
import com.kuaixiu.order.entity.Order;
import com.kuaixiu.shop.dao.ShopMapper;
import com.kuaixiu.shop.entity.Shop;
import com.kuaixiu.shop.entity.ShopModel;
import com.system.basic.address.entity.Address;
import com.system.basic.address.service.AddressService;
import com.system.basic.sequence.util.SeqUtil;
import com.system.basic.user.entity.SessionUser;
import com.system.basic.user.service.SysUserService;
import com.system.constant.SystemConstant;

/**
 * Shop Service
 * @CreateDate: 2016-08-26 上午02:22:49
 * @version: V 1.0
 */
@Service("shopService")
public class ShopService extends BaseService<Shop> {
    private static final Logger log= Logger.getLogger(ShopService.class);

    @Autowired
    private ShopMapper<Shop> mapper;
    @Autowired
    private AddressService addressService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private EngineerService engService;
    @Autowired
    private ShopModelService shopModelService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private NewBrandService newBrandService;
    @Autowired
    private NewShopModelService newShopModelService;
    
    public ShopMapper<Shop> getDao() {
        return mapper;
    }

    //**********自定义方法***********
    
    /**
     * 根据账号查询
     * @param code
     * @return
     * @CreateDate: 2016-9-6 下午11:57:44
     */
    public Shop queryByCode(String code){
        return getDao().queryByCode(code);
    }
    
    /**
     * 查询门店总数
     * @return
     * @CreateDate: 2016-9-4 下午4:21:29
     */
    public int queryAllCount(){
        return getDao().queryCount(null);
    }
    
    /**
     * 根据连锁商查询门店个数
     * @param proCode
     * @return
     * @CreateDate: 2016-9-4 下午4:21:29
     */
    public int queryCountByProviderCode(String proCode){
        Shop s = new Shop();
        s.setProviderCode(proCode);
        return getDao().queryCount(s);
    }
    
    /**
     * 根据连锁商查询门店
     * @param proCode
     * @return
     * @CreateDate: 2016-9-4 下午4:21:29
     */
    public List<Shop> queryByProviderCode(String proCode){
        Shop s = new Shop();
        s.setProviderCode(proCode);
        return getDao().queryList(s);
    }

    /**
     * 保存门店商
     * 1、获取坐标
     * 2、获取省份名称首字母
     * 3、生成账号
     * 4、创建登录用户
     * 5、保存
     * @param s
     * @return
     * @CreateDate: 2016-9-4 下午10:51:15
     */
    @Transactional
    public int save(Shop s, SessionUser su){
        //根据地址获取坐标
        JSONObject json = BaiduMapUtil.getLatAndLngByAddr(s.getFullAddress());
        // 设置坐标
        s.setLongitude(new BigDecimal(json.getFloat("lng")));
        s.setLatitude(new BigDecimal(json.getFloat("lat")));
        
        //获取省份名称首字母
        Address province = addressService.queryByAreaId(s.getProvince());
        String fristSpell = ConverterUtil.getFirstSpellForAreaName(province.getArea());
        //获取市名称首字母
        Address city = addressService.queryByAreaId(s.getCity());
        fristSpell += ConverterUtil.getFirstSpellForAreaName(city.getArea());
        //获取账号
        String code = SeqUtil.getNext(fristSpell);
        s.setCode(code);
        
        if(s.getBrands() != null && s.getBrands().length > 0){
	        Map<String, Object> params = new HashMap<>();
	        params.put("shopCode", code);
	        params.put("brands", s.getBrands());
	        shopModelService.addBatch(params);
        }
        //添加门店支持兑换手机品牌
   /*
        if(s.getNewBrands() != null && s.getNewBrands().length > 0){
	        Map<String, Object> params = new HashMap<>();
	        params.put("shopCode", code);
	        params.put("brands", s.getNewBrands());
	        newShopModelService.addBatch(params);
        }
     */   
        //创建登录用户
        String m = s.getManagerMobile();
        sysUserService.createUser(code, m.substring(m.length() - 6), code, s.getName(), 
                SystemConstant.USER_TYPE_SHOP, su.getUserId());
        
        int rest = getDao().add(s);
        
        //发送短信
        try{
            SmsSendUtil.sendAccountAndPasswd(m, code, m.substring(m.length() - 6));
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return rest;
    }
    
    /**
     * 更新商店
     * @param p
     * @return
     * @CreateDate: 2016-8-31 下午7:08:33
     */
    @Transactional
    public int update(Shop s, SessionUser su){
        if(s == null || StringUtils.isBlank(s.getId())){
            throw new SystemException("参数为空，无法更新");
        }
        Shop t = getDao().queryById(s.getId());
        if(!t.getName().equals(s.getName())){
            //如果名称发送改变则修改登录用户名称
            sysUserService.resetUserName(t.getCode(), s.getName(), su.getUserId());
        }
        t.setName(s.getName());
        
        //修改门店地址转换坐标
        JSONObject lal = BaiduMapUtil.getLatAndLngByAddr(s.getFullAddress());
        t.setLongitude(new BigDecimal(lal.getFloat("lng")));
        t.setLatitude(new BigDecimal(lal.getFloat("lat")));
        
        shopModelService.deleteByShopCode(t.getCode());
        if(s.getBrands() != null && s.getBrands().length > 0){
        	Map<String, Object> params = new HashMap<>();
        	params.put("shopCode", t.getCode());
        	params.put("brands", s.getBrands());
        	shopModelService.addBatch(params);
        }
        
        //修改门店支持兑换手机品牌
   /*     newShopModelService.deleteByShopCode(t.getCode());
        if(s.getNewBrands() != null && s.getNewBrands().length > 0){
	        Map<String, Object> params = new HashMap<>();
	        params.put("shopCode", t.getCode());
	        params.put("brands", s.getNewBrands());
	        newShopModelService.addBatch(params);
        }
     */   
        
        if(!t.getProviderCode().equals(s.getProviderCode())){
            //修改商店所属连锁商时同时修改维修工程师的所属连锁商
            t.setProviderCode(s.getProviderCode());
            List<Engineer> engs = engService.queryByShopCode(t.getCode());
            if(engs != null && engs.size() > 0){
                for(Engineer e : engs){
                    e.setProviderCode(t.getProviderCode());
                    engService.update(e);
                }
            }
        }
        t.setManagerName(s.getManagerName());
        t.setManagerMobile(s.getManagerMobile());
        t.setManagerMobile1(s.getManagerMobile1());
        t.setManagerMobile2(s.getManagerMobile2());
        t.setTel(s.getTel());
        t.setProvince(s.getProvince());
        t.setCity(s.getCity());
        t.setCounty(s.getCounty());
        t.setStreet(s.getStreet());
        t.setAreas(s.getAreas());
        t.setAddress(s.getAddress());
        t.setUpdateUserid(su.getUserId());
        t.setOldToNew(s.getOldToNew());
        t.setIsSendRepair(s.getIsSendRepair());
        return getDao().update(t);
    }
    
    /**
     * 更新派单模式
     * @param p
     * @return
     * @CreateDate: 2016-8-31 下午7:08:33
     */
    public int updateDispatchType(String id, int dispatchType){
        return getDao().updateDispatchType(id, dispatchType);
    }
    
    /**
     * 删除连接商
     * @param p
     * @return
     * @CreateDate: 2016-8-31 下午7:09:59
     */
    public int delete(Shop s){
        if(s == null || StringUtils.isBlank(s.getId())){
            throw new SystemException("参数为空，无法更新");
        }
        Shop t = getDao().queryById(s.getId());
        t.setIsDel(1);
        t.setUpdateUserid(s.getUpdateUserid());
        return getDao().update(t);
    }
    
    /**
     * 删除连接商
     * @param p
     * @return
     * @CreateDate: 2016-8-31 下午7:09:59
     */
    public int deleteById(String idStr, SessionUser su){
        if(StringUtils.isBlank(idStr)){
            throw new SystemException("参数为空，无法更新");
        }
        //处理批量操作
        String[] ids = idStr.split(",");
        for(String id : ids){
            Shop t = getDao().queryById(id);
            //如果该门店有工程师正在进行订单维修 则不允许删除
            Engineer eng=new Engineer();
            eng.setShopCode(t.getCode());
            eng.setIsDispatch(1);
            List<Engineer> list=engService.queryList(eng);
            if(list.size()>0){
            	throw new SystemException("该门店还有维修订单正在进行，不能删除");
            }
            t.setIsDel(1);
            t.setUpdateUserid(su.getUserId());
            getDao().update(t);
            //门店删除后  将该门店下的工程师也删除
            Engineer e=new Engineer();
            e.setShopCode(t.getCode());
            List<Engineer> engList=engService.queryList(e);
            for(Engineer engineer:engList){
            	engineer.setIsDel(1);
            	engService.saveUpdate(engineer);
            }
        }
        return 1;
    }
    
    /**
     * 根据经纬度查询最近的维修门店
     * 用于派单使用
     * @param lon
     * @param lat
     * @return
     * @CreateDate: 2016-9-15 下午5:27:48
     */
    public List<Shop> queryIdleShopByLonAndLat(Order o){
    	List<Shop> list = new ArrayList<>();
    	list.addAll(getDao().queryByOrderWithEmptyEng(o));
    	list.addAll(getDao().queryByOrder(o));
        return list;
    }
    
    /**
     * 根据经纬度查询最近的兑换门店
     * 用于派单使用
     *
     */
    public List<Shop> queryShopByLonAndLat(NewOrder o){
    	List<Shop> list = new ArrayList<>();
    	list.addAll(getDao().queryByNewOrderWithEmptyEng(o));
    	list.addAll(getDao().queryByNewOrder(o));
        return list;
    }
    
    
    /**
     * 查询经纬度附近有没有维修门店
     * 保存订单使用
     * @param lon
     * @param lat
     * @return
     * @CreateDate: 2016-9-15 下午5:27:48
     */
    public boolean checkShopByLonAndLat(BigDecimal lon, BigDecimal lat){
        Shop shop = new Shop();
        shop.setLongitude(lon);
        shop.setLatitude(lat);
        Page page = new Page();
        page.setPageSize(1);
        page.setStart(0);
        shop.setPage(page);
        List<Shop> list = getDao().queryByLonAndLatForPage(shop);
        return list != null && list.size() > 0;
    }
    
    /**
     * 查询经纬度附近有没有支持以旧换新功能的维修门店
     * 保存订单使用
     */
    public boolean checkShopOldToNew(BigDecimal lon, BigDecimal lat){
        Shop shop = new Shop();
        shop.setLongitude(lon);
        shop.setLatitude(lat);
        shop.setOldToNew(0);
        Page page = new Page();
        page.setPageSize(1);
        page.setStart(0);
        shop.setPage(page);
        List<Shop> shopList = getDao().queryNewByLonAndLatForPage(shop);
        return shopList != null && shopList.size() > 0;
    }
    /**
     * 根据订单检查经纬度附近有没有维修门店区分品牌
     * 保存订单使用
     * @param lon
     * @param lat
     * @return
     * @CreateDate: 2016-9-15 下午5:27:48
     */
    public boolean checkShopModelByLonAndLat(Order o){
        Page page = new Page();
        page.setPageSize(1);
        page.setStart(0);
        o.setPage(page);
        List<Shop> list = getDao().queryByOrderForPage(o);
        return list != null && list.size() > 0;
    }
    
    /**
     * 根据订单检查经纬度附近有没有兑换门店区分品牌
     * 保存订单使用
     */
    public boolean checkShopNewModelByLonAndLat(NewOrder o){
        Page page = new Page();
        page.setPageSize(1);
        page.setStart(0);
        o.setPage(page);
        List<Shop> list = getDao().queryByNewOrderForPage(o);
        return list != null && list.size() > 0;
    }
    
    /**
     * 根据IP地址查询最近的维修门店 分页
     * 用于到店维修展示维修门店列表 
     * @param ip IP地址
     * @return
     * @CreateDate: 2016-9-15 下午5:00:21
     */
    public List<Shop> queryByIPForPage(String ip, Page page){
        //根据IP地址获取坐标
        JSONObject json = BaiduMapUtil.getLatAndLngByIP(ip);
        // 设置坐标
        Shop shop = new Shop();
        shop.setLongitude(new BigDecimal(json.getString("x")));
        shop.setLatitude(new BigDecimal(json.getString("y")));
    //     shop.setLongitude(new BigDecimal("116.280392"));
    //     shop.setLatitude(new BigDecimal("40.104613"));
        shop.setPage(page);
        return getDao().queryByLonAndLatForPage(shop);
    }
    
    /**
     * 根据经纬度查询最近的维修门店 分页
     * 用于到店维修展示维修门店列表 附近暂不使用
     * @param lon 经度
     * @param lat 维度
     * @return
     * @CreateDate: 2016-9-15 下午5:00:21
     */
    public List<Shop> queryByLonAndLatForPage(BigDecimal lon, BigDecimal lat, Page page){
        Shop shop = new Shop();
        shop.setLongitude(lon);
        shop.setLatitude(lat);
        shop.setPage(page);
        return getDao().queryByLonAndLatForPage(shop);
    }
    
    /**
     * 门店商导入主入口
     */
    @Transactional
    public void importExcel(MultipartFile file, ImportReport report,SessionUser su){
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        Workbook workbook = null;
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            // 根据后缀实例化，xls实例化HSSFWorkbook,xlsx实例化XSSFWorkbook
            if (extension.equalsIgnoreCase("xls")){
                workbook = new HSSFWorkbook(inputStream);
            }
            else {
                workbook = new XSSFWorkbook(inputStream);
            }
            //检查模板是否正确
            if(checkExcelModel(workbook,report)){
                //检查表格数据
                List<Shop> list=checkData(workbook, report, su);
                if(report.isPass() && list.size() > 0){
                    //保存数据
                    saveData(list, su);
                    //发送短信
                    sendSms(list);
                }
            }
            else{
                report.setContinueNext(false);
                report.setPass(false);
            }
        }
        catch (SystemException e) {
            e.printStackTrace();
            report.setPass(false);
            report.setContinueNext(false);
            report.setError("导入错误："+ e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            report.setPass(false);
            report.setContinueNext(false);
            report.setError("系统异常请联系管理员");
        }
        finally{
            if(inputStream!=null){
                try {
                    inputStream.close();
                } 
                catch (IOException e) {
                
                }
            }
        }
    }
    
    /**
     * 保存门店商
     * @param m
     * @param repairCosts
     * @param su
     * @return
     * @CreateDate: 2016-9-3 上午12:43:26
     */
    @Transactional
    public void saveData(List<Shop> list, SessionUser su){
        for(Shop p : list){
        	
        	if(p.getBrands() != null && p.getBrands().length > 0){
    	        Map<String, Object> params = new HashMap<>();
    	        params.put("shopCode", p.getCode());
    	        params.put("brands", p.getBrands());
    	        shopModelService.addBatch(params);
            }
            //创建登录用户
            String m = p.getManagerMobile();
            sysUserService.createUser(p.getCode(), m.substring(m.length() - 6), p.getCode(), p.getName(), 
                    SystemConstant.USER_TYPE_SHOP, su.getUserId());
            //保存连锁商
            getDao().add(p);
        }
    }
    
    /**
     * 发送短信
     * @param m
     * @param repairCosts
     * @param su
     * @return
     * @CreateDate: 2016-9-3 上午12:43:26
     */
    public void sendSms(List<Shop> list){
        for(Shop p : list){
            //创建登录用户
            String m = p.getManagerMobile();
            //发送短信
            try{
                SmsSendUtil.sendAccountAndPasswd(m, p.getCode(), m.substring(m.length() - 6));
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    
    private static Map<Integer,String>titleMap=new HashMap<Integer, String>();
    static {
        titleMap.put(0, "连锁商账号（必填）");
        titleMap.put(1, "维修门店名称（必填）");
        titleMap.put(2, "负责人姓名（必填）");
        titleMap.put(3, "负责人手机号（必填）");
        titleMap.put(4, "备用手机号1");
        titleMap.put(5, "备用手机号2");
        titleMap.put(6, "维修门店电话号码（必填）");
        titleMap.put(7, "省（必填）");
        titleMap.put(8, "市（必填）");
        titleMap.put(9, "区（必填）");
        titleMap.put(10, "详细地址（必填）");
        titleMap.put(11, "支持品牌");
    }
    
    /**
     * 检查模板是否正确
     * @param workbook
     * @return
     */
    private boolean checkExcelModel(Workbook workbook, ImportReport report){
        Sheet sheet=workbook.getSheetAt(0);
        Row row0=sheet.getRow(0); 
        if(row0 == null){
            return false;
        }
        //模板数据
        Set<Integer> set = titleMap.keySet();
        for (Integer key : set) {
            String t1=row0.getCell(key).toString().trim();
            String t2=titleMap.get(key);
            if(t1==null||!t1.equals(t2)){
            	report.setPass(false);
                report.setContinueNext(false);
                report.setError("导入模板不正确");
                return false;
            }
        }
        return true;
    }
    
    /**
     * 检查表格数据
     * @param workbook
     * @param report
     * @return
     * @CreateDate: 2016-9-17 下午6:09:33
     */
    private List<Shop> checkData(Workbook workbook,ImportReport report,SessionUser su){
        Sheet sheet=workbook.getSheetAt(0);
        int rowNum=sheet.getLastRowNum();
        List<Shop> list=new ArrayList<Shop>();
        
        for(int i = 1; i <= rowNum; i++){
            Row row = sheet.getRow(i);
            if(row==null){
                continue;
            }
            Shop p=new Shop();
            int col = 0;
            String value = ExcelUtil.getCellValue(row, col);
            // 如果是连锁商则隐藏第一列
        	if(su.getType() == SystemConstant.USER_TYPE_PROVIDER){
        		sheet.setColumnHidden(0, true);
        		p.setProviderCode(su.getProviderCode());
        	}
        	else {
	            if(StringUtils.isBlank(value) || value.length() > 32){
	                ImportError error=new ImportError();
	                error.setPosition("第"+(row.getRowNum()+1)+"行,"+(col + 1)+"列");
	                error.setMsgType("账号错误");
	                error.setMessage("连锁商账号不能为空，长度不能超过32个字符！");
	                report.getErrorList().add(error);
	                report.setPass(false);
	            }
	            p.setProviderCode(value);
        	}
            
            col++;
            value = ExcelUtil.getCellValue(row, col);
            if(StringUtils.isBlank(value) || value.length() > 32){
                ImportError error=new ImportError();
                error.setPosition("第"+(row.getRowNum()+1)+"行,"+(col + 1)+"列");
                error.setMsgType("名称错误");
                error.setMessage("维修门店商名称不能为空，长度不能超过32个字符！");
                report.getErrorList().add(error);
                report.setPass(false);
            }
            p.setName(value);
            
            col++;
            value = ExcelUtil.getCellValue(row, col);
            if(StringUtils.isBlank(value) || value.length() > 32){
                ImportError error=new ImportError();
                error.setPosition("第"+(row.getRowNum()+1)+"行,"+(col + 1)+"列");
                error.setMsgType("名称错误");
                error.setMessage("负责人姓名不能为空，长度不能超过32个字符！");
                report.getErrorList().add(error);
                report.setPass(false);
            }
            p.setManagerName(value);

            col++;
            value = ExcelUtil.getCellValue(row, col);
            if(StringUtils.isBlank(value) || value.length() > 16){
                ImportError error=new ImportError();
                error.setPosition("第"+(row.getRowNum()+1)+"行,"+(col + 1)+"列");
                error.setMsgType("号码错误");
                error.setMessage("负责人手机号不能为空，长度不能超过16个字符！");
                report.getErrorList().add(error);
                report.setPass(false);
            }
            p.setManagerMobile(value);
            
            col++;
            value = ExcelUtil.getCellValue(row, col);
            if(StringUtils.isNotBlank(value) && value.length() > 16){
                ImportError error=new ImportError();
                error.setPosition("第"+(row.getRowNum()+1)+"行,"+(col + 1)+"列");
                error.setMsgType("号码错误");
                error.setMessage("备用手机号1长度不能超过16个字符！");
                report.getErrorList().add(error);
                report.setPass(false);
            }
            p.setManagerMobile1(value);

            col++;
            value = ExcelUtil.getCellValue(row, col);
            if(StringUtils.isNotBlank(value) && value.length() > 16){
                ImportError error=new ImportError();
                error.setPosition("第"+(row.getRowNum()+1)+"行,"+(col + 1)+"列");
                error.setMsgType("号码错误");
                error.setMessage("备用手机号2长度不能超过16个字符！");
                report.getErrorList().add(error);
                report.setPass(false);
            }
            p.setManagerMobile2(value);
            
            col++;
            value = ExcelUtil.getCellValue(row, col);
            if(StringUtils.isBlank(value) || value.length() > 16){
                ImportError error=new ImportError();
                error.setPosition("第"+(row.getRowNum()+1)+"行,"+(col + 1)+"列");
                error.setMsgType("号码错误");
                error.setMessage("维修门店电话号码不能为空，长度不能超过16个字符！");
                report.getErrorList().add(error);
                report.setPass(false);
            }
            p.setTel(value);
            
            StringBuffer areas = new StringBuffer();
            col++;
            String areaName = ExcelUtil.getCellValue(row, col);
            if(StringUtils.isBlank(areaName) || value.length() > 64){
                ImportError error=new ImportError();
                error.setPosition("第"+(row.getRowNum()+1)+"行,"+(col + 1)+"列");
                error.setMsgType("地址信息错误");
                error.setMessage("地址信息不能为空，长度不能超过64个字符！");
                report.getErrorList().add(error);
                report.setPass(false);
                continue;
            }

            Address addr = addressService.queryByAreaAndPid(areaName, "0");
            if(addr == null){
                ImportError error=new ImportError();
                error.setPosition("第"+(row.getRowNum()+1)+"行,"+(col + 1)+"列");
                error.setMsgType("地址信息错误");
                error.setMessage("地址信息未找到！");
                report.getErrorList().add(error);
                report.setPass(false);
            }
            
            p.setProvince(addr.getAreaId());
            areas.append(addr.getArea()).append(" ");
            
            String fristSpell = ConverterUtil.getFirstSpellForAreaName(addr.getArea());
            //获取账号
            String code = SeqUtil.getNext(fristSpell);
            p.setCode(code);
            
            col++;
            areaName = ExcelUtil.getCellValue(row, col);
            if(StringUtils.isBlank(areaName) || value.length() > 64){
                ImportError error=new ImportError();
                error.setPosition("第"+(row.getRowNum()+1)+"行,"+(col + 1)+"列");
                error.setMsgType("地址信息错误");
                error.setMessage("地址信息不能为空，长度不能超过64个字符！");
                report.getErrorList().add(error);
                report.setPass(false);
                continue;
            }
            
            addr = addressService.queryByAreaAndPid(areaName, p.getProvince());
            
            if(addr == null){
                ImportError error=new ImportError();
                error.setPosition("第"+(row.getRowNum()+1)+"行,"+(col + 1)+"列");
                error.setMsgType("地址信息错误");
                error.setMessage("地址信息未找到！");
                report.getErrorList().add(error);
                report.setPass(false);
            }
            p.setCity(addr.getAreaId());
            areas.append(addr.getArea()).append(" ");
            
            col++;
            areaName = ExcelUtil.getCellValue(row, col);
            if(StringUtils.isBlank(areaName) || value.length() > 64){
                ImportError error=new ImportError();
                error.setPosition("第"+(row.getRowNum()+1)+"行,"+(col + 1)+"列");
                error.setMsgType("地址信息错误");
                error.setMessage("地址信息不能为空，长度不能超过64个字符！");
                report.getErrorList().add(error);
                report.setPass(false);
                continue;
            }
            addr = addressService.queryByAreaAndPid(areaName, p.getCity());
            if(addr == null){
                ImportError error=new ImportError();
                error.setPosition("第"+(row.getRowNum()+1)+"行,"+(col + 1)+"列");
                error.setMsgType("地址信息错误");
                error.setMessage("地址信息未找到！");
                report.getErrorList().add(error);
                report.setPass(false);
            }
            p.setCounty(addr.getAreaId());
            areas.append(addr.getArea());
            p.setStreet("0");
            
            p.setAreas(areas.toString());
            
            col++;
            areaName = ExcelUtil.getCellValue(row, col);
            if(StringUtils.isBlank(areaName) || value.length() > 256){
                ImportError error=new ImportError();
                error.setPosition("第"+(row.getRowNum()+1)+"行,"+(col + 1)+"列");
                error.setMsgType("地址信息错误");
                error.setMessage("详细地址不能为空，长度不能超过256个字符！");
                report.getErrorList().add(error);
                report.setPass(false);
                continue;
            }
            p.setAddress(areaName);
            
            col++;
            areaName = ExcelUtil.getCellValue(row, col);
            String[] brandNames = areaName.split(",");
            List<String> brandIds = new ArrayList<>();
            for(String bname : brandNames){
            	List<Brand> brands = brandService.queryByName(bname);
            	if(brands != null && brands.size() > 0){
            		brandIds.add(brands.get(0).getId());
            	}
            	else {
            		ImportError error=new ImportError();
                    error.setPosition("第"+(row.getRowNum()+1)+"行,"+(col + 1)+"列");
                    error.setMsgType("品牌名称错误");
                    error.setMessage("品牌：" + bname + " 不存在！");
                    report.getErrorList().add(error);
                    report.setPass(false);
                    continue;
            	}
            }

            p.setBrands(brandIds.toArray(new String[]{}));
            
            try{
	            //根据地址获取坐标
	            JSONObject json = BaiduMapUtil.getLatAndLngByAddr(p.getFullAddress());
	            // 设置坐标
	            p.setLongitude(new BigDecimal(json.getFloat("lng")));
	            p.setLatitude(new BigDecimal(json.getFloat("lat")));
            }
            catch (SystemException e){
            	e.printStackTrace();
            	ImportError error=new ImportError();
                error.setPosition("第"+(row.getRowNum()+1)+"行,"+(col + 1)+"列");
                error.setMsgType("地址信息错误");
                error.setMessage("查询坐标接口异常：" + e.getMessage());
                report.getErrorList().add(error);
                report.setPass(false);
                continue;
            }
            
            p.setIsDel(0);
            p.setStatus(0);
            p.setSort(999);
            list.add(p);
        }
        
        return list;
    }
    
    /**
     * 已Excel形式导出列表数据
     * @param params
     */
    @SuppressWarnings("rawtypes")
    public void expDataExcel(Map<String, Object> params){
        String templateFileName = params.get("tempFileName")+"";
        String destFileName =params.get("outFileName")+"";
        
        String name = MapUtils.getString(params, "query_name");
        String code = MapUtils.getString(params, "query_code");
        String mobile = MapUtils.getString(params, "query_mobile");
        Shop p = new Shop();
        p.setName(name);
        p.setCode(code);
        p.setManagerMobile(mobile);
        
        String idStr = MapUtils.getString(params, "ids");
        if(StringUtils.isNotBlank(idStr)) {
        	String[] ids = StringUtils.split(idStr, ",");
        	p.setQueryIds(Arrays.asList(ids));
        }
        
        List project = queryList(p);

        Map<String,Object> map = new HashMap<String,Object>();
        map.put("shopList", project);
            
        XLSTransformer transformer = new XLSTransformer();
        try {
            transformer.transformXLS(templateFileName, map, destFileName);
        } 
        catch (ParsePropertyException e) {
            log.error("文件导出--ParsePropertyException", e);
        } 
        catch (InvalidFormatException e) {
            log.error("文件导出--InvalidFormatException", e);
        } 
        catch (IOException e) {
            log.error("文件导出--IOException", e);
        }
    }
    
    /**
     * 下载导入模板
     * @param params
     */
    @SuppressWarnings("rawtypes")
    public void expImportTemplate(Map<String, Object> params){
        String templateFileName = params.get("tempFileName")+"";
        String outFileName =params.get("outFileName")+"";
      
        try {
        	//获取登录用户
            SessionUser su = (SessionUser)params.get("user");
        	
            Workbook workbook = new HSSFWorkbook(new FileInputStream(templateFileName));
            
            // 数据字典页
            Sheet hideInfoSheet = workbook.createSheet(ExcelUtil.EXCEL_HIDE_SHEET_NAME);
            
            //获取省份地址
            List<Address> provinceL = addressService.queryByPid("0");
            // 第一行设置省份
            Row row = hideInfoSheet.createRow(0);
            ExcelUtil.creatRow(row, provinceL);
            ExcelUtil.creatExcelNameList(workbook, "province", 1, provinceL.size(), false);
            int rowIndex = 1;
            //循环省份
            for(Address prov : provinceL){
                //获取市地址
                List<Address> cityL = addressService.queryByPid(prov.getAreaId());
                row = hideInfoSheet.createRow(rowIndex);
                ExcelUtil.creatRow(row, cityL);
                ExcelUtil.creatExcelNameList(workbook, prov.getArea(), rowIndex + 1, cityL.size(), false);
                rowIndex++;
                for(Address ct : cityL){
                    //获取区县地址
                    List<Address> countyL = addressService.queryByPid(ct.getAreaId());
                    if (countyL.size() == 0) {
                        continue;
                    }
                    row = hideInfoSheet.createRow(rowIndex);
                    ExcelUtil.creatRow(row, countyL);
                    ExcelUtil.creatExcelNameList(workbook, prov.getArea() + ct.getArea(), rowIndex + 1, countyL.size(), false);
                    rowIndex++;
                }
            }
            
            Workbook wb = workbook;
            int sheetIndex = wb.getNumberOfSheets();
            if (sheetIndex > 0) {
                for (int i = 0; i < sheetIndex; i++) {
                    Sheet sheet = wb.getSheetAt(i);
                    if (!ExcelUtil.EXCEL_HIDE_SHEET_NAME.equals(sheet.getSheetName())) {
                    	// 如果是连锁商则隐藏第一列
                    	if(su.getType() == SystemConstant.USER_TYPE_PROVIDER){
                    		sheet.setColumnHidden(0, true);
                    	}
                    	
                        // 省份选项添加验证数据
                        DataValidation dataValidationList = ExcelUtil.getDataValidationByFormula(
                                "province", 1, 65535, 7, 7);
                        sheet.addValidationData(dataValidationList);
                        // 城市选项添加验证数据
                        dataValidationList = ExcelUtil.getDataValidationByFormula(
                                "INDIRECT(INDIRECT(\"$h$\"&ROW()))", 1, 65535, 8, 8);
                        sheet.addValidationData(dataValidationList);
                        // 城市选项添加验证数据
                        dataValidationList = ExcelUtil.getDataValidationByFormula(
                                "INDIRECT(INDIRECT(\"$h$\"&ROW())&INDIRECT(\"$i$\"&ROW()))", 1, 65535, 9, 9);
                        sheet.addValidationData(dataValidationList);
                    }
                }
            }
            
            FileOutputStream fileOut = new FileOutputStream(outFileName);
            workbook.write(fileOut);
            fileOut.close();
        } 
        catch (ParsePropertyException e) {
            e.printStackTrace();
            log.error("文件导出--ParsePropertyException", e);
        } 
        catch (IOException e) {
            e.printStackTrace();
            log.error("文件导出--IOException", e);
        }
    }
    
    
    
    /**
     * 通过用户地址和维修品牌确定门店信息
     */
    public Shop selectShop(String province,String city){
    	  Shop shop=new Shop();
          shop.setProvince(province);
          shop.setCity(city);
          shop.setIsSendRepair(1);
          List<Shop> list=getDao().queryList(shop);
          if(list.size()==0){
        	  //如果该城市没有寄修门店 则统一派单给杭州的寄修门店
        	  Shop s=new Shop();
        	  s.setProvince("15");//根据数据表中查询得到的浙江省简码为15
        	  s.setCity("1213");//根据数据表中查询得到的杭州市简码为1213
        	  s.setIsSendRepair(1);
        	  List<Shop> shoplist=getDao().queryList(s);
        	  if(shoplist.size()==0){
        		  throw new SystemException("亲，目前寄修服务已关闭");
        	  }else{
                  shop=shoplist.get(0);        		  
        	  }
          }else{
          	  shop=list.get(0);
          }
    	  return  shop;
    }
}