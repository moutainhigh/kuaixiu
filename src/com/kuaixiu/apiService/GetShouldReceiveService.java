package com.kuaixiu.apiService;

import com.alibaba.fastjson.JSONObject;
import com.kuaixiu.materiel.entity.EngineerStock;
import com.kuaixiu.materiel.service.EngineerStockService;
import com.kuaixiu.materiel.service.SecurityStockService;
import com.system.api.ApiServiceInf;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author: najy
 * 获取应该领取的物料，当前系统时间
 * Created by Administrator on 2019/3/27/027.
 */
@Service("getShouldReceiveService")
public class GetShouldReceiveService implements ApiServiceInf {
    private static final Logger log = Logger.getLogger(GetShouldReceiveService.class);

    @Autowired
    private EngineerStockService engineerStockService;
    @Autowired
    private SecurityStockService securityStockService;

    @Override
    public Object process(Map<String, String> params) {
        JSONObject json = new JSONObject();
        try {
            //获取工程师工号和密码
            String number = MapUtils.getString(params, "pmClientId");
            List<Map> maps=securityStockService.getDao().queryModelCount();
            Iterator<Map> it = maps.iterator();
            while(it.hasNext()){
                Map map = it.next();
                String brandId=map.get("brandId").toString();
                String modelId=map.get("modelId").toString();
                List<Map> maps1=securityStockService.getDao().queryMaterielCountByModel(brandId,modelId);
                Iterator<Map> it1 = maps1.iterator();
                while(it1.hasNext()) {
                    Map map1 = it1.next();
                    Integer materielId=Integer.valueOf(map1.get("materielId").toString());
                    EngineerStock engineerStocks=engineerStockService.getDao().queryCountByMaterielId(number,materielId);
                    if(engineerStocks!=null){
                        int securityNum=Integer.valueOf(map1.get("number").toString());
                        if(engineerStocks.getNumber() < securityNum){
                            map1.put("stockNum",securityNum);
                        }else{
                            it1.remove();
                        }
                    }
                }
                if(CollectionUtils.isEmpty(maps1)){
                    it.remove();
                }
            }
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            String nowTime=sdf.format(new Date());

            json.put("should",maps);
            json.put("nowTime",nowTime);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return json;
    }
}
