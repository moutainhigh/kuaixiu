package com.kuaixiu.sjBusiness.service;


import com.alibaba.fastjson.JSONObject;
import com.common.base.service.BaseService;
import com.common.util.ConverterUtil;
import com.common.util.DateUtil;
import com.common.wechat.common.util.StringUtils;
import com.kuaixiu.recycle.entity.RecycleCheckItems;
import com.kuaixiu.recycle.entity.RecycleOrder;
import com.kuaixiu.recycle.entity.RecycleSystem;
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
import com.kuaixiu.sjUser.entity.SjSessionUser;
import com.kuaixiu.sjUser.entity.SjUser;
import com.kuaixiu.sjUser.service.SjUserService;
import com.system.basic.address.entity.Address;
import com.system.basic.address.service.AddressService;
import com.system.basic.user.entity.SessionUser;
import com.system.constant.SystemConstant;
import jodd.util.StringUtil;
import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
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
    private static final Logger log = Logger.getLogger(SjOrderService.class);

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

    public List<JSONObject> sjListOrderToObejct(List<SjOrder> orders) {
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
        return jsonObjects;
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
            jsonObject.put("ap", o.getSingle());
            jsonObject.put("monitor", o.getGroupNet());
        } else {
            if (o.getState() > 200) {
                SjUser sjUser = sjUserService.getDao().queryByLoginId(o.getFeedbackPerson(), null);
                jsonObject.put("feedbackPerson", sjUser.getName() + "/" + sjUser.getLoginId());
                jsonObject.put("feedbackTime", o.getFeedbackTime());
                jsonObject.put("feedbackNote", o.getFeedbackNote());
            }
        }
        jsonObject.put("images", getImages(o.getOrderNo()));
        if (o.getState() >= 200) {
            SjUser sjUser = sjUserService.getDao().queryByLoginId(o.getFeedbackPerson(), null);
            jsonObject.put("approvalPerson", sjUser.getName() + "/" + sjUser.getLoginId());
            jsonObject.put("approvalTime", o.getApprovalTime());
            jsonObject.put("approvalNote", o.getApprovalNote());
        }
        return jsonObject;
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
        List<SjOrder> sjOrders = this.queryList(sjOrder);
        List<Map<String, Object>> maps = new ArrayList<>();
        for (SjOrder o : sjOrders) {
            List<String> projects = getProject(o.getProjectId());
            String projectName = listToString(projects);
            o.setProjectNames(projectName);
            o.setStrCreateTime(DateUtil.getDateyyyyMMddHHmmss(o.getCreateTime()));
            o.setNewDate(DateUtil.getDateyyyyMMddHHmmss(new Date()));
            if (o.getApprovalTime() != null) {
                o.setStrApprovalTime(DateUtil.getDateyyyyMMddHHmmss(o.getApprovalTime()));
            }
            Map<String, Object> map = new BeanMap(o);
            maps.add(map);
        }


        List<List<Map<String, Object>>> lists = new ArrayList<>();
        List<String> listAgent = new ArrayList<>();
        //创建street
        setListAgent(lists, listAgent, maps);
        //excel标题
        String[] header = new String[]{};
        if (type.equals("1")) {
            header = new String[]{"订单号", "创建时间", "类型", "提交人/账号", "企业名字", "企业负责人/电话", "需求",
                    "状态"};
        } else {
            header = new String[]{"订单号", "创建时间", "类型", "提交人/账号", "CRM编号", "企业名字",
                    "企业负责人/电话", "需求", "状态"};
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
            String filedisplay = "回收列表导出.xls";
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
        if (map.get("orderNo") == null) {
            row.createCell(count).setCellValue("");
        } else {
            row.createCell(count).setCellValue(map.get("orderNo").toString());
        }
        count = count + 1;
        if (map.get("strCreateTime") == null) {
            row.createCell(count).setCellValue("");
        } else {
            row.createCell(count).setCellValue(map.get("strCreateTime").toString());
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
        if (map.get("createUserid") == null && map.get("createName") == null) {
            row.createCell(count).setCellValue("");
        } else if (map.get("createUserid") != null && map.get("createName") != null) {
            row.createCell(count).setCellValue(map.get("createName").toString() + "/" + map.get("createUserid").toString());
        } else if (map.get("createUserid") != null) {
            row.createCell(count).setCellValue(map.get("createUserid").toString());
        } else if (map.get("createName") != null) {
            row.createCell(count).setCellValue(map.get("createName").toString());
        }
        if(type.equals("2")){
            count = count + 1;
            if (map.get("crmNo") == null) {
                row.createCell(count).setCellValue("");
            } else {
                row.createCell(count).setCellValue(map.get("crmNo").toString());
            }
        }
        count = count + 1;
        if (map.get("companyName") == null) {
            row.createCell(count).setCellValue("");
        } else {
            row.createCell(count).setCellValue(map.get("companyName").toString());
        }
        count = count + 1;
        if (map.get("person") == null && map.get("phone") == null) {
            row.createCell(count).setCellValue("");
        } else if (map.get("person") != null && map.get("phone") != null) {
            row.createCell(count).setCellValue(map.get("person").toString() + "/" + map.get("phone").toString());
        } else if (map.get("person") != null) {
            row.createCell(count).setCellValue(map.get("person").toString());
        } else if (map.get("phone") != null) {
            row.createCell(count).setCellValue(map.get("phone").toString());
        }
        count = count + 1;
        if (map.get("projectNames") == null) {
            row.createCell(count).setCellValue("");
        } else {
            row.createCell(count).setCellValue(map.get("projectNames").toString());
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
        if(type==1){
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
        }else{
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