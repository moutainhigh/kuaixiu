<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="/commons/taglibs.jsp"%>
<link rel="stylesheet"
	href="${webResourceUrl}/resource/order/css/order.detail.css">
<div class="am-cf am-padding am-padding-bottom-0">
	<div class="am-fl am-cf" style="width: 100%;">
		<strong class="am-text-primary am-text-lg"><a
			href="javascript:void(0);" onclick="toList();">回收订单管理</a></strong> / <small>订单详情</small>
		<strong class="am-text-primary"><a
			href="javascript:void(0);" onclick="func_to_back();">返回</a></strong>
	</div>
</div>


<div class="am-g order-info mt20">
	<table class="detail-table">
		<tr>
			<td class="td-title">
				<h4>企业信息：</h4>
			</td>
			<td class="td-space"></td>
			<td class="td-info">

				<div class="row">
					<div class="col-md-6 col-sm-6 col-xs-12">
						<h4>企业名称：${company.name }</h4>
					</div>
					<!-- /.col -->
					<div class="col-md-6 col-sm-6 col-xs-12">
						<h4>客户经理：${company.customerManager}</h4>
					</div>
					<!-- /.col -->
				</div> <!-- /.row -->

				<div class="row">
					<div class="col-md-6 col-sm-6 col-xs-12">
						<h4>客户经理电话：${company.mobile }</h4>
					</div>
					<!-- /.col -->
				</div>


				<div class="row">
					<div class="col-md-6 col-sm-6 col-xs-12">
						<h4>
							提交时间：
							<fmt:formatDate value="${company.inTime }"
								pattern="yyyy-MM-dd HH:mm:ss" />
						</h4>
					</div>
					<!-- /.col -->
					<div class="col-md-6 col-sm-6 col-xs-12">
						<h4>
							回收时间：
							<fmt:formatDate value="${company.recycleTime }"
								pattern="yyyy-MM-dd HH:mm:ss" />
						</h4>
					</div>
					<!-- /.col -->
				</div> <!-- /.row -->
			</td>
		<tr>
			<td colspan="3" class="tr-space"></td>
		</tr>
		
	</table>


</div>

<div class="am-g order-info mt20">
     <table class="detail-table">
      <tr>
			<td class="td-info">
			   <div class="row">
					<div class="text-left">
						<h4 style="position:relative;left:25px;">回收机型信息：</h4>
					</div>
			    </div>
			    <div style="height:25px;"></div>
				<table class="table table-bordered">
					<tr>
						<th>序号</th>
						<th>品牌</th>
						<th>机型</th>
						<th>数量（台）</th>
						<th>良品价（元）</th>
						<th>合计（元）</th>
					</tr>
					<c:forEach items="${list }" var="item" varStatus="i">
						<tr>
							<td>${i.index+1}</td>
							<td>${item.brand}</td>
							<td>${item.model}</td>
							<td>${item.sum}</td>
							<td>${(item.price+0)/item.sum}</td>
							<td>${item.price}</td>
						</tr>
					</c:forEach>
				</table>
				
				<div class="row">
					<div class="text-right">
						<h4 style="left:0px;"">订单总金额：${price }元</h4>
					</div>
			    </div>
			</td>
		</tr>
      </table>
</div>


<div class="am-g order-info mt20">
      <table class="detail-table">
      <h4 style="position:relative;left:25px;">处理信息：</h4>
                    <div class="form-group">
                    <c:choose>
                         <c:when test="${company.note!=null&&company.note!=''}">
                             <textarea class="form-control" rows="4"  id="note" style="position: relative;left: 20px;width: 97%;">${company.note}</textarea>
                         </c:when>
                         <c:otherwise>
                             <textarea class="form-control" rows="4"  id="note" style="position: relative;left: 20px;width: 97%;"/>
                             <button class="btn btn-default"  style="position:relative;left: 90%;width: 110px;"
                             type="button" onclick="update('${company.id}');">提交</button>
                         </c:otherwise>
                     </c:choose>
                     </div>
      </table>
 
</div>




<script type="text/javascript">
	function toList() {
		func_reload_page("${ctx}/recycle/companyList.do");
	}
	
	
	

	function update(id) {
			var url_ = AppConfig.ctx + "/recycle/company/updateNote.do";
			console.log('当前id：'+id);
			var note=$("#note").val();
			console.log(note);
			var data_ = {
				id : id,
				note: note
			};
			$.ajax({
				url : url_,
				data : data_,
				type : "POST",
				dataType : "json",
				success : function(result) {
					if (result.success) {
						//保存成功,关闭窗口，刷新列表
						AlertText.tips("d_alert", "提示", "更新成功");
						refreshPage();
					} else {
						AlertText.tips("d_alert", "提示", result.resultMessage);
						return false;
					}
				}
		});
	}
	
	
</script>



