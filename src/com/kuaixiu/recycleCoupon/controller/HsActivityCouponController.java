package com.kuaixiu.recycleCoupon.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.paginate.Page;
import com.common.util.Base64Util;
import com.common.util.Consts;
import com.common.util.CookiesUtil;
import com.common.util.DateUtil;
import com.common.wechat.common.util.StringUtils;
import com.kuaixiu.recycle.entity.RecycleCoupon;
import com.kuaixiu.recycle.service.RecycleCouponService;
import com.kuaixiu.recycleCoupon.entity.HsActivityAndCoupon;
import com.kuaixiu.recycleCoupon.entity.HsActivityCoupon;
import com.kuaixiu.recycleCoupon.entity.HsUserActivityCoupon;
import com.kuaixiu.recycleCoupon.service.HsActivityAndCouponService;
import com.kuaixiu.recycleCoupon.service.HsActivityCouponService;
import com.kuaixiu.recycleCoupon.service.HsUserActivityCouponService;
import com.kuaixiu.recycleUser.entity.HsUser;
import com.kuaixiu.recycleUser.service.HsUserService;
import com.system.api.entity.ResultData;
import com.system.basic.user.entity.SessionUser;
import com.system.constant.SystemConstant;
import org.activiti.engine.identity.User;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * HsActivityCoupon Controller
 *
 * @CreateDate: 2019-05-30 上午11:26:38
 * @version: V 1.0
 */
@Controller
public class HsActivityCouponController extends BaseController {
    private static final Logger log = Logger.getLogger(HsActivityCouponController.class);

    @Autowired
    private HsActivityCouponService hsActivityCouponService;
    @Autowired
    private HsUserService hsUserService;
    @Autowired
    private RecycleCouponService recycleCouponService;
    @Autowired
    private HsUserActivityCouponService userActivityCouponService;
    @Autowired
    private HsActivityAndCouponService hsActivityAndCouponService;

