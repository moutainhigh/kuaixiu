package com.kuaixiu.order.controller;

import com.common.base.controller.BaseController;
import com.kuaixiu.order.entity.OrderPayLog;
import com.kuaixiu.order.service.OrderPayLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * OrderPayLog Controller
 *
 * @CreateDate: 2016-09-16 下午10:39:34
 * @version: V 1.0
 */
@Controller
public class OrderPayLogController extends BaseController {

    @Autowired
    private OrderPayLogService orderPayLogService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/orderPayLog/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="orderPayLog/list";
        return new ModelAndView(returnView);
    }
}
