package com.kuaixiu.recycleCoupon.service;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.service.BaseService;
import com.common.paginate.Page;
import com.common.util.Base64Util;
import com.common.util.Consts;
import com.common.util.CookiesUtil;
import com.common.util.DateUtil;
import com.kuaixiu.recycle.entity.RecycleCoupon;
import com.kuaixiu.recycle.service.RecycleCouponService;
import com.kuaixiu.recycle.service.RecycleOrderService;
import com.kuaixiu.recycleCoupon.dao.HsActivityCouponMapper;
import com.kuaixiu.recycleCoupon.entity.HsActivityAndCoupon;
import com.kuaixiu.recycleCoupon.entity.HsActivityCoupon;
import com.kuaixiu.recycleCoupon.entity.HsActivityCouponRole;
import com.kuaixiu.recycleUser.entity.HsUser;
import com.kuaixiu.recycleUser.service.HsUserService;
import com.system.constant.SystemConstant;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

/**
 * HsActivityCoupon Service
 *
 * @CreateDate: 2019-05-30 上午11:26:38
 * @version: V 1.0
 */
@Service("hsActivityCouponService")
public class HsActivityCouponService extends BaseService<HsActivityCoupon> {
    private static final Logger log = Logger.getLogger(HsActivityCouponService.class);

    @Autowired
    private HsActivityCouponMapper<HsActivityCoupon> mapper;
    @Autowired
    private HsUserService userService;
    @Autowired
    private RecycleOrderService recycleOrderService;
    @Autowired
    private RecycleCouponService recycleCouponService;
    @Autowired
    private HsActivityAndCouponService hsActivityAndCouponService;
    @Autowired
    private HsActivityCouponRoleService activityCouponRoleService;

    public HsActivityCouponMapper<HsActivityCoupon> getDao() {
        return mapper;
    }

    //**********自定义方法***********

    public JSONObject actCoupon2Json(HsActivityCoupon actCoupon) throws Exception {
        JSONObject json = new JSONObject();
        json.put("activityLabel", actCoupon.getActivityLabel());
        JSONObject headImage = new JSONObject();
        headImage.put("headUrl", actCoupon.getHeadUrl());
//        headImage.put("headHeight", actCoupon.getHeadHeight());
//        headImage.put("headWide", actCoupon.getHeadWide());
        json.put("headImage", headImage);
//        JSONObject margin = new JSONObject();
//        margin.put("marginHeight", actCoupon.getMarginHeight());
//        margin.put("marginWide", actCoupon.getMarginWide());
//        json.put("margin", margin);
        JSONObject centerImage = new JSONObject();
        centerImage.put("centerUrl", actCoupon.getCenterUrl());
        centerImage.put("centercolorValue", actCoupon.getCentercolorValue());
//        centerImage.put("centerHeight", actCoupon.getCenterHeight());
//        centerImage.put("centerWide", actCoupon.getCenterWide());
        json.put("centerImage", centerImage);
        json.put("endTime", actCoupon.getEndTime());
        if (actCoupon.getActivityRole().contains("|")) {
            String[] roles = actCoupon.getActivityRole().split("\\|");
            JSONArray activityRole = new JSONArray();
            for (int i = 0; i < roles.length; i++) {
                JSONObject role = new JSONObject();
                role.put("role", roles[i]);
                activityRole.add(role);
            }
            json.put("activityRole", activityRole);
        }else{
            JSONArray activityRole = new JSONArray();
            JSONObject role = new JSONObject();
            role.put("role", actCoupon.getActivityRole());
            json.put("activityRole", activityRole);
        }
        return json;
    }


    public void saveUser(String phone) throws Exception {
        HsUser user = userService.getDao().queryByPhone(phone);
        if (user == null) {
            HsUser hsUser = new HsUser();
            hsUser.setId(UUID.randomUUID().toString().replace("-", ""));
            hsUser.setPhone(phone);
            userService.add(hsUser);
        }
    }

