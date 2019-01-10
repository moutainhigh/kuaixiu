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
import com.kuaixiu.order.entity.OrderDetail;
import com.kuaixiu.order.entity.ReworkOrder;

import com.kuaixiu.shop.entity.Shop;
import com.kuaixiu.shop.service.ShopService;
import com.system.constant.SystemConstant;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

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
    @Autowired
    private OrderDetailService detailService;

    public ReworkOrderMapper<ReworkOrder> getDao() {
        return mapper;
    }

//**********自定义方法***********

    //创建保存返修订单
    public void save(Order order, ReworkOrder reworkOrder) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
        reworkOrder.setInTime(new Date());
        Long time = order.getEndTime().getTime() - reworkOrder.getInTime().getTime();
        reworkOrder.setSurplusDay(time / (1000 * 3600 * 24));
        reworkOrder.setTotalDay(Long.valueOf(180));
        reworkOrder.setOrderPrice(new BigDecimal(getProject(order.getOrderNo()).get("orderPrice")));
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

    //获取项目信息，所有项目名称，项目总金额
    public Map<String,String> getProject(String orderNo){
        Map<String,String> map=new HashMap<String ,String>();
        //查询订单明细
        List<OrderDetail> details = detailService.queryByOrderNo(orderNo);
        List<String> projectNames = new ArrayList<>();
        Boolean isTrue = false;//判断是否有工程师确认的维修项目
        for (OrderDetail detail : details) {
            if (detail.getType() == 1) {
                isTrue = true;
                break;
            }
        }
        BigDecimal price=new BigDecimal("0");
        for (OrderDetail detail : details) {
            if (isTrue) {
                if (detail.getType() == 1) {//工程师确认的维修项目
                    projectNames.add(detail.getProjectName());
                    price.add(detail.getRealPrice());
                }
            } else {
                if (detail.getType() == 0) {//客户确认的维修项目
                    projectNames.add(detail.getProjectName());
                    price.add(detail.getRealPrice());
                }
            }
        }
        map.put("projectNames",StringUtils.join(projectNames, ","));
        map.put("orderPrice",String.valueOf(price));
        return map;
    }
}