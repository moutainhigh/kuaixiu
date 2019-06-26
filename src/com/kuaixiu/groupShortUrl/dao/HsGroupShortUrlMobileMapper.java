package com.kuaixiu.groupShortUrl.dao;

import com.common.base.dao.BaseDao;

/**
 * HsGroupShortUrlMobile Mapper
 *
 * @param <T>
 * @CreateDate: 2019-06-26 上午09:28:20
 * @version: V 1.0
 */
public interface HsGroupShortUrlMobileMapper<T> extends BaseDao<T> {

    T queryByMobile(String mobile);

    int deleteNull();
}


