package com.system.web;

import java.io.File;
import java.util.Locale;

import org.springframework.web.servlet.view.InternalResourceView;

/**
* @author: anson
* @CreateDate: 2017年9月21日 下午7:09:31
* @version: V 1.0
* 自定义Html视图解析器
*/
public class HtmlResourceView extends InternalResourceView{
	
	     public boolean checkResource(Locale locale) {  
	      File file = new File(this.getServletContext().getRealPath("/") + getUrl());  
	      return file.exists();// 判断该页面是否存在  
	     }  
}
