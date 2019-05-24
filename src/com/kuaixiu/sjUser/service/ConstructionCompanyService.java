package com.kuaixiu.sjUser.service;


import com.common.base.service.BaseService;
import com.kuaixiu.shop.entity.Shop;
import com.kuaixiu.sjBusiness.entity.SjOrder;
import com.kuaixiu.sjBusiness.service.SjOrderService;
import com.kuaixiu.sjSetMeal.service.SjPoeService;
import com.kuaixiu.sjSetMeal.service.SjSaveNetService;
import com.kuaixiu.sjSetMeal.service.SjSetMealService;
import com.kuaixiu.sjSetMeal.service.SjWifiMonitorTypeService;
import com.kuaixiu.sjUser.dao.ConstructionCompanyMapper;
import com.kuaixiu.sjUser.entity.ConstructionCompany;

import com.kuaixiu.sjUser.entity.CustomerDetail;
import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * ConstructionCompany Service
 *
 * @CreateDate: 2019-05-09 下午03:44:00
 * @version: V 1.0
 */
@Service("constructionCompanyService")
public class ConstructionCompanyService extends BaseService<ConstructionCompany> {
    private static final Logger log = Logger.getLogger(ConstructionCompanyService.class);

    @Autowired
    private ConstructionCompanyMapper<ConstructionCompany> mapper;
    @Autowired
    private SjOrderService orderService;
    @Autowired
    private SjSetMealService sjSetMealService;
    @Autowired
    private SjWifiMonitorTypeService sjWifiMonitorTypeService;
    @Autowired
    private SjPoeService sjPoeService;
    @Autowired
    private SjSaveNetService sjSaveNetService;

    public ConstructionCompanyMapper<ConstructionCompany> getDao() {
        return mapper;
    }

    //**********自定义方法***********


