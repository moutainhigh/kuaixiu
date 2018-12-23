package com.kuaixiu.apiService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.common.exception.ApiServiceException;
import com.common.util.DateUtil;
import com.kuaixiu.coupon.entity.Coupon;
import com.kuaixiu.coupon.service.CouponService;
import com.kuaixiu.engineer.entity.Engineer;
import com.kuaixiu.engineer.service.EngineerService;
import com.kuaixiu.shop.entity.Shop;
import com.kuaixiu.shop.service.ShopService;
import com.kuaixiu.wechat.entity.WechatUser;
import com.kuaixiu.wechat.service.WechatUserService;
import com.system.api.ApiServiceInf;
import com.system.constant.ApiResultConstant;

/**
 * @author: anson
 * @CreateDate: 2018年1月9日 下午4:44:26
 * @version: V 1.0 查询优惠券信息
 */
@Service("queryCouponService")
public class QueryCouponService implements ApiServiceInf {

	private static final Logger log = Logger.getLogger(ScreenCouponService.class);

	@Autowired
	private CouponService couponService;
	@Autowired
	private EngineerService engService;
	@Autowired
	private WechatUserService wechatUserService;
	@Autowired
	private ShopService shopService;

	@Override
	public Object process(Map<String, String> params) {

		// 解析请求参数
		String paramJson = MapUtils.getString(params, "params");
		JSONObject pmJson = JSONObject.parseObject(paramJson);

		// 验证请求参数
		if (pmJson == null || !pmJson.containsKey("couponCode")) {
			throw new ApiServiceException(ApiResultConstant.resultCode_1001, ApiResultConstant.resultCode_str_1001);
		}
		// 获取优惠券号码
		String couponCode = pmJson.getString("couponCode");
		Coupon coupon = couponService.queryByCode(couponCode);
		if (coupon == null) {
			throw new ApiServiceException(ApiResultConstant.resultCode_1003, "亲，您输入的优惠券不存在。请确认优惠券是否正确。");
		}
		if (coupon.getType() != 2) {
			throw new ApiServiceException(ApiResultConstant.resultCode_1003, "优惠券类型不正确");
		}
		// 获取贴膜优惠券绑定的微信用户信息
		WechatUser u = new WechatUser();
		u.setScreenCouponCode(couponCode);
		List<WechatUser> wechatUser = wechatUserService.queryListForPage(u);
		if (wechatUser.isEmpty()) {
			throw new ApiServiceException(ApiResultConstant.resultCode_1003, "该用户券暂未领取");
		}
		JSONObject json = new JSONObject();
		// 返回优惠券信息json字符串
		json.put("coupon_code", coupon.getCouponCode());
		json.put("nickname", wechatUser.get(0).getNickname());
		json.put("begin_time", coupon.getBeginTime().replace("-", "."));
		json.put("end_time", coupon.getEndTime().replace("-", "."));
		json.put("coupon_price", coupon.getCouponPrice());
		json.put("coupon_name", coupon.getCouponName());
		json.put("is_use", coupon.getIsUse());
		if (coupon.getIsUse() == 1) {
			json.put("use_time", coupon.getUseTime());
			// 查询使用门店
			Engineer eng = engService.queryByEngineerNumber(coupon.getUpdateUserid());
			if (eng != null) {
				Shop shop = shopService.queryByCode(eng.getShopCode());
				if (shop != null) {
					json.put("shop_name", shop.getName());
				}
			}else{
				    json.put("shop_name", "无");
			}
		}
		// 判断是否过期
		String nowDay = DateUtil.getNowyyyyMMdd();
		if (nowDay.compareTo(coupon.getEndTime()) > 0) {
			json.put("is_overdue", 1);// 过期
		} else {
			json.put("is_overdue", 0);// 未过期
		}

		return json;
	}

}
