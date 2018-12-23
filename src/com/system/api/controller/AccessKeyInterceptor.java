package com.system.api.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.common.base.controller.BaseController;
import com.common.wechat.common.util.StringUtils;
import com.system.constant.SystemConstant;

public class AccessKeyInterceptor extends HandlerInterceptorAdapter {

    private static Log log = LogFactory.getLog(AccessKeyInterceptor.class);

    /**
     * preHandle方法是进行处理器拦截用的，顾名思义，该方法将在Controller处理之前进行调用，
     * SpringMVC中的Interceptor拦截器是链式的，可以同时存在多个Interceptor，
     * 然后SpringMVC会根据声明的前后顺序一个接一个的执行，
     * 而且所有的Interceptor中的preHandle方法都会在Controller方法调用之前调用。
     * SpringMVC的这种Interceptor链式结构也是可以进行中断的，
     * 这种中断方式是令preHandle的返回值为false，当preHandle的返回值为false的时候整个请求就结束了。
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String host =BaseController.getIpAddress(request);    //获取真实ip地址
        String allow=request.getHeader("Origin");     //允许跨域的主机域名    
        boolean tip=true;    //默认false不开启跨域
        log.info("域名："+allow+" 来源："+host);
//        if(StringUtils.isNotBlank(allow)){
//      	  if(allow.contains("m-super.cn")||allow.contains("zj.189.cn")||allow.contains("ananla.top")){
//            	//允许跨域
//            }else{
//            	tip=false;
//            }
//        }
        if(tip){
                response.setHeader("Access-Control-Allow-Origin",allow);
            	response.setHeader("Access-Control-Allow-Credentials","true");
                response.setHeader("Access-Control-Allow-Methods", "POST, GET"); 
                response.setHeader("Access-Control-Allow-Headers", "Origin, No-Cache, X-Requested-With, If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, Content-Type, X-E4M-With,userId,token");
        }
            return true;
    }

    /**
     * 这个方法只会在当前这个Interceptor的preHandle方法返回值为true的时候才会执行。
     * postHandle是进行处理器拦截用的，它的执行时间是在处理器进行处理之 后， 也就是在Controller的方法调用之后执行，
     * 然后要在Interceptor之前调用的内容都写在调用invoke之前，要在Interceptor之后调用的内容都写在调用invoke方法之后。
     */
    @Override
    public void postHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
    }

    /**
     * 该方法也是需要当前对应的Interceptor的preHandle方法的返回值为true时才会执行
     */
    @Override
    public void afterCompletion(HttpServletRequest request,
            HttpServletResponse response, Object handler, Exception ex) {
    }
    
    
    
    /**
     * 判断是否为允许跨域请求的域名
     */
    public static boolean isAllow(String domain){
    	String allow=SystemConstant.ALLOW_DOMAIN;
		String[] split = allow.split(",");
		List<String> s=new ArrayList<String>();
		s.addAll(Arrays.asList(split));
		if(s.contains(domain)){
		    return true;
		}else{
		    return false;
		}
    	
    }

}
