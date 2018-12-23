package com.kuaixiu.balance.service;


import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import jodd.jtx.meta.Transaction;

import com.common.base.service.BaseService;
import com.common.exception.SystemException;
import com.common.util.NOUtil;
import com.common.util.NumberUtils;
import com.kuaixiu.balance.dao.BalanceMapper;
import com.kuaixiu.balance.entity.Balance;
import com.kuaixiu.balance.entity.BalanceProvider;
import com.kuaixiu.order.entity.Order;
import com.kuaixiu.order.service.OrderService;
import com.kuaixiu.provider.entity.Provider;
import com.kuaixiu.provider.service.ProviderService;
import com.system.basic.user.entity.SessionUser;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Balance Service
 * @CreateDate: 2016-10-19 上午12:50:08
 * @version: V 1.0
 */
@Service("balanceService")
public class BalanceService extends BaseService<Balance> {
    private static final Logger log= Logger.getLogger(BalanceService.class);

    @Autowired
    private BalanceMapper<Balance> mapper;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ProviderService providerService;
    @Autowired
    private BalanceProviderService balanceProviderService;


    public BalanceMapper<Balance> getDao() {
        return mapper;
    }

    //**********自定义方法***********

    /**
     * 根据批次号查询批次
     * @param batchNo
     * @return
     * @author: lijx
     * @CreateDate: 2016-10-23 上午1:21:08
     */
    public Balance queryByBatchNo(String batchNo){
        Balance t = new Balance();
        t.setBatchNo(batchNo);
        List<Balance> list = getDao().queryList(t);
        if(list != null && list.size() > 0){
            return list.get(0);
        }
        return null;
    }
    
    /**
     * 删除结算单
     * @param id
     * @author: lijx
     * @CreateDate: 2016-10-26 下午10:35:57
     */
    public void deleteById(String id){
        Balance b = getDao().queryById(id);
        if(b == null){
            throw new SystemException("结算单不存在");
        }
        //查询连锁商结算单
        List<BalanceProvider> balanceProvs = balanceProviderService.queryByBatchNo(b.getBatchNo());
        if(balanceProvs == null || balanceProvs.size() ==0){
            throw new SystemException("结算单不存在");
        }
        
        for(BalanceProvider bp : balanceProvs) {
            orderService.deleteBalanceStatus(bp.getBalanceNo());
            balanceProviderService.delete(bp.getId());
        }
        getDao().delete(b.getId());
    }
    
