package com.kuaixiu.project.dao;

import java.util.List;
import java.util.Map;

import com.common.base.dao.BaseDao;

/**
 * Project Mapper
 * 
 * @param <T>
 */
public interface ProjectMapper<T> extends BaseDao<T> {
    
    /**
     * 获取维修项目
     * @return
     */
    List<Map<String, Object>> queryProjectName();
    
    /**
     * 根据名称查询项目
     * @param name
     * @return
     * @CreateDate: 2016-9-29 下午7:55:09
     */
    List<T> queryByName(String name);
    /**
     * 根据名称模糊查询项目
     * @param name
     * @return
     * @CreateDate: 2016-9-29 下午7:55:09
     */
    List<T> queryByLikeName (String name);
}


