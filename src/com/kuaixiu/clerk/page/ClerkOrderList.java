package com.kuaixiu.clerk.page;

import java.io.Serializable;

import com.kuaixiu.order.constant.OrderConstant;

/**
* @author: anson
* @CreateDate: 2017年5月23日 下午2:58:30
* @version: V 1.0
* 用于分页显示店员订单列表和积分列表的包装类
*/
public class ClerkOrderList implements Serializable{

	
	private static final long serialVersionUID = -1171956582494442792L;
    
	/**
	 * 订单生成时间 转换后的显示
	 */
	private String inTime;
	/**
	 * 订单状态
	 */
	private Integer OrderStatus;
	/**
	 * 维修手机类型
	 */
	private String model;
	/**
	 * 维修手机颜色
	 */
	private String color;
	/**
	 * 维修项目
	 */
	private String projects;
	/**
	 * 订单价格
	 */
	private java.math.BigDecimal orderPrice;
	/**
	 * 积分兑换状态
	 */
	private int IntegralStatus;
	/**
	 * 积分兑换数目
	 */
	private int IntegralSum;
	/**
	 * 积分获得数目
	 */
	private int getIntegrals;
	/**
	 * 积分兑换生成时间
	 */
	private java.util.Date convertTime;
	/**
	 * 积分获得的时间
	 */
	private java.util.Date getTime;
    /**
     * 订单号 
     */
	private String orderNo;
	/**
	 * 是否已评价  0表示否  1表示是
	 */
	private Integer isComment;
	
	
	
	public Integer getIsComment() {
		return isComment;
	}
	public void setIsComment(Integer isComment) {
		this.isComment = isComment;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getInTime() {
		return inTime;
	}
	public void setInTime(String inTime) {
		this.inTime = inTime;
	}
	public Integer getOrderStatus() {
		return OrderStatus;
	}
	public void setOrderStatus(Integer orderStatus) {
		OrderStatus = orderStatus;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getProjects() {
		return projects;
	}
	public void setProjects(String projects) {
		this.projects = projects;
	}
	public java.math.BigDecimal getOrderPrice() {
		return orderPrice;
	}
	public void setOrderPrice(java.math.BigDecimal orderPrice) {
		this.orderPrice = orderPrice;
	}
	public int getIntegralStatus() {
		return IntegralStatus;
	}
	public void setIntegralStatus(int integralStatus) {
		IntegralStatus = integralStatus;
	}
	public int getIntegralSum() {
		return IntegralSum;
	}
	public void setIntegralSum(int integralSum) {
		IntegralSum = integralSum;
	}
	public int getGetIntegrals() {
		return getIntegrals;
	}
	public void setGetIntegrals(int getIntegrals) {
		this.getIntegrals = getIntegrals;
	}
	public java.util.Date getConvertTime() {
		return convertTime;
	}
	public void setConvertTime(java.util.Date convertTime) {
		this.convertTime = convertTime;
	}
	public java.util.Date getGetTime() {
		return getTime;
	}
	public void setGetTime(java.util.Date getTime) {
		this.getTime = getTime;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	
	 /**
     * 订单状态:
     *  2  待派单           3  待门店收件      5  门店已收件     11 待预约
     *  12 已预约           20 定位故障         30 待用户付款
     *  35 正在维修        40 待客户收件     50 已完成             60 已取消
     */	
	  public String getOrderStatusName() {
	        if(this.OrderStatus == null){
	            return null;
	        }
	        String name = "";
	        switch (OrderStatus) {
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
