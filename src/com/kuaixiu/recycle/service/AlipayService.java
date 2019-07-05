package com.kuaixiu.recycle.service;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayFundTransToaccountTransferRequest;
import com.alipay.api.response.AlipayFundTransToaccountTransferResponse;
import com.common.util.NOUtil;
import com.kuaixiu.recycle.entity.RecycleCustomer;
import com.kuaixiu.recycle.entity.RecycleOrder;
import com.kuaixiu.recycle.entity.RecyclePay;
import com.system.constant.SystemConstant;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.SystemException;
import java.util.List;
import java.util.UUID;

/**
 * @author: anson
 * @CreateDate: 2018年1月18日 上午10:36:10
 * @version: V 1.0
 * 支付宝 转账 代扣
 * https://docs.open.alipay.com/api_28/alipay.fund.trans.toaccount.transfer
 * 
 * 沙箱环境 https://openapi.alipaydev.com/gateway.do
 * 正式环境 https://openapi.alipay.com/gateway.do
 */
@Service("alipayService")
public class AlipayService {

	private static final Logger log = Logger.getLogger(AlipayService.class);

	@Autowired
	private RecyclePayService recyclePayService;
	@Autowired
	private RecycleCustomerService recycleCustomerService;
	@Autowired
	private RecycleOrderService recycleOrderService;
	
	/**
	 * 支付宝发起转账
	 * type     "区分支付类型"         1-信用预支付      2-支付余款
	 * callType "区分支付发起类型"     1-回收系统发起  2-财务管理系统发起   3-系统订单发起 
	 * @throws SystemException 
	 */
	public boolean transfer(RecycleOrder order,String type,String callType) throws SystemException {
		boolean tip=false;
		AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do",
				SystemConstant.ZHIFUMCH_APPID, SystemConstant.ZHIFUMCH_PRIVATE_RSA, "json", "UTF-8",
				SystemConstant.ZHIFUMCH_PUBLIC_RSA, "RSA2");
		AlipayFundTransToaccountTransferRequest request = new AlipayFundTransToaccountTransferRequest();
		RecyclePay pay = new RecyclePay();                                           //设置转账订单信息
		RecycleCustomer cust=recycleCustomerService.queryById(order.getCustomerId());
		pay.setId(UUID.randomUUID().toString().replace("-", ""));
        pay.setRecycleOrderNo(order.getOrderNo());
        pay.setPayShowName("superman");
		pay.setPayeeRealName(cust.getName());
		pay.setPayType(Integer.parseInt(type));                                      //1:信用预支付;2:支付余款
		pay.setPayLaunchType(Integer.parseInt(callType));                            
		pay.setOutBizNo(NOUtil.getNo("PN-"));                                        //商户转账唯一订单号  自定义
		
