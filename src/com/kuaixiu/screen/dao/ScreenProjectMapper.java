package com.kuaixiu.screen.dao;

import java.util.List;

import com.common.base.dao.BaseDao;

/**
* @author: anson
* @CreateDate: 2017年10月17日 下午6:21:02
* @version: V 1.0
* 
*/
public interface ScreenProjectMapper<T> extends BaseDao<T> {

	
	 /**
     * 根据名称查询项目
     */
    List<T> queryByName(String name);
}
