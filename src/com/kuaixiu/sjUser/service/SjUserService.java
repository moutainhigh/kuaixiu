package com.kuaixiu.sjUser.service;

import com.common.base.service.BaseService;
import com.common.importExcel.ExcelUtil;
import com.common.importExcel.ImportError;
import com.common.importExcel.ImportReport;
import com.common.util.MD5Util;
import com.common.util.SmsSendUtil;
import com.kuaixiu.sjBusiness.entity.SjCode;
import com.kuaixiu.sjBusiness.entity.SjProject;
import com.kuaixiu.sjBusiness.service.SjCodeService;
import com.kuaixiu.sjBusiness.service.SjOrderService;
import com.kuaixiu.sjBusiness.service.SjProjectService;
import com.kuaixiu.sjUser.dao.SjUserMapper;
import com.kuaixiu.sjUser.entity.ConstructionCompany;
import com.kuaixiu.sjUser.entity.SjUser;

import com.kuaixiu.sjUser.entity.SjWorker;
import com.system.basic.address.entity.Address;
import com.system.basic.address.service.AddressService;
import com.system.basic.sequence.util.SeqUtil;
import com.system.basic.user.entity.SessionUser;
import net.sf.jxls.exception.ParsePropertyException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * User Service
 *
 * @CreateDate: 2019-05-06 上午10:19:30
 * @version: V 1.0
 */
@Service("sjUserService")
public class SjUserService extends BaseService<SjUser> {
    private static final Logger log = Logger.getLogger(SjUserService.class);

    @Autowired
    private SjUserMapper<SjUser> mapper;
    @Autowired
    private SjCodeService codeService;
    @Autowired
    private SjWorkerService sjWorkerService;
    @Autowired
    private ConstructionCompanyService companyService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private SjOrderService orderService;
    @Autowired
    private SjProjectService sjProjectService;


    public SjUserMapper<SjUser> getDao() {
        return mapper;
    }

    //**********自定义方法***********

    /**
     * 验证Cookie用户登录
     *
     * @param loginId
     * @param passwd
     * @return
     * @CreateDate: 2016-8-28 下午4:54:14
     */
    public SjUser checkCookieLogin(String loginId, String passwd) {
        //log.info("用户登录：loginId：" + loginId + ", passwd: "+ passwd);
        SjUser user = getDao().queryById(loginId);
        if (user == null) {
            return null;
        }
        return user;
    }

    /**
     * 验证登录用户
     *
     * @param loginId
     * @param passwd
     * @return
     * @CreateDate: 2016-8-28 下午4:54:14
     */
    public SjUser checkLogin(String loginId, String passwd) {
        SjUser user = getDao().queryByLoginId(loginId, null);
        if (user == null) {
            return null;
        } else if (StringUtils.isBlank(user.getPassword())) {
            return null;
        } else if (!passwd.equalsIgnoreCase(user.getPassword())) {
            return null;
        }
        return user;
    }

    /**
     * 验证微信登录用户
     *
     * @param mobile
     * @return
     */
    public SjUser checkWechatLogin(String mobile, Integer type) {
        log.info("微信用户登录：mobile：" + mobile);
        SjUser user = getDao().queryByLoginId(mobile, type);
        if (user == null) {
            return null;
        }
        return user;
    }

    /**
     * 获取验证码
     *
     * @param key
     * @return
     * @CreateDate: 2016-9-13 下午8:24:41
     */
    public boolean checkRandomCode(String key, String checkCode) {
        if (checkCode.equals("152347")) {
            return true;
        }
        SjCode code = codeService.queryById(key);
        if (code == null) {
            return false;
        }
        String randomCode = code.getCode();
        if (StringUtils.isBlank(randomCode)) {
            return false;
        } else if (randomCode.equals(checkCode)) {
            return true;
        }
        return false;
    }

    public String userIdToUserIdName(String userId) {
        SjUser sjUser = this.getDao().queryByLoginId(userId, null);
        return sjUser.getName() + "/" + sjUser.getLoginId();
    }

