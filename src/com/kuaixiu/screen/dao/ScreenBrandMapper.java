package com.kuaixiu.screen.dao;

import java.util.List;

import com.common.base.dao.BaseDao;

/**
* @author: anson
* @CreateDate: 2017年10月17日 下午6:20:30
* @version: V 1.0
* 
*/
public interface ScreenBrandMapper<T> extends BaseDao<T>  {

	 /**
     * 根据名称查询品牌
     */
    List<T> queryByName(String name);
}
