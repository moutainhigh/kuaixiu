package com.kuaixiu.sjBusiness.dao;

import com.common.base.dao.BaseDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * OrderCompanyPicture Mapper
 *
 * @param <T>
 * @CreateDate: 2019-05-06 上午10:43:53
 * @version: V 1.0
 */
public interface OrderCompanyPictureMapper<T> extends BaseDao<T> {

    List<T> queryByOrderNo(@Param("orderNo")String orderNo);

    int deleteByOrderNo(@Param("orderNo")String orderNo);
}


