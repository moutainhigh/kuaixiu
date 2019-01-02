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

    //根据openId修改
    int updateByOpenId(T t);
    //根据openId查询
    T queryByOpenId(String openId);
    //根据标识查询
    T queryByIdent(String ident);
    //根据标识修改
    int updateByIden(T t);
    //查询快修预约列表 带分页
    List<Map<String,Object>> queryEstimateForPage(T t);

}


