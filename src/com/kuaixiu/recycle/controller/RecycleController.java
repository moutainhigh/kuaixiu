package com.kuaixiu.recycle.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.exception.SystemException;
import com.common.paginate.Page;
import com.common.util.*;
import com.common.wechat.common.util.StringUtils;
import com.google.common.collect.Maps;
import com.kuaixiu.recycle.entity.*;
import com.kuaixiu.recycle.service.*;
import com.system.api.entity.ResultData;
import com.system.basic.user.entity.SessionUser;
import com.system.basic.user.service.SessionUserService;
import com.system.constant.ApiResultConstant;
import com.system.constant.SystemConstant;
import jodd.util.StringUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.*;

/**
 * @author: anson
 * @CreateDate: 2017年11月7日 上午9:29:31
 * @version: V 1.0
 * 回收请求控制器
 */
@Controller
public class RecycleController extends BaseController {


    private static final Logger log = Logger.getLogger(RecycleController.class);

    @Autowired
    private SessionUserService sessionUserService;
    @Autowired
    private RecycleOrderService recycleOrderService;
    @Autowired
    private RecycleCustomerService recycleCustomerService;
    @Autowired
    private AlipayService alipayService;
    @Autowired
    private RecycleSystemService recycleSystemService;
    @Autowired
    private TestZhimaMerchantCreditlifeFundPay testZhimaMerchantCreditlifeFundPay;
    @Autowired
    private RecycleCheckItemsService recycleCheckItemsService;
    @Autowired
    private RecycleWechatService recycleWechatService;
    @Autowired
    private RecycleCouponService recycleCouponService;
    @Autowired
    private SearchModelService searchModelService;
    @Autowired
    private SourceService sourceService;
    @Autowired
    private CouponAddValueService couponAddValueService;

    /**
     * 基础访问接口地址
     */
    private static final String baseUrl = SystemConstant.RECYCLE_URL;
    /**
     * 需要加密的数据名
     */
    private static final String cipherdata = SystemConstant.RECYCLE_REQUEST;
    //回收订单状态变化时调用，更改状态  参数签名
    private static final String autograph = "HZYNKJ@SUPER2017";
    private static final String batchId = "A016";

    /**
     * 欢GO首页
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/ty_wap/")
    public String goIndex(HttpServletRequest request, HttpServletResponse response) {
        request.getRequestedSessionId();

//		Cookie[] cookies = request.getCookies();
//		if(cookies==null){
//			log.info("cookie为空");
//		}else{
//			for (Cookie cookie : cookies) {
//				log.info(cookie.getName()+"  "+cookie.getValue());
//			}
//
//			Cookie cookie = new Cookie("testCookie","15356152347");
//			// 顶级域名下，所有应用都是可见的
//			cookie.setPath("/");
//			// 添加Cookie
//			response.addCookie(cookie);
//		}


        return "ty_wap/index1";
    }

    /**
     * 获取品牌列表
     */

    @RequestMapping(value = "recycle/getBrandList")
    public void getBrandList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        String url = baseUrl + "getbrandlist";
        try {
            String categoryid = request.getParameter("categoryid");
            JSONObject requestNews = new JSONObject();
            JSONObject code = new JSONObject();
            code.put("categoryid", categoryid);
            String realCode = AES.Encrypt(code.toString());  //加密
            requestNews.put(cipherdata, realCode);
            //发起请求
            String getResult = AES.post(url, requestNews);
            //对得到结果进行解密
            jsonResult = getResult(AES.Decrypt(getResult));
            result.setResult(jsonResult);
            result.setResultCode("0");
            result.setSuccess(true);
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        renderJson(response, result);
    }


    /**
     * 获取机型列表
     */
    @RequestMapping(value = "recycle/getModelList")
    public void getModelList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        String url = baseUrl + "getmodellist";
        String seachId = "";
        try {
            //获取请求数据
            JSONObject params = getPrarms(request);
            String pageIndex = params.getString("pageindex");
            String pageSize = params.getString("pagesize");
            String categoryid = params.getString("categoryid");
            String brandId = params.getString("brandid");
            String keyword = params.getString("keyword");
            //将选中的品牌存入 session
            request.getSession().setAttribute("selectBrandId", brandId);
            //将搜索关键字空格去除
            if (StringUtils.isNotBlank(keyword)) {
                //存储搜索关键字
                SearchModel searchModel = new SearchModel();
                seachId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
                searchModel.setId(seachId);
                searchModel.setKeyWord(keyword);
                searchModel.setIsTrue("0");
                searchModelService.getDao().add(searchModel);
                //去掉关键字空格
                keyword = keyword.replaceAll(" ", "");
            }
            JSONObject requestNews = new JSONObject();
            //调用接口需要加密的数据
            JSONObject code = new JSONObject();
            code.put("pageindex", pageIndex);
            code.put("pagesize", pageSize);
            code.put("categoryid", categoryid);
            code.put("brandid", brandId);
            code.put("keyword", keyword);
            String realCode = AES.Encrypt(code.toString());  //加密
            requestNews.put(cipherdata, realCode);
            //发起请求
            String getResult = AES.post(url, requestNews);
            //对得到结果进行解密
            jsonResult = getResult(AES.Decrypt(getResult));
            //将结果中的产品id转为string类型  json解析 long类型精度会丢失
            //防止返回机型信息为空
            if (StringUtil.isNotBlank(jsonResult.getString("datainfo")) && !(jsonResult.getJSONArray("datainfo")).isEmpty()) {
                JSONArray jq = jsonResult.getJSONArray("datainfo");
                JSONObject jqs = (JSONObject) jq.get(0);
                JSONArray j = jqs.getJSONArray("sublist");
                for (int i = 0; i < j.size(); i++) {
                    JSONObject js = j.getJSONObject(i);
                    js.put("productid", js.getString("productid"));
                    //如果该机型的图片地址为空 则使用默认图片地址
                    String imgaePath = js.getString("modellogo");
                    if (StringUtil.isBlank(imgaePath)) {
                        String realUrl = request.getRequestURL().toString();
                        String domain = realUrl.replace(request.getRequestURI(), "");
                        String path = domain + "/" + SystemConstant.DEFAULTIMAGE;
                        js.put("modellogo", path);
                    }
                    //session储存选择的品牌和品牌id
                    request.setAttribute("selectBrandId", brandId);
                    request.setAttribute("selectBrandName", jqs.getString("brandname"));
                }

            } else {
                SearchModel searchModel = searchModelService.queryById(seachId);
                searchModel.setIsTrue("1");
                searchModel.setMessage("该机型未找到");
                searchModelService.getDao().update(searchModel);
                getResult(result, null, false, "3", "该机型未找到");
                log.info("该机型未找到");
            }

            if (StringUtils.isBlank(result.getResultCode())) {
                result.setResult(jsonResult);
                result.setResultCode("0");
                result.setSuccess(true);
            }
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            SearchModel searchModel = searchModelService.queryById(seachId);
            searchModel.setIsTrue("1");
            searchModel.setMessage(e.getMessage());
            searchModelService.getDao().update(searchModel);
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        renderJson(response, result);
    }


    /**
     * 获取指定机型的检测选项：
     */
    @RequestMapping(value = "recycle/getCheckList")
    public void getCheckList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        String url = baseUrl + "getchecklist";
        try {
            //获取请求数据
            JSONObject params = getPrarms(request);
            String productId = params.getString("productid");
            String modelName = params.getString("modelName");
            if (StringUtil.isBlank(productId)) {
                throw new SystemException("参数不完整");
            }
            JSONObject requestNews = new JSONObject();
            //调用接口需要加密的数据
            JSONObject code = new JSONObject();
            code.put("productid", productId);
            String realCode = AES.Encrypt(code.toString());  //加密
            requestNews.put(cipherdata, realCode);
            //发起请求
            String getResult = AES.post(url, requestNews);
            //对得到结果进行解密
            jsonResult = getResult(AES.Decrypt(getResult));
            //数据中vdescription字段的值转化为json格式前端解析错误 将其删除
//            if (StringUtil.isNotBlank(jsonResult.getString("datainfo"))) {
//                JSONArray j = jsonResult.getJSONArray("datainfo");
//                for (int i = 0; i < j.size(); i++) {
//                    JSONObject js = (JSONObject) j.get(i);
//                    JSONArray next = js.getJSONArray("value");
//                    for (int m = 0; m < next.size(); m++) {
//                        JSONObject n = (JSONObject) next.get(m);
//                        n.remove("vdescription");
//                    }
//                }
//            }
            //session储存机型名称
            request.setAttribute("selectModelName", modelName);
            result.setResult(jsonResult);
            result.setResultCode("0");
            result.setSuccess(true);
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        renderJson(response, result);
    }


