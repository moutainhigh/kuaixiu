package com.kuaixiu.screen.entity;

import com.common.base.entity.BaseEntity;

/**
* @author: anson
* @CreateDate: 2017年10月17日 下午5:11:38
* @version: V 1.0
* 支持碎屏险的品牌实体类
*/
public class ScreenBrand extends BaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	   private String id;
	   /**
	    * 品牌名称
	    */
	   private String name;
	   /**
	    * 品牌图标地址
	    */
	   private String logo;
	   /**
	    * 排序
	    */
	   private Integer sort;
	   /**
	    * 是否删除 0：否；1：是   默认0
	    */
	   private Integer isDel;
	   /**
	    *生成时间
	    */
	   private java.util.Date createTime;
	   /**
	    * 创建人的id
	    */
	   private String createUserId;
	   /**
	    *更新时间
	    */
	   private java.util.Date updateTime;
	   /**
	    * 更新人的id
	    */
	   private String updateUserId;
	   /**
	    * 最高保额
	    */
	   private java.math.BigDecimal maxPrice;
	   
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
	public String getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}
	public java.util.Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(java.util.Date updateTime) {
		this.updateTime = updateTime;
	}
	public String getUpdateUserId() {
		return updateUserId;
	}
	public void setUpdateUserId(String updateUserId) {
		this.updateUserId = updateUserId;
	}
	public java.math.BigDecimal getMaxPrice() {
		return maxPrice;
	}
	public void setMaxPrice(java.math.BigDecimal maxPrice) {
		this.maxPrice = maxPrice;
	}
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
    
	   
	   
	
}
