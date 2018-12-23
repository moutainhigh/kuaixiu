package com.kuaixiu.zhuanzhuan.dao;

import com.common.base.dao.BaseDao;

public interface HappyPrizeMapper<T> extends BaseDao<T> {

    /**
     * 通过抽奖手机号查询
     * @param mobile
     * @return
     */
    T queryByMobile(Object mobile);
}