    /**
     * 已Excel形式导出列表数据
     *
     * @param params
     */
    @SuppressWarnings("rawtypes")
    public void expDataExcel(Map<String, Object> params) {
        HttpServletResponse response = (HttpServletResponse) params.get("response");
        String orderId = MapUtils.getString(params, "orderId");
        SjOrder sjOrder = orderService.queryById(orderId);

        //excel标题
        String[] header = new String[]{"项目类型", "设备", "型号", "数量"};
        // 创建一个EXCEL
        HSSFWorkbook wb = new HSSFWorkbook();
        //1.1创建合并单元格对象
        CellRangeAddress callRangeAddress01 = new CellRangeAddress(1, 9, 0, 0);
        CellRangeAddress callRangeAddress02 = new CellRangeAddress(10, 25, 0, 0);
        CellRangeAddress callRangeAddress03 = new CellRangeAddress(26, 38, 0, 0);
        CellRangeAddress callRangeAddress11 = new CellRangeAddress(1, 3, 1, 1);
        CellRangeAddress callRangeAddress12 = new CellRangeAddress(4, 7, 1, 1);
        CellRangeAddress callRangeAddress13 = new CellRangeAddress(8, 9, 1, 1);
        CellRangeAddress callRangeAddress14 = new CellRangeAddress(10, 15, 1, 1);
        CellRangeAddress callRangeAddress15 = new CellRangeAddress(16, 19, 1, 1);
        CellRangeAddress callRangeAddress16 = new CellRangeAddress(20, 25, 1, 1);
        //2.创建工作表
        HSSFSheet sheet = wb.createSheet("施工单");
        //2.1加载合并单元格对象
        sheet.addMergedRegion(callRangeAddress01);
        sheet.addMergedRegion(callRangeAddress02);
        sheet.addMergedRegion(callRangeAddress03);
        sheet.addMergedRegion(callRangeAddress11);
        sheet.addMergedRegion(callRangeAddress12);
        sheet.addMergedRegion(callRangeAddress13);
        sheet.addMergedRegion(callRangeAddress14);
        sheet.addMergedRegion(callRangeAddress15);
        sheet.addMergedRegion(callRangeAddress16);
        // 第i个sheet第二行为列名
        HSSFRow rowFirst = sheet.createRow(0);
        // 写标题
        for (int j = 0; j < header.length; j++) {
            // 获取第一行的每一个单元格
            HSSFCell cell = rowFirst.createCell(j);
            // 往单元格里面写入值
            cell.setCellValue(header[j]);
        }
        setSheet(sheet,sjOrder);

        // 写出文件（path为文件路径含文件名）
        OutputStream os = null;
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/x-download");
            String filedisplay = "施工单导出.xls";
            //防止文件名含有中文乱码
            filedisplay = new String(filedisplay.getBytes("gb2312"), "ISO8859-1");
            response.setHeader("Content-Disposition", "attachment;filename=" + filedisplay);

            os = response.getOutputStream();
            wb.write(os);
        } catch (Exception e) {
            System.out.println("导出文件出错了.....\n" + e.getMessage());
        } finally {
            try {
                os.flush();
                os.close();
            } catch (IOException e) {
                System.out.println("导出文件出错了.....\n" + e.getMessage());
            }
        }
    }

    private void setSheet(HSSFSheet sheet,SjOrder sjOrder){
        int number=1;
        HSSFRow rower = sheet.createRow(number);
        String[] titles = {"云wifi", "AP", "普通型双频AP(50元/月)"};//""为占位字符串
        for (int i = 0; i < titles.length; i++) {
            HSSFCell cell = rower.createCell(i);
            cell.setCellValue(titles[i]);
        }
        if(sjOrder.getModelWifiId() - 7+1==number){
            HSSFCell cell01 = rower.createCell(3);
            cell01.setCellValue(sjOrder.getModelWifiNum());
        }
        number=number+1;
        HSSFRow rowTwo = sheet.createRow(number);
        HSSFCell cell1 = rowTwo.createCell(2);
        cell1.setCellValue("吸顶型双频AP(60元/月)");
        if(sjOrder.getModelWifiId() - 7+1==number){
            HSSFCell cell01 = rowTwo.createCell(3);
            cell01.setCellValue(sjOrder.getModelWifiNum());
        }
        number=number+1;
        HSSFRow rowThree = sheet.createRow(number);
        HSSFCell cell2 = rowThree.createCell(2);
        cell2.setCellValue("86面板型AP(35元/月)");
        if(sjOrder.getModelWifiId() - 7+1==number){
            HSSFCell cell01 = rowThree.createCell(3);
            cell01.setCellValue(sjOrder.getModelWifiNum());
        }
        number=number+1;
        HSSFRow rowFour = sheet.createRow(number);
        String[] titles4 = {"POE", "云wifi1口POE模块（免费）"};//""为占位字符串
        for (int i = 0; i < titles4.length; i++) {
            HSSFCell cell = rowFour.createCell(i+1);
            cell.setCellValue(titles4[i]);
        }
        if(sjOrder.getPoeWifiId() - 1+1==number){
            HSSFCell cell01 = rowFour.createCell(3);
            cell01.setCellValue(sjOrder.getPoeWifiNum());
        }
        number=number+1;
        HSSFRow rowFive = sheet.createRow(number);
        HSSFCell cell3 = rowFive.createCell(2);
        cell3.setCellValue("千兆云监控5口PEO注入器30元/月");
        if(sjOrder.getPoeWifiId() - 1+1==number){
            HSSFCell cell01 = rowThree.createCell(3);
            cell01.setCellValue(sjOrder.getPoeWifiNum());
        }
        number=number+1;
        HSSFRow rowSix = sheet.createRow(number);
        HSSFCell cell4 = rowSix.createCell(2);
        cell4.setCellValue("千兆云监控5口PEO注入器70元/月");
        if(sjOrder.getPoeWifiId() - 1+1==number){
            HSSFCell cell01 = rowSix.createCell(3);
            cell01.setCellValue(sjOrder.getPoeWifiNum());
        }
        number=number+1;
        HSSFRow rowSeven= sheet.createRow(number);
        HSSFCell cell5 = rowSeven.createCell(2);
        cell5.setCellValue("千兆云监控8口PEO注入器80元/月");
        if(sjOrder.getPoeWifiId() - 1+1==number){
            HSSFCell cell01 = rowSeven.createCell(3);
            cell01.setCellValue(sjOrder.getPoeWifiNum());
        }
        number=number+1;
        HSSFRow rowEight = sheet.createRow(number);
        String[] titles8 = {"NET设备", "A类NET(1O0人以下)50元/月"};//""为占位字符串
        for (int i = 0; i < titles8.length; i++) {
            HSSFCell cell = rowEight.createCell(i+1);
            cell.setCellValue(titles8[i]);
        }
        if(sjOrder.getStorageWifiId()+1==number){
            HSSFCell cell01 = rowEight.createCell(3);
            cell01.setCellValue(sjOrder.getPoeWifiNum());
        }
        number=number+1;
        HSSFRow rowNien= sheet.createRow(number);
        HSSFCell cell6 = rowNien.createCell(2);
        cell6.setCellValue("B类NET（100人-500人以下）200元/月");
        if(sjOrder.getStorageWifiId()+1==number){
            HSSFCell cell01 = rowNien.createCell(3);
            cell01.setCellValue(sjOrder.getPoeWifiNum());
        }

        number=number+1;
        HSSFRow rowTem = sheet.createRow(number);
        String[] title10 = {"云监控", "摄像头", "200万枪机（40元/月）"};
        for (int i = 0; i < titles.length; i++) {
            HSSFCell cell = rowTem.createCell(i);
            cell.setCellValue(title10[i]);
        }
        if(sjOrder.getModelId() +8+1==number){
            HSSFCell cell01 = rowTem.createCell(3);
            cell01.setCellValue(sjOrder.getModelNum());
        }
        String[] title11 = {"200万半球（50元/月）", "200万云台（50/月）", "100万枪机（40元/月）","100万半球（40元/月）","100万云台（40/月）"};
        for (int i = 0; i < title11.length; i++) {
            number=number+1;
            HSSFRow row11= sheet.createRow(number);
            HSSFCell cell11 = row11.createCell(2);
            cell11.setCellValue(title11[i]);
            if(sjOrder.getModelId()+8+1==number){
                HSSFCell cell01 = row11.createCell(3);
                cell01.setCellValue(sjOrder.getModelNum());
            }
        }

        number=number+1;
        HSSFRow row16 = sheet.createRow(number);
        String[] titles16 = {"POE", "千兆云监控1口PEO注入器5元/月"};//""为占位字符串
        for (int i = 0; i < titles16.length; i++) {
            HSSFCell cell = row16.createCell(i+1);
            cell.setCellValue(titles16[i]);
        }
        if(sjOrder.getPoeId()+15==number){
            HSSFCell cell01 = row16.createCell(3);
            cell01.setCellValue(sjOrder.getPoeNum());
        }

        String[] title17 = {"千兆云监控5口PEO注入器30元/月", "千兆云监控8口PEO注入器35元/月", "千兆云监控16口PEO注入器35元/月"};
        for (int i = 0; i < title17.length; i++) {
            number=number+1;
            HSSFRow row17= sheet.createRow(number);
            HSSFCell cell17 = row17.createCell(2);
            cell17.setCellValue(title17[i]);
            if(sjOrder.getPoeId()+15==number){
                HSSFCell cell01 = row17.createCell(3);
                cell01.setCellValue(sjOrder.getPoeNum());
            }
        }
        number=number+1;
        HSSFRow row19 = sheet.createRow(number);
        String[] titles19 = {"存储", "云存储（7天）"};//""为占位字符串
        for (int i = 0; i < titles19.length; i++) {
            HSSFCell cell = row19.createCell(i+1);
            cell.setCellValue(titles19[i]);
        }
        if(sjOrder.getStorageId()+19==number){
            HSSFCell cell01 = row19.createCell(3);
            cell01.setCellValue(sjOrder.getStorageNum());
        }

        String[] title20 = {"云存储（15天）", "TF卡（64G）", "TF卡（128G）","硬盘录像机（2T）","硬盘录像机（4T）"};
        for (int i = 0; i < title20.length; i++) {
            number=number+1;
            HSSFRow row20= sheet.createRow(number);
            HSSFCell cell20 = row20.createCell(2);
            cell20.setCellValue(title20[i]);
            if(sjOrder.getStorageId()+19==number){
                HSSFCell cell01 = row20.createCell(3);
                cell01.setCellValue(sjOrder.getStorageNum());
            }
        }

    }

}