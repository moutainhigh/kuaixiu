package com.kuaixiu.order.controller;

import com.common.base.controller.BaseController;
import com.common.paginate.Page;
import com.google.common.collect.Maps;
import com.kuaixiu.order.entity.FromSystem;
import com.kuaixiu.order.service.FromSystemService;
import com.system.basic.user.entity.SessionUser;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.SystemException;

/**
 * FromSystem Controller
 *
 * @CreateDate: 2017-03-11 下午10:40:00
 * @version: V 1.0
 */
@Controller
public class FromSystemController extends BaseController {

    @Autowired
    private FromSystemService fromSystemService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/fromSystem/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="fromSystem/list";
        return new ModelAndView(returnView);
    }
    
    /**
     * queryListForPage
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/fromSystem/queryListForPage")
    public void queryListForPage(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        //获取查询条件
        String name = request.getParameter("query_name");
        FromSystem t = new FromSystem();
        t.setName(name);
        
        Page page = getPageByRequest(request);
        t.setPage(page);
        t.setIsDel(0);
        List<FromSystem> list = fromSystemService.queryListForPage(t);
        
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
    @RequestMapping(value = "/fromSystem/add")
    public ModelAndView add(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="fromSystem/add";
        return new ModelAndView(returnView);
    }
    
    /**
     * index
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/fromSystem/save")
    public void save(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        Map<String, Object> resultMap = Maps.newHashMap();
        //获取项目名称
        String name = request.getParameter("addName");
        //序号
        String sortStr= request.getParameter("addSort");
        if(StringUtils.isBlank(sortStr)){
        	sortStr = "99";
        }
            
        SessionUser su = getCurrentUser(request);
        FromSystem t = new FromSystem();
        t.setName(name);
        t.setSort(Integer.parseInt(sortStr));
        t.setCreateUserid(su.getUserId());
        t.setIsDel(0);
        fromSystemService.save(t);
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
    @RequestMapping(value = "/fromSystem/edit")
    public ModelAndView edit(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        //获取项目id
        String id = request.getParameter("id");
        //查询项目内容
        FromSystem t = fromSystemService.queryById(id);
        
        request.setAttribute("fromSystem", t);
        String returnView ="fromSystem/edit";
        return new ModelAndView(returnView);
    }
    
    /**
     * update
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/fromSystem/update")
    public void update(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        Map<String, Object> resultMap = Maps.newHashMap();
        //获取项目id
        String id = request.getParameter("id");
        //获取项目名称
        String name = request.getParameter("upName");
        //序号
        String sortStr= request.getParameter("upSort");
        if(StringUtils.isBlank(sortStr)){
        	sortStr = "99";
        }
        SessionUser su = getCurrentUser(request);
        if(StringUtils.isBlank(id)){
        	throw new SystemException("参数错误");
        }
        FromSystem t = new FromSystem();
        t.setId(Long.parseLong(id));
        t.setName(name);
        t.setSort(Integer.parseInt(sortStr));
        t.setUpdateUserid(su.getUserId());
        fromSystemService.update(t);
        
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
    @RequestMapping(value = "/fromSystem/delete")
    public void delete(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        Map<String, Object> resultMap = Maps.newHashMap();
        //获取项目id
        String id = request.getParameter("id");
        SessionUser su = getCurrentUser(request);
        
        fromSystemService.deleteById(id, su);
        
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
        resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
        renderJson(response, resultMap);
    }
}
