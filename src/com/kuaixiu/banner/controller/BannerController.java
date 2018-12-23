package com.kuaixiu.banner.controller;

import com.common.base.controller.BaseController;
import com.kuaixiu.banner.entity.Banner;
import com.kuaixiu.banner.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Banner Controller
 *
 * @CreateDate: 2016-08-26 上午12:43:18
 * @version: V 1.0
 */
@Controller
public class BannerController extends BaseController {

    @Autowired
    private BannerService bannerService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/banner/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="banner/list";
        return new ModelAndView(returnView);
    }
}
