package com.kuaixiu.apiService;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kuaixiu.brand.entity.NewBrand;
import com.kuaixiu.project.entity.CancelReason;
import com.kuaixiu.project.service.CancelReasonService;

/**
* @author: anson
* @CreateDate: 2017年7月21日 下午3:48:48
* @version: V 1.0
* 
*/
@Service("getCancelReasonService")
public class GetCancelReasonService {
	private static final Logger log= Logger.getLogger(GetCancelReasonService.class);
	@Autowired
	private CancelReasonService cancelReasonService; 
	
	 public Object process(Map<String, String> params) {
	        
	       
	    //获取取消原因标签
		 CancelReason can=new CancelReason();
         List<CancelReason> reasonList=cancelReasonService.queryListForPage(can);
         JSONArray jsonArray = new JSONArray();
         if(reasonList.size()>0){
         for(CancelReason b:reasonList){
         	JSONObject json = new JSONObject();
         	json.put("reason", b.getReason());
         	jsonArray.add(json);
         }
         }
	        return jsonArray;
	    }
}
