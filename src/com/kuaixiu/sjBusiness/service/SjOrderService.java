package com.kuaixiu.sjBusiness.service;


import com.alibaba.fastjson.JSONObject;
import com.common.base.service.BaseService;
import com.common.util.ConverterUtil;
import com.common.wechat.common.util.StringUtils;
import com.kuaixiu.sjBusiness.dao.SjOrderMapper;
import com.kuaixiu.sjBusiness.entity.*;

import com.kuaixiu.sjUser.entity.SjUser;
import com.kuaixiu.sjUser.service.SjUserService;
import com.system.basic.address.entity.Address;
import com.system.basic.address.service.AddressService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
    private SjRegisterFormService registerFormService;


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
        if (type == 1) {
            SjRegisterForm registerForm = registerFormService.getDao().queryBy4Id(order.getStorageId(),
                    order.getPoeId(), order.getModelId(), order.getMealId());
            if (registerForm != null) {
                order.setMealName(registerForm.getMealName());
                order.setModelName(registerForm.getModelName());
                order.setPoeName(registerForm.getPoeName());
                order.setStorageName(registerForm.getStorageName());
            }
        } else {
            SjRegisterForm registerForm = registerFormService.getDao().queryBy4Id(order.getStorageWifiId(),
                    order.getPoeWifiId(), order.getModelWifiId(), order.getMealWifiId());
            if (registerForm != null) {
                order.setMealWifiName(registerForm.getMealName());
                order.setModelWifiName(registerForm.getModelName());
                order.setPoeWifiName(registerForm.getPoeName());
                order.setStorageWifiName(registerForm.getStorageName());
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
}