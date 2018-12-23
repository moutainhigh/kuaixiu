<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="/commons/taglibs.jsp"%>
<div class="modal-backdrop fade in"></div>
<div class="modal-dialog">
	<div class="modal-content">
		<div class="modal-title">
			<span>新增维修机型</span> <a href="javascript: void(0);" class="close"
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
						style="color: red">*</span> 维修机型</label>
					<div class="col-sm-10">
						<input type="text" id="addName" name="addName"
							class="form-control" placeholder="请输入维修机型名称">
					</div>
				</div>
				<div class="form-group">
					<input type="hidden" id="addColorValue" name="addColorValue" /> <label
						for="addColor" class="col-sm-2 control-label"><span
						style="color: red">*</span> 颜色</label>
					<div id="addColorLabelDiv" class="col-sm-10">
						<div class="input-group">
							<input type="text" id="addColorFrist" name="addColorFrist"
								class="form-control" placeholder="请输入维修机型支持的颜色"> <span
								onclick="addColorFrist();" class="btn input-group-addon">添加</span>
						</div>
					</div>
				</div>
				<div id="addColorBtn" class="form-group hide">
					<div class="col-sm-10 col-sm-offset-2">
						<div class="input-group">
							<input type="text" id="addColorInput" name="addColorInput"
								class="form-control" placeholder="请输入维修机型支持的颜色"> <span
								onclick="addColor();" class="btn input-group-addon">添加</span>
						</div>
					</div>
				</div>
				<div class="form-group">
					<label for="addSort" class="col-sm-2 control-label">排序</label>
					<div class="col-sm-10">
						<input type="text" id="addSort" name="addSort"
							class="form-control" placeholder="请输入显示序号" value="99">
					</div>
				</div>
				<div class="form-group">
					<input type="hidden" id="addRepairCostValue"
						name="addRepairCostValue" /> <label for="addColor"
						class="col-sm-2 control-label"><span style="color: red">*</span>
						故障类型</label>
				</div>
				<c:forEach items="${projects}" var="item" varStatus="i">
					<div class="form-group">
						<div class="col-sm-3 col-sm-offset-1">
							<div class="checkbox">
								<label> <input type="checkbox" name="addProjectId"
									index="${i.index }" value="${item.id }">${item.name }
								</label>
							</div>
						</div>
						<div class="col-sm-8 ">
							<div class="">
								<label for="addRepairCost" class="col-sm-4 control-label">
									价格：</label>
								<div class="col-sm-8">
									<input type="text" name="addRepairCost" index="${i.index }"
										class="form-control" placeholder="请输入维修价格">
								</div>
							</div>
						</div>
					</div>
				</c:forEach>
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
		})
				.on(
						"success.form.bv",
						function(e) {
							// 阻止表单提交
							e.preventDefault();
							// 验证颜色是否添加
							//console.log("ddd");
							// 如果需要保存输入框的颜色，先调用添加按钮事件在判断
							var color = $("#addColorValue").val();
							if (color == "") {
								AlertText.tips("d_alert", "提示", "请添加颜色！");
								$("#insertForm").data("bootstrapValidator")
										.resetForm();
								return false;
							}
							// 验证是否选择故障
							var chkboxs = $("input[name='addProjectId']");
							var costs = $("input[name='addRepairCost']");
							var json = "";
							for (var i = 0; i < chkboxs.size(); i++) {
								if (chkboxs[i].checked) {
									if (isNumber(costs[i].value)) {
										json += ",{projectId:'"
												+ chkboxs[i].value + "',cost:'"
												+ costs[i].value + "'}";
									} else {
										AlertText.tips("d_alert", "提示",
												"请给选择维修类型填写维修金额！");
										$("#insertForm").data(
												"bootstrapValidator")
												.resetForm();
										return false;
									}
								}
							}
							if (json != "") {
								json = "[" + json.substring(1, json.length)
										+ "]";
							} else {
								AlertText.tips("d_alert", "提示", "请选择维修类型！");
								$("#insertForm").data("bootstrapValidator")
										.resetForm();
								return false;
							}
							$("#addRepairCostValue").val(json);
							// 及是否输入金额
							//加载等待
							AlertText.tips("d_loading");
							//校验成功后的操作
							var btn = $("#addSaveBtn");
							//让按钮不能点击
							btn.button("loading");
							//遮盖层
							var options = {
								url : "${ctx}/model/save.do",
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

	function addColorFrist() {
		//获取输入的颜色
		var color = $("#addColorFrist").val();
		$("#addColorFrist").val("")
		color = color.trim();
		//console.log("ddd");
		if (color != "") {
			$("#addColorLabelDiv").html("");
			buildColorLabel(color)
			$("#addColorBtn").removeClass("hide");
		} else {
			AlertText.tips("d_alert", "提示", "请输入颜色！");
		}
	}

	function addColor() {
		//获取输入的颜色
		var color = $("#addColorInput").val();
		$("#addColorInput").val("")
		color = color.trim();
		//console.log("ddd");
		if (color != "") {
			buildColorLabel(color)
		} else {
			AlertText.tips("d_alert", "提示", "请输入颜色！");
		}
	}

	function buildColorLabel(color) {
		var colors = color.replace(/，/g, ",").split(",");
		var html = "";
		//判断颜色是否已经存在
		var colorOld = $("#addColorValue").val().split(",");
		$
				.each(
						colors,
						function(i, item) {
							if (colorOld.indexOf(item) < 0) {
								//alert(item);
								html += "<div class='label alert label-warning alert-dismissible' role='alert'>";
								html += "  <button onclick=\"removeColor('"
										+ item
										+ "');\" type='button' class='close' data-dismiss='alert'><span aria-hidden='true'>&times;</span><span class='sr-only'>Close</span></button>";
								html += "  <strong>" + item + "</strong>";
								html += "</div>";
								colorOld.push(item);
							}
						});
		//清空内容
		$("#addColorLabelDiv").append(html);
		$("#addColorValue").val(colorOld);
	}

	function removeColor(color) {
		var colorOld = $("#addColorValue").val().split(",");
		var i = colorOld.indexOf(color);
		if (i >= 0) {
			colorOld.splice(i, 1);
			$("#addColorValue").val(colorOld);
		}
	}
</script>