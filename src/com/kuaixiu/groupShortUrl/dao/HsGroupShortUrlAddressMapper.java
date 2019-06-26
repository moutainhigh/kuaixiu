package com.kuaixiu.groupShortUrl.dao;

import com.common.base.dao.BaseDao;

/**
 * HsGroupShortUrlAddress Mapper
 *
 * @param <T>
 * @CreateDate: 2019-06-26 上午09:26:11
 * @version: V 1.0
 */
public interface HsGroupShortUrlAddressMapper<T> extends BaseDao<T> {

    T queryByNameLabel(String nameLabel);

    int deleteByIsDel(String id);
}


