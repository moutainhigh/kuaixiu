package com.kuaixiu.oldtonew.entity;

import java.math.BigDecimal;
import java.util.List;

import com.common.base.entity.BaseEntity;
import com.kuaixiu.order.constant.OrderConstant;
import com.kuaixiu.order.entity.Order;
import com.kuaixiu.order.entity.OrderDetail;

/**
* @author: anson
* @CreateDate: 2017年6月21日 下午2:14:05
* @version: V 1.0
* 用于pc端和安卓端用户查询维修订单和以旧换新两种订单类型列表显示的公共包装类
*/
public class OrderShow extends BaseEntity{

	private static final long serialVersionUID = 1L;
    /**
     * 订单唯一标示
     */
	private String id;
	/**
	 * 以旧换新用户id
	 */
	private String userId;
	/**
	 * 订单号
	 */
	private String orderNo;
	/**
	 * 用户姓名
	 */
	private String customerName;
	/**
	 * 用户号码
	 */
	private String mobile;
	/**
	 * 维修机型
	 */
	private String modelName;
	/**
	 * 门店名字
	 */
	private String shopName;
	/**
	 * 工程师工号
	 */
	private String engineerNumber;
	/**
	 * 工程师名字
	 */
	private String engineerName;
	/**
	 * 下单时间
	 */
	private java.util.Date inTime;
	/**
	 * 预约时间
	 */
	private String agreedTime;
	/**
	 * 订单状态
	 */
	private Integer orderStatus;
	/**
	 * 维修项目id
	 */
	private String projectId;
	/**
	 * 维修项目
	 */
	private String projectName;
	/**
	 * 旧机型
	 */
	private String oldModel;
	/**
	 * 新机型
	 */
    private String newModel;
    /**
     * 工程师联系客户后预约的新机型
     */
    private String agreedModel;
    /**
     * 以旧换新机型颜色
     */
    private String color;
    /**
     * 机身内存
     */
    private Integer memory;
    /**
     * 手机网络类型
     */
    private String edition;
    /**
     * 工程师联系客户后确认的预约价格
     */
    private java.math.BigDecimal agreedPrice;    
    /**
     * 用户地址
     */
    private String address;
    /**
     * 经度
     */
    private java.math.BigDecimal longitude;
    /**
     * 纬度
     */
    private java.math.BigDecimal latitude;
    /**
     * 订单所属链接商
     */
    private String providerName;
    /**
     * 品牌id
     */
    private String brandName;
    /**
     * 机型id
     */
    private String modelId;
    /**
     * 保证金付款方式
     */
    private Integer depositType;
    /**
     * 保证金金额
     */
    private java.math.BigDecimal deposit_price;
    /**
     * 订单付款方式
     */
    private Integer payType;
    /**
     * 订单明细类型
     */
    private Integer orderType;
    
