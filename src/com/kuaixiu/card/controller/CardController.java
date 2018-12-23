package com.kuaixiu.card.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.exception.SystemException;
import com.common.importExcel.ImportReport;
import com.common.paginate.Page;
import com.common.systemEnum.ChannelExpress;
import com.common.systemEnum.ZhuanExpress;
import com.common.util.MD5Util;
import com.google.common.collect.Maps;
import com.kuaixiu.card.entity.*;
import com.kuaixiu.card.service.*;
import com.kuaixiu.station.entity.Station;
import com.kuaixiu.station.service.StationService;
import com.kuaixiu.timer.Job4Order;
import com.kuaixiu.zhuanzhuan.controller.AuctionOrderController;
import com.kuaixiu.zhuanzhuan.service.AuctionOrderService;
import com.system.api.entity.ResultData;
import com.system.basic.user.entity.SessionUser;
import com.system.basic.user.service.SessionUserService;
import com.system.constant.ApiResultConstant;
import com.system.constant.SystemConstant;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Auther: anson
 * @Date: 2018/7/16
 * @Description:  物流，批量卡号，单个卡号，卡号寄送信息
 */
@Controller
public class CardController extends BaseController{

    private static final Logger log=Logger.getLogger(CardController.class);
    @Autowired
    private BatchCardService batchCardService;
    @Autowired
    private TelecomCardService telecomCardService;
    @Autowired
    private BatchImportService batchImportService;
    @Autowired
    private ExpressService expressService;
    @Autowired
    private StationService stationService;
    @Autowired
    private SessionUserService sessionUserService;
    @Autowired
    private ExpressCardService expressCardService;
    @Autowired
    private SuperToChannelService superToChannelService;
    @Autowired
    private AuctionOrderService auctionOrderService;
    @Autowired
    private Job4Order job4Order;
    /**
     * 单个号卡列表页面
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/telecom/card")
    public ModelAndView card(HttpServletRequest request,
                                               HttpServletResponse response) throws Exception {
        //地市数据
        List<String> list = batchImportService.queryProvince();
        request.setAttribute("list",list);
        String returnView ="card/cardList";
        return new ModelAndView(returnView);
    }



    /**
     * 单个卡号详情页面
     * @param request
     * @param response
     */
    @RequestMapping(value = "/telecom/card/cardDetail")
    public ModelAndView cardDetail(HttpServletRequest request, HttpServletResponse response){
        String id=request.getParameter("id");  //iccid
        TelecomCard telecomCard = telecomCardService.queryById(id);
        if(telecomCard==null){
            throw new SystemException("该号卡不存在");
        }
        //转换物流编码和物流公司
        if(StringUtils.isNotBlank(telecomCard.getExpressName())) {
            for (ChannelExpress e : ChannelExpress.values()) {
                if (telecomCard.getExpressName().equals(e.toString())) {
                    telecomCard.setExpressName(e.getName());
                    break;
                }
            }
        }
        Station station = stationService.queryById(telecomCard.getStationId());
        if(station==null){
           station=new Station();
        }
        request.setAttribute("card",telecomCard);
        request.setAttribute("station",station);
        return new ModelAndView("card/cardDetail");
    }



