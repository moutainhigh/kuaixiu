package com.kuaixiu.apiService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.kuaixiu.order.entity.UpdateOrderPrice;
import com.kuaixiu.order.service.UpdateOrderPriceService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.exception.ApiServiceException;
import com.common.exception.SystemException;
import com.common.util.NumberUtils;
import com.common.validator.ValidateResult;
import com.common.validator.Validator;
import com.kuaixiu.engineer.entity.Engineer;
import com.kuaixiu.engineer.service.EngineerService;
import com.kuaixiu.model.entity.RepairCost;
import com.kuaixiu.model.service.RepairCostService;
import com.kuaixiu.order.constant.OrderConstant;
import com.kuaixiu.order.entity.Order;
import com.kuaixiu.order.entity.OrderDetail;
import com.kuaixiu.order.service.OrderDetailService;
import com.kuaixiu.order.service.OrderService;
import com.system.api.ApiServiceInf;
import com.system.constant.ApiResultConstant;
import com.system.util.SystemUtil;


/**
 * 更新订单状态操作接口实现类
 *
 * @author wugl
 */
@Service("updateOrderSubjectApiService")
public class UpdateOrderSubjectApiService implements ApiServiceInf {
    private static final Logger log = Logger.getLogger(UpdateOrderSubjectApiService.class);

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDetailService detailService;
    @Autowired
    private RepairCostService repairCostService;
    @Autowired
    private EngineerService engService;
    @Autowired
    private UpdateOrderPriceService updateOrderPriceService;

