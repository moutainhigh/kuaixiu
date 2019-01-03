package com.kuaixiu.order.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.domain.TradeFundBill;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.common.alipay.api.AliPayService;
import com.common.alipay.api.impl.AliPayServiceImpl;
import com.common.exception.SystemException;
import com.common.util.Base64Util;
import com.common.util.DateUtil;
import com.common.util.NOUtil;
import com.common.util.NumberUtils;
import com.common.util.SmsSendUtil;
import com.common.wechat.api.WxMpService;
import com.common.wechat.bean.result.WxMpPayCloseResult;
import com.common.wechat.bean.result.WxMpPayQueryRefundState;
import com.common.wechat.bean.result.WxMpPayRefundResult;
import com.common.wechat.bean.result.WxMpPayResult;
import com.common.wechat.bean.result.WxMpPrepayIdResult;
import com.common.wechat.common.exception.WxErrorException;
import com.common.wechat.common.util.StringUtils;
import com.kuaixiu.engineer.service.EngineerService;
import com.kuaixiu.integral.service.GetIntegralService;
import com.kuaixiu.oldtonew.entity.NewOrder;
import com.kuaixiu.oldtonew.entity.NewOrderPay;
import com.kuaixiu.oldtonew.entity.OldToNewUser;
import com.kuaixiu.oldtonew.service.NewOrderPayService;
import com.kuaixiu.oldtonew.service.NewOrderService;
import com.kuaixiu.oldtonew.service.OldToNewService;
import com.kuaixiu.order.constant.OrderConstant;
import com.kuaixiu.order.entity.*;
import com.kuaixiu.screen.entity.ScreenOrder;
import com.kuaixiu.screen.entity.ScreenProject;
import com.kuaixiu.screen.service.ScreenOrderService;
import com.kuaixiu.screen.service.ScreenProjectService;
import com.system.constant.SystemConstant;

import com.system.wechat.controller.PaymentController;
import jodd.util.StringUtil;

import com.system.basic.user.entity.SessionUser;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 订单支付服务类.
 *
 * @author: lijx
 * @CreateDate: 2016-10-6 上午2:21:21
 * @version: V 1.0
 */
@Service("orderPayService")
public class OrderPayService {
    private static final Logger log = Logger.getLogger(OrderPayService.class);

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDetailService detailService;
    @Autowired
    private OrderPayLogService payLogService;
    @Autowired
    private OrderRefundLogService refundLogService;
    @Autowired
    private EngineerService engineerService;
    @Autowired
    private WxMpService wxMpService;
    @Autowired
    private NewOrderService newOrderService;
    @Autowired
    private NewOrderPayService newOrderPayService;
    @Autowired
    private OldToNewService oldToNewService;
    @Autowired
    private GetIntegralService getIntegralService;
    @Autowired
    private ScreenOrderService screenOrderService;
    @Autowired
    private ScreenProjectService screenProjectService;
    // **********自定义方法***********

    /**
     * 发起微信支付 1、查询订单是否存在 2、校验是否有订单操作权限 3、校验订单状态是否已发起支付 4、生成支付订单号 5、保存支付日志
     *
     * @param id
     * @param su
     * @author: lijx
     * @CreateDate: 2016-9-7 下午10:31:07
     */
    @Transactional
    public OrderPayLog startWechatPay(String id, OrderPayLog payLog, SessionUser su) {
        Order o = orderService.queryById(id);
        payLog.setPayMethod(0);
        if (o == null) {
            throw new SystemException("订单不存在！");
        }
        if (su != null) {
            if (!su.getUserId().equals(o.getCustomerId())) {
                throw new SystemException("对不起，您无权操作该订单！");
            }
        }
        if (o.getOrderStatus() == OrderConstant.ORDER_STATUS_CANCEL) {
            throw new SystemException("该订单已取消！");
        }
        if (payLog.getExpenseType() == OrderConstant.ORDER_EXPENSE_TYPE_BALANCE
                && o.getOrderStatus() >= OrderConstant.ORDER_STATUS_FINISHED) {
            throw new SystemException("该订单已完成,无需支付！");
        }
        OrderPayLog payLogOld = new OrderPayLog();
        if (payLog.getIsApp() != null) {
            if (payLog.getIsApp() == 0) {
                OrderPayLog payLog1 = new OrderPayLog();
                payLog1.setOrderNo(o.getOrderNo());
                payLog1.setPayMethod(0);
                payLogOld = payLogService.queryAppUnFinishedByOrderNo(payLog1);
            }
        } else {
            // 判断是有未未完成的支付记录
            payLogOld = payLogService.queryUnFinishedByOrderNo(o.getOrderNo());
        }

        if (payLogOld != null) {
            // 判断更新未完成的支付订单
            updatePayLogStatus(payLogOld, o);
            if (payLogOld.getPayStatus() != OrderConstant.ORDER_PAY_STATUS_WAIT) {
                // 关闭老订单
                closeWechatOrder(payLogOld);
                if (payLogOld.getPayStatus() == OrderConstant.ORDER_PAY_STATUS_SUBMITED) {
                    // 关闭订单失败
                    throw new SystemException("该订单存在异常的支付记录，请稍后再试！");
                }
            }
            if (payLogOld.getPayStatus() == OrderConstant.ORDER_PAY_STATUS_SUCCESS) {
                // 支付成功
                payLog = payLogOld;
            } else if (payLogOld.getPayStatus() == OrderConstant.ORDER_PAY_STATUS_SUBMITED) {
                //APP支付
                if (payLog.getIsApp() != null) {
                    // 保存支付记录
                    payLog = createPayLog(payLog, o);
                    payLogService.save(payLog);
                    // 调用微信下单接口
                    createWechatOrder(payLog);
                    o.setPayStatus(payLog.getPayStatus());
                    // 已提交,添加判断是否和当前支付类型一致，如果不是则关闭原支付订单
                } else if (!payLogOld.getTradeType().equals(payLog.getTradeType())) {
                    // 保存支付记录
                    payLog = createPayLog(payLog, o);
                    payLogService.save(payLog);

                    // 调用微信下单接口
                    createWechatOrder(payLog);
                    o.setPayStatus(payLog.getPayStatus());
                } else {
                    payLog = payLogOld;
                }
            }
//            else if (payLogOld.getPayStatus() == OrderConstant.ORDER_PAY_STATUS_OVERTIME) {
//                // 已超时
//                // 保存支付记录
//                payLog = createPayLog(payLog, o);
//                payLogService.save(payLog);
//
//                // 调用微信下单接口
//                createWechatOrder(payLog);
//                o.setPayStatus(payLog.getPayStatus());
//            }
            else if (payLogOld.getPayStatus() == OrderConstant.ORDER_PAY_STATUS_WAIT) {
                // 订单不存在重新下单
                // 已提交,添加判断是否很当前支付类型一致，如果不是则关闭原支付订单
                if (!payLogOld.getTradeType().equals(payLog.getTradeType())) {
                    // 关闭历史支付记录
                    payLogOld.setPayStatus(OrderConstant.ORDER_PAY_STATUS_COMPLETE);
                    payLogService.update(payLogOld);

                    // 保存支付记录
                    payLog = createPayLog(payLog, o);
                    payLogService.save(payLog);

                    // 调用微信下单接口
                    createWechatOrder(payLog);
                    o.setPayStatus(payLog.getPayStatus());
                } else {
                    // 调用微信下单接口
                    createWechatOrder(payLogOld);
                    o.setPayStatus(payLogOld.getPayStatus());
                    payLog = payLogOld;
                }
            }
        } else {
            payLog = createPayLog(payLog, o);
            // 保存支付记录
            payLogService.save(payLog);
            // 调用微信下单接口
            createWechatOrder(payLog);
            o.setPayStatus(payLog.getPayStatus());
        }
        orderService.saveUpdate(o);
        return payLog;

    }

