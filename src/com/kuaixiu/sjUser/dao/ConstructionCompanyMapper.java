package com.kuaixiu.sjUser.dao;

import com.common.base.dao.BaseDao;

import java.util.List;
import java.util.Map;

/**
 * ConstructionCompany Mapper
 *
 * @param <T>
 * @CreateDate: 2019-05-09 下午03:44:00
 * @version: V 1.0
 */
public interface ConstructionCompanyMapper<T> extends BaseDao<T> {

    //添加企业人数1
    int updatePersonAddNum(String loginId);

    //减少企业人数1
    int updatePersonCutNum(String loginId);

    //查询企业列表
    List<Map<String,Object>> queryCompanyListForPage(T t);
}


