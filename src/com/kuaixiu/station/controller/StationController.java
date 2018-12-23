package com.kuaixiu.station.controller;

import com.common.base.controller.BaseController;
import com.common.exception.SystemException;
import com.common.importExcel.ImportReport;
import com.common.paginate.Page;
import com.google.common.collect.Maps;
import com.kuaixiu.station.entity.Station;
import com.kuaixiu.station.service.StationService;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
 * @Auther: anson
 * @Date: 2018/7/26
 * @Description: 站点控制
 */
@Controller
public class StationController extends BaseController {


    @Autowired
    private StationService stationService;

    /**
     * 站点页面
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/telecom/station")
    public ModelAndView card(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {

        String returnView ="station/stationList";
        return new ModelAndView(returnView);
    }


    /**
     * 站点刷新数据
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/telecom/station/queryListForPage")
    public void cardListForPage(HttpServletRequest request,
                                HttpServletResponse response) throws Exception {
        Page page = getPageByRequest(request);
        // 获取 站点ID  站点名称  联系人  验机电话
        String stationId=request.getParameter("query_stationId");
        String stationName=request.getParameter("query_stationName");
        String name=request.getParameter("query_name");
        String tel=request.getParameter("query_tel");
        Station s=new Station();
        s.setId(stationId);
        s.setStationName(stationName);
        s.setName(name);
        s.setTel(tel);
        s.setPage(page);
        List<Station> list = stationService.queryListForPage(s);
        page.setData(list);
        this.renderJson(response, page);
    }


    /**
     * 导入页面
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value="/telecom/station/import")
    public ModelAndView cardImport(HttpServletRequest request,HttpServletResponse response){
        return new ModelAndView("station/importIndex");
    }



    /**
     * 模板导入
     * @param myfile
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/telecom/station/startImport")
    public void doImport(
            @RequestParam("fileInput") MultipartFile myfile,
            HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        // 返回结果，默认失败
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
        ImportReport report = new ImportReport();
        StringBuffer errorMsg = new StringBuffer();
        try{
            if(myfile != null && StringUtils.isNotBlank(myfile.getOriginalFilename())){
                String fileName=myfile.getOriginalFilename();
                //扩展名
                String extension= FilenameUtils.getExtension(fileName);
                if (!extension.equalsIgnoreCase("xls")){
                    errorMsg.append("导入文件格式错误！只能导入excel  xls文件！");
                }
                else{
                    stationService.importExcel(myfile,report,getCurrentUser(request));
                    resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
                    resultMap.put(RESULTMAP_KEY_MSG, "导入成功");
                }
            }
            else{
                errorMsg.append("导入文件为空");
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            errorMsg.append("导入失败");
            resultMap.put(RESULTMAP_KEY_MSG, "导入失败");
        }
        request.setAttribute("report", report);
        resultMap.put(RESULTMAP_KEY_DATA, report);
        renderJson(response, resultMap);
    }

    /**
     * 修改站点启停状态
     * @param request
     * @param response
     */
     @RequestMapping(value = "/telecom/station/updateStatus")
     public void updateStatus(HttpServletRequest request,HttpServletResponse response) throws IOException {
         Map<String, Object> resultMap = Maps.newHashMap();
         String id=request.getParameter("id");   //站点id
         Station station = stationService.queryById(id);
         if(station==null){
             throw new SystemException("该站点不存在");
         }
         //根据站点原先状态 更新其开启状态
         if(station.getIsOpen()==0){
              station.setIsOpen(1);
         }else{
              station.setIsOpen(0);
         }
         stationService.saveUpdate(station);
         resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
         resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
         renderJson(response, resultMap);
     }



    /**
     * 通过名字实时模糊查询
     * @param request
     * @param response
     */
    @RequestMapping(value = "/telecom/station/queryByName")
    public void queryByName(HttpServletRequest request,HttpServletResponse response) throws IOException {
        Map<String, Object> resultMap = Maps.newHashMap();
        String name=request.getParameter("name");   //站点名字
        Station station=new Station();
        station.setStationName(name);
        station.setIsOpen(0);
        List<Station> stationList = stationService.queryByName(station);
        if(stationList.isEmpty()){
            stationList=new ArrayList<Station>();
        }
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
        resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
        resultMap.put("stationList",stationList);
        renderJson(response, resultMap);
    }

    /**
     * 站点删除
     * @param request
     * @param response
     */
    @RequestMapping(value="/telecom/station/delete")
    public void delete(HttpServletRequest request,HttpServletResponse response) throws IOException {
        Map<String, Object> resultMap = Maps.newHashMap();
        String id = request.getParameter("id");   //站点id
        Station station = stationService.queryById(id);
        if(station==null){
            throw new SystemException("该站点不存在");
        }
        station.setIsDel(1);
        stationService.saveUpdate(station);
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
        resultMap.put(RESULTMAP_KEY_MSG, "操作成功");
        renderJson(response, resultMap);
    }

}
