package com.kuaixiu.clerk.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.common.base.controller.BaseController;
import com.common.exception.SystemException;
import com.common.paginate.Page;
import com.common.util.MD5Util;
import com.common.util.SmsSendUtil;
import com.google.common.collect.Maps;
import com.kuaixiu.brand.entity.Brand;
import com.kuaixiu.brand.service.BrandService;
import com.kuaixiu.clerk.dao.ClerkMapper;
import com.kuaixiu.clerk.entity.Clerk;
import com.kuaixiu.clerk.page.ClerkOrderList;
import com.kuaixiu.clerk.page.IndexOrder;
import com.kuaixiu.clerk.page.OrderCompare;
import com.kuaixiu.clerk.service.ClerkService;
import com.kuaixiu.coupon.entity.Coupon;
import com.kuaixiu.coupon.entity.CouponModel;
import com.kuaixiu.coupon.entity.CouponProject;
import com.kuaixiu.coupon.service.CouponModelService;
import com.kuaixiu.coupon.service.CouponProjectService;
import com.kuaixiu.coupon.service.CouponService;
import com.kuaixiu.customer.entity.Customer;
import com.kuaixiu.engineer.entity.Engineer;
import com.kuaixiu.integral.entity.GetIntegral;
import com.kuaixiu.integral.entity.Integral;
import com.kuaixiu.integral.service.GetIntegralService;
import com.kuaixiu.integral.service.IntegralService;
import com.kuaixiu.model.entity.Model;
import com.kuaixiu.model.entity.RepairCost;
import com.kuaixiu.model.service.ModelService;
import com.kuaixiu.model.service.RepairCostService;
import com.kuaixiu.oldtonew.entity.OldToNewUser;
import com.kuaixiu.oldtonew.entity.OrderShow;
import com.kuaixiu.order.constant.OrderConstant;
import com.kuaixiu.order.dao.OrderMapper;
import com.kuaixiu.order.entity.Order;
import com.kuaixiu.order.entity.OrderComment;
import com.kuaixiu.order.entity.OrderDetail;
import com.kuaixiu.order.service.OrderCommentService;
import com.kuaixiu.order.service.OrderDetailService;
import com.kuaixiu.order.service.OrderService;
import com.kuaixiu.provider.entity.Provider;
import com.kuaixiu.shop.entity.Shop;
import com.kuaixiu.shop.entity.ShopModel;
import com.system.basic.address.entity.Address;
import com.system.basic.address.service.AddressService;
import com.system.basic.user.entity.SessionUser;
import com.system.basic.user.service.SessionUserService;
import com.system.constant.SystemConstant;

import freemarker.template.utility.StringUtil;

/**
* @author: anson
* @CreateDate: 2017年5月17日 下午3:47:42
* @version: V 1.0
* 
*/
@Controller
public class ClerkController extends BaseController {
     
	 @Autowired
	 private ClerkService clerkService;
	 @Autowired
	 private ClerkMapper<Clerk> mapper;
	 @Autowired
	 private SessionUserService sessionUserService;
	 @Autowired
	 private AddressService addressService;
	 @Autowired
	 private ModelService modelService;
	 @Autowired
	 private BrandService brandService;
	 @Autowired
	 private CouponService couponService;
	 @Autowired
	 private CouponModelService couponModelService;
	 @Autowired
	 private CouponProjectService couponProjectService;
	 @Autowired
	 private OrderService orderService;
	 @Autowired
	 private OrderMapper<Order> orderMapper;
	 @Autowired
	 private OrderDetailService orderDetailService;
	 @Autowired
	 private RepairCostService repairCostService;
	 @Autowired
	 private OrderCommentService orderCommentService;
	 @Autowired
	 private GetIntegralService getIntegralService;
	 @Autowired
	 private IntegralService integralService;
	 
	 public ClerkMapper<Clerk> getDao(){
		 return mapper;
	 }
	 public OrderMapper<Order> getOrderDao(){
		 return orderMapper;
	 }
	 /**
	  * 店员注册
	  */
	 @RequestMapping("/wap/clerk/register")
	 public ModelAndView register(){
		 
		 return new ModelAndView("clerk/register");
	 }
	 
	 /**
	  * 店员注册信息提交
	  */
	 @RequestMapping("/wap/clerk/addClerk")
	 public void addClerk(HttpServletRequest request,HttpServletResponse response) throws Exception{
		 Map<String, Object> resultMap = Maps.newHashMap();
		 Clerk clerk=new Clerk();
		    try {
	        String tel=request.getParameter("tel");
	    	String code=request.getParameter("code");
	    	String password=request.getParameter("password");
	    	String identityCard=request.getParameter("identityCard");
	    	String homeAddress=request.getParameter("homeAddress");
	    	String name=request.getParameter("name");
	    	String province=request.getParameter("province");
	    	String city=request.getParameter("city");
	    	String county=request.getParameter("county");
	    	String street=request.getParameter("street");
	    	String areas=request.getParameter("areas");
	    	String id= UUID.randomUUID().toString();
	    	String wechatId=request.getParameter("wechatId");
	    	//是否实名制  1表示是    2表示否
	    	String isRealName=request.getParameter("isRealName");
	    	boolean flag=true;
	    	Clerk c=clerkService.queryByTel(tel);
	    	//验证手机号和验证码
	        if(!checkRandomCode(request,tel,code)){
	        	 resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
	             resultMap.put(RESULTMAP_KEY_MSG, "手机号或验证码输入错误");
	             flag=false;
           }
	        //验证该手机号是否已被注册
	        else if(c!=null){
	        	 resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
	             resultMap.put(RESULTMAP_KEY_MSG, "该手机号已被注册");
	             flag=false;
	       }else{
	    	   
	        if(flag){
	        //密码不区分大小写，一律按大写保存	
	        password=password.toUpperCase();	
	    	clerk.setId(id);
	    	clerk.setTel(tel);
	    	clerk.setName(name);
	    	clerk.setCode(MD5Util.encodePassword(password));
	    	clerk.setIdentityCard(identityCard);
	    	clerk.setProvince(province);
	    	clerk.setCity(city);
	    	clerk.setCounty(county);
	    	clerk.setStreet(street);
	    	clerk.setAreas(areas);
	    	clerk.setAddress(homeAddress);
	    	clerk.setWechatId(wechatId);
	    	clerk.setIsRealName(Integer.parseInt(isRealName));
	    	getDao().add(clerk);
	    	//给注册成功用户发送短信
	    	SmsSendUtil.sendSmsToClerk(tel);
	    	resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
	        }
	        }
            } catch (Exception e) {
            	 e.printStackTrace();
                 resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
                 resultMap.put(RESULTMAP_KEY_MSG, "系统异常请稍后");
			}
		  
			renderJson(response, resultMap);
	 }
	 
