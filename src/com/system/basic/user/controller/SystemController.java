package com.system.basic.user.controller;

import java.util.Map;

import com.common.base.controller.BaseController;
import com.google.common.collect.Maps;
import com.system.api.service.ApiConfigService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 系统控制类.
 *
 * @author: lijx
 * @CreateDate: 2016-8-19 下午11:49:41
 * @version: V 1.0
 */
@Controller
public class SystemController extends BaseController {
    private final Logger logger = Logger.getLogger(SystemController.class);

    @Autowired
    private ApiConfigService apiConfigService;

    /**
     * 重新加载接口配置
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/system/reloadApiConfig")
    public void reloadApiConfig(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        Map<String, Object> resultMap = Maps.newHashMap();
        try {
            
            apiConfigService.initApiConfig();
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
            resultMap.put(RESULTMAP_KEY_MSG, "操作成功");
        } 
        catch (Exception e) {
            e.printStackTrace();
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
            resultMap.put(RESULTMAP_KEY_MSG, "系统异常请稍后");
        }
        renderJson(response, resultMap);
    }

}
