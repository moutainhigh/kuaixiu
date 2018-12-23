package com.common.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * 日期工具类.
 * 
 * @CreateDate: 2016-9-5 上午1:37:04
 * @version: V 1.0
 */
public final class DateUtil {

    /**
     * 秒 = 1000 毫秒
     */
    public static final int SECOND = 1000;

    /**
     * 分钟 = SECOND * 60
     */
    public static final int MINUTE = SECOND * 60;

    /**
     * 小时 = MINUTE * 60
     */
    public static final int HOUR = MINUTE * 60;

    /**
     * 天 = HOUR * 24;
     */
    public static final int DAY = HOUR * 24;

    /**
     * 星期 = DAY * 7;
     */
    public static final int WEEK = DAY * 7;

    /**
     * 年 = DAY * 365
     */
    public static final int YEAR = DAY * 365;

    /**
     * 北京时间
     */
    public static final long GMT_VIETNAM_TIME_OFFSET = HOUR * 8;

    /**
     * 偏移时间
     */
    private static long SERVER_TIME_OFFSET = 0;

    /**
     * 格式：yyyyMMdd
     */
    private static DateFormat serialFormat = new SimpleDateFormat(
            "yyyyMMdd");

    /**
     * 格式：yyyyMMddHHmmss
     */
    private static DateFormat serialFullFormat = new SimpleDateFormat(
            "yyyyMMddHHmmss");
    
    /**
     * 格式：yyyyMMddHHmmssSSS
     */
    private static DateFormat serialFullSSSFormat = new SimpleDateFormat(
            "yyyyMMddHHmmssSSS");
    
    /**
     * 格式：yyyy-MM-dd HH:mm:ss
     */
    private static DateFormat yyyyMMddHHmmssFormat = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");
    /**
     * 格式：yyyy-MM-dd 00:00:00
     */
    private static SimpleDateFormat yyyyMMdd000000Format = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
    
    /**
     * 格式：yyyy-MM-dd HH:mm:ss.SSS
     */
    private static DateFormat yyyyMMddHHmmssSSSFormat = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss.SSS");
    
    /**
     * 格式：dd/MM/yyyy
     */
    private static DateFormat ddMMyyyyFormat = new SimpleDateFormat(
            "dd/MM/yyyy");
    /**
     * 格式：yyyy-MM-dd
     */
    private static DateFormat yyyyMMddFormat = new SimpleDateFormat(
            "yyyy-MM-dd");

    /**
     * 格式：yyyy年MM月dd日
     */
    private static DateFormat yyyyMMddFormatCN = new SimpleDateFormat(
            "yyyy年MM月dd日");
    /**
     * 格式：yyyy年M月d日 E
     */
    private static DateFormat yyyyMdEFormatCN = new SimpleDateFormat(
            "yyyy年M月d日 E",Locale.CHINESE);

    /**
     * 格式：yyyy-MM-dd hh:mm:ss
     */
    private static DateFormat yyyyMMddhhmmssFormat = new SimpleDateFormat(
            "yyyy-MM-dd hh:mm:ss");

    /**
     * 格式：yyyy年MM月dd日 hh时mm分ss秒
     */
    private static DateFormat yyyyMMddhhmmssFormatCN = new SimpleDateFormat(
            "yyyy年MM月dd日 hh时mm分ss秒");

    /**
     * 格式：MM月dd日 hh时mm分
     */
    private static DateFormat MMddhhmmFormat = new SimpleDateFormat(
            "MM月dd日 hh时mm分");

    /**
     * 格式：yyyy-MM-dd hh:mm
     */
    private static DateFormat yyyyMMddhhmmFormat = new SimpleDateFormat(
            "yyyy-MM-dd hh:mm");

    /**
     * 格式：yyyy年MM月dd日 hh时mm分
     */
    private static DateFormat yyyyMMddhhmmFormatCN = new SimpleDateFormat(
            "yyyy年MM月dd日 hh时mm分");
    
    /**
     * 格式：MMdd
     */
    private static DateFormat MMddFormat = new SimpleDateFormat(
            "MMdd");

