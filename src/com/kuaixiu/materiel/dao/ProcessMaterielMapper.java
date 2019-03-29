package com.kuaixiu.materiel.dao;

import com.common.base.dao.BaseDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * ProcessMateriel Mapper
 *
 * @param <T>
 * @CreateDate: 2019-03-28 上午09:09:28
 * @version: V 1.0
 */
public interface ProcessMaterielMapper<T> extends BaseDao<T> {

    //流程编号查询流程 申请物料 总和
    int querySumByProcessNo(@Param("processNo")String processNo);
    //流程编号查询机型品牌
    List<Map> queryModelByProcessNo(@Param("processNo")String processNo);
}


