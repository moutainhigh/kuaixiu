package com.common.util;

import java.util.Random;

/**
 * 编码工具类.
 * 
 * @author: lijx
 * @CreateDate: 2016-7-29 上午8:28:00
 * @version: V 1.0
 */
public class NOUtil {
    /**
     * 获取简单编码： 前缀 + 时间
     * @param prefix 编码前缀
     * @return prefix + yyyyMMddHHmmssSSS
     * @author: lijx
     * @CreateDate: 2016-7-29 上午8:31:13
     */
    public static String getNo(String prefix){
        return prefix + DateUtil.getSerialFullSSSDate();
    }
    
    private static String base = "0123456789abcdefg0123456789hijklmn0123456789opqrst0123456789uvwxyz0123456789ABCDEFG0123456789HIJKLMN0123456789OPQRST0123456789UVWXYZ0123456789";   
    /**
     * length表示生成字符串的长度
     * @param length
     * @return
     */
    public static String getRandomString(int length) {
        Random r = new Random();   
        StringBuffer sb = new StringBuffer();   
        for (int i = 0; i < length; i++) {   
            int number = r.nextInt(base.length());   
            sb.append(base.charAt(number));   
        }   
        return sb.toString();   
     }  
    
    /**
     * 顺丰快递交易流水号 YYYYMMDD+流水号{10}, 例如:201404120000000001,交易流水号唯一 且不能重复
     */
    public static String getTransMessageId(){
        return DateUtil.getSerialFullSSSDate()+(int)(Math.random()*10);
    }
    
    
}
