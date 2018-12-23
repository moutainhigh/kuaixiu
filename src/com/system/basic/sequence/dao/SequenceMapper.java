package com.system.basic.sequence.dao;

import org.apache.ibatis.annotations.Param;

import com.common.base.dao.BaseDao;

/**
 * Sequence Mapper
 *
 * @param <T>
 * @CreateDate: 2016-09-03 下午11:14:22
 * @version: V 1.0
 */
public interface SequenceMapper<T> extends BaseDao<T> {
    /**
     * 根据key和类型查询当前序列
     * @param key
     * @param type
     * @return
     * @author: lijx
     * @CreateDate: 2016-9-3 下午11:23:35
     */
    String getNext(@Param(value="key")String key, @Param(value="type")String type);
}


