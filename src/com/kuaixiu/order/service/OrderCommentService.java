package com.kuaixiu.order.service;


import com.common.base.service.BaseService;
import com.common.exception.SystemException;
import com.kuaixiu.oldtonew.entity.NewOrder;
import com.kuaixiu.oldtonew.service.NewOrderService;
import com.kuaixiu.order.constant.OrderConstant;
import com.kuaixiu.order.dao.OrderCommentMapper;
import com.kuaixiu.order.entity.Order;
import com.kuaixiu.order.entity.OrderComment;
import com.kuaixiu.order.entity.ReworkOrder;
import com.system.basic.user.entity.SessionUser;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * OrderComment Service
 * @CreateDate: 2016-08-26 下午10:45:08
 * @version: V 1.0
 */
@Service("orderCommentService")
public class OrderCommentService extends BaseService<OrderComment> {
    private static final Logger log= Logger.getLogger(OrderCommentService.class);

    @Autowired
    private OrderCommentMapper<OrderComment> mapper;
    @Autowired
    private OrderService orderService;
    @Autowired
    private NewOrderService newOrderService;
    @Autowired
    private ReworkOrderService reworkOrderService;

    public OrderCommentMapper<OrderComment> getDao() {
        return mapper;
    }

    //**********自定义方法***********

    /**
     * 保存订单评价
     * @param orderId
     * @param comm
     * @param su
     * @return
     * @CreateDate: 2016-9-17 下午8:44:33
     */
    @Transactional
    public int save(String orderId, OrderComment comm, SessionUser su){
        Order o = orderService.queryById(orderId);
        if(o == null){
            throw new SystemException("订单不存在，不能进行评价");
        }
        if(!su.getUserId().equals(o.getCustomerId())){
            throw new SystemException("对不起你无法操作该订单");
        }
        if(o.getOrderStatus() == OrderConstant.ORDER_STATUS_CANCEL){
            throw new SystemException("订单已取消，不能进行评价");
        }
        if(o.getOrderStatus() != OrderConstant.ORDER_STATUS_FINISHED){
            throw new SystemException("订单未完成，不能进行评价");
        }
        if(o.getIsComment() == 1){
            throw new SystemException("订单已评价，不能重复评价");
        }
        
        //修改订单已评价
        o.setIsComment(1);
        orderService.saveUpdate(o);
        comm.setOrderNo(o.getOrderNo());
        comm.setIsDel(0);
        return getDao().add(comm);
    }
    
    
    /**
     * 保存以旧换新订单评价
     */
    @Transactional
    public int orderSave(String orderId, OrderComment comm, SessionUser su){
        NewOrder o = (NewOrder) newOrderService.queryById(orderId);
        if(o == null){
            throw new SystemException("订单不存在，不能进行评价");
        }
        if(!su.getUserId().equals(o.getCustomerId())){
            throw new SystemException("对不起你无法操作该订单");
        }
        if(o.getOrderStatus() == OrderConstant.ORDER_STATUS_CANCEL){
            throw new SystemException("订单已取消，不能进行评价");
        }
        if(o.getOrderStatus() != OrderConstant.ORDER_STATUS_FINISHED){
            throw new SystemException("订单未完成，不能进行评价");
        }
        if(o.getIsComment() == 1){
            throw new SystemException("订单已评价，不能重复评价");
        }
        
        //修改订单已评价
        o.setIsComment(1);
        newOrderService.saveUpdate(o);
        comm.setOrderNo(o.getOrderNo());
        comm.setIsDel(0);
        return getDao().add(comm);
    }

    /**
     * 保存返修订单评价
     * @param orderId
     * @param comm
     * @param su
     * @return
     * @CreateDate: 2016-9-17 下午8:44:33
     */
    @Transactional
    public int reSave(String orderId, OrderComment comm, SessionUser su){
        ReworkOrder o = reworkOrderService.queryById(orderId);
        if(o == null){
            throw new SystemException("订单不存在，不能进行评价");
        }
        if(o.getOrderStatus() == OrderConstant.ORDER_STATUS_CANCEL){
            throw new SystemException("订单已取消，不能进行评价");
        }
        if(o.getOrderStatus() != OrderConstant.ORDER_STATUS_FINISHED){
            throw new SystemException("订单未完成，不能进行评价");
        }
        if(o.getIsComment() == 1){
            throw new SystemException("订单已评价，不能重复评价");
        }

        //修改订单已评价
        o.setIsComment(1);
        reworkOrderService.saveUpdate(o);
        comm.setOrderNo(o.getOrderReworkNo());
        comm.setIsDel(0);
        return getDao().add(comm);
    }

    /**
     * 根据订单号查询评价信息
     */
    public OrderComment queryByOrderNo(String orderNo){
    	OrderComment o=new OrderComment();
    	o=getDao().queryByOrderNo(orderNo);
    	return o;
    }
}