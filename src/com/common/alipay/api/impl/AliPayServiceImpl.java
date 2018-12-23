package com.common.alipay.api.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeCloseModel;
import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.common.alipay.api.AliPayService;
import com.common.alipay.api.bean.result.AliPayResult;
import com.kuaixiu.recycle.controller.RecycleExternalController;
import com.system.constant.SystemConstant;
import org.apache.log4j.Logger;

/**
 * Created by Administrator on 2018/11/22/022.
 */
public class AliPayServiceImpl implements AliPayService {
    private static final Logger log = Logger.getLogger(RecycleExternalController.class);

    @Override
    public AlipayTradeQueryResponse alipayQueryOrder(String outTradeNo) {
        AlipayClient alipayClient = new DefaultAlipayClient(SystemConstant.ALIPAY_APP_URL,
                SystemConstant.ALIPAY_APP_ID, SystemConstant.ALIPAY_APP_PAIVATE_KEY,
                "json", "UTF-8",  SystemConstant.ALIPAY_APP_PUBLIC_KEY, "RSA2");
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        AlipayTradeQueryModel queryModel=new AlipayTradeQueryModel();
        queryModel.setOutTradeNo(outTradeNo);
        request.setBizModel(queryModel);
        log.info("支付宝"+outTradeNo);
        AlipayTradeQueryResponse response=new AlipayTradeQueryResponse();
        try {
            response = alipayClient.execute(request);
            if (response.isSuccess()) {
                return response;
            } else if("ACQ.TRADE_NOT_EXIST".equals(response.getSubCode())){
                log.info("查询的订单不存在");
            }else{
                log.info("业务错误码"+response.getSubCode());
                log.info("支付宝查询订单失败");
            }
        } catch (AlipayApiException a) {
            a.printStackTrace();
        }
        return response;
    }

    @Override
    public AlipayTradePrecreateResponse alipayPrepaidOrder(AlipayTradePrecreateModel precreateModel) {
        AlipayClient alipayClient = new DefaultAlipayClient(SystemConstant.ALIPAY_APP_URL,
                SystemConstant.ALIPAY_APP_ID, SystemConstant.ALIPAY_APP_PAIVATE_KEY,
                "json", "UTF-8",  SystemConstant.ALIPAY_APP_PUBLIC_KEY, "RSA2");
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
        request.setBizModel(precreateModel);
        request.setNotifyUrl(SystemConstant.ALIPAY_NOTIFY_URL);
        log.info("支付宝"+precreateModel+"NotifyUrl"+SystemConstant.ALIPAY_NOTIFY_URL);
        AlipayTradePrecreateResponse response=new AlipayTradePrecreateResponse();
        try {
            response=alipayClient.execute(request);
            if (response.isSuccess()) {
                return response;
            } else {
                log.info("业务错误码"+response.getSubCode());
                log.info("支付宝预创建订单失败");
            }
        }catch (AlipayApiException a){
            a.printStackTrace();
        }
        return response;
    }

    @Override
    public Boolean alipayCloseOrder(String outTradeNo){
        AlipayClient alipayClient = new DefaultAlipayClient(SystemConstant.ALIPAY_APP_URL,
                SystemConstant.ALIPAY_APP_ID, SystemConstant.ALIPAY_APP_PAIVATE_KEY,
                "json", "UTF-8",  SystemConstant.ALIPAY_APP_PUBLIC_KEY, "RSA2");
        AlipayTradeCloseRequest request=new AlipayTradeCloseRequest();
        AlipayTradeCloseModel closeModel=new AlipayTradeCloseModel();
        closeModel.setOutTradeNo(outTradeNo);
        request.setBizModel(closeModel);
        log.info("支付宝"+request);
        try {
            AlipayTradeCloseResponse response=alipayClient.execute(request);
            if (response.isSuccess()) {
                return true;
            } else {
                log.info("业务错误码"+response.getSubCode());
                log.info("支付宝关闭订单失败");
            }
        }catch (AlipayApiException a){
            a.printStackTrace();
        }
        return false;
    }
}