	 /**
	  * 店员登录页面
	  */
	 @RequestMapping("/wap/clerk/login")
	 public ModelAndView login(HttpServletRequest request,HttpServletResponse response) throws Exception{
		 
		 
		 return new ModelAndView("clerk/login");
	 }
	 
	 /**
	  * 登录检查
	  */
	 @RequestMapping("/wap/clerk/checkLogin")
	 public void checklogin(HttpServletRequest request,HttpServletResponse response) throws Exception{
		 Map<String, Object> resultMap = Maps.newHashMap();
		 String tel=request.getParameter("tel");
	     String password=request.getParameter("password");
	     //密码不区分大小写，按大写保存
	     password=password.toUpperCase();
	 	 Clerk clerk=clerkService.queryByTel(tel);
	 	 if(clerk==null){
	 		 resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
             resultMap.put(RESULTMAP_KEY_MSG, "用户不存在"); 
	 	 
	 	 }else if(clerk.getIsDel()==1){
	 		 resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
             resultMap.put(RESULTMAP_KEY_MSG, "用户已被删除"); 

	 	 }else if(!clerk.getCode().equals(MD5Util.encodePassword(password))){
	 		 resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
             resultMap.put(RESULTMAP_KEY_MSG, "手机号或密码输入错误"); 
		 }else{
			 //初始化SessionUser
			  SessionUser su = new SessionUser();
			  su.setUserId(clerk.getId());
			 //为店员设置session类型防止冲突方便分配权限页面  100表示登录用户是店员
			  su.setType(SystemConstant.USER_TYPE_CLERK);
			   //保存用户到session
		        HttpSession session = request.getSession();
		        session.setAttribute(SystemConstant.SESSION_USER_KEY, su);
		        session.setAttribute("clerkId", su.getUserId());
		        session.setAttribute("type", su.getType());
			 resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
		 }
		 renderJson(response, resultMap);
	 }
	 
	 /**
	  * 积分订单和手机订单整合到一个集合按时间排序
	  */
	 public  List<IndexOrder> OrderSort(Clerk clerk,Order order){
		 List<IndexOrder> indexOrderList=new ArrayList<IndexOrder>();
		 //获取该店员所有的订单
		  List<Order> list=orderService.queryList(order);
		  //获取该订单所有获得的积分信息
		  GetIntegral g=new GetIntegral();
		  g.setClerkId(clerk.getId());
		  g.setIsShow(0);
		  List<GetIntegral> integrals=getIntegralService.queryListForPage(g);
         for(Order or:list){
       	  IndexOrder in=new IndexOrder();
       	  in.setOrderNo(or.getOrderNo());
       	  in.setOrderTime(or.getInTime());
       	  in.setType(2);//表示任务订单
       	  indexOrderList.add(in);
         }
		  for(GetIntegral s:integrals){
			 IndexOrder in=new IndexOrder();
			 in.setOrderNo(s.getOrderNo());
			 in.setOrderTime(s.getInTime());
			 in.setType(1);//表示积分订单
			 in.setIntegrals(s.getIntegral());
			 in.setIntegralNo(s.getId());
			 indexOrderList.add(in);
		  }
		  //将添加到集合的类按orderTime字段排序
		 Collections.sort(indexOrderList,new OrderCompare());
		 
		 return indexOrderList;
	 }
	 
	 
	 /**
	  * 店员首页
	  */
	 @RequestMapping("/wap/clerk/index")
	 public ModelAndView index(HttpServletRequest request,HttpServletResponse response) throws Exception{
		 SimpleDateFormat sdf = new SimpleDateFormat("MM-dd  HH:mm");
		 SessionUser su = getCurrentUser(request);
		 if(su!=null&&su.getType()==SystemConstant.USER_TYPE_CLERK){
             //type为100代表店员身份               
			 return new ModelAndView("clerk/index");
		 }
		  return new ModelAndView("clerk/login"); 
		 
	 }
	 
