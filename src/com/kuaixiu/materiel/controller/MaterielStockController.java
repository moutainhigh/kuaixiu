package com.kuaixiu.materiel.controller;

import com.common.base.controller.BaseController;
import com.kuaixiu.materiel.entity.MaterielStock;
import com.kuaixiu.materiel.service.MaterielStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * MaterielStock Controller
 *
 * @CreateDate: 2019-03-27 下午04:14:22
 * @version: V 1.0
 */
@Controller
public class MaterielStockController extends BaseController {

    @Autowired
    private MaterielStockService materielStockService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/materielStock/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="materielStock/list";
        return new ModelAndView(returnView);
    }
}
