package com.shangji.user.controller;

import com.common.base.controller.BaseController;
import com.shangji.user.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * UserRole Controller
 *
 * @CreateDate: 2019-05-06 上午10:37:53
 * @version: V 1.0
 */
@Controller
public class UserRoleController extends BaseController {

    @Autowired
    private UserRoleService userRoleService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/userRole/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="userRole/list";
        return new ModelAndView(returnView);
    }
}
