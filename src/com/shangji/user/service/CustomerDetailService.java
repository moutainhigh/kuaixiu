package com.shangji.user.service;


import com.common.base.service.BaseService;
import com.shangji.user.dao.CustomerDetailMapper;
import com.shangji.user.entity.CustomerDetail;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * CustomerDetail Service
 * @CreateDate: 2019-05-06 上午10:48:12
 * @version: V 1.0
 */
@Service("customerDetailService")
public class CustomerDetailService extends BaseService<CustomerDetail> {
    private static final Logger log= Logger.getLogger(CustomerDetailService.class);

    @Autowired
    private CustomerDetailMapper<CustomerDetail> mapper;


    public CustomerDetailMapper<CustomerDetail> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}