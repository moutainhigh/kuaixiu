package com.kuaixiu.sjUser.dao;

import com.common.base.dao.BaseDao;

import java.util.List;

/**
 * Menu Mapper
 *
 * @param <T>
 * @CreateDate: 2019-05-06 上午10:47:20
 * @version: V 1.0
 */
public interface MenuMapper<T> extends BaseDao<T> {

    List<T> queryMenusByUserId(String userId);
}


