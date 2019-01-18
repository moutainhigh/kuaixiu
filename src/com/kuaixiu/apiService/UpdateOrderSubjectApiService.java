package com.kuaixiu.apiService;

import java.math.BigDecimal;
import java.util.Date;
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
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.exception.ApiServiceException;
import com.common.util.NumberUtils;
import com.common.validator.ValidateResult;
import com.common.validator.Validator;
import com.kuaixiu.engineer.service.EngineerService;
import com.kuaixiu.model.entity.RepairCost;
import com.kuaixiu.model.service.RepairCostService;
import com.kuaixiu.order.constant.OrderConstant;
import com.kuaixiu.order.entity.Order;
import com.kuaixiu.order.entity.OrderDetail;
import com.kuaixiu.order.service.OrderDetailService;
import com.kuaixiu.order.service.OrderService;
import com.system.api.ApiServiceInf;
import com.system.constant.ApiResultConstant;
import com.system.util.SystemUtil;


/**
 * 更新订单状态操作接口实现类
 *
 * @author wugl
 */
@Service("updateOrderSubjectApiService")
public class UpdateOrderSubjectApiService implements ApiServiceInf {
    private static final Logger log = Logger.getLogger(UpdateOrderSubjectApiService.class);

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDetailService detailService;
    @Autowired
    private RepairCostService repairCostService;
    @Autowired
    private EngineerService engService;
    @Autowired
    private UpdateOrderPriceService updateOrderPriceService;
    @Autowired
    private ReworkOrderService reworkOrderService;
    @Autowired
    private OrderDetailService orderDetailService;

