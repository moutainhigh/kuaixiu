package com.kuaixiu.recycle.entity;

import java.util.Date;

import com.common.base.entity.BaseEntity;

/**
 * 
* Description:微信小程序分享记录实体表

* @author anson  

* @date 2018年6月12日
 */
public class TransmitRecord extends BaseEntity{
    /**
	 * 
	 */
	private static final long serialVersionUID = 2195440078027065892L;

	/** */
    private String id;

    /** 微信用户openid*/
    private String wechatId;

    /** 手机号*/
    private String mobile;

    /** 微信分享id(微信群id)*/
    private String wechatGroupId;

    /** 记录生成时间*/
    private Date inTime;

    /** 订单更新时间*/
    private Date updateTime;
    
  

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getWechatId() {
		return wechatId;
	}

	public void setWechatId(String wechatId) {
		this.wechatId = wechatId;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getWechatGroupId() {
		return wechatGroupId;
	}

	public void setWechatGroupId(String wechatGroupId) {
		this.wechatGroupId = wechatGroupId;
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



	
   
}