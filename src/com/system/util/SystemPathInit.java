package com.system.util;

import javax.servlet.ServletContext;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.ServletContextAware;

import com.system.constant.SystemConstant;


/**
 * .
 * 
 * @author: lijx
 * @CreateDate: 2016-10-23 下午2:42:49
 * @version: V 1.0
 * 容器初始化数据
 */
@Configuration
public class SystemPathInit implements ServletContextAware{
    
    @Override
    public void setServletContext(ServletContext servletContext) {
        System.out.println("servlet init");
        SystemConstant.WEB_CONTEXT_PATH = servletContext.getContextPath();
        SystemConstant.WEB_REAL_PATH = servletContext.getRealPath("/");
    }
    
}