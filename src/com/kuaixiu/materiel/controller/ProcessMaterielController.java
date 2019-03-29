package com.kuaixiu.materiel.controller;

import com.common.base.controller.BaseController;
import com.kuaixiu.materiel.entity.ProcessMateriel;
import com.kuaixiu.materiel.service.ProcessMaterielService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ProcessMateriel Controller
 *
 * @CreateDate: 2019-03-28 上午09:09:28
 * @version: V 1.0
 */
@Controller
public class ProcessMaterielController extends BaseController {

    @Autowired
    private ProcessMaterielService processMaterielService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/processMateriel/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="processMateriel/list";
        return new ModelAndView(returnView);
    }
}
