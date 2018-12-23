package com.kuaixiu.customer.dao;

import com.common.base.dao.BaseDao;

/**
 * Customer Mapper
 *
 * @param <T>
 * @CreateDate: 2016-08-26 上午12:44:56
 * @version: V 1.0
 */
public interface CustomerMapper<T> extends BaseDao<T> {

    /**
     * 根据手机号查询客户信息
     * @param mobile
     * @return
     * @CreateDate: 2016-9-16 下午10:25:20
     */
    T queryByMobile(String mobile);
    
    /**
     * 查询用户数量
     * @param t
     * @return
     * @author: lijx
     * @CreateDate: 2016-9-19 下午11:35:44
     */
    int queryCount(T t);
}


