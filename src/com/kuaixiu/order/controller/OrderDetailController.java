package com.kuaixiu.order.controller;

import com.common.base.controller.BaseController;
import com.kuaixiu.order.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * OrderDetail Controller
 *
 * @CreateDate: 2016-08-26 下午10:44:39
 * @version: V 1.0
 */
@Controller
public class OrderDetailController extends BaseController {

    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/orderDetail/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="orderDetail/list";
        return new ModelAndView(returnView);
    }
}
