package com.system.basic.download.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.common.base.dao.BaseDao;
import com.common.base.service.BaseService;
import com.system.basic.download.dao.FileDownloadMapper;
import com.system.basic.download.entity.SysReport;


@Service("fileDownloadService")
public class FileDownloadService extends BaseService<SysReport> {
	private static final Logger log= Logger.getLogger(FileDownloadService.class);
	@Autowired
	private FileDownloadMapper<SysReport> mapper;
	
	@Override
	public BaseDao<SysReport> getDao() {
		return mapper;
	}

}
