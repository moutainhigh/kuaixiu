package com.kuaixiu.card.dao;

import com.common.base.dao.BaseDao;

import java.util.List;

public interface BatchImportMapper<T> extends BaseDao<T> {


    /**
     * 查找所有地市  不重复
     * @return
     */
    List<String> queryProvince();

}