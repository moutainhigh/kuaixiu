package com.kuaixiu.order.controller;

import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.wechat.common.util.StringUtils;
import com.kuaixiu.order.entity.Order;
import com.kuaixiu.order.entity.ReworkOrder;
import com.kuaixiu.order.service.OrderService;
import com.kuaixiu.order.service.ReworkOrderService;
import com.system.api.entity.ResultData;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * ReworkOrder Controller
 *
 * @CreateDate: 2019-01-10 上午09:45:45
 * @version: V 1.0
 */
@Controller
public class ReworkOrderController extends BaseController {
    private static final Logger log = Logger.getLogger(ReworkOrderController.class);
    @Autowired
    private ReworkOrderService reworkOrderService;
    @Autowired
    private OrderService orderService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/reworkOrder/list")
    public ModelAndView list(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {

        String returnView = "reworkOrder/list";
        return new ModelAndView(returnView);
    }


    /**
     * H5订单售后生成订单
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/reworkOrder/addRework")
    @ResponseBody
    public ResultData addRework(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        try {
            JSONObject params = getPrarms(request);
            String orderNo = params.getString("orderNo");
            String reworkReason = params.getString("reworkReason");
            String reasonDetail = params.getString("reasonDetail");

            if (StringUtils.isBlank(reworkReason) || StringUtils.isBlank(orderNo)) {
                return getResult(result, null, false, "2", "参数不完整");
            }
            Order order = orderService.queryByOrderNo(orderNo);
            if (order == null) {
                return getResult(result, null, false, "3", "该订单不存在");
            }
            List<ReworkOrder> reworkOrders = reworkOrderService.getDao().queryByParentOrder(order.getOrderNo());
            if (!CollectionUtils.isEmpty(reworkOrders)) {
                return getResult(result, null, false, "4", "该订单正在售后中");
            }
            ReworkOrder reworkOrder = new ReworkOrder();
            reworkOrder.setReworkReasons(Integer.valueOf(reworkReason));
            reworkOrder.setReasonsDetail(reasonDetail);
            //创建保存返修订单
            reworkOrderService.save(order, reworkOrder);
            //给工程师派单
            reworkOrderService.dispatch(order, reworkOrder);

            getResult(result, null, true, "0", "成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.info(e.getMessage());
        }
        return result;
    }

}
