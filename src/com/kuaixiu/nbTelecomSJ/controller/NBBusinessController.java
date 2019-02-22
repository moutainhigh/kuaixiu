package com.kuaixiu.nbTelecomSJ.controller;

import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.util.SmsSendUtil;
import com.common.wechat.common.util.StringUtils;
import com.kuaixiu.nbTelecomSJ.entity.NBArea;
import com.kuaixiu.nbTelecomSJ.entity.NBBusiness;
import com.kuaixiu.nbTelecomSJ.entity.NBManager;
import com.kuaixiu.nbTelecomSJ.service.NBAreaService;
import com.kuaixiu.nbTelecomSJ.service.NBBusinessService;
import com.kuaixiu.nbTelecomSJ.service.NBManagerService;
import com.system.api.entity.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * NBBusiness Controller
 *
 * @CreateDate: 2019-02-22 下午06:32:37
 * @version: V 1.0
 */
@Controller
public class NBBusinessController extends BaseController {

    @Autowired
    private NBBusinessService nBBusinessService;
    @Autowired
    private NBManagerService nBManagerService;
    @Autowired
    private NBAreaService nBAreaService;
    /**
     * 获取经营单元
     */
    @RequestMapping(value = "NBTelecomSJ/submit")
    @ResponseBody
    public ResultData submit(HttpServletRequest request, HttpServletResponse response){
        ResultData result=new ResultData();
        try {
            JSONObject params=getPrarms(request);
            String manager=params.getString("manager");
            String managerTel=params.getString("managerTel");
            String countyId=params.getString("countyId");
            String officeId=params.getString("officeId");
            String areaId=params.getString("areaId");
            String coutomerName=params.getString("coutomerName");
            String telephone=params.getString("telephone");
            String address=params.getString("address");
            String addressType=params.getString("addressType");
            String demand=params.getString("demand");
            String remarks=params.getString("remarks");

            if(StringUtils.isBlank(countyId)||StringUtils.isBlank(manager)||StringUtils.isBlank(managerTel)
                    ||StringUtils.isBlank(officeId)||StringUtils.isBlank(areaId)||StringUtils.isBlank(coutomerName)
                    ||StringUtils.isBlank(telephone)||StringUtils.isBlank(address)||StringUtils.isBlank(addressType)
                    ||StringUtils.isBlank(demand)){
                return getResult(result,null,false,"2","参数为空");
            }
            if("3".equals(demand)){
                if(StringUtils.isBlank(remarks)){
                    return getResult(result,null,false,"2","请填写备注");
                }
            }
            NBManager nbManager=new NBManager();
            nbManager.setManagerName(manager);
            nbManager.setManagerTel(managerTel);
            nBManagerService.getDao().add(nbManager);
            List<NBManager> nbManagers=nBManagerService.queryList(nbManager);

            NBBusiness nbBusiness=new NBBusiness();
            nbBusiness.setCountyId(Integer.valueOf(countyId));
            nbBusiness.setAreaId(Integer.valueOf(areaId));
            nbBusiness.setOfficeId(Integer.valueOf(officeId));
            nbBusiness.setManagerId(nbManagers.get(0).getManagerId());
            nbBusiness.setCoutomerName(coutomerName);
            nbBusiness.setTelephone(telephone);
            nbBusiness.setAddress(address);
            nbBusiness.setAddressType(Integer.valueOf(addressType));
            nbBusiness.setDemand(Integer.valueOf(demand));
            nbBusiness.setRemarks(remarks);
            nBBusinessService.add(nbBusiness);

            NBArea nbArea=nBAreaService.queryById(areaId);
            SmsSendUtil.mailSendSmsTobusiness(nbManager,nbBusiness,nbArea);

            getResult(result,null,true,"0","提交成功");
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
}
