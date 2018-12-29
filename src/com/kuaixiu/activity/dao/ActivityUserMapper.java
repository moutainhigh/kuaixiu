package com.kuaixiu.activity.dao;

import com.common.base.dao.BaseDao;
import com.kuaixiu.activity.entity.ActivityUser;

/**
 * ActivityUser Mapper
 *
 * @param <T>
 * @CreateDate: 2018-12-28 下午04:18:04
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
}


