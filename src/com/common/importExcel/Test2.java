package com.common.importExcel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class Test2 {

    private static String[] provinceList = { "广东省", "山东省" };

    
    public static void main(String[] args) {
        Map<String, String[]> provinces = new HashMap<String, String[]>();
        String[] citys = {"济南", "深圳"};
        provinces.put("广东省", citys);
        citys = new String[]{ "济南", "临沂"};
        provinces.put("山东省", citys);
        citys = new String[]{ "广州1", "广州2"};
        provinces.put("济南", citys);
        citys = new String[]{ "深圳1", "深圳2"};
        provinces.put("深圳", citys);
        citys = new String[]{ "历城", "历下"};
        provinces.put("济南", citys);
        citys = new String[]{ "兰陵", "罗庄"};
        provinces.put("临沂", citys);
        
        FileOutputStream fileOut;
        try {
            Workbook workbook = new HSSFWorkbook(new FileInputStream("e:/ex_provider_import.xls"));
            // 数据字典页
            Sheet hideInfoSheet = workbook.createSheet(ExcelUtil.EXCEL_HIDE_SHEET_NAME);
            // 在隐藏页设置选择信息
            // 第一行设置省份
            Row row = hideInfoSheet.createRow(0);
            ExcelUtil.creatRow(row, provinceList);
            ExcelUtil.creatExcelNameList(workbook, "province", 1, provinceList.length, false);
            int rowIndex = 1;
            //循环省份
            for(String prov : provinceList){
                //获取市
                String[] cts = provinces.get(prov);
                row = hideInfoSheet.createRow(rowIndex);
                ExcelUtil.creatRow(row, cts);
                ExcelUtil.creatExcelNameList(workbook, prov, rowIndex + 1, cts.length, false);
                rowIndex++;
                for(String ct : cts){
                    String[] st = provinces.get(ct);
                    row = hideInfoSheet.createRow(rowIndex);
                    ExcelUtil.creatRow(row, st);
                    ExcelUtil.creatExcelNameList(workbook, prov+ct, rowIndex + 1, st.length, false);
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
                                "province", 1, 65535, 10, 10);
                        sheet.addValidationData(dataValidationList);
                        // 城市选项添加验证数据
                        dataValidationList = ExcelUtil.getDataValidationByFormula(
                                "INDIRECT(INDIRECT(\"$k$\"&ROW()))", 1, 65535, 11, 11);
                        sheet.addValidationData(dataValidationList);
                        // 城市选项添加验证数据
                        dataValidationList = ExcelUtil.getDataValidationByFormula(
                                "INDIRECT(INDIRECT(\"$k$\"&ROW())&INDIRECT(\"$l$\"&ROW()))", 1, 65535, 12, 12);
                        sheet.addValidationData(dataValidationList);
                    }
                }
            }
            
            fileOut = new FileOutputStream("e://excel_test.xls");
            workbook.write(fileOut);
            fileOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
