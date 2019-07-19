package com.kuaixiu.nbTelecomSJ.controller;

import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.importExcel.ImportReport;
import com.common.paginate.Page;
import com.common.wechat.common.util.StringUtils;
import com.kuaixiu.nbTelecomSJ.entity.NBArea;
import com.kuaixiu.nbTelecomSJ.entity.NBCounty;
import com.kuaixiu.nbTelecomSJ.service.NBAreaService;
import com.kuaixiu.nbTelecomSJ.service.NBCountyService;
import com.system.api.entity.ResultData;
import com.system.basic.user.entity.SessionUser;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * NBArea Controller
 *
 * @CreateDate: 2019-02-22 下午07:26:25
 * @version: V 1.0
 */
@Controller
public class NBAreaController extends BaseController {
    private static final Logger log = Logger.getLogger(NBAreaController.class);

    @Autowired
    private NBAreaService nBAreaService;
    @Autowired
    private NBCountyService nbCountyService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/nbTelecomSJ/areaList")
    public ModelAndView list(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {
        List<NBCounty> counties = nbCountyService.queryList(null);
        request.setAttribute("counties", counties);
        String returnView = "nbTelecomSJ/areaList";
        return new ModelAndView(returnView);
    }

    /**
     * queryListForPage
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/nbTelecomSJ/nbAreaForPage")
    public void queryListForPage(HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        //获取查询条件
        String queryStartTime = request.getParameter("queryStartTime");
        String queryEndTime = request.getParameter("queryEndTime");
        String countyId = request.getParameter("countyId");
        String officeId = request.getParameter("officeId");
        String areaId = request.getParameter("areaId");
        String areaPerson = request.getParameter("areaPerson");
        String personTel = request.getParameter("personTel");

        NBArea nbArea = new NBArea();
        nbArea.setQueryStartTime(queryStartTime);
        nbArea.setQueryEndTime(queryEndTime);
        if (StringUtils.isNotBlank(countyId)) {
            nbArea.setCountyId(Integer.valueOf(countyId));
        }
        if (StringUtils.isNotBlank(officeId)) {
            nbArea.setOfficeId(Integer.valueOf(officeId));
        }
        if (StringUtils.isNotBlank(areaId)) {
            nbArea.setAreaId(Integer.valueOf(areaId));
        }
        nbArea.setAreaPerson(areaPerson);
        nbArea.setPersonTel(personTel);

        Page page = getPageByRequest(request);
        nbArea.setPage(page);
        List<NBArea> list = nBAreaService.getDao().queryListForPage(nbArea);
        for (NBArea nbArea1 : list) {
            NBCounty nbCounty = nbCountyService.queryById(nbArea1.getCountyId());
            nbArea1.setCounty(nbCounty.getCounty());
        }
        page.setData(list);
        this.renderJson(response, page);
    }

    /**
     * 获取支局包区
     */
    @RequestMapping(value = "NBTelecomSJ/getOfficeAndArea")
    @ResponseBody
    public ResultData getOfficeAndArea(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        try {
            JSONObject params = getPrarms(request);
            String countyId = params.getString("countyId");

            if (StringUtils.isBlank(countyId)) {
                return getResult(result, null, false, "2", "参数为空");
            }

            List<NBArea> nbAreas = nBAreaService.getDao().queryByCountyId(countyId);

            List<Map<String, Object>> maps = new ArrayList<>();
            for (NBArea nbArea : nbAreas) {
                Map<String, Object> map = new HashedMap();
                map.put("officeId", nbArea.getOfficeId());
                map.put("branchOffice", nbArea.getBranchOffice());
                NBArea nbArea1 = new NBArea();
                nbArea1.setCountyId(Integer.valueOf(countyId));
                nbArea1.setBranchOffice(nbArea.getBranchOffice());
                List<NBArea> nbAreas1 = nBAreaService.getDao().queryByBranchOffice(nbArea1);
                List<Map<String, Object>> maps1 = new ArrayList<>();
                for (NBArea nbArea2 : nbAreas1) {
                    Map<String, Object> map1 = new HashedMap();
                    map1.put("areaId", nbArea2.getAreaId());
                    map1.put("areaPerson", nbArea2.getAreaName());
                    maps1.add(map1);
                }
                map.put("area", maps1);
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
     * 新增
     */
    @RequestMapping(value = "nbTelecomSJ/addArea")
    @ResponseBody
    public ResultData addManager(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        try {
            String county = request.getParameter("county");
            String officeName = request.getParameter("officeName");
            String areaName = request.getParameter("areaName");
            String areaPerson = request.getParameter("areaPerson");
            String personTel = request.getParameter("personTel");

            if (StringUtils.isBlank(county) || StringUtils.isBlank(officeName)
                    || StringUtils.isBlank(areaName) || StringUtils.isBlank(areaPerson)
                    || StringUtils.isBlank(personTel)) {
                return getResult(result, null, false, "2", "参数为空");
            }
            SessionUser su = getCurrentUser(request);
            NBCounty nbCounty = nbCountyService.getDao().queryByName(county);

            NBArea nbArea = new NBArea();
            nbArea.setBranchOffice(officeName);
            nbArea.setAreaName(areaName);
            if (nbCounty != null) {
                nbArea.setCountyId(nbCounty.getCountyId());
                List<NBArea> nbAreas = nBAreaService.queryList(nbArea);
                if (!CollectionUtils.isEmpty(nbAreas)) {
                    return getResult(result, null, false, "3", "包区已存在");
                }
            } else {
                NBCounty nbCounty1 = new NBCounty();
                nbCounty1.setCounty(county);
                nbCountyService.add(nbCounty1);
                nbCounty1 = nbCountyService.getDao().queryByName(county);
                nbArea.setCountyId(nbCounty1.getCountyId());
            }
            nbArea.setAreaId(Integer.valueOf(nBAreaService.createAreaId()));
            nbArea.setAreaType("商客网格-5级");
            nbArea.setAreaPerson(areaPerson);
            nbArea.setPersonTel(personTel);
            nbArea.setCreateUserId(su.getUserId());
            nbArea.setUpdateUserId(su.getUserId());
            nBAreaService.add(nbArea);

            getResult(result, null, true, "0", "成功");
        } catch (Exception e) {
            getResult(result, null, false, "1", "新增失败");
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }

    /**
     * 编辑
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/nbTelecomSJ/updateArea")
    public ModelAndView update(HttpServletRequest request,
                               HttpServletResponse response) throws Exception {
        String offIceId = request.getParameter("offIceId");
        NBArea nbArea = nBAreaService.queryById(offIceId);
        NBCounty nbCounty = nbCountyService.queryById(nbArea.getCountyId());
        nbArea.setCounty(nbCounty.getCounty());
        request.setAttribute("nbArea", nbArea);
        String returnView = "nbTelecomSJ/editArea";
        return new ModelAndView(returnView);
    }

    /**
     * 编辑
     */
    @RequestMapping(value = "nbTelecomSJ/updateNBArea")
    @ResponseBody
    public ResultData udapateManager(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        try {
            String offIceId = request.getParameter("offIceId");
            String county = request.getParameter("county");
            String officeName = request.getParameter("officeName");
            String areaName = request.getParameter("areaName");
            String areaPerson = request.getParameter("areaPerson");
            String personTel = request.getParameter("personTel");

            if (StringUtils.isBlank(offIceId) || StringUtils.isBlank(county)
                    || StringUtils.isBlank(officeName) || StringUtils.isBlank(areaName)
                    || StringUtils.isBlank(areaPerson)|| StringUtils.isBlank(personTel)) {
                return getResult(result, null, false, "2", "参数为空");
            }
            SessionUser su = getCurrentUser(request);

            NBArea nbArea = nBAreaService.queryById(offIceId);
            NBCounty nbCounty = nbCountyService.getDao().queryByName(county);
            if (nbCounty != null) {
                nbArea.setCountyId(nbCounty.getCountyId());
            } else {
                NBCounty nbCounty1 = new NBCounty();
                nbCounty1.setCounty(county);
                nbCountyService.add(nbCounty1);
                nbCounty1 = nbCountyService.getDao().queryByName(county);
                nbArea.setCountyId(nbCounty1.getCountyId());
            }
            nbArea.setBranchOffice(officeName);
            nbArea.setAreaName(areaName);
            nbArea.setAreaPerson(areaPerson);
            nbArea.setPersonTel(personTel);
            nbArea.setUpdateUserId(su.getUserId());
            nBAreaService.saveUpdate(nbArea);

            getResult(result, null, true, "0", "成功");
        } catch (Exception e) {
            getResult(result, null, false, "1", "编辑失败");
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }

    /**
     * 删除
     */
    @RequestMapping(value = "nbTelecomSJ/deleteNBArea")
    @ResponseBody
    public ResultData deleteManager(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        try {
            String officeId = request.getParameter("id");

            if (StringUtils.isBlank(officeId)) {
                return getResult(result, null, false, "2", "参数为空");
            }
            SessionUser su = getCurrentUser(request);
            NBArea nbArea = nBAreaService.queryById(officeId);
            if (nbArea == null) {
                return getResult(result, null, false, "2", "数据为空");
            }
            nbArea.setUpdateUserId(su.getUserId());
            nBAreaService.getDao().deleteByOfficeId(nbArea);

            getResult(result, null, true, "0", "成功");
        } catch (Exception e) {
            getResult(result, null, false, "1", "删除失败");
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }


    /**
     * 编辑
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/nbTelecomSJ/areaImport")
    public ModelAndView importManager(HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {
        String returnView = "nbTelecomSJ/importNBArea";
        return new ModelAndView(returnView);
    }

    /**
     * fileUpload:文件上传.
     *
     * @param myfile   上传的文件
     * @param request  请求实体
     * @param response 返回实体
     * @throws IOException 异常信息
     * @date 2016-5-9
     * @author
     */
    @RequestMapping(value = "/nbTelecomSJ/importNBArea")
    public void doImport(
            @RequestParam("fileInput") MultipartFile myfile,
            HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        // 返回结果，默认失败
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
        ImportReport report = new ImportReport();
        StringBuffer errorMsg = new StringBuffer();
        try {
            if (myfile != null && org.apache.commons.lang3.StringUtils.isNotBlank(myfile.getOriginalFilename())) {
                String fileName = myfile.getOriginalFilename();
                //扩展名
                String extension = FilenameUtils.getExtension(fileName);
                if (!extension.equalsIgnoreCase("xls")) {
                    errorMsg.append("导入文件格式错误！只能导入excel文件！");
                } else {
                    nBAreaService.importExcel(myfile, report, getCurrentUser(request));
                    resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
                    resultMap.put(RESULTMAP_KEY_MSG, "导入成功");
                }
            } else {
                errorMsg.append("导入文件为空");
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMsg.append("导入失败");
            resultMap.put(RESULTMAP_KEY_MSG, "导入失败");
        }
        request.setAttribute("report", report);
        resultMap.put(RESULTMAP_KEY_DATA, report);
        renderJson(response, resultMap);
    }
}
