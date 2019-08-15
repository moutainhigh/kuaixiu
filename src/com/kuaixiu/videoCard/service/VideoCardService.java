package com.kuaixiu.videoCard.service;


import com.common.base.service.BaseService;
import com.kuaixiu.videoCard.dao.VideoCardMapper;
import com.kuaixiu.videoCard.entity.VideoCard;

import com.kuaixiu.videoUserRel.entity.VideoUserRel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * VideoCard Service
 * @CreateDate: 2019-08-15 下午03:37:26
 * @version: V 1.0
 */
@Service("videoCardService")
public class VideoCardService extends BaseService<VideoCard> {
    private static final Logger log= Logger.getLogger(VideoCardService.class);

    @Autowired
    private VideoCardMapper<VideoCard> mapper;


    public VideoCardMapper<VideoCard> getDao() {
        return mapper;
    }

    //**********自定义方法***********
    public List<VideoCard> getVideoUser(VideoUserRel rel) {
        return mapper.getVideoUser(rel);
    }

}