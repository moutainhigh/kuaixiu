package com.kuaixiu.nbTelecomSJ.service;


import com.common.base.service.BaseService;
import com.common.importExcel.ExcelUtil;
import com.common.importExcel.ImportError;
import com.common.importExcel.ImportReport;
import com.common.util.NOUtil;
import com.kuaixiu.nbTelecomSJ.dao.NBAreaMapper;
import com.kuaixiu.nbTelecomSJ.entity.NBArea;

import com.kuaixiu.nbTelecomSJ.entity.NBCounty;
import com.system.basic.user.entity.SessionUser;
import net.sf.jxls.exception.ParsePropertyException;
import org.apache.commons.collections.CollectionUtils;
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
 * NBArea Service
 *
 * @CreateDate: 2019-02-22 下午07:26:25
 * @version: V 1.0
 */
@Service("nBAreaService")
public class NBAreaService extends BaseService<NBArea> {
    private static final Logger log = Logger.getLogger(NBAreaService.class);

    @Autowired
    private NBAreaMapper<NBArea> mapper;
    @Autowired
    private NBCountyService nbCountyService;


    public NBAreaMapper<NBArea> getDao() {
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
                List<NBArea> list = checkData(workbook, report);
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
     * 生成新优惠编码
     *
     * @return
     */
    public String createAreaId() {
        String regex = "^\\d+$";
        // 获取新编码
        String areaId = NOUtil.getRandomInteger(6);
        // 检查编码是否存在 且必须包含英文字母
        NBArea area = this.getDao().queryByAreaId(areaId);
        if (area == null && areaId.matches(regex)) {
            return areaId;
        } else {
            return createAreaId();
        }
    }

    /**
     * 保存机型
     *
     * @param nbAreas
     * @param su
     * @return
     * @CreateDate: 2016-9-3 上午12:43:26
     */
    @Transactional
    public void saveData(List<NBArea> nbAreas, SessionUser su) {

        for (NBArea n : nbAreas) {
            /**
             * 根据县分名查询县分
             * 存在县分：
             *         根据县分id，支局名字，包区名字 查询数据NBArea。
             *                  存在包区。则修改数据
             *                  不存在包区则插入数据，随机生成包区id。
             * 不存在县分：
             *         新增县分：
             *                  查询新县分id，
             *                  插入支局，包区数据，随机生成包区id。
             */
            NBCounty nbCounty = nbCountyService.getDao().queryByName(n.getCounty());
            if (nbCounty != null) {
                NBArea nbArea = new NBArea();
                nbArea.setCountyId(nbCounty.getCountyId());
                nbArea.setBranchOffice(n.getBranchOffice());
                nbArea.setAreaName(n.getAreaName());
                List<NBArea> nbAreas1 = this.queryList(nbArea);
                if (CollectionUtils.isEmpty(nbAreas1)) {
                    n.setCountyId(nbCounty.getCountyId());
                    n.setAreaId(Integer.valueOf(createAreaId()));
                    n.setAreaType("商客网格-5级");
                    n.setCreateUserId(su.getUserId());
                    n.setUpdateUserId(su.getUserId());
                    this.add(n);
                } else {
                    if (nbAreas1.size() > 1) {
                        for (int i = 0; i < nbAreas1.size(); i++) {
                            if (i > 0) {
                                this.getDao().delete(nbAreas1.get(i));
                            }
                        }
                    }
                    nbAreas1.get(0).setAreaPerson(n.getAreaPerson());
                    nbAreas1.get(0).setPersonTel(n.getPersonTel());
                    nbAreas1.get(0).setUpdateUserId(su.getUserId());
                    this.saveUpdate(nbAreas1.get(0));
                }
            } else {
                NBCounty nbCounty1 = new NBCounty();
                nbCounty1.setCounty(n.getCounty());
                nbCountyService.add(nbCounty1);
                nbCounty1 = nbCountyService.getDao().queryByName(n.getCounty());
                n.setCountyId(nbCounty1.getCountyId());
                n.setAreaId(Integer.valueOf(createAreaId()));
                n.setAreaType("商客网格-5级");
                n.setCreateUserId(su.getUserId());
                n.setUpdateUserId(su.getUserId());
                this.add(n);
            }
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
    private List<NBArea> checkData(Workbook workbook, ImportReport report) {
        Sheet sheet = workbook.getSheetAt(0);
        int rowNum = sheet.getLastRowNum();
        List<NBArea> list = new ArrayList<NBArea>();

        for (int i = 1; i <= rowNum; i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            NBArea nbArea = new NBArea();
            //验证姓名
            checkCountyName(row, nbArea, report, 0);
            //验证电话
            checkOfficeName(row, nbArea, report, 1);
            //验证部门
            checkAreaName(row, nbArea, report, 2);
            String value = ExcelUtil.getCellValue(row, 3).trim();
            nbArea.setAreaPerson(value);
            value = ExcelUtil.getCellValue(row, 4).trim();
            nbArea.setPersonTel(value);
            list.add(nbArea);
        }

        return list;
    }

    /**
     * 验证机型名称
     *
     * @param row
     * @param nbArea
     * @param report
     * @param col
     * @CreateDate: 2016-9-17 下午5:41:23
     */
    private void checkCountyName(Row row, NBArea nbArea, ImportReport report, int col) {
        //机型id
        String county = getCellValue(row, col);

        if (StringUtils.isBlank(county)) {
            ImportError error = new ImportError();
            error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
            error.setMsgType("县分错误");
            error.setMessage("县分不能为空");
            report.getErrorList().add(error);
            report.setPass(false);
        } else {
            nbArea.setCounty(county);
        }
    }

    /**
     * 验证走访人手机号
     *
     * @param row
     * @param nbArea
     * @param report
     * @param col
     * @CreateDate: 2016-9-17 下午5:41:23
     */
    private void checkOfficeName(Row row, NBArea nbArea, ImportReport report, int col) {
        //机型id
        String branchOffice = getCellValue(row, col);

        if (StringUtils.isBlank(branchOffice)) {
            ImportError error = new ImportError();
            error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
            error.setMsgType("支局错误");
            error.setMessage("支局不能为空！");
            report.getErrorList().add(error);
            report.setPass(false);
        } else {
            nbArea.setBranchOffice(branchOffice);
        }
    }

    /**
     * 验证机型颜色
     *
     * @param row
     * @param nbArea
     * @param report
     * @param col
     * @CreateDate: 2016-9-17 下午5:41:23
     */
    private void checkAreaName(Row row, NBArea nbArea, ImportReport report, int col) {
        //机型id
        String areaName = getCellValue(row, col);

        if (StringUtils.isBlank(areaName)) {
            ImportError error = new ImportError();
            error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
            error.setMsgType("包区错误");
            error.setMessage("包区不能为空！");
            report.getErrorList().add(error);
            report.setPass(false);
        } else {
            nbArea.setAreaName(areaName);
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