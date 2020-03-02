package com.system.util;

import java.util.List;

/**
 * @author wxk
 * @Date 2020/02/03
 */
public class PageBean<T> {
    /**
     * 当前页， 默认显示第一页
     */
    private Integer currntPage = 1;
    /**
     * 查询返回的行数（每页显示的行数），默认每页显示3行
     */
    private int pageCount = 3;
    /**
     * 总记录数
     */
    private int totalCount;
    /**
     * 总页数 = 总记录数/每页显示的行数（+1）
     */
    private int totalPage;
    /**
     * 分页查询的数据,运用泛型，可以重复利用
     */
    private List<T> pageData;

    public int getTotalPage() {
        if (totalCount % pageCount == 0) {
            totalPage = totalCount / pageCount;
        } else {
            totalPage = totalCount / pageCount + 1;
        }
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getCurrntPage() {
        return currntPage;
    }

    public void setCurrntPage(int currntPage) {
        this.currntPage = currntPage;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }


    public List<T> getPageData() {
        return pageData;
    }

    public void setPageData(List<T> pageData) {
        this.pageData = pageData;
    }
}