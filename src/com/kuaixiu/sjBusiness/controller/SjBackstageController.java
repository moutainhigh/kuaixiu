package com.kuaixiu.sjBusiness.controller;


import com.common.base.controller.BaseController;
import com.common.paginate.Page;
import com.kuaixiu.sjBusiness.entity.OrderCompanyPicture;
import com.kuaixiu.sjBusiness.entity.OrderContractPicture;
import com.kuaixiu.sjBusiness.entity.SjOrder;
import com.kuaixiu.sjBusiness.service.OrderCompanyPictureService;
import com.kuaixiu.sjBusiness.service.OrderContractPictureService;
import com.kuaixiu.sjBusiness.service.SjOrderService;
import com.kuaixiu.sjUser.entity.ConstructionCompany;
import com.kuaixiu.sjUser.entity.SjSessionUser;
import com.kuaixiu.sjUser.entity.SjUser;
import com.kuaixiu.sjUser.entity.SjWorker;
import com.kuaixiu.sjUser.service.ConstructionCompanyService;
import com.kuaixiu.sjUser.service.SjUserService;
import com.kuaixiu.sjUser.service.SjWorkerService;
import com.system.api.entity.ResultData;
import com.system.basic.address.entity.Address;
import com.system.basic.address.service.AddressService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Order Controller
 *
 * @CreateDate: 2019-05-06 上午10:44:49
 * @version: V 1.0
 */
@Controller
public class SjBackstageController extends BaseController {
    private static final Logger log = Logger.getLogger(SjBackstageController.class);

    @Autowired
    private SjOrderService orderService;
    @Autowired
    private SjUserService sjUserService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private OrderCompanyPictureService orderCompanyPictureService;
    @Autowired
    private OrderContractPictureService orderContractPictureService;
    @Autowired
    private ConstructionCompanyService constructionCompanyService;
    @Autowired
    private SjWorkerService sjWorkerService;

