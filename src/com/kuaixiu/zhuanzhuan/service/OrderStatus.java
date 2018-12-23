package com.kuaixiu.zhuanzhuan.service;

/**
 * @Auther: anson
 * @Date: 2018/7/5
 * @Description:转转订单状态枚举类
 */
public enum OrderStatus {

    Created(1),           //已创建
    Qc(2),                //质检中
    Confirmed(3),         //已确认
    RefundApplied(4) ;    //退货申请完毕

    private int values;

    OrderStatus(int value){
        this.values=value;
    }

    public static OrderStatus getValue(int value) {
        switch (value) {
            case 1:
                return Created;
            case 2:
                return Qc;
            case 3:
                return Confirmed;
            case 4:
                return RefundApplied;
            default:
                return null;
        }
    }


    public static void main(String[] args) {
        OrderStatus value = OrderStatus.getValue(2);
        System.out.println(OrderStatus.Qc.ordinal());
        System.out.println(OrderStatus.Qc.values);
    }
}
