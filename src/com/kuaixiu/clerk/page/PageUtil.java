package com.kuaixiu.clerk.page;

import com.kuaixiu.clerk.page.Page;

/**
 * 分页信息辅助类
 * @author Administrator
 *
 */
public class PageUtil {
     
	public static Page createPage(int everyPage,int totalCount,int currentPage){
		Page page = null;
		//设置参数
		everyPage = getEveryPage(everyPage);
		int totalPage = getTotalPage(totalCount, everyPage);
		currentPage = getCurrentPage(currentPage);
		int beginIndex = getBeginIndex(everyPage, currentPage);
		boolean hasPrePage = isHasPrePage(currentPage);
		boolean hasNextPage = isHasNextPage(currentPage, totalPage);
		page = new Page(everyPage, totalPage, totalCount, currentPage,
				 beginIndex,hasPrePage,hasNextPage);
		return page;
	}
	
	/**
	 * 获得每页显示数
	 */
	public static int getEveryPage(int everyPage){
		return everyPage==0?1:everyPage;
	}
	
	/**
	 * 获得总页数
	 */
	
	public static int getTotalPage(int totalCount,int everyPage){
		int totalPage=0;
		if(totalCount/everyPage==0){
			totalPage=totalCount/everyPage;
		}else{
			totalPage=totalCount/everyPage+1;
		}
		
		return totalPage;
	}
	
	/**
	 * 获得当前页
	 */
	
	public static int getCurrentPage(int currentPage){
		return currentPage==0?1:currentPage;
		
	}
	
	/**
	 * 获得起始点
	 */
	
	public static int getBeginIndex(int everyPage,int currentPage){
		return (currentPage-1)*everyPage;
		
	}
	
	/**
	 * 是否有上一页
	 */
	
	public static boolean isHasPrePage(int currentPage){
		return currentPage==1?false:true;
	}
	
	/**
	 * 是否有下一页
	 */
	
	public static boolean isHasNextPage(int currentPage,int totalPage){
		return (totalPage==currentPage||totalPage==0)?false:true;
	}
	
}