    /**
     * 格式：SimpleDateFormat.DEFAULT
     */
    private static DateFormat dateFormat = SimpleDateFormat
            .getDateInstance(SimpleDateFormat.DEFAULT);

    /**
     * 格式：SimpleDateFormat.DEFAULT,SimpleDateFormat.DEFAULT
     */
    private static DateFormat datetimeFormat = SimpleDateFormat
            .getDateTimeInstance(SimpleDateFormat.DEFAULT,
                    SimpleDateFormat.DEFAULT);

    private DateUtil() {
    }
    
    /**
     * 返回给定日期的月份和天
     * 格式: MMdd
     * @param date 日期
     * @return MMdd 
     */
    public static synchronized String getDateMMDD(Date date) {
        return MMddFormat.format(date);
    }
    
    /**
     * 返回给定日期的 日/月/年
     * 格式: dd/MM/yyyy
     * @param date 日期
     * @return dd/MM/yyyy 
     */
    public static synchronized String getDateDDMMYYYY(Date date) {
        return ddMMyyyyFormat.format(date);
    }

    /**
     * 返回给定日期格式字符串的日期类型
     * 格式: dd/MM/yyyy
     * @param ddMMyyyyDate 日期格式字符串
     * @return 转换后的日期类型
     * @throws ParseException
     */
    public static synchronized Date getDDMMYYYYDate(String ddMMyyyyDate)
            throws ParseException {
        return ddMMyyyyFormat.parse(ddMMyyyyDate);
    }
    
    /**
     * 返回给定日期的 年-月-日<br/>
     * 格式: yyyy-MM-dd
     * @param date 日期
     * @return yyyy-MM-dd 
     */
    public static synchronized String getDateYYYYMMDD(Date date) {
        return yyyyMMddFormat.format(date);
    }
    /**
     * 返回给定日期格式字符串的日期类型<br/>
     * 格式: yyyy-MM-dd
     * @param yyyyMMddDate 日期格式字符串
     * @return 转换后的日期类型
     * @throws ParseException
     */
    public static synchronized Date getYYYYMMDDDate(String yyyyMMddDate)
            throws ParseException {
        return yyyyMMddFormat.parse(yyyyMMddDate);
    }

    /**
     * 返回给定日期的字符串格式<br/>
     * 格式: yyyy-MM-dd HH:mm:ss
     * @param date 日期
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static synchronized String getDateyyyyMMddHHmmss(Date date) {
        return yyyyMMddHHmmssFormat.format(date);
    } 

    /**
     * 返回给定日期格式字符串的日期类型<br/>
     * 格式: yyyy-MM-dd HH:mm:ss
     * @param yyyyMMddHHmmssDate 日期格式字符串
     * @return 转换后的日期类型
     * @throws ParseException
     */
    public static synchronized Date getyyyyMMddHHmmssDate(
            String yyyyMMddHHmmssDate) throws ParseException {
        return yyyyMMddHHmmssFormat.parse(yyyyMMddHHmmssDate);
    }
    
    /**
     * 返回给定日期的字符串格式<br/>
     * 格式: yyyy年MM月dd日
     * @param date 日期
     * @return yyyy年MM月dd日
     */
    public static synchronized String getDateYYYYMMDD_CN(Date date) {
        return yyyyMMddFormatCN.format(date);
    }

    /**
     * 返回给定日期格式字符串的日期类型<br/>
     * 格式: yyyy年MM月dd日
     * @param yyyyMMddCN_Date 日期格式字符串
     * @return 转换后的日期类型
     * @throws ParseException
     */
    public static synchronized Date getYYYYMMDD_CN_Date(String yyyyMMddCN_Date)
            throws ParseException {
        return yyyyMMddFormatCN.parse(yyyyMMddCN_Date);
    }
    