	 /**
	  * 店员首页下拉ajax加载更多信息
	  */
	 @RequestMapping("wap/clerk/loadMoreOrders")
	 public void loadMoreOrders(HttpServletRequest request,HttpServletResponse response) throws Exception{
		 SimpleDateFormat sdf = new SimpleDateFormat("MM-dd  HH:mm");
		 Map<String,Object> resultMap=Maps.newHashMap();
		 SessionUser su = getCurrentUser(request); 
		 Clerk clerk=clerkService.queryById(su.getUserId());
		 Order order=new Order();
		 order.setClerkId(clerk.getId());
		 //只显示is_show值为0的
		 order.setIsShow(0);
		 // 该店员下的订单
		 List<IndexOrder> indexOrderList=OrderSort(clerk,order);
		
		 
		 Page p = getPageByRequest(request);
         //实现上拉分页
         p.setRecordsTotal(indexOrderList.size());
         //分页显示,用于分页显示的集合
		 List<IndexOrder> indexOrders=new ArrayList<IndexOrder>();
		 int start=p.getStart();//开始数
         int end=p.getStart()+p.getPageSize();//结尾数
        
         if(indexOrderList.size()<=p.getPageSize()&&start==0){
         	for(IndexOrder o:indexOrderList){
         		indexOrders.add(o);
         	}
         }else if(indexOrderList.size()>end){
         	for(int i=start;i<end;i++){
         		indexOrders.add(indexOrderList.get(i));
         	}
         	
         }else if(indexOrderList.size()>=start&&indexOrderList.size()<=end){
         	for(int i=start;i<indexOrderList.size();i++){
         		indexOrders.add(indexOrderList.get(i));
         	}
         }
         
         //给显示订单的时间显示规定统一格式
		 for(IndexOrder o:indexOrders){
			 o.setInTime(sdf.format(o.getOrderTime()));
		 }
         resultMap.put("orderList", indexOrders);
		 resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
	     renderJson(response, resultMap);
		 
	 }
	 
	 
	 
	 /**
	  * 将订单设置为不可显示
	  */
	 @RequestMapping("/wap/clerk/orderShow")
	 public void orderShow(HttpServletRequest request,HttpServletResponse response) throws Exception{
		 Map<String,Object> resultMap=Maps.newHashMap();
		 SessionUser su = getCurrentUser(request);
		 String orderNo=request.getParameter("orderNo");
		 Order order=orderService.queryByOrderNo(orderNo);
		 //如果该订单不是该用户名下 则不允许修改
		 if(!order.getClerkId().equals(su.getUserId())){
			 //抛出异常
			 throw new SystemException("对不起您没有权限");
		 }
		 order.setIsShow(1);
		 //将该订单的设置为不显示
		 getOrderDao().updateByOrderNo(order);
		 resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
	        renderJson(response, resultMap);
		 
	 }
	 
	 
	 /**
	  * 将积分订单设置为不可显示
	  */
	 @RequestMapping("/wap/clerk/integralShow")
	 public void integralShow(HttpServletRequest request,HttpServletResponse response) throws Exception{
		 Map<String,Object> resultMap=Maps.newHashMap();
		 SessionUser su = getCurrentUser(request);
		 String id=request.getParameter("id");
         GetIntegral get=getIntegralService.queryById(id);
         if(!get.getClerkId().equals(su.getUserId())){
			 //抛出异常
			 throw new SystemException("对不起您没有权限");
		 }
		 get.setIsShow(1); //将该订单的设置为不显示
		 getIntegralService.updateShow(get);
		 resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
	        renderJson(response, resultMap);
		 
	 }
	 
	 
	 /**
	  * 后台店员列表查询
	  */
	 @RequestMapping("/wap/clerk/list")
	 public ModelAndView list(HttpServletRequest request) throws Exception{
		  //获取省份地址
	        List<Address> provinceL = addressService.queryByPid("0");
	        request.setAttribute("provinceL", provinceL);
		 return new ModelAndView("clerk/list");
	 }
	 
	 
	 /**
	  * 店员自身订单列表查询  判断订单类型 
	  * 前端展示进行中和已完成的订单，并分别对两种状态实现分页
	  */
	 @RequestMapping("/wap/clerk/clerkOrders")
	 public ModelAndView clerkOrders(HttpServletRequest request,HttpServletResponse response) throws Exception{
		 SessionUser su = getCurrentUser(request);
		 if(su!=null&&su.getType()==SystemConstant.USER_TYPE_CLERK){
			 return new ModelAndView("clerk/clerkOrders");
		 }else{
			 return new ModelAndView("clerk/login"); 
		 }
        
	 }
	 
	 
	 /**
	  * 店员订单列表初始化页面数据
	  */
	 @RequestMapping("/wap/clerk/addOrders")
	 public void addOrders(HttpServletRequest request,HttpServletResponse response) throws Exception{
		 //存入缓存的当前页信息 要区分进行中 和已完成
		 Map<String,Object> resultMap=Maps.newHashMap();
		 SessionUser su = getCurrentUser(request);
		     Clerk clerk=clerkService.queryById(su.getUserId());
		     Order o=new Order();
     		 o.setClerkId(clerk.getId());
		     //得到该店员创建的所有订单
             List<Order> list=orderService.queryList(o);
             //该店员所有进行中的订单的集合
   		     List<ClerkOrderList> proceedOrders=new ArrayList<ClerkOrderList>();
   		     //该店员所有已完成或已取消的订单的集合
   		     List<ClerkOrderList> finishOrders=new ArrayList<ClerkOrderList>();
   		       for(Order order:list){
		    	  //如果订单已完成或已取消
		    	  if(order.getOrderStatus()==50||order.getOrderStatus()==60){
		    		    ClerkOrderList	c=getList(order);
		                finishOrders.add(c);
		    	 }else{
		         //订单状态为正在进行中
		    		    ClerkOrderList c=getList(order);
		    		    proceedOrders.add(c);
		          }
		     }
   		    Page p = getPageByRequest(request);
   		    int start=p.getStart();//开始数
            int end=p.getStart()+p.getPageSize();//结尾数
            //进行中列表实际显示
            List<ClerkOrderList> process=getRealOrders(proceedOrders,p,start,end);
            //已完成列表实际显示
            List<ClerkOrderList> finish=getRealOrders(finishOrders,p,start,end);
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
            resultMap.put("process", process);
            resultMap.put("finish", finish);
  		    renderJson(response, resultMap);
  			 
	 }
	 
