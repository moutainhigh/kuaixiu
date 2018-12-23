package com.kuaixiu.version.service;


import com.common.base.service.BaseService;
import com.kuaixiu.version.dao.VersionMapper;
import com.kuaixiu.version.entity.Version;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Version Service
 * @CreateDate: 2017-05-02 下午09:55:22
 * @version: V 1.0
 */
@Service("versionService")
public class VersionService extends BaseService<Version> {
    private static final Logger log= Logger.getLogger(VersionService.class);

    @Autowired
    private VersionMapper<Version> mapper;


    public VersionMapper<Version> getDao() {
        return mapper;
    }

    //**********自定义方法***********

    /**
     * 根据名称查询最新版本
     * @param name
     * @return
     */
    public Version queryNewVersion(String name){
    	return getDao().queryNewVersion(name);
    }
}