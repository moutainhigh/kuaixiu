package com.kuaixiu.apiService;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.exception.ApiServiceException;
import com.kuaixiu.model.entity.NewModel;
import com.kuaixiu.model.service.NewModelService;
import com.kuaixiu.oldtonew.service.AgreedService;
import com.kuaixiu.oldtonew.service.NewOrderService;
import com.kuaixiu.oldtonew.service.OldToNewService;
import com.kuaixiu.shop.service.ShopService;
import com.system.api.ApiServiceInf;
import com.system.constant.ApiResultConstant;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
* @author: anson
* @CreateDate: 2017年6月25日 下午7:59:46
* @version: V 1.0
*  根据品牌id得到该品牌下所有机型
*/
@Service("getModelListService")
public class GetModelListService implements ApiServiceInf{
	private static final Logger log= Logger.getLogger(GetModelListService.class);
	 
    @Autowired
    private NewOrderService newOrderService;
    @Autowired
    private ShopService shopService;
    @Autowired
    private OldToNewService oldToNewService;
    @Autowired
    private AgreedService agreedService;
    @Autowired
    private NewModelService newModelService;
    
	  public Object process(Map<String, String> params) {
	        
	        //解析请求参数
	        String paramJson = MapUtils.getString(params, "params");
	        JSONObject pmJson = JSONObject.parseObject(paramJson);
	        
	        //验证请求参数
	        if (pmJson == null 
	                || !pmJson.containsKey("brandId")) {
	            throw new ApiServiceException(ApiResultConstant.resultCode_1001, ApiResultConstant.resultCode_str_1001);
	        }
	        //获取品牌id
	        String brandId = pmJson.getString("brandId");
	        //查询所有机型
			List<NewModel> modelList = newModelService.queryPutawayBrandId(brandId); 
			JSONArray jsonArray = new JSONArray();
			if(modelList.size()>0){
				for(NewModel n:modelList){
					JSONObject json = new JSONObject();
	            	json.put("id", n.getId());
	            	json.put("name", n.getName());
	            	json.put("brandName", n.getBrandName());
	            	json.put("memory", n.getMemory());
	            	String color=n.getColor().substring(1);
	            	json.put("color", color);
	            	json.put("edition", n.getEdition());
	            	json.put("price", n.getPrice());
	            	jsonArray.add(json);
				}
			}
			
	        
	        return jsonArray;
	    }

	
	
	
}
