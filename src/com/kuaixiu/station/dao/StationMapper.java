package com.kuaixiu.station.dao;

import com.common.base.dao.BaseDao;

import java.util.List;

public interface StationMapper<T> extends BaseDao<T> {

    /**
     * 通过名称模糊查询
     * @param t
     * @return
     */
    List<T> queryByName(T t);

    /**
     * 更新库存
     * @param t
     * @return
     */
    int updateById(T t);
}