    /**
     * 单个号卡刷新数据
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/telecom/card/queryListForPage")
    public void cardListForPage(HttpServletRequest request,
                                               HttpServletResponse response) throws Exception {

        // 获取iccid  是否已寄出  批次号   是否已分配   号卡类型   号卡名称   号卡地市   导入时间区间
        // 分配时间区间   推送电渠状态  转转推送给超人的时间区间  超人推送电渠的时间区间
        String iccid=request.getParameter("query_iccid");
        String isUse=request.getParameter("query_isUse");
        String batch=request.getParameter("query_batchId");
        String isDistribution=request.getParameter("query_isDistribution");
        String cardType=request.getParameter("query_cardType");
        String cardName=request.getParameter("query_cardName");
        String city=request.getParameter("query_city");
        String startTime=request.getParameter("query_startTime");
        String endTime=request.getParameter("query_endTime");
        String startDistributionTime=request.getParameter("query_startDistributionTime");
        String endDistributionTime=request.getParameter("query_endDistributionTime");
        String pushStatus=request.getParameter("query_pushStatus");
        String zhuangStartTime=request.getParameter("query_startZhuangTime");
        String zhuangEndTime=request.getParameter("query_endZhuangTime");
        String telecomStartTime=request.getParameter("query_startTelecomTime");
        String telecomEndTime=request.getParameter("query_endTelecomTime");


        Page page = getPageByRequest(request);
        TelecomCard telecomCard=new TelecomCard();
        telecomCard.setIccid(iccid);
        telecomCard.setBatch(batch);
        telecomCard.setProvince(city);
        telecomCard.setQueryStartTime(startTime);
        telecomCard.setQueryEndTime(endTime);
        telecomCard.setQueryStartDistributionTime(startDistributionTime);
        telecomCard.setQueryEndDistributionTime(endDistributionTime);
        if(StringUtils.isNotBlank(isUse)){
            telecomCard.setIsUse(Integer.parseInt(isUse));
        }
        if(StringUtils.isNotBlank(cardType)){
            telecomCard.setType(Integer.parseInt(cardType));
        }
        if(StringUtils.isNotBlank(isDistribution)){
            telecomCard.setIsDistribution(Integer.parseInt(isDistribution));
        }
        if(StringUtils.isNotBlank(cardName)){
            telecomCard.setCardName(Integer.parseInt(cardName));
        }
        if(StringUtils.isNotBlank(pushStatus)){
            telecomCard.setIsPush(Integer.parseInt(pushStatus));
        }
        // 转转推送给超人
        if(StringUtils.isNotBlank(zhuangStartTime)&&StringUtils.isNotBlank(zhuangEndTime)){
            telecomCard.setQueryZhuangStartTime(zhuangStartTime);
            telecomCard.setQueryZhuangEndTime(zhuangEndTime);
            // 转转推送给超人的号卡 状态为已使用
            telecomCard.setIsUse(1);
        }
        telecomCard.setPage(page);
        List<TelecomCard> cards=new ArrayList<TelecomCard>();
        // 超人推送给电渠
        if(StringUtils.isNotBlank(telecomStartTime)&&StringUtils.isNotBlank(telecomEndTime)){
            telecomCard.setQueryTelecomStartTime(telecomStartTime);
            telecomCard.setQueryTelecomEndTime(telecomEndTime);
            cards=telecomCardService.queryTelecomListForPage(telecomCard);
        }else{
            cards = telecomCardService.queryListForPage(telecomCard);
        }

        page.setData(cards);
        this.renderJson(response, page);
    }



    /**
     * 号卡批次页面   一个批次一条数据
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/telecom/batch/card")
    public ModelAndView batchCard(HttpServletRequest request,
                                               HttpServletResponse response) throws Exception {
        //地市数据
        List<String> list = batchImportService.queryProvince();
        request.setAttribute("list",list);
        String returnView ="card/batchCardList";
        return new ModelAndView(returnView);
    }


    /**
     * 号卡批次刷新数据    一个批次一条数据
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/telecom/batch/queryListForPage")
    public void batchCardListForPage(HttpServletRequest request,
                                HttpServletResponse response) throws Exception {
        // 获取批次id 号卡类型 号卡名称  所属本地网   导入时间区间
        String batchId=request.getParameter("query_batchId");
        String cardType=request.getParameter("query_cardType");
        String isDistribution=request.getParameter("query_isDistribution");
        String cardName=request.getParameter("query_cardName");
        String city=request.getParameter("query_city");
        String startTime=request.getParameter("query_startTime");
        String endTime=request.getParameter("query_endTime");


        Page page = getPageByRequest(request);
        BatchImport batchImport=new BatchImport();
        batchImport.setBatchId(batchId);
        if(StringUtils.isNotBlank(cardType)){
            batchImport.setType(Integer.parseInt(cardType));
        }
        if(StringUtils.isNotBlank(isDistribution)){
            batchImport.setIsDistribution(Integer.parseInt(isDistribution));
        }
        if(StringUtils.isNotBlank(cardName)){
            batchImport.setCardName(Integer.parseInt(cardName));
        }
        batchImport.setProvince(city);
        batchImport.setQueryStartTime(startTime);
        batchImport.setQueryEndTime(endTime);
        batchImport.setPage(page);
        List<BatchImport> batchImportList=batchImportService.queryListForPage(batchImport);
        page.setData(batchImportList);
        this.renderJson(response, page);
    }


    /**
     * 单个批次详情页面
     * @param request
     * @param response
     */
    @RequestMapping(value = "/telecom/batch/batchDetail")
    public ModelAndView batchDetail(HttpServletRequest request, HttpServletResponse response){
        List<BatchCard> list=new ArrayList<BatchCard>();
                String id=request.getParameter("id");
        //查询该导入批次的信息
        BatchImport batch = batchImportService.queryById(id);
        if(batch==null){
            throw new SystemException("该批次不存在");
        }
        //查询该批次下的分批次数据情况
        BatchCard b=new BatchCard();
        b.setBatch(id);
        List<BatchCard> batchCardList = batchCardService.queryListForPage(b);
        if(batchCardList.isEmpty()){
            throw new SystemException("该批次下没有任何信息");
        }
        //根据批次拆分出  已分配和未分配
        for(BatchCard bc:batchCardList){
            if(bc.getIsDistribution()==1){
                //有分配过  开始拆分
                BatchCard card1=new BatchCard();   //已分配的
                card1.setId(bc.getId());
                card1.setIsDistribution(1);
                card1.setBeginIccid(bc.getBeginIccid());
                BigDecimal mal=new BigDecimal(bc.getBeginIccid()).add(new BigDecimal(bc.getDistributionSum()));
                card1.setEndIccid(mal.subtract(new BigDecimal("1")).toString());
                card1.setSum(bc.getDistributionSum());
                card1.setDistributionTime(bc.getDistributionTime());


                BatchCard card2=new BatchCard();   //未分配的  如果为0则不要显示
                if(bc.getSum()-bc.getDistributionSum()!=0){
                    //设置未分配的信息
                    card2.setId(bc.getId());
                    card2.setSum(bc.getSum()-bc.getDistributionSum());
                    card2.setBeginIccid(mal.toString());
                    card2.setEndIccid(bc.getEndIccid());
                    card2.setIsDistribution(0);
                    card2.setDistributionTime(null);
                    list.add(card2);
                }
                list.add(card1);
            }else{
                list.add(bc);
            }
        }


        request.setAttribute("batch",batch);
        request.setAttribute("batchCardList",list);
        return new ModelAndView("card/batchCardDetail");
    }



