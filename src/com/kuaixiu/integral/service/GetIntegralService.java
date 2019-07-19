package com.kuaixiu.integral.service;

import com.common.base.service.BaseService;
import com.kuaixiu.clerk.entity.Clerk;
import com.kuaixiu.clerk.service.ClerkService;
import com.kuaixiu.integral.dao.GetIntegralMapper;
import com.kuaixiu.integral.entity.GetIntegral;
import com.kuaixiu.order.constant.OrderConstant;
import com.kuaixiu.order.entity.Order;
import com.kuaixiu.order.entity.OrderDetail;
import com.kuaixiu.order.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
* @author: anson
* @CreateDate: 2017年5月17日 下午4:50:11
* @version: V 1.0
* 
*/
@Service("getIntegralService")
public class GetIntegralService extends BaseService<GetIntegral>{
    @Autowired
	private GetIntegralMapper<GetIntegral> mapper;
	@Autowired
    private ClerkService clerkService;
	@Autowired
	private OrderDetailService detailService;
    public GetIntegralMapper<GetIntegral> getDao() {
		return mapper;
	}
    
    
    /**
     * 修改积分订单状态为不可现
     */
    public GetIntegral updateShow(GetIntegral g){
    	getDao().update(g);
    	return g;
    }
    
    /**
     * 通过订单号查询获得积分信息
     */
    public GetIntegral queryByOrder(String orderNo){
    	return getDao().queryByOrderNo(orderNo);
    }
    
    /**
     * 订单完成为对应店员增加积分
     */
    public void addIntegral(Order o){
    	  //如果该订单是店员创建，完成订单后则为该店员增加对应积分
        String clerkId=o.getClerkId();
    	  //获取该店员信息
        Clerk clerk=clerkService.queryById(clerkId);
        if(clerk!=null){
        	//获取该订单的维修项目
        	OrderDetail t=new OrderDetail();
        	t.setOrderNo(o.getOrderNo());
        	t.setType(1);
        	List<OrderDetail> list=detailService.queryList(t);
        	int getIntegral=0;//用来表示本次获得的积分
        	
        	if(list.size()>0){
        	  if(list.size()>1){
              	//换屏加换电池加50积分
              	clerk.setIntegral(clerk.getIntegral()+OrderConstant.SCREEN_INTEGRAL);
              	clerkService.addIntegralById(clerk);
              	getIntegral=OrderConstant.SCREEN_INTEGRAL;
              }else if(list.get(0).getProjectId().equals("50a8d609-ecf9-11e6-93f3-00163e04c890")){
              	//换电池得20积分
              	clerk.setIntegral(clerk.getIntegral()+OrderConstant.BATTERY_INTEGRAL);
              	clerkService.addIntegralById(clerk);
              	getIntegral=OrderConstant.BATTERY_INTEGRAL;
              }else{
              	//换屏得50积分
              	clerk.setIntegral(clerk.getIntegral()+OrderConstant.SCREEN_INTEGRAL);
              	clerkService.addIntegralById(clerk);
              	getIntegral=OrderConstant.SCREEN_INTEGRAL;
              }
             	  String IntegralOrderNo=o.getOrderNo();//存储订单号到积分表中
                  String IntegralOrderId=o.getId();//储存订单id
                  // 增加积分获得记录
       	        GetIntegral g=new GetIntegral();
     	        g.setOrderNo(IntegralOrderNo);
               	g.setOrderId(IntegralOrderId);
              	g.setId(UUID.randomUUID().toString());
              	g.setClerkId(clerk.getId());
              	g.setName(clerk.getName());
               	g.setTel(clerk.getTel());
               	g.setIntegral(getIntegral);
              	getDao().add(g);
        	 }
           
        }
    }
  
    
    
    
}
