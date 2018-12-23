package com.kuaixiu.oldtonew.entity;

import java.util.Comparator;


/**
* @author: anson
* @CreateDate: 2017年6月22日 上午9:31:14
* @version: V 1.0
* 讲两种订单按生成时间排序
*/
public class InTimeCompare implements Comparator<OrderShow>{

	@Override
	public int compare(OrderShow o1, OrderShow o2) {
		  if(o1.getInTime().after(o2.getInTime())){
	    	   return -1;
	       }
	       return 1;
		}

}
