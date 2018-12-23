package com.kuaixiu.oldtonew.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.common.base.entity.BaseEntity;

/**
* @author: anson
* @CreateDate: 2017年6月19日 下午4:56:11
* @version: V 1.0
*  订单支付信息类
*/
public class NewOrderPay extends BaseEntity{

	private static final long serialVersionUID = 1L;

	 private String id;
	   /**
	    * 订单号
	    */
	 private String orderNo;
	 /**
	    * 是否退款 0 无需退款 1 等待退款  2 退款失败 3 退款成功
	    */
	 private Integer isDrawback;
	
	 /**
	    * 退款金额
	    */
	 private java.math.BigDecimal drawbackPrice;
	   /**
	    * 退库时间
	    */
	 private java.util.Date drawbackTime;
	 /**
	    * 保证金支付状态 0 未支付  1 已支付  2 支付失败
	    */
	 private Integer isDeposit;
	 /**
	  * 支付方式:0微信支付
	  */
	 private Integer depositType;
	 /**
	  * 保证金金额
	  */
	 private java.math.BigDecimal depositPrice;
	 /**
	  * 保证金支付时间
	  */
	 private java.util.Date depositTime;
	 /**
	    * 支付方式 0 微信支付
	    */
	   private Integer payType;
	   /**
	    * 支付状态 0 未支付 1 已支付
	    */
	   private Integer payStatus;
	   /**
	    * 支付时间
	    */
	   private java.util.Date payTime;
	   /**
	    * 支付金额
	    */
	   private java.math.BigDecimal payPrice;
	   /**
	    *记录生成时间
	    */
	   private java.util.Date inTime;
	   
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public Integer getIsDrawback() {
		return isDrawback;
	}
	public void setIsDrawback(Integer isDrawback) {
		this.isDrawback = isDrawback;
	}
	public java.math.BigDecimal getDrawbackPrice() {
		return drawbackPrice;
	}
	public void setDrawbackPrice(java.math.BigDecimal drawbackPrice) {
		this.drawbackPrice = drawbackPrice;
	}
	public java.util.Date getDrawbackTime() {
		return drawbackTime;
	}
	public void setDrawbackTime(java.util.Date drawbackTime) {
		this.drawbackTime = drawbackTime;
	}
	public Integer getIsDeposit() {
		return isDeposit;
	}
	public void setIsDeposit(Integer isDeposit) {
		this.isDeposit = isDeposit;
	}
	public Integer getDepositType() {
		return depositType;
	}
	public void setDepositType(Integer depositType) {
		this.depositType = depositType;
	}
	public java.math.BigDecimal getDepositPrice() {
		return depositPrice;
	}
	public void setDepositPrice(java.math.BigDecimal depositPrice) {
		this.depositPrice = depositPrice;
	}
	public java.util.Date getDepositTime() {
		return depositTime;
	}
	public void setDepositTime(java.util.Date depositTime) {
		this.depositTime = depositTime;
	}
	public Integer getPayType() {
		return payType;
	}
	public void setPayType(Integer payType) {
		this.payType = payType;
	}
	public Integer getPayStatus() {
		return payStatus;
	}
	public void setPayStatus(Integer payStatus) {
		this.payStatus = payStatus;
	}
	public java.util.Date getPayTime() {
		return payTime;
	}
	public void setPayTime(java.util.Date payTime) {
		this.payTime = payTime;
	}
	public java.math.BigDecimal getPayPrice() {
		return payPrice;
	}
	public void setPayPrice(java.math.BigDecimal payPrice) {
		this.payPrice = payPrice;
	}
	public java.util.Date getInTime() {
		return inTime;
	}
	public void setInTime(java.util.Date inTime) {
		this.inTime = inTime;
	}
	 

	 
}
