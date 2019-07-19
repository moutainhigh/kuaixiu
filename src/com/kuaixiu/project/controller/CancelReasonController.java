package com.kuaixiu.project.controller;

import com.common.base.controller.BaseController;
import com.common.exception.SystemException;
import com.common.paginate.Page;
import com.google.common.collect.Maps;
import com.kuaixiu.project.entity.CancelReason;
import com.kuaixiu.project.service.CancelReasonService;
import com.system.basic.user.entity.SessionUser;
import com.system.constant.SystemConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
* @author: anson
* @CreateDate: 2017年7月20日 下午9:21:49
* @version: V 1.0
* 
*/
@Controller
public class CancelReasonController extends BaseController {

	@Autowired
	private CancelReasonService cancelReasonService;
	
	
	 /**
     * 标签列表查询
     */
    @RequestMapping(value = "/cancelReason/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="project/cancelReasonList";
        return new ModelAndView(returnView);
    }
    
    
    /**
     * queryListForPage
     */
    @RequestMapping(value = "/resource/queryListForPage")
    public void queryListForPage(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
    	//获取登录用户
        SessionUser su = getCurrentUser(request);
        if(su.getType() != SystemConstant.USER_TYPE_SYSTEM){
        	 throw new SystemException("对不起，您无权查看此信息！");
        }
        CancelReason can=new CancelReason();

        Page page = getPageByRequest(request);
        can.setPage(page);
        List<CancelReason> list = cancelReasonService.queryListForPage(can);
        
        page.setData(list);
        this.renderJson(response, page);
    }
    
    
    /**
     * add  
     * 新增标签页面
     */
    @RequestMapping(value = "/resource/add")
    public ModelAndView add(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
    	//获取登录用户
        SessionUser su = getCurrentUser(request);
        if(su.getType() != SystemConstant.USER_TYPE_SYSTEM){
        	 throw new SystemException("对不起，您无权查看此信息！");
        }
        String returnView ="project/addCancelReason";
        return new ModelAndView(returnView);
    }
    
    
    /**
     * 保存标签
     */
    @RequestMapping(value = "/resource/save")
    public void save(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        Map<String, Object> resultMap = Maps.newHashMap();
        SessionUser su = getCurrentUser(request);
        //获取标签内容
        String reason = request.getParameter("addName");
        //排序
        String sort = request.getParameter("addSort");
       
        System.out.println("增加标签");
        
        CancelReason can=new CancelReason();
        can.setReason(reason);
        if(!StringUtils.isBlank(sort)){
       	 can.setSort(Integer.parseInt(sort));
        }
        can.setCreateUserid(su.getUserId());
        cancelReasonService.add(can);
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
        resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
        renderJson(response, resultMap);
    }
    
    
    /**
     * delete 删除标签
     */
    @RequestMapping(value = "/resource/delete")
    public void delete(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
    	//获取登录用户
        SessionUser su = getCurrentUser(request);
        if(su.getType() != SystemConstant.USER_TYPE_SYSTEM){
        	 throw new SystemException("对不起，您无权查看此信息！");
        }
        Map<String, Object> resultMap = Maps.newHashMap();
        //获取标签id
        String id = request.getParameter("id");
        CancelReason can=cancelReasonService.queryById(id);
        can.setUpdateUserid(su.getUserId());
        can.setIsDel(1);
        cancelReasonService.saveUpdate(can);
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
        resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
        renderJson(response, resultMap);
    }
    
    
    /**
     * edit
     */
    @RequestMapping(value = "/resource/edit")
    public ModelAndView edit(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
    	//获取登录用户
        SessionUser su = getCurrentUser(request);
        if(su.getType() != SystemConstant.USER_TYPE_SYSTEM){
        	 throw new SystemException("对不起，您无权查看此信息！");
        }
        //获取标签id
        String id = request.getParameter("id");
        //查询品牌内容
        CancelReason can=cancelReasonService.queryById(id);
        request.setAttribute("cancelReason", can);
        String returnView ="project/editCancelReason";
        return new ModelAndView(returnView);
    }
    
    
    /**
     * update 更新标签
     */
    @RequestMapping(value = "/resource/update")
    public void update(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
    	//获取登录用户
        SessionUser su = getCurrentUser(request);
        if(su.getType() != SystemConstant.USER_TYPE_SYSTEM){
        	 throw new SystemException("对不起，您无权查看此信息！");
        }
        Map<String, Object> resultMap = Maps.newHashMap();
        //获取标签id
        String id = request.getParameter("id");
        //获取标签名称
        String reason = request.getParameter("upName");
        //排序
        String sort = request.getParameter("upSort");
        CancelReason can=cancelReasonService.queryById(id);
        if(!StringUtils.isBlank(sort)){
          	 can.setSort(Integer.parseInt(sort));
           }
        can.setReason(reason);
        can.setUpdateUserid(su.getUserId());
        cancelReasonService.saveUpdate(can);
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
        resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
        renderJson(response, resultMap);
    }
    
}
