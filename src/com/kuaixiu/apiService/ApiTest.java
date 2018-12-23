package com.kuaixiu.apiService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * API 接口测试类
 * @author wugl
 * 
 */
@Controller
public class ApiTest {
	/**
	 * 根据用户名获取用户对象
	 * @param name
	 * @return
	 */
	@RequestMapping(value="/api/getUserByName", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "根据用户名获取用户对象", httpMethod = "GET", response = ApiResult.class, notes = "根据用户名获取用户对象")
	public ApiResult getUserByName(@ApiParam(required = true, name = "name", value = "用户名") @RequestParam String name) throws Exception{
	    
		return new ApiResult(name,"OK");
	}
}
