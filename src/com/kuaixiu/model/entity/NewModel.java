package com.kuaixiu.model.entity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.common.base.entity.BaseEntity;

/**
* @author: anson
* @CreateDate: 2017年6月13日 下午4:55:00
* @version: V 1.0
* 以旧换新支持兑换的机型
*/
public class NewModel extends BaseEntity {

	private static final long serialVersionUID = 1L;

	 /**
     * 
     */
    private String id ;
    /**
     * 机型名称
     */
    private String name ;
    /**
     * 品牌id
     */
    private String brandId ;
    /**
     * 品牌名称
     */
    private String brandName ;
    /**
     * 
     */
    private String color ;
    /**
     * 排序
     */
    private Integer sort ;
    /**
     * 是否删除 0：否；1：是
     */
    private Integer isDel ;
    /**
     * 
     */
    private java.util.Date createTime ;
    /**
     * 
     */
    private String createUserid ;
    /**
     * 
     */
    private java.util.Date updateTime ;
    /**
     * 
     */
    private String updateUserid ;
    
    /**
     * 机型对应维修费用，机型导入时使用
     */
    private Map<String, BigDecimal> repairCostMap;
    
    /**
     * 机型支持的维修项目
     */
    private List<RepairCost> repairCosts;
    /**
     * 机型内存
     */
    private Integer memory;
    /**
     * 机型价格
     */
    private java.math.BigDecimal price ;
    /**
     * 是否上架  默认为1上架 ，2表示未上架
     */
    private Integer isPutaway;
    /**
     * 机型图标
     */
    private String logo;
    /**
     * 网络类型 
     */
    private String edition;
    
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public Integer getIsPutaway() {
		return isPutaway;
	}
	public void setIsPutaway(Integer isPutaway) {
		this.isPutaway = isPutaway;
	}
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
	public String getBrandId() {
		return brandId;
	}
	public void setBrandId(String brandId) {
		this.brandId = brandId;
	}
	public String getBrandName() {
		return brandName;
	}
	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
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
	public Map<String, BigDecimal> getRepairCostMap() {
		return repairCostMap;
	}
	public void setRepairCostMap(Map<String, BigDecimal> repairCostMap) {
		this.repairCostMap = repairCostMap;
	}
	public List<RepairCost> getRepairCosts() {
		return repairCosts;
	}
	public void setRepairCosts(List<RepairCost> repairCosts) {
		this.repairCosts = repairCosts;
	}
	
	public Integer getMemory() {
		return memory;
	}
	public void setMemory(Integer memory) {
		this.memory = memory;
	}
	public java.math.BigDecimal getPrice() {
		return price;
	}
	public void setPrice(java.math.BigDecimal price) {
		this.price = price;
	}
    
	
	public String getEdition() {
		return edition;
	}
	public void setEdition(String edition) {
		this.edition = edition;
	}
	/**
     * get:
     */
    public String[] getColors(){
        if(this.color != null){
            String formatColor = this.color;
            //以,开头
            if(formatColor.startsWith(",")){
                formatColor = formatColor.substring(1);
            }
            //以,结尾
            if(formatColor.endsWith(",")){
                formatColor = formatColor.substring(0,color.lastIndexOf(","));
            }
            return formatColor.split(",");
        }
        return null;
    }
    
    
    
    
    
    
    
}
