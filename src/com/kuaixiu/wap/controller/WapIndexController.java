package com.kuaixiu.wap.controller;

import com.common.base.controller.BaseController;
import com.common.exception.SystemException;
import com.common.util.SmsSendUtil;
import com.common.util.ValidatorUtil;
import com.google.common.collect.Maps;
import com.kuaixiu.brand.service.BrandService;
import com.kuaixiu.coupon.entity.Coupon;
import com.kuaixiu.coupon.entity.CouponModel;
import com.kuaixiu.coupon.entity.CouponProject;
import com.kuaixiu.coupon.service.CouponModelService;
import com.kuaixiu.coupon.service.CouponProjectService;
import com.kuaixiu.coupon.service.CouponService;
import com.kuaixiu.model.service.ModelService;
import com.kuaixiu.model.service.RepairCostService;
import com.kuaixiu.order.service.OrderService;
import com.kuaixiu.shop.service.ShopService;
import com.system.basic.user.service.SessionUserService;
import com.system.basic.user.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * 首页控制类.
 * 
 * @CreateDate: 2016-9-20 下午6:50:33
 * @version: V 1.0
 */
@Controller
public class WapIndexController extends BaseController {

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
    
    
    /**
     * 发送手机验证码
     * @throws IOException 
     */
    @RequestMapping("/wap/sendSmsCode")
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
     * 领用优惠券
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/wap/coupon/receive")
    public void receive(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        Map<String, Object> resultMap = Maps.newHashMap();
        //获取优惠券id
        String batchId = request.getParameter("batchId");
        //获取手机号
        String mobile = request.getParameter("mobile");
        //获取手机号
        String checkCode = request.getParameter("checkCode");
        //验证手机号和验证码
        if(!checkRandomCode(request,mobile,checkCode)){
            throw new SystemException("手机号或验证码输入错误");
        }
        Coupon c = couponService.checkReceiveCoupon(batchId, mobile);
        if (c == null) {
        	c = couponService.updateForReceive(batchId, mobile);
        	couponService.receiveSendSms(c);
        }
        resultMap.put(RESULTMAP_KEY_DATA, c);
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
        resultMap.put(RESULTMAP_KEY_MSG, "成功");
        request.getSession().setAttribute("receiveCoupon_", c);
        //最后移除验证码
        removeRandomCode(request, mobile);
        renderJson(response, resultMap);
    }
    
    /**
     * 支付余款
     * @throws ParseException 
     */
    @RequestMapping("/wap/coupon/receiveInfo")
    public String receiveInfo(HttpServletRequest request
    		, HttpServletResponse response) throws IOException, ParseException {
    	Map<String, Object> resultMap = Maps.newHashMap();
    	//验证优惠码是否存在
    	Coupon c = (Coupon)request.getSession().getAttribute("receiveCoupon_");
    	if(c == null){
    		return "wap/error";
    	}
        List<CouponModel> couModels = couponModelService.queryListByCouponId(c.getId());
        List<CouponProject> couProjects = couponProjectService.queryListByCouponId(c.getId());
        request.setAttribute("coupon", c);
        request.setAttribute("couModels", couModels);
        request.setAttribute("couProjects", couProjects);
        return "wap/coupon";
    }
}
