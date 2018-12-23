package com.kuaixiu.increaseOrder.entity;

import com.common.base.entity.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 抬价记录
 */
public class IncreaseRecord extends BaseEntity {
    private static final long serialVersionUID = -1134928489457348237L;
    /** id*/
    private String id;

    /** 抬价订单号*/
    private String orderNo;

    /** 对应回收订单号*/
    private String recycleOrderNo;

    /** 微信openId*/
    private String openId;

    /** 加价百分比*/
    private String plan;

    /** 加价时间*/
    private Date inTime;

    /**
     * 微信昵称
     */
    private String nickname;

    /**
     * 微信头像地址
     */
    private String imgUrl;

    private String price;

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

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

    public String getRecycleOrderNo() {
        return recycleOrderNo;
    }

    public void setRecycleOrderNo(String recycleOrderNo) {
        this.recycleOrderNo = recycleOrderNo;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public Date getInTime() {
        return inTime;
    }

    public void setInTime(Date inTime) {
        this.inTime = inTime;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}