package com.kuaixiu.sjBusiness.controller;

import com.common.base.controller.BaseController;
import com.kuaixiu.sjBusiness.entity.SjReworkOrderPicture;
import com.kuaixiu.sjBusiness.service.SjReworkOrderPictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * SjReworkOrderPicture Controller
 *
 * @CreateDate: 2019-07-02 上午10:33:28
 * @version: V 1.0
 */
@Controller
public class SjReworkOrderPictureController extends BaseController {

    @Autowired
    private SjReworkOrderPictureService sjReworkOrderPictureService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sjReworkOrderPicture/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="sjReworkOrderPicture/list";
        return new ModelAndView(returnView);
    }
}
