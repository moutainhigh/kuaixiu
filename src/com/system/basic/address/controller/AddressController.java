package com.system.basic.address.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.exception.SystemException;
import com.common.util.ConverterUtil;
import com.google.common.collect.Maps;
import com.system.api.entity.ResultData;
import com.system.basic.address.entity.Address;
import com.system.basic.address.service.AddressService;
import com.system.basic.user.service.SessionUserService;
import com.system.constant.ApiResultConstant;

/**
 * Address Controller
 *
 * @CreateDate: 2016-09-03 下午09:40:07
 * @version: V 1.0
 */
@Controller
public class AddressController extends BaseController {

    @Autowired
    private AddressService addressService;
    @Autowired
    private SessionUserService sessionUserService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/address/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        //测试获取地址首字母
        List<Address> addrL = addressService.queryByLevel(1);
        for(Address a : addrL){
            System.out.println(a.getArea() + " --> " + ConverterUtil.getFirstSpellForAreaName(a.getArea()));
        }
        String returnView ="address/list";
        return new ModelAndView(returnView);
    }
    
    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/address/getArea")
    public void getArea(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        Map<String, Object> resultMap = Maps.newHashMap();

        //获取请求参数
        String pid = request.getParameter("pid");
            
        List<Address> list = addressService.queryByPid(pid);
        
        resultMap.put(RESULTMAP_KEY_DATA, list);
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
        resultMap.put(RESULTMAP_KEY_MSG, "查询成功");
        renderJson(response, resultMap);
    }
    
    /**
     * 面向客户端 下拉地址筛选
     */
    @RequestMapping(value = "/address/getAreaNews")
    public void getAreaNews(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
    	ResultData result = new ResultData();
        try {
        	JSONObject params=getPrarms(request);
            String pid = params.getString("pid");  //获取请求参数
            List<Address> list = addressService.queryByPid(pid);
        		 JSONObject jsonResult=new JSONObject();
            	 JSONArray array=new JSONArray();
            	 if(list.size()>0||list!=null){
            		 for(Address address:list){
                		 JSONObject j=new JSONObject();
                		 j.put("area",address.getArea());
                		 j.put("areaId", address.getAreaId());
                		 j.put("pid", address.getPid());
                		 array.add(j);
                	 }
            	 }
            	 jsonResult.put("data", array);
            	 result.setResult(jsonResult);
            	 sessionUserService.getSuccessResult(result, jsonResult);        		
        }catch(SystemException e){
            e.printStackTrace();
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        renderJson(response, result);
    }
    
    
    
    
    
}