    /**
     * 批次分配页面
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/telecom/card/distribution")
    public ModelAndView cardDistribution(HttpServletRequest request,
                                  HttpServletResponse response) throws Exception {
        //批次号
        String batchId=request.getParameter("batchId");
        BatchCard c=new BatchCard();
        c.setBatch(batchId);
        List<BatchCard> cards = batchCardService.queryListForPage(c);
        request.setAttribute("batchCardList",cards);

        //加载物流公司信息列表
        List<Express> list=expressService.queryList(new Express());
        request.setAttribute("expressList",list);

        //加载站点信息
        Station s=new Station();
        s.setIsOpen(0);
        List<Station> stationList=stationService.queryList(s);
        request.setAttribute("stationList",stationList);
        String returnView ="card/cardDistribution";
        return new ModelAndView(returnView);
    }


    /**
     * 提交号卡分配订单  前端设置校验分配总数不能超过批次可分配总数   后端根据批次依次推送
     * @param request
     * @param response
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = "/telecom/telecom/createOrder")
    public ResultData createOrder(HttpServletRequest request,
                                              HttpServletResponse response, @RequestBody String params) {
        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        try {
            if(params==null){
                throw new SystemException(ApiResultConstant.resultCode_str_1001, ApiResultConstant.resultCode_1001);
            }
            JSONArray array= JSONArray.parseArray(params.toString());
            for (int i = 0; i <array.size() ; i++) {
                JSONObject j= (JSONObject) array.get(i);
                //依次获取号卡批次uuid 起始iccid 结束iccid 数量 站点id 物流公司 物流单号 发货时间
                String batchId=getUseString(j.getString("batchId"));
                String beginIccid=getUseString(j.getString("beginIccid"));
                String endIccid=getUseString(j.getString("endIccid"));
                String sum=getUseString(j.getString("sum"));
                String stationId=getUseString(j.getString("stationId"));
                String express=getUseString(j.getString("expressName"));
                String deliverOrder=getUseString(j.getString("expressNumber"));
                String deliverTime=getUseString(j.getString("time"));
                //对该批次做更新操作
                BatchCard batchCard = batchCardService.queryById(batchId);
                if(batchCard==null){
                    throw new SystemException("该批次不存在");
                }


                //判断号卡分配输量是否正确
                BigDecimal begin=new BigDecimal(beginIccid);
                BigDecimal end=new BigDecimal(endIccid);
                if((end.subtract(begin)).intValue()!=(Integer.parseInt(sum)-1)){
                     throw new SystemException("该区间分配的数量和实际iccid区间不符合");
                }


                //判断分配数是否大于库存数
                if((batchCard.getSum()-batchCard.getDistributionSum())<Integer.parseInt(sum)){
                    throw new SystemException(batchCard.getBatch()+"该批次可分配数量不足"+sum);
                }

                //开始分配 创建分配物流和号卡片批次信息  推送的信息
                ExpressCard expressCard=new ExpressCard();
                expressCard.setId(UUID.randomUUID().toString().replace("-",""));
                expressCard.setBatchId(batchCard.getId());
                expressCard.setStationId(stationId);
                expressCard.setSendTime(sf.parse(deliverTime));
                expressCard.setExpressCompany(express);
                expressCard.setExpressNumber(deliverOrder);
                expressCard.setCardType(batchCard.getType());
                expressCard.setCardName(batchCard.getCardName());
                expressCard.setSum(Integer.parseInt(sum));
                expressCard.setStartIccid(begin.toString());
                expressCard.setEndIccid(end.toString());

                //开始推送
                try {
                    sendTelecomCard(expressCard);
                    //新增超人-转转物流记录
                    expressCard.setStatus(0);
                    expressCardService.add(expressCard);

                    //修改批次信息
                    batchCard.setDistributionSum(batchCard.getDistributionSum()+Integer.parseInt(sum));
                    batchCard.setIsDistribution(1);
                    batchCard.setDistributionTime(new Date());
                    batchCardService.saveUpdate(batchCard);

                    //更新站点的分配信息
                    Station station = stationService.queryById(stationId);
                    if(station!=null){
                        station.setDistributionSum(station.getDistributionSum()+Integer.parseInt(sum));
                        station.setRepertory(station.getRepertory()+Integer.parseInt(sum));
                        stationService.saveUpdate(station);
                    }

                    //对该批次的总批次  进行数据更新
                    BatchImport batchImport = batchImportService.queryById(batchCard.getBatch());
                    if(batchImport!=null){
                        batchImport.setIsDistribution(1);
                        batchImport.setDistributionSum(batchImport.getDistributionSum()+Integer.parseInt(sum));
                        batchImportService.saveUpdate(batchImport);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    expressCard.setStatus(1);
                    expressCard.setMsg(e.getMessage());
                    expressCardService.add(expressCard);
                    throw new SystemException(e.getMessage());
                }
            }
            result.setResult(jsonResult);
            result.setResultCode("0");
            result.setSuccess(true);
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        return result;
    }


    /**
     * 机型号卡信息推送
     */
    public  void sendTelecomCard(ExpressCard card) throws Exception {
           String method="gift.toyoupin.sim.delivered";
               JSONObject j=new JSONObject();
               j.put("iccidStart",card.getStartIccid());
               j.put("iccidEnd",card.getEndIccid());
               j.put("count",card.getSum());
               j.put("deliverTime",card.getSendTime().getTime());
               j.put("expressType",card.getExpressCompany());
               j.put("expressCode",card.getExpressNumber());
               j.put("allyId",Long.parseLong(card.getStationId()));
               String post = AuctionOrderController.post(j, method);
               //判断是否返回正确
               log.info("转转推送成功");
               //推送成功 对该批次的号卡进行更新记录
               TelecomCard telecomCard=new TelecomCard();
               telecomCard.setBatchId(card.getBatchId());
               telecomCard.setBeginIccid(card.getStartIccid());
               telecomCard.setEndIccid(card.getEndIccid());
               telecomCard.setStationId(card.getStationId());
               telecomCard.setDistributionTime(new Date());
               telecomCard.setStationExpressId(card.getId());
               telecomCard.setStationExpressNumber(card.getExpressNumber());
               telecomCard.setIsDistribution(1);
               telecomCardService.updateByStartIccid(telecomCard);

    }



