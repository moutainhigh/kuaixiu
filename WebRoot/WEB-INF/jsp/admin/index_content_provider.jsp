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
          <h3>名称：${provider.name }</h3>
		  <h3>联系电话：${provider.tel }</h3>
		  <h3>地址：${provider.fullAddress }</h3>
        </div>
        
      </div>

      
      <div class="am-g mt20">
        <div class="panel panel-success index-panel-msg">
          <div class="row">
            <!-- /.col -->
            <div class="col-md-4 col-sm-6 col-xs-12">
              <div class="info-box">
                <span class="info-box-icon bg-red"><i class="am-icon-rmb"></i></span>
    
                <div class="info-box-content">
                  <span class="info-box-text">总交易金额（元）</span>
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
                  <span class="info-box-text">总订单数</span>
                  <span class="info-box-number">${orderAllCount }</span>
                </div>
                <!-- /.info-box-content -->
              </div>
              <!-- /.info-box -->
            </div>
            <!-- /.col -->
            <div class="col-md-4 col-sm-6 col-xs-12">
              <div class="info-box">
                <span class="info-box-icon bg-yellow"><i class="am-icon-bank"></i></span>
    
                <div class="info-box-content">
                  <span class="info-box-text">营业点</span>
                  <span class="info-box-number">${shopCount }</span>
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
                <span class="info-box-icon bg-teal"><i class="am-icon-rmb"></i></span>
    
                <div class="info-box-content">
                  <span class="info-box-text">周交易金额（元）</span>
                  <span class="info-box-number"><fmt:formatNumber value="${orderWeekAmount }" pattern="0.00" maxFractionDigits='2'/></span>
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
                <span class="info-box-icon bg-olive"><i class="am-icon-line-chart"></i></span>
    
                <div class="info-box-content">
                  <span class="info-box-text">周订单数</span>
                  <span class="info-box-number">${orderWeekCount }</span>
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
                  <span class="info-box-text">工程师人数</span>
                  <span class="info-box-number">${engCount }</span>
                </div>
                <!-- /.info-box-content -->
              </div>
              <!-- /.info-box -->
            </div>
            <!-- /.col -->
          </div><!-- /.row -->
        </div><!-- /.panel -->
        
      </div><!-- /.am-g -->