package com.kuaixiu.card.entity;

import com.common.base.entity.BaseEntity;

import java.util.Date;

/**
 * 超人系统提供的物流公司枚举值
 */
public class Express extends BaseEntity{
    private static final long serialVersionUID = 398124358383832072L;
    /** id*/
    private String id;

    /**
     * 物流公司编码
     */
    private String expressCode;

    /** 物流公司名称*/
    private String expressName;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 创建时间
     */
    private Date inTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExpressName() {
        return expressName;
    }

    public void setExpressName(String expressName) {
        this.expressName = expressName;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Date getInTime() {
        return inTime;
    }

    public void setInTime(Date inTime) {
        this.inTime = inTime;
    }

    public String getExpressCode() {
        return expressCode;
    }

    public void setExpressCode(String expressCode) {
        this.expressCode = expressCode;
    }
}