package com.kuaixiu.activity.dao;

import com.common.base.dao.BaseDao;

import java.util.List;
import java.util.Map;

/**
 * ActivityUser Mapper
 *
 * @param <T>
 * @CreateDate: 2019-01-02 上午09:44:06
 * @version: V 1.0
 */
public interface ActivityUserMapper<T> extends BaseDao<T> {

    //根据openId查询
    List<T> queryByOpenId(String openId);
    //查询快修预约列表 带分页
    List<Map<String,Object>> queryEstimateForPage(T t);

}


