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
import com.kuaixiu.brand.entity.Brand;
import com.kuaixiu.screen.entity.ScreenBrand;
import com.kuaixiu.screen.service.ScreenBrandService;
import com.system.api.entity.ResultData;
import com.system.basic.user.entity.SessionUser;
import com.system.basic.user.service.SessionUserService;
import com.system.constant.SystemConstant;

/**
 * @author: anson
 * @CreateDate: 2017年10月17日 下午7:54:07
 * @version: V 1.0
 * 
 */
@Controller
public class ScreenBrandController extends BaseController {

	@Autowired
	private ScreenBrandService screenBrandService;
	@Autowired
	private SessionUserService sessionUserService;

	/**
	 * 列表查询
	 */
	@RequestMapping(value = "/screen/brandList")
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String returnView = "screen/brandList";
		return new ModelAndView(returnView);
	}

	/**
	 * queryListForPage
	 */
	@RequestMapping(value = "/screen/brand/queryListForPage")
	public void queryListForPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		SessionUser su = getCurrentUser(request);
		if (su.getType() != SystemConstant.USER_TYPE_SYSTEM) {
			throw new SystemException("对不起，您没有操作权限!");
		}
		// 获取查询条件
		String name = request.getParameter("query_name");
		ScreenBrand b = new ScreenBrand();
		b.setName(name);

		Page page = getPageByRequest(request);
		b.setPage(page);
		List<ScreenBrand> list = screenBrandService.queryListForPage(b);
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
	@RequestMapping(value = "/screen/brand/add")
	public ModelAndView add(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String returnView = "screen/addBrand";
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
	@RequestMapping(value = "screen/brand/save")
	public void save(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> resultMap = Maps.newHashMap();
		SessionUser su = getCurrentUser(request);
		if (su.getType() != SystemConstant.USER_TYPE_SYSTEM) {
			resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
			resultMap.put(RESULTMAP_KEY_MSG, "对不起您没有操作权限!");
		}
		// 获取品牌名称
		String name = request.getParameter("addName");
		// 最高保额
		String maxPrice = request.getParameter("addMaxPrice");
		// 排序
		ScreenBrand b = new ScreenBrand();
		String sort = request.getParameter("addSort");
		if (!StringUtils.isBlank(sort)) {
			b.setSort(Integer.parseInt(sort));
		}
		b.setName(name);
		b.setMaxPrice(new BigDecimal(maxPrice));
		b.setCreateUserId(su.getUserId());
		b.setIsDel(0);
		
		
		// 品牌图标默认保存为安卓图标
		String logoUrl = "/resource/brandLogo/android.png";
		b.setLogo(logoUrl);
		screenBrandService.save(b);
		resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
		resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
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
	@RequestMapping(value = "screen/brand/edit")
	public ModelAndView edit(HttpServletRequest request, HttpServletResponse response) throws Exception {
		SessionUser su = getCurrentUser(request);
		if (su.getType() != SystemConstant.USER_TYPE_SYSTEM) {
			throw new SystemException("对不起，您没有操作权限!");
		}
		// 获取品牌id
		String id = request.getParameter("id");
		// 查询品牌内容
		ScreenBrand b = screenBrandService.queryById(id);

		request.setAttribute("brand", b);
		String returnView = "screen/editBrand";
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
	@RequestMapping(value = "screen/brand/update")
	public void update(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> resultMap = Maps.newHashMap();
		SessionUser su = getCurrentUser(request);
		if (su.getType() != SystemConstant.USER_TYPE_SYSTEM) {
			resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
			resultMap.put(RESULTMAP_KEY_MSG, "对不起您没有操作权限!");
		}

		// 获取品牌id
		String id = request.getParameter("id");
		// 获取品牌名称
		String name = request.getParameter("upName");
		// 排序
		String sort = request.getParameter("upSort");
		String maxPrice = request.getParameter("maxPrice");
		if (StringUtils.isBlank(sort)) {
			sort = "99";
		}

		ScreenBrand b = screenBrandService.queryById(id);
		if (b == null) {
			resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
			resultMap.put(RESULTMAP_KEY_MSG, "品牌不存在");
		} else {
			b.setName(name);
			b.setSort(Integer.parseInt(sort));
			b.setMaxPrice(new BigDecimal(maxPrice));
			screenBrandService.saveUpdate(b);
			resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
			resultMap.put(RESULTMAP_KEY_MSG, "修改成功");
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
	@RequestMapping(value = "screen/brand/delete")
	public void delete(HttpServletRequest request, HttpServletResponse response) throws Exception {

		Map<String, Object> resultMap = Maps.newHashMap();
		// 获取品牌id
		String id = request.getParameter("id");
		SessionUser su = getCurrentUser(request);
		if (su.getType() != SystemConstant.USER_TYPE_SYSTEM) {
			resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
			resultMap.put(RESULTMAP_KEY_MSG, "对不起您没有操作权限!");
		} else {
			ScreenBrand s = screenBrandService.queryById(id);
			if (s != null) {
				s.setIsDel(1);
				screenBrandService.saveUpdate(s);
				resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
				resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
			} else {
				resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
				resultMap.put(RESULTMAP_KEY_MSG, "该品牌不存在!");
			}
		}
		renderJson(response, resultMap);
	}

	/**
	 * 获得碎屏险手机品牌
	 * 
	 * @throws IOException
	 */
	@RequestMapping("/screen/order/brandList")
	@ResponseBody
	public void getBrandList(HttpServletResponse response, HttpServletRequest request) throws IOException {
		ResultData result = new ResultData();
		try {
			// 查询所有品牌
			List<ScreenBrand> list = screenBrandService.queryList(null);
			if (list != null && list.size() != 0) {
				JSONObject jsonResult = new JSONObject();
				JSONArray array = new JSONArray();
				for (ScreenBrand b : list) {
					JSONObject j = new JSONObject();
					j.put("brandId", b.getId());
					j.put("brandName", b.getName());
					j.put("maxPrice", b.getMaxPrice());
					j.put("logo", b.getLogo());
					array.add(j);
				}
				jsonResult.put("data", array);
				result.setResult(jsonResult);
				sessionUserService.getSuccessResult(result, jsonResult);
			}
		} catch (Exception e) {
			e.printStackTrace();
			sessionUserService.getException(result);
		}
		renderJson(response, result);
	}

}
