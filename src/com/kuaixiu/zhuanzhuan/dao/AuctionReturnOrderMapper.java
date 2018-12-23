package com.kuaixiu.zhuanzhuan.dao;


import com.common.base.dao.BaseDao;

public interface AuctionReturnOrderMapper<T> extends BaseDao<T> {


    /**
     * 根据转转订单id查询退货订单信息
     * @param auctionOrderId
     * @return
     */
    T queryByAuctionOrderId(String auctionOrderId);


}