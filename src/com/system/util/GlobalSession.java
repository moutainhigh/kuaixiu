package com.system.util;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author: anson
 * @CreateDate: 2018年5月3日 下午3:23:41
 * @version: V 1.0
 * 全局参数
 */
public class GlobalSession {

	private static HashMap<Object,Object> map=new HashMap<Object, Object>();
	
	public static void setSessionAttribute(String arg, Object o) {
		map.put(arg, o);
	}

	public static Object getSessionAttribute(String arg) {
		return map.get(arg);
	}

	public static void removeSessionAttribute(String arg) {
		Object session = map.get(arg);
		if (session != null) {
			map.remove(arg);
		}
	}


//	public static HttpSession getSession() {
//		HttpServletRequest session = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
//				.getRequest();
//		return session.getSession();
//	}
}
