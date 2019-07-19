package com.kuaixiu.apiService;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kuaixiu.brand.entity.NewBrand;
import com.kuaixiu.brand.service.NewBrandService;
import com.system.api.ApiServiceInf;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
* @author: anson
* @CreateDate: 2017年6月25日 下午7:59:46
* @version: V 1.0
*  获取所有支持兑换的品牌
*/
@Service("getBrandListService")
public class GetBrandListService implements ApiServiceInf{
	private static final Logger log= Logger.getLogger(GetBrandListService.class);
	 
    
    @Autowired
    private NewBrandService newBrandService;
    
	public Object process(Map<String, String> params) {
	        
	       
	        //获取可兑换品牌
            List<NewBrand> brand=newBrandService.queryList(null);
            JSONArray jsonArray = new JSONArray();
            if(brand.size()>0){
            for(NewBrand b:brand){
            	JSONObject json = new JSONObject();
            	json.put("id", b.getId());
            	json.put("name", b.getName());
            	jsonArray.add(json);
            }
            }
	        return jsonArray;
	    }

	
	
	
}
