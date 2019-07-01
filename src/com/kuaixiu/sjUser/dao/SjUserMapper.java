package com.kuaixiu.sjUser.dao;

import com.common.base.dao.BaseDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * User Mapper
 *
 * @param <T>
 * @CreateDate: 2019-05-06 上午10:19:30
 * @version: V 1.0
 */
public interface SjUserMapper<T> extends BaseDao<T> {

    T queryByLoginId(@Param("loginId") String loginId, @Param("type") Integer type);

    T queryByName(@Param("name")String name, @Param("type") Integer type);
    //注销
    int updateCancel1(@Param("loginId") String loginId, @Param("type") Integer type);
    //恢复
    int updateCancel0(@Param("loginId") String loginId, @Param("type") Integer type);

    List<T> queryByType(@Param("type") Integer type);

    List<T> assignReworkWorker(T t);
}


