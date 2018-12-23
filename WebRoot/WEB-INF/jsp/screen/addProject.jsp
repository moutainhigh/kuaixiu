<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="/commons/taglibs.jsp"%>
<div class="modal-backdrop fade in"></div>
<div class="modal-dialog">
	<div class="modal-content">
		<div class="modal-title">
			<span>新增碎屏险项目</span> <a href="javascript: void(0);" class="close"
				data-dismiss="modal" aria-label="Close">&times;</a>
		</div>
		<div class="modal-body">
			<form id="insertForm" method="post" class="form-horizontal">
				<div class="form-group">
					<label for="addBrandId" class="col-sm-2 control-label"><span
						style="color: red">*</span> 机型品牌</label>
					<div class="col-sm-10">
						<select id="addBrandId" name="addBrandId" class="form-control">
							<option value="">--选择品牌--</option>
							<c:forEach items="${brands }" var="item" varStatus="i">
								<option value="${item.id }">${item.name }</option>
							</c:forEach>
						</select>
					</div>
				</div>
				<div class="form-group">
					<label for="addName" class="col-sm-2 control-label"><span
						style="color: red">*</span> 项目名称</label>
					<div class="col-sm-10">
						<input type="text" id="addName" name="addName"
							class="form-control" placeholder="请输入碎屏险项目名称">
					</div>
				</div>
				
				<div class="form-group">
					<label for="addPrice" class="col-sm-2 control-label"><span
						style="color: red">*</span> 购买费用</label>
					<div class="col-sm-10">
						<input type="text" id="addPrice" name="addPrice"
							class="form-control" placeholder="请输入购买费用 单位:元" >
					</div>
				</div>
				
				<div class="form-group">
					<label for="addMaxPrice" class="col-sm-2 control-label"><span
						style="color: red">*</span> 最高保额</label>
					<div class="col-sm-10">
						<input type="text" id="addMaxPrice" name="addMaxPrice"
							class="form-control" placeholder="请输入最高保额  单位:元" >
					</div>
				</div>
				
				<div class="form-group">
					<label for="addProductCode" class="col-sm-2 control-label"><span
						style="color: red">*</span> 产品代码</label>
					<div class="col-sm-10">
						<input type="text" id="addProductCode" name="addProductCode"
							class="form-control" placeholder="请输入产品代码" >
					</div>
				</div>
				
			   <div class="form-group">
					<label for="addSort" class="col-sm-2 control-label">有效期</label>
					<div class="col-sm-10">
						<input type="number" id="maxTime" name="maxTime"
							class="form-control" placeholder="请输入数值  单位:月" >
					</div>
				</div>
				
				<div class="form-group">
					<label for="addDetail" class="col-sm-2 control-label">项目描述</label>
					<div class="col-sm-10">
						<input type="text" id="addDetail" name="addDetail"
							class="form-control" placeholder="请输入项目描述信息" >
					</div>
				</div>
				
				
				<div class="form-group">
					<label for="addSort" class="col-sm-2 control-label">排序</label>
					<div class="col-sm-10">
						<input type="text" id="addSort" name="addSort"
							class="form-control" placeholder="请输入显示序号" >
					</div>
				</div>
				
				
				<button type="submit" class="hide" id="addSubmitBtn"></button>
			</form>
		</div>
		<div class="modal-footer">
			<button type="button" id="addSaveBtn" class="btn modal-btn">
				<span class="am-icon-save icon-save"></span>提交
			</button>
			<button type="button" id="addMissBtn" class="btn modal-btn"
				data-dismiss="modal" aria-label="Close">
				<span class="am-icon-close icon-close"></span>取消
			</button>
		</div>
	</div>
	<!-- /.modal-content -->
</div>
<!-- /.modal-dialog -->

<script type="text/javascript">
	//表单验证
	$(document).ready(function() {
		insertValidatorForm();
	});

	//初始化表单
	function insertValidatorForm() {
		$("#insertForm").bootstrapValidator({
			message : "不能为空",
			feedbackIcons : {
				valid : 'glyphicon glyphicon-ok',
				invalid : 'glyphicon glyphicon-remove',
				validating : 'glyphicon glyphicon-refresh'
			},
			fields : {
				addBrandId : {
					validators : {
						notEmpty : {
							message : "不能为空"
						}
					}
				},
				addName : {
					validators : {
						notEmpty : {
							message : "不能为空"
						}
					}
				},
				addPrice : {
					validators : {
						notEmpty : {
							message : "不能为空"
						}
					}
				},
				addMaxPrice : {
					validators : {
						notEmpty : {
							message : "不能为空"
						}
					}
				},
				addProductCode : {
					validators : {
						notEmpty : {
							message : "不能为空"
						}
					}
				},
				addSort : {
					validators : {
						regexp : {
							regexp : /^\d+$/,
							message : '请输入正确数字'
						}
					}
				}
			}
		// end fields
		}).on("success.form.bv",function(e) {
			 // 阻止表单提交
            e.preventDefault();
            //加载等待
            AlertText.tips("d_loading");
            //校验成功后的操作
            var btn = $("#addSaveBtn");
            //让按钮不能点击
            btn.button("loading");
            //遮盖层
							var options = {
								url : "${ctx}/screen/project/save.do",
								dataType : "JSON",
								success : function(data) {
									if (data.success) {
										//保存成功,关闭窗口，刷新列表
										refreshPage();
										//全部更新完后关闭弹窗
										$("#addMissBtn").click();
										//重置表单数据
										document.getElementById("insertForm")
												.reset();
									} else {
										//保存失败
										alert(data.msg);
									}
									addFormReset();
								},
								error : function() {
									alert("系统异常，请稍后再试");
									addFormReset();
								}
							}; // end options
							$("#insertForm").ajaxSubmit(options);
						}); // end on("success.form.bv"
	}

	/**
	 * 重置表单
	 */
	function addFormReset() {
		//重置表单验证
		$("#insertForm").data("bootstrapValidator").resetForm();
		//让按钮重新能点击
		$("#addSaveBtn").button("reset");
		//隐藏等待
		AlertText.hide();
	}

	//点击保存按钮,提交form表单，触发校验
	$("#addSaveBtn").click(function() {
		//格式化分类属性信息为JSON串
		$("#addSubmitBtn").click();
	});

	
</script>