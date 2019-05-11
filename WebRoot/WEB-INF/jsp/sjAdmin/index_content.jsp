<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
      <div class="am-cf am-padding am-padding-bottom-0">
        <div class="am-fl am-cf" style="width: 100%;">
          <strong class="am-text-primary am-text-lg">首页</strong>
        </div>
      </div>
      
      <hr>
      
      <div class="am-g">
        <div class="panel panel-success index-panel-msg">
          <div class="row">
            <!-- /.col -->
            <div class="col-md-4 col-sm-6 col-xs-12">
              <div class="info-box">
                <span class="info-box-icon bg-red"><i class="am-icon-rmb"></i></span>
    
                <div class="info-box-content">
                  <span class="info-box-text">平台交易金额（元）</span>
                  <span class="info-box-number"><fmt:formatNumber value="${allAmount }" pattern="0.00" maxFractionDigits='2'/></span>
                </div>
                <!-- /.info-box-content -->
              </div>
              <!-- /.info-box -->
            </div>
            <!-- /.col -->

            <!-- fix for small devices only -->
            <div class="clearfix visible-sm-block"></div>
    
            <div class="col-md-4 col-sm-6 col-xs-12">
              <div class="info-box">
                <span class="info-box-icon bg-green"><i class="am-icon-line-chart"></i></span>
    
                <div class="info-box-content">
                  <span class="info-box-text">平台日订单数</span>
                  <span class="info-box-number">${orderDayCount }</span>
                </div>
                <!-- /.info-box-content -->
              </div>
              <!-- /.info-box -->
            </div>
            <!-- /.col -->
            <div class="col-md-4 col-sm-6 col-xs-12">
              <div class="info-box">
                <span class="info-box-icon bg-yellow"><i class="am-icon-diamond"></i></span>
    
                <div class="info-box-content">
                  <span class="info-box-text">平台日交易金额（元）</span>
                  <span class="info-box-number"><fmt:formatNumber value="${orderDayAmount }" pattern="0.00" maxFractionDigits='2'/></span>
                </div>
                <!-- /.info-box-content -->
              </div>
              <!-- /.info-box -->
            </div>
            <!-- /.col -->
          </div><!-- /.row -->
          <div class="row">
            <!-- /.col -->
            <div class="col-md-4 col-sm-6 col-xs-12">
              <div class="info-box">
                <span class="info-box-icon bg-teal"><i class="am-icon-bank"></i></span>
    
                <div class="info-box-content">
                  <span class="info-box-text">维修商家（个）</span>
                  <span class="info-box-number">${shopCount }</span>
                </div>
                <!-- /.info-box-content -->
              </div>
              <!-- /.info-box -->
            </div>
            <!-- /.col -->

            <!-- fix for small devices only -->
            <div class="clearfix visible-sm-block"></div>
    
            <div class="col-md-4 col-sm-6 col-xs-12">
              <div class="info-box">
                <span class="info-box-icon bg-olive"><i class="am-icon-users"></i></span>
    
                <div class="info-box-content">
                  <span class="info-box-text">工程师人数（人）</span>
                  <span class="info-box-number">${engCount }</span>
                </div>
                <!-- /.info-box-content -->
              </div>
              <!-- /.info-box -->
            </div>
            <!-- /.col -->
            <div class="col-md-4 col-sm-6 col-xs-12">
              <div class="info-box">
                <span class="info-box-icon bg-blue"><i class="am-icon-users"></i></span>
    
                <div class="info-box-content">
                  <span class="info-box-text">平台用户量（人）</span>
                  <!--
                  <span class="info-box-number">${customerCount }</span>
                  -->
                  <span class="info-box-number">3543</span>
                </div>
                <!-- /.info-box-content -->
              </div>
              <!-- /.info-box -->
            </div>
            <!-- /.col -->
          </div><!-- /.row -->
        </div><!-- /.panel -->
        
      </div><!-- /.am-g -->
      
      <div class="am-g mt20">
        <nav class="navbar navbar-default" role="navigation" style="margin-left: 20px; margin-right: 20px;">
	      <ul class="nav navbar-nav">
	        <li class="active"><a onclick="selectTarget(this, 'order');" href="javascript:void(0);">订单增长</a></li>
            <li><a onclick="selectTarget(this, 'provider');" href="javascript:void(0);">连锁商增长</a></li>
            <li><a onclick="selectTarget(this, 'costomer');" href="javascript:void(0);">平台用户增长</a></li>
            <li><a onclick="selectTarget(this, 'orderPrice');" href="javascript:void(0);">交易额增长</a></li>
	      </ul>
	    </nav>
      </div>
      
      <div class="am-g">
        <div class="panel panel-success" style="margin-left: 20px; margin-right: 20px;">
          <div class="panel-heading">
            <div class="btn-group">
			  <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown">
			    <span id="dateLength">最近30天</span> <span class="caret"></span>
			  </button>
			  <ul class="dropdown-menu" role="menu">
			    <li><a onclick="dateLengthChange(this,7);" href="javascript:void(0);">最近7天</a></li>
			    <li><a onclick="dateLengthChange(this,15);" href="javascript:void(0);">最近15天</a></li>
			    <li><a onclick="dateLengthChange(this,30);" href="javascript:void(0);">最近30天</a></li>
			  </ul>
			</div><!-- btn-group end -->
			<div class="btn-group">
                <input type="text" id="query_date" name="query_date" class="form-control" style="width: 185px;" readonly >
            </div><!-- btn-group end -->
            <div id="dataFromView" class="btn-group">
              <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown">
                <span id="dataFrom">全部来源</span> <span class="caret"></span>
              </button>
              <ul class="dropdown-menu" role="menu">
                <li><a onclick="dataFromChange(this,'');" href="javascript:void(0);">全部来源</a></li>
                <li><a onclick="dataFromChange(this,'1');" href="javascript:void(0);">微信端</a></li>
                <li><a onclick="dataFromChange(this,'0');" href="javascript:void(0);">PC端</a></li>
              </ul>
            </div><!-- btn-group end -->
          </div><!-- panel-heading -->
          <div class="row">
            <div class="col-md-12 col-sm-12 col-xs-12">
              <div id="echart-panel" style="width: 100%; height: 300px;"></div>
            </div><!-- /.col -->
          </div><!-- /.row -->
          
        </div><!-- /panel -->
      </div><!-- /am-g -->
      

