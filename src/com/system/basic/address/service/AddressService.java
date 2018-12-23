package com.system.basic.address.service;


import java.util.List;

import com.common.base.service.BaseService;
import com.system.basic.address.dao.AddressMapper;
import com.system.basic.address.entity.Address;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Address Service
 * @CreateDate: 2016-09-03 下午09:40:07
 * @version: V 1.0
 */
@Service("addressService")
public class AddressService extends BaseService<Address> {
    private static final Logger log= Logger.getLogger(AddressService.class);

    @Autowired
    private AddressMapper<Address> mapper;


    public AddressMapper<Address> getDao() {
        return mapper;
    }

    //**********自定义方法***********

    /**
     * 根据地址id查询 
     * @param aid
     * @return
     * @CreateDate: 2016-9-3 下午9:44:24
     */
    public Address queryByAreaId(String aid){
        return getDao().queryByAreaId(aid);
    }
    
    /**
     * 根据地址名称查询 
     * @param pid
     * @param area
     * @return
     * @CreateDate: 2016-11-7 下午11:41:37
     */
    public Address queryByAreaAndPid(String area, String pid){
        return getDao().queryByAreaAndPid(area, pid);
    }
    
    /**
     * 根据上级地址查询 
     * @param pid
     * @return
     * @CreateDate: 2016-9-3 下午9:44:24
     */
    public List<Address> queryByPid(String pid){
        return getDao().queryByPid(pid);
    }
    
    /**
     * 根据地址级别查询地址
     * @param level
     * @return
     * @CreateDate: 2016-9-6 下午9:34:49
     */
    public List<Address> queryByLevel(Integer level){
        Address t = new Address();
        t.setLevel(level);
        return getDao().queryList(t);
    }
}