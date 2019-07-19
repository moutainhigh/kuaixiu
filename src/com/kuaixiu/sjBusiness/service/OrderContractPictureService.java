package com.kuaixiu.sjBusiness.service;


import com.common.base.service.BaseService;
import com.kuaixiu.sjBusiness.dao.OrderContractPictureMapper;
import com.kuaixiu.sjBusiness.entity.OrderContractPicture;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * OrderContractPicture Service
 * @CreateDate: 2019-05-08 上午10:52:42
 * @version: V 1.0
 */
@Service("orderContractPictureService")
public class OrderContractPictureService extends BaseService<OrderContractPicture> {
    private static final Logger log= Logger.getLogger(OrderContractPictureService.class);

    @Autowired
    private OrderContractPictureMapper<OrderContractPicture> mapper;


    public OrderContractPictureMapper<OrderContractPicture> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}