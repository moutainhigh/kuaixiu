package com.kuaixiu.project.controller;

import com.common.base.controller.BaseController;
import com.common.paginate.Page;
import com.google.common.collect.Maps;
import com.kuaixiu.project.entity.Project;
import com.kuaixiu.project.service.ProjectService;
import com.system.basic.user.entity.SessionUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
* Project Controller
* @author: lijx
*
*/
@Controller
public class ProjectController extends BaseController {

    @Autowired
    private ProjectService projectService;

    /**
     * index
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/project/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="project/list";
        return new ModelAndView(returnView);
    }
    
    /**
     * queryListForPage
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/project/queryListForPage")
    public void queryListForPage(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        //获取查询条件
        String name = request.getParameter("query_name");
        Project project = new Project();
        project.setName(name);
        
        Page page = getPageByRequest(request);
        project.setPage(page);
        project.setIsDel(0);
        List<Project> list = projectService.queryListForPage(project);
        
        page.setData(list);
        this.renderJson(response, page);
    }
    
    /**
     * add
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/project/add")
    public ModelAndView add(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="project/addProject";
        return new ModelAndView(returnView);
    }
    
    /**
     * index
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/project/save")
    public void save(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        Map<String, Object> resultMap = Maps.newHashMap();
        //获取项目名称
        String name = request.getParameter("addName");
            
        SessionUser su = getCurrentUser(request);
        Project p = new Project();
        p.setName(name);
        p.setCreateUserid(su.getUserId());
        p.setIsAll(0);
        p.setIsDel(0);
        p.setPrice(new BigDecimal(0));
        projectService.save(p);
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
        resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
        renderJson(response, resultMap);
    }
    
    /**
     * edit
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/project/edit")
    public ModelAndView edit(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        //获取项目id
        String id = request.getParameter("id");
        //查询项目内容
        Project project = projectService.queryById(id);
        
        request.setAttribute("project", project);
        String returnView ="project/editProject";
        return new ModelAndView(returnView);
    }
    
    /**
     * update
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/project/update")
    public void update(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        Map<String, Object> resultMap = Maps.newHashMap();
        //获取项目id
        String id = request.getParameter("id");
        //获取项目名称
        String name = request.getParameter("upName");
        SessionUser su = getCurrentUser(request);
        
        Project p = new Project();
        p.setId(id);
        p.setName(name);
        p.setUpdateUserid(su.getUserId());
        projectService.update(p);
        
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
        resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
        renderJson(response, resultMap);
    }
    
    /**
     * delete
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/project/delete")
    public void delete(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        Map<String, Object> resultMap = Maps.newHashMap();
        //获取项目id
        String id = request.getParameter("id");
        SessionUser su = getCurrentUser(request);
        
        projectService.deleteById(id, su);
        
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
        resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
        renderJson(response, resultMap);
    }
}