    /**
     * 订单列表
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sj/order/list")
    public ModelAndView list(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {
        String returnView = "business/orderList";
        return new ModelAndView(returnView);
    }

    /**
     * 指派订单列表
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sj/order/unCheckedList")
    public ModelAndView unCheckedList(HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {
        String returnView = "business/unCheckedList";
        return new ModelAndView(returnView);
    }

    /**
     * 订单刷新列表
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/sj/order/queryListForPage")
    public void queryListForPage(HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        Page page = getPageByRequest(request);
        try {
            //获取查询条件
            String orderNo = request.getParameter("orderNo");
            String type = request.getParameter("type");
            String queryStartTime = request.getParameter("query_startTime");
            String queryEndTime = request.getParameter("query_endTime");
            String createUser = request.getParameter("createUser");
            String companyName = request.getParameter("companyName");
            String state = request.getParameter("state");
            SjOrder sjOrder = new SjOrder();
            sjOrder.setOrderNo(orderNo);
            if (StringUtils.isNotBlank(type)) {
                sjOrder.setType(Integer.valueOf(type));
            }
            if (StringUtils.isNotBlank(state)) {
                sjOrder.setState(Integer.valueOf(state));
            }
            sjOrder.setQueryStartTime(queryStartTime);
            sjOrder.setQueryEndTime(queryEndTime);
            sjOrder.setCompanyName(companyName);
            if (StringUtils.isNotBlank(createUser)) {
                if (orderService.isNumeric(createUser)) {
                    sjOrder.setCreateUserid(createUser);
                } else {
                    sjOrder.setCreateName(createUser);
                }
            }
            sjOrder.setPage(page);
            List<SjOrder> sjOrders = orderService.queryListForPage(sjOrder);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (SjOrder sjOrder1 : sjOrders) {
                List<String> projects = orderService.getProject(sjOrder1.getProjectId());
                String projectName = orderService.listToString(projects);
                sjOrder1.setProjectNames(projectName);
                sjOrder1.setStrCreateTime(sdf.format(sjOrder1.getCreateTime()));
                sjOrder1.setNewDate(sdf.format(new Date()));
            }
            page.setData(sjOrders);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.renderJson(response, page);
    }


    /**
     * 订单详情
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sj/order/detail")
    public ModelAndView detail(HttpServletRequest request,
                               HttpServletResponse response) throws Exception {
        String id = request.getParameter("id");
        SjOrder sjOrder = orderService.queryById(id);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String province = addressService.queryByAreaId(sjOrder.getProvinceId()).getArea();
        String city = addressService.queryByAreaId(sjOrder.getCityId()).getArea();
        String area = addressService.queryByAreaId(sjOrder.getAreaId()).getArea();
        Address address = addressService.queryByAreaId(sjOrder.getStreetId());
        String street = "";
        if (address != null) {
            street = address.getArea();
        }
        sjOrder.setAddress(province + " " + city + " " + area + " " + street);
        List<String> projects = orderService.getProject(sjOrder.getProjectId());
        String projectName = orderService.listToString(projects);
        sjOrder.setProjectNames(projectName);
        sjOrder.setStrCreateTime(sdf.format(sjOrder.getCreateTime()));
        List<OrderCompanyPicture> companyPictures = orderCompanyPictureService.getDao().queryByOrderNo(sjOrder.getOrderNo());
        if (sjOrder.getApprovalPerson() != null) {
            String user = sjUserService.userIdToUserIdName(sjOrder.getApprovalPerson());
            sjOrder.setApprovalPerson(user);
        }
        if (sjOrder.getAssignPerson() != null) {
            String user = sjUserService.userIdToUserIdName(sjOrder.getAssignPerson());
            sjOrder.setAssignPerson(user);
        }
        if (sjOrder.getBuildPerson() != null) {
            String user = sjUserService.userIdToUserIdName(sjOrder.getBuildPerson());
            sjOrder.setBuildPerson(user);
        }
        if (sjOrder.getState() == 500) {
            if (sjOrder.getCompletedPerson() != null) {
                String user = sjUserService.userIdToUserIdName(sjOrder.getCompletedPerson());
                sjOrder.setCompletedPerson(user);
            }
            List<OrderContractPicture> contractPictures = orderContractPictureService.getDao().queryByOrderNo(sjOrder.getOrderNo());
            request.setAttribute("contractPictures", contractPictures);
        }
        request.setAttribute("sjOrder", sjOrder);
        request.setAttribute("companyPictures", companyPictures);
        String returnView = "business/detail";
        return new ModelAndView(returnView);
    }


    /**
     * 订单审批
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sj/order/approval")
    @ResponseBody
    public ResultData getProject(HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        try {
            String id = request.getParameter("id");
            String note = request.getParameter("note");
            String isPast = request.getParameter("isPast");//是否通过   1通过   2不通过
            SjOrder sjOrder = orderService.queryById(id);
            SjSessionUser su = getSjCurrentUser(request);
            sjOrder.setApprovalPerson(su.getUserId());
            sjOrder.setApprovalTime(new Date());
            sjOrder.setApprovalNote(note);
            if ("1".equals(isPast)) {
                sjOrder.setState(200);
            } else if ("2".equals(isPast)) {
                sjOrder.setState(600);
            }
            sjOrder.setStayPerson(orderService.setStayPerson(2));
            orderService.saveUpdate(sjOrder);
            getSjResult(result, null, true, "0", null, "获取成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/sj/order/toAssign")
    public ModelAndView add(HttpServletRequest request,
                            HttpServletResponse response) {
        try {
            //获取省份地址
            List<Address> provinceL = addressService.queryByPid("0");
            //获取市地址
            List<Address> cityL = addressService.queryByPid("15");
            //获取区县地址
            List<Address> areal = addressService.queryByPid("1213");
            request.setAttribute("provinceL", provinceL);
            request.setAttribute("cityL", cityL);
            request.setAttribute("areal", areal);
        }catch (Exception e){
            e.printStackTrace();
        }
        String returnView ="business/againOrder";
        return new ModelAndView(returnView);
    }

    /**
     * 筛选查询企业
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/sj/order/queryCompanyForPage")
    public void queryCompanyForPage(HttpServletRequest request,
                                    HttpServletResponse response) throws Exception {
        Page page = getPageByRequest(request);
        try {
            //获取查询条件
            String provinceId = request.getParameter("addProvince");
            String cityId = request.getParameter("addCity");
            String areaId = request.getParameter("addCounty");
            String streetId = request.getParameter("addStreet");
            String project = request.getParameter("projectIds");
            List<String> projects = new ArrayList<>();
            if(StringUtils.isNotEmpty(project)){
                if (project.contains(",")) {
                    String[] projectIds1 = project.split(",");
                    for (int i = 0; i < projectIds1.length; i++) {
                        String project1 = projectIds1[i];
                        projects.add(project1);
                    }
                }
            }
            ConstructionCompany constructionCompany = new ConstructionCompany();
            constructionCompany.setQueryStatusArray(projects);
            constructionCompany.setProvince(provinceId);
            constructionCompany.setCity(cityId);
            constructionCompany.setArea(areaId);
            constructionCompany.setStreet(streetId);
            constructionCompany.setPage(page);
            List<ConstructionCompany> companies = constructionCompanyService.queryListForPage(constructionCompany);
            for (ConstructionCompany company : companies) {
                String province = addressService.queryByAreaId(company.getProvince()).getArea();
                String city = addressService.queryByAreaId(company.getCity()).getArea();
                String area = addressService.queryByAreaId(company.getArea()).getArea();
                Address address = addressService.queryByAreaId(company.getStreet());
                String street = "";
                if (address != null) {
                    street = address.getArea();
                }
                company.setAddress(province + city + area + street);
                List<String> projects1 = orderService.getProject(company.getProject());
                String projectName = orderService.listToString(projects1);
                company.setProjectNames(projectName);
                SjUser sjUser = sjUserService.getDao().queryByLoginId(company.getLoginId());
                company.setCompanyName(sjUser.getName());
            }
            page.setData(companies);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.renderJson(response, page);
    }

    /**
     * 指派订单
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sj/order/assign")
    @ResponseBody
    public ResultData assign(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        SjSessionUser su = getSjCurrentUser(request);
        try {
            String orderId = request.getParameter("orderId");
            String userId = request.getParameter("userId");

            List<SjWorker> sjWorkers=sjWorkerService.getDao().queryByCompanyId(userId);
            if(CollectionUtils.isEmpty(sjWorkers)){
                return getSjResult(result, null, true, "0", null, "该单位没有员工");
            }
            SjWorker sjWorker=sjWorkers.get(0);
            SjUser sjUser=sjUserService.getDao().queryByLoginId(sjWorker.getLoginId());
            SjOrder sjOrder = orderService.queryById(orderId);

            sjOrder.setState(300);
            sjOrder.setAssignTime(new Date());
            sjOrder.setAssignPerson(su.getUserId());
            sjOrder.setStayPerson(sjWorker.getLoginId());
            //待施工人信息
            sjOrder.setBuildPerson(sjWorker.getLoginId());
            sjOrder.setBuildCompany(sjWorker.getCompanyLoginId());
            sjOrder.setBuildPhone(sjUser.getPhone());
            orderService.saveUpdate(sjOrder);
            //工人接单数量加一
            sjWorker.setBuildingNum(sjWorker.getBuildingNum()+1);
            sjWorkerService.saveUpdate(sjWorker);

            getSjResult(result, null, true, "0", null, "指派成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }
}
