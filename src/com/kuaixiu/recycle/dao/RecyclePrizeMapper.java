package com.kuaixiu.recycle.dao;

import com.common.base.dao.BaseDao;

import java.util.List;

public interface RecyclePrizeMapper<T> extends BaseDao<T> {


    /**
     * 通过奖品等级排序查询
     * @param t
     * @return
     */
    List<T> queryListByGrade(T t);

    /**
     * 实时修改
     * @param prizeId
     * @return
     */
    int updateById(String prizeId);

}