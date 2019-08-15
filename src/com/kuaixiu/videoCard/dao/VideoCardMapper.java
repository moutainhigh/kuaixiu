package com.kuaixiu.videoCard.dao;

import com.common.base.dao.BaseDao;
import com.kuaixiu.videoCard.entity.VideoCard;
import com.kuaixiu.videoUserRel.entity.VideoUserRel;

import java.util.List;

/**
 * VideoCard Mapper
 *
 * @param <T>
 * @CreateDate: 2019-08-15 下午03:37:26
 * @version: V 1.0
 */
public interface VideoCardMapper<T> extends BaseDao<T> {


    VideoCard queryOne(T t);

    List<VideoCard> getVideoUser(VideoUserRel rel);
}


