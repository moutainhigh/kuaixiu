package com.kuaixiu.sjBusiness.controller;

import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.paginate.Page;
import com.common.wechat.common.util.StringUtils;
import com.kuaixiu.sjBusiness.entity.SjOrder;
import com.kuaixiu.sjBusiness.entity.SjProject;
import com.kuaixiu.sjBusiness.entity.SjReworkOrder;
import com.kuaixiu.sjBusiness.service.SjOrderService;
import com.kuaixiu.sjBusiness.service.SjProjectService;
import com.kuaixiu.sjBusiness.service.SjReworkOrderService;
import com.system.api.entity.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * SjReworkOrder Controller
 *
 * @CreateDate: 2019-06-28 下午04:31:50
 * @version: V 1.0
 */
@Controller
public class SjReworkOrderController extends BaseController {

    @Autowired
    private SjReworkOrderService sjReworkOrderService;
    @Autowired
    private SjOrderService orderService;
    @Autowired
    private SjProjectService projectService;

    /**
     * 联系人手机号查询甩单订单列表
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sj/reworkOrder/getOrderList")
    @ResponseBody
    public ResultData getOrderList(HttpServletRequest request,
                                   HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        try {
            JSONObject params = getPrarms(request);
            String phone = params.getString("phone");
            Integer pageIndex = params.getInteger("pageIndex");
            Integer pageSize = params.getInteger("pageSize");
            Integer type = params.getInteger("type");

            if (StringUtils.isBlank(phone)) {
                return getSjResult(result, null, false, "2", null, "手机号不能为空");
            }
            SjOrder sjOrder = new SjOrder();
            Page page = new Page();
            //将值转化为绝对值
            pageIndex = Math.abs(pageIndex);
            pageSize = Math.abs(pageSize);
            page.setPageSize(pageSize);
            page.setCurrentPage(pageIndex);
            sjOrder.setPage(page);
            sjOrder.setPhone(phone);
            sjOrder.setType(type);
            if (type == 1) {
                sjOrder.setState(400);
            } else if (type == 2) {
                sjOrder.setState(500);
            }
            List<SjOrder> sjOrders = orderService.getDao().queryWebListForPage(sjOrder);
            JSONObject jsonObject = orderService.sjListReOrderToObejct(sjOrders,page);
            getSjResult(result, jsonObject, true, "0", null, "查询成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 联系人手机号查询甩单订单详情
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sj/reworkOrder/getOrderDetail")
    @ResponseBody
    public ResultData getOrderDetail(HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        try {
            JSONObject params = getPrarms(request);
            String phone = params.getString("phone");
            String orderNo = params.getString("orderNo");
            if (StringUtils.isBlank(phone) || StringUtils.isBlank(orderNo)) {
                return getSjResult(result, null, false, "0", null, "参数为空");
            }
            SjOrder sjOrder = orderService.getDao().queryByPhoneOrderNo(orderNo, phone);
            if (sjOrder == null) {
                return getSjResult(result, null, false, "0", null, "订单号错误");
            }
            JSONObject jsonObject = orderService.sjOrderToObejct(sjOrder);

            getSjResult(result, jsonObject, true, "0", null, "查询成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 根据订单编号获取所有产品需求
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sj/reworkOrder/getProjects")
    @ResponseBody
    public ResultData getProject(HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        List<JSONObject> objects = new ArrayList<>();
        try {
            JSONObject params = getPrarms(request);
            String orderNo = params.getString("orderNo");
            SjOrder sjOrder = orderService.getDao().queryByOrderNo(orderNo);
            String[] projectIds = sjOrder.getProjectId().split(",");
            for (String projectId : projectIds) {
                SjProject project = projectService.queryById(projectId);
                JSONObject object = new JSONObject();
                object.put("projectId", project.getId());
                object.put("project", project.getProject());
                objects.add(object);
            }
            getSjResult(result, objects, true, "0", null, "获取成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 创建报障订单
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sj/reworkOrder/submitReworkOrder")
    @ResponseBody
    public ResultData submitReworkOrder(HttpServletRequest request,
                                        HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        try {
            JSONObject params = getPrarms(request);
            String phone = params.getString("phone");
            String orderNo = params.getString("orderNo");
            String projectId = params.getString("projectId");//需求id  ","隔开
            String note = params.getString("note");//报障备注
            if (StringUtils.isBlank(phone) || StringUtils.isBlank(orderNo) || StringUtils.isBlank(projectId)) {
                return getSjResult(result, null, false, "2", null, "参数为空");
            }
            SjOrder sjOrder = orderService.getDao().queryByOrderNo(orderNo);
            //创建报障订单
            SjReworkOrder sjReworkOrder = sjReworkOrderService.submitReworkOrder(sjOrder, projectId, note, phone);
            //指派订单给原企业
            sjReworkOrderService.assignReworkOrder(sjReworkOrder, sjOrder);

            JSONObject jsonObject=new JSONObject();
            jsonObject.put("reworkNo",sjReworkOrder.getReworkOrderNo());
            jsonObject.put("reworkId",sjReworkOrder.getId());
            jsonObject.put("orderNo",sjReworkOrder.getOrderNo());
            getSjResult(result, jsonObject, true, "0", null, "创建成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @RequestMapping(value = "/sj/reworkOrder/getReworkOrderList")
    @ResponseBody
    public ResultData getReworkOrderList(HttpServletRequest request,
                                         HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        try {
            JSONObject params = getPrarms(request);
            Integer state = params.getInteger("state");
            String phone = params.getString("phone");
            Integer pageIndex = params.getInteger("pageIndex");
            Integer pageSize = params.getInteger("pageSize");
            if (StringUtils.isBlank(phone) || null == pageIndex || null == pageSize) {
                return getSjResult(result, null, false, "2", null, "手机号不能为空");
            }
            SjReworkOrder sjOrder = new SjReworkOrder();
            Page page = new Page();
            //将值转化为绝对值
            pageIndex = Math.abs(pageIndex);
            pageSize = Math.abs(pageSize);
            page.setPageSize(pageSize);
            page.setCurrentPage(pageIndex);
            sjOrder.setPage(page);
            sjOrder.setCreateUserid(phone);
            sjOrder.setState(state);
            List<SjReworkOrder> sjReworkOrders = sjReworkOrderService.queryListForPage(sjOrder);
            List<JSONObject> jsonObjects = sjReworkOrderService.getObjectList(sjReworkOrders);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("pageSize", page.getPageSize());
            jsonObject.put("pageIndex", page.getCurrentPage());
            jsonObject.put("recordsTotal", page.getRecordsTotal());
            jsonObject.put("totalPage", page.getTotalPage());
            jsonObject.put("reworkOrders", jsonObjects);
            getSjResult(result, jsonObject, true, "0", null, "成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @RequestMapping(value = "/sj/reworkOrder/reworkOrderDetail")
    @ResponseBody
    public ResultData reworkOrderDetail(HttpServletRequest request,
                                        HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        try {
            JSONObject params = getPrarms(request);
            String reworkNo = params.getString("reworkNo");
            if (StringUtils.isBlank(reworkNo)) {
                return getSjResult(result, null, false, "2", null, "参数为空");
            }
            SjReworkOrder sjReworkOrder = sjReworkOrderService.getDao().queryByReworkOrderNo(reworkNo);
            JSONObject jsonObject = sjReworkOrderService.getObjectDetail(sjReworkOrder);
            getSjResult(result, jsonObject, true, "0", null, "成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
