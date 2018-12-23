package com.kuaixiu.screen.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.FileUploadBase.FileSizeLimitExceededException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.exception.SystemException;
import com.common.paginate.Page;
import com.common.util.Base64Util;
import com.common.util.MD5Util;
import com.common.util.NOUtil;
import com.common.util.SmsSendUtil;
import com.common.wechat.api.WxMpService;
import com.common.wechat.bean.result.WxMpPayRefundResult;
import com.google.common.collect.Maps;
import com.kuaixiu.brand.entity.Brand;
import com.kuaixiu.customer.entity.Customer;
import com.kuaixiu.join.entity.JoinNews;
import com.kuaixiu.order.constant.OrderConstant;
import com.kuaixiu.order.entity.Order;
import com.kuaixiu.order.entity.OrderComment;
import com.kuaixiu.order.entity.OrderDetail;
import com.kuaixiu.order.entity.OrderRefundLog;
import com.kuaixiu.order.service.OrderPayService;
import com.kuaixiu.order.service.OrderRefundLogService;
import com.kuaixiu.project.entity.CancelReason;
import com.kuaixiu.screen.entity.ScreenBrand;
import com.kuaixiu.screen.entity.ScreenCustomer;
import com.kuaixiu.screen.entity.ScreenOrder;
import com.kuaixiu.screen.entity.ScreenProject;
import com.kuaixiu.screen.service.ScreenBrandService;
import com.kuaixiu.screen.service.ScreenCustomerService;
import com.kuaixiu.screen.service.ScreenOrderService;
import com.kuaixiu.screen.service.ScreenProjectService;
import com.kuaixiu.shop.entity.Shop;
import com.system.api.entity.ResultData;
import com.system.basic.address.entity.Address;
import com.system.basic.user.entity.LoginUser;
import com.system.basic.user.entity.SessionUser;
import com.system.basic.user.entity.SysUser;
import com.system.basic.user.entity.SysUserRole;
import com.system.basic.user.service.SessionUserService;
import com.system.basic.user.service.SysUserRoleService;
import com.system.basic.user.service.SysUserService;
import com.system.constant.ApiResultConstant;
import com.system.constant.SystemConstant;

import jodd.util.StringUtil;

/**
 * @author: anson
 * @CreateDate: 2017年10月17日 下午5:09:07
 * @version: V 1.0
 * 
 */
@Controller
public class ScreenOrderController extends BaseController {

	@Autowired
	private ScreenOrderService screenOrderService;
	@Autowired
	private ScreenBrandService screenBrandService;
	@Autowired
	private ScreenProjectService screenProjectService;
	@Autowired
	private SessionUserService sessionUserService;
	@Autowired
	private OrderPayService orderPayService;
	@Autowired
	private OrderRefundLogService refundLogService;
	@Autowired
	private WxMpService wxMpService;
	@Autowired
	private SysUserService sysUserService;
    @Autowired
	private SysUserRoleService sysUserRoleService;
    @Autowired
	private ScreenCustomerService screenCustomerService;
	
	private String Ext_Name = "gif,jpg,jpeg,png";

