package com.kuaixiu.order.dao;

import com.common.base.dao.BaseDao;

import java.util.List;

/**
 * ReworkOrder Mapper
 *
 * @param <T>
 * @CreateDate: 2019-01-09 上午11:10:46
 * @version: V 1.0
 */
public interface ReworkOrderMapper<T> extends BaseDao<T> {

    //根据父订单号查询进行中返修单
    List<T> queryByParentOrder(String ParantOrderNo);
}


