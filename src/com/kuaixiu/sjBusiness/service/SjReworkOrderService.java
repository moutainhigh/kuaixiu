package com.kuaixiu.sjBusiness.service;


import com.common.base.service.BaseService;
import com.common.util.NOUtil;
import com.kuaixiu.sjBusiness.dao.SjReworkOrderMapper;
import com.kuaixiu.sjBusiness.entity.SjOrder;
import com.kuaixiu.sjBusiness.entity.SjReworkOrder;

import com.kuaixiu.sjUser.entity.SjUser;
import com.kuaixiu.sjUser.service.SjUserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * SjReworkOrder Service
 *
 * @CreateDate: 2019-06-28 下午04:31:50
 * @version: V 1.0
 */
@Service("sjReworkOrderService")
public class SjReworkOrderService extends BaseService<SjReworkOrder> {
    private static final Logger log = Logger.getLogger(SjReworkOrderService.class);

    @Autowired
    private SjReworkOrderMapper<SjReworkOrder> mapper;
    @Autowired
    private SjUserService sjUserService;


    public SjReworkOrderMapper<SjReworkOrder> getDao() {
        return mapper;
    }

//**********自定义方法***********

    public SjReworkOrder submitReworkOrder(SjOrder sjOrder, String projects, String note, String phone) {
        SjReworkOrder sjReworkOrder = new SjReworkOrder();
        sjReworkOrder.setId(UUID.randomUUID().toString().replace("-", ""));
        sjReworkOrder.setReworkOrderNo(NOUtil.getNo("NBR-"));
        sjReworkOrder.setOrderId(sjOrder.getId());
        sjReworkOrder.setOrderNo(sjOrder.getOrderNo());
        sjReworkOrder.setState(100);
        sjReworkOrder.setProjectIds(projects);
        sjReworkOrder.setNote(note);
        sjReworkOrder.setCreateUserid(phone);
        this.add(sjReworkOrder);
        return this.queryById(sjReworkOrder.getId());
    }

    public void assignReworkOrder(SjReworkOrder sjReworkOrder, SjOrder sjOrder) {
        SjUser sjUser = sjUserService.getDao().queryByLoginId(sjOrder.getAssignCompanyId(), 3);
        sjReworkOrder.setCompanyId(sjUser.getLoginId());
        sjReworkOrder.setCompanyName(sjUser.getName());
        this.saveUpdate(sjReworkOrder);
    }

}