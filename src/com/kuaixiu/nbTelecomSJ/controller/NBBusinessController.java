package com.kuaixiu.nbTelecomSJ.controller;

import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.util.SmsSendUtil;
import com.common.wechat.api.WxMpService;
import com.common.wechat.bean.result.WxMpOAuth2AccessToken;
import com.common.wechat.common.exception.WxErrorException;
import com.common.wechat.common.util.StringUtils;
import com.kuaixiu.nbTelecomSJ.entity.NBArea;
import com.kuaixiu.nbTelecomSJ.entity.NBBusiness;
import com.kuaixiu.nbTelecomSJ.entity.NBCounty;
import com.kuaixiu.nbTelecomSJ.entity.NBManager;
import com.kuaixiu.nbTelecomSJ.service.NBAreaService;
import com.kuaixiu.nbTelecomSJ.service.NBBusinessService;
import com.kuaixiu.nbTelecomSJ.service.NBCountyService;
import com.kuaixiu.nbTelecomSJ.service.NBManagerService;
import com.system.api.entity.ResultData;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * NBBusiness Controller
 *
 * @CreateDate: 2019-02-23 上午11:53:31
 * @version: V 1.0
 */
@Controller
public class NBBusinessController extends BaseController {
    private static final Logger log = Logger.getLogger(NBBusinessController.class);

    @Autowired
    private NBBusinessService nBBusinessService;
    @Autowired
    private NBManagerService nBManagerService;
    @Autowired
    private NBAreaService nBAreaService;
    @Autowired
    protected NBCountyService nbCountyService;
    @Autowired
    protected WxMpService wxMpService;

    /**
     * 用code换取oauth2的openid
     * 详情请见: http://mp.weixin.qq.com/wiki/1/8a5ce6257f1d3b2afb20f83e72b72ce9.html
     *
     * @param response
     * @param request
     */
    @RequestMapping(value = "NBTelecomSJ/getOpenid")
    public ResultData getOpenid(HttpServletResponse response, HttpServletRequest request) {
        ResultData resultData = new ResultData();
        WxMpOAuth2AccessToken accessToken;
        try {
            JSONObject params = getPrarms(request);
            String code = params.getString("code");
            accessToken = this.wxMpService.oauth2getAccessToken(code);
            Map<String, String> map = new HashMap();
            map.put("openId", accessToken.getOpenId());
            getResult(resultData, map, true, "0", "获取成功");
        } catch (WxErrorException e) {
            getResult(resultData, null, false, "2", "失败");
            log.error(e.getError().toString());
        }
        return resultData;
    }

    /**
     * 获取上次填写数据（会员机制）
     */
    @RequestMapping(value = "NBTelecomSJ/getLastTime")
    @ResponseBody
    public ResultData getLaseTime(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        try {
            JSONObject params = getPrarms(request);
            String openId = params.getString("openId");//微信OpenId
            if (StringUtils.isBlank(openId)) {
                return getResult(result, null, false, "2", "参数为空");
            }
            Map<String, Object> map = new HashedMap();

            NBBusiness nbBusiness = nBBusinessService.queryByOpenId(openId);
            if (nbBusiness != null) {
                map.put("address", nbBusiness.getAddress());
                NBCounty nbCounty = nbCountyService.queryById(nbBusiness.getCountyId());
                if (nbCounty != null) {
                    map.put("countyId", nbCounty.getCountyId());
                    map.put("county", nbCounty.getCounty());
                }
                NBArea nbArea = nBAreaService.getDao().queryByAreaId(String.valueOf(nbBusiness.getAreaId()));
                if (nbArea != null) {
                    map.put("officeId",nbArea.getOfficeId());
                    map.put("branchOffice",nbArea.getBranchOffice());
                    map.put("areaId",nbArea.getAreaId());
                    map.put("areaPerson",nbArea.getAreaName());
                }
                NBManager nbManager = nBManagerService.queryById(nbBusiness.getManagerId());
                if (nbManager != null) {
                    map.put("manager",nbManager.getManagerName());
                    map.put("managerTel",nbManager.getManagerTel());
                }
            }else{
                return getResult(result, null, true, "1", "数据为空");
            }
            getResult(result, map, true, "0", "查询成功");
        } catch (Exception e) {
            getResult(result, null, false, "2", "失败");
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }


    /**
     * 保存商机信息
     */
    @RequestMapping(value = "NBTelecomSJ/submit")
    @ResponseBody
    public ResultData submit(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        try {
            JSONObject params = getPrarms(request);
            String openId = params.getString("openId");//微信OpenId
            String manager = params.getString("manager");//走访人
            String managerTel = params.getString("managerTel");//走访人手机号
            String countyId = params.getString("countyId");//单元id
            String officeId = params.getString("officeId");//支局id
            String areaId = params.getString("areaId");//包区id
            String companyName = params.getString("companyName");//单位名字
            String landline = params.getString("landline");//固定电话  1,2,3
            String broadband = params.getString("broadband");//宽带   1,2
            String address = params.getString("address");//地址
            String addressType = params.getString("addressType");//地址属性
            String demand = params.getString("demand");//通信需求
            String remarks = params.getString("remarks");//备注
            String coutomerName = params.getString("coutomerName");//联系人
            String telephone = params.getString("telephone");//联系人手机号

            if (StringUtils.isBlank(countyId) || StringUtils.isBlank(manager) || StringUtils.isBlank(managerTel)
                    || StringUtils.isBlank(officeId) || StringUtils.isBlank(areaId) || StringUtils.isBlank(address)
                    || StringUtils.isBlank(addressType) || StringUtils.isBlank(broadband) || StringUtils.isBlank(landline)
                    || StringUtils.isBlank(demand) || StringUtils.isBlank(companyName)) {
                return getResult(result, null, false, "2", "参数为空");
            }
            if (!"1".equals(demand)) {
                if (StringUtils.isBlank(coutomerName) || StringUtils.isBlank(telephone)) {
                    return getResult(result, null, false, "2", "请填写联系人或手机号");
                }
            }
            NBManager nbManager = new NBManager();
            nbManager.setManagerName(manager);
            nbManager.setManagerTel(managerTel);
            List<NBManager> nbManagers = nBManagerService.queryList(nbManager);
            if (CollectionUtils.isEmpty(nbManagers)) {
                nBManagerService.getDao().add(nbManager);
                nbManagers = nBManagerService.queryList(nbManager);
            }

            NBBusiness nbBusiness = new NBBusiness();
            nbBusiness.setOpenId(openId);
            nbBusiness.setCountyId(Integer.valueOf(countyId));
            nbBusiness.setAreaId(Integer.valueOf(areaId));
            nbBusiness.setOfficeId(Integer.valueOf(officeId));
            nbBusiness.setManagerId(nbManagers.get(0).getManagerId());
            nbBusiness.setCompanyName(companyName);
            nbBusiness.setBroadband(broadband);
            nbBusiness.setLandline(landline);
            nbBusiness.setAddress(address);
            nbBusiness.setAddressType(Integer.valueOf(addressType));
            nbBusiness.setDemand(Integer.valueOf(demand));
            nbBusiness.setRemarks(remarks);
            nbBusiness.setCoutomerName(coutomerName);
            nbBusiness.setTelephone(telephone);
            nBBusinessService.add(nbBusiness);

            NBArea nbArea = nBAreaService.getDao().queryByAreaId(areaId);
            SmsSendUtil.mailSendSmsTobusiness(nbManager, nbBusiness, nbArea);

            getResult(result, null, true, "0", "提交成功");
        } catch (Exception e) {
            getResult(result, null, false, "2", "失败");
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }


}
