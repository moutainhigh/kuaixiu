package com.kuaixiu.recycle.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kuaixiu.recycle.entity.RecycleCustomer;
import com.kuaixiu.recycle.entity.RecycleOrder;

/**
 * @author: anson
 * @CreateDate: 2017年11月21日 下午7:02:48
 * @version: V 1.0
 * 指定位置创建json文件并写入内容
 */
public class ReadJson {

	// 文件路径+名称
	private static String filenameTemp;
	/**
	 * 反馈类型 2结束
	 */
	public static final Integer END=2;
	/**
	 * 反馈类型 4退货
	 */
	public static final Integer SALES_RETURN=4;
	/**
	 * 反馈类型 7金额预支
	 */
	public static final Integer PREPARE=7;
	/**
	 * 反馈类型 9异议
	 */
	public static final Integer OBJECTION=9;

	/**
	 * 创建文件
	 * 
	 * @param fileName
	 *            文件名称
	 * @param filecontent
	 *            文件内容
	 * @return 是否创建成功，成功则返回true
	 */
	public static String createFile(RecycleOrder order,RecycleCustomer cust,String path,Integer type) {
		// 根据回收订单号加当前时间存入
		filenameTemp = path + "/" +(order.getOrderNo()+"-"+System.currentTimeMillis()) + ".json";// 文件路径+名称+文件类型
		File file = new File(filenameTemp);
		try {
			// 如果文件不存在，则创建新的文件  如果文件名存在则删除后新建
			if(!file.exists()){
				file.createNewFile();
				System.out.println("success create file,the file is " + filenameTemp);
			}
			
			// 创建文件成功后，写入内容到文件里
			writeFileContent(filenameTemp, order,cust,type);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return filenameTemp;
	}

	/**
	 * 向文件中写入内容
	 * 
	 * @param filepath
	 *            文件路径与名称
	 * @param newstr
	 *            写入的内容
	 * @return
	 * @throws IOException
	 */
	public static boolean writeFileContent(String filepath, RecycleOrder order, RecycleCustomer cust,Integer type) throws IOException {
		Date now = new Date();
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		Boolean bool = false;
		String newstr = null;
		// 写入json格式
		JSONObject j = new JSONObject();
		JSONArray a = new JSONArray();
		j.put("records", a);
		JSONObject js = new JSONObject();
		js.put("biz_date", sf.format(now));
		js.put("user_name", cust.getName());
		js.put("user_credentials_type", "0");
		js.put("user_credentials_no", cust.getCertNo());
		js.put("order_no", order.getOrderNo());     //标识本笔业务的唯一编号。
		//反馈业务状态含义 0-未还款；1-超期；2-结束；3-申请；4-退货；5-用户确认；7-金额预支；9-异议；
		js.put("order_status", type.toString());
		js.put("order_status_date", sf.format(now));
		js.put("object_name", order.getProductName());

//		if(type==7){
//			//预支付
//			js.put("evaluate_amt", order.getPrice());
//			js.put("advance_amt", order.getPreparePrice());
//		}else if(type==2){
//			//流程结束
//		}else if(type==4){
//			//退货 
//			js.put("evaluate_amt", order.getPrice());
//			js.put("advance_amt", order.getPreparePrice());
//			js.put("bill_amt", order.getPreparePrice());      //根据实际评估后，客户应返还商户的实际金额
//		}else if(type==9){
//			//异议 
//			js.put("evaluate_amt", order.getPrice());         //物品的初评金额
//			js.put("actual_eva_amt", order.getFinalPrice());  //物品的实际评估金额             
//			js.put("advance_amt", order.getPreparePrice());   //打给用户的预付金额
//			
//		}
		
		js.put("evaluate_amt", 0.2);                 //物品的初评金额
		js.put("actual_eva_amt", 0.2);               //物品的实际评估金额
		js.put("advance_amt", 0.1);                  //打给用户的预付金额
		js.put("bill_amt", "0.10");                  //根据实际评估后，客户应返还商户的实际金额
		
		js.put("bill_type", "200");                  //200.终端(实物如手机、机顶盒、摄像头)
		js.put("bill_last_date", sf.format(now));    //要求客户归还所有"账单应还金额"的最迟日期
		js.put("memo", "0.00");                      //备注
		a.add(js);

		newstr = j.toJSONString();
		String filein = newstr + "\r\n";

		FileOutputStream fos = null;
		PrintWriter pw = null;
		try {
			File file = new File(filepath);          // 文件路径(包括文件名称)
			StringBuffer buffer = new StringBuffer();
			buffer.append(filein);
			fos = new FileOutputStream(file);
			pw = new PrintWriter(fos);
			pw.write(buffer.toString().toCharArray());
			pw.flush();
			bool = true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			if (pw != null) {
				pw.close();
			}
			if (fos != null) {
				fos.close();
			}
		}
		return bool;
	}

	public static void main(String[] args) {
		HttpClient client = new HttpClient();   
		//String url="https://zmhatcher.zmxy.com.cn/creditlife/operatorEntrance.htm?productId=2017110101000222123419688237&channel=creditlife&callBackUrl=http://17t033o025.iok.la/tpl/order.html";
		String url="http://www.baidu.com";
		HttpMethod method = new GetMethod(url);
		 try {
			 client.executeMethod(method);
			 System.out.println("状态："+method.getStatusLine());
			 System.out.println("内容："+method.getResponseBodyAsString());
			 method.releaseConnection();
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
