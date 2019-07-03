package com.kuaixiu.recycle.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.exception.SystemException;
import com.common.paginate.Page;
import com.common.util.HttpClientUtil;
import com.common.util.MD5Util;
import com.common.util.SmsSendUtil;
import com.google.common.collect.Maps;
import com.kuaixiu.recycle.entity.RecycleCoupon;
import com.kuaixiu.recycle.entity.RecycleOrder;
import com.kuaixiu.recycle.entity.WechatTemplate;
import com.kuaixiu.recycle.service.RecycleCouponService;
import com.kuaixiu.recycle.service.RecycleOrderService;
import com.system.api.entity.ResultData;
import com.system.basic.sequence.util.SeqUtil;
import com.system.basic.user.entity.SessionUser;
import com.system.basic.user.service.SessionUserService;
import com.system.constant.ApiResultConstant;
import com.system.constant.SystemConstant;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 回收加价券
 *
 * @author najy
 *         Created by Administrator on 2018/9/13/013.
 */

@Controller
public class RecycleCouponController extends BaseController {

    private static final Logger log = Logger.getLogger(RecycleCouponController.class);

    @Autowired
    private SessionUserService sessionUserService;
    @Autowired
    private RecycleCouponService recycleCouponService;
    @Autowired
    private RecycleOrderService recycleOrderService;
    //回收订单状态变化时调用，更改状态  参数签名
    private static final String autograph = "HZYNKJ@SUPER2017";


