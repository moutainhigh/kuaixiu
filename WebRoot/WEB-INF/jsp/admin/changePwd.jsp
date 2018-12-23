<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<link rel="stylesheet" href="${webResourceUrl}/resource/order/css/order.detail.css">
      <div class="am-cf am-padding am-padding-bottom-0">
        <div class="am-fl am-cf" style="width: 100%;">
          <strong class="am-text-primary am-text-lg">安全中心</strong> / <small>密码修改</small>
        </div>
      </div>
      
      <hr>
      <div class="am-g">
	    <form id="submitForm" class="form-horizontal" role="form">
		  <div class="form-group">
            <label class="col-sm-2 control-label">当前密码：</label>
            <div class="col-sm-4">
           		      <input type="password"  name="oldPwd" class="form-control" placeholder="请输入当前密码">
            </div>
          </div>
	      <div class="form-group">
            <label class="col-sm-2 control-label">新密码：</label>
            <div class="col-sm-4">
           		      <input type="password"  name="newPwd" class="form-control" placeholder="请输入新密码">
            </div>
          </div>
          <div class="form-group">
            <label class="col-sm-2 control-label">再次输入新密码：</label>
            <div class="col-sm-4">
           		      <input type="password"  name="twoNewPwd" class="form-control" placeholder="请再次输入新密码">
            </div>
          </div>
          <div class="form-group">
            <div class="col-sm-4 col-sm-offset-2">
              <button type="submit" id="submitBtn" class="btn btn-primary btn-lg btn-block" >确定</button>
            </div>
          </div>
	    </form>
      </div>
      
      <script type="text/javascript">
		//表单验证
		$(document).ready(function() {
			updateValidatorForm();
		});
		
		//初始化表单
		function updateValidatorForm() {
		    $("#submitForm").bootstrapValidator({
		            message : "不能为空",
		            feedbackIcons : {
		                valid : 'glyphicon glyphicon-ok',
		                invalid : 'glyphicon glyphicon-remove',
		                validating : 'glyphicon glyphicon-refresh'
		            },
		            fields : {
		            	oldPwd: {
		                    validators : {
		                        notEmpty: {
		                            message: '当前密码不能为空'
		                        },
		                        remote: {
		                            url: '${ctx}/sysUser/checkUserPasswd.do',
		                            message: '当前密码不正确'
		                        }
		                    }
		                },
		                newPwd: {
		                    validators: {
		                        notEmpty: {
		                            message: '新密码不能为空'
		                        }, regexp: {
		                            regexp: /^(?=.*[0-9])(?=.*[a-zA-Z]).{6,18}$/  ,
		                            message: '密码中必须包含字母、数字，至少6个字符，最多18个字符'
		                        }
		                    }
		                },
		                twoNewPwd: {
		                    validators: {
		                        notEmpty: {
		                            message: '确认密码不能为空' 
		                        },
		                        identical: {
		                            field: 'newPwd',
		                            message: '两次输入的密码不一致'
		                        }
		                    }
		                }
		               
		            }// end fields
		        }).on("success.form.bv", function(e) {
		            // 阻止表单提交
		            e.preventDefault();
		            //验证
		         
		            //加载等待
		            AlertText.tips("d_loading");
		            //校验成功后的操作
		             var btn = $("#submitBtn");
		            //让按钮不能点击
		            btn.button("loading");
		            //遮盖层
		            var options = {
		                url : "${ctx}/sysUser/updateUserPasswd.do",
		                dataType : "JSON",
		                success : function(data) {
		                    if(data.success){
		                    	AlertText.tips("d_alert", "提示", "新密码已成功设置！", function(){
		                    		window.location.href="${commonurl_home}";
		                    	});
		                    }else{
		                    	//隐藏等待
		                        AlertText.hide();
		                        //保存失败
		                        AlertText.tips("d_alert", "提示", data.msg);
			                    upFormReset();
		                    }
		                },
		                error : function() {
		                	//隐藏等待
		                    AlertText.hide();
		                    AlertText.tips("d_alert", "提示", "系统异常，请稍后再试");
		                    upFormReset();
		                }
		            }; // end options
		            $("#submitForm").ajaxSubmit(options);
		        }); // end on("success.form.bv"
		}
		
		/**
		 * 重置表单
		 */
		function upFormReset(){
		    //重置表单验证
		    $("#submitForm").data("bootstrapValidator").resetForm();
		    //让按钮重新能点击
		    $("#submitBtn").button("reset");
		}
</script>