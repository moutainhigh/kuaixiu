package com.kuaixiu.sfDao;

import com.common.base.entity.BaseEntity;

/**
* @author: anson
* @CreateDate: 2017年8月21日 上午11:09:07
* @version: V 1.0
* 顺丰接口返回参数统一类
*/
public class ResultData  extends BaseEntity{

	private static final long serialVersionUID = 1L;
	
	/**
	 * 客户订单号
	 */
	private String orderId;
    /**
     * 运单号
     */
	private String mailNo;
	/**
	 * 路由发生时间
	 */
	private String acceptTime;
	/**
	 * 路由发生地点
	 */
	private String acceptAddress;
	/**
	 * 操作码
	 */
	private String opcode;
	/**
	 * 备注
	 */
	private String remark;
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getMailNo() {
		return mailNo;
	}
	public void setMailNo(String mailNo) {
		this.mailNo = mailNo;
	}
	public String getAcceptTime() {
		return acceptTime;
	}
	public void setAcceptTime(String acceptTime) {
		this.acceptTime = acceptTime;
	}
	public String getAcceptAddress() {
		return acceptAddress;
	}
	public void setAcceptAddress(String acceptAddress) {
		this.acceptAddress = acceptAddress;
	}
	public String getOpcode() {
		return opcode;
	}
	public void setOpcode(String opcode) {
		this.opcode = opcode;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
	
	
	
	
	
	
	
}
