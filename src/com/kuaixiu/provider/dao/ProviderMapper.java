package com.kuaixiu.provider.dao;

import java.util.List;

import com.common.base.dao.BaseDao;

/**
 * Provider Mapper
 *
 * @param <T>
 * @CreateDate: 2016-08-26 上午12:45:50
 * @version: V 1.0
 */
public interface ProviderMapper<T> extends BaseDao<T> {

    /**
     * 根据账号查询
     * @param code
     * @return
     * @CreateDate: 2016-9-6 下午11:57:44
     */
    T queryByCode(String code);
    
    /**
     * 得到日期内需要结算的连锁商
     * @param t
     * @return
     * @author: lijx
     * @CreateDate: 2016-10-20 下午7:17:09
     */
    List<T> queryProviderForBalance(T t);

}


