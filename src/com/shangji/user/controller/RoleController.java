package com.shangji.user.controller;

import com.common.base.controller.BaseController;
import com.shangji.user.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Role Controller
 *
 * @CreateDate: 2019-05-06 上午10:28:21
 * @version: V 1.0
 */
@Controller
public class RoleController extends BaseController {

    @Autowired
    private RoleService roleService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/role/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="role/list";
        return new ModelAndView(returnView);
    }
}
