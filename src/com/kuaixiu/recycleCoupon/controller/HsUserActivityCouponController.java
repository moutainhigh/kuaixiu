package com.kuaixiu.recycleCoupon.controller;

import com.common.base.controller.BaseController;
import com.kuaixiu.recycleCoupon.entity.HsUserActivityCoupon;
import com.kuaixiu.recycleCoupon.service.HsUserActivityCouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * HsUserActivityCoupon Controller
 *
 * @CreateDate: 2019-05-30 上午11:28:06
 * @version: V 1.0
 */
@Controller
public class HsUserActivityCouponController extends BaseController {

    @Autowired
    private HsUserActivityCouponService hsUserActivityCouponService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/hsUserActivityCoupon/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="hsUserActivityCoupon/list";
        return new ModelAndView(returnView);
    }
}
