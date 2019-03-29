package com.kuaixiu.materiel.controller;

import com.common.base.controller.BaseController;
import com.kuaixiu.materiel.entity.EngineerStock;
import com.kuaixiu.materiel.service.EngineerStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * EngineerStock Controller
 *
 * @CreateDate: 2019-03-27 下午04:31:52
 * @version: V 1.0
 */
@Controller
public class EngineerStockController extends BaseController {

    @Autowired
    private EngineerStockService engineerStockService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/engineerStock/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="engineerStock/list";
        return new ModelAndView(returnView);
    }
}
