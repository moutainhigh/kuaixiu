package com.kuaixiu.recycle.dao;

import com.common.base.dao.BaseDao;

import java.util.List;
import java.util.Map;

/**
 * RecycleExternalTest Mapper
 *
 * @param <T>
 * @CreateDate: 2018-09-29 上午10:24:26
 * @version: V 1.0
 */
public interface RecycleExternalTestMapper<T> extends BaseDao<T> {


    /**
     * 查询数量
     * @param t
     * @return
     */
    Integer queryCount(T t);

    /**
     * 查询总价
     * @param t
     * @return
     */
    List<T> queryListTotalPriceForPage(T t);
}


