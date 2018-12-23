package com.kuaixiu.card.entity;

import com.common.base.entity.BaseEntity;

import java.util.Date;

public class ExpressCard extends BaseEntity{
    private static final long serialVersionUID = 2318700146914236028L;
    /** uuid*/
    private String id;

    /** 号卡批次uuid*/
    private String batchId;

    /** 站点id*/
    private String stationId;

    /** 发货时间*/
    private Date sendTime;

    /** 物流单号*/
    private String expressNumber;

    /** 物流公司*/
    private String expressCompany;

    /** 发货城市*/
    private String sendCity;

    /** 起始ICCID*/
    private String startIccid;

    /** 结束ICCID*/
    private String endIccid;

    /** 数量*/
    private Integer sum;

    /** 号卡类型 0小白卡   1即买即通卡*/
    private Integer cardType;

    /** 号卡名称 0白金卡 1抖音卡 2鱼卡 3 49元不限流量卡  4  99元不限流量卡  5  199元不限流量卡*/
    private Integer cardName;

    /**
     * 记录生成时间
     */
    private Date inTime;

    /**
     * 推送状态  0成功   1失败
     */
    private Integer status;

    /**
     * 推送失败信息
     */
    private String msg;

    /**
     * 批次号
     */
    private String batch;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public String getExpressNumber() {
        return expressNumber;
    }

    public void setExpressNumber(String expressNumber) {
        this.expressNumber = expressNumber;
    }

    public String getExpressCompany() {
        return expressCompany;
    }

    public void setExpressCompany(String expressCompany) {
        this.expressCompany = expressCompany;
    }

    public String getSendCity() {
        return sendCity;
    }

    public void setSendCity(String sendCity) {
        this.sendCity = sendCity;
    }

    public String getStartIccid() {
        return startIccid;
    }

    public void setStartIccid(String startIccid) {
        this.startIccid = startIccid;
    }

    public String getEndIccid() {
        return endIccid;
    }

    public void setEndIccid(String endIccid) {
        this.endIccid = endIccid;
    }

    public Integer getSum() {
        return sum;
    }

    public void setSum(Integer sum) {
        this.sum = sum;
    }

    public Integer getCardType() {
        return cardType;
    }

    public void setCardType(Integer cardType) {
        this.cardType = cardType;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }
}