package com.kuaixiu.zhuanzhuan.dao;

import com.common.base.dao.BaseDao;


public interface AuctionOrderMapper<T> extends BaseDao<T> {


    /**
     * 根据转转订单id查找
     * @param auctionOrderId
     * @return
     */
    T queryByAuctionOrderId(Object auctionOrderId);

}