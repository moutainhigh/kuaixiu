package com.kuaixiu.apiService;

import java.util.List;
import java.util.Map;

import com.kuaixiu.order.entity.ReworkOrder;
import com.kuaixiu.order.entity.UpdateOrderPrice;
import com.kuaixiu.order.service.ReworkOrderService;
import com.kuaixiu.order.service.UpdateOrderPriceService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.exception.ApiServiceException;
import com.kuaixiu.coupon.entity.Coupon;
import com.kuaixiu.coupon.entity.CouponModel;
import com.kuaixiu.coupon.entity.CouponProject;
import com.kuaixiu.coupon.service.CouponModelService;
import com.kuaixiu.coupon.service.CouponProjectService;
import com.kuaixiu.coupon.service.CouponService;
import com.kuaixiu.oldtonew.entity.Agreed;
import com.kuaixiu.oldtonew.entity.NewOrder;
import com.kuaixiu.oldtonew.entity.OldToNewUser;
import com.kuaixiu.oldtonew.service.AgreedService;
import com.kuaixiu.oldtonew.service.NewOrderService;
import com.kuaixiu.oldtonew.service.OldToNewService;
import com.kuaixiu.order.entity.Order;
import com.kuaixiu.order.entity.OrderDetail;
import com.kuaixiu.order.service.OrderDetailService;
import com.kuaixiu.order.service.OrderService;
import com.kuaixiu.shop.entity.Shop;
import com.kuaixiu.shop.service.ShopService;
import com.system.api.ApiServiceInf;
import com.system.constant.ApiResultConstant;


/**
 * 订单详情操作接口实现类
 *
 * @author wugl
 */
@Service("orderDetailsApiService")
public class OrderDetailsApiService implements ApiServiceInf {
    private static final Logger log = Logger.getLogger(OrderDetailsApiService.class);

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDetailService detailService;
    @Autowired
    private CouponService couponService;
    @Autowired
    private CouponModelService modelService;
    @Autowired
    private CouponProjectService couponProjectService;
    @Autowired
    private NewOrderService newOrderService;
    @Autowired
    private ShopService shopService;
    @Autowired
    private OldToNewService oldToNewService;
    @Autowired
    private AgreedService agreedService;
    @Autowired
    private UpdateOrderPriceService updateOrderPriceService;
    @Autowired
    private ReworkOrderService reworkOrderService;

