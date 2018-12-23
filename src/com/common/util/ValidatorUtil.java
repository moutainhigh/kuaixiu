package com.common.util;

import org.apache.commons.lang3.StringUtils;

/**
 * 字段验证工具.
 * 
 * @CreateDate: 2016-9-13 下午8:33:53
 * @version: V 1.0
 */
public class ValidatorUtil {

    private static final String numericRegexp = "^(-?\\d+)(\\.\\d+)?$";  
    private static final String emailRegexp = "^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$";
    private static final String mobileRegexp = "^1\\d{10}$";
    /** 
     * 判断是否为浮点数或者整数 
     * @param str 
     * @return true Or false 
     */  
    public static boolean isNumeric(String str){  
        if(StringUtils.isBlank(str)){
            return false;
        }
        else if(!str.matches(numericRegexp)){
            return false;
        }
        return true;
    }  
      
    /** 
     * 判断是否为正确的邮件格式 
     * @param str 
     * @return boolean 
     */  
    public static boolean isEmail(String str){  
        if(StringUtils.isBlank(str)){
            return false;
        }
        else if(!str.matches(emailRegexp)){
            return false;
        }
        return true;
    }  
      
    /** 
     * 判断字符串是否为合法手机号 11位 1 开头 
     * @param str 
     * @return boolean 
     */  
    public static boolean isMobile(String str){  
        if(StringUtils.isBlank(str)){
            return false;
        }
        else if(!str.matches(mobileRegexp)){
            return false;
        }
        return true;
    }
      
    /**
     * @param args
     * @CreateDate: 2016-9-13 下午8:33:41
     */
    public static void main(String[] args) {
        System.out.println(ValidatorUtil.isMobile("1500651367"));
    }

}
