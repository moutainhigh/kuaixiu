package com.kuaixiu.materiel.dao;

import com.common.base.dao.BaseDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * MaterielType Mapper
 *
 * @param <T>
 * @CreateDate: 2019-03-27 下午04:15:21
 * @version: V 1.0
 */
public interface MaterielTypeMapper<T> extends BaseDao<T> {

    //根据机型品牌查询物料
    List<T> queryByModel(@Param("brandId")String brandId, @Param("modelId")String modelId);
}


