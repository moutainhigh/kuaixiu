package com.kuaixiu.apiService;

import java.math.BigDecimal;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.common.exception.ApiServiceException;
import com.common.exception.SystemException;
import com.kuaixiu.engineer.entity.Engineer;
import com.kuaixiu.engineer.service.EngineerService;
import com.kuaixiu.model.entity.NewModel;
import com.kuaixiu.model.service.NewModelService;
import com.kuaixiu.oldtonew.entity.Agreed;
import com.kuaixiu.oldtonew.entity.NewOrder;
import com.kuaixiu.oldtonew.entity.OldToNewUser;
import com.kuaixiu.oldtonew.service.AgreedService;
import com.kuaixiu.oldtonew.service.NewOrderService;
import com.kuaixiu.oldtonew.service.OldToNewService;
import com.kuaixiu.order.constant.OrderConstant;
import com.kuaixiu.order.entity.Order;
import com.kuaixiu.order.service.OrderService;
import com.kuaixiu.shop.entity.Shop;
import com.kuaixiu.shop.service.ShopService;
import com.system.api.ApiServiceInf;
import com.system.basic.user.entity.SessionUser;
import com.system.constant.ApiResultConstant;
import com.system.constant.SystemConstant;

import jodd.util.StringUtil;


/**
 * 更新订单状态操作接口实现类
 * 保存以旧换新预约信息
 * @author wugl
 *
 */
@Service("newOrderAgreedService")
public class NewOrderAgreedService implements ApiServiceInf {
    private static final Logger log= Logger.getLogger(NewOrderAgreedService.class);
    
    @Autowired
    private OrderService orderService;
    @Autowired
    private EngineerService engService;
    @Autowired
    private NewOrderService newOrderService;
    @Autowired
    private ShopService shopService;
    @Autowired
    private OldToNewService oldToNewService;
    @Autowired
    private AgreedService agreedService;
    @Autowired
    private NewModelService newModelService;
    @Override
    @Transactional
    public Object process(Map<String, String> params) {
        
        //解析请求参数
        String paramJson = MapUtils.getString(params, "params");
        JSONObject pmJson = JSONObject.parseObject(paramJson);
    
        //验证请求参数
        if (pmJson == null 
                || !pmJson.containsKey("choose")
                || !pmJson.containsKey("agreedTime")
                || !pmJson.containsKey("orderNo")
        		) {
            throw new ApiServiceException(ApiResultConstant.resultCode_1001, ApiResultConstant.resultCode_str_1001);
        }
        String agreedTime=pmJson.getString("agreedTime");
        //获取填写类型  0表示选中的是库中已有手机类型  值为1表示为工程师手动输入的其他机型
        String choose = pmJson.getString("choose");
        String orderNo= pmJson.getString("orderNo");
        
        //查询以旧换新信息
        NewOrder newOrder=(NewOrder) newOrderService.queryByOrderNo(orderNo);
        String engNumber = MapUtils.getString(params, "pmClientId");
        if (newOrder==null) {
            throw new ApiServiceException(ApiResultConstant.resultCode_1003, "订单信息未找到");
        }
        	 Engineer engineer=engService.queryById(newOrder.getEngineerId());
        	 if(!engNumber.equals(engineer.getNumber())){
                 throw new ApiServiceException(ApiResultConstant.resultCode_3002, "对不起，您无权操作该订单");
             }

             if(newOrder.getOrderStatus() == OrderConstant.ORDER_STATUS_CANCEL){
                 throw new ApiServiceException(ApiResultConstant.resultCode_3002, "该订单已取消！");
             }
             
             if(newOrder.getOrderStatus() == OrderConstant.ALREADY_AGREED){
                 throw new ApiServiceException(ApiResultConstant.resultCode_3002, "该订单已确认，无需重复操作！");
             }
             //维修工程师改为空闲状态
             Engineer eng = engService.queryByEngineerNumber(engNumber);
             SessionUser su = new SessionUser();
             su.setUserId(eng.getNumber());
             su.setUserName(eng.getName());
             su.setType(SystemConstant.USER_TYPE_ENGINEER);
             
        String modelId=null;//机型id
        String agreedColor=null;//表示选中颜色
        String agreedPrice=null;//表示机型价格
        String agreedOther=null;//表示其他信息
        NewModel model=null;//表示选中机型
        if(choose.equals("0")){
         modelId=pmJson.getString("modelId");
       	 model=newModelService.queryById(modelId);
       	 agreedColor=pmJson.getString("agreedColor");
       }else{
            agreedPrice=pmJson.getString("otherPrice");
            agreedOther=pmJson.getString("otherModel");
       }
          
             //更新预约信息  包含预约时间 机型 价格 颜色 内存等
                  NewOrder o=new NewOrder();
                  Agreed agreed=new Agreed();
                  	agreed.setOrderNo(newOrder.getOrderNo());
                  	agreed.setEngineerId(newOrder.getEngineerId());
                  	 if(!StringUtil.isBlank(agreedTime)){
                           agreed.setAgreedTime(agreedTime);
                 	      }
                  	  if(!StringUtil.isBlank(agreedColor)){
                            agreed.setColor(agreedColor);
                  	  }
                  	  if(model!=null){
                  		  agreed.setAgreedBrand(model.getBrandName());
                  		  agreed.setColor(agreedColor);
                  		  agreed.setNewModelName(model.getName());
                  		  agreed.setMemory(model.getMemory());
                  		  agreed.setEdition(model.getEdition());
                  		  agreed.setAgreedOrderPrice(model.getPrice());
                  	  }else{
                  		  if(!StringUtil.isBlank(agreedPrice)){
                      		  BigDecimal b=new BigDecimal(agreedPrice);
                                agreed.setAgreedOrderPrice(b);
                            }
                      	  if(!StringUtil.isBlank(agreedOther)){
                                agreed.setOther(agreedOther);
                            }
                  	  }
                  	  
                if(newOrder.getOrderStatus() == OrderConstant.WAIT_AGREED){
                	newOrder.setOrderStatus(OrderConstant.ALREADY_AGREED);
                	newOrder.setUpdateUserid(su.getUserId());
     	            //用户支付金额按照工程师手动输入的价格为准
                	newOrder.setRealPrice(agreed.getAgreedOrderPrice());
     	            newOrderService.saveUpdate(newOrder);
     	        }
     	        else{
     	        	throw new SystemException("该订单无法预约维修时间！");
     	        }
     	        //保存预约信息
     	        agreedService.saveAgreedNews(agreed);
        return "OK";
    }
}
