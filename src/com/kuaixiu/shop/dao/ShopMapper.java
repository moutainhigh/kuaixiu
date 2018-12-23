package com.kuaixiu.shop.dao;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.common.base.dao.BaseDao;
import com.kuaixiu.oldtonew.entity.NewOrder;
import com.kuaixiu.order.entity.Order;
import com.kuaixiu.shop.entity.Shop;

/**
 * Shop Mapper
 *
 * @param <T>
 * @CreateDate: 2016-08-26 上午02:22:49
 * @version: V 1.0
 */
public interface ShopMapper<T> extends BaseDao<T> {
    
    /**
     * 根据账号查询
     * @param code
     * @return
     * @CreateDate: 2016-9-6 下午11:57:44
     */
    Shop queryByCode(String code);
    
    /**
     * 查询网点个数
     * @param code
     * @return
     * @CreateDate: 2016-9-6 下午11:57:44
     */
    int queryCount(T t);
    
    /**
     * 根据经纬度查询最近的维修门店
     * @param lon
     * @param lat
     * @return
     * @CreateDate: 2016-9-15 下午5:27:48
     */
    List<Shop> queryByOrderWithEmptyEng(Order o);
    
    /**
     * 根据经纬度查询最近支持以旧换新的兑换门店
     */
    List<Shop> queryByNewOrderWithEmptyEng(NewOrder o);
    
    /**
     * 根据经纬度查询最近的维修门店
     * @param lon
     * @param lat
     * @return
     * @CreateDate: 2016-9-15 下午5:27:48
     */
    List<Shop> queryByOrder(Order o);
    
    /**
     * 根据经纬度查询最近支持以旧换新的兑换门店
     */
    List<Shop> queryByNewOrder(NewOrder o);
    
    /**
     * 根据经纬度查询最近的维修门店 分页
     * @param lon
     * @param lat
     * @return
     * @CreateDate: 2016-9-15 下午5:00:21
     */
    List<Shop> queryByLonAndLatForPage(Shop shop);
    
    /**
     * 根据经纬度查询最近支持以旧换新的兑换门店  分页
     */
    List<Shop> queryNewByLonAndLatForPage(Shop shop);
    
    
    /**
     * 根据经纬度查询最近的维修门店区分品牌 分页
     * @param lon
     * @param lat
     * @return
     * @CreateDate: 2016-9-15 下午5:00:21
     */
    List<Shop> queryByOrderForPage(Order o);
    
    /**
     * 根据经纬度查询最近的兑换门店区分品牌 分页
     */
    List<Shop> queryByNewOrderForPage(NewOrder o);
    
    /**
     * 更新派单模式
     * @param p
     * @return
     * @CreateDate: 2016-8-31 下午7:08:33
     */
    int updateDispatchType(@Param("id")String id, @Param("dispatchType")int dispatchType);
}


