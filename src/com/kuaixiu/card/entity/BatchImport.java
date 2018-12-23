package com.kuaixiu.card.entity;

import com.common.base.entity.BaseEntity;

import java.util.Date;

/**
 * 电渠批次号卡
 */
public class BatchImport extends BaseEntity {
    private static final long serialVersionUID = -2832377452723792417L;
    /** 批次号*/
    private String batchId;

    /** 所属地市*/
    private String province;

    /** 数量*/
    private Integer sum;

    /** 号卡类型 0小白卡   1即买即通卡*/
    private Integer type;

    /** 号卡名称 0白金卡 1抖音卡 2鱼卡 3 49元不限流量卡  4  99元不限流量卡  5  199元不限流量卡*/
    private Integer cardName;

    /** 导入时间*/
    private Date inTime;

    /** 订单更新时间*/
    private Date updateTime;

    /** 导入人身份*/
    private String createUserid;

    /** 更改人身份*/
    private String updateUserid;

    /** 是否已分配 0否 1是*/
    private Integer isDistribution;

    /** 已分配数量*/
    private Integer distributionSum;

    /** 号卡失效时间*/
    private Date loseEfficacy;

    /** 是否删除  0否 1是*/
    private Integer isDel;

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public Integer getSum() {
        return sum;
    }

    public void setSum(Integer sum) {
        this.sum = sum;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getCardName() {
        return cardName;
    }

    public void setCardName(Integer cardName) {
        this.cardName = cardName;
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

    public String getCreateUserid() {
        return createUserid;
    }

    public void setCreateUserid(String createUserid) {
        this.createUserid = createUserid;
    }

    public String getUpdateUserid() {
        return updateUserid;
    }

    public void setUpdateUserid(String updateUserid) {
        this.updateUserid = updateUserid;
    }

    public Integer getIsDistribution() {
        return isDistribution;
    }

    public void setIsDistribution(Integer isDistribution) {
        this.isDistribution = isDistribution;
    }

    public Integer getDistributionSum() {
        return distributionSum;
    }

    public void setDistributionSum(Integer distributionSum) {
        this.distributionSum = distributionSum;
    }

    public Date getLoseEfficacy() {
        return loseEfficacy;
    }

    public void setLoseEfficacy(Date loseEfficacy) {
        this.loseEfficacy = loseEfficacy;
    }

    public Integer getIsDel() {
        return isDel;
    }

    public void setIsDel(Integer isDel) {
        this.isDel = isDel;
    }
}