package com.kuaixiu.nbTelecomSJ.controller;

import com.common.base.controller.BaseController;
import com.common.importExcel.ImportReport;
import com.common.paginate.Page;
import com.common.wechat.common.util.StringUtils;
import com.kuaixiu.nbTelecomSJ.entity.NBArea;
import com.kuaixiu.nbTelecomSJ.entity.NBCounty;
import com.kuaixiu.nbTelecomSJ.entity.NBManager;
import com.kuaixiu.nbTelecomSJ.service.NBManagerService;
import com.system.api.entity.ResultData;
import com.system.basic.user.entity.SessionUser;
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
 * NBManager Controller
 *
 * @CreateDate: 2019-02-22 下午06:34:26
 * @version: V 1.0
 */
@Controller
public class NBManagerController extends BaseController {
    private static final Logger log = Logger.getLogger(NBManagerController.class);
    @Autowired
    private NBManagerService nbManagerService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/nbTelecomSJ/managerList")
    public ModelAndView list(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {
        String returnView = "nbTelecomSJ/managerList";
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
    @RequestMapping(value = "/nbTelecomSJ/managerListForPage")
    public void queryListForPage(HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        String queryStartTime = request.getParameter("queryStartTime");
        String queryEndTime = request.getParameter("queryEndTime");
        String managerName = request.getParameter("managerName");
        String managerTel = request.getParameter("managerTel");
        String department = request.getParameter("department");
        Page page = getPageByRequest(request);
        NBManager nbManager = new NBManager();
        nbManager.setQueryStartTime(queryStartTime);
        nbManager.setQueryEndTime(queryEndTime);
        nbManager.setManagerName(managerName);
        nbManager.setManagerTel(managerTel);
        nbManager.setDepartment(department);
        nbManager.setPage(page);
        List<NBManager> nbManagers = nbManagerService.queryListForPage(nbManager);

        page.setData(nbManagers);
        this.renderJson(response, page);
    }

    /**
     * 新增
     */
    @RequestMapping(value = "nbTelecomSJ/addManager")
    @ResponseBody
    public ResultData addManager(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        try {
            String managerName = request.getParameter("managerName");
            String managerTel = request.getParameter("managerTel");
            String department = request.getParameter("department");

            if (StringUtils.isBlank(managerName) || StringUtils.isBlank(managerTel) || StringUtils.isBlank(department)) {
                return getResult(result, null, false, "2", "参数为空");
            }
            SessionUser su = getCurrentUser(request);
            NBManager nbManager = new NBManager();
            nbManager.setManagerName(managerName);
            nbManager.setManagerTel(managerTel);
            nbManager.setDepartment(department);
            nbManager.setCreateUserId(su.getUserId());
            nbManager.setUpdateUserId(su.getUserId());
            nbManagerService.add(nbManager);

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
    @RequestMapping(value = "/nbTelecomSJ/update")
    public ModelAndView update(HttpServletRequest request,
                               HttpServletResponse response) throws Exception {
        String managerId = request.getParameter("managerId");
        NBManager nbManager = nbManagerService.queryById(managerId);
        request.setAttribute("nbManager", nbManager);
        String returnView = "nbTelecomSJ/editManager";
        return new ModelAndView(returnView);
    }

    /**
     * 编辑
     */
    @RequestMapping(value = "nbTelecomSJ/udapateManager")
    @ResponseBody
    public ResultData udapateManager(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        try {
            String managerId = request.getParameter("managerId");
            String managerName = request.getParameter("managerName");
            String managerTel = request.getParameter("managerTel");
            String department = request.getParameter("department");

            if (StringUtils.isBlank(managerId) || StringUtils.isBlank(managerName) || StringUtils.isBlank(managerTel) || StringUtils.isBlank(department)) {
                return getResult(result, null, false, "2", "参数为空");
            }
            SessionUser su = getCurrentUser(request);
            NBManager nbManager = new NBManager();
            nbManager.setManagerId(Integer.valueOf(managerId));
            nbManager.setManagerName(managerName);
            nbManager.setManagerTel(managerTel);
            nbManager.setDepartment(department);
            nbManager.setCreateUserId(su.getUserId());
            nbManager.setUpdateUserId(su.getUserId());
            nbManagerService.saveUpdate(nbManager);

            getResult(result, null, true, "0", "成功");
        } catch (Exception e) {
            getResult(result, null, false, "1", "新增失败");
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }

    /**
     * 删除
     */
    @RequestMapping(value = "nbTelecomSJ/deleteManager")
    @ResponseBody
    public ResultData deleteManager(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        try {
            String managerId = request.getParameter("id");

            if (StringUtils.isBlank(managerId)) {
                return getResult(result, null, false, "2", "参数为空");
            }
            NBManager nbManager = nbManagerService.queryById(managerId);
            if (nbManager == null) {
                return getResult(result, null, false, "2", "参数为空");
            }
            nbManagerService.getDao().deleteByManagerId(nbManager);

            getResult(result, null, true, "0", "成功");
        } catch (Exception e) {
            getResult(result, null, false, "1", "新增失败");
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
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
    @RequestMapping(value = "/nbTelecomSJ/importManager")
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
                    nbManagerService.importExcel(myfile,report,getCurrentUser(request));
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