    @Override
    @Transactional
    public Object process(Map<String, String> params) {

        //解析请求参数
        String paramJson = MapUtils.getString(params, "params");
        JSONObject pmJson = JSONObject.parseObject(paramJson);
        if (pmJson == null) {
            throw new ApiServiceException(ApiResultConstant.resultCode_1001, ApiResultConstant.resultCode_str_1001);
        }
        //验证请求参数
        Validator validator = new Validator((JSONArray) SystemUtil.getProperty("goodsImportVoliatorJson"));
        ValidateResult result = validator.validate(pmJson);
        if (!result.getSuccess()) {
            throw new ApiServiceException(ApiResultConstant.resultCode_1001, result.getResultMessage());
        }

        //获取订单号
        String orderNo = pmJson.getString("orderNo");
        String engNumber = MapUtils.getString(params, "pmClientId");
        Integer isRework = pmJson.getInteger("isRework");
        //获取修改后台的维修项目
        JSONArray newDetails = pmJson.getJSONArray("projects");
        try {
            if (isRework != null && 1 == isRework) {
                //返修单修改县项目
                updateReworkProject(orderNo, engNumber, newDetails);
            } else {
                //维修订单修改项目
                updateOrderProject(orderNo, engNumber, newDetails, pmJson);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return "OK";
    }


    //返修单修改县项目
    private void updateReworkProject(String orderNo, String engNumber, JSONArray newDetails) throws Exception {
        ReworkOrder reworkOrder = reworkOrderService.getDao().queryByReworkNo(orderNo);
        if (reworkOrder == null) {
            throw new ApiServiceException(ApiResultConstant.resultCode_1003, "订单信息未找到");
        }
        for (int i = 0; i < newDetails.size(); i++) {
            JSONObject jsonPj = newDetails.getJSONObject(i);
            String projectId = jsonPj.getString("project_id");
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderNo(reworkOrder.getParentOrder());
            orderDetail.setProjectId(projectId);
            List<OrderDetail> orderDetails = orderDetailService.queryList(orderDetail);
            for (OrderDetail orderDetail1 : orderDetails) {
                orderDetail1.setIsRework(1);
                orderDetailService.saveUpdate(orderDetail1);
            }
        }

        reworkOrder.setEndTime(new Date());
        reworkOrder.setEndRepairTime(new Date());
        reworkOrder.setOrderStatus(OrderConstant.ORDER_STATUS_FINISHED);
        reworkOrder.setUpdateUserid(engNumber);
        reworkOrderService.saveUpdate(reworkOrder);

        Order o = orderService.queryByOrderNo(reworkOrder.getParentOrder());
        engService.checkDispatchState(o.getEngineerId());
    }

    //维修订单修改项目
    private void updateOrderProject(String orderNo, String engNumber, JSONArray newDetails, JSONObject pmJson) throws Exception {
        //查询订单信息
        Order o = orderService.queryByOrderNo(orderNo);
        if (o == null) {
            throw new ApiServiceException(ApiResultConstant.resultCode_1003, "订单信息未找到");
        }

        //判断是否有操作权限
        if (!engNumber.equals(o.getEngineerNumber())) {
            throw new ApiServiceException(ApiResultConstant.resultCode_2001, "对不起，您无权操作该订单");
        }


        if (o.getOrderStatus() == OrderConstant.ORDER_STATUS_FINISHED) {
            throw new ApiServiceException(ApiResultConstant.resultCode_3002, "该订单已完成！");
        }

        if (o.getOrderStatus() == OrderConstant.ORDER_STATUS_CANCEL) {
            throw new ApiServiceException(ApiResultConstant.resultCode_3002, "该订单已取消！");
        }

        if (o.getOrderStatus() < OrderConstant.ORDER_STATUS_START_CHECK) {
            throw new ApiServiceException(ApiResultConstant.resultCode_3002, "订单未开始检测，请先点击开始检测");
        }

        if (o.getOrderStatus() >= OrderConstant.ORDER_STATUS_END_REPAIR) {
            throw new ApiServiceException(ApiResultConstant.resultCode_3002, "订单已完成维修，无需重复操作");
        }
        //免费贴膜（现场优惠）项目id
        String francoProject = "6298c77e-d412-11e8-932d-00163e04c890";
        //下单时的金额=实际金额+退款金额
        BigDecimal orderPrice = new BigDecimal(0);

        //获取订单原来的维修项目
        //List<OrderDetail> details = detailService.queryByOrderNo(orderNo);
        //循环比较新增的维修项目
        for (int i = 0; i < newDetails.size(); i++) {
            JSONObject jsonPj = newDetails.getJSONObject(i);
            String projectId = jsonPj.getString("project_id");
            //新增订单明细
            OrderDetail od = new OrderDetail();
            od.setOrderNo(orderNo);
            od.setProjectId(projectId);
            //处理其它维修费用
            if ("zzpjqt".equals(projectId)) {
                od.setProjectName("其它");
                BigDecimal price = jsonPj.getBigDecimal("price");
                od.setPrice(price);
                od.setRealPrice(price);
                orderPrice = NumberUtils.add(orderPrice, price);
            } else {
                //查询维修维修费用
                RepairCost cost = repairCostService.queryByMidAndPid(o.getModelId(), projectId);
                if (cost == null) {
                    throw new ApiServiceException(ApiResultConstant.resultCode_1003, "维修项目未找到");
                }
                od.setProjectName(cost.getProjectName());
                if (newDetails.size() == 1 && francoProject.equals(projectId)) {
                    francoProject = "50";
                    od.setPrice(new BigDecimal(0));
                    od.setRealPrice(new BigDecimal(0));
                } else {
                    od.setPrice(cost.getPrice());
                    od.setRealPrice(cost.getPrice());
                    orderPrice = NumberUtils.add(orderPrice, cost.getPrice());
                }
            }
            od.setCreateUserid(engNumber);
            od.setType(1);
            od.setIsDel(0);
            detailService.add(od);
            //}
        }

        List<UpdateOrderPrice> updateOrderPrices = updateOrderPriceService.getDao().queryByUpOrderNo(o.getOrderNo());
        //未经管理员修改过金额的订单工程师才可以操作
        Boolean isZero = false;
        if (CollectionUtils.isEmpty(updateOrderPrices)) {
            o.setRealPrice(orderPrice);
            if (o.getRealPriceSubCoupon().doubleValue() == 0) {
                isZero = true;
            }
        }
        o.setEndCheckTime(new Date());
        o.setRepairType(pmJson.getInteger("repair_type"));
        o.setUpdateUserid(engNumber);
        //o.setOrderStatus(OrderConstant.ORDER_STATUS_END_CHECK);
        if ("50".equals(francoProject) || isZero) {
            o.setOrderStatus(OrderConstant.ORDER_STATUS_FINISHED);
            o.setEndTime(new Date());
        } else {
            o.setOrderStatus(OrderConstant.ORDER_STATUS_END_REPAIR);
        }
        o.setEndRepairTime(new Date());

        orderService.saveUpdate(o);
        engService.checkDispatchState(o.getEngineerId());
    }
}
