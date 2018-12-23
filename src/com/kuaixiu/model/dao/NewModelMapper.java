package com.kuaixiu.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.common.base.dao.BaseDao;

/**
* @author: anson
* @CreateDate: 2017年6月13日 下午5:00:44
* @version: V 1.0
* 
*/
public interface NewModelMapper<T> extends BaseDao<T>{

    /**
     * 根据名称查询机型
     */
    List<T> queryByName(@Param("name")String name, @Param("brandId")String brandId);
    
    /**
     * 精确查找机型
     */
    T findByModel(T t);
    /**
     * 根据品牌id查询该品牌下最大排序数
     */
    List<T> queryMaxSort(String brandId);
    
}
