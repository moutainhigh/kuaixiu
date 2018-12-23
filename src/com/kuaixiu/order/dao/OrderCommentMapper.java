package com.kuaixiu.order.dao;

import com.common.base.dao.BaseDao;
import com.kuaixiu.order.entity.OrderComment;

/**
 * OrderComment Mapper
 *
 * @param <T>
 * @CreateDate: 2016-08-26 下午10:45:08
 * @version: V 1.0
 */
public interface OrderCommentMapper<T> extends BaseDao<T> {

	/**
	 * 根据订单号查询评价信息
	 */
	OrderComment  queryByOrderNo(String orderNo);
}


