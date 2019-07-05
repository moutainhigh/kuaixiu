package com.kuaixiu.webpc.controller;

import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.exception.SystemException;
import com.common.util.SmsSendUtil;
import com.common.util.ValidatorUtil;
import com.google.common.collect.Maps;
import com.kuaixiu.brand.entity.Brand;
import com.kuaixiu.brand.service.BrandService;
import com.kuaixiu.coupon.entity.Coupon;
import com.kuaixiu.coupon.entity.CouponModel;
import com.kuaixiu.coupon.entity.CouponProject;
import com.kuaixiu.coupon.service.CouponModelService;
import com.kuaixiu.coupon.service.CouponProjectService;
import com.kuaixiu.coupon.service.CouponService;
import com.kuaixiu.customer.entity.Customer;
import com.kuaixiu.model.entity.Model;
import com.kuaixiu.model.entity.RepairCost;
import com.kuaixiu.model.service.ModelService;
import com.kuaixiu.model.service.RepairCostService;
import com.kuaixiu.order.constant.OrderConstant;
import com.kuaixiu.order.entity.Order;
import com.kuaixiu.order.service.OrderService;
import com.kuaixiu.shop.entity.Shop;
import com.kuaixiu.shop.service.ShopService;
import com.system.api.entity.ResultData;
import com.system.basic.user.entity.SysUser;
import com.system.basic.user.service.SessionUserService;
import com.system.basic.user.service.SysUserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 首页控制类.
 * 
 * @CreateDate: 2016-9-20 下午6:50:33
 * @version: V 1.0
 */
@Controller
public class PCIndexController extends BaseController {

    @Autowired
    private ModelService modelService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private RepairCostService repairCostService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private SysUserService userService;
    @Autowired
    private SessionUserService sessionUserService;
    @Autowired
    private ShopService shopService;
    @Autowired
    private CouponService couponService;
    @Autowired
    private CouponModelService couponModelService;
    @Autowired
    private CouponProjectService couponProjectService;
    
    private static final Logger log= Logger.getLogger(PCIndexController.class);

    //手机识别
    private final static String[] agent = {"android", "iphone", "ipod","ipad", "windows phone", "mqqbrowser"};
    private static boolean checkAgentIsMobile(String ua){
		boolean flag = false;
		if (!ua.contains("windows nt") 
				|| (ua.contains("windows nt") 
					&& ua.contains("compatible; msie 9.0;"))) {
			// 排除 苹果桌面系统
			if (!ua.contains("windows nt") && !ua.contains("macintosh")) {
				for (String item : agent) {
					if (ua.contains(item)) {
						flag = true;
						break;
					}
				}
			}
		}
		return flag;
    }
    /**
     * 首页
     */
    @RequestMapping("/index")
    public ModelAndView goIndex(HttpServletRequest request) {
    	String ua = request.getHeader("user-agent").toLowerCase();
    	if(checkAgentIsMobile(ua)){ 
    		return new ModelAndView("wechat/wap");
    	}
    	else { 
    	 	return new ModelAndView("webpc/index");
    	}
    }
    
   
 
    /**
     * 根据选择品牌加载机型
     * @throws IOException 
     */
    @RequestMapping("/webpc/repair/modelList")
    public void getModelList(HttpServletRequest request
            , HttpServletResponse response) throws IOException {
        Map<String, Object> resultMap = Maps.newHashMap();
        //选择的手机品牌ID
        String brandId = request.getParameter("brandId");
        //查询所有机型
		List<Model> models = modelService.queryByBrandId(brandId); 
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
        resultMap.put(RESULTMAP_KEY_DATA, models);
        renderJson(response, resultMap);
    }
    
    /**
     * 根据手机型号得到该机型的颜色和可维修项目
     * @throws IOException 
     */
   @RequestMapping("/webpc/repair/modelInfo")
    public void getModelInfo(HttpServletRequest request
            , HttpServletResponse response) throws IOException {
        Map<String, Object> resultMap = Maps.newHashMap();
        //选择的手机型号ID
        String modelId = request.getParameter("modelId");
        //得到所选机型信息
        Model model = modelService.queryById(modelId);
        //得到机型品牌
        Brand brand=brandService.queryById(model.getBrandId());
        String brandName=brand.getName();
        //得到所选机型可维修项目
        List<RepairCost> repairCostList = repairCostService.queryListByModelId(modelId);
        for(RepairCost r:repairCostList){
        	r.setBrandName(brandName);
        }
        if (model == null || repairCostList == null || repairCostList.size() == 0) {
            throw new SystemException("数据加载失败");
        } 
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
        resultMap.put("modelColor", model.getColors());
        resultMap.put("modelFault", repairCostList);
        renderJson(response, resultMap);
    }
    
   
    /**
     * 发送手机验证码
     * @throws IOException 
     */
    @RequestMapping("/webpc/sendSmsCode")
    public void sendSmsCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> resultMap = Maps.newHashMap();
        try {
            //获取手机号
            String mobile = request.getParameter("mobile");
            //验证手机号码
            if(ValidatorUtil.isMobile(mobile)){
                String randomCode = getRandomCode(request, mobile);
                SmsSendUtil.sendCheckCode(mobile, randomCode);
                resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
                //resultMap.put(RESULTMAP_KEY_DATA, randomCode);
            }
            else{
                resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
                resultMap.put(RESULTMAP_KEY_MSG, "请输入正确手机号码");
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
            resultMap.put(RESULTMAP_KEY_MSG, "系统异常请稍后再试");
        }
        renderJson(response, resultMap);
    }
    
