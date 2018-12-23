package com.kuaixiu.recycle.entity;

import java.util.Date;

import com.common.base.entity.BaseEntity;

/**
* @author: anson
* @CreateDate: 2018年5月18日 下午2:23:19
* @version: V 1.0
* 
*/
public class RecycleCompanyNews extends BaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6196605016582350943L;
	
	
	
	
	/** */
    private String id;

    /** 企业id*/
    private String companyId;

    /**
     * 回收品牌
     */
    private String brand;  
    
    /** 旧机机型*/
    private String model;
    
    /** 数量*/
    private Integer sum;
    
    /**
     * 回收机型id
     */
    private String productId;
    
    /**
     * 回收总价格
     */
    private java.math.BigDecimal price;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

    

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public Integer getSum() {
		return sum;
	}

	public void setSum(Integer sum) {
		this.sum = sum;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public java.math.BigDecimal getPrice() {
		return price;
	}

	public void setPrice(java.math.BigDecimal price) {
		this.price = price;
	}
      
   
}
