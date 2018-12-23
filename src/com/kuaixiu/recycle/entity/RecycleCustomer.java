package com.kuaixiu.recycle.entity;

import java.util.Date;

public class RecycleCustomer {
    /** */
    private String id;

    /** 姓名*/
    private String name;

    /** 身份证号*/
    private String certNo;

    /** 手机号*/
    private String mobile;

    /** 邮箱*/
    private String email;

    /** 省份id*/
    private String province;

    /** 城市id*/
    private String city;

    /** 区县id*/
    private String county;

    /** 具体地址*/
    private String address;

    /** 省市区*/
    private String area;

    /** 地址录入类型 1：支付宝地址 2：系统地址*/
    private Integer addressType;

    /** 用户在商端的身份标识 如果在接口的入参中存在该参数，芝麻网关会自动转化成roleId*/
    private String openId;

    /** 支付宝id*/
    private String userId;

    /** 芝麻风控产品集联合结果  0通过  1不通过*/
    private Integer zmRisk;

    /** 芝麻分*/
    private Integer zmScore;

    /** 人脸核身结果 0通过  1不通过*/
    private Integer zmFace;

    /** 人脸验证拍摄的图片*/
    private String zmFacePic;

    /** 芝麻流水号*/
    private String transactionId;

    /** 芝麻信用订单号*/
    private String zhimaNo;

    /** 渠道来源*/
    private String channelId;

    /** 芝麻信用业务状态 0-未反馈；1-超期；2-结束；3-申请；4-退货；5-用户确认；6-异常终止；7-金额预支；9-异议；*/
    private Integer zhimaStatus;

    /** 订单生成时间*/
    private Date inTime;

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

	public String getCertNo() {
		return certNo;
	}

	public void setCertNo(String certNo) {
		this.certNo = certNo;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public Integer getAddressType() {
		return addressType;
	}

	public void setAddressType(Integer addressType) {
		this.addressType = addressType;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Integer getZmRisk() {
		return zmRisk;
	}

	public void setZmRisk(Integer zmRisk) {
		this.zmRisk = zmRisk;
	}

	public Integer getZmScore() {
		return zmScore;
	}

	public void setZmScore(Integer zmScore) {
		this.zmScore = zmScore;
	}

	public Integer getZmFace() {
		return zmFace;
	}

	public void setZmFace(Integer zmFace) {
		this.zmFace = zmFace;
	}

	public String getZmFacePic() {
		return zmFacePic;
	}

	public void setZmFacePic(String zmFacePic) {
		this.zmFacePic = zmFacePic;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getZhimaNo() {
		return zhimaNo;
	}

	public void setZhimaNo(String zhimaNo) {
		this.zhimaNo = zhimaNo;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public Integer getZhimaStatus() {
		return zhimaStatus;
	}

	public void setZhimaStatus(Integer zhimaStatus) {
		this.zhimaStatus = zhimaStatus;
	}

	public Date getInTime() {
		return inTime;
	}

	public void setInTime(Date inTime) {
		this.inTime = inTime;
	}

    
}