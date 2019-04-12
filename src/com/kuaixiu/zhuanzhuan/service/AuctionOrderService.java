package com.kuaixiu.zhuanzhuan.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.service.BaseService;
import com.common.exception.SystemException;
import com.common.systemEnum.ChannelExpress;
import com.common.systemEnum.ZhuanExpress;
import com.common.util.HttpClientUtil;
import com.common.util.MD5Util;
import com.common.wechat.common.util.StringUtils;
import com.kuaixiu.card.entity.SuperToChannel;
import com.kuaixiu.card.entity.TelecomCard;
import com.kuaixiu.card.service.SuperToChannelService;
import com.kuaixiu.card.service.TelecomCardService;
import com.kuaixiu.station.entity.Station;
import com.kuaixiu.station.service.StationService;
import com.kuaixiu.zhuanzhuan.dao.AuctionOrderMapper;
import com.kuaixiu.zhuanzhuan.entity.AuctionOrder;
import com.kuaixiu.zhuanzhuan.entity.AuctionReturnOrder;
import com.system.constant.SystemConstant;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: anson
 * @Date: 2018/6/25
 * @Description:
 */
@Service("auctionOrderService")
public class AuctionOrderService extends BaseService<AuctionOrder>{

    private static final Logger log=Logger.getLogger(AuctionOrderService.class);
    @Autowired
    private AuctionOrderMapper<AuctionOrder> mapper;
    @Autowired
    private AuctionReturnOrderService auctionReturnOrderService;
    @Autowired
    private TelecomCardService telecomCardService;
    @Autowired
    private SuperToChannelService superToChannelService;
    @Autowired
    private StationService stationService;

    @Override
    public AuctionOrderMapper<AuctionOrder> getDao() {
        return mapper;
    }

    private static SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    /**
     *  通过转转订单id查询订单
     * @param auctionOrderId
     * @return
     */
    public AuctionOrder queryByAuctionOrderId(String auctionOrderId){
        return  mapper.queryByAuctionOrderId(auctionOrderId);
    }

    public static void main(String[] args) throws ParseException {
//        String url="http://188-24.zhuanzhuan.58v5.cn/router/rest";
//        System.out.println(1);
//        //String s = HttpClientUtil.sendPostContent(url, "");
//       // String yuantong = getExpress("yuantong");
//       // System.out.println(yuantong);
//        String str="{\n" +
//                "    \"partnerOrderId \":\"123456789000\",\n" +
//                "    \"orderId\":\"123456\",\n" +
//                "    \"qcCode\":\"10001\",\n" +
//                "    \"doneTime\":\"2018-04-19 18:31:45\",\n" +
//                "    \"pics\":[\n" +
//                "        \"http://c.58cdn.com.cn/xxx/xxx/xxxx1.jpg\",\n" +
//                "        \"http://c.58cdn.com.cn/xxxx2.jpg\"\n" +
//                "    ]\n" +
//                "}";
//        JSONObject j=JSONObject.parseObject(str);
//        System.out.println(j);
//        System.out.println(j.getString("pics"));


        String time="1532425567000";
        Date t=new Date(Long.valueOf(time));
        System.out.println(t);
    }



    /**
     * 转转已收货
     * @param json
     * @param order
     * @throws ParseException
     */
    public void recived(JSONObject json,AuctionOrder order) throws ParseException {
        String partnerOrderId= json.getString("partnerOrderId ");
        Long orderId= json.getLong("orderId");
        String expressCode = json.getString("expressCode");
        String expressCompany = json.getString("expressCompany");
        String receiveTime = json.getString("receiveTime");
        String remark = json.getString("remark");

        if(StringUtils.isBlank(receiveTime)){
            throw new SystemException("receiveTime不能为空");
        }
        order.setReceiveTime(sf.parse(receiveTime));
        order.setZhuanRemark(remark);
        this.saveUpdate(order);

    }



