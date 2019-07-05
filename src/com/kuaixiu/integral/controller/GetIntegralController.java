package com.kuaixiu.integral.controller;

import com.common.base.controller.BaseController;
import com.common.base.dao.BaseDao;
import com.common.paginate.Page;
import com.google.common.collect.Maps;
import com.kuaixiu.clerk.service.ClerkService;
import com.kuaixiu.integral.dao.GetIntegralMapper;
import com.kuaixiu.integral.entity.GetIntegral;
import com.kuaixiu.integral.entity.Integral;
import com.kuaixiu.integral.service.GetIntegralService;
import com.kuaixiu.integral.service.IntegralService;
import com.system.basic.user.entity.SessionUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
* @author: anson
* @CreateDate: 2017年5月17日 下午4:53:11
* @version: V 1.0
* 
*/
@Controller
public class GetIntegralController extends BaseController{
      
	 @Autowired
	 private IntegralService integralService;
	 @Autowired
	 private GetIntegralService getIntegralService;
     @Autowired
	 private ClerkService clerkService;
     @Autowired
 	 private GetIntegralMapper<GetIntegral> mapper;
 	
     public BaseDao<GetIntegral> getDao() {
 		return mapper;
 	}
     
     /**
      * 积分获得记录列表查询
      * 
      */
	 @RequestMapping("/wap/integral/getIntegralList")
	 public ModelAndView list(HttpServletRequest request,HttpServletResponse response) throws Exception{
		 
		 return new ModelAndView("integral/getIntegralList");
	 }
	 
	 
	 /**
	  * 积分获得记录列表数据
	  */
	 @RequestMapping("/wap/integral/getIntegralListForPage")
	 public void queryListForPage(HttpServletRequest request,HttpServletResponse response) throws Exception{
		 GetIntegral getIntegral=new GetIntegral();
		 String queryStartTime = request.getParameter("query_startTime");
	     String queryEndTime = request.getParameter("query_endTime");
	     String tel= request.getParameter("query_customerMobile");
	     getIntegral.setQueryStartTime(queryStartTime);
	     getIntegral.setQueryEndTime(queryEndTime);
	     getIntegral.setTel(tel);
		 Page page = getPageByRequest(request);
		 getIntegral.setPage(page);
	     List<GetIntegral> list=getIntegralService.queryListForPage(getIntegral);
	     page.setData(list);
	     this.renderJson(response, page);
	 }
	 
	 /**
	  * 支付积分
	  */
	 @RequestMapping("wap/integral/payIntegral")
	 public void payIntegral(HttpServletRequest request,HttpServletResponse response) throws Exception{
		 Map<String,Object> resultMap=Maps.newHashMap();
		 SessionUser su = getCurrentUser(request);
		 //得到付款人名字
		 String payer=su.getUserName();
         try {
			String id=request.getParameter("id");
			Integral integral=integralService.queryById(id);
			if(integral.getStatus()==1){
				 resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
	             resultMap.put(RESULTMAP_KEY_MSG, "该订单积分已兑换"); 
	             renderJson(response, resultMap);
			}
			//将该订单状态改为1   已完成
			integral.setStatus(1);
			integral.setPayer(payer);
			integralService.updateStatus(integral);
			resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
		} catch (Exception e) {
			e.printStackTrace();
			 resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
             resultMap.put(RESULTMAP_KEY_MSG, "系统异常，请稍后再试"); 
		}
	     renderJson(response, resultMap);
	 }
	 
}
