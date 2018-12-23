package com.system.basic.user.entity;

import java.util.List;

import com.common.base.entity.BaseEntity;

/**
* @author: anson
* @CreateDate: 2017年9月4日 下午5:11:49
* @version: V 1.0
* 用户登录信息
*/
public class LoginUser extends BaseEntity{

	
	private static final long serialVersionUID = 2428201559731220243L;
    /**
     * id
     */
	private String id;
	/**
	 * 用户业务id
	 */
	private String uid;
	/**
	 * 用户信息确认token
	 */
	private String accessToken;
	/**
	 * 刷新的token
	 */
	private String refreshToken;
	/**
	 * token有效期
	 */
	private Long indate;
	/**
	 * 登录用户名
	 */
	private String loginId;
	/**
	 * 微信登录用户openId
	 */
	private String openId;
	/**
	 * 登录方式  0微信端  1手机其他端（不包括微信端）
	 */
	private Integer loginType; 
	/**
	 * 用户类型，1：超级用户，2：系统管理员，3：连锁商管理员， 4：网点管理员， 
	 * 5：维修工程师， 6：用户 ，100店员
	 * 默认值为6   用户登录
	 */
	private Integer userType;
	/**
	 * 创建时间
	 */
	private java.util.Date createTime ;
    /**
     * 更新时间
     */
    private java.util.Date updateTime ;
    /**
     * 用户权限用于权限认证判断
     */
    private List<SysMenu> userAuthoritys;
    /**
     * 当前登录用户sessionId
     */
    private String sessionId;
    
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public String getRefreshToken() {
		return refreshToken;
	}
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	public Long getIndate() {
		return indate;
	}
	public void setIndate(Long indate) {
		this.indate = indate;
	}
	public String getLoginId() {
		return loginId;
	}
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	public Integer getLoginType() {
		return loginType;
	}
	public void setLoginType(Integer loginType) {
		this.loginType = loginType;
	}
	public Integer getUserType() {
		return userType;
	}
	public void setUserType(Integer userType) {
		this.userType = userType;
	}
	public java.util.Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(java.util.Date createTime) {
		this.createTime = createTime;
	}
	public java.util.Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(java.util.Date updateTime) {
		this.updateTime = updateTime;
	}
	public List<SysMenu> getUserAuthoritys() {
		return userAuthoritys;
	}
	public void setUserAuthoritys(List<SysMenu> userAuthoritys) {
		this.userAuthoritys = userAuthoritys;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
    
    
    
    
}
