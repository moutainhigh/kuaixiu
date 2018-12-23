package com.system.api.dao;

import com.common.base.dao.BaseDao;
import com.system.api.entity.ApiConfig;

/**
 * ApiConfig Mapper
 *
 * @param <T>
 * @CreateDate: 2016-09-05 下午08:40:20
 * @version: V 1.0
 */
public interface ApiConfigMapper<T> extends BaseDao<T> {
    
    /**
     * 根据code查询 
     * @param code
     * @return
     * @CreateDate: 2016-9-5 下午9:07:06
     */
    ApiConfig queryByCode(String code);
}


