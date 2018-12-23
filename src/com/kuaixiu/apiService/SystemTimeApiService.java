package com.kuaixiu.apiService;

import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.common.exception.ApiServiceException;
import com.common.exception.SystemException;
import com.common.util.DateUtil;
import com.kuaixiu.engineer.entity.Engineer;
import com.kuaixiu.engineer.service.EngineerService;
import com.kuaixiu.order.constant.OrderConstant;
import com.kuaixiu.order.entity.Order;
import com.kuaixiu.order.service.OrderService;
import com.system.api.ApiServiceInf;
import com.system.basic.user.entity.SessionUser;
import com.system.constant.ApiResultConstant;
import com.system.constant.SystemConstant;


/**
 * 更新订单状态操作接口实现类
 * @author wugl
 *
 */
@Service("systemTimeApiService")
public class SystemTimeApiService implements ApiServiceInf {
    private static final Logger log= Logger.getLogger(SystemTimeApiService.class);
    
    @Autowired
    private OrderService orderService;
    @Autowired
    private EngineerService engService;
    
    @Override
    @Transactional
    public Object process(Map<String, String> params) {
        
        //解析请求参数
        return DateUtil.getNowyyyyMMddHHmmss();
    }

}
