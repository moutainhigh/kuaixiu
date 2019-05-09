package com.kuaixiu.sjBusiness.controller;


import com.common.base.controller.BaseController;
import com.common.exception.SystemException;
import com.common.paginate.Page;
import com.kuaixiu.shop.entity.Shop;
import com.kuaixiu.sjBusiness.entity.SjOrder;
import com.kuaixiu.sjBusiness.service.OrderCompanyPictureService;
import com.kuaixiu.sjBusiness.service.SjOrderService;
import com.kuaixiu.sjBusiness.service.SjProjectService;
import com.system.basic.address.entity.Address;
import com.system.basic.user.entity.SessionUser;
import com.system.constant.SystemConstant;
import jodd.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Order Controller
 *
 * @CreateDate: 2019-05-06 上午10:44:49
 * @version: V 1.0
 */
@Controller
public class SjBackstageController  extends BaseController {
    private static final Logger log = Logger.getLogger(SjBackstageController.class);

    @Autowired
    private SjOrderService orderService;
    @Autowired
    private SjProjectService projectService;
    @Autowired
    private OrderCompanyPictureService orderCompanyPictureService;


    @RequestMapping(value = "/sj/order/list")
    public ModelAndView list(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {

        String returnView ="shangji/orderList";
        return new ModelAndView(returnView);
    }

    @RequestMapping(value = "/sj/order/queryListForPage")
    public void queryListForPage(HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        Page page = getPageByRequest(request);
        try {
            //获取查询条件
            String orderNo = request.getParameter("orderNo");
            SjOrder sjOrder=new SjOrder();
            sjOrder.setPage(page);
            List<SjOrder> sjOrders=orderService.queryListForPage(sjOrder);
            for(SjOrder sjOrder1:sjOrders){

            }

        }catch (Exception e){
            e.printStackTrace();
        }
        this.renderJson(response, page);
    }
}
