package com.kuaixiu.shop.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.common.base.controller.BaseController;
import com.common.exception.SystemException;
import com.common.importExcel.ImportReport;
import com.common.paginate.Page;
import com.google.common.collect.Maps;
import com.kuaixiu.brand.entity.Brand;
import com.kuaixiu.brand.entity.NewBrand;
import com.kuaixiu.brand.service.BrandService;
import com.kuaixiu.brand.service.NewBrandService;
import com.kuaixiu.engineer.entity.Engineer;
import com.kuaixiu.engineer.service.NewEngineerService;
import com.kuaixiu.provider.entity.Provider;
import com.kuaixiu.provider.service.ProviderService;
import com.kuaixiu.shop.entity.NewShopModel;
import com.kuaixiu.shop.entity.Shop;
import com.kuaixiu.shop.entity.ShopModel;
import com.kuaixiu.shop.service.NewShopModelService;
import com.kuaixiu.shop.service.ShopModelService;
import com.kuaixiu.shop.service.ShopService;
import com.system.basic.address.entity.Address;
import com.system.basic.address.service.AddressService;
import com.system.constant.SystemConstant;

import jodd.util.StringUtil;

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

/**
 * Shop Controller
 *
 * @CreateDate: 2016-08-26 上午02:22:49
 * @version: V 1.0
 */
@Controller
public class ShopController extends BaseController {