    @Override
    @Transactional
    public Object process(Map<String, String> params) {

        //解析请求参数
        String paramJson = MapUtils.getString(params, "params");
        JSONObject pmJson = JSONObject.parseObject(paramJson);
        if (pmJson == null) {
            throw new ApiServiceException(ApiResultConstant.resultCode_1001, ApiResultConstant.resultCode_str_1001);
        }
        //验证请求参数
        Validator validator = new Validator((JSONArray) SystemUtil.getProperty("goodsImportVoliatorJson"));
        ValidateResult result = validator.validate(pmJson);
        if (!result.getSuccess()) {
            throw new ApiServiceException(ApiResultConstant.resultCode_1001, result.getResultMessage());
        }

        //获取订单号
        String orderNo = pmJson.getString("orderNo");
        String engNumber = MapUtils.getString(params, "pmClientId");

        //查询订单信息
        Order o = orderService.queryByOrderNo(orderNo);
        if (o == null) {
            throw new ApiServiceException(ApiResultConstant.resultCode_1003, "订单信息未找到");
        }

        //判断是否有操作权限
        if (!engNumber.equals(o.getEngineerNumber())) {
            throw new ApiServiceException(ApiResultConstant.resultCode_2001, "对不起，您无权操作该订单");
        }


        if (o.getOrderStatus() == OrderConstant.ORDER_STATUS_FINISHED) {
            throw new ApiServiceException(ApiResultConstant.resultCode_3002, "该订单已完成！");
        }

        if (o.getOrderStatus() == OrderConstant.ORDER_STATUS_CANCEL) {
            throw new ApiServiceException(ApiResultConstant.resultCode_3002, "该订单已取消！");
        }

        if (o.getOrderStatus() < OrderConstant.ORDER_STATUS_START_CHECK) {
            throw new ApiServiceException(ApiResultConstant.resultCode_3002, "订单未开始检测，请先点击开始检测");
        }

        if (o.getOrderStatus() >= OrderConstant.ORDER_STATUS_END_REPAIR) {
            throw new ApiServiceException(ApiResultConstant.resultCode_3002, "订单已完成维修，无需重复操作");
        }
        //免费贴膜（现场优惠）项目id
        String francoProject = "6298c77e-d412-11e8-932d-00163e04c890";
        //下单时的金额=实际金额+退款金额
        BigDecimal orderPrice = new BigDecimal(0);
        //获取修改后台的维修项目
        JSONArray newDetails = pmJson.getJSONArray("projects");
        //获取订单原来的维修项目
        //List<OrderDetail> details = detailService.queryByOrderNo(orderNo);
        //循环比较新增的维修项目
        for (int i = 0; i < newDetails.size(); i++) {
            JSONObject jsonPj = newDetails.getJSONObject(i);
            String projectId = jsonPj.getString("project_id");
            //获取修改后的维修费用
            //BigDecimal price = jsonPj.getBigDecimal("price");
            //orderPrice = NumberUtils.add(orderPrice, price);
            //如果存在订单明细id
//            if(jsonPj.containsKey("detail_id") && StringUtils.isNotBlank(jsonPj.getString("detail_id"))){
//                String detailId = jsonPj.getString("detail_id");
//                OrderDetail old = null;
//                //查找订单明细
//                for(OrderDetail od : details){
//                    if(od.getId().equals(detailId)){
//                        old = od;
//                        break;
//                    }
//                }
//                //判断是否找到订单id
//                if(old == null){
//                    throw new ApiServiceException(ApiResultConstant.resultCode_1003, "订单明细未找到");
//                }
//                //更新订单明细
//                old.setRealPrice(price);
//                old.setUpdateUserid(engNumber);
//                //判断是否更换维修项目
//                if(!old.getProjectId().equals(projectId)){
//                    //查询维修维修费用
//                    RepairCost cost = repairCostService.queryByMidAndPid(o.getModelId(), projectId);
//                    old.setProjectId(projectId);
//                    old.setProjectName(cost.getProjectName());
//                }
//                detailService.saveUpdate(old);
//            }
//            else{
            //新增订单明细
            OrderDetail od = new OrderDetail();
            od.setOrderNo(orderNo);
            od.setProjectId(projectId);
            //处理其它维修费用
            if ("zzpjqt".equals(projectId)) {
                od.setProjectName("其它");
                BigDecimal price = jsonPj.getBigDecimal("price");
                od.setPrice(price);
                od.setRealPrice(price);
                orderPrice = NumberUtils.add(orderPrice, price);
            } else {
                //查询维修维修费用
                RepairCost cost = repairCostService.queryByMidAndPid(o.getModelId(), projectId);
                if (cost == null) {
                    throw new ApiServiceException(ApiResultConstant.resultCode_1003, "维修项目未找到");
                }
                od.setProjectName(cost.getProjectName());
                if (newDetails.size() == 1 && francoProject.equals(projectId)) {
                    francoProject="50";
                    od.setPrice(new BigDecimal(0));
                    od.setRealPrice(new BigDecimal(0));
                } else {
                    od.setPrice(cost.getPrice());
                    od.setRealPrice(cost.getPrice());
                    orderPrice = NumberUtils.add(orderPrice, cost.getPrice());
                }
            }
            od.setCreateUserid(engNumber);
            od.setType(1);
            od.setIsDel(0);
            detailService.add(od);
            //}
        }
//        //循环比较删除的维修项目
//        for(OrderDetail od : details){
//            //循环比较新增的维修项目
//            boolean exists = false;
//            for(int i = 0; i < newDetails.size(); i++){
//                JSONObject jsonPj = newDetails.getJSONObject(i);
//                //如果存在订单明细id
//                if(jsonPj.containsKey("detail_id") 
//                        && StringUtils.isNotBlank(jsonPj.getString("detail_id"))
//                        && od.getId().equals(jsonPj.getString("detail_id"))){
//                    exists = true;
//                    break;
//                }
//            }
//            //如果不存在订单明细id则删除
//            if(!exists){
//                od.setIsDel(1);
//                od.setUpdateUserid(engNumber);
//                detailService.saveUpdate(od);
//            }
//        }

        //判断订单明细汇总价格是否等于订单价格
//        if(orderPrice.compareTo(pmJson.getBigDecimal("price")) != 0){
//            throw new ApiServiceException(ApiResultConstant.resultCode_1003, "订单金额不一致");
//        }

        List<UpdateOrderPrice> updateOrderPrices=updateOrderPriceService.getDao().queryByUpOrderNo(o.getOrderNo());
        //未经管理员修改过金额的订单工程师才可以操作
        Boolean isZero=false;
        if(CollectionUtils.isEmpty(updateOrderPrices)){
            o.setRealPrice(orderPrice);
            if(o.getRealPriceSubCoupon().doubleValue()==0){
                isZero=true;
            }
        }
        o.setEndCheckTime(new Date());
        o.setRepairType(pmJson.getInteger("repair_type"));
        o.setUpdateUserid(engNumber);
        //o.setOrderStatus(OrderConstant.ORDER_STATUS_END_CHECK);
        if ("50".equals(francoProject)||isZero) {
            o.setOrderStatus(OrderConstant.ORDER_STATUS_FINISHED);
            o.setEndTime(new Date());
            //维修工程师改为空闲状态
            Engineer eng = engService.queryByEngineerNumber(engNumber);
            //维修工程师改为空闲状态
            if(eng.getIsDispatch() == 1){
                eng.setIsDispatch(0);
                engService.saveUpdate(eng);
            }
        } else {
            o.setOrderStatus(OrderConstant.ORDER_STATUS_END_REPAIR);
        }
        o.setEndRepairTime(new Date());

        orderService.saveUpdate(o);
        engService.checkDispatchState(o.getEngineerId());
        return "OK";
    }

}
