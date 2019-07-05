package com.kuaixiu.model.controller;

import com.common.base.controller.BaseController;
import com.common.importExcel.ImportReport;
import com.common.paginate.Page;
import com.google.common.collect.Maps;
import com.kuaixiu.brand.entity.Brand;
import com.kuaixiu.brand.service.BrandService;
import com.kuaixiu.model.entity.Model;
import com.kuaixiu.model.entity.RepairCost;
import com.kuaixiu.model.service.ModelService;
import com.kuaixiu.model.service.RepairCostService;
import com.kuaixiu.project.entity.Project;
import com.kuaixiu.project.service.ProjectService;
import com.system.basic.user.entity.SessionUser;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Model Controller
 *
 * @CreateDate: 2016-09-03 上午12:26:35
 * @version: V 1.0
 */
@Controller
public class ModelController extends BaseController {

    @Autowired
    private ModelService modelService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private RepairCostService repairCostService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/model/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        String returnView ="model/list";
        return new ModelAndView(returnView);
    }
    

    /**
     * queryListForPage
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/model/queryListForPage")
    public void queryListForPage(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        //获取查询条件
        String name = request.getParameter("query_name");
        Model m = new Model();
        m.setName(name);
        
        Page page = getPageByRequest(request);
        m.setPage(page);
        List<Model> list = modelService.queryListForPage(m);
        if(list != null && list.size() > 0){
            for (Model model : list) {
                model.setRepairCosts(repairCostService.queryListByModelId(model.getId()));
            }
        }
        
        page.setData(list);
        this.renderJson(response, page);
    }
    
    /**
     * queryByBrandId
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/model/queryByBrandId")
    public void queryByBrandId(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        Map<String, Object> resultMap = Maps.newHashMap();
        //获取项目id
        String brandId = request.getParameter("brandId");
        List<Model> list = modelService.queryByBrandId(brandId);
        
        resultMap.put(RESULTMAP_KEY_DATA, list);
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
        resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
        renderJson(response, resultMap);
    }
    
    /**
     * add
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/model/add")
    public ModelAndView add(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
    	List<Brand> brands = brandService.queryList(null);
    	request.setAttribute("brands", brands);
        //跳转页面时获取维修项目，用于新增机型时显示
        List<Project> projects = projectService.queryList(null);
        request.setAttribute("projects", projects);
        String returnView ="model/addModel";
        return new ModelAndView(returnView);
    }
    
    /**
     * index
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/model/save")
    public void save(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        Map<String, Object> resultMap = Maps.newHashMap();
        //获取项目名称
        String name = request.getParameter("addName");
        //品牌id
        String brandId = request.getParameter("addBrandId");
        //根据id查询品牌名称
        Brand brand=brandService.queryById(brandId);
        String brandName=brand.getName();
        //机型颜色
        String color = request.getParameter("addColorValue");
        //序号
        String sortStr= request.getParameter("addSort");
        if(StringUtils.isBlank(sortStr)){
        	sortStr = "99";
        }
        //维修费用
        String repairCosts = request.getParameter("addRepairCostValue");
        String logoPath="";
        try {
        	logoPath=getPath(request,"file",null,null);             //图片路径
        	System.out.println("图片路径："+logoPath);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}    
        SessionUser su = getCurrentUser(request);
        Model m = new Model();
        m.setName(name);
        m.setBrandId(brandId);
        m.setBrandName(brandName);
        m.setColor(color);
        m.setCreateUserid(su.getUserId());
        m.setSort(Integer.parseInt(sortStr));
        m.setIsDel(0);
        m.setLogo(logoPath);
        modelService.save(m, repairCosts, su);
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
    @RequestMapping(value = "/model/edit")
    public ModelAndView edit(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        //获取项目id
        String id = request.getParameter("id");
        //查询项目内容
        Model model = modelService.queryById(id);
        
        List<Brand> brands = brandService.queryList(null);
    	request.setAttribute("brands", brands);
        //查询维修项目
        List<Project> projects = projectService.queryList(null);
        //维修费用
        List<RepairCost> repairCosts = repairCostService.queryListByModelId(id);

        request.setAttribute("model", model);
        request.setAttribute("projects", projects);
        request.setAttribute("repairCosts", repairCosts);
        String returnView ="model/editModel";
        return new ModelAndView(returnView);
    }
    
    /**
     * update
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/model/update")
    public void update(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
    	SessionUser su = getCurrentUser(request);
        Map<String, Object> resultMap = Maps.newHashMap();
        //获取项目id
        String id = request.getParameter("id");
        //获取项目名称
        String name = request.getParameter("upName");
        //品牌id
        String brandId = request.getParameter("upBrandId");
        //根据id查询品牌名称
        Brand brand=brandService.queryById(brandId);
        String brandName=brand.getName();
        //机型颜色
        String color = request.getParameter("upColorValue");
        //序号
        String sortStr= request.getParameter("upSort");
        if(StringUtils.isBlank(sortStr)){
        	sortStr = "99";
        }
        //维修费用
        String repairCosts = request.getParameter("upRepairCostValue");
        String logoPath="";
        try {
        	logoPath=getPath(request,"file",null,null);             //图片路径
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}    
        
        Model m = new Model();
        m.setId(id);
        m.setName(name);
        m.setBrandId(brandId);
        m.setBrandName(brandName);
        m.setColor(color);
        m.setSort(Integer.parseInt(sortStr));
        m.setUpdateUserid(su.getUserId());
        m.setLogo(logoPath);
        modelService.update(m, repairCosts, su);
        
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
    @RequestMapping(value = "/model/delete")
    public void delete(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        Map<String, Object> resultMap = Maps.newHashMap();
        //获取项目id
        String id = request.getParameter("id");
        SessionUser su = getCurrentUser(request);
        
        modelService.deleteById(id, su);
        
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
        resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
        renderJson(response, resultMap);
    }
    
    /**
     * importIndex
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/model/importIndex")
    public ModelAndView importIndex(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="model/importIndex";
        return new ModelAndView(returnView);
    }
    
    /**
     * downloadTpl
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/model/downloadTpl")
    public void downloadTpl(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        Map<String, Object> resultMap = Maps.newHashMap();
        //获取项目id
        String id = request.getParameter("id");
        //获取项目名称
        String name = request.getParameter("upName");
        //品牌id
        String brandId = request.getParameter("upBrandId");
        //查询品牌名称
        String brandName = "iPhone";
        //机型颜色
        String color = request.getParameter("upColorValue");
        //维修费用
        String repairCosts = request.getParameter("upRepairCostValue");
            
        SessionUser su = getCurrentUser(request);
        
        Model m = new Model();
        m.setId(id);
        m.setName(name);
        m.setBrandId(brandId);
        m.setBrandName(brandName);
        m.setColor(color);
        m.setUpdateUserid(su.getUserId());
        modelService.update(m, repairCosts, su);
        
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
        resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
        renderJson(response, resultMap);
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
    @RequestMapping(value = "/model/import")
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
                String extension=FilenameUtils.getExtension(fileName);
                if (!extension.equalsIgnoreCase("xls")){
                    errorMsg.append("导入文件格式错误！只能导入excel文件！");
                }
                else{
                    modelService.importExcel(myfile,report,getCurrentUser(request));
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
     * export
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/model/export")
    public void export(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        Map<String, Object> resultMap = Maps.newHashMap();
        //获取项目id
        String id = request.getParameter("id");
        //获取项目名称
        String name = request.getParameter("upName");
        //品牌id
        String brandId = request.getParameter("upBrandId");
        //查询品牌名称
        String brandName = "iPhone";
        //机型颜色
        String color = request.getParameter("upColorValue");
        //维修费用
        String repairCosts = request.getParameter("upRepairCostValue");
            
        SessionUser su = getCurrentUser(request);
        
        Model m = new Model();
        m.setId(id);
        m.setName(name);
        m.setBrandId(brandId);
        m.setBrandName(brandName);
        m.setColor(color);
        m.setUpdateUserid(su.getUserId());
        modelService.update(m, repairCosts, su);
        
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
        resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
        renderJson(response, resultMap);
    }
}
