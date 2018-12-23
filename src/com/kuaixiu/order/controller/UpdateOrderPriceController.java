package com.kuaixiu.order.controller;

import com.common.base.controller.BaseController;
import com.common.exception.SystemException;
import com.common.paginate.Page;
import com.kuaixiu.order.entity.UpdateOrderPrice;
import com.kuaixiu.order.service.UpdateOrderPriceService;
import com.system.api.entity.ResultData;
import com.system.basic.user.service.SessionUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * UpdateOrderPrice Controller
 *
 * @CreateDate: 2018-11-06 上午10:36:10
 * @version: V 1.0
 */
@Controller
public class UpdateOrderPriceController extends BaseController {

    @Autowired
    private UpdateOrderPriceService updateOrderPriceService;
    @Autowired
    private SessionUserService sessionUserService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "order/updateOrderPriceList")
    public ModelAndView list(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {

        String returnView = "order/updateOrderPriceList";
        return new ModelAndView(returnView);
    }

    @RequestMapping(value = "order/updateOrderPrice/list")
    @ResponseBody
    public ResultData updateOrderPriceList(HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        ResultData resultData = new ResultData();
        Page page=new Page();
        try {
            UpdateOrderPrice orderPrice=new UpdateOrderPrice();
            page=getPageByRequest(request);
            orderPrice.setPage(page);
            List<UpdateOrderPrice> orderPrices=updateOrderPriceService.queryList(orderPrice);
            page.setData(orderPrices);
            resultData.setResultMessage("成功");
            resultData.setSuccess(true);
            resultData.setResult(page);
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, resultData);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(resultData);
        }
        return resultData;
    }
}
