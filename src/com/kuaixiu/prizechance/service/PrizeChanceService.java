package com.kuaixiu.prizechance.service;


import com.common.base.service.BaseService;
import com.kuaixiu.prizechance.dao.PrizeChanceMapper;
import com.kuaixiu.prizechance.entity.PrizeChance;

import org.apache.ibatis.annotations.Param;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * PrizeChance Service
 * @CreateDate: 2019-11-07 下午05:21:23
 * @version: V 1.0
 */
@Service("prizeChanceService")
public class PrizeChanceService extends BaseService<PrizeChance> {
    private static final Logger log= Logger.getLogger(PrizeChanceService.class);

    @Autowired
    private PrizeChanceMapper<PrizeChance> mapper;

    @Override
    public PrizeChanceMapper<PrizeChance> getDao() {
        return mapper;
    }

    //**********自定义方法***********
    /**
     * 添加抽奖机会次数
     * @param userMobile
     * @return
     */
    public int  updateCountByMobile(String userMobile){
        return mapper.updateCountByMobile(userMobile);
    }
    /**
     * 减少抽奖机会次数
     * @param userMobile
     * @return
     */
    public int  updateCountByReduce(String userMobile){
        return mapper.updateCountByReduce(userMobile);
    }

    public int  updateCountByMobileType(String userMobile,String type){
        return mapper.updateCountByMobileType(userMobile,type);
    }

    public int  insertCountByMobile(PrizeChance prizeChance){
        return mapper.insertCountByMobile(prizeChance);
    }

    /**
     * 查询抽奖次数
     * @param userMobile
     * @return
     */
    public Integer queryByMobile(String userMobile){
        return mapper.queryByMobile(userMobile);
    }
    public PrizeChance queryIDByMobile(String userMobile){
        return mapper.queryIDByMobile(userMobile);
    }


    public Integer queryByMobileAndType(String userMobile,String type){
        return mapper.queryByMobileAndType(userMobile,type);
    }

    public int addBytype(PrizeChance prizeChance){
        return mapper.addBytype(prizeChance);
    }

    public int updateBytype(PrizeChance prizeChance){
        return mapper.updateBytype(prizeChance);
    }



}