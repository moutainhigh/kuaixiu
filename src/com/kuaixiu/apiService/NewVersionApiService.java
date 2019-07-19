package com.kuaixiu.apiService;

import com.alibaba.fastjson.JSONObject;
import com.common.exception.ApiServiceException;
import com.kuaixiu.version.entity.Version;
import com.kuaixiu.version.service.VersionService;
import com.system.api.ApiServiceInf;
import com.system.constant.ApiResultConstant;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;


/**
 * 更新订单状态操作接口实现类
 * @author wugl
 *
 */
@Service("newVersionApiService")
public class NewVersionApiService implements ApiServiceInf {
    private static final Logger log= Logger.getLogger(NewVersionApiService.class);
    
    @Autowired
    private VersionService versionService;
    
    @Override
    @Transactional
    public Object process(Map<String, String> params) {
        Version vs = versionService.queryNewVersion("app");
        if(vs == null){
        	throw new ApiServiceException(ApiResultConstant.resultCode_3002, "版本信息未找到！");
        }
        JSONObject json = new JSONObject();
        json.put("version", vs.getVersion());
        json.put("level", vs.getLevel());
        json.put("path", vs.getPath());
        json.put("update_time", vs.getUpdateTime());
        return json;
    }

}