    /**
     * 用户备注
     */
    private String postScript;
    /**
     * 维修参考价
     */
    private java.math.BigDecimal referencePrice;
    /**
     * 是否需要返店维修  
     */ 
    private Integer repairType;
    /**
     * 是否评价
     */
    private Integer isComment;
    /**
     * 维修机型颜色
     */
    private String repairColor;
    /**
     * 订单明细类型
     */
    private Integer type;
    /**
     * 实际订单金额 （预测报价与实际价格不一致时以实际价格为准）
     */
    private java.math.BigDecimal orderPrice;
    /**
     * 实际价格
     */
    private java.math.BigDecimal realPrice;
    /**
     * 针对维修订单的订单详情
     */
    private List<OrderDetail> orderDetails;
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
     * 以旧换新兑换方式  0 : 上门兑换  1：到店兑换 2 : 返店兑换
     */
    private Integer convertType;
    /**
     * 派单时间
     */
    private java.util.Date dispatchTime;
    /**
     * 开始检修时间
     */
    private java.util.Date startCheckTime;
    /**
     * 完成检修时间
     */
    private java.util.Date endCheckTime;
    /**
     * 开始维修时间
     */
    private java.util.Date startRepairTime;
    /**
     * 完成维修时间
     */
    private java.util.Date endRepairTime;
    /**
     * 订单完成时间
     */
    private java.util.Date endTime;
    /**
     * 支付状态
     */
    private Integer payStatus; 
    /**
     * 取消状态 
     */
    private Integer cancelType;
    /**
     * 以旧换新是否已预约   0表示是   1表示否
     */
    private Integer isAgreed;
    /**
     * 工程师预约手动填入的信息 
     */
    private String agreedOther;
    /**
     * 工程师手动填入的预约机型价格
     */
    private java.math.BigDecimal agreedOtherPrice;
    /**
     * 订单更新时间 
     */
    private java.util.Date updateTime;
    
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getModelName() {
		return modelName;
	}
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	public String getShopName() {
		return shopName;
	}
	public void setShopName(String shopName) {
		this.shopName = shopName;
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
	public Integer getOrderStatus() {
		return orderStatus;
	}
	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
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
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getOldModel() {
		return oldModel;
	}
	public void setOldModel(String oldModel) {
		this.oldModel = oldModel;
	}
	public String getNewModel() {
		return newModel;
	}
	public void setNewModel(String newModel) {
		this.newModel = newModel;
	}
	public String getAgreedModel() {
		return agreedModel;
	}
	public void setAgreedModel(String agreedModel) {
		this.agreedModel = agreedModel;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPostScript() {
		return postScript;
	}
	public void setPostScript(String postScript) {
		this.postScript = postScript;
	}
	public java.math.BigDecimal getReferencePrice() {
		return referencePrice;
	}
	public void setReferencePrice(java.math.BigDecimal referencePrice) {
		this.referencePrice = referencePrice;
	}
	public Integer getRepairType() {
		return repairType;
	}
	public void setRepairType(Integer repairType) {
		this.repairType = repairType;
	}
	public Integer getIsComment() {
		return isComment;
	}
	public void setIsComment(Integer isComment) {
		this.isComment = isComment;
	}
	public String getRepairColor() {
		return repairColor;
	}
	public void setRepairColor(String repairColor) {
		this.repairColor = repairColor;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public java.math.BigDecimal getRealPrice() {
		return realPrice;
	}
	public void setRealPrice(java.math.BigDecimal realPrice) {
		this.realPrice = realPrice;
	}
	public java.util.Date getEndCheckTime() {
		return endCheckTime;
	}
	public void setEndCheckTime(java.util.Date endCheckTime) {
		this.endCheckTime = endCheckTime;
	}
	public List<OrderDetail> getOrderDetails() {
		return orderDetails;
	}
	public void setOrderDetails(List<OrderDetail> orderDetails) {
		this.orderDetails = orderDetails;
	}
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String projectId) {
		this.projectId = projectId;
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
	public String getProviderName() {
		return providerName;
	}
	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}
	public String getBrandName() {
		return brandName;
	}
	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}
	public String getModelId() {
		return modelId;
	}
	public void setModelId(String modelId) {
		this.modelId = modelId;
	}
	public Integer getDepositType() {
		return depositType;
	}
	public void setDepositType(Integer depositType) {
		this.depositType = depositType;
	}
	public java.math.BigDecimal getDeposit_price() {
		return deposit_price;
	}
	public void setDeposit_price(java.math.BigDecimal deposit_price) {
		this.deposit_price = deposit_price;
	}
	public Integer getPayType() {
		return payType;
	}
	public void setPayType(Integer payType) {
		this.payType = payType;
	}
	public String getEngineerNumber() {
		return engineerNumber;
	}
	public void setEngineerNumber(String engineerNumber) {
		this.engineerNumber = engineerNumber;
	}
	public String getEngineerName() {
		return engineerName;
	}
	public void setEngineerName(String engineerName) {
		this.engineerName = engineerName;
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
	public Integer getOrderType() {
		return orderType;
	}
	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}
	public Integer getConvertType() {
		return convertType;
	}
	public void setConvertType(Integer convertType) {
		this.convertType = convertType;
	}
	public java.util.Date getDispatchTime() {
		return dispatchTime;
	}
	public void setDispatchTime(java.util.Date dispatchTime) {
		this.dispatchTime = dispatchTime;
	}
	public java.util.Date getStartCheckTime() {
		return startCheckTime;
	}
	public void setStartCheckTime(java.util.Date startCheckTime) {
		this.startCheckTime = startCheckTime;
	}
	public java.util.Date getStartRepairTime() {
		return startRepairTime;
	}
	public void setStartRepairTime(java.util.Date startRepairTime) {
		this.startRepairTime = startRepairTime;
	}
	public java.util.Date getEndRepairTime() {
		return endRepairTime;
	}
	public void setEndRepairTime(java.util.Date endRepairTime) {
		this.endRepairTime = endRepairTime;
	}
	public java.util.Date getEndTime() {
		return endTime;
	}
	public void setEndTime(java.util.Date endTime) {
		this.endTime = endTime;
	}
	public String getAgreedTime() {
		return agreedTime;
	}
	public void setAgreedTime(String agreedTime) {
		this.agreedTime = agreedTime;
	}
	public Integer getPayStatus() {
		return payStatus;
	}
	public void setPayStatus(Integer payStatus) {
		this.payStatus = payStatus;
	}
	public Integer getCancelType() {
		return cancelType;
	}
	public void setCancelType(Integer cancelType) {
		this.cancelType = cancelType;
	}
	public Integer getIsAgreed() {
		return isAgreed;
	}
	public void setIsAgreed(Integer isAgreed) {
		this.isAgreed = isAgreed;
	}
	public java.math.BigDecimal getAgreedPrice() {
		return agreedPrice;
	}
	public void setAgreedPrice(java.math.BigDecimal agreedPrice) {
		this.agreedPrice = agreedPrice;
	}
	public String getAgreedOther() {
		return agreedOther;
	}
	public void setAgreedOther(String agreedOther) {
		this.agreedOther = agreedOther;
	}
	public java.math.BigDecimal getAgreedOtherPrice() {
		return agreedOtherPrice;
	}
	public void setAgreedOtherPrice(java.math.BigDecimal agreedOtherPrice) {
		this.agreedOtherPrice = agreedOtherPrice;
	}
	public java.util.Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(java.util.Date updateTime) {
		this.updateTime = updateTime;
	}
    
	 public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	/**
     * 订单状态:
     *  2  待派单           3  待门店收件      5  门店已收件     11 待预约
     *  12 已预约           20 定位故障         30 待用户付款
     *  35 正在维修        40 待客户收件     50 已完成             60 已取消
     */	
	  public String getOrderStatusName() {
	        if(this.orderStatus == null){
	            return null;
	        }
	        String name = "";
	        switch (orderStatus) {
            case OrderConstant.ORDER_STATUS_DEPOSITED:
                name = "待派单";
                break;
            case OrderConstant.ORDER_STATUS_WAIT_SHOP_SEND_RECEIVE:
                name = "待门店收件";
                break;   
            case OrderConstant.ORDER_STATUS_SHOP_ALERADY_RECEIVE:
                name = "门店已收件";
                break;      
            case OrderConstant.ORDER_STATUS_DISPATCHED:
                name = "待预约";
                break;
            case OrderConstant.ORDER_STATUS_RECEIVED:
                name = "已预约";
                break;
            case OrderConstant.ORDER_STATUS_START_CHECK:
                name = "定位故障";
                break;
            case OrderConstant.ORDER_STATUS_END_REPAIR:
                name = "待用户付款";
                break;  
            case OrderConstant.ORDER_STATUS_REPAIRING:
                name = "正在维修";
                break;      
            case OrderConstant.ORDER_STATUS_WAIT_CUSTOMER_RECEIVE:
                name = "待客户收件";
                break;  
            case OrderConstant.ORDER_STATUS_FINISHED:
            	if(isComment==1){
            		name="已完成";
            	}else{
            		name="待评价";
            	}
                break;
            case OrderConstant.ALREADY_COMMENT:
                name = "已评价";
                break;      
	        case OrderConstant.ORDER_STATUS_CANCEL:
	                name = "已取消";
	                break;
	            default:
	                name = "状态异常";
	                break;
	        }
	        return name;
	    }
    
    
}
