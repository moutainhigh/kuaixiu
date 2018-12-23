package com.kuaixiu.order.constant;

/**
 * 订单常量.
 * 
 * @CreateDate: 2016-9-16 上午11:03:16
 * @version: V 1.0
 */
public class OrderConstant {

    /**
     * 订单状态 : 0 生成订单
     */
    public static final int ORDER_STATUS_NEW = 0;
    /**
     * 订单状态 : 1 已提交
     */
    public static final int ORDER_STATUS_WAIT_DEPOSIT = 1;
    /**
     * 订单状态 : 2 待派单
     */
    public static final int ORDER_STATUS_DEPOSITED = 2;
    /**
     * 订单状态 : 3 待门店收件
     */
    public static final int ORDER_STATUS_WAIT_SHOP_SEND_RECEIVE = 3;
    /**
     * 订单状态 : 5 门店已收件
     */
    public static final int ORDER_STATUS_SHOP_ALERADY_RECEIVE = 5;
    /**
     * 订单状态 : 11 待预约
     */
    public static final int ORDER_STATUS_DISPATCHED = 11;
    /**
     * 订单状态 : 12 已预约
     */
    public static final int ORDER_STATUS_RECEIVED = 12;
    /**
     * 订单状态 : 20 开始检修
     */
    public static final int ORDER_STATUS_START_CHECK = 20;
    /**
     * 订单状态 : 21 完成检修
     */
    public static final int ORDER_STATUS_END_CHECK = 21;
    /**
     * 订单状态 : 22 已支付
     */
    public static final int ORDER_STATUS_END_PAY = 22;
    /**
     * 订单状态 : 25 开始维修
     */
    public static final int ORDER_STATUS_START_REPAIR = 25;
    /**
     * 订单状态 : 30 待用户付款
     */
    public static final int ORDER_STATUS_END_REPAIR = 30;
    /**
     * 订单状态 : 31 用户确认
     */
    public static final int ORDER_STATUS_USER_CONFIRM = 31;
    /**
     * 订单状态 : 35 正在维修
     */
    public static final int ORDER_STATUS_REPAIRING = 35;
    /**
     * 订单状态：40  待客户收件
     */
    public static final int ORDER_STATUS_WAIT_CUSTOMER_RECEIVE = 40;   
    /**
     * 待评价   45  主要用于前台订单状态筛选 获取列表时使用
     */
    public static final int ORDER_STATUS_WAIT_COMMENT = 45;
    /**
     * 订单状态 : 50 已完成
     */
    public static final int ORDER_STATUS_FINISHED = 50;
    /**
     * 订单状态 : 60 取消订单
     */
    public static final int ORDER_STATUS_CANCEL = 60;
    
    /**
     * 订单状态 : 0 未取消
     */
    public static final int ORDER_CANCEL_TYPE_NOT = 0;
    /**
     * 订单状态 : 1 用户自行取消
     */
    public static final int ORDER_CANCEL_TYPE_CUSTOMER = 1;
    /**
     * 订单状态 : 2 工程师取消
     */
    public static final int ORDER_CANCEL_TYPE_ENGINEER = 2;
    /**
     * 订单状态 : 3 管理员取消
     */
    public static final int ORDER_CANCEL_TYPE_ADMIN = 3;
    /**
     * 订单状态 : 4 客服取消
     */
    public static final int ORDER_CANCEL_TYPE_CUSTOMER_SERVICE = 4;
    
    /**
     * 支付状态 0 待提交
     */
    public static final int ORDER_PAY_STATUS_WAIT = 0;
    /**
     * 支付状态 1 提交失败
     */
    public static final int ORDER_PAY_STATUS_FAILD = 1;
    /**
     * 支付状态 2 提交成功
     */
    public static final int ORDER_PAY_STATUS_SUBMITED = 2;
    /**
     * 支付状态 3 支付成功
     */
    public static final int ORDER_PAY_STATUS_SUCCESS = 3;
    /**
     * 支付状态 4 支付超时
     */
    public static final int ORDER_PAY_STATUS_OVERTIME = 4;
    /**
     * 支付状态 5 支付失敗
     */
    public static final int ORDER_PAY_STATUS_FAIL = 5;
    /**
     * 支付状态 6支付完成
     */
    public static final int ORDER_PAY_STATUS_COMPLETE = 6;
    
    /**
     * 退款状态 0 无需退款
     */
    public static final int ORDER_REFUND_STATUS_NOT = 0;
    /**
     * 退款状态 1 待退款
     */
    public static final int ORDER_REFUND_STATUS_WAIT = 1;
    /**
     * 退款状态 2 退款失败
     */
    public static final int ORDER_REFUND_STATUS_FAILD = 2;
    /**
     * 退款状态 3 退款提交成功
     */
    public static final int ORDER_REFUND_STATUS_SUCCESS = 3;
    
    /**
     * 费用类型 0 保证金 
     */
    public static final int ORDER_EXPENSE_TYPE_DEPOSIT = 0;
    
    /**
     * 费用类型 1 余款
     */
    public static final int ORDER_EXPENSE_TYPE_BALANCE = 1;
    /**
     * 换屏获得积分数
     */
    public static final int SCREEN_INTEGRAL=50;
    /**
     * 换电池获得积分数
     */
    public static final int BATTERY_INTEGRAL=20;
    /**
     * 店员系统默认分页显示数
     */
    public static final int CLERK_SHOW=10;
    /**
     * 以旧换新订单  待预约
     */
    public static final int WAIT_AGREED=11;
    /**
     * 以旧换新订单  已预约
     */
    public static final int ALREADY_AGREED=12;
    /**
     * 以旧换新订单 待用户付款
     */
    public static final int WAIT_PAY=22;
    /**
     * 以旧换新订单  用户已评价
     */
    public static final int ALREADY_COMMENT=55;
    /**
     * 上门维修  
     */
    public static final int GO_TO_HOME_REPAIR=0;
    /**
     * 到店维修  
     */
    public static final int GO_TO_SHOP_REPAIR=1;
    /**
     * 返店维修
     */
    public static final int RETURN_TO_SHOP=2;
    /**
     * 寄修
     */
    public static final int SEND_TO_SHOP_REPAIR=3;
    /**
     * @param args
     * @author: lijx
     * @CreateDate: 2016-9-16 上午11:03:07
     */
    /**
     * 碎屏险订单状态  0未付款
     */
    public static final int SCREEN_ORDER_INACTIVE=0;
    /**
     * 碎屏险订单状态  1已付款
     */
    public static final int SCREEN_ORDER_ACTIVE=1;
    /**
     * 碎屏险订单状态  2退款中
     */
    public static final int SCREEN_ORDER_REFUNDING=2;
    /**
     * 碎屏险订单状态  3已退款
     */
    public static final int SCREEN_ORDER_REFUNDED=3;
    /**
     * 碎屏险订单状态  4提交失败
     */
    public static final int SCREEN_ORDER_ERROR=4;
    /**
     * 碎屏险订单状态  5提交成功
     */
    public static final int SCREEN_ORDER_SUCCESS=5;
    /**
     * 碎屏险订单状态  10已取消
     */
    public static final int SCREEN_ORDER_CANCEL=10;
    /**
     * 碎屏险订单支付状态  0未支付
     */
    public static final int SCREEN_ORDER_NON_PAYMENT=0;
    /**
     * 碎屏险订单支付状态  1已支付
     */
    public static final int SCREEN_ORDER_IS_PAYMENT=1;
}
