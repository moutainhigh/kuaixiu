package com.kuaixiu.card.dao;

import com.common.base.dao.BaseDao;
import com.kuaixiu.card.entity.TelecomCard;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface TelecomCardMapper<T> extends BaseDao<T>{

    /**
     * 批量添加卡号
     * @param list
     * @return
     */
    @Transactional
    int addBatch(List<TelecomCard> list);

    /**
     * 根据批次号起始截止iccid批量修改号卡状态
     * @param t
     * @return
     */
    @Transactional
    int updateByStartIccid(T t);


    /**
     * 查询当天更新未成功推送的号卡  0点开始
     * @param queryStartTime
     * @return
     */
    List<T> queryByTime(String queryStartTime);


    /**
     * 查询未推送成功的号卡
     * @return
     */
    List<T> queryPushFail();


    List<Map<String,Object>> queryListTwoForPage(T t);
    /**
     * 联合查询列表 带分页
     * @param t
     * @return
     */
    List<Map<String,Object>> queryTelecomList(T t);
    List<T> queryTelecomListForPage(T t);

}