package com.kuaixiu.sjBusiness.service;


import com.common.base.service.BaseService;
import com.kuaixiu.sjBusiness.dao.SjReworkOrderPictureMapper;
import com.kuaixiu.sjBusiness.entity.SjReworkOrderPicture;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * SjReworkOrderPicture Service
 * @CreateDate: 2019-07-02 上午10:33:28
 * @version: V 1.0
 */
@Service("sjReworkOrderPictureService")
public class SjReworkOrderPictureService extends BaseService<SjReworkOrderPicture> {
    private static final Logger log= Logger.getLogger(SjReworkOrderPictureService.class);

    @Autowired
    private SjReworkOrderPictureMapper<SjReworkOrderPicture> mapper;


    public SjReworkOrderPictureMapper<SjReworkOrderPicture> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}