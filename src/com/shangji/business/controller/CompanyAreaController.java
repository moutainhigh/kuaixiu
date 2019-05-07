package com.shangji.business.controller;

import com.common.base.controller.BaseController;
import com.shangji.business.entity.CompanyArea;
import com.shangji.business.service.CompanyAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * CompanyArea Controller
 *
 * @CreateDate: 2019-05-06 上午10:53:07
 * @version: V 1.0
 */
@Controller
public class CompanyAreaController extends BaseController {

    @Autowired
    private CompanyAreaService companyAreaService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/companyArea/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="companyArea/list";
        return new ModelAndView(returnView);
    }
}
