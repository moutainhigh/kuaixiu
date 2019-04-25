package com.kuaixiu.recycle.entity;

import com.common.base.entity.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 

* Description:回收订单表

* @author anson  

* @date 2018年6月12日
 */
public class RecycleOrder extends BaseEntity{
    /**
	 * 
	 */
	private static final long serialVersionUID = -4656164576514914363L;
	
	/**
	 *1订单已提交
	 */
	public static final Integer COMMIT=1;
	/**
	 * 2预支付转账成功
	 */
	public static final Integer PREPARE_SUCCESS=2;
	/**
	 * 3预支付转账失败
	 */
	public static final Integer PREPARE_FAIL=3;
	/**
	 * 4支付尾款失败
	 */
	public static final Integer FINAL_FAIL=4;
	/**
	 * 5扣款失败
	 */
	public static final Integer FUND_FAIL=5;
	/**
	 * 50订单已完成
	 */
	public static final Integer SUCCESS=50;
	/**
	 * 60订单已取消
	 */
	public static final Integer CANCEL=60;
	

	/** */
    private String id;

    /** 回收订单号*/
    private String orderNo;

    /** 回收询价流水编号*/
    private String checkId;

    /** 支付id*/
    private String payMentId;

    /** 支付宝id*/
    private String userId;

    /** 0信用回收   1普通回收*/
    private Integer recycleType;
    
    /**
     * 回收类型 1支付宝收款  2话费充值
     */
    private Integer exchangeType;
    
    /** 0预支付订单  1正式订单*/
    private Integer orderType;

    /**  支付手机号/充值手机号*/
    private String payMobile;

    /** 产品名称*/
    private String productName;

    /** 信用预支付比例*/
    private Integer percent;

    /** 预支付金额*/
    private BigDecimal preparePrice;

    /** 预支付总金额*/
    private BigDecimal price;

    private String strPrice;

    /** 议价后金额*/
    private BigDecimal negotiationPrice;

    /**
     * 最终支付总金额
     */
    private BigDecimal finalPrice;
    
    /** 订单来源 0支付宝  1微信公众号 2欢GO
     * 
     * */
    private Integer sourceType;

    /** 是否删除  0否   1是*/
    private Integer isDel;

    /** 订单状态
     *  1订单已提交  2预支付转账成功 3预支付转账失败  4支付尾款失败  5扣款失败  50订单已完成  60订单已取消
     */
    private Integer orderStatus;

    /** 订单生成时间*/
    private Date inTime;

    /** 订单更新时间*/
    private Date updateTime;

    /** 快递取件时间*/
    private String takeTime;

    /** 手机图片地址*/
    private String imagePath;

    /** 检测详情*/
    private String detail;

    /** 顺丰订单号*/
    private String sfOrderNo;

    /** 回收用户信息id*/
    private String customerId;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 芝麻信用代扣订单id
     */
    private String payRefundId;
    /**
     * 快递类型 1超人系统推送  2用户自行邮寄
     */
    private Integer mailType;
    /**
     * 用户备注
     */
    private String note;
    /**
     * 姓名
     */
    private String name;
    /**
     * 详细地址
     */
    private String address;
    /**
     * 省市区
     */
    private String area;
    /**
     * 系统来源
     */
    private String fm;
    
    /**
     * 微信用户openId
     */
    private String wechatOpenId;

	/**
	 * 是否拍卖 默认0否  1是
	 */
	private Integer isSale;

	/**
	 * 加价订单号
	 */
	private String increaseOrderNo;

	/**
	 * 加价券id
	 */
	private String couponId;
	/**
	 * 是否使用加价券
	 */
	private String isCoupon;

	public String getIsCoupon() {
		return isCoupon;
	}

	public void setIsCoupon(String isCoupon) {
		this.isCoupon = isCoupon;
	}

	public String getCouponId() {
		return couponId;
	}

	public BigDecimal getNegotiationPrice() {
		return negotiationPrice;
	}

	public void setNegotiationPrice(BigDecimal negotiationPrice) {
		this.negotiationPrice = negotiationPrice;
	}

	public void setCouponId(String couponId) {
		this.couponId = couponId;
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

	public String getCheckId() {
		return checkId;
	}

	public void setCheckId(String checkId) {
		this.checkId = checkId;
	}

	public String getPayMentId() {
		return payMentId;
	}

	public void setPayMentId(String payMentId) {
		this.payMentId = payMentId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Integer getRecycleType() {
		return recycleType;
	}

	public void setRecycleType(Integer recycleType) {
		this.recycleType = recycleType;
	}

	public Integer getOrderType() {
		return orderType;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}

	public String getPayMobile() {
		return payMobile;
	}

	public void setPayMobile(String payMobile) {
		this.payMobile = payMobile;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Integer getPercent() {
		return percent;
	}

	public void setPercent(Integer percent) {
		this.percent = percent;
	}

	public BigDecimal getPreparePrice() {
		return preparePrice;
	}

	public void setPreparePrice(BigDecimal preparePrice) {
		this.preparePrice = preparePrice;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getStrPrice() {
		return price.setScale(2).toString();
	}

	public void setStrPrice(String strPrice) {
		this.strPrice = strPrice;
	}

	public Integer getSourceType() {
		return sourceType;
	}

	public void setSourceType(Integer sourceType) {
		this.sourceType = sourceType;
	}

	public Integer getIsDel() {
		return isDel;
	}

	public void setIsDel(Integer isDel) {
		this.isDel = isDel;
	}

	public Integer getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
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

	public String getTakeTime() {
		return takeTime;
	}

	public void setTakeTime(String takeTime) {
		this.takeTime = takeTime;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getSfOrderNo() {
		return sfOrderNo;
	}

	public void setSfOrderNo(String sfOrderNo) {
		this.sfOrderNo = sfOrderNo;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getPayRefundId() {
		return payRefundId;
	}

	public void setPayRefundId(String payRefundId) {
		this.payRefundId = payRefundId;
	}

	public BigDecimal getFinalPrice() {
		return finalPrice;
	}

	public void setFinalPrice(BigDecimal finalPrice) {
		this.finalPrice = finalPrice;
	}

	public Integer getExchangeType() {
		return exchangeType;
	}

	public void setExchangeType(Integer exchangeType) {
		this.exchangeType = exchangeType;
	}


	public Integer getMailType() {
		return mailType;
	}

	public void setMailType(Integer mailType) {
		this.mailType = mailType;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getFm() {
		return fm;
	}

	public void setFm(String fm) {
		this.fm = fm;
	}

	public String getWechatOpenId() {
		return wechatOpenId;
	}

	public void setWechatOpenId(String wechatOpenId) {
		this.wechatOpenId = wechatOpenId;
	}


	public Integer getIsSale() {
		return isSale;
	}

	public void setIsSale(Integer isSale) {
		this.isSale = isSale;
	}

	public String getIncreaseOrderNo() {
		return increaseOrderNo;
	}

	public void setIncreaseOrderNo(String increaseOrderNo) {
		this.increaseOrderNo = increaseOrderNo;
	}
}