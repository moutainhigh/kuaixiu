package com.kuaixiu.screen.dao;

import java.util.List;

import com.common.base.dao.BaseDao;

public interface ScreenCustomerMapper<T> extends BaseDao<T> {
   
	/**
	 * 通过手机号查询
	 * @param mobile
	 * @return
	 */
	T queryByMobile(String mobile);
	
	/**
	 * 通过订单号查询
	 * @param mobile
	 * @return
	 */
	T queryByOrderNo(String orderNo);
	
	/**
	 * 查询有效的碎屏险品牌集合  有效期为5天
	 */
	List<String> queryAllBrand();
	
	/**
	 * 查询品牌下的机型
	 */
	List<String> queryAllModel(String brand);
	
	
}