    /**
     * 发起微信支付,针对以旧换新订单 1、查询订单是否存在 2、校验是否有订单操作权限 3、校验订单状态是否已发起支付 4、生成支付订单号
     * 5、保存支付日志
     */
    @Transactional
    public OrderPayLog newOrderStartWechatPay(String id, OrderPayLog payLog, SessionUser su) {
        NewOrder o = (NewOrder) newOrderService.queryById(id);
        if (o == null) {
            throw new SystemException("订单不存在！");
        }
        NewOrderPay orderPay = newOrderPayService.queryByOrderNo(o.getOrderNo());
        if (!su.getUserId().equals(o.getCustomerId())) {
            throw new SystemException("对不起，您无权操作该订单！");
        }
        if (o.getOrderStatus() == OrderConstant.ORDER_STATUS_CANCEL) {
            throw new SystemException("该订单已取消！");
        }
        if (payLog.getExpenseType() == OrderConstant.ORDER_EXPENSE_TYPE_BALANCE
                && o.getOrderStatus() >= OrderConstant.ORDER_STATUS_FINISHED) {
            throw new SystemException("该订单已完成,无需支付！");
        }

        // 判断是有未未完成的支付记录
        OrderPayLog payLogOld = payLogService.queryUnFinishedByOrderNo(o.getOrderNo());
        if (payLogOld != null) {
            // 判断更新未完成的支付订单
            updateNewPayLogStatus(payLogOld, o);
            if (payLogOld.getPayStatus() == OrderConstant.ORDER_PAY_STATUS_SUCCESS) {
                // 支付成功
                payLog = payLogOld;
            } else if (payLogOld.getPayStatus() == OrderConstant.ORDER_PAY_STATUS_SUBMITED) {
                // 已提交,添加判断是否很当前支付类型一致，如果不是则关闭原支付订单
                if (!payLogOld.getTradeType().equals(payLog.getTradeType())) {
                    // 关闭老订单
                    closeWechatOrder(payLogOld);
                    if (payLogOld.getPayStatus() == OrderConstant.ORDER_PAY_STATUS_SUBMITED) {
                        // 关闭订单失败
                        throw new SystemException("该订单存在异常的支付记录，请稍后再试！");
                    }
                    // 保存支付记录
                    payLog = createNewPayLog(payLog, o);
                    payLogService.save(payLog);
                    // 调用微信下单接口
                    createWechatOrder(payLog);
                    orderPay.setPayStatus(payLog.getPayStatus());
                } else {
                    payLog = payLogOld;
                }
            }
//            else if (payLogOld.getPayStatus() == OrderConstant.ORDER_PAY_STATUS_OVERTIME) {
//                // 已超时
//                // 保存支付记录
//                payLog = createNewPayLog(payLog, o);
//                payLogService.save(payLog);
//                // 调用微信下单接口
//                createWechatOrder(payLog);
//                orderPay.setPayStatus(payLog.getPayStatus());
//            }
            else if (payLogOld.getPayStatus() == OrderConstant.ORDER_PAY_STATUS_WAIT) {
                // 订单不存在重新下单
                // 已提交,添加判断是否很当前支付类型一致，如果不是则关闭原支付订单
                if (!payLogOld.getTradeType().equals(payLog.getTradeType())) {
                    // 关闭历史支付记录
                    payLogOld.setPayStatus(OrderConstant.ORDER_PAY_STATUS_COMPLETE);
                    payLogService.update(payLogOld);

                    // 保存支付记录
                    payLog = createNewPayLog(payLog, o);
                    payLogService.save(payLog);

                    // 调用微信下单接口
                    createWechatOrder(payLog);
                    orderPay.setPayStatus(payLog.getPayStatus());
                } else {
                    // 调用微信下单接口
                    createWechatOrder(payLogOld);
                    orderPay.setPayStatus(payLogOld.getPayStatus());
                    payLog = payLogOld;
                }
            }
        } else {
            payLog = createNewPayLog(payLog, o);
            // 保存支付记录
            payLogService.save(payLog);
            // 调用微信下单接口
            createWechatOrder(payLog);
            orderPay.setPayStatus(payLog.getPayStatus());
        }
        newOrderService.saveUpdate(o);
        newOrderPayService.UpdateOrderPay(orderPay);
        return payLog;

    }

    /**
     * 发起微信支付 针对碎屏险订单 1、查询订单是否存在 2、校验是否有订单操作权限 3、校验订单状态是否已发起支付 4、生成支付订单号
     * 5、保存支付日志
     */
    @Transactional
    public OrderPayLog screenWechatPay(String id, OrderPayLog payLog) {
        ScreenOrder o = screenOrderService.queryById(id);
        if (o == null) {
            throw new SystemException("订单不存在！");
        }
        // 如果订单是退款中 或者已退款则不再继续
        if (o.getOrderStatus() == OrderConstant.SCREEN_ORDER_REFUNDING
                || o.getOrderStatus() == OrderConstant.SCREEN_ORDER_REFUNDED) {
            throw new SystemException("该订单正在退款中！");
        }
        if (payLog.getExpenseType() == OrderConstant.ORDER_EXPENSE_TYPE_BALANCE
                && o.getOrderStatus() >= OrderConstant.ORDER_STATUS_FINISHED) {
            throw new SystemException("该订单已完成,无需支付！");
        }
        // 判断是有未未完成的支付记录
        OrderPayLog payLogOld = payLogService.queryUnFinishedByOrderNo(o.getOrderNo());
        if (payLogOld != null) {
            // 判断更新未完成的支付订单
            screenUpdatePayLogStatus(payLogOld, o);
            if (payLogOld.getPayStatus() == OrderConstant.ORDER_PAY_STATUS_SUCCESS) {
                // 支付成功
                payLog = payLogOld;
            } else if (payLogOld.getPayStatus() == OrderConstant.ORDER_PAY_STATUS_SUBMITED) {
                // 已提交,添加判断是否很当前支付类型一致，如果不是则关闭原支付订单
                if (!payLogOld.getTradeType().equals(payLog.getTradeType())) {
                    // 关闭老订单
                    closeWechatOrder(payLogOld);
                    if (payLogOld.getPayStatus() == OrderConstant.ORDER_PAY_STATUS_SUBMITED) {
                        // 关闭订单失败
                        throw new SystemException("该订单存在异常的支付记录，请稍后再试！");
                    }
                    // 保存支付记录
                    payLog = screenCreatePayLog(payLog, o);
                    payLogService.save(payLog);
                    // 调用微信下单接口
                    createWechatOrder(payLog);
                } else {
                    payLog = payLogOld;
                }
            }
//            else if (payLogOld.getPayStatus() == OrderConstant.ORDER_PAY_STATUS_OVERTIME) {
//                // 已超时
//                // 保存支付记录
//                payLog = screenCreatePayLog(payLog, o);
//                payLogService.save(payLog);
//
//                // 调用微信下单接口
//                createWechatOrder(payLog);
//            }
            else if (payLogOld.getPayStatus() == OrderConstant.ORDER_PAY_STATUS_WAIT) {
                // 订单不存在重新下单
                // 已提交,添加判断是否很当前支付类型一致，如果不是则关闭原支付订单
                if (!payLogOld.getTradeType().equals(payLog.getTradeType())) {
                    // 关闭历史支付记录
                    payLogOld.setPayStatus(OrderConstant.ORDER_PAY_STATUS_COMPLETE);
                    payLogService.update(payLogOld);

                    // 保存支付记录
                    payLog = screenCreatePayLog(payLog, o);
                    payLogService.save(payLog);

                    // 调用微信下单接口
                    createWechatOrder(payLog);
                } else {
                    // 调用微信下单接口
                    createWechatOrder(payLogOld);
                    payLog = payLogOld;
                }
            }
        } else {
            payLog = screenCreatePayLog(payLog, o);
            // 保存支付记录
            payLogService.save(payLog);
            // 调用微信下单接口
            createWechatOrder(payLog);
        }
        screenOrderService.saveUpdate(o);
        return payLog;

    }

    /**
     * 调用微信接口下单
     *
     * @param payLog
     * @author: lijx
     * @CreateDate: 2016-10-13 下午11:16:47
     */
    private void createWechatOrder(OrderPayLog payLog) {
        // 微信下单
        Map<String, String> payMap = getMapByPayLog(payLog);
        WxMpPrepayIdResult result = wxMpService.getPayService().getPrepayId(payMap);
        if (result == null) {
            payLog.setPayStatus(OrderConstant.ORDER_PAY_STATUS_FAILD);
            payLog.setErrCodeDes("微信接口返回空");
            // 更新支付状态
            payLogService.update(payLog);
            throw new SystemException("调用微信接口异常！");
        }
        if ("SUCCESS".equals(result.getReturn_code())) {
            if ("SUCCESS".equals(result.getResult_code())) {
                payLog.setPrepayId(result.getPrepay_id());
                payLog.setCodeUrl(result.getCode_url());
                payLog.setPayStatus(OrderConstant.ORDER_PAY_STATUS_SUBMITED);
            } else {
                payLog.setPayStatus(OrderConstant.ORDER_PAY_STATUS_FAILD);
                payLog.setErrCode(result.getErr_code());
                payLog.setErrCodeDes(result.getErr_code_des());
            }
        } else {
            payLog.setPayStatus(OrderConstant.ORDER_PAY_STATUS_FAILD);
            payLog.setErrCode(result.getErr_code());
            payLog.setErrCodeDes(result.getReturn_msg());
        }
        // 更新支付状态
        payLogService.update(payLog);
        log.info("Return_code: " + result.getReturn_code());
        log.info("Result_code: " + result.getResult_code());
        log.info("Return_msg: " + result.getReturn_msg());
        log.info("Prepay_id: " + result.getPrepay_id());
        log.info("Code_url: " + result.getCode_url());
    }

    @Autowired
    private UpdateOrderPriceService updateOrderPriceService;

    /**
     * 生成支付订单
     *
     * @param payLog
     * @param o
     * @return
     * @author: lijx
     * @CreateDate: 2016-10-13 下午10:42:23
     */
    public OrderPayLog createPayLog(OrderPayLog payLog, Order o) {
        // 生成支付订单号
        String payOrderNo = NOUtil.getNo("PN-");
        payLog.setPayOrderNo(payOrderNo);

        payLog.setOrderNo(o.getOrderNo());
        //修改后订单不用减优惠券
        List<UpdateOrderPrice> orderPrices = updateOrderPriceService.getDao().queryByUpOrderNo(o.getOrderNo());
        if (o.getCouponPrice() != null) {
            if (CollectionUtils.isEmpty(orderPrices)) {
                // 支付余款 = 订单实际费用 (已考虑用户是否使用优惠券)
                payLog.setTotalFee((int) (o.getRealPriceSubCoupon().doubleValue() * 100));
            } else {
                payLog.setTotalFee((int) (o.getRealPrice().doubleValue() * 100));
            }
        } else {
            payLog.setTotalFee((int) (o.getRealPrice().doubleValue() * 100));
        }
        payLog.setCustomerId(o.getCustomerId());
        payLog.setCustomerMobile(o.getMobile());
        payLog.setCustomerName(o.getCustomerName());

        Calendar cal = Calendar.getInstance();
        // 开始时间
        payLog.setTimeStart(DateUtil.getSerialFullDate(cal.getTime()));
        // 订单失效时间
        cal.add(Calendar.HOUR, 2);
        payLog.setTimeExpire(DateUtil.getSerialFullDate(cal.getTime()));

        payLog.setPayStatus(0);
        payLog.setPayType(0);
        payLog.setFeeType("CNY");
        payLog.setLimitPay("no_credit");
        payLog.setProductId(o.getId());

        JSONArray detailsJsonArray = new JSONArray();
        // 查询订单明细
        List<OrderDetail> details = detailService.queryByOrderNo(o.getOrderNo());
        for (OrderDetail dt : details) {
            if (dt != null && dt.getType() == 0) {
                JSONObject json = new JSONObject();
                json.put("goods_id", dt.getId());
                json.put("wxpay_goods_id", "");
                json.put("goods_name", dt.getProjectName());
                json.put("quantity", 1);
                json.put("price", (int) (o.getDepositPrice().doubleValue() * 100));
                json.put("goods_category", o.getModelName());
                json.put("body", "");
                detailsJsonArray.add(json);
            }
        }
        JSONObject detailJson = new JSONObject();
        detailJson.put("goods_detail", detailsJsonArray);
        payLog.setDetail(detailJson.toString());
        return payLog;
    }

