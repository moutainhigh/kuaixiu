package com.kuaixiu.recycle.entity;

import com.common.base.entity.BaseEntity;

import java.util.Date;

/**
 *

* Description:抽奖记录实体类

* @author anson

* @date 2018年6月12日
 */
public class PrizeRecord extends BaseEntity {
    /**
	 * 
	 */
	private static final long serialVersionUID = 221592108388116781L;

	/** 奖品id*/
    private String id;

    /** 手机号*/
    private String mobile;

    /** 微信用户openid*/
    private String wechatId;

    /** 奖品id*/
    private String prizeId;

    /** 记录生成时间*/
    private Date inTime;
    
    /**
     * 是否中奖  0未中奖  1已中奖
     */
    private Integer isGet;
    
    /**
     * 抽奖批次
     */
    private String batch;

	/**
	 * 1表示一等奖  2表示二等奖 3表示三等奖  4表示四等奖   0表示未定义
	 */
	private Integer grade;

	/**
	 * 维修通用优惠码
	 */
	private String couponCode;

	/**
	 * 抽奖来源  0微信小程序  1欢GO
	 */
	private Integer type;



	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getWechatId() {
		return wechatId;
	}

	public void setWechatId(String wechatId) {
		this.wechatId = wechatId;
	}

	public String getPrizeId() {
		return prizeId;
	}

	public void setPrizeId(String prizeId) {
		this.prizeId = prizeId;
	}

	public Date getInTime() {
		return inTime;
	}

	public void setInTime(Date inTime) {
		this.inTime = inTime;
	}

	public Integer getIsGet() {
		return isGet;
	}

	public void setIsGet(Integer isGet) {
		this.isGet = isGet;
	}

	public String getBatch() {
		return batch;
	}

	public void setBatch(String batch) {
		this.batch = batch;
	}

	public Integer getGrade() {
		return grade;
	}

	public void setGrade(Integer grade) {
		this.grade = grade;
	}

	public String getCouponCode() {
		return couponCode;
	}

	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
}