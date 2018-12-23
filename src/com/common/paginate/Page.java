package com.common.paginate;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.system.constant.SystemConstant;


/**
 * 分页实例.
 * 
 * @CreateDate: 2016-8-19 下午10:52:32
 * @version: V 1.0
 */
public class Page {
    //每页显示记录数
    private int pageSize = SystemConstant.DEFAULT_PAGE_SIZE;
    //总页数
    private int totalPage;        
    //dataTables必要, 即没有过滤的记录数（数据库里总共记录数）
    private int recordsTotal;
    //dataTables必要, 过滤后的记录数（如果有接收到前台的过滤条件，则返回的是过滤后的记录数）
    private int recordsFiltered;
    //当前页
    private int currentPage = 1;    
    //当前记录起始索引
    private int start;
    //dataTables必要,Datatables发送的draw是多少那么服务器就返回多少。 
    //这里注意，作者出于安全的考虑，强烈要求把这个转换为整形，即数字后再返回，
    //而不是纯粹的接受然后返回，这是 为了防止跨站脚本（XSS）攻击。
    private int draw;
    //true:需要分页的地方，传入的参数就是Page实体；false:需要分页的地方，传入的参数所代表的实体拥有Page属性
    private boolean entityOrField;    
    //最终页面显示的底部翻页导航，详细见：getPageStr();
    private String pageStr;        
    //存放查询的结果集
    private List<?> data;
    //查询条件在页面表单中的位置
    private String formName = ""; 
    //ajax异步加载分页方法名称
    private String ajaxNextPageMethod = ""; 
    
    public Page() {
    }
    public Page(int pageSize) {
        this.setPageSize(pageSize);
    }
    public Page(int pageSize,String formName) {
        this.setPageSize(pageSize);
        this.formName = formName;
    }
    public Page(String formName) {
        this.formName = formName;
    }
    
