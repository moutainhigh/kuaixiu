package com.kuaixiu.recycle.service;


import com.common.base.service.BaseService;
import com.kuaixiu.recycle.dao.SearchModelMapper;
import com.kuaixiu.recycle.entity.SearchModel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * SearchModel Service
 * @CreateDate: 2019-01-08 下午05:18:57
 * @version: V 1.0
 */
@Service("searchModelService")
public class SearchModelService extends BaseService<SearchModel> {
    private static final Logger log= Logger.getLogger(SearchModelService.class);

    @Autowired
    private SearchModelMapper<SearchModel> mapper;


    public SearchModelMapper<SearchModel> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}