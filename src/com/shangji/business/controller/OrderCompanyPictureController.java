package com.shangji.business.controller;

import com.common.base.controller.BaseController;
import com.shangji.business.entity.OrderCompanyPicture;
import com.shangji.business.service.OrderCompanyPictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * OrderCompanyPicture Controller
 *
 * @CreateDate: 2019-05-06 上午10:43:53
 * @version: V 1.0
 */
@Controller
public class OrderCompanyPictureController extends BaseController {

    @Autowired
    private OrderCompanyPictureService orderCompanyPictureService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/orderCompanyPicture/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="orderCompanyPicture/list";
        return new ModelAndView(returnView);
    }
}
