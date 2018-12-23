package com.kuaixiu.project.entity;

import com.common.base.entity.BaseEntity;

/**
* @author: anson
* @CreateDate: 2017年7月20日 下午9:13:58
* @version: V 1.0
* 订单取消原因自定义标签
*/
public class CancelReason extends BaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    
	
	/**
     * 
     */
    private String id ;
    /**
     * 取消原因
     */
    private String reason ;
    /**
     * 排序
     */
    private Integer sort ;
    /**
     * 是否删除 0：否；1：是
     */
    private Integer isDel ;
    /**
     * 生成时间	
     */
    private java.util.Date createTime ;
    /**
     * 生成人id
     */
    private String createUserid ;
    /**
     * 更新时间
     */
    private java.util.Date updateTime ;
    /**
     * 更新人id
     */
    private String updateUserid ;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public Integer getSort() {
		return sort;
	}
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	public Integer getIsDel() {
		return isDel;
	}
	public void setIsDel(Integer isDel) {
		this.isDel = isDel;
	}
	public java.util.Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(java.util.Date createTime) {
		this.createTime = createTime;
	}
	public String getCreateUserid() {
		return createUserid;
	}
	public void setCreateUserid(String createUserid) {
		this.createUserid = createUserid;
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
	
     
    
    

}
