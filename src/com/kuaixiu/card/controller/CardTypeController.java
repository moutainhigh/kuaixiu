package com.kuaixiu.card.controller;

import com.common.base.controller.BaseController;
import com.kuaixiu.card.entity.CardType;
import com.kuaixiu.card.service.CardTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * CardType Controller
 *
 * @CreateDate: 2018-12-27 下午03:29:34
 * @version: V 1.0
 */
@Controller
public class CardTypeController extends BaseController {

    @Autowired
    private CardTypeService cardTypeService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/cardType/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="cardType/list";
        return new ModelAndView(returnView);
    }
}
