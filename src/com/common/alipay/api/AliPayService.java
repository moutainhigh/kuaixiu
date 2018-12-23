package com.common.alipay.api;

import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.common.alipay.api.bean.result.AliPayResult;

/**
 * Created by Administrator on 2018/11/22/022.
 */
public interface AliPayService {

    //根据订单号查询
    AlipayTradeQueryResponse alipayQueryOrder(String outTradeNo);

    //预支付订单接口
    AlipayTradePrecreateResponse alipayPrepaidOrder(AlipayTradePrecreateModel precreateModel);

    //关闭订单
    Boolean alipayCloseOrder(String outTradeNo);
}
