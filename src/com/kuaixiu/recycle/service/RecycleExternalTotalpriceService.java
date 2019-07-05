package com.kuaixiu.recycle.service;


import com.common.base.service.BaseService;
import com.common.util.DateUtil;
import com.kuaixiu.recycle.dao.RecycleExternalTotalpriceMapper;
import com.kuaixiu.recycle.entity.RecycleExternalTest;
import com.kuaixiu.recycle.entity.RecycleExternalTotalprice;
import com.system.constant.SystemConstant;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.UUID;

/**
 * RecycleExternalTotalprice Service
 *
 * @CreateDate: 2018-09-29 上午09:18:51
 * @version: V 1.0
 */
@Service("recycleExternalTotalpriceService")
public class RecycleExternalTotalpriceService extends BaseService<RecycleExternalTotalprice> {
    private static final Logger log = Logger.getLogger(RecycleExternalTotalpriceService.class);

    //每单金额
    private static final String basePrice = SystemConstant.RECYCLE_BASEPRICE;
    //多少单一次计费
    private static final String baseNumber = SystemConstant.RECYCLE_BASENUMBER;

    @Autowired
    private RecycleExternalTotalpriceMapper<RecycleExternalTotalprice> mapper;
    @Autowired
    private RecycleExternalTestService recycleExternalTestService;

    public RecycleExternalTotalpriceMapper<RecycleExternalTotalprice> getDao() {
        return mapper;
    }

    //**********自定义方法***********

    /**
     * 保存每天总金额
     */
    public void add() {
        RecycleExternalTest test = new RecycleExternalTest();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startTime = sdf.format(DateUtil.getYesterdayStartTime());
        //取昨天结束时间
        String endTime = sdf.format(DateUtil.getYesterdayEndTime());
        test.setQueryStartTime(startTime);
        test.setQueryEndTime(endTime);
        //查询当天所有测评数量
        Integer number = recycleExternalTestService.getDao().queryCount(test);
        String price = calculation(number);
        RecycleExternalTotalprice totalprice = new RecycleExternalTotalprice();
        totalprice.setTotalPrice(new BigDecimal(price));
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        totalprice.setIntTime(sdf.format(DateUtil.getYesterdayStartTime()));
        totalprice.setId(UUID.randomUUID().toString().replace("-", "-"));
        getDao().add(totalprice);
    }

    /**
     * 计算总金额
     *
     * @param size
     */
    private String calculation(int size) {
        Integer intNumber = Integer.valueOf(baseNumber);
        if (size < intNumber && size != 0) {
            return "0.5";
        }
        int num = size / intNumber == 0 ? size / intNumber : size / intNumber + 1;
        Double price = Double.valueOf(basePrice) * num;
        return String.valueOf(price);
    }

}