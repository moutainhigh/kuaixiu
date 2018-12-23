package com.kuaixiu.engineer.dao;

import com.common.base.dao.BaseDao;
import com.kuaixiu.engineer.entity.Engineer;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * NewEngineer Mapper
 *
 * @param <T>
 * @CreateDate: 2018-10-30 上午11:12:05
 * @version: V 1.0
 */
public interface NewEngineerMapper<T> extends BaseDao<T> {

    int queryCount(T t);

    /**
     * 根据门店查询空闲工程师
     * @param shopCode 商店编码
     * @return 工程师对象
     */
    List<Engineer> queryUnDispatchByShopCode(@Param("shopCode")String shopCode, @Param("notEngId")String notEngId);

    /**
     * 根据门店查询m忙碌工程师
     * @param shopCode 商店编码
     * @return 工程师对象
     */
    List<Engineer> queryDispatchByShopCode(@Param("shopCode")String shopCode, @Param("notEngId")String notEngId);


    //从新派单查询工程师
    List<Engineer> queryAgainOrderForPage(T t);

    //查询业绩列表分页
    List<T> queryAchievementForPage(T t);
    //查询业绩列表
    List<T> queryListAchievement(T t);
}