    /**
     * 获取机型价格   如果openId存在则存储检测信息   如果手机号存在也存储检测信息
     * 目前环保手机有些估价会出现0元，现考虑把估价为0元的手机统一修改为5元
     */
    @RequestMapping(value = "recycle/getPrice")
    public void getPrice(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        String url = baseUrl + "getprice";
        try {
            //获取请求数据
            JSONObject params = getPrarms(request);
            String productId = params.getString("productid");
            String imei = params.getString("imei");
            String items = params.getString("items");
            String openId = params.getString("openId");
            String loginMobile = params.getString("loginMobile");

//            //转换items格式“1,2|2,6|4,15|5,19|6,21|35,114|11,43......”-->“2,6,15,19,21,114,43......”
//            StringBuilder sb = new StringBuilder();
//            String[] itemses = items.split("\\|");
//            for (int i = 0; i < itemses.length; i++) {
//                String[] item = itemses[i].split(",");
//                sb.append(item[1]);
//                if (itemses.length - 1 != i) {
//                    sb.append(",");
//                }
//            }
//            items = sb.toString();

            JSONObject requestNews = new JSONObject();
            //调用接口需要加密的数据
            JSONObject code = new JSONObject();
            code.put("productid", productId);
            if (StringUtils.isNotBlank(imei)) {
                code.put("imei", imei);
            }
            code.put("items", items);
            //session储存产品id 和检测项id用来后期下单保存到数据库
            request.getSession().setAttribute("productId", productId);
            request.getSession().setAttribute("recycleItems", items);
            String realCode = AES.Encrypt(code.toString());  //加密
            requestNews.put(cipherdata, realCode);
            //发起请求
            String getResult = AES.post(url, requestNews);
            //对得到结果进行解密
            jsonResult = getResult(AES.Decrypt(getResult));
            //将quoteid转为string
            if (StringUtil.isNotBlank(jsonResult.getString("datainfo"))) {
                JSONObject j = (JSONObject) jsonResult.get("datainfo");
                j.put("quoteid", j.getString("quoteid"));
                String price = j.getString("price");
                if (price.equals("0")) {
                    price = "5";
                }
                request.getSession().setAttribute(j.getString("quoteid"), price);
                //如果openId存在则修改检测项和上次评估价
                if (StringUtils.isNotBlank(openId)) {
                    RecycleCheckItems r = new RecycleCheckItems();
                    r.setWechatId(openId);
                    List<RecycleCheckItems> list = recycleCheckItemsService.queryList(r);
                    if (!list.isEmpty()) {
                        RecycleCheckItems checkItems = list.get(0);
                        checkItems.setItems(items);
                        checkItems.setLastPrice(checkItems.getPrice());
                        checkItems.setQuoteId(j.getString("quoteid"));
                        recycleCheckItemsService.saveUpdate(checkItems);
                    }

                }


                //查询该手机号是否有存储过
                if (StringUtils.isNotBlank(loginMobile)) {
                    RecycleCheckItems t = new RecycleCheckItems();
                    t.setLoginMobile(loginMobile);
//                    List<RecycleCheckItems> recycleCheckItems = recycleCheckItemsService.queryList(t);
                    // 得到当前session中品牌名称  品牌id  机型名称
                    String selectBrandId = (String) request.getSession().getAttribute("selectBrandId");
                    String selectBrandName = (String) request.getSession().getAttribute("selectBrandName");
                    String selectModelName = (String) request.getSession().getAttribute("selectModelName");

//                    if (recycleCheckItems.isEmpty()) {
                    //新增
                    t.setItems(items);
                    t.setPrice(new BigDecimal(price));
                    t.setProductId(productId);
                    t.setBrand(selectBrandName);
                    t.setBrandId(selectBrandId);
                    t.setRecycleModel(selectModelName);
                    t.setQuoteId(j.getString("quoteid"));
                    recycleCheckItemsService.add(t);
//                    } else {
//                        RecycleCheckItems checkItems = recycleCheckItems.get(0);
//                        //修改
//                        checkItems.setItems(items);
//                        checkItems.setPrice(new BigDecimal(price));
//                        checkItems.setLastPrice(checkItems.getPrice());
//                        checkItems.setProductId(productId);
//                        checkItems.setBrand(selectBrandName);
//                        checkItems.setBrandId(selectBrandId);
//                        checkItems.setRecycleModel(selectModelName);
//                        checkItems.setQuoteId(j.getString("quoteid"));
//                        recycleCheckItemsService.saveUpdate(checkItems);
//                    }


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
        renderJson(response, result);

    }


    /**
     * 通过手机号  获取用户上次评估信息
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "recycle/getNewsByMobile")
    public void getNewsByMobile(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        String tip = "0";   // 0表示该手机号有存储过  1表示未存储过
        try {
            //获取请求数据
            JSONObject params = getPrarms(request);
            String mobile = params.getString("mobile");
            String categoryid = params.getString("categoryid");
            RecycleCheckItems t = new RecycleCheckItems();
            t.setMobile(mobile);
            List<RecycleCheckItems> recycleCheckItems = recycleCheckItemsService.queryList(t);
            if (recycleCheckItems.isEmpty()) {
                //未检测存储过
                tip = "1";
            } else {
                RecycleCheckItems checkItems = recycleCheckItems.get(0);
                JSONObject news = new JSONObject();
                news.put("brandName", checkItems.getBrand());
                news.put("brandId", checkItems.getBrandId());
                news.put("modelName", checkItems.getRecycleModel());
                news.put("productId", checkItems.getProductId());
                news.put("lastPrice", checkItems.getLastPrice());
                // 获取该机型的良品价 和图片地址
                Map<String, Object> maps = getGoodPirce(checkItems.getBrandId(), checkItems.getProductId(), categoryid);
                int goodPirce = (int) maps.get("maxPrice");
                news.put("goodPrice", goodPirce);
                news.put("modelLogo", maps.get("imgUrl"));
                jsonResult.put("news", news);
            }
            jsonResult.put("isCheck", tip);

            result.setResult(jsonResult);
            result.setResultCode("0");
            result.setSuccess(true);

        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        renderJson(response, result);

    }


    /**
     * 获取机型询价信息
     */
    @RequestMapping(value = "recycle/getQuoteDetail")
    public void getQuoteDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        String url = baseUrl + "getquotedetail";
        try {
            //获取请求数据
            JSONObject params = getPrarms(request);
            String quoteId = params.getString("quoteid");

            JSONObject requestNews = new JSONObject();
            //调用接口需要加密的数据
            JSONObject code = new JSONObject();
            code.put("quoteid", quoteId);
            String realCode = AES.Encrypt(code.toString());  //加密
            requestNews.put(cipherdata, realCode);
            //发起请求
            String getResult = AES.post(url, requestNews);
            //对得到结果进行解密
            jsonResult = getResult(AES.Decrypt(getResult));

            result.setResult(jsonResult);
            result.setResultCode("0");
            result.setSuccess(true);

        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        renderJson(response, result);

    }


    /**
     * 信用回收提交订单
     */
    @RequestMapping(value = "recycle/createOrder")
    public void createOrder(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        String url = baseUrl + "createorder";
        try {
            //获取请求数据
            JSONObject params = getPrarms(request);
            String quoteid = params.getString("quoteid");
            String takeTime = params.getString("takeTime");
            RecycleOrder order = recycleOrderService.queryByQuoteId(quoteid);
            if (order == null) {
                throw new SystemException("订单不存在");
            }
            RecycleCustomer cust = recycleCustomerService.queryById(order.getCustomerId());
            if (cust == null) {
                throw new SystemException("用户信息不存在");
            }

            String areaname = getAddress(cust.getArea() + " " + cust.getAddress()); //用户完整地址 设置成回收规定格式
            JSONObject requestNews = new JSONObject();
            //调用接口需要加密的数据
            JSONObject code = new JSONObject();
            code.put("quoteid", quoteid);
            code.put("pricetype", "1");  // 1代表支付宝结款
            code.put("payment_name", cust.getName() + "(M超人测试)");
            code.put("payment_account", cust.getUserId());
            code.put("contactname", cust.getName());
            code.put("contactphone", "15356152347");
            code.put("areaname", areaname);
            code.put("address", cust.getAddress());
            code.put("idcard", cust.getCertNo());
            String realCode = AES.Encrypt(code.toString());  //加密
            requestNews.put(cipherdata, realCode);
            //发起请求
            String getResult = AES.post(url, requestNews);
            //对得到结果进行解密
            jsonResult = getResult(AES.Decrypt(getResult));
            //订单号传过来为long型转为string防止精度丢失
            if (StringUtil.isNotBlank(jsonResult.getString("datainfo"))) {
                JSONObject j = (JSONObject) jsonResult.get("datainfo");
                j.put("orderid", j.getString("orderid"));
                order.setOrderNo(j.getString("orderid"));
                order.setOrderType(1);   //改为正式订单
                order.setTakeTime(takeTime);
                order.setOrderStatus(1); //已提交
                order.setMailType(1);    //邮寄方式超人系统推送
                //调用顺丰快递接口
                // String mailNo=postSfOrder(order);
                // order.setSfOrderNo(mailNo);

                //更新记录存入订单号
                recycleOrderService.saveUpdate(order);


                if (order.getRecycleType() == 0) {
                    //下单成功后如果是信用回收则调用转账接口
                    //	alipayService.transfer(order, "1", "3");
                    //转账成功后提交数据反馈模板 预支付
                    //	TestZhimaDataBatchFeedback.testZhimaDataBatchFeedback(order,cust,request,ReadJson.PREPARE);
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
        renderJson(response, result);
    }


    /**
     * 回收订单状态变化时调用，更改状态
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "recycle/getOrderState")
    public void getOrderState(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        try {
            String sign = request.getParameter("sign");
            String orderNo = request.getParameter("orderNo");
            String orderState = request.getParameter("orderState");
            String orderPrice = request.getParameter("orderPrice");
            String MD5sign = "orderNo=" + orderNo + "&orderState=" + orderState +
                    "&orderPrice=" + orderPrice + autograph;
            MD5sign = MD5Util.md5Encode(MD5sign);
            if (StringUtils.isBlank(orderNo) || StringUtils.isBlank(orderState) || StringUtils.isBlank(sign)) {
                throw new SystemException(ApiResultConstant.resultCode_str_1001, ApiResultConstant.resultCode_1001);
            } else if (!sign.equals(MD5sign)) {
                throw new SystemException(ApiResultConstant.resultCode_str_1003, ApiResultConstant.resultCode_1003);
            }
            RecycleOrder recycleOrder1 = recycleOrderService.queryByOrderNo(orderNo);
            if (recycleOrder1 == null) {
                result.setResultCode("3001");
                result.setSuccess(false);
                result.setResultMessage("订单不存在");
            } else {
                RecycleOrder recycleOrder = new RecycleOrder();
                if (Integer.valueOf(orderState) == 0) {
                    //0是取消订单，要判断是否使用加价券，如果使用就返还
                    if (StringUtils.isNotBlank(recycleOrder1.getCouponId())) {
                        cancelOrder(orderNo, recycleOrder1.getCouponId());
                    }
                }
                recycleOrder.setOrderNo(orderNo);
                recycleOrder.setOrderStatus(Integer.valueOf(orderState));
                //状态是7时，传orderPrice议价后订单价格，其他状态不需要传；单位元
                if (Integer.valueOf(orderState) == 7) {
                    if (StringUtils.isNotBlank(orderPrice)) {
                        recycleOrder.setNegotiationPrice(new BigDecimal(orderPrice));
                    }
                }
                recycleOrderService.updateByOrderStatus(recycleOrder);
                result.setResultCode("0");
                result.setSuccess(true);
            }
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        renderJson(response, result);
    }


    /**
     * 返还加价券
     *
     * @param orderNo
     * @return
     */
    private void cancelOrder(String orderNo, String couponId) throws Exception {
        if (StringUtils.isBlank(orderNo)) {
            throw new SystemException("订单号为空");
        }
        //删除订单加价券使用记录
        recycleOrderService.deleteCouponIdByOrderStatus(orderNo);
        recycleCouponService.updateForNoUse(couponId);
    }


    /**
     * 微信平台回收提交订单  先保存超人平台    调用回收接口成功后再转为正常订单
     */
    @RequestMapping(value = "recycle/wechatCreateOrder")
    public void wechatCreateOrder(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        String url = baseUrl + "createorder";
        RecycleOrder order = new RecycleOrder();
        try {
            //获取请求数据
            JSONObject params = getPrarms(request);
            String quoteid = params.getString("quoteId");
            String openId = params.getString("openId");
            String name = params.getString("customerName");
            String mobile = params.getString("mobile");
            String province = params.getString("province");
            String city = params.getString("city");
            String area = params.getString("area");
            String address = params.getString("address");
            String recycleType = params.getString("recycleType");   //支付类型 1支付宝收款  2话费充值
            String payMobile = params.getString("payMobile");
            String imagePath = params.getString("imagePath");
            String detail = params.getString("detail");
            String takeTime = params.getString("takeTime");
            String source = params.getString("fm");     //  1公众号   2欢GO
            String mailType = params.getString("mailType"); //  1超人系统推送  2用户自行邮寄
            String note = params.getString("note");
            String couponCode = params.getString("couponCode");//加价券编码
            if (StringUtil.isBlank(quoteid) || StringUtil.isBlank(name) || StringUtil.isBlank(mobile) ||
                    StringUtil.isBlank(province) || StringUtil.isBlank(city) || StringUtil.isBlank(area) ||
                    StringUtil.isBlank(address) || StringUtil.isBlank(recycleType) || StringUtil.isBlank(payMobile)) {
                throw new SystemException("请填写完整信息");
            }
            //对重要信息作详细判断
            if (Integer.valueOf(recycleType) == 2) {
                if (payMobile.length() != 11 || mobile.length() != 11) {
                    throw new SystemException("请输入正确的手机号码");
                }
            }
            //判断数据 是否符合规范
            if (name.length() > 12 || address.length() > 64) {
                throw new SystemException("部分信息长度过长");
            }
            //确定来源，没有则默认微信公众号来源
            source = recycleOrderService.isHaveSource(order, source);
            List<String> sources = sourceService.getDao().queryByType(1);
            Boolean isSend10Coupon = false;
            if (sources.contains(source)) {
                isSend10Coupon = true;
            }
            RecycleCoupon recycleCoupon = new RecycleCoupon();
            if (StringUtils.isNotBlank(couponCode) && !isSend10Coupon) {
                recycleCoupon = recycleCouponService.queryByCode(couponCode);
                if (recycleCoupon == null) {
                    throw new SystemException("加价码输入错误");
                } else if (1 == recycleCoupon.getIsUse()) {
                    throw new SystemException("加价码已被使用");
                }
            }

            //设置下单间隔时间,最少3秒
            Long time = System.currentTimeMillis();
            Object requestTimes = request.getSession().getAttribute("newTimes");
            if (requestTimes == null) {
                request.getSession().setAttribute("newTimes", time);
            } else {
                long realTime = (long) (requestTimes);
                if ((time - realTime) < 5000) {
                    throw new SystemException("您下单过于频繁，请稍后重试!");
                }
            }

            //转化时间格式
            if (StringUtil.isNotBlank(takeTime)) {
                takeTime = recycleOrderService.getDate(takeTime);
            }
            //转化地址
            String areaname = recycleOrderService.getAreaname(province, city, area);

            //先保存订单到超人平台再调用回收平台下单接口  返回成功则更新订单状态
            String id = UUID.randomUUID().toString().replace("-", "");
            String customerId = UUID.randomUUID().toString().replace("-", "");
            order.setId(id);
            order.setOrderNo("");
            order.setOrderType(1);             //正式订单
            order.setRecycleType(1);           //普通回收
            order.setExchangeType(Integer.parseInt(recycleType));
            order.setPercent(0);
            order.setPreparePrice(new BigDecimal(0));
            order.setImagePath(imagePath);
            order.setDetail(detail);
            order.setMobile(mobile);
            order.setCustomerId(customerId);
            order.setPayMobile(payMobile);
            order.setTakeTime(takeTime);
            order.setOrderStatus(1);           //订单已生成
            order.setNote(note);
            order.setCheckId(quoteid);
            if (StringUtils.isNotBlank(mailType)) {
                order.setMailType(Integer.parseInt(mailType));
            } else {
                order.setMailType(1);        //快递类型   1超人系统推送
            }
            //通过quoteid获取机型信息
            JSONObject postNews = recycleOrderService.postNews(quoteid);
            order.setProductName(postNews.getString("modelname"));
            order.setPrice(new BigDecimal(postNews.getString("price")));
            //判断该订单是否来源于微信小程序
            if (StringUtils.isNotBlank(openId)) {
                order.setWechatOpenId(openId);
            }
            //查询回收下单所有加价规则
            List<CouponAddValue> addValues = couponAddValueService.getDao().queryByType(1);
            //判断该订单来源确定是否使用加价券
            if (isSend10Coupon) {
                recycleCoupon = recycleOrderService.getCouponCode(mobile, request, addValues, order.getPrice());
                if (recycleCoupon != null) {
                    couponCode = recycleCoupon.getCouponCode();
                }
            } else {
                if (StringUtils.isNotBlank(couponCode)) {
                    if (order.getPrice().intValue() < recycleCoupon.getSubtraction_price().intValue()) {
                        throw new SystemException("加价券的使用不满足条件");
                    }
                }
            }
            if (StringUtils.isNotBlank(recycleCoupon.getId())) {
                order.setCouponId(recycleCoupon.getId());
                recycleCouponService.updateForUse(recycleCoupon);
            }
            recycleOrderService.add(order);     //添加预付订单记录

            //保存用户信息
            RecycleCustomer cust = new RecycleCustomer();
            cust.setId(customerId);
            cust.setName(name);
            cust.setMobile(mobile);
            cust.setProvince(province);
            cust.setCity(city);
            cust.setCounty(area);
            cust.setAddressType(2);  //系统地址
            cust.setArea(areaname);
            cust.setAddress(address);
            recycleCustomerService.add(cust);  //添加用户信息

            //请求回收平台调用下单接口
            JSONObject requestNews = new JSONObject();
            //调用接口需要加密的数据
            JSONObject code = new JSONObject();
            code.put("quoteid", quoteid);                                      //流水号
            code.put("pricetype", Integer.parseInt(recycleType));              //付款类型
            code.put("payment_name", name);                                    //结款户名
            code.put("payment_account", payMobile);                            //结款账户
            code.put("contactname", name);                                     //联系人
            code.put("contactphone", mobile);                                  //联系电话
            code.put("areaname", areaname);                                     //省市区
            code.put("address", address);                                      //详细地址
            if (StringUtils.isNotBlank(couponCode)) {
                //发送给回收平台加价券
                recycleOrderService.sendRecycleCoupon(code, recycleCoupon, addValues);
            }
            String realCode = AES.Encrypt(code.toString());  //加密
            requestNews.put(cipherdata, realCode);
            //发起请求
            String getResult = AES.post(url, requestNews);
            //对得到结果进行解密
            jsonResult = getResult(AES.Decrypt(getResult));

            //订单号传过来为long型转为string防止精度丢失
            if (StringUtil.isNotBlank(jsonResult.getString("datainfo"))) {
                JSONObject j = (JSONObject) jsonResult.get("datainfo");
                j.put("orderid", j.getString("orderid"));
                //订单提交回收平台成功 更新订单状态
                order = recycleOrderService.queryById(id);
                order.setOrderNo(j.getString("orderid"));
//                //更新回收订单：订单号
//                recycleOrderService.saveUpdate(order);
                //订单提交成功 当用户选择超人系统推送时  调用顺丰取件接口
                if (mailType.equals("1")) {
                    recycleOrderService.getPostSF(order, time, request);//调用顺丰取件接口
                }
            } else {
                order.setIsDel(1);
            }
            //更新回收订单
            recycleOrderService.saveUpdate(order);

            getResult(result, jsonResult, true, "0", "成功");
            //下单成功后更新下单间隔时间
            request.getSession().setAttribute("times", time);
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        renderJson(response, result);
    }

    /**
     * 支付宝地址 为    上海 上海 黄浦          ------ 浙江 杭州 江干
     * 回收地址规范     上海市 黄浦区            ------ 浙江省 杭州市 江干区
     * 省份区别  西藏 宁夏 新疆 广西 内蒙古
     * 北京市  天津市 上海市  重庆市 其他省后面都加省
     * 支付宝地址转回收地址规范
     *
     * @param code
     * @return
     */
    public static String getAddress(String code) {
        StringBuffer sb = new StringBuffer();
        List<String> s = new ArrayList<String>();
        List<String> p = new ArrayList<String>();
        String[] plist = {"西藏", "宁夏", "新疆", "广西", "内蒙古"};
        String[] slist = {"北京", "天津", "上海", "重庆"};
        s.addAll(Arrays.asList(plist));
        p.addAll(Arrays.asList(slist));
        if (StringUtils.isNotBlank(code)) {
            String[] split = code.split(" ");
            //处理省份
            if (s.contains(split[0])) {
                sb.append(split[0]);
            } else if (p.contains(split[0])) {
                sb.append(split[0] + "市 ");
            } else {
                sb.append(split[0] + "省 ");
            }
            //处理市，区
            sb.append(split[1] + "市 " + split[2] + "区 " + split[3]);
        }
        return sb.toString();
    }

    /**
     * 获取订单列表
     */
    @RequestMapping(value = "recycle/getOrderList")
    @ResponseBody
    public ResultData getOrderList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        String url = baseUrl + "getorderlist";
        try {
            //获取请求数据
            JSONObject params = getPrarms(request);
            String contactphone = params.getString("contactphone");
            String pageindex = params.getString("pageindex");
            String pagesize = params.getString("pagesize");
            String starttime = params.getString("starttime");
            String endtime = params.getString("endtime");
            String processstatus = params.getString("processstatus");

            if (StringUtils.isBlank(contactphone)) {
                Cookie cookie = CookiesUtil.getCookieByName(request, Consts.COOKIE_H5_PHONE);
                if (cookie == null || StringUtils.isBlank(cookie.getValue())) {
                    return getResult(result, null, false, "2", "请输入手机号");
                }
                String cookiePhone = cookie.getValue();
                contactphone = URLDecoder.decode(cookiePhone, "UTF-8");
            } else {
                String[] dname = request.getServerName().split("\\.");
                CookiesUtil.setCookie(response, Consts.COOKIE_H5_PHONE, contactphone, CookiesUtil.prepare(dname), 999999999);
            }

            JSONObject requestNews = new JSONObject();
            //调用接口需要加密的数据
            JSONObject code = new JSONObject();
            code.put("contactphone", contactphone);
            if (StringUtils.isNotBlank(pageindex)) {
                code.put("pageindex", Integer.parseInt(pageindex));
            }
            if (StringUtils.isNotBlank(pagesize)) {
                code.put("pagesize", Integer.parseInt(pagesize));
            }
            if (StringUtils.isNotBlank(starttime)) {
                code.put("starttime", starttime);
            }
            if (StringUtils.isNotBlank(endtime)) {
                code.put("endtime", endtime);
            }
            if (StringUtils.isNotBlank(processstatus)) {
                code.put("processstatus", processstatus);
            }
            String realCode = AES.Encrypt(code.toString());  //加密
            requestNews.put(cipherdata, realCode);
            //发起请求
            String getResult = AES.post(url, requestNews);
            //对得到结果进行解密
            jsonResult = getResult(AES.Decrypt(getResult));
            //结合订单号查询更多信息
            JSONArray array = jsonResult.getJSONArray("datainfo");
            if (array.size() > 0) {
                for (int i = 0; i < array.size(); i++) {
                    JSONObject quote = (JSONObject) array.get(i);
                    String orderId = quote.getString("orderid");
                    if (StringUtil.isNotBlank(orderId)) {
                        RecycleOrder order = recycleOrderService.queryByOrderNo(orderId);
                        if (order != null) {
                            quote.put("raiseOrderNo", order.getIncreaseOrderNo());
                            quote.put("prepare_price", order.getPreparePrice());
                            if (StringUtil.isBlank(quote.getString("modelpic"))) {
                                //如果访问机型的图片为空 则使用默认图片
                                quote.put("modelpic", order.getImagePath());
                            }
                            if (StringUtils.isNotBlank(order.getCouponId())) {
                                RecycleCoupon recycleCoupon = recycleCouponService.queryById(order.getCouponId());
                                if (recycleCoupon != null) {
                                    JSONObject json = new JSONObject();
                                    json.put("batchId", recycleCoupon.getBatchId());
                                    json.put("couponCode", recycleCoupon.getCouponCode());
                                    json.put("couponName", recycleCoupon.getCouponName());
                                    json.put("pricingType", recycleCoupon.getPricingType());
                                    String orderPrice = quote.getString("orderprice");
                                    if (recycleCoupon.getPricingType() == 1) {
                                        json.put("couponPrice", recycleCoupon.getStrCouponPrice().toString() + "%");
                                        Integer addCouponPrice = (new BigDecimal(orderPrice).divide(new BigDecimal("100")).multiply(recycleCoupon.getStrCouponPrice())).intValue();
                                        if (recycleCoupon.getUpperLimit() != null && addCouponPrice > recycleCoupon.getUpperLimit().intValue()) {
                                            quote.put("addCouponPrice", recycleCoupon.getUpperLimit().toString());
                                            quote.put("orderprice", new BigDecimal(orderPrice).add(recycleCoupon.getUpperLimit()).toString());
                                        } else {
                                            quote.put("addCouponPrice", String.valueOf(addCouponPrice));
                                            quote.put("orderprice", (new BigDecimal(orderPrice).add((new BigDecimal(orderPrice).divide(new BigDecimal("100")).multiply(recycleCoupon.getStrCouponPrice())))).toString());
                                        }
                                    } else {
                                        json.put("couponPrice", recycleCoupon.getStrCouponPrice().toString());
                                        quote.put("addCouponPrice", recycleCoupon.getStrCouponPrice().toString());
                                        quote.put("orderprice", (new BigDecimal(orderPrice).add(recycleCoupon.getStrCouponPrice())).toString());
                                    }
                                    json.put("beginTime", recycleCoupon.getBeginTime());
                                    json.put("endTime", recycleCoupon.getEndTime());
                                    quote.put("Coupon", json);
                                }
                            }
                        }
                    }
                }
            }
            result.setResult(jsonResult);
            result.setResultCode("0");
            result.setSuccess(true);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        return result;
//        renderJson(response, result);
    }


    /**
     * 获取订单明细
     */
    @RequestMapping(value = "recycle/getOrderDetail")
    public void getOrderDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        String url = baseUrl + "getorderdetail";
        try {
            //获取请求数据
            JSONObject params = getPrarms(request);
            String orderid = params.getString("orderid");
            if (StringUtil.isBlank(orderid)) {
                throw new SystemException("请求参数为空!");
            }
            JSONObject requestNews = new JSONObject();
            //调用接口需要加密的数据
            JSONObject code = new JSONObject();
            code.put("orderid", orderid);
            String realCode = AES.Encrypt(code.toString());  //加密
            requestNews.put(cipherdata, realCode);
            //发起请求
            String getResult = AES.post(url, requestNews);
            //对得到结果进行解密
            jsonResult = getResult(AES.Decrypt(getResult));
            JSONObject info = jsonResult.getJSONObject("datainfo");
            if (info != null) {
                RecycleOrder r = recycleOrderService.queryByOrderNo(orderid);
                if (r != null) {
                    info.put("raiseOrderNo", r.getIncreaseOrderNo());
                    RecycleCustomer cust = recycleCustomerService.queryById(r.getCustomerId());
                    if (cust != null) {
                        info.put("name", cust.getName());
                        info.put("mobile", cust.getMobile());
                        info.put("pay_mobile", r.getPayMobile());
                        info.put("address", cust.getArea() + " " + cust.getAddress());
                    }
                    info.put("prepare_price", r.getPreparePrice());
                    info.put("percent", r.getPercent());
                    info.put("take_time", r.getTakeTime());
                    info.put("detail", r.getDetail());
                    info.put("recycle_type", r.getExchangeType());
                    info.put("mail_type", r.getMailType().toString());
                    info.put("note", r.getNote());
                    if (StringUtil.isBlank(info.getString("modelpic"))) {
                        //如果图片为空 则使用默认图片
                        String realUrl = request.getRequestURL().toString();
                        String domain = realUrl.replace(request.getRequestURI(), "");
                        String path = domain + "/" + SystemConstant.DEFAULTIMAGE;
                        info.put("modelpic", path);
                    }
                    // 根据物流订单号 判断是否需要用户填写快递单号
                    if (StringUtils.isBlank(r.getSfOrderNo())) {
                        info.put("alert", "1");  //需要填写
                    } else {
                        info.put("alert", "0");
                        info.put("mail_order_no", r.getSfOrderNo());
                    }

                    if (StringUtils.isNotBlank(r.getCouponId())) {
                        RecycleCoupon recycleCoupon = recycleCouponService.queryById(r.getCouponId());
                        if (recycleCoupon != null) {
                            JSONObject json = new JSONObject();
                            json.put("batchId", recycleCoupon.getBatchId());
                            json.put("couponCode", recycleCoupon.getCouponCode());
                            json.put("couponName", recycleCoupon.getCouponName());
                            json.put("pricingType", recycleCoupon.getPricingType());
                            String orderPrice = info.getString("orderprice");
                            if (recycleCoupon.getPricingType() == 1) {
                                json.put("couponPrice", recycleCoupon.getStrCouponPrice().toString() + "%");
                                Integer addCouponPrice = (new BigDecimal(orderPrice).divide(new BigDecimal("100")).multiply(recycleCoupon.getStrCouponPrice())).intValue();
                                if (recycleCoupon.getUpperLimit() != null && addCouponPrice > recycleCoupon.getUpperLimit().intValue()) {
                                    info.put("addCouponPrice", recycleCoupon.getUpperLimit().toString());
                                    info.put("orderprice", new BigDecimal(orderPrice).add(recycleCoupon.getUpperLimit()).toString());
                                } else {
                                    info.put("addCouponPrice", String.valueOf(addCouponPrice));
                                    info.put("orderprice", (new BigDecimal(orderPrice).add((new BigDecimal(orderPrice).divide(new BigDecimal("100")).multiply(recycleCoupon.getStrCouponPrice())))).toString());
                                }
                            } else {
                                json.put("couponPrice", recycleCoupon.getStrCouponPrice().toString());
                                info.put("addCouponPrice", recycleCoupon.getStrCouponPrice().toString());
                                info.put("orderprice", (new BigDecimal(orderPrice).add(recycleCoupon.getStrCouponPrice())).toString());
                            }
                            json.put("beginTime", recycleCoupon.getBeginTime());
                            json.put("endTime", recycleCoupon.getEndTime());
                            info.put("Coupon", json);
                        }
                    }

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
        renderJson(response, result);
    }


    /**
     * 提交预订单
     */
    @RequestMapping(value = "recycle/commitOrder")
    public void commitOrder(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        try {
            //获取请求数据
            JSONObject params = getPrarms(request);
            String quoteId = params.getString("quoteId");
            String imagePath = params.getString("imagePath");
            String detail = params.getString("detail");   //机型故障信息
            if (StringUtil.isBlank(quoteId) || StringUtil.isBlank(detail) || StringUtil.isBlank(imagePath)) {
                throw new SystemException("参数不完整");
            }
            //将图片存入session 再调起芝麻信用数据后存入数据
            request.getSession().setAttribute("imagePath", imagePath);
            request.getSession().setAttribute("quoteId", quoteId);
            request.getSession().setAttribute("detail", detail);
            //芝麻信用下单地址
            String url = "https://zmhatcher.zmxy.com.cn/creditlife/operatorEntrance.htm?productId=2017110101000222123419688237&channel=creditlife&callBackUrl=" + SystemConstant.ZHIMA_CALLBACKURL;
            jsonResult.put("path", Base64Util.getBase64(url));  //通过base64加密后传送
            result.setResult(jsonResult);
            result.setResultCode("0");
            result.setSuccess(true);
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        renderJson(response, result);
    }


    /**
     * 回收商发起付款请求
     */
    @RequestMapping(value = "recycle/getMoney")
    public void getMoney(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        try {
            String channelid = request.getParameter("channelid");
            String data = request.getParameter("cipherdata");
            if (StringUtils.isBlank(channelid) || StringUtils.isBlank(data)) {
                throw new SystemException(ApiResultConstant.resultCode_str_1001, ApiResultConstant.resultCode_1001);
            } else if (!channelid.equals("1")) {
                throw new SystemException(ApiResultConstant.resultCode_str_1003, ApiResultConstant.resultCode_1003);
            }
            //解析加密字段 data
            JSONObject resultData = getResult(AES.Decrypt(data));
            String orderNo = resultData.getString("order_no");
            if (StringUtils.isBlank(orderNo)) {
                throw new SystemException(ApiResultConstant.resultCode_str_1001, ApiResultConstant.resultCode_1001);
            }
            RecycleOrder o = recycleOrderService.queryByOrderNo(orderNo);
            if (o == null) {
                throw new SystemException(ApiResultConstant.resultCode_str_3003, ApiResultConstant.resultCode_3003);
            }

            //发起转账操作  类型 ：2支付尾款   发起类型：3系统订单发起
            alipayService.transfer(o, "2", "3");

            result.setResult(jsonResult);
            result.setResultCode("0");
            result.setSuccess(true);
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        renderJson(response, result);
    }


    /**
     * 回收商修改订单状态  主要是取消操作
     */
    @RequestMapping(value = "recycle/updateOrderStatus")
    public void updateOrderStatus(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        try {
            String channelid = request.getParameter("channelid");
            String data = request.getParameter("cipherdata");
            if (StringUtils.isBlank(channelid) || StringUtils.isBlank(data)) {
                throw new SystemException(ApiResultConstant.resultCode_str_1001, ApiResultConstant.resultCode_1001);
            } else if (!channelid.equals("1")) {
                throw new SystemException(ApiResultConstant.resultCode_str_1003, ApiResultConstant.resultCode_1003);
            }
            //解析加密字段 data
            JSONObject resultData = getResult(AES.Decrypt(data));
            String orderNo = resultData.getString("order_no");
            if (StringUtils.isBlank(orderNo)) {
                throw new SystemException(ApiResultConstant.resultCode_str_1001, ApiResultConstant.resultCode_1001);
            }
            RecycleOrder o = recycleOrderService.queryByOrderNo(orderNo);
            if (o == null) {
                throw new SystemException(ApiResultConstant.resultCode_str_3003, ApiResultConstant.resultCode_3003);
            }
            String type = resultData.getString("type");
            if (!type.equals("1") && !type.equals("0")) {
                throw new SystemException(ApiResultConstant.resultCode_str_2004, ApiResultConstant.resultCode_2004);
            }

            if (type.equals("1")) {
                //取消订单操作
                o.setOrderStatus(SystemConstant.DEFAULT_PAGE_SIZE);
                //如果是信用回收 已支付过预付款 则需发起扣款

            } else {
                String price = resultData.getString("price");
                if (StringUtils.isBlank(price)) {
                    throw new SystemException("发起议价时价格不能为空");
                }
                if (o.getRecycleType() == 1) {
                    //普通回收  按最新返回价格转账
                    o.setPrice(new BigDecimal(price));
                    alipayService.transfer(o, "2", "3");
                } else {
                    //信用回收  用(订单预支付价格-议价价格)  判断是代扣还是付尾款
                    int compareTo = o.getPreparePrice().compareTo(new BigDecimal(price));
                    if (compareTo == 1) {
                        //最终价格小于预付款的价格  发起代扣
                        o.setFinalPrice(new BigDecimal(price));
                        testZhimaMerchantCreditlifeFundPay.OrderFundPay(o, 3);
                    } else if (compareTo == 0) {
                        //相等  无需支付尾款订单结束 更改订单状态已完成
                        o.setOrderStatus(RecycleOrder.SUCCESS);
                        o.setFinalPrice(o.getPreparePrice());
                        recycleOrderService.saveUpdate(o);
                    } else {
                        //最终价格大于预付款的价格 发起支付尾款
                        o.setFinalPrice(new BigDecimal(price));
                        alipayService.transfer(o, "2", "3");

                    }
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
        renderJson(response, result);
    }


    /**
     * 用户订单详情填写快递单号
     */
    @RequestMapping(value = "recycle/mailOrderNo")
    public void mailOrderNo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        try {
            //获取请求数据
            JSONObject params = getPrarms(request);
            String orderNo = params.getString("orderNo");
            String mailOrderNo = params.getString("mailOrderNo");
            if (StringUtil.isBlank(orderNo) || StringUtil.isBlank(mailOrderNo)) {
                throw new SystemException("参数不完整");
            }
            RecycleOrder order = recycleOrderService.queryByOrderNo(orderNo);
            if (order == null) {
                throw new SystemException("订单不存在");
            }
            order.setSfOrderNo(mailOrderNo);
            recycleOrderService.saveUpdate(order);
            result.setResult(jsonResult);
            result.setResultCode("0");
            result.setSuccess(true);
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        renderJson(response, result);
    }


    /**
     * 芝麻反馈调试接口 预支付
     */
    @RequestMapping(value = "recycle/json")
    public void show(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        try {
            //获取请求数据
            JSONObject params = getPrarms(request);
            String quoteId = params.getString("quoteId");
            RecycleOrder order = recycleOrderService.queryByQuoteId(quoteId);
            RecycleCustomer cust = recycleCustomerService.queryById(order.getCustomerId());
            TestZhimaDataBatchFeedback.testZhimaDataBatchFeedback(order, cust, request, ReadJson.PREPARE);
            result.setResult(jsonResult);
            result.setResultCode("0");
            result.setSuccess(true);

        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        renderJson(response, result);
    }

    /**
     * 返回结果解析
     *
     * @param originalString
     * @return
     */
    public static JSONObject getResult(String originalString) {
        JSONObject result = (JSONObject) JSONObject.parse(originalString);
        if (result.getString("result") != null && !result.getString("result").equals("RESPONSESUCCESS")) {
            throw new SystemException(result.getString("msg"));
        }
        return result;
    }

    /**
     * 返回错误码解析
     */
    public static String getReason(String code) {
        String result = "";
        if (code.equals("PARAMSERR")) {
            result = "参数错误";
        } else if (code.equals("SESSIONERR")) {
            result = "sessionid有关错误";
        } else if (code.equals("SYSTEMERR")) {
            result = "系统错误";
        } else if (code.equals("SIGNATUREERR")) {
            result = "签名错误";
        } else if (code.equals("UNKNOWNERR")) {
            result = "未知错误";
        }
        return result;
    }


    /**
     * 回收订单列表
     */
    @RequestMapping(value = "/recycle/list")
    public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
        SessionUser su = getCurrentUser(request);
        if (su == null) {
            throw new SystemException("您离开系统时间过长，请重新登录");
        }
        RecycleSystem r = new RecycleSystem();
        r.setIsDel(0);
        List<RecycleSystem> list = recycleSystemService.queryList(r);
        request.setAttribute("userId", su.getUserId());
        request.setAttribute("fromSystems", list);
        String returnView = "recycle/listForAdmin";
        return new ModelAndView(returnView);
    }


    /**
     * 刷新数据
     */
    @RequestMapping(value = "recycle/order/queryListForPage")
    public void queryListForPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        SessionUser su = getCurrentUser(request);
        if (su == null) {
            throw new SystemException("您离开系统时间过长，请重新登录");
        }
        if (su.getType() != SystemConstant.USER_TYPE_SYSTEM && su.getType() != SystemConstant.USER_TYPE_CUSTOMER_SERVICE) {
            throw new SystemException("对不起，您没有操作权限!");
        }
        // 获取查询条件
        String orderNo = request.getParameter("query_orderNo");
        String status = request.getParameter("query_orderStates");
        String mobile = request.getParameter("query_customerMobile");
        String queryStartTime = request.getParameter("query_startTime");
        String queryEndTime = request.getParameter("query_endTime");
        String fromSystem = request.getParameter("fromSystem");
        String isCoupon = request.getParameter("isCoupon");
        RecycleOrder r = new RecycleOrder();
        r.setOrderNo(orderNo);
        if (StringUtils.isNotBlank(fromSystem)) {
            r.setSourceType(Integer.valueOf(fromSystem));
        }
        if (StringUtil.isNotBlank(status)) {
            r.setOrderStatus(Integer.parseInt(status));
        }
        r.setMobile(mobile);
        r.setQueryStartTime(queryStartTime);
        r.setQueryEndTime(queryEndTime);
        r.setIsCoupon(isCoupon);
        Page page = getPageByRequest(request);
        r.setPage(page);
        List<RecycleOrder> list = recycleOrderService.queryListForPage(r);
        List<RecycleSystem> flist = recycleSystemService.queryList(null);
        for (RecycleOrder o : list) {
//            String price=recycleOrderService.div095(o.getPrice().toString());//加个乘以0.95
            o.setPrice(new BigDecimal(o.getPrice().toString()));
            if (StringUtils.isNotBlank(o.getCouponId())) {
                RecycleCoupon recycleCoupon = recycleCouponService.queryById(o.getCouponId());
                if (recycleCoupon.getPricingType() == 1) {
                    if (recycleCoupon.getStrCouponPrice().compareTo(new BigDecimal("5")) != 0) {
                        if (recycleCoupon.getAddPriceUpper() != null && recycleCoupon.getStrCouponPrice().intValue() > recycleCoupon.getAddPriceUpper().intValue()) {
                            o.setPrice(o.getPrice().add(recycleCoupon.getAddPriceUpper()));
                        } else {
                            o.setPrice(o.getPrice().add(new BigDecimal(recycleCoupon.getStrCouponPrice().toString())));
                        }
                    } else {
                        BigDecimal addCouponPrice = recycleOrderService.getAddCouponPrice(o.getPrice());
                        if (recycleCoupon.getAddPriceUpper() != null && addCouponPrice.intValue() > recycleCoupon.getAddPriceUpper().intValue()) {
                            o.setPrice(o.getPrice().add(recycleCoupon.getAddPriceUpper()));
                        } else {
                            o.setPrice(o.getPrice().add(new BigDecimal(addCouponPrice.toString())));
                        }
                    }
                } else {
                    o.setPrice(o.getPrice().add(new BigDecimal(recycleCoupon.getStrCouponPrice().toString())));
                }
            }
            for (RecycleSystem system : flist) {
                if (o.getSourceType() == system.getId()) {
                    o.setFm(system.getName());  //系统来源
                    break;
                }
            }
        }
        page.setData(list);
        this.renderJson(response, page);
    }


    /**
     * 回收订单详情
     */
    @RequestMapping(value = "recycle/order/detail")
    public ModelAndView detail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        SessionUser su = getCurrentUser(request);
        if (su == null) {
            throw new SystemException("您离开系统时间过长，请重新登录");
        }
        if (su.getType() != SystemConstant.USER_TYPE_SYSTEM && su.getType() != SystemConstant.USER_TYPE_CUSTOMER_SERVICE) {
            throw new SystemException("对不起，您没有操作权限!");
        }
        String id = request.getParameter("id");
        RecycleOrder o = recycleOrderService.queryById(id);
        if (o == null) {
            throw new SystemException("订单未找到！！");
        }
//        String price=recycleOrderService.div095(o.getPrice().toString());//加个乘以0.95
        o.setPrice(new BigDecimal(o.getPrice().toString()));
        RecycleCoupon recycleCoupon = new RecycleCoupon();
        if (StringUtils.isNotBlank(o.getCouponId())) {
            recycleCoupon = recycleCouponService.queryById(o.getCouponId());
            if (recycleCoupon.getPricingType() == 1) {
                if (recycleCoupon.getStrCouponPrice().compareTo(new BigDecimal("5")) != 0) {
                    if (recycleCoupon.getAddPriceUpper() != null && recycleCoupon.getStrCouponPrice().intValue() > recycleCoupon.getAddPriceUpper().intValue()) {
                        o.setPrice(o.getPrice().add(recycleCoupon.getAddPriceUpper()));
                        recycleCoupon.setCouponPrice(recycleCoupon.getAddPriceUpper().toString());
                    } else {
                        o.setPrice(o.getPrice().add(o.getPrice().divide(new BigDecimal("100")).multiply(recycleCoupon.getStrCouponPrice())));
                        recycleCoupon.setCouponPrice(o.getPrice().divide(new BigDecimal("100")).multiply(recycleCoupon.getStrCouponPrice()).toString());
                    }
                } else {
                    BigDecimal addCouponPrice = recycleOrderService.getAddCouponPrice(o.getPrice());
                    if (recycleCoupon.getAddPriceUpper() != null && addCouponPrice.intValue() > recycleCoupon.getAddPriceUpper().intValue()) {
                        o.setPrice(o.getPrice().add(recycleCoupon.getAddPriceUpper()));
                        recycleCoupon.setCouponPrice(recycleCoupon.getAddPriceUpper().toString());
                    } else {
                        o.setPrice(o.getPrice().add(addCouponPrice));
                        recycleCoupon.setCouponPrice(addCouponPrice.toString());
                    }
                }
            } else {
                o.setPrice(o.getPrice().add(new BigDecimal(recycleCoupon.getStrCouponPrice().toString())));
            }
        }
        RecycleCustomer cust = recycleCustomerService.queryById(o.getCustomerId());
        RecycleSystem fm = recycleSystemService.queryById(o.getSourceType());
        if (fm != null) {
            o.setFm(fm.getName());
        }
        request.setAttribute("coupon", recycleCoupon);
        request.setAttribute("order", o);
        request.setAttribute("cust", cust);
        String returnView = "recycle/detail";
        return new ModelAndView(returnView);
    }


    /**
     * 后台手动发起预支付
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "recycle/preparePay")
    public void preparePay(HttpServletRequest request, HttpServletResponse response) throws Exception {
        SessionUser su = getCurrentUser(request);
        if (su == null) {
            throw new SystemException("您离开系统时间过长，请重新登录");
        }
        if (su.getType() != SystemConstant.USER_TYPE_SYSTEM) {
            throw new SystemException("对不起，您没有操作权限!");
        }
        Map<String, Object> resultMap = Maps.newHashMap();
        // 获取查询条件
        String id = request.getParameter("id");
        RecycleOrder order = null;
        if (StringUtils.isBlank(id)) {
            order = recycleOrderService.queryById(id);
            if (order == null) {
                throw new SystemException("订单信息不存在");
            }
        }
        // 判断是否满足发起预支付的条件  回收系统发起转账
        boolean transfer = alipayService.transfer(order, "1", "1");
        if (!transfer) {
            throw new SystemException("调用转账接口失败,请重试");
        }
        //转账成功后提交数据反馈模板 预支付
        RecycleCustomer cust = recycleCustomerService.queryById(order.getCustomerId());
        TestZhimaDataBatchFeedback.testZhimaDataBatchFeedback(order, cust, request, ReadJson.PREPARE);
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
        renderJson(response, resultMap);
    }


    /**
     * 重新发起支付尾款
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "recycle/endPay")
    public void endPay(HttpServletRequest request, HttpServletResponse response) throws Exception {
        SessionUser su = getCurrentUser(request);
        if (su == null) {
            throw new SystemException("您离开系统时间过长，请重新登录");
        }
        if (su.getType() != SystemConstant.USER_TYPE_SYSTEM) {
            throw new SystemException("对不起，您没有操作权限!");
        }
        Map<String, Object> resultMap = Maps.newHashMap();
        // 获取查询条件
        String id = request.getParameter("id");
        RecycleOrder order = null;
        if (StringUtils.isBlank(id)) {
            order = recycleOrderService.queryById(id);
            if (order == null) {
                throw new SystemException("订单信息不存在");
            }
        }
        // 判断是否满足发起预支付的条件  回收系统支付尾款
        boolean transfer = alipayService.transfer(order, "2", "1");
        if (!transfer) {
            throw new SystemException("调用转账接口失败,请重试");
        }
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
        renderJson(response, resultMap);
    }


    /**
     * 重新发起扣款
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "recycle/deductPay")
    public void deductPay(HttpServletRequest request, HttpServletResponse response) throws Exception {
        SessionUser su = getCurrentUser(request);
        if (su == null) {
            throw new SystemException("您离开系统时间过长，请重新登录");
        }
        if (su.getType() != SystemConstant.USER_TYPE_SYSTEM) {
            throw new SystemException("对不起，您没有操作权限!");
        }
        Map<String, Object> resultMap = Maps.newHashMap();
        // 获取查询条件
        String id = request.getParameter("id");
        RecycleOrder order = null;
        if (StringUtils.isBlank(id)) {
            order = recycleOrderService.queryById(id);
            if (order == null) {
                throw new SystemException("订单信息不存在");
            }
        }
        // 判断是否满足发起扣款的条件  回收系统发起扣款
        boolean tip = testZhimaMerchantCreditlifeFundPay.OrderFundPay(order, 1);
        if (!tip) {
            throw new SystemException("调用扣款失败，请重试");
        }
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
        renderJson(response, resultMap);
    }


    /**
     * 根据机型获取最高报价
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "recycle/getMaxPrice")
    public void getMaxPrice(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        String url = baseUrl + "getmodellist";
        int maxPrice = 0;
        try {
            //获取请求数据
            JSONObject params = getPrarms(request);
            String productId = params.getString("productid");
            String categoryid = params.getString("categoryid");
            if (StringUtil.isBlank(productId)) {
                throw new SystemException("参数不完整");
            }
            JSONObject requestNews = new JSONObject();
            String brandId = (String) request.getSession().getAttribute("selectBrandId");
            //调用接口需要加密的数据
            JSONObject code = new JSONObject();
            code.put("pageindex", "1");
            code.put("pagesize", "500");
            code.put("brandid", brandId);
            code.put("categoryid", categoryid);
            String realCode = AES.Encrypt(code.toString());  //加密
            requestNews.put(cipherdata, realCode);
            String getResult = AES.post(url, requestNews);
            //对得到结果进行解密
            jsonResult = getResult(AES.Decrypt(getResult));
            //将接口返回的最高价打9折展示小数点默认去除
            if (StringUtil.isNotBlank(jsonResult.getString("datainfo"))) {
                JSONArray jq = jsonResult.getJSONArray("datainfo");
                JSONObject jqs = (JSONObject) jq.get(0);
                JSONArray j = jqs.getJSONArray("sublist");
                for (int i = 0; i < j.size(); i++) {
                    JSONObject js = j.getJSONObject(i);
                    if (js.getString("productid").equals(productId)) {
                        maxPrice = (int) (js.getInteger("productprice") * 0.9);
                        break;
                    }
                }
            }
            //产品id和金额存入  session后期下订单验证
            request.getSession().setAttribute(productId, String.valueOf(maxPrice));
            jsonResult.put("price", maxPrice);
            result.setResult(jsonResult);
            result.setResultCode("0");
            result.setSuccess(true);
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        renderJson(response, result);
    }


    /**
     * 通过产品id获取最高配置选项
     *
     * @param productId
     * @return
     * @throws Exception
     */
    public static String getMaxItem(String productId) throws Exception {
        StringBuffer items = new StringBuffer();   //需要传入的检测项
        String itemCode = null;
        JSONObject jsonResult = new JSONObject();
        String url = baseUrl + "getchecklist";
        JSONObject requestNews = new JSONObject();
        //调用接口需要加密的数据
        JSONObject code = new JSONObject();
        code.put("productid", productId);
        String realCode = AES.Encrypt(code.toString());  //加密
        requestNews.put(cipherdata, realCode);
        //发起请求
        String getResult = AES.post(url, requestNews);
        //对得到结果进行解密
        jsonResult = getResult(AES.Decrypt(getResult));
        //最终得到的最高价
        String price = null;
        //得到各检测项最高配置
        if (StringUtil.isNotBlank(jsonResult.getString("datainfo"))) {
            JSONArray j = jsonResult.getJSONArray("datainfo");
            JSONObject js = null;
            JSONArray next = null;
            JSONObject n = null;
            for (int i = 0; i < j.size(); i++) {
                js = (JSONObject) j.get(i);
                //规则 checkid为1的内存 取vlevelid最大值   其余检测项都取最小值 即为最高配置
                String checkId = js.get("checkid").toString();
                if (checkId.equals("1")) {
                    Integer itemMin = 0;
                    next = js.getJSONArray("value");
                    for (int m = 0; m < next.size(); m++) {
                        n = (JSONObject) next.get(m);
                        if (itemMin < n.getInteger("vlevelid")) {
                            itemMin = n.getInteger("vlevelid");
                        }
                    }
                    items.append(1).append(",").append(itemMin).append("|");
                } else {
                    Integer itemId = Integer.MAX_VALUE;    //检测项对应最高配置值
                    next = js.getJSONArray("value");
                    for (int m = 0; m < next.size(); m++) {
                        n = (JSONObject) next.get(m);
                        if (itemId > n.getInteger("vlevelid")) {
                            itemId = n.getInteger("vlevelid");
                        }
                    }
                    items.append(Integer.parseInt(checkId)).append(",").append(itemId).append("|");
                }

            }
        }
        String item = items.toString();
        itemCode = item.substring(0, item.length() - 1);
        return itemCode;
    }


    /**
     * 通过检测项和产品id获取回收价格
     *
     * @param productId
     * @param items
     * @return
     * @throws Exception
     */
    public static String getPrice(String productId, String items) throws Exception {
        String price = null;
        JSONObject jsonResult = new JSONObject();
        String url = baseUrl + "getprice";
        JSONObject requestNews = new JSONObject();
        //调用接口需要加密的数据
        JSONObject code = new JSONObject();
        code.put("productid", productId);
        code.put("items", items);
        String realCode = AES.Encrypt(code.toString());  //加密
        requestNews.put(cipherdata, realCode);
        //发起请求
        String getResult = AES.post(url, requestNews);
        //对得到结果进行解密
        jsonResult = getResult(AES.Decrypt(getResult));
        //将quoteid转为string
        if (StringUtil.isNotBlank(jsonResult.getString("datainfo"))) {
            JSONObject j = (JSONObject) jsonResult.get("datainfo");
            price = j.getString("price");
        }
        return price;
    }


    /**
     * Description: 通过微信openId和微信小程序检测出的用户机型和品牌获取机型品牌名称 品牌id 机型名称  机型id  产品id
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "recycle/getProductId")
    public void getProductId(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        String productId = null;   //通过回收结果返回的最终机型id
        try {
            //获取请求数据
            JSONObject params = getPrarms(request);
            String brand = params.getString("brand");
            String modelName = params.getString("modelName");
            String openId = params.getString("openId");
            String categoryid = params.getString("categoryid");
            if (StringUtil.isBlank(brand) || StringUtil.isBlank(modelName) || StringUtil.isBlank(openId)) {
                throw new SystemException("参数不完整");
            }
            //将微信小程序检测的机型名称转换成回收平台的机型名称
            Map<String, String> map = AES.getModelName(brand, modelName);
            JSONObject requestNews = new JSONObject();
            if (map.isEmpty()) {
                throw new SystemException("对应机型未找到!");
            }
            //通过转换过的机型 使用回收搜索接口得到对应机型id
            JSONObject code = new JSONObject();
            code.put("pageindex", "1");
            code.put("pagesize", "50");
            code.put("categoryid", categoryid);
            code.put("keyword", map.get("modelName").replaceAll(" ", ""));
            String realCode = AES.Encrypt(code.toString());  //加密
            requestNews.put(cipherdata, realCode);
            //发起获取对应productid请求
            String getResult = AES.post(baseUrl + "getmodellist", requestNews);
            //对得到结果进行解密
            code = getResult(AES.Decrypt(getResult));
            JSONArray product = code.getJSONArray("datainfo");
            if (product.isEmpty()) {
                throw new SystemException("对应机型不存在");
            }
            int goodPirce = 0;
            //判断返回的机型是否有多个
            if (product.size() > 1) {
                //如果有多个机型 则通过机型名称匹配唯一
                for (int i = 0; i < product.size(); i++) {
                    JSONObject object = (JSONObject) product.get(i);
                    JSONArray sublist = object.getJSONArray("sublist");
                    if (((JSONObject) sublist.get(0)).getString("modelname").equals(map.get("modelName"))) {
                        productId = object.getString("productid");
                        goodPirce = (int) (object.getInteger("productprice") * 0.9);
                    }
                }
            } else {
                JSONObject o = (JSONObject) product.get(0);
                JSONArray sublist = o.getJSONArray("sublist");
                productId = ((JSONObject) sublist.get(0)).getString("productid");
                goodPirce = (int) (((JSONObject) sublist.get(0)).getInteger("productprice") * 0.9);
            }
            if (StringUtils.isBlank(productId)) {
                throw new SystemException("对应机型id不存在");
            }

            //获取该机型的良品价
            //Map<String, Object> maps = getGoodPirce(map.get("brandId"), productId, categoryid);
            //int goodPirce = (int) maps.get("maxPrice");
            //获取该用户该机型上次的评估价
            Integer lastPrice = null;
            RecycleCheckItems checkItems = new RecycleCheckItems();
            checkItems.setWechatId(openId);
            checkItems.setWechatModel(modelName);
            List<RecycleCheckItems> checkList = recycleCheckItemsService.queryList(checkItems);
            if (!checkList.isEmpty() && checkList.get(0).getLastPrice() != null) {
                lastPrice = checkList.get(0).getLastPrice().intValue();
            }
            //将图片的http的链接转换为https返回  图片保存位置   项目根目录的 resource/brandLogo下
            String imageUrl = map.get("modelLogo");
            String savePath = serverPath(request.getServletContext().getRealPath("")) + System.getProperty("file.separator") + SystemConstant.IMAGE_PATH + System.getProperty("file.separator") + "recycleModel";
            log.info("图片保存路径:" + savePath);
            String modelUrl = getProjectUrl(request) + "/images/recycleModel/" + imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            //先判断文件是否存在  不存在则下载
            File f = new File(savePath + System.getProperty("file.separator") + (imageUrl.substring(imageUrl.lastIndexOf("/") + 1)));
            if (!f.exists()) {
                log.info("图片不存在，开始下载");
                FileUtil.download(imageUrl, imageUrl.substring(imageUrl.lastIndexOf("/") + 1), savePath);
            }

            //返回数据
            jsonResult.put("brandName", map.get("brandName"));
            jsonResult.put("brandId", map.get("brandId"));
            jsonResult.put("modelName", map.get("modelName"));
            jsonResult.put("productId", productId);
            jsonResult.put("modelId", map.get("modelId"));
            jsonResult.put("goodPrice", goodPirce);
            jsonResult.put("lastPrice", lastPrice);
            jsonResult.put("modelLogo", modelUrl);
            result.setResult(jsonResult);
            result.setResultCode("0");
            result.setSuccess(true);


            // 将用户手机品牌  品牌id  微信小程序检测的机型  以及对应的回收平台的进行名称存入数据库
            if (checkList.isEmpty()) {
                //新增
                checkItems.setBrandId(map.get("brandId"));
                checkItems.setModelId(map.get("modelId"));
                checkItems.setRecycleModel(map.get("modelName"));
                checkItems.setWechatModel(modelName);
                checkItems.setBrand(brand);
                checkItems.setProductId(productId);
                recycleCheckItemsService.add(checkItems);
                //初始化给予一次抽奖机会
                RecycleWechat recycleWechat = recycleWechatService.queryByOpenId(openId);
                if (recycleWechat.getTotalUse() < 1) {
                    recycleWechat.setTotalUse(1);
                    recycleWechatService.saveUpdate(recycleWechat);
                }
            } else {
                //修改
                RecycleCheckItems cr = checkList.get(0);
                cr.setBrandId(map.get("brandId"));
                cr.setModelId(map.get("modelId"));
                cr.setRecycleModel(map.get("modelName"));
                cr.setWechatModel(modelName);
                cr.setBrand(brand);
                cr.setProductId(productId);
                recycleCheckItemsService.saveUpdate(cr);
            }
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        renderJson(response, result);
    }


    /**
     * 获取机型良品价 和图片地址
     *
     * @param brandId
     * @param productId
     * @return
     */
    public Map<String, Object> getGoodPirce(String brandId, String productId, String categoryid) {
        Map<String, Object> map = new HashMap();
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        String url = baseUrl + "getmodellist";
        int maxPrice = 0;
        String imgUrl = "";
        try {
            JSONObject requestNews = new JSONObject();
            //调用接口需要加密的数据
            JSONObject code = new JSONObject();
            code.put("pageindex", "1");
            code.put("categoryid", categoryid);
            code.put("pagesize", "500");
            code.put("brandid", brandId);
            String realCode = AES.Encrypt(code.toString());  //加密
            requestNews.put(cipherdata, realCode);
            String getResult = AES.post(url, requestNews);
            //对得到结果进行解密
            jsonResult = getResult(AES.Decrypt(getResult));
            //将接口返回的最高价打9折展示小数点默认去除
            if (StringUtil.isNotBlank(jsonResult.getString("datainfo"))) {
                JSONArray jq = jsonResult.getJSONArray("datainfo");
                JSONObject jqs = (JSONObject) jq.get(0);
                map.put("categoryid", jqs.getString("categoryid"));
                JSONArray j = jqs.getJSONArray("sublist");
                for (int i = 0; i < j.size(); i++) {
                    JSONObject js = j.getJSONObject(i);
                    if (js.getString("productid").equals(productId)) {
                        maxPrice = (int) (js.getInteger("productprice"));
                        imgUrl = js.getString("modellogo");
                        map.put("maxPrice", maxPrice);
                        map.put("imgUrl", imgUrl);
                        break;
                    }
                }
            }

        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        return map;
    }


    /**
     * 通过微信openId和微信小程序检测出的用户机型
     * 获取回收机型良品价格 并返回击败百分之多少用户，和下周预估下降金额  如果用户是当天第一次评估 那么给予一次抽奖机会
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "recycle/getChangePrice")
    public void getChangePrice(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        String brandId = null;  //检测的品牌id
        List<RecycleCheckItems> list = null;
        RecycleCheckItems checkItems = new RecycleCheckItems();
        try {
            //获取请求数据
            JSONObject params = getPrarms(request);
            String imei = params.getString("imei");
            //判断是否是一键重新获取机型价格  即openId和用户机型是否存在值
            String openId = params.getString("openId");
            String loginMobile = params.getString("loginMobile");
            String modelName = params.getString("modelName");
            String categoryid = params.getString("categoryid");
            String productId = null;
            if (StringUtils.isBlank(openId) && StringUtils.isBlank(loginMobile)) {
                throw new SystemException("参数不完整!");
            }

            RecycleCheckItems r = new RecycleCheckItems();
            r.setWechatId(openId);
            r.setWechatModel(modelName);
            r.setLoginMobile(loginMobile);
            list = recycleCheckItemsService.queryList(r);
            if (list.isEmpty()) {
                throw new SystemException("您当前机型之前没有任何检测记录");
            }
            checkItems = list.get(0);
            productId = checkItems.getProductId();
            if (StringUtils.isBlank(productId)) {
                throw new SystemException("产品id不存在");
            }
            brandId = checkItems.getBrandId();

            //获取良品价
            Map<String, Object> maps = getGoodPirce(brandId, productId, categoryid);
            int goodPirce = (int) maps.get("maxPrice");

            JSONObject j = new JSONObject();
            //计算击败多少用户
            String news = getPercent(String.valueOf(goodPirce));
            j.put("price", goodPirce);
            j.put("percent", news.substring(0, news.indexOf("-")));
            j.put("tip", news.substring(news.indexOf("-") + 1));
            // ios的brandId为1  其他都是安卓机型
            BigDecimal price = new BigDecimal(String.valueOf(goodPirce));
            BigDecimal percent = new BigDecimal(SystemConstant.ANDROID_NEXTPRICE);
            if ("1".equals(brandId)) {
                percent = new BigDecimal(SystemConstant.IOS_NEXTPRICE);
            }
            j.put("nextPirce", (price.multiply(percent)).intValue());
            //判断是否为当日第一次检测  如果是则给予一次抽奖机会
            if (checkItems.getUpdateTime().getTime() < DateUtil.getStartTime().getTime()) {
                RecycleWechat wechat = new RecycleWechat();
                if (StringUtils.isNotEmpty(openId)) {
                    wechat = recycleWechatService.queryByOpenId(openId);
                } else {
                    wechat = recycleWechatService.queryLoginMobile(loginMobile);
                }
                if (wechat != null) {
                    if (wechat.getTotalUse() < 2) {
                        wechat.setTotalUse(wechat.getTotalUse() + 1);
                    }
                    recycleWechatService.saveUpdate(wechat);
                } else {
                    throw new SystemException("当前用户中奖信息为空");
                }
            }
            jsonResult.put("datainfo", j);
            result.setResult(jsonResult);
            result.setResultCode("0");
            result.setSuccess(true);

        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        renderJson(response, result);

    }


    /**
     * 通过oproductId和brandId算出良品价
     * 获取回收机型良品价格 并返回击败百分之多少用户，和下周预估下降金额  如果用户是当天第一次评估 那么给予一次抽奖机会
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "recycle/getDeclinePrice")
    public void getDeclinePrice(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        try {
            //获取请求数据
            JSONObject params = getPrarms(request);
            String loginMobile = get(params.getString("loginMobile"));
            String productId = get(params.getString("productId")); //平台机型id
            String brandId = get(params.getString("brandId")); //检测的品牌id
            String categoryid = get(params.getString("categoryid"));

            RecycleCheckItems t = new RecycleCheckItems();
            t.setLoginMobile(loginMobile);
            t.setBrandId(brandId);
            t.setProductId(productId);
            List<RecycleCheckItems> list = recycleCheckItemsService.queryList(t);
            if (list.isEmpty()) {
                recycleCheckItemsService.add(t);
            }

            //获取良品价
            Map<String, Object> maps = getGoodPirce(brandId, productId, categoryid);
            int goodPirce = (int) maps.get("maxPrice");

            JSONObject j = new JSONObject();
            //计算击败多少用户
            String news = getPercent(String.valueOf(goodPirce));
            j.put("price", goodPirce);
            j.put("percent", news.substring(0, news.indexOf("-")));
            j.put("tip", news.substring(news.indexOf("-") + 1));
            // ios的brandId为1  其他都是安卓机型
            BigDecimal price = new BigDecimal(String.valueOf(goodPirce));
            BigDecimal percent = new BigDecimal(SystemConstant.ANDROID_NEXTPRICE);
            if ("1".equals(brandId)) {
                percent = new BigDecimal(SystemConstant.IOS_NEXTPRICE);
            }
            j.put("nextPirce", (price.multiply(percent)).intValue());

            jsonResult.put("datainfo", j);
            result.setResult(jsonResult);
            result.setResultCode("0");
            result.setSuccess(true);

        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        renderJson(response, result);

    }

    /**
     * 通过品牌id和产品id获取机型最高价和击败百分之多少用户，和下周预估下降金额
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "recycle/getPriceById")
    public void getPriceById(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        try {
            //获取请求数据
            JSONObject params = getPrarms(request);
            String brandId = params.getString("brandId");
            String productId = params.getString("productId");
            String categoryid = params.getString("categoryid");
            if (StringUtils.isBlank(productId) || StringUtils.isBlank(brandId)) {
                throw new SystemException("参数不完整!");
            }
            //获取良品价
            Map<String, Object> maps = getGoodPirce(brandId, productId, categoryid);
            int goodPirce = (int) maps.get("maxPrice");

            JSONObject j = new JSONObject();
            //计算击败多少用户
            String news = getPercent(String.valueOf(goodPirce));
            j.put("price", goodPirce);
            j.put("percent", news.substring(0, news.indexOf("-")));
            j.put("tip", news.substring(news.indexOf("-") + 1));
            // ios的brandId为1  其他都是安卓机型
            BigDecimal price = new BigDecimal(String.valueOf(goodPirce));
            BigDecimal percent = new BigDecimal(SystemConstant.ANDROID_NEXTPRICE);
            if ("1".equals(brandId)) {
                percent = new BigDecimal(SystemConstant.IOS_NEXTPRICE);
            }
            j.put("nextPirce", (price.multiply(percent)).intValue());
            jsonResult.put("datainfo", j);
            result.setResult(jsonResult);
            result.setResultCode("0");
            result.setSuccess(true);

        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        renderJson(response, result);
    }

    public static void main(String[] args) {
//	   String p="qtWgcvPj1DRpDzwwYxh7tcopd422GtwwYvXc8c5NkCDnk/Aif74zCeEep1gX9LanIkRo9iFjifEmHF4cyrfj+FN1wXCPjHABqtj06UMdDEe4p5uKxyGxa2LcD0Cn1UNRJ55CR1B1DYQTl2M9ivdpjxjls15zl4qVCJMXPIsprWU=";
//	   System.out.println("回收解密");
//	   try {
//		System.out.println(AES.Decrypt(p));
//	} catch (Exception e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}

    }


    /**
     * 回收击败比 计算方法
     */
    public String getPercent(String price) {
        int realPrice = Integer.parseInt(price);
        int percent = 0;
        String msg = "";
        if (realPrice < 100) {
            percent = 3;
        } else if (realPrice >= 100 && realPrice < 200) {
            percent = 6;
        } else if (realPrice >= 200 && realPrice < 500) {
            percent = 12;
        } else if (realPrice >= 500 && realPrice < 800) {
            percent = 18;
        } else if (realPrice >= 800 && realPrice < 1000) {
            percent = 23;
        } else if (realPrice >= 1000 && realPrice < 1300) {
            percent = 35;
        } else if (realPrice >= 1300 && realPrice < 1500) {
            percent = 38;
        } else if (realPrice >= 1500 && realPrice < 1800) {
            percent = 40;
        } else if (realPrice >= 1800 && realPrice < 2000) {
            percent = 58;
        } else if (realPrice >= 2000 && realPrice < 2500) {
            percent = 68;
        } else if (realPrice >= 2500 && realPrice < 3000) {
            percent = 88;
        } else if (realPrice >= 3000) {
            percent = 95;
        }


//
//    	  if(realPrice<300){
//    		  percent=12;
//    		  msg="勤俭持家，吃土";
//    	  }else if(realPrice>=300&&realPrice<500){
//    		  percent=21;
//    		  msg="爱的深沉，喝粥";
//    	  }else if(realPrice>=500&&realPrice<1000){
//              percent=36;
//              msg="小家生活，吃饭";
//    	  }else if(realPrice>=1000&&realPrice<2000){
//              percent=58;
//              msg="高端大气，吃肉";
//    	  }else if(realPrice>=2000){
//              percent=66;
//    		  msg="挥金如土，大鱼大肉";
//    	  }

        return percent + "-" + msg;
    }


}