	 //得到实际追加列表集合
	 public List<ClerkOrderList> getRealOrders(List<ClerkOrderList> proceedOrders,Page p,int start,int end){
		  List<ClerkOrderList> process=new ArrayList<ClerkOrderList>();
		  if(proceedOrders.size()<=p.getPageSize()&&start==0){
           	for(ClerkOrderList od:proceedOrders){
           		process.add(od);
           	}
           }else if(proceedOrders.size()>end){
           	for(int i=start;i<end;i++){
           		process.add(proceedOrders.get(i));
           	}
           	
           }else if(proceedOrders.size()>=start&&proceedOrders.size()<=end){
           	for(int i=start;i<proceedOrders.size();i++){
           		process.add(proceedOrders.get(i));
           	}
           }
		 
		 return process;
	 }
	 
	 
	 /**
	  * 店员订单列表进行中列表追加数据
	  */
	 @RequestMapping("/wap/clerk/proceedOrders")
	 public void proceedOrders(HttpServletRequest request,HttpServletResponse response) throws Exception{
		 Map<String,Object> resultMap=Maps.newHashMap();
		 SessionUser su = getCurrentUser(request);
	     Clerk clerk=clerkService.queryById(su.getUserId());
	     Order o=new Order();
 		 o.setClerkId(clerk.getId());
	     //得到该店员创建的所有订单
         List<Order> list=orderService.queryList(o);
         //该店员所有进行中的订单的集合
		 List<ClerkOrderList> proceedOrders=new ArrayList<ClerkOrderList>();
		 for(Order order:list){
	    	  if(order.getOrderStatus()!=50&&order.getOrderStatus()!=60){
	    		    ClerkOrderList	c=getList(order);
	    		    proceedOrders.add(c);
	    	 }
		 }
		    Page p = getPageByRequest(request);
		    int start=p.getStart();//开始数
            int end=p.getStart()+p.getPageSize();//结尾数
         //进行中列表实际显示
         List<ClerkOrderList> process=getRealOrders(proceedOrders,p,start,end);
         resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
         resultMap.put("list", process);
		 renderJson(response, resultMap);
	   }
	 
	 
	 /**
	  * 店员订单列表已完成列表追加数据
	  */
	 @RequestMapping("/wap/clerk/finishOrders")
	 public void finishOrders(HttpServletRequest request,HttpServletResponse response) throws Exception{
		 Map<String,Object> resultMap=Maps.newHashMap();
		 SessionUser su = getCurrentUser(request);
		  Clerk clerk=clerkService.queryById(su.getUserId());
		  Order o=new Order();
	 	  o.setClerkId(clerk.getId());
		  //得到该店员创建的所有订单
	      List<Order> list=orderService.queryList(o);
	      
	      //该店员所有已完成或已取消的订单的集合
		  List<ClerkOrderList> finishOrders=new ArrayList<ClerkOrderList>();
		  for(Order order:list){
	    	  //如果订单已完成或已取消
	    	  if(order.getOrderStatus()==50||order.getOrderStatus()==60){
	    		    ClerkOrderList	c=getList(order);
	                finishOrders.add(c);
	    	 }
	       }
		    Page p = getPageByRequest(request);
 		    int start=p.getStart();//开始数
            int end=p.getStart()+p.getPageSize();//结尾数
          //已完成列表实际显示
          List<ClerkOrderList> finish=getRealOrders(finishOrders,p,start,end);
          resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
          resultMap.put("list", finish);
		  renderJson(response, resultMap);
	 }
	 
	 

	 
	 
	 
	 /**
	  *将符合条件的订单状态为封装的ClerkOrderList类
	  */
	 public ClerkOrderList getList(Order order){
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 ClerkOrderList c=new ClerkOrderList();
	    	c.setInTime(sdf.format(order.getInTime()));
	    	c.setOrderStatus(order.getOrderStatus());
	    	c.setIsComment(order.getIsComment());
	    	 //查询该订单所选的所有维修项目
			 List<OrderDetail> AllDetails=orderDetailService.queryByOrderNo(order.getOrderNo());
			 //只显示最新的维修项目，如果没有修改过那么type的值为0，如果工程师修改了维修项目那该值为1
			 List<OrderDetail> details=new ArrayList<OrderDetail>();
			 //被更新了的维修项目集合
			 List<OrderDetail> UpdateDetails=new ArrayList<OrderDetail>();
			 for(int i=0;i<AllDetails.size();i++){
				 if(AllDetails.get(i).getType()==1){
					 details.add(AllDetails.get(i));
				 }else{
					 UpdateDetails.add(AllDetails.get(i));
				 }
			 }
			 if(details.size()==0){
				 details=UpdateDetails;
			 }
	    	
	    	
         String projects="";//表示维修项目
         BigDecimal price=new BigDecimal("0");//表示维修总价
	    	for(int i=0;i<details.size();i++){
	    		projects=projects+details.get(i).getProjectName()+",";
	    	    price=price.add(details.get(i).getRealPrice());   
	    	}
	    	projects = projects.substring(0,projects.length()- 1);
	    	//设置维修项目，维修总价,订单号
	    	c.setProjects(projects);
	    	c.setOrderPrice(price);
	    	c.setModel(order.getModelName());
            c.setColor(order.getColor());
            c.setOrderNo(order.getOrderNo());
		 return  c;
	 }
	 
	
	 
