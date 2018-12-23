package com.kuaixiu.coupon.controller;

import com.common.base.controller.BaseController;
import com.kuaixiu.coupon.entity.CouponModel;
import com.kuaixiu.coupon.service.CouponModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * CouponModel Controller
 *
 * @CreateDate: 2017-02-19 下午11:44:13
 * @version: V 1.0
 */
@Controller
public class CouponModelController extends BaseController {

    @Autowired
    private CouponModelService couponModelService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/couponModel/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="couponModel/list";
        return new ModelAndView(returnView);
    }
}
