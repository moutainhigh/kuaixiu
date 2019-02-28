package com.kuaixiu.nbTelecomSJ.controller;

import com.common.base.controller.BaseController;
import com.common.paginate.Page;
import com.common.wechat.common.util.StringUtils;
import com.kuaixiu.nbTelecomSJ.entity.NBArea;
import com.kuaixiu.nbTelecomSJ.entity.NBCounty;
import com.kuaixiu.nbTelecomSJ.entity.NBManager;
import com.kuaixiu.nbTelecomSJ.service.NBManagerService;
import com.system.api.entity.ResultData;
import com.system.basic.user.entity.SessionUser;
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
}
