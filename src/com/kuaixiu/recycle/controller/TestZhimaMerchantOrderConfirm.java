package com.kuaixiu.recycle.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.antgroup.zmxy.openplatform.api.DefaultZhimaClient;
import com.antgroup.zmxy.openplatform.api.ZhimaApiException;
import com.antgroup.zmxy.openplatform.api.request.ZhimaMerchantOrderConfirmRequest;
import com.antgroup.zmxy.openplatform.api.response.ZhimaMerchantOrderConfirmResponse;
import com.common.base.controller.BaseController;
import com.common.exception.SystemException;
import com.common.util.AES;
import com.common.wechat.common.util.StringUtils;
import com.kuaixiu.recycle.entity.RecycleCustomer;
import com.kuaixiu.recycle.entity.RecycleOrder;
import com.kuaixiu.recycle.service.RecycleCustomerService;
import com.kuaixiu.recycle.service.RecycleOrderService;
import com.system.api.entity.ResultData;
import com.system.basic.user.service.SessionUserService;
import com.system.constant.SystemConstant;
import jodd.util.StringUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author: anson
 * @CreateDate: 2017年11月6日 下午4:39:16
 * @version: V 1.0
 * 芝麻信用提交订单  获取芝麻用户信息 设置芝麻业务流水号
 * https://b.zmxy.com.cn/technology/openDoc.htm?relInfo=zhima.merchant.order.confirm@1.0@1.5
 */
@Controller
public class TestZhimaMerchantOrderConfirm extends BaseController {

