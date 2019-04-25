package com.kuaixiu.recycle.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.service.BaseService;
import com.common.exception.SystemException;
import com.common.util.AES;
import com.common.wechat.common.util.StringUtils;
import com.kuaixiu.recycle.dao.RecycleOrderMapper;
import com.kuaixiu.recycle.entity.CouponAddValue;
import com.kuaixiu.recycle.entity.PushsfException;
import com.kuaixiu.recycle.entity.RecycleCoupon;
import com.kuaixiu.recycle.entity.RecycleOrder;
import com.system.basic.address.entity.Address;
import com.system.basic.address.service.AddressService;
import com.system.basic.user.entity.SessionUser;
import com.system.constant.SystemConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

/**
* @author: anson
* @CreateDate: 2017年11月17日 上午10:18:53
* @version: V 1.0
* 
*/
@Service("recycleOrderService")
public class RecycleOrderService extends BaseService<RecycleOrder>{

	@Autowired
	private RecycleOrderMapper<RecycleOrder> mapper;
	@Autowired
	private RecycleCouponService recycleCouponService;
	@Autowired
	private CouponAddValueService couponAddValueService;
	@Autowired
	private AddressService addressService;
	@Autowired
	private PushsfExceptionService pushsfExceptionService;

	private static final String baseNewUrl = SystemConstant.RECYCLE_NEW_URL;
	private static final String cipherdata = SystemConstant.RECYCLE_REQUEST;


	@Override
	public RecycleOrderMapper<RecycleOrder> getDao() {

		return mapper;
	}
	
	/**
	 * 根据回收流水号获取订单信息
	 * @param code
	 * @return
	 */
	public RecycleOrder queryByQuoteId(String code){
        return getDao().queryByQuoteId(code);
    }
	
	public RecycleOrder queryByOrderState(RecycleOrder recycleOrder){
		return getDao().queryByOrderState(recycleOrder);
	}

	/**
	 * 根据orderNo改订单状态
	 * @param recycleOrder
	 * @return
	 */
	public int updateByOrderStatus(RecycleOrder recycleOrder){
		return getDao().updateByOrderStatus(recycleOrder);
	}

	public int deleteCouponIdByOrderStatus(String orderNo){
		return getDao().deleteCouponIdByOrderStatus(orderNo);
	}

	/**
	 * 根据回收订单号获取订单信息
	 * @param code
	 * @return
	 */
	public RecycleOrder queryByOrderNo(String code){
        return getDao().queryByOrderNo(code);
    }

	
	/**
	 * 根据手机号查询该用户是否有正在进行信用回收的订单
	 * 芝麻订单状态值不为0(未反馈) 和2(结束)
	 */
	 public List<RecycleOrder> queryByMobile(RecycleOrder t) {
	        return getDao().queryByMobile(t);
	    }


	/**
	 * 根据抬价订单号查询
	 * @param code
	 * @return
	 */
	public RecycleOrder queryByIncreaseOrderNo(String code){
		return getDao().queryByIncreaseOrderNo(code);
	}



	//确定来源，没有则默认微信公众号来源
	public String isHaveSource(RecycleOrder order, String source) {
		if (!source.equals("null") && StringUtils.isNotBlank(source)) {
			String sourceType = source;
			if (source.contains("?")) {
				sourceType = org.apache.commons.lang3.StringUtils.substringBefore(source, "?");
			}
			order.setSourceType(Integer.parseInt(sourceType));
		} else {
			source = "1";
			order.setSourceType(Integer.valueOf(source));        //订单来源  默认微信公众号来源
		}
		return source;
	}


	//判断该订单来源确定是否使用加价券
	public RecycleCoupon getCouponCode(String mobile,HttpServletRequest request){
		CouponAddValue addValue = couponAddValueService.getDao().queryByType(1);
		RecycleCoupon recycleCoupon = new RecycleCoupon();
		if (new Date().getTime() < addValue.getActivityEndTime().getTime()) {
			SessionUser su = (SessionUser) request.getSession().getAttribute(SystemConstant.SESSION_USER_KEY);
			recycleCoupon = this.addCoupon(addValue, su, mobile);
		}
		return recycleCoupon;
	}

