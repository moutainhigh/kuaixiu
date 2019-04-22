package com.kuaixiu.order.dao;

import com.common.base.dao.BaseDao;

/**
 * OrderRecord Mapper
 *
 * @param <T>
 * @CreateDate: 2019-03-29 下午03:38:02
 * @version: V 1.0
 */
public interface OrderRecordMapper<T> extends BaseDao<T> {


    T queryByOrderNo(String orderNo);
}


