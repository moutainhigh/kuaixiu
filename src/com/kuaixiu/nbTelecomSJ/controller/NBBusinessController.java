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
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
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
 * @CreateDate: 2019-02-23 上午11:53:31
 * @version: V 1.0
 */
@Controller
public class NBBusinessController extends BaseController {
    private static final Logger log= Logger.getLogger(NBBusinessController.class);

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
            String manager=params.getString("manager");//走访人
            String managerTel=params.getString("managerTel");//走访人手机号
            String countyId=params.getString("countyId");//单元id
            String officeId=params.getString("officeId");//支局id
            String areaId=params.getString("areaId");//包区id
            String companyName=params.getString("companyName");//单位名字
            String landline=params.getString("landline");//固定电话
            String broadband=params.getString("broadband");//宽带
            String address=params.getString("address");//地址
            String addressType=params.getString("addressType");//地址属性
            String demand=params.getString("demand");//通信需求
            String remarks=params.getString("remarks");//备注
            String coutomerName=params.getString("coutomerName");//联系人
            String telephone=params.getString("telephone");//联系人手机号

            if(StringUtils.isBlank(countyId)||StringUtils.isBlank(manager)||StringUtils.isBlank(managerTel)
                    ||StringUtils.isBlank(officeId)||StringUtils.isBlank(areaId)||StringUtils.isBlank(address)
                    ||StringUtils.isBlank(addressType)||StringUtils.isBlank(broadband)||StringUtils.isBlank(landline)
                    ||StringUtils.isBlank(demand)||StringUtils.isBlank(companyName)){
                return getResult(result,null,false,"2","参数为空");
            }
            if(!"1".equals(demand)){
                if(StringUtils.isBlank(coutomerName)||StringUtils.isBlank(telephone)){
                    return getResult(result,null,false,"2","请填写联系人或手机号");
                }
            }
            NBManager nbManager=new NBManager();
            nbManager.setManagerName(manager);
            nbManager.setManagerTel(managerTel);
            List<NBManager> nbManagers=nBManagerService.queryList(nbManager);
            if(CollectionUtils.isEmpty(nbManagers)){
                nBManagerService.getDao().add(nbManager);
                nbManagers=nBManagerService.queryList(nbManager);
            }

            NBBusiness nbBusiness=new NBBusiness();
            nbBusiness.setCountyId(Integer.valueOf(countyId));
            nbBusiness.setAreaId(Integer.valueOf(areaId));
            nbBusiness.setOfficeId(Integer.valueOf(officeId));
            nbBusiness.setManagerId(nbManagers.get(0).getManagerId());
            nbBusiness.setCompanyName(companyName);
            nbBusiness.setBroadband(Integer.valueOf(broadband));
            nbBusiness.setLandline(Integer.valueOf(landline));
            nbBusiness.setAddress(address);
            nbBusiness.setAddressType(Integer.valueOf(addressType));
            nbBusiness.setDemand(Integer.valueOf(demand));
            nbBusiness.setRemarks(remarks);
            nbBusiness.setCoutomerName(coutomerName);
            nbBusiness.setTelephone(telephone);
            nBBusinessService.add(nbBusiness);

            NBArea nbArea=nBAreaService.getDao().queryByAreaId(areaId);
            SmsSendUtil.mailSendSmsTobusiness(nbManager,nbBusiness,nbArea);

            getResult(result,null,true,"0","提交成功");
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }


}