	 /**
	  * 订单详情
	  */
	 @RequestMapping("/wap/clerk/orderDetail")
	 public ModelAndView orderDetail(HttpServletRequest request,HttpServletResponse response) throws Exception{
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 SessionUser su = getCurrentUser(request);
		 if(su==null||su.getType()!=SystemConstant.USER_TYPE_CLERK){
			 return new ModelAndView("clerk/login"); 
		 }
		 Clerk clerk=clerkService.queryById(su.getUserId());
		 String orderNo=request.getParameter("orderNo");
		 Order order=orderService.queryByOrderNo(orderNo);
		 //如果该订单不是该用户名下 则不允许修改
		 if(!order.getClerkId().equals(su.getUserId())){
			 //抛出异常
			 throw new SystemException("对不起您没有权限");
		 }
		 GetIntegral getIntegral=getIntegralService.queryByOrder(orderNo);
		 //下单时间	
		 String time=sdf.format(order.getInTime());
		 //如果订单已完成则查询订单的用户评价
		 if(order.getIsComment()==1&&order.getOrderStatus()==50){
			 OrderComment comment=orderCommentService.queryByOrderNo(order.getOrderNo());
			 request.setAttribute("comment",comment.getContent());
		 }
		 //查询该订单所选的所有维修项目
		 List<OrderDetail> AllDetails=orderDetailService.queryByOrderNo(orderNo);
		 //只显示最新的维修项目，如果没有修改过那么type的值为0，如果工程师修改了维修项目那该值为1
		 List<OrderDetail> details=new ArrayList<OrderDetail>();
		 //被更新了的维修项目集合
		 List<OrderDetail> UpdateDetails=new ArrayList<OrderDetail>();
		 //标记其他维修费用
		 boolean other=false;
		 OrderDetail otherProject=null;
		 for(int i=0;i<AllDetails.size();i++){
			 if(AllDetails.get(i).getProjectName().equals("其它")){
				 otherProject=AllDetails.get(i);
				 other=true;
			 }
			 if(AllDetails.get(i).getType()==1){
				 details.add(AllDetails.get(i));
			 }else{
				 UpdateDetails.add(AllDetails.get(i));
			 }
		 }
		 if(details.size()==0){
			 details=UpdateDetails;
		 }
		  String projects="";//表示维修项目
		  BigDecimal price=order.getRealPriceSubCoupon();//表示用户需付实际价格
	    	for(int i=0;i<details.size();i++){
	    		projects=projects+details.get(i).getProjectName()+",";
	    	}
	    	projects = projects.substring(0,projects.length()- 1);
	    	//根据机型id得到该机型支持的维修项目和对应维修费用
	     List<RepairCost> repairCostList = repairCostService.queryListByModelId(order.getModelId());
	     if(other){
	    	 RepairCost r=new RepairCost();
	    	 r.setProjectName(otherProject.getProjectName());
	    	 r.setPrice(otherProject.getRealPrice());
	    	 repairCostList.add(r);
	     }
	     if(order.getIsUseCoupon()==1){
	    	 Coupon c = couponService.queryByCode(order.getCouponCode());
	    	 if(c == null){
	         	throw new SystemException("优惠码不存在");
	         }
	    	 List<CouponModel> couModels = couponModelService.queryListByCouponId(c.getId());
	         List<CouponProject> couProjects = couponProjectService.queryListByCouponId(c.getId());
	         request.setAttribute("c", c);
	         request.setAttribute("models", couModels);
	         request.setAttribute("Couponprojects", couProjects);
	     }
	     request.setAttribute("getIntegral", getIntegral);
	     request.setAttribute("repairCostList",repairCostList);	
		 request.setAttribute("projects", projects);
		 request.setAttribute("price", price);
         request.setAttribute("order", order);
         request.setAttribute("time", time);
         request.setAttribute("detail", AllDetails);
		 return new ModelAndView("clerk/orderDetail");
	 }
	 
	 /**
	  * 忘记密码页面
	  */
	 @RequestMapping("/wap/clerk/forgot")
	 public ModelAndView forgot(HttpServletRequest request) throws Exception{
		 SessionUser su = getCurrentUser(request);
		 if(su==null||su.getType()!=SystemConstant.USER_TYPE_CLERK){
			 return new ModelAndView("clerk/login"); 
		 }
		 return new ModelAndView("clerk/forgot");
	 }
	 
	 @RequestMapping("/wap/clerk/saveForgot")
	 public void saveForgot(HttpServletRequest request,HttpServletResponse response) throws Exception{
        Map<String,Object> resultMap=Maps.newHashMap();
        String name=request.getParameter("name");
        String identityCard=request.getParameter("identityCard");
        String tel=request.getParameter("tel");
		Clerk clerk=clerkService.queryByTel(tel);
		if(clerk==null){
			 resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
             resultMap.put(RESULTMAP_KEY_MSG, "手机号不匹配"); 
		}else if(!clerk.getIdentityCard().equals(identityCard)){
			 resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
             resultMap.put(RESULTMAP_KEY_MSG, "身份证不匹配"); 
		}else if(!clerk.getName().equals(name)){
			 resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
             resultMap.put(RESULTMAP_KEY_MSG, "姓名不匹配"); 
		}else{
			//重置密码并发送短信
			clerkService.resetPassword(clerk);
			resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
		}
		 renderJson(response, resultMap);
	 }
	 
