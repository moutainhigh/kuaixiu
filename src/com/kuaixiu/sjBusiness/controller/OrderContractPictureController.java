package com.kuaixiu.sjBusiness.controller;

import com.common.base.controller.BaseController;
import com.kuaixiu.sjBusiness.entity.OrderContractPicture;
import com.kuaixiu.sjBusiness.service.OrderContractPictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * OrderContractPicture Controller
 *
 * @CreateDate: 2019-05-08 上午10:52:42
 * @version: V 1.0
 */
@Controller
public class OrderContractPictureController extends BaseController {

    @Autowired
    private OrderContractPictureService orderContractPictureService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/orderContractPicture/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="orderContractPicture/list";
        return new ModelAndView(returnView);
    }
}
