/** jquery.dataTables插件定制内容 */

	var language_zh= {
				"processing": "数据加载中...",
				"lengthMenu": "显示 _MENU_ 条结果",
				"zeroRecords": "没有匹配结果",
				"info": "显示第 _START_ 至 _END_ 条结果，共 _TOTAL_ 条",
				"infoEmpty": "显示第 0 至 0 条结果，共 0 项",
				"infoFiltered": "(由 _MAX_ 条结果过滤)",
				"infoPostFix": "",
				"url": "",
				"paginate": {
					"first":    "首页",
					"previous": "上一页",
					"next":     "下一页",
					"last":     "末页"
				}
	};
			
	function DtOptions(){
		/**
		 * 设置数据列
		 */
		this.setColumns = function(_columns){
			this.columns=_columns;		 
		}
		/**
		 * 显示布局
		 * 1. `l` 代表 length，左上角的改变每页显示条数控件
		 * 2. `f` 代表 filtering，右上角的过滤搜索框控件
		 * 3. `t` 代表 table，表格本身
		 * 4. `i` 代表 info，左下角的表格信息显示控件
		 * 5. `p` 代表 pagination，右下角的分页控件
		 * 6. `r` 代表 processing，表格中间的数据加载等待提示框控件
		 * 7. `B` 代表 button，Datatables可以提供的按钮控件，默认显示在左上角
		 */
		this.dom='<"top">rt<"bottom"lip><"clear">';
		this.setDom = function(_dom){
			this.dom=_dom;		 
		}
		/**
		 * 是否开启本地分页
		 */
		this.paging=true;
		this.setPaging = function(_paging){
			this.paging=_paging;
			this.info=_paging;
		}
		/**
		 * 控制是否显示表格左下角的信息
		 */
		this.info=true;
		this.setInfo = function(_info){
			this.info=_info;
		}
		/**
		 * 设置定义列的初始属性
		 */
		this.setColumnDefs = function(_columnDefs){
			this.columnDefs=_columnDefs;		 
		}
		
		
		this.sScrollX=true;
		/**
		 * 设置水平滚动
		 */
		this.setSScrollX = function(_sScrollX){
			this.sScrollX=_sScrollX;		 
		}
		
		this.sScrollXInner="100%";
		/**
		 * 表格的内容宽度
		 */
		this.setSScrollXInner = function(_sScrollXInner){
			this.sScrollXInner=_sScrollXInner;		 
		}
		
		this.bScrollCollapse=true;
		/**
		 * 当显示的数据不足以支撑表格的默认的高度时，依然显示纵向的滚动条。(默认是false)
		 */
		this.setBScrollCollapse = function(_bScrollCollapse){
			this.bScrollCollapse=_bScrollCollapse;		 
		}
		
		//"sLoadingRecords": "正在加载数据...",
		this.sLoadingRecords="正在加载数据...";
		//"sZeroRecords": "暂无数据",
		this.sZeroRecords="暂无数据";
		
		this.bAutoWidth=false;
		/**
		 * 自动宽度
		 */
		this.setBAutoWidth = function(_bAutoWidth){
			this.bAutoWidth=_bAutoWidth;		 
		}
		
		this.bSort=false;
		/**
		 * 排序功能
		 */
		this.setBSort = function(_bSort){
			this.bSort=_bSort;		 
		}
		
		this.lengthChange = false;
		/**
		 * 是否允许用户改变表格每页显示的记录数
		 */
		this.setLengthChange = function(_lengthChange){
			this.lengthChange = _lengthChange;
		}
		
		this.iDisplayLength=10;
		/**
		 * 设置每页显示条数
		 */	
		this.setIDisplayLength = function(_iDisplayLength){
			this.iDisplayLength = _iDisplayLength;
		}
		
		//"bProcessing": true,
		this.bProcessing=true;
		this.bServerSide=true;
		/**
		 * 是否启动服务器端数据导入，即要和sAjaxSource结合使用
		 */
		this.setBServerSide = function(_bServerSide){
			this.bServerSide=_bServerSide;		 
		}
		
		this.pagingType="simple_numbers";
		/**
		 * 分页按钮的显示方式
		 * numbers - Page number buttons only (1.10.8)
		 * simple - 'Previous' and 'Next' buttons only
		 * simple_numbers - 'Previous' and 'Next' buttons, plus page numbers
		 * full - 'First', 'Previous', 'Next' and 'Last' buttons
		 * full_numbers - 'First', 'Previous', 'Next' and 'Last' buttons, plus page numbers
		 */
		this.setPagingType = function(_pagingType){
			this.pagingType=_pagingType;		 
		}

		// 首次加载完成回调,为表单动态添加一个隐藏域值默认为0  当使用指定内容搜索时，请求时会将该值改为1
		// 获取数据成功后下面的刷新回调会将其在次改为0
		this.fnInitComplete=function(){
			$("#searchForm").append("<input type='hidden' id='pageStatus' name='pageStatus' value='0'>");
			$(".dataTables_scrollHeadInner > table").css("width", this.sScrollXInner);
		}
		// 刷新回调
		this.fnDrawCallback=function(){
			$("#pageStatus").val(0);
			$(".dataTables_scrollHeadInner > table").css("width", this.sScrollXInner);
		}
	}
	
	DtOptions.prototype.language = language_zh;

		