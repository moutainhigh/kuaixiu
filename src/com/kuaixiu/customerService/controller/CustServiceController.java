package com.kuaixiu.customerService.controller;

import com.common.base.controller.BaseController;
import com.common.exception.SystemException;
import com.common.paginate.Page;
import com.google.common.collect.Maps;
import com.kuaixiu.customerService.entity.CustService;
import com.kuaixiu.customerService.service.CustServiceService;
import com.kuaixiu.provider.entity.Provider;
import com.kuaixiu.provider.service.ProviderService;
import com.kuaixiu.shop.entity.Shop;
import com.kuaixiu.shop.service.ShopService;
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
 * CustService Controller
 *
 * @CreateDate: 2016-12-18 下午10:13:47
 * @version: V 1.0
 */
@Controller
public class CustServiceController extends BaseController {

    @Autowired
    private CustServiceService custServiceService;
    @Autowired
    private ProviderService providerService;
    @Autowired
    private ShopService shopService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/custService/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="custService/list";
        return new ModelAndView(returnView);
    }
    

    /**
     * queryListForPage
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/custService/queryListForPage")
    public void queryListForPage(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        //获取查询条件
        String name = request.getParameter("query_name");
        String number = request.getParameter("query_number");
        String mobile = request.getParameter("query_mobile");
        CustService c = new CustService();
        c.setName(name);
        c.setNumber(number);
        c.setMobile(mobile);
        
        //获取登录用户
        SessionUser su = getCurrentUser(request);
        //判断用户类型系统管理员可以查看所有工程师
        if (su.getType() == SystemConstant.USER_TYPE_SYSTEM) {
            
        }
        else if (su.getType() == SystemConstant.USER_TYPE_PROVIDER) {
            //连锁商只能查看自己的工程师
            c.setProviderCode(su.getProviderCode());
        }
        else if (su.getType() == SystemConstant.USER_TYPE_SHOP) {
            //门店商只能查看自己的工程师
            c.setShopCode(su.getShopCode());
        }
        else {
            throw new SystemException("对不起，您无权查看此信息！");
        }
        //获取隐藏域搜索条件
        String shopCode = request.getParameter("hide_shopCode");
        if(StringUtils.isNotBlank(shopCode)){
            c.setShopCode(shopCode);
        }
        
        Page page = getPageByRequest(request);
        c.setPage(page);
        c.setIsDel(0);
        List<CustService> list = custServiceService.queryListForPage(c);
        
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
    @RequestMapping(value = "/custService/add")
    public ModelAndView add(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
      //获取登录用户
        SessionUser su = getCurrentUser(request);
        //判断用户类型系统管理员可以查看所有工程师
        if (su.getType() == SystemConstant.USER_TYPE_SYSTEM) {
            //获取连锁商
            List<Provider> providerL = providerService.queryList(null);
            request.setAttribute("providerL", providerL);
        }
        else if (su.getType() == SystemConstant.USER_TYPE_PROVIDER) {
            //获取维修门店
            List<Shop> shopL = shopService.queryByProviderCode(su.getProviderCode());
            request.setAttribute("shopL", shopL);
        }
        
        String returnView ="custService/addCustService";
        return new ModelAndView(returnView);
    }
    
    /**
     * index
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/custService/save")
    public void save(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        Map<String, Object> resultMap = Maps.newHashMap();
        //获取名称
        String name = request.getParameter("addName");
        String gender = request.getParameter("addGender");
        String mobile = request.getParameter("addMobile");
        String providerCode = request.getParameter("addProviderCode");
        String shopCode = request.getParameter("addShopCode");
            
        SessionUser su = getCurrentUser(request);
        
        if (su.getType() == SystemConstant.USER_TYPE_PROVIDER) {
            //如果是连锁商
            providerCode = su.getProviderCode();
        }
        else if (su.getType() == SystemConstant.USER_TYPE_SHOP) {
            //如果是门店商
            providerCode = su.getProviderCode();
            shopCode = su.getShopCode();
        }
        
        CustService c = new CustService();
        c.setProviderCode(providerCode);
        c.setShopCode(shopCode);
        c.setName(name);
        if("1".equals(gender)){
            c.setGender("男");
        }
        else{
            c.setGender("女");
        }
        c.setMobile(mobile);
        c.setIsDel(0);
        c.setCreateUserid(su.getUserId());

        custServiceService.save(c, su);
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
        resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
        renderJson(response, resultMap);
    }
    
    /**
     * detail
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/custService/detail")
    public ModelAndView detail(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        //获取工程师id
        String id = request.getParameter("id");
        //查询工程师内容
        CustService c = custServiceService.queryById(id);

        request.setAttribute("cust", c);

        String returnView ="custService/detail";
        return new ModelAndView(returnView);
    }
    
    /**
     * edit
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/custService/edit")
    public ModelAndView edit(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        //获取工程师id
        String id = request.getParameter("id");
        //查询工程师内容
        CustService c = custServiceService.queryById(id);
        //获取连锁商
        List<Provider> providerL = providerService.queryList(null);
        //获取维修门店
        List<Shop> shopL = shopService.queryByProviderCode(c.getProviderCode());
        

        request.setAttribute("cust", c);
        request.setAttribute("providerL", providerL);
        request.setAttribute("shopL", shopL);
        String returnView ="custService/editCustService";
        return new ModelAndView(returnView);
    }
    
    /**
     * update
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/custService/update")
    public void update(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        Map<String, Object> resultMap = Maps.newHashMap();
        SessionUser su = getCurrentUser(request);
        
        //获取工程师id
        String id = request.getParameter("id");
        
        //获取名称
        String name = request.getParameter("addName");
        String gender = request.getParameter("addGender");
        String mobile = request.getParameter("addMobile");
        String providerCode = request.getParameter("addProviderCode");
        String shopCode = request.getParameter("addShopCode");


        if (su.getType() == SystemConstant.USER_TYPE_PROVIDER) {
            //如果是连锁商
            providerCode = su.getProviderCode();
        }
        else if (su.getType() == SystemConstant.USER_TYPE_SHOP) {
            //如果是门店商
            providerCode = su.getProviderCode();
            shopCode = su.getShopCode();
        }
        
        CustService c = new CustService();
        c.setId(id);
        c.setProviderCode(providerCode);
        c.setShopCode(shopCode);
        c.setName(name);
        if("1".equals(gender)){
            c.setGender("男");
        }
        else{
            c.setGender("女");
        }
        c.setMobile(mobile);
        c.setIsDel(0);
        c.setUpdateUserid(su.getUserId());
        custServiceService.update(c);
        
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
    @RequestMapping(value = "/custService/delete")
    public void delete(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        Map<String, Object> resultMap = Maps.newHashMap();
        //获取工程师id
        String id = request.getParameter("id");
        SessionUser su = getCurrentUser(request);
        
        custServiceService.deleteById(id, su);
        
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
        resultMap.put(RESULTMAP_KEY_MSG, "删除成功");
        renderJson(response, resultMap);
    }
}
