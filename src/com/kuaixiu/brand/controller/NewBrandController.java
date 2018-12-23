package com.kuaixiu.brand.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.common.base.controller.BaseController;
import com.common.paginate.Page;
import com.google.common.collect.Maps;
import com.kuaixiu.brand.entity.NewBrand;
import com.kuaixiu.brand.service.NewBrandService;
import com.system.basic.user.entity.SessionUser;

/**
* @author: anson
* @CreateDate: 2017年6月13日 下午4:24:11
* @version: V 1.0
* 
*/
@Controller
public class NewBrandController extends BaseController {
     @Autowired
	 private NewBrandService newBrandService;
     
     /**
      * 列表查询
      */
     @RequestMapping(value = "/newBrand/list")
     public ModelAndView list(HttpServletRequest request,
                               HttpServletResponse response) throws Exception {
         
         String returnView ="newBrand/list";
         return new ModelAndView(returnView);
     }
     
     /**
      * queryListForPage
      */
     @RequestMapping(value = "/newBrand/queryListForPage")
     public void queryListForPage(HttpServletRequest request,
                               HttpServletResponse response) throws Exception {
         //获取查询条件
         String name = request.getParameter("query_name");
         NewBrand b = new NewBrand();
         b.setName(name);
         
         Page page = getPageByRequest(request);
         b.setPage(page);
         List<NewBrand> list = newBrandService.queryListForPage(b);
         
         page.setData(list);
         this.renderJson(response, page);
     }
     
     /**
      * add
      */
     @RequestMapping(value = "/newBrand/add")
     public ModelAndView add(HttpServletRequest request,
                               HttpServletResponse response) throws Exception {
         
         String returnView ="newBrand/addBrand";
         return new ModelAndView(returnView);
     }
     
     /**
      * index
      */
     @RequestMapping(value = "/newBrand/save")
     public void save(HttpServletRequest request,
                               HttpServletResponse response) throws Exception {
         System.out.println("新品牌保存");
         Map<String, Object> resultMap = Maps.newHashMap();
         //获取品牌名称
         String name = request.getParameter("addName");
         //排序
         String sort = request.getParameter("addSort");
       
             
         SessionUser su = getCurrentUser(request);
         NewBrand b = new NewBrand();
         b.setName(name);
         if(!StringUtils.isBlank(sort)){
        	 b.setSort(Integer.parseInt(sort));
         }
         b.setCreateUserid(su.getUserId());
         b.setIsDel(0);
         newBrandService.save(b);
         resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
         resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
         renderJson(response, resultMap);
     }
     
     /**
      * edit
      */
     @RequestMapping(value = "/newBrand/edit")
     public ModelAndView edit(HttpServletRequest request,
                               HttpServletResponse response) throws Exception {
         
         //获取品牌id
         String id = request.getParameter("id");
         //查询品牌内容
         NewBrand b=(NewBrand)newBrandService.queryById(id);
         request.setAttribute("brand", b);
         String returnView ="newBrand/editBrand";
         return new ModelAndView(returnView);
     }
     
     /**
      * update
      */
     @RequestMapping(value = "/newBrand/update")
     public void update(HttpServletRequest request,
                               HttpServletResponse response) throws Exception {
         
         Map<String, Object> resultMap = Maps.newHashMap();
         //获取品牌id
         String id = request.getParameter("id");
         //获取品牌名称
         String name = request.getParameter("upName");
         //排序
         String sort = request.getParameter("upSort");
         if(StringUtils.isBlank(sort)){
         	sort = "99";
         }
         SessionUser su = getCurrentUser(request);
         
         NewBrand b = new NewBrand();
         b.setId(id);
         b.setName(name);
         b.setSort(Integer.parseInt(sort));
         b.setUpdateUserid(su.getUserId());
         newBrandService.update(b);
         
         resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
         resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
         renderJson(response, resultMap);
     }
     
     /**
      * delete
      */
     @RequestMapping(value = "/newBrand/delete")
     public void delete(HttpServletRequest request,
                               HttpServletResponse response) throws Exception {
         
         Map<String, Object> resultMap = Maps.newHashMap();
         //获取品牌id
         String id = request.getParameter("id");
         SessionUser su = getCurrentUser(request);
         
         newBrandService.deleteById(id, su);
         
         resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
         resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
         renderJson(response, resultMap);
     }
}
