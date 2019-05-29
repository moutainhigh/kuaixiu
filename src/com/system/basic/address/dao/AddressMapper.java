package com.system.basic.address.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.common.base.dao.BaseDao;
import com.system.basic.address.entity.Address;

/**
 * Address Mapper
 *
 * @param <T>
 * @CreateDate: 2016-09-03 下午09:40:07
 * @version: V 1.0
 */
public interface AddressMapper<T> extends BaseDao<T> {
    
    /**
     * 根据地址id查询 
     * @param aid
     * @return
     * @CreateDate: 2016-9-3 下午9:44:24
     */
    Address queryByAreaId(String aid);
    
    /**
     * 根据地址名称查询 
     * @param pid
     * @param area
     * @return
     * @CreateDate: 2016-11-7 下午11:41:37
     */
    Address queryByAreaAndPid(@Param("area")String area, @Param("pid")String pid);

    Address queryLikeByAreaAndPid(@Param("area")String area, @Param("pid")String pid);
    /**
     * 根据上级地址查询 
     * @param pid
     * @return
     * @CreateDate: 2016-9-3 下午9:44:24
     */
    List<Address> queryByPid(String pid);
}


