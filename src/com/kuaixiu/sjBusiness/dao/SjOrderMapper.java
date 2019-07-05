package com.kuaixiu.sjBusiness.dao;

import com.common.base.dao.BaseDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * SjOrder Mapper
 *
 * @param <T>
 * @CreateDate: 2019-05-08 下午12:48:51
 * @version: V 1.0
 */
public interface SjOrderMapper<T> extends BaseDao<T> {

    T queryByOrderNo(@Param("orderNo") String orderNo);

    T queryByOrderNoCreateUId(@Param("orderNo") String orderNo, @Param("createUserid")String phone);

    T queryByPhoneOrderNo(@Param("orderNo") String orderNo, @Param("phone")String phone);

    List<T> queryWebListForPage(T t);

    List<Map<String,Object>> queryImportList(T t);
}


