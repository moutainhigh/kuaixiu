package com.kuaixiu.videoUserRel.controller;

import com.common.base.controller.BaseController;
import com.kuaixiu.videoUserRel.entity.VideoUserRel;
import com.kuaixiu.videoUserRel.service.VideoUserRelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * VideoUserRel Controller
 *
 * @CreateDate: 2019-08-15 下午03:39:20
 * @version: V 1.0
 */
@Controller
public class VideoUserRelController extends BaseController {

    @Autowired
    private VideoUserRelService videoUserRelService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/videoUserRel/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="videoUserRel/list";
        return new ModelAndView(returnView);
    }



}
