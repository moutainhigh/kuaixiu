package com.kuaixiu.recycle.service;

import com.common.base.service.BaseService;
import com.kuaixiu.recycle.dao.SingleLoginMapper;
import com.kuaixiu.recycle.entity.SingleLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author: anson
* @CreateDate: 2018-08-22 16:50:19
* @version: V 1.0
*
*/
@Service
public class SingleLoginService extends BaseService<SingleLogin> {

    @Autowired
    private SingleLoginMapper<SingleLogin> mapper;

        @Override
        public SingleLoginMapper<SingleLogin> getDao() {

            return mapper;
            }




            }