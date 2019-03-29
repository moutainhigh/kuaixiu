package com.kuaixiu.apiService;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.exception.ApiServiceException;
import com.kuaixiu.materiel.entity.MaterielType;
import com.kuaixiu.materiel.service.MaterielTypeService;
import com.kuaixiu.model.entity.Model;
import com.system.api.ApiServiceInf;
import com.system.constant.ApiResultConstant;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author: najy
 * 根据品牌，机型获取物料
 * Created by Administrator on 2019/3/27/027.
 */
@Service("getMaterielByModelService")
public class GetMaterielByModelService implements ApiServiceInf{
    private static final Logger log = Logger.getLogger(GetMaterielByModelService.class);

    @Autowired
    private MaterielTypeService materielTypeService;

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
                    || !pmJson.containsKey("brandId")
                    || !pmJson.containsKey("modelId")) {
                throw new ApiServiceException(ApiResultConstant.resultCode_1001, ApiResultConstant.resultCode_str_1001);
            }
            //获取订单号
            String brandId = pmJson.getString("brandId");
            String modelId = pmJson.getString("modelId");
            List<MaterielType> materielTypes=materielTypeService.getDao().queryByModel(brandId,modelId);
            for(MaterielType materielType:materielTypes){
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("materielId",materielType.getId());
                jsonObject.put("materielName",materielType.getMaterielName());
                jsonArray.add(jsonObject);
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return jsonArray;
    }
}
