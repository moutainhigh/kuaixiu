package com.kuaixiu.groupSMS.dao;

import com.common.base.dao.BaseDao;

/**
 * HsGroupMobileAddress Mapper
 *
 * @param <T>
 * @CreateDate: 2019-06-19 下午04:31:51
 * @version: V 1.0
 */
public interface HsGroupMobileAddressMapper<T> extends BaseDao<T> {

    T queryByNameLabel(String nameLabel);

    int deleteByIsDel(String id);
}


