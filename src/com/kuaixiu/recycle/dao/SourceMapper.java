package com.kuaixiu.recycle.dao;

import com.common.base.dao.BaseDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Source Mapper
 *
 * @param <T>
 * @CreateDate: 2019-04-22 下午04:52:20
 * @version: V 1.0
 */
public interface SourceMapper<T> extends BaseDao<T> {

    //根据业务类型查询来源
    List<String> queryByType(@Param("type") Integer type);
}


