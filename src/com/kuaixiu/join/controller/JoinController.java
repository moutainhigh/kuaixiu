package com.kuaixiu.join.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.exception.SystemException;
import com.common.paginate.Page;
import com.common.wechat.common.util.StringUtils;
import com.google.common.collect.Maps;
import com.kuaixiu.join.entity.JoinNews;
import com.kuaixiu.join.service.JoinNewsService;
import com.system.api.entity.ResultData;
import com.system.basic.address.entity.Address;
import com.system.basic.address.service.AddressService;
import com.system.basic.user.entity.SessionUser;
import com.system.basic.user.service.SessionUserService;
import com.system.constant.ApiResultConstant;
import com.system.constant.SystemConstant;

/**
 * @author: anson
 * @CreateDate: 2018年3月1日 下午2:55:48
 * @version: V 1.0 加盟连锁商和门店信息
 */
@Controller
public class JoinController extends BaseController {

	@Autowired
	private SessionUserService sessionUserService;
	@Autowired
	private JoinNewsService joinNewsService;
	@Autowired
	private AddressService addressService;

	/**
	 * 添加加盟连锁商和门店信息
	 */
	@RequestMapping("/join/add")
	public void sendSmsCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 返回对象
		ResultData result = new ResultData();
		try {
			JSONObject params = getPrarms(request);
			String name = params.getString("name");
			String mobile = params.getString("mobile");
			String province = params.getString("province");
			String city = params.getString("city");
			String area = params.getString("area");
			String street = params.getString("street");
			if (StringUtils.isBlank(name) || StringUtils.isBlank(mobile) || StringUtils.isBlank(province)
					|| StringUtils.isBlank(city) || StringUtils.isBlank(area) || StringUtils.isBlank(street)) {
				throw new SystemException("请填写完整信息");
			}
			// 转化地址
			Address provinceName = addressService.queryByAreaId(province);
			Address cityName = addressService.queryByAreaId(city);
			Address areaName = addressService.queryByAreaId(area);
			if (provinceName == null || cityName == null || areaName == null) {
				throw new SystemException("请确认地址信息是否无误");
			}
			String address = provinceName.getArea() + " "+cityName.getArea() + " " + areaName.getArea() + " " + street;
			JoinNews j = new JoinNews();
			j.setName(name);
			j.setMobile(mobile);
			j.setProvince(province);
			j.setCity(city);
			j.setArea(area);
			j.setStreet(street);
			j.setAddress(address);
			j.setIsDel(0);
			j.setIsSuccess(0);
			j.setType(1); // 1连锁商 2门店
			joinNewsService.add(j);
			result.setSuccess(true);
			result.setResultCode(ApiResultConstant.resultCode_0);
		} catch (SystemException e) {
			sessionUserService.getSystemException(e, result);
		} catch (Exception e) {
			e.printStackTrace();
			sessionUserService.getException(result);
		}
		renderJson(response, result);
	}

	/**
	 * 加盟信息列表
	 */
	@RequestMapping(value = "/join/list")
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {

		List<Address> provinceL = addressService.queryByPid("0");
		request.setAttribute("provinceL", provinceL);
		String returnView = "join/list";
		return new ModelAndView(returnView);
	}

	/**
	 * 刷新数据
	 */
	@RequestMapping(value = "join/queryListForPage")
	public void queryListForPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		SessionUser su = getCurrentUser(request);
		if (su.getType() != SystemConstant.USER_TYPE_SYSTEM) {
			throw new SystemException("对不起，您没有操作权限!");
		}
		// 获取查询条件
		String name = request.getParameter("query_name");
		String mobile = request.getParameter("query_customerMobile");
		String queryStartTime = request.getParameter("query_startTime");
		String queryEndTime = request.getParameter("query_endTime");
		String province = request.getParameter("queryProvince");
		String city = request.getParameter("queryCity");
		String county = request.getParameter("queryCounty");
		JoinNews news = new JoinNews();
		news.setName(name);
		news.setMobile(mobile);
		news.setQueryStartTime(queryStartTime);
		news.setQueryEndTime(queryEndTime);
		if (StringUtils.isNotBlank(province)) {
			news.setProvince(province);
		}
		if (StringUtils.isNotBlank(city)) {
			news.setCity(city);
		}
		if (StringUtils.isNotBlank(county)) {
			news.setArea(county);
		}
		Page page = getPageByRequest(request);
		news.setPage(page);
		List<JoinNews> list = joinNewsService.queryListForPage(news);
		page.setData(list);
		this.renderJson(response, page);
	}

	@RequestMapping(value = "/join/delete")
	public void delete(HttpServletRequest request, HttpServletResponse response) throws Exception {

		Map<String, Object> resultMap = Maps.newHashMap();
		// 获取品牌id
		String id = request.getParameter("id");
		SessionUser su = getCurrentUser(request);
		if (su.getType() != SystemConstant.USER_TYPE_SYSTEM) {
			throw new SystemException("对不起，您没有操作权限!");
		}
		joinNewsService.deleteById(id, su);
		resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
		resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
		renderJson(response, resultMap);
	}

}
