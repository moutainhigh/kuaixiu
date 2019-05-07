package com.shangji.business.service;


import com.common.base.service.BaseService;
import com.shangji.business.dao.ProjectMapper;
import com.shangji.business.entity.Project;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Project Service
 * @CreateDate: 2019-05-06 上午10:39:49
 * @version: V 1.0
 */
@Service("projectService")
public class ProjectService extends BaseService<Project> {
    private static final Logger log= Logger.getLogger(ProjectService.class);

    @Autowired
    private ProjectMapper<Project> mapper;


    public ProjectMapper<Project> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}