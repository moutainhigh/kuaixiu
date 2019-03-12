package com.kuaixiu.recycle.entity;

import com.common.base.entity.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 
* Description:回收检测项信息

* @author anson  

* @date 2018年6月14日
 */
public class RecycleCheckItems extends BaseEntity{
    /**
	 * 
	 */
	private static final long serialVersionUID = 3389091903425546462L;

	/** id*/
    private String id;

    /** 微信用户openid*/
    private String wechatId;

    /** 手机品牌名称*/
    private String brand;

    /** 微信小程序检测的手机型号*/
    private String wechatModel;

    /** 回收平台对应的手机型号*/
    private String recycleModel;

    /** 回收平台机型id*/
    private String productId;

    /** 上次检测项*/
    private String items;

    /** 记录生成时间*/
    private Date inTime;

    /** 上次检测价格*/
    private BigDecimal lastPrice;

    /** 本次检测价格*/
    private BigDecimal price;

    /** 记录更新时间*/
    private Date updateTime;

	/**
	 * 回收品牌id
	 */
	private String brandId;

	/**
	 * 手机号
	 */
	private String mobile;

	/**
	 * 回收产品id
	 */
    private String modelId;

	/**
	 * 登录手机号
	 */
	private String loginMobile;
	//是否成单    传参
	private String isOrder;
	//是否回访    传参
	private String isVisit;

	public String getIsOrder() {
		return isOrder;
	}

	public void setIsOrder(String isOrder) {
		this.isOrder = isOrder;
	}

	public String getIsVisit() {
		return isVisit;
	}

	public void setIsVisit(String isVisit) {
		this.isVisit = isVisit;
	}

	public String getLoginMobile() {
		return loginMobile;
	}

	public void setLoginMobile(String loginMobile) {
		this.loginMobile = loginMobile;
	}

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

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getWechatModel() {
		return wechatModel;
	}

	public void setWechatModel(String wechatModel) {
		this.wechatModel = wechatModel;
	}

	public String getRecycleModel() {
		return recycleModel;
	}

	public void setRecycleModel(String recycleModel) {
		this.recycleModel = recycleModel;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getItems() {
		return items;
	}

	public void setItems(String items) {
		this.items = items;
	}

	public Date getInTime() {
		return inTime;
	}

	public void setInTime(Date inTime) {
		this.inTime = inTime;
	}

	public BigDecimal getLastPrice() {
		return lastPrice;
	}

	public void setLastPrice(BigDecimal lastPrice) {
		this.lastPrice = lastPrice;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getBrandId() {
		return brandId;
	}

	public void setBrandId(String brandId) {
		this.brandId = brandId;
	}

	public String getModelId() {
		return modelId;
	}

	public void setModelId(String modelId) {
		this.modelId = modelId;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
}