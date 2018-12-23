package com.kuaixiu.zhuanzhuan.entity;

import java.util.Date;

/**
 * @Auther: anson
 * @Date: 2018/7/17
 * @Description: 欢GO抽奖用户信息
 */
public class HappyPrize {
    /** */
    private String id;

    /**
     * 手机品牌
     */
    private String brand;

    /**
     * 手机机型
     */
    private String model;

    /**
     * 记录生成时间
     */
    private Date inTime;

    /**
     * 记录更新时间
     */
    private Date updateTime;

    /**
     * 是否删除  0否  1是
     */
    private Integer isDel;

    /**
     * 当日已抽奖次数  目前每天最多可抽奖3次
     */
    private Integer alreadyUse;

    /**
     * 该用户总可以抽奖次数  保留字段 暂时不用
     */
    private Integer totalUse;

    /**
     * 用户抽奖的手机号
     */
    private String lotteryMobile;

    /**
     * 上一次抽奖时间
     */
    private Date lastPrizeTime;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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