    public String checkCookiePhone(HttpServletRequest request) throws Exception {
        //如果session为空，则取Cookie验证
        Cookie cookie = CookiesUtil.getCookieByName(request, Consts.COOKIE_HS_PHONE);
        if (cookie == null || StringUtils.isBlank(cookie.getValue())) {
            return null;
        }
        //Cookie不过期
        String phoneBase64 = cookie.getValue();
        try {
            phoneBase64 = URLDecoder.decode(phoneBase64, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //1.先base64解密成拼接字符串
        return Base64Util.getFromBase64(phoneBase64);
    }

    public JSONObject hsUser2Object(HsUser user) {
        JSONObject json = new JSONObject();
        if (StringUtils.isNotBlank(user.getImageUrl())) {
            json.put("headPortrait", user.getImageUrl());
        }
        if (StringUtils.isNotBlank(user.getName())) {
            json.put("name", user.getName());
        }
        if (StringUtils.isNotBlank(user.getPhone())) {
            json.put("phone", user.getPhone());
        }
        Integer orderNum = recycleOrderService.getDao().queryCountByMobile(user.getPhone());
        json.put("orderNum", orderNum);
        RecycleCoupon recycleCoupon = new RecycleCoupon();
        recycleCoupon.setReceiveMobile(user.getPhone());
        recycleCoupon.setIsDel(0);
        Integer couponNum = recycleCouponService.getDao().queryCount(recycleCoupon);
        json.put("couponNum", couponNum);
        json.put("otherCouponNum", 0);
        return json;
    }

    public JSONObject recycleCoupons2Json(List<RecycleCoupon> recycleCoupons, Page page) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pageSize", page.getPageSize());
        jsonObject.put("pageIndex", page.getCurrentPage());
        jsonObject.put("recordsTotal", page.getRecordsTotal());
        jsonObject.put("totalPage", page.getTotalPage());
        JSONArray jsonArray = new JSONArray();
        for (RecycleCoupon recycleCoupon : recycleCoupons) {
            JSONObject json = new JSONObject();
            json.put("couponId", recycleCoupon.getId());
            json.put("couponCode", recycleCoupon.getCouponCode());
            json.put("couponName", recycleCoupon.getCouponName());
            json.put("upperLimit", recycleCoupon.getUpperLimit());
            json.put("subtractionPrice", recycleCoupon.getSubtraction_price());
            if (recycleCoupon.getPricingType() == 1) {
                json.put("couponPrice", recycleCoupon.getStrCouponPrice().setScale(0, BigDecimal.ROUND_HALF_UP) + "%");
            } else {
                json.put("couponPrice", recycleCoupon.getStrCouponPrice());
            }
            json.put("beginTime", recycleCoupon.getBeginTime());
            json.put("endTime", recycleCoupon.getEndTime());
            json.put("ruleDescription", recycleCoupon.getRuleDescription());
            jsonArray.add(json);
        }
        jsonObject.put("Coupons", jsonArray);
        return jsonObject;
    }

    public JSONObject recycleCouponDetail2Json(RecycleCoupon recycleCoupon) {
        JSONObject json = new JSONObject();
        json.put("batchId", recycleCoupon.getBatchId());
        json.put("couponId", recycleCoupon.getId());
        json.put("couponCode", recycleCoupon.getCouponCode());
        json.put("couponName", recycleCoupon.getCouponName());
        json.put("couponType", recycleCoupon.getPricingType());
        json.put("upperLimit", recycleCoupon.getUpperLimit());
        json.put("addPriceUpper", recycleCoupon.getAddPriceUpper());
        json.put("subtractionPrice", recycleCoupon.getSubtraction_price());
        json.put("couponPrice", recycleCoupon.getStrCouponPrice());
        json.put("beginTime", recycleCoupon.getBeginTime());
        json.put("endTime", recycleCoupon.getEndTime());
        json.put("ruleDescription", recycleCoupon.getRuleDescription());
        json.put("isUser", recycleCoupon.getIsUse());
        if (recycleCoupon.getUseTime() != null) {
            json.put("userTime", DateUtil.getDateyyyyMMddHHmmss(recycleCoupon.getUseTime()));
        }
        json.put("note", recycleCoupon.getNote());
        return json;
    }


    public void receiveCoupon(List<HsActivityAndCoupon> activityAndCoupons, String mobile) throws Exception {
        List<HsActivityCouponRole> activityCouponRoles = new ArrayList<>();
        for(HsActivityAndCoupon activityAndCoupon:activityAndCoupons){
            HsActivityCouponRole activityCouponRole = activityCouponRoleService.queryById(activityAndCoupon.getCouponId());
            activityCouponRoles.add(activityCouponRole);
        }
        for (HsActivityCouponRole activityCouponRole : activityCouponRoles) {
            RecycleCoupon recycleCoupon = new RecycleCoupon();
            recycleCoupon.setBatchId(SystemConstant.RECYCLE_COUPON_BATCH);
            recycleCoupon.setCreateUserid("admin");
            recycleCoupon.setUpdateUserid("admin");
            recycleCoupon.setId(UUID.randomUUID().toString().replace("-", "").substring(0, 16));
            recycleCoupon.setCouponName(activityCouponRole.getCouponName());
            recycleCoupon.setPricingType(activityCouponRole.getPricingType());
            recycleCoupon.setRuleDescription(activityCouponRole.getRuleDescription());
            recycleCoupon.setUpperLimit(activityCouponRole.getUpperLimit());
            recycleCoupon.setSubtraction_price(activityCouponRole.getSubtractionPrice());
            recycleCoupon.setStrCouponPrice(activityCouponRole.getCouponPrice());
            recycleCoupon.setBeginTime(DateUtil.getNowyyyyMMdd());
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 7);
            recycleCoupon.setEndTime(DateUtil.getDateYYYYMMDD(calendar.getTime()));
            recycleCoupon.setReceiveMobile(mobile);
            recycleCoupon.setAddPriceUpper(activityCouponRole.getAddPriceUpper());
            recycleCoupon.setIsDel(0);
            recycleCoupon.setIsUse(0);
            recycleCoupon.setIsReceive(1);
            recycleCoupon.setStatus(1);
            recycleCoupon.setNote(activityCouponRole.getNote());
            recycleCoupon.setCouponCode(recycleCouponService.createNewCode());
            recycleCouponService.add(recycleCoupon);
        }
    }


    public void activityAndCoupon(String activityId, String[] couponRoles, String endTime) {
        for (int i = 0; i < couponRoles.length; i++) {
            HsActivityAndCoupon hsActivityAndCoupon = new HsActivityAndCoupon();
            hsActivityAndCoupon.setId(UUID.randomUUID().toString().replace("-", ""));
            hsActivityAndCoupon.setActivityId(activityId);
            hsActivityAndCoupon.setEndTime(endTime);
            hsActivityAndCoupon.setCouponId(couponRoles[i]);
            hsActivityAndCouponService.add(hsActivityAndCoupon);
        }
    }
}