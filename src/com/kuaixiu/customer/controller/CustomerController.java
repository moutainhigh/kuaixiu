package com.kuaixiu.customer.controller;

import com.common.base.controller.BaseController;
import com.kuaixiu.customer.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Customer Controller
 *
 * @CreateDate: 2016-08-26 上午12:44:56
 * @version: V 1.0
 */
@Controller
public class CustomerController extends BaseController {

    @Autowired
    private CustomerService customerService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/customer/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="customer/list";
        return new ModelAndView(returnView);
    }
}
