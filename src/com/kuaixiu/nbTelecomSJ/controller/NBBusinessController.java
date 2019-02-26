package com.kuaixiu.nbTelecomSJ.controller;

import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.paginate.Page;
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
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
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
    private NBCountyService nbCountyService;
    @Autowired
    private WxMpService wxMpService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/NBTelecomSJ/list")
    public ModelAndView list(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {
        List<NBCounty> counties = nbCountyService.queryList(null);
        request.setAttribute("counties", counties);
        String returnView = "nbTelecomSJ/list";
        return new ModelAndView(returnView);
    }

    /**
     * 获取支局
     */
    @RequestMapping(value = "NBTelecomSJ/getOffice")
    @ResponseBody
    public ResultData getOffice(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        try {
            String countyId = request.getParameter("countyId");

            if (StringUtils.isBlank(countyId)) {
                return getResult(result, null, false, "2", "参数为空");
            }

            List<NBArea> nbAreas = nBAreaService.getDao().queryByCountyId(countyId);

            List<Map<String, Object>> maps = new ArrayList<>();
            for (NBArea nbArea : nbAreas) {
                Map<String, Object> map = new HashedMap();
                map.put("officeId", nbArea.getOfficeId());
                map.put("branchOffice", nbArea.getBranchOffice());
                maps.add(map);
            }
            getResult(result, maps, true, "0", "成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }

    /**
     * 获取包区
     */
    @RequestMapping(value = "NBTelecomSJ/getArea")
    @ResponseBody
    public ResultData getOfficeAndArea(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        try {
            String countyId = request.getParameter("countyId");
            String officeId = request.getParameter("officeId");

            if (StringUtils.isBlank(countyId)) {
                return getResult(result, null, false, "2", "参数为空");
            }

            NBArea nbArea = nBAreaService.queryById(officeId);

            List<Map<String, Object>> maps = new ArrayList<>();
            NBArea nbArea1 = new NBArea();
            nbArea1.setCountyId(Integer.valueOf(countyId));
            nbArea1.setBranchOffice(nbArea.getBranchOffice());
            List<NBArea> nbAreas1 = nBAreaService.getDao().queryByBranchOffice(nbArea1);
            for (NBArea nbArea2 : nbAreas1) {
                Map<String, Object> map = new HashedMap();
                map.put("areaId", nbArea2.getAreaId());
                map.put("areaPerson", nbArea2.getAreaName());
                maps.add(map);
            }
            
            getResult(result, maps, true, "0", "成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }

    /**
     * queryListForPage
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/NBTelecomSJ/queryListForPage")
    public void queryListForPage(HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        //获取查询条件
        String queryStartTime = request.getParameter("queryStartTime");
        String queryEndTime = request.getParameter("queryEndTime");
        String countyId = request.getParameter("countyId");
        String officeId = request.getParameter("officeId");
        String areaId = request.getParameter("areaId");
        String companyName = request.getParameter("companyName");
        String landline = request.getParameter("landline");
        String broadband = request.getParameter("broadband");
        String addressType = request.getParameter("addressType");
        String demand = request.getParameter("demand");

        NBBusiness nbBusiness = new NBBusiness();
        nbBusiness.setQueryStartTime(queryStartTime);
        nbBusiness.setQueryEndTime(queryEndTime);
        if (StringUtils.isNotBlank(countyId)) {
            nbBusiness.setCountyId(Integer.valueOf(countyId));
        }
        if (StringUtils.isNotBlank(officeId)) {
            nbBusiness.setOfficeId(Integer.valueOf(officeId));
        }
        if (StringUtils.isNotBlank(areaId)) {
            nbBusiness.setAreaId(Integer.valueOf(areaId));
        }
        nbBusiness.setCompanyName(companyName);
        nbBusiness.setLandline(landline);
        nbBusiness.setBroadband(broadband);
        if (StringUtils.isNotBlank(addressType)) {
            nbBusiness.setAddressType(Integer.valueOf(addressType));
        }
        if (StringUtils.isNotBlank(demand)) {
            nbBusiness.setDemand(Integer.valueOf(demand));
        }

        Page page = getPageByRequest(request);
        nbBusiness.setPage(page);
        List<Map<String, Object>> list = nBBusinessService.getDao().queryListMapForPage(nbBusiness);
        for(Map map:list){
            String landline1=map.get("landline").toString();
            if (landline1.contains(",")) {
                String[] landlines = landline1.split(",");
                StringBuilder sb=new StringBuilder();
                for (int i = 0; i < landlines.length; i++) {
                    sb.append(getlandlines(landlines[i])+",");
                }
                map.put("landline",sb.toString());
            }else{
                map.put("landline",getlandlines(landline1));
            }
            String broadband1=map.get("broadband").toString();
            if (broadband1.contains(",")) {
                String[] broadbands = broadband1.split(",");
                StringBuilder sb=new StringBuilder();
                for (int i = 0; i < broadbands.length; i++) {
                    sb.append(getlandlines(broadbands[i])+",");
                }
                map.put("broadband",sb.toString());
            }else{
                map.put("broadband",getlandlines(broadband1));
            }
        }
        page.setData(list);
        this.renderJson(response, page);
    }

    private String getlandlines(String landline){
        String landline1="";
        switch (landline) {
            case "1":
                landline1 = "联通";
                break;
            case "2":
                landline1 = "电信";
                break;
            case "3":
                landline1 = "移动";
                break;
            case "4":
                landline1 = "无";
                break;
            default:
                landline1 = "";
        }
        return landline1;
    }

    /**
     * 用code换取oauth2的openid
     * 详情请见: http://mp.weixin.qq.com/wiki/1/8a5ce6257f1d3b2afb20f83e72b72ce9.html
     *
     * @param response
     * @param request
     */
    @RequestMapping(value = "NBTelecomSJ/getOpenid")
    @ResponseBody
    public ResultData getOpenid(HttpServletResponse response, HttpServletRequest request) {
        ResultData resultData = new ResultData();
        WxMpOAuth2AccessToken accessToken;
        try {
            JSONObject params = getPrarms(request);
            String code = params.getString("code");
            log.info("request:code="+code);
            accessToken = this.wxMpService.oauth2getAccessToken(code);
            Map<String, String> map = new HashMap();
            map.put("openId", accessToken.getOpenId());
            log.info("response:openId="+accessToken.getOpenId());
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
                    map.put("officeId", nbArea.getOfficeId());
                    map.put("branchOffice", nbArea.getBranchOffice());
                    map.put("areaId", nbArea.getAreaId());
                    map.put("areaPerson", nbArea.getAreaName());
                }
                NBManager nbManager = nBManagerService.queryById(nbBusiness.getManagerId());
                if (nbManager != null) {
                    map.put("manager", nbManager.getManagerName());
                    map.put("managerTel", nbManager.getManagerTel());
                }
            } else {
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
                return getResult(result, null, false, "2", "您没有提交权限");
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
