package com.kuaixiu.apiService;

import com.alibaba.fastjson.JSONObject;
import com.system.api.ApiServiceInf;
import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author: najy
 * 提交补物料信息
 * Created by Administrator on 2019/3/27/027.
 */
@Service("repairSubmitService")
public class RepairSubmitService implements ApiServiceInf {


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
