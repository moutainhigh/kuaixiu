package com.system.wechat.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.util.*;

import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.common.wechat.bean.result.WxMpPayResult;
import com.kuaixiu.order.entity.Order;
import com.kuaixiu.order.entity.UpdateOrderPrice;
import com.kuaixiu.order.service.*;
import com.kuaixiu.recycle.controller.RecycleExternalController;
import jodd.util.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.system.constant.SystemConstant;
import com.system.wechat.util.MD5Util;
import com.system.wechat.util.ReturnModel;
import com.system.wechat.util.Sha1Util;
import com.system.wechat.util.XMLUtil;
import com.google.gson.Gson;
import com.kuaixiu.order.constant.OrderConstant;
import com.kuaixiu.order.entity.OrderPayLog;
import com.common.exception.SystemException;
import com.common.util.Base64Util;
import com.common.wechat.common.exception.WxErrorException;
import com.common.wechat.api.WxMpConfigStorage;
import com.common.wechat.api.WxMpService;
import com.common.wechat.bean.result.WxMpPrepayIdResult;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 微信支付Controller
 * <p>
 * Created by FirenzesEagle on 2016/6/20 0020. Email:liumingbo2008@gmail.com
 */
@Controller
public class PaymentController extends GenericController {
    private static final Logger log = Logger.getLogger(RecycleExternalController.class);

    @Autowired
    protected WxMpConfigStorage configStorage;
    @Autowired
    protected WxMpService wxMpService;
    @Autowired
    private OrderPayLogService payLogService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderPayLogService orderPayLogService;
    @Autowired
    private OrderPayService orderPayService;
    @Autowired
    private UpdateOrderPriceService updateOrderPriceService;

    // 企业向个人转账微信API路径
    private static final String ENTERPRISE_PAY_URL = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers";
    // apiclient_cert.p12证书存放路径
    private static final String CERTIFICATE_LOCATION = "";

    /**
     * 用于返回预支付的结果 WxMpPrepayIdResult，一般不需要使用此接口
     *
     * @param response
     * @param openid
     * @param out_trade_no
     * @param total_fee
     * @param body
     * @param trade_type
     * @param spbill_create_ip
     */
    @RequestMapping(value = "getPrepayIdResult")
    public void getPrepayId(HttpServletResponse response, @RequestParam(value = "openid") String openid,
                            @RequestParam(value = "out_trade_no") String out_trade_no,
                            @RequestParam(value = "total_fee") String total_fee, @RequestParam(value = "body") String body,
                            @RequestParam(value = "trade_type") String trade_type,
                            @RequestParam(value = "spbill_create_ip") String spbill_create_ip) {
        WxMpPrepayIdResult wxMpPrepayIdResult;
        Map<String, String> payInfo = new HashMap<String, String>();
        payInfo.put("openid", openid);
        payInfo.put("out_trade_no", out_trade_no);
        payInfo.put("total_fee", total_fee);
        payInfo.put("body", body);
        payInfo.put("trade_type", trade_type);
        payInfo.put("spbill_create_ip", spbill_create_ip);
        payInfo.put("notify_url", "");
        this.logger.info("PartnerKey is :" + this.configStorage.getPartnerKey());
        wxMpPrepayIdResult = this.wxMpService.getPayService().getPrepayId(payInfo);
        this.logger.info(new Gson().toJson(wxMpPrepayIdResult));
        renderString(response, wxMpPrepayIdResult);
    }

