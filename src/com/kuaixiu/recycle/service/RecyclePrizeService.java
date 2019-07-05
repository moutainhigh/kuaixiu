package com.kuaixiu.recycle.service;

import com.common.base.service.BaseService;
import com.kuaixiu.recycle.dao.RecyclePrizeMapper;
import com.kuaixiu.recycle.entity.RecyclePrize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("recyclePrizeService")
public class RecyclePrizeService extends BaseService<RecyclePrize>{

	@Autowired
	private RecyclePrizeMapper mapper;
	
	@Override
	public RecyclePrizeMapper<RecyclePrize> getDao() {
		
		return mapper;
	}


	/**
	 * 通过奖品等级大小降序查询奖品
	 * @param c
	 * @return
	 */
	public List<RecyclePrize> queryListByGrade(RecyclePrize c){
		return  getDao().queryListByGrade(c);
	}


	/**
	 * 实时修改奖品数量
	 * @param prizeId
	 * @return
	 */
	public int updateById(String prizeId){
		return mapper.updateById(prizeId);
	}

}
