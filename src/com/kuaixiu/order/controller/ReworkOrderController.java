package com.kuaixiu.order.controller;

import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.paginate.Page;
import com.common.wechat.common.util.StringUtils;
import com.kuaixiu.brand.entity.Brand;
import com.kuaixiu.brand.service.BrandService;
import com.kuaixiu.model.entity.Model;
import com.kuaixiu.model.service.ModelService;
import com.kuaixiu.order.entity.Order;
import com.kuaixiu.order.entity.OrderDetail;
import com.kuaixiu.order.entity.ReworkOrder;
import com.kuaixiu.order.service.OrderDetailService;
import com.kuaixiu.order.service.OrderService;
import com.kuaixiu.order.service.ReworkOrderService;
import com.kuaixiu.project.entity.Project;
import com.kuaixiu.project.service.ProjectService;
import com.system.api.entity.ResultData;
import org.apache.commons.collections.CollectionUtils;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private OrderDetailService orderDetailService;

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
                orderDetails = orderDetailService.queryByOrderNo(order.getOrderNo());
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

            request.setAttribute("realPrice", order.getOrderPrice());
            request.setAttribute("couponPrice", order.getOrderPrice().subtract(price));
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
     * H5订单售后生成订单
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/reworkOrder/addRework")
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
            List<ReworkOrder> reworkOrders = reworkOrderService.getDao().queryByParentOrder(order.getOrderNo());
            if (!CollectionUtils.isEmpty(reworkOrders)) {
                return getResult(result, null, false, "4", "该订单正在售后中");
            }
            ReworkOrder reworkOrder = new ReworkOrder();
            reworkOrder.setReworkReasons(Integer.valueOf(reworkReason));
            reworkOrder.setReasonsDetail(reasonDetail);
            //创建保存返修订单
            reworkOrderService.save(order, reworkOrder);
            //给工程师派单
            reworkOrderService.dispatch(order, reworkOrder);

            getResult(result, null, true, "0", "成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.info(e.getMessage());
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
    @RequestMapping(value = "/reworkOrder/submitProject")
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
            ReworkOrder reworkOrder=reworkOrderService.getDao().queryByReworkNo(reOrderNo);
            if(reworkOrder==null){
                return getResult(result, null, false, "2", "该返修订单不存在");
            }
            OrderDetail orderDetail=new OrderDetail();
            orderDetail.setOrderNo(reworkOrder.getParentOrder());
            orderDetail.setProjectId(projectId);
            List<OrderDetail> orderDetails=orderDetailService.queryList(orderDetail);
            for(OrderDetail orderDetail1:orderDetails){
                orderDetail1.setIsRework(1);
                orderDetailService.saveUpdate(orderDetail1);
            }

            getResult(result, null, true, "0", "成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.info(e.getMessage());
        }
        return result;
    }


}
