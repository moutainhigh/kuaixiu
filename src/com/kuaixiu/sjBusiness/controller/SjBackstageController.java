package com.kuaixiu.sjBusiness.controller;

import com.common.base.controller.BaseController;
import com.common.importExcel.ImportReport;
import com.common.paginate.Page;
import com.common.util.SmsSendUtil;
import com.kuaixiu.sjBusiness.entity.ApprovalNote;
import com.kuaixiu.sjBusiness.entity.OrderCompanyPicture;
import com.kuaixiu.sjBusiness.entity.OrderContractPicture;
import com.kuaixiu.sjBusiness.entity.SjOrder;
import com.kuaixiu.sjBusiness.service.ApprovalNoteService;
import com.kuaixiu.sjBusiness.service.OrderCompanyPictureService;
import com.kuaixiu.sjBusiness.service.OrderContractPictureService;
import com.kuaixiu.sjBusiness.service.SjOrderService;
import com.kuaixiu.sjUser.entity.*;
import com.kuaixiu.sjUser.service.ConstructionCompanyService;
import com.kuaixiu.sjUser.service.CustomerDetailService;
import com.kuaixiu.sjUser.service.SjUserService;
import com.kuaixiu.sjUser.service.SjWorkerService;
import com.system.api.entity.ResultData;
import com.system.basic.address.entity.Address;
import com.system.basic.address.service.AddressService;
import com.system.basic.sequence.util.SeqUtil;
import org.apache.commons.collections.CollectionUtils;
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
            String isAssign = request.getParameter("isAssign");//是否查询指派订单
            SjOrder sjOrder = new SjOrder();
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
            if (StringUtils.isNotBlank(note)) {
                return getSjResult(result, null, false, "0", null, "请填写备注");
            }
            SjOrder sjOrder = orderService.queryById(id);
            SjSessionUser su = getSjCurrentUser(request);
            sjOrder.setApprovalPerson(su.getUserId());
            sjOrder.setApprovalTime(new Date());
            sjOrder.setApprovalNote(note);
            if ("1".equals(isPast)) {
                if (sjOrder.getType() == 1) {
                    sjOrder.setState(500);
                } else if (sjOrder.getType() == 2) {
                    sjOrder.setState(200);
                }
            } else if ("2".equals(isPast)) {
                sjOrder.setState(600);
            }
            sjOrder.setStayPerson(orderService.setStayPerson(2));
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
            String streetId = request.getParameter("addStreet");
            String project = request.getParameter("projectIds");
            List<String> projects = new ArrayList<>();
            if (StringUtils.isNotEmpty(project)) {
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
                SjUser sjUser = sjUserService.getDao().queryByLoginId(company.getLoginId(), null);
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

            List<SjWorker> sjWorkers = sjWorkerService.getDao().queryByCompanyId(userId);
            if (CollectionUtils.isEmpty(sjWorkers)) {
                return getSjResult(result, null, true, "0", null, "该单位没有员工");
            }
            SjWorker sjWorker = sjWorkers.get(0);
            SjUser sjUser = sjUserService.getDao().queryByLoginId(sjWorker.getLoginId(), null);
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
            sjWorker.setBuildingNum(sjWorker.getBuildingNum() + 1);
            sjWorkerService.saveUpdate(sjWorker);

            getSjResult(result, null, true, "0", null, "指派成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/sj/order/toRegister")
    public ModelAndView toRegister(HttpServletRequest request,
                                   HttpServletResponse response) {
        try {
            //获取省份地址
            List<Address> provinceL = addressService.queryByPid("0");
            request.setAttribute("provinceL", provinceL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String returnView = "business/register";
        return new ModelAndView(returnView);
    }

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
            String personPhone = request.getParameter("personPhone");//负责人手机号
            String provinceId = request.getParameter("provinceId");
            String cityId = request.getParameter("cityId");
            String areaId = request.getParameter("areaId");
            String projects = request.getParameter("projects");
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
            sjUser.setPassword(pwd);
            if ("1".equals(type)) {
                if (StringUtils.isBlank(name) || StringUtils.isBlank(person) || StringUtils.isBlank(personPhone)
                        || StringUtils.isBlank(provinceId) || StringUtils.isBlank(cityId) || StringUtils.isBlank(areaId)) {
                    return getSjResult(result, null, true, "0", null, "参数为空F");
                }
                String fristSpell = orderService.getFristSpell(provinceId, cityId);
                sjUser.setLoginId(SeqUtil.getNext(fristSpell));
                sjUser.setType(3);

                ConstructionCompany company = new ConstructionCompany();
                company.setLoginId(sjUser.getLoginId());
                company.setProvince(provinceId);
                company.setCity(cityId);
                company.setArea(areaId);
                company.setProject(projects);
                company.setPerson(person);
                company.setPhone(personPhone);
                constructionCompanyService.add(company);
            }
            if ("2".equals(type)) {
                sjUser.setLoginId(SeqUtil.getNext("sj"));
                sjUser.setType(3);
            }
            if ("3".equals(type)) {
                sjUser.setLoginId(SeqUtil.getNext("sj"));
                sjUser.setType(4);
            }
            if ("4".equals(type)) {
                if (StringUtils.isBlank(companyId)) {
                    return getSjResult(result, null, true, "0", null, "参数为空F");
                }
                sjUser.setLoginId(SeqUtil.getNext("yg"));
                sjUser.setType(8);
                SjWorker sjWorker = new SjWorker();
                sjWorker.setLoginId(sjUser.getLoginId());
                sjWorker.setCompanyLoginId(companyId);
                sjWorkerService.add(sjWorker);
            }
            sjUserService.add(sjUser);

            SmsSendUtil.sjRegisterUserSend(sjUser);

            getSjResult(result, null, true, "0", null, "指派成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/sj/order/toCompany")
    public ModelAndView toCompany(HttpServletRequest request,
                                  HttpServletResponse response) {
        try {
            //获取省份地址
            List<Address> provinceL = addressService.queryByPid("0");
            request.setAttribute("provinceL", provinceL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String returnView = "business/company";
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
            String provinceId = request.getParameter("addProvince");
            String cityId = request.getParameter("addCity");
            String areaId = request.getParameter("addCounty");
            ConstructionCompany constructionCompany = new ConstructionCompany();
            constructionCompany.setProvince(provinceId);
            constructionCompany.setCity(cityId);
            constructionCompany.setArea(areaId);
            constructionCompany.setPage(page);
            //loginId,companyName,province,city,area,addressDetail,person,phone,
            // project,createTime,personNum,endOrderNum,isCancel
            List<Map<String, String>> companies = constructionCompanyService.getDao().queryCompanyListForPage(constructionCompany);
            for (Map<String, String> company : companies) {
                String province = addressService.queryByAreaId(company.get("province")).getArea();
                String city = addressService.queryByAreaId(company.get("city")).getArea();
                String area = addressService.queryByAreaId(company.get("area")).getArea();
                company.put("address", province + city + area + company.get("addressDetail"));
                company.put("areaAddress", province + city + area);
                List<String> projects1 = orderService.getProject(company.get("project"));
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
    @RequestMapping(value = "/sj/order/toCustomer")
    public ModelAndView toWorker(HttpServletRequest request,
                                 HttpServletResponse response) {
        String returnView = "business/customer";
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
            String name = request.getParameter("name");
            String phone = request.getParameter("phone");
            String ascription = request.getParameter("ascription");//归属
            String queryStartTime = request.getParameter("queryStartTime");
            String queryEndTime = request.getParameter("queryEndTime");
            String marketingNo = request.getParameter("marketingNo");
            CustomerDetail customerDetail = new CustomerDetail();
            customerDetail.setName(name);
            customerDetail.setPhone(phone);
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
}
