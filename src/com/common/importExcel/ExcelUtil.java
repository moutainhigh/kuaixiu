package com.common.importExcel;

import java.util.List;

import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellUtil;

import com.system.basic.address.entity.Address;

/**
 * Excel工具类.
 * 
 * @author: lijx
 * @CreateDate: 2016-11-7 下午11:24:19
 * @version: V 1.0
 */
public class ExcelUtil {
    
    public static String EXCEL_HIDE_SHEET_NAME = "hidesheet";
    
    /**
     * 先设置单元格格式为string类型然后获取单元格内的值，
     * @param row
     * @param columnIndex
     * @return
     * @CreateDate: 2016-9-17 下午5:43:50
     */
    public static String getCellValue(Row row,int columnIndex){
        Cell cell=CellUtil.getCell(row,columnIndex);
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        String value=cell.getStringCellValue().trim();
        return value;
    }

    /**   
     * 创建一列数据   
     * @param currentRow   
     * @param textList   
     */    
    public static void creatRow(Row currentRow,String[] textList){     
        if(textList!=null&&textList.length>0){     
            int i = 0;     
            for(String cellValue : textList){     
                Cell userNameLableCell = currentRow.createCell(i++);     
                userNameLableCell.setCellValue(cellValue);     
            }     
        }     
    }
    
    /**   
     * 创建一列数据   
     * @param currentRow   
     * @param addressList   
     */    
    public static void creatRow(Row currentRow,List<Address> addressList){     
        if(addressList!=null&&addressList.size()>0){     
            int i = 0;     
            for(Address addr : addressList){     
                Cell userNameLableCell = currentRow.createCell(i++);     
                userNameLableCell.setCellValue(addr.getArea());     
            }     
        }     
    }
    
    /**   
     * 使用已定义的数据源方式设置一个数据验证   
     * @param formulaString   
     * @param firstRow 起始行
     * @param lastRow 终止行
     * @param firstCol 起始列 
     * @param lastCol 终止列 
     * @return   
     */    
    public static DataValidation getDataValidationByFormula(String formulaString,int firstRow,int lastRow,int firstCol,int lastCol){     
        //加载下拉列表内容       
        DVConstraint constraint = DVConstraint.createFormulaListConstraint(formulaString);      
        //设置数据有效性加载在哪个单元格上。       
        //四个参数分别是：起始行、终止行、起始列、终止列          
        CellRangeAddressList regions=new CellRangeAddressList(firstRow,lastRow,firstCol,lastCol);       
        //数据有效性对象      
        DataValidation dataValidationList = new HSSFDataValidation(regions,constraint);     
        //设置输入信息提示信息     
        dataValidationList.createPromptBox("下拉选择提示","请使用下拉方式选择合适的值！");     
        //设置输入错误提示信息     
        dataValidationList.createErrorBox("选择错误提示","你输入的值未在备选列表中，请下拉选择合适的值！");     
        return dataValidationList;     
    }
    
    /**   
     * 创建一个名称   
     * @param workbook   
     */    
    public static void creatExcelNameList(Workbook workbook,String nameCode,int order,int size,boolean cascadeFlag){     
        Name name;     
        name = workbook.createName();     
        name.setNameName(nameCode);     
        name.setRefersToFormula(EXCEL_HIDE_SHEET_NAME+"!"+creatExcelNameList(order,size,cascadeFlag));     
    }
    
    /**   
     * 名称数据行列计算表达式   
     * @param order   
     */    
    private static String creatExcelNameList(int order,int size,boolean cascadeFlag){     
        char start = 'A';     
        if(cascadeFlag){     
            start = 'B';     
            if(size<=25){
                char end = (char)(start+size-1);     
                return "$"+start+"$"+order+":$"+end+"$"+order;     
            }
            else{
                char endPrefix = 'A';     
                char endSuffix = 'A';     
                //26-51之间，包括边界（仅两次字母表计算）
                if ((size-25)/26==0||size==51) {
                    //边界值
                    if ((size-25)%26==0) {
                        endSuffix = (char)('A'+25);
                    }
                    else {
                        endSuffix = (char)('A'+(size-25)%26-1);
                    }     
                }
                else {
                    //51以上     
                    if ((size-25)%26==0) {
                        endSuffix = (char)('A'+25);
                        endPrefix = (char)(endPrefix + (size-25)/26 - 1);
                    }
                    else {
                        endSuffix = (char)('A'+(size-25)%26-1);
                        endPrefix = (char)(endPrefix + (size-25)/26);
                    }     
                }     
                return "$"+start+"$"+order+":$"+endPrefix+endSuffix+"$"+order;     
            }     
        }
        else {
            if (size<=26) {
                char end = (char)(start+size-1);     
                return "$"+start+"$"+order+":$"+end+"$"+order;     
            }
            else {
                char endPrefix = 'A';     
                char endSuffix = 'A';     
                if(size%26==0){
                    endSuffix = (char)('A'+25);
                    if(size>52&&size/26>0){
                        endPrefix = (char)(endPrefix + size/26-2);
                    }     
                }
                else {
                    endSuffix = (char)('A'+size%26-1);
                    if(size>52&&size/26>0){
                        endPrefix = (char)(endPrefix + size/26-1);
                    }     
                }     
                return "$"+start+"$"+order+":$"+endPrefix+endSuffix+"$"+order;     
            }     
        }     
    }
    
    /**
     * @param args
     * @author: lijx
     * @CreateDate: 2016-11-7 下午11:23:57
     */

    public static void main(String[] args) {

    }

}
