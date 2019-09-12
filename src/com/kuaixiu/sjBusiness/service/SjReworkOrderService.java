package com.kuaixiu.sjBusiness.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.common.base.service.BaseService;
import com.common.util.NOUtil;
import com.common.wechat.common.util.StringUtils;
import com.kuaixiu.sjBusiness.dao.SjReworkOrderMapper;
import com.kuaixiu.sjBusiness.entity.SjOrder;
import com.kuaixiu.sjBusiness.entity.SjReworkOrder;
import com.kuaixiu.sjUser.entity.ConstructionCompany;
import com.kuaixiu.sjUser.entity.SjUser;
import com.kuaixiu.sjUser.service.ConstructionCompanyService;
import com.kuaixiu.sjUser.service.SjUserService;
import com.system.basic.address.service.AddressService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * SjReworkOrder Service
 *
 * @CreateDate: 2019-06-28 下午04:31:50
 * @version: V 1.0
 */
@Service("sjReworkOrderService")
public class SjReworkOrderService extends BaseService<SjReworkOrder> {
    private static final Logger log = Logger.getLogger(SjReworkOrderService.class);

    @Autowired
    private SjReworkOrderMapper<SjReworkOrder> mapper;
    @Autowired
    private SjUserService sjUserService;
    @Autowired
    private SjOrderService orderService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private ConstructionCompanyService companyService;


    public SjReworkOrderMapper<SjReworkOrder> getDao() {
        return mapper;
    }

//**********自定义方法***********

    public SjReworkOrder submitReworkOrder(SjOrder sjOrder, String projects, String note, String phone) {
        SjReworkOrder sjReworkOrder = new SjReworkOrder();
        sjReworkOrder.setId(UUID.randomUUID().toString().replace("-", ""));
        sjReworkOrder.setReworkOrderNo(NOUtil.getNo("NR-"));
        sjReworkOrder.setOrderId(sjOrder.getId());
        sjReworkOrder.setOrderNo(sjOrder.getOrderNo());
        sjReworkOrder.setState(100);
        sjReworkOrder.setProjectIds(projects);
        sjReworkOrder.setNote(note);
        sjReworkOrder.setCreateUserid(phone);
        this.add(sjReworkOrder);
        return this.queryById(sjReworkOrder.getId());
    }

    public void assignReworkOrder(SjReworkOrder sjReworkOrder, SjOrder sjOrder) {
        SjUser sjUser = sjUserService.getDao().queryByLoginId(sjOrder.getAssignCompanyId(), 3);
        sjReworkOrder.setCompanyId(sjUser.getLoginId());
        sjReworkOrder.setCompanyName(sjUser.getName());
        sjReworkOrder.setState(200);
        this.saveUpdate(sjReworkOrder);
    }


    public List<JSONObject> getObjectList(List<SjReworkOrder> sjReworkOrders) {
        List<JSONObject> jsonObjects = new ArrayList<>();
        for (SjReworkOrder sjReworkOrder1 : sjReworkOrders) {
            JSONObject jsonObject = new JSONObject();
            List<String> projects = orderService.getProject(sjReworkOrder1.getProjectIds());
//            JSONObject jsonObject=JSONObject.parseObject(JSON.toJSONString(sjReworkOrder1));
            jsonObject.put("reworkNo", sjReworkOrder1.getReworkOrderNo());
            jsonObject.put("companyName", sjReworkOrder1.getCompanyName());
            jsonObject.put("state", sjReworkOrder1.getState());
            jsonObject.put("createTime", sjReworkOrder1.getCreateTime());
            if(sjReworkOrder1.getNote()==null){
                jsonObject.put("note","");
            }else{
                jsonObject.put("note", sjReworkOrder1.getNote());
            }
            jsonObject.put("workerName", sjReworkOrder1.getWorkerName());
            jsonObject.put("endTime", sjReworkOrder1.getEndTime());
            jsonObject.put("createUserid", sjReworkOrder1.getCreateUserid());



            String stayPerson = "";
            if (sjReworkOrder1.getState() == 200) {
                SjUser sjUser = sjUserService.getDao().queryByLoginId(sjReworkOrder1.getCompanyId(), 3);
                ConstructionCompany company = companyService.getDao().queryByLoginId(sjUser.getId());
                stayPerson = company.getPerson();
            } else if (sjReworkOrder1.getState() == 400) {
                stayPerson = sjReworkOrder1.getWorkerName();
            }
            jsonObject.put("stayPerson", stayPerson);
            jsonObject.put("projects", projects);
            jsonObjects.add(jsonObject);
        }
        return jsonObjects;
    }

    public JSONObject getObjectDetail(SjReworkOrder sjReworkOrder) {
        JSONObject jsonObject = new JSONObject();
        List<String> projects = orderService.getProject(sjReworkOrder.getProjectIds());
        String projectName = orderService.listToString(projects);
        SjOrder o = orderService.getDao().queryByOrderNo(sjReworkOrder.getOrderNo());
        jsonObject.put("reworkNo", sjReworkOrder.getReworkOrderNo());
        jsonObject.put("createTime", sjReworkOrder.getStrCreateTime());
        jsonObject.put("state", sjReworkOrder.getState());
        jsonObject.put("orderNo", sjReworkOrder.getOrderNo());
        if (StringUtils.isNotBlank(sjReworkOrder.getCompanyId())) {
            SjUser sjUser = sjUserService.getDao().queryByLoginId(sjReworkOrder.getCompanyId(), 3);
            if (sjUser != null && StringUtils.isNotBlank(sjUser.getPhone())) {
                jsonObject.put("handlerPhone", sjUser.getPhone());//处理方式电话
            } else {
                if (StringUtils.isNotBlank(sjReworkOrder.getWorkerId())) {
                    SjUser sjWorkerUser = sjUserService.getDao().queryByLoginId(sjReworkOrder.getWorkerId(), 8);
                    if (sjWorkerUser != null && StringUtils.isNotBlank(sjWorkerUser.getPhone())) {
                        jsonObject.put("handlerPhone", sjWorkerUser.getPhone());//处理方式电话
                    }
                }
            }
        }
        String stayPerson = "";
        if (sjReworkOrder.getState() == 200) {
            SjUser sjUser = sjUserService.getDao().queryByLoginId(sjReworkOrder.getCompanyId(), 3);
            ConstructionCompany company = companyService.getDao().queryByLoginId(sjUser.getId());
            stayPerson = company.getPerson();
        } else if (sjReworkOrder.getState() == 400) {
            stayPerson = sjReworkOrder.getWorkerName();
        }
        jsonObject.put("stayPerson", stayPerson);
        jsonObject.put("note", sjReworkOrder.getNote());
        jsonObject.put("companyName", sjReworkOrder.getCompanyName());
        String province = addressService.queryByAreaId(o.getProvinceId()).getArea();
        String city = addressService.queryByAreaId(o.getCityId()).getArea();
        String area = addressService.queryByAreaId(o.getAreaId()).getArea();
        String addressDetail = "";
        if (StringUtils.isNotBlank(o.getAddressDetail())) {
            addressDetail = o.getAddressDetail();
        }
        jsonObject.put("address", province + city + area + addressDetail);
        jsonObject.put("projectNames", projectName);
        jsonObject.put("person", o.getPerson());
        jsonObject.put("personPhone", o.getPhone());
        jsonObject.put("ap", o.getSingle());
        jsonObject.put("monitor", o.getGroupNet());
//        if (sjReworkOrder.getState() == 500) {
            jsonObject.put("endTime", sjReworkOrder.getStrEndTime());
//        }
        return jsonObject;
    }
}