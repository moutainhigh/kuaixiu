package com.kuaixiu.apiService;

import com.alibaba.fastjson.JSONObject;
import com.common.exception.ApiServiceException;
import com.kuaixiu.engineer.entity.Engineer;
import com.kuaixiu.materiel.entity.MaterielType;
import com.kuaixiu.materiel.service.MaterielTypeService;
import com.kuaixiu.materiel.service.ProcessMaterielService;
import com.kuaixiu.materiel.service.ProcessService;
import com.system.api.ApiServiceInf;
import com.system.constant.ApiResultConstant;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 获取所有审批流程
 *
 * @author: najy
 * Created by Administrator on 2019/3/27/027.
 */
@Service("getApprovalListService")
public class GetApprovalListService implements ApiServiceInf {
    private static final Logger log = Logger.getLogger(GetApprovalListService.class);

    @Autowired
    private ProcessService processService;
    @Autowired
    private ProcessMaterielService processMaterielService;
    @Autowired
    private MaterielTypeService materielTypeService;

    @Override
    public Object process(Map<String, String> params) {
        Object json = new Object();
        try {
            //获取工程师工号和密码
            String number = MapUtils.getString(params, "pmClientId");
            //解析请求参数
            String paramJson = MapUtils.getString(params, "params");
            JSONObject pmJson = JSONObject.parseObject(paramJson);
            //验证请求参数
            if (pmJson == null
                    || !pmJson.containsKey("type")) {
                throw new ApiServiceException(ApiResultConstant.resultCode_1001, ApiResultConstant.resultCode_str_1001);
            }
            //获取订单号
            Integer type = pmJson.getInteger("type");
            List<Map> maps = processService.getDao().queryApprovalList(type);
            for(Map map:maps){
                String processNo=map.get("processNo").toString();
                int totalNum=processMaterielService.getDao().querySumByProcessNo(processNo);
                map.put("totalNum",totalNum);
                List<Map> maps1 =processMaterielService.getDao().queryModelByProcessNo(processNo);
                for(Map map1:maps1){
                    String brandId = map1.get("brandId").toString();
                    String modelId = map1.get("modelId").toString();
                    List<MaterielType> materielTypes=materielTypeService.getDao().queryByModel(brandId,modelId);
                    List<String> lists=new ArrayList<>();
                    for(MaterielType materielType:materielTypes){
                        lists.add(materielType.getMaterielName());
                    }
                    map1.put("materielDetail",lists);
                }
                map.put("materiel",maps1);
            }
            json = maps;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return json;
    }
}
