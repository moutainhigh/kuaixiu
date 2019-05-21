package com.kuaixiu.sjBusiness.controller;

import com.common.base.controller.BaseController;
import com.kuaixiu.sjBusiness.entity.SjRegisterForm;
import com.kuaixiu.sjBusiness.service.SjRegisterFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * SjRegisterForm Controller
 *
 * @CreateDate: 2019-05-21 下午12:04:52
 * @version: V 1.0
 */
@Controller
public class SjRegisterFormController extends BaseController {

    @Autowired
    private SjRegisterFormService sjRegisterFormService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sjRegisterForm/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="sjRegisterForm/list";
        return new ModelAndView(returnView);
    }
}
