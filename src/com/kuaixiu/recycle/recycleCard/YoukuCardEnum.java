package com.kuaixiu.recycle.recycleCard;

import java.math.BigDecimal;

/**
 * @Author gqa
 * @Description 优酷卡类型
 * @Date 15:05 2019/8/15
 * @Param
 * @return
 **/
public enum YoukuCardEnum {


    WEEK_CARd(1, "周卡", new BigDecimal(10)),
    MONTH_CARD(2, "月卡", new BigDecimal(19.8)),
    SEASON_CARD(3, "季卡", new BigDecimal(58)),
    HEAR_YEAR_CARD(4, "半年卡", new BigDecimal(108)),
    YEAR_CARD(5, "年卡", new BigDecimal(198));


    YoukuCardEnum(Integer type, String code, BigDecimal price) {
        this.type = type;
        this.code = code;
        this.price = price;
    }

    private Integer type;

    private String code;

    private BigDecimal price;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }


    public static Integer getCardType(BigDecimal price) {
        Integer type = null;
        if(price==null){
            return type;
        }
        if(price.compareTo(new BigDecimal(0))>0&&price.compareTo(new BigDecimal(100))<=0){
            type=1;
        }else if(price.compareTo(new BigDecimal(101))>0&&price.compareTo(new BigDecimal(300))<=0){
            type=2;
        }else if(price.compareTo(new BigDecimal(301))>0&&price.compareTo(new BigDecimal(600))<=0){
            type=3;
        }else if(price.compareTo(new BigDecimal(601))>0&&price.compareTo(new BigDecimal(2000))<=0){
            type=4;
        }else if(price.compareTo(new BigDecimal(2000))>0){
            type=5;
        }
        return type;

    }

    public static Integer getType(String code){
        for (YoukuCardEnum cardEnum: YoukuCardEnum.values()){
            if(cardEnum.getCode().equals(code)){
                return cardEnum.getType();
            }
        }
        return null;
    }



}
