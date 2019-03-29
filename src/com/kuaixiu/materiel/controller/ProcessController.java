package com.kuaixiu.materiel.controller;

import com.common.base.controller.BaseController;
import com.kuaixiu.materiel.entity.Process;
import com.kuaixiu.materiel.service.ProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Process Controller
 *
 * @CreateDate: 2019-03-28 上午09:07:03
 * @version: V 1.0
 */
@Controller
public class ProcessController extends BaseController {

    @Autowired
    private ProcessService processService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/process/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="process/list";
        return new ModelAndView(returnView);
    }
}
