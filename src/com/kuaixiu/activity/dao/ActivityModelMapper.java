package com.kuaixiu.activity.dao;

import com.common.base.dao.BaseDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * ActivityModel Mapper
 *
 * @param <T>
 * @CreateDate: 2019-03-08 下午12:38:37
 * @version: V 1.0
 */
public interface ActivityModelMapper<T> extends BaseDao<T> {


    T queryByOpenId(@Param("openId")String openId);
}


