package com.kuaixiu.groupSMS.dao;

import com.common.base.dao.BaseDao;

/**
 * HsGroupMobileSms Mapper
 *
 * @param <T>
 * @CreateDate: 2019-06-20 下午03:44:16
 * @version: V 1.0
 */
public interface HsGroupMobileSmsMapper<T> extends BaseDao<T> {

    T queryByNameLabel(String nameLabel);

    int deleteByIsDel(String id);
}


