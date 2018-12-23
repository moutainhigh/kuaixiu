package com.kuaixiu.oldtonew.entity;

import com.common.base.entity.BaseEntity;
/**
 * 以旧换新用户类
 * @author Administrator
 *
 */
public class OldToNewUser extends BaseEntity{

	private static final long serialVersionUID = 1754918270010502342L;
	
	private String id;
	/**
	 * 用户姓名
	 */
	private String name;
	
	/**
     * 用户手机
     */
	private String tel;
	/**
	 * 用户验证码
	 */
	private String code;
	/**
	 * 用户旧手机信息
	 */
	private String oldMobile;
	/**
	 * 用户新手机信息
	 */
	private String newMobile;
	/**
	 * 用户地址
	 */
	private String homeAddress;
	 /**
     * 是否删除（0：否；1：是）
     */
    private Integer isDel;
    /**
     * 删除该信息的用户身份
     */
    private String updateUserid ;
    /**
     * 更新时间
     * @return
     */
    private java.util.Date updateTime;
    /**
     * 生成时间
     */
    private java.util.Date inTime;
    
    private String province;
    
    private String city;
    
    private String county;
    
    private String street;
    
    private String areas;
    /**
     * 用户备注
     */
    private String postscript;
    
    /**
     * Date转化为String输出
     * @return
     */
    private String stringDate;
    
    
	public String getStringDate() {
		return stringDate;
	}
	public void setStringDate(String stringDate) {
		this.stringDate = stringDate;
	}
	
	public java.util.Date getInTime() {
		return inTime;
	}
	public void setInTime(java.util.Date inTime) {
		this.inTime = inTime;
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
	public String getCounty() {
		return county;
	}
	public void setCounty(String county) {
		this.county = county;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getAreas() {
		return areas;
	}
	public void setAreas(String areas) {
		this.areas = areas;
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
	public String getId() {
		return id;
	}
	public Integer getIsDel() {
		return isDel;
	}
	public void setIsDel(Integer isDel) {
		this.isDel = isDel;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getOldMobile() {
		return oldMobile;
	}
	public void setOldMobile(String oldMobile) {
		this.oldMobile = oldMobile;
	}
	public String getNewMobile() {
		return newMobile;
	}
	public void setNewMobile(String newMobile) {
		this.newMobile = newMobile;
	}
	public String getHomeAddress() {
		return homeAddress;
	}
	public void setHomeAddress(String homeAddress) {
		this.homeAddress = homeAddress;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public OldToNewUser(String name,String tel, String code, String oldMobile, String newMobile, String homeAddress) {
		this.name=name;
		this.tel = tel;
		this.code = code;
		this.oldMobile = oldMobile;
		this.newMobile = newMobile;
		this.homeAddress = homeAddress;
	}
	public OldToNewUser() {
		super();
	}
	public String getPostscript() {
		return postscript;
	}
	public void setPostscript(String postscript) {
		this.postscript = postscript;
	}
    
	
	
	
}
