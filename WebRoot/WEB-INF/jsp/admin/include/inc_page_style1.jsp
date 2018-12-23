<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<c:if test="${page.recordsTotal > 0 }">
<div class="am-g" style="margin-bottom: 10px;">
  <div class="am-u-sm-6">
    <div class="dataTables_info" style="margin-top: 6px;" role="alert" aria-live="polite" aria-relevant="all">
                显示第 ${page.start + 1} 至 ${page.currentPage < page.totalPage ? page.currentPage * page.pageSize : page.recordsTotal} 条结果，共 ${page.recordsTotal } 条
    </div>
  </div>
  <div class="am-u-sm-6">
	<div class="paginate_style1 paging_simple_numbers">
	  <ul class="pagination">
	    <li class="paginate_button previous ${page.currentPage == 1 ? 'disabled' : ''}" aria-controls="dataTables-example" tabindex="0" id="dataTables-example_previous">
	      <a onclick="${page.ajaxNextPageMethod}(${page.currentPage - 1});" href="javascript:void(0);">上一页</a>
	    </li>
	    <c:forEach var="i" begin="1" end="${page.totalPage }">
		  <li class="paginate_button ${page.currentPage == i ? 'active' : ''}"><a onclick="${page.ajaxNextPageMethod}(${i});" href="javascript:void(0);">${i}</a></li>
	    </c:forEach>
	    <li class="paginate_button next ${page.currentPage == page.totalPage ? 'disabled' : ''}" aria-controls="dataTables-example" tabindex="0" id="dataTables-example_next">
	      <a onclick="${page.ajaxNextPageMethod}(${page.currentPage + 1});" href="javascript:void(0);">下一页</a>
	    </li>
	  </ul>
	</div>
  </div>
</div>
</c:if>