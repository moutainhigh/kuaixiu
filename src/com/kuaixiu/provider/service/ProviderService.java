package com.kuaixiu.provider.service;


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
import org.apache.commons.lang3.math.NumberUtils;
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

import com.common.base.service.BaseService;
import com.common.exception.SystemException;
import com.common.importExcel.ExcelUtil;
import com.common.importExcel.ImportError;
import com.common.importExcel.ImportReport;
import com.common.util.ConverterUtil;
import com.common.util.SmsSendUtil;
import com.kuaixiu.model.entity.Model;
import com.kuaixiu.provider.dao.ProviderMapper;
import com.kuaixiu.provider.entity.Provider;
import com.system.basic.address.entity.Address;
import com.system.basic.address.service.AddressService;
import com.system.basic.sequence.util.SeqUtil;
import com.system.basic.user.entity.SessionUser;
import com.system.basic.user.service.SysUserService;
import com.system.constant.SystemConstant;

/**
 * Provider Service
 * @CreateDate: 2016-08-26 上午12:45:50
 * @version: V 1.0
 */
@Service("providerService")
public class ProviderService extends BaseService<Provider> {
    private static final Logger log= Logger.getLogger(ProviderService.class);

    @Autowired
    private ProviderMapper<Provider> mapper;
    @Autowired
    private AddressService addressService;
    @Autowired
    private SysUserService sysUserService;


    public ProviderMapper<Provider> getDao() {
        return mapper;
    }

    //**********自定义方法***********
    
    /**
     * 根据账号查询
     * @param code
     * @return
     * @CreateDate: 2016-9-6 下午11:57:44
     */
    public Provider queryByCode(String code){
        return getDao().queryByCode(code);
    }
    
    /**
     * 得到日期内需要结算的连锁商
     * @param startTime
     * @param endTime
     * @return
     * @author: lijx
     * @CreateDate: 2016-10-20 下午7:19:40
     */
    public List<Provider> queryProviderForBalance(String startTime, String endTime){
        Provider p = new Provider();
        p.setQueryStartTime(startTime);
        p.setQueryEndTime(endTime);
        return getDao().queryProviderForBalance(p);
    }
    
    /**
     * 1、获取省份名称首字母
     * 2、生成账号
     * 3、创建登录用户
     * 4、保存连锁商
     * @param p
     * @param su
     * @return
     * @author: lijx
     * @CreateDate: 2016-9-6 下午10:50:31
     */
    @Transactional
    public int save(Provider p, SessionUser su){
        //获取省份名称首字母
        Address province = addressService.queryByAreaId(p.getProvince());
        String fristSpell = ConverterUtil.getFirstSpellForAreaName(province.getArea());
        //获取账号
        String code = SeqUtil.getNext(fristSpell);
        p.setCode(code);
        p.setIsDel(0);
        p.setStatus(0);
        p.setSort(999);
        //创建登录用户
        String m = p.getManagerMobile();
        sysUserService.createUser(code, m.substring(m.length() - 6), code, p.getName(), 
                SystemConstant.USER_TYPE_PROVIDER, su.getUserId());
        
        //保存连锁商
        int rest = getDao().add(p);
        
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
     * 更新连接商
     * @param p
     * @return
     * @CreateDate: 2016-8-31 下午7:08:33
     */
    public int update(Provider p, SessionUser su){
        if(p == null || StringUtils.isBlank(p.getId())){
            throw new SystemException("参数为空，无法更新");
        }
        Provider t = getDao().queryById(p.getId());
        if(!t.getName().equals(p.getName())){
            //如果名称发送改变则修改登录用户名称
            sysUserService.resetUserName(t.getCode(), p.getName(), su.getUserId());
        }
        p.setCode(t.getCode());
        p.setAmountPrice(t.getAmountPrice());
        p.setBalance(t.getBalance());
        p.setIsDel(t.getIsDel());
        p.setStatus(t.getStatus());
        p.setSort(t.getSort());
        p.setUpdateUserid(su.getUserId());
        return getDao().update(p);
    }
    
    /**
     * 删除连接商
     * @param p
     * @return
     * @CreateDate: 2016-8-31 下午7:09:59
     */
    public int delete(Provider p){
        if(p == null || StringUtils.isBlank(p.getId())){
            throw new SystemException("参数为空，无法更新");
        }
        Provider t = getDao().queryById(p.getId());
        t.setIsDel(1);
        t.setUpdateUserid(p.getUpdateUserid());
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
            Provider t = getDao().queryById(id);
            t.setIsDel(1);
            t.setUpdateUserid(su.getUserId());
            getDao().update(t);
        }
        return 1;
    }
    
