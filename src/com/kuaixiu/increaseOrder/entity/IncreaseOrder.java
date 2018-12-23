package com.kuaixiu.increaseOrder.entity;

import com.common.base.entity.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 *  翼回收  抬价订单
 */
public class IncreaseOrder extends BaseEntity{
    private static final long serialVersionUID = -9212796032745182417L;
    /** 订单号*/
    private String orderNo;

    /** 对应回收订单号*/
    private String recycleOrderNo;

    private BigDecimal price;
    //用于金额传参
    private String strPrice;

    /** 加价百分比*/
    private String plan;

    /** 加价次数*/
    private Integer times;

    /** 加价状态 0加价中  1加价失败  2加价成功*/
    private Integer isSuccess;

    /** 订单生成时间*/
    private Date inTime;
    /** 抬价剩余时间*/
    private String RemainingTime;

    /**
     * 查询参数，开始时间
     */
    private String increaseStartTime;
    /**
     * 查询参数，结束时间
     */
    private String increaseEndTime;

    /** 订单更新时间*/
    private Date updateTime;

    /**
     * 抬价数组
     */
    private String scoreList;

    /**
     * 加价到百分之百所需次数
     */
    private  Integer totalTimes;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getStrPrice() {
        return strPrice;
    }

    public void setStrPrice(String strPrice) {
        this.strPrice = strPrice;
    }

    public String getRecycleOrderNo() {
        return recycleOrderNo;
    }

    public void setRecycleOrderNo(String recycleOrderNo) {
        this.recycleOrderNo = recycleOrderNo;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public String getRemainingTime() {
        return RemainingTime;
    }

    public void setRemainingTime(String remainingTime) {
        RemainingTime = remainingTime;
    }

    public String getIncreaseStartTime() {
        return increaseStartTime;
    }

    public void setIncreaseStartTime(String increaseStartTime) {
        this.increaseStartTime = increaseStartTime;
    }

    public String getIncreaseEndTime() {
        return increaseEndTime;
    }

    public void setIncreaseEndTime(String increaseEndTime) {
        this.increaseEndTime = increaseEndTime;
    }

    public Integer getTimes() {
        return times;
    }

    public void setTimes(Integer times) {
        this.times = times;
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

    public String getScoreList() {
        return scoreList;
    }

    public void setScoreList(String scoreList) {
        this.scoreList = scoreList;
    }

    public Integer getTotalTimes() {
        return totalTimes;
    }

    public void setTotalTimes(Integer totalTimes) {
        this.totalTimes = totalTimes;
    }
}