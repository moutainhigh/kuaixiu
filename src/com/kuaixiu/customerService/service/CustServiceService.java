package com.kuaixiu.customerService.service;


import com.common.base.service.BaseService;
import com.common.exception.SystemException;
import com.common.util.ConverterUtil;
import com.common.util.SmsSendUtil;
import com.kuaixiu.customerService.dao.CustServiceMapper;
import com.kuaixiu.customerService.entity.CustService;
import com.kuaixiu.shop.entity.Shop;
import com.kuaixiu.shop.service.ShopService;
import com.system.basic.address.entity.Address;
import com.system.basic.address.service.AddressService;
import com.system.basic.sequence.util.SeqUtil;
import com.system.basic.user.entity.SessionUser;
import com.system.basic.user.service.SysUserService;
import com.system.constant.SystemConstant;
import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CustService Service
 * @CreateDate: 2016-12-18 下午10:13:47
 * @version: V 1.0
 */
@Service("custServiceService")
public class CustServiceService extends BaseService<CustService> {
    private static final Logger log= Logger.getLogger(CustServiceService.class);

    @Autowired
    private CustServiceMapper<CustService> mapper;
    @Autowired
    private ShopService shopService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private SysUserService sysUserService;


    public CustServiceMapper<CustService> getDao() {
        return mapper;
    }

    //**********自定义方法***********

    /**
     * 根据工号，获取客服基本信息,包含商，店对应
     * @param number 工号
     * @return 客服对象
     */
    public CustService queryByNumber(String number){
        return getDao().queryByNumber(number);
    }
    
    /**
     * 根据工号，获取客服信息
     */
    public CustService queryByCustNumber(String number){
        return getDao().queryByCustNumber(number);
    }
    
    /**
     * 1、获取省份名称首字母
     * 2、生成账号
     * 3、创建登录用户
     * 4、保存
     * @param t
     * @param su
     * @return
     * @CreateDate: 2016-9-7 上午1:18:20
     */
    public int save(CustService t, SessionUser su){
    	String fristSpell = "";
        if(StringUtils.isNotBlank(t.getShopCode())){
        	Shop s = shopService.queryByCode(t.getShopCode());
        	//获取省份名称首字母
        	Address province = addressService.queryByAreaId(s.getProvince());
        	fristSpell = ConverterUtil.getFirstSpellForAreaName(province.getArea());
        	//获取市名称首字母
        	Address city = addressService.queryByAreaId(s.getCity());
        	fristSpell += ConverterUtil.getFirstSpellForAreaName(city.getArea());
        }
        //获取账号
        fristSpell += "kf";
        String code = SeqUtil.getNext(fristSpell);
        t.setNumber(code);
        
        //创建登录用户
        String m = t.getMobile();
        sysUserService.createUser(code, m.substring(m.length() - 6), code, t.getName(), 
                SystemConstant.USER_TYPE_CUSTOMER_SERVICE, su.getUserId());
        int rest = getDao().add(t);
        
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
     * 根据连锁商查询工程师
     * @param proCode
     * @return
     * @CreateDate: 2016-9-4 下午4:21:29
     */
    public List<CustService> queryByProviderCode(String proCode){
    	CustService t = new CustService();
        t.setProviderCode(proCode);
        return getDao().queryList(t);
    }
    
    /**
     * 根据门店查询工程师
     * @param proCode
     * @return
     * @CreateDate: 2016-9-4 下午4:21:29
     */
    public List<CustService> queryByShopCode(String shopCode){
    	CustService t = new CustService();
        t.setShopCode(shopCode);
        return getDao().queryList(t);
    }
    
    /**
     * 更新工程师
     * @param p
     * @return
     * @CreateDate: 2016-8-31 下午7:08:33
     */
    public int update(CustService cust){
        if (cust == null || StringUtils.isBlank(cust.getId())) {
            throw new SystemException("参数为空，无法更新");
        }
        CustService t = getDao().queryById(cust.getId());
        //如果名称发送改变则修改登录用户名称
        sysUserService.resetUserName(t.getNumber(), cust.getName(), cust.getUpdateUserid());
        cust.setNumber(t.getNumber());
        cust.setIsDel(0);
        return getDao().update(cust);
    }
    
    /**
     * 删除连接商
     * @param p
     * @return
     * @CreateDate: 2016-8-31 下午7:09:59
     */
    public int delete(CustService cust){
        if(cust == null || StringUtils.isBlank(cust.getId())){
            throw new SystemException("参数为空，无法更新");
        }
        CustService t = getDao().queryById(cust.getId());
        t.setIsDel(1);
        t.setUpdateUserid(cust.getUpdateUserid());
        sysUserService.deleteUser(t.getNumber(), cust.getUpdateUserid());
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
        	CustService t = getDao().queryById(id);
            t.setIsDel(1);
            t.setUpdateUserid(su.getUserId());
            sysUserService.deleteUser(t.getNumber(), su.getUserId());
            getDao().update(t);
        }
        return 1;
    }
    /**
     * 已Excel形式导出列表数据
     * @param params
     */
    @SuppressWarnings("rawtypes")
    public void expDataExcel(Map<String, Object> params){
        String templateFileName = params.get("tempFileName")+"";
        String destFileName =params.get("outFileName")+"";
        
        //获取查询条件
        String name = MapUtils.getString(params, "query_name");
        String number = MapUtils.getString(params, "query_number");
        String mobile = MapUtils.getString(params, "query_mobile");
        CustService cust = new CustService();
        
        cust.setName(name);
        cust.setNumber(number);
        cust.setMobile(mobile);
        
        String idStr = MapUtils.getString(params, "ids");
        if(StringUtils.isNotBlank(idStr)) {
        	String[] ids = StringUtils.split(idStr, ",");
        	cust.setQueryIds(Arrays.asList(ids));
        }
        
        List project = queryList(cust);

        Map<String,Object> map = new HashMap<String,Object>();
        map.put("engineerList", project);
            
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
}