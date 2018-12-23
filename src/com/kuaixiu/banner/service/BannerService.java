package com.kuaixiu.banner.service;


import com.common.base.service.BaseService;
import com.kuaixiu.banner.dao.BannerMapper;
import com.kuaixiu.banner.entity.Banner;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Banner Service
 * @CreateDate: 2016-08-26 上午12:43:18
 * @version: V 1.0
 */
@Service("bannerService")
public class BannerService extends BaseService<Banner> {
    private static final Logger log= Logger.getLogger(BannerService.class);

    @Autowired
    private BannerMapper<Banner> mapper;


    public BannerMapper<Banner> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}