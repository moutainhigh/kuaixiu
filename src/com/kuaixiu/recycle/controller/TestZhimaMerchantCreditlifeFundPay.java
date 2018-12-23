package com.kuaixiu.recycle.controller;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author: anson
* @CreateDate: 2017年11月23日 下午4:59:39
* @version: V 1.0
* 信用套餐-商户扣款
* 接入商户通过该API发起扣款
* https://b.zmxy.com.cn/technology/openDoc.htm?id=845
*/
import com.antgroup.zmxy.openplatform.api.DefaultZhimaClient;
import com.antgroup.zmxy.openplatform.api.ZhimaApiException;
import com.antgroup.zmxy.openplatform.api.request.ZhimaMerchantCreditlifeFundPayRequest;
import com.antgroup.zmxy.openplatform.api.response.ZhimaMerchantCreditlifeFundPayResponse;
import com.common.exception.SystemException;
import com.common.util.NOUtil;
import com.kuaixiu.recycle.entity.RecycleCustomer;
import com.kuaixiu.recycle.entity.RecycleOrder;
import com.kuaixiu.recycle.entity.RecyclePay;
import com.kuaixiu.recycle.service.RecycleCustomerService;
import com.kuaixiu.recycle.service.RecycleOrderService;
import com.kuaixiu.recycle.service.RecyclePayService;
import com.system.constant.SystemConstant;

