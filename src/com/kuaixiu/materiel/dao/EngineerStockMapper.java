package com.kuaixiu.materiel.dao;

import com.common.base.dao.BaseDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * EngineerStock Mapper
 *
 * @param <T>
 * @CreateDate: 2019-03-27 下午04:31:52
 * @version: V 1.0
 */
public interface EngineerStockMapper<T> extends BaseDao<T> {

    //根据库存类型查询总数量数量
    int queryCountByType(@Param("engineerNo")String engineerNo,@Param("materielType")Integer materielType);
    //根据库存类型查询机型品牌及其数量
    List<Map> queryModelCountByEngNo(@Param("engineerNo")String engineerNo, @Param("materielType")Integer materielType);
    //根据机型品牌查询物料其数量
    List<Map> queryMaterielCountByModel(@Param("engineerNo")String engineerNo,
                                      @Param("materielType")Integer materielType,
                                      @Param("brandId")String brandName,
                                      @Param("modelId")String modelName);
    T queryCountByMaterielId(@Param("engineerNo")String engineerNo,
                                   @Param("materielId")Integer materielId);
}


