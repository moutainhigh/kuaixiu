package com.kuaixiu.increaseOrder.dao;

import com.common.base.dao.BaseDao;

import java.util.List;

public interface IncreaseOrderMapper<T> extends BaseDao<T> {

    List<T> queryByIsSuccess(T t);

}