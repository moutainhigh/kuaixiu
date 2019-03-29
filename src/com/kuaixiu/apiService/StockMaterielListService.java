package com.kuaixiu.apiService;

import com.alibaba.fastjson.JSONObject;
import com.common.exception.ApiServiceException;
import com.kuaixiu.materiel.entity.SecurityStock;
import com.kuaixiu.materiel.service.EngineerStockService;
import com.kuaixiu.materiel.service.SecurityStockService;
import com.system.api.ApiServiceInf;
import com.system.constant.ApiResultConstant;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 库存物料列表
 * @author: najy
 * Created by Administrator on 2019/3/27/027.
 */
@Service("stockMaterielListService")
public class StockMaterielListService implements ApiServiceInf{
    private static final Logger log = Logger.getLogger(StockMaterielListService.class);

    @Autowired
    private EngineerStockService engineerStockService;
    @Autowired
    private SecurityStockService securityStockService;

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
            List<Map> maps=engineerStockService.getDao().queryModelCountByEngNo(number,type);
            for(Map map:maps){
                String brandId=map.get("brandId").toString();
                String modelId=map.get("modelId").toString();
                List<Map> maps1=engineerStockService.getDao().queryMaterielCountByModel(number,type,brandId,modelId);
                for(Map map1:maps1){
                    String materielId=map1.get("materielId").toString();
                    Integer stockNum=Integer.valueOf(map1.get("stockNum").toString());
                    SecurityStock securityStock=securityStockService.getDao().queryByMaterielId(materielId);
                    if(securityStock!=null){
                        if(securityStock.getNumber()>stockNum){
                            map1.put("lackNum",securityStock.getNumber()-stockNum);
                        }
                    }
                }
            }
            json = maps;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return json;
    }
}
