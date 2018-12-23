package com.common.wechat.aes;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.system.constant.SystemConstant;

/**
* @author: anson
* @CreateDate: 2018年1月26日 上午11:19:08
* @version: V 1.0
* 微信安全模式下消息加解密
*/
public class AesSignUtil {  
    /** 
     *  与开发模式接口配置信息中的Token保持一致 
     */  
    private static String token =SystemConstant.TOKEN;  
      
    /** 
     * 微信生成的 ASEKey 
     */  
    private static String encodingAesKey =SystemConstant.AES_KEY;  
  
    /** 
     * 应用的AppId 
     */  
    private static String appId=SystemConstant.APP_ID;  
      
   
  
    /** 
     * 加密给微信的消息内容 
     * @param replayMsg 
     * @param timeStamp 
     * @param nonce 
     * @return 
     * @throws DocumentException 
     */  
    public static String ecryptMsg(String replayMsg,String timeStamp, String nonce) throws DocumentException {  
        WXBizMsgCrypt pc; 
        String result ="";  
        try {  
            pc = new WXBizMsgCrypt(token, encodingAesKey, appId);  
            result = pc.encryptMsg(replayMsg, timeStamp, nonce);  
        } catch (AesException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
        //SAXReader reader = new SAXReader();    
        Document document =DocumentHelper.parseText(result);  
        // 得到xml根元素    
        Element root = document.getRootElement();    
        // 得到根元素的所有子节点    
        List<Element> elementList = root.elements();    
        // 遍历所有子节点    
        for (Element e : elementList)    
          // map.put(e.getName(), e.getText());    
            if(e.getName().equals("Encrypt")){
               result=e.getText();
            }
        return result;  
    }  
    
    /**
     * 安全模式  解密消息
     * @param request
     * @return
     * @throws Exception
     * illegal Key Size
     */
    public static Map<String, String> parseXmlCrypt(HttpServletRequest request) throws Exception {    
        // 将解析结果存储在HashMap中    
        Map<String, String> map = new HashMap<String, String>();    
    
        // 从request中取得输入流    
        InputStream inputStream = request.getInputStream();    
          
        BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream));  
        String line;  
        StringBuffer buf=new StringBuffer();  
        while((line=reader.readLine())!=null){  
         buf.append(line);  
        }  
        reader.close();  
        inputStream.close();  
          
        WXBizMsgCrypt wxCeypt=new WXBizMsgCrypt(token, encodingAesKey, appId);  
        // 微信加密签名    
        String signature = request.getParameter("msg_signature");    
        // 时间戳    
        String timestamp = request.getParameter("timestamp");    
        // 随机数    
        String nonce = request.getParameter("nonce");    
        String respXml=wxCeypt.decryptMsg(signature, timestamp, nonce, buf.toString());  
          
        //SAXReader reader = new SAXReader();    
        Document document =DocumentHelper.parseText(respXml);  
        // 得到xml根元素    
        Element root = document.getRootElement();    
        // 得到根元素的所有子节点    
        List<Element> elementList = root.elements();    
        // 遍历所有子节点    
        for (Element e : elementList)  {
        	map.put(e.getName(), e.getText());    
        }
        return map;    
    }    
}
