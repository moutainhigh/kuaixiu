package com.kuaixiu.oldtonew.controller;

import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.exception.SystemException;
import com.common.paginate.Page;
import com.common.util.SmsSendUtil;
import com.google.common.collect.Maps;
import com.kuaixiu.customer.entity.Customer;
import com.kuaixiu.oldtonew.entity.NewOrder;
import com.kuaixiu.oldtonew.entity.OldToNewUser;
import com.kuaixiu.oldtonew.service.NewOrderService;
import com.kuaixiu.oldtonew.service.OldToNewService;
import com.kuaixiu.order.constant.OrderConstant;
import com.system.api.entity.ResultData;
import com.system.basic.address.entity.Address;
import com.system.basic.address.service.AddressService;
import com.system.basic.user.entity.SessionUser;
import com.system.basic.user.service.SessionUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.UUID;
@Controller
public class OldToNewController extends BaseController{
    
	@Autowired
	private AddressService addressService;
    @Autowired
    private OldToNewService oldToNewService;
    @Autowired
    private NewOrderService newOrderService;
    @Autowired
    private SessionUserService sessionUserService;
	/**
     * 以旧换新信息填写页面
     */
    @RequestMapping("webpc/activity/MobileChange")
    public String change(){
    	
    	return "pc/oldToNew";
    }
    
    /**
     * 手机端活动以旧换新进入页面
     *   kuaixiu/oldToNew.jsp
     */
    
    /**
     * 提交成功页面
     * @return
     */
    @RequestMapping("webpc/activity/success")
    public String success(){
    	
    	return "oldToNew/success";
    }
    
    /**
     * 以旧换新添加信息
     */
    @RequestMapping("webpc/activity/addNews")
    public void addNews(HttpServletRequest request,HttpServletResponse response) throws Exception{
    	ResultData result = new ResultData();
    	try {
    		    JSONObject params=getPrarms(request);
    	        String name =params.getString("name");                          //获取用户名
    	        String tel =params.getString("tel");                            //获取手机号
                String oldMobile=params.getString("oldMobile");                 //旧机型
    	        String code =params.getString("code");                          //获取验证码
    	        String province =params.getString("province");                  //获取省份id
    	        String city =params.getString("city");                          //获取城市id
    	        String county=params.getString("county");                       //获取区id
    	        String street =params.getString("street");                      //获取街道
    	        String selectType =params.getString("selectType");              //获取兑换类型
    	     	String fm =params.getString("fm");                              //系统来源
    	     	//验证手机号和验证码
    	         if(!checkRandomCode(request,tel,code)){
    	            throw new SystemException("手机号或验证码输入错误");
    	         }
    	         Address provinceName=addressService.queryByAreaId(province);
     	         Address cityName=addressService.queryByAreaId(city);
     	         Address areaName=addressService.queryByAreaId(county);
     	         if(provinceName==null||cityName==null||areaName==null){
    	        	throw new SystemException("请选择正确的地址");
    	         }
     	         if(Integer.parseInt(selectType)!=0&&Integer.parseInt(selectType)!=1){
     	        	throw new SystemException("请选择正确的兑换类型");
     	         }
     	         String address=provinceName.getArea()+" "+cityName.getArea()+" "+areaName.getArea();
    	     	 String id= UUID.randomUUID().toString();
    	     	 String newMobile="";//新需求去除了原先需要用户填写的新机型
    	     	 OldToNewUser user=new OldToNewUser(name,tel,code,oldMobile,newMobile,address+" "+street);
    	     	 
    	     	 user.setId(id);
    	     	 user.setProvince(province);
    	     	 user.setCity(city);
    	     	 user.setCounty(county);
    	     	 user.setAreas(street);
    	     	 //添加对应信息至customer表中,派单时统一调用
    	         Customer cust = new Customer();
    	         cust.setName(name);
    	         cust.setMobile(tel);
    	         cust.setProvince(province);
    	         cust.setCity(city);
    	         cust.setCounty(county);
    	         cust.setStreet("0");
    	         cust.setAreas(street);
    	         cust.setAddress(address);
    	         
    	         
    	         //根据客户填写要求派单给满足条件的门店
    	         NewOrder o=new NewOrder();
    	         o.setOrderStatus(OrderConstant.ORDER_STATUS_DEPOSITED);
    	         o.setConvertType(0);// 0 上门兑换 
    	         o.setUserId(id);
    	         o.setSelectType(Integer.parseInt(selectType));
    	         o.setFromSystem(fm);
    	         //保存以旧换新兑换订单
    	         newOrderService.save(o,cust);
    	         //添加用户信息至对应表中
    	     	 oldToNewService.addUser(user);
    	 	     //支付完成自动派单
    	 	     newOrderService.autoDispatch(o);
    	 	     //下单成功清除验证码
    	 	     removeRandomCode(request, tel);                      
    	         OldToNewUser old=oldToNewService.queryById(id);
    	     	//向用户发送预约信息
    	     	 SmsSendUtil.sendSmsToOldToNew(tel,old);
    	     	 result.setSuccess(true);
    	     	 result.setResultCode("0");
    	 }catch(SystemException e){
             sessionUserService.getSystemException(e, result);
         } catch (Exception e) {
             e.printStackTrace();
             sessionUserService.getException(result);
         }
        renderJson(response,result);
    }
    
  
    
