package com.kuaixiu.zhuanzhuan.entity;

import com.common.base.entity.BaseEntity;

import java.util.Date;

public class AuctionReturnOrder extends BaseEntity{
    private static final long serialVersionUID = -3072212986617772294L;
    /** */
    private String id;

    /** 回收订单号*/
    private String orderNo;

    /** 转转保卖订单id*/
    private String auctionOrderId;

    /** 转转发货物流单号*/
    private String expressCode;

    /** 转转发货物流公司名称*/
    private String expressCompany;

    /** 转转发货时间，如：2018-04-19 18:31:45*/
    private Date doneTime;

    /** 超人平台收货人手机号*/
    private String cneeMobile;

    /** 超人平台收货人名称*/
    private String cneeName;

    /** 超人平台收货人地址*/
    private String cneeAddress;

    /** 超人平台收货人备注*/
    private String cneeRemark;

    /** 超人发送快递付款方式 1-寄付 2-到付*/
    private Integer expressType;

    /** 超人平台记录生成时间*/
    private Date inTime;

    /** 超人平台记录更新时间*/
    private Date updateTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getAuctionOrderId() {
        return auctionOrderId;
    }

    public void setAuctionOrderId(String auctionOrderId) {
        this.auctionOrderId = auctionOrderId;
    }

    public String getExpressCode() {
        return expressCode;
    }

    public void setExpressCode(String expressCode) {
        this.expressCode = expressCode;
    }

    public String getExpressCompany() {
        return expressCompany;
    }

    public void setExpressCompany(String expressCompany) {
        this.expressCompany = expressCompany;
    }

    public Date getDoneTime() {
        return doneTime;
    }

    public void setDoneTime(Date doneTime) {
        this.doneTime = doneTime;
    }

    public String getCneeMobile() {
        return cneeMobile;
    }

    public void setCneeMobile(String cneeMobile) {
        this.cneeMobile = cneeMobile;
    }

    public String getCneeName() {
        return cneeName;
    }

    public void setCneeName(String cneeName) {
        this.cneeName = cneeName;
    }

    public String getCneeAddress() {
        return cneeAddress;
    }

    public void setCneeAddress(String cneeAddress) {
        this.cneeAddress = cneeAddress;
    }

    public String getCneeRemark() {
        return cneeRemark;
    }

    public void setCneeRemark(String cneeRemark) {
        this.cneeRemark = cneeRemark;
    }

    public Integer getExpressType() {
        return expressType;
    }

    public void setExpressType(Integer expressType) {
        this.expressType = expressType;
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
}