	 /**
	  * 店员列表查询
	  * @param request
	  * @param response
	  * @throws Exception
	  */
	 @RequestMapping("/wap/clerk/queryListForPage")
	 public void queryListForPage(HttpServletRequest request,HttpServletResponse response) throws Exception{
		 Clerk clerk=new Clerk(); 
	     String queryStartTime = request.getParameter("query_startTime");
	     String queryEndTime = request.getParameter("query_endTime");
	     String province = request.getParameter("queryProvince");
	     String city = request.getParameter("queryCity");
	     String county = request.getParameter("queryCounty");
	     String tel= request.getParameter("query_customerMobile");
	     String isRealName= request.getParameter("addRealName");
	    
	     clerk.setTel(tel);
		 clerk.setQueryStartTime(queryStartTime);
		 clerk.setQueryEndTime(queryEndTime);
		 clerk.setProvince(province);
		 clerk.setCity(city);
		 clerk.setCounty(county);
		 if(isRealName!=null&&isRealName!=""){
		 clerk.setIsRealName(Integer.parseInt(isRealName));
		 }
		 Page page = getPageByRequest(request);
	     clerk.setPage(page);
	     List<Clerk> list=clerkService.queryListForPage(clerk);
	     page.setData(list);
	     this.renderJson(response, page);
	 }
	 
	 /**
	  * 店员创建订单
	  */
	 @RequestMapping("/wap/clerk/createOrder")
	 public ModelAndView createOrder(HttpServletRequest request,HttpServletResponse response) throws Exception{
		SessionUser su = getCurrentUser(request);
		if(su==null||su.getType()!=SystemConstant.USER_TYPE_CLERK){
			 return new ModelAndView("clerk/login");
		}
		Brand b = new Brand();
    	b.setIsDel(0);
    	//查询品牌
        List<Brand> brands = brandService.queryList(null);
        if(brands != null && brands.size() > 0){
        	b = brands.get(0);
        	Model m = new Model();
        	m.setBrandId(b.getId());
        	m.setIsDel(0);
        	List<Model> models = modelService.queryList(m);
        	request.setAttribute("models", models);
        }
        request.setAttribute("brands", brands);
        //获取省份地址
        List<Address> provinceL = addressService.queryByPid("0");
        request.setAttribute("provinceL", provinceL);
        return new ModelAndView("clerk/createOrder");
	    }
	 
	
	 /**
	  * 保存订单
	  */
	 @RequestMapping("/wap/clerk/saveOrder")
	 public void saveOrder(HttpServletRequest request,HttpServletResponse response) throws Exception{
	     //获取用户session信息
		 SessionUser su = getCurrentUser(request);
		 //获取该店员的id
		 String clerkId=su.getUserId();
		 
		 String brandId=request.getParameter("brandId");
		 String modelId=request.getParameter("modelId");
		 String colorId=request.getParameter("colorId");
		 String projectIds=request.getParameter("projectId");
		 String customerName=request.getParameter("customerName");
		 String customerMobile=request.getParameter("customerMobile");
		 String note=request.getParameter("note");
		 String couponCode=request.getParameter("couponCode");
		 String province=request.getParameter("province");
	     String city=request.getParameter("city");
	     String county=request.getParameter("county");
	     //表示详细地址 街道/小区
	     String address=request.getParameter("areas");
		 //表示省市县
	     String areas=request.getParameter("homeAddress");
		   //用户信息
	        Customer cust = new Customer();
	        cust.setName(customerName);
	        cust.setMobile(customerMobile);
	        cust.setProvince(province);
	        cust.setCity(city);
	        cust.setCounty(county);
	        cust.setStreet("0");
	        cust.setAreas(areas);
	        cust.setAddress(address);
	        //订单信息
	        Order o = new Order();
	        o.setBrandId(brandId);
	        o.setModelId(modelId);
	        o.setColor(colorId);
	        o.setIsMobile(1);
	        o.setRepairType(0);
	        o.setPostscript(note);
	        o.setCouponCode(couponCode);
	        o.setOrderStatus(OrderConstant.ORDER_STATUS_DEPOSITED);
	        //设置订单店员
	        o.setClerkId(clerkId);
	        //保存订单
	        orderService.save(o, projectIds, cust,false);
	        
	        try{
		        //支付完成自动派单
		        orderService.autoDispatch(o);
	        }catch(Exception e){
	        	e.printStackTrace();
	        }
	    	Map<String, Object> resultMap = Maps.newHashMap();
	    	resultMap.put(RESULTMAP_KEY_DATA, o.getId());
	        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
	        renderJson(response, resultMap);
	 }
	 
	 
	 /**
	     * 优惠券信息无权限查询
	     */
	    @RequestMapping("/wap/clerk/couponInfo")
	    public void couponInfo(HttpServletRequest request
	    		, HttpServletResponse response) throws IOException {
	    	Map<String, Object> resultMap = Maps.newHashMap();
	        //获取优惠码
	        String couponCode = request.getParameter("couponCode");
	        if(StringUtils.isBlank(couponCode)){
	        	throw new SystemException("请填写优惠码");
	        }
	        Coupon c = couponService.queryByCode(couponCode);
	        if(c == null){
	        	throw new SystemException("优惠码不存在");
	        }
	        List<CouponModel> couModels = couponModelService.queryListByCouponId(c.getId());
	        List<CouponProject> couProjects = couponProjectService.queryListByCouponId(c.getId());
	        resultMap.put(RESULTMAP_KEY_DATA, c);
	        resultMap.put("models", couModels);
	        resultMap.put("projects", couProjects);
	        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
	        renderJson(response, resultMap);
	    }
	 