	/**
	 * 碎屏险订单提交
	 */
	@RequestMapping("/screen/order/save")
	@ResponseBody
	public void saveOrder(HttpServletRequest request, HttpServletResponse response) throws IOException {
		ResultData result = new ResultData();
		JSONObject jsonResult = new JSONObject();
		try {
			JSONObject params = getPrarms(request);
			String name = params.getString("name"); // 获取客户姓名
			String mobile = params.getString("mobile"); // 获取手机号
			String code = params.getString("code"); // 获取验证码
			String projectId = params.getString("projectId"); // 获取碎屏险项目
			String fm = params.getString("fm"); // 系统来源
			if (!checkRandomCode(request, mobile, code)) { // 验证手机号和验证码
				throw new SystemException("手机号或验证码输入错误");
			}
			// 判断该手机号是否购买过碎屏险 如果购买成功过则不允许其再次购买任何碎屏险项目
			ScreenOrder order = new ScreenOrder();
			order.setMobile(mobile);
			order.setIsPayment(OrderConstant.SCREEN_ORDER_IS_PAYMENT);
			List<ScreenOrder> list = screenOrderService.queryList(order);
			if (list != null && list.size() > 0) {
				throw new SystemException("该手机号已购买过碎屏险，请勿重复购买！");
			}
			// 根据projectId获取对应碎屏险项目详细信息 该结果应该是唯一的
			ScreenOrder o = new ScreenOrder();
			ScreenProject project = screenProjectService.queryById(projectId);
			if (project != null) {
				o.setProjectName(project.getName());
				o.setOrderPrice(project.getPrice());
				o.setRealPrice(project.getPrice());
				o.setMaxPrice(project.getMaxPrice());
				// 查找对应品牌
				ScreenBrand brand = screenBrandService.queryById(project.getBrandId());
				if (brand != null) {
					o.setProjectBrand(brand.getName());
				} else {
					throw new SystemException("该碎屏险品牌不存在！");
				}
			} else {
				throw new SystemException("该碎屏险项目不存在!");
			}

			// 开始保存订单 初始化数据
			o.setProjectId(projectId);
			o.setName(name);
			o.setMobile(mobile);
			o.setId(UUID.randomUUID().toString().replace("-", ""));
			o.setOrderNo(NOUtil.getNo("PO-"));
			o.setOrderStatus(OrderConstant.SCREEN_ORDER_INACTIVE); // 默认状态未付款
			o.setIsPayment(0); // 先保存订单 默认未付款
			o.setIsDrawback(0); // 默认无需退款
			o.setFromSystem(fm);
			o.setIsDel(0);
			o.setIsActive(0);
			screenOrderService.save(o);
			removeRandomCode(request, mobile);  // 最后移除验证码
			jsonResult.put("id", o.getId());    // 返回订单id
			// 如果是支付0元的项目 返回标示 并且直接向碎屏险商发送订单信息
            if(SystemConstant.SCREEN_CODE.contains(project.getProductCode())){
            	orderPayService.screenPaySuccess(o);   
            	jsonResult.put("pay", "0");
            }
			sessionUserService.getSuccessResult(result, jsonResult);
		} catch (SystemException e) {
			e.printStackTrace();
			sessionUserService.getSystemException(e, result);
		} catch (Exception e) {
			e.printStackTrace();
			sessionUserService.getException(result);
		}
		renderJson(response, result);
	}