    /**
     * 返回给定日期的字符串格式<br/>
     * 格式: yyyy-MM-dd 00:00:00
     * @return yyyy-MM-dd 00:00:00
     * @throws ParseException 
     */
    public static synchronized Date getDateyyyyMMdd000000_Date() throws ParseException {
        GregorianCalendar now = new GregorianCalendar();
        return yyyyMMdd000000Format.parse(yyyyMMdd000000Format.format(now.getTime()));
    }

    /**
     * 返回给定日期的字符串格式<br/>
     * 格式: yyyy-MM-dd 00:00:00
     * @param date 日期
     * @return yyyy-MM-dd 00:00:00
     */
    public static synchronized String getDateyyyyMMdd000000(Date date) {
        return yyyyMMdd000000Format.format(date);
    }
    
    /**
     * 返回给定日期的字符串格式<br/>
     * 格式: yyyy-MM-dd hh:mm:ss
     * @param date 日期
     * @return yyyy-MM-dd hh:mm:ss
     */
    public static synchronized String getDateyyyyMMddhhmmss(Date date) {
        return yyyyMMddhhmmssFormat.format(date);
    }

    /**
     * 返回给定日期格式字符串的日期类型<br/>
     * 格式: yyyy-MM-dd hh:mm:ss
     * @param yyyyMMddhhmmssDate 日期格式字符串
     * @return 转换后的日期类型
     * @throws ParseException
     */
    public static synchronized Date getyyyyMMddhhmmssDate(
            String yyyyMMddhhmmssDate) throws ParseException {
        return yyyyMMddhhmmssFormat.parse(yyyyMMddhhmmssDate);
    }
    
    /**
     * 返回给定日期的字符串格式<br/>
     * 格式: yyyy-MM-dd hh:mm
     * @param date 日期
     * @return yyyy-MM-dd hh:mm
     */
    public static synchronized String getDateyyyyMMddhhmm(Date date) {
        return yyyyMMddhhmmFormat.format(date);
    }

    /**
     * 返回给定日期格式字符串的日期类型<br/>
     * 格式: yyyy-MM-dd hh:mm
     * @param yyyyMMddhhmmDate 日期格式字符串
     * @return 转换后的日期类型
     * @throws ParseException
     */
    public static synchronized Date getyyyyMMddhhmmDate(String yyyyMMddhhmmDate)
            throws ParseException {
        return yyyyMMddhhmmFormat.parse(yyyyMMddhhmmDate);
    }

    /**
     * 返回给定日期的字符串格式<br/>
     * 格式: yyyy年MM月dd日 hh时mm分ss秒
     * @param date 日期
     * @return yyyy年MM月dd日 hh时mm分ss秒
     */
    public static synchronized String getDateyyyyMMddhhmmss_CN(Date date) {
        return yyyyMMddhhmmssFormatCN.format(date);
    }

    /**
     * 返回给定日期格式字符串的日期类型<br/>
     * 格式: yyyy年MM月dd日 hh时mm分ss秒
     * @param yyyyMMddhhmmssCN_Date 日期格式字符串
     * @return 转换后的日期类型
     * @throws ParseException
     */
    public static synchronized Date getyyyyMMddhhmmss_CN_Date(
            String yyyyMMddhhmmssCN_Date) throws ParseException {
        return yyyyMMddhhmmssFormatCN.parse(yyyyMMddhhmmssCN_Date);
    }

    /**
     * 返回给定日期的字符串格式<br/>
     * 格式: yyyy-MM-dd hh:mm
     * @param date 日期
     * @return yyyy-MM-dd hh:mm
     */
    public static synchronized String getDateSearch(Date date) {
        return MMddhhmmFormat.format(date);
    }

    /**
     * 返回给定日期的默认格式字符串<br/>
     * 格式: SimpleDateFormat.DEFAULT
     * @param date 日期
     * @return 默认格式
     */
    public static synchronized String formatDate(Date date) {
        return dateFormat.format(date);
    }

    /**
     * 返回给定日期的默认格式字符串<br/>
     * 格式: SimpleDateFormat.DEFAULT,SimpleDateFormat.DEFAULT
     * @param date 日期
     * @return 默认格式
     */
    public static synchronized String formatDateTime(Date date) {
        return datetimeFormat.format(date);
    }