    /**
     * 获取总页数
     * 注：该方法调用需要在设置总行数之后
     * @return
     */
    public int getTotalPage() {
        if(recordsTotal%pageSize==0) {
            totalPage = recordsTotal/pageSize;
        } 
        else {
            totalPage = recordsTotal/pageSize+1;
        }
        return totalPage;
    }
    
    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }
    
    public int getRecordsTotal() {
        return recordsTotal;
    }
    /**
     * 设置数据总行数
     * @param totalResult
     * @CreateDate: 2016-9-1 下午10:29:15
     */
    public void setRecordsTotal(int totalResult) {
        this.recordsTotal = totalResult;
        this.recordsFiltered = totalResult;
    }
    
    public int getRecordsFiltered() {
        return recordsFiltered;
    }
    public void setRecordsFiltered(int recordsFiltered) {
        this.recordsFiltered = recordsFiltered;
    }
    public int getDraw() {
        return draw;
    }
    public void setDraw(int draw) {
        this.draw = draw;
    }
    /**
     * 取当前页数
     * 注：该方法调用需要在设置总行数之后，否则返回1
     * @return
     * @author: lijx
     * @CreateDate: 2016-8-19 下午10:58:08
     */
    public int getCurrentPage() {
        if (currentPage > getTotalPage()) {
            currentPage = getTotalPage();
        }
        //确保当前页大于0
        if (currentPage < 1) {
            currentPage = 1;
        }
        return currentPage;
    }
    /**
     * 设置当前页,注意设置页数前请先设置每页查询数量
     * @param currentPage
     * @author: lijx
     * @CreateDate: 2016-8-28 下午10:11:02
     */
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage < 1 ? 1 : currentPage;
        //设置当前页数同时设置起始索引
        this.start = (this.currentPage - 1) * this.pageSize;
    }
    public int getPageSize() {
        return pageSize;
    }
    
    /**
     * 设置每页显示记录数
     * @param pageSize
     * @author: lijx
     * @CreateDate: 2016-8-28 下午10:16:19
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
        //设置起始索引同时设置页数
        this.currentPage = this.start / this.pageSize + 1;
    }
    public int getStart() {
        return start;
    }
    /**
     * 当前记录起始索引
     * @param start
     * @CreateDate: 2016-9-1 下午10:40:33
     */
    public void setStart(int start) {
        this.start = start;
        //设置起始索引同时设置页数
        this.currentPage = this.start / this.pageSize + 1;
    }
    public boolean isEntityOrField() {
        return entityOrField;
    }
    public void setEntityOrField(boolean entityOrField) {
        this.entityOrField = entityOrField;
    }
    public List<?> getData() {
        return data;
    }
    public void setData(List<?> result) {
        this.data = result;
    }
    public String getFormName() {
        return formName;
    }
    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getPageStr() {
        return pageStr;
    }

    public void setPageStr(String pageStr) {
        this.pageStr = pageStr;
    }
    
    /**
     * ajax异步加载分页方法名称
     * @return
     */
    public String getAjaxNextPageMethod() {
        if(StringUtils.isBlank(ajaxNextPageMethod)){
            return "ajaxNextPageMethod";
        }
        return ajaxNextPageMethod;
    }
    public void setAjaxNextPageMethod(String ajaxNextPageMethod) {
        this.ajaxNextPageMethod = ajaxNextPageMethod;
    }
    /**
     * 获取简单版的分页组件，无form，非url提交； 包括 首页  上页 5 下页 尾页 共15页 每页....
     * type 判断是否显示 分页标签
     */
    public String getAjaxPageStr() {
        StringBuffer sb = new StringBuffer();
        if (getRecordsTotal() > 0) {
            sb.append("<div class='pages_bar'>\n");
            if (getCurrentPage() == 1) {
                sb.append("    <a href='javascript:void(0);'>首页</a>\n");
                sb.append("    <a href='javascript:void(0);'>上页</a>\n");
            } 
            else {
                sb.append("    <a onclick=\"" + getAjaxNextPageMethod() + "(1);return false;\" href=\"javascript:void(0);\">首页</a>\n");
                sb.append("    <a onclick=\"" + getAjaxNextPageMethod() + "(" + (getCurrentPage() - 1) + ");return false;\" href=\"javascript:void(0);\">上页</a>\n");
            }
            //分页标签显示数量
            int showTag = 5;
            int startTag = 1;
            if (getCurrentPage() > showTag) {
                startTag = getCurrentPage() - 1;
            }
            int endTag = startTag + showTag - 1;
            for (int i = startTag; i <= getTotalPage() && i <= endTag; i++) {
                if (getCurrentPage() == i){
                    sb.append("<a href='javascript:void(0);' class='current_page'>" + i + "</a>\n");
                }
                else {
                    sb.append("    <a onclick=\"" + getAjaxNextPageMethod() + "(" + i + ");return false;\" href=\"javascript:void(0);\">" + i + "</a>\n");
                }
            }
            if (getCurrentPage() == getTotalPage()) {
                sb.append("    <a href='javascript:void(0);'>下页</a>\n");
                sb.append("    <a href='javascript:void(0);'>尾页</a>\n");
            } 
            else {
                sb.append("    <a onclick=\"" + getAjaxNextPageMethod() + "(" + (getCurrentPage() + 1) + ");return false;\" href=\"javascript:void(0);\">下页</a>\n");
                sb.append("    <a onclick=\"" + getAjaxNextPageMethod() + "(" + getTotalPage() + ");return false;\" href=\"javascript:void(0);\">尾页</a>\n");
            }
            sb.append("    <span>第" + getCurrentPage() + "页</span>\n");
            sb.append("    <span>共" + getTotalPage() + "页</span>\n");
            sb.append("    <span>每页" + getPageSize() + "条记录,共" + getRecordsTotal() + "条</span>\n");
            sb.append("</div>\n");

        }
        pageStr = sb.toString();
        return pageStr;
    }
}
