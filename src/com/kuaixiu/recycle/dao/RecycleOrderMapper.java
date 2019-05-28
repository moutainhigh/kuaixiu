package com.kuaixiu.recycle.dao;

import com.common.base.dao.BaseDao;
import com.kuaixiu.recycle.entity.RecycleOrder;

import java.util.List;

public interface RecycleOrderMapper<T> extends BaseDao<T>  {

	List<String> querySubmitOrderIdsBy();

	/**
	 * 根据回收流水号查询记录
	 */
	T queryByQuoteId(String t);

	T queryByOrderState(T t);

	/**
	 * 根据order改订单状态
	 * @param t
	 * @return
	 */
	int updateByOrderStatus(T t);

	/**
	 * 根据orderno删除加价券使用记录
	 * @param t
	 * @return
	 */
	int deleteCouponIdByOrderStatus(String t);

	/**
	 * 根据回收订单号查询记录 
	 */
	T queryByOrderNo(String t);
	/**
	 * 根据手机号查询该用户是否有正在进行信用回收的订单
	 * 芝麻订单状态值不为0(未反馈) 和2(结束)
	 */
	List<T> queryByMobile(T t);


	/**
	 * 根据抬价订单号查询
	 * @param t
	 * @return
	 */
	T queryByIncreaseOrderNo(String t);

	List<T> queryImportList(T t);
	
}