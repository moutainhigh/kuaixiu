package com.system.api.service;


import java.util.List;

import com.common.base.service.BaseService;
import com.system.api.dao.ApiConfigMapper;
import com.system.api.entity.ApiConfig;
import com.system.util.SystemUtil;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ApiConfig Service
 * @CreateDate: 2016-09-05 下午08:40:20
 * @version: V 1.0
 */
@Service("apiConfigService")
public class ApiConfigService extends BaseService<ApiConfig> {
    private static final Logger log= Logger.getLogger(ApiConfigService.class);

    @Autowired
    private ApiConfigMapper<ApiConfig> mapper;


    public ApiConfigMapper<ApiConfig> getDao() {
        return mapper;
    }

    //**********自定义方法***********

    /**
     * 根据code查询 
     * @param code
     * @return
     * @CreateDate: 2016-9-5 下午9:07:06
     */
    public ApiConfig queryByCode(String code){
        return getDao().queryByCode(code);
    }
    
    /**
     * 初始化接口配置信息到内存
     * 
     * @CreateDate: 2016-9-5 下午9:19:29
     */
    public void initApiConfig(){
        ApiConfig cfg = new ApiConfig();
        //查询启用接口
        cfg.setState(0);
        List<ApiConfig> configs = queryList(cfg);
        SystemUtil.initApiConfigMap(configs);
    }
}