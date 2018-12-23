package com.kuaixiu.engineer.dao;

import com.common.base.dao.BaseDao;

import java.util.List;

/**
 * EngineerSignIn Mapper
 *
 * @param <T>
 * @CreateDate: 2018-11-06 下午04:59:14
 * @version: V 1.0
 */
public interface EngineerSignInMapper<T> extends BaseDao<T> {


    T queryByOrderNo(String orderNo);
}


