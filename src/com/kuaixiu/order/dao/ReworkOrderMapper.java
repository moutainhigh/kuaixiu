package com.kuaixiu.order.dao;

import com.common.base.dao.BaseDao;

import java.util.List;
import java.util.Map;

/**
 * ReworkOrder Mapper
 *
 * @param <T>
 * @CreateDate: 2019-01-10 上午09:45:45
 * @version: V 1.0
 */
public interface ReworkOrderMapper<T> extends BaseDao<T> {

    //根据返修订单号查询
    T queryByReworkNo(String reworkNo);
    //根据父订单号查询未完成返修单
    List<T> queryByParentOrder(String fatherOrderNo);

    List<T> queryByOrderNo(String orderNo);
    //查询返修订单分页
    List<Map<String,String>> queryReworkListForPage(T t);
}


