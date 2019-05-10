package com.kuaixiu.sjBusiness.controller;

import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.kuaixiu.sjBusiness.entity.AreaBranchOffice;
import com.kuaixiu.sjBusiness.entity.AreaCityCompany;
import com.kuaixiu.sjBusiness.entity.AreaContractBody;
import com.kuaixiu.sjBusiness.entity.AreaManagementUnit;
import com.kuaixiu.sjBusiness.service.AreaBranchOfficeService;
import com.kuaixiu.sjBusiness.service.AreaCityCompanyService;
import com.kuaixiu.sjBusiness.service.AreaContractBodyService;
import com.kuaixiu.sjBusiness.service.AreaManagementUnitService;
import com.system.api.entity.ResultData;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * AreaCityCompany Controller
 *
 * @CreateDate: 2019-05-08 上午09:22:35
 * @version: V 1.0
 */
@Controller
public class AreaCityCompanyController extends BaseController {
    private static final Logger log = Logger.getLogger(AreaCityCompanyController.class);

    @Autowired
    private AreaCityCompanyService cityCompanyService;
    @Autowired
    private AreaBranchOfficeService branchOfficeService;
    @Autowired
    private AreaManagementUnitService managementUnitService;
    @Autowired
    private AreaContractBodyService contractBodyService;

    /**
     * 查询归属
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping("/sj/wechat/getAscription")
    @ResponseBody
    public ResultData getAscription(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResultData result = new ResultData();
        try {
            JSONObject params = getPrarms(request);
            Integer type = params.getInteger("type");
            Integer cityCompanyId = params.getInteger("cityCompanyId");
            Integer managementUnitId = params.getInteger("managementUnitId");
            Integer branchOfficeId = params.getInteger("branchOfficeId");
            if (type == null) {
                return getSjResult(result, null, false, "2", null, "查询类型为空");
            }
            JSONObject jsonObject = new JSONObject();
            switch (type) {
                case 1:
                    List<AreaCityCompany> areaCityCompany = cityCompanyService.queryList(null);
                    List<JSONObject> objects1 = new ArrayList<>();
                    for (AreaCityCompany areaCityCompany1 : areaCityCompany) {
                        JSONObject object = new JSONObject();
                        object.put("cityCompanyId", areaCityCompany1.getId());
                        object.put("cityCompany", areaCityCompany1.getCityCompany());
                        objects1.add(object);
                    }
                    jsonObject.put("company", objects1);
                    break;
                case 2:
                    if (cityCompanyId == null) {
                        return getSjResult(result, null, false, "2", null, "市公司为空");
                    }
                    AreaManagementUnit managementUnit = new AreaManagementUnit();
                    managementUnit.setCityCompanyId(cityCompanyId);
                    List<AreaManagementUnit> managementUnits = managementUnitService.queryList(managementUnit);
                    List<JSONObject> objects2 = new ArrayList<>();
                    for (AreaManagementUnit managementUnit1 : managementUnits) {
                        JSONObject object = new JSONObject();
                        object.put("managementUnitId", managementUnit1.getId());
                        object.put("managementUnit", managementUnit1.getManagementUnit());
                        objects2.add(object);
                    }
                    jsonObject.put("unit", objects2);
                    break;
                case 3:
                    if (cityCompanyId == null) {
                        return getSjResult(result, null, false, "2", null, "市公司为空");
                    }
                    if (managementUnitId == null) {
                        return getSjResult(result, null, false, "2", null, "经营单元为空");
                    }
                    AreaBranchOffice areaBranchOffice = new AreaBranchOffice();
                    areaBranchOffice.setCityCompanyId(cityCompanyId);
                    areaBranchOffice.setManagementUnitId(managementUnitId);
                    List<AreaBranchOffice> areaBranchOffices = branchOfficeService.queryList(areaBranchOffice);
                    List<JSONObject> objects3 = new ArrayList<>();
                    for (AreaBranchOffice branchOffice : areaBranchOffices) {
                        JSONObject object = new JSONObject();
                        object.put("branchOfficeId", branchOffice.getId());
                        object.put("branchOffice", branchOffice.getBranchOffice());
                        objects3.add(object);
                    }
                    jsonObject.put("office", objects3);
                    break;
                case 4:
                    if (cityCompanyId == null ) {
                        return getSjResult(result, null, false, "2", null, "市公司为空");
                    }
                    if (managementUnitId == null) {
                        return getSjResult(result, null, false, "2", null, "经营单元为空");
                    }
                    if (branchOfficeId == null) {
                        return getSjResult(result, null, false, "2", null, "支局为空");
                    }
                    AreaContractBody areaContractBody = new AreaContractBody();
                    areaContractBody.setCityCompanyId(cityCompanyId);
                    areaContractBody.setManagementUnitId(managementUnitId);
                    areaContractBody.setBranchOfficeId(branchOfficeId);
                    List<AreaContractBody> areaContractBodies = contractBodyService.queryList(areaContractBody);
                    List<JSONObject> objects4 = new ArrayList<>();
                    for (AreaContractBody contractBody1 : areaContractBodies) {
                        JSONObject object = new JSONObject();
                        object.put("contractBodyId", contractBody1.getId());
                        object.put("contractBody", contractBody1.getContractBody());
                        objects4.add(object);
                    }
                    jsonObject.put("body", objects4);
                    break;
            }
            getSjResult(result, jsonObject, true, "0", null, "查询成功");
        } catch (Exception e) {
            e.printStackTrace();
            getSjResult(result, null, false, "5", null, "系统异常");
        }
        return result;
    }
}
