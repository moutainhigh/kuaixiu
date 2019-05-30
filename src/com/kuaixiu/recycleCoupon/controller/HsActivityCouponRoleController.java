package com.kuaixiu.recycleCoupon.controller;

import com.common.base.controller.BaseController;
import com.kuaixiu.recycleCoupon.entity.HsActivityCouponRole;
import com.kuaixiu.recycleCoupon.service.HsActivityCouponRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * HsActivityCouponRole Controller
 *
 * @CreateDate: 2019-05-30 上午11:27:08
 * @version: V 1.0
 */
@Controller
public class HsActivityCouponRoleController extends BaseController {

    @Autowired
    private HsActivityCouponRoleService hsActivityCouponRoleService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/hsActivityCouponRole/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="hsActivityCouponRole/list";
        return new ModelAndView(returnView);
    }
}
