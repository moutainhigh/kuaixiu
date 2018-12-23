package com.common.logic;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * 配置文件工具类.
 * 
 * @author: lijx
 * @CreateDate: 2016-8-25 上午1:19:28
 * @version: V 1.0
 */
public class Config {
    
    /**
     * 读取配置文件
     */
    private static final ResourceBundle db_bundle = ResourceBundle.getBundle("config");
    
    /**
     * 存放配置信息
     */
    private static Map<String, Object> config;
    
    /**
     * 通过key获取对应配置信息
     * @param key
     * @return
     * @CreateDate: 2016-8-25 上午1:20:48
     */
    public static String getProperty(String key){
        return db_bundle.getString(key);
    }
    
    /**
     * 获取所有配置信息
     * @return
     * @CreateDate: 2016-8-25 上午1:21:25
     */
    public static Map<String, Object> getProopertyMap(){
        if(config==null){
            Map<String, Object> properties = new HashMap<String,Object>(); 
            Enumeration<String> keys= db_bundle.getKeys();
            while(keys.hasMoreElements()){
                String key = keys.nextElement();
                properties.put(key, String.valueOf(db_bundle.getObject(key)).replaceAll(" ", ""));
            }
            config=properties;
        }
        return config;
    }
}
