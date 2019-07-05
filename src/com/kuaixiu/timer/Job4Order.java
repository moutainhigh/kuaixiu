package com.kuaixiu.timer;

import com.alibaba.fastjson.JSONObject;
import com.common.util.DateUtil;
import com.common.util.GlobalConstants;
import com.common.util.HttpClientUtil;
import com.common.util.SmsSendUtil;
import com.common.wechat.common.util.StringUtils;
import com.kuaixiu.card.entity.TelecomCard;
import com.kuaixiu.card.service.SuperToChannelService;
import com.kuaixiu.card.service.TelecomCardService;
import com.kuaixiu.customerService.service.CustServiceService;
import com.kuaixiu.engineer.entity.Engineer;
import com.kuaixiu.engineer.service.EngineerService;
import com.kuaixiu.oldtonew.entity.NewOrder;
import com.kuaixiu.oldtonew.service.NewOrderService;
import com.kuaixiu.order.entity.Order;
import com.kuaixiu.order.entity.OrderPayLog;
import com.kuaixiu.order.service.OrderPayLogService;
import com.kuaixiu.order.service.OrderPayService;
import com.kuaixiu.order.service.OrderService;
import com.kuaixiu.recycle.service.RecycleExternalTotalpriceService;
import com.kuaixiu.shop.entity.Shop;
import com.kuaixiu.shop.service.ShopService;
import com.system.basic.user.entity.SessionUser;
import com.system.constant.SystemConstant;
import com.system.util.MyRunnable;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 定时处理派单、订单确认.  定时处理
 * 
 * @CreateDate: 2016-9-15 下午5:38:21
 * @version: V 1.0
 */
public class Job4Order {

    @Autowired
    private OrderService orderService;
    @Autowired
    private NewOrderService newOrderService;
    @Autowired
    private EngineerService engineerService;
    @Autowired
    private ShopService shopService;
    @Autowired
    private CustServiceService custServiceService;
    @Autowired
    private OrderPayLogService payLogService;
    @Autowired
    private OrderPayService orderPayService;
    @Autowired
    private TelecomCardService telecomCardService;
    @Autowired
    private SuperToChannelService superToChannelService;
    @Autowired
    private RecycleExternalTotalpriceService recycleExternalTotalpriceService;
    
    private static final Logger log=Logger.getLogger(Job4Order.class);


    /**
     * 计算每天外部订单总额
     *
     * @CreateDate 2018年10月8日08:54:17
     */
    public void externalTotalprice(){
        recycleExternalTotalpriceService.add();
    }


    /**
     * 自动配单
     * 
     * @CreateDate: 2016-9-15 下午5:48:51
     */
    public void dealAutoDispatch(){
       // log.info("自动配单开始...");
        //查未配单的订单
        List<Order> orders = orderService.queryUnDispatch();
        //循环派单
        for(Order o : orders){
            orderService.autoDispatch(o);
        }
        //查未配单的以旧换新订单
        List<NewOrder> newOrders = newOrderService.queryUnDispatch();
        for(NewOrder o:newOrders){
        	newOrderService.autoDispatch(o);
        }
        
       // log.info("自动配单结束...");
    }
    
