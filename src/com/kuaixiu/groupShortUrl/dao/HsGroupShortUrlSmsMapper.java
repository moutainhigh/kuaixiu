package com.kuaixiu.groupShortUrl.dao;

import com.common.base.dao.BaseDao;
import org.apache.ibatis.annotations.Param;

/**
 * HsGroupShortUrlSms Mapper
 *
 * @param <T>
 * @CreateDate: 2019-06-26 上午09:29:12
 * @version: V 1.0
 */
public interface HsGroupShortUrlSmsMapper<T> extends BaseDao<T> {

    T queryByNameLabel(String nameLabel);

    int deleteByIsDel(@Param("id") String id,@Param("updateUserid") String updateUserid);
}


