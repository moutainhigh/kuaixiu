package com.kuaixiu.order.controller;

import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.paginate.Page;
import com.common.wechat.common.util.StringUtils;
import com.google.common.collect.Maps;
import com.kuaixiu.brand.entity.Brand;
import com.kuaixiu.brand.service.BrandService;
import com.kuaixiu.engineer.entity.EngineerSignIn;
import com.kuaixiu.engineer.service.EngineerSignInService;
import com.kuaixiu.model.entity.Model;
import com.kuaixiu.model.service.ModelService;
import com.kuaixiu.order.constant.OrderConstant;
import com.kuaixiu.order.entity.Order;
import com.kuaixiu.order.entity.OrderDetail;
import com.kuaixiu.order.entity.ReworkOrder;
import com.kuaixiu.order.service.OrderDetailService;
import com.kuaixiu.order.service.OrderService;
import com.kuaixiu.order.service.ReworkOrderService;
import com.kuaixiu.project.entity.CancelReason;
import com.kuaixiu.project.entity.Project;
import com.kuaixiu.project.service.CancelReasonService;
import com.kuaixiu.project.service.ProjectService;
import com.system.api.entity.ResultData;
import com.system.basic.user.entity.SessionUser;
import com.system.constant.SystemConstant;
import jodd.util.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * ReworkOrder Controller
 *
 * @CreateDate: 2019-01-10 上午09:45:45
 * @version: V 1.0
 */
