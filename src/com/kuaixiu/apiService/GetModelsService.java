package com.kuaixiu.apiService;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.exception.ApiServiceException;
import com.kuaixiu.brand.entity.Brand;
import com.kuaixiu.model.entity.Model;
import com.kuaixiu.model.service.ModelService;
import com.system.api.ApiServiceInf;
import com.system.constant.ApiResultConstant;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 根据品牌获取机型
 * @author: najy
 * Created by Administrator on 2019/3/27/027.
 */
@Service("getModelsService")
public class GetModelsService implements ApiServiceInf{
    private static final Logger log = Logger.getLogger(GetModelsService.class);

    @Autowired
    private ModelService modelService;

    @Override
    public Object process(Map<String, String> params) {
        JSONArray jsonArray = new JSONArray();
        try {
            //获取工程师工号和密码
            String number = MapUtils.getString(params, "pmClientId");
            //解析请求参数
            String paramJson = MapUtils.getString(params, "params");
            JSONObject pmJson = JSONObject.parseObject(paramJson);
            //验证请求参数
            if (pmJson == null
                    || !pmJson.containsKey("brandId")) {
                throw new ApiServiceException(ApiResultConstant.resultCode_1001, ApiResultConstant.resultCode_str_1001);
            }
            //获取订单号
            String brandId = pmJson.getString("brandId");
            List<Model> models=modelService.getDao().queryByBrandId(brandId);
            for(Model model:models){
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("modelId",model.getId());
                jsonObject.put("modelName",model.getName());
                jsonArray.add(jsonObject);
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return jsonArray;
    }
}
