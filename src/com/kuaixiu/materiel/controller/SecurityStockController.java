package com.kuaixiu.materiel.controller;

import com.common.base.controller.BaseController;
import com.kuaixiu.materiel.entity.SecurityStock;
import com.kuaixiu.materiel.service.SecurityStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * SecurityStock Controller
 *
 * @CreateDate: 2019-03-27 下午04:21:15
 * @version: V 1.0
 */
@Controller
public class SecurityStockController extends BaseController {

    @Autowired
    private SecurityStockService securityStockService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/securityStock/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="securityStock/list";
        return new ModelAndView(returnView);
    }
}
