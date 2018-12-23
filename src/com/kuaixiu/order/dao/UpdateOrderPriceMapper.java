package com.kuaixiu.order.dao;

import com.common.base.dao.BaseDao;
import com.kuaixiu.order.entity.UpdateOrderPrice;

import java.util.List;

/**
 * UpdateOrderPrice Mapper
 *
 * @param <T>
 * @CreateDate: 2018-11-06 上午10:36:10
 * @version: V 1.0
 */
public interface UpdateOrderPriceMapper<T> extends BaseDao<T> {


    List<T> queryByUpOrderNo(String UpOrderNo);
}


