package com.kuaixiu.recycle.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
/**
 *  获取订单列表
 * @author xadmin
 *
 */
public class ResultTest {
    private String processstatus_name;
    private Integer processstatus;
    private Date createtime;
    private String orderid;
    private String modelname;
    private String imei;
    private String modelpic;
    private String brandname;
    private BigDecimal orderprice;
    private String detail;
    private Integer orderStatus;
    private BigDecimal lovemoney;

    public BigDecimal getLovemoney() {
        return lovemoney;
    }

    public void setLovemoney(BigDecimal lovemoney) {
        this.lovemoney = lovemoney;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getProcessstatus_name() {
        if(getProcessstatus()==100){
            return "已提交";
        }else if(getProcessstatus()==-100){
            return "交易取消";
        }else if(getProcessstatus()==101){
            return "已收货，验机中";
        }else if(getProcessstatus()==200){
            return "已收货，验机中";
        }else if(getProcessstatus()==201){
            return "验机通过";
        }else if(getProcessstatus()==202){
            return "检测完成，请保持电话畅通";
        }else if(getProcessstatus()==203){
            return "待结款";
        }else if(getProcessstatus()==204){
            return "交易完成";
        }else if(getProcessstatus()==-200){
            return "交易关闭";
        }else{
            return null;
        }
    }

//    public void setProcessstatus_name(String processstatus_name) {
//        this.processstatus_name = processstatus_name;
//    }

    //    public void setProcessstatus_name(String processstatus_name) {
//        this.processstatus_name = processstatus_name;
//    }

    public Integer getProcessstatus() {
        if(getOrderStatus()==0){
            return -200;
        }else if(getOrderStatus()==1){
            return 100;
        }else if(getOrderStatus()==2){
            return 100;
        }else if(getOrderStatus()==3){
            return 100;
        }else if(getOrderStatus()==4){
            return 200;
        }else if(getOrderStatus()==5){
            return 201;
        }else if(getOrderStatus()==6){
            return 202;
        }else if(getOrderStatus()==7){
            return 202;
        }else if(getOrderStatus()==8){
            return 203;
        }else if(getOrderStatus()==9){
            return 204;
        }else {
            return null;
        }

    }

//    public void setProcessstatus(Integer processstatus) {
//        this.processstatus = processstatus;
//    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getModelname() {
        return modelname;
    }

    public void setModelname(String modelname) {
        this.modelname = modelname;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getModelpic() {
        return modelpic;
    }

    public void setModelpic(String modelpic) {
        this.modelpic = modelpic;
    }

    public String getBrandname() {
        return brandname;
    }

    public void setBrandname(String brandname) {
        this.brandname = brandname;
    }

    public BigDecimal getOrderprice() {
        return orderprice;
    }

    public void setOrderprice(BigDecimal orderprice) {
        this.orderprice = orderprice;
    }
}
