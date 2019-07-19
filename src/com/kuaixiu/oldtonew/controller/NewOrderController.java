package com.kuaixiu.oldtonew.controller;

import com.common.base.controller.BaseController;
import com.common.exception.SystemException;
import com.common.paginate.Page;
import com.google.common.collect.Maps;
import com.kuaixiu.brand.entity.Brand;
import com.kuaixiu.brand.entity.NewBrand;
import com.kuaixiu.brand.service.BrandService;
import com.kuaixiu.brand.service.NewBrandService;
import com.kuaixiu.customer.service.CustomerService;
import com.kuaixiu.engineer.entity.Engineer;
import com.kuaixiu.engineer.service.EngineerService;
import com.kuaixiu.model.entity.NewModel;
import com.kuaixiu.model.service.NewModelService;
import com.kuaixiu.oldtonew.entity.*;
import com.kuaixiu.oldtonew.service.AgreedService;
import com.kuaixiu.oldtonew.service.NewOrderPayService;
import com.kuaixiu.oldtonew.service.NewOrderService;
import com.kuaixiu.oldtonew.service.OldToNewService;
import com.kuaixiu.order.constant.OrderConstant;
import com.kuaixiu.project.entity.CancelReason;
import com.kuaixiu.project.service.CancelReasonService;
import com.kuaixiu.shop.entity.Shop;
import com.kuaixiu.shop.service.ShopService;
import com.system.basic.address.entity.Address;
import com.system.basic.address.service.AddressService;
import com.system.basic.user.entity.SessionUser;
import com.system.constant.SystemConstant;
import jodd.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
* @author: anson
* @CreateDate: 2017年6月15日 下午4:47:42
* @version: V 1.0
* 
*/
@Controller
public class NewOrderController extends BaseController{

	@Autowired
	private NewOrderService newOrderService;
	@Autowired 
	private AddressService addressService;
	@Autowired
	private BrandService brandService;
	@Autowired
	private CustomerService customerService;
	@Autowired
	private EngineerService engineerService;
	@Autowired
	private OldToNewService oldToNewService;
	@Autowired
	private AgreedService agreedService;
	@Autowired
	private ShopService shopService;
	@Autowired
	private NewOrderPayService orderPayService;
	@Autowired
	private NewBrandService newBrandService;
	@Autowired
	private NewModelService newModelService;
	@Autowired
	private CancelReasonService cancelReasonService;
	/**
	 * 以旧换新实时订单列表
	 */
	 /**
     * 列表查询 -- 后台管理员
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/newOrder/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        //获取登录用户
        SessionUser su = getCurrentUser(request);
        
        String returnView = "";
       
        //获取省份地址
        List<Address> provinceL = addressService.queryByPid("0");
        request.setAttribute("provinceL", provinceL);
        //获取可兑换品牌
        List<NewBrand> newBrands=newBrandService.queryList(null);
        request.setAttribute("newBrands", newBrands);
        
        //判断用户类型系统管理员可以查看所有工程师
        if (su.getType() == SystemConstant.USER_TYPE_SYSTEM || su.getType() == SystemConstant.USER_TYPE_CUSTOMER_SERVICE) {
            List<Brand> brands = brandService.queryList(null);
        	request.setAttribute("brands", brands);
            returnView = "newOrder/listForAdmin";
        }
        else if (su.getType() == SystemConstant.USER_TYPE_PROVIDER) {
            //连锁商只能查看自己的订单
            returnView ="newOrder/listForProvider";
        }
        else if (su.getType() == SystemConstant.USER_TYPE_SHOP) {
            //门店商只能查看自己的订单
            returnView ="newOrder/listForShop";
        }
        else {
            throw new SystemException("对不起，您无权查看此信息！");
        }
        return new ModelAndView(returnView);
    }
	
    /**
     * 根据选择品牌加载机型
     */
    @RequestMapping("/webpc/repair/modelLists")
    public void getModelList(HttpServletRequest request
            , HttpServletResponse response) throws IOException {
        Map<String, Object> resultMap = Maps.newHashMap();
        //选择的手机品牌ID
        String brandId = request.getParameter("brandId");
        //查询该品牌下所有已上架机型
		List<NewModel> models = newModelService.queryPutawayBrandId(brandId); 
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
        resultMap.put(RESULTMAP_KEY_DATA, models);
        renderJson(response, resultMap);
    }
    
  
	