    /**
     * 返回前台H5调用JS支付所需要的参数，公众号支付调用此接口
     *
     * @param response
     * @param openid
     * @param out_trade_no
     * @param total_fee
     * @param body
     * @param trade_type
     * @param spbill_create_ip
     */
    @RequestMapping(value = "getJSSDKPayInfo")
    public void getJSSDKPayInfo(HttpServletResponse response, @RequestParam(value = "openid") String openid,
                                @RequestParam(value = "out_trade_no") String out_trade_no,
                                @RequestParam(value = "total_fee") String total_fee, @RequestParam(value = "body") String body,
                                @RequestParam(value = "trade_type") String trade_type,
                                @RequestParam(value = "spbill_create_ip") String spbill_create_ip) {
        ReturnModel returnModel = new ReturnModel();
        Map<String, String> prepayInfo = new HashMap<String, String>();
        prepayInfo.put("openid", openid);
        prepayInfo.put("out_trade_no", out_trade_no);
        prepayInfo.put("total_fee", total_fee);
        prepayInfo.put("body", body);
        prepayInfo.put("trade_type", trade_type);
        prepayInfo.put("spbill_create_ip", spbill_create_ip);
        // TODO 填写通知回调地址
        prepayInfo.put("notify_url", "");
        try {
            Map<String, String> payInfo = this.wxMpService.getPayService().getPayInfo(prepayInfo);
            returnModel.setResult(true);
            returnModel.setDatum(payInfo);
            renderString(response, returnModel);
        } catch (WxErrorException e) {
            returnModel.setResult(false);
            returnModel.setReason(e.getError().toString());
            renderString(response, returnModel);
            this.logger.error(e.getError().toString());
        }
    }

