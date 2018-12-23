package com.kuaixiu.zhuanzhuan.controller;

import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.exception.SystemException;
import com.common.paginate.Page;
import com.common.util.MD5Util;
import com.common.wechat.common.util.StringUtils;
import com.kuaixiu.recycle.entity.RecycleOrder;
import com.kuaixiu.recycle.service.RecycleOrderService;
import com.kuaixiu.zhuanzhuan.entity.AuctionOrder;
import com.kuaixiu.zhuanzhuan.entity.AuctionReturnOrder;
import com.kuaixiu.zhuanzhuan.service.AuctionOrderService;
import com.kuaixiu.zhuanzhuan.service.AuctionReturnOrderService;
import com.system.api.entity.ResultData;
import com.system.basic.user.entity.SessionUser;
import com.system.basic.user.service.SessionUserService;
import com.system.constant.SystemConstant;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @Auther: anson
 * @Date: 2018/6/25
 * @Description: 转转平台请求控制类
 */
@Controller
public class AuctionOrderController extends BaseController{

    private static final Logger log=Logger.getLogger(AuctionOrderController.class);
    @Autowired
    private SessionUserService sessionUserService;
    @Autowired
    private RecycleOrderService recycleOrderService;
    @Autowired
    private AuctionOrderService auctionOrderService;
    @Autowired
    private AuctionReturnOrderService auctionReturnOrderService;

    private static SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /**
     * 请求地址
     */
    public static final  String OPEN_URL =SystemConstant.ZHUANG_URL;
    /**
     * 调用方id
     */
    public static final Long PARTNEW_ID=Long.parseLong(SystemConstant.ZHUANG_PARTNEW_ID);
    /**
     * 数据加密补充字段
     */
    public static final String APP_KEY=SystemConstant.ZHUANG_APP_KEY;

    //设置超人平台发货信息  快递单号  快递公司  转转类别id  卖家手机号  昵称  地址
    public static final String EXPRESS_COMPANY="shunfeng";
    public static final String EXPRESS_CODE="821218017251";
    public static final Integer CATE_ID=101;
    public static final String SENDER_MOBILE="15356152347";
    public static final String SENDER_NICKNAME="李白";
    public static final String SENDER_ADDRESS="浙江省 杭州市 江干区 凤起东路132号";


    //设置超人平台收货信息（转转退货时使用）   手机号  名称  地址  备注
    public static final String CNEE_MOBILE="15356152347";
    public static final String CNEE_NAME="李白";
    public static final String CNEE_ADDRESS="浙江省 杭州市 江干区 凤起东路132号";
    public static final String CNEE_REMARK="测试一下";







