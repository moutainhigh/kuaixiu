package com.common.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.exception.SystemException;
import com.common.wechat.common.util.StringUtils;
import com.system.constant.SystemConstant;
import jodd.util.Base64;
import org.apache.log4j.Logger;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
* @author: anson
* @CreateDate: 2017年11月6日 上午10:59:11
* @version: V 1.0
* 
*/
public class AES {
	
    private static final Logger log=Logger.getLogger(AES.class);
	/**
	 * 加解密的密钥
	 */
	private static final String sKey=SystemConstant.RECYCLE_CODE;
	/**
	 * 加解密用的偏移量
	 */
	private static final String initVector =SystemConstant.RECYCLE_VECTOR;
	
	
    /**
     * 加密
     * @param sSrc
     * @return
     * @throws Exception
     */
    public static String Encrypt(String sSrc) throws Exception {
    //	log.info("加密内容："+sSrc);
        if (sKey == null) {
            log.info("Key为空null");
            return null;
        }
        // 判断Key是否为16位
        if (sKey.length() != 16) {
            log.info("Key长度不是16位");
            return null;
        }
        byte[] raw = sKey.getBytes("utf-8");
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");//"算法/模式/补码方式"
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec,iv);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));

        return new Base64().encodeToString(encrypted);//此处使用BASE64做转码功能，同时能起到2次加密的作用。
    }

    /**
     * 解密
     * @param sSrc
     * @return
     * @throws Exception
     */
    public static String Decrypt(String sSrc) throws Exception {
        try {
            // 判断Key是否正确
            if (sKey == null) {
                log.info("Key为空null");
                return null;
            }
            // 判断Key是否为16位
            if (sKey.length() != 16) {
                log.info("Key长度不是16位");
                return null;
            }
            byte[] raw = sKey.getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            //NoPadding    PKCS5Padding
            cipher.init(Cipher.DECRYPT_MODE, skeySpec,iv);
            byte[] encrypted1 = new Base64().decode(sSrc);//先用base64解密
            try {
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original,"utf-8");
                return originalString;
            } catch (Exception e) {
                log.info(e.toString());
                return null;
            }
        } catch (Exception ex) {
            log.info(ex.toString());
            return null;
        }
    }
    
    /**
	   * 发送post请求   针对回收请求  返回数据默认进行url解码
     * @throws Exception 
	   */
	 public static String post(String url, JSONObject param) throws Exception {
		    //log.info("发起请求");
		    String code=param.getString(SystemConstant.RECYCLE_REQUEST);  //需要编码的参数
		    StringBuffer params = new StringBuffer();  //需要发送的数据
            params.append(SystemConstant.RECYCLE_ID).append("=").append(SystemConstant.RECYCLE_ID_VALUE)
            .append("&").append(SystemConstant.RECYCLE_REQUEST).append("=").append(URLEncoder.encode(code,"utf-8"));
	     //   log.info("加密后发送的请求参数："+params.toString());
		    PrintWriter out = null;
	        BufferedReader in = null;
	        String result = "";
	        try {
	            URL realUrl = new URL(url);
	            // 打开和URL之间的连接
	            URLConnection conn = realUrl.openConnection();
	            // 设置通用的请求属性
	            conn.setRequestProperty("accept", "*application/json*");
	            conn.setRequestProperty("connection", "Keep-Alive");
	            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
	            // 发送POST请求必须设置如下两行
	            conn.setDoOutput(true);
	            conn.setDoInput(true);
	            // 获取URLConnection对象对应的输出流
	            out = new PrintWriter(conn.getOutputStream());
	            // 发送请求参数
	            out.print(params.toString());
	            // flush输出流的缓冲
	            out.flush();
	            // 定义BufferedReader输入流来读取URL的响应
	            in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
	            String line;
	            while ((line = in.readLine()) != null) {
	                result += line;
	            }
	        } catch (Exception e) {
	            log.info("发送 POST 请求出现异常！" + e);
	            e.printStackTrace();
	        }
	        // 使用finally块来关闭输出流、输入流
	        finally {
	            try {
	                if (out != null) {
	                    out.close();
	                }
	                if (in != null) {
	                    in.close();
	                }
	            } catch (IOException ex) {
	                ex.printStackTrace();
	            }
	        }
	        result=URLDecoder.decode(result, "utf-8");
	        return result;
	    }

	 
	 /**
	  * 
	 
	  * Description: 通过微信小程序机型名称找到对应回收平台的机型名称  机型id 和品牌id  品牌名称  机型图片
	 
	  * @param brand 
	  * @param modelName
	  */
	 public static Map getModelName(String brand,String modelName){
		 Map<String,String> map=new HashMap<String,String>();
		 //请求格式：
		 //http://114.215.210.238/super_sdk/j-0009?brandcode=huawei&modelcode=huaweinxt-al10&channelid=1007&platform=ios&version=1.0.0&remark
		 StringBuffer sb=new StringBuffer();
		 sb.append("http://114.215.210.238/super_sdk/j-0009?channelid=1007&platform=ios&version=1.0.0");
		 sb.append("&brandcode=").append(brand);
		 sb.append("&modelcode=").append(modelName);
		 //对面服务器没有写url解码 只能把url里的空格全部去掉了
		 String result = HttpClientUtil.httpGet(sb.toString().replaceAll(" ",""));
		 if(!StringUtils.isBlank(result)){
			 JSONObject json= null;
			 try {
				 json = JSONObject.parseObject(result);
			 } catch (Exception e) {
				throw new SystemException("对应回收平台机型未找到！");
			 }
			 System.out.println("返回json:"+json);
			 String data = json.getString("datainfo");
			 if(!StringUtils.isBlank(data)){
				 JSONArray array=JSONArray.parseArray(data);
				 if(!array.isEmpty()){
					 JSONObject object = (JSONObject) array.get(0);
					 //获得机型对应的回收平台的品牌和品牌id
					 map.put("brandId", object.getString("brandid"));
					 map.put("brandName",object.getString("brandname"));
					 //获取机型对应回收平台的机型名称和机型id 和机型图片
					 JSONArray jsonArray = object.getJSONArray("sublist");
			         if(!jsonArray.isEmpty()){
			             map.put("modelName", ((JSONObject)(jsonArray.get(0))).getString("modelname"));
						 map.put("modelId",((JSONObject)(jsonArray.get(0))).getString("modelid"));
						 map.put("modelLogo",((JSONObject)(jsonArray.get(0))).getString("modellogo"));
			         }
				 }
			 }
		 }
		 return map;
	 }
	 
	 
	 

    public static void main(String[] args) throws Exception {
        String url="http://114.215.210.238/super_webapi/getPrice";      //请求地址
        log.info("请求地址："+url);
        JSONObject requestNews=new JSONObject();   //请求内容
		//调用接口需要加密的数据
		JSONObject code=new JSONObject();
		code.put("productid", "635645354202559276");
		code.put("items", "1,2|2,6|4,15|5,19|6,21|35,114|11,43|12,45|13,47|14,59|26,83|27,85|33,108|32,103|20,78|21,91|28,88|36,118|31,100|30,97");
		System.out.println("请求参数："+code);
		String realCode=AES.Encrypt(code.toString());  //加密
		log.info("加密后："+realCode);

		requestNews.put(SystemConstant.RECYCLE_REQUEST, realCode);
		//发起请求
		String getResult=AES.post(url, requestNews);
		String j=AES.Decrypt(getResult);
		log.info("返回："+JSONObject.parse(j));
		log.info("解密："+getResult(j));

    }

