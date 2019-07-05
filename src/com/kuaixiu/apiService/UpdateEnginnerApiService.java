package com.kuaixiu.apiService;

import com.alibaba.fastjson.JSONObject;
import com.common.exception.ApiServiceException;
import com.kuaixiu.engineer.entity.Engineer;
import com.kuaixiu.engineer.service.EngineerService;
import com.system.api.ApiServiceInf;
import com.system.constant.ApiResultConstant;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;


/**
 * 工程师操作接口实现类
 * @author wugl
 *
 */
@Service("updateEnginnerApiService")
public class UpdateEnginnerApiService implements ApiServiceInf {
    private static final Logger log= Logger.getLogger(UpdateEnginnerApiService.class);
    
    @Autowired
    private EngineerService engService;
    
    @Override
    public Object process(Map<String, String> params) {
        
    	//获取工程师工号和密码
        String number = MapUtils.getString(params, "pmClientId");
        //解析请求参数
        String paramJson = MapUtils.getString(params, "params");
        JSONObject pmJson = JSONObject.parseObject(paramJson);
        //验证请求参数
        if (pmJson == null 
                || !pmJson.containsKey("state")
                || pmJson.getString("state") == null) {
            throw new ApiServiceException(ApiResultConstant.resultCode_1001, ApiResultConstant.resultCode_str_1001);
        }
        Engineer eng = engService.queryByEngineerNumber(number);
        if(eng == null){
            throw new ApiServiceException(ApiResultConstant.resultCode_1003, "工程师不存在");
        }
        String state = pmJson.getString("state");
        if("0".equals(state)){
        	//上线
        	if(eng.getIsDispatch() != 2){
        		throw new ApiServiceException(ApiResultConstant.resultCode_3002, "工程师非下线状态无需上线操作！");
        	}
        	eng.setIsDispatch(0);
        	engService.saveUpdate(eng);
        }
        else{
        	//下线
        	if(eng.getIsDispatch() == 2){
        		throw new ApiServiceException(ApiResultConstant.resultCode_3002, "工程师已经是下线状态无需重复操作！");
        	}
        	if(eng.getIsDispatch() == 1){
        		throw new ApiServiceException(ApiResultConstant.resultCode_3002, "工程师已派单不能下线操作！");
        	}
        	eng.setIsDispatch(2);
        	engService.saveUpdate(eng);
        }
        
        return "OK";
    }

}
