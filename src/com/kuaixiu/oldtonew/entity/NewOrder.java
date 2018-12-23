package com.kuaixiu.oldtonew.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.common.base.entity.BaseEntity;
import com.kuaixiu.order.entity.OrderDetail;
import com.kuaixiu.shop.entity.Shop;

/**
* @author: anson
* @CreateDate: 2017年6月15日 下午3:48:36
* @version: V 1.0
* 以旧换新订单类
*/
public class NewOrder extends BaseEntity {

	private static final long serialVersionUID = 1L;

	
	 /**
    *
    */
   private String id;
   /**
    * 订单号
    */
   private String orderNo;
   /**
    * 用户id
    */
   private String customerId;
   /**
    * 以旧换新表用户对应唯一id
    */
   private String userId;
   /**
    * 连锁商账号
    */
   private String providerCode;
   
   /**
    * 兑换门店账号
    */
   private String shopCode;
   
   /**
    * 品牌id
    */
   private String brandId;
 
   /**
    * 新机型id
    */
   private String modelId;
  
   /**
    * 兑换方式   0 : 上门兑换  1：到店兑换 2 : 返店兑换
    */
   private Integer convertType;
   /**
    * 下单时订单金额 （预测价格）
    */
   private java.math.BigDecimal orderPrice;
   /**
    * 实际订单金额  工程师预约用户后确认机型后的价格
    * 用户真实应付金额应是该金额减去用户使用的优惠卷金额   即：  (realPrice-couponPrice)
    */
   private java.math.BigDecimal realPrice;
 
   /**
    * 订单状态  0 生成订单  2 待派单 11 待预约  12 已预约   50 已完成  60 已取消
    */
   private Integer orderStatus;
   /**
    * 取消订单类型 0 未取消， 1 用户自行取消，  2 工程师取消， 3 管理员取消
    */
   private Integer cancelType;
   /**
    * 取消前订单状态
    */
   private Integer cancelStatus;
   /**
    * 系统来源
    */
   private String fromSystem;
   /**
    * 是否派单 0 否， 1是
    */
   private Integer isDispatch;
   /**
    * 派单时间
    */
   private java.util.Date dispatchTime;
   /**
    * 工程师ID
    */
   private String engineerId;
   /**
    * 开始检修时间
    */
   private java.util.Date startCheckTime;
   /**
    * 完成检修时间
    */
   private java.util.Date endCheckTime;
   /**
    * 排序
    */
   private Integer sort;
   /**
    * 是否已评价
    */
   private Integer isComment;
   /**
    * 结算状态（-1不需要 0：未结算对账；1：待结算；2：结算单生成）
    */
   private Integer balanceStatus;
   /**
    * 结算时间
    */
   private java.util.Date balanceTime;
   /**
    * 结算单号
    */
   private String balanceNo;
   /**
    * 处理并发 0：未锁定 1：已锁定
    */
   private Integer isLock;
   /**
    * 是否删除 0：否；1：是
    */
   private Integer isDel;
   /**
    *订单生成时间
    */
   private java.util.Date inTime;
   /**
    *订单更新时间
    */
   private java.util.Date updateTime;
   /**
    *更新人id
    */
   private String updateUserid;
   /**
    * 订单完成时间
    */
   private java.util.Date endTime;

   private List<OrderDetail> orderDetails;
   
   private Shop shop;
   
