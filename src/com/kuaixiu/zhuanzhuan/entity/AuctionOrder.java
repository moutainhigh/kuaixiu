package com.kuaixiu.zhuanzhuan.entity;

import com.common.base.entity.BaseEntity;

import java.util.Date;

public class AuctionOrder extends BaseEntity{
    private static final long serialVersionUID = -9174257480152868056L;
    /** */
    private String id;

    /** 回收订单号*/
    private String orderNo;

    /** 转转保卖订单id*/
    private String auctionOrderId;

    /** 转转类别id*/
    private Integer cateId;

    /** 站点id*/
    private String allyId;

    /**
     * 站点电话
     */
    private String stationTel;

    /** 站点名称*/
    private String stationName;

    /** 站点地址*/
    private String stationAddress;

    /** 转转订单生成时间*/
    private Date createTime;

    /** 超人平台商品描述 如iphone6s*/
    private String productDescription;

    /** 超人平台卖家手机号*/
    private String senderMobile;

    /** 超人平台卖家昵称*/
    private String senderNickname;

    /** 超人平台卖家地址*/
    private String senderAddress;

    /** 超人平台发货物流单号*/
    private String expressCode;

    /** 超人平台发货物流公司名称*/
    private String expressCompany;

    /** 超人平台发货时间*/
    private Date shippedTime;

    /** 转转保卖订单状态,已创建：created*/
    private String orderStatus;

    /** 确认售卖的时间，如：2018-04-19 18:31:45*/
    private Date saleTime;

    /** 超人平台请求的售卖价格  单位分*/
    private Integer superPrice;

    /** 转转预估的价格  单位分*/
    private Integer probablyPrice;

    /** 最终售出的价格  单位分*/
    private Integer realPrice;


    /** 转转订单更新时间*/
    private Date auctionUpdateTime;

    /** 转转收件时间*/
    private Date receiveTime;

    /**
     * 转转分拣完成时间
     */
    private Date sortDoneTime;

    /**
     * 转转分拣备注
     */
    private String  sortRemark;

    /** 包装清单*/
    private String packageList;

    /** 转转平台备注*/
    private String zhuanRemark;

    /** 超人平台备注*/
    private String superRemark;

    /**  质检结果状态，0正常，非0异常*/
    private Integer checkStatus;

    /** 状态描述{1、无法开机|2、仿冒产品|3、非质检商品|4、有开机密码|5、无法质检 }*/
    private String statusDesc;

    /** 工程师有话说*/
    private String checkDesc;

    /** 图片地址数组*/
    private String pics;

    /**  质检物品编码(该编码与实物唯一对应)*/
    private String qcCode;

    /**
     * 质检完成时间
     */
    private Date qcDoneTime;

    /** 申请拍照时间，如：2018-04-19 18:31:45*/
    private Date applyPhotoTime;

    /** 拍照完成时间，如：2018-04-19 18:31:45*/
    private Date donePhotoTime;

    /** 流拍次数*/
    private Integer times;

    /** 物品分类信息*/
    private String cateInfo;

    /** 报告信息*/
    private String report;

    /** 是否删除  0否  1是*/
    private Integer isDel;

    /** 超人平台记录生成时间*/
    private Date inTime;

    /** 超人平台记录更新时间*/
    private Date updateTime;

    /** 转转退货订单id*/
    private String returnId;

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

    public Integer getCateId() {
        return cateId;
    }

    public void setCateId(Integer cateId) {
        this.cateId = cateId;
    }

    public String getAllyId() {
        return allyId;
    }

    public void setAllyId(String allyId) {
        this.allyId = allyId;
    }

    public String getStationTel() {
        return stationTel;
    }

