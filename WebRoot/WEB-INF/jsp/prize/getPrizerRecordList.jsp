<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="/commons/taglibs.jsp"%>
<div class="am-cf am-padding am-padding-bottom-0">
    <div class="am-fl am-cf" style="width: 100%;">
        <strong class="am-text-primary am-text-lg">奖品管理</strong> / <small>抽奖列表</small>
    </div>
</div>

<hr>

<div class="am-g">
    <form id="searchForm" class="form form-horizontal">
        <table id="searchTable">
            <tr>
                <td class="search_th"><label class="control-label">抽奖手机号
                    ：</label></td>
                <td class="search_td"><input type="text" name="query_mobile"
                                             class="form-control"></td>

                <td class="search_th"><label class="control-label">奖品批次
                    ：</label></td>
                <td class="search_td"><input type="text" name="query_batch"
                                             class="form-control"></td>

            </tr>
            <tr>
                <td class="search_th search_th_frist"><label
                        class="control-label">&nbsp&nbsp&nbsp奖品等级：</label></td>
                <td class="search_td"><select name="query_grade"
                                              class="form-control">
                    <option value="">--奖品等级--</option>
                    <option value="1">一等奖</option>
                    <option value="2">二等奖</option>
                    <option value="3">三等奖</option>
                    <option value="4">四等奖</option>
                    <option value="5">五等奖</option>
                    <option value="6">六等奖</option>
                </select></td>

                <td class="search_th search_th_frist"><label
                        class="control-label">&nbsp&nbsp&nbsp是否中奖：</label></td>
                <td class="search_td"><select name="query_isGet"
                                              class="form-control">
                    <option value="">--选择--</option>
                    <option value="0">未中奖</option>
                    <option value="1">中奖</option>
                </select></td>
            </tr>
        </table>


        <div class="form-group">
            <div class="am-u-sm-12 am-u-md-6">
                <div class="am-btn-toolbar">
                    <div class="am-btn-group am-btn-group-sm m20">
                        <button onclick="refreshPage();" class="am-btn am-btn-default search_btn" type="button">搜 索</button>
                    </div>
                </div>
            </div>
        </div>
    </form>
</div>


<div class="am-g">
    <div class="am-u-sm-12">
        <table id="dt"
               class="table table-striped table-bordered table-radius table-hover">
            <thead>
            <tr>
                <th class="fontWeight_normal tdwidth50"><input id="check_all_btn" onclick="checkAll(this)" type="checkbox" />序号</th>
                <th class="fontWeight_normal tdwidth110 center">手机号</th>
                <th class="fontWeight_normal tdwidth150  center">奖品id</th>
                <th class="fontWeight_normal tdwidth110  center">奖品等级</th>
                <th class="fontWeight_normal tdwidth70 center">是否中奖</th>
                <th class="fontWeight_normal tdwidth140  center">创建时间</th>
                <th class="fontWeight_normal tdwidth70  center">奖品批次</th>
            </tr>
            </thead>
            <tbody>

            </tbody>
        </table>
    </div>
</div>



<!-- 修改弹窗 end -->
<script type="text/javascript">


    //自定义datatable的数据
    var dto = new DtOptions();
    //设置数据刷新路径
    dto.ajax = {
        "url" : "${ctx}/recycle/prizeRecord/queryListForPage.do",
        "data" : function(d) {
            //将表单中的查询条件追加到请求参数中
            var array = $("#searchForm").serializeArray();
            $.each(array, function() {
                d[this.name] = this.value;
            });
        }
    };

    //设置数据列
    dto.setColumns([
        {"data": "id","class":"tdwidth50 center"},
        {"data": "mobile","class":""},
        {"data": "prizeId","class":""},
        {"data": "grade","class":""},
        {"data": "isGet","class":"am-hide-sm-only"},
        {"data": "inTime","class":""},
        {"data": "batch","class":"am-hide-sm-only"}
    ]);


    //设置定义列的初始属性
    dto.setColumnDefs([ {//复选框
        targets : 0,
        render : function(data, type, row, meta) {
            var context = {
                func : [ {
                    "id" : row.id,
                    "order" : meta.row + 1
                } ]
            };
            var html = template_chk(context);
            return html;
        }
    },

        {
            targets: -4,
            render: function (data, type, row, meta) {
                var state='';
                switch(row.grade){
                    case 1:
                        state = "一等奖";
                        break;
                    case 2:
                        state = "二等奖";
                        break;
                    case 3:
                        state = "三等奖";
                        break;
                    case 4:
                        state = "四等奖";
                        break;
                    case 5:
                        state = "五等奖";
                        break;
                    case 6:
                        state = "六等奖";
                        break;
                    default:
                        state = "";
                }
                return state;
            }
        },

        {
            targets: -3,
            render: function (data, type, row, meta) {
                var state='';
                switch(row.isGet){
                    case 0:
                        state = "未中奖";
                        break;
                    case 1:
                        state = "中奖";
                        break;
                    default:
                        state = "";
                }
                return state;

            }
        }
    ]);
    var myTable = $("#dt").DataTable(dto);

    /**
     * 刷新列表
     */
    function refreshPage() {
        $("#pageStatus").val(1);
        myTable.ajax.reload(null, false);
    }



    function addBtnClick() {
        $("#modal-insertView").modal("show");
    }


    function checkAll(obj){
        $("input[name='item_check_btn']").each(function(){
            $(this).prop("checked", obj.checked);
        });
    }


    function editBtnClick(prizeId){
        $("#modal-updateView").html("");
        $("#modal-updateView").load("${ctx}/recycle/prize/edit.do?prizeId="+prizeId, function(){
            func_after_model_load(this);
        });
    }




</script>