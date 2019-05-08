package com.kuaixiu.sjBusiness.service;


import com.common.base.service.BaseService;
import com.kuaixiu.sjBusiness.dao.OrderCompanyPictureMapper;
import com.kuaixiu.sjBusiness.entity.OrderCompanyPicture;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * OrderCompanyPicture Service
 * @CreateDate: 2019-05-06 上午10:43:53
 * @version: V 1.0
 */
@Service("orderCompanyPictureService")
public class OrderCompanyPictureService extends BaseService<OrderCompanyPicture> {
    private static final Logger log= Logger.getLogger(OrderCompanyPictureService.class);

    @Autowired
    private OrderCompanyPictureMapper<OrderCompanyPicture> mapper;


    public OrderCompanyPictureMapper<OrderCompanyPicture> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}