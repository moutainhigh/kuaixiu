package com.kuaixiu.oldtonew.entity;

import java.util.Comparator;


/**
* @author: anson
* @CreateDate: 2017年6月22日 上午9:31:14
* @version: V 1.0
* 讲两种订单按订单更新时间排序
*/
public class UpdateTimeCompare implements Comparator<OrderShow>{

	@Override
	public int compare(OrderShow o1, OrderShow o2) {
		  if(o1.getUpdateTime().after(o2.getUpdateTime())){
	    	   return -1;
	       }
	       return 1;
		}

}