    /**
     * 获取当前时间的Timestamp
     * @return 
     */
    public static Timestamp getCurrentGMTTimestamp() {
        return new Timestamp(System.currentTimeMillis() + SERVER_TIME_OFFSET);
    }

    /**
     * 将给定的Timestamp更改给当前时间
     * @param timeToUpdate 给定的Timestamp
     */
    public static void updateCurrentGMTTimestamp(Timestamp timeToUpdate) {
        timeToUpdate.setTime(System.currentTimeMillis() + SERVER_TIME_OFFSET);
    }

    /**
     * 将给定日期加上八个小时
     * @param date
     * @return
     */
    public static Date getVietnamDateFromGMTDate(Date date) {
        return new Date(date.getTime() + GMT_VIETNAM_TIME_OFFSET);
    }

    /**
     * 将给定日期加上 hourOffset 个小时
     * @param gmtDate 日期
     * @param hourOffset 指定加上几个小时
     * @return
     */
    public static Date convertGMTDate(Date gmtDate, int hourOffset) {
        return new Date(gmtDate.getTime() + hourOffset * HOUR);
    }

    /**
     * 将给定 Timestamp 加上 hourOffset 个小时
     * @param gmtTimestamp Timestamp
     * @param hourOffset 指定加上几个小时
     * @return
     */
    public static Timestamp convertGMTTimestamp(Timestamp gmtTimestamp,
            int hourOffset) {
        return new Timestamp(gmtTimestamp.getTime() + hourOffset * HOUR);
    }
    /**
     * 返回当前日期<br/>
     * 格式: yyyyMMdd
     * @return 
     */
    public static synchronized String getSerialDate() {
        GregorianCalendar now = new GregorianCalendar();
        return serialFormat.format(now.getTime());
    }
    
    /**
     * 返回当前日期<br/>
     * 格式: yyyyMMddHHmmss
     * @return 
     */
    public static synchronized String getSerialFullDate() {
        GregorianCalendar now = new GregorianCalendar();
        return serialFullFormat.format(now.getTime());
    }
    
    /**
     * 返回当前日期<br/>
     * 格式: yyyyMMddHHmmss
     * @return 
     */
    public static synchronized String getSerialFullDate(Date date) {
        return serialFullFormat.format(date);
    }

    /**
     * 返回当前日期<br/>
     * 格式: yyyyMMddHHmmssSSS
     * @return 
     */
    public static synchronized String getSerialFullSSSDate() {
        GregorianCalendar now = new GregorianCalendar();
        return serialFullSSSFormat.format(now.getTime());
    }
    
    /**
     * 返回当前日期
     * @return 
     */
    public static synchronized Date getNow() {
        GregorianCalendar now = new GregorianCalendar();
        return now.getTime();
    }

    /**
     * 返回当前日期<br/>
     * 格式: MMdd
     * @return 
     */
    public static synchronized String getNowMMDD() {
        GregorianCalendar now = new GregorianCalendar();
        return MMddFormat.format(now.getTime());
    }
    
    /**
     * 返回当前日期<br/>
     * 格式: yyyy-MM-dd
     * @return 
     */
    public static synchronized String getNowyyyyMMdd() {
        GregorianCalendar now = new GregorianCalendar();
        return yyyyMMddFormat.format(now.getTime());
    }

    /**
     * 返回当前日期<br/>
     * 格式: yyyy年MM月dd日
     * @return 
     */
    public static synchronized String getNowyyyyMMddCN() {
        GregorianCalendar now = new GregorianCalendar();
        return yyyyMMddFormatCN.format(now.getTime());
    }
    
    /**
     * 返回当前日期<br/>
     * 格式：yyyy年M月d日 E
     * @return
     * @CreateDate: 2016-9-5 上午1:38:13
     */
    public static synchronized String getNowyyyyMdECN() {
        GregorianCalendar now = new GregorianCalendar();
        return yyyyMdEFormatCN.format(now.getTime());
    }
    
