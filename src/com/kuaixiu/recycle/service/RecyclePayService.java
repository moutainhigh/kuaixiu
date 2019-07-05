package com.kuaixiu.recycle.service;

import com.common.base.service.BaseService;
import com.kuaixiu.recycle.dao.RecyclePayMapper;
import com.kuaixiu.recycle.entity.RecyclePay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author: anson
* @CreateDate: 2017年11月17日 上午10:18:53
* @version: V 1.0
* 
*/
@Service("recyclePayService")
public class RecyclePayService extends BaseService<RecyclePay>{

	@Autowired
	private RecyclePayMapper<RecyclePay> mapper;
	
	@Override
	public RecyclePayMapper<RecyclePay> getDao() {

		return mapper;
	}
	
	
	
	
}
