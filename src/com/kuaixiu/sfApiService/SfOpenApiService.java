package com.kuaixiu.sfApiService;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.util.NOUtil;
import com.kuaixiu.sfConstant.Constant;
import com.kuaixiu.sfEntity.SfToken;
import com.system.constant.SystemConstant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;



/**
* @author: anson
* @CreateDate: 2017年8月15日 下午2:55:03
* @version: V 1.0
* 顺丰开放接口
*/
public class SfOpenApiService {

	
	/**
     * 获取access_token和refreshToken
     * access_token有效期为24小时
     * 如果忘记可以调用查询访问令牌接口重新获取
     */
    public String getAccessToken() throws Exception {
         String url="https://"
         		+ SystemConstant.SF_TEST_DOMAIN
         		+ "/public/v1.0/security/access_token/sf_appid/"
         		+ SystemConstant.SF_APP_ID
         		+ "/sf_appkey/"
         		+ SystemConstant.SF_APP_KEY;
   		 JSONObject params=new JSONObject();
   		 JSONObject j=new JSONObject();
   		 //生成交易流水号
   		 String transMessageId=NOUtil.getTransMessageId();
   		 j.put("transType", Constant.SF_APPLY_ACCESS_TOKEN);
   		 j.put("transMessageId", transMessageId);
   		 params.put("head", j);
   		 //返回JSON格式数据
   		 String result=sendPost(url,params);
   		 System.out.println(result);
    	 return result;
    }
	
	
	 /**
     * post请求路径方式
     */
    public static String sendPost(String url, JSONObject param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");  
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(),"utf-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！" + e);
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
        return result;
    }
    
    /**
     * 快速下单
     */
    public void startOrder(SfToken sfToken){
    	  String url="https://"
           		+ SystemConstant.SF_TEST_DOMAIN
           		+ "/rest/v1.0/order/access_token/"
           		+ sfToken.getAccessToken()
           		+ "/sf_appid/"
           		+ SystemConstant.SF_APP_ID
           		+ "/sf_appkey/"
           		+ SystemConstant.SF_APP_KEY;
    	  JSONObject params=new JSONObject();
          //设置响应头参数
    	  JSONObject j=new JSONObject(); 
    	  j.put("transMessageId", sfToken.getTransMessageId());
          j.put("transType", Constant.SF_ORDER);
    	  params.put("head", j);
    	  //
    	  JSONObject body=new JSONObject();
    	  //设置公共参数
    	  body.put("orderId","PO-20170221032609680");
    	  body.put("expressType",1);
    	  body.put("payMethod","1");
    	  body.put("custId","7550010173");
    	  //设置货物信息
    	  JSONObject cargo=new JSONObject();
    	  cargo.put("cargo","iphone5s");
    	  cargo.put("cargoAmount","4999.00");
    	  body.put("cargoInfo", cargo);
    	  //设置到件方信息
    	  JSONObject consigneer=new JSONObject();
    	  consigneer.put("address","南山区深圳软件产业基地");
    	  consigneer.put("city","深圳市");
    	  consigneer.put("company","顺丰科技");
    	  consigneer.put("contact","黄飞鸿");
    	  consigneer.put("mobile","18588416666");
    	  consigneer.put("province","广东省");
    	  consigneer.put("tel","0755-33916666");
    	  body.put("consigneeInfo", consigneer);
    	  //设置寄件方信息
    	  JSONObject deliver=new JSONObject();
    	  deliver.put("address","沧浪区人民路沧浪亭街 31 号宝芝林贸易有限公司");
    	  deliver.put("city","苏州市");
    	  deliver.put("company","宝芝林贸易");
    	  deliver.put("contact","黄飞飞");
    	  deliver.put("mobile","15356152347");
    	  deliver.put("province","江苏省");
    	  deliver.put("tel","0755-33916655");
    	  body.put("deliverInfo", deliver);
    	  params.put("body", body);
    	  System.out.println("请求数据："+params);
    	  String result=sendPost(url,params);
          System.out.println(result);
    	  
    }
    
    
    
    /**
     * 将得到的access_token 和refreshToken和对应订单信息存入方便查询
     */
	public SfToken saveToken(String result){
		JSONObject j=JSONObject.parseObject(result);
		JSONObject json=(JSONObject) j.get("body");
		JSONObject head=(JSONObject) j.get("head");
		SfToken s=new SfToken();
		s.setAccessToken((String)(json.get("accessToken")));
		s.setTransMessageId((String)(head.get("transMessageId")));
	    return s;
	}
	
    
	/**
	 * 订单结果查询
	 */
	public void queryResult(SfToken sfToken){
		 String url="https://"
	           		+ SystemConstant.SF_TEST_DOMAIN
	           		+ "/rest/v1.0/order/query/access_token/"
	           		+ sfToken.getAccessToken()
	           		+ "/sf_appid/"
	           		+ SystemConstant.SF_APP_ID
	           		+ "/sf_appkey/"
	           		+ SystemConstant.SF_APP_KEY;
	    	  JSONObject params=new JSONObject();	
		      
	    	  JSONObject head=new JSONObject();
		      head.put("transType", Constant.SF_ORDER_QUERY);
		      head.put("transMessageId", sfToken.getTransMessageId());
		      JSONObject body=new JSONObject();
		      body.put("orderId", sfToken.getOrderNo());
		      params.put("head", head);
		      params.put("boyd", body);
		      //发送请求
		      String result=sendPost(url,params);
		      //解析结果
		      JSONObject returnResult=JSONObject.parseObject(result);
		      try {
				JSONObject resultBody=returnResult.getJSONObject("body");
				String orderId=resultBody.getString("orderId");
				String mailNo=resultBody.getString("mailNo");
				String filterResult=resultBody.getString("filterResult");
				String remark=resultBody.getString("remark");
		    	  
			} catch (Exception e) {
				  System.out.println("解析返回参数异常");
                  e.printStackTrace();
			}
		      
	}
    
	
	/**
	 * 订单筛选
	 * 客户系统通过此接口向顺丰企业服务平台发送自动筛单请求，用于判断客户的收、派地址是 否属于顺丰的收派范围。
	 * 系统会根据收派双方的地址自动判断是否在顺丰的收派范围内。如果属 于范围内则返回可收派，否则返回不可收派
	 */
	public void select(){
		
	}
	
	
	/**
	 *  路由查询 
	 */
	public void selectRoute(SfToken sfToken){
		 String url="https://"
	           		+ SystemConstant.SF_TEST_DOMAIN
	           		+ "/rest/v1.0/route/query/access_token/"
	           		+ sfToken.getAccessToken()
	           		+ "/sf_appid/"
	           		+ SystemConstant.SF_APP_ID
	           		+ "/sf_appkey/"
	           		+ SystemConstant.SF_APP_KEY;
	    	  JSONObject params=new JSONObject();	
		      
	    	  JSONObject head=new JSONObject();
		      head.put("transType", Constant.SF_ROUTE_QUERY);
		      head.put("transMessageId", sfToken.getTransMessageId());
		      JSONObject body=new JSONObject();
		      body.put("trackingType", "1");
		      body.put("trackingNumber", sfToken.getMailNo());
		      body.put("methodType", "1");
		      params.put("head", head);
		      params.put("boyd", body);
		      //发送请求
		      String result=sendPost(url,params);
		      
		      //解析返回结果
		      JSONObject returnResult=JSONObject.parseObject(result);
		      JSONArray returnBody=returnResult.getJSONArray("body");
              //遍历解析成自定义的ResultData对象
		      Iterator<Object> it = returnBody.iterator();
		      
		      
	}
	
	
	
	
    public static void main(String[] args) {
    	SfOpenApiService s=new SfOpenApiService();
		try {
			//获得access_token
	     	String result=s.getAccessToken();
			SfToken sfToken=s.saveToken(result);
			//快速下单
			s.startOrder(sfToken);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
    
}
