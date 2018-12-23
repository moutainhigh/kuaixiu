package com.kuaixiu.admin.controller;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.common.base.controller.BaseController;
import com.kuaixiu.customer.service.CustomerService;
import com.kuaixiu.engineer.service.EngineerService;
import com.kuaixiu.order.entity.Order;
import com.kuaixiu.order.service.OrderService;
import com.kuaixiu.provider.entity.Provider;
import com.kuaixiu.provider.service.ProviderService;
import com.kuaixiu.shop.entity.Shop;
import com.kuaixiu.shop.service.ShopService;
import com.system.basic.user.entity.SessionUser;
import com.system.constant.SystemConstant;


/**
 * 首页控制类.
 *
 * @author: lijx
 * @CreateDate: 2016-8-19 下午11:49:41
 * @version: V 1.0
 */
@Controller
public class IndexController extends BaseController {
    private final Logger logger = Logger.getLogger(IndexController.class);

    @Autowired
    private ProviderService providerService;
    @Autowired
    private ShopService shopService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private EngineerService engineerService;
    @Autowired
    private CustomerService customerService;

    /**
     * 首页面
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/admin/index")
    public ModelAndView index(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {

        //获取登录用户
        String returnView = "admin/index";
        SessionUser su = getCurrentUser(request);
        if (su == null) {
            returnView = "admin/loginCheck";
        }
        else {
            int orderDayCount = 0;
            int orderWeekCount = 0;
            int orderMonthCount = 0;
            int orderAllCount = 0;
            int engCount = 0;
            int shopCount = 0;
            int customerCount = 0;
            BigDecimal orderDayAmount = null;
            BigDecimal orderWeekAmount = null;
            BigDecimal orderMonthAmount = null;
            BigDecimal allAmount = null;
            Order o = new Order();
            //判断用户类型系统管理员可以查看所有工程师
            if (su.getType() == SystemConstant.USER_TYPE_SYSTEM) {
                orderAllCount = orderService.queryAllCount(o);
                allAmount = orderService.queryAllAmount(o);
                orderDayCount = orderService.queryDayCount(o);
                orderDayAmount = orderService.queryDayAmount(o);
                engCount = engineerService.queryAllCount();
                shopCount = shopService.queryAllCount();
                customerCount = customerService.queryAllCount();
            }
            else if (su.getType() == SystemConstant.USER_TYPE_PROVIDER) {
                Provider provider = providerService.queryByCode(su.getProviderCode());
                request.setAttribute("provider", provider);
                o.setProviderCode(provider.getCode());
                orderAllCount = orderService.queryAllCount(o);
                allAmount = orderService.queryAllAmount(o);
                orderWeekCount = orderService.queryWeekCount(o);
                orderWeekAmount = orderService.queryWeekAmount(o);
                engCount = engineerService.queryCountByProviderCode(provider.getCode());
                shopCount = shopService.queryCountByProviderCode(provider.getCode());
            }
            else if (su.getType() == SystemConstant.USER_TYPE_SHOP) {
                Shop shop = shopService.queryByCode(su.getShopCode());
                request.setAttribute("shop", shop);
                o.setShopCode(shop.getCode());
                allAmount = orderService.queryAllAmount(o);
                orderAllCount = orderService.queryAllCount(o);
                orderMonthCount = orderService.queryMonthCount(o);
                orderMonthAmount = orderService.queryMonthAmount(o);
                engCount = engineerService.queryCountByShopCode(shop.getCode());
            }
            request.setAttribute("orderDayCount", orderDayCount);
            request.setAttribute("orderWeekCount", orderWeekCount);
            request.setAttribute("orderMonthCount", orderMonthCount);
            request.setAttribute("orderAllCount", orderAllCount);
            request.setAttribute("orderDayAmount", orderDayAmount);
            request.setAttribute("orderWeekAmount", orderWeekAmount);
            request.setAttribute("orderMonthAmount", orderMonthAmount);
            request.setAttribute("allAmount", allAmount);
            request.setAttribute("engCount", engCount);
            request.setAttribute("shopCount", shopCount);
            request.setAttribute("customerCount", customerCount);
        }
        return new ModelAndView(returnView);
    }

}
