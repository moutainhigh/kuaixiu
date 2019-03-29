package com.kuaixiu.materiel.dao;

import com.common.base.dao.BaseDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Process Mapper
 *
 * @param <T>
 * @CreateDate: 2019-03-28 上午09:07:03
 * @version: V 1.0
 */
public interface ProcessMapper<T> extends BaseDao<T> {

    //根据工程师/申请人工号查询流程数量
    int queryCountByEngineerNo(@Param("applyNo")String engineerNo);

    List<Map> queryApprovalList(@Param("type")Integer type);
}


