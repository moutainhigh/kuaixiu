package com.kuaixiu.activity.controller;

import com.common.base.controller.BaseController;
import com.kuaixiu.activity.entity.ActivityCompany;
import com.kuaixiu.activity.service.ActivityCompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ActivityCompany Controller
 *
 * @CreateDate: 2018-12-24 下午05:11:47
 * @version: V 1.0
 */
@Controller
public class ActivityCompanyController extends BaseController {

    @Autowired
    private ActivityCompanyService activityCompanyService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/activityCompany/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="activityCompany/list";
        return new ModelAndView(returnView);
    }
}
