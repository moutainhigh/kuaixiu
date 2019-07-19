package com.kuaixiu.apiService;

import com.alibaba.fastjson.JSONObject;
import com.common.exception.ApiServiceException;
import com.kuaixiu.engineer.service.EngineerService;
import com.kuaixiu.order.entity.Order;
import com.kuaixiu.order.entity.ReworkOrder;
import com.kuaixiu.order.service.OrderService;
import com.kuaixiu.order.service.ReworkOrderService;
import com.system.api.ApiServiceInf;
import com.system.constant.ApiResultConstant;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 创建返修订单
 *
 * @author wugl
 */
@Service("reworkOrderApiService")
public class ReworkOrderApiService implements ApiServiceInf {
    private static final Logger log = Logger.getLogger(ReworkOrderApiService.class);

    @Autowired
    private EngineerService engService;
    @Autowired
    private ReworkOrderService reworkOrderService;
    @Autowired
    private OrderService orderService;

    @Override
    public Object process(Map<String, String> params) {
        try {
//            //获取工程师工号
//            String number = MapUtils.getString(params, "pmClientId");
            //解析请求参数
            String paramJson = MapUtils.getString(params, "params");
            JSONObject pmJson = JSONObject.parseObject(paramJson);

            //验证请求参数
            if (pmJson == null
                    || !pmJson.containsKey("orderNo")
                    || !pmJson.containsKey("reworkReason")
                    || !pmJson.containsKey("reasonDetail")
                    ) {
                throw new ApiServiceException(ApiResultConstant.resultCode_1001, ApiResultConstant.resultCode_str_1001);
            }
            String orderNo = pmJson.getString("orderNo");
            String reworkReason = pmJson.getString("reworkReason");
            String reasonDetail = pmJson.getString("reasonDetail");
            Order order = orderService.queryByOrderNo(orderNo);
            if (order == null) {
                throw new ApiServiceException(ApiResultConstant.resultCode_1003, ApiResultConstant.resultCode_str_1003);
            }
            List<ReworkOrder> reworkOrders = reworkOrderService.getDao().queryByParentOrder(order.getOrderNo());
            if (!CollectionUtils.isEmpty(reworkOrders)) {
                throw new ApiServiceException(ApiResultConstant.resultCode_1003, "该订单正在售后中！");
            }
            ReworkOrder reworkOrder = new ReworkOrder();
            reworkOrder.setReworkReasons(Integer.valueOf(reworkReason));
            reworkOrder.setReasonsDetail(reasonDetail);
            //创建保存返修订单
            reworkOrderService.save(order, reworkOrder);
            order.setIsRework(1);
            orderService.saveUpdate(order);
            //给工程师派单
            reworkOrderService.dispatch(order, reworkOrder);
        } catch (Exception e) {
            e.printStackTrace();
            log.info(e.getMessage());
        }
        return "OK";
    }
}
