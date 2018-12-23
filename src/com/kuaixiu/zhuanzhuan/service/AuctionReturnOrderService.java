package com.kuaixiu.zhuanzhuan.service;

import com.common.base.dao.BaseDao;
import com.common.base.service.BaseService;
import com.kuaixiu.zhuanzhuan.dao.AuctionReturnOrderMapper;
import com.kuaixiu.zhuanzhuan.entity.AuctionOrder;
import com.kuaixiu.zhuanzhuan.entity.AuctionReturnOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Auther: anson
 * @Date: 2018/6/29
 * @Description:
 */
@Service("auctionReturnOrderService")
public class AuctionReturnOrderService extends BaseService<AuctionReturnOrder> {


    @Autowired
    private AuctionReturnOrderMapper<AuctionReturnOrder> mapper;

    @Override
    public AuctionReturnOrderMapper<AuctionReturnOrder> getDao() {
        return mapper;
    }

    /**
     * 根据转转订单查询退货信息
     * @param auctionOrderId
     * @return
     */
    public AuctionReturnOrder queryByAuctionOrderId(String auctionOrderId){
        return mapper.queryByAuctionOrderId(auctionOrderId);
    }

}
