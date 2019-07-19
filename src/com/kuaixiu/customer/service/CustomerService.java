package com.kuaixiu.customer.service;


import com.common.base.service.BaseService;
import com.kuaixiu.customer.dao.CustomerMapper;
import com.kuaixiu.customer.entity.Customer;
import com.system.basic.address.service.AddressService;
import com.system.basic.user.service.SysUserService;
import com.system.constant.SystemConstant;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Customer Service
 * @CreateDate: 2016-08-26 上午12:44:56
 * @version: V 1.0
 */
@Service("customerService")
public class CustomerService extends BaseService<Customer> {
    private static final Logger log= Logger.getLogger(CustomerService.class);

    @Autowired
    private CustomerMapper<Customer> mapper;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private AddressService addressService;

    public CustomerMapper<Customer> getDao() {
        return mapper;
    }

    //**********自定义方法***********

    /**
     * 查询客户总数
     * @return
     * @CreateDate: 2016-9-4 下午4:21:29
     */
    public int queryAllCount(){
        return getDao().queryCount(null);
    }
    
    /**
     * 根据手机号查询客户信息
     * @param mobile
     * @return
     * @CreateDate: 2016-9-16 下午10:27:49
     */
    public Customer queryByMobile(String mobile){
        return getDao().queryByMobile(mobile);
    }
    
    /**
     * 1、创建登录用户
     * 2、保存
     * @param t
     * @param su
     * @return
     * @CreateDate: 2016-9-7 上午1:18:20
     */
    public Customer save(Customer t){
        
        //创建登录用户
        String m = t.getMobile();
        Customer cust = getDao().queryByMobile(m);
        if(cust != null){
            cust.setName(t.getName());
            cust.setLongitude(t.getLongitude());
            cust.setLatitude(t.getLatitude());
            cust.setProvince(t.getProvince());
            cust.setCity(t.getCity());
            cust.setCounty(t.getCounty());
            cust.setStreet(t.getStreet());
            cust.setAreas(t.getAreas());
            cust.setAddress(t.getAddress());
            cust.setUpdateUserid(t.getId());
            getDao().update(cust);
            return cust;
        }
        else{
            //生成客户id
            String custId = UUID.randomUUID().toString();
            t.setId(custId);
            sysUserService.createUser(m, m.substring(m.length() - 6), t.getId(), t.getName(), 
                    SystemConstant.USER_TYPE_CUSTOMER, t.getId());
            getDao().add(t);
            return t;
        }
    }
}