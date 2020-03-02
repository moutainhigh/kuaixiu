package com.kuaixiu.prizechance.dao;

import com.common.base.dao.BaseDao;
import com.kuaixiu.prizechance.entity.PrizeChance;
import org.apache.ibatis.annotations.Param;

/**
 * PrizeChance Mapper
 *
 * @param <T>
 * @CreateDate: 2019-11-07 下午05:21:23
 * @version: V 1.0
 */
public interface PrizeChanceMapper<T> extends BaseDao<T> {

    /**,@Param("type")String type
     * 添加抽奖机会次数
     * @param userMobile
     * @return
     */
    int  updateCountByMobile(@Param("userMobile") String userMobile);

    int  updateCountByReduce(@Param("userMobile") String userMobile);

    int  updateCountByMobileType(@Param("userMobile") String userMobile, @Param("type") String type);

    int  insertCountByMobile(PrizeChance prizeChance);
    /**
     * 查询抽奖次数
     * @param userMobile
     * @return
     */
    Integer queryByMobile(@Param("userMobile") String userMobile);


    PrizeChance queryIDByMobile(@Param("userMobile") String userMobile);

    Integer queryByMobileAndType(@Param("userMobile") String userMobile, @Param("type") String type);

    int addBytype(PrizeChance prizeChance);

    int updateBytype(PrizeChance prizeChance);

}