//	public static void main(String[] args) throws UnsupportedEncodingException {
////	 	String url="http://114.215.210.238/super_sdk/j-0009?brandcode=huawei&modelcode=HUAWEINXT-AL10&channelid=1007&platform=ios&version=1.0.0";
////
////		String result = HttpClientUtil.httpGet(url);
////		JSONObject json=JSONObject.parseObject(result);
////		System.out.println(json);
////		String data = json.getString("datainfo");
////		System.out.println(data);
//		Map huawei = getModelName("huawei", "HUAWEI NXT-AL10");
//		System.out.println();
//	}
    

	 
    public static JSONObject getResult(String originalString) throws UnsupportedEncodingException{
		JSONObject result=(JSONObject)JSONObject.parse(originalString);
        if(result.getString("result")!=null&&!result.getString("result").equals("RESPONSESUCCESS")){
        	throw new SystemException(getReason(result.getString("result")));
        }
        return result;
	}
    
    
    public static String getReason(String code){
    	String result="";
    	if(code.equals("PARAMSERR")){
    		result="参数错误";
    	}else if(code.equals("SESSIONERR")){
    		result="sessionid有关错误";
    	}else if(code.equals("SYSTEMERR")){
    		result="系统错误";
    	}else if(code.equals("SIGNATUREERR")){
    		result="签名错误";
    	}else if(code.equals("UNKNOWNERR")){
    		result="未知错误";
    	}
    	return result;
    }
    
}

