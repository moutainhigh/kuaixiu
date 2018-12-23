package com.common.alipay.api.bean.result;

/**
 * Created by Administrator on 2018/11/22/022.
 */
public class AliPayResult {
    private String outTradeNo;
    private String subCode;
    private String subMsg;

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getSubCode() {
        return subCode;
    }

    public void setSubCode(String subCode) {
        this.subCode = subCode;
    }

    public String getSubMsg() {
        return subMsg;
    }

    public void setSubMsg(String subMsg) {
        this.subMsg = subMsg;
    }
}
