package com.kuaixiu.engineer.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.common.base.dao.BaseDao;
import com.kuaixiu.engineer.entity.Engineer;

/**
 * Engineer Mapper
 *
 * @param <T>
 * @CreateDate: 2016-08-26 上午03:13:05
 * @version: V 1.0
 */
public interface EngineerMapper<T> extends BaseDao<T> {
    /**
     * 根据工程师工号，获取工程师基本信息
     * @param number 工号
     * @return 工程师对象
     */
    Engineer queryByEngineerNumber(String number);
    
    /**
     * 查询个数
     * @param t
     * @return
     * @CreateDate: 2016-9-11 上午2:24:39
     */
    int queryCount(T t);
    
    /**
     * 根据工程师ID查询未完成的维修订单个数 
     * @return
     */
    int queryUnFinishedOrderByEngId(String id);
    
    /**
     * 根据工程师ID查询未完成的以旧换新订单个数 
     * @return
     */
    int queryUnFinishedNewOrderByEngId(String id);
    
    /**
     * 根据门店查询空闲工程师
     * @param shopCode 商店编码
     * @return 工程师对象
     */
    List<Engineer> queryUnDispatchByShopCode(@Param("shopCode")String shopCode, @Param("notEngId")String notEngId);


    /**
     * 根据门店查询忙碌工程师
     * @param shopCode 商店编码
     * @return 工程师对象
     */
    List<Engineer> queryDispatchByShopCode(@Param("shopCode")String shopCode, @Param("notEngId")String notEngId);

}


