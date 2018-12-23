package com.kuaixiu.sfApi.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.kuaixiu.order.entity.Order;
import com.kuaixiu.order.service.OrderService;
import com.kuaixiu.sfApiService.SfOpenApiService;
import com.kuaixiu.sfEntity.SfToken;

/**
* @author: anson
* @CreateDate: 2017年8月15日 下午3:04:51
* @version: V 1.0
* 顺丰开放接口
*/
public class OpenApi extends BaseController{

	private SfOpenApiService sfOpenApiService;
	
	private OrderService orderService;
	
	
	/**
	 * 下单结果通知
	 * 下单成功  顺丰返回运单号 签回单单号等信息
	 */
	 @RequestMapping(value = "/open/sfApi/getResult")
	 public JSONObject api(@RequestBody String body) throws Exception {
		     JSONObject result=null;
        	 JSONObject j=JSONObject.parseObject(body);
       try {
        	 //得到对应订单号
        	 String orderId=j.getString("orderId");
        	 //得到顺丰返回的运单号
        	 String mailNo=j.getString("mailNo");
        	 //原寄地代码
        	 String originCode=j.getString("originCode");
        	 //目的地代码
        	 String destCode=j.getString("destCode");
        	 //签回单单号
        	 String returnTrackingNo=j.getString("returnTrackingNo");
        	 
        	 //将得到的信息存入对应的微信订单表中
        	 Order order=orderService.queryByOrderNo(orderId);
        	 
        	 //按照顺丰接口要求返回对应参数
        	 SfToken sf=new SfToken();
        	 
        	 JSONObject resultHead=new JSONObject();
        	 resultHead.put("transType", "4201");
        	 resultHead.put("transMessageId", sf.getTransMessageId());
        	 JSONObject resultBody=new JSONObject();
        	 resultBody.put("orderId", sf.getOrderNo());
        	 result.put("head", resultHead);
        	 result.put("body", resultBody);
		} catch (Exception e) {
			System.out.println("返回结果异常");
			e.printStackTrace();
		}
         return result;
	 }
	 
	 @Test
	 public void add(){
		
		   ApplicationContext ctx = new FileSystemXmlApplicationContext( "classpath:applicationContext.xml");  
		   System.out.println("加载");
	 }
	 
	
}
