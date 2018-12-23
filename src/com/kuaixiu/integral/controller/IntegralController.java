package com.kuaixiu.integral.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.common.base.controller.BaseController;
import com.common.paginate.Page;
import com.google.common.collect.Maps;
import com.kuaixiu.clerk.controller.ClerkController;
import com.kuaixiu.clerk.entity.Clerk;
import com.kuaixiu.clerk.page.ClerkOrderList;
import com.kuaixiu.clerk.service.ClerkService;
import com.kuaixiu.integral.dao.IntegralMapper;
import com.kuaixiu.integral.entity.GetIntegral;
import com.kuaixiu.integral.entity.Integral;
import com.kuaixiu.integral.service.GetIntegralService;
import com.kuaixiu.integral.service.IntegralService;
import com.system.basic.user.entity.SessionUser;
import com.system.constant.SystemConstant;

/**
* @author: anson
* @CreateDate: 2017年5月17日 下午4:43:11
* @version: V 1.0
* 
*/
@Controller
public class IntegralController extends BaseController{
      
	 @Autowired
	 private IntegralService integralService;
     @Autowired
	 private ClerkService clerkService;
     @Autowired
     private IntegralMapper<Integral> mapper;
     @Autowired
     private GetIntegralService getIntegralService;
     
     public IntegralMapper<Integral> getDao(){
    	 return mapper;
     }
     
    
	 
	 /**
	  * 积分兑换提交页面
	  */
	 @RequestMapping("/wap/integral/convert")
	 public ModelAndView integralConvert(HttpServletRequest request,HttpServletResponse response) throws Exception{
		 SessionUser su = getCurrentUser(request);
		 if(su==null||su.getType()!=SystemConstant.USER_TYPE_CLERK){          
			 return new ModelAndView("clerk/login");
		 }
		 Clerk clerk=clerkService.queryById(su.getUserId());
         if(clerk!=null){
		 request.setAttribute("name", clerk.getName());
		 request.setAttribute("integral", clerk.getIntegral());
         }
	    return new ModelAndView("integral/convert");
	 }
	 
	 /**
	  * 积分兑换记录
	  */
	 @RequestMapping("/wap/integral/convertRecord")
	 public ModelAndView convertRecord(HttpServletRequest request,HttpServletResponse response) throws Exception{
		 SessionUser su = getCurrentUser(request);
		 if(su==null||su.getType()!=SystemConstant.USER_TYPE_CLERK){          
			 return new ModelAndView("clerk/login");
		 }
		 return new ModelAndView("integral/convertRecord");
	 }
	 
	 /**
	  * ajax追加分页数据
	  */
	 @RequestMapping("/wap/integral/addOrders")
	 public void addOrders(HttpServletRequest request,HttpServletResponse response) throws Exception{
		 Map<String,Object> resultMap=Maps.newHashMap();
		 SessionUser su = getCurrentUser(request);
		 //得到该店员的积分兑换订单
		 Integral integral=new Integral();
		 integral.setClerkId(su.getUserId());
		 Page page = getPageByRequest(request);
		 integral.setPage(page);
		 List<Integral> list=integralService.queryListForPage(integral);
		     resultMap.put("list", list);
			 resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
		     renderJson(response, resultMap);
		
	 }
	 
	 
	 /**
	  * 积分获得记录
	  */
	 @RequestMapping("/wap/integral/getRecord")
	 public ModelAndView getRecord(HttpServletRequest request,HttpServletResponse response) throws Exception{
		 SessionUser su = getCurrentUser(request);
		 if(su==null||su.getType()!=SystemConstant.USER_TYPE_CLERK){          
			 return new ModelAndView("clerk/login");
		 }
		 return new ModelAndView("integral/getRecord");
	 }
	 
	 /**
	  * 获得记录追加分页
	  */
	 @RequestMapping("/wap/integral/addRecord")
	 public void addRecord(HttpServletRequest request,HttpServletResponse response) throws Exception{
		 Map<String,Object> resultMap=Maps.newHashMap();
		 SessionUser su = getCurrentUser(request);
		 GetIntegral getIntegral=new GetIntegral();
		 getIntegral.setClerkId(su.getUserId());
		 Page page = getPageByRequest(request);
		 getIntegral.setPage(page);
		 List<GetIntegral> list=getIntegralService.queryListForPage(getIntegral);
		 resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
		 resultMap.put("list", list);
	     renderJson(response, resultMap);
		
	 }
	 
	 
	 /**
	  * 保存积分提交订单
	  */
	 @RequestMapping("/wap/integral/commitIntegral")
	 public void commitIntegral(HttpServletRequest request,HttpServletResponse response) throws Exception{
		 Map<String, Object> resultMap = Maps.newHashMap();
		 SessionUser su = getCurrentUser(request);
		 Clerk clerk=clerkService.queryById(su.getUserId());
		 String input=request.getParameter("input"); 
	     
		 Integral integral=new Integral();
          //为订单赋值
		 integral.setId(UUID.randomUUID().toString());
         integral.setClerkId(clerk.getId());
         integral.setIntegral(Integer.parseInt(input));
         integral.setName(clerk.getName());
         integral.setTel(clerk.getTel());
         integral.setWechatId(clerk.getWechatId());
         integral.setIsRealName(clerk.getIsRealName());
         getDao().add(integral);
         //更新该店员的实时积分
         clerk.setIntegral(clerk.getIntegral()-integral.getIntegral());
         clerkService.addIntegralById(clerk);
         resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
     	 renderJson(response, resultMap);
	 }
	 
	 /**
	  * 积分订单列表数据
	  */
	 @RequestMapping("/wap/integral/queryListForPage")
	 public void queryListForPage(HttpServletRequest request,HttpServletResponse response) throws Exception{
		 Integral integral=new Integral();
		 String queryStartTime = request.getParameter("query_startTime");
	     String queryEndTime = request.getParameter("query_endTime");
	     
	     //订单状态 2审核中  1已兑换
	     String orderState = request.getParameter("query_orderState");
	     //兑换人手机号
	     String tel= request.getParameter("query_customerMobile");
	     if (StringUtils.isNotBlank(orderState)) {
	    	  integral.setStatus(Integer.valueOf(orderState));
	      }
	      integral.setTel(tel);
	      integral.setQueryStartTime(queryStartTime);
		  integral.setQueryEndTime(queryEndTime);
		 Page page = getPageByRequest(request);
	     integral.setPage(page);
	     List<Integral> list=integralService.queryListForPage(integral);
	     page.setData(list);
	     this.renderJson(response, page);
	 }
	 
	 
	 
	 
}
