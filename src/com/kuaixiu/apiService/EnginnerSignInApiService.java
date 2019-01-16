package com.kuaixiu.apiService;

import com.alibaba.fastjson.JSONObject;
import com.common.exception.ApiServiceException;
import com.kuaixiu.engineer.entity.EngineerSignIn;
import com.kuaixiu.engineer.service.EngineerSignInService;
import com.kuaixiu.order.entity.Order;
import com.kuaixiu.order.entity.ReworkOrder;
import com.kuaixiu.order.service.OrderService;
import com.kuaixiu.order.service.ReworkOrderService;
import com.system.api.ApiServiceInf;
import com.system.constant.ApiResultConstant;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;


/**
 * 工程师操作接口实现类
 *
 * @author wugl
 */
@Service("enginnerSignInApiService")
public class EnginnerSignInApiService implements ApiServiceInf {
    private static final Logger log = Logger.getLogger(EnginnerSignInApiService.class);

    @Autowired
    private EngineerSignInService engineerSignInService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ReworkOrderService reworkOrderService;

    @Override
    public Object process(Map<String, String> params) {
        Object json = new Object();
        try {

            //获取工程师工号和密码
            String number = MapUtils.getString(params, "pmClientId");
            //解析请求参数
            String paramJson = MapUtils.getString(params, "params");
            JSONObject pmJson = JSONObject.parseObject(paramJson);
            //验证请求参数
            isParamNull(pmJson);
            //保存数据
            add(pmJson, number);
            json = "OK";
        } catch (Exception e) {
            e.printStackTrace();
            json = "NO";
        }
        return json;
    }

    //验证参数
    private void isParamNull(JSONObject pmJson) throws Exception {
        if (pmJson == null
                || !pmJson.containsKey("orderNo")
                || !pmJson.containsKey("province")
                || !pmJson.containsKey("city")
                || !pmJson.containsKey("area")
                || !pmJson.containsKey("address")
                || !pmJson.containsKey("longitude")
                || !pmJson.containsKey("latitude")
                || !pmJson.containsKey("isRework")
                || pmJson.getString("orderNo") == null
                || pmJson.getString("province") == null
                || pmJson.getString("city") == null
                || pmJson.getString("area") == null
                || pmJson.getString("address") == null
                || pmJson.getString("longitude") == null
                || pmJson.getString("latitude") == null
                || pmJson.getString("isRework") == null) {
            throw new ApiServiceException(ApiResultConstant.resultCode_1001, ApiResultConstant.resultCode_str_1001);
        }
    }

    //保存数据
    private void add(JSONObject pmJson, String number) throws Exception {
        String orderNo = pmJson.getString("orderNo");
        Integer isRework = pmJson.getInteger("isRework");
        if (1 == isRework) {
            ReworkOrder reworkOrder = reworkOrderService.getDao().queryByReworkNo(orderNo);
            if (reworkOrder == null) {
                throw new ApiServiceException(ApiResultConstant.resultCode_1003, ApiResultConstant.resultCode_str_1003);
            }
        } else {
            Order order = orderService.queryByOrderNo(orderNo);
            if (order == null) {
                throw new ApiServiceException(ApiResultConstant.resultCode_1003, ApiResultConstant.resultCode_str_1003);
            }
        }
        String province = pmJson.getString("province");
        String city = pmJson.getString("city");
        String area = pmJson.getString("area");
        String address = pmJson.getString("address");
        Double longitude = pmJson.getDouble("longitude");
        Double latitude = pmJson.getDouble("latitude");
        EngineerSignIn signIn = new EngineerSignIn();
        signIn.setId(UUID.randomUUID().toString().replace("-", ""));
        signIn.setEngineerNo(number);
        signIn.setOrderNo(orderNo);
        signIn.setProvince(province);
        signIn.setCity(city);
        signIn.setCounty(area);
        signIn.setAddress(address);
        signIn.setLongitude(new BigDecimal(Double.toString(longitude)));
        signIn.setLatitude(new BigDecimal(Double.toString(latitude)));
        String areas = province + " " + city + " " + area;
        signIn.setAreas(areas);
        engineerSignInService.add(signIn);
    }

}
