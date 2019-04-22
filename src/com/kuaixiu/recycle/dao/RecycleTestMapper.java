package com.kuaixiu.recycle.dao;

import com.common.base.dao.BaseDao;
import org.apache.ibatis.annotations.Param;

/**
 * RecycleTest Mapper
 *
 * @param <T>
 * @CreateDate: 2019-03-12 下午04:08:20
 * @version: V 1.0
 */
public interface RecycleTestMapper<T> extends BaseDao<T> {

    //用测评单号查询
    T queryByCheckId(@Param("checkItemsId")String checkItemsId);
}


