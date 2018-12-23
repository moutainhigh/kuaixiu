package com.system.api.controller;

import com.common.base.controller.BaseController;
import com.system.api.service.ApiConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ApiConfig Controller
 *
 * @CreateDate: 2016-09-05 下午08:40:20
 * @version: V 1.0
 */
@Controller
public class ApiConfigController extends BaseController {

    @Autowired
    private ApiConfigService apiConfigService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/apiConfig/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="apiConfig/list";
        return new ModelAndView(returnView);
    }
}
