package com.kuaixiu.materiel.dao;

import com.common.base.dao.BaseDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * SecurityStock Mapper
 *
 * @param <T>
 * @CreateDate: 2019-03-27 下午04:21:15
 * @version: V 1.0
 */
public interface SecurityStockMapper<T> extends BaseDao<T> {

    //根据物料种类id查询
    T queryByMaterielId(@Param("materielId")String materielId);

    List<Map> queryModelCount();

    List<Map> queryMaterielCountByModel(@Param("brandId")String brandId,
                                        @Param("modelId")String modelId);
}


