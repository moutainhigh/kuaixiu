package com.system.basic.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.common.base.controller.BaseController;
import com.system.basic.user.service.LoginUserService;

/**
* @author: anson
* @CreateDate: 2017年9月5日 上午9:18:24
* @version: V 1.0
* 
*/
@Controller
public class LoginUserController extends BaseController{

	@Autowired
	private LoginUserService loginUserService;


}