    /**
     * 返回当前日期<br/>
     * 格式: yyyy-MM-dd HH:mm:ss
     * @return 
     */
    public static synchronized String getNowyyyyMMddHHmmss() {
        GregorianCalendar now = new GregorianCalendar();
        return yyyyMMddHHmmssFormat.format(now.getTime());
    }
    
    /**
     * 返回当前日期<br/>
     * 格式: yyyy-MM-dd HH:mm:ss.SSS
     * @return 
     */
    public static synchronized String getNowyyyyMMddHHmmssSSS() {
        GregorianCalendar now = new GregorianCalendar();
        return yyyyMMddHHmmssSSSFormat.format(now.getTime());
    }
    
    /**
     * 返回当前日期<br/>
     * 格式: yyyy-MM-dd hh:mm
     * @return 
     */
    public static synchronized String getNowyyyyMMddhhmm() {
        GregorianCalendar now = new GregorianCalendar();
        return yyyyMMddhhmmFormat.format(now.getTime());
    }

    /**
     * 返回当前日期<br/>
     * 格式: yyyy年MM月dd日 hh时mm分
     * @return 
     */
    public static synchronized String getNowyyyyMMddhhmmCN() {
        GregorianCalendar now = new GregorianCalendar();
        return yyyyMMddhhmmFormatCN.format(now.getTime());
    }

    /**
     * 返回当前日期<br/>
     * 格式: yyyy-MM-dd hh:mm:ss
     * @return 
     */
    public static synchronized String getNowyyyyMMddhhmmss() {
        GregorianCalendar now = new GregorianCalendar();
        return yyyyMMddhhmmssFormat.format(now.getTime());
    }

    /**
     * 返回当前日期<br/>
     * 格式: yyyy-MM-dd 00:00:00
     * @return 
     */
    public static synchronized String getNowyyyyMM000000() {
        GregorianCalendar now = new GregorianCalendar();
        return yyyyMMdd000000Format.format(now.getTime());
    }
    
    /**
     * 返回当前日期<br/>
     * 格式: yyyy年MM月dd日 hh时mm分ss秒
     * @return 
     */
    public static synchronized String getNowyyyyMMddhhmmssCN() {
        GregorianCalendar now = new GregorianCalendar();
        return yyyyMMddhhmmssFormatCN.format(now.getTime());
    }
    
    /**
     * 返回本周是一年中的第几个周
     * @return
     */
    public static synchronized int getWeekCountInYear(){
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * 返回指定个数的日期集合<br/>
     * @param count 日期数量
     * @return 日期集合
     * @throws ParseException
     */
    public static synchronized List<Long> getDaysByCount(int count) throws ParseException {
        GregorianCalendar now = new GregorianCalendar();
        //去除时分秒
        Date nowday = yyyyMMddFormat.parse(yyyyMMddFormat.format(now.getTime()));
        Calendar cal = Calendar.getInstance();
        cal.setTime(nowday);
        List<Long> daysL = new ArrayList<Long>();
        for(int i = 0; i < count; i++){
            daysL.add(0,cal.getTime().getTime());
            cal.add(Calendar.DATE, -1);
        }
        return daysL;
    }
    
    /**
     * 返回按时间段的日期集合<br/>
     * @param startdate 日期数量
     * @return 日期集合
     * @throws ParseException
     */
    public static synchronized List<Long> getDaysByArea(String startdate,String enddate) throws ParseException {
        //去除时分秒
        Date startday = getyyyyMMddHHmmssDate(startdate);
        Date endday = getyyyyMMddHHmmssDate(enddate);
        List<Long> daysL = new ArrayList<Long>();
        for(long i=startday.getTime(); i<=endday.getTime();i=i+24000*3600){
            daysL.add(i);
        }
        return daysL;
    }
    
    /**
     * 获取本周第一天
     * @return
     * @CreateDate: 2016-9-19 下午10:59:11
     */
    public static Date getWeekFirstDay() {
        Calendar cal =Calendar.getInstance();
        //获取本周一的日期
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); 
        //System.out.println("********得到本周一的日期*******"+yyyyMMdd000000Format.format(cal.getTime()));
        //这种输出的是上个星期周日的日期，因为老外那边把周日当成第一天
        //cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        //增加一个星期，才是我们中国人理解的本周日的日期
        //cal.add(Calendar.WEEK_OF_YEAR, 1);
        //System.out.println("********得到本周天的日期*******"+yyyyMMdd000000Format.format(cal.getTime()));
        return cal.getTime();
    }
    