    /**
     * 回收加价券列表
     */
    @RequestMapping(value = "/recycle/couponList")
    public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String returnView = "recycle/recycleCouponList";
        return new ModelAndView(returnView);
    }

    /**
     * create
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/recycle/createCoupon")
    public ModelAndView create(HttpServletRequest request,
                               HttpServletResponse response) throws Exception {

        //获取登录用户
        SessionUser su = getCurrentUser(request);
        String returnView = "recycle/recycleCreateCoupon";
        return new ModelAndView(returnView);
    }


    /**
     * 创建加价券
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "recycle/addCoupon")
    @ResponseBody
    public void addCoupon(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> resultMap = Maps.newHashMap();
        try {
            SessionUser su = getCurrentUser(request);
            if (su.getType() != SystemConstant.USER_TYPE_SYSTEM && su.getType() != SystemConstant.USER_TYPE_SUPPER) {
                throw new SystemException("对不起，您没有操作权限!");
            }
            String name = request.getParameter("name");//优惠券名称
            String pricingType = request.getParameter("pricingType");//加价类型 1：百分比 2:：固定加价
            String subtractionPrice = request.getParameter("subtractionPrice");//满减金额下限额度
            String upperLimit = request.getParameter("upperLimit");//订单金额上限额度
            String addPriceUpper=request.getParameter("addPriceUpper");//加价金额上限
            String price = request.getParameter("price");//优惠券金额
            String description = request.getParameter("ruleDescription");//加价规则描述
            String startTime = request.getParameter("startTime");//优惠券开始时间
            String endTime = request.getParameter("endTime");//优惠券过期时间
            String number = request.getParameter("number");//优惠券数量
            String note = request.getParameter("note");//优惠券备注
            if (StringUtils.isBlank(name) || StringUtils.isBlank(price) || StringUtils.isBlank(startTime)
                    || StringUtils.isBlank(endTime) || StringUtils.isBlank(number) || StringUtils.isBlank(pricingType)
                    || StringUtils.isBlank(subtractionPrice) || StringUtils.isBlank(description)) {
                throw new SystemException("参数不完整");
            }
            if (1 == Integer.valueOf(pricingType)) {
                if (price.contains("%")) {
                    throw new SystemException("类型为百分比无需加%");
                }
            }
            if (Integer.valueOf(number) > 1000) {
                throw new SystemException("最大支持一次生成1000张加价券");
            }

            RecycleCoupon recycleCoupon = new RecycleCoupon();
            recycleCoupon.setBatchId(SeqUtil.getNext("A"));
            recycleCoupon.setCreateUserid(su.getUserId());
            for (int i = 0; i < Integer.valueOf(number); i++) {
                //获取登录用户
                recycleCoupon.setCreateUserid(su.getUserId());
                recycleCoupon.setId(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16));
                recycleCoupon.setCouponName(name);
                recycleCoupon.setPricingType(Integer.valueOf(pricingType));
                recycleCoupon.setRuleDescription(description);
                recycleCoupon.setUpperLimit(new BigDecimal(upperLimit));
                recycleCoupon.setAddPriceUpper(new BigDecimal(addPriceUpper));
                recycleCoupon.setSubtraction_price(new BigDecimal(subtractionPrice));
                recycleCoupon.setStrCouponPrice(new BigDecimal(price));
                recycleCoupon.setBeginTime(startTime);
                recycleCoupon.setEndTime(endTime);
                recycleCoupon.setNote(note);
                recycleCoupon.setCouponCode(recycleCouponService.createNewCode());
                recycleCouponService.add(recycleCoupon);
            }

            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
            resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
            resultMap.put("batchId", recycleCoupon.getBatchId());
            resultMap.put("count", number);

        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
            resultMap.put(RESULTMAP_KEY_MSG, "保存失败");
        }
        renderJson(response, resultMap);
    }

    /**
     * 刷新数量
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/recycle/reflashCount")
    public void reflashCount(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {
        SessionUser su = getCurrentUser(request);

        Map<String, Object> resultMap = Maps.newHashMap();
        //批次ID
        String batchId = request.getParameter("batchId");

        try {
            RecycleCoupon t = new RecycleCoupon();
            t.setBatchId(batchId);
            int count = recycleCouponService.queryCount(t);
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
            resultMap.put(RESULTMAP_KEY_MSG, "加载成功");
            resultMap.put("count", count);
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
            resultMap.put(RESULTMAP_KEY_MSG, "加载失败");
        }
        renderJson(response, resultMap);
    }

    /**
     * 查询加价券分页列表
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("recycle/getCouponListForPage")
    public void getCouponListForPage(HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        SessionUser su = getCurrentUser(request);
        if (su.getType() != SystemConstant.USER_TYPE_SYSTEM && su.getType() != SystemConstant.USER_TYPE_SUPPER) {
            throw new SystemException("没有权限");
        }
        Page page = getPageByRequest(request);
        try {
            //获取查询条件
            String batchId = request.getParameter("batchId");//批次
            String isUse = request.getParameter("isUse");//是否使用
            String isReceive = request.getParameter("isReceive");//是否领用
            String code = request.getParameter("couponCode");//加价码

            RecycleCoupon t = new RecycleCoupon();
            if (StringUtils.isNotBlank(code)) {
                t.setCouponCode(code);
            }
            if (StringUtils.isNotBlank(batchId)) {
                t.setBatchId(batchId);
            }
            if (StringUtils.isNotBlank(isUse)) {
                t.setIsUse(Integer.parseInt(isUse));
            }
            if (StringUtils.isNotBlank(isReceive)) {
                t.setIsReceive(Integer.parseInt(isReceive));
            }
            t.setIsDel(0);
            t.setPage(page);
            List<RecycleCoupon> list = recycleCouponService.queryListForPage(t);
            if (list == null) {
                throw new SystemException("加价券为空");
            }
            for (RecycleCoupon recycleCoupon : list) {
                if (recycleCoupon.getPricingType() == 1) {
                    recycleCoupon.setCouponPrice(recycleCoupon.getStrCouponPrice().stripTrailingZeros().toPlainString() + "%");
                } else {
                    recycleCoupon.setCouponPrice(recycleCoupon.getStrCouponPrice().stripTrailingZeros().toPlainString() + "元");
                }
            }
            page.setData(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.renderJson(response, page);

    }

    /**
     * 获取已领用加价券列表
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("recycle/getIsUserCouponList")
    @ResponseBody
    public ResultData getIsUserCouponList(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        try {
            JSONObject params = getPrarms(request);
            String mobile = params.getString("mobile");

            RecycleCoupon recycleCoupon = new RecycleCoupon();
            recycleCoupon.setStatus(1);
            recycleCoupon.setIsDel(0);
            recycleCoupon.setIsUse(0);
            recycleCoupon.setReceiveMobile(mobile);

            JSONArray array = new JSONArray();
            //查询所有可使用加价券
            List<RecycleCoupon> recycleCoupons = recycleCouponService.queryUnReceive(recycleCoupon);
            recycleCouponService.getCouponList(recycleCoupons,array);
            jsonResult.put("Coupons", array);
            getResult(result,jsonResult,true,"0","成功");
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        return result;
    }


    /**
     * //     * 获取加价券手机号验证
     * //     * @param request
     * //     * @param response
     * //     * @return
     * //
     */
    @RequestMapping("recycle/receiveRole")
    @ResponseBody
    public ResultData getCouponList(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        try {
            JSONObject params = getPrarms(request);
            String validateCode = params.getString("checkCode");
            String mobile = params.getString("mobile");
            if (!checkRandomCode(request, mobile, validateCode)) {
                result.setSuccess(false);
                result.setResultMessage("验证码错误");
            }
            //用于领取加价券用户验证
            String isCheckCode = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
            request.getSession().setAttribute("isCheckCode", isCheckCode);
            jsonResult.put("isCheckCode", isCheckCode);
            getResult(result,jsonResult,true,"0","成功");
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        return result;
    }

    /**
     * 客户领取加价券
     *
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping("recycle/receiveCoupons")
    public ResultData receiveCoupons(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        try {
            JSONObject params = getPrarms(request);
            String isCheckCode = params.getString("isCheckCode");
            String mobile = params.getString("mobile");
            String batchId = params.getString("batchId");
            String openId = params.getString("openId");
            if (StringUtils.isBlank(batchId) ||
                    (StringUtils.isBlank(isCheckCode) && StringUtils.isBlank(mobile)
                            && StringUtils.isBlank(openId))) {
                result.setResultMessage("参数不完整");
                result.setResultCode("3");
                result.setSuccess(false);
                return result;
            }
            if (StringUtils.isNotBlank(isCheckCode)) {
                if (request.getSession().getAttribute("isCheckCode") != null) {
                    String CheckCode = request.getSession().getAttribute("isCheckCode").toString();
                    if (!CheckCode.equals(isCheckCode)) {
                        result.setResultMessage("isCheckCode不正确,请稍后再试");
                        result.setSuccess(false);
                        return result;
                    }
                } else {
                    result.setResultMessage("isCheckCode不正确,请稍后再试");
                    result.setSuccess(false);
                    return result;
                }
            }
            RecycleCoupon recycleCoupon = new RecycleCoupon();
            recycleCoupon.setBatchId(batchId);
            recycleCoupon.setIsDel(0);
            recycleCoupon.setIsUse(0);
            recycleCoupon.setIsReceive(0);
            recycleCoupon.setStatus(1);
            //查询该批次所有未领取可用加价券
            List<RecycleCoupon> recycleCoupons = recycleCouponService.queryList(recycleCoupon);
            if (CollectionUtils.isEmpty(recycleCoupons)) {
                result.setResultMessage("该加价券已被领完");
                result.setResultCode("2");
                result.setSuccess(false);
                return result;
            }
            recycleCoupon.setReceiveMobile(mobile);
            recycleCoupon.setIsReceive(1);
            List<RecycleCoupon> recycleCoupons1 = recycleCouponService.queryList(recycleCoupon);
            if (!CollectionUtils.isEmpty(recycleCoupons1)) {
                result.setResultMessage("您已领取过该加价券");
                result.setResultCode("1");
                result.setSuccess(false);
                return result;
            }
            //加价券绑定手机号
            recycleCoupon.setReceiveMobile(mobile);
            String recycleCode = recycleCoupons.get(0).getCouponCode();
            recycleCoupon.setCouponCode(recycleCode);
            recycleCouponService.updateForReceive(recycleCoupon);
            RecycleCoupon recycleCoupon1 = recycleCouponService.queryByCode(recycleCode);
            recycleCouponService.getRecycleCoupon(jsonResult,recycleCoupon1);

            if (StringUtils.isBlank(openId)) {
                //发短信
                StringBuffer content = new StringBuffer();
                content.append("恭喜您已领取满" + recycleCoupon1.getSubtraction_price().toString() + "元");
                if (recycleCoupon1.getPricingType() == 1) {
                    content.append("加价" + recycleCoupon1.getStrCouponPrice().toString() + "%券");
                } else {
                    content.append("加价" + recycleCoupon1.getStrCouponPrice().toString() + "券");
                }
                content.append("该券编码为" + recycleCoupon.getCouponCode() + "。下单时输入券码即可加价。" +
                        "还可将券码赠予亲友使用。关注“M超人”微信公众号还可参与大转盘抽奖，" +
                        "将有机会赢取iPhone XS！立即使用请点击http://t.cn/EztuvoQ。\n");
                Boolean isBoolean = SmsSendUtil.sendSmsThread(mobile, content.toString());
                if (isBoolean == false) {
                    return getResult(result,jsonResult,false,"5","短信发送失败");
                }
            } else {
                //微信推送
                String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&";
                url = url + "appid=" + SystemConstant.WECHAT_APPLET_APPID + "&secret=" + SystemConstant.WECHAT_APPLET_SECRET;
                String httpGet = HttpClientUtil.httpGet(url);
                JSONObject jsonObject = (JSONObject) JSONObject.parse(httpGet);
                String access_token = jsonObject.get("access_token").toString();
                // 发送模板消息
                String resultUrl2 = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + access_token;
                // 封装基础数据
                WechatTemplate wechatTemplate = new WechatTemplate();
                wechatTemplate.setTemplate_id("ngqIpbwh8bUfcSsECmogfXcV14J0tQlEpBO27izEYtY");
                wechatTemplate.setTouser("OPENID");
                wechatTemplate.setUrl("\"http://weixin.qq.com/download\"");
                JSONObject param = new JSONObject();
                //根据具体模板参数组装
                param.put("first", wechatTemplate.item("恭喜加价券领取成功", "#000000"));
                param.put("keyword1", wechatTemplate.item(recycleCoupon.getCouponName(), "#000000"));
                if (recycleCoupon.getPricingType() == 1) {
                    DecimalFormat df = new DecimalFormat("0%");
                    BigDecimal pricce = new BigDecimal(recycleCoupon.getStrCouponPrice().toString());
                    param.put("keyword2", wechatTemplate.item(df.format(pricce) + "%", "#000000"));
                } else {
                    param.put("keyword2", wechatTemplate.item(recycleCoupon.getStrCouponPrice().toString(), "#000000"));
                }
                param.put("keyword3", wechatTemplate.item(recycleCoupon.getBeginTime() + "-" + recycleCoupon.getEndTime(), "#000000"));
                param.put("keyword4", wechatTemplate.item(recycleCoupon.getRuleDescription(), "#000000"));
                param.put("remark", wechatTemplate.item("祝您生活愉快,谢谢！", "#000000"));

                String json = HttpClientUtil.sendJsonPost(resultUrl2, param.toJSONString());

                log.info("模板消息发送结果：" + json);
            }

            getResult(result,jsonResult,true,"0","成功");
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        return result;
    }


    /**
     * 查询批次
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("recycle/selectBatch")
    public ModelAndView selectBatch(HttpServletRequest request,
                                    HttpServletResponse response) {
        try {
            String batchId = request.getParameter("batchId");
            if (StringUtils.isBlank(batchId)) {
                throw new SystemException("参数不完整");
            }
            RecycleCoupon recycleCoupon = new RecycleCoupon();
            recycleCoupon.setBatchId(batchId);
            recycleCoupon.setIsUse(0);
            //查询所有未使用的
            List<RecycleCoupon> recycleCoupons = recycleCouponService.queryList(recycleCoupon);
            if (CollectionUtils.isEmpty(recycleCoupons)) {
                recycleCoupon.setIsUse(1);
                //再查询已使用的
                recycleCoupons = recycleCouponService.queryList(recycleCoupon);
                if (CollectionUtils.isEmpty(recycleCoupons)) {
                    throw new SystemException("该批次不存在");
                }
                throw new SystemException("该批次已全部使用");
            }
            RecycleCoupon recycleCoupon1 = recycleCoupons.get(0);
            if (recycleCoupon1.getPricingType() == 1) {
                recycleCoupon1.setCouponPrice(recycleCoupon1.getStrCouponPrice().toString() + "%");
            } else {
                recycleCoupon1.setCouponPrice(recycleCoupon1.getStrCouponPrice().toString());
            }
            request.setAttribute("coupon", recycleCoupon1);
            request.setAttribute("batchId", batchId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String returnView = "recycle/recycleEditCouponByBatch";
        return new ModelAndView(returnView);
    }


    /**
     * 根据id查询
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("recycle/selectById")
    public ModelAndView selectById(HttpServletRequest request,
                                   HttpServletResponse response) {
        try {
            String id = request.getParameter("id");
            if (StringUtils.isBlank(id)) {
                throw new SystemException("参数不完整");
            }
            RecycleCoupon recycleCoupon = recycleCouponService.queryById(id);
            if (recycleCoupon.getPricingType() == 1) {
                recycleCoupon.setCouponPrice(recycleCoupon.getStrCouponPrice().stripTrailingZeros().toPlainString() + "%");
            } else {
                recycleCoupon.setCouponPrice(recycleCoupon.getStrCouponPrice().stripTrailingZeros().toPlainString() + "元");
            }
            request.setAttribute("coupon", recycleCoupon);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String returnView = "recycle/recycleEditCoupon";
        return new ModelAndView(returnView);
    }


    /**
     * 修改加价券
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("recycle/updateCoupons")
    public void updateCoupons(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> resultMap = Maps.newHashMap();
        try {
            SessionUser su = getCurrentUser(request);
            if (su.getType() != SystemConstant.USER_TYPE_SUPPER && su.getType() != SystemConstant.USER_TYPE_SYSTEM) {
                throw new SystemException("对不起，您没有操作权限!");
            }
            String batchId = request.getParameter("batchId");//批次
            String couponCode = request.getParameter("couponCode");//加价券编码
            String name = request.getParameter("name");//优惠券名称
            String pricingType = request.getParameter("pricingType");//加价类型 1：百分比 2:：固定加价
            String upperLimit = request.getParameter("upperLimit");//订单金额额度
            String addPriceUpper = request.getParameter("addPriceUpper");//加价金额额度
            String subtractionPrice = request.getParameter("subtractionPrice");//满减金额额度
            String description = request.getParameter("ruleDescription");//加价规则描述
            String price = request.getParameter("price");//优惠券金额
            String startTime = request.getParameter("startTime");//优惠券开始时间
            String endTime = request.getParameter("endTime");//优惠券过期时间
            String note = request.getParameter("note");//优惠券备注
            if (StringUtils.isBlank(name) || StringUtils.isBlank(price) || StringUtils.isBlank(startTime)
                    || StringUtils.isBlank(endTime) || StringUtils.isBlank(pricingType)
                    || StringUtils.isBlank(subtractionPrice) || StringUtils.isBlank(description)) {
                throw new SystemException("参数不完整");
            }
            RecycleCoupon recycleCoupon = new RecycleCoupon();
            recycleCoupon.setUpdateUserid(su.getUserId());
            recycleCoupon.setCouponName(name);
            recycleCoupon.setPricingType(Integer.valueOf(pricingType));
            recycleCoupon.setRuleDescription(description);
            recycleCoupon.setUpperLimit(new BigDecimal(upperLimit));
            recycleCoupon.setAddPriceUpper(new BigDecimal(addPriceUpper));
            recycleCoupon.setSubtraction_price(new BigDecimal(subtractionPrice));
            if (price.contains("%")) {
                price = StringUtils.remove(price, "%");
            }
            if (price.contains("元")) {
                price = StringUtils.remove(price, "元");
            }
            recycleCoupon.setStrCouponPrice(new BigDecimal(price));
            recycleCoupon.setBeginTime(startTime);
            recycleCoupon.setEndTime(endTime);
            recycleCoupon.setNote(note);
            if (StringUtils.isNotBlank(batchId) && StringUtils.isBlank(couponCode)) {
                recycleCoupon.setBatchId(batchId);
                recycleCouponService.updateByBatchId(recycleCoupon);
            } else if (StringUtils.isBlank(batchId) && StringUtils.isNotBlank(couponCode)) {
                recycleCoupon.setCouponCode(couponCode);
                recycleCouponService.couponCodeUpdate(recycleCoupon);
            } else {
                throw new SystemException("参数有误");
            }
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
            resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
            resultMap.put(RESULTMAP_KEY_MSG, "保存失败");
        }
        renderJson(response, resultMap);
    }


    @RequestMapping(value = "/recycle/delete")
    public void delete(HttpServletRequest request,
                       HttpServletResponse response) throws Exception {

        Map<String, Object> resultMap = Maps.newHashMap();
        try {
            SessionUser su = getCurrentUser(request);
            //获取优惠券id
            String batchId = request.getParameter("batchId");
            RecycleCoupon recycleCoupon = new RecycleCoupon();
            if (StringUtils.isNotBlank(batchId)) {
                recycleCoupon.setBatchId(batchId);
                List<RecycleCoupon> recycleCoupons = recycleCouponService.queryList(recycleCoupon);
                if (CollectionUtils.isEmpty(recycleCoupons)) {
                    throw new SystemException("改批次不存在");
                }
                recycleCouponService.deleteByBatchId(recycleCoupon);
            } else {
                //获取优惠券id
                recycleCoupon.setIsDel(1);
                String ids = request.getParameter("id");
                recycleCouponService.deleteById(ids);
            }
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
            resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
        } catch (Exception e) {
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
            e.printStackTrace();
        }
        renderJson(response, resultMap);
    }


    /**
     * 返还加价券
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "recycle/cancelCoupon")
    @ResponseBody
    public ResultData cancelCoupon(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        try {
            String sign = request.getParameter("sign");
            String orderNo = request.getParameter("orderNo");
            String MD5sign = "orderNo=" + orderNo + autograph;
            MD5sign = MD5Util.md5Encode(MD5sign);
            if (StringUtils.isBlank(orderNo) || StringUtils.isBlank(sign)) {
                result.setSuccess(false);
                result.setResultCode(ApiResultConstant.resultCode_1001);
                result.setResultMessage(ApiResultConstant.resultCode_str_1001);
                return result;
            } else if (!sign.equals(MD5sign)) {
                result.setSuccess(false);
                result.setResultCode(ApiResultConstant.resultCode_1003);
                result.setResultMessage(ApiResultConstant.resultCode_str_1003);
                return result;
            }
            RecycleOrder order = recycleOrderService.queryByOrderNo(orderNo);
            if (order == null) {
                result.setSuccess(false);
                result.setResultCode(ApiResultConstant.resultCode_3003);
                result.setResultMessage(ApiResultConstant.resultCode_str_3003);
                return result;
            }
            //删除订单加价券使用记录
            recycleOrderService.deleteCouponIdByOrderStatus(orderNo);
            RecycleCoupon recycleCoupon = recycleCouponService.queryById(order.getCouponId());
            if (recycleCoupon == null) {
                result.setSuccess(false);
                result.setResultCode(ApiResultConstant.resultCode_3003);
                result.setResultMessage(ApiResultConstant.resultCode_str_3003);
                return result;
            }
            recycleCouponService.updateForNoUse(order.getCouponId());

            //发短信
            StringBuffer content = sendThread(recycleCoupon);
            Boolean isBoolean = SmsSendUtil.sendSmsThread(order.getMobile(), content.toString());
            if (isBoolean == false) {
                result.setResultMessage("短信发送失败");
                result.setResultCode("5");
                result.setSuccess(false);
                return result;
            }

            result.setSuccess(true);
            result.setResultCode(ApiResultConstant.resultCode_0);
            result.setResultMessage(ApiResultConstant.resultCode_str_0);
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        return result;
    }

    //发送短信内容
    private StringBuffer sendThread(RecycleCoupon recycleCoupon) throws Exception {
        StringBuffer content = new StringBuffer();
        if (recycleCoupon.getPricingType() == 1) {
            content.append("您的加价" + recycleCoupon.getStrCouponPrice().toString() + "%券");
        } else {
            content.append("您的加价" + recycleCoupon.getStrCouponPrice().toString() + "元券");
        }
        content.append("劵编码为" + recycleCoupon.getCouponCode() + "状态已变为可用。");
        content.append("关注“M超人”微信公众号还可参与大转盘抽奖，将有机会赢取iphone XS！立即使用请点击http://t.cn/EztuvoQ。\n");
        return content;
    }

}