    /**
     * 列表查询
     */
    @RequestMapping("webpc/activity/list")
    public String list(HttpServletRequest request) throws Exception{
    	  //获取省份地址
        List<Address> provinceL = addressService.queryByPid("0");
        request.setAttribute("provinceL", provinceL);
    	return "oldToNew/list";
    }
    
    /**
     * 信息查询显示
     */
    @RequestMapping("webpc/activity/queryListForPage")
    public void queryListForPage(HttpServletRequest request,HttpServletResponse response) throws Exception{
    	String tel=request.getParameter("query_name");
    	String queryStartTime = request.getParameter("query_startTime");
        String queryEndTime = request.getParameter("query_endTime");
        String province = request.getParameter("queryProvince");
        String city = request.getParameter("queryCity");
        String county = request.getParameter("queryCounty");
    	OldToNewUser user=new OldToNewUser();
    	user.setTel(tel);
    	user.setQueryStartTime(queryStartTime);
    	user.setQueryEndTime(queryEndTime);
    	user.setProvince(province);
    	user.setCity(city);
    	user.setCounty(county);
    	Page page = getPageByRequest(request);
    	user.setPage(page);
    	//查询以旧换新列表带分页
    	List<OldToNewUser> list=oldToNewService.queryListForPage(user);
    	page.setData(list);
    	this.renderJson(response, page);
    	
    }
    /**
     * 进入编辑信息页面
     */
    @RequestMapping("webpc/activity/edit")
    public String edit(HttpServletRequest request,HttpServletResponse response) throws Exception{
    	//获取登录用户
    	SessionUser su=getCurrentUser(request);
    	String id=request.getParameter("id");
    	//取得用户信息
    	OldToNewUser user=oldToNewService.queryById(id);
    	 //获取省份地址
        List<Address> provinceL = addressService.queryByPid("0");
        //获取市地址
        List<Address> cityL = addressService.queryByPid(user.getProvince());
        //获取区县地址
        List<Address> countyL = addressService.queryByPid(user.getCity());
        request.setAttribute("provinceL", provinceL);
        request.setAttribute("cityL", cityL);
        request.setAttribute("countyL", countyL);
        request.setAttribute("user", user);    	
    	
    	return "oldToNew/editOldToNew";
    }
    
    /**
     * 删除信息
     */
    @RequestMapping("webpc/activity/delete")
    public void delete(HttpServletRequest request,HttpServletResponse response) throws Exception{
        Map<String,Object> resultMap=Maps.newHashMap();
        //获取需要删除的id
        String id=request.getParameter("id");
        //获取当前登录用户信息
        SessionUser su=getCurrentUser(request);
        oldToNewService.deleteById(id, su);
        
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
        resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
        renderJson(response, resultMap);
    }
    
    /**
     * 编辑页面信息提交
     */
    @RequestMapping("webpc/activity/update")
    public void update(HttpServletRequest request,HttpServletResponse response) throws Exception{
        Map<String,Object> resultMap=Maps.newHashMap();
    	//获取登录用户信息
        SessionUser su=getCurrentUser(request);
    	//获取编辑页面传来的数据
        String id=request.getParameter("id");
        String name=request.getParameter("name");
     //   String tel=request.getParameter("tel");
        String oldMobile=request.getParameter("oldMobile");
        String newMobile=request.getParameter("newMobile");
        String province = request.getParameter("addProvince");
        String city = request.getParameter("addCity");
        String county = request.getParameter("addCounty");
        String areas=request.getParameter("addAddress");//街道、小区
        String address= request.getParameter("addAreas");//省市县
        String homeAddress=address+" "+areas;//完成地址
        OldToNewUser user=oldToNewService.queryById(id);
        user.setName(name);
     //   user.setTel(tel);
        user.setOldMobile(oldMobile);
        user.setNewMobile(newMobile);
        user.setProvince(province);
        user.setCity(city);
        user.setCounty(county);
        user.setAreas(areas);
        user.setHomeAddress(homeAddress);
        //更新数据
        oldToNewService.update(user, su);
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
        resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
        renderJson(response, resultMap);
    }
    
    
   
}
