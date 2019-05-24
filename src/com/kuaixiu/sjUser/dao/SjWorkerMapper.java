package com.kuaixiu.sjUser.dao;

import com.common.base.dao.BaseDao;

import java.util.List;
import java.util.Map;

/**
 * SjWorker Mapper
 *
 * @param <T>
 * @CreateDate: 2019-05-09 下午06:30:39
 * @version: V 1.0
 */
public interface SjWorkerMapper<T> extends BaseDao<T> {

    List<T> queryByCompanyId(String companId);

    T queryByloginId(Integer LoginId);

    List<Map<String,String>> queryWorkerListForPage(T t);
}


