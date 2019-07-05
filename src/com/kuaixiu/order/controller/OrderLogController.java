package com.kuaixiu.order.controller;

import com.common.base.controller.BaseController;
import com.kuaixiu.order.service.OrderLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * OrderLog Controller
 *
 * @CreateDate: 2016-08-26 下午10:45:29
 * @version: V 1.0
 */
@Controller
public class OrderLogController extends BaseController {

    @Autowired
    private OrderLogService orderLogService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/orderLog/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="orderLog/list";
        return new ModelAndView(returnView);
    }
}
