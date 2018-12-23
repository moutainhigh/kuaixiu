package com.kuaixiu.screen.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.exception.SystemException;
import com.common.importExcel.ImportReport;
import com.common.paginate.Page;
import com.common.util.NOUtil;
import com.kuaixiu.screen.entity.ScreenCustomer;
import com.kuaixiu.screen.entity.ScreenOrder;
import com.kuaixiu.screen.entity.ScreenProject;
import com.kuaixiu.screen.service.ScreenCustomerService;
import com.kuaixiu.screen.service.ScreenOrderService;
import com.kuaixiu.screen.service.ScreenProjectService;
import com.system.api.entity.ResultData;
import com.system.basic.user.entity.SessionUser;
import com.system.basic.user.service.SessionUserService;
import com.system.constant.SystemConstant;

/**
* @author: anson
* @CreateDate: 2018年3月5日 下午3:09:23
* @version: V 1.0
* 
*/
@Controller
public class ScreenCustomerController extends BaseController{

	@Autowired
	private ScreenCustomerService screenCustomerService;
	@Autowired
	private SessionUserService sessionUserService;
	@Autowired
	private ScreenProjectService screenProjectService;
	@Autowired
	private ScreenOrderService screenOrderService;
	/**
	 * 免激活用户列表
	 */
	@RequestMapping(value = "screen/freeCustomer")
	public ModelAndView customerList() {
		String returnView = "screen/freeCustomer";
		return new ModelAndView(returnView);
	}

	
	/**
	 * 数据获取
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	 @RequestMapping(value = "screen/freeCustomer/queryListForPage")
	    public void queryListForPage(HttpServletRequest request,
	                              HttpServletResponse response) throws Exception {
			SessionUser su = getCurrentUser(request);
	    	if (su.getType() != SystemConstant.USER_TYPE_SYSTEM ) {
				throw new SystemException("对不起，您没有访问权限!");
			}
	        //获取查询条件
	        String mobile = request.getParameter("query_customerMobile");
	    	String queryStartTime = request.getParameter("query_startTime");
			String queryEndTime = request.getParameter("query_endTime");
			String isActive=request.getParameter("query_isActive");
			String isOutOfTime=request.getParameter("query_isOutOfTime");
	        ScreenCustomer sc=new ScreenCustomer();
	        sc.setMobile(mobile);
	        sc.setQueryStartTime(queryStartTime);
	        sc.setQueryEndTime(queryEndTime);
	        if(StringUtils.isNotBlank(isActive)){
	        	sc.setIsActive(Integer.parseInt(isActive));
	        }
	        if(StringUtils.isNotBlank(isOutOfTime)){
	        	sc.setIsOutOfTime(isOutOfTime);
	        }
	        Page page = getPageByRequest(request);
	        sc.setPage(page);
	        List<ScreenCustomer> list = screenCustomerService.queryListForPage(sc);
	        long now=System.currentTimeMillis();
	        long time=0L;
	        for(ScreenCustomer s:list){
	        	time= s.getInTime().getTime();
				if(now-time>5*24*60*60*1000){
					s.setIsOutOfTime("1");
				}else{
					s.setIsOutOfTime("0");
				}
	        }
	        page.setData(list);
	        this.renderJson(response, page);
	    }
	 
	
	/**
	 * 添加免激活用户
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping(value="screen/addCustomer")
	public void add(HttpServletRequest request, HttpServletResponse response) throws IOException{
		ResultData result = new ResultData();
		JSONObject jsonResult = new JSONObject();
		ScreenCustomer sc=new ScreenCustomer();
		try {
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
			//获取请求数据
			JSONObject params = getPrarms(request);
			String brand=params.getString("brand");
			String model=params.getString("model");
			String imei=params.getString("imei");
			String mobile=params.getString("mobile");
			sc.setId(UUID.randomUUID().toString().replace("-", ""));
			sc.setBrand(brand);
			sc.setModel(model);
			sc.setImei(imei);
		    sc.setMobile(mobile);
		    sc.setIsDel(0);
		    sc.setIsActive(0);
		    screenCustomerService.add(sc);
			
            result.setResult(jsonResult);	
            result.setResultCode("0");
			result.setSuccess(true);
			
		} catch (SystemException e) {
			sessionUserService.getSystemException(e, result);
		} catch (Exception e) {
			e.printStackTrace();
			sessionUserService.getException(result);
		}
		renderJson(response, result);
		
	}
	
	
	/**
	 * 批量导入页面
	 */
	@RequestMapping(value = "screen/importIndex")
	public ModelAndView importIndex() {
		String returnView = "screen/importIndex";
		return new ModelAndView(returnView);
	}
	
	
	  /**
	   * 批量导入免激活用户数据
	   * @param myfile
	   * @param request
	   * @param response
	   * @throws IOException
	   */
	  @RequestMapping(value = "/screen/import")
	    public void doImport(
	            @RequestParam("fileInput") MultipartFile myfile,
	            HttpServletRequest request, HttpServletResponse response)
	            throws IOException {
		    SessionUser su = getCurrentUser(request);
	    	if (su.getType() != SystemConstant.USER_TYPE_SYSTEM ) {
				throw new SystemException("对不起，您没有访问权限!");
			}
	        // 返回结果，默认失败
	        Map<String, Object> resultMap = new HashMap<String, Object>();
	        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
	        ImportReport report = new ImportReport();
	        StringBuffer errorMsg = new StringBuffer();
	        try{
	            if(myfile != null && StringUtils.isNotBlank(myfile.getOriginalFilename())){
	                String fileName=myfile.getOriginalFilename();
	                //扩展名
	                String extension=FilenameUtils.getExtension(fileName);
	                if (!extension.equalsIgnoreCase("xls")&&!extension.equalsIgnoreCase("xlsx")){
	                    errorMsg.append("导入文件格式错误！只能导入excel文件！");
	                }
	                else{
	                    screenCustomerService.importExcel(myfile,report,getCurrentUser(request));
	                    resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
	                    resultMap.put(RESULTMAP_KEY_MSG, "导入成功");
	                }
	            }
	            else{
	                errorMsg.append("导入文件为空");
	            }
	        }
	        catch(Exception e) {
	            e.printStackTrace();
	            errorMsg.append("导入失败");
	            resultMap.put(RESULTMAP_KEY_MSG, "导入失败");
	        }
	        request.setAttribute("report", report);
	        resultMap.put(RESULTMAP_KEY_DATA, report);
	        renderJson(response, resultMap);
	    }
	
	  
	/**
	 * 碎屏险免激活用户激活
	 */
	  @RequestMapping(value="screen/active")
		public void active(HttpServletRequest request, HttpServletResponse response) throws IOException{
			ResultData result = new ResultData();
			JSONObject jsonResult = new JSONObject();
			try {
				JSONObject params = getPrarms(request);
				String name=params.getString("name");
				String mobile=params.getString("mobile");
				String code = params.getString("code"); 
				String fm=params.getString("fm");
				String brand=params.getString("brand");
				String model=params.getString("model");
				if(StringUtils.isBlank(brand)||StringUtils.isBlank(model)){
					throw new SystemException("请求参数不完整");
				}
				if (!checkRandomCode(request, mobile, code)) { // 验证手机号和验证码
					throw new SystemException("手机号或验证码输入错误");
				}
				//判断该手机号是否已经激活过
				ScreenCustomer s=new ScreenCustomer();
				s.setMobile(mobile);
				List<ScreenCustomer> list=screenCustomerService.queryList(s);
				if(list.isEmpty()){
					throw new SystemException("该手机号不支持免激活！");
				}
				
				if(list.size()>1){
					//判断是否有激活过
					for(ScreenCustomer cust:list){
						if(cust.getIsActive()==1){
							throw new SystemException("该手机号已激活过");
						}
					}
				}
				ScreenCustomer sc=list.get(0);  //实际可激活的对象
				if(StringUtils.isBlank(sc.getImei())){
					throw new SystemException("手机串号不存在");
				}
				//判断品牌和机型是否匹配
				if(!sc.getBrand().equals(brand)||!sc.getModel().equals(model)){
					throw new SystemException("激活品牌和机型不匹配");
				}
				//判断当前激活时间减去数据库录入时间是否小于等于5天
				long now=System.currentTimeMillis();
				long time= sc.getInTime().getTime();
				if(now-time>5*24*60*60*1000){
					throw new SystemException("对不起，已超过激活时间");
				}
				//满足条件给予激活 设置器projectId为1 
				ScreenOrder o=new ScreenOrder();
				o.setId(UUID.randomUUID().toString().replace("-", ""));
				o.setName(name);
				o.setMobile(mobile);
				String orderNo=NOUtil.getNo("PO-");
				o.setOrderNo(orderNo);
				o.setProjectId("1");
				o.setProjectName("vip免激活");
				o.setOrderStatus(6);  //6已激活
				o.setOrderPrice(new BigDecimal("0"));
				o.setRealPrice(new BigDecimal("500"));
				o.setMaxPrice(new BigDecimal("500"));
				o.setIsPayment(1);
				o.setFromSystem(fm);
				o.setEndTime(new Date());
				o.setIsDrawback(0);
				o.setProjectBrand(brand);
				o.setMobileBrand(brand);
				o.setMobileModel(model);
				o.setIsActive(1);
				o.setMobileCode(sc.getImei());
				screenOrderService.add(o);
				
				sc.setIsActive(1);
				sc.setOrderNo(orderNo);
				sc.setName(name);
				screenCustomerService.saveUpdate(sc);
	            result.setResult(jsonResult);	
	            result.setResultCode("0");
				result.setSuccess(true);
			} catch (SystemException e) {
				sessionUserService.getSystemException(e, result);
			} catch (Exception e) {
				e.printStackTrace();
				sessionUserService.getException(result);
			}
			renderJson(response, result);
			
		}
	  