    @Autowired
    private ShopService shopService;
    @Autowired
    private ProviderService providerService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private NewEngineerService newEngineerService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private ShopModelService shopModelService;
    @Autowired
    private NewBrandService newBrandService;
    @Autowired
    private NewShopModelService newShopModelService;
    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/shop/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        //String code = SeqUtil.getNext("PRO");
    	 //获取省份地址
        List<Address> provinceL = addressService.queryByPid("0");
        request.setAttribute("provinceL", provinceL);
        String returnView ="shop/list";
        return new ModelAndView(returnView);
    }
    
    /**
     * queryListForPage
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/shop/queryListForPage")
    public void queryListForPage(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        Page page = getPageByRequest(request);
        try {
            //获取查询条件
            String name = request.getParameter("query_name");
            String oldToNew = request.getParameter("query_oldtonew");          //是否支持以旧换新
            String isSendRepair = request.getParameter("query_isSendRepair");  //是否支持寄修
            String province = request.getParameter("queryProvince");
            String city = request.getParameter("queryCity");
            String county = request.getParameter("queryCounty");
            Shop p = new Shop();
            p.setName(name);
            p.setProvince(province);
            p.setCity(city);
            p.setCounty(county);
            if (!StringUtil.isBlank(isSendRepair)) {
                p.setIsSendRepair(Integer.parseInt(isSendRepair));
            }
            if (!StringUtil.isBlank(oldToNew)) {
                p.setOldToNew(Integer.parseInt(oldToNew));
            }
            //获取登录用户
            SessionUser su = getCurrentUser(request);
            //判断用户类型系统管理员可以查看所有门店商
            if (su.getType() == SystemConstant.USER_TYPE_SYSTEM
                    || su.getType() == SystemConstant.USER_TYPE_CUSTOMER_SERVICE) {

            } else if (su.getType() == SystemConstant.USER_TYPE_PROVIDER) {
                //连锁商只能查看自己的门店商
                p.setProviderCode(su.getProviderCode());
            } else {
                throw new SystemException("对不起，您无权查看此信息！");
            }
            //获取隐藏域搜索条件
            String providerCode = request.getParameter("hide_providerCode");
            if (StringUtils.isNotBlank(providerCode)) {
                p.setProviderCode(providerCode);
            }
            //如果是指定条件查询则从0页开始查
            p.setPage(page);
            List<Shop> list = shopService.queryListForPage(p);
            page.setData(list);
        }catch (Exception e){
            e.printStackTrace();
        }
        this.renderJson(response, page);
    }
    
    /**
     * queryListForPage
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/shop/queryByProCode")
    public void queryByProCode(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        Map<String, Object> resultMap = Maps.newHashMap();
        //获取查询条件
        String proCode = request.getParameter("proCode");
        List<Shop> list = shopService.queryByProviderCode(proCode);
        
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
    @RequestMapping(value = "/shop/add")
    public ModelAndView add(HttpServletRequest request,
                              HttpServletResponse response) {
        try {
            //获取省份地址
            List<Address> provinceL = addressService.queryByPid("0");
            request.setAttribute("provinceL", provinceL);
            //获取登录用户h
            SessionUser su = getCurrentUser(request);
            //判断用户类型系统管理员可以查看所有工程师
            if (su.getType() == SystemConstant.USER_TYPE_SYSTEM) {
                //获取连锁商
                List<Provider> providerL = providerService.queryList(null);
                request.setAttribute("providerL", providerL);
            }

            Brand b = new Brand();
            NewBrand c = new NewBrand();
            b.setIsDel(0);
            List<Brand> brandL = brandService.queryList(b);
            //新增门店兑换机型功能
            List<NewBrand> newBrand = newBrandService.queryList(c);
            request.setAttribute("newBrand", newBrand);
            request.setAttribute("brandL", brandL);
        }catch (Exception e){
            e.printStackTrace();
        }
        String returnView ="shop/addShop";
        return new ModelAndView(returnView);
    }
    
    /**
     * index
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/shop/save")
    public void save(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        Map<String, Object> resultMap = Maps.newHashMap();
        try {
            //获取名称
            String name = request.getParameter("addName");
            String managerName = request.getParameter("addManagerName");
            String managerMobile = request.getParameter("addManagerMobile");
            String managerMobile1 = request.getParameter("addManagerMobile1");
            String managerMobile2 = request.getParameter("addManagerMobile2");
            String tel = request.getParameter("addTel");
            String areas = request.getParameter("addAreas");
            String province = request.getParameter("addProvince");
            String city = request.getParameter("addCity");
            String county = request.getParameter("addCounty");
            String street = request.getParameter("addStreet");
            String address = request.getParameter("addAddress");
            String providerCode = request.getParameter("addProviderCode");
            String[] brands = request.getParameterValues("addBrand");
            //是否支持以旧换新功能
            String oldToNew = request.getParameter("oldToNew");

            //支持兑换品牌
            //   String[] newBrands=request.getParameterValues("addNewBrand");
            SessionUser su = getCurrentUser(request);
            //如果是连锁商
            if (su.getType() == SystemConstant.USER_TYPE_PROVIDER) {
                providerCode = su.getProviderCode();
            }

            Shop p = new Shop();
            p.setProviderCode(providerCode);
            p.setName(name);
            p.setManagerName(managerName);
            p.setManagerMobile(managerMobile);
            p.setManagerMobile1(managerMobile1);
            p.setManagerMobile2(managerMobile2);
            p.setTel(tel);
            p.setProvince(province);
            p.setCity(city);
            p.setCounty(county);
            p.setStreet(street);
            p.setAreas(areas);
            p.setAddress(address);
            p.setCreateUserid(su.getUserId());
            p.setBrands(brands);
            p.setIsDel(0);
            p.setSort(0);
            p.setStatus(0);
            p.setOldToNew(Integer.parseInt(oldToNew));
            //   p.setNewBrands(newBrands);
            shopService.save(p, su);
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
            resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
        }catch (Exception e){
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
            e.printStackTrace();
        }
        renderJson(response, resultMap);
    }



    /**
     * detail
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/shop/detail")
    public ModelAndView detail(HttpServletRequest request,
                              HttpServletResponse response){
        try {
            //获取网点id
            String id = request.getParameter("id");
            //获取网点id
            String code = request.getParameter("code");
            //查询网点信息
            Shop p = shopService.queryById(id);
            if (p == null) {
                p = shopService.queryByCode(code);
            }
            Engineer engineer = new Engineer();
            engineer.setShopCode(p.getCode());
            //查询工程师个数
            int engNum = newEngineerService.getDao().queryCount(engineer);

            ShopModel sm = new ShopModel();
            sm.setShopCode(p.getCode());
            List<ShopModel> shopModels = shopModelService.queryList(sm);
            request.setAttribute("shopModels", shopModels);

            request.setAttribute("shop", p);
            request.setAttribute("engNum", engNum);
        }catch (Exception e){
            e.printStackTrace();
        }
        String returnView ="shop/detail";
        return new ModelAndView(returnView);
    }
    
    /**
     * edit
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/shop/edit")
    public ModelAndView edit(HttpServletRequest request,
                              HttpServletResponse response) {
        try {
            //获取登录用户
            SessionUser su = getCurrentUser(request);
            //判断用户类型系统管理员可以查看所有工程师
            if (su.getType() == SystemConstant.USER_TYPE_SYSTEM) {
                //获取连锁商
                List<Provider> providerL = providerService.queryList(null);
                request.setAttribute("providerL", providerL);
            }

            //获取网点id
            String id = request.getParameter("id");
            //查询网点信息
            Shop p = shopService.queryById(id);

            //获取省份地址
            List<Address> provinceL = addressService.queryByPid("0");
            //获取市地址
            List<Address> cityL = addressService.queryByPid(p.getProvince());
            //获取区县地址
            List<Address> countyL = addressService.queryByPid(p.getCity());
            //获取街道地址
            List<Address> streetL = addressService.queryByPid(p.getCounty());

            Brand b = new Brand();
            b.setIsDel(0);
            List<Brand> brandL = brandService.queryList(b);

            ShopModel sm = new ShopModel();
            sm.setShopCode(p.getCode());
            List<ShopModel> shopModels = shopModelService.queryList(sm);

            //新增门店兑换机型功能
            NewBrand nb = new NewBrand();
            nb.setIsDel(0);
            List<NewBrand> newBrand = newBrandService.queryList(nb);
            request.setAttribute("newBrand", newBrand);
            //新增兑换品牌
            NewShopModel nsm = new NewShopModel();
            nsm.setShopCode(p.getCode());
            List<NewShopModel> newShopModels = newShopModelService.queryList(nsm);
            request.setAttribute("newShopModels", newShopModels);

            request.setAttribute("brandL", brandL);
            request.setAttribute("shopModels", shopModels);

            request.setAttribute("shop", p);
            request.setAttribute("provinceL", provinceL);
            request.setAttribute("cityL", cityL);
            request.setAttribute("countyL", countyL);
            request.setAttribute("streetL", streetL);
        }catch (Exception e){
            e.printStackTrace();
        }
        String returnView ="shop/editShop";
        return new ModelAndView(returnView);
    }
    
    /**
     * update
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/shop/update")
    public void update(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        Map<String, Object> resultMap = Maps.newHashMap();
        try {
            SessionUser su = getCurrentUser(request);

            //获取网点id
            String id = request.getParameter("id");

            //获取名称
            String name = request.getParameter("addName");
            String managerName = request.getParameter("addManagerName");
            String managerMobile = request.getParameter("addManagerMobile");
            String managerMobile1 = request.getParameter("addManagerMobile1");
            String managerMobile2 = request.getParameter("addManagerMobile2");
            String tel = request.getParameter("addTel");
            String areas = request.getParameter("addAreas");
            String province = request.getParameter("addProvince");
            String city = request.getParameter("addCity");
            String county = request.getParameter("addCounty");
            String street = request.getParameter("addStreet");
            String address = request.getParameter("addAddress");
            String providerCode = request.getParameter("addProviderCode");
            String[] brands = request.getParameterValues("addBrand");
            //是否支持以旧换新
            String oldToNew = request.getParameter("oldToNew");
            //是否支持寄修
            String isSendRepair = request.getParameter("isSendRepair");
            //支持兑换品牌
            //String[] newBrands=request.getParameterValues("addNewBrand");
            //如果是连锁商
            if (su.getType() == SystemConstant.USER_TYPE_PROVIDER) {
                providerCode = su.getProviderCode();
            }

            Shop p = new Shop();
            p.setId(id);
            p.setProviderCode(providerCode);
            p.setName(name);
            p.setManagerName(managerName);
            p.setManagerMobile(managerMobile);
            p.setManagerMobile1(managerMobile1);
            p.setManagerMobile2(managerMobile2);
            p.setTel(tel);
            p.setProvince(province);
            p.setCity(city);
            p.setCounty(county);
            p.setStreet(street);
            p.setAreas(areas);
            p.setAddress(address);
            p.setIsDel(0);
            p.setBrands(brands);
            if (!StringUtil.isBlank(oldToNew)) {
                p.setOldToNew(Integer.parseInt(oldToNew));
            }
            if (!StringUtil.isBlank(isSendRepair)) {
                p.setIsSendRepair(Integer.parseInt(isSendRepair));
            }
            boolean tip = true;

            Shop shopl = shopService.queryById(p.getId());
            //是否支持寄修只有管理员支持编辑
            if (shopl.getIsSendRepair() != p.getIsSendRepair()) {
                if (su.getType() != SystemConstant.USER_TYPE_SYSTEM && su.getType() != SystemConstant.USER_TYPE_SUPPER) {
                    resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
                    resultMap.put(RESULTMAP_KEY_MSG, "对不起您没有权限");
                    renderJson(response, resultMap);
                    return;
                }
            }
            //每个城市目前只允许一家门店支持寄修功能
            Shop shop = new Shop();
            shop.setProvince(shopl.getProvince());
            shop.setCity(shopl.getCity());
            shop.setIsSendRepair(1);
            List<Shop> list = shopService.queryList(shop);

            if (list.size() > 0 && p.getIsSendRepair() == 1) {
                if (!list.get(0).getCode().equals(shopl.getCode())) {
                    tip = false;
                    resultMap.put("already", RESULTMAP_SUCCESS_TRUE);
                    resultMap.put("shopId", id);
                    resultMap.put(RESULTMAP_KEY_MSG, "该城市已存在寄修门店，是否替换");
                }
            }

            if (tip) {
                //p.setNewBrands(newBrands);
                shopService.update(p, su);
                resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
                resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
            }
        }catch (Exception e){
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
            resultMap.put(RESULTMAP_KEY_MSG, "保存失败");
            e.printStackTrace();
        }
        renderJson(response, resultMap);
    }
    
    
    /**
     * 编辑替换支持寄修的门店
     */
    @RequestMapping(value = "/webpc/activity/changeSendShop")
    public void changeSendShop(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        Map<String, Object> resultMap = Maps.newHashMap();
        SessionUser su = getCurrentUser(request);
        String id=request.getParameter("id");
        //只有管理员可以编辑寄修门店
        if(su.getType() != SystemConstant.USER_TYPE_SYSTEM&&su.getType() != SystemConstant.USER_TYPE_SUPPER){
        	resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
            resultMap.put(RESULTMAP_KEY_MSG, "对不起您没有权限");
          }else{
        	  Shop shopl=shopService.queryById(id);
        	  if(shopl!=null){
        		    Shop shop=new Shop();
        	        shop.setProvince(shopl.getProvince());
        	        shop.setCity(shopl.getCity());
        	        shop.setIsSendRepair(1);
        	        List<Shop> list=shopService.queryList(shop);
        	        if(list.size()>0){
        	        	Shop s=list.get(0);
        	        	s.setIsSendRepair(0);
        	        	 //将原寄修门店该为不支持
        	        	shopService.saveUpdate(s);
        	        	 //将编辑的门店修改为支持
        	        	shopl.setIsSendRepair(1);
        	        	shopService.saveUpdate(shopl);
        	        }
        	  }
        	  resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
              resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
          }
        renderJson(response, resultMap);
    }
    
    
    
    /**
     * update
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/shop/setDispatchType")
    public void setDispatchType(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        Map<String, Object> resultMap = Maps.newHashMap();
        SessionUser su = getCurrentUser(request);
        
        //获取网点id
        String id = request.getParameter("id");
        String dispatchType = request.getParameter("dispatchType");
        if(StringUtils.isNumeric(dispatchType)){
            shopService.updateDispatchType(id, Integer.parseInt(dispatchType));
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
            resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
        }
        else{
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
            resultMap.put(RESULTMAP_KEY_MSG, "派单类型错误");
        }
        
        renderJson(response, resultMap);
    }
    
    /**
     * delete
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/shop/delete")
    public void delete(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        Map<String, Object> resultMap = Maps.newHashMap();
        //获取网点id
        String id = request.getParameter("id");
        SessionUser su = getCurrentUser(request);
        
        shopService.deleteById(id, su);
        
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
    @RequestMapping(value = "/shop/importIndex")
    public ModelAndView importIndex(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="shop/importIndex";
        return new ModelAndView(returnView);
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
    @RequestMapping(value = "/shop/import")
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
                    shopService.importExcel(myfile,report,getCurrentUser(request));
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
    
    
    
}
