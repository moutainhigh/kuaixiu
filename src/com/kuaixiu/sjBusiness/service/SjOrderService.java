package com.kuaixiu.sjBusiness.service;


import com.alibaba.fastjson.JSONObject;
import com.common.base.service.BaseService;
import com.common.importExcel.ExcelUtil;
import com.common.importExcel.ImportError;
import com.common.importExcel.ImportReport;
import com.common.paginate.Page;
import com.common.util.ConverterUtil;
import com.common.util.MD5Util;
import com.common.util.NOUtil;
import com.common.wechat.common.util.StringUtils;
import com.kuaixiu.card.entity.BatchCard;
import com.kuaixiu.card.entity.BatchImport;
import com.kuaixiu.sjBusiness.dao.SjOrderMapper;
import com.kuaixiu.sjBusiness.entity.*;
import com.kuaixiu.sjSetMeal.entity.SjPoe;
import com.kuaixiu.sjSetMeal.entity.SjSaveNet;
import com.kuaixiu.sjSetMeal.entity.SjSetMeal;
import com.kuaixiu.sjSetMeal.entity.SjWifiMonitorType;
import com.kuaixiu.sjSetMeal.service.SjPoeService;
import com.kuaixiu.sjSetMeal.service.SjSaveNetService;
import com.kuaixiu.sjSetMeal.service.SjSetMealService;
import com.kuaixiu.sjSetMeal.service.SjWifiMonitorTypeService;
import com.kuaixiu.sjUser.entity.ConstructionCompany;
import com.kuaixiu.sjUser.entity.SjSessionUser;
import com.kuaixiu.sjUser.entity.SjUser;
import com.kuaixiu.sjUser.entity.UserRole;
import com.kuaixiu.sjUser.service.SjUserService;
import com.system.basic.address.entity.Address;
import com.system.basic.address.service.AddressService;
import com.system.basic.sequence.util.SeqUtil;
import com.system.basic.user.entity.SessionUser;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * SjOrder Service
 *
 * @CreateDate: 2019-05-08 下午12:48:51
 * @version: V 1.0
 */
@Service("sjOrderService")
public class SjOrderService extends BaseService<SjOrder> {

    @Autowired
    private SjOrderMapper<SjOrder> mapper;
    @Autowired
    private AddressService addressService;
    @Autowired
    private SjProjectService projectService;
    @Autowired
    private OrderCompanyPictureService orderCompanyPictureService;
    @Autowired
    private SjUserService sjUserService;
    @Autowired
    private SjSetMealService sjSetMealService;
    @Autowired
    private SjWifiMonitorTypeService sjWifiMonitorTypeService;
    @Autowired
    private SjPoeService sjPoeService;
    @Autowired
    private SjSaveNetService sjSaveNetService;


    public SjOrderMapper<SjOrder> getDao() {
        return mapper;
    }

    private static Map<Integer, String> titleMap = new HashMap<Integer, String>();

    //**********自定义方法***********

    public JSONObject sjListOrderToObejct(List<SjOrder> orders, Page page) {
        List<JSONObject> jsonObjects = new ArrayList<>();
        for (SjOrder o : orders) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", o.getType());
            jsonObject.put("orderNo", o.getOrderNo());
            jsonObject.put("companyName", o.getCompanyName());
            jsonObject.put("state", o.getState());
            jsonObject.put("createTime", o.getCreateTime());
            jsonObject.put("stayPerson", o.getStayPerson());
            jsonObject.put("projects", getProject(o.getProjectId()));
            SjUser user = sjUserService.getDao().queryByLoginId(o.getStayPerson(), null);
            if(user!=null){
                jsonObject.put("stayPersonName", user.getName());
            }else{
                jsonObject.put("stayPersonName", "");
            }
            jsonObjects.add(jsonObject);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pageSize", page.getPageSize());
        jsonObject.put("pageIndex", page.getCurrentPage());
        jsonObject.put("recordsTotal", page.getRecordsTotal());
        jsonObject.put("totalPage", page.getTotalPage());
        jsonObject.put("sjOrders", jsonObjects);
        return jsonObject;
    }

    public JSONObject sjListReOrderToObejct(List<SjOrder> orders, Page page) {

        List<JSONObject> jsonObjects = new ArrayList<>();
        for (SjOrder o : orders) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", o.getType());
            jsonObject.put("orderNo", o.getOrderNo());
            jsonObject.put("companyName", o.getCompanyName());
            jsonObject.put("state", o.getState());
            jsonObject.put("createTime", o.getCreateTime());
            jsonObject.put("stayPerson", o.getStayPerson());
            jsonObject.put("projects", getProject(o.getProjectId()));
            jsonObjects.add(jsonObject);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pageSize", page.getPageSize());
        jsonObject.put("pageIndex", page.getCurrentPage());
        jsonObject.put("recordsTotal", page.getRecordsTotal());
        jsonObject.put("totalPage", page.getTotalPage());
        jsonObject.put("sjOrders", jsonObjects);
        return jsonObject;
    }

    public JSONObject sjOrderToObejct(SjOrder o) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("phone", o.getCreateUserid());
        jsonObject.put("type", o.getType());
        jsonObject.put("crmNo", o.getCrmNo());
        jsonObject.put("orderNo", o.getOrderNo());
        jsonObject.put("companyName", o.getCompanyName());
        jsonObject.put("state", o.getState());
        jsonObject.put("createTime", o.getCreateTime());
        jsonObject.put("stayPerson", o.getStayPerson());
        jsonObject.put("projects", getProjectIds(o.getProjectId()));
        // 获取订单处理人信息
        SjUser user = sjUserService.getDao().queryByLoginId(o.getStayPerson(), null);
        if(user!=null){
            jsonObject.put("stayPersonName", user.getName());
            jsonObject.put("stayPersonPhone", user.getPhone());
        }else{
            jsonObject.put("stayPersonName", "");
            jsonObject.put("stayPersonPhone", "");
        }

