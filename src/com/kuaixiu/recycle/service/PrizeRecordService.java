package com.kuaixiu.recycle.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.common.base.dao.BaseDao;
import com.common.base.service.BaseService;
import com.kuaixiu.recycle.dao.PrizeRecordMapper;
import com.kuaixiu.recycle.entity.PrizeRecord;

@Service("prizeRecordService")
public class PrizeRecordService extends BaseService<PrizeRecord>{

	@Autowired
	private PrizeRecordMapper mapper;
	
	@Override
	public PrizeRecordMapper<PrizeRecord> getDao() {
		return mapper;
	}

}
