package com.kuaixiu.recycle.entity;

import com.common.base.entity.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;
/**
 * 微信小程序回收用户信息实体类
 * @author Administrator
 *
 */
public class RecycleWechat extends BaseEntity{
    /**
	 * 
	 */
	private static final long serialVersionUID = 6505686118746215212L;

	/** */
    private String id;

    /** 手机号*/
    private String mobile;

    /** 手机机型*/
    private String model;

    /** 姓名*/
    private String name;

    /** 地址*/
    private String address;

    /** 昵称(微信号)*/
    private String nick;

    /** 用户头像*/
    private String url;

    /** 性别  0男  1女*/
    private String gender;

    /** 省份*/
    private String province;

    /** 市*/
    private String city;

    /** 所在国家*/
    private String country;

    /** 语言*/
    private String language;

    /** 记录生成时间*/
    private Date inTime;

    /** 记录更新时间*/
    private Date updateTime;
    /**
     *  0未删除  1已删除
     */
    private Integer isDel;
     /**
      * 微信用户openid
      */
    private String openId;
	/**
	 * 微信用户unionId
	 */
	private String unionId;
	/**
	 * \用户登录手机号
	 */
	private String loginMobile;
	/**
     * 微信登录流程会获取到会话密钥
     */
    private String sessionKey;
    /**
     * 经度
     */
    private BigDecimal longitude;
    /**
     * 纬度
     */
    private BigDecimal latitude;
    /**
     * 手机品牌
     */
    private String brand;
    
    private String stringInTime;
    
    /**
	 * 当日已抽奖次数  目前每个手机号每天最多只能抽2次
	 */
	private Integer alreadyUse;
	/**
	 * 总可抽奖次数
	 */
	private Integer totalUse;

	/**
	 * 奖品收货人姓名
	 */
	private String prizeName;

	/**
	 * 奖品收货人手机号
	 */
	private String prizeMobile;

	/**
	 * 奖品收货人省份
	 */
	private String prizeProvince;

	/**
	 * 奖品收货人城市
	 */
	private String prizeCity;

	/**
	 * 奖品收货人地区
	 */
	private String prizeArea;

	/**
	 * 奖品收货人详细地址
	 */
	private String prizeStreet;

	/**
	 * 用户抽奖的手机号
	 */
	private String lotteryMobile;

	/**
	 * 上一次抽奖的时间
	 */
	private Date lastPrizeTime;
    
    
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUnionId() {
		return unionId;
	}

	public void setUnionId(String unionId) {
		this.unionId = unionId;
	}

	public String getLoginMobile() {
		return loginMobile;
	}

	public void setLoginMobile(String loginMobile) {
		this.loginMobile = loginMobile;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
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

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
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

	public Integer getIsDel() {
		return isDel;
	}

	public void setIsDel(Integer isDel) {
		this.isDel = isDel;
	}

	public String getStringInTime() {
		return stringInTime;
	}

	public void setStringInTime(String stringInTime) {
		this.stringInTime = stringInTime;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getSessionKey() {
		return sessionKey;
	}

	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}

	public BigDecimal getLongitude() {
		return longitude;
	}

	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude;
	}

	public BigDecimal getLatitude() {
		return latitude;
	}

	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public Integer getAlreadyUse() {
		return alreadyUse;
	}

	public void setAlreadyUse(Integer alreadyUse) {
		this.alreadyUse = alreadyUse;
	}

	public Integer getTotalUse() {
		return totalUse;
	}

	public void setTotalUse(Integer totalUse) {
		this.totalUse = totalUse;
	}


	public String getPrizeName() {
		return prizeName;
	}

	public void setPrizeName(String prizeName) {
		this.prizeName = prizeName;
	}

	public String getPrizeMobile() {
		return prizeMobile;
	}

	public void setPrizeMobile(String prizeMobile) {
		this.prizeMobile = prizeMobile;
	}

	public String getPrizeProvince() {
		return prizeProvince;
	}

	public void setPrizeProvince(String prizeProvince) {
		this.prizeProvince = prizeProvince;
	}

	public String getPrizeCity() {
		return prizeCity;
	}

	public void setPrizeCity(String prizeCity) {
		this.prizeCity = prizeCity;
	}

	public String getPrizeArea() {
		return prizeArea;
	}

	public void setPrizeArea(String prizeArea) {
		this.prizeArea = prizeArea;
	}

	public String getPrizeStreet() {
		return prizeStreet;
	}

	public void setPrizeStreet(String prizeStreet) {
		this.prizeStreet = prizeStreet;
	}

	public String getLotteryMobile() {
		return lotteryMobile;
	}

	public void setLotteryMobile(String lotteryMobile) {
		this.lotteryMobile = lotteryMobile;
	}

	public Date getLastPrizeTime() {
		return lastPrizeTime;
	}

	public void setLastPrizeTime(Date lastPrizeTime) {
		this.lastPrizeTime = lastPrizeTime;
	}
}