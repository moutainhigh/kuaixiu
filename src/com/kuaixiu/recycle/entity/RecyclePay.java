package com.kuaixiu.recycle.entity;

import java.math.BigDecimal;
import java.util.Date;

public class RecyclePay {
    /** */
    private String id;

    /** 回收订单号*/
    private String recycleOrderNo;

    /** 支付宝商户转账唯一订单号*/
    private String outBizNo;

    /** 支付宝id*/
    private String userId;

    /** 支付发起类型 1-回收系统发起  2-财务管理系统发起 3-系统订单发起*/
    private Integer payLaunchType;

    /**   收款方账户类型。可取值： 
1、ALIPAY_USERID：支付宝账号对应的支付宝唯一用户号。以2088开头的16位纯数字组成。 
2、ALIPAY_LOGONID：支付宝登录号，支持邮箱和手机号格式。*/
    private String payeeType;

    /** 收款方账户 ,与payee_type配合使用。付款方和收款方不能是同一个账户*/
    private String payeeAccount;

    /** 付款方姓名*/
    private String payShowName;

    /** 收款方姓名*/
    private String payeeRealName;

    /** 转账备注*/
    private String remark;

    /** 交易金额*/
    private BigDecimal amount;

    /** 支付宝转账单据号 成功一定返回 失败可能不返回也可能返回*/
    private String alipayTransactionNo;

    /** 转账返回业务结果信息*/
    private String msg;

    /** 转账订单状态*/
    private Integer payStatus;

    /** 1:信用预支付;2:支付余款;3:发起扣款
     */
    private Integer payType;

    /** 订单生成时间*/
    private Date inTime;

    /** 订单支付时间：格式为yyyy-MM-dd HH:mm:ss，仅转账成功返回。*/
    private String paySuccessTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRecycleOrderNo() {
		return recycleOrderNo;
	}

	public void setRecycleOrderNo(String recycleOrderNo) {
		this.recycleOrderNo = recycleOrderNo;
	}

	public String getOutBizNo() {
		return outBizNo;
	}

	public void setOutBizNo(String outBizNo) {
		this.outBizNo = outBizNo;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Integer getPayLaunchType() {
		return payLaunchType;
	}

	public void setPayLaunchType(Integer payLaunchType) {
		this.payLaunchType = payLaunchType;
	}

	public String getPayeeType() {
		return payeeType;
	}

	public void setPayeeType(String payeeType) {
		this.payeeType = payeeType;
	}

	public String getPayeeAccount() {
		return payeeAccount;
	}

	public void setPayeeAccount(String payeeAccount) {
		this.payeeAccount = payeeAccount;
	}

	public String getPayShowName() {
		return payShowName;
	}

	public void setPayShowName(String payShowName) {
		this.payShowName = payShowName;
	}

	public String getPayeeRealName() {
		return payeeRealName;
	}

	public void setPayeeRealName(String payeeRealName) {
		this.payeeRealName = payeeRealName;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getAlipayTransactionNo() {
		return alipayTransactionNo;
	}

	public void setAlipayTransactionNo(String alipayTransactionNo) {
		this.alipayTransactionNo = alipayTransactionNo;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Integer getPayStatus() {
		return payStatus;
	}

	public void setPayStatus(Integer payStatus) {
		this.payStatus = payStatus;
	}

	public Integer getPayType() {
		return payType;
	}

	public void setPayType(Integer payType) {
		this.payType = payType;
	}

	public Date getInTime() {
		return inTime;
	}

	public void setInTime(Date inTime) {
		this.inTime = inTime;
	}

	public String getPaySuccessTime() {
		return paySuccessTime;
	}

	public void setPaySuccessTime(String paySuccessTime) {
		this.paySuccessTime = paySuccessTime;
	}

  
}