    /**
     * 获取活动领取加价券首页信息
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping("/recycle/getCouponIndex")
    @ResponseBody
    public ResultData getCouponIndex(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        try {
            JSONObject params = getPrarms(request);
            Integer source = params.getInteger("fm");
            String activityLabel = params.getString("label");//活动标识
            if (source == null) {
                return getSjResult(result, null, false, "2", null, "来源不能为空");
            }
            HsActivityCoupon activityCoupon = hsActivityCouponService.getDao().queryBySourceActivityLabel(source,activityLabel);
            if (activityCoupon == null) {
                getSjResult(result, null, true, "0", null, "活动不存在");
            } else {
                JSONObject jsonObject = hsActivityCouponService.actCoupon2Json(activityCoupon);
                getSjResult(result, jsonObject, true, "0", null, "获取成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            getSjResult(result, null, false, "5", null, "系统异常请稍后再试");
        }
        return result;
    }

    /**
     * 用户登录
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/recycle/checkLogin")
    @ResponseBody
    public ResultData checkLogin(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        try {
            JSONObject params = getPrarms(request);
            String phone = params.getString("phone");
            String code = params.getString("checkCode");
            if (StringUtils.isBlank(phone) || StringUtils.isBlank(code)) {
                return getSjResult(result, null, false, "2", null, "参数为空");
            }
            if (!checkRandomCode(request, phone, code)) {
                return getSjResult(result, null, false, "3", null, "验证码错误");
            }
            String[] dname = request.getServerName().split("\\.");
            String phoneBase64 = Base64Util.getBase64(phone);
            CookiesUtil.setCookie(response, Consts.COOKIE_HS_PHONE, phoneBase64, CookiesUtil.prepare(dname), 999999999);
            hsActivityCouponService.saveUser(phone);
            // 儲存用戶手机号
            HttpSession session = request.getSession();
            session.setAttribute("mobile", phone);
            getSjResult(result, null, true, "0", null, "登录成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }

    @RequestMapping("/recycle/isLogin")
    @ResponseBody
    public ResultData isLogin(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        try {
            JSONObject jsonObject = new JSONObject();
            Cookie cookie = CookiesUtil.getCookieByName(request, Consts.COOKIE_HS_PHONE);
            if (cookie == null || StringUtils.isBlank(cookie.getValue())) {
                return getResult(result, null, false, "2", "请登录");
            }
            String cookiePhone = cookie.getValue();
            String phoneBase64 = URLDecoder.decode(cookiePhone, "UTF-8");
            String phone = Base64Util.getFromBase64(phoneBase64);
            jsonObject.put("phone", phone);
            getSjResult(result, jsonObject, true, "0", null, "已登录");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }

    /**
     * 获取回收用户信息
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/recycle/getUser")
    @ResponseBody
    public ResultData getUser(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        try {
            JSONObject params = getPrarms(request);
            String phone = params.getString("phone");
            if (StringUtils.isBlank(phone)) {
                return getSjResult(result, null, false, "2", null, "参数为空");
            }
            if (StringUtils.isBlank(phone)) {
                phone = hsActivityCouponService.checkCookiePhone(request);
                if (StringUtils.isBlank(phone)) {
                    return getSjResult(result, null, false, "5", null, "请登录");
                }
            }
            HsUser hsUser = hsUserService.getDao().queryByPhone(phone);
            if (hsUser == null) {
                hsUser = new HsUser();
                hsUser.setPhone(phone);
            }
            JSONObject jsonObject = hsActivityCouponService.hsUser2Object(hsUser);
            getSjResult(result, jsonObject, true, "0", null, "获取成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }

    /**
     * 获取加价券列表
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/recycle/getCouponList")
    @ResponseBody
    public ResultData getOrderList(HttpServletRequest request,
                                   HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        try {
            JSONObject params = getPrarms(request);
            Integer state = params.getInteger("state");
            String phone = params.getString("phone");
            Integer pageIndex = params.getInteger("pageIndex");
            Integer pageSize = params.getInteger("pageSize");

            if (StringUtils.isBlank(phone)) {
                return getSjResult(result, null, false, "2", null, "手机号不能为空");
            }
            RecycleCoupon recycleCoupon = new RecycleCoupon();
            Page page = new Page();
            //将值转化为绝对值
            pageIndex = Math.abs(pageIndex);
            pageSize = Math.abs(pageSize);
            page.setPageSize(pageSize);
            page.setCurrentPage(pageIndex);
            recycleCoupon.setPage(page);
            recycleCoupon.setReceiveMobile(phone);
            if (state == 1) {
                recycleCoupon.setIsUse(0);
            } else if (state == 2) {
                recycleCoupon.setIsUse(1);
            } else if (state == 3) {
                recycleCoupon.setEndTime("-");
            }
            List<RecycleCoupon> recycleCoupons = recycleCouponService.getDao().queryCouponListForPage(recycleCoupon);
            JSONObject jsonObject = hsActivityCouponService.recycleCoupons2Json(recycleCoupons, page);
            getSjResult(result, jsonObject, true, "0", null, "查询成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }

    /**
     * 获取已领用加价券列表
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/recycle/getAllUserCouponList")
    @ResponseBody
    public ResultData getIsUserCouponList(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        try {
            JSONObject params = getPrarms(request);
            String mobile = params.getString("mobile");

            RecycleCoupon recycleCoupon1 = new RecycleCoupon();
            recycleCoupon1.setStatus(1);
            recycleCoupon1.setIsDel(0);
            recycleCoupon1.setIsUse(0);
            recycleCoupon1.setReceiveMobile(mobile);

            JSONArray array = new JSONArray();

            //查询所有可使用加价券
            List<RecycleCoupon> recycleCoupons = recycleCouponService.queryUnReceive(recycleCoupon1);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            for (RecycleCoupon recycleCoupon : recycleCoupons) {
                if (sdf.parse(recycleCoupon.getEndTime()).getTime() - new Date().getTime() < 0) {
                    if (recycleCoupon.getStatus() == 1) {
                        recycleCouponService.updateStatusByCouponCode(recycleCoupon.getCouponCode());
                    }
                    continue;
                }
                JSONObject jsonObject = hsActivityCouponService.recycleCouponDetail2Json(recycleCoupon);
                array.add(jsonObject);
            }
            jsonResult.put("Coupons", array);
            getSjResult(result, jsonResult, true, "0", null, "查询成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/recycle/getHsCouponDetail")
    @ResponseBody
    public ResultData getHsCouponDetail(HttpServletRequest request,
                                        HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        try {
            JSONObject params = getPrarms(request);
            String couponId = params.getString("couponId");

            if (StringUtils.isBlank(couponId)) {
                return getSjResult(result, null, false, "2", null, "参数为空");
            }
            RecycleCoupon recycleCoupon = recycleCouponService.queryById(couponId);
            if (recycleCoupon == null) {
                return getSjResult(result, null, false, "2", null, "id错误");
            }
            JSONObject jsonObject = hsActivityCouponService.recycleCouponDetail2Json(recycleCoupon);
            getSjResult(result, jsonObject, true, "0", null, "查询成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }

    /**
     * 用户活动领取加价券
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/recycle/receiveActivityCoupon")
    @ResponseBody
    public ResultData receiveActivityCoupon(HttpServletRequest request,
                                            HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        try {
            JSONObject params = getPrarms(request);
            Integer source = params.getInteger("fm");
            String activityLabel = params.getString("activityLabel");
            String phone = params.getString("phone");
            if (source == null || StringUtils.isBlank(activityLabel) || StringUtils.isBlank(phone)) {
                return getSjResult(result, null, false, "2", null, "参数为空");
            }
            HsActivityCoupon activityCoupon = hsActivityCouponService.getDao().queryBySourceActivityLabel(source, activityLabel);
            if (activityCoupon == null) {
                return getSjResult(result, null, false, "1", null, "活动标识错误");
            }
            SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
            if(sdf.parse(activityCoupon.getEndTime()).getTime()<DateUtil.getNow().getTime()){
                return getSjResult(result, null, false, "3", null, "活动已过期");
            }
            HsUserActivityCoupon userActivityCoupon = new HsUserActivityCoupon();
            userActivityCoupon.setAcvityId(activityCoupon.getId());
            userActivityCoupon.setSource(source);
            userActivityCoupon.setUserPhone(phone);
            List<HsUserActivityCoupon> userActivityCoupons = userActivityCouponService.queryList(userActivityCoupon);
            if (CollectionUtils.isNotEmpty(userActivityCoupons)) {
                return getSjResult(result, null, false, "3", null, "您已领取过此加价券");
            }
            List<HsActivityAndCoupon> activityAndCoupons=hsActivityAndCouponService.getDao().queryByActivityId(activityCoupon.getId());
            if (CollectionUtils.isEmpty(activityAndCoupons)) {
                return getSjResult(result, null, false, "3", null, "该活动没有加价券");
            }
            hsActivityCouponService.receiveCoupon(activityAndCoupons, phone);
            HsUserActivityCoupon userActivityCoupon1 = new HsUserActivityCoupon();
            userActivityCoupon1.setAcvityId(activityCoupon.getId());
            userActivityCoupon1.setSource(source);
            userActivityCoupon1.setUserPhone(phone);
            userActivityCoupon1.setId(UUID.randomUUID().toString().replace("-", ""));
            userActivityCouponService.add(userActivityCoupon1);
            getSjResult(result, null, true, "0", null, "领取成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }

    /**
     * 退出登录
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping("/recycle/loginOut")
    @ResponseBody
    public ResultData loginOut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResultData result = new ResultData();
        try {
            JSONObject params = getPrarms(request);
            String phone = params.getString("phone");
            if (StringUtils.isBlank(phone)) {
                return getSjResult(result, null, false, "2", null, "参数为空");
            }
            String[] dname = request.getServerName().split("\\.");
            CookiesUtil.setCookie(response, Consts.COOKIE_HS_PHONE, null, CookiesUtil.prepare(dname), 0);

            getSjResult(result, null, true, "0", null, "操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            getSjResult(result, null, false, "1", null, "操作失败");
        }
        return result;
    }
}
