package com.kuaixiu.card.entity;

import com.common.base.entity.BaseEntity;
import com.common.util.DateUtil;
import com.common.wechat.common.util.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 电信号卡实体类
 */
@Component
public class TelecomCard extends BaseEntity{
    private static final long serialVersionUID = -2823018516258677448L;

    /** 号卡iccid*/
    private String iccid;

    /** 号卡批次uuid*/
    private String batchId;

    /** 批次*/
    private String batch;

    /** 所属本地网*/
    private String province;

    /** 号卡类型 0小白卡   1即买即通卡*/
    private Integer type;

    /** 号卡名称 0白金卡 1抖音卡 2鱼卡 3 49元不限流量卡  4  99元不限流量卡  5  199元不限流量卡*/
    private Integer cardName;

    /** 导入时间*/
    private Date inTime;

    /** 分配时间*/
    private Date distributionTime;

    /** 号卡失效时间*/
    private Date loseEfficacy;

    /** 分配站点id*/
    private String stationId;

    /** 寄往站点的物流信息id(超人-站点)*/
    private String stationExpressId;

    /** 是否已使用  0否  1是*/
    private Integer isUse;

    /** 站点成功售出的订单号(站点-用户)*/
    private String successOrderId;

    /** (站点-用户)物流公司*/
    private String expressName;

    /** (站点-用户)物流单号*/
    private String expressNumber;

    /** (站点-用户)发货站点id*/
    private String sendStationId;

    /** (站点-用户)发货时间*/
    private Date sendTime;

    /** (站点-用户)发货城市*/
    private String sendCity;

    /**
     * 是否分配 0否  1是
     */
    private Integer isDistribution;

    /**
     * 寄往站点的物流单号(超人-站点)
     */
    private String stationExpressNumber;

    /**
     * 推送电渠情况  0不满足条件  1推送失败  2推送成功
     */
    private Integer isPush;

    /**
     *修改号卡区间iccid起始值
     */
    private String beginIccid;
    /**
     *修改号卡区间iccid结束值
     */
    private String endIccid;

    /**
     * 查询分配开始时间
     */
    private String queryStartDistributionTime;

    /**
     * 查询分配结束时间
     */
    private String queryEndDistributionTime;

    /**
     * 查询转转推送给超人的开始时间
     */
    private String queryZhuangStartTime;

    /**
     * 查询转转推送给超人的结束时间
     */
    private String queryZhuangEndTime;


    /**
     * 查询超人推送给电渠的开始时间
     */
    private String queryTelecomStartTime;

    /**
     * 查询超人推送给电渠的结束时间
     */
    private String queryTelecomEndTime;



    /**
     * 更新时间
     */
    private Date updateTime;

    private String strUpdateTime;


    public String getIccid() {
        return iccid;
    }

    public void setIccid(String iccid) {
        this.iccid = iccid;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
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

    public Date getDistributionTime() {
        return distributionTime;
    }

    public void setDistributionTime(Date distributionTime) {
        this.distributionTime = distributionTime;
    }

    public Date getLoseEfficacy() {
        return loseEfficacy;
    }

    public void setLoseEfficacy(Date loseEfficacy) {
        this.loseEfficacy = loseEfficacy;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getStationExpressId() {
        return stationExpressId;
    }

    public void setStationExpressId(String stationExpressId) {
        this.stationExpressId = stationExpressId;
    }

    public Integer getIsUse() {
        return isUse;
    }

    public void setIsUse(Integer isUse) {
        this.isUse = isUse;
    }

    public String getSuccessOrderId() {
        return successOrderId;
    }

    public void setSuccessOrderId(String successOrderId) {
        this.successOrderId = successOrderId;
    }

    public String getExpressName() {
        return expressName;
    }

    public void setExpressName(String expressName) {
        this.expressName = expressName;
    }

    public String getExpressNumber() {
        return expressNumber;
    }

    public void setExpressNumber(String expressNumber) {
        this.expressNumber = expressNumber;
    }

    public String getSendStationId() {
        return sendStationId;
    }

    public void setSendStationId(String sendStationId) {
        this.sendStationId = sendStationId;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public String getSendCity() {
        return sendCity;
    }

    public void setSendCity(String sendCity) {
        this.sendCity = sendCity;
    }

    public Integer getIsDistribution() {
        return isDistribution;
    }

    public void setIsDistribution(Integer isDistribution) {
        this.isDistribution = isDistribution;
    }

    public String getStationExpressNumber() {
        return stationExpressNumber;
    }

    public void setStationExpressNumber(String stationExpressNumber) {
        this.stationExpressNumber = stationExpressNumber;
    }

    public Integer getIsPush() {
        return isPush;
    }

    public void setIsPush(Integer isPush) {
        this.isPush = isPush;
    }

    public String getBeginIccid() {
        return beginIccid;
    }

    public void setBeginIccid(String beginIccid) {
        this.beginIccid = beginIccid;
    }

    public String getEndIccid() {
        return endIccid;
    }

    public void setEndIccid(String endIccid) {
        this.endIccid = endIccid;
    }

    public String getQueryStartDistributionTime() {
        if(StringUtils.isNotBlank(queryStartDistributionTime)){
            return queryStartDistributionTime+" 00:00:00";
        }
        return queryStartDistributionTime;
    }

    public void setQueryStartDistributionTime(String queryStartDistributionTime) {
        this.queryStartDistributionTime = queryStartDistributionTime;
    }

    public String getQueryEndDistributionTime() {
        if(StringUtils.isNotBlank(queryEndDistributionTime)){
            return queryEndDistributionTime+" 00:00:00";
        }
        return queryEndDistributionTime;
    }

    public void setQueryEndDistributionTime(String queryEndDistributionTime) {
        this.queryEndDistributionTime = queryEndDistributionTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getQueryZhuangStartTime() {
        if(StringUtils.isNotBlank(queryZhuangStartTime)){
            return queryZhuangStartTime+" 00:00:00";
        }
        return queryZhuangStartTime;
    }

    public void setQueryZhuangStartTime(String queryZhuangStartTime) {
        this.queryZhuangStartTime = queryZhuangStartTime;
    }

    public String getQueryZhuangEndTime() {
        if(StringUtils.isNotBlank(queryZhuangEndTime)){
            return queryZhuangEndTime+ " 23:59:59";
        }
        return queryZhuangEndTime;
    }

    public void setQueryZhuangEndTime(String queryZhuangEndTime) {
        this.queryZhuangEndTime = queryZhuangEndTime;
    }


    public String getQueryTelecomStartTime() {
        if(StringUtils.isNotBlank(queryTelecomStartTime)){
            return queryTelecomStartTime+ " 00:00:00";
        }
        return queryTelecomStartTime;
    }

    public void setQueryTelecomStartTime(String queryTelecomStartTime) {
        this.queryTelecomStartTime = queryTelecomStartTime;
    }

    public String getQueryTelecomEndTime() {
        if(StringUtils.isNotBlank(queryTelecomEndTime)){
            return queryTelecomEndTime+ " 23:59:59";
        }
        return queryTelecomEndTime;
    }

    public void setQueryTelecomEndTime(String queryTelecomEndTime) {
        this.queryTelecomEndTime = queryTelecomEndTime;
    }

    public String getStrUpdateTime() {
        return DateUtil.getDateyyyyMMddHHmmss(updateTime);
    }

    public void setStrUpdateTime(String strUpdateTime) {
        this.strUpdateTime = strUpdateTime;
    }
}