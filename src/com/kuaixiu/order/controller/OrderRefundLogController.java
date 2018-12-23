package com.kuaixiu.order.controller;

import com.common.base.controller.BaseController;
import com.kuaixiu.order.entity.OrderRefundLog;
import com.kuaixiu.order.service.OrderRefundLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * OrderRefundLog Controller
 *
 * @CreateDate: 2016-10-23 下午07:30:27
 * @version: V 1.0
 */
@Controller
public class OrderRefundLogController extends BaseController {

    @Autowired
    private OrderRefundLogService orderRefundLogService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/orderRefundLog/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="orderRefundLog/list";
        return new ModelAndView(returnView);
    }
}
