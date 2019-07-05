package com.kuaixiu.recycleCoupon.controller;

import com.common.base.controller.BaseController;
import com.kuaixiu.recycleCoupon.service.HsActivityAndCouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * HsActivityAndCoupon Controller
 *
 * @CreateDate: 2019-06-10 下午05:04:01
 * @version: V 1.0
 */
@Controller
public class HsActivityAndCouponController extends BaseController {

    @Autowired
    private HsActivityAndCouponService hsActivityAndCouponService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/hsActivityAndCoupon/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="hsActivityAndCoupon/list";
        return new ModelAndView(returnView);
    }
}
