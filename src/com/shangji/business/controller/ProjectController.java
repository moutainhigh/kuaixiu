package com.shangji.business.controller;

import com.common.base.controller.BaseController;
import com.shangji.business.entity.Project;
import com.shangji.business.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Project Controller
 *
 * @CreateDate: 2019-05-06 上午10:39:49
 * @version: V 1.0
 */
@Controller
public class ProjectController extends BaseController {

    @Autowired
    private ProjectService projectService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/project/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="project/list";
        return new ModelAndView(returnView);
    }
}