    @Override
    public Object process(Map<String, String> params) {

        //解析请求参数
        String paramJson = MapUtils.getString(params, "params");
        JSONObject pmJson = JSONObject.parseObject(paramJson);

        //验证请求参数
        if (pmJson == null
                || !pmJson.containsKey("orderNo")
                || !pmJson.containsKey("isRework")) {
            throw new ApiServiceException(ApiResultConstant.resultCode_1001, ApiResultConstant.resultCode_str_1001);
        }

        //获取订单号
        String orderNo = pmJson.getString("orderNo");
        Integer isRework = pmJson.getInteger("isRework");
        JSONObject json = new JSONObject();
        if (isRework == 1) {
            ReworkOrder reworkOrder = reworkOrderService.getDao().queryByReworkNo(orderNo);
            Order o = orderService.queryByOrderNo(reworkOrder.getParentOrder());
            json.put("order_no", reworkOrder.getOrderReworkNo());
            json.put("customer_name", o.getCustomerName());
            json.put("customer_mobile", o.getMobile());
            json.put("address", o.getFullAddress());
            json.put("postscript", o.getPostscript());
            json.put("longitude", o.getLongitude());
            json.put("latitude", o.getLatitude());
            json.put("provider_name", o.getProviderName());
            json.put("shop_name", o.getShopName());
            json.put("number", o.getEngineerNumber());
            json.put("name", o.getEngineerName());
            json.put("brand_name", o.getBrandName());
            json.put("model_id", o.getModelId());
            json.put("model_name", o.getModelName());
            json.put("is_update_price", o.getIsUpdatePrice());
            json.put("color", o.getColor());
            json.put("price", reworkOrder.getOrderPrice());
            json.put("real_price", 0);//管理员修改过金额。直接显示总价
            json.put("order_status", reworkOrder.getOrderStatus());
            json.put("agreed_time", reworkOrder.getAgreedTime());
            json.put("cancel_type", reworkOrder.getCancelType());
            json.put("in_time", reworkOrder.getInTime());
            json.put("eng_note", reworkOrder.getEngNote());
            //查询订单明细
            List<OrderDetail> orderDetails = detailService.queryByOrderNo(o.getOrderNo());
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
            json.put("is_rework", 1);
        } else {
            //根据订单号判定该订单是维修订单还是以旧换新订单
            //查询订单信息
            Order o = orderService.queryByOrderNo(orderNo);
            //查询以旧换新订单
            NewOrder newOrder = (NewOrder) newOrderService.queryByOrderNo(orderNo);

            if (o == null && newOrder == null) {
                throw new ApiServiceException(ApiResultConstant.resultCode_1003, "订单信息未找到");
            }
            List<UpdateOrderPrice> orderPrices = updateOrderPriceService.getDao().queryByUpOrderNo(o.getOrderNo());

            if (o != null) {
                json.put("is_rework", 0);
                json.put("order_no", o.getOrderNo());
                json.put("customer_name", o.getCustomerName());
                json.put("customer_mobile", o.getMobile());
                json.put("address", o.getFullAddress());
                json.put("postscript", o.getPostscript());
                json.put("longitude", o.getLongitude());
                json.put("latitude", o.getLatitude());
                json.put("provider_name", o.getProviderName());
                json.put("shop_name", o.getShopName());
                json.put("number", o.getEngineerNumber());
                json.put("name", o.getEngineerName());
                json.put("brand_name", o.getBrandName());
                json.put("model_id", o.getModelId());
                json.put("model_name", o.getModelName());
                json.put("is_update_price", o.getIsUpdatePrice());
                json.put("color", o.getColor());
                json.put("repair_type", o.getRepairType());
                json.put("deposit_type", o.getDepositType());
                json.put("deposit_price", o.getDepositPrice());
                json.put("pay_type", o.getPayType());
                json.put("price", o.getOrderPrice());
                if (!CollectionUtils.isEmpty(orderPrices)) {
                    json.put("real_price", o.getRealPrice());//管理员修改过金额。直接显示总价
                } else {
                    json.put("real_price", o.getRealPriceSubCoupon());//未修改金额，去掉优惠券
                }
                json.put("is_use_coupon", o.getIsUseCoupon());
                json.put("coupon_code", o.getCouponCode());
                json.put("coupon_name", o.getCouponName());
                json.put("coupon_price", o.getCouponPrice());
                json.put("cancel_reason", o.getCancelReason());
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
                        JSONArray modelDetails = new JSONArray();
                        List<CouponModel> couModels = modelService.queryListByCouponId(c.getId());
                        if (couModels != null && couModels.size() > 0) {
                            for (CouponModel cm : couModels) {
                                JSONObject item = new JSONObject();
                                item.put("brand_id", cm.getBrandId());
                                item.put("brand_name", cm.getBrandName());
                                modelDetails.add(item);
                            }
                        }
                        cpDetail.put("brands", modelDetails);
                        JSONArray projectDetails = new JSONArray();
                        List<CouponProject> couProjects = couponProjectService.queryListByCouponId(c.getId());
                        if (couProjects != null && couProjects.size() > 0) {
                            for (CouponProject cp : couProjects) {
                                JSONObject item = new JSONObject();
                                item.put("brand_id", cp.getProjectId());
                                item.put("brand_name", cp.getProjectName());
                                projectDetails.add(item);
                            }
                        }
                        cpDetail.put("projects", projectDetails);
                        json.put("coupon_info", cpDetail);
                    }
                }
                json.put("order_status", o.getOrderStatus());
                json.put("agreed_time", o.getAgreedTime());
                json.put("pay_Status", o.getPayStatus());
                json.put("cancel_type", o.getCancelType());
                json.put("in_time", o.getInTime());
                json.put("eng_note", o.getEngNote());
                //查询订单明细
                List<OrderDetail> orderDetails = detailService.queryByOrderNo(o.getOrderNo());
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

            } else {
                //添加以旧换新信息
                Shop shop = new Shop();
                if (newOrder.getIsDispatch() == 1) {
                    shop = shopService.queryByCode(newOrder.getShopCode());
                }
                OldToNewUser old = oldToNewService.queryById(newOrder.getUserId());
                json.put("is_rework", 0);
                json.put("order_no", newOrder.getOrderNo());
                json.put("order_status", newOrder.getOrderStatus());
                json.put("in_time", newOrder.getInTime());
                json.put("customer_name", old.getName());
                json.put("old_model", old.getOldMobile());
                json.put("new_model", old.getNewMobile());
                json.put("shop_name", shop.getName());
                json.put("order_status", newOrder.getOrderStatus());
                json.put("in_time", newOrder.getInTime());
                json.put("customer_mobile", old.getTel());
                json.put("old_model", old.getOldMobile());
                json.put("new_model", old.getNewMobile());
                json.put("shop_name", shop.getName());
                json.put("cancel_reason", newOrder.getCancelReason());
                //预约信息
                Agreed agreed = agreedService.queryByOrderNo(newOrder.getOrderNo());
                if (agreed != null) {
                    json.put("memory", agreed.getMemory());
                    json.put("color", agreed.getColor());
                    json.put("agreed_time", agreed.getAgreedTime());
                    json.put("agreed_model", agreed.getNewModelName());
                    json.put("edition", agreed.getEdition());
                    json.put("agreed_other", agreed.getOther());
                    json.put("agreed_brand", agreed.getAgreedBrand());
                }
                json.put("address", old.getHomeAddress());
                json.put("postscript", old.getPostscript());
                json.put("convert_type", newOrder.getConvertType());
                json.put("real_price", newOrder.getRealPrice());
                //添加优惠券信息
                JSONObject c = new JSONObject();
                if (newOrder.getCouponId() != null) {
                    c.put("coupon_code", newOrder.getCouponCode());
                    c.put("coupon_name", newOrder.getCouponName());
                    c.put("coupon_price", newOrder.getCouponPrice());
                    Coupon coupon = couponService.queryById(newOrder.getCouponId());
                    if (coupon != null) {
                        c.put("begin_time", coupon.getBeginTime());
                        c.put("end_time", coupon.getEndTime());
                        json.put("coupon_info", c);
                    }
                }

            }
        }
        return json;
    }

}
