package com.common.base.dao;


import java.util.List;

/**
 * 超级数据库接口.
 * 
 * @param <T>
 * @CreateDate: 2016-08-19 下午21:53:35
 * @version: V 1.0
 */
public interface BaseDao<T> {

    /**
     * 插入记录
     * @param t
     */
    int add(T t);

    /**
     * 根据id，修改记录
     * @param t
     */
    int update(T t);

    /**
     * 删除记录
     * @param id
     */
    int delete(Object id);

    /**
     * 根据主键查找
     * @param id
     * @return
     */
    T queryById(Object id);

    /**
     * 查询列表 无分页
     * @param t
     * @return
     */
    List<T> queryList(T t);
    
    /**
     * 查询列表 带分页
     * @param t
     * @return
     */
    List<T> queryListForPage(T t);
}