    /**
     * 生成以旧换新支付订单
     */
    public OrderPayLog createNewPayLog(OrderPayLog payLog, NewOrder o) {
        // 生成支付订单号
        String payOrderNo = NOUtil.getNo("PN-");
        payLog.setPayOrderNo(payOrderNo);
        // NewOrderPay
        // newOrder=newOrderPayService.queryByOrderNo(o.getOrderNo());
        OldToNewUser old = oldToNewService.queryById(o.getUserId());
        payLog.setOrderNo(o.getOrderNo());

        // 支付余款
        if (o.getIsUseCoupon() == 1) {
            // 如果用户使用了优惠卷
            payLog.setTotalFee((int) (o.getRealPrice().subtract(o.getCouponPrice()).doubleValue() * 100));
        } else {
            payLog.setTotalFee((int) (o.getRealPrice().doubleValue() * 100));
        }
        payLog.setCustomerId(o.getCustomerId());
        payLog.setCustomerMobile(old.getTel());
        payLog.setCustomerName(old.getName());

        Calendar cal = Calendar.getInstance();
        // 开始时间
        payLog.setTimeStart(DateUtil.getSerialFullDate(cal.getTime()));
        // 订单失效时间
        cal.add(Calendar.HOUR, 2);
        payLog.setTimeExpire(DateUtil.getSerialFullDate(cal.getTime()));

        payLog.setPayStatus(0);
        payLog.setPayType(0);
        payLog.setFeeType("CNY");
        payLog.setLimitPay("no_credit");
        payLog.setProductId(o.getId());

        JSONArray detailsJsonArray = new JSONArray();
        JSONObject json = new JSONObject();
        json.put("goods_id", o.getId());
        json.put("wxpay_goods_id", "");
        json.put("goods_name", "以旧换新兑换");
        json.put("quantity", 1);
        json.put("price", (int) (o.getRealPrice().doubleValue() * 100));
        json.put("body", "");
        detailsJsonArray.add(json);
        JSONObject detailJson = new JSONObject();
        detailJson.put("goods_detail", detailsJsonArray);
        payLog.setDetail(detailJson.toString());
        return payLog;
    }

    /**
     * 生成支付订单 针对碎屏险订单
     */
    public OrderPayLog screenCreatePayLog(OrderPayLog payLog, ScreenOrder o) {
        // 生成支付订单号
        String payOrderNo = NOUtil.getNo("PN-");
        payLog.setPayOrderNo(payOrderNo);

        payLog.setOrderNo(o.getOrderNo());

        // 支付余款
        payLog.setTotalFee((int) (o.getRealPrice().doubleValue() * 100));
        payLog.setCustomerMobile(o.getMobile());
        payLog.setCustomerName(o.getName());
        Calendar cal = Calendar.getInstance();
        // 开始时间
        payLog.setTimeStart(DateUtil.getSerialFullDate(cal.getTime()));
        // 订单失效时间
        cal.add(Calendar.HOUR, 2);
        payLog.setTimeExpire(DateUtil.getSerialFullDate(cal.getTime()));

        payLog.setPayStatus(0);
        payLog.setPayType(0);
        payLog.setFeeType("CNY");
        payLog.setLimitPay("no_credit");
        payLog.setProductId(o.getId());

        JSONArray detailsJsonArray = new JSONArray();
        JSONObject json = new JSONObject();
        json.put("goods_id", o.getId());
        json.put("wxpay_goods_id", "");
        json.put("goods_name", "碎屏险支付");
        json.put("quantity", 1);
        json.put("price", (int) (o.getRealPrice().doubleValue() * 100));
        json.put("body", "");
        detailsJsonArray.add(json);

        JSONObject detailJson = new JSONObject();
        detailJson.put("goods_detail", detailsJsonArray);
        payLog.setDetail(detailJson.toString());
        return payLog;
    }

