package com.kuaixiu.recycle.controller;

import com.common.base.controller.BaseController;
import com.common.exception.SystemException;
import com.common.paginate.Page;
import com.google.common.collect.Maps;
import com.kuaixiu.recycle.entity.RecycleSystem;
import com.kuaixiu.recycle.service.RecycleSystemService;
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



@Controller
public class RecycleSystemController extends BaseController {

    @Autowired
    private RecycleSystemService recycleSystemService;
    /**
     * 系统来源列表查询
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "recycle/recycleSystem/list")
    public ModelAndView RecycleSystem(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="recycle/recycleSystem";
        return new ModelAndView(returnView);
    }
    
    
    
    /**
	 * 刷新数据
	 */
	@RequestMapping(value = "recycle/recycleSystem/queryListForPage")
	public void systemForPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		SessionUser su = getCurrentUser(request);
//        if (su.getType() != SystemConstant.USER_TYPE_SYSTEM&&su.getType() != SystemConstant.USER_TYPE_CUSTOMER_SERVICE) {
//			throw new SystemException("对不起，您没有操作权限!");
//		}
		 //获取查询条件
        String name = request.getParameter("query_name");
		
		RecycleSystem r=new RecycleSystem();
		r.setName(name);
		Page page = getPageByRequest(request);
		r.setPage(page);
		r.setIsDel(0);
		List<RecycleSystem> list =recycleSystemService.queryListForPage(r);
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
    @RequestMapping(value = "recycle/recycleSystem/add")
    public ModelAndView add(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
//    	SessionUser su = getCurrentUser(request);
//    	if (su.getType() != SystemConstant.USER_TYPE_SYSTEM) {
//			throw new SystemException("对不起，您没有操作权限!");
//		}
        String returnView ="recycle/add";
        return new ModelAndView(returnView);
    }
    
    /**
     * index
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "recycle/recycleSystem/save")
    public void save(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        Map<String, Object> resultMap = Maps.newHashMap();
        //获取项目名称
        String name = request.getParameter("addName");
        String smsType = request.getParameter("smsType");
        //序号
        String sortStr= request.getParameter("addSort");
        if(StringUtils.isBlank(sortStr)){
        	sortStr = "99";
        }
            
        SessionUser su = getCurrentUser(request);
//    	if (su.getType() != SystemConstant.USER_TYPE_SYSTEM) {
//			throw new SystemException("对不起，您没有操作权限!");
//		}
        RecycleSystem t = new RecycleSystem();
        t.setName(name);
        t.setSmsType(Integer.valueOf(smsType));
        t.setSort(Integer.parseInt(sortStr));
        t.setCreateUserid(su.getUserId());
        t.setIsDel(0);
        recycleSystemService.save(t);
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
    @RequestMapping(value = "recycle/recycleSystem/edit")
    public ModelAndView edit(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
//    	SessionUser su = getCurrentUser(request);
//    	if (su.getType() != SystemConstant.USER_TYPE_SYSTEM) {
//			throw new SystemException("对不起，您没有操作权限!");
//		}
        //获取项目id
        String id = request.getParameter("id");
        //查询项目内容
        RecycleSystem t = recycleSystemService.queryById(id);
        
        request.setAttribute("fromSystem", t);
        String returnView ="recycle/edit";
        return new ModelAndView(returnView);
    }
    
    /**
     * update
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "recycle/recycleSystem/update")
    public void update(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
    	SessionUser su = getCurrentUser(request);
//    	if (su.getType() != SystemConstant.USER_TYPE_SYSTEM) {
//			throw new SystemException("对不起，您没有操作权限!");
//		}
        Map<String, Object> resultMap = Maps.newHashMap();
        //获取项目id
        String id = request.getParameter("id");
        //获取项目名称
        String name = request.getParameter("upName");
        String smsType = request.getParameter("smsType");
        //序号
        String sortStr= request.getParameter("upSort");
        if(StringUtils.isBlank(sortStr)){
        	sortStr = "99";
        }
        RecycleSystem t = recycleSystemService.queryById(id);
        if(t==null){
        	throw new SystemException("该记录不存在");
        }
        t.setName(name);
        t.setSmsType(Integer.valueOf(smsType));
        t.setSort(Integer.parseInt(sortStr));
        t.setUpdateUserid(su.getUserId());
        recycleSystemService.saveUpdate(t);
        
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
    @RequestMapping(value = "recycle/recycleSystem/delete")
    public void delete(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
    	SessionUser su = getCurrentUser(request);
//    	if (su.getType() != SystemConstant.USER_TYPE_SYSTEM) {
//			throw new SystemException("对不起，您没有操作权限!");
//		}
        Map<String, Object> resultMap = Maps.newHashMap();
        //获取项目id
        String id = request.getParameter("id");
        
        recycleSystemService.deleteById(id, su);
        
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
        resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
        renderJson(response, resultMap);
    }
}