@Service
public class TestZhimaMerchantCreditlifeFundPay {
	    // 芝麻开放平台地址
		private String gatewayUrl = SystemConstant.ZHIMA_OPEN_URL;
		// 商户应用 Id
		private String appId = SystemConstant.ZHIMA_APPID;
		// 商户 RSA 私钥
		private String privateKey = SystemConstant.ZHIFUBAO_PRIVATE_RSA;
		// 芝麻 RSA 公钥
		private String zhimaPublicKey = SystemConstant.ZHIMA_PUBLIC_RSA;
        @Autowired
		private RecyclePayService recyclePayService;
        @Autowired
        private RecycleCustomerService recycleCustomerService;
        @Autowired
        private RecycleOrderService recycleOrderService;
		/**
		 * 支付宝接入商户发起扣款  测试方法
		 * @param order
		 */
    public void testZhimaMerchantCreditlifeFundPay() {
        ZhimaMerchantCreditlifeFundPayRequest req = new ZhimaMerchantCreditlifeFundPayRequest();
        String transactionId=new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date())+ UUID.randomUUID().toString();
        System.out.println(transactionId);
        req.setChannel("apppc");
        req.setPlatform("zmop");
        req.setOutOrderNo("PN-20180312165033145");//必要参数 商户订单号
        req.setTransactionId(transactionId);      //必要参数 业务流水号
        req.setUserId("2088022820323602");        //必要参数 支付宝用户id（付款方id） 
        req.setPayAmount("0.1");                  //必要参数 支付金额
        req.setFundPayType("withholding_pay");    //必要参数 扣款类型(withholding_pay:代扣扣款,preauth_pay:预授权转支付)
        req.setSellerId("2017103009618971");      //必要参数 收款方支付宝id
       // req.setPreAuthNo("208830208450818112"); //预授权号(付款方式为预授权转支付时必须提供)
       // req.setAgreementNo("2017302084508181"); //代扣协议号(代扣扣款时必须提供)
        req.setGoodsTitle("信用购机");             // 必要参数 
        req.setGoodsType("1");                    // 必要参数  商品类型(0:虚拟物品,1:实物)
        req.setRoleId("268815953610987723926746000");// 必要参数 芝麻用户id
        DefaultZhimaClient client = new DefaultZhimaClient(gatewayUrl, appId, privateKey, zhimaPublicKey);
        try {
            ZhimaMerchantCreditlifeFundPayResponse response = client.execute(req);
            System.out.println(response.isSuccess());
            System.out.println(response.getErrorCode());
            System.out.println(response.getErrorMessage());
        } catch (ZhimaApiException e) {
            e.printStackTrace();
        }
    }
    
    
    /**
	 * 支付宝接入商户发起扣款
	 * @param order
	 * @param type  扣款 发起类型 1-回收系统发起  2-财务管理系统发起 3-系统订单发起
	 */
    public boolean OrderFundPay(RecycleOrder order,int type) {
        boolean tip=false;
    	ZhimaMerchantCreditlifeFundPayRequest req = new ZhimaMerchantCreditlifeFundPayRequest();
        String transactionId=new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date())+ UUID.randomUUID().toString();
        System.out.println(transactionId);
        RecyclePay r=new RecyclePay();
        r.setPayStatus(1); //状态已完成 
        r.setPayType(1);   //信用预支付
        r.setRecycleOrderNo(order.getOrderNo());
        //查询支付记录
        List<RecyclePay> list= recyclePayService.queryList(r);
        if(list.isEmpty()){
        	throw new SystemException("该订单不存在支付记录，不能发起扣款");
        }
        //查询用户信息
        RecycleCustomer cust=recycleCustomerService.queryById(order.getCustomerId());
        if(cust==null){
        	throw new SystemException("支付接口异常，请稍后重试");
        }
        BigDecimal subtract = order.getPreparePrice().subtract(order.getFinalPrice());
        req.setChannel("apppc");
        req.setPlatform("zmop");
        req.setOutOrderNo(list.get(0).getOutBizNo());                   //必要参数 商户订单号
        req.setTransactionId(transactionId);                            //必要参数 业务流水号
        req.setUserId(cust.getUserId());                                //必要参数 支付宝用户id（付款方id） 
        req.setPayAmount(subtract.toString());                          //必要参数 支付金额
        req.setFundPayType("withholding_pay");                          //必要参数 扣款类型(withholding_pay:代扣扣款,preauth_pay:预授权转支付)
        req.setSellerId(SystemConstant.ZHIFUMCH_APPID);                 //必要参数 收款方支付宝id
       // req.setPreAuthNo("208830208450818112");                       //预授权号(付款方式为预授权转支付时必须提供)
       // req.setAgreementNo("2017302084508181");                       //代扣协议号(代扣扣款时必须提供)
        req.setGoodsTitle("信用购机");                                   // 必要参数 
        req.setGoodsType("1");                                          // 必要参数  商品类型(0:虚拟物品,1:实物)
        req.setRoleId(cust.getOpenId());                                // 必要参数 芝麻用户id
        DefaultZhimaClient client = new DefaultZhimaClient(gatewayUrl, appId, privateKey, zhimaPublicKey);
        try {
            ZhimaMerchantCreditlifeFundPayResponse response = client.execute(req);
            System.out.println(response.isSuccess());
            System.out.println(response.getErrorCode());
            System.out.println(response.getErrorMessage());
            if(response.isSuccess()){
            	order.setOrderStatus(RecycleOrder.SUCCESS);
            	tip=true;
            }else{
            	order.setOrderStatus(RecycleOrder.FUND_FAIL);
            }
            //退款调用完毕 新增一条退款操作记录 
            RecyclePay addPay = addPay(order,cust,type);
            
            recyclePayService.add(addPay);
            recycleOrderService.saveUpdate(order);
        } catch (ZhimaApiException e) {
            e.printStackTrace();
        }
        return tip;
    }
    
    /**
     * 新增退款记录
     * @param order
     * @param cust
     * @param type
     */
    public RecyclePay addPay(RecycleOrder order,RecycleCustomer cust,int type){
    	 RecyclePay pay = new RecyclePay();    
         pay.setId(UUID.randomUUID().toString().replace("-", ""));
         pay.setRecycleOrderNo(order.getOrderNo());
         pay.setPayShowName("superman");
 		 pay.setPayeeRealName(cust.getName());
 		 pay.setPayType(3);                                      //3发起扣款
 		 pay.setPayLaunchType(type);                            
 		 pay.setOutBizNo(NOUtil.getNo("PN-"));                      
 		 if(order.getSourceType()==0){
 			//订单来源为支付宝生活号 转账使用用户支付宝id
 			pay.setPayeeType("ALIPAY_USERID");
 			pay.setPayeeAccount(cust.getUserId());
 		 }else{
 			//订单来源为微信平台  转账使用用户填写的支付宝手机号
 			pay.setPayeeType("ALIPAY_LOGONID");
 			pay.setPayeeAccount(order.getMobile());
 		}
 		return pay;
    }
    
    

    public static void main(String[] args) {
        TestZhimaMerchantCreditlifeFundPay result = new  TestZhimaMerchantCreditlifeFundPay();
        result.testZhimaMerchantCreditlifeFundPay();
    }
}
