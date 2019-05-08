package com.common.handler;

import com.alibaba.fastjson.JSON;
import com.common.base.controller.BaseController;
import com.common.exception.SessionInvalidateException;
import com.common.exception.SystemException;
import com.google.common.collect.Maps;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义异常处理
 */
public class MyExceptionHandler implements HandlerExceptionResolver {
    private static final Logger log = Logger.getLogger(MyExceptionHandler.class);
    /**
     * 异常处理
     * @param request
     * @param response
     * @param object
     * @param exception
     * @return
     */
    public ModelAndView resolveException(HttpServletRequest request,
                                         HttpServletResponse response, Object object, Exception exception) {
        //  打印错误日志
        exception.printStackTrace();
        //获取请求地址
        String uri = request.getRequestURI();
        //获取应用名称
        String ctx = request.getContextPath();
        //去掉项目名称
        uri = uri.replaceFirst(ctx, "");
        // 判断是否ajax请求
        if (!(request.getHeader("accept").indexOf("application/json") > -1 
                || (request.getHeader("X-Requested-With") != null
                && request.getHeader("X-Requested-With").indexOf("XMLHttpRequest") > -1))) {

            // 如果不是ajax，JSP格式返回
            // 为安全起见，只有业务异常我们对前端可见，否则否则统一归为系统异常
            Map<String, Object> map = new HashMap<String, Object>();
            if (exception instanceof SessionInvalidateException) {
                map.put("exceptionType", "sessionError");
                map.put("errorExceptionMsg", exception.getMessage());
            }
            else if (exception instanceof SystemException) {
                map.put("exceptionType", "commError");
                map.put("errorExceptionMsg", exception.getMessage());
            }
            else {
                map.put("exceptionType", "commError");
                map.put("errorExceptionMsg", "系统异常，请稍后再试");
            }
            //打印日志
            if(exception instanceof SystemException||exception instanceof SessionInvalidateException){
            }else{
            	   log.error(exception.getMessage());
                  // exception.printStackTrace();
            }
         
            //对于非ajax请求，我们都统一跳转到error.jsp页面
            request.setAttribute("exceptJson", map);
            if (uri.startsWith("/wechat/")) {
                return new ModelAndView("/wechat/error", map);
            }
            else if (uri.startsWith("/webpc/")) {
                return new ModelAndView("/pc/error", map);
            }
            else if (uri.startsWith("/sj/")) {
                return new ModelAndView("/sjAdmin/error", map);
            }
            else {
            	log.info("其他错误");
                return new ModelAndView("/admin/error", map);
            }
        }
        else {
             // 如果是ajax请求，JSON格式返回
            try {
                response.setContentType("application/json;charset=UTF-8");
                PrintWriter writer = response.getWriter();
                Map<String, Object> resultMap = Maps.newHashMap();
                resultMap.put(BaseController.RESULTMAP_KEY_SUCCESS, BaseController.RESULTMAP_SUCCESS_FALSE);
    
                // 为安全起见，只有业务异常我们对前端可见，否则统一归为系统异常
                if (exception instanceof SessionInvalidateException) {
                    resultMap.put(BaseController.RESULTMAP_KEY_MSG, exception.getMessage());
                    resultMap.put("exceptionType", "sessionError");
                }
                else if (exception instanceof SystemException) {
                    resultMap.put(BaseController.RESULTMAP_KEY_MSG, exception.getMessage());
                    resultMap.put("exceptionType", "commError");
                }
                else {
                    resultMap.put(BaseController.RESULTMAP_KEY_MSG, "系统异常，请稍后重试");
                    resultMap.put("exceptionType", "commError");
                }
                request.setAttribute("result", resultMap);
    
                writer.write(JSON.toJSONString(resultMap));
                writer.flush();
                writer.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}