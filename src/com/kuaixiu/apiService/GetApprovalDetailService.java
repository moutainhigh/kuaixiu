package com.kuaixiu.apiService;

import com.alibaba.fastjson.JSONObject;
import com.system.api.ApiServiceInf;
import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 获取审批流程详情
 * @author: najy
 * Created by Administrator on 2019/3/27/027.
 */
@Service("getApprovalDetailService")
public class GetApprovalDetailService implements ApiServiceInf {

    @Override
    public Object process(Map<String, String> params) {
        Object json = new Object();
        try {
            //获取工程师工号和密码
            String number = MapUtils.getString(params, "pmClientId");
            //解析请求参数
            String paramJson = MapUtils.getString(params, "params");
            JSONObject pmJson = JSONObject.parseObject(paramJson);

            json = "OK";
        } catch (Exception e) {
            e.printStackTrace();
            json = "NO";
        }
        return json;
    }
}
