package com.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 数值格式化类.
 * @author Administrator
 *
 */
public class NumberUtils {
    // 数字格式化默认格式
    private static final String DEFAULT_DF = "###,###,###.##";
    // 数字精确默认位数
    private static final int DEFAULT_SCALE = 2;
    //2015-03-23 规则为先四舍五入到5位，再到2位
    private static final int FIRST_SCALE = 5;
    
    /**
     * 转成int.
     * @param obj
     * @return
     */
    public static int toInt(Object obj) {
        return Integer.parseInt(StringUtils.defaultIfEmpty(ObjectUtils.toString(obj), "0"));
    }
    
    /**
     * 转成long.
     * @param obj
     * @return
     */
    public static long toLong(Object obj) {
        return Long.parseLong(StringUtils.defaultIfEmpty(ObjectUtils.toString(obj), "0"));
    }
    /**
     * 转成float.
     * @param obj
     * @return
     */
    public static float toFloat(Object obj) {
        return Float.parseFloat(StringUtils.defaultIfEmpty(ObjectUtils.toString(obj), "0"));
    }
    
    /**
     * 转成double.
     * @param obj
     * @return
     */
    public static double toDouble(Object obj) {
        return Double.parseDouble(StringUtils.defaultIfEmpty(ObjectUtils.toString(obj), "0"));
    }
    
    /**
     * 把Object转成Integer.
     * 
     * @param obj
     * @return
     */
    public static Integer createInteger(Object obj) {
        String val = StringUtils.defaultIfEmpty(ObjectUtils.toString(obj), null);
        return org.apache.commons.lang3.math.NumberUtils.createInteger(val);
    }
    
    /**
     * 把Object转成Long.
     * 
     * @param obj
     * @return
     */
    public static Long createLong(Object obj) {
        String val = StringUtils.defaultIfEmpty(ObjectUtils.toString(obj), null);
        return org.apache.commons.lang3.math.NumberUtils.createLong(val);
    }
    
    /**
     * double数据四舍五入.
     * @param d 四舍五入的值
     * @param n 保留小数位
     * @return
     */
    public static double round(double d,int n) {
        BigDecimal bd=new BigDecimal(d); 
        bd=bd.setScale(n, BigDecimal.ROUND_HALF_UP);
    
        return bd.doubleValue();
    }
    
    /**
     * BigDecimal数据四舍五入.
     * @param d 四舍五入的值
     * @param scale 保留小数位
     * @return
     */
    public static BigDecimal round(BigDecimal d, int scale) {
        d=d.setScale(scale, BigDecimal.ROUND_HALF_UP);
        return d;
    }
    
    /**
     * 将给定的数据按默认格式格式化.
     * 
     * @param obj 格式的数据
     * @return
     */
    public static String format(Object obj) {
        DecimalFormat df2 = new DecimalFormat(DEFAULT_DF);
        df2.setRoundingMode(RoundingMode.HALF_UP);
        return df2.format(obj);
    }
    
    /**
     * 将double数据格式化.
     * 
     * @param v 格式的数据
     * @return
     */
    public static String format(double v) {
        DecimalFormat df = new DecimalFormat();
        df.setGroupingUsed(false);
        return df.format(v);
    }
    
    /**
     * BigDecimal加法运算.
     * 
     * @param v1 被加数
     * @param v2 加数
     * @return 两个参数的和
     */
    public static BigDecimal add(Object v1, Object v2) {
        BigDecimal b1 = getBigDecimal(v1);   
        BigDecimal b2 = getBigDecimal(v2);
        return add(b1, b2);
    }
    
    /**
     * BigDecimal加法运算.
     * 
     * @param v1 被加数
     * @param v2 加数
     * @return 两个参数的和
     */
    public static BigDecimal add(BigDecimal v1, Object v2) {
        BigDecimal b2 = getBigDecimal(v2);
        return add(v1, b2);
    }
    
    /**
     * BigDecimal加法运算.
     * 
     * @param v1 被加数
     * @param v2 加数
     * @return 两个参数的和
     */
    public static BigDecimal add(BigDecimal v1, BigDecimal v2) {
        return v1.add(v2);
    }
    
    /**
     * BigDecimal减法运算.
     * 
     * @param v1 被减数
     * @param v2 减数
     * @return
     */
    public static BigDecimal sub(Object v1, Object v2) {
        BigDecimal b1 = getBigDecimal(v1);   
        BigDecimal b2 = getBigDecimal(v2);
        return sub(b1, b2);
    }
    