        String province = addressService.queryByAreaId(o.getProvinceId()).getArea();
        String city = addressService.queryByAreaId(o.getCityId()).getArea();
        String area = addressService.queryByAreaId(o.getAreaId()).getArea();
        String street = "";
        if (StringUtils.isNotBlank(o.getStreetId())) {
            Address address = addressService.queryByAreaId(o.getStreetId());
            street = address.getArea();
            jsonObject.put("streetId", o.getStreetId());
            jsonObject.put("streetName", street);
        }
        jsonObject.put("provinceId", o.getProvinceId());
        jsonObject.put("provinceName", province);
        jsonObject.put("cityId", o.getCityId());
        jsonObject.put("cityName", city);
        jsonObject.put("areaId", o.getAreaId());
        jsonObject.put("areaName", area);
        String addressDetail = "";
        if (StringUtils.isNotBlank(o.getAddressDetail())) {
            addressDetail = o.getAddressDetail();
            jsonObject.put("addressDetail", addressDetail);
        }
        jsonObject.put("address", province + city + area + street + addressDetail);
        jsonObject.put("person", o.getPerson());
        jsonObject.put("personPhone", o.getPhone());
        if (o.getType() == 2) {
            if (StringUtils.isNotBlank(o.getResponsibleIdNumber()) && StringUtils.isNotBlank(o.getResponsibleName())) {
                jsonObject.put("responsibleName", o.getResponsibleName());
                jsonObject.put("responsibleIdNumber", o.getResponsibleIdNumber());
            }
            jsonObject.put("ap", o.getSingle());
            jsonObject.put("monitor", o.getGroupNet());
        } else {
            if (o.getState() > 200 && o.getState() != 600) {
                SjUser sjUser = sjUserService.getDao().queryByLoginId(o.getFeedbackPerson(), null);
                jsonObject.put("feedbackPerson", sjUser.getName() + "/" + sjUser.getLoginId());
                jsonObject.put("feedbackTime", o.getFeedbackTime());
                jsonObject.put("feedbackNote", o.getFeedbackNote());
            }
        }
        jsonObject.put("images", getImages(o.getOrderNo()));
        if (o.getState() >= 200) {
            SjUser sjUser = sjUserService.getDao().queryByLoginId(o.getApprovalPerson(), null);
            System.out.println("用户信息："+JSONObject.toJSONString(sjUser));
            if(sjUser!=null){
                jsonObject.put("approvalPerson", sjUser.getName() + "/" + sjUser.getLoginId());
            }else{
                jsonObject.put("approvalPerson", "");
            }
            jsonObject.put("approvalTime", o.getApprovalTime());
            jsonObject.put("approvalNote", o.getApprovalNote());
        }
        return jsonObject;
    }

    public void createOrder(String projectId, SjOrder sjOrder) {
        if (projectId.contains("1") && projectId.contains("2") &&
                (projectId.contains("3") || projectId.contains("4") || projectId.contains("5") || projectId.contains("6"))) {
            sjOrder.setOrderNo(NOUtil.getNo("NB-"));
            sjOrder.setProjectId("1");
            this.add(removeSjOrder(sjOrder));
            sjOrder.setOrderNo(NOUtil.getNo("NB-"));
            sjOrder.setProjectId("2");
            this.add(removeWifiSjOrder(sjOrder));
            sjOrder.setOrderNo(NOUtil.getNo("NB-"));
            sjOrder.setProjectId(org.apache.commons.lang3.StringUtils.remove(projectId, "1,2,"));
            this.add(removeAllSjOrder(sjOrder));
        } else if (projectId.contains("1") &&
                (projectId.contains("3") || projectId.contains("4") || projectId.contains("5") || projectId.contains("6"))) {
            sjOrder.setOrderNo(NOUtil.getNo("NB-"));
            sjOrder.setProjectId("1");
            this.add(removeSjOrder(sjOrder));
            sjOrder.setOrderNo(NOUtil.getNo("NB-"));
            sjOrder.setProjectId(org.apache.commons.lang3.StringUtils.remove(projectId, "1,"));
            this.add(removeAllSjOrder(sjOrder));
        } else if (projectId.contains("2") &&
                (projectId.contains("3") || projectId.contains("4") || projectId.contains("5") || projectId.contains("6"))) {
            sjOrder.setOrderNo(NOUtil.getNo("NB-"));
            sjOrder.setProjectId("2");
            this.add(removeWifiSjOrder(sjOrder));
            sjOrder.setOrderNo(NOUtil.getNo("NB-"));
            sjOrder.setProjectId(org.apache.commons.lang3.StringUtils.remove(projectId, "2,"));
            this.add(removeAllSjOrder(sjOrder));
        } else if (projectId.contains("1") && projectId.contains("2")) {
            sjOrder.setOrderNo(NOUtil.getNo("NB-"));
            sjOrder.setProjectId("1");
            this.add(removeSjOrder(sjOrder));
            sjOrder.setOrderNo(NOUtil.getNo("NB-"));
            sjOrder.setProjectId("2");
            this.add(removeWifiSjOrder(sjOrder));
        }  else if (projectId.equals("1")) {
            sjOrder.setOrderNo(NOUtil.getNo("NB-"));
            sjOrder.setProjectId("1");
            this.add(removeSjOrder(sjOrder));
        } else if (projectId.equals("2")) {
            sjOrder.setOrderNo(NOUtil.getNo("NB-"));
            sjOrder.setProjectId("2");
            this.add(removeWifiSjOrder(sjOrder));
        } else {
            sjOrder.setOrderNo(NOUtil.getNo("NB-"));
            sjOrder.setProjectId(projectId);
            this.add(removeAllSjOrder(sjOrder));
        }
    }

    private SjOrder removeSjOrder(SjOrder sjOrder) {
        SjOrder sjOrder1 = new SjOrder();
        BeanUtils.copyProperties(sjOrder, sjOrder1);
        sjOrder1.setMealId(0);
        sjOrder1.setModelId(0);
        sjOrder1.setModelNum(0);
        sjOrder1.setPoeId(0);
        sjOrder1.setPoeNum(0);
        sjOrder1.setStorageId(0);
        sjOrder1.setStorageNum(0);
        return sjOrder1;
    }

    private SjOrder removeWifiSjOrder(SjOrder sjOrder) {
        SjOrder sjOrder1 = new SjOrder();
        BeanUtils.copyProperties(sjOrder, sjOrder1);
        sjOrder1.setMealWifiId(0);
        sjOrder1.setModelWifiId(0);
        sjOrder1.setModelWifiNum(0);
        sjOrder1.setPoeWifiId(0);
        sjOrder1.setPoeWifiNum(0);
        sjOrder1.setStorageWifiId(0);
        sjOrder1.setStorageWifiNum(0);
        return sjOrder1;
    }

    private SjOrder removeAllSjOrder(SjOrder sjOrder) {
        SjOrder sjOrder1 = new SjOrder();
        BeanUtils.copyProperties(sjOrder, sjOrder1);
        sjOrder1.setMealId(0);
        sjOrder1.setModelId(0);
        sjOrder1.setModelNum(0);
        sjOrder1.setPoeId(0);
        sjOrder1.setPoeNum(0);
        sjOrder1.setStorageId(0);
        sjOrder1.setStorageNum(0);
        sjOrder1.setMealWifiId(0);
        sjOrder1.setModelWifiId(0);
        sjOrder1.setModelWifiNum(0);
        sjOrder1.setPoeWifiId(0);
        sjOrder1.setPoeWifiNum(0);
        sjOrder1.setStorageWifiId(0);
        sjOrder1.setStorageWifiNum(0);
        return sjOrder1;
    }

    public void getSjDetail(SjOrder sjOrder) {
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
        List<String> projects = this.getProject(sjOrder.getProjectId());
        String projectName = this.listToString(projects);
        sjOrder.setProjectNames(projectName);
        sjOrder.setStrCreateTime(sdf.format(sjOrder.getCreateTime()));
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
    }

    public void getCompanies(List<ConstructionCompany> companies) {
        for (ConstructionCompany company : companies) {
            String province = addressService.queryByAreaId(company.getProvince()).getArea();
            String city = addressService.queryByAreaId(company.getCity()).getArea();
            String area = addressService.queryByAreaId(company.getArea()).getArea();
            company.setAddress(province + city + area);
            List<String> projects1 = this.getProject(company.getProject());
            String projectName = this.listToString(projects1);
            company.setProjectNames(projectName);
            SjUser sjUser = sjUserService.getDao().queryById(company.getLoginId());
            company.setCompanyName(sjUser.getName());
        }
    }

    public void getCompany(List<Map<String, Object>> companies) {
        for (Map<String, Object> company : companies) {
            String province = addressService.queryByAreaId(company.get("province").toString()).getArea();
            String city = addressService.queryByAreaId(company.get("city").toString()).getArea();
            String area = addressService.queryByAreaId(company.get("area").toString()).getArea();
            if (company.get("addressDetail") == null) {
                company.put("address", province + city + area);
            } else {
                company.put("address", province + city + area + company.get("addressDetail").toString());
            }
            if (company.get("service_area") != null) {
                String serviceArea = company.get("service_area").toString();
                StringBuilder sb = new StringBuilder();
                if (serviceArea.contains(",")) {
                    String[] serviceAreas = serviceArea.split(",");
                    for (int i = 0; i < serviceAreas.length; i++) {
                        Address address = addressService.queryByAreaId(serviceAreas[i]);
                        sb.append(address.getArea());
                        sb.append(",");
                    }
                } else {
                    Address address = addressService.queryByAreaId(serviceArea);
                    sb.append(address.getArea());
                }
                company.put("serviceArea", sb.toString());
            } else {
                company.put("serviceArea", "");
            }

            List<String> projects1 = this.getProject(company.get("project").toString());
            String projectName = this.listToString(projects1);
            company.put("projectName", projectName);
        }
    }

    private List<String> getImages(String orderNo) {
        List<String> images = new ArrayList<>();
        List<OrderCompanyPicture> companyPicture = orderCompanyPictureService.getDao().queryByOrderNo(orderNo);
        for (OrderCompanyPicture companyPicture1 : companyPicture) {
            images.add(companyPicture1.getCompanyPictureUrl());
        }
        return images;
    }

    public List<JSONObject> getProjectIds(String projectIds) {
        String[] projectIds1 = projectIds.split(",");
        List<JSONObject> projects = new ArrayList<>();
        for (int i = 0; i < projectIds1.length; i++) {
            JSONObject object = new JSONObject();
            SjProject project = projectService.queryById(projectIds1[i]);
            object.put("projectId", project.getId());
            object.put("project", project.getProject());
            projects.add(object);
        }
        return projects;
    }

    public List<String> getProject(String projectIds) {
        String[] projectIds1 = projectIds.split(",");
        List<String> projects = new ArrayList<>();
        for (int i = 0; i < projectIds1.length; i++) {
            String project = projectService.queryById(projectIds1[i]).getProject();
            projects.add(project);
        }
        return projects;
    }

    public String listToString(List<String> projects) {
        StringBuilder sb = new StringBuilder();
        for (String project : projects) {
            sb.append(project);
            sb.append(",");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * 判断是否纯数字
     *
     * @param str
     * @return
     */
    public boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }


    public String getFristSpell(String provinceId, String cityId) {
        Address province = addressService.queryByAreaId(provinceId);
        String fristSpell = ConverterUtil.getFirstSpellForAreaName(province.getArea());
        //获取市名称首字母
        Address city = addressService.queryByAreaId(cityId);
        fristSpell += ConverterUtil.getFirstSpellForAreaName(city.getArea());
        return fristSpell;
    }


    public void setWifi(Integer type, SjOrder order) {
        if (type == 2) {
            SjSetMeal sjSetMeal = sjSetMealService.queryById(order.getMealId());
            SjWifiMonitorType wifiMonitorType = sjWifiMonitorTypeService.queryById(order.getModelId());
            SjPoe sjPoe = sjPoeService.queryById(order.getPoeId());
            SjSaveNet sjSaveNet = sjSaveNetService.queryById(order.getStorageId());
            if (sjSetMeal != null) {
                order.setMealName(sjSetMeal.getMealName());
            }
            if (wifiMonitorType != null) {
                order.setModelName(wifiMonitorType.getMonitorTypeWifiName());
            }
            if (sjPoe != null) {
                order.setPoeName(sjPoe.getPoeName());
            }
            if (sjSaveNet != null) {
                order.setStorageName(sjSaveNet.getSaveNetName());
            }
        } else {
            SjSetMeal sjSetMeal = sjSetMealService.queryById(order.getMealWifiId());
            SjWifiMonitorType wifiMonitorType = sjWifiMonitorTypeService.queryById(order.getModelWifiId());
            SjPoe sjPoe = sjPoeService.queryById(order.getPoeWifiId());
            SjSaveNet sjSaveNet = sjSaveNetService.queryById(order.getStorageWifiId());
            if (sjSetMeal != null) {
                order.setMealWifiName(sjSetMeal.getMealName());
            }
            if (wifiMonitorType != null) {
                order.setModelWifiName(wifiMonitorType.getMonitorTypeWifiName());
            }
            if (sjPoe != null) {
                order.setPoeWifiName(sjPoe.getPoeName());
            }
            if (sjSaveNet != null) {
                order.setStorageWifiName(sjSaveNet.getSaveNetName());
            }
        }
    }

    /**
     * 1://审批人 2://指派人 3://施工人 4://竣工人
     *
     * @param num
     * @return
     */
    public String setStayPerson(Integer num) {
        String stayPerson = "";
        switch (num) {
            case 1://审批人
                stayPerson = "admin";
                break;
            case 2://指派人
                stayPerson = "admin";
                break;
            case 3://施工人
                stayPerson = "admin";
                break;
            case 4://竣工人
                stayPerson = "admin";
                break;
        }
        return stayPerson;
    }

    public Integer setOrderType(Integer num, Integer type) {
        Integer ordertype = null;
        if (type == 2) {
            switch (num) {
                case 1:
                    break;
                case 2:
                    ordertype = 100;
                    break;
                case 3:
                    ordertype = 600;
                    break;
                case 4:
                    ordertype = 300;
                    break;
                case 5:
                    ordertype = 500;
                    break;
            }
        } else {
            switch (num) {
                case 1:
                    break;
                case 2:
                    ordertype = 100;
                    break;
                case 3:
                    ordertype = 200;
                    break;
                case 4:
                    ordertype = 300;
                    break;
                case 5:
                    ordertype = 400;
                    break;
                case 6:
                    ordertype = 600;
                    break;
            }
        }

        return ordertype;
    }


    /**
     * 已Excel形式导出列表数据
     *
     * @param params
     */
    @SuppressWarnings("rawtypes")
    public void expDataExcel(Map<String, Object> params) {
        HttpServletResponse response = (HttpServletResponse) params.get("response");
        //获取登录用户
        SjSessionUser sjSessionUser = (SjSessionUser) params.get("user");
        //获取查询条件
        String orderNo = MapUtils.getString(params, "orderNo");
        String type = MapUtils.getString(params, "type");
        String queryStartTime = MapUtils.getString(params, "query_startTime");
        String queryEndTime = MapUtils.getString(params, "query_endTime");
        String createUser = MapUtils.getString(params, "createUser");
        String companyName = MapUtils.getString(params, "companyName");
        String state = MapUtils.getString(params, "state");
        String isAssign = MapUtils.getString(params, "isAssign");//是否查询指派订单
        String responsibleName = MapUtils.getString(params, "responsibleName");//负责人姓名
        String responsibleIdNumber = MapUtils.getString(params, "responsibleIdNumber");//负责人身份证号
        SjOrder sjOrder = new SjOrder();
        String idStr = MapUtils.getString(params, "ids");
        if (StringUtils.isNotBlank(idStr)) {
            String[] ids = org.apache.commons.lang3.StringUtils.split(idStr, ",");
            sjOrder.setQueryIds(Arrays.asList(ids));
        }
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
        sjOrder.setResponsibleIdNumber(responsibleIdNumber);
        sjOrder.setResponsibleName(responsibleName);
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
            if (isNumeric(createUser)) {
                sjOrder.setCreateUserid(createUser);
            } else {
                sjOrder.setCreateName(createUser);
            }
        }
        List<Map<String, Object>> maps = this.getDao().queryImportList(sjOrder);
        for (Map<String, Object> map : maps) {
            List<String> projects = getProject(map.get("project_id").toString());
            String projectName = listToString(projects);
            map.put("projectName", projectName);
        }

        List<List<Map<String, Object>>> lists = new ArrayList<>();
        List<String> listAgent = new ArrayList<>();
        //创建street
        setListAgent(lists, listAgent, maps);
        //excel标题
        String[] header = new String[]{};
        if (type.equals("1")) {
            header = new String[]{"订单编号", "创建时间", "类型", "创建人/创建名字", "企业名字", "联系人/联系电话", "产品需求",
                    "地址", "审批人", "审批备注", "审批时间", "反馈人", "反馈备注", "反馈时间", "状态"};
        } else {
            header = new String[]{"订单编号", "创建时间", "类型", "创建人/创建名字", "企业名字", "联系人/联系电话",
                    "负责人姓名/身份证号", "产品需求", "地址", "审批人", "审批备注", "审批时间", "ORM编号", "指派人",
                    "指派时间", "施工人", "施工单位", "完成时间", "竣工人", "竣工时间", "监控机型", "监控机型数量",
                    "监控poe", "监控poe数量", "监控存储", "监控存储数量", "wifi无线", "wifi无线数量", "wifipoe",
                    "wifipoe数量", "wifi网关", "wifi网关数量", "是否导入安装资产", "状态"};
        }

// 导出到多个sheet中--------------------------------------------------------------------------------开始
        // 创建一个EXCEL
        HSSFWorkbook wb = new HSSFWorkbook();
        // 循环经销商，每个经销商一个sheet
        for (int i = 0; i < listAgent.size(); i++) {
            // 第 i 个sheet,以经销商命名
            HSSFSheet sheet = wb.createSheet(listAgent.get(i).toString());
            // 第i个sheet第二行为列名
            HSSFRow rowFirst = sheet.createRow(0);
            // 写标题
            for (int j = 0; j < header.length; j++) {
                // 获取第一行的每一个单元格
                HSSFCell cell = rowFirst.createCell(j);
                // 往单元格里面写入值
                cell.setCellValue(header[j]);
            }
            for (int j = 0; j < lists.get(i).size(); j++) {
                Map<?, ?> map = (Map<?, ?>) lists.get(i).get(j);
                // 创建数据行，从第三行开始
                HSSFRow row = sheet.createRow(j + 1);
                // 设置对应单元格的值
                getRow(row, map, type);
            }
        }
        // 写出文件（path为文件路径含文件名）
        OutputStream os = null;
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/x-download");
            String filedisplay = "商机单导出.xls";
            //防止文件名含有中文乱码
            filedisplay = new String(filedisplay.getBytes("gb2312"), "ISO8859-1");
            response.setHeader("Content-Disposition", "attachment;filename=" + filedisplay);

            os = response.getOutputStream();
            wb.write(os);
        } catch (Exception e) {
            System.out.println("导出文件出错了.....\n" + e.getMessage());
        } finally {
            try {
                os.flush();
                os.close();
            } catch (IOException e) {
                System.out.println("导出文件出错了.....\n" + e.getMessage());
            }
        }
        // 导出到多个sheet中---------------------------------------------------------------------------------结束
    }


    private HSSFRow getRow(HSSFRow row, Map<?, ?> map, String type) {
        // 设置对应单元格的值
        int count = 0;
        if (map.get("order_no") == null) {
            row.createCell(count).setCellValue("");
        } else {
            row.createCell(count).setCellValue(map.get("order_no").toString());
        }
        count = count + 1;
        if (map.get("create_time") == null) {
            row.createCell(count).setCellValue("");
        } else {
            row.createCell(count).setCellValue(map.get("create_time").toString());
        }
        count = count + 1;
        if (map.get("type") == null) {
            row.createCell(count).setCellValue("");
        } else {
            if ("1".equals(map.get("type").toString())) {
                row.createCell(count).setCellValue("商机");
            } else {
                row.createCell(count).setCellValue("派单");
            }
        }
        count = count + 1;
        if (map.get("create_userid") == null) {
            row.createCell(count).setCellValue("");
        } else {
            row.createCell(count).setCellValue(map.get("create_userid").toString());
        }
        count = count + 1;
        if (map.get("company_name") == null) {
            row.createCell(count).setCellValue("");
        } else {
            row.createCell(count).setCellValue(map.get("company_name").toString());
        }
        count = count + 1;
        if (map.get("person") == null) {
            row.createCell(count).setCellValue("");
        } else {
            row.createCell(count).setCellValue(map.get("person").toString());
        }
        count = count + 1;
        if (map.get("responsibleName") == null) {
            row.createCell(count).setCellValue("");
        } else {
            row.createCell(count).setCellValue(map.get("responsibleName").toString());
        }
        count = count + 1;
        if (map.get("projectName") == null) {
            row.createCell(count).setCellValue("");
        } else {
            row.createCell(count).setCellValue(map.get("projectName").toString());
        }
        count = count + 1;
        if (map.get("address") == null) {
            row.createCell(count).setCellValue("");
        } else {
            row.createCell(count).setCellValue(map.get("address").toString());
        }

        count = count + 1;
        if (map.get("approval_person") == null) {
            row.createCell(count).setCellValue("");
        } else {
            row.createCell(count).setCellValue(map.get("approval_person").toString());
        }
        count = count + 1;
        if (map.get("approval_note") == null) {
            row.createCell(count).setCellValue("");
        } else {
            row.createCell(count).setCellValue(map.get("approval_note").toString());
        }
        count = count + 1;
        if (map.get("approval_time") == null) {
            row.createCell(count).setCellValue("");
        } else {
            row.createCell(count).setCellValue(map.get("approval_time").toString());
        }
        if (type.equals("1")) {
            count = count + 1;
            if (map.get("feedback_person") == null) {
                row.createCell(count).setCellValue("");
            } else {
                row.createCell(count).setCellValue(map.get("feedback_person").toString());
            }
            count = count + 1;
            if (map.get("feedback_note") == null) {
                row.createCell(count).setCellValue("");
            } else {
                row.createCell(count).setCellValue(map.get("feedback_note").toString());
            }
            count = count + 1;
            if (map.get("feedback_time") == null) {
                row.createCell(count).setCellValue("");
            } else {
                row.createCell(count).setCellValue(map.get("feedback_time").toString());
            }
        }
        if (type.equals("2")) {
            count = count + 1;
            if (map.get("crm_no") == null) {
                row.createCell(count).setCellValue("");
            } else {
                row.createCell(count).setCellValue(map.get("crm_no").toString());
            }
            count = count + 1;
            if (map.get("assign_person") == null) {
                row.createCell(count).setCellValue("");
            } else {
                row.createCell(count).setCellValue(map.get("assign_person").toString());
            }
            count = count + 1;
            if (map.get("assign_time") == null) {
                row.createCell(count).setCellValue("");
            } else {
                row.createCell(count).setCellValue(map.get("assign_time").toString());
            }
            count = count + 1;
            if (map.get("build_person") == null) {
                row.createCell(count).setCellValue("");
            } else {
                row.createCell(count).setCellValue(map.get("build_person").toString());
            }
            count = count + 1;
            if (map.get("build_company") == null) {
                row.createCell(count).setCellValue("");
            } else {
                row.createCell(count).setCellValue(map.get("build_company").toString());
            }
            count = count + 1;
            if (map.get("end_time") == null) {
                row.createCell(count).setCellValue("");
            } else {
                row.createCell(count).setCellValue(map.get("end_time").toString());
            }
            count = count + 1;
            if (map.get("completed_person") == null) {
                row.createCell(count).setCellValue("");
            } else {
                row.createCell(count).setCellValue(map.get("completed_person").toString());
            }
            count = count + 1;
            if (map.get("completed_time") == null) {
                row.createCell(count).setCellValue("");
            } else {
                row.createCell(count).setCellValue(map.get("completed_time").toString());
            }
            count = count + 1;
            if (map.get("model_name") == null) {
                row.createCell(count).setCellValue("");
            } else {
                row.createCell(count).setCellValue(map.get("model_name").toString());
            }
            count = count + 1;
            if (map.get("model_num") == null || map.get("model_num").toString().equals("0")) {
                row.createCell(count).setCellValue("");
            } else {
                row.createCell(count).setCellValue(map.get("model_num").toString());
            }
            count = count + 1;
            if (map.get("poe_name") == null) {
                row.createCell(count).setCellValue("");
            } else {
                row.createCell(count).setCellValue(map.get("poe_name").toString());
            }
            count = count + 1;
            if (map.get("poe_num") == null || map.get("poe_num").toString().equals("0")) {
                row.createCell(count).setCellValue("");
            } else {
                row.createCell(count).setCellValue(map.get("poe_num").toString());
            }
            count = count + 1;
            if (map.get("save_net_name") == null) {
                row.createCell(count).setCellValue("");
            } else {
                row.createCell(count).setCellValue(map.get("save_net_name").toString());
            }
            count = count + 1;
            if (map.get("storage_num") == null || map.get("storage_num").toString().equals("0")) {
                row.createCell(count).setCellValue("");
            } else {
                row.createCell(count).setCellValue(map.get("storage_num").toString());
            }
            count = count + 1;
            if (map.get("model_wifi_name") == null) {
                row.createCell(count).setCellValue("");
            } else {
                row.createCell(count).setCellValue(map.get("model_wifi_name").toString());
            }
            count = count + 1;
            if (map.get("model_wifi_num") == null || map.get("model_wifi_num").toString().equals("0")) {
                row.createCell(count).setCellValue("");
            } else {
                row.createCell(count).setCellValue(map.get("model_wifi_num").toString());
            }
            count = count + 1;
            if (map.get("poe_wifi_name") == null) {
                row.createCell(count).setCellValue("");
            } else {
                row.createCell(count).setCellValue(map.get("poe_wifi_name").toString());
            }
            count = count + 1;
            if (map.get("poe_wifi_num") == null || map.get("poe_wifi_num").toString().equals("0")) {
                row.createCell(count).setCellValue("");
            } else {
                row.createCell(count).setCellValue(map.get("poe_wifi_num").toString());
            }
            count = count + 1;
            if (map.get("save_wifi_name") == null) {
                row.createCell(count).setCellValue("");
            } else {
                row.createCell(count).setCellValue(map.get("save_wifi_name").toString());
            }
            count = count + 1;
            if (map.get("storage_wifi_num") == null || map.get("storage_wifi_num").toString().equals("0")) {
                row.createCell(count).setCellValue("");
            } else {
                row.createCell(count).setCellValue(map.get("storage_wifi_num").toString());
            }
            count = count + 1;
            if (map.get("is_import") == null || map.get("is_import").toString().equals("0")) {
                row.createCell(count).setCellValue("否");
            } else {
                row.createCell(count).setCellValue("是");
            }
        }
        count = count + 1;
        if (map.get("state") == null) {
            row.createCell(count).setCellValue("");
        } else {
            row.createCell(count).setCellValue(getState(Integer.valueOf(map.get("state").toString()), Integer.valueOf(map.get("type").toString())));
        }
        return row;
    }

    private String getState(Integer state, Integer type) {
        if (type == 1) {
            if (state == 100) {
                return "待审核";
            } else if (state == 200) {
                return "待反馈";
            } else if (state == 300) {
                return "已转化";
            } else if (state == 400) {
                return "已完结";
            } else if (state == 600) {
                return "未通过";
            } else {
                return "未知";
            }
        } else {
            if (state == 100) {
                return "待审核";
            } else if (state == 200) {
                return "待指派";
            } else if (state == 300) {
                return "待施工";
            } else if (state == 400) {
                return "待竣工";
            } else if (state == 500) {
                return "已完成";
            } else if (state == 600) {
                return "未通过";
            } else {
                return "未知";
            }
        }
    }


    /**
     * 商机派单导入
     *
     * @param file
     * @param report
     * @param su
     * @param type
     */
    @Transactional
    public void importExcel(MultipartFile file, ImportReport report, SessionUser su, Integer type) {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        Workbook workbook = null;
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            // 根据后缀实例化，xls实例化HSSFWorkbook,xlsx实例化XSSFWorkbook
            if (extension.equalsIgnoreCase("xls")) {
                workbook = new HSSFWorkbook(inputStream);
            } else {
                workbook = new XSSFWorkbook(inputStream);
            }
            //检查模板是否正确
            if (checkExcelModel(workbook, report)) {
                List<SjOrder> list = checkData(workbook, report);
                if (report.isPass() && list.size() > 0) {
                    //保存数据
                    saveData(list, su);
                }
            } else {
                report.setContinueNext(false);
                report.setPass(false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {

                }
            }
        }
    }

    @Transactional
    private List<SjOrder> saveData(List<SjOrder> sjOrders, SessionUser su) {
        List<SjOrder> sjOrders1 = new ArrayList<>();
        for (SjOrder sjOrder : sjOrders) {
            sjOrder.setType(2);
            sjOrder.setState(500);
            sjOrder.setIsImport(1);
            sjOrder.setOrderNo(NOUtil.getNo("NB-"));
            sjOrder.setCreateTime(new Date());
            this.createOrder(sjOrder.getProjectId(), sjOrder);
        }
        return sjOrders1;
    }

    /**
     * 检查表格数据  将数据转化为对应卡号批次实体类  自动生成一个批次
     *
     * @param workbook
     * @param report
     * @return
     */
    private List<SjOrder> checkData(Workbook workbook, ImportReport report) {
        Sheet sheet = workbook.getSheetAt(0);
        int rowNum = sheet.getLastRowNum();
        List<SjOrder> sjOrders = new ArrayList<SjOrder>();

        for (int i = 1; i <= rowNum; i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            SjOrder o = new SjOrder();

            int col = 0;
            //用户手机号
            String value = ExcelUtil.getCellValue(row, col).trim();
            if (org.apache.commons.lang3.StringUtils.isBlank(value) || value.length() > 11) {
                ImportError error = new ImportError();
                error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                error.setMessage("用户手机号不能为空，长度不能超过11个字符！");
                report.getErrorList().add(error);
                report.setPass(false);
            } else {
                o.setCreateUserid(value);
            }
            //单位名字
            col++;
            value = ExcelUtil.getCellValue(row, col).trim();
            if (org.apache.commons.lang3.StringUtils.isBlank(value)) {
                ImportError error = new ImportError();
                error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                error.setMsgType("单位名字错误");
                error.setMessage("单位名字不能为空");
                report.getErrorList().add(error);
                report.setPass(false);
            } else {
                o.setCompanyName(value);
            }

            StringBuffer areas = new StringBuffer();
            col++;
            value = ExcelUtil.getCellValue(row, col);
            if (org.apache.commons.lang3.StringUtils.isBlank(value) || value.length() > 64) {
                ImportError error = new ImportError();
                error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                error.setMsgType("地址信息错误");
                error.setMessage("地址信息不能为空，长度不能超过64个字符！");
                report.getErrorList().add(error);
                report.setPass(false);
                continue;
            }

            Address addr = addressService.queryByAreaAndPid(value, "0");
            if (addr == null) {
                ImportError error = new ImportError();
                error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                error.setMsgType("地址信息错误");
                error.setMessage("地址信息未找到！");
                report.getErrorList().add(error);
                report.setPass(false);
            }

            o.setProvinceId(addr.getAreaId());
            areas.append(addr.getArea()).append(" ");

            col++;
            value = ExcelUtil.getCellValue(row, col);
            if (org.apache.commons.lang3.StringUtils.isBlank(value) || value.length() > 64) {
                ImportError error = new ImportError();
                error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                error.setMsgType("地址信息错误");
                error.setMessage("地址信息不能为空，长度不能超过64个字符！");
                report.getErrorList().add(error);
                report.setPass(false);
                continue;
            }

            addr = addressService.queryByAreaAndPid(value, o.getProvinceId());

            if (addr == null) {
                ImportError error = new ImportError();
                error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                error.setMsgType("地址信息错误");
                error.setMessage("地址信息未找到！");
                report.getErrorList().add(error);
                report.setPass(false);
            }
            o.setCityId(addr.getAreaId());
            areas.append(addr.getArea()).append(" ");

            col++;
            value = ExcelUtil.getCellValue(row, col);
            if (org.apache.commons.lang3.StringUtils.isBlank(value) || value.length() > 64) {
                ImportError error = new ImportError();
                error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                error.setMsgType("地址信息错误");
                error.setMessage("地址信息不能为空，长度不能超过64个字符！");
                report.getErrorList().add(error);
                report.setPass(false);
                continue;
            }
            addr = addressService.queryByAreaAndPid(value, o.getCityId());
            if (addr == null) {
                ImportError error = new ImportError();
                error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                error.setMsgType("地址信息错误");
                error.setMessage("地址信息未找到！");
                report.getErrorList().add(error);
                report.setPass(false);
            }
            o.setAreaId(addr.getAreaId());
            areas.append(addr.getArea());

            col++;
            value = ExcelUtil.getCellValue(row, col);
            o.setAddressDetail(value);

            col++;
            value = ExcelUtil.getCellValue(row, col);
            if (org.apache.commons.lang3.StringUtils.isBlank(value) || value.length() > 50) {
                ImportError error = new ImportError();
                error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                error.setMsgType("联系人错误");
                error.setMessage("联系人不能为空，长度不能超过50个字符！");
                report.getErrorList().add(error);
                report.setPass(false);
                continue;
            } else {
                o.setPerson(value);
            }

            col++;
            value = ExcelUtil.getCellValue(row, col);
            if (org.apache.commons.lang3.StringUtils.isBlank(value) || value.length() > 11) {
                ImportError error = new ImportError();
                error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                error.setMsgType("联系人手机号错误");
                error.setMessage("联系人手机号不能为空，长度不能超过11位！");
                report.getErrorList().add(error);
                report.setPass(false);
                continue;
            } else {
                o.setPhone(value);
            }

            col++;
            value = ExcelUtil.getCellValue(row, col);
            if (org.apache.commons.lang3.StringUtils.isBlank(value)) {
                ImportError error = new ImportError();
                error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                error.setMsgType("负责人姓名错误");
                error.setMessage("负责人姓名不能为空");
                report.getErrorList().add(error);
                report.setPass(false);
                continue;
            } else {
                o.setResponsibleName(value);
            }

            col++;
            value = ExcelUtil.getCellValue(row, col);
            if (org.apache.commons.lang3.StringUtils.isBlank(value) || value.length() > 18) {
                ImportError error = new ImportError();
                error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                error.setMsgType("负责人身份证号错误");
                error.setMessage("负责人身份证号不能为空，长度不能超过18位！");
                report.getErrorList().add(error);
                report.setPass(false);
                continue;
            } else {
                o.setResponsibleIdNumber(value);
            }

            col++;
            value = ExcelUtil.getCellValue(row, col);
            if (org.apache.commons.lang3.StringUtils.isBlank(value) || value.length() != 18) {
                ImportError error = new ImportError();
                error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                error.setMsgType("CRM编号错误");
                error.setMessage("CRM编号不能为空，长度不能超过18位！");
                report.getErrorList().add(error);
                report.setPass(false);
                continue;
            } else {
                o.setCrmNo(value);
            }

            col++;
            value = ExcelUtil.getCellValue(row, col);
            if (org.apache.commons.lang3.StringUtils.isBlank(value) || value.length() > 50) {
                ImportError error = new ImportError();
                error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                error.setMsgType("需求错误");
                error.setMessage("产品需求不能为空，长度不能超过50个字符！");
                report.getErrorList().add(error);
                report.setPass(false);
                continue;
            }
            if (value.contains(",")) {
                String[] projectIds1 = value.split(",");
                for (int p = 0; p < projectIds1.length; p++) {
                    String project1 = projectIds1[p];
                    SjProject sjProject = projectService.queryById(project1);
                    if (sjProject == null) {
                        ImportError error = new ImportError();
                        error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                        error.setMsgType("产品需求错误");
                        error.setMessage("产品需求" + project1 + "未找到！");
                        report.getErrorList().add(error);
                        report.setPass(false);
                    }
                }
            }
            o.setProjectId(value);

            col++;
            value = ExcelUtil.getCellValue(row, col);
            if (o.getProjectId().contains("1")) {
                if (org.apache.commons.lang3.StringUtils.isBlank(value)) {
                    ImportError error = new ImportError();
                    error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                    error.setMsgType("AP错误");
                    error.setMessage("AP不能为空");
                    report.getErrorList().add(error);
                    report.setPass(false);
                    continue;
                } else {
                    o.setSingle(Integer.valueOf(value));
                }
            }

            col++;
            value = ExcelUtil.getCellValue(row, col);
            if (o.getProjectId().contains("2")) {
                if (org.apache.commons.lang3.StringUtils.isBlank(value)) {
                    ImportError error = new ImportError();
                    error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                    error.setMsgType("监控错误");
                    error.setMessage("监控不能为空");
                    report.getErrorList().add(error);
                    report.setPass(false);
                    continue;
                } else {
                    o.setGroupNet(Integer.valueOf(value));
                }
            }

            col++;
            value = ExcelUtil.getCellValue(row, col);
            if (o.getProjectId().contains("2")) {
                if (org.apache.commons.lang3.StringUtils.isBlank(value)) {
                    ImportError error = new ImportError();
                    error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                    error.setMsgType("监控型号错误");
                    error.setMessage("监控型号不能为空");
                    report.getErrorList().add(error);
                    report.setPass(false);
                    continue;
                } else {
                    o.setModelId(Integer.valueOf(value));
                    o.setMealId(1);
                }
            }

            col++;
            value = ExcelUtil.getCellValue(row, col);
            if (o.getProjectId().contains("2")) {
                if (org.apache.commons.lang3.StringUtils.isBlank(value)) {
                    ImportError error = new ImportError();
                    error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                    error.setMsgType("监控型号个数错误");
                    error.setMessage("监控型号个数不能为空");
                    report.getErrorList().add(error);
                    report.setPass(false);
                    continue;
                } else {
                    o.setModelNum(Integer.valueOf(value));
                }
            }

            col++;
            value = ExcelUtil.getCellValue(row, col);
            if (o.getProjectId().contains("2")) {
                if (org.apache.commons.lang3.StringUtils.isBlank(value)) {
                    ImportError error = new ImportError();
                    error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                    error.setMsgType("监控poe错误");
                    error.setMessage("监控poe不能为空");
                    report.getErrorList().add(error);
                    report.setPass(false);
                    continue;
                } else {
                    o.setPoeId(Integer.valueOf(value));
                }
            }

            col++;
            value = ExcelUtil.getCellValue(row, col);
            if (o.getProjectId().contains("2")) {
                if (org.apache.commons.lang3.StringUtils.isBlank(value)) {
                    ImportError error = new ImportError();
                    error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                    error.setMsgType("监控poe个数错误");
                    error.setMessage("监控poe个数不能为空");
                    report.getErrorList().add(error);
                    report.setPass(false);
                    continue;
                } else {
                    o.setPoeNum(Integer.valueOf(value));
                }
            }

            col++;
            value = ExcelUtil.getCellValue(row, col);
            if (o.getProjectId().contains("2")) {
                if (org.apache.commons.lang3.StringUtils.isBlank(value)) {
                    ImportError error = new ImportError();
                    error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                    error.setMsgType("存储错误");
                    error.setMessage("存储不能为空");
                    report.getErrorList().add(error);
                    report.setPass(false);
                    continue;
                } else {
                    o.setStorageId(Integer.valueOf(value));
                }
            }

            col++;
            value = ExcelUtil.getCellValue(row, col);
            if (o.getProjectId().contains("2")) {
                if (org.apache.commons.lang3.StringUtils.isBlank(value)) {
                    ImportError error = new ImportError();
                    error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                    error.setMsgType("存储个数错误");
                    error.setMessage("存储个数不能为空");
                    report.getErrorList().add(error);
                    report.setPass(false);
                    continue;
                } else {
                    o.setStorageNum(Integer.valueOf(value));
                }
            }

            col++;
            value = ExcelUtil.getCellValue(row, col);
            if (o.getProjectId().contains("1")) {
                if (org.apache.commons.lang3.StringUtils.isBlank(value)) {
                    ImportError error = new ImportError();
                    error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                    error.setMsgType("云wifi错误");
                    error.setMessage("云wifi不能为空");
                    report.getErrorList().add(error);
                    report.setPass(false);
                    continue;
                } else {
                    o.setModelWifiId(Integer.valueOf(value) + 6);
                    o.setMealWifiId(2);
                }
            }

            col++;
            value = ExcelUtil.getCellValue(row, col);
            if (o.getProjectId().contains("1")) {
                if (org.apache.commons.lang3.StringUtils.isBlank(value)) {
                    ImportError error = new ImportError();
                    error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                    error.setMsgType("云wifi个数错误");
                    error.setMessage("云wifi个数不能为空");
                    report.getErrorList().add(error);
                    report.setPass(false);
                    continue;
                } else {
                    o.setModelWifiNum(Integer.valueOf(value));
                }
            }

            col++;
            value = ExcelUtil.getCellValue(row, col);
            if (o.getProjectId().contains("1")) {
                if (org.apache.commons.lang3.StringUtils.isBlank(value)) {
                    ImportError error = new ImportError();
                    error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                    error.setMsgType("云wifipoe错误");
                    error.setMessage("云wifipoe不能为空");
                    report.getErrorList().add(error);
                    report.setPass(false);
                    continue;
                } else {
                    o.setPoeWifiId(Integer.valueOf(value) + 4);
                }
            }

            col++;
            value = ExcelUtil.getCellValue(row, col);
            if (o.getProjectId().contains("1")) {
                if (org.apache.commons.lang3.StringUtils.isBlank(value)) {
                    ImportError error = new ImportError();
                    error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                    error.setMsgType("云wifipoe个数错误");
                    error.setMessage("云wifipoe个数不能为空");
                    report.getErrorList().add(error);
                    report.setPass(false);
                    continue;
                } else {
                    o.setPoeWifiNum(Integer.valueOf(value));
                }
            }

            col++;
            value = ExcelUtil.getCellValue(row, col);
            if (o.getProjectId().contains("1")) {
                if (org.apache.commons.lang3.StringUtils.isBlank(value)) {
                    ImportError error = new ImportError();
                    error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                    error.setMsgType("NET/网关/路由错误");
                    error.setMessage("NET/网关/路由不能为空");
                    report.getErrorList().add(error);
                    report.setPass(false);
                    continue;
                } else {
                    o.setStorageWifiId(Integer.valueOf(value) + 6);
                }
            }

            col++;
            value = ExcelUtil.getCellValue(row, col);
            if (o.getProjectId().contains("1")) {
                if (org.apache.commons.lang3.StringUtils.isBlank(value)) {
                    ImportError error = new ImportError();
                    error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                    error.setMsgType("NET/网关/路由个数错误");
                    error.setMessage("NET/网关/路由个数不能为空");
                    report.getErrorList().add(error);
                    report.setPass(false);
                    continue;
                } else {
                    o.setStorageWifiNum(Integer.valueOf(value));
                }
            }

            sjOrders.add(o);
        }
        return sjOrders;
    }

    /**
     * 检查模板是否正确
     *
     * @param workbook
     * @return
     */
    private boolean checkExcelModel(Workbook workbook, ImportReport report) {
        Sheet sheet = workbook.getSheetAt(0);
        Row row0 = sheet.getRow(0);
        if (row0 == null) {
            return false;
        }
        //模板数据
        Set<Integer> set = titleMap.keySet();
        for (Integer key : set) {
            String t1 = row0.getCell(key).toString().trim();
            String t2 = titleMap.get(key);
            if (t1 == null || !t1.equals(t2)) {
                report.setContinueNext(false);
                return false;
            }
        }
        return true;
    }
}