    /**
     * 批次导入页面
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value="/telecom/batch/import")
    public ModelAndView cardImport(HttpServletRequest request,HttpServletResponse response){
        return new ModelAndView("card/importIndex");
    }


    /**
     * 模板导入
     * @param myfile
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/telecom/card/import")
    public void doImport(
            @RequestParam("fileInput") MultipartFile myfile,
            HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        // 返回结果，默认失败
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
        ImportReport report = new ImportReport();
        StringBuffer errorMsg = new StringBuffer();
        try{
            if(myfile != null && StringUtils.isNotBlank(myfile.getOriginalFilename())){
                String fileName=myfile.getOriginalFilename();
                //扩展名
                String extension= FilenameUtils.getExtension(fileName);
                if (!extension.equalsIgnoreCase("xls")){
                    errorMsg.append("导入文件格式错误！只能导入excel  xls文件！");
                }
                else{
                    batchCardService.importExcel(myfile,report,getCurrentUser(request));
                    resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
                    resultMap.put(RESULTMAP_KEY_MSG, "导入成功");
                }
            }
            else{
                errorMsg.append("导入文件为空");
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            errorMsg.append("导入失败");
            resultMap.put(RESULTMAP_KEY_MSG, "导入失败");
        }
        request.setAttribute("report", report);
        resultMap.put(RESULTMAP_KEY_DATA, report);
        renderJson(response, resultMap);
    }


    /**
     * 号卡重新推送
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/telecom/card/push")
    public void push(HttpServletRequest request,HttpServletResponse response) throws IOException {
        Map<String, Object> resultMap = Maps.newHashMap();
        String id=request.getParameter("id");   //iccid
        if(StringUtils.isBlank(id)){
            throw new SystemException("参数不完整");
        }
        SuperToChannel superToChannel = superToChannelService.queryById(id);
        if(superToChannel==null){
            throw new SystemException("该号卡推送记录不存在");
        }
        //开始推送
        Map map = auctionOrderService.sendToChannel(superToChannel);
        if((int)(map.get("code"))==1){
            throw new SystemException((String)(map.get("msg")));
        }

        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
        resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
        renderJson(response, resultMap);
    }



    /**
     * 批次推送列表
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value="/telecom/batch/push")
    public ModelAndView batchPush(HttpServletRequest request,HttpServletResponse response){
        return new ModelAndView("card/batchPushList");
    }


    /**
     * 批次推送列表刷新数据
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/telecom/push/queryListForPage")
    public void pushListForPage(HttpServletRequest request,
                                HttpServletResponse response) throws Exception {
        // 获取   批次id  站点id  物流单号   推送状态   号卡类型   号卡名称
        String batchId=request.getParameter("query_batchId");
        String stationId=request.getParameter("query_stationId");
        String expressNumber=request.getParameter("query_number");
        String status=request.getParameter("query_status");
        String type=request.getParameter("query_type");
        String name=request.getParameter("query_name");

        Page page = getPageByRequest(request);
        ExpressCard expressCard=new ExpressCard();
        expressCard.setBatch(batchId);
        expressCard.setStationId(stationId);
        expressCard.setExpressNumber(expressNumber);
        if(StringUtils.isNotBlank(status)){
            expressCard.setStatus(Integer.parseInt(status));
        }
        if(StringUtils.isNotBlank(type)){
            expressCard.setCardType(Integer.parseInt(type));
        }
        if(StringUtils.isNotBlank(name)){
            expressCard.setCardName(Integer.parseInt(name));
        }

        expressCard.setPage(page);
        List<ExpressCard> expressCards = expressCardService.queryListForPage(expressCard);
        for(ExpressCard card:expressCards){
            if(StringUtils.isNotBlank(card.getExpressCompany())) {
                for (ZhuanExpress e : ZhuanExpress.values()) {
                    if (card.getExpressCompany().equals(e.toString())) {
                        card.setExpressCompany(e.getName());
                        break;
                    }
                }
            }

        }
        page.setData(expressCards);
        this.renderJson(response, page);
    }


    /**
     * 批次重新推送
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/telecom/batch/rePush")
    public void batchRepush(HttpServletRequest request,HttpServletResponse response) throws IOException {
        Map<String, Object> resultMap = Maps.newHashMap();
        String id=request.getParameter("id");   //
        if(StringUtils.isBlank(id)){
            throw new SystemException("参数不完整");
        }
        //查询该推送记录
        ExpressCard expressCard = expressCardService.queryById(id);
        if(expressCard==null){
            throw new SystemException("该推送记录不存在");
        }
        try {
            sendTelecomCard(expressCard);
            expressCard.setStatus(0);
            expressCard.setInTime(new Date());
            expressCardService.saveUpdate(expressCard);
        } catch (Exception e) {
            e.printStackTrace();
            expressCard.setStatus(1);
            expressCard.setMsg(e.getMessage());
            expressCard.setInTime(new Date());
            expressCardService.saveUpdate(expressCard);
            throw new SystemException(e.getMessage());

        }
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
        resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
        renderJson(response, resultMap);
    }


    /**
     * 管理员主动推送号卡给电渠
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/recycle/admin/push")
    public void adminPush(HttpServletRequest request,HttpServletResponse response) throws IOException {
        Map<String, Object> resultMap = Maps.newHashMap();
        SessionUser su = getCurrentUser(request);
        //管理员才可以操作
        if (su.getType() != SystemConstant.USER_TYPE_SYSTEM ) {
             throw new SystemException("您无权操作！");
        }
        //批量推送号卡给电渠
        job4Order.pushTelecom();
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
        resultMap.put(RESULTMAP_KEY_MSG, "操作成功");
        renderJson(response, resultMap);
    }



    public static void main(String[] args) {
        //ac263148bb12a16e2af5a72ee8446216
        String srcsysid="zhzh";
        String inputtime="20171201160911";
        String key="9876543210";
        String s="srcsysid"+srcsysid+"inputtime"+inputtime+key;
        System.out.println(s);
        String sign= MD5Util.encodePassword(s);
        System.out.println(sign);


    }
}
