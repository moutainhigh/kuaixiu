package com.kuaixiu.apiService;

import com.alibaba.fastjson.JSONObject;
import com.kuaixiu.materiel.service.EngineerStockService;
import com.kuaixiu.materiel.service.ProcessService;
import com.system.api.ApiServiceInf;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author: najy
 * 各种物料数量统计
 * Created by Administrator on 2019/3/27/027.
 */
@Service("stockNumStatisticsService")
public class StockNumStatisticsService implements ApiServiceInf{
    private static final Logger log = Logger.getLogger(StockNumStatisticsService.class);

    @Autowired
    private EngineerStockService engineerStockService;
    @Autowired
    private ProcessService processService;

    @Override
    public Object process(Map<String, String> params) {
        JSONObject json = new JSONObject();
        try {
            //获取工程师工号和密码
            String number = MapUtils.getString(params, "pmClientId");
            int stockNum=engineerStockService.getDao().queryCountByType(number,1);
            int retreatNum=engineerStockService.getDao().queryCountByType(number,2);
            int scrapNum=engineerStockService.getDao().queryCountByType(number,3);
            int exchangeNum=engineerStockService.getDao().queryCountByType(number,4);
            int auditProcess=processService.getDao().queryCountByEngineerNo(number);

            json.put("stockNum",stockNum);//待使用
            json.put("retreatNum",retreatNum);//待退
            json.put("scrapNum",scrapNum);//待报废·
            json.put("exchangeNum",exchangeNum);//待兑换
            json.put("auditProcess",auditProcess);//流程数量
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return json;
    }
}
