package com.kuaixiu.screen.entity;

import java.util.Date;

import com.common.base.entity.BaseEntity;

public class ScreenCustomer extends BaseEntity{
    /**
	 * 
	 */
	private static final long serialVersionUID = 8871911349794906223L;

	/** */
    private String id;

    /** 手机品牌*/
    private String brand;

    /** 手机型号*/
    private String model;

    /** 手机串号*/
    private String imei;

    /** 售卖时间*/
    private Date saleTime;

    /** 顾客姓名*/
    private String name;

    /** 联系电话*/
    private String mobile;

    /** 是否删除 0：否；1：是*/
    private Integer isDel;

    /** 是否激活 0：否；1：是*/
    private Integer isActive;

    /** 订单生成时间*/
    private Date inTime;

    /** 订单更新时间*/
    private Date updateTime;

    /** 更新人id*/
    private String updateUserid;
    
    /**
     * string格式时间
     */
    private String stringInTime;
    /**
     * 是否激活
     * @return
     */
    private String active;
    /**
     * 是否过期 如果值为1  则查询过期的；为0则查询未过期的
     * @return
     */
    private String isOutOfTime;
    
    /**
     * 激活对应订单号
     */
    private String orderNo;
    
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

 
	public Date getSaleTime() {
		return saleTime;
	}

	public void setSaleTime(Date saleTime) {
		this.saleTime = saleTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Integer getIsDel() {
		return isDel;
	}

	public void setIsDel(Integer isDel) {
		this.isDel = isDel;
	}

	public Integer getIsActive() {
		return isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
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

	public String getUpdateUserid() {
		return updateUserid;
	}

	public void setUpdateUserid(String updateUserid) {
		this.updateUserid = updateUserid;
	}

	public String getStringInTime() {
		return stringInTime;
	}

	public void setStringInTime(String stringInTime) {
		this.stringInTime = stringInTime;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getIsOutOfTime() {
		return isOutOfTime;
	}

	public void setIsOutOfTime(String isOutOfTime) {
		this.isOutOfTime = isOutOfTime;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

   
}