    public void setStationTel(String stationTel) {
        this.stationTel = stationTel;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getStationAddress() {
        return stationAddress;
    }

    public void setStationAddress(String stationAddress) {
        this.stationAddress = stationAddress;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getSenderMobile() {
        return senderMobile;
    }

    public void setSenderMobile(String senderMobile) {
        this.senderMobile = senderMobile;
    }

    public String getSenderNickname() {
        return senderNickname;
    }

    public void setSenderNickname(String senderNickname) {
        this.senderNickname = senderNickname;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
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

    public Date getShippedTime() {
        return shippedTime;
    }

    public void setShippedTime(Date shippedTime) {
        this.shippedTime = shippedTime;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Date getSaleTime() {
        return saleTime;
    }

    public void setSaleTime(Date saleTime) {
        this.saleTime = saleTime;
    }

    public Integer getSuperPrice() {
        return superPrice;
    }

    public void setSuperPrice(Integer superPrice) {
        this.superPrice = superPrice;
    }

    public Integer getProbablyPrice() {
        return probablyPrice;
    }

    public void setProbablyPrice(Integer probablyPrice) {
        this.probablyPrice = probablyPrice;
    }

    public Integer getRealPrice() {
        return realPrice;
    }

    public void setRealPrice(Integer realPrice) {
        this.realPrice = realPrice;
    }

    public Date getAuctionUpdateTime() {
        return auctionUpdateTime;
    }

    public void setAuctionUpdateTime(Date auctionUpdateTime) {
        this.auctionUpdateTime = auctionUpdateTime;
    }

    public Date getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(Date receiveTime) {
        this.receiveTime = receiveTime;
    }

    public Date getSortDoneTime() {
        return sortDoneTime;
    }

    public void setSortDoneTime(Date sortDoneTime) {
        this.sortDoneTime = sortDoneTime;
    }

    public String getSortRemark() {
        return sortRemark;
    }

    public void setSortRemark(String sortRemark) {
        this.sortRemark = sortRemark;
    }

    public String getPackageList() {
        return packageList;
    }

    public void setPackageList(String packageList) {
        this.packageList = packageList;
    }

    public String getZhuanRemark() {
        return zhuanRemark;
    }

    public void setZhuanRemark(String zhuanRemark) {
        this.zhuanRemark = zhuanRemark;
    }

    public String getSuperRemark() {
        return superRemark;
    }

    public void setSuperRemark(String superRemark) {
        this.superRemark = superRemark;
    }

    public Integer getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(Integer checkStatus) {
        this.checkStatus = checkStatus;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    public String getCheckDesc() {
        return checkDesc;
    }

    public void setCheckDesc(String checkDesc) {
        this.checkDesc = checkDesc;
    }

    public String getPics() {
        return pics;
    }

    public void setPics(String pics) {
        this.pics = pics;
    }

    public String getQcCode() {
        return qcCode;
    }

    public void setQcCode(String qcCode) {
        this.qcCode = qcCode;
    }

    public Date getQcDoneTime() {
        return qcDoneTime;
    }

    public void setQcDoneTime(Date qcDoneTime) {
        this.qcDoneTime = qcDoneTime;
    }

    public Date getApplyPhotoTime() {
        return applyPhotoTime;
    }

    public void setApplyPhotoTime(Date applyPhotoTime) {
        this.applyPhotoTime = applyPhotoTime;
    }

    public Date getDonePhotoTime() {
        return donePhotoTime;
    }

    public void setDonePhotoTime(Date donePhotoTime) {
        this.donePhotoTime = donePhotoTime;
    }

    public Integer getTimes() {
        return times;
    }

    public void setTimes(Integer times) {
        this.times = times;
    }

    public String getCateInfo() {
        return cateInfo;
    }

    public void setCateInfo(String cateInfo) {
        this.cateInfo = cateInfo;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public Integer getIsDel() {
        return isDel;
    }

    public void setIsDel(Integer isDel) {
        this.isDel = isDel;
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

    public String getReturnId() {
        return returnId;
    }

    public void setReturnId(String returnId) {
        this.returnId = returnId;
    }
}