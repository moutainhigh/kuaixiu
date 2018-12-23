package com.kuaixiu.wechat.entity;

import java.util.Date;

import com.common.base.entity.BaseEntity;

public class WechatUser extends BaseEntity {

	private static final long serialVersionUID = 1617204087575110004L;

	/** 微信用户唯一标示 */
	private String openId;

	/** 状态关注 0：已关注；1：已取消关注 */
	private Integer isSubscribe;
	/**
	 * 维修通用优惠码
	 */
	private String commonCouponCode;

	/**
	 * 换膜优惠码
	 */
	private String screenCouponCode;

	/** 首次关注时间 */
	private Date inTime;

	/** 更新时间 */
	private Date updateTime;
	/**
	 * 用户昵称
	 */
	private String nickname;
	/**
	 * 用户性别 值为1时是男性，值为2时是女性，值为0时是未知 
	 */
	private Integer sex;
	/**
	 * 用户头像地址
	 */
	private String headimgurl;
	
	

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public Integer getIsSubscribe() {
		return isSubscribe;
	}

	public void setIsSubscribe(Integer isSubscribe) {
		this.isSubscribe = isSubscribe;
	}

	public Date getInTime() {
		return inTime;
	}

	public void setInTime(Date inTime) {
		this.inTime = inTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getCommonCouponCode() {
		return commonCouponCode;
	}

	public void setCommonCouponCode(String commonCouponCode) {
		this.commonCouponCode = commonCouponCode;
	}

	public String getScreenCouponCode() {
		return screenCouponCode;
	}

	public void setScreenCouponCode(String screenCouponCode) {
		this.screenCouponCode = screenCouponCode;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public String getHeadimgurl() {
		return headimgurl;
	}

	public void setHeadimgurl(String headimgurl) {
		this.headimgurl = headimgurl;
	}

	

	
	

}