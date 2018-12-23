package com.kuaixiu.screen.entity;

import com.common.base.entity.BaseEntity;

/**
* @author: anson
* @CreateDate: 2017年10月17日 下午5:18:05
* @version: V 1.0
* 碎屏险支持的项目种类
*/
public class ScreenProject extends BaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	   private String id;
	   /**
	    * 品牌id
	    */
	   private String brandId;
	   /**
	    * 碎屏险名称
	    */
	   private String name;
	   /**
	    * 购买费用
	    */
	   private java.math.BigDecimal price;
	   /**
	    * 最高保额
	    */
	   private java.math.BigDecimal maxPrice;
	   /**
	    * 保障期限 单位月 默认12
	    */
	   private Integer maxTime;
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
	    * 项目描述详情
	    */
	   private String detail;
	   /**
	    * 品牌名称 
	    */
	   private String brandName;
	   /**
	    * 产品代码 
	    */
	   private String productCode;
	   
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getBrandId() {
		return brandId;
	}
	public void setBrandId(String brandId) {
		this.brandId = brandId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public java.math.BigDecimal getPrice() {
		return price;
	}
	public void setPrice(java.math.BigDecimal price) {
		this.price = price;
	}
	public java.math.BigDecimal getMaxPrice() {
		return maxPrice;
	}
	public void setMaxPrice(java.math.BigDecimal maxPrice) {
		this.maxPrice = maxPrice;
	}
	public Integer getMaxTime() {
		return maxTime;
	}
	public void setMaxTime(Integer maxTime) {
		this.maxTime = maxTime;
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
	public String getBrandName() {
		return brandName;
	}
	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	   
	   
	   
	   
	   

}
