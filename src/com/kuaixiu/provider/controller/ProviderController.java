package com.kuaixiu.provider.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.exception.SystemException;
import com.common.importExcel.ImportReport;
import com.common.paginate.Page;
import com.common.util.SmsSendUtil;
import com.common.util.ValidatorUtil;
import com.google.common.collect.Maps;
import com.kuaixiu.provider.entity.Provider;
import com.kuaixiu.provider.service.ProviderService;
import com.kuaixiu.shop.entity.Shop;
import com.kuaixiu.shop.service.ShopService;
import com.system.api.entity.ResultData;
import com.system.basic.address.entity.Address;
import com.system.basic.address.service.AddressService;
import com.system.basic.user.entity.SessionUser;
import com.system.basic.user.service.SessionUserService;
import com.system.constant.ApiResultConstant;

import jodd.util.StringUtil;

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
 * Provider Controller
 *
 * @CreateDate: 2016-08-26 上午12:45:50
 * @version: V 1.0
 */
@Controller
public class ProviderController extends BaseController {

    @Autowired
    private ProviderService providerService;
    @Autowired
    private AddressService addressService;
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
    @RequestMapping(value = "/provider/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        //String code = SeqUtil.getNext("PRO");
        String returnView ="provider/list";
        return new ModelAndView(returnView);
    }
    
    /**
     * queryListForPage
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/provider/queryListForPage")
    public void queryListForPage(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        //获取查询条件
        String name = request.getParameter("query_name");
        String code = request.getParameter("query_code");
        String mobile = request.getParameter("query_mobile");
        Provider p = new Provider();
        p.setName(name);
        p.setCode(code);
        p.setManagerMobile(mobile);
        
        Page page = getPageByRequest(request);
        p.setPage(page);
        List<Provider> list = providerService.queryListForPage(p);
        
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
    @RequestMapping(value = "/provider/add")
    public ModelAndView add(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        //获取省份地址
        List<Address> provinceL = addressService.queryByPid("0");
        
        request.setAttribute("provinceL", provinceL);
        String returnView ="provider/addProvider";
        return new ModelAndView(returnView);
    }
    
    /**
     * index
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/provider/save")
    public void save(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        Map<String, Object> resultMap = Maps.newHashMap();
        //获取名称
        String name = request.getParameter("addName");
        String managerName = request.getParameter("addManagerName");
        String managerMobile = request.getParameter("addManagerMobile");
        String tel = request.getParameter("addTel");
        String accountBank = request.getParameter("addAccountBank");
        String accountBankBranch = request.getParameter("addAccountBankBranch");
        String accountName = request.getParameter("addAccountName");
        String accountNumber = request.getParameter("addAccountNumber");
        String proportion = request.getParameter("addProportion");
        String areas = request.getParameter("addAreas");
        String province = request.getParameter("addProvince");
        String city = request.getParameter("addCity");
        String county = request.getParameter("addCounty");
        String street = request.getParameter("addStreet");
        String address = request.getParameter("addAddress");
            
        SessionUser su = getCurrentUser(request);
        Provider p = new Provider();
        p.setName(name);
        p.setManagerName(managerName);
        p.setManagerMobile(managerMobile);
        p.setTel(tel);
        p.setAccountBank(accountBank);
        p.setAccountBankBranch(accountBankBranch);
        p.setAccountName(accountName);
        p.setAccountNumber(accountNumber);
        BigDecimal prop = new BigDecimal(proportion);
        p.setProportion(prop.divide(new BigDecimal(100)));
        p.setProvince(province);
        p.setCity(city);
        p.setCounty(county);
        p.setStreet(street);
        p.setAreas(areas);
        p.setAddress(address);
        p.setCreateUserid(su.getUserId());
        p.setIsDel(0);
        p.setStatus(0);
        providerService.save(p, su);
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
    @RequestMapping(value = "/provider/detail")
    public ModelAndView detail(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        //获取连锁商id
        String id = request.getParameter("id");
        //查询连锁商内容
        Provider p = providerService.queryById(id);
        //查询网点个数
        int shopNum = shopService.queryCountByProviderCode(p.getCode());
        
        request.setAttribute("provider", p);
        request.setAttribute("shopNum", shopNum);
        String returnView ="provider/detail";
        return new ModelAndView(returnView);
    }
    
    /**
     * edit
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/provider/edit")
    public ModelAndView edit(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        //获取连锁商id
        String id = request.getParameter("id");
        //查询连锁商内容
        Provider p = providerService.queryById(id);
        
        //获取省份地址
        List<Address> provinceL = addressService.queryByPid("0");
        //获取市地址
        List<Address> cityL = addressService.queryByPid(p.getProvince());
        //获取区县地址
        List<Address> countyL = addressService.queryByPid(p.getCity());
        //获取街道地址
        List<Address> streetL = addressService.queryByPid(p.getCounty());

        request.setAttribute("provider", p);
        request.setAttribute("provinceL", provinceL);
        request.setAttribute("cityL", cityL);
        request.setAttribute("countyL", countyL);
        request.setAttribute("streetL", streetL);
        String returnView ="provider/editProvider";
        return new ModelAndView(returnView);
    }
    
    /**
     * update
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/provider/update")
    public void update(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        Map<String, Object> resultMap = Maps.newHashMap();
        //获取连锁商id
        String id = request.getParameter("id");
        //获取连锁商名称
        String name = request.getParameter("addName");
        String managerName = request.getParameter("addManagerName");
        String managerMobile = request.getParameter("addManagerMobile");
        String tel = request.getParameter("addTel");
        String accountBank = request.getParameter("addAccountBank");
        String accountBankBranch = request.getParameter("addAccountBankBranch");
        String accountName = request.getParameter("addAccountName");
        String accountNumber = request.getParameter("addAccountNumber");
        String proportion = request.getParameter("addProportion");
        String areas = request.getParameter("addAreas");
        String province = request.getParameter("addProvince");
        String city = request.getParameter("addCity");
        String county = request.getParameter("addCounty");
        String street = request.getParameter("addStreet");
        String address = request.getParameter("addAddress");
        
        SessionUser su = getCurrentUser(request);
        
        Provider p = new Provider();
        p.setId(id);
        p.setName(name);
        p.setManagerName(managerName);
        p.setManagerMobile(managerMobile);
        p.setTel(tel);
        p.setAccountBank(accountBank);
        p.setAccountBankBranch(accountBankBranch);
        p.setAccountName(accountName);
        p.setAccountNumber(accountNumber);
        BigDecimal prop = new BigDecimal(proportion);
        p.setProportion(prop.divide(new BigDecimal(100)));
        p.setProvince(province);
        p.setCity(city);
        p.setCounty(county);
        p.setStreet(street);
        p.setAreas(areas);
        p.setAddress(address);
        providerService.update(p, su);
        
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
    @RequestMapping(value = "/provider/delete")
    public void delete(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        Map<String, Object> resultMap = Maps.newHashMap();
        //获取连锁商id
        String id = request.getParameter("id");
        SessionUser su = getCurrentUser(request);
        
        providerService.deleteById(id, su);
        
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
    @RequestMapping(value = "/provider/importIndex")
    public ModelAndView importIndex(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="provider/importIndex";
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
    @RequestMapping(value = "/provider/import")
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
                    providerService.importExcel(myfile,report,getCurrentUser(request));
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
