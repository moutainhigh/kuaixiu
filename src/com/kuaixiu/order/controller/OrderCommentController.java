package com.kuaixiu.order.controller;

import com.common.base.controller.BaseController;
import com.kuaixiu.order.entity.OrderComment;
import com.kuaixiu.order.service.OrderCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * OrderComment Controller
 *
 * @CreateDate: 2016-08-26 下午10:45:08
 * @version: V 1.0
 */
@Controller
public class OrderCommentController extends BaseController {

    @Autowired
    private OrderCommentService orderCommentService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/orderComment/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="orderComment/list";
        return new ModelAndView(returnView);
    }
}
