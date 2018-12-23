<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<div class="modal-backdrop fade in"></div>
<div class="modal-dialog">
  <div class="modal-content">
    <div class="modal-title"><span>新增维修工程师</span>
      <a href="javascript: void(0);" class="close" data-dismiss="modal" aria-label="Close">&times;</a>
    </div>
    <div class="modal-body">
      <form id="insertForm" method="post" class="form-horizontal">
        <div class="form-group">
          <label for="addName" class="col-sm-3 control-label"><span style="color:red">*</span> 角色名称</label>
          <div class="col-sm-9">
            <input type="text" id="roleName" name="roleName" class="form-control" placeholder="请输入角色名称">
          </div>
        </div>
        <div class="form-group">
          <label for="addMobile" class="col-sm-3 control-label"><span style="color:red">*</span> 登录账号</label>
          <div class="col-sm-9">
            <input type="text" id="userId" name="userId" maxlength="11" class="form-control" placeholder="请输入用户ID">
          </div>
        </div>
        <div class="form-group">
          <label for="addIdcard" class="col-sm-3 control-label"><span style="color:red">*</span> 用户姓名</label>
          <div class="col-sm-9">
            <input type="text" id="userName" name="userName" maxlength="18" class="form-control" placeholder="请输入用户姓名">
          </div>
        </div>
        <button type="submit" class="hide" id="addSubmitBtn"></button>
      </form>
    </div>
    <div class="modal-footer">
      <button type="button" id="addSaveBtn" onclick="submitRoleSearch();" class="btn modal-btn" ><span class="am-icon-save icon-save"></span>提交</button>
      <button type="button" hidden="hidden" id="addMissBtn" class="btn modal-btn" data-dismiss="modal" aria-label="Close"><span class="am-icon-close icon-close"></span>取消</button>
    </div>
  </div><!-- /.modal-content -->
</div><!-- /.modal-dialog -->
<script type="text/javascript">

  function submitRoleSearch() {
      var roleName=$("#roleName").val();
      var userId=$("#userId").val();
      var userName=$("#userName").val();
      var data_ = {roleName: roleName,userId:userId,userName:userName};
      $.ajax({
          data: data_,
          type: "POST",
          url : "${ctx}/sysMenu/listTree.do",
          dataType : "JSON",
          success : function(result) {
              if(result.success){
                  //保存成功,关闭窗口，刷新列表
                  refreshPage(result.result);
                  //全部更新完后关闭弹窗
                  $("#addMissBtn").click();
              }else{
                  //保存失败
                  alert(result.resultMessage);
//                  $("#addMissBtn").click();
              }
          }
      });
  }

</script>