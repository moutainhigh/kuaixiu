package com.kuaixiu.recycle.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.common.base.service.BaseService;
import com.kuaixiu.recycle.dao.RecycleCheckItemsMapper;
import com.kuaixiu.recycle.entity.RecycleCheckItems;

@Service("recycleCheckItemsService")
public class RecycleCheckItemsService extends BaseService<RecycleCheckItems>{

	@Autowired
	private RecycleCheckItemsMapper mapper;

	@Override
	public RecycleCheckItemsMapper<RecycleCheckItems> getDao() {

		return mapper;
	}
	
	
	/**
	 * 
	
	 * Description:通过微信openid修改记录 
	
	 * @param r
	 * @return
	 */
	public int updateByWechatId(RecycleCheckItems r){
          return getDao().update(r);		
	}
}
