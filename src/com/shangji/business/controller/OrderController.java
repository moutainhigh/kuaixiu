package com.shangji.business.controller;

import com.common.base.controller.BaseController;
import com.shangji.business.entity.Order;
import com.shangji.business.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Order Controller
 *
 * @CreateDate: 2019-05-06 上午10:44:49
 * @version: V 1.0
 */
@Controller
public class OrderController extends BaseController {

    @Autowired
    private OrderService orderService;

}
