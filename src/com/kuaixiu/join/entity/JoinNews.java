package com.kuaixiu.join.entity;

import java.util.Date;

import com.common.base.entity.BaseEntity;

public class JoinNews extends BaseEntity{
    /**
	 * 
	 */
	private static final long serialVersionUID = -7044597801149889040L;

	/** 主键*/
    private String id;

    /** 姓名*/
    private String name;

    /** 手机号*/
    private String mobile;

    /** 省份id*/
    private String province;

    /** 市id*/
    private String city;

    /** 区域id*/
    private String area;
    
    /**
     * 街道信息
     */
    private String street;
    
    /** 完整地址*/
    private String address;

    /** excel表填写信息url地址*/
    private String excelPath;

    /** 合同url地址*/
    private String contractPath;

    /** 加盟类型 1连锁商  2门店*/
    private Integer type;

    /** 是否删除 0否 1是*/
    private Integer isDel;

    /** 是否激活 0否  1是*/
    private Integer isSuccess;

    /** 记录生成时间*/
    private Date inTime;

    /** 记录更新时间*/
    private Date updateTime;

    /** 更新人id*/
    private String updateUserid;

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

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getExcelPath() {
		return excelPath;
	}

	public void setExcelPath(String excelPath) {
		this.excelPath = excelPath;
	}

	public String getContractPath() {
		return contractPath;
	}

	public void setContractPath(String contractPath) {
		this.contractPath = contractPath;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getIsDel() {
		return isDel;
	}

	public void setIsDel(Integer isDel) {
		this.isDel = isDel;
	}

	public Integer getIsSuccess() {
		return isSuccess;
	}

	public void setIsSuccess(Integer isSuccess) {
		this.isSuccess = isSuccess;
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

	public String getUpdateUserid() {
		return updateUserid;
	}

	public void setUpdateUserid(String updateUserid) {
		this.updateUserid = updateUserid;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

    
}