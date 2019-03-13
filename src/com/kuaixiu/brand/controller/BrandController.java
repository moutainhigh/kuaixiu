package com.kuaixiu.brand.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.common.base.controller.BaseController;
import com.common.exception.SystemException;
import com.common.paginate.Page;
import com.google.common.collect.Maps;
import com.kuaixiu.brand.entity.Brand;
import com.kuaixiu.brand.service.BrandService;
import com.system.basic.user.entity.SessionUser;
import com.system.constant.SystemConstant;

import jodd.util.StringUtil;

/**
 * Brand Controller
 *
 * @CreateDate: 2016-08-25 上午03:57:21
 * @version: V 1.0
 */
@Controller
public class BrandController extends BaseController {

    @Autowired
    private BrandService brandService;
   
    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/brand/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="brand/list";
        return new ModelAndView(returnView);
    }
    
    /**
     * queryListForPage
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/brand/queryListForPage")
    public void queryListForPage(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        //获取查询条件
        String name = request.getParameter("query_name");
        Brand b = new Brand();
        b.setName(name);
        
        Page page = getPageByRequest(request);
        b.setPage(page);
        List<Brand> list = brandService.queryListForPage(b);
        
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
    @RequestMapping(value = "/brand/add")
    public ModelAndView add(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="brand/addBrand";
        return new ModelAndView(returnView);
    }
    
    /**
     * index
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/brand/save")
    public void save(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
    	SessionUser su = getCurrentUser(request);
        Map<String, Object> resultMap = Maps.newHashMap();
        //获取品牌名称
        String name = request.getParameter("addName");
        //排序
        String sort = request.getParameter("addSort");
        if(StringUtils.isBlank(sort)){
        	sort = "99";
        }
        String logoPath="";
        try {
        	logoPath=getPath(request,"file",null);             //图片路径
        	System.out.println("图片路径："+logoPath);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
        Brand b = new Brand();
        b.setName(name);
        b.setSort(Integer.parseInt(sort));
        b.setCreateUserid(su.getUserId());
        b.setIsDel(0);
        b.setLogo(logoPath);
        brandService.save(b);
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
    @RequestMapping(value = "/brand/edit")
    public ModelAndView edit(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        //获取品牌id
        String id = request.getParameter("id");
        //查询品牌内容
        Brand b = brandService.queryById(id);
        
        request.setAttribute("brand", b);
        String returnView ="brand/editBrand";
        return new ModelAndView(returnView);
    }
    
    /**
     * update
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/brand/update")
    public void update(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
    	SessionUser su = getCurrentUser(request);
    	if (su.getType() != SystemConstant.USER_TYPE_SYSTEM ) {
			throw new SystemException("对不起，您无权操作该订单！");
		}
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
        String logoPath="";
        try {
        	logoPath=getPath(request,"file",null);             //图片路径
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
        Brand b = new Brand();
        b.setId(id);
        b.setName(name);
        b.setSort(Integer.parseInt(sort));
        b.setUpdateUserid(su.getUserId());
        if(StringUtil.isNotBlank(logoPath)){
        	b.setLogo(logoPath);
        }
        brandService.update(b);
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
    @RequestMapping(value = "/brand/delete")
    public void delete(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        Map<String, Object> resultMap = Maps.newHashMap();
        //获取品牌id
        String id = request.getParameter("id");
        SessionUser su = getCurrentUser(request);
        
        brandService.deleteById(id, su);
        
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
        resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
        renderJson(response, resultMap);
    }
    
    
    public static void main(String[] args) {
    	  String savePath="E:\\tomcat\\apache-tomcat-8.0.46\\images";
    	  File file = new File(savePath);
    	  if(!file.exists()){
    		  System.out.println("创建目录");
    	      file.mkdirs();
    	  }
	}
}