	  /**
	   * 免激活碎屏险可选品牌
	   */
	  @RequestMapping(value="screen/selectBrand")
		public void selectBrand(HttpServletRequest request, HttpServletResponse response) throws IOException{
			ResultData result = new ResultData();
			JSONObject jsonResult = new JSONObject();
			try {
				List<String> list=screenCustomerService.queryAllBrand();
				if(list.isEmpty()){
					throw new SystemException("暂无可激活品牌");
				}
				jsonResult.put("brand",list);
	            result.setResult(jsonResult);	
	            result.setResultCode("0");
				result.setSuccess(true);
				
			} catch (SystemException e) {
				sessionUserService.getSystemException(e, result);
			} catch (Exception e) {
				e.printStackTrace();
				sessionUserService.getException(result);
			}
			renderJson(response, result);
			
		}
	 
	  
	  /**
	   * 品牌下对应可选机型
	   */
	  @RequestMapping(value="screen/selectModel")
		public void selectModel(HttpServletRequest request, HttpServletResponse response) throws IOException{
			ResultData result = new ResultData();
			JSONObject jsonResult = new JSONObject();
			try {
				JSONObject params = getPrarms(request);
				String brand=params.getString("brand");
				if(StringUtils.isBlank(brand)){
					throw new SystemException("请求参数不完整");
				}
				List<String> queryList = screenCustomerService.queryAllModel(brand);
			    if(queryList.isEmpty()){
			    	throw new SystemException("暂无可选机型");
			    }
			    jsonResult.put("model", queryList);
	            result.setResult(jsonResult);	
	            result.setResultCode("0");
				result.setSuccess(true);
				
			} catch (SystemException e) {
				sessionUserService.getSystemException(e, result);
			} catch (Exception e) {
				e.printStackTrace();
				sessionUserService.getException(result);
			}
			renderJson(response, result);
			
		}
	  
}
