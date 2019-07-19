package com.kuaixiu.version.controller;

import com.common.base.controller.BaseController;
import com.kuaixiu.version.service.VersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Version Controller
 *
 * @CreateDate: 2017-05-02 下午09:55:22
 * @version: V 1.0
 */
@Controller
public class VersionController extends BaseController {

    @Autowired
    private VersionService versionService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/version/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="version/list";
        return new ModelAndView(returnView);
    }
}
