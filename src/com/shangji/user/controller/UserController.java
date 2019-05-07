package com.shangji.user.controller;

import com.common.base.controller.BaseController;
import com.shangji.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * User Controller
 *
 * @CreateDate: 2019-05-06 上午10:19:30
 * @version: V 1.0
 */
@Controller
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/user/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="user/list";
        return new ModelAndView(returnView);
    }
}
