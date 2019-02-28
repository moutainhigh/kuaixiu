package com.kuaixiu.nbTelecomSJ.service;


import com.common.base.service.BaseService;
import com.common.exception.SystemException;
import com.common.importExcel.ImportError;
import com.common.importExcel.ImportReport;
import com.common.paginate.Page;
import com.kuaixiu.brand.entity.Brand;
import com.kuaixiu.model.entity.Model;
import com.kuaixiu.model.entity.RepairCost;
import com.kuaixiu.nbTelecomSJ.dao.NBManagerMapper;
import com.kuaixiu.nbTelecomSJ.entity.NBManager;

import com.kuaixiu.order.entity.Order;
import com.kuaixiu.project.entity.Project;
import com.system.basic.user.entity.SessionUser;
import com.system.constant.SystemConstant;
import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
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

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * NBManager Service
 * @CreateDate: 2019-02-22 下午06:34:26
 * @version: V 1.0
 */
@Service("nBManagerService")
public class NBManagerService extends BaseService<NBManager> {
    private static final Logger log= Logger.getLogger(NBManagerService.class);

    @Autowired
    private NBManagerMapper<NBManager> mapper;


    public NBManagerMapper<NBManager> getDao() {
        return mapper;
    }

    //**********自定义方法***********
    private static Map<Integer, String> titleMap = new HashMap<Integer, String>();
    /**
     * 商品导入主入口
     */
    @Transactional
    public void importExcel(MultipartFile file, ImportReport report, SessionUser su) {
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
                //检查表格数据
                List<NBManager> list = checkData(workbook, report);
                if (report.isPass() && list.size() > 0) {
                    //保存数据
                    saveData(list, su);
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
    /**
     * 保存机型
     *
     * @param nbManagers
     * @param su
     * @return
     * @CreateDate: 2016-9-3 上午12:43:26
     */
    @Transactional
    public void saveData(List<NBManager> nbManagers, SessionUser su) {

        for (NBManager n : nbManagers) {
            n.setCreateUserId(su.getUserId());
            n.setUpdateUserId(su.getUserId());
            getDao().add(n);
        }
    }

    /**
     * 检查表格数据
     *
     * @param workbook
     * @param report
     * @return
     * @CreateDate: 2016-9-17 下午6:09:33
     */
    private List<NBManager> checkData(Workbook workbook, ImportReport report) {
        Sheet sheet = workbook.getSheetAt(0);
        int rowNum = sheet.getLastRowNum();
        List<NBManager> list = new ArrayList<NBManager>();
        //存放维修项目名称
        Map<Integer, String> projectName = new HashMap<Integer, String>();
        Row rowTitle = sheet.getRow(0);
        for (int i = 3; ; i++) {
            String title = rowTitle.getCell(i).toString().trim();
            if (StringUtils.isBlank(title)) {
                break;
            }
            projectName.put(i, title);
        }

        if (projectName.size() == 0) {
            ImportError error = new ImportError();
            error.setPosition("第1行,3列");
            error.setMsgType("模板错误");
            error.setMessage("维修费用不能为空");
            report.getErrorList().add(error);
            report.setPass(false);
            return list;
        }

        for (int i = 1; i <= rowNum; i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            NBManager nbManager=new NBManager();
            //验证姓名
            checkMmanagerName(row, nbManager, report, 0);
            //验证电话
            checkManagerTel(row, nbManager, report, 1);
            //验证部门
            checkManagerDepartment(row, nbManager, report, 2);
            list.add(nbManager);
        }

        return list;
    }

    /**
     * 验证机型名称
     *
     * @param row
     * @param nbManager
     * @param report
     * @param col
     * @CreateDate: 2016-9-17 下午5:41:23
     */
    private void checkMmanagerName(Row row, NBManager nbManager, ImportReport report, int col) {
        //机型id
        String managerName = getCellValue(row, col);

        if (StringUtils.isBlank(managerName) || managerName.length() > 32) {
            ImportError error = new ImportError();
            error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
            error.setMsgType("姓名错误");
            error.setMessage("姓名不能为空，长度不能超过32个字符！");
            report.getErrorList().add(error);
            report.setPass(false);
        } else {
            nbManager.setManagerName(managerName);
        }
    }
    /**
     * 验证走访人手机号
     *
     * @param row
     * @param nbManager
     * @param report
     * @param col
     * @CreateDate: 2016-9-17 下午5:41:23
     */
    private void checkManagerTel(Row row, NBManager nbManager, ImportReport report, int col) {
        //机型id
        String managerTel= getCellValue(row, col);

        if (StringUtils.isBlank(managerTel) || managerTel.length() > 11) {
            ImportError error = new ImportError();
            error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
            error.setMsgType("电话错误");
            error.setMessage("电话不能为空，长度不能超过11个字符！");
            report.getErrorList().add(error);
            report.setPass(false);
        } else {
            nbManager.setManagerTel(managerTel);
        }
    }

    /**
     * 验证机型颜色
     *
     * @param row
     * @param nbManager
     * @param report
     * @param col
     * @CreateDate: 2016-9-17 下午5:41:23
     */
    private void checkManagerDepartment(Row row, NBManager nbManager, ImportReport report, int col) {
        //机型id
        String managerDepartment = getCellValue(row, col);

        if (StringUtils.isBlank(managerDepartment)) {
            ImportError error = new ImportError();
            error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
            error.setMsgType("部门错误");
            error.setMessage("部门不能为空！");
            report.getErrorList().add(error);
            report.setPass(false);
        } else {
            nbManager.setDepartment(managerDepartment);
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
     * 已Excel形式导出列表数据
     *
     * @param params
     */
    @SuppressWarnings("rawtypes")
    public void expDataExcel(Map<String, Object> params) {
        String templateFileName = params.get("tempFileName") + "";
        String destFileName = params.get("outFileName") + "";

        //获取查询条件
        String queryStartTime = MapUtils.getString(params,"queryStartTime");
        String queryEndTime = MapUtils.getString(params,"queryEndTime");
        String managerName = MapUtils.getString(params,"managerName");
        String managerTel = MapUtils.getString(params,"managerTel");
        String department = MapUtils.getString(params,"department");
        String idStr = MapUtils.getString(params, "ids");

        NBManager nbManager=new NBManager();
        if (StringUtils.isNotBlank(idStr)) {
            String[] ids = StringUtils.split(idStr, ",");
            nbManager.setQueryIds(Arrays.asList(ids));
        }
        nbManager.setQueryStartTime(queryStartTime);
        nbManager.setQueryEndTime(queryEndTime);
        nbManager.setManagerName(managerName);
        nbManager.setManagerTel(managerTel);
        nbManager.setDepartment(department);
        List<NBManager> nbManagers=this.queryList(nbManager);
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for(NBManager nbManager1:nbManagers){
            nbManager1.setStrCreateTime(sdf.format(nbManager1.getCreateTime()));
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("list", nbManagers);
        XLSTransformer transformer = new XLSTransformer();
        try {
            transformer.transformXLS(templateFileName, map, destFileName);
        } catch (ParsePropertyException e) {
            log.error("文件导出--ParsePropertyException", e);
        } catch (InvalidFormatException e) {
            log.error("文件导出--InvalidFormatException", e);
        } catch (IOException e) {
            log.error("文件导出--IOException", e);
        }
    }
}