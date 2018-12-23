package com.kuaixiu.webpc.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.exception.SystemException;
import com.common.paginate.Page;
import com.common.util.QRCodeUtil;
import com.common.wechat.bean.result.WxMpOAuth2AccessToken;
import com.google.common.collect.Maps;
import com.google.zxing.WriterException;
import com.kuaixiu.coupon.entity.Coupon;
import com.kuaixiu.coupon.service.CouponService;
import com.kuaixiu.customer.entity.Customer;
import com.kuaixiu.customer.service.CustomerService;
import com.kuaixiu.engineer.entity.Engineer;
import com.kuaixiu.engineer.service.EngineerService;
import com.kuaixiu.oldtonew.entity.Agreed;
import com.kuaixiu.oldtonew.entity.InTimeCompare;
import com.kuaixiu.oldtonew.entity.NewOrder;
import com.kuaixiu.oldtonew.entity.NewOrderList;
import com.kuaixiu.oldtonew.entity.OldToNewUser;
import com.kuaixiu.oldtonew.entity.OrderShow;
import com.kuaixiu.oldtonew.entity.UpdateTimeCompare;
import com.kuaixiu.oldtonew.service.AgreedService;
import com.kuaixiu.oldtonew.service.NewOrderService;
import com.kuaixiu.oldtonew.service.OldToNewService;
import com.kuaixiu.order.constant.OrderConstant;
import com.kuaixiu.order.entity.Order;
import com.kuaixiu.order.entity.OrderComment;
import com.kuaixiu.order.entity.OrderDetail;
import com.kuaixiu.order.entity.OrderPayLog;
import com.kuaixiu.order.service.OrderCommentService;
import com.kuaixiu.order.service.OrderDetailService;
import com.kuaixiu.order.service.OrderPayService;
import com.kuaixiu.order.service.OrderService;
import com.kuaixiu.project.entity.CancelReason;
import com.kuaixiu.project.service.CancelReasonService;
import com.kuaixiu.shop.entity.Shop;
import com.kuaixiu.shop.service.ShopService;
import com.system.api.entity.ResultData;
import com.system.basic.user.entity.LoginUser;
import com.system.basic.user.entity.SessionUser;
import com.system.basic.user.entity.SysUser;
import com.system.basic.user.service.SessionUserService;
import com.system.basic.user.service.SysUserService;
import com.system.constant.ApiResultConstant;

import jodd.util.StringUtil;

/**
 * 微信订单controller
 *
 * @author yq
 */
@Controller
public class PCOrderController extends BaseController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDetailService detailService;
    @Autowired
    private OrderCommentService commentService;
    @Autowired
    private SysUserService userService;
    @Autowired
    private SessionUserService sessionUserService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private ShopService shopService;
    @Autowired
    private OrderPayService orderPayService;
    @Autowired
    private NewOrderService newOrderService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private OldToNewService oldToNewService;
    @Autowired
    private EngineerService engineerService;
    @Autowired
    private AgreedService agreedService;
    @Autowired
    private CouponService couponService;
    @Autowired
    private CancelReasonService cancelReasonService;
    

    /**
     * 登录验证
     *
     * @param request
     */
    @RequestMapping("/webpc/checkLogin")
    public void checkLogin(HttpServletRequest request, 
            HttpServletResponse response) throws IOException {
        Map<String, Object> resultMap = Maps.newHashMap();
        try {
            //获取用户手机号
            String mobile = request.getParameter("mobile");
            //获取验证码
            String validateCode = request.getParameter("checkCode");
            //验证手机号和验证码
            SysUser user = userService.checkWechatLogin(mobile);
            if (!checkRandomCode(request, mobile, validateCode)) {
                resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
                resultMap.put(RESULTMAP_KEY_MSG, "验证码错误");
            }else if (user == null) {
                resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
                resultMap.put(RESULTMAP_KEY_MSG, "该手机用户不存在");
            } 
            else {
                //初始化SessionUser
                sessionUserService.initSessionUser(user, request);
                resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
                resultMap.put(RESULTMAP_KEY_MSG, "登录成功");
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
            resultMap.put(RESULTMAP_KEY_MSG, "系统异常请稍后");
        }
        renderJson(response, resultMap);
    }

   
    
  
   
    
    /**
     * 发起支付余款
     * @param request
     * @param response
     * @throws IOException
     * @CreateDate: 2016-9-17 上午12:13:53
     */
    @RequestMapping("/webpc/order/startPayBalance")
    public void startPayBalance(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	ResultData result = new ResultData();
    	JSONObject jsonResult=new JSONObject();
    	try {
    		getLoginUser(request);//验证token
        	SessionUser su=getCurrentUser(request);//得到当前用户               
    		JSONObject params=getPrarms(request);                 //获取请求参数
    		String id=params.getString("id");
    		Order order=orderService.queryById(id);
    		NewOrder newOrder=(NewOrder) newOrderService.queryById(id);
    		if(order==null&&newOrder==null){
    			throw new SystemException("订单不存在!");
    		}
    		        String ip=getIpAddress(request); //获取用户请求的主机IP地址
    		        System.out.println(ip);
    		        OrderPayLog payLog = new OrderPayLog();
    		        payLog.setTradeType("NATIVE");
    		        payLog.setSpbillCreateIp(ip);
    		        payLog.setBody("M-超人-支付余款");
    		        payLog.setAttach("balance");
    		        payLog.setOpenid("");
    		        payLog.setExpenseType(OrderConstant.ORDER_EXPENSE_TYPE_BALANCE);
    		        if(order!=null){    //维修订单支付
    		            payLog = orderPayService.startWechatPay(id, payLog, su);
    		        }else{              //以旧换新支付
    		        	payLog = orderPayService.newOrderStartWechatPay(id, payLog, su);
    		        }
    		        
    		        if (payLog.getPayStatus() == OrderConstant.ORDER_PAY_STATUS_SUBMITED) {
    		            //提交成功
    		            try {
    		                //生成二维码
    		                String imageBase64 = QRCodeUtil.createQRCodeOfBase64(payLog.getCodeUrl());
    		                jsonResult.put("data", imageBase64);
    		                jsonResult.put("pay_status", payLog.getPayStatus());
    		                sessionUserService.getSuccessResult(result, jsonResult);
    		            } 
    		            catch (WriterException e) {
    		                e.printStackTrace();
    		                result.setSuccess(false);
    		                result.setResultCode(ApiResultConstant.resultCode_5001);
    		                result.setResultMessage("二维码生成错误，请稍后再试。");
    		            }
    		        }
    		        else if(payLog.getPayStatus() == OrderConstant.ORDER_PAY_STATUS_SUCCESS){
    		        	jsonResult.put("pay_status", payLog.getPayStatus());
    		        	result.setSuccess(false);
    		        	result.setResultMessage("订单已支付");
    		        	result.setResult(jsonResult);
    		        }
    		        else {
    		        	jsonResult.put("pay_status", payLog.getPayStatus());
    		        	result.setSuccess(false);
    		        	result.setResultMessage(payLog.getErrCodeDes());
    		            result.setResult(jsonResult);
    		        }
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
