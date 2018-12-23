package com.kuaixiu.model.controller;

import com.common.base.controller.BaseController;
import com.kuaixiu.model.entity.RepairCost;
import com.kuaixiu.model.service.RepairCostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * RepairCost Controller
 *
 * @CreateDate: 2016-08-26 下午10:40:42
 * @version: V 1.0
 */
@Controller
public class RepairCostController extends BaseController {

    @Autowired
    private RepairCostService repairCostService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/repairCost/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="repairCost/list";
        return new ModelAndView(returnView);
    }
}
