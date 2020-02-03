package com.kuaixiu.recycle.dao;

import com.common.base.dao.BaseDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface RecycleCheckItemsMapper<T> extends BaseDao<T>{
   
	/**
	 * 
	
	 * Description:通过微信openId修改 
	
	 * @param t
	 * @return
	 */
	int updateByWechatId(T t);

	List<Map<String, Object>> queryTestList(T t);
	List<Map> queryTestListForPage(T t);

	T queryByTestId(@Param("id") String id);
	/**
	 * 根据用户登录手机号,查询当天估计次数
	 * @param loginMobile
	 * @return
	 */
	int queryCountByToday(@Param("loginMobile") String loginMobile);
}