    /**
     * queryListForPage
     * 刷新数据
     */
    @RequestMapping(value = "/newOrder/queryListForPage")
    public void queryListForPage(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
    	//获取查询条件
        String orderNo = request.getParameter("query_orderNo");
        String customerMobile = request.getParameter("query_customerMobile");
        String queryStartTime = request.getParameter("query_startTime");
        String queryEndTime = request.getParameter("query_endTime");
        String orderStates = request.getParameter("query_orderStates");
        String province = request.getParameter("queryProvince");
        String city = request.getParameter("queryCity");
        String county = request.getParameter("queryCounty");
        NewOrder o = new NewOrder();
        if(StringUtil.isNotBlank(orderStates)){
        	if(Integer.parseInt(orderStates)==OrderConstant.ALREADY_COMMENT){
        		o.setIsComment(1);
        		o.setOrderStatus(OrderConstant.ORDER_STATUS_FINISHED);//已完成
        	}else{
        		o.setOrderStatus(Integer.parseInt(orderStates));
        	}
        }
        o.setOrderNo(orderNo);
        o.setCustomerMobile(customerMobile);
        o.setQueryStartTime(queryStartTime);
        o.setQueryEndTime(queryEndTime);
        o.setProvince(province);
        o.setCity(city);
        o.setCounty(county);
        o.setIsDel(0);
      //  if (StringUtils.isNotBlank(orderStates)) {
      //      o.setQueryStatusArray(Arrays.asList(StringUtils.split(orderStates, ",")));;
      //  }
        //获取登录用户
        SessionUser su = getCurrentUser(request);
        //判断用户类型系统管理员可以查看所有订单
        if (su.getType() == SystemConstant.USER_TYPE_SYSTEM || su.getType() == SystemConstant.USER_TYPE_CUSTOMER_SERVICE) {
            
        }
        else if (su.getType() == SystemConstant.USER_TYPE_PROVIDER) {
            //连锁商只能查看自己的订单
            o.setProviderCode(su.getProviderCode());
        }
        else if (su.getType() == SystemConstant.USER_TYPE_SHOP) {
            //门店商只能查看自己的订单
            o.setShopCode(su.getShopCode());
        }
        else {
            throw new SystemException("对不起，您无权查看此信息！");
        }
        
        Page page = getPageByRequest(request);
        o.setPage(page);
        List<NewOrder> lists = newOrderService.queryListForPage(o);
        //将查询结果包装成自定义的展示类
        List<NewOrderList> list=createList(lists);
       
        //订单状态筛选
        page.setData(list);
        this.renderJson(response, page);
    }
   
    
    
    
    /**
     * 用于后台订单列表展示的包装类
     */
    public List<NewOrderList> createList(List<NewOrder> lists){
    	List<NewOrderList> list=new ArrayList<NewOrderList>();
        for(NewOrder order:lists){
        	NewOrderList l=new NewOrderList();
        	l.setId(order.getId());
        	l.setOrderNo(order.getOrderNo());
        	l.setInTime(order.getInTime());
        	l.setOrderPrice(order.getRealPrice());
        	l.setOrderStatus(order.getOrderStatus());
        	l.setDispatchTime(order.getDispatchTime());
        	l.setEngineerId(order.getEngineerId());
            l.setEndTime(order.getEndTime());
            l.setCancelType(order.getCancelType());
            l.setBalanceStatus(order.getBalanceStatus());
            l.setSelectType(order.getSelectType());
            l.setIsComment(order.getIsComment());
        	OldToNewUser u=oldToNewService.queryById(order.getUserId());
            if(u!=null){
            l.setCustomerName(u.getName());
            l.setCustomerMobile(u.getTel());
        	l.setOldMobile(u.getOldMobile());
        	l.setNewMobile(u.getNewMobile());
        	l.setAddress(u.getHomeAddress());
            }
        	Engineer e=engineerService.queryById(order.getEngineerId());
            if(e!=null){
            l.setEngineerNumber(e.getNumber());
            l.setEngineerName(e.getName());
            }
            Shop shop=shopService.queryByCode(order.getShopCode());
            if(shop!=null){
            l.setShopName(shop.getName());
            }
            //如果已预约则显示预约的新手机
            l.setAgreedModel("");
            if(order.getOrderStatus()>OrderConstant.WAIT_AGREED){
            	Agreed agreed = agreedService.queryByOrderNo(order.getOrderNo());
        		if (agreed != null) {
        			l.setAgreedModel(agreed.getNewModelName());
                }
            }
        	list.add(l);
        }
        return list;
    }
    
    
    /**
     * 查看订单详情
     */
    @RequestMapping("/newOrder/detail")
    public ModelAndView newOrderDetail(HttpServletRequest request,HttpServletResponse response){
        
    	  String id = request.getParameter("id");
          NewOrder order = (NewOrder)newOrderService.queryById(id);
          NewOrderList newOrder=newOrderService.createOrderDetail(order);
          //取消原因标签列表
          List<CancelReason> reasonList=cancelReasonService.queryListForPage(new CancelReason());
          //支付情况
          NewOrderPay orderPay=orderPayService.queryByOrderNo(order.getOrderNo());
          if(orderPay!=null){
        	  request.setAttribute("orderPay",orderPay);
          }
          request.setAttribute("reasonList", reasonList);
          request.setAttribute("order", newOrder);
          String returnView = "newOrder/detail";
    	return new ModelAndView(returnView);
    }
    