    /**
     * 制作结算单
     * 1、生成结算单批次号
     * 2、查询需要结算的连锁商
     * 3、根据指定时间段查询需要结算的订单
     * 4、生成结算单
     * 5、更多订单结算状态计算结算金额
     * @param balance
     * @param amountPrice
     * @param su
     * @throws Exception
     * @CreateDate: 2016-10-19 上午1:02:35
     */
    @Transaction
    public void saveBalance(Balance balance, String amountPriceCheck, SessionUser su) throws Exception{
        //结算单创建时间
        Date createTime = new Date();
        //生成结算单批次号
        String batchNo = NOUtil.getNo("BT-");
        balance.setBatchNo(batchNo);
        balance.setBalanceType(1);
        balance.setStatus(0);
        balance.setCreateUserid(su.getUserId());
        balance.setCreateTime(createTime);
        
        //结算开始时间
        String startTime = balance.getBalanceBeginTime();
        //结算结束时间
        String endTime = balance.getBalanceEndTime();
        //查询需要结算的连锁商
        List<Provider> providerList = providerService.queryProviderForBalance(startTime, endTime);
        if(providerList == null || providerList.size() == 0){
            throw new SystemException("不存在需要结算的连锁商，请确认结算时间");
        }
        
        
        //本次结算总金额
        BigDecimal amountPrice = new BigDecimal(0);
        //结算金额 = 结算总金额 * (1 - 结算比例)
        BigDecimal amountBalancePrice = new BigDecimal(0);
        //盈利金额 = 结算总金额 - 结算金额 (不使用 [结算总金额 * 结算比例] 计算，防止差一分钱) 
        BigDecimal amountProfitPrice = new BigDecimal(0);
        
        int providerCount = 0;
        //int shopCode = 0;
        int orderCount = 0;
        for(Provider provider : providerList){
            //根据指定时间段查询需要结算的订单
            List<Order> orderList = orderService.queryOrderForBalance(startTime, endTime, provider.getCode());
            if(orderList == null || orderList.size() == 0){
                continue;
            }

            //连锁商结算总金额
            BigDecimal providerAmountPrice = new BigDecimal(0);
            //结算金额 = 结算总金额 * (1 - 结算比例)
            BigDecimal providerBalancePrice = new BigDecimal(0);
            //盈利金额 = 结算总金额 - 结算金额 (不使用 [结算总金额 * 结算比例] 计算，防止差一分钱) 
            BigDecimal providerProfitPrice = new BigDecimal(0);
            
            //生成结算单
            BalanceProvider bp = buildBalanceProvider(balance, provider);
            bp.setOrderCount(orderList.size());
            //循环处理订单
            for(Order o : orderList){
                //修改订单结算状态
                o.setBalanceNo(bp.getBalanceNo());
                o.setBalanceStatus(2);
                o.setBalanceTime(createTime);
                if(orderService.updateBalanceStatus(o) == 0){
                    throw new SystemException("结算订单异常，请确认结算时间");
                }
                //计算结算金额
                providerAmountPrice = NumberUtils.add(providerAmountPrice, o.getRealPrice());
                BigDecimal balancePrice = NumberUtils.mul(o.getRealPrice(), NumberUtils.sub(1, provider.getProportion()));
                providerBalancePrice = NumberUtils.add(providerBalancePrice, balancePrice);
                providerProfitPrice = NumberUtils.add(providerProfitPrice, NumberUtils.sub(o.getRealPrice(), balancePrice));
            }
            bp.setAmountPrice(providerAmountPrice);
            bp.setBalancePrice(providerBalancePrice);
            bp.setProfitPrice(providerProfitPrice);
            //保存结算单
            balanceProviderService.add(bp);
            
            //计算本次结算金额
            amountPrice = NumberUtils.add(amountPrice, providerAmountPrice);
            amountBalancePrice = NumberUtils.add(amountBalancePrice, providerBalancePrice);
            amountProfitPrice = NumberUtils.add(amountProfitPrice, providerProfitPrice);
            
            orderCount += bp.getOrderCount();
            providerCount++;
        }
        
        //保存结算批次
        balance.setAmountPrice(amountPrice);
        balance.setBalancePrice(amountBalancePrice);
        balance.setProfitPrice(amountProfitPrice);
        balance.setProviderCount(providerCount);
        balance.setOrderCount(orderCount);
        getDao().add(balance);
    }

    /**
     * 拼装连锁商结算单
     * @param balance
     * @param provider
     * @author: lijx
     * @CreateDate: 2016-10-20 下午8:07:52
     */
    private BalanceProvider buildBalanceProvider(Balance balance, Provider provider) {
        BalanceProvider bp = new BalanceProvider();
        bp.setBatchNo(balance.getBatchNo());
        bp.setBatchName(balance.getBatchName());
        bp.setBalanceNo(NOUtil.getNo("BA-"));
        bp.setBalanceName(balance.getBatchName() + " -- " + provider.getName());
        bp.setProviderCode(provider.getCode());
        bp.setProviderName(provider.getName());
        bp.setBalanceBeginTime(balance.getBalanceBeginTime());
        bp.setBalanceEndTime(balance.getBalanceEndTime());
        bp.setProportion(provider.getProportion());
        bp.setAccountBank(provider.getAccountBank());
        bp.setAccountBankBranch(provider.getAccountBankBranch());
        bp.setAccountName(provider.getAccountName());
        bp.setAccountNumber(provider.getAccountNumber());
        bp.setBalanceType(1);
        bp.setStatus(0);
        bp.setCreateUserid(balance.getCreateUserid());
        bp.setCreateTime(balance.getCreateTime());
        return bp;
    }
    
}