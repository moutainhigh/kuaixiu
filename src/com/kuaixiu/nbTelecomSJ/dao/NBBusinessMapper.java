package com.kuaixiu.nbTelecomSJ.dao;

import com.common.base.dao.BaseDao;

import java.util.List;
import java.util.Map;

/**
 * NBBusiness Mapper
 *
 * @param <T>
 * @CreateDate: 2019-02-23 上午11:53:31
 * @version: V 1.0
 */
public interface NBBusinessMapper<T> extends BaseDao<T> {

    List<T> queryByOpenId(String OpenId);

    List<Map<String,Object>> queryListMap(T t);

    List<Map<String,Object>> queryListMapForPage(T t);
}


