package com.kuaixiu.apiService;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.exception.ApiServiceException;
import com.common.paginate.Page;
import com.kuaixiu.coupon.entity.Coupon;
import com.kuaixiu.coupon.service.CouponService;
import com.kuaixiu.engineer.entity.Engineer;
import com.kuaixiu.engineer.service.EngineerService;
import com.kuaixiu.order.entity.Order;
import com.kuaixiu.order.entity.OrderDetail;
import com.kuaixiu.order.entity.UpdateOrderPrice;
import com.kuaixiu.order.service.OrderDetailService;
import com.kuaixiu.order.service.OrderService;
import com.kuaixiu.order.service.ReworkOrderService;
import com.kuaixiu.order.service.UpdateOrderPriceService;
import com.system.api.ApiServiceInf;
import com.system.constant.ApiResultConstant;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 订单操作接口实现类
 *
 * @author wugl
 */
@Service("orderApiService")
public class OrderApiService implements ApiServiceInf {
    private static final Logger log = Logger.getLogger(OrderApiService.class);

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDetailService detailService;
    @Autowired
    private EngineerService engService;
    @Autowired
    private CouponService couponService;
    @Autowired
    private UpdateOrderPriceService updateOrderPriceService;
    @Autowired
    private ReworkOrderService reworkOrderService;

    @Override
    public Object process(Map<String, String> params) {

        //获取工程师工号
        String number = MapUtils.getString(params, "pmClientId");
        //解析请求参数
        String paramJson = MapUtils.getString(params, "params");
        JSONObject pmJson = JSONObject.parseObject(paramJson);

        //验证请求参数
        if (pmJson == null
                || !pmJson.containsKey("pageIndex")
                || !pmJson.containsKey("pageSize")
                || !pmJson.containsKey("status")
                || !pmJson.containsKey("newStatus")
                ) {
            throw new ApiServiceException(ApiResultConstant.resultCode_1001, ApiResultConstant.resultCode_str_1001);
        }
        JSONArray jsonArray = new JSONArray();
        try {
            Engineer eng = engService.queryByEngineerNumber(number);
            Order order = new Order();
            order.setEngineerId(eng.getId());
            List<Object> statusL = new ArrayList<Object>();
            //查询状态 0 待维修（新派单未处理） 1 进行中（开始检修未结束）  2 已完成 3 已取消 4 所有订单
            String status = pmJson.getString("status");
            if (StringUtils.isNotBlank(status)) {
                String[] statusArray = status.split(",");
                for (String s : statusArray) {
                    statusL.add(s);
                }
            }
            order.setQueryStatusArray(statusL);
            Page page1 = new Page();
            Integer pageIndex = pmJson.getInteger("pageIndex");
            Integer pageSize = pmJson.getInteger("pageSize");
            //将值转化为绝对值
            pageIndex = Math.abs(pageIndex);
            pageSize = Math.abs(pageSize);
            page1.setCurrentPage(pageIndex);
            page1.setPageSize(pageSize);
            order.setPage(page1);
            //查找该工程师下所有维修订单
            List<Order> orderList = orderService.getDao().queryListApiForPage(order);
            getJsonArray(jsonArray, orderList);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return jsonArray;

    }

    private JSONArray getJsonArray(JSONArray jsonArray, List<Order> orderList) throws Exception {
        if (orderList != null) {
            for (Order o : orderList) {
                JSONObject json = new JSONObject();
                json.put("order_no", o.getOrderNo());
                json.put("customer_name", o.getCustomerName());
                json.put("brand_name", o.getBrandName());
                json.put("model_id", o.getModelId());
                json.put("model_name", o.getModelName());
                json.put("repair_type", o.getRepairType());
                json.put("price", o.getOrderPrice());
                json.put("color", o.getColor());
                List<UpdateOrderPrice> orderPrices = updateOrderPriceService.getDao().queryByUpOrderNo(o.getOrderNo());
                if (!CollectionUtils.isEmpty(orderPrices)) {
                    json.put("real_price", o.getRealPrice());//管理员修改过金额。直接显示总价
                } else {
                    json.put("real_price", o.getRealPriceSubCoupon());//未修改金额，去掉优惠券
                }
                json.put("is_use_coupon", o.getIsUseCoupon());
                json.put("is_update_price", o.getIsUpdatePrice());
                json.put("coupon_code", o.getCouponCode());
                json.put("coupon_name", o.getCouponName());
                json.put("coupon_price", o.getCouponPrice());
                if (o.getIsUseCoupon() == 1) {
                    Coupon c = couponService.queryByCode(o.getCouponCode());
                    if (c != null) {
                        JSONObject cpDetail = new JSONObject();
                        cpDetail.put("coupon_code", c.getCouponCode());
                        cpDetail.put("coupon_name", c.getCouponName());
                        cpDetail.put("coupon_price", c.getCouponPrice());
                        cpDetail.put("begin_time", c.getBeginTime());
                        cpDetail.put("end_time", c.getEndTime());
                        cpDetail.put("note", c.getNote());
                        json.put("coupon_info", cpDetail);
                    }
                }
                json.put("order_status", o.getOrderStatus());
                json.put("agreed_time", o.getAgreedTime());
                json.put("pay_Status", o.getPayStatus());
                json.put("cancel_type", o.getCancelType());
                json.put("in_time", o.getInTime());
                json.put("eng_note", o.getEngNote());
                json.put("is_rework", o.getIsRework());
                //查询订单明细
                List<OrderDetail> orderDetails = new ArrayList<OrderDetail>();
//                if (o.getIsRework() == null || 0 == o.getIsRework()) {
                    orderDetails = detailService.queryByOrderNo(o.getOrderNo());
//                } else if (1 == o.getIsRework()) {
//                    ReworkOrder reworkOrder = reworkOrderService.getDao().queryByReworkNo(o.getOrderNo());
//                    orderDetails = detailService.queryByOrderNo(reworkOrder.getParentOrder());
//                }
                JSONArray jsonDetails = new JSONArray();
                for (OrderDetail od : orderDetails) {
                    JSONObject item = new JSONObject();
                    item.put("type", od.getType());
                    item.put("project_id", od.getProjectId());
                    item.put("project_name", od.getProjectName());
                    item.put("price", od.getRealPrice());
                    jsonDetails.add(item);
                }
                json.put("projects", jsonDetails);
                jsonArray.add(json);
            }
        }
        return jsonArray;
    }


}
