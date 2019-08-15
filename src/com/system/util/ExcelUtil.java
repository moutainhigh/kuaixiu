package com.system.util;

import com.alibaba.fastjson.JSONObject;
import com.kuaixiu.recycle.recycleCard.CardEnum;
import com.kuaixiu.videoCard.entity.VideoCard;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import java.io.FileInputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @Author gqa
 * @Date 2019/4/17 16:55
 * @Description
 **/
public class ExcelUtil {





    public static Object testProjectList(String file) throws Exception {
        List<VideoCard> list = new ArrayList<VideoCard>();

        XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(ResourceUtils.getFile(file)));
        //Excel工作表  获取第几个sheet 从0开始    sheetAt.getLastRowNum()-6
        XSSFSheet sheetAt = wb.getSheetAt(0);
        System.out.println("文件行数："+sheetAt.getLastRowNum());
        for(int i=1; i<sheetAt.getLastRowNum()+1; i++) {
            XSSFRow row = sheetAt.getRow(i);

            String type = (String) getJavaValue(row.getCell(0));  // 卡密类型
            String price = (String) getJavaValue(row.getCell(1)); // 价格
            String cardId = (String) getJavaValue(row.getCell(2));// 卡密
            String time = (String) getJavaValue(row.getCell(3));  // 有效期

            if(StringUtils.isEmpty(cardId)){
                continue;
            }





            VideoCard card=new VideoCard();
            card.setCardId(cardId);
            card.setType(CardEnum.getType(type));
            card.setPrice(new BigDecimal(price));
            card.setValidityTime(time);






            list.add(card);
        }

        System.out.println("共有 " + list.size() + " 条数据：");
        System.out.println(JSONObject.toJSONString(list.get(0)));
        return list;
    }



    /**
     * @Author gqa
     * @Description 转换不同类型
     * @Date 18:14 2019/4/17
     * @Param [cell]
     * @return java.lang.Object
     **/
    public static Object getJavaValue(XSSFCell cell) {
        Object o = null;
        if(cell==null){
            return "";
        }
        int cellType = cell.getCellType();
        switch (cellType) {
            case XSSFCell.CELL_TYPE_BLANK:
                o = "";
                break;
            case XSSFCell.CELL_TYPE_BOOLEAN:
                o = cell.getBooleanCellValue();
                break;
            case XSSFCell.CELL_TYPE_ERROR:
                o = "Bad value!";
                break;
            case XSSFCell.CELL_TYPE_NUMERIC:
                o = getValueOfNumericCell(cell);
                break;
            case XSSFCell.CELL_TYPE_FORMULA:
                try {
                    o = getValueOfNumericCell(cell);
                } catch (IllegalStateException e) {
                    try {
                        o = cell.getRichStringCellValue().toString();
                    } catch (IllegalStateException e2) {
                        o = cell.getErrorCellString();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                o = cell.getRichStringCellValue().getString();
        }
        return o;
    }



    // 获取数字类型的cell值
    private static Object getValueOfNumericCell(XSSFCell cell) {
        Boolean isDate = DateUtil.isCellDateFormatted(cell);
        Double d = cell.getNumericCellValue();
        Object o = null;
        if (isDate) {
            o = DateFormat.getDateTimeInstance()
                    .format(cell.getDateCellValue());
        } else {
            o = getRealStringValueOfDouble(d);
        }
        return o;
    }


    // 处理科学计数法与普通计数法的字符串显示，尽最大努力保持精度
    private static String getRealStringValueOfDouble(Double d) {
        String doubleStr = d.toString();
        boolean b = doubleStr.contains("E");
        int indexOfPoint = doubleStr.indexOf('.');
        if (b) {
            int indexOfE = doubleStr.indexOf('E');
            // 小数部分
            BigInteger xs = new BigInteger(doubleStr.substring(indexOfPoint
                    + BigInteger.ONE.intValue(), indexOfE));
            // 指数
            int pow = Integer.valueOf(doubleStr.substring(indexOfE
                    + BigInteger.ONE.intValue()));
            int xsLen = xs.toByteArray().length;
            int scale = xsLen - pow > 0 ? xsLen - pow : 0;
            doubleStr = String.format("%." + scale + "f", d);
        } else {
            java.util.regex.Pattern p = Pattern.compile(".0$");
            java.util.regex.Matcher m = p.matcher(doubleStr);
            if (m.find()) {
                doubleStr = doubleStr.replace(".0", "");
            }
        }
        return doubleStr;
    }


}



