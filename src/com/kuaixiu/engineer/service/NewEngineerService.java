package com.kuaixiu.engineer.service;


import com.common.base.service.BaseService;
import com.kuaixiu.engineer.dao.NewEngineerMapper;
import com.kuaixiu.engineer.entity.Engineer;
import com.kuaixiu.shop.entity.Shop;
import com.kuaixiu.shop.service.ShopService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * NewEngineer Service
 *
 * @CreateDate: 2018-10-30 上午11:12:05
 * @version: V 1.0
 */
@Service("newEngineerService")
public class NewEngineerService extends BaseService<Engineer> {
    private static final Logger log = Logger.getLogger(NewEngineerService.class);

    @Autowired
    private NewEngineerMapper<Engineer> mapper;
    @Autowired
    private ShopService shopService;
    @Autowired
    private EngineerService engineerService;

    public NewEngineerMapper<Engineer> getDao() {
        return mapper;
    }

    //**********自定义方法***********

    /**
     * 根据门店查询空闲工程师
     *
     * @return
     * @CreateDate: 2016-9-4 下午4:21:29
     */
    public Engineer queryUnDispatchByShopCode(String shopCode) {
        return queryUnDispatchByShopCode(shopCode, null);
    }

    /**
     * 根据门店查询空闲工程师
     * @return
     * @CreateDate: 2016-9-4 下午4:21:29
     */
    public Engineer queryUnDispatchByShopCode(String shopCode, String notEngId) {
        List<Engineer> list = getDao().queryUnDispatchByShopCode(shopCode, notEngId);
        return list != null && list.size() > 0 ? list.get(0) : null;
    }

    /**
     * 根据门店查询忙碌工程师
     * @return
     */
    public Engineer queryDispatchByShopCode(String shopCode, String notEngId) {
        List<Engineer> list = getDao().queryDispatchByShopCode(shopCode, notEngId);
        return list != null && list.size() > 0 ? list.get(0) : null;
    }

    /**
     * 根据门店查询空闲工程师个数
     *
     * @return
     * @CreateDate: 2016-9-4 下午4:21:29
     */
    public int queryUnDispatchCount(String shopCode) {
        Engineer t = new Engineer();
        t.setShopCode(shopCode);
        t.setIsDispatch(0);
        return getDao().queryCount(t);
    }

    //如果工程师门店账号是字符串分割
    //获取工程师姓名，工程师工号和名称转化，
    public Engineer engineerShopCode(Engineer engineer) {
        if(engineer.getShopCode()==null){
            return engineer;
        }
        if (engineer.getShopCode().contains(",")) {
            List<String> shopCodeList = Arrays.asList(engineer.getShopCode().split(","));
            List<String> shopCodeListCODE = new ArrayList<>(shopCodeList);
            StringBuilder sb = new StringBuilder();
            for (String shopCodeList1 : shopCodeList) {
                Shop shop = shopService.queryByCode(shopCodeList1);
                //如果没有此门店。要把工程师的门店号删除
                if (shop == null) {
                    shopCodeListCODE.remove(shopCodeList1);
                    StringBuffer sb1 = new StringBuffer();
                    for (int i = 0; i < shopCodeListCODE.size(); i++) {
                        sb1.append(shopCodeListCODE.get(i));
                        if ((i + 1) != shopCodeListCODE.size()) {
                            sb1.append(",");
                        }
                    }
                    engineer.setShopCode(sb1.toString());
                    engineerService.saveUpdate(engineer);
                } else {
                    sb.append(shop.getName() + ",");
                }
            }
            engineer.setShopName(sb.toString());
        } else {
            Shop shop = shopService.queryByCode(engineer.getShopCode());
            if (shop == null) {
                engineer.setShopCode(null);
                engineerService.saveUpdate(engineer);
            } else {
                engineer.setShopName(shop.getName());
            }
        }
        return engineer;
    }
}