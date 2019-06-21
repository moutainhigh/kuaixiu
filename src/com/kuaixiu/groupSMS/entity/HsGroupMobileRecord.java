package com.kuaixiu.groupSMS.entity;import com.common.base.entity.BaseEntity;import com.common.util.DateUtil;/** * . * * @CreateDate: 2019-06-19 下午02:38:21 * @version: V 1.0 */public class HsGroupMobileRecord extends BaseEntity {        /**     * 序列化Id     */    private static final long serialVersionUID = 1L;    /**     *      */    private String id ;    /**     * 批次     */    private String batchId;    /**     * 手机号     */    private String mobile ;    /**     * 加价券id     */    private String couponId ;    /**     * 地址id     */    private String addressId;    /**     * 短信模板id     */    private String smsId;    /**     *      */    private java.util.Date createTime ;    /**     *      */    private String createUserid ;    /**非dba字段 字符串创建时间*/    private String strCreateTime;    /**非dba字段 地址id*/    private String address;    /**非dba字段 加价券编码*/    private String couponCode ;    /**非dba字段 短信模板*/    private String smsTemplate ;    public String getSmsTemplate() {        return smsTemplate;    }    public void setSmsTemplate(String smsTemplate) {        this.smsTemplate = smsTemplate;    }    public String getAddress() {        return address;    }    public void setAddress(String address) {        this.address = address;    }    public String getAddressId() {        return addressId;    }    public void setAddressId(String addressId) {        this.addressId = addressId;    }    public String getSmsId() {        return smsId;    }    public void setSmsId(String smsId) {        this.smsId = smsId;    }    public String getStrCreateTime() {        return DateUtil.getDateyyyyMMddHHmmss(createTime);    }    public void setStrCreateTime(String strCreateTime) {        this.strCreateTime = strCreateTime;    }    public String getId(){        return this.id;    }    public void setId(String id){        this.id=id;    }    public String getBatchId() {        return batchId;    }    public void setBatchId(String batchId) {        this.batchId = batchId;    }    public String getMobile(){        return this.mobile;    }    public void setMobile(String mobile){        this.mobile=mobile;    }    public String getCouponId(){        return this.couponId;    }    public void setCouponId(String couponId){        this.couponId=couponId;    }    public String getCouponCode(){        return this.couponCode;    }    public void setCouponCode(String couponCode){        this.couponCode=couponCode;    }    public java.util.Date getCreateTime(){        return this.createTime;    }    public void setCreateTime(java.util.Date createTime){        this.createTime=createTime;    }    public String getCreateUserid(){        return this.createUserid;    }    public void setCreateUserid(String createUserid){        this.createUserid=createUserid;    }}