    /**
     * @param payLog
     * @return
     * @author: lijx
     * @CreateDate: 2016-10-7 下午6:41:59
     */
    public Map<String, String> getMapByPayLog(OrderPayLog payLog) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("body", payLog.getBody());
        // map.put("detail", "<![CDATA[" + payLog.getDetail() + "]]>");
        map.put("detail", payLog.getDetail());
        map.put("attach", payLog.getAttach());
        map.put("out_trade_no", payLog.getPayOrderNo());
        // map.put("fee_type", payLog.getFeeType());
        map.put("total_fee", payLog.getTotalFee() + "");
        // map.put("total_fee", "1");
        map.put("spbill_create_ip", payLog.getSpbillCreateIp());
        // map.put("time_start", payLog.getTimeStart());
        // map.put("time_expire", payLog.getTimeExpire());
        if (payLog.getIsApp() != null) {
            map.put("time_start", payLog.getTimeStart());
            map.put("time_expire", payLog.getTimeExpire());
        }
        map.put("notify_url", SystemConstant.WECHAT_PAY_NOTIFY_URL);
        map.put("trade_type", payLog.getTradeType());
        map.put("product_id", payLog.getProductId());
        // map.put("limit_pay", payLog.getLimitPay());
        map.put("openid", payLog.getOpenid());
        return map;
    }

    /**
     * 支付宝查询关闭
     *
     * @param payLog
     * @author: najy
     * @CreateDate: 2016-10-23 下午10:21:48
     */
    public void closeAlipayOrder(OrderPayLog payLog) {
        AliPayService aliPayService=new AliPayServiceImpl();
        AlipayTradeQueryResponse result = aliPayService.alipayQueryOrder(payLog.getPayOrderNo());
        if (result.isSuccess()) {
            if ("TRADE_CLOSED".equals(result.getTradeStatus())) {
                // 订单已超时重新生成支付订单
                log.info("修改："+payLog.getPayOrderNo());
                payLog.setPayStatus(OrderConstant.ORDER_PAY_STATUS_COMPLETE);
                payLogService.update(payLog);
            }else if ("TRADE_SUCCESS".equals(result.getTradeStatus())) {
                log.info(payLog.getOrderNo() + "=该订单已支付成功");
                Order o = orderService.queryByOrderNo(payLog.getOrderNo());
                // 支付成功
                payLog.setPayStatus(OrderConstant.ORDER_PAY_STATUS_SUCCESS);
                // 更新订单状态
                o.setPayType(1);//支付宝支付
                paySuccess(payLog, o);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                // 保存支付信息
                //去掉时间字符串空格，横线，冒号
                String endTime = sdf.format(result.getSendPayDate()).replace(" ", "").replace("-", "").replace(":", "");
                payLog.setTimeEnd(endTime);
                payLog.setTransactionId(result.getTradeNo());
                payLog.setBuyerLogonId(result.getBuyerLogonId());
                List<TradeFundBill> tradeFundBills = result.getFundBillList();
                for (TradeFundBill fundBill : tradeFundBills) {
                    payLog.setFundChannel(fundBill.getFundChannel());
                }
                payLogService.update(payLog);
            }
        } else if ("ACQ.TRADE_NOT_EXIST".equals(result.getSubCode())) {
            // 订单不存在
            log.info("修改："+payLog.getPayOrderNo());
            payLog.setPayStatus(OrderConstant.ORDER_PAY_STATUS_COMPLETE);
            payLogService.update(payLog);
        } else {
            throw new SystemException("系统异常请稍后再试！");
        }
    }

    /**
     * @param payLog
     * @author: lijx
     * @CreateDate: 2016-10-23 下午10:21:48
     */
    public void closeWechatOrder(OrderPayLog payLog) {
        WxMpPayCloseResult result = wxMpService.getPayService().closeOrder(payLog.getPayOrderNo());
        if (result != null && "SUCCESS".equals(result.getReturn_code())) {
            if ("SUCCESS".equals(result.getResult_code())) {
                log.info("修改："+payLog.getPayOrderNo());
                // 订单已取消重新生成支付订单
                payLog.setPayStatus(OrderConstant.ORDER_PAY_STATUS_COMPLETE);
                payLogService.update(payLog);
            } else {
                if ("ORDERNOTEXIST".equals(result.getErr_code()) || "ORDERCLOSED".equals(result.getErr_code())) {
                    // 订单不存在 、订单已关闭
                    log.info("修改："+payLog.getPayOrderNo());
                    payLog.setPayStatus(OrderConstant.ORDER_PAY_STATUS_FAIL);
                    payLogService.update(payLog);
                } else {
                    // 其它情况不改变支付状态
                    payLog.setErrCode(result.getErr_code());
                    payLog.setErrCodeDes(result.getErr_code_des());
                    payLogService.update(payLog);
                }
            }
        } else {
            throw new SystemException("系统异常请稍后再试！");
        }
    }

    /**
     * 关闭微信订单 维修订单 1、更新微信订单状态 2、关闭订单
     *
     * @param o
     * @param su
     * @return
     */
    public boolean cancelWechatOrder(Order o, SessionUser su) {
        if (su.getType() != SystemConstant.USER_TYPE_SYSTEM && su.getType() != SystemConstant.USER_TYPE_CUSTOMER_SERVICE
                && !su.getUserId().equals(o.getCustomerId()) && !su.getUserId().equals(o.getEngineerId())) {
            throw new SystemException("对不起，您无权操作该订单！");
        }
        if (o.getOrderStatus() == OrderConstant.ORDER_STATUS_FINISHED) {
            throw new SystemException("该订单已完成！");
        }
        if (o.getIsDrawback() == OrderConstant.ORDER_REFUND_STATUS_SUCCESS) {
            throw new SystemException("该订单已退款！");
        }
        boolean isRefund = true;
        // 查询支付订单
        List<OrderPayLog> payLogs = payLogService.queryByOrderNo(o.getOrderNo());
        if (payLogs == null || payLogs.isEmpty()) {
            return false;
        }
        for (OrderPayLog pLog : payLogs) {
            if (pLog.getPayStatus() == OrderConstant.ORDER_PAY_STATUS_FAILD
                    || pLog.getPayStatus() == OrderConstant.ORDER_PAY_STATUS_SUBMITED) {
                // 判断更新未完成的支付订单
                updatePayLogStatus(pLog, o);
            }
            if (pLog.getPayStatus() == OrderConstant.ORDER_PAY_STATUS_SUBMITED) {
                closeWechatOrder(pLog);
                if (pLog.getPayStatus() == OrderConstant.ORDER_PAY_STATUS_SUBMITED) {
                    // 关闭订单失败
                    isRefund = false;
                }
            }
        }
        return isRefund;
    }

    /**
     * 关闭以旧换新支付订单 1、更新微信订单状态 2、关闭订单
     *
     * @param o
     * @param su
     * @return
     */
    public boolean cancelWechatNewOrder(NewOrder o, SessionUser su) {
        if (su.getType() != SystemConstant.USER_TYPE_SYSTEM && su.getType() != SystemConstant.USER_TYPE_CUSTOMER_SERVICE
                && !su.getUserId().equals(o.getCustomerId()) && !su.getUserId().equals(o.getEngineerId())) {
            throw new SystemException("对不起，您无权操作该订单！");
        }
        if (o.getOrderStatus() == OrderConstant.ORDER_STATUS_FINISHED) {
            throw new SystemException("该订单已完成！");
        }
        boolean isRefund = true;
        // 查询支付订单
        List<OrderPayLog> payLogs = payLogService.queryByOrderNo(o.getOrderNo());
        if (payLogs == null || payLogs.isEmpty()) {
            return false;
        }
        for (OrderPayLog pLog : payLogs) {
            if (pLog.getPayStatus() == OrderConstant.ORDER_PAY_STATUS_FAILD
                    || pLog.getPayStatus() == OrderConstant.ORDER_PAY_STATUS_SUBMITED) {
                // 判断更新未完成的支付订单
                updateNewPayLogStatus(pLog, o);
            }
            if (pLog.getPayStatus() == OrderConstant.ORDER_PAY_STATUS_SUBMITED) {
                closeWechatOrder(pLog);
                if (pLog.getPayStatus() == OrderConstant.ORDER_PAY_STATUS_SUBMITED) {
                    // 关闭订单失败
                    isRefund = false;
                }
            }
        }
        return isRefund;
    }

    /**
     * 退款
     *
     * @param id         订单id
     * @param refundType 退款方式 0 用户退款， 1 工程师退款，2 管理员退款
     * @throws WxErrorException
     * @author: lijx
     * @CreateDate: 2016-10-23 上午2:51:58
     */
    @Transactional
    public void payRefund(String id, int refundType, SessionUser su) throws WxErrorException {
        Order o = orderService.queryById(id);
        if (o == null) {
            throw new SystemException("订单不存在！");
        }
        if (su.getType() != SystemConstant.USER_TYPE_SYSTEM && su.getType() != SystemConstant.USER_TYPE_CUSTOMER_SERVICE
                && !su.getUserId().equals(o.getCustomerId()) && !su.getUserId().equals(o.getEngineerId())) {
            throw new SystemException("对不起，您无权操作该订单！");
        }
        if (o.getIsDrawback() == OrderConstant.ORDER_REFUND_STATUS_SUCCESS) {
            throw new SystemException("该订单已退款！");
        }

        boolean isRefund = true;
        // 查询支付订单
        List<OrderPayLog> payLogs = payLogService.queryByOrderNo(o.getOrderNo());
        if (payLogs == null || payLogs.isEmpty()) {
            return;
        }
        for (OrderPayLog pLog : payLogs) {
            if (pLog.getPayStatus() == OrderConstant.ORDER_PAY_STATUS_FAILD
                    || pLog.getPayStatus() == OrderConstant.ORDER_PAY_STATUS_SUBMITED) {
                // 判断更新未完成的支付订单
                updatePayLogStatus(pLog, o);
            }
            if (pLog.getPayStatus() == OrderConstant.ORDER_PAY_STATUS_SUCCESS) {
                // 获取已完成的支付订单
                // 生成退款订单
                OrderRefundLog refLog = createRefundLog(pLog, o);
                refLog.setRefundType(refundType);
                refundLogService.save(refLog);
                createWechatRefund(refLog);

                if (refLog.getRefundStatus() == OrderConstant.ORDER_REFUND_STATUS_SUCCESS) {
                    o.setDepositPrice(NumberUtils.add(o.getDrawbackPrice(), refLog.getRefundFee() / 100));
                } else {
                    // 关闭订单失败
                    isRefund = false;
                }
                orderService.saveUpdate(o);
            } else if (pLog.getPayStatus() == OrderConstant.ORDER_PAY_STATUS_SUBMITED) {
                closeWechatOrder(pLog);
                if (pLog.getPayStatus() == OrderConstant.ORDER_PAY_STATUS_SUBMITED) {
                    // 关闭订单失败
                    isRefund = false;
                }
            }
        }

        if (isRefund) {
            if (o.getOrderStatus() == 1 && o.getPayStatus() != OrderConstant.ORDER_PAY_STATUS_SUCCESS) {
                o.setIsDrawback(OrderConstant.ORDER_REFUND_STATUS_NOT);
            } else {
                o.setIsDrawback(OrderConstant.ORDER_REFUND_STATUS_SUCCESS);
            }
        } else {
            o.setIsDrawback(OrderConstant.ORDER_REFUND_STATUS_FAILD);
        }
        o.setDrawbackTime(new Date());
        orderService.saveUpdate(o);
        // 微信退款
        // Map<String, String> refundMap = new HashMap<>();
        // refundMap.put("transaction_id", "4001792001201610156755987446");
        // refundMap.put("out_trade_no", "PN-20161015041241148");
        // refundMap.put("out_refund_no", "RN-20161015041241148");
        // refundMap.put("total_fee", "1");
        // refundMap.put("refund_fee", "1");
        // refundMap.put("refund_account", "REFUND_SOURCE_RECHARGE_FUNDS");
        // WxMpPayRefundResult result =
        // wxMpService.getPayService().refundPay(refundMap);
        // log.info(result);
        // log.info("Return_code: " + result.getReturnCode());
        // log.info("Result_code: " + result.getResultCode());
        // log.info("Return_msg: " + result.getReturnMsg());
    }

    /**
     * 以旧换新退款
     *
     * @param id
     *            订单id
     * @param refundType
     *            退款方式 0 用户退款， 1 工程师退款，2 管理员退款
     */
    /*
     * @Transactional public void newPayRefund(String id, int refundType,
	 * SessionUser su) throws WxErrorException{ NewOrder o =
	 * (NewOrder)newOrderService.queryById(id); if(o == null){ throw new
	 * SystemException("订单不存在！"); } if(su.getType() !=
	 * SystemConstant.USER_TYPE_SYSTEM && su.getType() !=
	 * SystemConstant.USER_TYPE_CUSTOMER_SERVICE &&
	 * !su.getUserId().equals(o.getCustomerId()) &&
	 * !su.getUserId().equals(o.getEngineerId())){ throw new
	 * SystemException("对不起，您无权操作该订单！"); } if(o.getIsDrawback() ==
	 * OrderConstant.ORDER_REFUND_STATUS_SUCCESS){ throw new
	 * SystemException("该订单已退款！"); }
	 * 
	 * boolean isRefund = true; //查询支付订单 List<OrderPayLog> payLogs =
	 * payLogService.queryByOrderNo(o.getOrderNo()); if(payLogs == null ||
	 * payLogs.isEmpty()){ return; } for(OrderPayLog pLog : payLogs){
	 * if(pLog.getPayStatus() == OrderConstant.ORDER_PAY_STATUS_FAILD ||
	 * pLog.getPayStatus() == OrderConstant.ORDER_PAY_STATUS_SUBMITED){
	 * //判断更新未完成的支付订单 updatePayLogStatus(pLog, o); } if(pLog.getPayStatus() ==
	 * OrderConstant.ORDER_PAY_STATUS_SUCCESS){ //获取已完成的支付订单 //生成退款订单
	 * OrderRefundLog refLog = createRefundLog(pLog, o);
	 * refLog.setRefundType(refundType); refundLogService.save(refLog);
	 * createWechatRefund(refLog);
	 * 
	 * if(refLog.getRefundStatus() ==
	 * OrderConstant.ORDER_REFUND_STATUS_SUCCESS){
	 * o.setDepositPrice(NumberUtils.add(o.getDrawbackPrice(),
	 * refLog.getRefundFee()/100)); } else { //关闭订单失败 isRefund = false; }
	 * orderService.saveUpdate(o); } else if(pLog.getPayStatus() ==
	 * OrderConstant.ORDER_PAY_STATUS_SUBMITED){ closeWechatOrder(pLog);
	 * if(pLog.getPayStatus() == OrderConstant.ORDER_PAY_STATUS_SUBMITED){
	 * //关闭订单失败 isRefund = false; } } }
	 * 
	 * if(isRefund){ if(o.getOrderStatus() == 1 && o.getPayStatus() !=
	 * OrderConstant.ORDER_PAY_STATUS_SUCCESS){
	 * o.setIsDrawback(OrderConstant.ORDER_REFUND_STATUS_NOT); } else {
	 * o.setIsDrawback(OrderConstant.ORDER_REFUND_STATUS_SUCCESS); } } else{
	 * o.setIsDrawback(OrderConstant.ORDER_REFUND_STATUS_FAILD); }
	 * o.setDrawbackTime(new Date()); orderService.saveUpdate(o);
	 * 
	 * }
	 */


    /**
     * 碎屏险订单退款
     * true 表示退款申请成功
     */
    @Transactional
    public OrderRefundLog screenPayRefund(ScreenOrder o, SessionUser su) throws WxErrorException {
        if (su.getType() != SystemConstant.USER_TYPE_SYSTEM && su.getType() != SystemConstant.USER_TYPE_CUSTOMER_SERVICE) {
            throw new SystemException("对不起，您无权操作该订单！");
        }
        if (o.getIsDrawback() == OrderConstant.ORDER_REFUND_STATUS_SUCCESS) {
            throw new SystemException("该订单已退款！");
        }
        boolean isRefund = true;
        OrderRefundLog refLog = null;   //实际退款的订单信息
        // 查询支付订单
        List<OrderPayLog> payLogs = payLogService.queryByOrderNo(o.getOrderNo());
        if (payLogs == null || payLogs.isEmpty()) {
            throw new SystemException("该订单没有支付信息!");
        }
        for (OrderPayLog pLog : payLogs) {
            if (pLog.getPayStatus() == OrderConstant.ORDER_PAY_STATUS_FAILD
                    || pLog.getPayStatus() == OrderConstant.ORDER_PAY_STATUS_SUBMITED) {
                // 判断更新未完成的支付订单
                screenUpdatePayLogStatus(pLog, o);
            }
            if (pLog.getPayStatus() == OrderConstant.ORDER_PAY_STATUS_SUCCESS) {
                // 生成退款订单
                refLog = screenCreateRefundLog(pLog, o);
                refLog.setRefundType(2);                 //管理员退款
                refundLogService.save(refLog);           //保存退款订单信息
                refLog = createWechatRefund(refLog);       //向微信服务器发起退款  得到退款订单结果

                if (refLog.getRefundStatus() == OrderConstant.ORDER_REFUND_STATUS_SUCCESS) {
                    //退款提交成功
                } else {
                    // 关闭订单失败
                    isRefund = false;
                }
            } else if (pLog.getPayStatus() == OrderConstant.ORDER_PAY_STATUS_SUBMITED) {
                closeWechatOrder(pLog);
                if (pLog.getPayStatus() == OrderConstant.ORDER_PAY_STATUS_SUBMITED) {
                    // 关闭订单失败
                    isRefund = false;
                }
            }
        }

        if (isRefund) {
            //退款成功  更改订单状态
            o.setIsDrawback(OrderConstant.ORDER_REFUND_STATUS_SUCCESS);    //退款提交成功
            o.setOrderStatus(OrderConstant.SCREEN_ORDER_REFUNDING);        //订单状态   退款中
        } else {
            o.setIsDrawback(OrderConstant.ORDER_REFUND_STATUS_FAILD);
        }
        o.setRefundTime(new Date());                                           //发起退款的时间
        screenOrderService.saveUpdate(o);
        return refLog;
    }


    /**
     * 查询退款订单状态
     * true 表示退款到账成功
     */
    @Transactional
    public ScreenOrder queryPayRefund(ScreenOrder o, OrderRefundLog refLog) throws WxErrorException {
        o = queryWechatRefund(refLog, o);       //向微信服务器发起查询退款请求
        return o;
    }


    /**
     * 调用微信接口下单
     *
     * @author: lijx
     * @CreateDate: 2016-10-13 下午11:16:47
     */
    private OrderRefundLog createWechatRefund(OrderRefundLog refLog) {
        // 微信下单
        Map<String, String> refundMap = getMapByRefundLog(refLog);
        WxMpPayRefundResult result = null;
        try {
            result = wxMpService.getPayService().refundPay(refundMap);
        } catch (WxErrorException e) {
            e.printStackTrace();
            refLog.setRefundStatus(OrderConstant.ORDER_REFUND_STATUS_FAILD);
            refLog.setErrCodeDes("微信接口异常");
            refLog.setRepeatNumber(refLog.getRepeatNumber() + 1);
            // 更新支付状态
            refundLogService.update(refLog);
            throw new SystemException("调用微信接口异常！");
        }
        if (result == null) {
            refLog.setRefundStatus(OrderConstant.ORDER_REFUND_STATUS_FAILD);
            refLog.setErrCodeDes("微信接口返回空");
            refLog.setRepeatNumber(refLog.getRepeatNumber() + 1);
            // 更新支付状态
            refundLogService.update(refLog);
            throw new SystemException("调用微信接口异常！");
        }
        log.info(result);
        if ("SUCCESS".equals(result.getResultCode())) {
            refLog.setRefundId(result.getRefundId());
            refLog.setRefundStatus(OrderConstant.ORDER_REFUND_STATUS_SUCCESS);
            // 更新支付状态
            refundLogService.update(refLog);
        } else {
            refLog.setRefundStatus(OrderConstant.ORDER_REFUND_STATUS_FAILD);
            refLog.setErrCode(result.getErrCode());
            refLog.setErrCodeDes(result.getErrCodeDes());
            refLog.setRepeatNumber(refLog.getRepeatNumber() + 1);
            // 更新支付状态
            refundLogService.update(refLog);
        }
        return refLog;
    }


    /**
     * 调用微信接口查询退款结果  针对碎屏险
     */
    private ScreenOrder queryWechatRefund(OrderRefundLog refLog, ScreenOrder o) {
        // 微信下单
        Map<String, String> refundMap = queryMapByRefundLog(refLog);
        WxMpPayQueryRefundState result = null;
        try {
            result = wxMpService.getPayService().queryRefundPay(refundMap);
        } catch (WxErrorException e) {
            e.printStackTrace();
            refLog.setErrCodeDes("微信接口异常");
            // 更新支付状态
            refundLogService.update(refLog);
            throw new SystemException("调用微信接口异常！");
        }
        if (result == null) {
            refLog.setRefundStatus(OrderConstant.ORDER_REFUND_STATUS_FAILD);
            refLog.setErrCodeDes("微信接口返回空");
            // 更新支付状态
            refundLogService.update(refLog);
            throw new SystemException("调用微信接口异常！");
        }
        log.info(result);
        if ("SUCCESS".equals(result.getResultCode())) {
            //查询结果成功 再判断退款结果
            if ("SUCCESS".equals(result.getRefundStatus())) {
                o.setOrderStatus(OrderConstant.SCREEN_ORDER_REFUNDED);
                o.setRefundResult("退款成功");
            } else if ("PROCESSING".equals(result.getRefundStatus())) {
                o.setRefundResult("退款处理中");
            } else if ("REFUNDCLOSE".equals(result.getRefundStatus())) {
                o.setRefundResult("退款关闭");
            } else if ("CHANGE".equals(result.getRefundStatus())) {
                o.setRefundResult("退款异常");
            }

        } else {
            refLog.setErrCode(result.getErrCode());
            refLog.setErrCodeDes(result.getErrCodeDes());
            o.setRefundResult(result.getErrCodeDes());
            // 更新订单状态
            refundLogService.update(refLog);
        }
        //更新碎屏险订单状态
        screenOrderService.saveUpdate(o);
        return o;
    }

    /**
     * 生成退款信息
     *
     * @return
     * @author: lijx
     * @CreateDate: 2016-10-7 下午6:41:59
     */
    public Map<String, String> getMapByRefundLog(OrderRefundLog refLog) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("transaction_id", refLog.getTransactionId());
        map.put("out_trade_no", refLog.getPayOrderNo());
        map.put("out_refund_no", refLog.getPayRefundNo());
        map.put("total_fee", refLog.getTotalFee() + "");
        map.put("refund_fee", refLog.getRefundFee() + "");
        map.put("refund_account", refLog.getRefundAccount());
        return map;
    }


    /**
     * 生成查询退款订单的信息
     */
    public Map<String, String> queryMapByRefundLog(OrderRefundLog refLog) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("out_trade_no", refLog.getPayOrderNo());
        map.put("out_refund_no", refLog.getPayRefundNo());
        return map;
    }


    /**
     * 生成支付订单
     *
     * @param payLog
     * @param o
     * @return
     * @author: lijx
     * @CreateDate: 2016-10-13 下午10:42:23
     */
    public OrderRefundLog createRefundLog(OrderPayLog payLog, Order o) {
        OrderRefundLog refLog = new OrderRefundLog();
        // 生成支付订单号
        String payRefundNo = NOUtil.getNo("RN-");
        refLog.setPayRefundNo(payRefundNo);

        refLog.setOrderNo(o.getOrderNo());
        refLog.setPayOrderNo(payLog.getPayOrderNo());
        refLog.setTransactionId(payLog.getTransactionId());
        refLog.setTotalFee(payLog.getTotalFee());
        refLog.setRefundFee(payLog.getTotalFee());
        refLog.setExpenseType(payLog.getExpenseType());

        // 设置退款方式
        try {
            if (DateUtil.getDateyyyyMMdd000000_Date().compareTo(o.getDepositTime()) < 0) {
                // 如果是当天付款使用未结算金额退款
                refLog.setRefundAccount("REFUND_SOURCE_UNSETTLED_FUNDS");
            } else {
                // 否则使用余额退款
                refLog.setRefundAccount("REFUND_SOURCE_RECHARGE_FUNDS");
            }

        } catch (ParseException e) {
            e.printStackTrace();
            refLog.setRefundAccount("REFUND_SOURCE_RECHARGE_FUNDS");
        }
        refLog.setCustomerId(o.getCustomerId());
        refLog.setCustomerMobile(o.getMobile());
        refLog.setCustomerName(o.getCustomerName());

        refLog.setRefundStatus(1);
        refLog.setFeeType("CNY");
        refLog.setRepeatNumber(0);

        return refLog;
    }


    /**
     * 生成碎屏险退款订单
     */
    public OrderRefundLog screenCreateRefundLog(OrderPayLog payLog, ScreenOrder o) {
        OrderRefundLog refLog = new OrderRefundLog();
        // 生成支付订单号
        String payRefundNo = NOUtil.getNo("RN-");
        refLog.setPayRefundNo(payRefundNo);

        refLog.setOrderNo(o.getOrderNo());
        refLog.setPayOrderNo(payLog.getPayOrderNo());
        refLog.setTransactionId(payLog.getTransactionId());
        refLog.setTotalFee(payLog.getTotalFee());
        refLog.setRefundFee(payLog.getTotalFee());
        refLog.setExpenseType(payLog.getExpenseType());

        // 使用余额退款
        refLog.setRefundAccount("REFUND_SOURCE_RECHARGE_FUNDS");
        refLog.setCustomerMobile(o.getMobile());
        refLog.setCustomerName(o.getName());
        refLog.setRefundStatus(1);
        refLog.setFeeType("CNY");
        refLog.setRepeatNumber(0);

        return refLog;
    }


    /**
     * 微信支付结果通知接口 1、验证参数 2、获取订单、支付订单日志 3、判断是否已经处理 4、判断订单状态是否一致 5、保存支付结果
     *
     * @author: lijx
     * @CreateDate: 2016-10-12 下午10:22:54 2017 6-22修改
     */
    @Transactional
    public void payCallback(Map<String, String> resultMap) {
        // 获取支付订单号
        String outTradeNo = resultMap.get("out_trade_no");
        // 查询支付订单
        OrderPayLog payLog = payLogService.queryByPayOrderNo(outTradeNo);
        if (payLog == null) {
            throw new SystemException("支付日志不存在！");
        }
        // 查询订单
        Order o = orderService.queryByOrderNo(payLog.getOrderNo());
        NewOrder newOrder = newOrderService.queryByOrderNo(payLog.getOrderNo());
        ScreenOrder screen = screenOrderService.queryByOrderNo(payLog.getOrderNo());
        if (o == null && newOrder == null && screen == null) {
            throw new SystemException("订单未找到！");
        }


        // 支付结果未处理
        if (payLog.getPayStatus() == OrderConstant.ORDER_PAY_STATUS_SUBMITED) {
            // 处理支付结果
            if ("SUCCESS".equals(resultMap.get("result_code"))) {
                // 支付成功
                payLog.setPayStatus(OrderConstant.ORDER_PAY_STATUS_SUCCESS);

                if (o != null) {
                    o.setPayType(0);
                    paySuccess(payLog, o);                // 维修订单更新订单状态
                } else if (newOrder != null) {
                    newPaySuccess(payLog, newOrder);      // 依旧换新订单更新订单状态
                } else {
                    screenPaySuccess(screen);      // 碎屏险订单更新订单状态
                }

                // 保存支付信息
                payLog.setOpenid(resultMap.get("openid"));
                payLog.setIsSubscribe(resultMap.get("is_subscribe"));
                payLog.setBankType(resultMap.get("bank_type"));
                payLog.setTransactionId(resultMap.get("transaction_id"));
                payLog.setAttach(resultMap.get("attach"));
                payLog.setTimeEnd(resultMap.get("time_end"));
                payLogService.update(payLog);

                //微信支付成功，
                //关闭支付宝订单
                AliPayService aliPayService=new AliPayServiceImpl();
                aliPayService.alipayCloseOrder(outTradeNo);
                //修改支付宝预订单为完成
                OrderPayLog payLog1 = new OrderPayLog();
                payLog1.setOrderNo(payLog.getOrderNo());
                payLog1.setPayMethod(1);
                List<OrderPayLog> payLogs = payLogService.queryList(payLog1);
                if (!CollectionUtils.isEmpty(payLogs)) {
                    payLogs.get(0).setPayStatus(OrderConstant.ORDER_PAY_STATUS_COMPLETE);
                    payLogService.update(payLogs.get(0));
                }
            } else {
                // 支付失败
                payLog.setPayStatus(OrderConstant.ORDER_PAY_STATUS_FAIL);
                payLog.setErrCode(resultMap.get("err_code"));
                payLog.setErrCodeDes(resultMap.get("err_code_des"));
                payLogService.update(payLog);
                // 判断是否支付保证金还是余额
                o.setPayStatus(OrderConstant.SCREEN_ORDER_ERROR);
                orderService.saveUpdate(o);
            }
        }
    }

    /**
     * 更新订单状态 状态更新后的状态结果：支付成功、已超时、提交、待提交 注意对支付状态为成功状态的支付订单不进行更新
     *
     * @param payLog
     * @author: lijx
     * @CreateDate: 2016-10-23 下午10:21:48
     */
    @Transactional
    public void updatePayLogStatus(OrderPayLog payLog, Order o) {
        if (payLog.getPayStatus() == OrderConstant.ORDER_PAY_STATUS_SUCCESS) {
            return;
        }
        // 通过接口查询微信端订单状态
        WxMpPayResult payResult = wxMpService.getPayService().getJSSDKPayResult(null, payLog.getPayOrderNo());
        if (payResult != null && "SUCCESS".equals(payResult.getReturn_code())) {
            if ("SUCCESS".equals(payResult.getResult_code())) {
                // 判断订单状态
                if ("SUCCESS".equals(payResult.getTrade_state())) {
                    // 支付成功
                    payLog.setPayStatus(OrderConstant.ORDER_PAY_STATUS_SUCCESS);
                    // 更新订单状态
                    o.setPayType(0);
                    paySuccess(payLog, o);

                    // 保存支付信息
                    payLog.setOpenid(payResult.getOpenid());
                    payLog.setIsSubscribe(payResult.getIs_subscribe());
                    payLog.setBankType(payResult.getBank_type());
                    payLog.setTransactionId(payResult.getTransaction_id());
                    payLog.setAttach(payResult.getAttach());
                    payLog.setTimeEnd(payResult.getTime_end());
                    payLogService.update(payLog);
                } else if ("CLOSED".equals(payResult.getTrade_state())
                        || "REVOKED".equals(payResult.getTrade_state())) {
                    // 订单已取消重新生成支付订单
                    payLog.setPayStatus(OrderConstant.ORDER_PAY_STATUS_COMPLETE);
                    payLogService.update(payLog);
                } else if ("REFUND".equals(payResult.getTrade_state())) {
                    // 已退款
                    payLog.setPayStatus(OrderConstant.ORDER_PAY_STATUS_SUCCESS);
                    payLog.setIsDrawback(OrderConstant.ORDER_REFUND_STATUS_SUCCESS);
                    payLogService.update(payLog);
                } else {
                    if (payLog.getPayStatus() == OrderConstant.ORDER_PAY_STATUS_SUBMITED) {
                        //先判断预支付交易会话标识  prepay_id(两小时以内是否过期)
                        String expireTime = payLog.getTimeExpire();     //支付订单的过期时间
                        String now = DateUtil.getSerialFullDate(Calendar.getInstance().getTime());
                        //如果expireTime<now说明在支付标识已经无效了
                        if (StringUtil.isNotBlank(expireTime) && Long.parseLong(expireTime) < Long.parseLong(now)) {
                            payLog.setPayStatus(OrderConstant.ORDER_PAY_STATUS_COMPLETE);
                            payLogService.update(payLog);
                        }
                    } else {
                        payLog.setPayStatus(OrderConstant.ORDER_PAY_STATUS_COMPLETE);
                        payLogService.update(payLog);
                    }

                }
            } else {
                if ("ORDERNOTEXIST".equals(payResult.getErr_code())) {
                    payLog.setPayStatus(OrderConstant.ORDER_PAY_STATUS_WAIT);
                    payLogService.update(payLog);
                }
            }
        } else {
            throw new SystemException("系统异常请稍后再试！");
        }
    }

    /**
     * 更新以旧换新订单状态 状态更新后的状态结果：支付成功、已超时、提交、待提交 注意对支付状态为成功状态的支付订单不进行更新
     *
     * @CreateDate: 2017-06-22 下午10:21:48
     */
    @Transactional
    public void updateNewPayLogStatus(OrderPayLog payLog, NewOrder o) {
        if (payLog.getPayStatus() == OrderConstant.ORDER_PAY_STATUS_SUCCESS) {
            return;
        }
        // 通过接口查询微信端订单状态
        WxMpPayResult payResult = wxMpService.getPayService().getJSSDKPayResult(null, payLog.getPayOrderNo());
        if (payResult != null && "SUCCESS".equals(payResult.getReturn_code())) {
            if ("SUCCESS".equals(payResult.getResult_code())) {
                // 判断订单状态
                if ("SUCCESS".equals(payResult.getTrade_state())) {
                    // 支付成功
                    payLog.setPayStatus(OrderConstant.ORDER_PAY_STATUS_SUCCESS);
                    // 更新订单状态
                    newPaySuccess(payLog, o);

                    // 保存支付信息
                    payLog.setOpenid(payResult.getOpenid());
                    payLog.setIsSubscribe(payResult.getIs_subscribe());
                    payLog.setBankType(payResult.getBank_type());
                    payLog.setTransactionId(payResult.getTransaction_id());
                    payLog.setAttach(payResult.getAttach());
                    payLog.setTimeEnd(payResult.getTime_end());
                    payLogService.update(payLog);
                } else if ("CLOSED".equals(payResult.getTrade_state())
                        || "REVOKED".equals(payResult.getTrade_state())) {
                    // 订单已取消重新生成支付订单
                    payLog.setPayStatus(OrderConstant.ORDER_PAY_STATUS_COMPLETE);
                    payLogService.update(payLog);
                } else if ("REFUND".equals(payResult.getTrade_state())) {
                    // 已退款
                    payLog.setPayStatus(OrderConstant.ORDER_PAY_STATUS_SUCCESS);
                    payLog.setIsDrawback(OrderConstant.ORDER_REFUND_STATUS_SUCCESS);
                    payLogService.update(payLog);
                } else {
                    if (payLog.getPayStatus() == OrderConstant.ORDER_PAY_STATUS_SUBMITED) {
                        //先判断预支付交易会话标识  prepay_id(两小时以内是否过期)
                        String expireTime = payLog.getTimeExpire();     //支付订单的过期时间
                        String now = DateUtil.getSerialFullDate(Calendar.getInstance().getTime());
                        //如果expireTime<now说明在支付标识已经无效了
                        if (StringUtil.isNotBlank(expireTime) && Long.parseLong(expireTime) < Long.parseLong(now)) {
                            payLog.setPayStatus(OrderConstant.ORDER_PAY_STATUS_COMPLETE);
                            payLogService.update(payLog);
                        }
                    } else {
                        payLog.setPayStatus(OrderConstant.ORDER_PAY_STATUS_COMPLETE);
                        payLogService.update(payLog);
                    }

                }
            } else {
                if ("ORDERNOTEXIST".equals(payResult.getErr_code())) {
                    payLog.setPayStatus(OrderConstant.ORDER_PAY_STATUS_WAIT);
                    payLogService.update(payLog);
                }
            }
        } else {
            throw new SystemException("系统异常请稍后再试！");
        }
    }

    /**
     * 更新订单状态 针对碎屏险订单 状态更新后的状态结果：支付成功、已超时、提交、待提交 注意对支付状态为成功状态的支付订单不进行更新
     */
    @Transactional
    public void screenUpdatePayLogStatus(OrderPayLog payLog, ScreenOrder o) {
        if (payLog.getPayStatus() == OrderConstant.ORDER_PAY_STATUS_SUCCESS) {
            return;
        }
        // 通过接口查询微信端订单状态
        WxMpPayResult payResult = wxMpService.getPayService().getJSSDKPayResult(null, payLog.getPayOrderNo());
        if (payResult != null && "SUCCESS".equals(payResult.getReturn_code())) {
            if ("SUCCESS".equals(payResult.getResult_code())) {
                // 判断订单状态
                if ("SUCCESS".equals(payResult.getTrade_state())) {
                    // 支付成功
                    payLog.setPayStatus(OrderConstant.ORDER_PAY_STATUS_SUCCESS);
                    // 更新订单状态
                    screenPaySuccess(o);

                    // 保存支付信息
                    payLog.setOpenid(payResult.getOpenid());
                    payLog.setIsSubscribe(payResult.getIs_subscribe());
                    payLog.setBankType(payResult.getBank_type());
                    payLog.setTransactionId(payResult.getTransaction_id());
                    payLog.setAttach(payResult.getAttach());
                    payLog.setTimeEnd(payResult.getTime_end());
                    payLogService.update(payLog);
                } else if ("CLOSED".equals(payResult.getTrade_state())
                        || "REVOKED".equals(payResult.getTrade_state())) {
                    // 订单已取消重新生成支付订单
                    payLog.setPayStatus(OrderConstant.ORDER_PAY_STATUS_COMPLETE);
                    payLogService.update(payLog);
                } else if ("REFUND".equals(payResult.getTrade_state())) {
                    // 已退款
                    payLog.setPayStatus(OrderConstant.ORDER_PAY_STATUS_SUCCESS);
                    payLog.setIsDrawback(OrderConstant.ORDER_REFUND_STATUS_SUCCESS);
                    payLogService.update(payLog);
                } else {
                    if (payLog.getPayStatus() == OrderConstant.ORDER_PAY_STATUS_SUBMITED) {
                        //先判断预支付交易会话标识  prepay_id(两小时以内是否过期)
                        String expireTime = payLog.getTimeExpire();     //支付订单的过期时间
                        String now = DateUtil.getSerialFullDate(Calendar.getInstance().getTime());
                        //如果expireTime<now说明在支付标识已经无效了
                        if (StringUtil.isNotBlank(expireTime) && Long.parseLong(expireTime) < Long.parseLong(now)) {
                            payLog.setPayStatus(OrderConstant.ORDER_PAY_STATUS_COMPLETE);
                            payLogService.update(payLog);
                        }
                    } else {
                        payLog.setPayStatus(OrderConstant.ORDER_PAY_STATUS_COMPLETE);
                        payLogService.update(payLog);
                    }

                }
            } else {
                if ("ORDERNOTEXIST".equals(payResult.getErr_code())) {
                    payLog.setPayStatus(OrderConstant.ORDER_PAY_STATUS_WAIT);
                    payLogService.update(payLog);
                }
            }
        } else {
            throw new SystemException("系统异常请稍后再试！");
        }
    }

    /**
     * 支付成功修改订单状态 支付余额
     *
     * @param payLog
     * @param o
     * @author: lijx
     * @CreateDate: 2016-10-24 上午12:34:05
     */
    public void paySuccess(OrderPayLog payLog, Order o) {
        //o.setPayType(0);//微信支付
        o.setPayStatus(OrderConstant.ORDER_PAY_STATUS_SUCCESS);
        o.setPayTime(new Date());
        try {
            // 如果订单状态是已取消则不修改订单
            if (o.getOrderStatus() != OrderConstant.ORDER_STATUS_CANCEL) {
                // o.setOrderStatus(OrderConstant.ORDER_STATUS_END_PAY);
                // //判断是否为返店维修如果是则释放工程师
                // if(o.getRepairType() == 2){
                // //将原工程师状态改为空闲
                // Engineer eng = engineerService.queryById(o.getEngineerId());
                // eng.setIsDispatch(0);
                // engineerService.saveUpdate(eng);
                // }
                // 如果是寄修订单 支付完成后订单状态改为正在维修，其他订单则改为已完成
                if (o.getRepairType() == OrderConstant.SEND_TO_SHOP_REPAIR) {
                    o.setOrderStatus(OrderConstant.ORDER_STATUS_REPAIRING);
                } else {
                    o.setOrderStatus(OrderConstant.ORDER_STATUS_FINISHED);
                    // 如果该订单是店员创建，完成订单后则为该店员增加对应积分
                    getIntegralService.addIntegral(o);
                }

                o.setEndTime(new Date());
            }
            orderService.saveUpdate(o);
            // 判断是否为返店维修如果是则释放工程师
            if (o.getRepairType() == 2) {
                // 将原工程师状态改为空闲
                // Engineer eng = engineerService.queryById(o.getEngineerId());
                // if(eng.getIsDispatch() == 1){
                // eng.setIsDispatch(0);
                // engineerService.saveUpdate(eng);
                // }
                engineerService.checkDispatchState(o.getEngineerId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 以旧换新支付成功修改订单状态
     *
     * @CreateDate: 2017-06-22 上午12:34:05
     */
    private void newPaySuccess(OrderPayLog payLog, NewOrder o) {
        // 支付余额
        try {
            // 如果订单状态是已取消则不修改订单
            if (o.getOrderStatus() != OrderConstant.ORDER_STATUS_CANCEL) {
                // 将订单状态改为已完成
                o.setOrderStatus(OrderConstant.ORDER_STATUS_FINISHED);
                o.setEndTime(new Date());
                // 将支付信息赋入kx_oldtonew_pay表中
                NewOrderPay newOrderPay = newOrderPayService.queryByOrderNo(o.getOrderNo());
                newOrderPay.setPayType(0);// 微信支付
                newOrderPay.setPayTime(payLog.getPayTime());
                newOrderPay.setPayStatus(payLog.getPayStatus());
                newOrderPay.setIsDrawback(payLog.getIsDrawback());
                // 将int型以分为单位的付款费用转化
                BigDecimal c = new BigDecimal(payLog.getTotalFee());
                BigDecimal b = new BigDecimal(100);
                BigDecimal big = c.divide(b, 2, RoundingMode.HALF_UP);
                newOrderPay.setPayPrice(big);
                newOrderPayService.saveUpdate(newOrderPay);
            }
            newOrderService.saveUpdate(o);
            engineerService.checkDispatchState(o.getEngineerId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 支付成功修改订单状态 支付余额 针对碎屏险订单
     */
    public void screenPaySuccess(ScreenOrder o) {
        o.setIsPayment(OrderConstant.SCREEN_ORDER_IS_PAYMENT);  // 将支付状态改为已付款
        screenOrderService.saveUpdate(o);                       //支付成功 更改订单状态
        //   支付成功向碎屏险商发送订单信息
        String url = SystemConstant.SCREEN_URL;   //碎屏险商调用地址
        String headerValue = "Basic " + Base64Util.getBase64(SystemConstant.SCREEN_USERNAME + ":" + SystemConstant.SCREEN_PASSWORD);
        ScreenProject project = screenProjectService.queryById(o.getProjectId());
        if (project == null) {
            throw new SystemException("产品代码不存在！");
        }
        JSONArray j = new JSONArray();
        JSONObject params = new JSONObject();
        params.put("card_id", o.getOrderNo());
        params.put("product", project.getProductCode());
        params.put("mobile", o.getMobile());
        params.put("username", o.getName());
        j.add(params);
        //开始调用碎屏险商接口发送订单信息   并处理返回
        String result = sendPost(url, j, headerValue);
        //解析返回结果    解析方法参考碎屏险的文档接口
        JSONObject json = JSONObject.parseObject(result);
        log.info("接口返回结果：" + json);
        JSONObject message = json.getJSONObject("Exception");
        if (message != null) {
            JSONArray data = message.getJSONArray("data");
            if (data != null && data.isEmpty()) {
                //调用接口下单成功  更改订单状态
                o.setOrderStatus(OrderConstant.SCREEN_ORDER_SUCCESS);       //订单状态改为提交成功
                o.setEndTime(new Date());
                screenOrderService.saveUpdate(o);
                //订单提交成功后 向用户发送提醒短信
                ScreenProject p = screenProjectService.queryById(o.getProjectId());
                int month = 12;
                if (p != null) {
                    month = p.getMaxTime();
                }
                SmsSendUtil.sendSmsToScreenUser(o, month);
            } else {
                //先判断是否是用户名密码 或链接等服务器错误
                //下单失败 将该订单作废 下单人以旧可以继续下单
                o.setIsDel(2);  //已付款  调用碎屏险下单接口失败
                screenOrderService.saveUpdate(o);

                String code = message.getString("code");
                String errorNews = "";    //接口错误信息
                if (code != null) {
                    //接口配置错误
                    errorNews = message.getString("Message");
                } else {
                    //提交信息错误  获取错误信息
                    JSONArray m = message.getJSONArray("Message");
                    if (m != null && !m.isEmpty()) {
                        errorNews = m.getString(0);
                    }
                }
                o.setOrderStatus(OrderConstant.SCREEN_ORDER_ERROR);        //订单状态改为提交失败
                screenOrderService.saveUpdate(o);
                throw new SystemException(errorNews);
            }
        } else {
            throw new SystemException("接口返回异常");
        }
    }


    public static void main(String[] args) {
        String url = SystemConstant.SCREEN_URL;   //调用地址
        //请求头增加    "Basic " + Base64.encode("用户名:密码")
        String headerValue = "Basic " + Base64Util.getBase64(SystemConstant.SCREEN_USERNAME + ":" + SystemConstant.SCREEN_PASSWORD);
        //发送的订单信息
        JSONArray j = new JSONArray();
        JSONObject params = new JSONObject();
        params.put("card_id", "1");
        params.put("product", "J01");
        params.put("mobile", "18019031119");
        params.put("username", "测试");
        j.add(params);
        //开始调用碎屏险商接口发送订单信息   并处理返回
        String result = sendPost(url, j, headerValue);
        //解析返回结果
        JSONObject json = JSONObject.parseObject(result);
        log.info("返回结果：" + json);
        JSONObject message = json.getJSONObject("Exception");
        if (message != null) {
            JSONArray data = message.getJSONArray("data");
            //log.info("返回data："+data+"data 长度："+data.size());
            if (data != null && data.isEmpty()) {
                log.info("返回为空，订单正确");
            } else {
                String code = message.getString("code");
                String errorNews = "";    //接口错误信息
                if (code != null) {
                    //接口配置错误
                    errorNews = message.getString("Message");
                } else {
                    //提交信息错误  获取错误信息
                    JSONArray m = message.getJSONArray("Message");
                    if (m != null && !m.isEmpty()) {
                        errorNews = m.getString(0);
                    }
                }
                log.info("错误信息：" + errorNews);
            }
        } else {
            throw new SystemException("接口返回异常");
        }


    }

    /**
     * 发送post请求   针对碎屏险
     *
     * @param url
     * @return
     */
    public static String sendPost(String url, JSONArray param, String headerValue) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*application/json*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestProperty(SystemConstant.SCREEN_HEADER, headerValue);
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            log.info("发送 POST 请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }


    /**
     * 支付宝查询修改状态
     *
     * @param payLog
     * @author: najy
     * @CreateDate: 2016-10-23 下午10:21:48
     */
    public void aliPayOrder(OrderPayLog payLog) {
        AliPayService aliPayService=new AliPayServiceImpl();
        AlipayTradeQueryResponse result = aliPayService.alipayQueryOrder(payLog.getPayOrderNo());
        if (result.isSuccess()) {
            // 判断订单状态
            if ("TRADE_SUCCESS".equals(result.getTradeStatus())) {
                log.info(payLog.getOrderNo() + "=该订单已支付成功");
                Order o = orderService.queryByOrderNo(payLog.getOrderNo());
                // 支付成功
                payLog.setPayStatus(OrderConstant.ORDER_PAY_STATUS_SUCCESS);
                // 更新订单状态
                o.setPayType(1);//支付宝支付
                paySuccess(payLog, o);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                // 保存支付信息
                //去掉时间字符串空格，横线，冒号
                String endTime = sdf.format(result.getSendPayDate()).replace(" ", "").replace("-", "").replace(":", "");
                payLog.setTimeEnd(endTime);
                payLog.setTransactionId(result.getTradeNo());
                payLog.setBuyerLogonId(result.getBuyerLogonId());
                List<TradeFundBill> tradeFundBills = result.getFundBillList();
                for (TradeFundBill fundBill : tradeFundBills) {
                    payLog.setFundChannel(fundBill.getFundChannel());
                }
                payLogService.update(payLog);

                //支付宝支付成功，关闭微信预支付订单
                OrderPayLog payLog1 = new OrderPayLog();
                payLog1.setOrderNo(o.getOrderNo());
                payLog1.setPayMethod(0);
                OrderPayLog orderPayLog = payLogService.queryAppUnFinishedByOrderNo(payLog1);
                // 通过接口查询微信端订单状态
                WxMpPayResult payResult = wxMpService.getPayService().getJSSDKPayResult(null, payLog.getPayOrderNo());
                if (payResult != null && "SUCCESS".equals(payResult.getReturn_code())) {
                    if ("SUCCESS".equals(payResult.getResult_code())) {
                        closeWechatOrder(orderPayLog);
                    }
                }
            } else if ("TRADE_CLOSED".equals(result.getTradeStatus())) {
                // 订单已超时已关闭，或全额退款
                log.info(payLog.getOrderNo() + "=该订单已超时已关闭，或全额退款");
                payLog.setPayStatus(OrderConstant.ORDER_PAY_STATUS_FAIL);
                payLogService.update(payLog);
            }
        }
//        else if ("ACQ.TRADE_NOT_EXIST".equals(result.getSubCode())) {
//            // 订单不存在
//            payLog.setPayStatus(OrderConstant.ORDER_PAY_STATUS_COMPLETE);
//            payLogService.update(payLog);
//        } else {
//            throw new SystemException("系统异常请稍后再试！");
//        }
    }

    public void orderPayLog(OrderPayLog payLog) {
        // 通过接口查询微信端订单状态
        WxMpPayResult payResult = wxMpService.getPayService().getJSSDKPayResult(null, payLog.getPayOrderNo());
        if (payResult != null && "SUCCESS".equals(payResult.getReturn_code())) {
            if ("SUCCESS".equals(payResult.getResult_code())) {
                // 判断订单状态
                if ("SUCCESS".equals(payResult.getTrade_state())) {
                    log.info(payLog.getOrderNo() + "=该订单已支付成功");
                    Order o = orderService.queryByOrderNo(payLog.getOrderNo());
                    // 支付成功
                    payLog.setPayStatus(OrderConstant.ORDER_PAY_STATUS_SUCCESS);
                    // 更新订单状态
                    o.setPayType(0);
                    paySuccess(payLog, o);

                    // 保存支付信息
                    payLog.setOpenid(payResult.getOpenid());
                    payLog.setIsSubscribe(payResult.getIs_subscribe());
                    payLog.setBankType(payResult.getBank_type());
                    payLog.setTransactionId(payResult.getTransaction_id());
                    payLog.setAttach(payResult.getAttach());
                    payLog.setTimeEnd(payResult.getTime_end());
                    payLogService.update(payLog);

                    //微信支付成功，
                    //关闭支付宝订单
                    AliPayService aliPayService=new AliPayServiceImpl();
                    aliPayService.alipayCloseOrder(payLog.getPayOrderNo());
                    //修改支付宝预订单为完成
                    OrderPayLog payLog1 = new OrderPayLog();
                    payLog1.setOrderNo(payLog.getOrderNo());
                    payLog1.setPayMethod(1);
                    List<OrderPayLog> payLogs = payLogService.queryList(payLog1);
                    if (!CollectionUtils.isEmpty(payLogs)) {
                        payLogs.get(0).setPayStatus(OrderConstant.ORDER_PAY_STATUS_COMPLETE);
                        payLogService.update(payLogs.get(0));
                    }
                } else if ("CLOSED".equals(payResult.getTrade_state())
                        || "REVOKED".equals(payResult.getTrade_state())) {
                    // 订单已取消支付失敗
                    log.info(payLog.getOrderNo() + "=该订单已取消或关闭");
                    payLog.setPayStatus(OrderConstant.ORDER_PAY_STATUS_FAIL);
                    payLogService.update(payLog);
                } else if ("REFUND".equals(payResult.getTrade_state())) {
                    // 已退款
                    log.info(payLog.getOrderNo() + "=该订单已退款");
                    payLog.setPayStatus(OrderConstant.ORDER_PAY_STATUS_SUCCESS);
                    payLog.setIsDrawback(OrderConstant.ORDER_REFUND_STATUS_SUCCESS);
                    payLogService.update(payLog);
                }
            } else {
                if ("ORDERNOTEXIST".equals(payResult.getErr_code())) {
                    payLog.setPayStatus(OrderConstant.ORDER_PAY_STATUS_WAIT);
                    payLogService.update(payLog);
                }
            }
        }
    }
}