    /**
     * 保存订单
     */
    @RequestMapping("/webpc/repair/saveOrder")
    public void saveOrder(HttpServletRequest request
            , HttpServletResponse response) throws IOException {
        Map<String, Object> resultMap = Maps.newHashMap();
        //获取品牌
        String brandId = request.getParameter("brandId");
        //获取机型
        String modelId = request.getParameter("modelId");
        //获取手机号
        String color = request.getParameter("color");
        //获取手机号
        String projectIds = request.getParameter("projectIds");
        String repairType = request.getParameter("repairType");
        //获取手机号
        String username = request.getParameter("username");
        //获取手机号
        String mobile = request.getParameter("mobile");
        //获取手机号
        String checkCode = request.getParameter("checkCode");
        //获取手机号
        String province = request.getParameter("province");
        //获取手机号
        String city = request.getParameter("city");
        //获取手机号
        String county = request.getParameter("county");
        //获取手机号
        String street = request.getParameter("street");
        //获取手机号
        String areas = request.getParameter("areas");
        //获取手机号
        String address = request.getParameter("address");
        //获取手机号
        String note = request.getParameter("note");
        //优惠券
        String couponCode = request.getParameter("couponCode");
        //验证手机号和验证码
        if(!checkRandomCode(request,mobile,checkCode)){
            throw new SystemException("手机号或验证码输入错误");
        }
        
        //系统来源
        String fm = (String)request.getSession().getAttribute("from_system_");
        
        //用户信息
        Customer cust = new Customer();
        cust.setName(username);
        cust.setMobile(mobile);
        cust.setProvince(province);
        cust.setCity(city);
        cust.setCounty(county);
        cust.setStreet(street);
        cust.setAreas(areas);
        cust.setAddress(address);
        //订单信息
        Order o = new Order();
        o.setBrandId(brandId);
        o.setModelId(modelId);
        o.setColor(color);
        o.setIsMobile(0);
        o.setRepairType(Integer.parseInt(repairType));
        o.setPostscript(note);
        o.setCouponCode(couponCode);
        o.setOrderStatus(OrderConstant.ORDER_STATUS_DEPOSITED);
        o.setFromSystem(fm);
        
        
        if(o.getRepairType()==3){
            //如果是寄修
        	 Shop s=shopService.selectShop(province,city);
        	 if(s.getCode()!=null){
        		    //满足寄修条件的门店存在  保存订单
        		    orderService.sendSave(o, projectIds, cust,false); 
        		    //初始化session
        	        SysUser user = userService.checkWechatLogin(mobile);
        	        sessionUserService.initSessionUser(user, request);
        	        //最后移除验证码
        	        removeRandomCode(request, mobile);
        	        try {
						    //订单保存完成后派单给门店
        	        	    o.setProviderCode(s.getProviderCode());
        	                o.setProviderName(s.getProviderName());
        	                o.setShopCode(s.getCode());
        	                o.setShopName(s.getName());
        	                o.setIsDispatch(2);
        	                o.setDispatchTime(new Date());
        	                o.setOrderStatus(OrderConstant.ORDER_STATUS_WAIT_SHOP_SEND_RECEIVE);
        	                orderService.saveUpdate(o);
        	                //寄修订单下单成功，向客户发送下单成功信息
        	                SmsSendUtil.mailSendSmsToCustomer(cust.getMobile());
        	                //向对应门店店主发送下单信息
        	                SmsSendUtil.mailSendSmsToShop(s,o);
					} catch (Exception e) {
						e.printStackTrace();
					}
        	 }
         }else{
        //上门维修方式保存订单
        orderService.save(o, projectIds, cust,false);
        //初始化session
        SysUser user = userService.checkWechatLogin(mobile);
        sessionUserService.initSessionUser(user, request);
        //最后移除验证码
        removeRandomCode(request, mobile);
        try{
	        //支付完成自动派单
	        orderService.autoDispatch(o);
        }catch(Exception e){
        	e.printStackTrace();
        }
        
        }
        resultMap.put(RESULTMAP_KEY_DATA, o.getId());
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
        renderJson(response, resultMap);
    }
    
   
    /**
     * 支付余款
     * @throws ParseException 
     */
    @RequestMapping("/webpc/repair/couponInfo")
    public void couponInfo(HttpServletRequest request
    		, HttpServletResponse response) throws IOException, ParseException {
    	Map<String, Object> resultMap = Maps.newHashMap();
    	//验证优惠码是否存在
    	Coupon c = checkCouponExisit(request);
        List<CouponModel> couModels = couponModelService.queryListByCouponId(c.getId());
        List<CouponProject> couProjects = couponProjectService.queryListByCouponId(c.getId());
        resultMap.put(RESULTMAP_KEY_DATA, c);
        resultMap.put("models", couModels);
        resultMap.put("projects", couProjects);
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
        renderJson(response, resultMap);
    }
    
    
    /**
     * 清除验证码
     */
    @RequestMapping("/webpc/checkLogin/cleanCheckCode")
    public void cleanCheckCode(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        try {
        	JSONObject params=getPrarms(request);
            String mobile = params.getString("mobile");
            removeRandomCode(request, mobile);//清除验证码
            result.setSuccess(true);
            result.setResultCode("0");
        }catch(SystemException e){
            e.printStackTrace();
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        renderJson(response, result);
    }
    
    
}