@Controller
public class ReworkOrderController extends BaseController {
    private static final Logger log = Logger.getLogger(ReworkOrderController.class);
    @Autowired
    private ReworkOrderService reworkOrderService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private ModelService modelService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private EngineerSignInService engineerSignInService;
    @Autowired
    private CancelReasonService cancelReasonService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/reworkOrder/list")
    @ResponseBody
    public ModelAndView list(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {
        List<Brand> brands = brandService.queryList(null);
        List<Model> models = modelService.queryList(null);
        List<Project> projects = projectService.queryList(null);
        request.setAttribute("brands", brands);
        request.setAttribute("models", models);
        request.setAttribute("projects", projects);

        String returnView = "order/listForRework";
        return new ModelAndView(returnView);
    }

    /**
     * 返修刷新列表
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/reworkOrder/listForPage")
    @ResponseBody
    public void listForPage(HttpServletRequest request,
                            HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        Page page = getPageByRequest(request);
        try {
            String projectId = request.getParameter("projectId");
            String engineerNumber = request.getParameter("engineerNumber");
            String brandId = request.getParameter("brandId");
            String queryStartTime = request.getParameter("queryStartTime");
            String queryEndTime = request.getParameter("queryEndTime");
            String modelId = request.getParameter("modelId");
            String orderReworkNo = request.getParameter("orderReworkNo");
            String parentOrder = request.getParameter("parentOrder");

            ReworkOrder reworkOrder = new ReworkOrder();
            reworkOrder.setEngineerMobile(engineerNumber);
            reworkOrder.setBrandId(brandId);
            reworkOrder.setProjectId(projectId);
            reworkOrder.setQueryStartTime(queryStartTime);
            reworkOrder.setQueryEndTime(queryEndTime);
            reworkOrder.setModelId(modelId);
            reworkOrder.setParentOrder(parentOrder);
            reworkOrder.setOrderReworkNo(orderReworkNo);
            reworkOrder.setPage(page);
            List<Map<String, String>> reworkOrders = reworkOrderService.getDao().queryReworkListForPage(reworkOrder);
            for (Map<String, String> map : reworkOrders) {
                Map<String, String> map1 = reworkOrderService.getProject(map.get("parentNo"));
                map.put("projectNames", map1.get("projectNames"));
                //订单状态筛选列表
                Map<Integer, Object> m = orderService.getSelectOrderStatus();
                map.put("orderStatusName", m.get(map.get("orderStatus")).toString());
            }

            page.setData(reworkOrders);

        } catch (Exception e) {
            e.printStackTrace();
            log.info(e.getMessage());
        }
        this.renderJson(response, page);
    }


    /**
     * 返修订单详情
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/order/reworkOrderDetail")
    public ModelAndView reworkOrderDetail(HttpServletRequest request,
                                          HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        try {
            String reworkOrderNo = request.getParameter("reworkNo");

            ReworkOrder reworkOrder = reworkOrderService.getDao().queryByReworkNo(reworkOrderNo);
            Order order = orderService.queryByOrderNo(reworkOrder.getParentOrder());
            List<Map<String, String>> list = new ArrayList<Map<String, String>>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            reworkOrder.setStrInTime(sdf.format(reworkOrder.getInTime()));
            if (null != reworkOrder.getAgreedTime()) {
                reworkOrder.setStrAgreedTime(sdf.format(reworkOrder.getAgreedTime()));
            }
            if (null != reworkOrder.getEndTime()) {
                reworkOrder.setStrEndTime(sdf.format(reworkOrder.getEndTime()));
            }
            List<OrderDetail> orderDetails;
            //查询是否有返修项目
            orderDetails = orderDetailService.getDao().queryIsReworkByOrderNo(order.getOrderNo());
            if (CollectionUtils.isEmpty(orderDetails)) {
                //没有返修项目就查询全部
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrderNo(order.getOrderNo());
                orderDetail.setType(1);
                orderDetails = orderDetailService.queryList(orderDetail);
            }
            BigDecimal price = new BigDecimal("0");
            for (OrderDetail orderDetail : orderDetails) {
                Map<String, String> map = new HashMap<>();
                map.put("projectName", orderDetail.getProjectName());
                map.put("price", orderDetail.getRealPrice().toString());
                map.put("surplusDay", reworkOrder.getSurplusDay().toString());
                map.put("totalDay", reworkOrder.getTotalDay().toString());
                list.add(map);
                price = price.add(orderDetail.getRealPrice());
            }
            EngineerSignIn signIn = new EngineerSignIn();
            signIn.setOrderNo(reworkOrderNo);
            List<EngineerSignIn> signIns = engineerSignInService.getDao().queryList(signIn);
            if (!CollectionUtils.isEmpty(signIns)) {
                signIns.get(0).setStrCreateTime(sdf.format(signIns.get(0).getCreateTime()));
                request.setAttribute("engineerSignIn", signIns.get(0));
            }

            //取消原因标签列表
            List<CancelReason> reasonList = cancelReasonService.queryListForPage(new CancelReason());

            request.setAttribute("reasonList", reasonList);
            request.setAttribute("realPrice", order.getOrderPrice());
            request.setAttribute("couponPrice", "-" + order.getOrderPrice());
            request.setAttribute("projects", list);
            request.setAttribute("rework", reworkOrder);
            request.setAttribute("order", order);

        } catch (Exception e) {
            e.printStackTrace();
            log.info(e.getMessage());
        }
        String returnView = "order/reworkDetail";
        return new ModelAndView(returnView);
    }

    /**
     * 进入返修订单页面
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/order/reworkOrder")
    public ModelAndView reworkOrder(HttpServletRequest request,
                                    HttpServletResponse response) throws Exception {
        try {
            String orderNo = request.getParameter("orderNo");
            request.setAttribute("orderNo", orderNo);
        } catch (Exception e) {
            e.printStackTrace();
            log.info(e.getMessage());
        }
        String returnView = "order/addRewordOrder";
        return new ModelAndView(returnView);
    }

    /**
     * 后台售后生成订单
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/reworkOrder/endAddRework")
    @ResponseBody
    public ResultData endAddRework(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        try {
            String orderNo = request.getParameter("orderNo");
            String reworkReason = request.getParameter("reworkReason");
            String reasonDetail = request.getParameter("reasonDetail");

            if (StringUtils.isBlank(reworkReason) || StringUtils.isBlank(orderNo)) {
                return getResult(result, null, false, "2", "参数不完整");
            }
            Order order = orderService.queryByOrderNo(orderNo);
            if (order == null) {
                return getResult(result, null, false, "3", "该订单不存在");
            }
            List<ReworkOrder> reworkOrders = reworkOrderService.getDao().queryByParentOrder(order.getOrderNo());
            if (!CollectionUtils.isEmpty(reworkOrders)) {
                return getResult(result, null, false, "4", "该订单正在售后中");
            }
            ReworkOrder reworkOrder = new ReworkOrder();
            reworkOrder.setReworkReasons(Integer.valueOf(reworkReason));
            reworkOrder.setReasonsDetail(reasonDetail);
            //创建保存返修订单
            ReworkOrder reworkOrder1 = reworkOrderService.save(order, reworkOrder);
            order.setIsRework(1);
            orderService.saveUpdate(order);
            //给工程师派单
            reworkOrderService.dispatch(order, reworkOrder);

            getResult(result, reworkOrder1, true, "0", "成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.info(e.getMessage());
        }
        return result;
    }

    /**
     * 取消订单
     *
     * @param request
     * @param response
     * @throws IOException
     * @CreateDate: 2016-9-17 上午12:13:53
     */
    @RequestMapping("/order/reworkOrderCancel")
    public void orderCancel(HttpServletRequest request,
                            HttpServletResponse response) throws IOException {
        SessionUser su = getCurrentUser(request);
        //订单id
        String id = request.getParameter("id");
        String selectReason = request.getParameter("selectReason");
        String reason = request.getParameter("reason");
        String cancelReason = null;
        if (!StringUtil.isBlank(selectReason)) {
            cancelReason = selectReason;
        }
        if (!StringUtil.isBlank(reason) && !StringUtil.isBlank(selectReason)) {
            cancelReason = cancelReason + "；" + reason;
        }
        if (!StringUtil.isBlank(reason) && StringUtil.isBlank(selectReason)) {
            cancelReason = reason;
        }

        if (su.getType() == SystemConstant.USER_TYPE_SYSTEM) {
            reworkOrderService.orderCancel(id, OrderConstant.ORDER_CANCEL_TYPE_ADMIN, cancelReason, su);
        } else if (su.getType() == SystemConstant.USER_TYPE_CUSTOMER_SERVICE) {
            reworkOrderService.orderCancel(id, OrderConstant.ORDER_CANCEL_TYPE_CUSTOMER_SERVICE, cancelReason, su);
        }

        Map<String, Object> resultMap = Maps.newHashMap();
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
        renderJson(response, resultMap);
    }

    /**
     * H5订单售后生成订单
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/wechat/reworkOrder/addRework")
    @ResponseBody
    public ResultData addRework(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        try {
            JSONObject params = getPrarms(request);
            String orderNo = params.getString("orderNo");
            String reworkReason = params.getString("reworkReason");
            String reasonDetail = params.getString("reasonDetail");

            if (StringUtils.isBlank(reworkReason) || StringUtils.isBlank(orderNo)) {
                return getResult(result, null, false, "2", "参数不完整");
            }
            Order order = orderService.queryByOrderNo(orderNo);
            if (order == null) {
                return getResult(result, null, false, "3", "该订单不存在");
            }
            if (order.getOrderStatus() == 60) {
                return getResult(result, null, false, "3", "该订单已取消，不能返修");
            }
            if (order.getOrderStatus() != 50) {
                return getResult(result, null, false, "3", "该订单未完成，不能返修");
            }
            List<ReworkOrder> reworkOrders = reworkOrderService.getDao().queryByParentOrder(order.getOrderNo());
            if (!CollectionUtils.isEmpty(reworkOrders)) {
                return getResult(result, null, false, "4", "该订单正在售后中");
            }
            ReworkOrder reworkOrder = new ReworkOrder();
            reworkOrder.setReworkReasons(Integer.valueOf(reworkReason));
            reworkOrder.setReasonsDetail(reasonDetail);
            //创建保存返修订单
            ReworkOrder reworkOrder1=reworkOrderService.save(order, reworkOrder);
            order.setIsRework(1);
            orderService.saveUpdate(order);
            //给工程师派单
            reworkOrderService.dispatch(order, reworkOrder);
            JSONObject json=new JSONObject();
            json.put("orderReworkNo",reworkOrder1.getOrderReworkNo());
            getResult(result, json, true, "0", "成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }


    /**
     * 工程师去人返修故障
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/wechat/reworkOrder/submitProject")
    @ResponseBody
    public ResultData submitReworkProject(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        try {
            JSONObject params = getPrarms(request);
            String reOrderNo = params.getString("reOrderNo");
            String projectId = params.getString("projectId");

            if (StringUtils.isBlank(reOrderNo)) {
                return getResult(result, null, false, "2", "参数不完整");
            }
            ReworkOrder reworkOrder = reworkOrderService.getDao().queryByReworkNo(reOrderNo);
            if (reworkOrder == null) {
                return getResult(result, null, false, "2", "该返修订单不存在");
            }
            if (projectId.contains(",")) {
                String[] project = projectId.split(",");
                for (int i = 0; i < project.length; i++) {
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setOrderNo(reworkOrder.getParentOrder());
                    orderDetail.setProjectId(project[i]);
                    List<OrderDetail> orderDetails = orderDetailService.queryList(orderDetail);
                    for (OrderDetail orderDetail1 : orderDetails) {
                        orderDetail1.setIsRework(1);
                        orderDetailService.saveUpdate(orderDetail1);
                    }
                }
            }

            reworkOrder.setEndTime(new Date());
            reworkOrder.setOrderStatus(OrderConstant.ORDER_STATUS_FINISHED);
            reworkOrderService.saveUpdate(reworkOrder);


            getResult(result, null, true, "0", "成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.info(e.getMessage());
        }
        return result;
    }


}
