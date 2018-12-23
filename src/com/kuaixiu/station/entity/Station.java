package com.kuaixiu.station.entity;

import com.common.base.entity.BaseEntity;

import java.util.Date;

public class Station extends BaseEntity{
    private static final long serialVersionUID = 4951426676985395108L;
    /** 站点id*/
    private String id;

    /** 站点名称*/
    private String stationName;

    /** 联系人*/
    private String name;

    /** 验机电话*/
    private String tel;

    /** 业务是否开启  0是  1否*/
    private Integer isOpen;

    /** 已分配张数*/
    private Integer distributionSum;

    /** 库存*/
    private Integer repertory;

    /** 地址*/
    private String address;

    /** 导入时间*/
    private Date inTime;

    /**
     * 是否删除  0否  1是
     */
    private Integer isDel;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public Integer getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(Integer isOpen) {
        this.isOpen = isOpen;
    }

    public Integer getDistributionSum() {
        return distributionSum;
    }

    public void setDistributionSum(Integer distributionSum) {
        this.distributionSum = distributionSum;
    }

    public Integer getRepertory() {
        return repertory;
    }

    public void setRepertory(Integer repertory) {
        this.repertory = repertory;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getInTime() {
        return inTime;
    }

    public void setInTime(Date inTime) {
        this.inTime = inTime;
    }

    public Integer getIsDel() {
        return isDel;
    }

    public void setIsDel(Integer isDel) {
        this.isDel = isDel;
    }
}