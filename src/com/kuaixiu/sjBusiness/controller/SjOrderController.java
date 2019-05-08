package com.kuaixiu.sjBusiness.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.util.NOUtil;
import com.common.wechat.common.util.StringUtils;
import com.kuaixiu.sjBusiness.entity.AreaCityCompany;
import com.kuaixiu.sjBusiness.entity.OrderCompanyPicture;
import com.kuaixiu.sjBusiness.entity.SjOrder;
import com.kuaixiu.sjBusiness.entity.SjProject;
import com.kuaixiu.sjBusiness.service.OrderCompanyPictureService;
import com.kuaixiu.sjBusiness.service.SjOrderService;
import com.kuaixiu.sjBusiness.service.SjProjectService;
import com.kuaixiu.sjUser.entity.CustomerDetail;
import com.kuaixiu.sjUser.entity.SjUser;
import com.system.api.entity.ResultData;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Order Controller
 *
 * @CreateDate: 2019-05-06 上午10:44:49
 * @version: V 1.0
 */
@Controller
public class SjOrderController extends BaseController {
    private static final Logger log = Logger.getLogger(SjOrderController.class);

    @Autowired
    private SjOrderService orderService;
    @Autowired
    private SjProjectService projectService;
    @Autowired
    private OrderCompanyPictureService orderCompanyPictureService;

    /**
     * 获取所有产品需求
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sj/wechat/getProject")
    @ResponseBody
    public ResultData getProject(HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        try {
            List<SjProject> projects = projectService.queryList(null);
            List<JSONObject> objects1 = new ArrayList<>();
            for (SjProject project : projects) {
                JSONObject object = new JSONObject();
                object.put("projectId", project.getId());
                object.put("project", project.getProject());
                objects1.add(object);
            }
            getSjResult(result, objects1, true, "0", null, "获取成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }


    /**
     * 提交商机/派单
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sj/wechat/submitOrder")
    @ResponseBody
    public ResultData register(HttpServletRequest request,
                               HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        try {
            JSONObject params = getPrarms(request);
            Integer type = params.getInteger("type");
            String phone = params.getString("phone");
            String companyName = params.getString("companyName");
            String provinceId = params.getString("provinceId");
            String cityId = params.getString("cityId");
            String areaId = params.getString("areaId");
            String streetId = params.getString("streetId");
            String addressDetail = params.getString("addressDetail");
            String person = params.getString("person");
            String personPhone = params.getString("personPhone");
            JSONArray imagesList = params.getJSONArray("imagesList");
            String projectId = params.getString("projectId");
            Integer single = params.getInteger("single");
            Integer group = params.getInteger("group");

            if (StringUtils.isBlank(phone) || StringUtils.isBlank(companyName)
                    || StringUtils.isBlank(addressDetail) || StringUtils.isBlank(person)
                    || StringUtils.isBlank(personPhone) || StringUtils.isBlank(projectId)
                    || StringUtils.isBlank(provinceId) || StringUtils.isBlank(cityId)
                    || StringUtils.isBlank(areaId) || null == type || null == imagesList) {
                return getSjResult(result, null, false, "2", null, "参数为空");
            }
            if (type == 2) {
                if (null == single || null == group) {
                    return getSjResult(result, null, false, "2", null, "参数为空");
                }
            }

            SjOrder sjOrder = new SjOrder();
            sjOrder.setOrderNo(NOUtil.getNo("NB-"));
            sjOrder.setState(100);
            sjOrder.setCompanyName(companyName);
            sjOrder.setProvinceId(provinceId);
            sjOrder.setCityId(cityId);
            sjOrder.setAreaId(areaId);
            sjOrder.setStreetId(streetId);
            sjOrder.setAddressDetail(addressDetail);
            sjOrder.setPerson(person);
            sjOrder.setPhone(personPhone);
            sjOrder.setProjectId(projectId);
            sjOrder.setSingle(single);
            sjOrder.setGroup(group);
            sjOrder.setCreateUserid(phone);
            orderService.add(sjOrder);

            for (int i = 0; i < imagesList.size(); i++) {
                String image = imagesList.get(i).toString();
                OrderCompanyPicture companyPicture = new OrderCompanyPicture();
                companyPicture.setOrderNo(sjOrder.getOrderNo());
                companyPicture.setCompanyPictureUrl(image);
                orderCompanyPictureService.add(companyPicture);
            }

            getSjResult(result, null, true, "0", null, "注册成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }
}
