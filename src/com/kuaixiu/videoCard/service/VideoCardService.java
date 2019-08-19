package com.kuaixiu.videoCard.service;


import com.common.base.service.BaseService;
import com.common.exception.SystemException;
import com.common.importExcel.ExcelUtil;
import com.common.importExcel.ImportError;
import com.common.importExcel.ImportReport;
import com.kuaixiu.recycle.recycleCard.CardEnum;
import com.kuaixiu.station.entity.Station;
import com.kuaixiu.videoCard.dao.VideoCardMapper;
import com.kuaixiu.videoCard.entity.VideoCard;

import com.kuaixiu.videoUserRel.entity.VideoUserRel;
import com.system.basic.user.entity.SessionUser;
import net.sf.jxls.exception.ParsePropertyException;
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
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * VideoCard Service
 * @CreateDate: 2019-08-15 下午03:37:26
 * @version: V 1.0
 */
@Service("videoCardService")
public class VideoCardService extends BaseService<VideoCard> {
    private static final Logger log= Logger.getLogger(VideoCardService.class);

    @Autowired
    private VideoCardMapper<VideoCard> mapper;


    public VideoCardMapper<VideoCard> getDao() {
        return mapper;
    }

    //**********自定义方法***********
    public List<VideoCard> getVideoUser(VideoUserRel rel) {
        return mapper.getVideoUser(rel);
    }


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
                List<VideoCard> list=checkData(workbook, report, su);
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


    @Transactional
    public void saveData(List<VideoCard> list, SessionUser su){
        for(VideoCard s:list){
            VideoCard v=new VideoCard();
            v.setIsUse(0);
            v.setCreateTime(new Date());
            v.setCreateUserid(su.getUserId());
            v.setCardId(s.getCardId());
            v.setType(s.getType());
            v.setPrice(s.getPrice());
            v.setValidityTime(s.getValidityTime());
            mapper.add(v);
        }
    }

    /**
     * 导入模板字段
     */
    private static Map<Integer,String> titleMap=new HashMap<Integer, String>();
    static {
        titleMap.put(0, "卡类型（必填）");
        titleMap.put(1, "金额（必填）");
        titleMap.put(2, "卡密（必填）");
        titleMap.put(3, "有效期（必填）");
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
    private List<VideoCard> checkData(Workbook workbook,ImportReport report,SessionUser su){
        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
        Sheet sheet=workbook.getSheetAt(0);
        int rowNum=sheet.getLastRowNum();
        //添加的站点集合
        List<VideoCard> stationList=new ArrayList<VideoCard>();

        for(int i = 1; i <= rowNum; i++){
            Row row = sheet.getRow(i);
            if(row==null){
                continue;
            }
            VideoCard s=new VideoCard();
            int col = 0;
            String value = ExcelUtil.getCellValue(row, col).trim();
            if (StringUtils.isBlank(value) || value.length() >36) {
                ImportError error = new ImportError();
                error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                error.setMessage("卡类型不能为空，长度不能超过36个字符！");
                report.getErrorList().add(error);
                report.setPass(false);
            } else {
                s.setType(CardEnum.getType(value));
            }
            //
            col++;
            value = ExcelUtil.getCellValue(row, col).trim();
            if (StringUtils.isBlank(value) || value.length() > 36) {
                ImportError error = new ImportError();
                error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                error.setMessage("金额不能为空，长度不能超过36个字符！");
                report.getErrorList().add(error);
                report.setPass(false);
            } else {
                s.setPrice(new BigDecimal(value));
            }
            //联系人
            col++;
            value = ExcelUtil.getCellValue(row, col).trim();
            if (StringUtils.isBlank(value) || value.length() > 36) {
                ImportError error = new ImportError();
                error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                error.setMessage("卡密不能为空，长度不能超过36个字符！");
                report.getErrorList().add(error);
                report.setPass(false);
            } else {
                s.setCardId(value);
            }
            //验机电话
            col++;
            value = ExcelUtil.getCellValue(row, col).trim();
            if (StringUtils.isBlank(value)||value.length() > 36) {
                ImportError error = new ImportError();
                error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                error.setMessage("有效期不能为空，长度不能超过36个字符！");
                report.getErrorList().add(error);
                report.setPass(false);
            } else {
                s.setValidityTime(value);
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
}