    /**
     * 转转分拣完成
     * @param json
     * @param order
     */
    public void sorted(JSONObject json,AuctionOrder order) throws ParseException {
        String partnerOrderId = json.getString("partnerOrderId ");
        String qcCode = json.getString("qcCode");
        Long orderId = json.getLong("orderId");
        String doneTime = json.getString("doneTime");
        String expressCode = json.getString("expressCode");
        String expressCompany = json.getString("expressCompany");
        String packageList = json.getString("packageList");    //string数组
        String remark = json.getString("remark");
        if(StringUtils.isBlank(packageList)){
            throw new SystemException("packageList不能为空");
        }
        if(StringUtils.isBlank(qcCode)){
            throw new SystemException("qcCode不能为空");
        }
        if(StringUtils.isBlank(doneTime)){
            throw new SystemException("doneTime不能为空");
        }
        order.setPackageList(packageList);
        order.setQcCode(qcCode);
        order.setSortDoneTime(sf.parse(doneTime));
        order.setSortRemark(remark);
        this.saveUpdate(order);



    }






    /**
     * 转转质检完成
     * @param json
     * @param order
     */
    public void qcDone(JSONObject json,AuctionOrder order) throws ParseException {
        String partnerOrderId_ = json.getString("partnerOrderId ");
        Long orderId = json.getLong("orderId");
        String qcCode = json.getString("qcCode");
        String doneTime = json.getString("doneTime");
        Integer status = json.getInteger("status");
        String statusDesc = json.getString("statusDesc");
        JSONArray cateInfo = json.getJSONArray("cateInfo");
        String checkDesc = json.getString("checkDesc");
        JSONArray report = json.getJSONArray("report");
        if(StringUtils.isBlank(cateInfo.toJSONString())||StringUtils.isBlank(report.toJSONString())||StringUtils.isBlank(doneTime)){
             throw new SystemException("部分参数为空");
        }

        order.setCheckStatus(status);
        order.setStatusDesc(statusDesc);
        order.setCateInfo(cateInfo.toJSONString());
        order.setCheckDesc(checkDesc);
        order.setReport(report.toJSONString());
        order.setQcDoneTime(sf.parse(doneTime));
        this.saveUpdate(order);



    }


    /**
     * 拍照完成
     * @param json
     * @param order
     */
    public void photographDone(JSONObject json,AuctionOrder order) throws ParseException {
        String partnerOrderId_ = json.getString("partnerOrderId ");
        Long orderId = json.getLong("orderId");
        String qcCode = json.getString("qcCode");
        String doneTime = json.getString("doneTime");
        String pics = json.getString("pics");
        if(StringUtils.isBlank(doneTime)){
            throw new SystemException("doneTime不能为空");
        }
        order.setDonePhotoTime(sf.parse(doneTime));
        order.setPics(pics);

        this.saveUpdate(order);

    }



    /**
     * 已发货  针对退货订单
     * @param json
     * @param order
     */
    public void delivered(JSONObject json,AuctionOrder order) throws ParseException {
        String partnerOrderId = json.getString("partnerOrderId ");
        Long orderId = json.getLong("orderId");
        String qcCode = json.getString("qcCode");
        String doneTime = json.getString("doneTime");
        String expressCode = json.getString("expressCode");
        String expressCompany = json.getString("expressCompany");

        if(StringUtils.isBlank(order.getReturnId())){
            throw new SystemException("退货信息不存在");
        }
        if(StringUtils.isBlank(doneTime)){
            throw new SystemException("doneTime不能为空");
        }
        AuctionReturnOrder returnOrder = auctionReturnOrderService.queryById(order.getReturnId());
        returnOrder.setExpressCode(expressCode);
        returnOrder.setExpressCompany(expressCompany);
        returnOrder.setDoneTime(sf.parse(doneTime));
        auctionReturnOrderService.saveUpdate(returnOrder);

    }



    /**
     * 估价完成
     * @param json
     * @param order
     */
    public void evaluate(JSONObject json,AuctionOrder order){
        String partnerOrderId_ = json.getString("partnerOrderId ");
        Long orderId = json.getLong("orderId");
        String price = json.getString("price");
        if(StringUtils.isBlank(price)){
            throw new SystemException("price不能为空");
        }
        order.setProbablyPrice(Integer.parseInt(price));
        this.saveUpdate(order);

    }



