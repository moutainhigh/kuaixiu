package com.kuaixiu.increaseOrder.controller;

import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.exception.SystemException;
import com.common.paginate.Page;
import com.common.util.DateUtil;
import com.common.wechat.common.util.StringUtils;
import com.kuaixiu.increaseOrder.entity.IncreaseOrder;
import com.kuaixiu.increaseOrder.entity.IncreaseRecord;
import com.kuaixiu.increaseOrder.service.IncreaseOrderService;
import com.kuaixiu.increaseOrder.service.IncreaseRecordService;
import com.kuaixiu.order.entity.Order;
import com.kuaixiu.order.service.OrderService;
import com.kuaixiu.recycle.entity.RecycleOrder;
import com.kuaixiu.recycle.service.RecycleOrderService;
import com.system.api.entity.ResultData;
import com.system.basic.user.entity.SessionUser;
import com.system.constant.SystemConstant;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author: najy
 * @CreateDate: 2018年9月6日11:09:57
 * @version: V 1.0
 * 回收抬价控制器
 */
@Controller
public class IncreaseOrderListController extends BaseController {

    private static final Logger log = Logger.getLogger(IncreaseOrderListController.class);

    //抬价订单活动持续时间
    private static final Integer DurationOfActivity = 24;

    @Autowired
    IncreaseOrderService increaseOrderService;
    @Autowired
    RecycleOrderService recycleOrderService;
    @Autowired
    IncreaseRecordService increaseRecordService;

