package com.kuaixiu.clerk.page;

import java.util.Comparator;

/**
* @author: anson
* @CreateDate: 2017年5月27日 下午3:28:54
* @version: V 1.0
* 
*/
public class OrderCompare implements Comparator<IndexOrder>{

	public int compare(IndexOrder o1, IndexOrder o2) {
       if(o1.getOrderTime().after(o2.getOrderTime())){
    	   return -1;
       }
       return 1;
	}

}
