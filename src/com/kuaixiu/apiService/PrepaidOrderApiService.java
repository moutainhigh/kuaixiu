package com.kuaixiu.apiService;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.common.alipay.api.AliPayService;
import com.common.alipay.api.impl.AliPayServiceImpl;
import com.common.base.controller.BaseController;
import com.common.exception.ApiServiceException;
import com.common.exception.SystemException;
import com.common.util.DateUtil;
import com.kuaixiu.order.constant.OrderConstant;
import com.kuaixiu.order.entity.Order;
import com.kuaixiu.order.entity.OrderPayLog;
import com.kuaixiu.order.service.OrderPayLogService;
import com.kuaixiu.order.service.OrderPayService;
import com.kuaixiu.order.service.OrderService;
import com.system.api.ApiServiceInf;
import com.system.constant.ApiResultConstant;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Map;

/**
 * 订单预支付接口实现类
 *
 * @author wugl
 */
@Service("PrepaidOrderApiService")
public class PrepaidOrderApiService extends BaseController implements ApiServiceInf {
    private static final Logger log = Logger.getLogger(PrepaidOrderApiService.class);

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderPayService orderPayService;
    @Autowired
    private OrderPayLogService payLogService;

    @Override
    public Object process(Map<String, String> params) {
        JSONObject jsonResult = new JSONObject();
        try {
            //解析请求参数
            //获取工程师工号和密码
            String number = MapUtils.getString(params, "pmClientId");
            String paramJson = MapUtils.getString(params, "params");
            String ip = MapUtils.getString(params, "request_ip");//获取用户请求的主机IP地址
            JSONObject pmJson = JSONObject.parseObject(paramJson);
            log.info("request=" + pmJson.toJSONString());
            //验证请求参数
            if (pmJson == null
                    || !pmJson.containsKey("order_no") || !pmJson.containsKey("pay_type")) {
                throw new ApiServiceException(ApiResultConstant.resultCode_1001, ApiResultConstant.resultCode_str_1001);
            }
            String orderNo = pmJson.getString("order_no");
            String payType = pmJson.getString("pay_type");
            Order order = orderService.queryByOrderNo(orderNo);
            if (order == null) {
                throw new SystemException("订单不存在!");
            }
            OrderPayLog payLog = new OrderPayLog();
            if ("1".equals(payType)) {//微信支付
                payLog.setTradeType("NATIVE");
                payLog.setSpbillCreateIp(ip);
                payLog.setBody("M-超人-订单支付");
                payLog.setAttach("balance");
                payLog.setOpenid("");
                payLog.setExpenseType(OrderConstant.ORDER_EXPENSE_TYPE_BALANCE);
                payLog.setEngCode(number);//工程师工号
                payLog.setPayMethod(0);//支付方式 1:微信支付
                payLog.setIsApp(0);//app支付
                Calendar cal = Calendar.getInstance();
                // 开始时间
                payLog.setTimeStart(DateUtil.getSerialFullDate(cal.getTime()));
                // 订单失效时间
                cal.add(Calendar.MINUTE, 30);
                payLog.setTimeExpire(DateUtil.getSerialFullDate(cal.getTime()));
                //维修订单支付
                payLog = orderPayService.startWechatPay(order.getId(), payLog, null);
            }else if("2".equals(payType)){
                if (order.getOrderStatus() == OrderConstant.ORDER_STATUS_CANCEL) {
                    throw new SystemException("该订单已取消！");
                }
                if (order.getOrderStatus() >= OrderConstant.ORDER_STATUS_FINISHED) {
                    throw new SystemException("该订单已完成,无需支付！");
                }
                OrderPayLog payLog1=new OrderPayLog();
                payLog1.setOrderNo(orderNo);
                payLog1.setPayMethod(1);
                // 判断是有未未完成的支付记录
                OrderPayLog payLogOld = payLogService.queryAppUnFinishedByOrderNo(payLog1);
                if(payLogOld!=null){
                    if (payLogOld.getPayStatus() == OrderConstant.ORDER_PAY_STATUS_SUCCESS) {
                        return "ok";
                    }
                    AliPayService aliPayService=new AliPayServiceImpl();
                    AlipayTradeQueryResponse payResult=aliPayService.alipayQueryOrder(payLogOld.getPayOrderNo());
                    if("10000".equals(payResult.getCode())){
                        aliPayService.alipayCloseOrder(payLogOld.getPayOrderNo());
                    }
                    // 订单已取消重新生成支付订单
                    payLogOld.setPayStatus(OrderConstant.ORDER_PAY_STATUS_COMPLETE);
                    payLogService.update(payLogOld);

                    payLog.setBody("M-超人-订单支付");
                    payLog.setEngCode(number);//工程师工号
                    payLog.setPayMethod(1);//支付方式 1:支付宝支付
                    payLog.setIsApp(0);//app支付
                    orderPayService.createPayLog(payLog,order);
                    payLogService.save(payLog);

                    AlipayTradePrecreateModel model=new AlipayTradePrecreateModel();
                    model.setOutTradeNo(payLog.getPayOrderNo());
                    model.setTotalAmount(String.valueOf(Double.valueOf(payLog.getTotalFee())/100));
                    model.setSubject(payLog.getBody());
                    AlipayTradePrecreateResponse precreateResponse =aliPayService.alipayPrepaidOrder(model);

                    payLog.setPayStatus(2);
                    payLog.setCodeUrl(precreateResponse.getQrCode());
                    payLogService.saveUpdate(payLog);
                }else{
                    AliPayService aliPayService=new AliPayServiceImpl();
                    payLog.setBody("M-超人-订单支付");
                    payLog.setEngCode(number);//工程师工号
                    payLog.setPayMethod(1);//支付方式 1:支付宝支付
                    payLog.setIsApp(0);//app支付
                    orderPayService.createPayLog(payLog,order);
                    payLogService.save(payLog);
                    AlipayTradePrecreateModel model=new AlipayTradePrecreateModel();
                    model.setOutTradeNo(payLog.getPayOrderNo());
                    model.setTotalAmount(String.valueOf(Double.valueOf(payLog.getTotalFee())/100));
                    model.setSubject(payLog.getBody());
                    AlipayTradePrecreateResponse precreateResponse=aliPayService.alipayPrepaidOrder(model);
                    payLog.setPayStatus(2);
                    payLog.setCodeUrl(precreateResponse.getQrCode());
                    payLogService.saveUpdate(payLog);
                }
            }
            if (payLog.getPayStatus() == OrderConstant.ORDER_PAY_STATUS_SUBMITED) {
                //提交成功
                jsonResult.put("data", payLog.getCodeUrl());
                jsonResult.put("pay_status", payLog.getPayStatus());
            } else if (payLog.getPayStatus() == OrderConstant.ORDER_PAY_STATUS_SUCCESS) {
                jsonResult.put("pay_status", payLog.getPayStatus());
            } else {
                jsonResult.put("pay_status", payLog.getPayStatus());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonResult;
    }

}
