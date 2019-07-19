package com.kuaixiu.balance.controller;

import com.common.base.controller.BaseController;
import com.kuaixiu.balance.service.BalanceShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * BalanceShop Controller
 *
 * @CreateDate: 2016-10-15 下午02:52:11
 * @version: V 1.0
 */
@Controller
public class BalanceShopController extends BaseController {

    @Autowired
    private BalanceShopService balanceShopService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/balanceShop/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="balanceShop/list";
        return new ModelAndView(returnView);
    }
}