    /**
     * 取消订单
     */
    @RequestMapping("/newOrder/orderCancel")
    public void orderCancel(HttpServletRequest request, 
            HttpServletResponse response) throws IOException {
        SessionUser su = getCurrentUser(request);
        
        //订单id
        String id = request.getParameter("id");
        String selectReason=request.getParameter("selectReason");
        String reason=request.getParameter("reason");
        String cancelReason=null;//实际存入的取消原因
        if(!StringUtil.isBlank(selectReason)){
        	cancelReason=selectReason;
        }
        if(!StringUtil.isBlank(reason)&&!StringUtil.isBlank(selectReason)){
        	cancelReason=cancelReason+"；"+reason;
        }
        if(!StringUtil.isBlank(reason)&&StringUtil.isBlank(selectReason)){
        	cancelReason=reason;
        }
        if(su.getType() == SystemConstant.USER_TYPE_SYSTEM){
        	newOrderService.orderCancel(id, OrderConstant.ORDER_CANCEL_TYPE_ADMIN, cancelReason, su);
        }
        else if(su.getType() == SystemConstant.USER_TYPE_CUSTOMER_SERVICE){
        	newOrderService.orderCancel(id, OrderConstant.ORDER_CANCEL_TYPE_CUSTOMER_SERVICE, cancelReason, su);
        }
        
        Map<String, Object> resultMap = Maps.newHashMap();
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
        renderJson(response, resultMap);
    }
    
    
    /**
     * 预约兑换时间
     */
    @RequestMapping(value = "/newOrder/agreedTime")
    public void agreedTime(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        
        Map<String, Object> resultMap = Maps.newHashMap();
        SessionUser su = getCurrentUser(request);
        
   
        String id = request.getParameter("orderId");//订单号id
        String agreedTime = request.getParameter("agreedTime");
        //choose 值为0表示选中的是库中已有手机类型  值为1表示为工程师手动输入的其他机型
        //String choose=request.getParameter("choose");
        // 目前只提供库类已有机型 choose值为0
        String choose="0";
        String modelId=null;//机型id
        String agreedColor=null;//表示选中颜色
        String agreedPrice=null;//表示机型价格
        String agreedOther=null;//表示其他信息
        NewModel model=null;//表示选中机型
        if(choose.equals("0")){
        	 modelId=request.getParameter("query_model");
        	 model=newModelService.queryById(modelId);
        	 agreedColor=request.getParameter("query_color");
        }else{
             agreedPrice=request.getParameter("otherPrice");
             agreedOther=request.getParameter("otherModel");
        }
        NewOrder o=new NewOrder();
        Agreed agreed=new Agreed();
        //设置预约信息
        if(StringUtils.isNotBlank(id)){
        	o=(NewOrder)newOrderService.queryById(id);
        	agreed.setOrderNo(o.getOrderNo());
        	agreed.setEngineerId(o.getEngineerId());
        	 if(!StringUtil.isBlank(agreedTime)){
                 agreed.setAgreedTime(agreedTime);
       	      }
        	  if(!StringUtil.isBlank(agreedColor)){
                  agreed.setColor(agreedColor);
        	  }
        	  if(model!=null){
        		  agreed.setAgreedBrand(model.getBrandName());
        		  agreed.setColor(agreedColor);
        		  agreed.setNewModelName(model.getName());
        		  agreed.setMemory(model.getMemory());
        		  agreed.setEdition(model.getEdition());
        		  agreed.setAgreedOrderPrice(model.getPrice());
        	  }else{
        		  if(!StringUtil.isBlank(agreedPrice)){
            		  BigDecimal b=new BigDecimal(agreedPrice);
                      agreed.setAgreedOrderPrice(b);
                  }
            	  if(!StringUtil.isBlank(agreedOther)){
                      agreed.setOther(agreedOther);
                  }
        	  }
            newOrderService.agreedTime(agreed, agreedTime, su);
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
            resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
        }
        else{
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
            resultMap.put(RESULTMAP_KEY_MSG, "参数为空");
        }

        renderJson(response, resultMap);
    }
    
    
    
