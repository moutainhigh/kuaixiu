package com.kuaixiu.groupShortUrl.service;


import com.common.base.service.BaseService;
import com.common.importExcel.ImportError;
import com.common.importExcel.ImportReport;
import com.kuaixiu.groupShortUrl.dao.HsGroupShortUrlMobileMapper;
import com.kuaixiu.groupShortUrl.entity.HsGroupShortUrlMobile;

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
 * HsGroupShortUrlMobile Service
 * @CreateDate: 2019-06-26 上午09:28:20
 * @version: V 1.0
 */
@Service("hsGroupShortUrlMobileService")
public class HsGroupShortUrlMobileService extends BaseService<HsGroupShortUrlMobile> {
    private static final Logger log= Logger.getLogger(HsGroupShortUrlMobileService.class);

    @Autowired
    private HsGroupShortUrlMobileMapper<HsGroupShortUrlMobile> mapper;


    public HsGroupShortUrlMobileMapper<HsGroupShortUrlMobile> getDao() {
        return mapper;
    }

    //**********自定义方法***********
    private static Map<Integer, String> titleMap = new HashMap<Integer, String>();

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
                List<HsGroupShortUrlMobile> list = checkData(workbook, report);
                if (report.isPass() && list.size() > 0) {
                    deleteNull();
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

    public void deleteNull(){
        this.getDao().deleteNull();
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

    @Transactional
    public void saveData(List<HsGroupShortUrlMobile> groupMobiles, SessionUser su) {
        for (HsGroupShortUrlMobile m : groupMobiles) {
            HsGroupShortUrlMobile groupMobile = new HsGroupShortUrlMobile();
            groupMobile.setId(UUID.randomUUID().toString().replace("-", ""));
            groupMobile.setMobile(m.getMobile());
            this.add(groupMobile);
        }
    }

    private List<HsGroupShortUrlMobile> checkData(Workbook workbook, ImportReport report) {
        Sheet sheet = workbook.getSheetAt(0);
        int rowNum = sheet.getLastRowNum();
        List<HsGroupShortUrlMobile> list = new ArrayList<HsGroupShortUrlMobile>();
        for (int i = 1; i <= rowNum; i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            HsGroupShortUrlMobile groupMobile = new HsGroupShortUrlMobile();
            //验证手机号
            checkPhone(row, groupMobile, report, 0);
            list.add(groupMobile);
        }
        return list;
    }

    private void checkPhone(Row row, HsGroupShortUrlMobile groupMobile, ImportReport report, int col) {
        String phone = getCellValue(row, col);

        if (StringUtils.isBlank(phone) || phone.length() < 11) {
            ImportError error = new ImportError();
            error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
            error.setMsgType("手机号错误");
            error.setMessage("手机号不能为空，长度不能小于11位！");
            report.getErrorList().add(error);
            report.setPass(false);
        }

        groupMobile.setMobile(phone);
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