    /**
     * 对门店商超过15分钟没有派单的订单自动重新派单
     * @throws ParseException 
     * 
     * @CreateDate: 2016-9-15 下午5:48:51
     */
    public void dealReDispatch() throws ParseException{
    	//log.info("自动重新派单开始...");
        SessionUser su = new SessionUser();
        su.setUserId("admin");
        su.setUserName("admin");
        //查未派单的订单
        List<Order> orders = orderService.queryUnDispatchForShop();
        //循环派单
        for(Order o : orders){
            try{
                orderService.reDispatch(o.getId(), su);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        
        //查未派单的以旧换新订单
        List<NewOrder> newOrders=newOrderService.queryUnDispatchForShop();
        for(NewOrder o:newOrders){
        	try {
				newOrderService.reDispatch(o.getId(), su);
			} catch (Exception e) {
                 e.printStackTrace();
			}
        }
        
        
        
        //log.info("自动重新派单结束...");
    }



    //定时查询15分钟前后微信已提交订单
    public void dealPayLogOrder(){
        try{
            log.info("-------定时查询已提交订单开始--------");
//            OrderPayLog orderPayLog0=new OrderPayLog();
//            //查询15分钟之前的订单
//            orderPayLog0.setQueryEndTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(DateUtil.getDateAddMinute(-15)));
//            List<OrderPayLog> payLogsEnd=payLogService.queryPayLogSubmit(orderPayLog0);
//            if(!CollectionUtils.isEmpty(payLogsEnd)){
//                for(OrderPayLog payLog:payLogsEnd) {
//                    if(payLog.getPayMethod()==0) {
//                        orderPayService.closeWechatOrder(payLog);//微信
//                    }else if(payLog.getPayMethod()==1){
//                        orderPayService.closeAlipayOrder(payLog);//支付宝
//                    }
//                }
//            }else{
//                log.info("15分钟之前的订单为空");
//            }

            OrderPayLog orderPayLog=new OrderPayLog();
            //查询15分钟之内的订单
            orderPayLog.setQueryStartTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(DateUtil.getDateAddMinute(-15)));
            List<OrderPayLog> payLogsStart=payLogService.queryPayLogSubmit(orderPayLog);
            if(!CollectionUtils.isEmpty(payLogsStart)){
                for(OrderPayLog payLog:payLogsStart) {
                    if(payLog.getPayMethod()==0) {
                        orderPayService.orderPayLog(payLog);//微信
                    }else if(payLog.getPayMethod()==1){
                        orderPayService.aliPayOrder(payLog);//支付宝
                    }
                }
            }else{
                log.info("15分钟之内的订单为空");
            }
            log.info("-------定时查询已提交订单结束--------");
        }catch (Exception e){
            log.info("失败");
            e.printStackTrace();
        }
    }
   
    //订单预约短信提示任务
    public void dealAgreed() throws ParseException{
    	//log.info("自动发送提醒短信开始");
    	SessionUser su = new SessionUser();
        su.setUserId("admin");
        su.setUserName("admin");
        //超过30分钟还未预约的维修订单
        List<Order> orders = orderService.queryUnAgreedForShop(30,0);
        //对维修订单超过30分钟还未预约的，给对应工程师和门店商负责人发送一次信息
        for(Order o:orders){
        	o.setSendAgreedNews(1);
        	//给对应工程师发送消息
        	Engineer eng=engineerService.queryById(o.getEngineerId());
        	SmsSendUtil.sendAgreedSmsToEngineer(eng,o);
        	//给对应门店发送提醒消息
        	Shop s=shopService.queryByCode(o.getShopCode());
        	SmsSendUtil.sendAgreedSmsToShop(s,eng, o);
        	orderService.saveUpdate(o);
        }
        
        //对维修订单超过两小时还未预约的，给客服发送一次信息
        List<Order> agreedOrders = orderService.queryUnAgreedForShop(120,1);
        for(Order o:agreedOrders){
        	o.setSendAgreedNews(2);
        	//给客服发送提醒短信
        	Engineer eng=engineerService.queryById(o.getEngineerId());
        	Shop s=shopService.queryByCode(o.getShopCode());
        	//CustService cust=custServiceService.queryByCustNumber("kf003");
            String mobile="18668163070";
        	SmsSendUtil.sendAgreedSmsToCustomerService(mobile,s,eng, o);
        	orderService.saveUpdate(o);
        }
        
      
        
        
        //超过30分钟还未预约的以旧换新订单
        List<NewOrder> oldToNewOrders = newOrderService.queryUnAgreedForShop(30,0);
        for(NewOrder o:oldToNewOrders){
        	o.setSendAgreedNews(1);
        	//给对应工程师发送消息
        	Engineer eng=engineerService.queryById(o.getEngineerId());
        	SmsSendUtil.sendOldToNewSmsToEngineer(eng,o);
        	//给对应门店发送提醒消息
        	Shop s=shopService.queryByCode(o.getShopCode());
        	SmsSendUtil.sendOldToNewSmsToShop(s,eng, o);
        	newOrderService.saveUpdate(o);
        }
        
        
        //对以旧换新订单超过两小时还未预约的，给客服发送一次信息
        List<NewOrder> oldToNewMoreOrders = newOrderService.queryUnAgreedForShop(120,1);
        for(NewOrder o:oldToNewMoreOrders){
        	o.setSendAgreedNews(2);
        	//给客服发送提醒短信
        	Engineer eng=engineerService.queryById(o.getEngineerId());
        	Shop s=shopService.queryByCode(o.getShopCode());
        	//CustService cust=custServiceService.queryByCustNumber("kf003");
            String mobile="18668163070";
        	SmsSendUtil.sendOldToNewSmsToCustomerService(mobile,s,eng, o);
        	newOrderService.saveUpdate(o);
        }
        
        //log.info("自动发送提醒短信结束");
    }
   
    
    /**
     * 定时刷新微信调用接口凭证access_token
     */
    public void getAccessToken(){
    	//log.info("开始获取微信access_token");
    	StringBuffer sb=new StringBuffer();
    	sb.append(SystemConstant.WECHAT_TOKENURL);
    	sb.append("?grant_type=client_credential").append("&appid=").append(SystemConstant.APP_ID);
    	sb.append("&secret=").append(SystemConstant.APP_SECRET);
    	//请求该URL
    	String httpGet = HttpClientUtil.httpGet(sb.toString());
    	//得到access_token写入指定properties文件
    	if(StringUtils.isNotBlank(httpGet)){
    		JSONObject fromObject = JSONObject.parseObject(httpGet);
    	    String token=fromObject.getString("access_token");
    	    GlobalConstants.interfaceUrlProperties.put("access_token", token);
    	}else{
    		log.info("获取微信access_token返回为空");
    	}
    	
    }


    /**
     * 定时推送号卡给电渠
     */
    public void pushTelecom(){
        //查询未推送的号卡
        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        Date startTime = DateUtil.getStartTime();
        String time=sf.format(startTime);
        log.info("推送给电渠号卡查询起始时间："+time);
        //推送当天的
        List<TelecomCard> telecomCards =telecomCardService.queryByTime(time);
        //推送所有的
        //List<TelecomCard> telecomCards = telecomCardService.queryPushFail();
        log.info("未推送的号卡数："+telecomCards.size());
        log.info("开始推送号卡给电渠  推送地址："+SystemConstant.TELECOM_URL);
        signletonThread(telecomCards);   //单线程同步推送
        //manyThread(telecomCards);      //多线程推送
        log.info("推送号卡给电渠完毕");
    }


    /**
     * 单线程模式推送号卡
     * @param telecomCards
     */
    public void signletonThread(List<TelecomCard> telecomCards){
        for(TelecomCard card:telecomCards){
            try {
                superToChannelService.singletonPush(card);
            } catch (Exception e) {
                e.printStackTrace();
                log.info("推送电渠失败："+e.getMessage());
            }
        }
    }



    /**
     * 多线程模式推送号卡
     */
    public void manyThread(List<TelecomCard> telecomCards){
        List<List<TelecomCard>> list=new ArrayList<List<TelecomCard>>();    //推送的批次集合
        if(!telecomCards.isEmpty()){
            int sum=0;
            if(telecomCards.size()>Integer.parseInt(SystemConstant.PUSH_MAXSUM)){
                int size=telecomCards.size();
                int maxSum=Integer.parseInt(SystemConstant.PUSH_MAXSUM);
                double pushSum=(((double)size/(double)maxSum)>(size/maxSum)?size/maxSum+1:size/maxSum);   //推送的次数  向上取整
                for (int i = 0; i < pushSum; i++) {
                    List<TelecomCard> cardList=new ArrayList<TelecomCard>();
                    for(int j=sum;j<sum+100;j++){
                        if(j>=size){
                            //如果超过最大数 跳出循环
                            break;
                        }else{
                            cardList.add(telecomCards.get(j));
                        }
                    }
                    sum=sum+100;
                    list.add(cardList);
                }

                //分多次推送批次  多线程模式
                for(List<TelecomCard> cards:list){
                    try {
                        thread(cards);
                        Thread.sleep(60000);   //至少一分钟推送一次
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }else{
                thread(telecomCards);
            }
        }
    }






    /**
     * 多线程推送
     * @param list
     */
    public void thread(List<TelecomCard> list){
        //创建线程池对象
        ExecutorService service = Executors.newFixedThreadPool(Integer.parseInt(SystemConstant.PUSH_MAXSUM));
        for (int i = 0; i < list.size(); i++) {
            //创建Runnable实例对象

            MyRunnable r = new MyRunnable(list.get(i),superToChannelService);
            service.submit(r);
        }
        //关闭线程
        service.shutdown();
    }


    public static void main(String[] args) {
        List<List<TelecomCard>> list=new ArrayList<List<TelecomCard>>();    //推送的批次集合
        for (int i = 0; i <list.size() ; i++) {
            //推送次数
            new Job4Order().thread(list.get(i));
            System.out.println("-------------------------------------------------------------------------执行完"+i);
        }
        System.out.println("最终完毕");
    }

}
