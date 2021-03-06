package com.kuaixiu.sjLogin.controller;

import com.common.base.controller.BaseController;
import com.kuaixiu.sjUser.entity.SjSessionUser;
import com.system.constant.SystemConstant;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;


/**
 * 首页控制类.
 *
 * @author: lijx
 * @CreateDate: 2016-8-19 下午11:49:41
 * @version: V 1.0
 */
@Controller
public class SjIndexController extends BaseController {
    private final Logger log = Logger.getLogger(SjIndexController.class);

    /**
     * 首页面
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sj/admin/index")
    public ModelAndView index(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {

        //获取登录用户
        String returnView = "sjAdmin/index";
        SjSessionUser su = (SjSessionUser) request.getSession().getAttribute(SystemConstant.SESSION_SJ_USER_KEY);
        if (su == null) {
            returnView = "sjAdmin/loginCheck";
        }
        else {
            int orderDayCount = 0;
            int orderWeekCount = 0;
            int orderMonthCount = 0;
            int orderAllCount = 0;
            int engCount = 0;
            int shopCount = 0;
            int customerCount = 0;
            BigDecimal orderDayAmount = new BigDecimal("0");
            BigDecimal orderWeekAmount = new BigDecimal("0");
            BigDecimal orderMonthAmount = new BigDecimal("0");
            BigDecimal allAmount = new BigDecimal("0");
            //判断用户类型系统管理员可以查看所有工程师
            request.setAttribute("orderDayCount", orderDayCount);
            request.setAttribute("orderWeekCount", orderWeekCount);
            request.setAttribute("orderMonthCount", orderMonthCount);
            request.setAttribute("orderAllCount", orderAllCount);
            request.setAttribute("orderDayAmount", orderDayAmount);
            request.setAttribute("orderWeekAmount", orderWeekAmount);
            request.setAttribute("orderMonthAmount", orderMonthAmount);
            request.setAttribute("allAmount", allAmount);
            request.setAttribute("engCount", engCount);
            request.setAttribute("shopCount", shopCount);
            request.setAttribute("customerCount", customerCount);
        }
        return new ModelAndView(returnView);
    }

}
