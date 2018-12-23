package com.system.basic.dict.service;


import java.util.List;

import com.common.base.service.BaseService;
import com.system.basic.dict.dao.DictMapper;
import com.system.basic.dict.entity.Dict;
import com.system.util.SystemUtil;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Dict Service
 * @CreateDate: 2016-09-26 下午11:12:03
 * @version: V 1.0
 */
@Service("dictService")
public class DictService extends BaseService<Dict> {
    private static final Logger log= Logger.getLogger(DictService.class);

    @Autowired
    private DictMapper<Dict> mapper;


    public DictMapper<Dict> getDao() {
        return mapper;
    }

    //**********自定义方法***********

    /**
     * 初始化字典配置到内存
     * 
     * @CreateDate: 2016-9-5 下午9:19:29
     */
    public void initDict(){
        List<Dict> list = getDao().queryList(null);
        SystemUtil.initDictMap(list);
    }
}