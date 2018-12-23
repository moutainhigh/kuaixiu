package com.kuaixiu.apiService;

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
import com.system.api.ApiServiceInf;
import com.system.basic.user.entity.SessionUser;
import com.system.constant.ApiResultConstant;

/**
* @author: anson
* @CreateDate: 2018年1月4日 下午4:13:29
* @version: V 1.0
* 验证,使用贴膜优惠券
*/
@Service("screenCouponService")
public class ScreenCouponService implements ApiServiceInf{

	private static final Logger log= Logger.getLogger(ScreenCouponService.class);
	 
	@Autowired
	private CouponService couponService;
	@Autowired
	private EngineerService engService;
	
	@Override
	public Object process(Map<String, String> params) {
	
		//解析请求参数
        String paramJson = MapUtils.getString(params, "params");
        JSONObject pmJson = JSONObject.parseObject(paramJson);
		
        //验证请求参数
        if (pmJson == null 
                || !pmJson.containsKey("couponCode")) {
            throw new ApiServiceException(ApiResultConstant.resultCode_1001, ApiResultConstant.resultCode_str_1001);
        }
		//获取优惠券号码
        String couponCode=pmJson.getString("couponCode");
        Coupon coupon=couponService.queryByCode(couponCode);
        if(coupon==null){
        	 throw new ApiServiceException(ApiResultConstant.resultCode_1003, "亲，您输入的优惠券不存在。请确认优惠券是否正确。");
        }
        if(coupon.getType()!=2){
        	 throw new ApiServiceException(ApiResultConstant.resultCode_1003, "优惠券类型不正确");
        }
        if(coupon.getIsUse()!=0){
        	 throw new ApiServiceException(ApiResultConstant.resultCode_1003, "亲，您输入的优惠券已使用。请确认优惠券是否正确。");
        }
        String nowDay = DateUtil.getNowyyyyMMdd();
//		if(nowDay.compareTo(coupon.getBeginTime()) < 0){
//			throw new ApiServiceException(ApiResultConstant.resultCode_1003, "亲，您输入的优惠券还未生效，无法使用。");
//		}
		if(nowDay.compareTo(coupon.getEndTime()) > 0){
			throw new ApiServiceException(ApiResultConstant.resultCode_1003, "亲，您输入的优惠券已超过有效期，无法使用。");
		}
		 //满足使用条件 则修改优惠券为已使用状态
		 //获取工程师工号
         String number = MapUtils.getString(params, "pmClientId");
         if(StringUtils.isNotBlank(number)){
        	 Engineer eng = engService.queryByEngineerNumber(number);
        	 coupon.setUpdateUserid(eng.getNumber());
         }
         couponService.updateForUse(coupon);
		return "OK";
	}

}