    /**
     * BigDecimal减法运算.
     * 
     * @param v1 被减数
     * @param v2 减数
     * @return
     */
    public static BigDecimal sub(BigDecimal v1, Object v2) {  
        BigDecimal b2 = getBigDecimal(v2);
        return sub(v1, b2);
    }
    
    /**
     * BigDecimal减法运算.
     * 
     * @param v1 被减数
     * @param v2 减数(v1-v2)
     * @return
     */
    public static BigDecimal sub(BigDecimal v1, BigDecimal v2) {
        return v1.subtract(v2);
    }
    /**
     * BigDecimal乘法运算不进行四舍五入运算
     * @param v1
     * @param v2
     * @return
     */
    public static BigDecimal mulNoRound(Object v1, Object v2) {
        BigDecimal b1 = getBigDecimal(v1);   
        BigDecimal b2 = getBigDecimal(v2);   
        return b1.multiply(b2);
    }
    
    /**
     * BigDecimal乘法运算，默认四舍五入保留两位小数.
     * 
     * @param v1 被乘数
     * @param v2 乘数
     * @return
     */
    public static BigDecimal mul(Object v1, Object v2) {
        BigDecimal b1 = getBigDecimal(v1);   
        BigDecimal b2 = getBigDecimal(v2);   
        return mul(b1, b2);
    }
    
    /**
     * BigDecimal乘法运算，默认四舍五入保留两位小数.
     * 
     * @param v1 被乘数
     * @param v2 乘数
     * @return
     */
    public static BigDecimal mul(BigDecimal v1, Object v2) {
        BigDecimal b2 = getBigDecimal(v2);   
        return mul(v1, b2);
    }
    
    /**
     * BigDecimal乘法运算，默认四舍五入保留两位小数.
     * 
     * @param v1 被乘数
     * @param v2 乘数
     * @return
     */
    public static BigDecimal mul(BigDecimal v1, BigDecimal v2) {
        //2015-03-23 规则为先四舍五入到5位，再到2位
        //return round(v1.multiply(v2), DEFAULT_SCALE);
        return round(round(v1.multiply(v2), FIRST_SCALE), DEFAULT_SCALE);
    }
    /**
     * BigDecimal乘法运算，四舍五入保留scale位小数.
     * 
     * @param v1 被乘数
     * @param v2 乘数
     * @param scale
     * @return
     */
    public static BigDecimal mul(BigDecimal v1, Object v2, int scale) {
        BigDecimal b2 = getBigDecimal(v2);   
        return round(v1.multiply(b2), scale);
    }
    
    
    /**
     * BigDecimal除法运算四舍五入到scale位
     * @param v1
     * @param v2
     * @param scale <0时默认5位
     * @return
     */
    public static BigDecimal div(Object v1, Object v2, int scale) {
        BigDecimal b1 = getBigDecimal(v1);   
        BigDecimal b2 = getBigDecimal(v2);   
        
        if(scale < 0){
            scale = FIRST_SCALE;
        }
        
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP);
    }
    /**
     * BigDecimal除法运算，四舍五入保留两位小数.
     * 
     * @param v1 被除数
     * @param v2 除数
     * @return
     */
    public static BigDecimal div(Object v1, Object v2) {
        BigDecimal b1 = getBigDecimal(v1);   
        BigDecimal b2 = getBigDecimal(v2);   
        return div(b1, b2);
    }
    
    /**
     * BigDecimal除法运算，四舍五入保留两位小数.
     * 
     * @param v1 被除数
     * @param v2 除数
     * @return
     */
    public static BigDecimal div(BigDecimal v1, Object v2) {
        BigDecimal b2 = getBigDecimal(v2);   
        return div(v1, b2);
    }
    
    /**
     * BigDecimal除法运算，四舍五入保留两位小数.
     * 
     * @param v1 被除数
     * @param v2 除数
     * @return
     */
    public static BigDecimal div(BigDecimal v1, BigDecimal v2) {   
        //2015-03-23 规则为先四舍五入到5位，再到2位
        //return v1.divide(v2, DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);
        return round(v1.divide(v2, FIRST_SCALE, BigDecimal.ROUND_HALF_UP), DEFAULT_SCALE);
    }
    
    /**
     * 转换为BigDecimal类型.
     * 
     * @param v 被转换的数
     * @return
     */
    public static BigDecimal getBigDecimal(Object v) {
        return new BigDecimal(StringUtils.defaultIfEmpty(ObjectUtils.toString(v), "0"));
    }
    
    /**
     * 
     * @param args
     * @CreateDate: 2016-9-11 下午8:19:31
     */
    public static void main(String[] args) {
        //System.err.println(round(3.225, 2));
        System.err.println(createInteger(""));
    }
}