	/**
	 * 碎屏险订单列表
	 */
	@RequestMapping(value = "/screen/orderList")
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String returnView = "screen/listForAdmin";
		return new ModelAndView(returnView);
	}

	/**
	 * 刷新数据
	 */
	@RequestMapping(value = "screen/order/queryListForPage")
	public void queryListForPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		SessionUser su = getCurrentUser(request);
		if (su.getType() != SystemConstant.USER_TYPE_SYSTEM&&su.getType()!=SystemConstant.USER_TYPE_SCREEN) {
			throw new SystemException("对不起，您没有操作权限!");
		}
		// 获取查询条件
		String orderNo = request.getParameter("query_orderNo");
		String status = request.getParameter("query_orderStates");
		String mobile = request.getParameter("query_customerMobile");
		String queryStartTime = request.getParameter("query_startTime");
		String queryEndTime = request.getParameter("query_endTime");
		String isActive = request.getParameter("query_isActive");   //是否免激活  
		ScreenOrder order = new ScreenOrder();
		order.setOrderNo(orderNo);
		if (StringUtils.isNotBlank(status)) {
			order.setOrderStatus(Integer.parseInt(status));
		}
		if (StringUtils.isNotBlank(isActive)) {
			order.setIsActive(Integer.parseInt(isActive));
		}
		order.setMobile(mobile);
		order.setQueryStartTime(queryStartTime);
		order.setQueryEndTime(queryEndTime);
		Page page = getPageByRequest(request);
		order.setPage(page);
		List<ScreenOrder> list = screenOrderService.queryListForPage(order);
		page.setData(list);
		this.renderJson(response, page);
	}

	/**
	 * 订单详情
	 */
	@RequestMapping(value = "screen/order/detail")
	public ModelAndView detail(HttpServletRequest request, HttpServletResponse response) throws Exception {
		SessionUser su = getCurrentUser(request);
		if (su.getType() != SystemConstant.USER_TYPE_SYSTEM&&su.getType()!=SystemConstant.USER_TYPE_SCREEN) {
			throw new SystemException("对不起，您没有操作权限!");
		}
		String id = request.getParameter("id");
		ScreenOrder o = screenOrderService.queryById(id);
		if (o == null) {
			throw new SystemException("订单未找到！！");
		}
		if(o.getIsActive()==1){
			Date inTime = o.getInTime();
			Calendar calendar=new GregorianCalendar(); 
			calendar.setTime(inTime); 
			calendar.add(Calendar.MONTH, 6);
			inTime=calendar.getTime(); 
			request.setAttribute("outOfTime", inTime);
			ScreenCustomer queryByOrderNo = screenCustomerService.queryByOrderNo(o.getOrderNo());
	        if(queryByOrderNo!=null){
	        	request.setAttribute("importTime", queryByOrderNo.getInTime());
	        }else{
	        	request.setAttribute("importTime", null);
	        }
		}
		request.setAttribute("order", o);
		String returnView = "screen/detail";
		return new ModelAndView(returnView);
	}

	/**
	 * 碎屏险订单退款
	 */
	@RequestMapping(value = "screen/order/refund")
	public void refund(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ResultData result = new ResultData();
		JSONObject jsonResult = new JSONObject();
		SessionUser su = getCurrentUser(request);
		try {
			String id = request.getParameter("id");
			String reason = request.getParameter("reason"); // 退款原因
			ScreenOrder o = screenOrderService.queryById(id);
			if (o == null) {
				throw new SystemException("订单不存在!");
			} else {
				o.setRefundReason(reason);
				// 开始退款流程 得到退款的订单
				OrderRefundLog refLog = orderPayService.screenPayRefund(o, su);
				if (refLog != null) {
					if (refLog.getRefundStatus() == OrderConstant.ORDER_REFUND_STATUS_SUCCESS) {
						// 退款申请提交成功
						result.setSuccess(true);
						result.setResultCode("0");
					} else {
						result.setSuccess(false);
						result.setResultMessage(refLog.getErrCodeDes());
					}
				} else {
					result.setSuccess(false);
					result.setResultMessage("退款申请失败");
				}
			}
		} catch (SystemException e) {
			e.printStackTrace();
			sessionUserService.getSystemException(e, result);
		} catch (Exception e) {
			e.printStackTrace();
			sessionUserService.getException(result);
		}
		renderJson(response, result);
	}

	/**
	 * 取消订单
	 */
	@RequestMapping(value = "screen/order/cancel")
	public void cancel(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ResultData result = new ResultData();
		JSONObject jsonResult = new JSONObject();
		try {
			String id = request.getParameter("id");
			ScreenOrder o = screenOrderService.queryById(id);
			if (o == null) {
				throw new SystemException("订单不存在!");
			} else if (o.getIsPayment() == OrderConstant.SCREEN_ORDER_IS_PAYMENT) {
				throw new SystemException("该订单已支付，请联系客服取消退款！");
			} else {
				o.setOrderStatus(OrderConstant.SCREEN_ORDER_CANCEL);
				o.setEndTime(new Date());
				screenOrderService.saveUpdate(o);
				sessionUserService.getSuccessResult(result, jsonResult);
			}
		} catch (SystemException e) {
			e.printStackTrace();
			sessionUserService.getSystemException(e, result);
		} catch (Exception e) {
			e.printStackTrace();
			sessionUserService.getException(result);
		}
		renderJson(response, result);
	}

	/**
	 * 删除订单
	 */
	@RequestMapping(value = "screen/order/delete")
	public void delete(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ResultData result = new ResultData();
		JSONObject jsonResult = new JSONObject();
		SessionUser su = getCurrentUser(request);
		try {
			if (su.getType() != SystemConstant.USER_TYPE_SYSTEM) {
				throw new SystemException("对不起，您没有操作权限!");
			}
			String id = request.getParameter("id");
			ScreenOrder order = screenOrderService.queryById(id);
			if (order == null) {
				throw new SystemException("订单不存在!");
			}
			order.setIsDel(1);
			screenOrderService.saveUpdate(order);
			sessionUserService.getSuccessResult(result, jsonResult);
		} catch (SystemException e) {
			e.printStackTrace();
			sessionUserService.getSystemException(e, result);
		} catch (Exception e) {
			e.printStackTrace();
			sessionUserService.getException(result);
		}
		renderJson(response, result);
	}

	/**
	 * 当调用接口提交失败后 订单状态会改为 提交失败 此时管理员可后台再尝试向碎屏险公司发起订单提交
	 */
	@RequestMapping(value = "screen/order/commitOrder")
	public void commitOrder(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ResultData result = new ResultData();
		JSONObject jsonResult = new JSONObject();
		SessionUser su = getCurrentUser(request);
		try {
			if (su.getType() != SystemConstant.USER_TYPE_SYSTEM) {
				throw new SystemException("对不起，您没有操作权限!");
			}
			String id = request.getParameter("id");
			ScreenOrder order = screenOrderService.queryById(id);
			orderPayService.screenPaySuccess(order); // 再次向碎屏险公司发起订单提交申请
			sessionUserService.getSuccessResult(result, jsonResult);

		} catch (SystemException e) {
			e.printStackTrace();
			sessionUserService.getSystemException(e, result);
		} catch (Exception e) {
			e.printStackTrace();
			sessionUserService.getException(result);
		}
		renderJson(response, result);
	}

	/**
	 * 手动查询 订单退款是否成功
	 */
	@RequestMapping(value = "screen/order/queryRefundNews")
	public void queryRefundNews(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ResultData result = new ResultData();
		SessionUser su = getCurrentUser(request);
		try {
			String id = request.getParameter("id");
			ScreenOrder o = screenOrderService.queryById(id);
			if (su.getType() != SystemConstant.USER_TYPE_SYSTEM
					&& su.getType() != SystemConstant.USER_TYPE_CUSTOMER_SERVICE) {
				throw new SystemException("对不起，您无权操作该订单！");
			}
			if (o == null) {
				throw new SystemException("订单不存在!");
			} else {
				// 开始查询退款订单 针对申请退款成功了 但是可能还未到账的退款订单
				List<OrderRefundLog> refLog = refundLogService.queryByPayOrderNo(o.getOrderNo());
				if (refLog == null || refLog.isEmpty()) {
					throw new SystemException("该订单没有成功提交过退款申请！");
				}
				// 向微信发起查询退款进度结果
				o = orderPayService.queryPayRefund(o, refLog.get(0));
				if (o.getOrderStatus() == OrderConstant.SCREEN_ORDER_REFUNDED) {
					result.setSuccess(true); // 微信退款已到账
					result.setResultCode("0");
					result.setResultMessage(o.getRefundResult());
				} else {
					result.setSuccess(false);
					result.setResultMessage(o.getRefundResult());
				}
			}
		} catch (SystemException e) {
			e.printStackTrace();
			sessionUserService.getSystemException(e, result);
		} catch (Exception e) {
			e.printStackTrace();
			sessionUserService.getException(result);
		}
		renderJson(response, result);
	}

	/**
	 * 上传品牌图标
	 */
	@RequestMapping(value = "screen/uploadLogo")
	public void upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ResultData result = new ResultData();
		JSONObject jsonResult = new JSONObject();
		SessionUser su = getCurrentUser(request);
		try {
			if (su.getType() != SystemConstant.USER_TYPE_SYSTEM) {
				throw new SystemException("对不起，您无权操作该订单！");
			}
			String id = request.getParameter("filename"); // 品牌id
			String logoPath = getPath(request); // 图片路径
			ScreenBrand b = screenBrandService.queryById(id);
			if (b == null) {
				throw new SystemException("品牌不存在！");
			}
			b.setLogo(logoPath);
			screenBrandService.saveUpdate(b);
			sessionUserService.getSuccessResult(result, jsonResult);
		} catch (SystemException e) {
			e.printStackTrace();
			sessionUserService.getSystemException(e, result);
		} catch (Exception e) {
			e.printStackTrace();
			sessionUserService.getException(result);
		}
		renderJson(response, result);
	}

	/**
	 * 保存文件
	 */
	public String getPath(HttpServletRequest request) {
		String fileName = ""; // 上传的文件名
		String path = ""; // 存储路径
		try {
			// 转化request
			MultipartHttpServletRequest rm = (MultipartHttpServletRequest) request;
			MultipartFile mfile = rm.getFile("file"); // 获得前端页面传来的文件
			byte[] bfile = mfile.getBytes(); // 获得文件的字节数组
			fileName = mfile.getOriginalFilename(); // 获得文件名
			System.out.println("文件名称：" + fileName + "大小：" + bfile.length); // 输出测试
			// 处理获取到的上传文件的文件名的路径部分，只保留文件名部分
			fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);

			// 得到上传文件的扩展名
			String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
			// 保存文件时为防止文件名的冲突 统一将文件名改为当前时间戳命名格式
			fileName = System.currentTimeMillis() + "." + fileExt;
			// 检查扩展名
			if (!Ext_Name.contains(fileExt)) {
				throw new SystemException("上传文件扩展名是不允许的扩展名：" + fileExt);
			} else {
				// 保存文件
				path = saveFile(bfile, fileName, request);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return path;
	}

	/**
	 * 碎屏险商发送 订单已激活通知
	 */
	@RequestMapping(value = "screen/updateOrderStatus")
	public void updateOrderStatus(@RequestBody String params, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ResultData result = new ResultData();
		JSONObject jsonResult = new JSONObject();
		try {
			String header = request.getHeader("WWW-Authenticate");// 得到的约定头值
			if (header != null) {
				String headerValue = "Basic "
						+ Base64Util.getBase64(SystemConstant.SCREEN_USERNAME_RECEIVE + ":" + SystemConstant.SCREEN_PASSWORD_RECEIVE);
				if (!header.equals(headerValue)) {
					throw new SystemException("用户身份认证失败");
				}
			} else {
				throw new SystemException("用户身份认证失败");
			}
			JSONObject parse = (JSONObject) JSONObject.parse(params);
			String orderNo = parse.getString("order_no");
			String orderStatus = parse.getString("order_status");
			String mobileBrand = parse.getString("mobile_brand");
			String mobileModel = parse.getString("mobile_model");
			String mobileCode = parse.getString("mobile_code");
			if (StringUtils.isBlank(orderNo) || StringUtils.isBlank(orderStatus) || StringUtils.isBlank(mobileBrand)
					|| StringUtils.isBlank(mobileModel) || StringUtils.isBlank(mobileCode)) {
				throw new SystemException("参数不完整");
			}
			ScreenOrder order = screenOrderService.queryByOrderNo(orderNo);
			if (order == null) {
				throw new SystemException("订单不存在!");
			}
			order.setMobileBrand(mobileBrand);
			order.setMobileModel(mobileModel);
			order.setMobileCode(mobileCode);
			order.setOrderStatus(Integer.parseInt(orderStatus));
			ScreenProject project = screenProjectService.queryById(order.getProjectId());
			if(project!=null){
				int time = project.getMaxTime();
				// 计算过期时间
				Calendar now = Calendar.getInstance();
				now.add(Calendar.MONTH ,time);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String dateStr = sdf.format(now.getTimeInMillis());
				order.setDueToTime(dateStr);   
			}
			
			screenOrderService.saveUpdate(order);
			sessionUserService.getSuccessResult(result, jsonResult);
		} catch (SystemException e) {
			e.printStackTrace();
			sessionUserService.getSystemException(e, result);
		} catch (Exception e) {
			e.printStackTrace();
			sessionUserService.getException(result);
		}
		renderJson(response, result);
	}
	
	
	
	
	    /**
		 * 碎屏险商登录用户
		 */
		@RequestMapping(value = "/screen/account.do")
		public ModelAndView accountlist(HttpServletRequest request, HttpServletResponse response) throws Exception {

			String returnView = "screen/account";
			return new ModelAndView(returnView);
		}
		
		
		/**
		 * 刷新碎屏险商数据
		 */
		@RequestMapping(value = "screen/account/queryListForPage")
		public void accountQueryListForPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
			SessionUser su = getCurrentUser(request);
			if (su.getType() != SystemConstant.USER_TYPE_SYSTEM) {
				throw new SystemException("对不起，您没有操作权限!");
			}
			SysUser user=new SysUser();
			user.setType(SystemConstant.USER_TYPE_SCREEN);
			user.setIsDel(0);
			Page page = getPageByRequest(request);
			user.setPage(page);
			List<SysUser> list=sysUserService.queryListForPage(user);
			page.setData(list);
			this.renderJson(response, page);
		}
		
		
		
		/**
	     * add增加碎屏险商用户
	     * @param request
	     * @param response
	     * @return
	     * @throws Exception
	     */
	    @RequestMapping(value = "screen/account/add")
	    public ModelAndView add(HttpServletRequest request,
	                              HttpServletResponse response) throws Exception {
	        
	        String returnView ="screen/addAccount";
	        return new ModelAndView(returnView);
	    }
	    
	    /**
	     * @param request
	     * @param response
	     * @return
	     * @throws Exception
	     */
	    @RequestMapping(value = "screen/account/save")
	    public void save(HttpServletRequest request,
	                              HttpServletResponse response) throws Exception {
	    	SessionUser su = getCurrentUser(request);
	    	if (su.getType() != SystemConstant.USER_TYPE_SYSTEM) {
				throw new SystemException("对不起，您没有操作权限!");
			}
	        Map<String, Object> resultMap = Maps.newHashMap();
	        String account = request.getParameter("addAccount");
	        String password = request.getParameter("addPassword");
	        String name = request.getParameter("addName");
	        if(StringUtil.isBlank(name)||StringUtil.isBlank(password)||StringUtil.isBlank(account)){
	        	throw new SystemException("请填写完整信息");
	        }
	        //先查询该用户是否已注册
	        SysUser queryById = sysUserService.queryById(account);
	        if(queryById!=null){
	        	throw new SystemException("该账号名已被注册!");
	        }
	        SysUser user=new SysUser();
	        user.setLoginId(account);
	        user.setPassword(MD5Util.encodePassword(password));
	        user.setUname(name);
	        user.setType(SystemConstant.USER_TYPE_SCREEN);
	        user.setUid(account);
	        user.setIsDel(0);
	        user.setCreateUserid(su.getUserId());
	        sysUserService.add(user);   //增加用户
	        // 配置角色权限
	        SysUserRole role=new SysUserRole();
	        role.setUserId(account);
	        role.setState(0);
	        role.setRoleId(SystemConstant.USER_ROLE_SPECIAL_CUSTOMER);
	        sysUserRoleService.add(role);
	        
	        
	        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
	        resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
	        renderJson(response, resultMap);
	    }
	    
	    
	    
	    
	    /**
	     * delete
	     * @param request
	     * @param response
	     * @return
	     * @throws Exception 
	     * 删除碎屏险商用户
	     */
	    @RequestMapping(value = "screen/account/delete")
	    public void deleteAccount(HttpServletRequest request,
	                              HttpServletResponse response) throws Exception {
	        
	        Map<String, Object> resultMap = Maps.newHashMap();
	        //login_id
	        String id = request.getParameter("id");
	        SessionUser su = getCurrentUser(request);
	        if (su.getType() != SystemConstant.USER_TYPE_SYSTEM) {
				throw new SystemException("对不起，您没有操作权限!");
			}
	        sysUserService.deleteUser(id, su.getUserId());
	        
	        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
	        resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
	        renderJson(response, resultMap);
	    }
	    
	    
	    
	   
	    
	    
	    
	  
		

	public static void main(String[] args) {
		String headerValue = "Basic "
				+ Base64Util.getBase64(SystemConstant.SCREEN_USERNAME_RECEIVE + ":" + SystemConstant.SCREEN_PASSWORD_RECEIVE);
		System.out.println(headerValue);
		Calendar now = Calendar.getInstance();
		now.add(Calendar.MONTH ,6);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateStr = sdf.format(now.getTimeInMillis());
		System.out.println(dateStr);
	}

}
