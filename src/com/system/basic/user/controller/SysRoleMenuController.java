package com.system.basic.user.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.common.base.controller.BaseController;
import com.system.basic.user.service.SysRoleMenuService;

/**
 * SysRoleMenu Controller
 *
 * @CreateDate: 2016-08-26 下午10:28:52
 * @version: V 1.0
 */
@Controller
public class SysRoleMenuController extends BaseController {

    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sysRoleMenu/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="sysRoleMenu/list";
        return new ModelAndView(returnView);
    }
}
