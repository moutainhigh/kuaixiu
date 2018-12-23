package com.kuaixiu.apiService;

import java.util.Map;

import com.common.wechat.common.util.StringUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.common.exception.ApiServiceException;
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
 *
 * @author wugl
 */
@Service("orderAgreedTimeApiService")
public class OrderAgreedTimeApiService implements ApiServiceInf {
    private static final Logger log = Logger.getLogger(OrderAgreedTimeApiService.class);

    @Autowired
    private OrderService orderService;
    @Autowired
    private EngineerService engService;

    @Override
    @Transactional
    public Object process(Map<String, String> params) {

        //解析请求参数
        String paramJson = MapUtils.getString(params, "params");
        JSONObject pmJson = JSONObject.parseObject(paramJson);

        //验证请求参数
        if (pmJson == null
                || !pmJson.containsKey("orderNo")
                || !pmJson.containsKey("agreedTime")) {
            throw new ApiServiceException(ApiResultConstant.resultCode_1001, ApiResultConstant.resultCode_str_1001);
        }

        //获取订单号
        String orderNo = pmJson.getString("orderNo");
        //获取操作类型
        String agreedTime = pmJson.getString("agreedTime");
        String engNote = pmJson.getString("engNote");
        String engNumber = MapUtils.getString(params, "pmClientId");

        //查询订单信息
        Order o = orderService.queryByOrderNo(orderNo);
        if (o == null) {
            throw new ApiServiceException(ApiResultConstant.resultCode_1003, "订单信息未找到");
        }

        if (!engNumber.equals(o.getEngineerNumber())) {
            throw new ApiServiceException(ApiResultConstant.resultCode_3002, "对不起，您无权操作该订单");
        }

        if (o.getOrderStatus() == OrderConstant.ORDER_STATUS_CANCEL) {
            throw new ApiServiceException(ApiResultConstant.resultCode_3002, "该订单已取消！");
        }
        //预约成功仍可以继续预约
//        if(o.getOrderStatus() != OrderConstant.ORDER_STATUS_DISPATCHED){
//            throw new ApiServiceException(ApiResultConstant.resultCode_3002, "该订单已确认，无需重复操作！");
//        }

        //维修工程师改为空闲状态
        Engineer eng = engService.queryByEngineerNumber(engNumber);
        SessionUser su = new SessionUser();
        su.setUserId(eng.getNumber());
        su.setUserName(eng.getName());
        su.setType(SystemConstant.USER_TYPE_ENGINEER);
        if (StringUtils.isNotBlank(engNote)) {
            if (engNote.length() <= 250) {
                o.setEngNote(engNote);
            } else {
                throw new ApiServiceException(ApiResultConstant.resultCode_3002, "备注仅限250字！");
            }
        } else {
            o.setEngNote(engNote);
        }
        o.setUpdateUserid(engNumber);
        o.setAgreedTime(agreedTime);
        o.setOrderStatus(OrderConstant.ORDER_STATUS_RECEIVED);
        orderService.saveUpdate(o);
        return "OK";
    }

}
