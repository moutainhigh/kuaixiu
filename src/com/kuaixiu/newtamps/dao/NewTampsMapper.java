package com.kuaixiu.newtamps.dao;

import com.common.base.dao.BaseDao;
import com.kuaixiu.newtamps.entity.NewTamps;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * NewTamps Mapper
 *
 * @param <T>
 * @CreateDate: 2019-11-08 上午09:39:09
 * @version: V 1.0
 */
public interface NewTampsMapper<T> extends BaseDao<T> {

    List<NewTamps> queryListByUserMobile(@Param("userMobile") String userMobile);

}


