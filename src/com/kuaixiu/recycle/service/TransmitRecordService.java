package com.kuaixiu.recycle.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.common.base.dao.BaseDao;
import com.common.base.service.BaseService;
import com.kuaixiu.recycle.dao.TransmitRecordMapper;
import com.kuaixiu.recycle.entity.TransmitRecord;

@Service("transmitRecordService")
public class TransmitRecordService extends BaseService<TransmitRecord> {

	@Autowired
	private TransmitRecordMapper mapper;
	
	@Override
	public TransmitRecordMapper<TransmitRecord> getDao() {
		// TODO Auto-generated method stub
		return mapper;
	}
	
	
	
	

}
