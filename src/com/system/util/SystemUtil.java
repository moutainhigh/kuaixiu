package com.system.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.system.api.entity.ApiConfig;
import com.system.api.entity.OauthToken;
import com.system.basic.dict.entity.Dict;

/**
 * .
 * 
 * @author: lijx
 * @CreateDate: 2016-9-4 下午9:52:22
 * @version: V 1.0
 */
public class SystemUtil {
    /**
     * 读取配置文件
     */
    private static final ResourceBundle systemCfg = ResourceBundle.getBundle("systemcfg");
    
    /**
     * 保存用户token, key 为 accessToken
     */
    private static final Map<String, OauthToken> tokenMap = new HashMap<String, OauthToken>();
    /**
     * 保存用户token, key 为 clientId
     */
    private static final Map<String, OauthToken> tokenClientIdMap = new HashMap<String, OauthToken>();
    
    
    /**
     * 通过key获取对应配置信息
     * @param key
     * @return
     * @CreateDate: 2016-8-25 上午1:20:48
     */
    public static String getSysCfgProperty(String key){
        return systemCfg.containsKey(key) ? systemCfg.getString(key) : null;
    }
    
    private static final Map<String, Object> propertyMap = new HashMap<String, Object>();
    
    /**
     * 设置属性
     * @param key
     * @param obj
     * @CreateDate: 2016-8-11 上午10:23:51
     */
    public static void setProperty(String key, Object value){
        synchronized(propertyMap){
            propertyMap.put(key, value);
        }
    }
    
    /**
     * 获取配置参数
     * @param key
     * @return
     * @CreateDate: 2016-8-11 上午10:41:31
     */
    public static Object getProperty(String key){
        return propertyMap.get(key);
    }
    
    /**
     * 保存token
     * @param token
     * @CreateDate: 2016-9-5 上午2:01:34
     */
    public static synchronized void saveToken(OauthToken token){
        tokenMap.put(token.getAccessToken(), token);
        tokenClientIdMap.put(token.getClientId() + "_" + token.getTokenType(), token);
    }
    
    /**
     * 删除token
     * @param token
     * @CreateDate: 2016-9-5 上午2:01:34
     */
    public static synchronized void removeToken(OauthToken token){
        tokenMap.remove(token.getAccessToken());
        tokenClientIdMap.remove(token.getClientId() + "_" + token.getTokenType());
    }
    
    /**
     * 获取token
     * @param token
     * @CreateDate: 2016-9-5 上午2:01:34
     */
    public static OauthToken getToken(String token){
        return tokenMap.get(token);
    }
    
    /**
     * 获取token
     * @param token
     * @CreateDate: 2016-9-5 上午2:01:34
     */
    public static OauthToken getTokenByClientId(String clientId, String tokenType){
        return tokenClientIdMap.get(clientId + "_" + tokenType);
    }
    
    /**
     * 保存接口配置信息
     */
    private static final Map<String, ApiConfig> apiConfigMap = new HashMap<String, ApiConfig>();

    /**
     * 根据加密后接口代码获取接口配置信息
     * @param code
     * @return
     * @CreateDate: 2016-6-3 下午2:29:05
     */
    public static ApiConfig getApiConfig(String code){
        return apiConfigMap.get(code);
    }

    /**
     * 初始化接口配置信息
     * @param list
     * @CreateDate: 2016-6-3 下午2:32:22
     */
    public static void initApiConfigMap(List<ApiConfig> list){
        synchronized(apiConfigMap){
            if(list != null && list.size() > 0){
                apiConfigMap.clear();
                for(ApiConfig cfg : list){
                    apiConfigMap.put(cfg.getCode(), cfg);
                }
            }
        }
    }
    
    /**
     * 保存接口配置信息
     */
    private static final Map<String, Dict> dictMap = new HashMap<String, Dict>();

    /**
     * 根据加密后接口代码获取接口配置信息
     * @param code
     * @return
     * @CreateDate: 2016-6-3 下午2:29:05
     */
    public static Dict getDict(String key){
        return dictMap.get(key);
    }

    /**
     * 初始化接口配置信息
     * @param list
     * @CreateDate: 2016-6-3 下午2:32:22
     */
    public static void initDictMap(List<Dict> list){
        synchronized(dictMap){
            if(list != null && list.size() > 0){
                dictMap.clear();
                for(Dict d : list){
                    if("0".equals(d.getPkey())){
                        setProperty(d.getKey(), d.getValue());
                        dictMap.put(d.getKey(), d);
                    }
                }
            }
        }
    }
    
    /**
     * 执行本地命令
     * @param command 命令
     * @return 执行结果
     */
    public static String runtime_exec(String command){
        String result="";
        try {
            Process proc = Runtime.getRuntime().exec(command);  
            StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "Error");  
            StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "Output");  
            errorGobbler.start();  
            outputGobbler.start();  
            proc.waitFor();
            result = outputGobbler.getResult();
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}

/**
 * 开启线程异步读取命令执行结果和命令执行错误信息.
 * 
 * @CreateDate: 2016-9-16 下午1:04:40
 * @version: V 1.0
 */
class StreamGobbler extends Thread {  
    
    private InputStream is;  
    private String type;  
    private StringBuffer sb = new StringBuffer();
    
    public StreamGobbler(InputStream is, String type) {  
        this.is = is;  
        this.type = type;  
    }  
  
    /**
     * 开启线程
     */
    public void run() {  
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {  
            isr = new InputStreamReader(is);  
            br = new BufferedReader(isr);  
            String line = null;  
            while ((line = br.readLine()) != null) {
                sb.append(line);
                //System.out.println(type + "   :" + line);
            }  
        } 
        catch (IOException ioe) {  
            ioe.printStackTrace();  
        } 
        finally {
            if(br != null){
                try{
                    br.close();
                } 
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            if(isr != null){
                try{
                    isr.close();
                } 
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * 获取命令执行结果
     * @return
     * @CreateDate: 2016-9-16 下午1:05:15
     */
    public String getResult(){
        if(sb != null){
            return sb.toString();
        }
        else{
            return null;
        }
    }
}

