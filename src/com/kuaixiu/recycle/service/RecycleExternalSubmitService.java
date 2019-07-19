package com.kuaixiu.recycle.service;


import com.common.base.service.BaseService;
import com.common.exception.SystemException;
import com.kuaixiu.recycle.dao.RecycleExternalSubmitMapper;
import com.kuaixiu.recycle.entity.RecycleExternalSubmit;
import com.kuaixiu.recycle.entity.RecycleExternalTest;
import com.system.basic.address.entity.Address;
import com.system.basic.address.service.AddressService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * RecycleExternalSubmit Service
 *
 * @CreateDate: 2018-09-29 上午09:23:19
 * @version: V 1.0
 */
@Service("recycleExternalSubmitService")
public class RecycleExternalSubmitService extends BaseService<RecycleExternalSubmit> {
    private static final Logger log = Logger.getLogger(RecycleExternalSubmitService.class);

    @Autowired
    private RecycleExternalSubmitMapper<RecycleExternalSubmit> mapper;
    @Autowired
    private AddressService addressService;
    @Autowired
    private RecycleExternalTestService recycleExternalTestService;

    public RecycleExternalSubmitMapper<RecycleExternalSubmit> getDao() {
        return mapper;
    }

    //**********自定义方法***********

    /**
     * 保存信息
     *
     * @param submit
     * @return
     */
    public RecycleExternalTest add(RecycleExternalSubmit submit, String imagePath) throws Exception {
        submit.setId(UUID.randomUUID().toString().replace("-", "-"));
        getDao().add(submit);
        RecycleExternalTest test = new RecycleExternalTest();
        test.setToken(submit.getToken());
        //查询要提交的最新测评信息
        List<RecycleExternalTest> tests = recycleExternalTestService.getDao().queryList(test);
        if (!CollectionUtils.isEmpty(tests)) {
            tests.get(0).setImagePath(imagePath);
            tests.get(0).setIsOrder("1");
            //测评表修改为已成单
            recycleExternalTestService.saveUpdate(tests.get(0));
            submit.setTestId(tests.get(0).getId());
            submit.setTestPrice(tests.get(0).getTestPrice());
            getDao().update(submit);//保存测评价格，关联成单信息
            return tests.get(0);
        }
        return null;
    }

    /**
     * 判断下单间隔
     *
     * @param request
     * @return
     */
    public Boolean timeVerification(HttpServletRequest request) {

        //设置下单间隔时间,最少3秒
        Long time = System.currentTimeMillis();
        Object requestTimes = request.getSession().getAttribute("times");
        if (requestTimes == null) {
            request.getSession().setAttribute("times", time);
        } else {
            long realTime = (long) (requestTimes);
            if ((time - realTime) < 3000) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取地址转化
     *
     * @param province
     * @param city
     * @param area
     * @return
     */
    public String getProvince(String province, String city, String area) {
        //转化地址
        Address provinceName = addressService.queryByAreaId(province);
        Address cityName = addressService.queryByAreaId(city);
        Address areaName = addressService.queryByAreaId(area);
        if (provinceName == null || cityName == null || areaName == null) {
            throw new SystemException("请确认地址信息是否无误");
        }
        return getProvince(provinceName.getArea()) + cityName.getArea() + " " + areaName.getArea();
    }

    /**
     * 超人系统地址为 上海市 黄浦区 城区    -----  浙江 杭州市 江干区
     * 回收地址规范     上海市 黄浦区            ------ 浙江省 杭州市 江干区
     * 省份区别  西藏 宁夏 新疆 广西 内蒙古
     * 北京市  天津市 上海市  重庆市 其他省后面都加省
     *
     * @param code
     * @return 超人地址转化回收地址规范
     */
    public static String getProvince(String code) {
        List<String> s = new ArrayList<String>();
        List<String> p = new ArrayList<String>();
        String[] plist = {"西藏", "宁夏", "新疆", "广西", "内蒙古"};
        String[] slist = {"北京", "天津", "上海", "重庆"};
        s.addAll(Arrays.asList(plist));
        p.addAll(Arrays.asList(slist));
        if (s.contains(code)) {
            //不用更改
        } else if (p.contains(code)) {
            code += "市";
        } else {
            code += "省";
        }
        return code;
    }

}