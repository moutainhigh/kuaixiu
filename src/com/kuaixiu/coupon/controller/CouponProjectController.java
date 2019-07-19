package com.kuaixiu.coupon.controller;

import com.common.base.controller.BaseController;
import com.kuaixiu.coupon.service.CouponProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * CouponProject Controller
 *
 * @CreateDate: 2017-02-21 下午11:53:44
 * @version: V 1.0
 */
@Controller
public class CouponProjectController extends BaseController {

    @Autowired
    private CouponProjectService couponProjectService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/couponProject/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="couponProject/list";
        return new ModelAndView(returnView);
    }
}
