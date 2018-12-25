package com.kuaixiu.activity.controller;

import com.common.base.controller.BaseController;
import com.kuaixiu.activity.entity.ActivityUser;
import com.kuaixiu.activity.service.ActivityUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ActivityUser Controller
 *
 * @CreateDate: 2018-12-25 上午10:12:03
 * @version: V 1.0
 */
@Controller
public class ActivityUserController extends BaseController {

    @Autowired
    private ActivityUserService activityUserService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/activityUser/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="activityUser/list";
        return new ModelAndView(returnView);
    }
}
