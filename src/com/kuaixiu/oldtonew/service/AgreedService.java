package com.kuaixiu.oldtonew.service;

import com.common.base.service.BaseService;
import com.kuaixiu.oldtonew.dao.AgreedMapper;
import com.kuaixiu.oldtonew.entity.Agreed;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
* @author: anson
* @CreateDate: 2017年6月19日 下午4:51:05
* @version: V 1.0
* 
*/
@Service("agreedService")
public class AgreedService extends BaseService{
    
	private static final Logger log=Logger.getLogger(AgreedService.class);
	@Autowired
	private AgreedMapper<Agreed> mapper;

	public AgreedMapper<Agreed> getDao() {
		return mapper;
	}
	
	/**
	 * 根据订单号查询
	 */
     public Agreed queryByOrderNo(String orderNo){
    	 Agreed agreed=getDao().queryByOrderNo(orderNo);
    	 
    	 return agreed;
     }
     
     /**
      * 保存预约信息
      */
	 public void saveAgreedNews(Agreed agreed){
		 String id= UUID.randomUUID().toString();
		 agreed.setId(id);
		 getDao().add(agreed);
		 
	 }
	 
	 /**
	  * 更新预留信息
	  */
     public void updateAgreed(Agreed a){
    	 getDao().update(a);
     }
}
