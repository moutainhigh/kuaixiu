package com.kuaixiu.apiService;

import java.util.Date;
import java.util.Map;

import com.kuaixiu.order.entity.ReworkOrder;
import com.kuaixiu.order.service.ReworkOrderService;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.common.exception.ApiServiceException;
import com.common.exception.SystemException;
import com.common.util.SmsSendUtil;
import com.kuaixiu.engineer.entity.Engineer;
import com.kuaixiu.engineer.service.EngineerService;
import com.kuaixiu.oldtonew.entity.Agreed;
import com.kuaixiu.oldtonew.entity.NewOrder;
import com.kuaixiu.oldtonew.entity.OldToNewUser;
import com.kuaixiu.oldtonew.service.AgreedService;
import com.kuaixiu.oldtonew.service.NewOrderService;
import com.kuaixiu.oldtonew.service.OldToNewService;
import com.kuaixiu.order.constant.OrderConstant;
import com.kuaixiu.order.entity.Order;
import com.kuaixiu.order.service.OrderService;
import com.kuaixiu.shop.entity.Shop;
import com.kuaixiu.shop.service.ShopService;
import com.system.api.ApiServiceInf;
import com.system.basic.user.entity.SessionUser;
import com.system.constant.ApiResultConstant;
import com.system.constant.SystemConstant;

import jodd.util.StringUtil;


/**
 * 更新订单状态操作接口实现类
 *
 * @author wugl
 */
@Service("orderStatusApiService")
public class OrderStatusApiService implements ApiServiceInf {
    private static final Logger log = Logger.getLogger(OrderStatusApiService.class);

    @Autowired
    private OrderService orderService;
    @Autowired
    private EngineerService engService;
    @Autowired
    private NewOrderService newOrderService;
    @Autowired
    private ShopService shopService;
    @Autowired
    private OldToNewService oldToNewService;
    @Autowired
    private AgreedService agreedService;
    @Autowired
    private ReworkOrderService reworkOrderService;

