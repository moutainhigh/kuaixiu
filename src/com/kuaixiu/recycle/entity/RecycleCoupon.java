package com.kuaixiu.recycle.entity;import com.common.base.entity.BaseEntity;import java.math.BigDecimal;import java.util.Date;/** * . * * @CreateDate: 2017-02-19 下午11:43:18 * @version: V 1.0 */public class RecycleCoupon extends BaseEntity {    /**     * 序列化Id     */    private static final long serialVersionUID = 1L;    /**     * id     */    private String id;    /**     * 批次ID     */    private String batchId;    /**     * 优惠码     */    private String couponCode;    /**     * 优惠券名称     */    private String couponName;    /**     * 加价类型 1：百分比 2:：固定加价     */    private Integer pricingType;    /**     * 加价规则描述     */    private String ruleDescription;    /**     * 满减金额上限额度     */    private BigDecimal upperLimit;    /**     * 满减金额下限额度     */    private BigDecimal Subtraction_price;    /**     * 满减金额上限额度(数据库没有字段，暂时传死值)     */    private BigDecimal upSubtractionrice = new BigDecimal("20000");    /**     * 优惠券金额(用于转换为前端显示)     */    private String couponPrice;    /**     * 优惠券金额（数据库存储）     */    private BigDecimal strCouponPrice;    /**     * 开始有效时间     */    private String beginTime;    /**     * 结束有效时间     */    private String endTime;    /**     * 优惠券状态 1 可用2 失效     */    private Integer status;    /**     * 是否使用 0 未使用  1 已使用     */    private Integer isUse;    /**     * 使用时间     */    private Date useTime;    /**     * 是否领用 0 未领用  1 已领用     */    private Integer isReceive;    /**     * 领用手机号码     */    private String receiveMobile;    /**     * 备注     */    private String note;    /**     * 是否删除 0：否；1：是     */    private Integer isDel;    /**     * 创建时间     */    private Date createTime;    /**     *     */    private String createUserid;    /**     *     */    private Date updateTime;    /**     *     */    private String updateUserid;    /**     * get:     */    public String getId() {        return this.id;    }    /**     * set：     */    public void setId(String id) {        this.id = id;    }    public String getBatchId() {        return batchId;    }    public void setBatchId(String batchId) {        this.batchId = batchId;    }    public BigDecimal getUpperLimit() {        return upperLimit;    }    public void setUpperLimit(BigDecimal upperLimit) {        this.upperLimit = upperLimit;    }    public BigDecimal getSubtraction_price() {        return Subtraction_price;    }    public void setSubtraction_price(BigDecimal subtraction_price) {        Subtraction_price = subtraction_price;    }    public BigDecimal getUpSubtractionrice() {        return upSubtractionrice;    }    public void setUpSubtractionrice(BigDecimal upSubtractionrice) {        this.upSubtractionrice = upSubtractionrice;    }    public Integer getPricingType() {        return pricingType;    }    public void setPricingType(Integer pricingType) {        this.pricingType = pricingType;    }    public String getRuleDescription() {        return ruleDescription;    }    public void setRuleDescription(String ruleDescription) {        this.ruleDescription = ruleDescription;    }    /**     * get:优惠码     */    public String getCouponCode() {        return this.couponCode;    }    /**     * set：优惠码     */    public void setCouponCode(String couponCode) {        this.couponCode = couponCode;    }    /**     * get:优惠券名称     */    public String getCouponName() {        return this.couponName;    }    /**     * set：优惠券名称     */    public void setCouponName(String couponName) {        this.couponName = couponName;    }    public String getCouponPrice() {        return couponPrice;    }    public void setCouponPrice(String couponPrice) {        this.couponPrice = couponPrice;    }    public BigDecimal getStrCouponPrice() {        return strCouponPrice;    }    public void setStrCouponPrice(BigDecimal strCouponPrice) {        this.strCouponPrice = strCouponPrice;    }    /**     * get:开始有效时间     */    public String getBeginTime() {        return this.beginTime;    }    /**     * set：开始有效时间     */    public void setBeginTime(String beginTime) {        this.beginTime = beginTime;    }    /**     * get:结束有效时间     */    public String getEndTime() {        return this.endTime;    }    /**     * set：结束有效时间     */    public void setEndTime(String endTime) {        this.endTime = endTime;    }    /**     * get:优惠券状态 1 可用2 失效     */    public Integer getStatus() {        return this.status;    }    /**     * set：优惠券状态 1 可用2 失效     */    public void setStatus(Integer status) {        this.status = status;    }    /**     * get:是否使用 0 未使用  1 已使用     */    public Integer getIsUse() {        return this.isUse;    }    /**     * set：是否使用 0 未使用  1 已使用     */    public void setIsUse(Integer isUse) {        this.isUse = isUse;    }    /**     * get:使用时间     */    public java.util.Date getUseTime() {        return this.useTime;    }    /**     * set：使用时间     */    public void setUseTime(java.util.Date useTime) {        this.useTime = useTime;    }    /**     * 是否领用 0 未领用  1 已领用     *     * @return     */    public Integer getIsReceive() {        return isReceive;    }    /**     * 是否领用 0 未领用  1 已领用     *     * @param isReceive     */    public void setIsReceive(Integer isReceive) {        this.isReceive = isReceive;    }    public String getReceiveMobile() {        return receiveMobile;    }    public void setReceiveMobile(String receiveMobile) {        this.receiveMobile = receiveMobile;    }    /**     * get:备注     */    public String getNote() {        return this.note;    }    /**     * set：备注     */    public void setNote(String note) {        this.note = note;    }    /**     * get:是否删除 0：否；1：是     */    public Integer getIsDel() {        return this.isDel;    }    /**     * set：是否删除 0：否；1：是     */    public void setIsDel(Integer isDel) {        this.isDel = isDel;    }    /**     * get:创建时间     */    public java.util.Date getCreateTime() {        return this.createTime;    }    /**     * set：创建时间     */    public void setCreateTime(java.util.Date createTime) {        this.createTime = createTime;    }    /**     * get:     */    public String getCreateUserid() {        return this.createUserid;    }    /**     * set：     */    public void setCreateUserid(String createUserid) {        this.createUserid = createUserid;    }    /**     * get:     */    public java.util.Date getUpdateTime() {        return this.updateTime;    }    /**     * set：     */    public void setUpdateTime(java.util.Date updateTime) {        this.updateTime = updateTime;    }    /**     * get:     */    public String getUpdateUserid() {        return this.updateUserid;    }    /**     * set：     */    public void setUpdateUserid(String updateUserid) {        this.updateUserid = updateUserid;    }}