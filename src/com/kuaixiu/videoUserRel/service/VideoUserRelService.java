package com.kuaixiu.videoUserRel.service;


import com.common.base.service.BaseService;
import com.kuaixiu.videoUserRel.dao.VideoUserRelMapper;
import com.kuaixiu.videoUserRel.entity.VideoUserRel;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * VideoUserRel Service
 * @CreateDate: 2019-08-15 下午03:39:20
 * @version: V 1.0
 */
@Service("videoUserRelService")
public class VideoUserRelService extends BaseService<VideoUserRel> {
    private static final Logger log= Logger.getLogger(VideoUserRelService.class);

    @Autowired
    private VideoUserRelMapper<VideoUserRel> mapper;


    public VideoUserRelMapper<VideoUserRel> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}