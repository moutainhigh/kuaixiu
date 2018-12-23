package com.kuaixiu.clerk.page;

import java.io.Serializable;
import java.util.List;

public class Page implements Serializable {

	
	private static final long serialVersionUID = 5673827390706059673L;
	//每页显示记录数
	private int everyPage;
	//总页数
	private int totalPage;
	//总记录数
	private int totalCount;
	//当前页
	private int currentPage;
	//起始点
	private int beginIndex;
	//是否有上一页
	private boolean hasPrePage;
	//是否有下一页
	private boolean hasNextPage;
	//存放查询的结果集，配合dataTable表格插件使用
    private List<?> data;
    
	public List<?> getData() {
		return data;
	}
	public void setData(List<?> data) {
		this.data = data;
	}
	//默认构造函数
	public Page(){
		
	}
	public int getEveryPage() {
		return everyPage;
	}
	public void setEveryPage(int everyPage) {
		this.everyPage = everyPage;
	}
	public int getTotalPage() {
		return totalPage;
	}
	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public int getBeginIndex() {
		return beginIndex;
	}
	public void setBeginIndex(int beginIndex) {
		this.beginIndex = beginIndex;
	}
	public boolean isHasPrePage() {
		return hasPrePage;
	}
	public void setHasPrePage(boolean hasPrePage) {
		this.hasPrePage = hasPrePage;
	}
	public boolean isHasNextPage() {
		return hasNextPage;
	}
	public void setHasNextPage(boolean hasNextPage) {
		this.hasNextPage = hasNextPage;
	}
	
	public Page(int everyPage, int totalPage, int totalCount, int currentPage, int beginIndex, boolean hasPrePage,
			boolean hasNextPage) {
	
		this.everyPage = everyPage;
		this.totalPage = totalPage;
		this.totalCount = totalCount;
		this.currentPage = currentPage;
		this.beginIndex = beginIndex;
		this.hasPrePage = hasPrePage;
		this.hasNextPage = hasNextPage;
	}
	
	
	
}
