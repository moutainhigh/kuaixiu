package com.kuaixiu.activity.dao;

import com.common.base.dao.BaseDao;

/**
 * ActivityCompany Mapper
 *
 * @param <T>
 * @CreateDate: 2018-12-27 上午11:24:16
 * @version: V 1.0
 */
public interface ActivityCompanyMapper<T> extends BaseDao<T> {
    //根据活动标识查询
    T queryByIdentification(String activityIdentification);

}