    /**
     * 微信通知支付结果的回调地址，notify_url
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/wechat/pay/callback")
    public void getJSSDKCallbackData(HttpServletRequest request, HttpServletResponse response) {
        try {
            synchronized (this) {
                Map<String, String> kvm = XMLUtil.parseRequestXmlToMap(request);
                System.out.println(kvm.toString());
                System.out.println("微信回调");
                StringBuffer resultMsg = new StringBuffer();
                if (this.wxMpService.getPayService().checkJSSDKCallbackDataSignature(kvm, kvm.get("sign"))) {
                    if (kvm.get("result_code").equals("SUCCESS")) {
                        try {
                            orderPayService.payCallback(kvm);
                            this.logger.info("out_trade_no: " + kvm.get("out_trade_no") + " pay SUCCESS!");
                            resultMsg.append(
                                    "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[ok]]></return_msg></xml>");
                        } catch (SystemException e) {
                            e.printStackTrace();
                            this.logger.error("out_trade_no: " + kvm.get("out_trade_no") + " result_code is FAIL");
                            resultMsg.append("<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[")
                                    .append(e.getMessage()).append("]]></return_msg></xml>");
                        } catch (Exception e) {
                            e.printStackTrace();
                            resultMsg.append(
                                    "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[System Exception]]></return_msg></xml>");
                        }
                    } else {
                        this.logger.error("out_trade_no: " + kvm.get("out_trade_no") + " result_code is FAIL");
                        resultMsg.append(
                                "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[result_code is FAIL]]></return_msg></xml>");
                    }
                } else {
                    resultMsg.append(
                            "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[check signature FAIL]]></return_msg></xml>");
                    this.logger.error("out_trade_no: " + kvm.get("out_trade_no") + " check signature FAIL");
                }
                response.getWriter().write(resultMsg.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "payToIndividual")
    public void payToIndividual(HttpServletResponse response,
                                @RequestParam(value = "partner_trade_no") String partner_trade_no,
                                @RequestParam(value = "openid") String openid, @RequestParam(value = "amount") String amount,
                                @RequestParam(value = "desc") String desc,
                                @RequestParam(value = "spbill_create_ip") String spbill_create_ip) {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("mch_appid", this.configStorage.getAppId());
        map.put("mchid", this.configStorage.getPartnerId());
        map.put("nonce_str", Sha1Util.getNonceStr());
        map.put("partner_trade_no", partner_trade_no);
        map.put("openid", openid);
        map.put("check_name", "NO_CHECK");
        map.put("amount", amount);
        map.put("desc", desc);
        map.put("spbill_create_ip", spbill_create_ip);
        try {
            Map<String, String> returnMap = enterprisePay(map, this.configStorage.getPartnerKey(), CERTIFICATE_LOCATION,
                    ENTERPRISE_PAY_URL);
            if ("SUCCESS".equals(returnMap.get("result_code").toUpperCase())
                    && "SUCCESS".equals(returnMap.get("return_code").toUpperCase())) {
                this.logger.info("企业对个人付款成功！\n付款信息：\n" + returnMap.toString());
            } else {
                this.logger.error(
                        "err_code: " + returnMap.get("err_code") + "  err_code_des: " + returnMap.get("err_code_des"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 微信退款回调地址通知 该地址在商户号手动配置
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/wechat/refund/callback")
    public void getJSSDKRefundCallbackData(HttpServletRequest request, HttpServletResponse response) {
        try {
            //AES 256解密很麻烦 现在采用后台按钮手动查询订单状态  而对于该地址微信返回的退款结果  默认都按成功接收
            System.out.println("退款回调结果");
            StringBuffer resultMsg = new StringBuffer();
            resultMsg.append("<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[ok]]></return_msg></xml>");
            response.getWriter().write(resultMsg.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 企业付款方法,传入map, 除用到上面内容外,还用到path(证书磁盘路径),key(商户支付密匙),url(接口地址)
     *
     * @return
     */
    /**
     * @param map,根据map中的 map中包含字段 mch_appid 微信分配的公众账号ID（企业号corpid即为此appId） mchid
     *                    微信支付分配的商户号 nonce_str 随机字符串，不长于32位 partner_trade_no
     *                    商户订单号，需保持唯一性 openid 商户appid下，某用户的openid check_name
     *                    是否校验真实姓名,如果需要,则还需要传re_user_name字段 NO_CHECK：不校验真实姓名 amount
     *                    支付金额,以分为单位 desc 企业付款操作说明信息。必填。 spbill_create_ip
     *                    调用接口的机器Ip地址(随便填,查询订单详情时会显示出来)
     * @param keys        商品平台支付密匙
     * @param paths       证书路径
     * @param uri         接口地址
     * @return 返回Map<String,String>
     * @throws Exception
     */
    public Map<String, String> enterprisePay(TreeMap<String, String> map, String keys, String paths, String uri)
            throws Exception {
        Map<String, String> returnMap = new HashMap<String, String>();
        String mch_id = map.get("mchid");
        Set<Map.Entry<String, String>> entry2 = map.entrySet();
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> obj : entry2) {
            String k = obj.getKey();
            String v = obj.getValue();
            if (v == null || v.equals("")) {
                continue;
            }
            sb.append(k + "=" + v + "&");
        }
        sb.append("key=" + keys);
        String str2 = MD5Util.MD5Encode(sb.toString(), "UTF-8").toUpperCase();
        map.put("sign", str2);
        StringBuffer buffer = new StringBuffer();
        buffer.append("<xml>");
        if (map != null && !map.isEmpty()) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                buffer.append("<" + entry.getKey() + ">");
                buffer.append(entry.getValue());
                buffer.append("</" + entry.getKey() + ">");
            }
        }
        buffer.append("</xml>");
        String desc = new String(buffer.toString().getBytes("UTF-8"), "ISO-8859-1");
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        FileInputStream instream = new FileInputStream(new File(paths));
        try {
            keyStore.load(instream, mch_id.toCharArray());
        } finally {
            instream.close();
        }
        SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, mch_id.toCharArray()).build();
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[]{"TLSv1"}, null,
                SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
        CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
        try {
            HttpPost httpPost = new HttpPost(uri);
            StringEntity str = new StringEntity(desc);
            httpPost.setEntity(str);
            CloseableHttpResponse response = httpclient.execute(httpPost);
            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entity.getContent()));
                    String text;
                    StringBuffer result = new StringBuffer();
                    while ((text = bufferedReader.readLine()) != null) {
                        result.append(text);
                    }
                    returnMap = XMLUtil.parseXmlStringToMap(new String(result.toString().getBytes(), "UTF-8"));// 调用统一接口返回的值转换为XML格式
                }
                EntityUtils.consume(entity);
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
        return returnMap;
    }


    /**
     * 支付宝支付成功后.异步请求该接口
     *
     * @param request
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/alipay/pay/callback")
    @ResponseBody
    public String notify(HttpServletRequest request, HttpServletResponse response) {
        //用以存放转化后的参数集合
        Map<String, String> conversionParams = new HashMap<String, String>();
        try {
            log.info("==================支付宝异步返回支付结果开始");
            //1.从支付宝回调的request域中取值
            //获取支付宝返回的参数集合
            Map<String, String[]> aliParams = request.getParameterMap();

            for (Iterator<String> iter = aliParams.keySet().iterator(); iter.hasNext(); ) {
                String key = iter.next();
                String[] values = aliParams.get(key);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
                }
                // 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
                // valueStr = new String(valueStr.getBytes("ISO-8859-1"), "uft-8");
                conversionParams.put(key, valueStr);
            }
            log.info("==================返回参数集合：" + conversionParams);
        }catch (Exception e){
            e.printStackTrace();
        }
        return notify(conversionParams);
    }


    /**
     * 支付宝异步请求逻辑处理
     *
     * @return
     * @throws IOException
     */
    private String notify(Map<String, String> conversionParams) {

        log.info("==================支付宝异步请求逻辑处理");

        //签名验证(对支付宝返回的数据验证，确定是支付宝返回的)
        boolean signVerified = false;

        try {
            //调用SDK验证签名
            signVerified = AlipaySignature.rsaCheckV1(conversionParams, SystemConstant.ALIPAY_APP_PUBLIC_KEY, "UTF-8", "RSA2");

        } catch (AlipayApiException e) {
            log.info("==================验签失败 ！");
            e.printStackTrace();
        }

        //对验签进行处理
        if (signVerified) {
            //验签通过
            //获取需要保存的数据
//            String outBizNo = conversionParams.get("out_biz_no");//商户业务号(商户业务ID，主要是退款通知中返回退款申请的流水号)
//            String gmtCreate = conversionParams.get("gmt_create");//交易创建时间:yyyy-MM-dd HH:mm:ss
//            String gmtPayment = conversionParams.get("gmt_payment");//交易付款时间
//            String gmtRefund = conversionParams.get("gmt_refund");//交易退款时间
//            String gmtClose = conversionParams.get("gmt_close");//交易结束时间
//            String sellerId = conversionParams.get("seller_id");//卖家支付宝用户号
//            String sellerEmail = conversionParams.get("seller_email");//卖家支付宝账号
//            String receiptAmount = conversionParams.get("receipt_amount");//实收金额:商家在交易中实际收到的款项，单位为元
//            String invoiceAmount = conversionParams.get("invoice_amount");//开票金额:用户在交易中支付的可开发票的金额
//            String buyerPayAmount = conversionParams.get("buyer_pay_amount");//付款金额:用户在交易中支付的金额
            String appId = conversionParams.get("app_id");//支付宝分配给开发者的应用Id
            String notifyTime = conversionParams.get("notify_time");//通知时间:yyyy-MM-dd HH:mm:ss
            String tradeNo = conversionParams.get("trade_no");//支付宝的交易号
            String outTradeNo = conversionParams.get("out_trade_no");//获取商户之前传给支付宝的订单号（商户系统的唯一订单号）
            String buyerLogonId = conversionParams.get("buyer_logon_id");//买家支付宝账号
            String totalAmount = conversionParams.get("total_amount");//订单金额:本次交易支付的订单金额，单位为人民币（元）
            String tradeStatus = conversionParams.get("trade_status");// 获取交易状态
            String fundBillList=conversionParams.get("fund_bill_list");//交易渠道

            //支付宝官方建议校验的值（out_trade_no、total_amount、sellerId、app_id）
            OrderPayLog payLog = orderPayLogService.queryByPayOrderNo(outTradeNo);
            Order o = orderService.queryByOrderNo(payLog.getOrderNo());
            if(o==null){
                log.info("该订单不存在！"+payLog.getOrderNo());
                return "fail";
            }
            //修改后订单不计算优惠券
            List<UpdateOrderPrice> orderPrices = updateOrderPriceService.getDao().queryByUpOrderNo(o.getOrderNo());
            if(o.getCouponPrice()!=null){
                if (CollectionUtils.isEmpty(orderPrices)) {
                    // 支付余款 = 订单实际费用 (已考虑用户是否使用优惠券)
                    o.setRealPrice(o.getRealPriceSubCoupon());
                    log.info("订单金额"+o.getRealPrice()+"支付宝金额"+totalAmount);
                }
            }
            if (totalAmount.equals(o.getRealPrice().toString()) && SystemConstant.ALIPAY_APP_ID.equals(appId)) {
                // 支付结果未处理
                if (payLog.getPayStatus() == OrderConstant.ORDER_PAY_STATUS_SUBMITED) {
                    // 处理支付结果
                    if ("TRADE_SUCCESS".equals(tradeStatus)) {
                        // 支付成功
                        payLog.setPayStatus(OrderConstant.ORDER_PAY_STATUS_SUCCESS);

                        o.setPayType(1);
                        orderPayService.paySuccess(payLog, o);                // 维修订单更新订单状态

                        // 保存支付信息
                        //去掉时间字符串空格，横线，冒号
                        String endTime=notifyTime.replace(" ","").replace("-","").replace(":","");
                        payLog.setTimeEnd(endTime);
                        payLog.setTransactionId(tradeNo);
                        payLog.setBuyerLogonId(buyerLogonId);
                        payLog.setFundChannel(funBillListToString(fundBillList));
                        payLogService.update(payLog);
                        o.setPayStatus(OrderConstant.ORDER_PAY_STATUS_SUCCESS);
                        o.setPayTime(new Date());
                        o.setEndTime(new Date());
                        o.setPayType(1);//支付宝支付
                        orderService.saveUpdate(o);

                        //支付宝支付成功，关闭微信预支付订单
                        OrderPayLog payLog1 = new OrderPayLog();
                        payLog1.setOrderNo(o.getOrderNo());
                        payLog1.setPayMethod(0);
                        OrderPayLog orderPayLog = payLogService.queryAppUnFinishedByOrderNo(payLog1);
                        // 通过接口查询微信端订单状态
                        WxMpPayResult payResult = wxMpService.getPayService().getJSSDKPayResult(null, payLog.getPayOrderNo());
                        if (payResult != null && "SUCCESS".equals(payResult.getReturn_code())) {
                            if ("SUCCESS".equals(payResult.getResult_code())) {
                                orderPayService.closeWechatOrder(orderPayLog);
                            }
                        }
                        log.info("==================支付宝异步返回支付结果结束");
                        return "success";
                    } else {
                        log.info("支付失败");
                        return "fail";
                    }
                } else {
                    log.info("支付结果不一致");
                    return "fail";
                }
            } else {
                log.info("支付金额不一致");
                return "fail";
            }
        } else { //验签不通过
            log.info("==================验签不通过 ！");
            return "fail";
        }

    }

    //支付渠道转换
    public static String funBillListToString(String funBillList){
        String fundBillList1=funBillList.replace("[","").replace("]","")
                .replace("{","").replace("}","");
        String funBill[]=fundBillList1.split(",");
        String funDao="";
        for(int i=0;i<funBill.length;i++){
            String fun[]=funBill[i].split(":");
            if("fundChannel".equals(fun[0])){
                funDao=fun[1];

                break;
            }
        }
        return funDao;
    }

    public static void main(String[] args) {

        String result = Base64Util.getBase64("13867475311:113574");
        System.out.println(result);
    }

}
