package com.kuaixiu.order.controller;

import com.common.base.controller.BaseController;
import com.common.wechat.common.util.StringUtils;
import com.kuaixiu.coupon.entity.Coupon;
import com.kuaixiu.coupon.service.CouponService;
import com.kuaixiu.order.entity.Order;
import com.kuaixiu.order.entity.OrderRecord;
import com.kuaixiu.order.service.OrderRecordService;
import com.kuaixiu.order.service.OrderService;
import com.kuaixiu.wechat.service.WechatUserService;
import com.system.api.entity.ResultData;
import com.system.basic.user.entity.SessionUser;
import com.system.constant.SystemConstant;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * OrderRecord Controller
 *
 * @author najy
 * @CreateDate: 2019-03-29 下午03:38:02
 * @version: V 1.0
 */
@Controller
public class OrderRecordController extends BaseController {
    private static final Logger log = Logger.getLogger(OrderRecordController.class);

    @Autowired
    private OrderRecordService orderRecordService;
    @Autowired
    private WechatUserService wechatUserService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private CouponService couponService;

    /**
     * 维修完成订单回访
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/order/record")
    @ResponseBody
    public ResultData record(HttpServletRequest request,
                             HttpServletResponse response) {
        ResultData resultData = new ResultData();
        try {
            String orderId = request.getParameter("orderId");
            String couponType = request.getParameter("couponType");
            String note = request.getParameter("note");
            if (StringUtils.isBlank(orderId) || StringUtils.isBlank(couponType) || StringUtils.isBlank(note)) {
                return getResult(resultData, null, false, "2", "参数不能为空");
            }
            Order order = orderService.queryById(orderId);
            if (order == null) {
                return getResult(resultData, null, false, "2", "参数错误");
            }
            SessionUser su = getCurrentUser(request);
            //发送优惠券
            String code = sendCoupon(Integer.valueOf(couponType), order.getMobile(), su.getUserId());

            //创建订单回访
            OrderRecord orderRecord = new OrderRecord();
            orderRecord.setOrderNo(order.getOrderNo());
            orderRecord.setRecordName(su.getUserId());
            orderRecord.setCouponType(Integer.valueOf(couponType));
            orderRecord.setNote(note);
            orderRecord.setCouponCode(code);
            orderRecordService.add(orderRecord);
            order.setIsRecord(1);
            orderService.saveUpdate(order);

            getResult(resultData, null, true, "0", "成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return resultData;
    }

    private String sendCoupon(Integer couponType, String mobile, String userId) throws Exception {
        String batchId = "";
        String price = "";
        String projectName = "";
        String couponName = "";
        switch (couponType){
            case 1:
                batchId = SystemConstant.RECORD_COMMON_BATCHID;
                price = SystemConstant.RECORD_COMMON_PRICE;
                projectName = null;
                couponName = "20元通用优惠券";
                break;
            case 2:
                batchId = SystemConstant.RECORD_SCREEN30_BATCHID;
                price = SystemConstant.RECORD_SCREEN30_PRICE;
                projectName = "屏幕";
                couponName = "30元屏幕优惠券";
                break;
            case 3:
                batchId = SystemConstant.RECORD_SCREEN50_BATCHID;
                price = SystemConstant.RECORD_SCREEN50_PRICE;
                projectName = "屏幕";
                couponName = "50元屏幕优惠券";
                break;
        }
        String commonCode = wechatUserService.kxCreateCoupon(batchId, price, projectName, couponName, mobile, userId);
        Coupon coupon = couponService.getDao().queryByCode(commonCode);
        couponService.kxReceiveSendSms(coupon);
        return commonCode;
    }
}
