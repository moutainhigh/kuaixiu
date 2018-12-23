package com.kuaixiu.integral.entity;

import com.common.base.entity.BaseEntity;

/**
* @author: anson
* @CreateDate: 2017年5月17日 下午4:45:07
* @version: V 1.0
* 兑换积分订单类
*/
public class GetIntegral extends BaseEntity{

	

	private static final long serialVersionUID = 3045167659127706625L;
	/**
	 * 积分订单唯一标示id
	 */
	private String id;
	/**
	 * 积分订单对应店员id
	 */
	private String clerkId;
	/**
	 * 积分值
	 */
	private Integer integral;
	/**
	 * 订单生成时间
	 */
	private java.util.Date inTime;
	
	/**
	 * 积分兑换人
	 */
	private String name;
	/**
	 * 兑换人号码
	 */
	private String tel; 
	/**
	 * 是否显示  0表示显示  1表示不显示 
	 */
	private Integer isShow;
	/**
	 * 该积分来自的订单号 
	 */
	private String orderNo;
	/**
	 * 该积分来自的订单号id
	 */
	private String orderId;
	
	
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getClerkId() {
		return clerkId;
	}
	public void setClerkId(String clerkId) {
		this.clerkId = clerkId;
	}
	public Integer getIntegral() {
		return integral;
	}
	public void setIntegral(Integer integral) {
		this.integral = integral;
	}
	public java.util.Date getInTime() {
		return inTime;
	}
	public void setInTime(java.util.Date inTime) {
		this.inTime = inTime;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public Integer getIsShow() {
		return isShow;
	}
	public void setIsShow(Integer isShow) {
		this.isShow = isShow;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	
	
	
	
	
	
	
}