	 /**
	  * 我的信息
	  */
	 @RequestMapping("/wap/clerk/myNews")
	 public ModelAndView myNews(HttpServletRequest request,HttpServletResponse response) throws Exception{
		 SessionUser su = getCurrentUser(request);
		 if(su==null||su.getType()!=SystemConstant.USER_TYPE_CLERK){
			 return new ModelAndView("clerk/login");
		 }
		 Clerk clerk=clerkService.queryById(su.getUserId());
		 request.setAttribute("name", clerk.getName());
		 request.setAttribute("tel", clerk.getTel());
		 request.setAttribute("identityCard", clerk.getIdentityCard());
		 request.setAttribute("address", clerk.getAddress());
		 return new ModelAndView("clerk/myNews");
	 }
	 
	 /**
	  * 修改手机号
	  */
	 @RequestMapping("/wap/clerk/updateTel")
	 public ModelAndView updateTel(HttpServletRequest request,HttpServletResponse response) throws Exception{
		 SessionUser su = getCurrentUser(request);
		 if(su==null||su.getType()!=SystemConstant.USER_TYPE_CLERK){
			 return new ModelAndView("clerk/login");
		 }
		 Clerk clerk=clerkService.queryById(su.getUserId());
		 request.setAttribute("tel", clerk.getTel());
		 return new ModelAndView("clerk/updateTel");
	 }	 
	 
	 /**
	  * 更新手机号
	  */
	 @RequestMapping("/wap/clerk/saveUpdateTel")
	 public void saveUpdateTel(HttpServletRequest request,HttpServletResponse response) throws Exception{
		 Map<String, Object> resultMap = Maps.newHashMap();
		 SessionUser su = getCurrentUser(request);
		 Clerk clerk=clerkService.queryById(su.getUserId());
		  String oldTel=request.getParameter("oldTel");
	      String newTel=request.getParameter("newTel");
	      String code=request.getParameter("code");
	      //判断新手机号是否已被注册
	      Clerk newClerk=clerkService.queryByTel(newTel);
	         if(clerk==null){
		 		 resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
	             resultMap.put(RESULTMAP_KEY_MSG, "用户不存在"); 
		 	 
	         } else if(!checkRandomCode(request,oldTel,code)){
	        	 resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
	             resultMap.put(RESULTMAP_KEY_MSG, "手机号或验证码输入错误");
		 	 } else if(newClerk!=null){
	             resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
	             resultMap.put(RESULTMAP_KEY_MSG, "修改的新手机号已被注册"); 
			 }
		 	 else{
				 clerk.setTel(newTel);
				 clerk.setUpdateUserId(clerk.getName());
				 getDao().update(clerk);
				 resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
			 }
			 renderJson(response, resultMap);
	      
	 } 
	/**
	 * 更新身份证号	 
	 */
	 @RequestMapping("/wap/clerk/saveIdentityCard")
	 public void saveIdentityCard(HttpServletRequest request,HttpServletResponse response) throws Exception{
		 Map<String,Object> resultMap=Maps.newHashMap();
		 SessionUser su = getCurrentUser(request);
		 Clerk clerk=clerkService.queryById(su.getUserId());
		 String identityCard=request.getParameter("identityCard");
		 String tel=request.getParameter("tel");
		 String code=request.getParameter("code");
		 String password=request.getParameter("password");
		 
		 if(clerk==null){
	 		 resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
             resultMap.put(RESULTMAP_KEY_MSG, "用户不存在"); 
         } else if(!checkRandomCode(request,tel,code)){
        	 resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
             resultMap.put(RESULTMAP_KEY_MSG, "手机号或验证码输入错误");
	 	 } else if(!MD5Util.encodePassword(password).equals(clerk.getCode())){
             resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
             resultMap.put(RESULTMAP_KEY_MSG, "密码错误"); 
		 }
	 	 else{
             clerk.setIdentityCard(identityCard);
	 		 clerk.setUpdateUserId(clerk.getName());
			 getDao().update(clerk);
			 resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
		 }
		 renderJson(response, resultMap);
	 }	 
	 
	 /**
	  * 修改身份证号
	  */
	 @RequestMapping("/wap/clerk/updateIdentityCard")
	 public ModelAndView updateIdentityCard(HttpServletRequest request,HttpServletResponse response) throws Exception{
		 SessionUser su = getCurrentUser(request);
		 if(su==null||su.getType()!=SystemConstant.USER_TYPE_CLERK){
			 return new ModelAndView("clerk/login");
		 }
		 Clerk clerk=clerkService.queryById(su.getUserId());
		 return  new ModelAndView("clerk/updateIdentityCard");
	 }	 
	 
	  /**
	   * 修改登录密码
	   */
	 @RequestMapping("/wap/clerk/updatePassword")
	 public ModelAndView updatePassword(HttpServletRequest request,HttpServletResponse response) throws Exception{
		 SessionUser su = getCurrentUser(request);
		 if(su==null||su.getType()!=SystemConstant.USER_TYPE_CLERK){
			 return new ModelAndView("clerk/login");
		 }
		 Clerk clerk=clerkService.queryById(su.getUserId());
		 return new ModelAndView("clerk/updatePassword");
	 }	 
	 
	 /**
	  * 更新密码
	  */
	 @RequestMapping("/wap/clerk/savePassword")
	 public void savePassword(HttpServletRequest request,HttpServletResponse response) throws Exception{
		 Map<String,Object> resultMap=Maps.newHashMap();
		 SessionUser su = getCurrentUser(request);
		 Clerk clerk=clerkService.queryById(su.getUserId());
		 String oldPassword=request.getParameter("oldPassword");
		 String newPassword=request.getParameter("newPassword");
		 String rePassword=request.getParameter("rePassword");
		 if(clerk==null){
	 		 resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
             resultMap.put(RESULTMAP_KEY_MSG, "用户不存在"); 
         } 
	 	 else if(! MD5Util.encodePassword(oldPassword.toUpperCase()).equals(clerk.getCode())){
             resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
             resultMap.put(RESULTMAP_KEY_MSG, "密码错误"); 
		 }
	 	 else{
             clerk.setCode( MD5Util.encodePassword(newPassword.toUpperCase()));
	 		 clerk.setUpdateUserId(clerk.getName());
			 getDao().update(clerk);
			 resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
		 }
		 renderJson(response, resultMap);
	 }	
	 