    /**
     * 回收抬价订单列表
     */
    @RequestMapping(value = "/recycle/increaseList")
    public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String returnView = "recycle/increaseList";
        return new ModelAndView(returnView);
    }


    /**
     * 刷新数据
     */
    @RequestMapping(value = "recycle/increase/queryListForPage")
    public void queryListForPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        SessionUser su = getCurrentUser(request);
        if (su.getType() != SystemConstant.USER_TYPE_SYSTEM && su.getType() != SystemConstant.USER_TYPE_CUSTOMER_SERVICE) {
            throw new SystemException("对不起，您没有操作权限!");
        }
        // 获取查询条件
        String increaseOrderNo = request.getParameter("increase_order_no");//抬价订单号
        String recycleOrderNo = request.getParameter("recycle_order_no");//回收订单号
        String increaseSuccess = request.getParameter("increase_success");//订单状态
        String StartIncreaseStartTime = request.getParameter("start_increase_startTime");//创建订单开始时间
        String StartIncreaseEndTime = request.getParameter("start_increase_endTime");//创建订单结束时间
        String EndIncreaseStartTime = request.getParameter("end_increase_startTime");//结束订单开始时间
        String EndIncreaseEndTime = request.getParameter("end_increase_endTime");//结束订单结束时间

        if ((StringUtils.isNotBlank(StartIncreaseStartTime) || StringUtils.isNotBlank(StartIncreaseEndTime))
                && (StringUtils.isNotBlank(EndIncreaseStartTime) || StringUtils.isNotBlank(EndIncreaseEndTime))) {
            throw new SystemException("开始时间和结束时间不可同时作为查询条件");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        //获取当前时间
        GregorianCalendar now = new GregorianCalendar();
        long nowTime = now.getTime().getTime();

        IncreaseOrder increaseOrder = new IncreaseOrder();
        //查询加价中订单是否已过期
        List<IncreaseOrder> orders = increaseOrderService.getDao().queryByIsSuccess(increaseOrder);
        for (IncreaseOrder increaseOrder1 : orders) {
            //结束时间
            calendar.setTime(increaseOrder1.getInTime());
            calendar.add(Calendar.HOUR_OF_DAY, DurationOfActivity);
            long endTime = calendar.getTime().getTime();
            if (endTime - nowTime < 0) {
                increaseOrder1.setIsSuccess(1);
                increaseOrderService.saveUpdate(increaseOrder1);
            }
        }

        increaseOrder.setOrderNo(increaseOrderNo);
        increaseOrder.setRecycleOrderNo(recycleOrderNo);
        if (StringUtils.isNotBlank(increaseSuccess)) {
            increaseOrder.setIsSuccess(Integer.valueOf(increaseSuccess));
        }

        if (StringUtils.isNotBlank(StartIncreaseStartTime)) {
            increaseOrder.setIncreaseEndTime(getTime(StartIncreaseStartTime));
        } else if (StringUtils.isNotBlank(StartIncreaseEndTime)) {
            increaseOrder.setIncreaseStartTime(getTime(StartIncreaseEndTime));
        } else if (StringUtils.isNotBlank(EndIncreaseStartTime)) {
            calendar.setTime(sdf.parse(getTime(EndIncreaseStartTime)));
            calendar.add(Calendar.HOUR_OF_DAY, -DurationOfActivity);
            increaseOrder.setIncreaseStartTime(sdf.format(calendar.getTime()));
        } else if (StringUtils.isNotBlank(EndIncreaseEndTime)) {
            calendar.setTime(sdf.parse(getTime(EndIncreaseEndTime)));
            calendar.add(Calendar.HOUR_OF_DAY, -DurationOfActivity);
            increaseOrder.setIncreaseStartTime(sdf.format(calendar.getTime()));
        }
        Page page = getPageByRequest(request);
        increaseOrder.setPage(page);
        List<IncreaseOrder> list = increaseOrderService.queryListForPage(increaseOrder);
        for (IncreaseOrder increaseOrder1 : list) {
            //查询订单金额
            RecycleOrder order = recycleOrderService.getDao().queryByOrderNo(increaseOrder1.getRecycleOrderNo());
            if (order != null) {
                increaseOrder1.setPrice(order.getPrice());
            }
            //开始时间
            increaseOrder1.setIncreaseStartTime(sdf.format(increaseOrder1.getInTime()));
            //结束时间
            calendar.setTime(increaseOrder1.getInTime());
            calendar.add(Calendar.HOUR_OF_DAY, DurationOfActivity);
            increaseOrder1.setIncreaseEndTime(sdf.format(calendar.getTime()));
            //加价的才有剩余时间
            if (increaseOrder1.getIsSuccess() == 0) {
                SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                sdf1.setTimeZone(TimeZone.getTimeZone("GMT"));
                //加价剩余时间
                long endTime = (sdf.parse(increaseOrder1.getIncreaseEndTime())).getTime();
                String remainingTime = sdf1.format(new Date(endTime - nowTime));
                increaseOrder1.setRemainingTime(remainingTime);
            } else {
                increaseOrder1.setRemainingTime("00:00:00");
            }
        }
        page.setData(list);
        this.renderJson(response, page);
    }

    //时间参数改变格式
    public String getTime(String time) {
        if (StringUtils.isNotBlank(time) && time.length() == 10) {
            return time + " 00:00:00";
        }
        return time;
    }

    /**
     * 回收订单详情
     */
    @RequestMapping(value = "recycle/increase/detail")
    public ModelAndView detail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        SessionUser su = getCurrentUser(request);
        if (su.getType() != SystemConstant.USER_TYPE_SYSTEM && su.getType() != SystemConstant.USER_TYPE_CUSTOMER_SERVICE) {
            throw new SystemException("对不起，您没有操作权限!");
        }
        String id = request.getParameter("id");
        IncreaseOrder order = increaseOrderService.getDao().queryById(id);
        if (order == null) {
            throw new SystemException("订单未找到！！");
        }
        //查询订单金额
        RecycleOrder recycleOrder = recycleOrderService.getDao().queryByOrderNo(order.getRecycleOrderNo());
        if (recycleOrder != null && recycleOrder.getPrice() != null) {
            order.setStrPrice((recycleOrder.getPrice()).toString());
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        //开始时间
        order.setIncreaseStartTime(sdf.format(order.getInTime()));
        //结束时间
        calendar.setTime(order.getInTime());
        calendar.add(Calendar.HOUR_OF_DAY, DurationOfActivity);
        order.setIncreaseEndTime(sdf.format(calendar.getTime()));
        //获取当前时间
        GregorianCalendar now = new GregorianCalendar();
        long nowTime = now.getTime().getTime();
        //加价的才有剩余时间
        if (order.getIsSuccess() == 0) {
            SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            sdf1.setTimeZone(TimeZone.getTimeZone("GMT"));
            //加价剩余时间
            long endTime = (sdf.parse(order.getIncreaseEndTime())).getTime();
            String remainingTime = sdf1.format(new Date(endTime - nowTime));
            order.setRemainingTime(remainingTime);
        } else {
            order.setRemainingTime("00:00:00");
        }

        request.setAttribute("increaseOrder", order);
        String returnView = "recycle/increaseDetail";
        return new ModelAndView(returnView);
    }

    @RequestMapping("recycle/increase/detailList")
    @ResponseBody
    public void getCouponList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String recycleOrderNo = request.getParameter("recycleOrderNo");
        BigDecimal price = new BigDecimal("0");
        RecycleOrder recycleOrder = recycleOrderService.queryByOrderNo(recycleOrderNo);
        if (recycleOrder != null) {
            price = recycleOrder.getPrice();
        }
        IncreaseRecord increaseRecord = new IncreaseRecord();
        increaseRecord.setRecycleOrderNo(recycleOrderNo);
        Page page = getPageByRequest(request);
        increaseRecord.setPage(page);
        List<IncreaseRecord> increaseRecords = increaseRecordService.queryListForPage(increaseRecord);
        for (IncreaseRecord increaseRecord1 : increaseRecords) {
            BigDecimal strPrice=price.divide(new BigDecimal("100"));
            strPrice=strPrice.multiply(new BigDecimal(increaseRecord1.getPlan()));
            increaseRecord1.setPrice(strPrice.toString());
        }
        page.setData(increaseRecords);
        this.renderJson(response, page);
    }
}
