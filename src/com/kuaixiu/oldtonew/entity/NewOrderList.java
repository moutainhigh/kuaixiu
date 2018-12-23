package com.kuaixiu.oldtonew.entity;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.common.base.entity.BaseEntity;

/**
* @author: anson
* @CreateDate: 2017年6月20日 上午9:01:18
* @version: V 1.0
* 以旧换新订单列表展示包装类
*/
public class NewOrderList extends BaseEntity{

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
    *订单生成时间
    */
   private java.util.Date inTime;
   /**
    * 客户姓名
    */
   private String customerName;
   /**
    * 客户手机号
    */
   private String customerMobile;
   /**
    * 旧机型
    */
   private String oldMobile;
   /**
    * 新机型
    */
   private String newMobile;
   /**
    * 工程师id
    */
   private String engineerId;
   /**
    * 工程师姓名
    */
   private String engineerName;
   /**
    * 工程师电话  
    */
   private String engineerNumber;
   /**
    * 订单状态
    */
   private Integer orderStatus;
   /**
    * 订单完成时间
    */
   private java.util.Date endTime;
   /**
    * 维修门店
    */
   private String shopName;
   /**
    * 维修门店编号
    */
   private String shopCode;
   /**
    * 维修门店负责人
    */
   private String shopManagerName;
   /**
    * 维修门店号码
    */
   private String shopMobile;
   /**
    * 门店负责人手机号码
    */
   private String shopManagerMobile;
   /**
    * 订单参考金额(以旧换新)
    */
   private java.math.BigDecimal orderPrice;
   /**
    * 当前系统时间  格式类似  2014-04-22 15:47:06
    */
   private String sysTime;
   /**
    * 派单时间
    */
   private java.util.Date dispatchTime;
   /**
    * 取消订单状态
    */
   private Integer cancelType;
   /**
    * 客户完整地址
    */
   private String fullAddress;
   /**
    * 用户备注
    */
   private String postScript;
   /**
    * 工程师联系客户后填写的预约机型
    */
   private String agreedModel;
   /**
    * 以旧换新机型颜色
    */
   private String color;
   /**
    * 以旧换新机型内存
    */
   private Integer memory;
   /**
    * 手机网络类型   
    */
   private String edition;
   /**
    * 以旧换新是否预约  0表示是   1表示否 
    */ 
   private Integer isAgreed;
   /**
    * 以旧换新预约时间
    */
   private String agreedTime;
   /**
    * 工程师联系客户后确认的预约价格
    */
   private java.math.BigDecimal agreedPrice;
   /**
    * 订单真实价格
    */
   private java.math.BigDecimal realPrice;
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
    * 结算状态  -1不需要 0：未结算对账；1：待结算；2：结算单生成 
    */
   private Integer balanceStatus;
   /**
    * 优惠券开始有效时间
    */
   private String couponBeginTime ;
   /**
    * 优惠券结束有效时间
    */
   private String couponEndTime ;
   /**
    * 订单是否指派  0否  1是
    */
   private Integer isDispatch;
   /**
    * 预约信息为其他机型的信息
    */
   private String agreedOther;
   /**
    * 用户地址
    */
   private String address;
   /**
    * 导出格式下单时间 
    */
   private String exportInTime;
   /**
    * 导出格式完成时间
    */
   private String exportEndTime;
   /**
    * 订单状态导出模式
    */
   private String exportOrderStatus;
   /**
    * 订单取消原因
    */
   private String cancelReason;
   /**
    * 兑换类型 0换手机  1换话费
    */
   private Integer selectType;
   /**
    * 是否评价  0否  1是
    */
   private Integer isComment; 
   
   
public Integer getIsComment() {
	return isComment;
}
public void setIsComment(Integer isComment) {
	this.isComment = isComment;
}
public Integer getSelectType() {
	return selectType;
}
public void setSelectType(Integer selectType) {
	this.selectType = selectType;
}
public String getShopName() {
	return shopName;
}
public void setShopName(String shopName) {
	this.shopName = shopName;
}
public java.math.BigDecimal getOrderPrice() {
	return orderPrice;
}
public void setOrderPrice(java.math.BigDecimal orderPrice) {
	this.orderPrice = orderPrice;
}
public String getSysTime() {
	 SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	 return df.format(new Date());
}

public java.util.Date getDispatchTime() {
	return dispatchTime;
}
public void setDispatchTime(java.util.Date dispatchTime) {
	this.dispatchTime = dispatchTime;
}

public Integer getCancelType() {
	return cancelType;
}
public void setCancelType(Integer cancelType) {
	this.cancelType = cancelType;
}

public String getShopCode() {
	return shopCode;
}
public void setShopCode(String shopCode) {
	this.shopCode = shopCode;
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
public java.util.Date getInTime() {
	return inTime;
}
public void setInTime(java.util.Date inTime) {
	this.inTime = inTime;
}
public String getCustomerName() {
	return customerName;
}
public void setCustomerName(String customerName) {
	this.customerName = customerName;
}
public String getCustomerMobile() {
	return customerMobile;
}
public void setCustomerMobile(String customerMobile) {
	this.customerMobile = customerMobile;
}
public String getOldMobile() {
	return oldMobile;
}
public void setOldMobile(String oldMobile) {
	this.oldMobile = oldMobile;
}
public String getNewMobile() {
	return newMobile;
}
public void setNewMobile(String newMobile) {
	this.newMobile = newMobile;
}
public String getEngineerId() {
	return engineerId;
}
public void setEngineerId(String engineerId) {
	this.engineerId = engineerId;
}
public String getEngineerName() {
	return engineerName;
}
public void setEngineerName(String engineerName) {
	this.engineerName = engineerName;
}
public String getEngineerNumber() {
	return engineerNumber;
}
public void setEngineerNumber(String engineerNumber) {
	this.engineerNumber = engineerNumber;
}
public Integer getOrderStatus() {
	return orderStatus;
}
public void setOrderStatus(Integer orderStatus) {
	this.orderStatus = orderStatus;
}
public java.util.Date getEndTime() {
	return endTime;
}
public void setEndTime(java.util.Date endTime) {
	this.endTime = endTime;
}


public java.math.BigDecimal getAgreedPrice() {
	return agreedPrice;
}
public void setAgreedPrice(java.math.BigDecimal agreedPrice) {
	this.agreedPrice = agreedPrice;
}
public String getFullAddress() {
	return fullAddress;
}
public void setFullAddress(String fullAddress) {
	this.fullAddress = fullAddress;
}

public String getPostScript() {
	return postScript;
}
public void setPostScript(String postScript) {
	this.postScript = postScript;
}

public String getAgreedModel() {
	return agreedModel;
}
public void setAgreedModel(String agreedModel) {
	this.agreedModel = agreedModel;
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



public java.math.BigDecimal getRealPrice() {
	return realPrice;
}
public void setRealPrice(java.math.BigDecimal realPrice) {
	this.realPrice = realPrice;
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
public Integer getIsAgreed() {
	return isAgreed;
}
public void setIsAgreed(Integer isAgreed) {
	this.isAgreed = isAgreed;
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
public Integer getBalanceStatus() {
	return balanceStatus;
}
public void setBalanceStatus(Integer balanceStatus) {
	this.balanceStatus = balanceStatus;
}
public String getCouponBeginTime() {
	return couponBeginTime;
}
public void setCouponBeginTime(String couponBeginTime) {
	this.couponBeginTime = couponBeginTime;
}
public String getCouponEndTime() {
	return couponEndTime;
}
public void setCouponEndTime(String couponEndTime) {
	this.couponEndTime = couponEndTime;
}
public Integer getIsDispatch() {
	return isDispatch;
}
public void setIsDispatch(Integer isDispatch) {
	this.isDispatch = isDispatch;
}
public String getAgreedOther() {
	return agreedOther;
}
public void setAgreedOther(String agreedOther) {
	this.agreedOther = agreedOther;
}
public String getAddress() {
	return address;
}
public void setAddress(String address) {
	this.address = address;
}
public String getExportInTime() {
	return exportInTime;
}
public void setExportInTime(String exportInTime) {
	this.exportInTime = exportInTime;
}
public String getExportEndTime() {
	return exportEndTime;
}
public void setExportEndTime(String exportEndTime) {
	this.exportEndTime = exportEndTime;
}
public String getExportOrderStatus() {
	return exportOrderStatus;
}
public void setExportOrderStatus(String exportOrderStatus) {
	this.exportOrderStatus = exportOrderStatus;
}
public String getShopManagerName() {
	return shopManagerName;
}
public void setShopManagerName(String shopManagerName) {
	this.shopManagerName = shopManagerName;
}
public String getShopMobile() {
	return shopMobile;
}
public void setShopMobile(String shopMobile) {
	this.shopMobile = shopMobile;
}
public String getShopManagerMobile() {
	return shopManagerMobile;
}
public void setShopManagerMobile(String shopManagerMobile) {
	this.shopManagerMobile = shopManagerMobile;
}
public String getAgreedTime() {
	return agreedTime;
}
public void setAgreedTime(String agreedTime) {
	this.agreedTime = agreedTime;
}
public String getCancelReason() {
	return cancelReason;
}
public void setCancelReason(String cancelReason) {
	this.cancelReason = cancelReason;
}



   
}
