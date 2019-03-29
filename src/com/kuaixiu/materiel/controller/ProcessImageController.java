package com.kuaixiu.materiel.controller;

import com.common.base.controller.BaseController;
import com.kuaixiu.materiel.entity.ProcessImage;
import com.kuaixiu.materiel.service.ProcessImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ProcessImage Controller
 *
 * @CreateDate: 2019-03-28 上午09:08:41
 * @version: V 1.0
 */
@Controller
public class ProcessImageController extends BaseController {

    @Autowired
    private ProcessImageService processImageService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/processImage/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="processImage/list";
        return new ModelAndView(returnView);
    }
}
