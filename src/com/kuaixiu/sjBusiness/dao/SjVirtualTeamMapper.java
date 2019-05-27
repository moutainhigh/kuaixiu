package com.kuaixiu.sjBusiness.dao;

import com.common.base.dao.BaseDao;

/**
 * SjVirtualTeam Mapper
 *
 * @param <T>
 * @CreateDate: 2019-05-26 下午06:00:35
 * @version: V 1.0
 */
public interface SjVirtualTeamMapper<T> extends BaseDao<T> {

    T queryByUnitId(Integer managementUnitId);
}