	 /**
	  * 注销登录
	  */
	 @RequestMapping("/wap/clerk/logout")
	 public ModelAndView logout(HttpServletRequest request,HttpServletResponse response) throws Exception{
		 SessionUser su = getCurrentUser(request);
		 Clerk clerk=clerkService.queryById(su.getUserId());  
		 try {
	            sessionUserService.removeSessionUser(request);
	        } 
	        catch (Exception e) {
	            e.printStackTrace();

	        }
		 return new ModelAndView("clerk/login");
	 }
	 
	 /**
	  * 删除店员
	  */
	 @RequestMapping("/wap/clerk/delete")
	 public void delete(HttpServletRequest request,HttpServletResponse response) throws Exception{
	        Map<String,Object> resultMap=Maps.newHashMap();
	        //获取需要删除的id
	        String id=request.getParameter("id");
	        Clerk clerk=clerkService.queryById(id);
	        //获取当前登录用户信息
	        SessionUser su=getCurrentUser(request);
	        clerkService.deleteClerk(clerk,su.getUserName());
	        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
	        resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
	        renderJson(response, resultMap);
	    }
	 
	 /**
	  * 编辑店员
	  */
	 @RequestMapping("/wap/clerk/edit")
	 public ModelAndView edit(HttpServletRequest request,
             HttpServletResponse response) throws Exception {
	        String id = request.getParameter("id");
	        Clerk clerk=clerkService.queryById(id);
	        //获取省份地址
	        List<Address> provinceL = addressService.queryByPid("0");
	        //获取市地址
	        List<Address> cityL = addressService.queryByPid(clerk.getProvince());
	        //获取区县地址
	        List<Address> countyL = addressService.queryByPid(clerk.getCity());
	        request.setAttribute("provinceL", provinceL);
	        request.setAttribute("cityL", cityL);
	        request.setAttribute("countyL", countyL);
		    request.setAttribute("clerk", clerk);
           String returnView ="clerk/editClerk";
     return new ModelAndView(returnView);
} 
	 
	 /**
	  * 保存店员修改信息
	  */
	 @RequestMapping("/wap/clerk/update")
	    public void update(HttpServletRequest request,
	                              HttpServletResponse response) throws Exception {
		    SessionUser su = getCurrentUser(request);
	        Map<String, Object> resultMap = Maps.newHashMap();
	        String id = request.getParameter("id");
	        String name=request.getParameter("name");
	        String tel=request.getParameter("tel");
	        String wechatId=request.getParameter("wechatId");
	        String identityCard=request.getParameter("identityCard");
	        String addRealName= request.getParameter("addRealName");
	        String province = request.getParameter("addProvince");
	        String city = request.getParameter("addCity");
	        String county = request.getParameter("addCounty");
	        String street=request.getParameter("addAddress");
	        String areas = request.getParameter("addAreas");
	        //手机号只能修改成数据库中未被注册的手机号
	        Clerk test=clerkService.queryByTel(tel);
	        Clerk clerk=clerkService.queryById(id);
	        if(test!=null&&!clerk.getTel().equals(tel)){
	        	  resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
	              resultMap.put(RESULTMAP_KEY_MSG, "填写的手机号已被注册"); 
	        }else{
	        clerk.setName(name);
	        clerk.setTel(tel);
	        clerk.setWechatId(wechatId);
	        clerk.setIdentityCard(identityCard);
	        clerk.setIsRealName(Integer.parseInt(addRealName));
	        clerk.setProvince(province);
	        clerk.setCity(city);
	        clerk.setCounty(county);
	        clerk.setStreet(street);
	        clerk.setAreas(areas);
	        clerk.setAddress(areas+" "+street);
	        clerkService.updateClerk(clerk, su.getUserName());
	        //修改积分获得记录表中信息
	          GetIntegral getIntegral=new GetIntegral();
	          getIntegral.setClerkId(clerk.getId());
	        List<GetIntegral> getIntegralList=getIntegralService.queryListForPage(getIntegral);
	        for(GetIntegral g:getIntegralList){
	        	g.setName(name);
		        g.setTel(tel);
		        getIntegralService.saveUpdate(g);
	        }
	        
	        //修改积分兑换表中对应信息
	          Integral integral=new Integral();
	          integral.setClerkId(clerk.getId());
	        List<Integral> integralList=integralService.queryListForPage(integral);  
	          for(Integral in:integralList){
	        	  in.setName(name);
	        	  in.setTel(tel);
	        	  in.setWechatId(wechatId);
	        	  in.setIsRealName(Integer.parseInt(addRealName));
	        	  integralService.saveUpdate(in);
	          }
	          
	        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
	        resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
	        }
	        renderJson(response, resultMap);
	    }
	 
	 /**
	  * 店员条款
	  */
	 @RequestMapping("/wap/clerk/Instructions")
	 public ModelAndView Instructions(){
		 
		 return new ModelAndView("clerk/Instructions_for_use");
	 } 
	 
	 /**
      * 积分兑换订单列表查询
      * 
      */
	 @RequestMapping("/wap/integral/list")
	 public ModelAndView list(HttpServletRequest request,HttpServletResponse response) throws Exception{
		 return new ModelAndView("integral/list");
	 }
	 
}