    /**
     * queryListForPage
     * 订单调度  针对订单状态为 2  11
     */
    @RequestMapping(value = "/newOrder/queryListMapForPage")
    public void queryListMapForPage(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
    	//获取查询条件
        String orderNo = request.getParameter("query_orderNo");
        String customerMobile = request.getParameter("query_customerMobile");
        String queryStartTime = request.getParameter("query_startTime");
        String queryEndTime = request.getParameter("query_endTime");
        String province = request.getParameter("queryProvince");
        String city = request.getParameter("queryCity");
        String county = request.getParameter("queryCounty");
        String orderState = request.getParameter("query_orderState");
        String orderStates = request.getParameter("query_orderStates");
        String isDispatch = request.getParameter("query_isDispatch");
        NewOrder o = new NewOrder();
        o.setOrderNo(orderNo);
        o.setCustomerMobile(customerMobile);
        o.setQueryStartTime(queryStartTime);
        o.setQueryEndTime(queryEndTime);
        o.setProvince(province);
        o.setCity(city);
        o.setCounty(county);
        if (StringUtils.isNotBlank(orderState)) {
            o.setOrderStatus(Integer.parseInt(orderState));
        }
        if (StringUtils.isNotBlank(orderStates)) {
            o.setQueryStatusArray(Arrays.asList(StringUtils.split(orderStates, ",")));;
        }
        if (StringUtils.isNotBlank(isDispatch)) {
            o.setIsDispatch(Integer.parseInt(isDispatch));
        }
        //获取登录用户
        SessionUser su = getCurrentUser(request);
        //判断用户类型系统管理员可以查看所有订单
        if (su.getType() == SystemConstant.USER_TYPE_SYSTEM || su.getType() == SystemConstant.USER_TYPE_CUSTOMER_SERVICE) {
            
        }
        else if (su.getType() == SystemConstant.USER_TYPE_PROVIDER) {
            //连锁商只能查看自己的订单
            o.setProviderCode(su.getProviderCode());
        }
        else if (su.getType() == SystemConstant.USER_TYPE_SHOP) {
            //门店商只能查看自己的订单
            o.setShopCode(su.getShopCode());
        }
        else {
            throw new SystemException("对不起，您无权查看此信息！");
        }
        
        Page page = getPageByRequest(request);
        o.setPage(page);
        o.setIsDel(0);
        List<Map<String, Object>> list = newOrderService.queryMapForPage(o);
      
           page.setData(list);
        this.renderJson(response, page);
    }
    
    /**
     * 未检修列表查询 -- 后台管理员
     * 订单调度
     */
    @RequestMapping(value = "/newOrder/unCheckedList")
    public ModelAndView unCheckedList(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        //获取登录用户
        SessionUser su = getCurrentUser(request);
        
        List<Address> provinceL = addressService.queryByPid("0");
        request.setAttribute("provinceL", provinceL);
        List<Brand> brands = brandService.queryList(null);
    	request.setAttribute("brands", brands);
    	
        String returnView = "newOrder/unCheckedList";
        return new ModelAndView(returnView);
    }
    
    
    /**
     * 未派单列表查询 -- 后台管理员
     */
    @RequestMapping(value = "/newOrder/unDispatchForAdmin")
    public ModelAndView unDispatchForAdmin(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        //获取登录用户
        SessionUser su = getCurrentUser(request);
        
        String returnView = "newOrder/unDispatchForAdmin";
        return new ModelAndView(returnView);
    }
    
    /**
     * 未派单列表查询 -- 门店商管理员
     */
    @RequestMapping(value = "/newOrder/unDispatchList")
    public ModelAndView unDispatchList(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        //获取登录用户
        SessionUser su = getCurrentUser(request);
        
        Shop s = shopService.queryByCode(su.getShopCode());
        List<Engineer> engList = engineerService.queryListUnDispatchByShopCode(su.getShopCode());
        request.setAttribute("shop", s);
        request.setAttribute("engList", engList);
        
        //门店商只能查看自己的订单
        String returnView = "newOrder/unDispatchForShop";
        
        return new ModelAndView(returnView);
    }

    
    /**
     * 重新派单
     */
    @RequestMapping(value = "/newOrder/reDispatch")
    public void reDispatch(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        Map<String, Object> resultMap = Maps.newHashMap();
        SessionUser su = getCurrentUser(request);
        
        //获取网点id
        String id = request.getParameter("id");
        if(StringUtils.isNotBlank(id)){
            newOrderService.reDispatch(id, su);
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
            resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
        }
        else{
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
            resultMap.put(RESULTMAP_KEY_MSG, "参数为空");
        }
        
        renderJson(response, resultMap);
    }
    
    
    /**
     * 门店订单指派订单给工程师
     */
    @RequestMapping(value = "/newOrder/dispatch")
    public void dispatchToEngineer(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        Map<String, Object> resultMap = Maps.newHashMap();
        SessionUser su = getCurrentUser(request);
        
        //获取网点id
        String id = request.getParameter("id");
        String engId = request.getParameter("engId");
        if(StringUtils.isNotBlank(id) && StringUtils.isNotBlank(engId)){
            newOrderService.dispatchToEngineer(id, engId, su);
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
            resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
        }
        else{
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
            resultMap.put(RESULTMAP_KEY_MSG, "参数为空");
        }
        
        renderJson(response, resultMap);
    }
    
    
    
}