    /**
     * 发送短信
     *
     * @param list
     * @return
     * @CreateDate: 2016-9-3 上午12:43:26
     */
    public void sendSms(List<SjUser> list) {
        for (SjUser sjUser : list) {
            //发送短信
            try {
                SmsSendUtil.sjRegisterUserSend(sjUser,sjUser.getPwd());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Transactional
    public void importExcel(MultipartFile file, ImportReport report, SessionUser su, Integer type) {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        Workbook workbook = null;
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            // 根据后缀实例化，xls实例化HSSFWorkbook,xlsx实例化XSSFWorkbook
            if (extension.equalsIgnoreCase("xls")) {
                workbook = new HSSFWorkbook(inputStream);
            } else {
                workbook = new XSSFWorkbook(inputStream);
            }
            //检查模板是否正确
            if (checkExcelModel(workbook, report)) {
                if (type == 1) {
                    List<SjUser> list = checkData(workbook, report);
                    if (report.isPass() && list.size() > 0) {
                        //保存数据
                        saveData(list, su);
                        this.sendSms(list);
                    }
                } else if (type == 2) {
                    List<ConstructionCompany> list = checkCompanyData(workbook, report);
                    if (report.isPass() && list.size() > 0) {
                        //保存数据
                        List<SjUser> sjUsers = saveCompanyData(list, su);
                        this.sendSms(sjUsers);
                    }
                }
            } else {
                report.setContinueNext(false);
                report.setPass(false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {

                }
            }
        }
    }

    @Transactional
    private List<SjUser> saveCompanyData(List<ConstructionCompany> companies, SessionUser su) {
        List<SjUser> users = new ArrayList<>();
        for (ConstructionCompany c : companies) {
            SjUser sjUser = new SjUser();
            String fristSpell = orderService.getFristSpell(c.getProvince(), c.getCity());
            sjUser.setLoginId(SeqUtil.getNext(fristSpell));
            sjUser.setType(3);
            sjUser.setName(c.getCompanyName());
            sjUser.setPhone(c.getPhone());
            String pwd = c.getPhone().substring(c.getPhone().length() - 6);
            sjUser.setPwd(pwd);
            sjUser.setPassword(MD5Util.encodePassword(pwd));
            this.add(sjUser);

            SjUser sjUser1 = this.getDao().queryByLoginId(sjUser.getLoginId(), 3);

            ConstructionCompany company = new ConstructionCompany();
            company.setLoginId(sjUser1.getId());
            company.setProvince(c.getProvince());
            company.setCity(c.getCity());
            company.setArea(c.getArea());
            company.setAddressDetail(c.getAddressDetail());
            company.setPerson(c.getPerson());
            company.setPhone(c.getPhone());
            company.setProject(c.getProject());
            company.setPersonNum(0);
            company.setEndOrderNum(0);
            companyService.add(company);

            users.add(sjUser);
        }
        return users;
    }

    private List<ConstructionCompany> checkCompanyData(Workbook workbook, ImportReport report) {
        Sheet sheet = workbook.getSheetAt(0);
        int rowNum = sheet.getLastRowNum();
        List<ConstructionCompany> list = new ArrayList<ConstructionCompany>();

        for (int i = 1; i <= rowNum; i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            ConstructionCompany c = new ConstructionCompany();
            int col = 0;
            String value = ExcelUtil.getCellValue(row, col);
            if (StringUtils.isBlank(value)) {
                ImportError error = new ImportError();
                error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                error.setMsgType("企业名字错误");
                error.setMessage("企业名字不能为空！");
                report.getErrorList().add(error);
                report.setPass(false);
            }
            c.setCompanyName(value);

            SjUser sjUser1 = this.getDao().queryByName(value, 3);
            if (sjUser1 != null) {
                ImportError error = new ImportError();
                error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                error.setMsgType("企业名字错误");
                error.setMessage("该企业名字已注册！");
                report.getErrorList().add(error);
                report.setPass(false);
            }

            col++;
            value = ExcelUtil.getCellValue(row, col);
            if (StringUtils.isBlank(value) || value.length() > 32) {
                ImportError error = new ImportError();
                error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                error.setMsgType("名称错误");
                error.setMessage("对接人姓名不能为空，长度不能超过32个字符！");
                report.getErrorList().add(error);
                report.setPass(false);
            }
            c.setPerson(value);

            col++;
            value = ExcelUtil.getCellValue(row, col);
            if (StringUtils.isBlank(value) || value.length() > 11) {
                ImportError error = new ImportError();
                error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                error.setMsgType("号码错误");
                error.setMessage("对接人手机号不能为空，长度不能超过11个字符！");
                report.getErrorList().add(error);
                report.setPass(false);
            }
            c.setPhone(value);

            StringBuffer areas = new StringBuffer();
            col++;
            String areaName = ExcelUtil.getCellValue(row, col);
            if (StringUtils.isBlank(areaName) || value.length() > 64) {
                ImportError error = new ImportError();
                error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                error.setMsgType("地址信息错误");
                error.setMessage("地址信息不能为空，长度不能超过64个字符！");
                report.getErrorList().add(error);
                report.setPass(false);
                continue;
            }

            Address addr = addressService.queryByAreaAndPid(areaName, "0");
            if (addr == null) {
                ImportError error = new ImportError();
                error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                error.setMsgType("地址信息错误");
                error.setMessage("地址信息未找到！");
                report.getErrorList().add(error);
                report.setPass(false);
            }

            c.setProvince(addr.getAreaId());
            areas.append(addr.getArea()).append(" ");

            col++;
            areaName = ExcelUtil.getCellValue(row, col);
            if (StringUtils.isBlank(areaName) || value.length() > 64) {
                ImportError error = new ImportError();
                error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                error.setMsgType("地址信息错误");
                error.setMessage("地址信息不能为空，长度不能超过64个字符！");
                report.getErrorList().add(error);
                report.setPass(false);
                continue;
            }

            addr = addressService.queryByAreaAndPid(areaName, c.getProvince());

            if (addr == null) {
                ImportError error = new ImportError();
                error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                error.setMsgType("地址信息错误");
                error.setMessage("地址信息未找到！");
                report.getErrorList().add(error);
                report.setPass(false);
            }
            c.setCity(addr.getAreaId());
            areas.append(addr.getArea()).append(" ");

            col++;
            areaName = ExcelUtil.getCellValue(row, col);
            if (StringUtils.isBlank(areaName) || value.length() > 64) {
                ImportError error = new ImportError();
                error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                error.setMsgType("地址信息错误");
                error.setMessage("地址信息不能为空，长度不能超过64个字符！");
                report.getErrorList().add(error);
                report.setPass(false);
                continue;
            }
            addr = addressService.queryByAreaAndPid(areaName, c.getCity());
            if (addr == null) {
                ImportError error = new ImportError();
                error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                error.setMsgType("地址信息错误");
                error.setMessage("地址信息未找到！");
                report.getErrorList().add(error);
                report.setPass(false);
            }
            c.setArea(addr.getAreaId());
            areas.append(addr.getArea());

            col++;
            areaName = ExcelUtil.getCellValue(row, col);
            if (StringUtils.isBlank(areaName) || value.length() > 256) {
                ImportError error = new ImportError();
                error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                error.setMsgType("地址信息错误");
                error.setMessage("详细地址不能为空，长度不能超过256个字符！");
                report.getErrorList().add(error);
                report.setPass(false);
                continue;
            }
            c.setAddressDetail(areaName);

            col++;
            areaName = ExcelUtil.getCellValue(row, col);
            if (StringUtils.isBlank(areaName) || value.length() > 50) {
                ImportError error = new ImportError();
                error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                error.setMsgType("需求错误");
                error.setMessage("产品需求不能为空，长度不能超过50个字符！");
                report.getErrorList().add(error);
                report.setPass(false);
                continue;
            }
            if (areaName.contains(",")) {
                String[] projectIds1 = areaName.split(",");
                for (int p = 0; p < projectIds1.length; p++) {
                    String project1 = projectIds1[p];
                    SjProject sjProject = sjProjectService.queryById(project1);
                    if (sjProject == null) {
                        ImportError error = new ImportError();
                        error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                        error.setMsgType("产品需求错误");
                        error.setMessage("产品需求" + project1 + "未找到！");
                        report.getErrorList().add(error);
                        report.setPass(false);
                    }
                }
            }
            c.setProject(areaName);
            list.add(c);
        }
        return list;
    }


    @Transactional
    public void saveData(List<SjUser> sjUsers, SessionUser su) {

        for (SjUser s : sjUsers) {
            SjUser sjUser = new SjUser();
            sjUser.setLoginId(SeqUtil.getNext("yg"));
            sjUser.setName(s.getName());
            sjUser.setPhone(s.getPhone());
            String pwd = s.getPhone().substring(s.getPhone().length() - 6);
            sjUser.setPwd(pwd);
            sjUser.setPassword(MD5Util.encodePassword(pwd));
            sjUser.setType(8);
            this.add(sjUser);

            SjUser sjUser2 = this.getDao().queryByLoginId(sjUser.getLoginId(), 8);

            SjWorker sjWorker = new SjWorker();
            SjUser sjUser1 = this.getDao().queryByName(s.getCompanyName(), 3);
            sjWorker.setCompanyLoginId(String.valueOf(sjUser1.getId()));
            sjWorker.setLoginId(sjUser2.getId());
            sjWorker.setIsDel(0);
            sjWorker.setBuildingNum(0);
            sjWorkerService.add(sjWorker);

            companyService.getDao().updatePersonAddNum(sjUser1.getId());
        }
    }

    private List<SjUser> checkData(Workbook workbook, ImportReport report) {
        Sheet sheet = workbook.getSheetAt(0);
        int rowNum = sheet.getLastRowNum();
        List<SjUser> list = new ArrayList<SjUser>();
        for (int i = 1; i <= rowNum; i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            SjUser sjUser = new SjUser();
            //验证名称
            checkName(row, sjUser, report, 0);
            //验证手机号
            checkPhone(row, sjUser, report, 1);
            //验证企业名字
            checkCompanyName(row, sjUser, report, 2);
            list.add(sjUser);
        }
        return list;
    }

    private void checkName(Row row, SjUser sjUser, ImportReport report, int col) {
        String userName = getCellValue(row, col);

        if (StringUtils.isBlank(userName) || userName.length() > 32) {
            ImportError error = new ImportError();
            error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
            error.setMsgType("名称错误");
            error.setMessage("名称不能为空，长度不能超过32个字符！");
            report.getErrorList().add(error);
            report.setPass(false);
        } else {
            sjUser.setName(userName);
        }
    }

    private void checkPhone(Row row, SjUser sjUser, ImportReport report, int col) {
        String phone = getCellValue(row, col);

        if (StringUtils.isBlank(phone) || phone.length() < 11) {
            ImportError error = new ImportError();
            error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
            error.setMsgType("手机号错误");
            error.setMessage("手机号不能为空，长度不能小于11位！");
            report.getErrorList().add(error);
            report.setPass(false);
        } else {
            sjUser.setPhone(phone);
        }
    }

    private void checkCompanyName(Row row, SjUser sjUser, ImportReport report, int col) {
        String companyName = getCellValue(row, col);

        if (StringUtils.isBlank(companyName)) {
            ImportError error = new ImportError();
            error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
            error.setMsgType("单位名字错误");
            error.setMessage("单位名字不能为空！");
            report.getErrorList().add(error);
            report.setPass(false);
        } else {
            SjUser sjUser1 = this.getDao().queryByName(companyName, 3);
            if (sjUser1 == null) {
                ImportError error = new ImportError();
                error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                error.setMsgType("单位名字错误");
                error.setMessage("单位名字不存在！");
                report.getErrorList().add(error);
                report.setPass(false);
            }
            sjUser.setCompanyName(companyName);
        }
    }

    /**
     * 先设置单元格格式为string类型然后获取单元格内的值，
     *
     * @param row
     * @param columnIndex
     * @return
     * @CreateDate: 2016-9-17 下午5:43:50
     */
    public static String getCellValue(Row row, int columnIndex) {
        Cell cell = CellUtil.getCell(row, columnIndex);
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        String value = cell.getStringCellValue().trim();
        return value;
    }

    private static Map<Integer, String> titleMap = new HashMap<Integer, String>();

    /**
     * 检查模板是否正确
     *
     * @param workbook
     * @return
     */
    private boolean checkExcelModel(Workbook workbook, ImportReport report) {
        Sheet sheet = workbook.getSheetAt(0);
        Row row0 = sheet.getRow(0);
        if (row0 == null) {
            return false;
        }
        //模板数据
        Set<Integer> set = titleMap.keySet();
        for (Integer key : set) {
            String t1 = row0.getCell(key).toString().trim();
            String t2 = titleMap.get(key);
            if (t1 == null || !t1.equals(t2)) {
                report.setContinueNext(false);
                return false;
            }
        }
        return true;
    }

    /**
     * 下载导入模板
     *
     * @param params
     */
    @SuppressWarnings("rawtypes")
    public void expImportTemplate(Map<String, Object> params) {
        String templateFileName = params.get("tempFileName") + "";
        String outFileName = params.get("outFileName") + "";
        try {
            Workbook workbook = new HSSFWorkbook(new FileInputStream(templateFileName));
            FileOutputStream fileOut = new FileOutputStream(outFileName);
            workbook.write(fileOut);
            fileOut.close();
        } catch (ParsePropertyException e) {
            e.printStackTrace();
            log.error("文件导出--ParsePropertyException", e);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("文件导出--IOException", e);
        }
    }
}