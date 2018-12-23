package com.kuaixiu.integral.entity;

import com.common.base.entity.BaseEntity;

/**
* @author: anson
* @CreateDate: 2017年5月17日 下午4:39:07
* @version: V 1.0
* 兑换积分订单类
*/
public class Integral extends BaseEntity{

	
	private static final long serialVersionUID = 7945376610110107424L;

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
	 * 积分订单状态  默认为2表示审核中    1 表示订单已完成
	 */
	private Integer status;
	/**
	 * 积分兑换人
	 */
	private String name;
	/**
	 * 兑换人号码
	 */
	private String tel;
	/**
	 * 微信号
	 */
	private String wechatId;
	/**
	 * 微信是否实名制 1表示是  2表示否
	 */
	private Integer isRealName;
	/**
	 * 付款时间
	 */
	private java.util.Date updateTime;
	/**
	 * 付款人
	 */
	private String payer;
	
	
	
	public java.util.Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(java.util.Date updateTime) {
		this.updateTime = updateTime;
	}
	public String getPayer() {
		return payer;
	}
	public void setPayer(String payer) {
		this.payer = payer;
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
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
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
	public String getWechatId() {
		return wechatId;
	}
	public void setWechatId(String wechatId) {
		this.wechatId = wechatId;
	}
	public Integer getIsRealName() {
		return isRealName;
	}
	public void setIsRealName(Integer isRealName) {
		this.isRealName = isRealName;
	}
	
    	
	
	
	
}
