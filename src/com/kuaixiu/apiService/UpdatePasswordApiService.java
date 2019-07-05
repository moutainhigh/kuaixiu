package com.kuaixiu.apiService;

import com.alibaba.fastjson.JSONObject;
import com.common.exception.ApiServiceException;
import com.common.exception.SystemException;
import com.system.api.ApiServiceInf;
import com.system.basic.user.service.SysUserService;
import com.system.constant.ApiResultConstant;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;


/**
 * 工程师修改密码接口实现类
 * @author wugl
 *
 */
@Service("updatePasswordApiService")
public class UpdatePasswordApiService implements ApiServiceInf {
    private static final Logger log= Logger.getLogger(UpdatePasswordApiService.class);
    
    @Autowired
    private SysUserService sysUserService;
    
    @Override
    public Object process(Map<String, String> params) {
        //获取工程师工号和密码
        String number = MapUtils.getString(params, "pmClientId");
        //解析请求参数
        String paramJson = MapUtils.getString(params, "params");
        JSONObject pmJson = JSONObject.parseObject(paramJson);
        //验证请求参数
        if (pmJson == null 
                || !pmJson.containsKey("password")
                || !pmJson.containsKey("newPassword")
                || pmJson.getString("password") == null
                || pmJson.getString("newPassword") == null) {
            throw new ApiServiceException(ApiResultConstant.resultCode_1001, ApiResultConstant.resultCode_str_1001);
        }
        
        String passwd = pmJson.getString("password");
        //新密码格式粗略校验
        String newPwd = pmJson.getString("newPassword").toLowerCase();
        if(newPwd.length() != 32){
            throw new ApiServiceException(ApiResultConstant.resultCode_1002, "新密码MD5值格式不正确");
        }
        
        try{
            sysUserService.updateUserPasswd(number, passwd, newPwd);
        }
        catch (SystemException e){
            throw new ApiServiceException(ApiResultConstant.resultCode_3002, e.getMessage());
        }
        
        return "OK";
    }

}