    /**
     * 连锁商导入主入口
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
                List<Provider> list=checkData(workbook,report);
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
     * 保存连锁商
     * @param m
     * @param repairCosts
     * @param su
     * @return
     * @CreateDate: 2016-9-3 上午12:43:26
     */
    @Transactional
    public void saveData(List<Provider> list, SessionUser su){
        for(Provider p : list){
            //创建登录用户
            String m = p.getManagerMobile();
            sysUserService.createUser(p.getCode(), m.substring(m.length() - 6), p.getCode(), p.getName(), 
                    SystemConstant.USER_TYPE_PROVIDER, su.getUserId());
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
    public void sendSms(List<Provider> list){
        for(Provider p : list){
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
        titleMap.put(0, "连锁商名称（必填）");
        titleMap.put(1, "负责人姓名（必填）");
        titleMap.put(2, "负责人手机号（必填）");
        titleMap.put(3, "连锁商电话号码（必填）");
        titleMap.put(4, "连锁商开户银行（必填）");
        titleMap.put(5, "连锁商开户银行支行（必填）");
        titleMap.put(6, "连锁商开户户名（必填）");
        titleMap.put(7, "连锁商开户账号（必填）");
        titleMap.put(8, "手续费扣除比例（必填）");
        titleMap.put(9, "省（必填）");
        titleMap.put(10, "市（必填）");
        titleMap.put(11, "区（必填）");
        titleMap.put(12, "详细地址（必填）");
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
    private List<Provider> checkData(Workbook workbook,ImportReport report){
        Sheet sheet=workbook.getSheetAt(0);
        int rowNum=sheet.getLastRowNum();
        List<Provider> list=new ArrayList<Provider>();
        
        for(int i = 1; i <= rowNum; i++){
            Row row = sheet.getRow(i);
            if(row==null){
                continue;
            }
            Provider p=new Provider();
            int col = 0;
            String value = ExcelUtil.getCellValue(row, col);
            if(StringUtils.isBlank(value) || value.length() > 32){
                ImportError error=new ImportError();
                error.setPosition("第"+(row.getRowNum()+1)+"行,"+(col + 1)+"列");
                error.setMsgType("名称错误");
                error.setMessage("连锁商名称不能为空，长度不能超过32个字符！");
                report.getErrorList().add(error);
                report.setPass(false);
            }
            else {
                p.setName(value);
            }
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
            else {
                p.setManagerName(value);
            }
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
            else {
                p.setManagerMobile(value);
            }
            col++;
            value = ExcelUtil.getCellValue(row, col);
            if(StringUtils.isBlank(value) || value.length() > 16){
                ImportError error=new ImportError();
                error.setPosition("第"+(row.getRowNum()+1)+"行,"+(col + 1)+"列");
                error.setMsgType("号码错误");
                error.setMessage("连锁商电话号码不能为空，长度不能超过16个字符！");
                report.getErrorList().add(error);
                report.setPass(false);
            }
            else {
                p.setTel(value);
            }
            
            col++;
            value = ExcelUtil.getCellValue(row, col);
            if(StringUtils.isBlank(value) || value.length() > 32){
                ImportError error=new ImportError();
                error.setPosition("第"+(row.getRowNum()+1)+"行,"+(col + 1)+"列");
                error.setMsgType("开户信息错误");
                error.setMessage("开户银行不能为空，长度不能超过32个字符！");
                report.getErrorList().add(error);
                report.setPass(false);
            }
            else {
                p.setAccountBank(value);
            }
            
            col++;
            value = ExcelUtil.getCellValue(row, col);
            if(StringUtils.isBlank(value) || value.length() > 64){
                ImportError error=new ImportError();
                error.setPosition("第"+(row.getRowNum()+1)+"行,"+(col + 1)+"列");
                error.setMsgType("开户信息错误");
                error.setMessage("开户支行不能为空，长度不能超过64个字符！");
                report.getErrorList().add(error);
                report.setPass(false);
            }
            else {
                p.setAccountBankBranch(value);
            }
            
            col++;
            value = ExcelUtil.getCellValue(row, col);
            if(StringUtils.isBlank(value) || value.length() > 32){
                ImportError error=new ImportError();
                error.setPosition("第"+(row.getRowNum()+1)+"行,"+(col + 1)+"列");
                error.setMsgType("开户信息错误");
                error.setMessage("开户名称不能为空，长度不能超过32个字符！");
                report.getErrorList().add(error);
                report.setPass(false);
            }
            else {
                p.setAccountName(value);
            }
            col++;
            value = ExcelUtil.getCellValue(row, col);
            if(StringUtils.isBlank(value) || value.length() > 32){
                ImportError error=new ImportError();
                error.setPosition("第"+(row.getRowNum()+1)+"行,"+(col + 1)+"列");
                error.setMsgType("开户信息错误");
                error.setMessage("开户账号不能为空，长度不能超过32个字符！");
                report.getErrorList().add(error);
                report.setPass(false);
            }
            else {
                p.setAccountNumber(value);
            }
            
            col++;
            value = ExcelUtil.getCellValue(row, col);
            if(StringUtils.isBlank(value) || value.length() > 6 || !NumberUtils.isNumber(value)){
                ImportError error=new ImportError();
                error.setPosition("第"+(row.getRowNum()+1)+"行,"+(col + 1)+"列");
                error.setMsgType("结算比例错误");
                error.setMessage("结算比例不能为空不能为空，必须是数字，长度不能超过6个字符！");
                report.getErrorList().add(error);
                report.setPass(false);
            }
            else {
                p.setProportion(new BigDecimal(value));
            }
            
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
        
        //获取查询条件
        String name = MapUtils.getString(params, "query_name");
        String code = MapUtils.getString(params, "query_code");
        String mobile = MapUtils.getString(params, "query_mobile");
        Provider p = new Provider();
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
        map.put("providerList", project);
            
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
                        // 省份选项添加验证数据
                        DataValidation dataValidationList = ExcelUtil.getDataValidationByFormula(
                                "province", 1, 65535, 9, 9);
                        sheet.addValidationData(dataValidationList);
                        // 城市选项添加验证数据
                        dataValidationList = ExcelUtil.getDataValidationByFormula(
                                "INDIRECT(INDIRECT(\"$j$\"&ROW()))", 1, 65535, 10, 10);
                        sheet.addValidationData(dataValidationList);
                        // 城市选项添加验证数据
                        dataValidationList = ExcelUtil.getDataValidationByFormula(
                                "INDIRECT(INDIRECT(\"$j$\"&ROW())&INDIRECT(\"$k$\"&ROW()))", 1, 65535, 11, 11);
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
}