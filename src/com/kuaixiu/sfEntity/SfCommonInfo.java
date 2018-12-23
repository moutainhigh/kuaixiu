package com.kuaixiu.sfEntity;

import com.common.base.entity.BaseEntity;

/**
* @author: anson
* @CreateDate: 2017年8月15日 上午9:14:31
* @version: V 1.0
* 顺丰快递快速下单公共部分实体类
*/
public class SfCommonInfo extends BaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
  
	/**
	 * 客户订单号 客户自定义
	 */
	private String orderId;
	/**
	 * 快件产品类别
	 * 1 标准快递 2 顺丰特惠 3 电商特惠 5 顺丰次晨 6 顺丰即日 7 电商速配 15 生鲜速配
	 */
    private String expressType;
    /**
     * 付款方式
     * 1 寄方付/寄付月结【默认值】 2 收方付 3 第三方月结卡号支付
     */
	private Integer payMethod;
	/**
	 * 是否下 call（通知收派员上门取件） 类别 描述 1 下 call 0 不下 call【默认值】
	 */
	private Integer isDocall;
	/**
	 * 是否申请运单号 类别 描述 1 申请【默认值】 0 不申请
	 */
	private Integer isGenBilno;
	/**
	 * 是否生成电子运单图片 类别 描述 1 生成【默认值】 0 不生成
	 */
	private Integer isGenEletricPic;
	/**
	 * 顺丰月结卡号 一般是 10 位数字
	 */
	private String custId;
	/**
	 * 月节卡号对应的网点
	 * 月结卡号对应的网点，如果付款方式为第三方月 结卡号支付，则必填
	 */
	private String payArea;
	/**
	 * 要 求 上 门 取 件 开 始 时 间 ， 格 式 ： YYYY-MM-DD HH24:MM:SS，
	 * 示例：2013-7-30 09:30:00， 默认值为系统收到订单的系统时间
	 */
	private String sendStartTime;
	/**
	 * 是否需要签回单号
	 * 1 需要 0 不需要【默认值】
	 */
	private String needReturnTrackingNo;
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
	public String getExpressType() {
		return expressType;
	}
	public void setExpressType(String expressType) {
		this.expressType = expressType;
	}
	public Integer getPayMethod() {
		return payMethod;
	}
	public void setPayMethod(Integer payMethod) {
		this.payMethod = payMethod;
	}
	public Integer getIsDocall() {
		return isDocall;
	}
	public void setIsDocall(Integer isDocall) {
		this.isDocall = isDocall;
	}
	public Integer getIsGenBilno() {
		return isGenBilno;
	}
	public void setIsGenBilno(Integer isGenBilno) {
		this.isGenBilno = isGenBilno;
	}
	public Integer getIsGenEletricPic() {
		return isGenEletricPic;
	}
	public void setIsGenEletricPic(Integer isGenEletricPic) {
		this.isGenEletricPic = isGenEletricPic;
	}
	public String getCustId() {
		return custId;
	}
	public void setCustId(String custId) {
		this.custId = custId;
	}
	public String getPayArea() {
		return payArea;
	}
	public void setPayArea(String payArea) {
		this.payArea = payArea;
	}
	public String getSendStartTime() {
		return sendStartTime;
	}
	public void setSendStartTime(String sendStartTime) {
		this.sendStartTime = sendStartTime;
	}
	public String getNeedReturnTrackingNo() {
		return needReturnTrackingNo;
	}
	public void setNeedReturnTrackingNo(String needReturnTrackingNo) {
		this.needReturnTrackingNo = needReturnTrackingNo;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