    /**
     * 获取本周第一天
     * @return
     * @CreateDate: 2016-9-19 下午10:59:11
     */
    public static Date getMonthFirstDay() {
        Calendar cal =Calendar.getInstance();
        //获取本周一的日期
        cal.set(Calendar.DAY_OF_MONTH, 1); 
        //System.out.println("********得到本周一的日期*******"+yyyyMMdd000000Format.format(cal.getTime()));
        //这种输出的是上个星期周日的日期，因为老外那边把周日当成第一天
        //cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        //增加一个星期，才是我们中国人理解的本周日的日期
        //cal.add(Calendar.WEEK_OF_YEAR, 1);
        //System.out.println("********得到本周天的日期*******"+yyyyMMdd000000Format.format(cal.getTime()));
        return cal.getTime();
    }
    
//    /**
//     *
//     * @param args
//     * @author: lijx
//     * @CreateDate: 2016-9-5 上午1:39:08
//     */
//    public static void main(String[] args) {
//        try {
////            List<Long> s=getDaysByArea("2013-02-03 18:00:00","2013-05-21 18:00:00");
////            for (Long long1 : s) {
////                System.out.println(new Date(long1));
////            }
//            //getWeekFirstDay();
//            //getMonthFirstDay();
//            System.out.println(getDateyyyyMMddhhmmss(getDateAddMinute(-15)));
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    
    /**
     * 返回指定个数的日期集合<br/>
     * @param count 日期数量
     * @return 日期集合
     * @throws ParseException
     */
    public static synchronized List<String> getDaysByCountToString(int count) throws ParseException {
        GregorianCalendar now = new GregorianCalendar();
        //去除时分秒
        Date nowday = yyyyMMddFormat.parse(yyyyMMddFormat.format(now.getTime()));
        Calendar cal = Calendar.getInstance();
        cal.setTime(nowday);
        List<String> daysL = new ArrayList<String>();
        for(int i = 0; i < count; i++){
            Date date=new Date((cal.getTime().getTime()));
            daysL.add(0,getDateYYYYMMDD(date));
            cal.add(Calendar.DATE, -1);
        }
        return daysL;
    }
    
    /**
     * 返回日期的年月日格式日期
     * @param 日期
     * @return 日期
     * @throws ParseException
     */
    public static Date getDateToYYYYMMDD(Date date) throws ParseException {
        //去除时分秒
        Date temp = yyyyMMddFormat.parse(yyyyMMddFormat.format(date));
        return temp;
    }
    
    
    
    /**
     * 格式：yyyy-MM-dd HH
     */
    private static DateFormat yyyyMMddHHFormat = new SimpleDateFormat("yyyy-MM-dd HH");
    /**
     * 返回某日期的某小时的日期格式<br/>
     * 格式: yyyy-MM-dd HH
     * @param date 日期
     * @return yyyy-MM-dd HH
     * @throws ParseException 
     */
    public static synchronized Date getDateyyyyMMddHH(String date)
        throws ParseException {
        return yyyyMMddHHFormat.parse(date);
    }
    /**
     * 返回某日期的某小时的字符串格式<br/>
     * 格式: yyyy-MM-dd HH
     * @param date
     * @return
     * @CreateDate: 2016-9-5 上午1:40:05
     */
    public static synchronized String getDateyyyyMMddHH(Date date){
        return yyyyMMddHHFormat.format(date);
    }
    /**
     * 返回时间加速 hour 小时
     * @param date
     * @param hour
     * @return
     * @throws ParseException
     * @CreateDate: 2016-9-5 上午1:41:12
     */
    public static synchronized Date getyyyyMMddHHDate(Integer hour) throws ParseException {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        return getDateyyyyMMddHH(getDateyyyyMMddHH(cal.getTime()));
    }
    