    @Override
    @Transactional
    public Object process(Map<String, String> params) {

        //解析请求参数
        String paramJson = MapUtils.getString(params, "params");
        JSONObject pmJson = JSONObject.parseObject(paramJson);

        //验证请求参数
        if (pmJson == null
                || !pmJson.containsKey("orderNo")
                || !pmJson.containsKey("action")) {
            throw new ApiServiceException(ApiResultConstant.resultCode_1001, ApiResultConstant.resultCode_str_1001);
        }

        //获取订单号
        String orderNo = pmJson.getString("orderNo");
        //获取操作类型
        String action = pmJson.getString("action");
        Integer isRework = pmJson.getInteger("isRework");
        System.out.println("操作类型是：" + action);
        String engNumber = MapUtils.getString(params, "pmClientId");
        Engineer eng = engService.queryByEngineerNumber(engNumber);

        SessionUser su = new SessionUser();
        su.setUserId(eng.getNumber());
        su.setUserName(eng.getName());
        su.setType(SystemConstant.USER_TYPE_ENGINEER);

        try {
            if (isRework != null && 1 == isRework) {
                //修改返修订单状态
                updateReworkStatus(orderNo, action, eng, pmJson, su);
            } else {
                //查询订单信息
                Order o = orderService.queryByOrderNo(orderNo);
                //查询以旧换新订单信息
                NewOrder newOrder = (NewOrder) newOrderService.queryByOrderNo(orderNo);

                if (o == null && newOrder == null) {
                    throw new ApiServiceException(ApiResultConstant.resultCode_1003, "订单信息未找到");
                }

                if (o != null) {
                    //如果是维修订单
                    updateOrderStatus(o, engNumber, action, eng, pmJson, su);
                } else {
                    //修改以旧换新订单状态
                    updateNewOrderStatus(newOrder, engNumber, action, pmJson, su);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return "OK";
    }


    //修改返修订单状态
    private void updateReworkStatus(String orderNo, String action, Engineer eng, JSONObject pmJson, SessionUser su) throws Exception {
        ReworkOrder reworkOrder = reworkOrderService.getDao().queryByReworkNo(orderNo);
        if (reworkOrder == null) {
            throw new ApiServiceException(ApiResultConstant.resultCode_1003, "订单信息未找到");
        }
        Order order = orderService.queryByOrderNo(reworkOrder.getParentOrder());
        if (order == null) {
            throw new ApiServiceException(ApiResultConstant.resultCode_1003, "订单信息未找到");
        }
        if (reworkOrder.getOrderStatus() == OrderConstant.ORDER_STATUS_CANCEL) {
            throw new SystemException("该订单已取消！");
        }
        switch (action) {
            case "10":
                if (reworkOrder.getOrderStatus() < OrderConstant.ORDER_STATUS_RECEIVED) {
                    throw new ApiServiceException(ApiResultConstant.resultCode_3002, "订单未确认，无法进行检修");
                } else if (reworkOrder.getOrderStatus() == OrderConstant.ORDER_STATUS_START_CHECK) {
                    throw new ApiServiceException(ApiResultConstant.resultCode_3002, "订单正在进行检修，无需重复操作");
                } else if (reworkOrder.getOrderStatus() > OrderConstant.ORDER_STATUS_START_CHECK) {
                    throw new ApiServiceException(ApiResultConstant.resultCode_3002, "订单已完成检修，无需重复操作");
                }

                reworkOrder.setOrderStatus(OrderConstant.ORDER_STATUS_START_CHECK);
                reworkOrder.setStartCheckTime(new Date());
                reworkOrderService.saveUpdate(reworkOrder);
                break;
            case "20":
                //判断订单状态是否已经是完成支付状态
                if (reworkOrder.getOrderStatus() < OrderConstant.ORDER_STATUS_END_CHECK) {
                    throw new ApiServiceException(ApiResultConstant.resultCode_3002, "订单未完成检修，无法进行维修");
                } else if (reworkOrder.getOrderStatus() == OrderConstant.ORDER_STATUS_START_REPAIR) {
                    throw new ApiServiceException(ApiResultConstant.resultCode_3002, "订单正在进行维修，无需重复操作");
                } else if (reworkOrder.getOrderStatus() > OrderConstant.ORDER_STATUS_START_REPAIR) {
                    throw new ApiServiceException(ApiResultConstant.resultCode_3002, "订单已完成维修，无需重复操作");
                }
                reworkOrder.setOrderStatus(OrderConstant.ORDER_STATUS_START_REPAIR);
                reworkOrder.setStartRepairTime(new Date());
                reworkOrderService.saveUpdate(reworkOrder);
                break;
            case "30":
                //判断订单状态是否已经是完成支付状态
                if (reworkOrder.getOrderStatus() < OrderConstant.ORDER_STATUS_END_REPAIR) {
                    throw new ApiServiceException(ApiResultConstant.resultCode_3002, "订单未付款，不能进行发货操作");
                }
                //更改为待客户收件
                reworkOrder.setOrderStatus(OrderConstant.ORDER_STATUS_WAIT_CUSTOMER_RECEIVE);
                reworkOrder.setStartRepairTime(new Date());
                reworkOrderService.saveUpdate(reworkOrder);
                //向客户发送手机已发货信息
                SmsSendUtil.finishSendSmsToCustomer(order.getMobile());
                break;
            case "50":
                //判断订单状态是否已经是完成支付状态
                if (reworkOrder.getOrderStatus() < OrderConstant.ORDER_STATUS_START_CHECK) {
                    throw new ApiServiceException(ApiResultConstant.resultCode_3002, "订单未检修，请先点击开始检修");
                } else if (reworkOrder.getOrderStatus() >= OrderConstant.ORDER_STATUS_END_REPAIR) {
                    throw new ApiServiceException(ApiResultConstant.resultCode_3002, "订单已付款，无需重复操作");
                }
                reworkOrder.setOrderStatus(OrderConstant.ORDER_STATUS_END_REPAIR);
                reworkOrder.setEndRepairTime(new Date());
                //维修工程师改为空闲状态
                if (eng.getIsDispatch() == 1) {
                    engService.checkDispatchState(order.getEngineerId());
                }
                //通知客户确认订单
                reworkOrderService.saveUpdate(reworkOrder);
                break;
            case "60":
                //取消订单原因
                String selectReason = pmJson.getString("select_reason");
                String reason = pmJson.getString("cancel_reason");
                String cancelReason = null;
                if (!StringUtil.isBlank(selectReason)) {
                    cancelReason = selectReason;
                }
                if (!StringUtil.isBlank(reason) && !StringUtil.isBlank(selectReason)) {
                    cancelReason = cancelReason + "；" + reason;
                }
                if (!StringUtil.isBlank(reason) && StringUtil.isBlank(selectReason)) {
                    cancelReason = reason;
                }
                //判断订单状态是否已经是完成支付状态
                reworkOrderService.orderCancel(reworkOrder.getId(), OrderConstant.ORDER_CANCEL_TYPE_ENGINEER, cancelReason, su);
                break;

        }
    }


    //修改维修订单状态
    private void updateOrderStatus(Order o, String engNumber, String action, Engineer eng, JSONObject pmJson, SessionUser su) throws Exception {
        if (!engNumber.equals(o.getEngineerNumber())) {
            throw new ApiServiceException(ApiResultConstant.resultCode_3002, "对不起，您无权操作该订单");
        }
        if (o.getOrderStatus() == OrderConstant.ORDER_STATUS_CANCEL) {
            throw new SystemException("该订单已取消！");
        }

        o.setUpdateUserid(engNumber);
        //开始检修
        if ("10".equals(action)) {
            if (o.getOrderStatus() < OrderConstant.ORDER_STATUS_RECEIVED) {
                throw new ApiServiceException(ApiResultConstant.resultCode_3002, "订单未确认，无法进行检修");
            } else if (o.getOrderStatus() == OrderConstant.ORDER_STATUS_START_CHECK) {
                throw new ApiServiceException(ApiResultConstant.resultCode_3002, "订单正在进行检修，无需重复操作");
            } else if (o.getOrderStatus() > OrderConstant.ORDER_STATUS_START_CHECK) {
                throw new ApiServiceException(ApiResultConstant.resultCode_3002, "订单已完成检修，无需重复操作");
            }

            o.setOrderStatus(OrderConstant.ORDER_STATUS_START_CHECK);
            o.setStartCheckTime(new Date());
            orderService.saveUpdate(o);
        }
        //开始维修
        else if ("20".equals(action)) {
            //判断订单状态是否已经是完成支付状态
            if (o.getOrderStatus() < OrderConstant.ORDER_STATUS_END_CHECK) {
                throw new ApiServiceException(ApiResultConstant.resultCode_3002, "订单未完成检修，无法进行维修");
            } else if (o.getOrderStatus() == OrderConstant.ORDER_STATUS_START_REPAIR) {
                throw new ApiServiceException(ApiResultConstant.resultCode_3002, "订单正在进行维修，无需重复操作");
            } else if (o.getOrderStatus() > OrderConstant.ORDER_STATUS_START_REPAIR) {
                throw new ApiServiceException(ApiResultConstant.resultCode_3002, "订单已完成维修，无需重复操作");
            }
            o.setOrderStatus(OrderConstant.ORDER_STATUS_START_REPAIR);
            o.setStartRepairTime(new Date());
            orderService.saveUpdate(o);
            //寄修订单  确定发货
        } else if ("30".equals(action)) {
            //判断订单状态是否已经是完成支付状态
            if (o.getOrderStatus() < OrderConstant.ORDER_STATUS_END_REPAIR) {
                throw new ApiServiceException(ApiResultConstant.resultCode_3002, "订单未付款，不能进行发货操作");
            }
            //更改为待客户收件
            o.setOrderStatus(OrderConstant.ORDER_STATUS_WAIT_CUSTOMER_RECEIVE);
            o.setStartRepairTime(new Date());
            orderService.saveUpdate(o);
            //向客户发送手机已发货信息
            SmsSendUtil.finishSendSmsToCustomer(o.getMobile());
        }
        //完成维修
        else if ("50".equals(action)) {
            //判断订单状态是否已经是完成支付状态
            if (o.getOrderStatus() < OrderConstant.ORDER_STATUS_START_CHECK) {
                throw new ApiServiceException(ApiResultConstant.resultCode_3002, "订单未检修，请先点击开始检修");
            } else if (o.getOrderStatus() >= OrderConstant.ORDER_STATUS_END_REPAIR) {
                throw new ApiServiceException(ApiResultConstant.resultCode_3002, "订单已付款，无需重复操作");
            }
            o.setOrderStatus(OrderConstant.ORDER_STATUS_END_REPAIR);
            o.setEndRepairTime(new Date());
            //维修工程师改为空闲状态
            if (eng.getIsDispatch() == 1) {
                engService.checkDispatchState(o.getEngineerId());
            }
            //通知客户确认订单
            orderService.saveUpdate(o);
        }
        //取消订单
        else if ("60".equals(action)) {
            //取消订单原因
            String selectReason = pmJson.getString("select_reason");
            String reason = pmJson.getString("cancel_reason");
            String cancelReason = null;
            if (!StringUtil.isBlank(selectReason)) {
                cancelReason = selectReason;
            }
            if (!StringUtil.isBlank(reason) && !StringUtil.isBlank(selectReason)) {
                cancelReason = cancelReason + "；" + reason;
            }
            if (!StringUtil.isBlank(reason) && StringUtil.isBlank(selectReason)) {
                cancelReason = reason;
            }
            //判断订单状态是否已经是完成支付状态
            orderService.orderCancel(o.getId(), OrderConstant.ORDER_CANCEL_TYPE_ENGINEER, cancelReason, su);
        }
    }


    //修改以旧换新订单状态
    private void updateNewOrderStatus(NewOrder newOrder, String engNumber, String action, JSONObject pmJson, SessionUser su) throws Exception {
        //是以旧换新订单
        Shop shop = shopService.queryByCode(newOrder.getShopCode());
        Engineer engineer = engService.queryById(newOrder.getEngineerId());
        OldToNewUser old = oldToNewService.queryById(newOrder.getUserId());
        //预约信息
        Agreed agreed = agreedService.queryByOrderNo(newOrder.getOrderNo());
        if (!engNumber.equals(engineer.getNumber())) {
            throw new ApiServiceException(ApiResultConstant.resultCode_3002, "对不起，您无权操作该订单");
        }
        if (newOrder.getOrderStatus() == OrderConstant.ORDER_STATUS_CANCEL) {
            throw new SystemException("该订单已取消！");
        }
        newOrder.setUpdateUserid(engNumber);
        //取消订单
        if ("70".equals(action)) {
            String selectReason = pmJson.getString("select_reason");
            String reason = pmJson.getString("cancel_reason");
            String cancelReason = null;
            if (!StringUtil.isBlank(selectReason)) {
                cancelReason = selectReason;
            }
            if (!StringUtil.isBlank(reason) && !StringUtil.isBlank(selectReason)) {
                cancelReason = cancelReason + "；" + reason;
            }
            if (!StringUtil.isBlank(reason) && StringUtil.isBlank(selectReason)) {
                cancelReason = reason;
            }
            //判断订单状态是否已经是完成支付状态
            newOrderService.newOrderCancel(newOrder.getId(), OrderConstant.ORDER_CANCEL_TYPE_ENGINEER, cancelReason, su);
        }
    }
}