	private static final Logger log= Logger.getLogger(TestZhimaMerchantOrderConfirm.class);
	@Autowired
	private SessionUserService sessionUserService;
	@Autowired
	private RecycleOrderService recycleOrderService;
	@Autowired
	private RecycleCustomerService recycleCustomerService;
	// 芝麻开放平台地址
	private String gatewayUrl = SystemConstant.ZHIMA_OPEN_URL;
	// 商户应用 Id
	private String appId = SystemConstant.ZHIMA_APPID;
	// 商户 RSA 私钥
	private String privateKey = SystemConstant.ZHIFUBAO_PRIVATE_RSA;
	// 芝麻 RSA 公钥
	private String zhimaPublicKey = SystemConstant.ZHIMA_PUBLIC_RSA;
	// 根据回收流水号查询订单信息地址
    private String quoteUrl=SystemConstant.RECYCLE_URL+"getquotedetail";
	// 根据回收流水号查询订单信息地址
	private String quoteNewUrl=SystemConstant.RECYCLE_NEW_URL+"getquotedetail";
    /**
     * 信用回收最低分数 600
     */
    private Integer lowScore=600;
    /**
     * 高信用回收分数    700
     */
    private Integer highScore=700;
    /**
     *  600-699  预支付百分之50
     */
    private Integer lowPercent=50;
    /**
     *  700以上    预支付百分之70
     */
    private Integer highPercent=70;
    /**
     *  600-699  最高预付金额  400
     */
    private Integer lowPrice=400;
    /**
     *  700   以上最高预付金额  1500
     */
    private Integer highPrice=1500;
    /**
     * 1个月时间 单位毫秒
     */
    private Long time=2592000000L;
    
    
	public ZhimaMerchantOrderConfirmResponse testZhimaMerchantOrderConfirm(String orderNo,HttpServletRequest request) {
		ZhimaMerchantOrderConfirmRequest req = new ZhimaMerchantOrderConfirmRequest();
		req.setChannel("apppc");
		req.setPlatform("zmop");
		req.setOrderNo(orderNo);// 必要参数  
		String transactionId=new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date())+ UUID.randomUUID().toString();
		req.setTransactionId(transactionId);// 流水号  官方推荐30位
		//将流水号存入session  请求成功后返回流水号给回收项目组
	    request.getSession().setAttribute("transactionId",transactionId);
		DefaultZhimaClient client = new DefaultZhimaClient(gatewayUrl, appId, privateKey, zhimaPublicKey);
		try {
			ZhimaMerchantOrderConfirmResponse response = client.execute(req);
			if(response.isSuccess()==false){
				throw new SystemException(response.getErrorMessage(),response.getErrorCode());
			}
			return response;
		} catch (ZhimaApiException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 通过芝麻信用获取用户信息
	 */
	@RequestMapping(value = "recycleNew/zhimaNews")
	public void getNewUrlZhimaNews(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ResultData result = new ResultData();
		try {
			JSONObject params = getPrarms(request);
			String orderNo =params.getString("orderNo");
			if (StringUtil.isBlank(orderNo)) {
				throw new SystemException("订单号不能为空");
			}
			String quoteId=(String) request.getSession().getAttribute("quoteId");
			if(StringUtil.isBlank(quoteId)){
				throw new SystemException("回收流水号不能为空");
			}
			String imagePath=(String) request.getSession().getAttribute("imagePath");
			//根据回收流水号查询回收订单信息
			String modelName=null;           //订单产品名称
			BigDecimal price=null;           //订单金额
			JSONObject orderNews=new JSONObject();
			orderNews=getNewUrlRecycleNews(quoteId);
			if(orderNews!=null){
				JSONObject  info=orderNews.getJSONObject("datainfo");
				if(info!=null){
					modelName=info.getString("modelname");
					price=info.getBigDecimal("price");
				}
			}else{
				throw new SystemException("回收流水号不存在");
			}



			// 开始向芝麻发起请求
			TestZhimaMerchantOrderConfirm confirm = new TestZhimaMerchantOrderConfirm();
			ZhimaMerchantOrderConfirmResponse news = confirm.testZhimaMerchantOrderConfirm(orderNo,request);
			JSONObject zhimaNews=getNews(news,request);
			String str = JSONObject.toJSONString(zhimaNews,SerializerFeature.WriteMapNullValue);
			System.out.println("芝麻返回数据"+str);


			//风控条件判断
			Integer type=null;                              //0表示信用回收  1表示普通回收
			Integer percent=0;                              //支付宝预支付比额
			BigDecimal preparePrice=new BigDecimal("0");                   //预付金额
			//判断芝麻分是否大于或等于600且人脸识别结果和风控产品集联合结果通过
			if(StringUtil.isNotBlank(news.getZmScore())&&StringUtil.isNotBlank(news.getZmFace())&&StringUtil.isNotBlank(news.getZmRisk())){
				//人脸识别和芝麻风投是否通过
				if(news.getZmFace().equals("Y")&&news.getZmRisk().equals("Y")){
					int score=Integer.parseInt(news.getZmScore()); //用户芝麻分
					if(score>=lowScore){
						type=0;
						//芝麻分在600-699之间  预支付百分之50最高400   超过700的预支付百分之70最高1500
						if(score>=highScore){
							percent=highPercent;
							preparePrice=price.multiply(new BigDecimal(percent).divide(new BigDecimal(100)));
							if(preparePrice.compareTo(new BigDecimal(highPrice))>0){
								preparePrice=new BigDecimal(highPrice);
							}
						}else{
							percent=lowPercent;
							preparePrice=price.multiply(new BigDecimal(percent).divide(new BigDecimal(100)));
							if(preparePrice.compareTo(new BigDecimal(lowPrice))>0){
								preparePrice=new BigDecimal(lowPrice);
							}
						}

					}else{
						//芝麻分小于600 普通回收
						type=1;
					}
					//每位用户仅限1笔未完成的信用回收订单，每月仅限申请3笔信用回收订单
					judge(news);

				}else{
					type=1;
					log.info("人脸识别未通过");
				}
			}else{
				type=1;
				log.info("芝麻信用评估失败");
			}


			//保证流水号对应订单的唯一性
			RecycleOrder recycle=recycleOrderService.queryByQuoteId(quoteId);
			if(recycle!=null){
				//如果存在，且未下单成功的记录则将之前的删除
				if(StringUtil.isBlank(recycle.getOrderNo())&&recycle.getOrderType()==0){
					recycleOrderService.delete(recycle);
				}else{
					throw new SystemException("请求重复该订单!");
				}
			}



			JSONObject zhima=new JSONObject();
			zhima.put("quoteid", quoteId);
			zhima.put("type", type);
			zhima.put("percent", percent);
			zhima.put("price", price);
			zhima.put("preparePrice", preparePrice);
			zhima.put("imagePath",imagePath);
			zhima.put("modelName", modelName);
			zhima.put("detail", request.getSession().getAttribute("detail"));

			//解析芝麻用户信息
			RecycleCustomer cust=addOrder(news,request,orderNo);
			RecycleOrder order=new RecycleOrder();
			String id=UUID.randomUUID().toString().replace("-", "");
			String customerId=UUID.randomUUID().toString().replace("-", "");
			order.setId(id);
			order.setRecycleType(type);
			order.setOrderType(0);  //预支付订单类型
			order.setSourceType(0); //支付宝来源
			order.setProductName(modelName);
			order.setPercent(percent);
			order.setPrice(price);
			order.setPreparePrice(preparePrice);
			order.setImagePath(imagePath);
			order.setDetail((String)request.getSession().getAttribute("detail"));
			order.setCustomerId(customerId);
			order.setCheckId(quoteId);
			recycleOrderService.add(order);    //添加预付订单记录
			cust.setId(customerId);
			recycleCustomerService.add(cust);  //添加芝麻用户信息
			result.setResult(zhima);
			result.setResultCode("0");
			result.setSuccess(true);
		} catch (SystemException e) {
			e.printStackTrace();
			sessionUserService.getSystemException(e, result);
		} catch (Exception e) {
			e.printStackTrace();
			sessionUserService.getException(result);
		}
		renderJson(response, result);
	}


	/**
	 * 通过芝麻信用获取用户信息
	 */
	@RequestMapping(value = "recycle/zhimaNews")
	public void getZhimaNews(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ResultData result = new ResultData();
		try {
         	JSONObject params = getPrarms(request);  
			String orderNo =params.getString("orderNo");
			if (StringUtil.isBlank(orderNo)) {
				throw new SystemException("订单号不能为空");
			}
			String quoteId=(String) request.getSession().getAttribute("quoteId");
			if(StringUtil.isBlank(quoteId)){
				throw new SystemException("回收流水号不能为空");
			}
			String imagePath=(String) request.getSession().getAttribute("imagePath");
			//根据回收流水号查询回收订单信息
			String modelName=null;           //订单产品名称
			BigDecimal price=null;           //订单金额
			JSONObject orderNews=new JSONObject();
			orderNews=getRecycleNews(quoteId);
			if(orderNews!=null){
					JSONObject  info=orderNews.getJSONObject("datainfo");
				    if(info!=null){
				    	modelName=info.getString("modelname");
				    	price=info.getBigDecimal("price");
				    }
			}else{
				throw new SystemException("回收流水号不存在");
			}
			
			
			
			// 开始向芝麻发起请求
			TestZhimaMerchantOrderConfirm confirm = new TestZhimaMerchantOrderConfirm();
			ZhimaMerchantOrderConfirmResponse news = confirm.testZhimaMerchantOrderConfirm(orderNo,request);
			JSONObject zhimaNews=getNews(news,request);
			String str = JSONObject.toJSONString(zhimaNews,SerializerFeature.WriteMapNullValue);  
			System.out.println("芝麻返回数据"+str);
			
			
			//风控条件判断
			Integer type=null;                              //0表示信用回收  1表示普通回收
			Integer percent=0;                              //支付宝预支付比额
			BigDecimal preparePrice=new BigDecimal("0");                   //预付金额
			//判断芝麻分是否大于或等于600且人脸识别结果和风控产品集联合结果通过
			if(StringUtil.isNotBlank(news.getZmScore())&&StringUtil.isNotBlank(news.getZmFace())&&StringUtil.isNotBlank(news.getZmRisk())){
				//人脸识别和芝麻风投是否通过
				if(news.getZmFace().equals("Y")&&news.getZmRisk().equals("Y")){
					int score=Integer.parseInt(news.getZmScore()); //用户芝麻分
					if(score>=lowScore){
						type=0;
						//芝麻分在600-699之间  预支付百分之50最高400   超过700的预支付百分之70最高1500
						if(score>=highScore){
							percent=highPercent;
							preparePrice=price.multiply(new BigDecimal(percent).divide(new BigDecimal(100)));
							if(preparePrice.compareTo(new BigDecimal(highPrice))>0){
								preparePrice=new BigDecimal(highPrice);
							}
						}else{
							percent=lowPercent;
							preparePrice=price.multiply(new BigDecimal(percent).divide(new BigDecimal(100)));
							if(preparePrice.compareTo(new BigDecimal(lowPrice))>0){
								preparePrice=new BigDecimal(lowPrice);
							}
						}
						
					}else{
						//芝麻分小于600 普通回收 
						type=1;
					}
					//每位用户仅限1笔未完成的信用回收订单，每月仅限申请3笔信用回收订单
					judge(news);
					
				}else{
					type=1;
					log.info("人脸识别未通过");
				}
			}else{
				    type=1;
				    log.info("芝麻信用评估失败");
			}
			
			
			 //保证流水号对应订单的唯一性
			RecycleOrder recycle=recycleOrderService.queryByQuoteId(quoteId);
			if(recycle!=null){
				//如果存在，且未下单成功的记录则将之前的删除
				if(StringUtil.isBlank(recycle.getOrderNo())&&recycle.getOrderType()==0){
					recycleOrderService.delete(recycle);
				}else{
					throw new SystemException("请求重复该订单!");
				}
			}
			
			
			
			JSONObject zhima=new JSONObject();
			zhima.put("quoteid", quoteId);
			zhima.put("type", type);
			zhima.put("percent", percent);
			zhima.put("price", price);
			zhima.put("preparePrice", preparePrice);
			zhima.put("imagePath",imagePath);
			zhima.put("modelName", modelName);
			zhima.put("detail", request.getSession().getAttribute("detail"));
			
			//解析芝麻用户信息
			RecycleCustomer cust=addOrder(news,request,orderNo);
			RecycleOrder order=new RecycleOrder();
			String id=UUID.randomUUID().toString().replace("-", "");
			String customerId=UUID.randomUUID().toString().replace("-", "");
			order.setId(id);
		    order.setRecycleType(type);
		    order.setOrderType(0);  //预支付订单类型
		    order.setSourceType(0); //支付宝来源
		    order.setProductName(modelName);
		    order.setPercent(percent);
		    order.setPrice(price);
		    order.setPreparePrice(preparePrice);
		    order.setImagePath(imagePath);
		    order.setDetail((String)request.getSession().getAttribute("detail"));
		    order.setCustomerId(customerId);
		    order.setCheckId(quoteId);
				recycleOrderService.add(order);    //添加预付订单记录
				cust.setId(customerId);            
				recycleCustomerService.add(cust);  //添加芝麻用户信息
			result.setResult(zhima);
			result.setResultCode("0");
			result.setSuccess(true);
		} catch (SystemException e) {
			e.printStackTrace();
			sessionUserService.getSystemException(e, result);
		} catch (Exception e) {
			e.printStackTrace();
			sessionUserService.getException(result);
		}
		renderJson(response, result);
	}

	/**
	 * 
	 * 每位用户仅限1笔未完成的信用回收订单，每月仅限申请3笔信用回收订单
	 * @param news
	 */
	public void judge(ZhimaMerchantOrderConfirmResponse news){
		//每位用户仅限1笔未完成的信用回收订单，每月仅限申请3笔信用回收订单
		RecycleOrder query=new RecycleOrder();
		query.setMobile(news.getMobile());
		query.setRecycleType(0); //信用回收
		query.setOrderType(1);   //正式订单
		List<RecycleOrder> o=recycleOrderService.queryList(query);
		if(o.size()>0){
			for(RecycleOrder r:o){
				if(r.getOrderStatus()!=50){
					throw new SystemException("您已有信用回收订单正在进行中");
				}
			}
		}
		query.setOrderStatus(50); //信用回收流程完毕的订单
		List<RecycleOrder> list=recycleOrderService.queryList(query);
		int tip=0;  //当月完成的信用回收订单笔数
		if(list.size()>=3){
			for(RecycleOrder r:list){
				long nowTime=System.currentTimeMillis()-r.getUpdateTime().getTime();
				if(nowTime<time){
					tip++;
					if(tip>=3){
						throw new SystemException("您当月信用回收订单已超过三笔，请下月再试");
					}
				}
			}
		}
	}
	
	
	/**
	 * 储存芝麻用户信息 
	 */
	public RecycleCustomer addOrder(ZhimaMerchantOrderConfirmResponse z,HttpServletRequest request,String orderNo) {
		RecycleCustomer cust=new RecycleCustomer();
		cust.setName(z.getName());
		cust.setCertNo(z.getCertNo());
		if(StringUtils.isBlank(z.getMobile())){
			//如果返回为空 默认设置号码
			cust.setMobile("15356152347");
		}else{
			cust.setMobile(z.getMobile());
		}
		cust.setEmail(z.getEmail());
		cust.setZhimaNo(orderNo);
	//	order.setHouse(z.getHouse());  完整地址 拆分为 area  address
		cust.setAddressType(1);  //支付宝地址
 		cust.setArea(z.getHouse().substring(0,z.getHouse().lastIndexOf(" ")));
 		cust.setAddress(z.getHouse().substring(z.getHouse().lastIndexOf(" ")+1));
		cust.setOpenId(z.getOpenId());
		cust.setUserId(z.getUserId());
		//cust.setContactName(z.getContactName());
		//cust.setContactMobile(z.getContactMobile());
		if(StringUtil.isNotBlank(z.getZmRisk())){
			if(z.getZmFace().equals("Y")){
				cust.setZmRisk(0);
			}else{
				cust.setZmRisk(1);
			}
		}
		if(StringUtil.isNotBlank(z.getZmScore())){
			cust.setZmScore(Integer.parseInt(z.getZmScore()));
		}
		if(StringUtil.isNotBlank(z.getZmFace())){
			if(z.getZmFace().equals("Y")){
				cust.setZmFace(0);
			}else{
				cust.setZmFace(1);
			}
		}
		cust.setZmFacePic(z.getZmFacePic());
		cust.setTransactionId((String)request.getSession().getAttribute("transactionId"));
		cust.setChannelId(z.getChannelId());
		return cust;
	}
	
	
	public JSONObject getNews(ZhimaMerchantOrderConfirmResponse z,HttpServletRequest request) {
		JSONObject j =new JSONObject();
		j.put("name", z.getName());
		j.put("name_spell", z.getNameSpell());
		j.put("cert_no", z.getCertNo());
		j.put("gender", z.getGender());
		j.put("age", z.getAge());
		j.put("education_degree", z.getEducationDegree());
		j.put("mobile", z.getMobile());
		j.put("email", z.getEmail());
		j.put("city_name", z.getCityName());
		j.put("house", z.getHouse());
		j.put("house_type", z.getHouseType());
		j.put("marriage_status", z.getMarriageStatus());
		j.put("taxed_income", z.getTaxedIncome());
		j.put("company_name", z.getCompanyName());
		j.put("company_address", z.getCompanyAddress());
		j.put("company_contact", z.getCompanyContact());
		j.put("industry_type", z.getIndustryType());
		j.put("occupation", z.getOccupation());
		j.put("company_type", z.getCompanyType());
		j.put("position_level", z.getPositionLevel());
		j.put("department", z.getDepartment());
		j.put("worked_years", z.getWorkedYears());
		j.put("mailing_address", z.getMailingAddress());
		j.put("direct_relation", z.getDirectRelation());
		j.put("direct_relation_name", z.getDirectRelationName());
		j.put("direct_relation_mobile", z.getDirectRelationMobile());
		j.put("contact_relation", z.getContactRelation());
		j.put("contact_name", z.getContactName());
		j.put("contact_mobile", z.getContactMobile());
		j.put("open_id", z.getOpenId());
		j.put("user_id", z.getUserId());
		j.put("channel_id", z.getChannelId());
		j.put("cert_city", z.getCertCity());
		j.put("cert_indate", z.getCertIndate());
		j.put("zm_risk", z.getZmRisk());
		j.put("zm_score", z.getZmScore());
		j.put("zm_face", z.getZmFace());
		j.put("zm_face_pic", z.getZmFacePic());
		j.put("ivs_detail", z.getIvsDetails());
		j.put("wetchlist_detail", z.getWatchlistDetail());
		j.put("visa_report", z.getVisaReport());
		j.put("passport_no", z.getPassportNo());
		j.put("transactionId", request.getSession().getAttribute("transactionId"));
		return j;
	}
	
	/**
	 * 根据回收流水号查询订单信息
	 */
	public JSONObject getRecycleNews(String quoteId){
		JSONObject orderNews=new JSONObject();
		JSONObject requestNews=new JSONObject();
		//调用接口需要加密的数据
		JSONObject code=new JSONObject();
		code.put("quoteid", quoteId);
		try {
			String realCode=AES.Encrypt(code.toString());  //加密
			requestNews.put(SystemConstant.RECYCLE_REQUEST, realCode);
			//发起请求
			String getResult=AES.post(quoteUrl, requestNews);
			//对得到结果进行解密
			orderNews=RecycleController.getResult(AES.Decrypt(getResult));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orderNews;
	}

	/**
	 * 根据回收流水号查询订单信息(新回收接口)
	 */
	public JSONObject getNewUrlRecycleNews(String quoteId){
		JSONObject orderNews=new JSONObject();
		JSONObject requestNews=new JSONObject();
		//调用接口需要加密的数据
		JSONObject code=new JSONObject();
		code.put("quoteid", quoteId);
		try {
			String realCode=AES.Encrypt(code.toString());  //加密
			requestNews.put(SystemConstant.RECYCLE_REQUEST, realCode);
			//发起请求
			String getResult=AES.post(quoteNewUrl, requestNews);
			//对得到结果进行解密
			orderNews=RecycleController.getResult(AES.Decrypt(getResult));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orderNews;
	}
	
	public static void main(String[] args) throws Exception {
		
	}
}
