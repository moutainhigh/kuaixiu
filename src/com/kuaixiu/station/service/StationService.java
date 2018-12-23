package com.kuaixiu.station.service;

import com.common.base.service.BaseService;
import com.common.exception.SystemException;
import com.common.importExcel.ExcelUtil;
import com.common.importExcel.ImportError;
import com.common.importExcel.ImportReport;
import com.kuaixiu.station.dao.StationMapper;
import com.kuaixiu.station.entity.Station;
import com.system.basic.user.entity.SessionUser;
import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Auther: anson
 * @Date: 2018/7/26
 * @Description:站点服务类
 */
@Service
public class StationService extends BaseService<Station> {

    private static final Logger log= Logger.getLogger(StationService.class);
    @Autowired
    private StationMapper<Station> mapper;

    @Override
    public StationMapper<Station> getDao() {
        return mapper;
    }


    /**
     * 通过名称模糊查询站点
     * @param s
     * @return
     */
    public List<Station> queryByName(Station s){
       return mapper.queryByName(s);
    }

    /**
     * 站点批量导入
     * @param file
     * @param report
     * @param su
     */
    @Transactional
    public void importExcel(MultipartFile file, ImportReport report, SessionUser su){
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
                List<Station> list=checkData(workbook, report, su);
                if(report.isPass() && list.size() > 0){
                    //保存数据
                    saveData(list, su);
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
     * 站点保存数据
     * @param list
     * @param su
     */
    @Transactional
    public void saveData(List<Station> list, SessionUser su){
        for(Station s:list){
            mapper.add(s);
        }
    }


    /**
     * 导入模板字段
     */
    private static Map<Integer,String> titleMap=new HashMap<Integer, String>();
    static {
        titleMap.put(0, "站点id");
        titleMap.put(1, "站点名称");
        titleMap.put(2, "联系人");
        titleMap.put(3, "验机电话");
        titleMap.put(4, "地址");
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
     * @param su
     * @return
     */
    private List<Station> checkData(Workbook workbook,ImportReport report,SessionUser su){
        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
        Sheet sheet=workbook.getSheetAt(0);
        int rowNum=sheet.getLastRowNum();
        //添加的站点集合
        List<Station> stationList=new ArrayList<Station>();

        for(int i = 1; i <= rowNum; i++){
            Row row = sheet.getRow(i);
            if(row==null){
                continue;
            }
            Station s=new Station();

            int col = 0;
            //站点id
            String value = ExcelUtil.getCellValue(row, col).trim();
            if (StringUtils.isBlank(value) || value.length() >36) {
                ImportError error = new ImportError();
                error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                error.setMessage("站点id不能为空，长度不能超过36个字符！");
                report.getErrorList().add(error);
                report.setPass(false);
            } else {
                s.setId(value);
            }
            //站点名称
            col++;
            value = ExcelUtil.getCellValue(row, col).trim();
            if (StringUtils.isBlank(value) || value.length() > 36) {
                ImportError error = new ImportError();
                error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                error.setMessage("站点名称不能为空，长度不能超过36个字符！");
                report.getErrorList().add(error);
                report.setPass(false);
            } else {
                s.setStationName(value);
            }
            //联系人
            col++;
            value = ExcelUtil.getCellValue(row, col).trim();
            if (StringUtils.isBlank(value) || value.length() > 36) {
                ImportError error = new ImportError();
                error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                error.setMessage("联系人不能为空，长度不能超过36个字符！");
                report.getErrorList().add(error);
                report.setPass(false);
            } else {
                s.setName(value);
            }
            //验机电话
            col++;
            value = ExcelUtil.getCellValue(row, col).trim();
            if (StringUtils.isBlank(value)||value.length() > 36) {
                ImportError error = new ImportError();
                error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                error.setMessage("验机电话不能为空，长度不能超过36个字符！");
                report.getErrorList().add(error);
                report.setPass(false);
            } else {
                s.setTel(value);
            }
            //地址
            col++;
            value = ExcelUtil.getCellValue(row, col).trim();
            if (StringUtils.isBlank(value)||value.length() > 64) {
                ImportError error = new ImportError();
                error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                error.setMessage("地址信息不能为空，长度不能超过64个字符！");
                report.getErrorList().add(error);
                report.setPass(false);
            } else {
                s.setAddress(value);
            }
            stationList.add(s);
        }
        return stationList;
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


    /**
     * Excel形式导出列表数据
     * @param params
     */
    @SuppressWarnings("rawtypes")
    public void expDataExcel(Map<String, Object> params) {
        String templateFileName = params.get("tempFileName") + "";
        String destFileName = params.get("outFileName") + "";
        // 获取查询条件  站点id  站点名称  联系人  验机电话
        String stationId = MapUtils.getString(params, "query_stationId");
        String stationName = MapUtils.getString(params, "query_stationName");
        String name = MapUtils.getString(params, "query_name");
        String tel = MapUtils.getString(params, "query_tel");
        Station station=new Station();
        station.setId(stationId);
        station.setStationName(stationName);
        station.setName(name);
        station.setTel(tel);

        String idStr = MapUtils.getString(params, "ids");
        if (StringUtils.isNotBlank(idStr)) {
            String[] ids = StringUtils.split(idStr, ",");
            station.setQueryIds(Arrays.asList(ids));
        }

        List<Station> stationList = queryList(station);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("stationList", stationList);
        XLSTransformer transformer = new XLSTransformer();
        try {
            transformer.transformXLS(templateFileName, map, destFileName);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (org.apache.poi.openxml4j.exceptions.InvalidFormatException e) {
            e.printStackTrace();
        }


    }


    /**
     * 更新站点库存
     * @param station
     * @return
     */
    public int updateById(Station station){
        return  mapper.updateById(station);
    }


}
