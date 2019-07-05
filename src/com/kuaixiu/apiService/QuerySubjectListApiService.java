package com.kuaixiu.apiService;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.exception.ApiServiceException;
import com.kuaixiu.model.entity.RepairCost;
import com.kuaixiu.model.service.RepairCostService;
import com.kuaixiu.order.service.OrderService;
import com.system.api.ApiServiceInf;
import com.system.constant.ApiResultConstant;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


/**
 * 3.6.7    查询维修项目
 * 一期考虑根据机型查询维修项目
 * @author wugl
 *
 */
@Service("querySubjectListApiService")
public class QuerySubjectListApiService implements ApiServiceInf {
    private static final Logger log= Logger.getLogger(QuerySubjectListApiService.class);
    
    @Autowired
    private OrderService orderService;
    @Autowired
    private RepairCostService repairCostService;
    
    @Override
    public Object process(Map<String, String> params) {
        
        //解析请求参数
        String paramJson = MapUtils.getString(params, "params");
        JSONObject pmJson = JSONObject.parseObject(paramJson);
        //获取机型id
        String modelId = pmJson.getString("modelId");
        if (StringUtils.isBlank(modelId)) {
            throw new ApiServiceException(ApiResultConstant.resultCode_1003, "modelId参数为空");
        }
        //维修费用
        List<RepairCost> repairCosts = repairCostService.queryListByModelId(modelId);
        if(repairCosts == null){
            throw new ApiServiceException(ApiResultConstant.resultCode_1003, "维修项目未找到");
        }
        
        JSONArray jsonArray = new JSONArray();
        for(RepairCost cost : repairCosts){
            JSONObject json = new JSONObject();
            json.put("project_id", cost.getProjectId());
            json.put("project_name", cost.getProjectName());
            json.put("price", cost.getPrice());
            jsonArray.add(json);
        }

        return jsonArray;
    }

}