	//创建加价券
	private RecycleCoupon addCoupon(CouponAddValue addValue, SessionUser su, String mobile) {
		RecycleCoupon recycleCoupon = new RecycleCoupon();
		recycleCoupon.setBatchId(addValue.getBatchId());
		if(su != null){
			recycleCoupon.setCreateUserid(su.getUserId());
			recycleCoupon.setUpdateUserid(su.getUserId());
		}
		recycleCoupon.setId(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16));
		recycleCoupon.setCouponName(addValue.getCouponName());
		recycleCoupon.setPricingType(addValue.getPricingType());
		recycleCoupon.setRuleDescription(addValue.getRuleDescription());
		recycleCoupon.setUpperLimit(addValue.getUpperLimit());
		recycleCoupon.setSubtraction_price(addValue.getSubtractionPrice());
		recycleCoupon.setStrCouponPrice(addValue.getCouponPrice());
		recycleCoupon.setBeginTime(addValue.getBeginTime());
		recycleCoupon.setEndTime(addValue.getEndTime());
		recycleCoupon.setReceiveMobile(mobile);
		recycleCoupon.setIsDel(0);
		recycleCoupon.setIsUse(0);
		recycleCoupon.setIsReceive(1);
		recycleCoupon.setStatus(1);
		recycleCoupon.setNote(addValue.getNote());
		recycleCoupon.setCouponCode(recycleCouponService.createNewCode());
		recycleCouponService.add(recycleCoupon);
		return recycleCoupon;
	}


	//转化地址
	public String getAreaname(String province,String city,String area){
		Address provinceName = addressService.queryByAreaId(province);
		Address cityName = addressService.queryByAreaId(city);
		Address areaName = addressService.queryByAreaId(area);
		if (provinceName == null || cityName == null || areaName == null) {
			throw new SystemException("请确认地址信息是否无误");
		}
		return getProvince(provinceName.getArea()) + cityName.getArea() + " " + areaName.getArea();
	}

	//发送给回收平台加价券
	public void sendRecycleCoupon(JSONObject code,RecycleCoupon recycleCoupon){
		JSONArray jsonArray = new JSONArray();
		JSONObject json = new JSONObject();
		json.put("couponId", recycleCoupon.getCouponCode());
		json.put("actType", recycleCoupon.getPricingType());
		if (1 == (recycleCoupon.getPricingType())) {
			json.put("percent", recycleCoupon.getStrCouponPrice().divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_DOWN));
			json.put("addFee", 0);
		} else if (2 == (recycleCoupon.getPricingType())) {
			json.put("percent", 0);
			json.put("addFee", recycleCoupon.getStrCouponPrice().intValue());
		}
		json.put("up", new BigDecimal("2000"));
		json.put("low", new BigDecimal("10"));
		json.put("desc", recycleCoupon.getRuleDescription());
		jsonArray.add(json);
		JSONObject json2 = new JSONObject();
		json2.put("couponId", recycleCoupon.getCouponCode());
		json2.put("actType", recycleCoupon.getPricingType());
		if (1 == (recycleCoupon.getPricingType())) {
			json2.put("percent", recycleCoupon.getStrCouponPrice().divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_DOWN));
			json2.put("addFee", 0);
		} else if (2 == (recycleCoupon.getPricingType())) {
			json2.put("percent", 0);
			json2.put("addFee", recycleCoupon.getStrCouponPrice().intValue());
		}
		json2.put("up", new BigDecimal("10000"));
		json2.put("low", new BigDecimal("2000"));
		json2.put("desc", recycleCoupon.getRuleDescription());
		jsonArray.add(json2);
		code.put("coupon_rule", jsonArray.toJSONString());
	}


	//订单提交成功 当用户选择超人系统推送时  调用顺丰取件接口
	public void getPostSF(RecycleOrder order, Long time, HttpServletRequest request){
		try {
			String mailNo = postSfOrder(order);
			order.setSfOrderNo(mailNo);
			order.setOrderStatus(2);
//			this.saveUpdate(order);
//			log.info("顺丰推送成功");
		} catch (Exception e) {
			//记录顺丰推送异常信息
			PushsfException exception = new PushsfException();
			exception.setShNo(UUID.randomUUID().toString().replace("-", ""));
			exception.setOrderNo(order.getOrderNo());
			exception.setShExceptin(e.getMessage());
			pushsfExceptionService.add(exception);
			request.getSession().setAttribute("newTimes", time);
			throw new SystemException("推送顺丰快递失败");
		}
	}

	/**
	 * 发起订单物流请求
	 */
	private String postSfOrder(RecycleOrder order) throws Exception {
		String mailNo = null;
		String sfUrl = baseNewUrl + "pushsforder";
		JSONObject requestNews = new JSONObject();
		JSONObject code = new JSONObject();
		code.put("orderid", order.getOrderNo());
		code.put("sendtime", order.getTakeTime());
		String realCode = AES.Encrypt(code.toString());  //加密
		requestNews.put(cipherdata, realCode);
		//发起请求
		String getResult = AES.post(sfUrl, requestNews);
		//对得到结果进行解密  //得到运单号
		JSONObject jsonResult = getResult(AES.Decrypt(getResult));
		JSONObject j = (JSONObject) jsonResult.get("datainfo");
		if (j != null) {
			mailNo = j.getString("mailno");
		} else {
			throw new SystemException("参数值不正确");
		}
		return mailNo;
	}

	/**
	 * 返回结果解析
	 */
	public static JSONObject getResult(String originalString) {
		JSONObject result = (JSONObject) JSONObject.parse(originalString);
		if (result.getString("result") != null && !result.getString("result").equals("RESPONSESUCCESS")) {
			throw new SystemException(result.getString("msg"));
		}
		return result;
	}

	/**
	 * 超人系统地址为 上海市 黄浦区 城区    -----  浙江 杭州市 江干区
	 * 回收地址规范     上海市 黄浦区            ------ 浙江省 杭州市 江干区
	 * 省份区别  西藏 宁夏 新疆 广西 内蒙古
	 * 北京市  天津市 上海市  重庆市 其他省后面都加省
	 *
	 * @param code
	 * @return 超人地址转化回收地址规范
	 */
	public static String getProvince(String code) {
		List<String> s = new ArrayList<String>();
		List<String> p = new ArrayList<String>();
		String[] plist = {"西藏", "宁夏", "新疆", "广西", "内蒙古"};
		String[] slist = {"北京", "天津", "上海", "重庆"};
		s.addAll(Arrays.asList(plist));
		p.addAll(Arrays.asList(slist));
		if (s.contains(code)) {
			//不用更改
		} else if (p.contains(code)) {
			code += "市";
		} else {
			code += "省";
		}
		return code;
	}

	/**
	 * 通过quoteid查询 订单信息
	 */
	public JSONObject postNews(String quoteId) throws Exception {
		JSONObject requestNews = new JSONObject();
		//调用接口需要加密的数据
		String url = baseNewUrl + "getquotedetail";
		JSONObject code = new JSONObject();
		code.put("quoteid", quoteId);
		String realCode = AES.Encrypt(code.toString());  //加密
		requestNews.put(cipherdata, realCode);
		//发起请求
		String getResult = AES.post(url, requestNews);
		//对得到结果进行解密
		JSONObject jsonResult = getResult(AES.Decrypt(getResult));
		JSONObject j = (JSONObject) jsonResult.get("datainfo");
		return j;
	}

	/**
	 * 更新字符串格式
	 */
	public String getDate(String time) {
		String realTime = "";
		try {
			Calendar c = Calendar.getInstance();
			c.setTime(new Date());
			String month = c.get(Calendar.YEAR) + "/" + (time.substring(0, time.indexOf("月"))) + "/"
					+ (time.substring((time.indexOf("月") + 1), time.indexOf("日")));
			String hour = time.substring((time.lastIndexOf(" ") + 1), time.indexOf(":"));
			realTime = month + " " + hour + ":30:00";
		} catch (Exception e) {
			realTime = time;
		}
		return realTime;
	}
}
