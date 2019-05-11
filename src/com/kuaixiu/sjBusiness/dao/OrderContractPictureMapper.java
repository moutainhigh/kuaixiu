package com.kuaixiu.sjBusiness.dao;

import com.common.base.dao.BaseDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * OrderContractPicture Mapper
 *
 * @param <T>
 * @CreateDate: 2019-05-08 上午10:52:42
 * @version: V 1.0
 */
public interface OrderContractPictureMapper<T> extends BaseDao<T> {

    List<T> queryByOrderNo(@Param("orderNo")String orderNo);
}


