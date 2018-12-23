<%@page pageEncoding="UTF-8"%>
<script src="${webResourceUrl}/resource/wap/oldToNew/js/jQuery 1.7.1.js"></script>
<script src="${webResourceUrl}/plugins/mui/js/mui.min.js"></script>  
<script type="text/javascript">

    //绝对路径
    function AppConfig() { 
     }
    AppConfig.ctx = "${ctx}";
    /**
     * 提示方法
     */
    function alertTip(msg, callback){
    	if(!msg){
    		return;
    	}
        $(".remind_delivery_bg").show();
        $(".remind_delivery_cont p").text(msg);
        window.setTimeout(function () {
            $(".remind_delivery_bg").fadeOut();
            if (callback) {
                callback();
            }
        }, 2000);
    }
    
    /**
     * 确认提示
     */
    function confirmTip(title, msg, callback){
    	if(!msg){
            return;
        }
    	if(!title){
    		title = "系统提示";
    	}
        $(".popup_content .popup_title").text(title);
        $(".popup_content .popup_font").text(msg);
        $(".popup_but .cancel").show();
        $(".popup_bg, .popup_content").show();
    	//点击确认
        $(".popup_but .confirm").unbind("click").click(function(){
            $(".popup_bg, .popup_content").hide();
            if (callback) {
                callback();
            }
        });
    	//点击取消
        $(".popup_but .cancel").click(function(){
            $(".popup_bg, .popup_content").hide();
          
        });
    }
    
    /**
     * 带确认按钮提示框
     */
    function realAlert(title, msg, callback){
        if(!msg){
            return;
        }
        if(!title){
            title = "系统提示";
        }
        $(".popup_content .popup_title").text(title);
        $(".popup_content .popup_font").text(msg);
        //隐藏取消按钮
        $(".popup_but .cancel").hide();
        $(".popup_bg, .popup_content").show();
        //点击确认
        $(".popup_but .confirm").unbind("click").click(function(){
            $(".popup_bg, .popup_content").hide();
            if (callback) {
                callback();
            }
        });
    }
    
    //关闭弹出框
    $(".popup_but .cancel,.popup_content .popup_exit").click(function(){
        $(".popup_bg, .popup_content").hide();   
    });
    
    /*
    * 数据格式化
    * @param val 要格式化的数据
    * @param n 格式化后的小数位数
    */
    function dataFormat(val, n) {
    	if (isNaN(val)) return "";
    	if (n == undefined || n == null || isNaN(n)) n = 2;
     
    	return new Number(val).toFixed(n);
    }
    
    
    function fn_select_address(level,pid,current,profix){
    	  var url =AppConfig.ctx+"/address/getArea.do";
    	  $.post(url, {pid: pid}, function(result){
    	  	if(!result.success){
    	  		return false;
    	  	}
    	  	var json = result.data;
    	      //地市级   
    	      if(level==2){
    	          $("#"+profix+"City option[value!='']").remove();
    	          $("#"+profix+"County option[value!='']").remove();
    	          $("#"+profix+"City").append(packageAddress(json,current));
    	      }
    	      //区县级
    	      if(level==3)
    	      {
    	          $("#"+profix+"County option[value!='']").remove();
    	          $("#"+profix+"County").append(packageAddress(json,current));
    	      }
    	      //区县级
    	      if(level==4)
    	      {
    	          $("#"+profix+"Street option[value!='']").remove();
    	          $("#"+profix+"Street").append(packageAddress(json,current));
    	      }
    	  });
    	}
    
    /**
    * 封装地址
    * @param json
    * @param current
    * @returns {String}
    */
    function packageAddress(json,current){  
      var select_html = '';
      if(json.length>0){
          for( a in json)
          {
              if(current == json[a]['areaId'])
                  select_html +='<option value="'+json[a]['areaId']+'" selected="selected">'+json[a]['area']+'</option>';
              else
                  select_html +='<option value="'+json[a]['areaId']+'">'+json[a]['area']+'</option>';
          }
      }
      return select_html;
    }


    //地址信息
    function getArea(id,name){
    	var area = new Object();
    	area.areaId = id;
    	area.area = name;
    	return area;
    }

    var privinceData = new Array();
    privinceData.push(getArea("1","北京"));
    privinceData.push(getArea("2","上海"));
    privinceData.push(getArea("3","天津"));
    privinceData.push(getArea("4","重庆"));
    privinceData.push(getArea("5","河北"));
    privinceData.push(getArea("6","山西"));
    privinceData.push(getArea("7","河南"));
    privinceData.push(getArea("8","辽宁"));
    privinceData.push(getArea("9","吉林"));
    privinceData.push(getArea("10","黑龙江"));
    privinceData.push(getArea("11","内蒙古"));
    privinceData.push(getArea("12","江苏"));
    privinceData.push(getArea("13","山东"));
    privinceData.push(getArea("14","安徽"));
    privinceData.push(getArea("15","浙江"));
    privinceData.push(getArea("16","福建"));
    privinceData.push(getArea("17","湖北"));
    privinceData.push(getArea("18","湖南"));
    privinceData.push(getArea("19","广东"));
    privinceData.push(getArea("20","广西"));
    privinceData.push(getArea("21","江西"));
    privinceData.push(getArea("22","四川"));
    privinceData.push(getArea("23","海南"));
    privinceData.push(getArea("24","贵州"));
    privinceData.push(getArea("25","云南"));
    privinceData.push(getArea("26","西藏"));
    privinceData.push(getArea("27","陕西"));
    privinceData.push(getArea("28","甘肃"));
    privinceData.push(getArea("29","青海"));
    privinceData.push(getArea("30","宁夏"));
    privinceData.push(getArea("31","新疆"));
    privinceData.push(getArea("32","台湾"));
    privinceData.push(getArea("42","香港"));
    privinceData.push(getArea("43","澳门"));
    privinceData.push(getArea("84","钓鱼岛"));
    
    //验证身份证号
    function IdentityCodeValid(code) {
	var city={11:"北京",12:"天津",13:"河北",14:"山西",15:"内蒙古",21:"辽宁",22:"吉林",23:"黑龙江 ",31:"上海",32:"江苏",33:"浙江",34:"安徽",35:"福建",36:"江西",37:"山东",41:"河南",42:"湖北 ",43:"湖南",44:"广东",45:"广西",46:"海南",50:"重庆",51:"四川",52:"贵州",53:"云南",54:"西藏 ",61:"陕西",62:"甘肃",63:"青海",64:"宁夏",65:"新疆",71:"台湾",81:"香港",82:"澳门",91:"国外 "};
	var pass= true;

	if(!code || !/^\d{6}(18|19|20)?\d{2}(0[1-9]|1[12])(0[1-9]|[12]\d|3[01])\d{3}(\d|X)$/i.test(code)){

		pass = false;
	}

	else if(!city[code.substr(0,2)]){

		pass = false;
	}
	else{
		//18位身份证需要验证最后一位校验位
		if(code.length == 18){
			code = code.split('');
			//∑(ai×Wi)(mod 11)
			//加权因子
			var factor = [ 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 ];
			//校验位
			var parity = [ 1, 0, 'X', 9, 8, 7, 6, 5, 4, 3, 2 ];
			var sum = 0;
			var ai = 0;
			var wi = 0;
			for (var i = 0; i < 17; i++)
			{
				ai = code[i];
				wi = factor[i];
				sum += ai * wi;
			}
			var last = parity[sum % 11];
			if(parity[sum % 11] != code[17]){

				pass =false;
			}
		}
	}
	if(!pass) {
		alertTip("请输入正确的身份证号");	
	}
	return pass;
}
    
</script>

<!--错误提示-->
<div class="remind_delivery_bg">
     <div class="remind_delivery_cont"> 
          <p></p>
     </div>
</div>
<!--错误提示end-->
<!--确认提示-->
<div class="popup_bg" style="display: none;z-index: 1222;"></div>
<div class="popup_content" style="display: none;z-index: 1223;">
     <div class="popup_cont">
          <div class="popup_title"></div>
          <div class="popup_font"></div>
          <div class="popup_but">
               <a class="but cancel" style="position:relative;left:6px;"href="javascript:void(0);">取 消</a>
               <a class="but confirm" href="javascript:void(0);">确 认</a>
          </div>
          <a class="popup_exit" href="javascript:void(0);"></a>
     </div>
</div>
<!--确认提示end-->
<!--遮盖层-->
<div id="mask_boxs" style="display: none;height:100%;">
    <div id="masks"></div>
    <div id="mcon">
        <img src="${webResourceUrl}/resource/login/images/loadings.gif" style="width: 80px;height:80px;"/>
    </div>
</div>