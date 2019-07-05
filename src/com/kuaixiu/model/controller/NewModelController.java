package com.kuaixiu.model.controller;

import com.common.base.controller.BaseController;
import com.common.exception.SystemException;
import com.common.paginate.Page;
import com.google.common.collect.Maps;
import com.kuaixiu.brand.entity.NewBrand;
import com.kuaixiu.brand.service.NewBrandService;
import com.kuaixiu.model.entity.NewModel;
import com.kuaixiu.model.service.NewModelService;
import com.system.basic.user.entity.SessionUser;
import jodd.util.StringUtil;
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
* @author: anson
* @CreateDate: 2017年6月13日 下午5:13:35
* @version: V 1.0
* 
*/
@Controller
public class NewModelController extends BaseController {

	@Autowired
	private NewModelService newModelService;
	@Autowired
	private NewBrandService newBrandService;
	/**
	 * 列表查询
	 */
	 @RequestMapping(value = "/newModel/list")
	 public ModelAndView list(HttpServletRequest request,
	                              HttpServletResponse response) throws Exception {
		 List<NewBrand> brands=newBrandService.queryList(null);
		    request.setAttribute("brands", brands);
	        String returnView ="newModel/list";
	        return new ModelAndView(returnView);
	    }
	 
	 /**
	  * 刷新数据
	  */
	  @RequestMapping(value = "/newModel/queryListForPage")
	    public void queryListForPage(HttpServletRequest request,
	                              HttpServletResponse response) throws Exception {
	        //获取查询条件,品牌id
	        String brand = request.getParameter("query_brand");
	        //brand= new String (brand.getBytes("iso8859-1"),"UTF-8"); 
	        Page page = getPageByRequest(request);
	        NewModel m = new NewModel();
	        m.setBrandId(brand);
	        m.setPage(page);
	        m.setOrderBy(" t.sort asc ");
	        //排序按sort值递增排序
	        List<NewModel> list = newModelService.queryListForPage(m);
	        for(NewModel model:list){
	        	String color=model.getColor();
	 	        model.setColor(color.substring(1));
	        }
	        page.setData(list);
	        this.renderJson(response, page);
	    } 
	  
	  
	  /**
	     * add
	     */
	    @RequestMapping(value ="/newModel/add")
	    public ModelAndView add(HttpServletRequest request,
	                              HttpServletResponse response) throws Exception {
	    	List<NewBrand> brands =newBrandService.queryList(null);
	    	//默认显示苹果所有兑换机型
	    	
	    	request.setAttribute("brands", brands);
	        String returnView ="newModel/addModel";
	        return new ModelAndView(returnView);
	    }
	    
	    /**
	     * 新增机型
	     */
	    @RequestMapping(value ="/newModel/save")
	    public void save(HttpServletRequest request,
	                              HttpServletResponse response) throws Exception {
	        
	        Map<String, Object> resultMap = Maps.newHashMap();
	        
	        //品牌id
	        String brandId = request.getParameter("addBrandId");
	        //根据id查询品牌名称
	        NewBrand  brand=(NewBrand)newBrandService.queryById(brandId);
	        if(brand==null){
	        	 throw new SystemException("该品牌不存在");
	        }
	        String brandName=brand.getName();
	        //获取机型名称
	        String name = request.getParameter("addName");
	        //机型颜色
	        String color = request.getParameter("addColorValue");
	        
	        //内存
	        String memory=request.getParameter("addMemory");
	        //价格
	        String price=request.getParameter("addPrice");
	        //网络类型
	        String edition=request.getParameter("edition");
	        
	        //序号
	        String sortStr= request.getParameter("addSort");
	        Integer realSort=null;//最终排序
	       //如果排序值为空，默认在该品牌下排序递增
	        if(StringUtil.isBlank(sortStr)){
	        	//得到当前品牌最大排序数
	        	List<NewModel> modelList=newModelService.queryMaxSort(brandId);
	        	if(!modelList.isEmpty()){
	        		realSort=modelList.get(0).getSort()+1;
	        	}else{
	        		//表中当前没有该品牌机型，为其设值为1
	        		realSort=1;
	        	}
	        }else{
	        	realSort=Integer.parseInt(sortStr);
	        }
	        SessionUser su = getCurrentUser(request);
	        NewModel m = new NewModel();
	        m.setName(name);
	        m.setBrandId(brandId);
	        m.setBrandName(brandName);
	        m.setColor(color);
	        m.setCreateUserid(su.getUserId());
	        m.setEdition(edition);
	        m.setSort(realSort);
	        m.setIsDel(0);
	        m.setMemory(Integer.parseInt(memory));
	        m.setPrice(new BigDecimal(price));
	        
	        newModelService.save(m, su);
	        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
	        resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
	        renderJson(response, resultMap);
	    }
	    
	  
	    /**
	     * 编辑页面
	     */
	    @RequestMapping(value ="/newModel/edit")
	    public ModelAndView edit(HttpServletRequest request,
	                              HttpServletResponse response) throws Exception {
	        //获取项目id
	        String id = request.getParameter("id");
	        //查询项目内容
	        NewModel model = newModelService.queryById(id);
	        List<NewBrand> brands = newBrandService.queryList(null);
	    	request.setAttribute("brands", brands);
	        request.setAttribute("model", model);
	        String returnView ="newModel/editModel";
	        return new ModelAndView(returnView);
	    }
	    
	    
	    /**
	     * 编辑信息保存更新
	     */
	    @RequestMapping(value ="/newModel/update")
	    public void update(HttpServletRequest request,
	                              HttpServletResponse response) throws Exception {
	        
	        Map<String, Object> resultMap = Maps.newHashMap();
	        //获取项目id
	        String id = request.getParameter("id");
	        //获取项目名称
	        String name = request.getParameter("upName");
	        //机型颜色
	        String color = request.getParameter("upColorValue");
	        //序号
	        String sortStr= request.getParameter("upSort");
	        //价格
	        String price=request.getParameter("upPrice");
	        //是否上架  1表示是  2表示否
	        String isPutaway=request.getParameter("addPutaway");
	        
	        SessionUser su = getCurrentUser(request);
	        
	        NewModel m = new NewModel();
	        m.setId(id);
	        m.setName(name);
	        m.setColor(color);
	        m.setSort(Integer.parseInt(sortStr));
	        m.setUpdateUserid(su.getUserId());
	        m.setPrice(new BigDecimal(price));
	        m.setIsPutaway(Integer.parseInt(isPutaway));
	        newModelService.update(m,su);
	        
	        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
	        resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
	        renderJson(response, resultMap);
	    }
	    
	    
	    /**
	     * delete
	     * 删除机型
	     */
	    @RequestMapping(value = "/newModel/delete")
	    public void delete(HttpServletRequest request,
	                              HttpServletResponse response) throws Exception {
	        
	        Map<String, Object> resultMap = Maps.newHashMap();
	        //获取项目id
	        String id = request.getParameter("id");
	        SessionUser su = getCurrentUser(request);
	        
	        newModelService.deleteById(id, su);
	        
	        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
	        resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
	        renderJson(response, resultMap);
	    }
	  
}
