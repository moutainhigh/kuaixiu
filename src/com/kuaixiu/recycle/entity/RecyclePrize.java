package com.kuaixiu.recycle.entity;

import java.util.Date;

import com.common.base.entity.BaseEntity;

/**
 * 
* Description:奖品信息实体类

* @author anson  

* @date 2018年6月12日
 */
public class RecyclePrize extends BaseEntity{
    /**
	 * 
	 */
	private static final long serialVersionUID = -5886839185798698470L;

	public RecyclePrize(){

	}


	public RecyclePrize(int grade,String p){
         this.grade=grade;
         this.prizeProbability=p;
	}

	/** 奖品id*/
    private String prizeId;

    /** 奖品名称*/
    private String prizeName;

    /** 奖品中奖率  单位： 百分比*/
    private String prizeProbability;

    /** 0未删除  1已删除*/
    private Integer isDel;

    /** 奖品总数量*/
    private Integer totalSum;
    
    /** 已抽中次数*/
    private Integer useSum;

    /** 记录生成时间*/
    private Date inTime;

    /** 记录更新时间*/
    private Date updateTime;

    /** 创建人id*/
    private String createUser;
    
    /** 修改人id*/
    private String updateUser;
    
    /**
     * 奖品等级  0未定义  1表示一等奖  2表示二等奖 3三等奖  4四等奖
     */
    private Integer grade;
    
    /**
     * 奖品详情
     */
    private String details;

    /**
     * 奖品批次
     */
	private String batch;
	
	/**
	 * 排序
	 */
	private Integer sort;

	public String getPrizeId() {
		return prizeId;
	}

	public void setPrizeId(String prizeId) {
		this.prizeId = prizeId;
	}

	public String getPrizeName() {
		return prizeName;
	}

	public void setPrizeName(String prizeName) {
		this.prizeName = prizeName;
	}

	public String getPrizeProbability() {
		return prizeProbability;
	}

	public void setPrizeProbability(String prizeProbability) {
		this.prizeProbability = prizeProbability;
	}

	public Integer getIsDel() {
		return isDel;
	}

	public void setIsDel(Integer isDel) {
		this.isDel = isDel;
	}

	public Integer getTotalSum() {
		return totalSum;
	}

	public void setTotalSum(Integer totalSum) {
		this.totalSum = totalSum;
	}

	public Integer getUseSum() {
		return useSum;
	}

	public void setUseSum(Integer useSum) {
		this.useSum = useSum;
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

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public Integer getGrade() {
		return grade;
	}

	public void setGrade(Integer grade) {
		this.grade = grade;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getBatch() {
		return batch;
	}

	public void setBatch(String batch) {
		this.batch = batch;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}
	
	
	

    
}