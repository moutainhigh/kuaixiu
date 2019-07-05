package com.kuaixiu.recycle.controller;

import com.antgroup.zmxy.openplatform.api.DefaultZhimaClient;
import com.antgroup.zmxy.openplatform.api.FileItem;
import com.antgroup.zmxy.openplatform.api.ZhimaApiException;
import com.antgroup.zmxy.openplatform.api.request.ZhimaDataBatchFeedbackRequest;
import com.antgroup.zmxy.openplatform.api.response.ZhimaDataBatchFeedbackResponse;
import com.kuaixiu.recycle.entity.RecycleCustomer;
import com.kuaixiu.recycle.entity.RecycleOrder;
import com.system.constant.SystemConstant;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.math.BigDecimal;

/**
* @author: anson
* @CreateDate: 2017年11月21日 下午12:34:00
* @version: V 1.0
* 芝麻信用反馈
* https://b.zmxy.com.cn/technology/openDoc.htm?relInfo=zhima.data.batch.feedback@1.0@1.1
//*/
public class TestZhimaDataBatchFeedback {
	// 芝麻开放平台地址
	private static String gatewayUrl = SystemConstant.ZHIMA_OPEN_URL;
	// 商户应用 Id
	private static String appId = SystemConstant.ZHIMA_APPID;
	// 商户 RSA 私钥
	private static String privateKey = SystemConstant.ZHIFUBAO_PRIVATE_RSA;
	// 芝麻 RSA 公钥
	private static String zhimaPublicKey = SystemConstant.ZHIMA_PUBLIC_RSA;
    // type_id 推送数据的标识  测试环境300000636-default-test  正式环境
	private static String typeId="300000636-default-test";
	
    /**
     * 
     * @param order
     * @param cust
     * @param request
     * @param type  反馈类型：2结束(线上线下评估金额一致),4退货(用户取消订单，返还预支付金额),
     *                       7金额预支(预支付),9异议(线上线下评估金额不一致)   
     */
    public static void  testZhimaDataBatchFeedback(RecycleOrder order, RecycleCustomer cust,HttpServletRequest request,Integer type) {
        ZhimaDataBatchFeedbackRequest req = new ZhimaDataBatchFeedbackRequest();
        req.setChannel("apppc");
        req.setPlatform("zmop");
        req.setFileType("json_data");// 必要参数 
        req.setFileCharset("UTF-8");// 必要参数 
        req.setRecords("1");// 必要参数 
        req.setColumns("biz_date,user_name,user_credentials_type,user_credentials_no,order_no,order_status,order_status_date,object_name,evaluate_amt,advance_amt,actual_eva_amt,bill_type,bill_amt,bill_last_date,memo");// 必要参数 
        req.setPrimaryKeyColumns("order_no");// 必要参数 
        req.setFileDescription("支付宝转账业务JSON数据");// 
        req.setTypeId(typeId);// 必要参数 
        req.setBizExtParams("{\"extparam1\":\"value1\"}");// 
        
        //存入指定目录  
    	String path = "/json";
		String savePath = request.getServletContext().getRealPath("");
		System.out.println(savePath);
		String serverPath = serverPath(savePath); // 得到服务器位置
		String upload = serverPath + path;
		File saveFileDir = new File(upload);
		if (!saveFileDir.exists()) {
			// 如果不存在则创建目录
			saveFileDir.mkdirs();
		}
        //向json文件夹里创建json文件并写入json格式内容并得到文件路径
        String realPath=ReadJson.createFile(order,cust,upload,type);
		
        req.setFile(new FileItem(realPath));// 必要参数 
        DefaultZhimaClient client = new DefaultZhimaClient(gatewayUrl, appId, privateKey, zhimaPublicKey);
        try {
            ZhimaDataBatchFeedbackResponse response = client.execute(req);
            System.out.println(response.isSuccess());
            System.out.println(response.getErrorCode());
            System.out.println(response.getErrorMessage());
            System.out.println(response.getBizSuccess());
        } catch (ZhimaApiException e) {
            e.printStackTrace();
        }
    }
    
    
    /**
	 * 得到服务器路径 如E:\tomcat\apache-tomcat-8.0.46
	 * 
	 * @param path
	 * @return
	 */
	public static String serverPath(String path) {
		int a = path.lastIndexOf("-");
		int a1 = path.lastIndexOf("/");
		int b1 = path.lastIndexOf("\\");
		String realPath = "";
		if (a1 > 0) {
			int b = path.indexOf("/", a);
			realPath = path.substring(0, b);
		} else if (b1 > 0) {
			int b = path.indexOf("\\", a);
			realPath = path.substring(0, b);
		}
		return realPath;
	}

    public static void main(String[] args) {
    	RecycleOrder order=new RecycleOrder();
//        order.setName("高琼安");	
//        order.setCertNo("421023199304090418");
        order.setOrderNo("20171128105359995");
        order.setProductName("iPhone6s");
        order.setPrice(new BigDecimal(2000));
        order.setPreparePrice(new BigDecimal(1400));
 //       order.setTransactionId("201711281053200039dfdcd88-0e6b-47ff-bbce-ba974002d3b7");
  //      testZhimaDataBatchFeedback(order);
    }
}