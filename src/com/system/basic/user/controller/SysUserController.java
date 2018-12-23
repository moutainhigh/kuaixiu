package com.system.basic.user.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.common.base.controller.BaseController;
import com.common.exception.SystemException;
import com.common.util.MD5Util;
import com.system.basic.user.entity.SessionUser;
import com.system.basic.user.service.SysUserService;
import com.system.constant.SystemConstant;

/**
 * SysUser Controller
 *
 * @CreateDate: 2016-08-26 下午10:27:15
 * @version: V 1.0
 */
@Controller
public class SysUserController extends BaseController {

    @Autowired
    private SysUserService sysUserService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sysUser/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="sysUser/list";
        return new ModelAndView(returnView);
    }
    /**
     * 跳转到修改用户密码视图
     */
    @RequestMapping(value = "/sysUser/changePwd")
    public ModelAndView changePwd(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="admin/changePwd";
        return new ModelAndView(returnView);
    }
    /**
     * 更新用户密码
     */
    @RequestMapping(value = "/sysUser/updateUserPasswd")
    public void updateUserPasswd(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
    	SessionUser user = this.getCurrentUser(request);
    	String currentPwd = request.getParameter("oldPwd");
    	String newPwd = request.getParameter("newPwd");
    	
    	Map<String, Object> result=new HashMap<String, Object>();
    	boolean bool = true;

    	try{
    		sysUserService.updateUserPasswd(user.getUserId(), MD5Util.encodePassword(currentPwd), MD5Util.encodePassword(newPwd));
    	}catch(SystemException se){
    		result.put("msg", se.getMessage());
    		bool = false;
    	}
    	result.put("success", bool);
    	//更新缓存
    	if(bool){
    		user.getUser().setPassword(MD5Util.encodePassword(newPwd));
    		request.getSession().setAttribute(SystemConstant.SESSION_USER_KEY, user);
    	}
    	renderJson(response, result);
    }
    
    /**
     * 检验当前用户的密码
     * OK：{ "valid": true }
     * NO：{ "valid": false }
     */
    @RequestMapping(value = "/sysUser/checkUserPasswd")
    public void checkUserPasswd(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
    	SessionUser user = this.getCurrentUser(request);
    	String currentPwd = request.getParameter("oldPwd");
    	Map<String,Boolean> result=new HashMap<String,Boolean>();
    	
    	result.put("valid", user.getUser().getPassword().equals(MD5Util.encodePassword(currentPwd)));
    	renderJson(response, result);
    }
}
