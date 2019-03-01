package com.kuaixiu.nbTelecomSJ.dao;

import com.common.base.dao.BaseDao;

import java.util.List;

/**
 * NBArea Mapper
 *
 * @param <T>
 * @CreateDate: 2019-02-22 下午07:26:25
 * @version: V 1.0
 */
public interface NBAreaMapper<T> extends BaseDao<T> {

    T queryByAreaId(String areaId);

    //根据县份id查询
    List<T> queryByCountyId(String countyId);

    //根据支局名字查询
    List<T> queryByBranchOffice(T t);

    int deleteByOfficeId(T t);

    List<T> queryImportList(T t);
}


