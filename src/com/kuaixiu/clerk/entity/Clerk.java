package com.kuaixiu.clerk.entity;

import com.common.base.entity.BaseEntity;

/**
* @author: anson
* @CreateDate: 2017年5月17日 下午3:47:08
* @version: V 1.0
* 店员实体类
*/
public class Clerk extends BaseEntity {

	
	private static final long serialVersionUID = -6383977670168621557L;
	/**
	 * 店员id
	 */
	private String id;
	/**
	 * 店员姓名
	 */
	private String name;
	/**
	 * 店员手机号
	 */
	private String tel;
	/**
	 * 店员身份证号
	 */
	private String identityCard;
	/**
	 * 登录密码
	 */
	private String code;
	/**
	 * 积分值
	 */
	private Integer integral;
	/**
	 * 已完成订单数
	 */
	private Integer successOrder;
	/**
	 * 正在进行的订单数 
	 */
	private Integer proceedOrder;
	/**
	 *省 
	 */
	private String province;
	/**
	 * 市
	 */
	private String city;
	/**
	 * 县
	 */
	private String county;
	/**
	 * 街道
	 */
	private String street;
	/**
	 * 区域
	 */
	private String areas;
	/**
	 * 完整地址
	 */
	private String address;
	/**
	 * 是否删除   1 表示删除  默认值为0
	 */
	private Integer isDel;
	/**
	 * 订单生成时间
	 */
	private java.util.Date inTime;
	/**
	 * 订单更新时间
	 */
	private java.util.Date updateTime;
	/**
	 * 订单更改人
	 */
	private String updateUserId;
	/**
	 * 微信号
	 */
	private String wechatId;
	/**
	 * 微信是否实名制  1表示是    2表示否
	 */
	private Integer isRealName;
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
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getIdentityCard() {
		return identityCard;
	}
	public void setIdentityCard(String identityCard) {
		this.identityCard = identityCard;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Integer getIntegral() {
		return integral;
	}
	public void setIntegral(Integer integral) {
		this.integral = integral;
	}
	public Integer getSuccessOrder() {
		return successOrder;
	}
	public void setSuccessOrder(Integer successOrder) {
		this.successOrder = successOrder;
	}
	public Integer getProceedOrder() {
		return proceedOrder;
	}
	public void setProceedOrder(Integer proceedOrder) {
		this.proceedOrder = proceedOrder;
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
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Integer getIsDel() {
		return isDel;
	}
	public void setIsDel(Integer isDel) {
		this.isDel = isDel;
	}
	public java.util.Date getInTime() {
		return inTime;
	}
	public void setInTime(java.util.Date inTime) {
		this.inTime = inTime;
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
	public String getWechatId() {
		return wechatId;
	}
	public void setWechatId(String wechatId) {
		this.wechatId = wechatId;
	}
	public Integer getIsRealName() {
		return isRealName;
	}
	public void setIsRealName(Integer isRealName) {
		this.isRealName = isRealName;
	}
	
	/**
     * get:全部地址
     */
    public String getFullAddress(){
        return this.areas + " " + this.street;
    }
	
	
	
}
