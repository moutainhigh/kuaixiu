package com.kuaixiu.screen.entity;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.common.base.entity.BaseEntity;
import com.kuaixiu.order.entity.OrderDetail;
import com.kuaixiu.shop.entity.Shop;

/**
 * @author: anson
 * @CreateDate: 2017年10月17日 下午4:16:47
 * @version: V 1.0 碎屏险订单实体类
 */
public class ScreenOrder extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;
	/**
	 * 客户姓名
	 */
	private String name;
	/**
	 * 手机号
	 */
	private String mobile;
	/**
	 * 订单号
	 */
	private String orderNo;
	/**
	 * 碎屏险项目id
	 */
	private String projectId;
	/**
	 * 碎屏险项目名称
	 */
	private String projectName;
	/**
	 * 碎屏险品牌
	 */
	private String projectBrand;
	/**
	 * 下单时订单金额 （预测价格）
	 */
	private java.math.BigDecimal orderPrice;
	/**
	 * 实际订单金额
	 */
	private java.math.BigDecimal realPrice;
	/**
	 * 碎屏最高保额
	 */
	private java.math.BigDecimal maxPrice;
	/**
	 * 订单状态  0未付款   1已付款   2退款中   3已退款  4提交失败   5提交成功  10已取消
	 */
	private Integer orderStatus;
	/**
	 * 是否付款 0 未付款 1 已付款 默认0
	 */
	private Integer isPayment;
	/**
	 * 是否退款 0 无需退款 1 退款中 2 退款失败 3 退款成功 默认0
	 */
	private Integer isDrawback;
	/**
	 * 结算状态（-1不需要 0：未结算对账；1：待结算；2：结算单生成） 默认0
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
	 * 是否删除 0：否；1：是 默认0  2 已付款  调用碎屏险下单接口失败
	 */
	private Integer isDel;
	/**
	 * 订单生成时间
	 */
	private java.util.Date inTime;
	/**
	 * 订单更新时间
	 */
	private java.util.Date updateTime;
	/**
	 * 订单完成时间
	 */
	private java.util.Date endTime;
	/**
	 * 退款原因
	 */
	private String refundReason;
	/**
	 * 系统来源
	 */
	private String fromSystem;
	/**
	 * 订单退款时间
	 */
	private java.util.Date refundTime;
	   /**
     * 查询开始时间，用于查询传参
     */
    private String queryStartTime;
    /**
     * 查询结束时间，用于查询传参
     */
    private String queryEndTime;
    /**
     * 错误原因  通过接口向碎屏险公司发送订单信息的错误信息返回
     */
    private String errorReason;
    /**
     * 退款处理结果 
     */
    private String refundResult;
    /**
     * 手机品牌
     */
    private String mobileBrand;
    /**
     * 手机型号
     */
    private String mobileModel;
    /**
     * 手机串码
     */
    private String mobileCode;
    /**
     * 碎屏险过期时间
     */
    private String dueToTime;
    /**
     * 格式化订单生成时间
     * @return
     */
    private String stringInTime;
    /**
     * 格式化订单完成时间
     * @return
     */
    private String stringEndTime;
    
    /**
     * 是否免激活  默认0否  1是
     */
    private Integer isActive;
    
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
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
	public java.math.BigDecimal getMaxPrice() {
		return maxPrice;
	}
	public void setMaxPrice(java.math.BigDecimal maxPrice) {
		this.maxPrice = maxPrice;
	}
	/**
	 * 订单状态  0未付款   1已付款   2退款中   3已退款  4提交失败   5提交成功  10已取消
	 */
	public Integer getOrderStatus() {
		return orderStatus;
	}
	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}
	public Integer getIsPayment() {
		return isPayment;
	}
	public void setIsPayment(Integer isPayment) {
		this.isPayment = isPayment;
	}
	public Integer getIsDrawback() {
		return isDrawback;
	}
	public void setIsDrawback(Integer isDrawback) {
		this.isDrawback = isDrawback;
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
	public java.util.Date getEndTime() {
		return endTime;
	}
	public void setEndTime(java.util.Date endTime) {
		this.endTime = endTime;
	}
	public String getRefundReason() {
		return refundReason;
	}
	public void setRefundReason(String refundReason) {
		this.refundReason = refundReason;
	}
	public String getFromSystem() {
		return fromSystem;
	}
	public void setFromSystem(String fromSystem) {
		this.fromSystem = fromSystem;
	}
	public java.util.Date getRefundTime() {
		return refundTime;
	}
	public void setRefundTime(java.util.Date refundTime) {
		this.refundTime = refundTime;
	}
	public String getQueryStartTime() {
		return queryStartTime;
	}
	public void setQueryStartTime(String queryStartTime) {
		this.queryStartTime = queryStartTime;
	}
	public String getQueryEndTime() {
		return queryEndTime;
	}
	public void setQueryEndTime(String queryEndTime) {
		this.queryEndTime = queryEndTime;
	}
	public String getErrorReason() {
		return errorReason;
	}
	public void setErrorReason(String errorReason) {
		this.errorReason = errorReason;
	}
	public String getRefundResult() {
		return refundResult;
	}
	public void setRefundResult(String refundResult) {
		this.refundResult = refundResult;
	}
	public String getProjectBrand() {
		return projectBrand;
	}
	public void setProjectBrand(String projectBrand) {
		this.projectBrand = projectBrand;
	}
	public String getMobileBrand() {
		return mobileBrand;
	}
	public void setMobileBrand(String mobileBrand) {
		this.mobileBrand = mobileBrand;
	}
	public String getMobileModel() {
		return mobileModel;
	}
	public void setMobileModel(String mobileModel) {
		this.mobileModel = mobileModel;
	}
	public String getMobileCode() {
		return mobileCode;
	}
	public void setMobileCode(String mobileCode) {
		this.mobileCode = mobileCode;
	}
	public String getDueToTime() {
		return dueToTime;
	}
	public void setDueToTime(String dueToTime) {
		this.dueToTime = dueToTime;
	}
	public String getStringInTime() {
		return stringInTime;
	}
	public void setStringInTime(String stringInTime) {
		this.stringInTime = stringInTime;
	}
	public String getStringEndTime() {
		return stringEndTime;
	}
	public void setStringEndTime(String stringEndTime) {
		this.stringEndTime = stringEndTime;
	}
	public Integer getIsActive() {
		return isActive;
	}
	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}
	

	

}
