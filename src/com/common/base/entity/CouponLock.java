package com.common.base.entity;

import java.io.Serializable;


/**
 * 优惠码锁对象
 */
@SuppressWarnings("serial")
public class CouponLock implements Serializable {
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 当前日期
     */
    private String nowDay;
    /**
     * 错误次数
     */
    private int errCount;
    /**
     * 累计错误次数
     */
    private int hisErrCount;
    /**
     * 是否锁定
     */
    private boolean isLock;
    /**
     * 开始锁定时间
     */
    private String beginLockTime;
    /**
     * 锁定结束时间
     */
    private String endLockTime;
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getNowDay() {
		return nowDay;
	}
	public void setNowDay(String nowDay) {
		this.nowDay = nowDay;
	}
	public int getErrCount() {
		return errCount;
	}
	public void setErrCount(int errCount) {
		this.errCount = errCount;
	}
	public int getHisErrCount() {
		return hisErrCount;
	}
	public void setHisErrCount(int hisErrCount) {
		this.hisErrCount = hisErrCount;
	}
	public String getBeginLockTime() {
		return beginLockTime;
	}
	public void setBeginLockTime(String beginLockTime) {
		this.beginLockTime = beginLockTime;
	}
	public String getEndLockTime() {
		return endLockTime;
	}
	public void setEndLockTime(String endLockTime) {
		this.endLockTime = endLockTime;
	}
	public boolean isLock() {
		return isLock;
	}
	public void setLock(boolean isLock) {
		this.isLock = isLock;
	}
    
}
