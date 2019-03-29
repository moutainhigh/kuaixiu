package com.kuaixiu.apiService;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kuaixiu.brand.entity.Brand;
import com.kuaixiu.brand.service.BrandService;
import com.system.api.ApiServiceInf;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author: najy
 * 获取所有品牌
 * Created by Administrator on 2019/3/27/027.
 */
@Service("getBrandsService")
public class GetBrandsService implements ApiServiceInf {
    private static final Logger log = Logger.getLogger(GetBrandsService.class);

    @Autowired
    private BrandService brandService;

    @Override
    public Object process(Map<String, String> params) {
        JSONArray jsonArray = new JSONArray();
        try {
            //获取工程师工号和密码
            String number = MapUtils.getString(params, "pmClientId");
            List<Brand> brands=brandService.queryList(null);
            for(Brand brand:brands){
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("brandId",brand.getId());
                jsonObject.put("brandName",brand.getName());
                jsonArray.add(jsonObject);
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return jsonArray;
    }
}
