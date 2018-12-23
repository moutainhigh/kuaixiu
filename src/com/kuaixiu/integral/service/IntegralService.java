package com.kuaixiu.integral.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.common.base.dao.BaseDao;
import com.common.base.service.BaseService;
import com.kuaixiu.integral.dao.IntegralMapper;
import com.kuaixiu.integral.entity.Integral;

/**
* @author: anson
* @CreateDate: 2017年5月17日 下午4:40:11
* @version: V 1.0
* 
*/
@Service("integralService")
public class IntegralService extends BaseService<Integral>{
    @Autowired
	private IntegralMapper<Integral> mapper;
	
    public BaseDao<Integral> getDao() {
		return mapper;
	}
    
    /**
     * 修改订单状态
     */
    public int updateStatus(Integral i){
    	
    	return getDao().update(i);
    }
}
