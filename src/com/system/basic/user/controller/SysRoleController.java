package com.system.basic.user.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.common.base.controller.BaseController;
import com.system.basic.user.service.SysRoleService;

/**
 * SysRole Controller
 *
 * @CreateDate: 2016-08-26 下午10:26:44
 * @version: V 1.0
 */
@Controller
public class SysRoleController extends BaseController {

    @Autowired
    private SysRoleService sysRoleService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sysRole/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="sysRole/list";
        return new ModelAndView(returnView);
    }
}
