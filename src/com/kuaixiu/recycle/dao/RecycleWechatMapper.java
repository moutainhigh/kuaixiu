package com.kuaixiu.recycle.dao;

import java.util.List;

import com.common.base.dao.BaseDao;

public interface RecycleWechatMapper<T> extends BaseDao<T> {
   
	/**
	 * 通过openId查询
	 * @param openId
	 * @return
	 */
	T queryByOpenId(Object openId);
	
	/**
	 * 通过手机号查询
	 * @param mobile
	 * @return
	 */
	T queryByMobie(Object mobile);

	/**
	 * 通过登录手机号查询
	 * @param mobile
	 * @return
	 */
	T queryLoginMobie(Object mobile);
	
	/**
	 * 通过openId修改数据
	 * @param t
	 * @return
	 */
	int updateByOpenId(T t);

	/**
	 * 通过登录手机号修改
	 * @param t
	 * @return
	 */
	int updateByLoginMobile(T t);

	/**
	 * 查找所有省份
	 * @return
	 */
	List<String> queryProvince();
	
	/**
	 * 查找省份下城市
	 * @param province
	 * @return
	 */
	List<String> queryCity(Object province);
	
	/**
	 * 查找所有品牌
	 * @return
	 */
	List<String> queryBrand();
	
	/**
	 * 查找品牌下机型
	 * @return
	 */
	List<String> queryModel(Object brand);
}