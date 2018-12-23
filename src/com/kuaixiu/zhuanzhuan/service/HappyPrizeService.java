package com.kuaixiu.zhuanzhuan.service;

import com.common.base.service.BaseService;
import com.kuaixiu.zhuanzhuan.dao.HappyPrizeMapper;
import com.kuaixiu.zhuanzhuan.entity.HappyPrize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Auther: anson
 * @Date: 2018/7/17
 * @Description:
 */
@Service
public class HappyPrizeService extends BaseService<HappyPrize> {

    @Autowired
    private HappyPrizeMapper<HappyPrize>  mapper;

    @Override
    public HappyPrizeMapper<HappyPrize> getDao() {
        return mapper;
    }


    /**
     * 通过抽奖号码查询
     * @param mobile
     * @return
     */
    public HappyPrize queryByMobile(String mobile){
          return mapper.queryByMobile(mobile);
    }

}