    /**
     * 转转定制post请求方法  判断请求是否成功  返回请求成功的data数据
     * @param param   请求主体信息
     * @param method  请求方法
     * @return
     * @throws Exception
     */
    public static String post(JSONObject param,String method) throws Exception {
        String url=OPEN_URL;         //请求地址
        String code=JSONObject.toJSONString(param);  //需要编码的参数
        String timeStamp=sf.format(new Date());
        String sign=method+PARTNEW_ID+timeStamp+code+APP_KEY;  //生成签名
        //log.info("需要签名的参数："+sign);
        sign= MD5Util.md5Encode(sign).toLowerCase();
        //log.info("MD5加密后："+sign);
        StringBuffer params = new StringBuffer();   //需要发送的数据
        params.append("method").append("=").append(method)
                .append("&").append("partner_id").append("=").append(PARTNEW_ID)
                .append("&").append("timestamp").append("=").append(timeStamp)
                .append("&").append("sign").append("=").append(sign)
                .append("&").append("params").append("=").append(code);
        //URLEncoder.encode(code,"utf-8")
        log.info("请求转转参数："+params);
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            //conn.setRequestProperty("accept", "*application/json*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            //out.print(params.toString());
            out.print(params);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            log.info("发送 POST 请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        result= URLDecoder.decode(result, "utf-8");
        log.info("转转返回："+result);
        //判断接口返回是否成功
        JSONObject jsonObject = null;

        try {
            jsonObject = JSONObject.parseObject(result);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SystemException("转转返回数据异常，具体信息请查看日志");
        }

        if(jsonObject.getInteger("code")!=0){
            throw new SystemException(jsonObject.getString("msg"));
        }
        return jsonObject.getString("data");
    }


    /**
     * 获取验机地址
     */
    public static JSONObject getCheckMobileAddress(JSONObject j) throws Exception {
        JSONObject result=new JSONObject();
        String method="base.ally.applyAddr";
//            j.put("province","河北省");
//            j.put("city","河北省");
//            j.put("type","bm");
//            j.put("cateId",101);
//            j.put("lon","40.025858");
//            j.put("lat","116.369199");
            result = JSONObject.parseObject(post(j, method));
        return result;
    }


    /**
     * 创建保卖订单  创建成功后更新回收订单拍卖状态为已拍卖
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value="recycle/createSaleOrder")
    @ResponseBody
    public ResultData createSaleOrder(HttpServletRequest request, HttpServletResponse response){
        String method="esale.order.create";
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        try {
            String expressCompany=EXPRESS_COMPANY;    //快递公司
            String expressCode=EXPRESS_CODE;         //快递单号
            Date time=new Date();                    //快递发货时间
            Integer cateId=CATE_ID;                 //转转类别id
            String senderMobile=SENDER_MOBILE;      //卖家手机号
            String nickName=SENDER_NICKNAME;        //卖家昵称
            String address=SENDER_ADDRESS;          //卖家地址


            //获取需要拍卖的订单信息
            String id=request.getParameter("id");
            RecycleOrder recycleOrder = recycleOrderService.queryById(id);
            if(recycleOrder==null){
                throw new SystemException("订单不存在");
            }



            //先获取验机地址
            JSONObject json=new JSONObject();
            json.put("province","浙江省");
            json.put("city","杭州市");
            json.put("type","bm");
            json.put("cateId",CATE_ID);
            json.put("lon","120.207372");
            json.put("lat","30.26933");
            JSONObject jsonAddress = getCheckMobileAddress(json);


            //创建订单
            JSONObject j=new JSONObject();
            j.put("partnerOrderId",recycleOrder.getOrderNo());
            j.put("cateId",cateId);
            j.put("productDescription",recycleOrder.getProductName());
            //j.put("partnerProductId","superman");   //合作方商品标示  非必填
            //卖家json对象
            JSONObject seller=new JSONObject();
            seller.put("mobile",senderMobile);
            seller.put("name",nickName);
            seller.put("address",address);
            j.put("seller",seller);
            //发货信息json对象
            JSONObject shipments=new JSONObject();
            shipments.put("allyId",jsonAddress.getString("allyId"));
            shipments.put("expressCompany",expressCompany);
            shipments.put("expressCode",expressCode);
            shipments.put("senderMobile","18900001234");             //   非必须
            shipments.put("shippedTime","2018-04-01 09:09:08");      //   非必须
            j.put("shipments",shipments);
            String returnResult = post(j, method);

            //验机成功返回信息  得到转转平台订单id 和订单状态
            JSONObject jsonObject = JSONObject.parseObject(returnResult);
            //新建转转订单
            AuctionOrder order=new AuctionOrder();

            order.setAuctionOrderId(jsonObject.getString("orderId"));
            order.setOrderNo(jsonObject.getString("partnerOrderId"));
            order.setCreateTime(sf.parse(jsonObject.getString("createTime")));
            order.setOrderStatus(jsonObject.getString("status"));
            order.setCateId(cateId);
            order.setExpressCompany(expressCompany);
            order.setExpressCode(expressCode);
            order.setSenderMobile(senderMobile);      //卖家手机号
            order.setSenderAddress(address);
            order.setSenderNickname(nickName);
            order.setAllyId(jsonAddress.getString("allyId"));
            order.setStationTel(jsonAddress.getString("tel"));
            order.setStationName(jsonAddress.getString("name"));
            order.setStationAddress(jsonAddress.getString("addr"));
            order.setShippedTime(time);               //卖家发货时间
            order.setProductDescription(recycleOrder.getProductName());
            auctionOrderService.add(order);
            //新建转转拍卖订单成功后  修改回收订单状态
            recycleOrder.setIsSale(1);
            recycleOrderService.saveUpdate(recycleOrder);


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
     * 申请拍照
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value="recycle/applyPhotograph")
    @ResponseBody
    public ResultData applyPhotograph(HttpServletRequest request, HttpServletResponse response){
        String method="esale.order.photograph.apply";
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        try {
            String id=request.getParameter("id");
            String remark=request.getParameter("remark");   //拍照内容描述  如   屏幕划痕，磕碰边角
            AuctionOrder order = auctionOrderService.queryById(id);
            if(order==null){
                throw new SystemException("订单不存在");
            }
            if(StringUtils.isBlank(remark)){
                remark="申请拍照测试";
            }
            JSONObject j=new JSONObject();
            j.put("orderId",order.getAuctionOrderId());
            j.put("remark",remark);
            String message = post(j, method);
            JSONObject jsonObject = JSONObject.parseObject(message);
            //拍照申请完毕  更新订单信息
            order.setOrderStatus(jsonObject.getString("status"));      //状态,质检中：qc
            order.setApplyPhotoTime(sf.parse(jsonObject.getString("createTime")));
            order.setSuperRemark(remark);
            auctionOrderService.saveUpdate(order);

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
     * 确认售卖
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value="recycle/confirmSale")
    @ResponseBody
    public ResultData confirmSale(HttpServletRequest request, HttpServletResponse response){
        String method="esale.order.confirm";
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        try {
            String id=request.getParameter("id");
            String price=request.getParameter("price");   //售卖价格（单位分）
            if(StringUtils.isBlank(id)){
                throw new SystemException("请求参数不完整");
            }
            if(StringUtils.isBlank(price)){
                //默认售价1000快
                price="1000";
            }
            AuctionOrder order = auctionOrderService.queryById(id);
            if(order==null){
                throw new SystemException("订单不存在");
            }

            JSONObject j=new JSONObject();
            j.put("orderId",order.getAuctionOrderId());
            j.put("price",Integer.parseInt(price));
            String message = post(j, method);
            JSONObject jsonObject = JSONObject.parseObject(message);
            //更新订单状态
            order.setSuperPrice(jsonObject.getInteger("price"));
            order.setOrderStatus(jsonObject.getString("status"));   //状态,已确认：confirmed
            auctionOrderService.saveUpdate(order);

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
     * 退货申请
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value="recycle/refundApply")
    @ResponseBody
    public ResultData refundApply(HttpServletRequest request, HttpServletResponse response){
        String method="esale.order.refund.apply";
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        try {
            String id=request.getParameter("id");
            String remark=request.getParameter("remark");              //收货人备注
            String expressType=request.getParameter("expressType");    //邮寄类型   1-寄付 2-到付
            if(StringUtils.isBlank(id)){
                throw new SystemException("请求参数不完整");
            }
            AuctionOrder order = auctionOrderService.queryById(id);
            if(order==null){
                throw new SystemException("订单不存在");
            }
            //设置收货人手机号  名称  地址  备注   邮寄类型
            String cneeMobile=CNEE_MOBILE;
            String cneeName=CNEE_NAME;
            String cneeAddress=CNEE_ADDRESS;
            String cneeRemark=CNEE_REMARK;

            JSONObject j=new JSONObject();
            j.put("orderId",order.getAuctionOrderId());
            j.put("cneeMobile",cneeMobile);
            j.put("cneeName",cneeName);
            j.put("cneeAddress",cneeAddress);
            j.put("cneeRemark",cneeRemark);
            if(StringUtils.isNotBlank(expressType)){
                j.put("expressType",Integer.parseInt(expressType));
            }
            String message = post(j, method);
            //解析转转退货返回信息
            JSONObject jsonObject = JSONObject.parseObject(message);
            //先判断该转转订单对应退货订单是否存在 不存在则新建
            AuctionReturnOrder returnOrder1 = auctionReturnOrderService.queryByAuctionOrderId(order.getAuctionOrderId());
            if(returnOrder1==null) {
                //新建超人-转转退货订单
                AuctionReturnOrder returnOrder = new AuctionReturnOrder();
                String returnId = UUID.randomUUID().toString().replaceAll("-", "");
                returnOrder.setId(returnId);
                returnOrder.setOrderNo(order.getOrderNo());
                returnOrder.setAuctionOrderId(order.getAuctionOrderId());
                returnOrder.setCneeMobile(cneeMobile);
                returnOrder.setCneeName(cneeName);
                returnOrder.setCneeAddress(cneeAddress);
                returnOrder.setCneeRemark(cneeRemark);
                returnOrder.setExpressType(1);
                auctionReturnOrderService.add(returnOrder);
                //更新转转订单状态
                order.setOrderStatus(jsonObject.getString("status"));   //状态,退货申请完成：refundApplied
                order.setReturnId(returnId);
                auctionOrderService.saveUpdate(order);
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
     * 转转推送rest接口
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value="recycle/rest")
    @ResponseBody
    public JSONObject rest(HttpServletRequest request, HttpServletResponse response){
        JSONObject returnObject=new JSONObject();   //返回的数据
        returnObject.put("code",0);
        returnObject.put("msg","success");
        boolean tip=true;                           //是否转转订单业务
        try {
            String sign = request.getParameter("sign");
            String method = request.getParameter("method");
            String timestamp = request.getParameter("timestamp");
            String params = request.getParameter("params");

            if(StringUtils.isBlank(sign)||StringUtils.isBlank(method)||StringUtils.isBlank(timestamp)||StringUtils.isBlank(params)){
                throw new SystemException("参数不完整");
            }
            //先判断签名是否正确
            String code=method+timestamp+params+APP_KEY;
            log.info("请求方法："+method+"  时间戳："+timestamp+"   超人生成签名为："+MD5Util.md5Encode(code)+"  转转签名："+sign+"  请求参数："+params);
            if(!MD5Util.md5Encode(code).equals(sign)){
                throw new SystemException("签名错误");
            }
            JSONObject jsonObject = JSONObject.parseObject(params);
            log.info("业务参数："+jsonObject);
            if(method.equals("qc.callback.simgift")){
                //电信号卡推送结果
                tip=false;
                auctionOrderService.simgift(jsonObject);
            }

            if(tip) {
                //判断订单是否存在
                AuctionOrder order = auctionOrderService.queryByAuctionOrderId(jsonObject.getString("orderId"));
                if (order == null) {
                    throw new SystemException("订单信息不存在");
                }
                //判断请求方法
                if (method.equals("qc.callback.recived")) {
                    //已收货
                    auctionOrderService.recived(jsonObject, order);

                } else if (method.equals("qc.callback.sorted")) {
                    //分拣完成
                    auctionOrderService.sorted(jsonObject, order);

                } else if (method.equals("qc.callback.qcDone")) {
                    //质检完成
                    auctionOrderService.qcDone(jsonObject, order);

                } else if (method.equals("qc.callback.photographDone")) {
                    //拍照完成
                    auctionOrderService.photographDone(jsonObject, order);

                } else if (method.equals("qc.callback.delivered")) {
                    //已发货
                    auctionOrderService.delivered(jsonObject, order);

                } else if (method.equals("qc.callback.evaluate")) {
                    //估价完成
                    auctionOrderService.evaluate(jsonObject, order);

                } else if (method.equals("qc.callback.sold")) {
                    //已售出
                    auctionOrderService.sold(jsonObject, order);

                } else if (method.equals("qc.callback.auctionfail")) {
                    //流拍
                    auctionOrderService.auctionfail(jsonObject, order);
                }
            }

        } catch (SystemException e) {
            returnObject.put("code",1);
            returnObject.put("msg",e.getMessage());
        } catch (Exception e) {
            returnObject.put("code",1);
            returnObject.put("msg",e.getMessage());
        }
        log.info("转转推送结束返回："+returnObject);
        return returnObject;
    }


    /**
     * 转转平台异常code编码字典表
     * @return
     */
    public static String getExceptionCode(int code){
        String message="";
        switch (code){
            case 0:
                message="成功";
                break;
            case 100:
                message="数据校验异常";
                break;
            case 101:
                message="时间差超限";
                break;
            case 102:
                message="PartnerId异常";
                break;
        }
        return message;
    }


    /**
     * 转转拍卖订单列表
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/recycle/auctionOrderList")
    public ModelAndView auctionOrderList(HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {

        String returnView ="auction/auctionOrderList";
        return new ModelAndView(returnView);
    }



    /**
     * 转转拍卖订单列表刷新数据
     */
    @RequestMapping(value = "recycle/auctionOrderList/queryListForPage")
    public void auctionOrderForPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        SessionUser su = getCurrentUser(request);
        if (su.getType() != SystemConstant.USER_TYPE_SYSTEM&&su.getType() != SystemConstant.USER_TYPE_CUSTOMER_SERVICE) {
            throw new SystemException("对不起，您没有操作权限!");
        }
        String orderNo=request.getParameter("query_orderNo");
        String auctionOrderId=request.getParameter("query_actionOrderId");
        Page page = getPageByRequest(request);
        AuctionOrder order=new AuctionOrder();
        order.setOrderNo(orderNo);
        order.setAuctionOrderId(auctionOrderId);
        order.setPage(page);
        order.setIsDel(0);
        List<AuctionOrder> list = auctionOrderService.queryListForPage(order);
        page.setData(list);
        this.renderJson(response, page);

    }



