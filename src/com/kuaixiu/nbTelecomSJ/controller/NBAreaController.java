package com.kuaixiu.nbTelecomSJ.controller;

import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.importExcel.ImportReport;
import com.common.wechat.common.util.StringUtils;
import com.kuaixiu.nbTelecomSJ.entity.NBArea;
import com.kuaixiu.nbTelecomSJ.service.NBAreaService;
import com.system.api.entity.ResultData;
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
    private static final Logger log= Logger.getLogger(NBAreaController.class);

    @Autowired
    private NBAreaService nBAreaService;

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

            List<NBArea> nbAreas=nBAreaService.getDao().queryByCountyId(countyId);

            List<Map<String, Object>> maps = new ArrayList<>();
            for (NBArea nbArea : nbAreas) {
                Map<String, Object> map = new HashedMap();
                map.put("officeId", nbArea.getOfficeId());
                map.put("branchOffice", nbArea.getBranchOffice());
                NBArea nbArea1=new NBArea();
                nbArea1.setCountyId(Integer.valueOf(countyId));
                nbArea1.setBranchOffice(nbArea.getBranchOffice());
                List<NBArea> nbAreas1=nBAreaService.getDao().queryByBranchOffice(nbArea1);
                List<Map<String, Object>> maps1 = new ArrayList<>();
                for(NBArea nbArea2:nbAreas1){
                    Map<String, Object> map1 = new HashedMap();
                    map1.put("areaId", nbArea2.getAreaId());
                    map1.put("areaPerson", nbArea2.getAreaName());
                    maps1.add(map1);
                }
                map.put("area",maps1);
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
     * @param myfile
     *            上传的文件
     * @param request
     *            请求实体
     * @param response
     *            返回实体
     * @date 2016-5-9
     * @author
     * @throws IOException
     *             异常信息
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
        try{
            if(myfile != null && org.apache.commons.lang3.StringUtils.isNotBlank(myfile.getOriginalFilename())){
                String fileName=myfile.getOriginalFilename();
                //扩展名
                String extension= FilenameUtils.getExtension(fileName);
                if (!extension.equalsIgnoreCase("xls")){
                    errorMsg.append("导入文件格式错误！只能导入excel文件！");
                }
                else{
                    nBAreaService.importExcel(myfile,report,getCurrentUser(request));
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
}