<script type="text/javascript">
function toList(){
    func_reload_page("${ctx}/engineer/list.do");
}

var queryParam = new Object();
queryParam.query_endTime = getDateDayFormat(-1);
queryParam.query_startTime = getDateDayFormat(-30);
queryParam.target = "order";
queryParam.query_type = "";

/**
 * 选择时间段
 */
function dateLengthChange(obj, dateType){
	$("#dateLength").text($(obj).html());
	queryParam.query_endTime = getDateDayFormat(-1);
	queryParam.query_startTime = getDateDayFormat(0 - dateType);
	reloadEchart();
}


/**
 * 选择数据来源
 */
function dataFromChange(obj, dataFrom){
    $("#dataFrom").text($(obj).html());
    queryParam.query_type = dataFrom;
    reloadEchart();
}

function selectTarget(obj, target){
	$(obj).parent().siblings('.active').removeClass('active');
    $(obj).parent().addClass('active');
	queryParam.target = target;
	if("order" == target){
		$("#dataFromView").show();
	}else{
		$("#dataFromView").hide();
		queryParam.query_type = "";
	}
	reloadEchart();
}

//初始化时间选择控件
$("#query_date").daterangepicker({
    "startDate": queryParam.query_startTime,
    "endDate": queryParam.query_endTime
        },function(start, end, label) {
        	queryParam.query_endTime = end.format("YYYY-MM-DD");
            queryParam.query_startTime = start.format("YYYY-MM-DD");
            reloadEchart();
            });

// 初始化echarts实例
var myChart = echarts.init(document.getElementById("echart-panel"),"macarons");

function reloadEchart(){
	var url_ = AppConfig.ctx + "/statistic/queryStatistic.do";
    $.ajax({
        url: url_,
        type: "POST",
        data: queryParam,
        dataType: "json",
        success: function (result) {
        	//console.log("d");
            if (result.success) {
            	myChart.setOption(result.data);
            }
        },
        error : function() {
        }
    });
}

reloadEchart();
</script>