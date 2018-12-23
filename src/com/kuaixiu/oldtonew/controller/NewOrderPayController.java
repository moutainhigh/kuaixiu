package com.kuaixiu.oldtonew.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.common.base.controller.BaseController;
import com.kuaixiu.oldtonew.service.NewOrderPayService;

/**
* @author: anson
* @CreateDate: 2017年6月19日 下午5:24:50
* @version: V 1.0
* 
*/
@Controller
public class NewOrderPayController extends BaseController{
     
	 @Autowired
	 private NewOrderPayService orderPayService;
	
	 
}
