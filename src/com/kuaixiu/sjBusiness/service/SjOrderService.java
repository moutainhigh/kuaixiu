package com.kuaixiu.sjBusiness.service;


import com.alibaba.fastjson.JSONObject;
import com.common.base.service.BaseService;
import com.common.paginate.Page;
import com.common.util.ConverterUtil;
import com.common.util.NOUtil;
import com.common.wechat.common.util.StringUtils;
import com.kuaixiu.sjBusiness.dao.SjOrderMapper;
import com.kuaixiu.sjBusiness.entity.OrderCompanyPicture;
import com.kuaixiu.sjBusiness.entity.SjOrder;
import com.kuaixiu.sjBusiness.entity.SjProject;
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
import com.kuaixiu.sjUser.service.SjUserService;
import com.system.basic.address.entity.Address;
import com.system.basic.address.service.AddressService;
import org.apache.commons.collections.MapUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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

    //**********自定义方法***********

    public JSONObject sjListOrderToObejct(List<SjOrder> orders,Page page) {
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

    public JSONObject sjListReOrderToObejct(List<SjOrder> orders,Page page) {

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
            jsonObject.put("approvalPerson", sjUser.getName() + "/" + sjUser.getLoginId());
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
            this.add(sjOrder);
            sjOrder.setOrderNo(NOUtil.getNo("NB-"));
            sjOrder.setProjectId("2");
            this.add(sjOrder);
            sjOrder.setOrderNo(NOUtil.getNo("NB-"));
            sjOrder.setProjectId(org.apache.commons.lang3.StringUtils.remove(projectId, "1,2,"));
            this.add(sjOrder);
        } else if (projectId.contains("1") &&
                (projectId.contains("3") || projectId.contains("4") || projectId.contains("5") || projectId.contains("6"))) {
            sjOrder.setOrderNo(NOUtil.getNo("NB-"));
            sjOrder.setProjectId("1");
            this.add(sjOrder);
            sjOrder.setOrderNo(NOUtil.getNo("NB-"));
            sjOrder.setProjectId(org.apache.commons.lang3.StringUtils.remove(projectId, "1,"));
            this.add(sjOrder);
        } else if (projectId.contains("2") &&
                (projectId.contains("3") || projectId.contains("4") || projectId.contains("5") || projectId.contains("6"))) {
            sjOrder.setOrderNo(NOUtil.getNo("NB-"));
            sjOrder.setProjectId("2");
            this.add(sjOrder);
            sjOrder.setOrderNo(NOUtil.getNo("NB-"));
            sjOrder.setProjectId(org.apache.commons.lang3.StringUtils.remove(projectId, "2,"));
            this.add(sjOrder);
        } else if (projectId.contains("1") && projectId.contains("2")) {
            sjOrder.setOrderNo(NOUtil.getNo("NB-"));
            sjOrder.setProjectId("1");
            this.add(sjOrder);
            sjOrder.setOrderNo(NOUtil.getNo("NB-"));
            sjOrder.setProjectId("2");
            this.add(sjOrder);
        } else {
            sjOrder.setOrderNo(NOUtil.getNo("NB-"));
            sjOrder.setProjectId(projectId);
            this.add(sjOrder);
        }

    }

    public void getSjDetail(SjOrder sjOrder){
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

    public void getCompanies(List<ConstructionCompany> companies){
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

    public void getCompany(List<Map<String, Object>> companies){
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
                    "wifipoe数量", "wifi网关", "wifi网关数量", "状态"};
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
}