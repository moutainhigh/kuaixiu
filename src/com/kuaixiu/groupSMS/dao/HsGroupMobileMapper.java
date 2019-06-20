package com.kuaixiu.groupSMS.dao;

import com.common.base.dao.BaseDao;

/**
 * HsGroupMobile Mapper
 *
 * @param <T>
 * @CreateDate: 2019-06-19 上午09:22:13
 * @version: V 1.0
 */
public interface HsGroupMobileMapper<T> extends BaseDao<T> {

    T queryByMobile(String mobile);

    T deleteNull();
}


