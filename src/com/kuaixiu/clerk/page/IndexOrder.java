package com.kuaixiu.clerk.page;

import java.math.BigDecimal;
import java.util.List;

import com.common.base.entity.BaseEntity;
import com.kuaixiu.order.entity.OrderDetail;
import com.kuaixiu.shop.entity.Shop;

/**
* @author: anson
* @CreateDate: 2017年5月27日 下午2:41:04
* @version: V 1.0
* 
*/
public class IndexOrder extends BaseEntity implements Comparable{

	
	private static final long serialVersionUID = -8301817545632954010L;

	/**
	 * 任务订单号
	 */
	private String orderNo;
	
	/**
	 * 积分订单号
	 */
	private String integralNo;
	/**
	 * 订单类型  1表示积分订单  2表示任务订单
	 */
	private Integer type; 
	/**
	 * 订单时间 用来主页排序
	 */
	private java.util.Date orderTime;
	/**
	 * 获得的积分
	 */
	private Integer integrals;
	/**
	 * String类型 用于前台显示规定样式时间
	 */
	private String inTime;
	
	
	public String getInTime() {
		return inTime;
	}
	public void setInTime(String inTime) {
		this.inTime = inTime;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getIntegralNo() {
		return integralNo;
	}
	public void setIntegralNo(String integralNo) {
		this.integralNo = integralNo;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public java.util.Date getOrderTime() {
		return orderTime;
	}
	public void setOrderTime(java.util.Date orderTime) {
		this.orderTime = orderTime;
	}
	public Integer getIntegrals() {
		return integrals;
	}
	public void setIntegrals(Integer integrals) {
		this.integrals = integrals;
	}
	@Override
	public String toString() {
		return "IndexOrder [orderNo=" + orderNo + ", integralNo=" + integralNo + ", type=" + type + ", orderTime="
				+ orderTime + ", integrals=" + integrals + "]";
	}
	@Override
	public int compareTo(Object o) {
      IndexOrder order=new IndexOrder();
      if(this.getOrderTime().after(order.getOrderTime())){
    	  return -1;
      }
		return 1;
	}
	
   
   
}
