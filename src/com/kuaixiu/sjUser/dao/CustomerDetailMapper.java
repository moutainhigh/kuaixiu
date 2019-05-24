package com.kuaixiu.sjUser.dao;

import com.common.base.dao.BaseDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * CustomerDetail Mapper
 *
 * @param <T>
 * @CreateDate: 2019-05-06 上午10:48:12
 * @version: V 1.0
 */
public interface CustomerDetailMapper<T> extends BaseDao<T> {

    T queryByLoginId(@Param("loginId")Integer loginId);

    List<Map<String,String>> queryCustomerListForPage(T t);
    List<Map<String,Object>> queryCustomerList(T t);
    int queryByLoginIdState(@Param("loginId")String loginId,@Param("state")String state);
    int queryIngByLoginIdState(@Param("loginId")String loginId,@Param("state")String state);
}


