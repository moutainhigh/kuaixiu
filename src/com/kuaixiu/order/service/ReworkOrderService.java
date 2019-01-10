package com.kuaixiu.order.service;


import com.common.base.service.BaseService;
import com.common.util.NOUtil;
import com.common.util.SmsSendUtil;
import com.kuaixiu.engineer.entity.Engineer;
import com.kuaixiu.engineer.service.EngineerService;
import com.kuaixiu.engineer.service.NewEngineerService;
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
 * @CreateDate: 2019-01-10 上午09:45:45
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
    @Autowired
    private NewEngineerService newEngineerService;

    public ReworkOrderMapper<ReworkOrder> getDao() {
        return mapper;
    }

//**********自定义方法***********

    //创建保存返修订单
    public void save(Order order, ReworkOrder reworkOrder) {

        reworkOrder.setOrderReworkNo(NOUtil.getNo(""));
        reworkOrder.setParentOrder(order.getOrderNo());
        reworkOrder.setId(UUID.randomUUID().toString().replace("-", ""));
        reworkOrder.setFromSystem("返修");
        //设置初始值
        reworkOrder.setIsLock(0);
        reworkOrder.setSort(0);
        reworkOrder.setIsDel(0);
        reworkOrder.setCancelType(0);
        reworkOrder.setCancelStatus(0);
        reworkOrder.setSendAgreedNews(0);
        reworkOrder.setOrderStatus(OrderConstant.ORDER_STATUS_DEPOSITED);
        reworkOrder.setOrderPrice(new BigDecimal(0));
        reworkOrder.setRealPrice(new BigDecimal(0));
        //下单完成给用户发送成功短信
        SmsSendUtil.sendSmsToCustomerforRework(order.getMobile());
        getDao().add(reworkOrder);
    }

    //售后订单派单给工程师
    public void dispatch(Order order, ReworkOrder reworkOrder) {
        Engineer engineer = engineerService.queryByEngineerNumber(order.getEngineerNumber());
        if (engineer == null || 2 == engineer.getIsDispatch()) {
            engineer = engineerService.queryByEngineerNumber(SystemConstant.REWORK_ENGINEER_NUMBER);
        }
        reworkOrder.setProviderCode(engineer.getProviderCode());
        reworkOrder.setProviderName(engineer.getProviderName());
        //如果是多个门店，就便利查找名称
        newEngineerService.engineerShopCode(engineer);
        reworkOrder.setShopCode(engineer.getShopCode());
        reworkOrder.setShopName(engineer.getShopName());
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
        SmsSendUtil.sendSmsToEngineerForRework(engineer, shop, reworkOrder, order);
    }


}