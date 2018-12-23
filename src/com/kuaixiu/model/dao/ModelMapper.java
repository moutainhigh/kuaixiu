package com.kuaixiu.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.common.base.dao.BaseDao;

/**
 * Model Mapper
 *
 * @param <T>
 * @CreateDate: 2016-09-03 上午12:26:35
 * @version: V 1.0
 */
public interface ModelMapper<T> extends BaseDao<T> {
    
    /**
     * 查询机型对应项目的维修费用
     * 使用动态行转列
     * @param params
     * @return
     * @CreateDate: 2016-9-7 下午9:02:54
     */
    List<Map<String, Object>> queryModelPriceGroupByProject(Map<String, Object> params);

    /**
     * 根据机型name修改
     * @param t
     * @return
     */
    int updateByNmae(T t);

    /**
     * 根据名称查询机型
     * @param name
     * @return
     * @CreateDate: 2016-9-29 下午7:55:09
     */
    List<T> queryByName(@Param("name")String name, @Param("brandId")String brandId);
}


