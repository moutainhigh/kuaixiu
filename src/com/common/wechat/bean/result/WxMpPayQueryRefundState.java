package com.common.wechat.bean.result;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
* @author: anson
* @CreateDate: 2017年10月20日 下午9:30:33
* @version: V 1.0
* 查询微信退款订单的结果  
* 详细见https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_5
*/
@XStreamAlias("xml")
public class WxMpPayQueryRefundState implements Serializable {


	private static final long serialVersionUID = 1L;

	  @XStreamAlias("return_code")
	  private String returnCode;
	  
	  @XStreamAlias("return_msg")
	  private String returnMsg;
	  
	  @XStreamAlias("result_code")
	  private String resultCode;
	  
	  @XStreamAlias("err_code")
	  private String errCode;
	  
	  @XStreamAlias("err_code_des")
	  private String errCodeDes;
	  
	  @XStreamAlias("appid")
	  private String appid;
	  
	  @XStreamAlias("mch_id")
	  private String mchId;
	  
	  @XStreamAlias("nonce_str")
	  private String nonceStr;
	  
	  @XStreamAlias("sign")
	  private String sign;
	  
	  @XStreamAlias("transaction_id")
	  private String transactionId;
	  
	  @XStreamAlias("out_trade_no")
	  private String outTradeNo;
	  
	  @XStreamAlias("total_fee")
	  private String totalFee;
	  
	  @XStreamAlias("refund_count")
	  private String refundCount;
	  
	  @XStreamAlias("out_refund_no_0")
	  private String outRefundNo;
	  
	  @XStreamAlias("refund_id_0")
	  private String refundId;
	  
	  @XStreamAlias("refund_channel_0")
	  private String refundChannel;
	  
	  @XStreamAlias("refund_fee_0")
	  private String refundFee;
	  
	  @XStreamAlias("fee_type")
	  private String feeType;
	  
	  @XStreamAlias("cash_fee")
	  private String cashFee;
	  
	  @XStreamAlias("coupon_refund_fee")
	  private String couponRefundFee;
	  
	  @XStreamAlias("coupon_refund_count")
	  private String couponRefundCount;
	  
	  @XStreamAlias("coupon_refund_id")
	  private String couponRefundId;
	  
	  @XStreamAlias("refund_status_0")
	  private String refundStatus;
	  
	  @XStreamAlias("refund_account_0")
	  private String refundAccount;
	  
	  @XStreamAlias("refund_recv_accout_0")
	  private String refundRecvAccout;
	  
	  @XStreamAlias("refund_success_time_0")
	  private String refundSuccessTime;

	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	public String getReturnMsg() {
		return returnMsg;
	}

	public void setReturnMsg(String returnMsg) {
		this.returnMsg = returnMsg;
	}

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public String getErrCode() {
		return errCode;
	}

	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}

	public String getErrCodeDes() {
		return errCodeDes;
	}

	public void setErrCodeDes(String errCodeDes) {
		this.errCodeDes = errCodeDes;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getMchId() {
		return mchId;
	}

	public void setMchId(String mchId) {
		this.mchId = mchId;
	}

	public String getNonceStr() {
		return nonceStr;
	}

	public void setNonceStr(String nonceStr) {
		this.nonceStr = nonceStr;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getOutTradeNo() {
		return outTradeNo;
	}

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}

	public String getTotalFee() {
		return totalFee;
	}

	public void setTotalFee(String totalFee) {
		this.totalFee = totalFee;
	}

	public String getRefundCount() {
		return refundCount;
	}

	public void setRefundCount(String refundCount) {
		this.refundCount = refundCount;
	}

	public String getOutRefundNo() {
		return outRefundNo;
	}

	public void setOutRefundNo(String outRefundNo) {
		this.outRefundNo = outRefundNo;
	}

	public String getRefundId() {
		return refundId;
	}

	public void setRefundId(String refundId) {
		this.refundId = refundId;
	}

	public String getRefundChannel() {
		return refundChannel;
	}

	public void setRefundChannel(String refundChannel) {
		this.refundChannel = refundChannel;
	}

	public String getRefundFee() {
		return refundFee;
	}

	public void setRefundFee(String refundFee) {
		this.refundFee = refundFee;
	}

	public String getFeeType() {
		return feeType;
	}

	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}

	public String getCashFee() {
		return cashFee;
	}

	public void setCashFee(String cashFee) {
		this.cashFee = cashFee;
	}

	public String getCouponRefundFee() {
		return couponRefundFee;
	}

	public void setCouponRefundFee(String couponRefundFee) {
		this.couponRefundFee = couponRefundFee;
	}

	public String getCouponRefundCount() {
		return couponRefundCount;
	}

	public void setCouponRefundCount(String couponRefundCount) {
		this.couponRefundCount = couponRefundCount;
	}

	public String getCouponRefundId() {
		return couponRefundId;
	}

	public void setCouponRefundId(String couponRefundId) {
		this.couponRefundId = couponRefundId;
	}

	public String getRefundStatus() {
		return refundStatus;
	}

	public void setRefundStatus(String refundStatus) {
		this.refundStatus = refundStatus;
	}

	public String getRefundAccount() {
		return refundAccount;
	}

	public void setRefundAccount(String refundAccount) {
		this.refundAccount = refundAccount;
	}

	public String getRefundRecvAccout() {
		return refundRecvAccout;
	}

	public void setRefundRecvAccout(String refundRecvAccout) {
		this.refundRecvAccout = refundRecvAccout;
	}

	public String getRefundSuccessTime() {
		return refundSuccessTime;
	}

	public void setRefundSuccessTime(String refundSuccessTime) {
		this.refundSuccessTime = refundSuccessTime;
	}

	@Override
	public String toString() {
		return "WxMpPayQueryRefundState [returnCode=" + returnCode + ", returnMsg=" + returnMsg + ", resultCode="
				+ resultCode + ", errCode=" + errCode + ", errCodeDes=" + errCodeDes + ", appid=" + appid + ", mchId="
				+ mchId + ", nonceStr=" + nonceStr + ", sign=" + sign + ", transactionId=" + transactionId
				+ ", outTradeNo=" + outTradeNo + ", totalFee=" + totalFee + ", refundCount=" + refundCount
				+ ", outRefundNo=" + outRefundNo + ", refundId=" + refundId + ", refundChannel=" + refundChannel
				+ ", refundFee=" + refundFee + ", feeType=" + feeType + ", cashFee=" + cashFee + ", couponRefundFee="
				+ couponRefundFee + ", couponRefundCount=" + couponRefundCount + ", couponRefundId=" + couponRefundId
				+ ", refundStatus=" + refundStatus + ", refundAccount=" + refundAccount + ", refundRecvAccout="
				+ refundRecvAccout + ", refundSuccessTime=" + refundSuccessTime + "]";
	}
	  
	  
	  
	  
}