		//通过订单来源确定转账账号为支付宝id还是手机号
		if(order.getSourceType()==0){
			//订单来源为支付宝生活号 转账使用用户支付宝id
			pay.setPayeeType("ALIPAY_USERID");
			pay.setPayeeAccount(cust.getUserId());
		}else{
			//订单来源为微信平台  转账使用用户填写的支付宝手机号
			pay.setPayeeType("ALIPAY_LOGONID");
			pay.setPayeeAccount(order.getMobile());
		}
		
		
		//判断该支付类型是否已经支付过
		if(type.equals("1")){                              // 预支付 
			RecyclePay r=new RecyclePay();
			r.setRecycleOrderNo(order.getOrderNo());
			r.setPayStatus(1);
			r.setPayType(1);
			List<RecyclePay> queryList =recyclePayService.queryList(r);
			if(!queryList.isEmpty()){
				throw new SystemException("预支付订单已提交，请勿重复提交");
			}
			pay.setAmount(order.getPreparePrice());
		}else if(type.equals("2")){                                            // 支付尾款
			RecyclePay r=new RecyclePay();
			r.setRecycleOrderNo(order.getOrderNo());
			r.setPayStatus(1);
			r.setPayType(2);
			List<RecyclePay> queryList =recyclePayService.queryList(r);
			if(!queryList.isEmpty()){
				throw new SystemException("支付尾款订单已提交，请勿重复提交");
			}
			pay.setAmount(order.getFinalPrice().subtract(order.getPreparePrice()));
		}else{ 
			    throw new SystemException("支付类型错误");
		    }     
		
		
		
		
		//发起接口请求
		request.setBizContent(getPostNews(pay).toJSONString());  
		AlipayFundTransToaccountTransferResponse response = null;
		try {
			response = alipayClient.execute(request);
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}
		log.info("返回主体:" + response.getBody());
		log.info("返回参数：" + response.getParams());
		JSONObject data=JSONObject.parseObject(response.getBody());
		String payNews = data.getString("alipay_fund_trans_toaccount_transfer_response");
		JSONObject resultNews=JSONObject.parseObject(payNews);
		if (response.isSuccess()) {
			tip=true;
			log.info("转账接口调用成功");
			pay.setPayStatus(1);                                              //修改转账订单状态
			pay.setAlipayTransactionNo(resultNews.getString("order_id"));	  //保存支付宝转账单据号  
			pay.setPaySuccessTime(resultNews.getString("pay_date"));
			order.setPayMentId(pay.getId());
			if(Integer.parseInt(type)==1){
				order.setOrderStatus(RecycleOrder.PREPARE_SUCCESS);   //预支付
			}else if(Integer.parseInt(type)==2){
				order.setOrderStatus(RecycleOrder.SUCCESS);        //支付尾款
			}
		} else {
			tip=false;
			log.info("转账接口调用失败");
			pay.setPayStatus(0);            
			pay.setMsg(resultNews.getString("sub_msg"));
			if(Integer.parseInt(type)==1){
				order.setOrderStatus(RecycleOrder.PREPARE_FAIL);   //预支付
			}else if(Integer.parseInt(type)==2){
				order.setOrderStatus(RecycleOrder.FINAL_FAIL);     //支付尾款
			}
		}
		recyclePayService.add(pay);                                  // 新增支付记录
		recycleOrderService.saveUpdate(order);                       // 更新回收订单数据
		if(!tip){
			throw new SystemException("支付宝转账接口调用失败");
		}
		return tip;
	}

	
	
	
	
	
	
	
	
	/**
	 * 包装请求数据
	 * @param order
	 * @param cust
	 * @return
	 */
	public JSONObject getPostNews(RecyclePay pay){
		JSONObject j = new JSONObject();
		j.put("out_biz_no", pay.getOutBizNo());
		j.put("payee_type", pay.getPayeeType());
		j.put("payee_account", pay.getPayeeAccount());
		j.put("amount", pay.getAmount());
		j.put("payer_show_name", pay.getPayShowName());
		j.put("payee_real_name", pay.getPayeeRealName());
		j.put("remark", pay.getRemark());
		return j;
	}
	
	
	
	
	public RecyclePay getPay(RecycleOrder order) {
		RecyclePay pay = new RecyclePay();
		RecycleCustomer cust=recycleCustomerService.queryById(order.getCustomerId());
		pay.setId(UUID.randomUUID().toString().replace("-", ""));
        pay.setRecycleOrderNo(order.getOrderNo());
		pay.setPayeeRealName(cust.getName());
		return pay;
	}
	

	public static void main(String[] args) {
		transferTest();
	}
	
	
	
	public static void transferTest(){
		AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do",
				SystemConstant.ZHIFUMCH_APPID, SystemConstant.ZHIFUMCH_PRIVATE_RSA, "json", "UTF-8",
				SystemConstant.ZHIFUMCH_PUBLIC_RSA, "RSA2");
		AlipayFundTransToaccountTransferRequest request = new AlipayFundTransToaccountTransferRequest();
//		JSONObject j = new JSONObject();
//		j.put("out_biz_no", "3142321423432");
//		j.put("payee_type", "ALIPAY_LOGONID");
//		j.put("payee_account", "abc@sina.com");
//		j.put("amount", "12.23");
//		j.put("payer_show_name", "上海交通卡退款");
//		j.put("payee_real_name", "张三");
//		j.put("remark", "转账备注");
//		request.setBizContent(j.toJSONString());  
		request.setBizContent("{" +
				"\"out_biz_no\":\"3142321423442\"," +
				"\"payee_type\":\"ALIPAY_LOGONID\"," +
				"\"payee_account\":\"15356152347\"," +
				"\"amount\":\"0.1\"," +
				"\"payer_show_name\":\"测试转账\"," +
				"\"payee_real_name\":\"高琼安\"," +
				"\"remark\":\"转账备注\"" +
				"}");
		AlipayFundTransToaccountTransferResponse response = null;
		try {
			response = alipayClient.execute(request);
			
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}
		if (response.isSuccess()) {
			System.out.println("返回成功");
		} else {
			System.out.println("返回错误");
		}
		log.info("返回主体:" + response.getBody());
		log.info("返回参数：" + response.getParams());
	}
}