    /**
     * 已售出
     * @param json
     * @param order
     */
    public void sold(JSONObject json,AuctionOrder order){
        String partnerOrderId_ = json.getString("partnerOrderId ");
        Long orderId = json.getLong("orderId");
        String price = json.getString("price");
        if(StringUtils.isBlank(price)){
            throw new SystemException("price不能为空");
        }
        order.setRealPrice(Integer.parseInt(price));
        this.saveUpdate(order);

    }



    /**
     * 流拍
     * @param json
     * @param order
     */
    public void auctionfail(JSONObject json,AuctionOrder order){
        String partnerOrderId_ = json.getString("partnerOrderId ");
        Long orderId = json.getLong("orderId");
        Integer times = json.getInteger("times");

        order.setTimes(times);
        this.saveUpdate(order);

    }


    /**
     * 电信sim卡赠送结果推送   目前将信息保存  统一在每天凌晨3点推送给电渠  一次最多100
     * @param json
     */
    public void simgift(JSONObject json) throws ParseException {
        //依次获取订单id 物流公司 物流单号  站点id 发货实际戳 发货城市 ICCID
        String orderId = json.getString("orderId");
        String expressType = json.getString("expressType");
        String expressCode = json.getString("expressCode");
        String allyId = json.getString("allyId");
        String deliverTime = json.getString("deliverTime");
        String city = json.getString("city");
        String iccid = json.getString("iccid");
        TelecomCard telecomCard = telecomCardService.queryById(iccid);
        if (telecomCard == null) {
            throw new SystemException("该号卡不存在");
        }
        //如果号卡转转已成功推送，则不予再次推送
        if (telecomCard.getSuccessOrderId() != null) {
            throw new SystemException("该号卡转转已成功推送，请勿重复推送");
        }
        //如果号卡已成功推送给电渠则不予再次推送
        if(telecomCard.getIsPush()==2){
            throw new SystemException(allyId+"该号卡已成功推送，请勿重复推送！");
        }

        Station station = stationService.queryById(allyId);
        if(station==null){
            throw new SystemException(allyId+" 该站点不存在");
        }

        //如果号卡是第一次推送则更新库存
        if(telecomCard.getIsUse()==0) {
            stationService.updateById(station);
        }

        Date time=new Date(Long.valueOf(deliverTime));
        telecomCard.setSuccessOrderId(orderId);
        telecomCard.setExpressName(expressType);
        telecomCard.setExpressNumber(expressCode);
        telecomCard.setSendStationId(allyId);
        telecomCard.setSendCity(city);
        telecomCard.setSendTime(time);
        telecomCard.setIsUse(1);
        telecomCard.setUpdateTime(new Date());
        //更新号卡信息
        telecomCardService.saveUpdate(telecomCard);





        // ------------------------ 单个同步推送模式   若要开始超人定时推送给电渠  则注释改区间代码---------------------------------------------------
//        SimpleDateFormat s=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        SuperToChannel superToChannel=new SuperToChannel();
//        superToChannel.setIccid(iccid);
//        superToChannel.setOrderNo(orderId);
//        superToChannel.setExpressName(expressType);
//        superToChannel.setExpressNumber(expressCode);
//        superToChannel.setSendStationId(allyId);
//        superToChannel.setSendCity(city);
//        superToChannel.setSendTime(new Date());
//        superToChannel.setOperateTime(s.format(new Date(Long.parseLong(deliverTime))));
//
//
//        //判断该号卡是否已经成功推送过给电渠
//        SuperToChannel channel = superToChannelService.queryById(iccid);
//        if(channel==null){
//            superToChannelService.add(superToChannel);
//        }
//        //保存成功将推送结果同步给电渠
//        sendToChannel(superToChannel);
        //-----------------单个推送同步结束   -------------------------------------

    }



