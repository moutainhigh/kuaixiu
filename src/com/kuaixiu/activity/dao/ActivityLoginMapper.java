package com.kuaixiu.activity.dao;

import com.common.base.dao.BaseDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * ActivityLogin Mapper
 *
 * @param <T>
 * @CreateDate: 2019-03-08 下午02:13:48
 * @version: V 1.0
 */
public interface ActivityLoginMapper<T> extends BaseDao<T> {

    List<T> queryByOpenId(@Param("openId")String openId);
}


