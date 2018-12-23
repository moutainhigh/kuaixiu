<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<div class="modal-backdrop fade in"></div>
<div class="modal-dialog">
  <div class="modal-content">
    <div class="modal-title"><span>站点信息</span>
      <a href="javascript: void(0);" class="close" data-dismiss="modal" aria-label="Close">&times;</a>
    </div>
    <div class="modal-body" style="overflow: hidden">


        <div class="row">
          <div class="col-lg-6">
            <div class="input-group">
              <input style="height: 34px;width: 450px;" type="text" class="form-control" id="queryStationName" placeholder="请输入站点"  oninput="queryStaion()">
              <span class="input-group-btn">
                   <button class="btn btn-default" type="button" onclick="queryStaion()">搜索</button>
               </span>
            </div><!-- /input-group -->
          </div><!-- /.col-lg-6 -->
        </div><!-- /.row -->


      <!--追加数据-->
       <div class="row" style="height: 400px; overflow-x: hidden; overflow-y: auto; position: relative;top:5px;">
         <div class="col-lg-6"   id="appendNews">

         </div>
       </div>
    </div>


</div><!-- /.modal-dialog -->
</div>
<script type="text/javascript">


    /**
     * 通过名字实时查询站点信息
     * @param name
     */
    function queryStaion(){
        var queryName= $("#queryStationName").val();
        var url_ = AppConfig.ctx + "/telecom/station/queryByName.do";
        var data_ = {name: queryName};
        $.ajax({
            url: url_,
            data: data_,
            type: "POST",
            dataType: "json",
            success: function (result) {
                if (result.success) {
                    var data=result.stationList;
                //    console.log(data);
                    //先清空div里之前的数据
                    $('#appendNews').empty();
                    for(var o in data){
                        var stationValue=data[o].id;   //站点id
                        var news= "<li><a href='javascript:void(0);'  onclick='confrimStation(this)'  value=" + stationValue + " >"    +data[o].stationName+    "</a></li>";
                        $(news).attr("value",data[o].id);
                        $('#appendNews').append(news);
                    }
                    //保存成功,关闭窗口，刷新列表
                    refreshPage();
                } else {
                    AlertText.tips("d_alert", "提示", result.msg);
                    return false;
                }
                //隐藏等待
                AlertText.hide();
            }
        });

    }


    /**
     * 确认选中的站点信息
     * @param station
     */
   function confrimStation(station) {
        var value=$(station).attr("value");        //value属性值
        var name=$(station).text();                //文本值
         //赋值给站点
        var option2=$("#stationId option:selected");  //获取选中的站点
        option2.val(value);
        option2.text(name);
        $("#modal-insertView").modal("hide"); //选中后关闭弹窗
   }



</script>