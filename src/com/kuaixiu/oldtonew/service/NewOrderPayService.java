package com.kuaixiu.oldtonew.service;

import com.common.base.service.BaseService;
import com.kuaixiu.oldtonew.dao.NewOrderPayMapper;
import com.kuaixiu.oldtonew.entity.NewOrderPay;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author: anson
* @CreateDate: 2017年6月19日 下午5:22:20
* @version: V 1.0
* 
*/
@Service("newOrderPaySerice")
public class NewOrderPayService extends BaseService{

	private static final Logger log=Logger.getLogger(NewOrderPayService.class);
	
	@Autowired
	private NewOrderPayMapper<NewOrderPay> mapper;
	
	public NewOrderPayMapper<NewOrderPay> getDao() {

		return mapper;
	}
	
	/**
	 * 根据订单号查询
	 */
     public NewOrderPay queryByOrderNo(String orderNo){
    	 NewOrderPay orderPay=getDao().queryByOrderNo(orderNo);
    	 
    	 return orderPay;
     }
     
     /**
      * 新增付款信息
      */
     public void addOrderPay(NewOrderPay o){
    	 getDao().add(o);
     }
     /**
      * 更新付款信息
      */
     public void UpdateOrderPay(NewOrderPay o){
    	 getDao().update(o);
     }
     
}