    /**
     * 转转拍卖退货订单列表
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/recycle/auctionReturnOrderList")
    public ModelAndView auctionReturnOrderList(HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {

        String returnView ="auction/auctionReturnOrderList";
        return new ModelAndView(returnView);
    }


    /**
     * 转转拍卖退货订单列表刷新数据
     */
    @RequestMapping(value = "recycle/auctionReturnOrderList/queryListForPage")
    public void auctionReturnOrderForPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        SessionUser su = getCurrentUser(request);
        if (su.getType() != SystemConstant.USER_TYPE_SYSTEM&&su.getType() != SystemConstant.USER_TYPE_CUSTOMER_SERVICE) {
            throw new SystemException("对不起，您没有操作权限!");
        }
        String orderNo=request.getParameter("query_orderNo");
        String auctionOrderId=request.getParameter("query_actionOrderId");
        Page page = getPageByRequest(request);
        AuctionReturnOrder order=new AuctionReturnOrder();
        order.setOrderNo(orderNo);
        order.setAuctionOrderId(auctionOrderId);
        order.setPage(page);
        List<AuctionReturnOrder> list = auctionReturnOrderService.queryListForPage(order);
        page.setData(list);
        this.renderJson(response, page);
    }


    /**
     * 参数方法生成签名
     * @param method
     * @param params
     */
    public static void apply(String method,String params){
        String timeStamp=sf.format(new Date());
        String code=method+timeStamp+params+APP_KEY;
        String s = MD5Util.md5Encode(code);
        log.info("时间戳："+timeStamp);
        log.info("签名为："+s);
    }


    public static void main(String[] args) throws Exception {
//        AuctionOrderController o=new AuctionOrderController();
//        HttpServletRequest request=null;
//        HttpServletResponse response=null;
//        JSONObject json=new JSONObject();
//        json.put("province","河北省");
//        json.put("city","河北省");
//        json.put("type","bm");
//        json.put("cateId",101);
//        json.put("lon","40.025858");
//        json.put("lat","116.369199");
//        String returnId= UUID.randomUUID().toString().replaceAll("-","");
//        String code="{     \"partnerOrderId \":\"20180208100142350\",     \"orderId\":\"26574193947657\",     \"qcCode\":\"10001\",     \"doneTime\":\"2018-04-19 18:31:45\",     \"expressCode \":\"669822033322\",     \"expressCompany \":\"shunfeng\" }";
//        log.info("请求参数："+code);
//        apply("qc.callback.delivered",code);
        //JSONObject jsonAddress = getCheckMobileAddress(json);   //验机地址
        //log.info(jsonAddress);
        //o.createSaleOrder(request,response);        //创建保卖订单
        //o.applyPhotograph(request,response);        //申请拍照
        //o.confirmSale(request,response);            //确认售卖
        //  o.refundApply(request,response);          //退货申请
        sendTelecomCard();
    }


    public static void sendTelecomCard() throws Exception {
        String method="gift.toyoupin.sim.delivered";
        JSONObject j=new JSONObject();
        String params="{\n" +
                "\t\"iccidStart\": \"12340000\",\n" +
                "\t\"iccidEnd\": \"12349999\",\n" +
                "\t\"count\": 10000,\n" +
                "\t\"deliverTime\": 1514739661000,\n" +
                "\t\"expressType\": \"shunfeng\",\n" +
                "\t\"expressCode\": \"56223456567744\",\n" +
                "\t\"allyId\": 1000000110\n" +
                "}";
        j=JSONObject.parseObject(params);
        post(j,method);


    }


}