    /**
     * 返回时间加速 hour 小时
     * @param date
     * @param hour
     * @return
     * @throws ParseException
     * @CreateDate: 2016-9-5 上午1:41:12
     */
    public static synchronized Date getyyyyMMddHHDate(Date date,Integer hour) throws ParseException {
        Calendar cal = Calendar.getInstance();
        cal.setTime((date));
        cal.set(Calendar.HOUR_OF_DAY, hour);
        return getDateyyyyMMddHH(getDateyyyyMMddHH(cal.getTime()));
    }
    
    /**
     * 返回时间加速 hour 小时
     * @param date
     * @param hour
     * @return
     * @throws ParseException
     * @CreateDate: 2016-9-5 上午1:41:12
     */
    public static synchronized Date getDateAddHour(Integer hour) throws ParseException {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        return cal.getTime();
    }
    
    /**
     * 返回时间加速 hour 小时
     * @param date
     * @param hour
     * @return
     * @throws ParseException
     * @CreateDate: 2016-9-5 上午1:41:12
     */
    public static synchronized Date getDateAddHour(Date date,Integer hour) throws ParseException {
        Calendar cal = Calendar.getInstance();
        cal.setTime((date));
        cal.set(Calendar.HOUR_OF_DAY, hour);
        return cal.getTime();
    }
    
    /**
     * 返回时间加速 minute 分钟
     * @param date
     * @param hour
     * @return
     * @throws ParseException
     * @CreateDate: 2016-9-5 上午1:41:12
     */
    public static synchronized Date getDateAddMinute(Integer minute) throws ParseException {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, minute);
        return cal.getTime();
    }
    
    /**
     * 返回时间加速 minute 分钟
     * @param date
     * @param hour
     * @return
     * @throws ParseException
     * @CreateDate: 2016-9-5 上午1:41:12
     */
    public static synchronized Date getDateAddMinute(Date date,Integer minute) throws ParseException {
        Calendar cal = Calendar.getInstance();
        cal.setTime((date));
        cal.add(Calendar.MINUTE, minute);
        return cal.getTime();
    }
    
    /**
     * 返回指定小时集合<br/>
     * @return 小时集合
     * @throws ParseException
     */
    public static synchronized List<Long> getHoursArea() throws ParseException {
        GregorianCalendar now = new GregorianCalendar();
        //去除分秒
        Date nowday = yyyyMMddHHFormat.parse(yyyyMMddHHFormat.format(now.getTime()));
        Calendar cal = Calendar.getInstance();
        cal.setTime(nowday);
        List<Long> daysL = new ArrayList<Long>();
        for(int i = 0; i < 24; i++){
            daysL.add(0,cal.getTime().getTime());
            cal.add(Calendar.HOUR_OF_DAY, -1);
        }
        return daysL;
    }

    /**
     * 获取昨天起始时间
     * @Name njy
     * @return
     */
    public static Date getYesterdayStartTime() {
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        todayStart.add(Calendar.DAY_OF_YEAR,-1);
        return todayStart.getTime();
    }
    /**
     * 返回昨天结束时间
     * @Name njy
     * @return
     */
    public static Date getYesterdayEndTime() {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.set(Calendar.HOUR, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        todayEnd.add(Calendar.DAY_OF_YEAR,-1);
        return todayEnd.getTime();
    }
    /**
     * 获取当天起始时间
     * @return
     */
    public static Date getStartTime() {
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime();
    }

    /**
     * 返回当天结束时间
     * @return
     */
    public static Date getEndTime() {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.set(Calendar.HOUR, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        return todayEnd.getTime();
    }

    public static void main(String[] args) {
        System.out.println(System.currentTimeMillis());
        System.out.println(getStartTime().getTime());
    }
}
