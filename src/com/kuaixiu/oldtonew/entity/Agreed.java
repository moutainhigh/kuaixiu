package com.kuaixiu.oldtonew.entity;

import com.common.base.entity.BaseEntity;

/**
* @author: anson
* @CreateDate: 2017年6月19日 下午4:26:56
* @version: V 1.0
* 预约信息表,工程师手动填写
*/
public class Agreed extends BaseEntity{

	private static final long serialVersionUID = 1L;

	private String id;
	
	/**
	 * 对应订单号
	 */
	private String orderNo;
	/**
	 * 预约时间
	 */
	private String agreedTime;
	/**
	 * 工程师填写预约信息时的新机型的价格
	 */
	private java.math.BigDecimal agreedOrderPrice;
	/**
	 * 工程师填写预约信息时的新机型
	 */
	private String newModelName;
	/**
	 * 其他附加预约信息
	 */
	private String other;
	/**
	 * 订单生成时间
	 */
	private java.util.Date inTime;
	/**
	 * 工程师id
	 */
	private String engineerId;
	/**
	 * 机型颜色
	 */
	private String color;
	/**
	 * 机型内存
	 */
	private Integer memory;
	/**
	 * 手机支持网络类型
	 */
	private String edition;
	/**
	 * 是否删除  默认为0  表否  1表示是
	 */
	private Integer isDel;
	/**
	 * 预约机型品牌
	 */
	private String agreedBrand;
	
	
	public String getAgreedBrand() {
		return agreedBrand;
	}
	public void setAgreedBrand(String agreedBrand) {
		this.agreedBrand = agreedBrand;
	}
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
	public String getAgreedTime() {
		return agreedTime;
	}
	public void setAgreedTime(String agreedTime) {
		this.agreedTime = agreedTime;
	}
	public java.math.BigDecimal getAgreedOrderPrice() {
		return agreedOrderPrice;
	}
	public void setAgreedOrderPrice(java.math.BigDecimal agreedOrderPrice) {
		this.agreedOrderPrice = agreedOrderPrice;
	}
	public String getNewModelName() {
		return newModelName;
	}
	public void setNewModelName(String newModelName) {
		this.newModelName = newModelName;
	}
	public String getOther() {
		return other;
	}
	public void setOther(String other) {
		this.other = other;
	}
	public java.util.Date getInTime() {
		return inTime;
	}
	public void setInTime(java.util.Date inTime) {
		this.inTime = inTime;
	}
	public String getEngineerId() {
		return engineerId;
	}
	public void setEngineerId(String engineerId) {
		this.engineerId = engineerId;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	
	public Integer getMemory() {
		return memory;
	}
	public void setMemory(Integer memory) {
		this.memory = memory;
	}
	public String getEdition() {
		return edition;
	}
	public void setEdition(String edition) {
		this.edition = edition;
	}
	public Integer getIsDel() {
		return isDel;
	}
	public void setIsDel(Integer isDel) {
		this.isDel = isDel;
	}
	
   
	
	
}
