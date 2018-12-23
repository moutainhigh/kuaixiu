package com.kuaixiu.sfEntity;

import com.common.base.entity.BaseEntity;

/**
* @author: anson
* @CreateDate: 2017年8月15日 下午3:08:55
* @version: V 1.0
* 用来保存accessToken和其他访问顺丰api所需参数
*/
public class SfToken extends BaseEntity{

	private static final long serialVersionUID = 1L;

	private String id;
	/**
	 * 订单号
	 */
	private String orderNo;
	/**
	 * 运单号
	 */
	private String mailNo;
	/**
	 * access_token
	 */
	private String accessToken;
	/**
	 *  refresh_token
	 */
	private String  refreshToken;
	/**
	 *  access_token有效期
	 */
	private Long expiresIn;
	/**
	 *  交易流水号
	 */
	private String transMessageId;
	/**
	 *  筛单结果： 1-人工确认，2-可收派 3-不可以收 派 ,
	 *  0-筛单初始化中（调用快速下单后约 5 分钟 会返回顺丰运单号，如查询返回 0 ,请稍候重试）
	 */
	private String filterResult;
	/**
	 *  原寄地代码，如果可收派，此项不能为空
	 */
	private String origincode;
	/**
	 *  目的地代码，如果可收派，此项不能为空
	 */
	private String destCode;
	 /**
     * 生成时间
     */
    private java.util.Date createTime ;
    /**
     * 更新时间
     */
    private java.util.Date updateTime ;
    /**
     * 备注：1-收方超范围， 2-派方超范围， 3-其他 原因
     */
    private String remark;
    
    
    
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
	public String getMailNo() {
		return mailNo;
	}
	public void setMailNo(String mailNo) {
		this.mailNo = mailNo;
	}
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public String getRefreshToken() {
		return refreshToken;
	}
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	public Long getExpiresIn() {
		return expiresIn;
	}
	public void setExpiresIn(Long expiresIn) {
		this.expiresIn = expiresIn;
	}
	public String getTransMessageId() {
		return transMessageId;
	}
	public void setTransMessageId(String transMessageId) {
		this.transMessageId = transMessageId;
	}
	public java.util.Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(java.util.Date createTime) {
		this.createTime = createTime;
	}
	public java.util.Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(java.util.Date updateTime) {
		this.updateTime = updateTime;
	}
	public String getFilterResult() {
		return filterResult;
	}
	public void setFilterResult(String filterResult) {
		this.filterResult = filterResult;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getOrigincode() {
		return origincode;
	}
	public void setOrigincode(String origincode) {
		this.origincode = origincode;
	}
	public String getDestCode() {
		return destCode;
	}
	public void setDestCode(String destCode) {
		this.destCode = destCode;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
}