   /**
    * 经度
    */
   private java.math.BigDecimal longitude;
   /**
    * 纬度
    */
   private java.math.BigDecimal latitude;
   /**
    * 客户手机号
    */
   private String customerMobile;
   /**
    * 省
    */
   private String province;
   /**
    * 市
    */
   private String city;
   /**
    * 县
    */
   private String county;
   /**
    * id数组查询，用于查询传参
    */
   private List queryIds;
   /**
    * 状态数组查询，用于查询传参
    */
   private List queryStatusArray;
   /**
    * 是否使用优惠券 0 否 1 是
    */
   private Integer isUseCoupon;
   /**
    * 优惠券ID
    */
   private String couponId;
   /**
    * 优惠码
    */
   private String couponCode;
   /**
    * 优惠码名称
    */
   private String couponName;
   /**
    * 优惠券类型
    */
   private Integer couponType;
   /**
    * 优惠码金额
    */
   private BigDecimal couponPrice;
   /**
    * 距离订单生成后时间  默认为0   1表示超过半小时   2表示超过两小时
    */
   private Integer sendAgreedNews;    
   /**
    * 订单取消原因
    */
   private String cancelReason;
   /**
    * 兑换类型  0换手机  1换话费 
    */
   private Integer selectType;
   
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

public String getCustomerId() {
	return customerId;
}

public void setCustomerId(String customerId) {
	this.customerId = customerId;
}

public String getUserId() {
	return userId;
}

public void setUserId(String userId) {
	this.userId = userId;
}

public String getProviderCode() {
	return providerCode;
}

public void setProviderCode(String providerCode) {
	this.providerCode = providerCode;
}

public String getShopCode() {
	return shopCode;
}

public void setShopCode(String shopCode) {
	this.shopCode = shopCode;
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

public Integer getConvertType() {
	return convertType;
}

public void setConvertType(Integer convertType) {
	this.convertType = convertType;
}

public java.math.BigDecimal getOrderPrice() {
	return orderPrice;
}

public void setOrderPrice(java.math.BigDecimal orderPrice) {
	this.orderPrice = orderPrice;
}

public java.math.BigDecimal getRealPrice() {
	return realPrice;
}

public void setRealPrice(java.math.BigDecimal realPrice) {
	this.realPrice = realPrice;
}

public Integer getOrderStatus() {
	return orderStatus;
}

public void setOrderStatus(Integer orderStatus) {
	this.orderStatus = orderStatus;
}

public Integer getCancelType() {
	return cancelType;
}

public void setCancelType(Integer cancelType) {
	this.cancelType = cancelType;
}

public Integer getCancelStatus() {
	return cancelStatus;
}

public void setCancelStatus(Integer cancelStatus) {
	this.cancelStatus = cancelStatus;
}

public String getFromSystem() {
	return fromSystem;
}

public void setFromSystem(String fromSystem) {
	this.fromSystem = fromSystem;
}

public Integer getIsDispatch() {
	return isDispatch;
}

public void setIsDispatch(Integer isDispatch) {
	this.isDispatch = isDispatch;
}

public java.util.Date getDispatchTime() {
	return dispatchTime;
}

public void setDispatchTime(java.util.Date dispatchTime) {
	this.dispatchTime = dispatchTime;
}

public String getEngineerId() {
	return engineerId;
}

public void setEngineerId(String engineerId) {
	this.engineerId = engineerId;
}

public java.util.Date getStartCheckTime() {
	return startCheckTime;
}

public void setStartCheckTime(java.util.Date startCheckTime) {
	this.startCheckTime = startCheckTime;
}

public java.util.Date getEndCheckTime() {
	return endCheckTime;
}

public void setEndCheckTime(java.util.Date endCheckTime) {
	this.endCheckTime = endCheckTime;
}

public Integer getSort() {
	return sort;
}

public void setSort(Integer sort) {
	this.sort = sort;
}

public Integer getIsComment() {
	return isComment;
}

public void setIsComment(Integer isComment) {
	this.isComment = isComment;
}

public Integer getBalanceStatus() {
	return balanceStatus;
}

public void setBalanceStatus(Integer balanceStatus) {
	this.balanceStatus = balanceStatus;
}

public java.util.Date getBalanceTime() {
	return balanceTime;
}

public void setBalanceTime(java.util.Date balanceTime) {
	this.balanceTime = balanceTime;
}

public String getBalanceNo() {
	return balanceNo;
}

public void setBalanceNo(String balanceNo) {
	this.balanceNo = balanceNo;
}

public Integer getIsLock() {
	return isLock;
}

public void setIsLock(Integer isLock) {
	this.isLock = isLock;
}

public Integer getIsDel() {
	return isDel;
}

public void setIsDel(Integer isDel) {
	this.isDel = isDel;
}

public java.util.Date getInTime() {
	return inTime;
}

public void setInTime(java.util.Date inTime) {
	this.inTime = inTime;
}

public java.util.Date getUpdateTime() {
	return updateTime;
}

public void setUpdateTime(java.util.Date updateTime) {
	this.updateTime = updateTime;
}

public String getUpdateUserid() {
	return updateUserid;
}

public void setUpdateUserid(String updateUserid) {
	this.updateUserid = updateUserid;
}

public java.util.Date getEndTime() {
	return endTime;
}

public void setEndTime(java.util.Date endTime) {
	this.endTime = endTime;
}

public List<OrderDetail> getOrderDetails() {
	return orderDetails;
}

public void setOrderDetails(List<OrderDetail> orderDetails) {
	this.orderDetails = orderDetails;
}

public Shop getShop() {
	return shop;
}

public void setShop(Shop shop) {
	this.shop = shop;
}

public java.math.BigDecimal getLongitude() {
	return longitude;
}

public void setLongitude(java.math.BigDecimal longitude) {
	this.longitude = longitude;
}

public java.math.BigDecimal getLatitude() {
	return latitude;
}

public void setLatitude(java.math.BigDecimal latitude) {
	this.latitude = latitude;
}

public String getCustomerMobile() {
	return customerMobile;
}

public void setCustomerMobile(String customerMobile) {
	this.customerMobile = customerMobile;
}

public String getProvince() {
	return province;
}

public void setProvince(String province) {
	this.province = province;
}

public String getCity() {
	return city;
}

public void setCity(String city) {
	this.city = city;
}

public String getCounty() {
	return county;
}

public void setCounty(String county) {
	this.county = county;
}

public List getQueryIds() {
	return queryIds;
}

public void setQueryIds(List queryIds) {
	this.queryIds = queryIds;
}

public List getQueryStatusArray() {
	return queryStatusArray;
}

public void setQueryStatusArray(List queryStatusArray) {
	this.queryStatusArray = queryStatusArray;
}  

/**
 * get:等待时间（毫秒）
 */
public long getWaitTime() {
    if(this.dispatchTime != null){
        return new Date().getTime() - this.dispatchTime.getTime();
    }
    return 0;
}

public Integer getIsUseCoupon() {
	return isUseCoupon;
}

public void setIsUseCoupon(Integer isUseCoupon) {
	this.isUseCoupon = isUseCoupon;
}

public String getCouponId() {
	return couponId;
}

public void setCouponId(String couponId) {
	this.couponId = couponId;
}

public String getCouponCode() {
	return couponCode;
}

public void setCouponCode(String couponCode) {
	this.couponCode = couponCode;
}

public String getCouponName() {
	return couponName;
}

public void setCouponName(String couponName) {
	this.couponName = couponName;
}

public Integer getCouponType() {
	return couponType;
}

public void setCouponType(Integer couponType) {
	this.couponType = couponType;
}

public BigDecimal getCouponPrice() {
	return couponPrice;
}

public void setCouponPrice(BigDecimal couponPrice) {
	this.couponPrice = couponPrice;
}

public Integer getSendAgreedNews() {
	return sendAgreedNews;
}

public void setSendAgreedNews(Integer sendAgreedNews) {
	this.sendAgreedNews = sendAgreedNews;
}

public String getCancelReason() {
	return cancelReason;
}

public void setCancelReason(String cancelReason) {
	this.cancelReason = cancelReason;
}

public Integer getSelectType() {
	return selectType;
}

public void setSelectType(Integer selectType) {
	this.selectType = selectType;
}



   
   
   
}
