package com.kuaixiu.recycle.entity;

import java.util.Date;

import com.common.base.entity.BaseEntity;
/**
 * 
 * @author Administrator
 * 企业回收信息
 */
public class RecycleCompany extends BaseEntity{
    /**
	 * 
	 */
	private static final long serialVersionUID = -4247269068253639684L;

	/** */
    private String id;

    /** 企业名称*/
    private String name;

    /** 旧机机型*/
    private String model;

    /** 回收日期*/
    private Date recycleTime;

    /** 客户经理*/
    private String customerManager;

    /** 联系电话*/
    private String mobile;

    /** 是否删除 0：否；1：是*/
    private Integer isDel;

    /** 记录生成时间*/
    private Date inTime;

    /** 记录更新时间*/
    private Date updateTime;
    
    /**
     * 查询回收开始时间，用于查询传参
     */
    private String recycleStartTime;
    /**
     * 查询回收结束时间，用于查询传参
     */
    private String recycleEndTime;
    
    /**
     * 订单备注，处理信息 
     */
    private String note;
    
    /**
     * 订单状态  默认0未处理   1已处理
     */
    private Integer orderStatus;
    
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

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}



	public Date getRecycleTime() {
		return recycleTime;
	}

	public void setRecycleTime(Date recycleTime) {
		this.recycleTime = recycleTime;
	}

	public String getCustomerManager() {
		return customerManager;
	}

	public void setCustomerManager(String customerManager) {
		this.customerManager = customerManager;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Integer getIsDel() {
		return isDel;
	}

	public void setIsDel(Integer isDel) {
		this.isDel = isDel;
	}

	public Date getInTime() {
		return inTime;
	}

	public void setInTime(Date inTime) {
		this.inTime = inTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getRecycleStartTime() {
		return recycleStartTime;
	}

	public void setRecycleStartTime(String recycleStartTime) {
		this.recycleStartTime = recycleStartTime;
	}

	public String getRecycleEndTime() {
		return recycleEndTime;
	}

	public void setRecycleEndTime(String recycleEndTime) {
		this.recycleEndTime = recycleEndTime;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Integer getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}
    
	

    
}