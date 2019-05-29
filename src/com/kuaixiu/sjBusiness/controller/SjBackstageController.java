package com.kuaixiu.sjBusiness.controller;

import com.common.base.controller.BaseController;
import com.common.importExcel.ImportReport;
import com.common.paginate.Page;
import com.common.util.MD5Util;
import com.common.util.SmsSendUtil;
import com.kuaixiu.sjBusiness.entity.*;
import com.kuaixiu.sjBusiness.service.*;
import com.kuaixiu.sjSetMeal.entity.SjPoe;
import com.kuaixiu.sjSetMeal.entity.SjSaveNet;
import com.kuaixiu.sjSetMeal.entity.SjSetMeal;
import com.kuaixiu.sjSetMeal.entity.SjWifiMonitorType;
import com.kuaixiu.sjSetMeal.service.SjPoeService;
import com.kuaixiu.sjSetMeal.service.SjSaveNetService;
import com.kuaixiu.sjSetMeal.service.SjSetMealService;
import com.kuaixiu.sjSetMeal.service.SjWifiMonitorTypeService;
import com.kuaixiu.sjUser.entity.*;
import com.kuaixiu.sjUser.service.*;
import com.system.api.entity.ResultData;
import com.system.basic.address.entity.Address;
import com.system.basic.address.service.AddressService;
import com.system.basic.sequence.util.SeqUtil;
import com.system.constant.SystemConstant;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

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
    @Autowired
    private ApprovalNoteService approvalNoteService;
    @Autowired
    private CustomerDetailService customerDetailService;
    @Autowired
    private SjProjectService projectService;
    @Autowired
    private ConstructionCompanyService companyService;
    @Autowired
    private SjSetMealService sjSetMealService;
    @Autowired
    private SjWifiMonitorTypeService sjWifiMonitorTypeService;
    @Autowired
    private SjPoeService sjPoeService;
    @Autowired
    private SjSaveNetService sjSaveNetService;
    @Autowired
    private UserRoleService userRoleService;

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

    @RequestMapping(value = "/sj/order/approvalList")
    public ModelAndView approvalList(HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        String returnView = "business/orderApprovalList";
        return new ModelAndView(returnView);
    }

    @RequestMapping(value = "/sj/order/list2")
    public ModelAndView list2(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        String returnView = "business/order2List";
        return new ModelAndView(returnView);
    }

    @RequestMapping(value = "/sj/order/approvalList2")
    public ModelAndView approvalList2(HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {
        String returnView = "business/order2ApprovalList";
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
            String isAssign = request.getParameter("isAssign");//是否查询指派订单
            SjOrder sjOrder = new SjOrder();
            SjSessionUser sjSessionUser = getSjCurrentUser(request);
            if (sjSessionUser.getType() == 3) {
                sjOrder.setAssignCompanyId(sjSessionUser.getUserId());
            }
            if (sjSessionUser.getType() == 8) {
                sjOrder.setAssignWorkerId(sjSessionUser.getUserId());
            }
            if (sjSessionUser.getType() == 6) {
                sjOrder.setStayPerson(sjSessionUser.getUserId());
            }
            sjOrder.setIsAssign(isAssign);
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
                if (sjOrder1.getApprovalTime() != null) {
                    sjOrder1.setStrApprovalTime(sdf.format(sjOrder1.getApprovalTime()));
                }
            }
            page.setData(sjOrders);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.renderJson(response, page);
    }

    /**
     * 工人列表
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sj/order/goWorker")
    public ModelAndView goWorker(HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        String returnView = "business/workerList";
        return new ModelAndView(returnView);
    }

    @RequestMapping(value = "/sj/order/queryWorkerForPage")
    public void queryWorkerForPage(HttpServletRequest request,
                                   HttpServletResponse response) throws Exception {
        Page page = getPageByRequest(request);
        try {
            //获取查询条件
            SjWorker sjWorker = new SjWorker();
            SjSessionUser sjSessionUser = getSjCurrentUser(request);
            if (sjSessionUser.getType() == 3) {
                SjUser sjUser = sjUserService.getDao().queryByLoginId(sjSessionUser.getUserId(), 3);
                sjWorker.setCompanyLoginId(String.valueOf(sjUser.getId()));
            }
            sjWorker.setPage(page);
            List<Map<String, String>> sjWorkers = sjWorkerService.getDao().queryWorkerListForPage(sjWorker);
            for (Map<String, String> worker : sjWorkers) {
                SjUser sjUser = sjUserService.queryById(worker.get("company_id"));
                worker.put("companyName", sjUser.getName());
            }
            page.setData(sjWorkers);
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
        if (sjOrder.getState() >= 300) {
            if (sjOrder.getCompletedPerson() != null) {
                String user = sjUserService.userIdToUserIdName(sjOrder.getCompletedPerson());
                sjOrder.setCompletedPerson(user);
            }
            List<OrderContractPicture> contractPictures = orderContractPictureService.getDao().queryByOrderNo(sjOrder.getOrderNo());
            if (CollectionUtils.isNotEmpty(contractPictures)) {
                request.setAttribute("contractPictures", contractPictures);
            }
        }
        request.setAttribute("sjOrder", sjOrder);
        request.setAttribute("companyPictures", companyPictures);
        String returnView = "";
        if (sjOrder.getType() == 1) {
            if (sjOrder.getFeedbackPerson() != null) {
                SjUser sjUser = sjUserService.getDao().queryByLoginId(sjOrder.getFeedbackPerson(), null);
                if (sjUser != null) {
                    sjOrder.setFeedbackPerson(sjUser.getLoginId() + "/" + sjUser.getName());
                }
            }
            returnView = "business/detail2";
        } else {
            String projectIds = sjOrder.getProjectId();
            orderService.setWifi(1, sjOrder);
            orderService.setWifi(2, sjOrder);
            if (projectIds.contains("1") && projectIds.contains("2")) {
                request.setAttribute("isWifi", 0);
            } else if (projectIds.contains("1")) {
                request.setAttribute("isWifi", 1);
            } else if (projectIds.contains("2")) {
                request.setAttribute("isWifi", 2);
            } else {
                request.setAttribute("isWifi", 4);
            }
            returnView = "business/detail";
        }

        return new ModelAndView(returnView);
    }

    @Autowired
    private SjVirtualTeamService virtualTeamService;

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
    public ResultData approval(HttpServletRequest request,
                               HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        try {
            String id = request.getParameter("id");
            String note = request.getParameter("note");
            String isPast = request.getParameter("isPast");//是否通过   1通过   2不通过
            if (StringUtils.isBlank(note)) {
                return getSjResult(result, null, false, "0", null, "请填写备注");
            }
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

            if (sjOrder.getType() == 1) {
                SjUser sjUser = sjUserService.getDao().queryByLoginId(sjOrder.getCreateUserid(), null);
                CustomerDetail customerDetail = customerDetailService.getDao().queryByLoginId(sjUser.getId());
                SjVirtualTeam virtualTeam = virtualTeamService.getDao().queryByUnitId(customerDetail.getManagementUnitId());
                SjUser sjUser1 = sjUserService.queryById(virtualTeam.getLoginId());
                sjOrder.setStayPerson(sjUser1.getLoginId());
                SmsSendUtil.sjApprovalSend(virtualTeam, sjOrder.getOrderNo());
            } else {
                sjOrder.setStayPerson(orderService.setStayPerson(2));
            }

            orderService.saveUpdate(sjOrder);

            ApprovalNote approvalNote = new ApprovalNote();
            approvalNote.setOrderNo(sjOrder.getOrderNo());
            approvalNote.setNote(note);
            approvalNoteService.add(approvalNote);

            getSjResult(result, null, true, "0", null, "获取成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }

    /**
     * 反馈
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sj/order/feedback")
    @ResponseBody
    public ResultData feedback(HttpServletRequest request,
                               HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        try {
            String id = request.getParameter("id");
            String note = request.getParameter("feedNote");
            String isPast = request.getParameter("isPast");//是否通过   1转化   2不完结
            if (StringUtils.isBlank(note)) {
                return getSjResult(result, null, false, "0", null, "请填写备注");
            }
            SjOrder sjOrder = orderService.queryById(id);
            SjSessionUser su = getSjCurrentUser(request);
            sjOrder.setFeedbackPerson(su.getUserId());
            sjOrder.setFeedbackTime(new Date());
            sjOrder.setFeedbackNote(note);
            if ("1".equals(isPast)) {
                sjOrder.setState(300);
            } else if ("2".equals(isPast)) {
                sjOrder.setState(400);
            }
            orderService.saveUpdate(sjOrder);

            getSjResult(result, null, true, "0", null, "反馈成功");
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
            String projectIds = request.getParameter("projectIds");
            String orderId = request.getParameter("orderId");
            //获取省份地址
            List<Address> provinceL = addressService.queryByPid("0");
            //获取市地址
            List<Address> cityL = addressService.queryByPid("15");
            //获取区县地址
            List<Address> areal = addressService.queryByPid("1158");
            request.setAttribute("provinceL", provinceL);
            request.setAttribute("cityL", cityL);
            request.setAttribute("areal", areal);
            request.setAttribute("orderId", orderId);
            request.setAttribute("projectIds", projectIds);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String returnView = "business/againOrder";
        return new ModelAndView(returnView);
    }

    /**
     * 弹窗筛选查询企业
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
            String project = request.getParameter("projectIds");
            ConstructionCompany constructionCompany = new ConstructionCompany();
            constructionCompany.setProject(project);
            constructionCompany.setProvince(provinceId);
            constructionCompany.setCity(cityId);
            constructionCompany.setArea(areaId);
            constructionCompany.setPage(page);
            List<ConstructionCompany> companies = constructionCompanyService.queryListForPage(constructionCompany);
            for (ConstructionCompany company : companies) {
                String province = addressService.queryByAreaId(company.getProvince()).getArea();
                String city = addressService.queryByAreaId(company.getCity()).getArea();
                String area = addressService.queryByAreaId(company.getArea()).getArea();
                company.setAddress(province + city + area);
                List<String> projects1 = orderService.getProject(company.getProject());
                String projectName = orderService.listToString(projects1);
                company.setProjectNames(projectName);
                SjUser sjUser = sjUserService.getDao().queryById(company.getLoginId());
                company.setCompanyName(sjUser.getName());
            }
            page.setData(companies);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.renderJson(response, page);
    }

    @RequestMapping(value = "/sj/order/toVirtualTeam")
    public ModelAndView toVirtualTeam(HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {
        String returnView = "business/virtualTeamList";
        return new ModelAndView(returnView);
    }

    @RequestMapping(value = "/sj/order/queryVirtualTeamForPage")
    public void queryVirtualTeamForPage(HttpServletRequest request,
                                        HttpServletResponse response) throws Exception {
        Page page = getPageByRequest(request);
        try {
            //获取查询条件
            SjVirtualTeam virtualTeam = new SjVirtualTeam();
            virtualTeam.setPage(page);
            //t.id,t.login_id,t.management_unit_id,t.name,phone,t.post,t.office_num,m.management_unit,loginId
            List<Map> virtualTeams = virtualTeamService.getDao().queryListMapForPage(virtualTeam);
            page.setData(virtualTeams);
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
            String companyId = request.getParameter("userId");

            List<SjWorker> sjWorkers = sjWorkerService.getDao().queryByCompanyId(companyId);
            if (CollectionUtils.isEmpty(sjWorkers)) {
                return getSjResult(result, null, false, "0", null, "该单位没有员工");
            }
            SjWorker sjWorker = sjWorkers.get(0);
            SjUser sjUser = sjUserService.getDao().queryById(sjWorker.getLoginId());
            SjUser companyUser = sjUserService.getDao().queryById(companyId);
            SjOrder sjOrder = orderService.queryById(orderId);

            sjOrder.setState(300);
            sjOrder.setAssignTime(new Date());
            sjOrder.setAssignPerson(su.getUserId());
            sjOrder.setStayPerson(sjUser.getLoginId());
            //待施工人信息
            sjOrder.setBuildPerson(sjUser.getLoginId());
            SjUser sjUser1 = sjUserService.getDao().queryById(sjWorker.getCompanyLoginId());
            sjOrder.setBuildCompany(sjUser1.getName());
            sjOrder.setBuildPhone(sjUser.getPhone());
            sjOrder.setAssignWorkerId(sjUser.getLoginId());
            sjOrder.setAssignCompanyId(companyUser.getLoginId());
            orderService.saveUpdate(sjOrder);
            //工人接单数量加一
            sjWorker.setBuildingNum(sjWorker.getBuildingNum() + 1);
            sjWorkerService.saveUpdate(sjWorker);

            SmsSendUtil.sjAssignOrderSend(companyUser.getPhone(), sjOrder.getOrderNo(), 1);
            SmsSendUtil.sjAssignOrderSend(companyUser.getPhone(), sjOrder.getOrderNo(), 2);

            getSjResult(result, null, true, "0", null, "指派成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/sj/order/toRegisterCompany")
    public ModelAndView toRegisterCompany(HttpServletRequest request,
                                          HttpServletResponse response) {
        try {
            //获取省份地址
            List<Address> provinceL = addressService.queryByPid("0");
            List<SjProject> projects = projectService.queryList(null);
            request.setAttribute("projects", projects);
            request.setAttribute("provinceL", provinceL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String returnView = "business/addCompany";
        return new ModelAndView(returnView);
    }

    @RequestMapping(value = "/sj/order/toRegisterWorker")
    public ModelAndView toRegister(HttpServletRequest request,
                                   HttpServletResponse response) {
        try {
            List<SjUser> companys = sjUserService.getDao().queryByType(3);
            request.setAttribute("companys", companys);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String returnView = "business/addWorker";
        return new ModelAndView(returnView);
    }


    /**
     * 用户注册
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sj/order/register")
    @ResponseBody
    public ResultData register(HttpServletRequest request,
                               HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        SjSessionUser su = getSjCurrentUser(request);
        try {
            String type = request.getParameter("type");//1企业单位，2审批人，3指派人，4工人
            String phone = request.getParameter("phone");//注册人手机号
            String name = request.getParameter("name");//名字
            String person = request.getParameter("person");//负责人
            String provinceId = request.getParameter("addProvince");
            String cityId = request.getParameter("addCity");
            String areaId = request.getParameter("addCounty");
            String addressDetail = request.getParameter("addAddress");
            String[] projectIds = request.getParameterValues("projects");
            String companyId = request.getParameter("companyId");//企业Id
            if (StringUtils.isBlank(type) || StringUtils.isBlank(phone)) {
                return getSjResult(result, null, true, "0", null, "参数为空F");
            }
            if (phone.length() < 11) {
                return getSjResult(result, null, true, "0", null, "参数为空F");
            }
            SjUser sjUser = new SjUser();
            sjUser.setName(name);
            sjUser.setPhone(phone);
            String pwd = phone.substring(phone.length() - 6);
            sjUser.setPassword(MD5Util.encodePassword(pwd));

            if ("1".equals(type)) {
                if (StringUtils.isBlank(name) || StringUtils.isBlank(person) || StringUtils.isBlank(provinceId)
                        || StringUtils.isBlank(cityId) || StringUtils.isBlank(areaId)) {
                    return getSjResult(result, null, true, "0", null, "参数为空F");
                }
                SjUser sjUser1 = sjUserService.getDao().queryByName(name, 3);
                if (sjUser1 != null) {
                    return getSjResult(result, null, true, "0", null, "该企业名字已注册");
                }
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < projectIds.length; i++) {
                    sb.append(projectIds[i]);
                    sb.append(",");
                }

                String fristSpell = orderService.getFristSpell(provinceId, cityId);
                sjUser.setLoginId(SeqUtil.getNext(fristSpell));
                sjUser.setType(3);
                sjUserService.add(sjUser);

                SjUser sjUser2 = sjUserService.getDao().queryByLoginId(sjUser.getLoginId(), 3);

                ConstructionCompany company = new ConstructionCompany();
                company.setLoginId(sjUser2.getId());
                company.setProvince(provinceId);
                company.setCity(cityId);
                company.setArea(areaId);
                company.setAddressDetail(addressDetail);
                company.setProject(sb.toString());
                company.setPerson(person);
                company.setPhone(phone);
                company.setEndOrderNum(0);
                company.setPersonNum(0);
                constructionCompanyService.add(company);
                UserRole userRole = new UserRole();
                userRole.setLoginId(sjUser.getLoginId());
                userRole.setRoleId("COMPANY");
                userRoleService.add(userRole);
            }
            if ("2".equals(type)) {
                sjUser.setLoginId(SeqUtil.getNext("sj"));
                sjUser.setType(3);
                sjUserService.add(sjUser);
                UserRole userRole = new UserRole();
                userRole.setLoginId(sjUser.getLoginId());
                userRole.setRoleId("APPROVAL");
                userRoleService.add(userRole);
            }
            if ("3".equals(type)) {
                sjUser.setLoginId(SeqUtil.getNext("sj"));
                sjUser.setType(4);
                sjUserService.add(sjUser);

                UserRole userRole = new UserRole();
                userRole.setLoginId(sjUser.getLoginId());
                userRole.setRoleId("ASSIGN");
                userRoleService.add(userRole);
            }
            if ("4".equals(type)) {
                if (StringUtils.isBlank(companyId)) {
                    return getSjResult(result, null, true, "0", null, "参数为空F");
                }
                sjUser.setLoginId(SeqUtil.getNext("yg"));
                sjUser.setType(8);
                sjUserService.add(sjUser);

                SjUser sjUser1 = sjUserService.getDao().queryByLoginId(sjUser.getLoginId(), 8);

                SjWorker sjWorker = new SjWorker();
                sjWorker.setLoginId(sjUser1.getId());
                sjWorker.setCompanyLoginId(companyId);
                sjWorker.setIsDel(0);
                sjWorker.setBuildingNum(0);
                sjWorkerService.add(sjWorker);

                companyService.getDao().updatePersonAddNum(Integer.valueOf(companyId));

                UserRole userRole = new UserRole();
                userRole.setLoginId(sjUser.getLoginId());
                userRole.setRoleId("WORKER");
                userRoleService.add(userRole);
            }

            SmsSendUtil.sjRegisterUserSend(sjUser, pwd);
            getSjResult(result, null, true, "0", null, "指派成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/sj/order/toCompanyList")
    public ModelAndView toCompany(HttpServletRequest request,
                                  HttpServletResponse response) {
        try {
            //获取省份地址
            List<Address> provinceL = addressService.queryByPid("0");
            request.setAttribute("provinceL", provinceL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String returnView = "business/companyList";
        return new ModelAndView(returnView);
    }

    /**
     * 查询企业列表
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/sj/order/queryCompanyListForPage")
    public void queryCompanyListForPage(HttpServletRequest request,
                                        HttpServletResponse response) throws Exception {
        Page page = getPageByRequest(request);
        try {
            //获取查询条件
            String companyName = request.getParameter("companyName");
            String provinceId = request.getParameter("queryProvince");
            String cityId = request.getParameter("queryCity");
            String areaId = request.getParameter("queryCounty");
            String queryStartTime = request.getParameter("query_startTime");
            String queryEndTime = request.getParameter("query_endTime");
            String personPhone = request.getParameter("personPhone");
            ConstructionCompany constructionCompany = new ConstructionCompany();
            constructionCompany.setCompanyName(companyName);
            constructionCompany.setProvince(provinceId);
            constructionCompany.setCity(cityId);
            constructionCompany.setArea(areaId);
            if (StringUtils.isNotBlank(personPhone)) {
                if (orderService.isNumeric(personPhone)) {
                    constructionCompany.setPhone(personPhone);
                } else {
                    constructionCompany.setPerson(personPhone);
                }
            }
            constructionCompany.setQueryStartTime(queryStartTime);
            constructionCompany.setQueryEndTime(queryEndTime);
            constructionCompany.setPage(page);
            //loginId,companyName,province,city,area,addressDetail,person,phone,
            // project,createTime,personNum,endOrderNum,isCancel
            List<Map<String, Object>> companies = constructionCompanyService.getDao().queryCompanyListForPage(constructionCompany);
            for (Map<String, Object> company : companies) {
                String province = addressService.queryByAreaId(company.get("province").toString()).getArea();
                String city = addressService.queryByAreaId(company.get("city").toString()).getArea();
                String area = addressService.queryByAreaId(company.get("area").toString()).getArea();
                if (company.get("addressDetail") == null) {
                    company.put("address", province + city + area);
                } else {
                    company.put("address", province + city + area + company.get("addressDetail").toString());
                }
                company.put("areaAddress", province + city + area);
                List<String> projects1 = orderService.getProject(company.get("project").toString());
                String projectName = orderService.listToString(projects1);
                company.put("projectName", projectName);
            }
            page.setData(companies);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.renderJson(response, page);
    }

    //客户经理
    @RequestMapping(value = "/sj/order/toCustomerList")
    public ModelAndView toWorker(HttpServletRequest request,
                                 HttpServletResponse response) {
        String returnView = "business/customerList";
        return new ModelAndView(returnView);
    }

    /**
     * 客户经理列表
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/sj/order/queryCustomerListForPage")
    public void queryCustomerListForPage(HttpServletRequest request,
                                         HttpServletResponse response) throws Exception {
        Page page = getPageByRequest(request);
        try {
            //获取查询条件
            String namePhone = request.getParameter("namePhone");
            String ascription = request.getParameter("ascription");//归属
            String queryStartTime = request.getParameter("query_startTime");
            String queryEndTime = request.getParameter("query_endTime");
            String marketingNo = request.getParameter("marketingNo");
            CustomerDetail customerDetail = new CustomerDetail();
            customerDetail.setPage(page);
            if (StringUtils.isNotBlank(namePhone)) {
                if (orderService.isNumeric(namePhone)) {
                    customerDetail.setPhone(namePhone);
                } else {
                    customerDetail.setName(namePhone);
                }
            }
            customerDetail.setQueryStartTime(queryStartTime);
            customerDetail.setQueryEndTime(queryEndTime);
            customerDetail.setMarketingNo(marketingNo);
            if (StringUtils.isNotBlank(ascription)) {
                switch (ascription) {
                    case "1":
                        customerDetail.setCityCompanyId(1);
                        break;
                    case "2":
                        customerDetail.setManagementUnitId(1);
                        break;
                    case "3":
                        customerDetail.setBranchOfficeId(1);
                        break;
                    case "4":
                        customerDetail.setContractBodyId(1);
                        break;
                }
            }
            //login_id,name,phone,city_company_id,management_unit_id,branch_office_id,
            // contract_body_id,marketing_no,create_time,is_cancel
            List<Map<String, String>> customerDetails = customerDetailService.getDao().queryCustomerListForPage(customerDetail);
            for (Map<String, String> companies : customerDetails) {
                int totalNum = customerDetailService.getDao().queryByLoginIdState(companies.get("login_id"), null);
                companies.put("totalNum", String.valueOf(totalNum));
                int noPassNum = customerDetailService.getDao().queryByLoginIdState(companies.get("login_id"), "600");
                companies.put("noPassNum", String.valueOf(noPassNum));
                int endNum = customerDetailService.getDao().queryByLoginIdState(companies.get("login_id"), "500");
                companies.put("endNum", String.valueOf(endNum));
                int ingNum = customerDetailService.getDao().queryIngByLoginIdState(companies.get("login_id"), null);
                companies.put("ingNum", String.valueOf(ingNum));
            }
            page.setData(customerDetails);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.renderJson(response, page);
    }

    /**
     * 跳转登记单
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/sj/order/registerForm")
    public ModelAndView registerForm(HttpServletRequest request,
                                     HttpServletResponse response) {
        String orderId = request.getParameter("orderId");
        String isWifi = request.getParameter("isWifi");

        SjOrder sjOrder = orderService.queryById(orderId);
        Long mealId = Long.valueOf(isWifi);
        SjSetMeal sjSetMeal = new SjSetMeal();
        sjSetMeal.setId(mealId);
        List<SjSetMeal> sjSetMeals = sjSetMealService.queryList(sjSetMeal);

        SjWifiMonitorType wifiMonitorType = new SjWifiMonitorType();
        wifiMonitorType.setMealId(mealId);
        List<SjWifiMonitorType> wifiMonitorTypes = sjWifiMonitorTypeService.queryList(wifiMonitorType);

        SjPoe sjPoe = new SjPoe();
        sjPoe.setMealId(mealId);
        List<SjPoe> sjPoes = sjPoeService.queryList(sjPoe);

        SjSaveNet sjSaveNet = new SjSaveNet();
        sjSaveNet.setMealId(mealId);
        List<SjSaveNet> sjSaveNets = sjSaveNetService.queryList(sjSaveNet);

        request.setAttribute("sjSetMeal", sjSetMeals.get(0));
        request.setAttribute("modelL", wifiMonitorTypes);
        request.setAttribute("poeL", sjPoes);
        request.setAttribute("storageL", sjSaveNets);

        request.setAttribute("isWifi", isWifi);
        request.setAttribute("sjOrder", sjOrder);
        String returnView = "business/registerForm";
        return new ModelAndView(returnView);
    }

    /**
     * 上传合同照片
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sj/order/upContractImage")
    @ResponseBody
    public ResultData upContractImage(HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        try {
            String orderNo = request.getParameter("orderNo");
            //获取图片，保存图片到webapp同级inages/sj_images
            String savePath = serverPath(request.getServletContext().getRealPath("")) + System.getProperty("file.separator") + SystemConstant.IMAGE_PATH + System.getProperty("file.separator") + "sj_images" + System.getProperty("file.separator") + "sj_contract";
            String logoPath = getPath(request, "file", savePath);             //图片路径
            String imageUrl = getProjectUrl(request) + "/images/sj_images/sj_contract/" + logoPath.substring(logoPath.lastIndexOf("/") + 1);
            System.out.println("图片路径：" + savePath);
            OrderContractPicture contractPicture = new OrderContractPicture();
            contractPicture.setOrderNo(orderNo);
            contractPicture.setContractPictureUrl(imageUrl);
            orderContractPictureService.add(contractPicture);
            getSjResult(result, imageUrl, true, "0", null, "上传成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }

    /**
     * 竣工
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sj/order/contract")
    @ResponseBody
    public ResultData contract(HttpServletRequest request,
                               HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        try {
            String id = request.getParameter("id");
            SjOrder sjOrder = orderService.queryById(id);
            SjSessionUser su = getSjCurrentUser(request);
            sjOrder.setState(400);
            sjOrder.setCompletedPerson(su.getUserId());
            sjOrder.setEndTime(new Date());
            sjOrder.setStayPerson(orderService.setStayPerson(4));
            orderService.saveUpdate(sjOrder);

            getSjResult(result, null, true, "0", null, "获取成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }

    /**
     * 完成
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sj/order/endOrder")
    @ResponseBody
    public ResultData endOrder(HttpServletRequest request,
                               HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        try {
            String id = request.getParameter("id");
            SjOrder sjOrder = orderService.queryById(id);
            sjOrder.setState(500);
            sjOrder.setCompletedTime(new Date());
            orderService.saveUpdate(sjOrder);

            getSjResult(result, null, true, "0", null, "获取成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }

    /**
     * 录单
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sj/order/setRegisterForm")
    @ResponseBody
    public ResultData setRegisterForm(HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        try {
            String orderId = request.getParameter("orderId");
            String isWifi = request.getParameter("isWifi");
            Integer storageId = Integer.valueOf(request.getParameter("storageId"));
            Integer storageNum = Integer.valueOf(request.getParameter("storageNum"));
            Integer poeId = Integer.valueOf(request.getParameter("poeId"));
            Integer poeNum = Integer.valueOf(request.getParameter("poeNum"));
            Integer modelId = Integer.valueOf(request.getParameter("modelId"));
            Integer modelNum = Integer.valueOf(request.getParameter("modelNum"));
            Integer mealId = Integer.valueOf(request.getParameter("mealId"));
            SjOrder sjOrder = orderService.queryById(orderId);
            if (isWifi.equals("1")) {
                sjOrder.setMealId(mealId);
                sjOrder.setModelId(modelId);
                sjOrder.setModelNum(modelNum);
                sjOrder.setPoeId(poeId);
                sjOrder.setPoeNum(poeNum);
                sjOrder.setStorageId(storageId);
                sjOrder.setStorageNum(storageNum);
            } else {
                sjOrder.setMealWifiId(mealId);
                sjOrder.setModelWifiId(modelId);
                sjOrder.setModelWifiNum(modelNum);
                sjOrder.setPoeWifiId(poeId);
                sjOrder.setPoeWifiNum(poeNum);
                sjOrder.setStorageWifiId(storageId);
                sjOrder.setStorageWifiNum(storageNum);
            }
            orderService.saveUpdate(sjOrder);
            getResult(result, null, true, "0", "成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }

    /**
     * 注销
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sj/company/isCancellation")
    @ResponseBody
    public ResultData isCancellation(HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        try {
            String type = request.getParameter("type");//角色类型
            String loginId = request.getParameter("loginId");
            String isCancel = request.getParameter("isCancel");//是否注销   1注销   0恢复
            ConstructionCompany company = new ConstructionCompany();
            if (Integer.valueOf(type) == 8) {
                SjUser sjUser = sjUserService.getDao().queryByLoginId(loginId, 8);
                SjWorker sjWorker = sjWorkerService.getDao().queryByloginId(sjUser.getId());
                company = companyService.getDao().queryByLoginId(Integer.valueOf(sjWorker.getCompanyLoginId()));
                companyService.saveUpdate(company);
            }
            if ("1".equals(isCancel)) {
                sjUserService.getDao().updateCancel1(loginId, Integer.valueOf(type));
                company.setPersonNum(company.getPersonNum() - 1);
                getSjResult(result, null, true, "0", null, "注销成功");
            } else {
                sjUserService.getDao().updateCancel0(loginId, Integer.valueOf(type));
                company.setPersonNum(company.getPersonNum() + 1);
                getSjResult(result, null, true, "0", null, "恢复成功");
            }
            if (Integer.valueOf(type) == 8) {
                companyService.saveUpdate(company);
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }

    //企业导入
    @RequestMapping(value = "/sj/company/importIndex")
    public ModelAndView importcompanyIndex(HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {

        String returnView = "business/importCompany";
        return new ModelAndView(returnView);
    }

    //员工导入
    @RequestMapping(value = "/sj/worker/importIndex")
    public ModelAndView importworkerIndex(HttpServletRequest request,
                                          HttpServletResponse response) throws Exception {

        String returnView = "business/importWorker";
        return new ModelAndView(returnView);
    }

    //虚拟团队导入
    @RequestMapping(value = "/sj/virtualTeam/importIndex")
    public ModelAndView importVirtualTeamIndex(HttpServletRequest request,
                                               HttpServletResponse response) throws Exception {

        String returnView = "business/importVirtualTeam";
        return new ModelAndView(returnView);
    }

    /**
     * fileUpload:员工注册导入.
     *
     * @param myfile   上传的文件
     * @param request  请求实体
     * @param response 返回实体
     * @throws IOException 异常信息
     * @date 2016-5-9
     * @author
     */
    @RequestMapping(value = "/sj/worker/import")
    public void doImportWorker(
            @RequestParam("fileInput") MultipartFile myfile,
            HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        // 返回结果，默认失败
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
        ImportReport report = new ImportReport();
        StringBuffer errorMsg = new StringBuffer();
        try {
            if (myfile != null && StringUtils.isNotBlank(myfile.getOriginalFilename())) {
                String fileName = myfile.getOriginalFilename();
                //扩展名
                String extension = FilenameUtils.getExtension(fileName);
                if (!extension.equalsIgnoreCase("xls")) {
                    errorMsg.append("导入文件格式错误！只能导入excel文件！");
                } else {
                    sjUserService.importExcel(myfile, report, getCurrentUser(request), 1);
                    resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
                    resultMap.put(RESULTMAP_KEY_MSG, "导入成功");
                }
            } else {
                errorMsg.append("导入文件为空");
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMsg.append("导入失败");
            resultMap.put(RESULTMAP_KEY_MSG, "导入失败");
        }
        request.setAttribute("report", report);
        resultMap.put(RESULTMAP_KEY_DATA, report);
        renderJson(response, resultMap);
    }

    /**
     * fileUpload:企业单位注册导入.
     *
     * @param myfile   上传的文件
     * @param request  请求实体
     * @param response 返回实体
     * @throws IOException 异常信息
     * @date 2016-5-9
     * @author
     */
    @RequestMapping(value = "/sj/company/import")
    public void doImportCompany(
            @RequestParam("fileInput") MultipartFile myfile,
            HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        // 返回结果，默认失败
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
        ImportReport report = new ImportReport();
        StringBuffer errorMsg = new StringBuffer();
        try {
            if (myfile != null && StringUtils.isNotBlank(myfile.getOriginalFilename())) {
                String fileName = myfile.getOriginalFilename();
                //扩展名
                String extension = FilenameUtils.getExtension(fileName);
                if (!extension.equalsIgnoreCase("xls")) {
                    errorMsg.append("导入文件格式错误！只能导入excel文件！");
                } else {
                    sjUserService.importExcel(myfile, report, getCurrentUser(request), 2);
                    resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
                    resultMap.put(RESULTMAP_KEY_MSG, "导入成功");
                }
            } else {
                errorMsg.append("导入文件为空");
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMsg.append("导入失败");
            resultMap.put(RESULTMAP_KEY_MSG, "导入失败");
        }
        request.setAttribute("report", report);
        resultMap.put(RESULTMAP_KEY_DATA, report);
        renderJson(response, resultMap);
    }


    /**
     * fileUpload:企业单位注册导入.
     *
     * @param myfile   上传的文件
     * @param request  请求实体
     * @param response 返回实体
     * @throws IOException 异常信息
     * @date 2016-5-9
     * @author
     */
    @RequestMapping(value = "/sj/virtualTeam/import")
    public void doImportVirtualTeam(
            @RequestParam("fileInput") MultipartFile myfile,
            HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        // 返回结果，默认失败
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
        ImportReport report = new ImportReport();
        StringBuffer errorMsg = new StringBuffer();
        try {
            if (myfile != null && StringUtils.isNotBlank(myfile.getOriginalFilename())) {
                String fileName = myfile.getOriginalFilename();
                //扩展名
                String extension = FilenameUtils.getExtension(fileName);
                if (!extension.equalsIgnoreCase("xls")) {
                    errorMsg.append("导入文件格式错误！只能导入excel文件！");
                } else {
                    sjUserService.importExcel(myfile, report, getCurrentUser(request), 3);
                    resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
                    resultMap.put(RESULTMAP_KEY_MSG, "导入成功");
                }
            } else {
                errorMsg.append("导入文件为空");
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMsg.append("导入失败");
            resultMap.put(RESULTMAP_KEY_MSG, "导入失败");
        }
        request.setAttribute("report", report);
        resultMap.put(RESULTMAP_KEY_DATA, report);
        renderJson(response, resultMap);
    }
}
