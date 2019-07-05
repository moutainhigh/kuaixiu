package com.kuaixiu.shop.controller;

import com.common.base.controller.BaseController;
import com.kuaixiu.shop.service.ShopModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ShopModel Controller
 *
 * @CreateDate: 2017-02-12 上午12:32:01
 * @version: V 1.0
 */
@Controller
public class ShopModelController extends BaseController {

    @Autowired
    private ShopModelService shopModelService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/shopModel/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="shopModel/list";
        return new ModelAndView(returnView);
    }
}
