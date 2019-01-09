package com.kuaixiu.order.service;


import com.common.base.service.BaseService;
import com.common.util.NOUtil;
import com.common.util.SmsSendUtil;
import com.kuaixiu.engineer.entity.Engineer;
import com.kuaixiu.engineer.service.EngineerService;
import com.kuaixiu.order.constant.OrderConstant;
import com.kuaixiu.order.dao.ReworkOrderMapper;
import com.kuaixiu.order.entity.Order;
import com.kuaixiu.order.entity.ReworkOrder;

import com.kuaixiu.shop.entity.Shop;
import com.kuaixiu.shop.service.ShopService;
import com.system.constant.SystemConstant;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

/**
 * ReworkOrder Service
 *
 * @CreateDate: 2019-01-09 上午11:10:46
 * @version: V 1.0
 */
@Service("reworkOrderService")
public class ReworkOrderService extends BaseService<ReworkOrder> {
    private static final Logger log = Logger.getLogger(ReworkOrderService.class);

    @Autowired
    private ReworkOrderMapper<ReworkOrder> mapper;
    @Autowired
    private EngineerService engineerService;
    @Autowired
    private ShopService shopService;


    public ReworkOrderMapper<ReworkOrder> getDao() {
        return mapper;
    }

    //**********自定义方法***********

    //创建保存返修订单
    public void save(Order order, ReworkOrder reworkOrder) {

        reworkOrder.setOrderReworkNo(NOUtil.getNo("RO-"));
        reworkOrder.setParentOrder(order.getOrderNo());
        reworkOrder.setId(UUID.randomUUID().toString().replace("-", ""));
        reworkOrder.setCustomerId(order.getCustomerId());
        reworkOrder.setCustomerName(order.getCustomerName());
        reworkOrder.setMobile(order.getMobile());
        reworkOrder.setEmail(order.getEmail());
        reworkOrder.setProvince(order.getProvince());
        reworkOrder.setCity(order.getCity());
        reworkOrder.setCounty(order.getCounty());
        reworkOrder.setStreet(order.getStreet());
        reworkOrder.setAreas(order.getAreas());
        reworkOrder.setAddress(order.getAddress());
        //用户坐标
        reworkOrder.setLatitude(order.getLatitude());
        reworkOrder.setLongitude(order.getLongitude());
        //设置初始值
        reworkOrder.setIsLock(0);
        reworkOrder.setSort(0);
        reworkOrder.setIsDel(0);
        reworkOrder.setBalanceStatus(0);
        reworkOrder.setCancelType(0);
        reworkOrder.setCancelStatus(0);
        reworkOrder.setSendAgreedNews(0);
        //  如果该订单不是店员创建的 将clerkId设置为0
        if (reworkOrder.getClerkId() == null) {
            reworkOrder.setClerkId("0");
        }
        reworkOrder.setBrandId(order.getBrandId());
        reworkOrder.setModelId(order.getModelId());
        reworkOrder.setColor(order.getColor());
        reworkOrder.setIsMobile(1);
        reworkOrder.setRepairType(order.getRepairType());
        reworkOrder.setPostscript(order.getPostscript());
        reworkOrder.setOrderStatus(OrderConstant.ORDER_STATUS_DEPOSITED);
        reworkOrder.setBrandName(order.getBrandName());
        reworkOrder.setModelName(order.getModelName());
        reworkOrder.setOrderPrice(new BigDecimal(0));
        reworkOrder.setRealPrice(new BigDecimal(0));
        //下单完成给用户发送成功短信
        SmsSendUtil.sendSmsToCustomerforRework(reworkOrder.getMobile());
        getDao().add(reworkOrder);
    }

    //售后订单派单给工程师
    public void dispatch(Order order, ReworkOrder reworkOrder,Boolean isApp) {
        Engineer engineer = engineerService.queryByEngineerNumber(order.getEngineerNumber());
        if (engineer == null) {
            return;
        }
        if(isApp) {
            if (2 == engineer.getIsDispatch()) {
                engineer = engineerService.queryByEngineerNumber(SystemConstant.REWORK_ENGINEER_NUMBER);
            }
        }else{
            if (0 != engineer.getIsDispatch()) {
                engineer = engineerService.queryByEngineerNumber(SystemConstant.REWORK_ENGINEER_NUMBER);
            }
        }
        reworkOrder.setProviderCode(order.getProviderCode());
        reworkOrder.setProviderName(order.getProviderName());
        reworkOrder.setShopCode(order.getShopCode());
        reworkOrder.setShopName(order.getShopName());
        reworkOrder.setEngineerId(engineer.getId());
        reworkOrder.setEngineerName(engineer.getName());
        reworkOrder.setEngineerNumber(engineer.getNumber());
        reworkOrder.setEngineerMobile(engineer.getMobile());
        reworkOrder.setIsDispatch(1);
        reworkOrder.setDispatchTime(new Date());
        reworkOrder.setOrderStatus(OrderConstant.ORDER_STATUS_DISPATCHED);
        getDao().update(reworkOrder);
        //更改工程师状态
        engineer.setIsDispatch(1);
        engineerService.saveUpdate(engineer);
        Shop shop = shopService.queryByCode(engineer.getShopCode());
        //给工程师发送短信提示
        SmsSendUtil.sendSmsToEngineerForRework(engineer, shop, reworkOrder);
    }

}