    /**
     * 推送单个号卡消息给电渠  记录推送状态
     */
    public Map sendToChannel(SuperToChannel s){
        s.setSendTime(new Date());  //推送时间
        Map<Object,Object> map=new HashMap<>();
        map.put("code",0);


        SimpleDateFormat sf=new SimpleDateFormat("YYYYMMDDHHMMSS");
        String url= SystemConstant.TELECOM_URL;
        String key=SystemConstant.TELECOM_KEY;
        String srcsysid=SystemConstant.TELECOM_ID;
        String inputtime=sf.format(new Date());

        //电渠 数据签名
        String sign="srcsysid"+srcsysid+"inputtime"+inputtime+key;
        sign= MD5Util.encodePassword(sign);
        JSONObject j=new JSONObject();
        // 依次设置  渠道  请求时间(YYYYMMDDHHMISS)  签名  ICCID  物流单号  物流公司编码   物流公司名称   发货时间
        j.put("srcsysid",srcsysid);
        j.put("inputtime",inputtime);
        j.put("sign",sign);
        j.put("iccid",s.getIccid());
        j.put("expressId",s.getExpressNumber());

        //结合电渠和转转提供的物流编码枚举值  转换为对应平台的值
        String expressCode=s.getExpressName();    //转转平台的物流编码
        String telecomExpressCode="";             //转换得到的电渠平台的物流编码
        String telecomExpressName="";             //转换得到的电渠平台的物流公司名称
        boolean tip=false;                        //是否精确匹配
        for(ChannelExpress e: ChannelExpress.values()){
            if(expressCode.equals(e.toString())){
                telecomExpressCode=expressCode;
                telecomExpressName=e.getName();
                tip=true;
                break;
            }
        }
        if(!tip){
            //未能精确匹配  则将其设置为其他快递
            telecomExpressCode= ChannelExpress.qita.toString();
            for(ZhuanExpress e: ZhuanExpress.values()){
               if(expressCode.equals(e.toString())){
                   telecomExpressName=e.getName();
                   break;
               }
            }
        }

        j.put("expressCompany",telecomExpressCode);
        j.put("expressName",telecomExpressName);
        j.put("operateTime",s.getOperateTime());
        log.info("开始推送给电渠  iccid："+s.getIccid());
        String result = HttpClientUtil.sendJsonPost(url, j.toJSONString());
        log.info("电渠返回结果："+result);

        //解析返回结果  如果解析错误则记录错误信息
        JSONObject jsonObject= null;
        TelecomCard telecomCard = telecomCardService.queryById(s.getIccid());
        try {
            jsonObject = JSONObject.parseObject(result);
            if(!jsonObject.getString("result_code").equals("0")){
                //推送错误 记录错误信息
                s.setStatus(1);
                s.setReturnCode(jsonObject.getString("result_msg"));
                superToChannelService.saveUpdate(s);
                //记录号卡推送失败情况
                if(telecomCard!=null){
                    telecomCard.setIsPush(1);
                    telecomCardService.saveUpdate(telecomCard);
                }
                map.put("code",1);
                map.put("msg",jsonObject.getString("result_msg"));
            }else{
                //记录号卡推送成功情况
                if(telecomCard!=null){
                    telecomCard.setIsPush(2);
                    telecomCardService.saveUpdate(telecomCard);
                }
            }
        } catch (Exception e) {
            //推送异常 记录错误信息
            s.setStatus(1);
            s.setReturnCode("推送电渠异常，详细请查看对应日志");
            superToChannelService.saveUpdate(s);
            //记录号卡推送失败情况
            if(telecomCard!=null){
                telecomCard.setIsPush(1);
                telecomCardService.saveUpdate(telecomCard);
            }
            map.put("code",1);
            map.put("msg","推送电渠异常，详细请查看对应日志");
            e.printStackTrace();
        }
        return map;
    }



    /**
     * 获取快递信息
     * @param code
     * @return
     */
    public static String getExpress(String code){
        String result="";
        switch (code){
            case "shunfeng":
                result="顺丰";
                break;
            case "yuantong":
                result="圆通";
                break;
            case "zhongtong":
                result="中通";
                break;
            case "yunda":
                result="韵达";
                break;
            case "shentong":
                result="申通";
                break;
            case "tiantian":
                result="天天快递";
                break;
            case "huitong":
                result="汇通";
                break;
            case "ems":
                result="EMS";
                break;
            case "jd":
                result="京东快递";
                break;
            case "unknow":
                result="未知快递";
                break;
        }
          return result;
    }





}
