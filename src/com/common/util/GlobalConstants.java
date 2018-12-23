package com.common.util;

import java.util.Properties;


/**
 * 配置文件全局变量  储存信心至指定properties文件
 * @author Administrator
 *
 */
public class GlobalConstants {
	 
    public static Properties interfaceUrlProperties;
    static {
        if (GlobalConstants.interfaceUrlProperties == null) {
            InterfaceUrlInti.init();
        }
    }
 
    /**
     * 
     * @Description: 根据不同类型取值
     * @param @param key
     * @param @return
     */
    public static String getInterfaceUrl(String key) {
        String Properties = (String) interfaceUrlProperties.get(key);
        return Properties == null ? null : Properties;
    }
 
    public static Integer getInt(String key) {
        String Properties = (String) interfaceUrlProperties.get(key);
        return Properties == null ? null : Integer.parseInt(Properties);
    }
 
    public static Boolean getBoolean(String key) {
        String Properties = (String) interfaceUrlProperties.get(key);
        return Properties == null ? null : Boolean.valueOf(Properties);
    }
 
    public static Long getLong(String key) {
        String Properties = (String) interfaceUrlProperties.get(key);
        return Properties == null ? null : Long.valueOf(Properties);
    }


 
}
