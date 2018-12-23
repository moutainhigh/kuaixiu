package com.kuaixiu.sfConstant;

import com.common.base.entity.BaseEntity;

/**
* @author: anson
* @CreateDate: 2017年8月15日 下午2:19:16
* @version: V 1.0
* 顺丰接口常量   对应transType值
*/
public class Constant extends BaseEntity{

	private static final long serialVersionUID = -9214528530737639656L;
    
	/**
	 * 快速下单 200
	 */
    public static final String SF_ORDER="200";
    /**
     * 订单结果通知 201
     */
    public static final String SF_ORDER_RESULT="201";
    /**
     * 订单查询 203
     */
    public static final String SF_ORDER_QUERY="203";
    /**
     * 订单筛选 204
     */
    public static final String SF_FILTER="204";
    /**
     * 电子运单图片下载 205
     */
    public static final String SF_WAYBILL_IMAGE="205";
    /**
     * 路由查询 501
     */
    public static final String SF_ROUTE_QUERY="501";
    /**
     * 查询ACCESS_TOKEN 300
     */
    public static final String SF_QUERY_ACCESS_TOKEN="300";
    /**
     * 申请ACCESS_TOKEN 301
     */
    public static final String SF_APPLY_ACCESS_TOKEN="301";
    /**
     * 刷新ACCESS_TOKEN 302
     */
    public static final String SF_REFRESH_ACCESS_TOKEN="302";
    
    
    
    
}
