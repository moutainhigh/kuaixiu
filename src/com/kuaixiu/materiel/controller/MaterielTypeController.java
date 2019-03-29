package com.kuaixiu.materiel.controller;

import com.common.base.controller.BaseController;
import com.kuaixiu.materiel.entity.MaterielType;
import com.kuaixiu.materiel.service.MaterielTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * MaterielType Controller
 *
 * @CreateDate: 2019-03-27 下午04:15:21
 * @version: V 1.0
 */
@Controller
public class MaterielTypeController extends BaseController {

    @Autowired
    private MaterielTypeService materielTypeService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/materielType/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="materielType/list";
        return new ModelAndView(returnView);
    }
}
