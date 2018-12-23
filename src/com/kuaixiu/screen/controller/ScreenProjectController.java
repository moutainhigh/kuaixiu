package com.kuaixiu.screen.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.exception.SystemException;
import com.common.paginate.Page;
import com.google.common.collect.Maps;
import com.kuaixiu.project.entity.Project;
import com.kuaixiu.screen.entity.ScreenBrand;
import com.kuaixiu.screen.entity.ScreenProject;
import com.kuaixiu.screen.service.ScreenBrandService;
import com.kuaixiu.screen.service.ScreenProjectService;
import com.system.api.entity.ResultData;
import com.system.basic.user.entity.SessionUser;
import com.system.basic.user.service.SessionUserService;
import com.system.constant.ApiResultConstant;
import com.system.constant.SystemConstant;

import jodd.util.StringUtil;

/**
 * @author: anson
 * @CreateDate: 2017年10月17日 下午7:54:23
 * @version: V 1.0
 * 
 */
@Controller
public class ScreenProjectController extends BaseController {

	@Autowired
	private ScreenProjectService screenProjectService;
	@Autowired
	private ScreenBrandService screenBrandService;
	@Autowired
	private SessionUserService sessionUserService;

	@RequestMapping(value = "screen/projectList")
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<ScreenBrand> list = screenBrandService.queryList(null);
		request.setAttribute("brands", list);
		String returnView = "screen/projectList";
		return new ModelAndView(returnView);
	}

	/**
	 * queryListForPage
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "screen/project/queryListForPage")
	public void queryListForPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		SessionUser su = getCurrentUser(request);
		if (su.getType() != SystemConstant.USER_TYPE_SYSTEM) {
			throw new SystemException("对不起，您没有操作权限!");
		}
		// 获取查询条件
		String name = request.getParameter("query_name");
		String brandId = request.getParameter("query_brand");
		ScreenProject project = new ScreenProject();
		project.setName(name);
		project.setBrandId(brandId);
		Page page = getPageByRequest(request);
		project.setPage(page);
		List<ScreenProject> list = screenProjectService.queryListForPage(project);
		page.setData(list);
		this.renderJson(response, page);
	}

	/**
	 * add
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "screen/project/add")
	public ModelAndView add(HttpServletRequest request, HttpServletResponse response) throws Exception {
		SessionUser su = getCurrentUser(request);
		if (su.getType() != SystemConstant.USER_TYPE_SYSTEM) {
			throw new SystemException("对不起，您没有操作权限!");
		}
		List<ScreenBrand> list = screenBrandService.queryList(null);
		request.setAttribute("brands", list);
		String returnView = "screen/addProject";
		return new ModelAndView(returnView);
	}

	/**
	 * index
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "screen/project/save")
	public void save(HttpServletRequest request, HttpServletResponse response) throws Exception {
		SessionUser su = getCurrentUser(request);
		Map<String, Object> resultMap = Maps.newHashMap();
		// 获取数据
		String name = request.getParameter("addName");
		String brandId = request.getParameter("addBrandId");
		String price = request.getParameter("addPrice");
		String maxPrice = request.getParameter("addMaxPrice");
		String sort = request.getParameter("addSort");
		String detail = request.getParameter("addDetail");
		String productCode = request.getParameter("addProductCode");
		String maxTime=request.getParameter("maxTime");
		System.out.println("输入最大月份："+maxTime);
		if (su.getType() != SystemConstant.USER_TYPE_SYSTEM) {
			resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
			resultMap.put(RESULTMAP_KEY_MSG, "对不起您没有操作权限!");
		} else {
			ScreenProject p = new ScreenProject();
			p.setName(name);
			p.setCreateUserId(su.getUserId());
			p.setBrandId(brandId);
			if (!StringUtils.isBlank(sort)) {
				p.setSort(Integer.parseInt(sort));
			}
			if (StringUtils.isNotBlank(price)) {
				p.setPrice(new BigDecimal(price));
			}
			if (StringUtils.isNotBlank(maxPrice)) {
				p.setMaxPrice(new BigDecimal(maxPrice));
			}
			if(StringUtils.isNotBlank(maxTime)){
				p.setMaxTime(Integer.parseInt(maxTime));
			}
			p.setDetail(detail);
			p.setIsDel(0);
			p.setProductCode(productCode);
			screenProjectService.save(p);
			resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
			resultMap.put(RESULTMAP_KEY_MSG, "保存成功");

		}
		renderJson(response, resultMap);
	}

	/**
	 * edit
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "screen/project/edit")
	public ModelAndView edit(HttpServletRequest request, HttpServletResponse response) throws Exception {
		SessionUser su = getCurrentUser(request);
		if (su.getType() != SystemConstant.USER_TYPE_SYSTEM) {
			throw new SystemException("对不起，您没有操作权限!");
		}
		// 获取项目id
		String id = request.getParameter("id");
		// 查询项目内容
		ScreenProject project = screenProjectService.queryById(id);
		// 查询品牌内容
		List<ScreenBrand> b = screenBrandService.queryList(null);
		request.setAttribute("project", project);
		request.setAttribute("brands", b);
		String returnView = "screen/editProject";
		return new ModelAndView(returnView);
	}

	/**
	 * update
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "screen/project/update")
	public void update(HttpServletRequest request, HttpServletResponse response) throws Exception {
		SessionUser su = getCurrentUser(request);
		Map<String, Object> resultMap = Maps.newHashMap();
		// 获取项目id
		String id = request.getParameter("id");
		ScreenProject project = screenProjectService.queryById(id);
		if (project == null) {
			resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
			resultMap.put(RESULTMAP_KEY_MSG, "该项目不存在!");
		} else {
			// 获取项目名称
			String brandId = request.getParameter("upBrandId");
			String name = request.getParameter("upName");
			String price = request.getParameter("upPrice");
			String maxPrice = request.getParameter("upMaxPrice");
			String sort = request.getParameter("upSort");
			String detail = request.getParameter("upDetail");
			String productCode = request.getParameter("upProductCode");
			String maxTime=request.getParameter("maxTime");
			if (su.getType() != SystemConstant.USER_TYPE_SYSTEM) {
				resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
				resultMap.put(RESULTMAP_KEY_MSG, "对不起您没有操作权限!");
			} else {
				if (StringUtil.isNotBlank(brandId)) {
					project.setBrandId(brandId);
				}
				project.setName(name);
				project.setDetail(detail);
				project.setUpdateUserId(su.getUserId());
				project.setProductCode(productCode);
				if (!StringUtils.isBlank(sort)) {
					project.setSort(Integer.parseInt(sort));
				}
				if (StringUtils.isNotBlank(price)) {
					project.setPrice(new BigDecimal(price));
				}
				if (StringUtils.isNotBlank(maxPrice)) {
					project.setMaxPrice(new BigDecimal(maxPrice));
				}
				if (StringUtils.isNotBlank(maxTime)) {
					project.setMaxTime(Integer.parseInt(maxTime));
				}
				
				screenProjectService.saveUpdate(project);
				resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
				resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
			}
		}

		renderJson(response, resultMap);
	}

	/**
	 * delete
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "screen/project/delete")
	public void delete(HttpServletRequest request, HttpServletResponse response) throws Exception {

		Map<String, Object> resultMap = Maps.newHashMap();
		// 获取项目id
		String id = request.getParameter("id");
		SessionUser su = getCurrentUser(request);
		ScreenProject project = screenProjectService.queryById(id);
		if (project == null) {
			resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
			resultMap.put(RESULTMAP_KEY_MSG, "该项目不存在!");
		} else {
			if (su.getType() != SystemConstant.USER_TYPE_SYSTEM) {
				resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
				resultMap.put(RESULTMAP_KEY_MSG, "对不起您没有操作权限!");
			} else {
				project.setIsDel(1);
				project.setUpdateUserId(su.getUserId());
				screenProjectService.saveUpdate(project);
				resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
				resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
			}
		}
		renderJson(response, resultMap);
	}

	@RequestMapping("/screen/order/projects")
	@ResponseBody
	public void getModelInfo(HttpServletRequest request, HttpServletResponse response) throws IOException {
		ResultData result = new ResultData();
		try {
			JSONObject params = getPrarms(request);
			String brandId = params.getString("brandId");// 品牌型号
			if (StringUtils.isBlank(brandId)) {
				throw new SystemException(ApiResultConstant.resultCode_str_1001, ApiResultConstant.resultCode_1001);
			}
			// 得到该品牌下支持的碎屏险项目
			ScreenProject p = new ScreenProject();
			p.setBrandId(brandId);
			List<ScreenProject> list = screenProjectService.queryList(p);
			if (list == null || list.size() == 0) {
				throw new SystemException("该品牌下不存在碎屏险项目");
			} else {
				JSONObject jsonResult = new JSONObject();
				JSONArray array = new JSONArray();
				for (ScreenProject r : list) {
					//避免冲突  M19 和M20的不返回 由前端写死
					if(!r.getProductCode().equals("M19")&&!r.getProductCode().equals("M20")){
					JSONObject j = new JSONObject();
					j.put("projectId", r.getId());
					j.put("projectName", r.getName());
					j.put("price", r.getPrice());
					j.put("maxPrice", r.getMaxPrice());
					j.put("detail", r.getDetail());
					array.add(j);
					}
				}
				jsonResult.put("data", array);
				result.setResult(jsonResult);
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
}
