package com.kuaixiu.card.service;

import com.common.base.service.BaseService;
import com.kuaixiu.card.dao.TelecomCardMapper;
import com.kuaixiu.card.entity.TelecomCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Auther: anson
 * @Date: 2018/7/25
 * @Description:
 */
@Service
public class TelecomCardService extends BaseService<TelecomCard> {

    @Autowired
    private TelecomCardMapper<TelecomCard> mapper;

    @Override
    public TelecomCardMapper<TelecomCard> getDao() {
        return mapper;
    }


    /**
     * 批量添加卡号
     * @param list
     * @return
     */
    @Transactional
    public int addBatch(List<TelecomCard> list){
       return  mapper.addBatch(list);
    }


    @Transactional
    public int updateByStartIccid(TelecomCard t){
        return mapper.updateByStartIccid(t);
    }

    /**
     * 查询当天更新未成功推送的号卡  0点开始
     * @param queryStartTime
     * @return
     */
    public List<TelecomCard> queryByTime(String queryStartTime){
          return mapper.queryByTime(queryStartTime);
    }


    /**
     * 查询未推送成功的号卡
     * @return
     */
    public List<TelecomCard> queryPushFail(){
        return mapper.queryPushFail();
    }


    /**
     * 联合查询列表 带分页
     * @param t
     * @return
     */
    public List<TelecomCard> queryTelecomListForPage(TelecomCard t){
        return mapper.queryTelecomListForPage(t);
    }
}
