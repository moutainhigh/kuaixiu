package com.kuaixiu.recycle.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.exception.SystemException;
import com.common.util.*;
import com.common.wechat.common.util.StringUtils;
import com.kuaixiu.prizechance.service.PrizeChanceService;
import com.google.gson.JsonArray;
import com.kuaixiu.recycle.entity.*;
import com.kuaixiu.recycle.recycleCard.CardEnum;
import com.kuaixiu.recycle.service.*;
import com.kuaixiu.recycleCoupon.entity.HsActivityCouponRole;
import com.kuaixiu.recycleCoupon.service.HsActivityCouponRoleService;
import com.kuaixiu.recycleCoupon.service.HsActivityCouponService;
import com.kuaixiu.videoCard.dao.VideoCardMapper;
import com.kuaixiu.videoCard.entity.VideoCard;
import com.kuaixiu.videoCard.service.VideoCardService;
import com.kuaixiu.videoUserRel.entity.VideoUserRel;
import com.kuaixiu.videoUserRel.service.VideoUserRelService;
import com.system.api.entity.ResultData;
import com.system.basic.dict.dao.DictMapper;
import com.system.basic.dict.entity.Dict;
import com.system.basic.dict.service.DictService;
import com.system.basic.user.service.SessionUserService;
import com.system.constant.SystemConstant;
import com.system.util.PageBean;
import jodd.util.StringUtil;
import net.sf.json.JSONString;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.*;
import java.math.BigDecimal;
/**
 * @author: anson
 * @CreateDate: 2017年11月7日 上午9:29:31
 * @version: V 1.0
 * 回收请求控制器(新接口)
 */
@Controller
public class RecycleNewController extends BaseController {

    private static final Logger log = Logger.getLogger(RecycleNewController.class);

    @Autowired
    private SessionUserService sessionUserService;
    @Autowired
    private RecycleOrderService recycleOrderService;
    @Autowired
    private RecycleCustomerService recycleCustomerService;
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
    @Autowired
    private HsActivityCouponService activityCouponService;
    @Autowired
    private HsActivityCouponRoleService activityCouponRoleService;
    @Autowired
    private PrizeChanceService prizeChanceService;

    /**
     * 基础访问接口地址
     */
    private static final String baseNewUrl = SystemConstant.RECYCLE_NEW_URL;
    /**
     * 需要加密的数据名
     */
    private static final String cipherdata = SystemConstant.RECYCLE_REQUEST;

    @Autowired
    private DictService dictService;

    /**
     * 获取品牌列表
     */
    @RequestMapping(value = "recycleNew/getBrandList")
    public void getBrandList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        String url = baseNewUrl + "getbrandlist";
        try {
            String categoryid = request.getParameter("categoryid");
//            categoryid="2";
            if(categoryid==null){
                     categoryid = "0";
            }
            // 如果是笔记本则调用数据库数据
            if(categoryid.equals("2")){
                JSONObject j=new JSONObject();
                JSONArray array=new JSONArray();
                Dict dict=new Dict();
                dict.setKey("macbook");
                List<Dict> dicts = dictService.queryList(dict);
                if(!dicts.isEmpty()){
                    array=JSONArray.parseArray(dicts.get(0).getValue());
                }
                j.put("datainfo",array);
                result.setResult(j);
            }else{
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
            }
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
    @RequestMapping(value = "recycleNew/getModelList")
    public void getModelList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        String url = baseNewUrl + "getmodellist";
        String seachId = "";
        try {
            //获取请求数据
            JSONObject params = getPrarms(request);
            String pageIndex = params.getString("pageindex");
            String pageSize = params.getString("pagesize");
            String categoryid = params.getString("categoryid");
//            categoryid="2";
            if(categoryid==null){
                     categoryid = "0";  
            }
            String brandId = params.getString("brandid");
            String keyword = params.getString("keyword");
            //将选中的品牌存入 session
            request.getSession().setAttribute("selectBrandId", brandId);
            //将搜索关键字空格去除
            if (StringUtils.isNotBlank(keyword)) {
                //存储搜索关键字
                seachId = recycleOrderService.saveKeyword(seachId, keyword);
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
                recycleOrderService.saveException(seachId, "该机型未找到");
                getResult(result, null, false, "3", "该机型未找到");
                log.info("该机型未找到");
            }

            result.setResult(jsonResult);
            result.setResultCode("0");
            result.setSuccess(true);

        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            recycleOrderService.saveException(seachId, e.getMessage());
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        renderJson(response, result);
    }

    /**
     * 获取机型名字图片
     */
    @RequestMapping(value = "recycleNew/getModelName")
    @ResponseBody
    public ResultData getModelNameImage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        String url = baseNewUrl + "getmodellist";
        try {
            //获取请求数据
            JSONObject params = getPrarms(request);
            String productId = params.getString("productId");
            String brandId = params.getString("brandId");

            JSONObject requestNews = new JSONObject();
            //调用接口需要加密的数据
            JSONObject code = new JSONObject();
            code.put("pageindex", 1);
            code.put("pagesize", 500);
            code.put("categoryid", null);
            code.put("brandid", brandId);
            String realCode = AES.Encrypt(code.toString());  //加密
            requestNews.put(cipherdata, realCode);
            //发起请求
            String getResult = AES.post(url, requestNews);
            //对得到结果进行解密
            jsonResult = getResult(AES.Decrypt(getResult));
            //将结果中的产品id转为string类型  json解析 long类型精度会丢失
            //防止返回机型信息为空
            JSONObject jsonObject = new JSONObject();
            if (StringUtil.isNotBlank(jsonResult.getString("datainfo")) && !(jsonResult.getJSONArray("datainfo")).isEmpty()) {
                JSONArray jq = jsonResult.getJSONArray("datainfo");
                JSONObject jqs = (JSONObject) jq.get(0);
                JSONArray j = jqs.getJSONArray("sublist");
                for (int i = 0; i < j.size(); i++) {
                    JSONObject js = j.getJSONObject(i);
                    if (productId.equals(js.getString("productid"))) {
                        jsonObject.put("modelLogo", js.getString("modellogo"));
                        jsonObject.put("modelName", js.getString("modelname"));
                        break;
                    }
                }
            }
            getResult(result, jsonObject, true, "0", "成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }


    /**
     * 获取指定机型的检测选项：
     */
    @RequestMapping(value = "recycleNew/getCheckList")
    public void getCheckList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        String url = baseNewUrl + "getchecklist";
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
    @RequestMapping(value = "recycleNew/getPrice")
    public void getPrice(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();

        try {
            //获取请求数据
            JSONObject params = getPrarms(request);
            String productId = params.getString("productid");
            String imei = params.getString("imei");
            String items = params.getString("items");
            String openId = params.getString("openId");
            String loginMobile = params.getString("loginMobile");
            String source = params.getString("fm");//来源

            String url = baseNewUrl + "getprice"+"?channelid="+source;
            //转换items格式“1,2|2,6|4,15|5,19|6,21|35,114|11,43......”-->“2,6,15,19,21,114,43......”
            StringBuilder sb = new StringBuilder();
            String[] itemses = items.split("\\|");
            for (int i = 0; i < itemses.length; i++) {
                String[] item = itemses[i].split(",");
                sb.append(item[1]);
                if (itemses.length - 1 != i) {
                    sb.append(",");
                }
            }
            items = sb.toString();
            JSONObject requestNews = new JSONObject();
            //调用接口需要加密的数据
            JSONObject code = new JSONObject();
            code.put("productid", productId);
            if (StringUtils.isNotBlank(imei)) {
                code.put("imei", imei);
            }
            code.put("items", items);
            String realCode = AES.Encrypt(code.toString());  //加密
            requestNews.put(cipherdata, realCode);
            //发起请求
            String getResult = AES.post(url, requestNews);
            //对得到结果进行解密
            jsonResult = getResult(AES.Decrypt(getResult));
            if (StringUtil.isNotBlank(jsonResult.getString("datainfo"))) {
                JSONObject j = (JSONObject) jsonResult.get("datainfo");
                j.put("quoteid", j.getString("quoteid"));
                String price = j.getString("price");
//                if (price.equals("0")) {
//                    price = "5";
//                }
                //异步保存数据
                if (StringUtils.isBlank(loginMobile)) {
                    Cookie cookie = CookiesUtil.getCookieByName(request, Consts.COOKIE_NEW_H5_PHONE);
                    if (cookie != null && StringUtils.isNotBlank(cookie.getValue())) {
                        String cookiePhone = cookie.getValue();
                        loginMobile = URLDecoder.decode(cookiePhone, "UTF-8");
                    }
                }
                HttpSession session = request.getSession();
                MyExecutor myExecutor = new MyExecutor();
                myExecutor.fun(session, j, openId, loginMobile, items, productId,
                        price, source, recycleCheckItemsService);
//                price = recycleOrderService.div095(price);//加个乘以0.95
                j.put("price", Integer.valueOf(price));
            }//新增估价数据之后查询该号码当天估价了几次,一天只增加一次抽奖机会
            int count = recycleCheckItemsService.queryCountByToday(loginMobile);
            if(count < 1){
                int i = prizeChanceService.updateCountByMobile(loginMobile);
                if(i<1){
                    log.info("估价后增加抽奖次数时异常");
                }
            }

            getResult(result, jsonResult, true, "0", "");
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
    @RequestMapping(value = "recycleNew/getNewsByMobile")
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
            getResult(result, jsonResult, true, "0", "成功");
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
    @RequestMapping(value = "recycleNew/getQuoteDetail")
    public void getQuoteDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        String url = baseNewUrl + "getquotedetail";
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

            getResult(result, jsonResult, true, "0", "成功");
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
    @RequestMapping(value = "recycleNew/createOrder")
    public void createOrder(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        String url = baseNewUrl + "createorder";
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

                //更新记录存入订单号
                recycleOrderService.saveUpdate(order);
//                if (order.getRecycleType() == 0) {
//                    //下单成功后如果是信用回收则调用转账接口
//                    	alipayService.transfer(order, "1", "3");
//                    //转账成功后提交数据反馈模板 预支付
//                    	TestZhimaDataBatchFeedback.testZhimaDataBatchFeedback(order,cust,request,ReadJson.PREPARE);
//                }
            }
            getResult(result, jsonResult, true, "0", "成功");
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        renderJson(response, result);
    }


    /**
     * 微信平台回收提交订单  先保存超人平台    调用回收接口成功后再转为正常订单
     */
    @RequestMapping(value = "recycleNew/wechatCreateOrder")
    public void wechatCreateOrder(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();

        RecycleOrder order = new RecycleOrder();
        try {
            //获取请求数据
            JSONObject params = getPrarms(request);

            String lovemoney = params.getString("loveMoney");//爱心捐款
            if(lovemoney==null){
                lovemoney="0";
            }
            String donationsName = params.getString("donationsName");//捐款人
            String donationsPhone = params.getString("donationsPhone");//捐款手机号
            String donationsEmail = params.getString("donationsEmail");//捐款邮箱
            String phone = params.getString("phone");//用户登录手机号
            String quoteid = params.getString("quoteId");
            String openId = params.getString("openId");
            String name = params.getString("customerName");
            String mobile = params.getString("mobile");
            String province = params.getString("province");
            String city = params.getString("city");
            String area = params.getString("area");
            String address = filter(params.getString("address"));
            String recycleType = params.getString("recycleType");   //支付类型 1支付宝收款  2话费充值
            String payMobile = params.getString("payMobile");
            String imagePath = params.getString("imagePath");
            String detail = params.getString("detail");
            String takeTime = params.getString("takeTime");
            String source = params.getString("fm");     //  1公众号   2欢GO
            String mailType = params.getString("mailType"); //  1超人系统推送  2用户自行邮寄
            String note = params.getString("note");
            String couponCode = params.getString("couponCode");//加价券编码

            String url = baseNewUrl + "createorder"+"?channelid="+source;

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
            RecycleCoupon recycleCoupon = new RecycleCoupon();
            if (StringUtils.isNotBlank(couponCode)) {
                recycleCoupon = recycleCouponService.queryByCode(couponCode);
                if (recycleCoupon == null) {
                    throw new SystemException("加价码输入错误");
                } else if (1 == recycleCoupon.getIsUse()) {
                    throw new SystemException("加价码已被使用");
                }
            }

            //设置下单间隔时间,最少5秒
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
            if(StringUtil.isNotBlank(phone)){
                order.setPhone(phone);
            }else{
                order.setPhone(mobile);
            }
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
            order.setDonationsName(donationsName);
            order.setDonationsPhone(donationsPhone);
            order.setDonationsEmail(donationsEmail);
            order.setLovemoney(new BigDecimal(lovemoney));//爱心捐款

            if (StringUtils.isNotBlank(mailType)) {
                order.setMailType(Integer.parseInt(mailType));
            } else {
                order.setMailType(1);        //快递类型   1超人系统推送
            }


            //通过quoteid获取机型信息
            JSONObject postNews = recycleOrderService.postNews(quoteid);
            BigDecimal price = new BigDecimal(postNews.getString("price"));
            //金额小于20不予下单
            if (price.compareTo(new BigDecimal("20")) == -1) {
                throw new SystemException("由于估值过低，需要与有价值的手机一起寄出，谢谢！");
            }
            order.setProductName(postNews.getString("modelname"));
            order.setBrandName(postNews.getString("brandname"));
            order.setPrice(price);
            //判断该订单是否来源于微信小程序
            if (StringUtils.isNotBlank(openId)) {
                order.setWechatOpenId(openId);
            }

//            List<HsActivityCouponRole> activityCouponRoles = activityCouponRoleService.queryList(null);
            if (StringUtils.isNotBlank(recycleCoupon.getId())) {
                order.setCouponId(recycleCoupon.getId());
                recycleCouponService.updateForUse(recycleCoupon);
            }
            int add1 = recycleOrderService.add(order);//添加预付订单记录

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
            code.put("areaname", areaname);                                     //省市区null
            code.put("address", address);                                      //详细地址order

            JSONArray jsonArray = new JSONArray();
            JSONObject json = new JSONObject();
            json.put("couponId", null);
            json.put("actType",2);
            json.put("addFee", Integer.parseInt("-"+lovemoney));
            json.put("percent", 0);
            json.put("up", "999999");
            json.put("low", "0");
            json.put("desc", "爱心捐款(捐款金额："+lovemoney+"元");
            jsonArray.add(json);
            code.put("coupon_rule", jsonArray.toJSONString());

            if (StringUtils.isNotBlank(couponCode)) {
                List<HsActivityCouponRole> activityCouponRoles = activityCouponRoleService.queryList(null);
                //发送给回收平台加价券
                recycleOrderService.sendActivityRecycleCoupon(code, recycleCoupon, activityCouponRoles,json);//追加爱心捐款 拼接进加价卷
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
                recycleOrderService.saveUpdate(order);
                //订单提交成功 当用户选择超人系统推送时  调用顺丰取件接口
                if (mailType.equals("1")) {
                    recycleOrderService.getPostSF(order, time, request);//调用顺丰取件接口
                }
            } else {
                order.setIsDel(1);
            }
            //更新回收订单
            recycleOrderService.saveUpdate(order);
            //下单成功发送短信
            SmsSendUtil.submitRecycleOrder(order.getMobile(), source,recycleSystemService);


            getResult(result, jsonResult, true, "0", "成功");
            //下单成功后更新下单间隔时间
            request.getSession().setAttribute("newTimes", time);
            //下单成功,且下单金额大于100,添加一次抽奖机会
            if(order.getPrice().compareTo(new BigDecimal("100"))==1){
                prizeChanceService.updateCountByMobile(phone);
            }
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        renderJson(response, result);
    }


    @Autowired
    private VideoCardMapper videoCardMapper;

    @Autowired
    private VideoUserRelService videoUserRelService;

    /*
     * @Author gqa
     * @Description 下单成功分配卡密，目前不适用
     * @Date 16:41 2019/8/21
     * @Param [order]
     * @return java.lang.String
     **/
    private String getVideoCard(RecycleOrder order){
        String msg="";
        if(order.getPrice()==null){
            return msg;
        }
        Integer cardType = CardEnum.getCardType(order.getPrice());
        VideoCard videoCard=new VideoCard();
        videoCard.setIsUse(0);
        videoCard.setType(cardType);
        VideoCard card = videoCardMapper.queryOne(videoCard);
        if(card!=null){
            card.setIsUse(1);
            videoCardMapper.update(card);
            VideoUserRel rel=new VideoUserRel();
            rel.setMobile(order.getMobile());
            rel.setCardId(card.getCardId());
            rel.setOrderNo(order.getOrderNo());
            rel.setCreateTime(new Date());
            videoUserRelService.add(rel);
        }
        return msg;
    }





    @Autowired
    private RecycleSystemService recycleSystemService;
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
  /* @RequestMapping(value = "recycleNew/getOrderList")
    @ResponseBody
    public ResultData getOrderList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        String url = baseNewUrl + "getorderlist";
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
                Cookie cookie = CookiesUtil.getCookieByName(request, Consts.COOKIE_HS_PHONE);
                if (cookie == null || StringUtils.isBlank(cookie.getValue())) {
                    return getResult(result, null, false, "2", "请输入手机号");
                }
                String cookiePhone = cookie.getValue();
                String phoneBase64 = URLDecoder.decode(cookiePhone, "UTF-8");
                contactphone = Base64Util.getFromBase64(phoneBase64);
            } else {
                String[] dname = request.getServerName().split("\\.");
                CookiesUtil.setCookie(response, Consts.COOKIE_HS_PHONE, Base64Util.getBase64(contactphone), CookiesUtil.prepare(dname), 999999999);
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
                    if ("202".equals(quote.getString("processstatus"))) {
                        quote.put("processstatus_name", "检测完成，请保持电话畅通");
                    }
                    if (StringUtil.isNotBlank(orderId)) {
                        RecycleOrder order = recycleOrderService.queryByOrderNo(orderId);
                        if (order != null) {
                            quote.put("raiseOrderNo", order.getIncreaseOrderNo());
                            quote.put("prepare_price", order.getPreparePrice());
                            if (StringUtil.isBlank(quote.getString("modelpic"))) {
                                //如果访问机型的图片为空 则使用默认图片
                                quote.put("modelpic", order.getImagePath());
                            }
                            if (!"204".equals(quote.getString("processstatus"))) {
                                if (StringUtils.isNotBlank(order.getCouponId())) {
                                    RecycleCoupon recycleCoupon = recycleCouponService.queryById(order.getCouponId());
                                    if (recycleCoupon != null) {
                                        JSONObject json = activityCouponService.recycleCouponDetail2Json(recycleCoupon);
                                        quote.put("coupon", json);
                                        String orderPrice = quote.getString("orderprice");
//                                        orderPrice = recycleOrderService.div095(orderPrice);//加个乘以0.95
                                        if (recycleCoupon.getPricingType() == 1) {
                                            if (recycleCoupon.getStrCouponPrice().compareTo(new BigDecimal("5")) != 0) {
                                                if (recycleCoupon.getAddPriceUpper() != null && recycleCoupon.getStrCouponPrice().intValue() > recycleCoupon.getAddPriceUpper().intValue()) {
                                                    quote.put("addCouponPrice", recycleCoupon.getAddPriceUpper());
                                                    quote.put("orderprice", new BigDecimal(orderPrice).add(recycleCoupon.getAddPriceUpper()));
                                                } else {
                                                    quote.put("addCouponPrice", recycleCoupon.getStrCouponPrice());
                                                    quote.put("orderprice", new BigDecimal(orderPrice).add(new BigDecimal(orderPrice).divide(new BigDecimal("100")).multiply(recycleCoupon.getStrCouponPrice())));
                                                }
                                            } else {
                                                String percent = recycleCoupon.getStrCouponPrice().toString();  // 加价比例
                                                Integer addCouponPrice = (recycleOrderService.getAddCouponPrice(new BigDecimal(orderPrice),percent)).intValue();
                                                if (recycleCoupon.getAddPriceUpper() != null && addCouponPrice > recycleCoupon.getAddPriceUpper().intValue()) {
                                                    quote.put("addCouponPrice", recycleCoupon.getAddPriceUpper().toString());
                                                    quote.put("orderprice", new BigDecimal(orderPrice).add(recycleCoupon.getAddPriceUpper()));
                                                } else {
                                                    quote.put("addCouponPrice", addCouponPrice.toString());
                                                    quote.put("orderprice", new BigDecimal(orderPrice).add(new BigDecimal(addCouponPrice.toString())));
                                                }
                                            }
                                        } else {
                                            quote.put("addCouponPrice", recycleCoupon.getStrCouponPrice().toString());
                                            quote.put("orderprice", new BigDecimal(orderPrice).add(new BigDecimal(recycleCoupon.getStrCouponPrice().toString())));
                                        }
                                    }
                                }
                            }
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
        return result;
//        renderJson(response, result);
    }
*/
     /**
     * 获取订单列表
     */

    @RequestMapping(value = "recycleNew/getOrderList")
    @ResponseBody
    public ResultData getOrderList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        String url = baseNewUrl + "getorderlist";
        try {
            //获取请求数据
            JSONObject params = getPrarms(request);
            String contactphone = params.getString("contactphone");
            String phone = params.getString("contactphone");
            String pageindex = params.getString("pageindex");
            String pagesize = params.getString("pagesize");
            String starttime = params.getString("starttime");
            String endtime = params.getString("endtime");
            String processstatus = params.getString("processstatus");

            if (StringUtils.isBlank(contactphone)) {
                Cookie cookie = CookiesUtil.getCookieByName(request, Consts.COOKIE_HS_PHONE);
                if (cookie == null || StringUtils.isBlank(cookie.getValue())) {
                    return getResult(result, null, false, "2", "请输入手机号");
                }
                String cookiePhone = cookie.getValue();
                String phoneBase64 = URLDecoder.decode(cookiePhone, "UTF-8");
                contactphone = Base64Util.getFromBase64(phoneBase64);
            } else {
                String[] dname = request.getServerName().split("\\.");
                CookiesUtil.setCookie(response, Consts.COOKIE_HS_PHONE, Base64Util.getBase64(contactphone), CookiesUtil.prepare(dname), 999999999);
            }

            RecycleOrder recycleOrder = new RecycleOrder();
            recycleOrder.setPhone(phone);
            recycleOrder.setMobile(contactphone);
            List<RecycleOrder> datainfos = new ArrayList<>();
            //分页
            PageBean<RecycleOrder> pageBean = new PageBean<>();
            //类型转换 当前页数
            Integer currenPage = Integer.valueOf(pageindex);
            //数据库第几行开始查询
            int startPage=(currenPage-1)*pageBean.getPageCount();
            //查询多少行数据 分页类里默认3行
            int selectCount=pageBean.getPageCount();
            recycleOrder.setStartPage(startPage);
            recycleOrder.setSelectCount(selectCount);
            if(StringUtils.isNotBlank(phone)){//判断登录手机号为空就查询联系人手机号
                datainfos = recycleOrderService.queryByPhoneNo(recycleOrder);
            }else{
                datainfos = recycleOrderService.queryByMobileNo(recycleOrder);//根据联系人手机号获取所有信息
            }

            List<ResultTest> datainfo = new ArrayList<>();
            for (RecycleOrder datainfo1:datainfos) {
                ResultTest resultTest = new ResultTest();


                String brandname = datainfo1.getBrandName();
                if(brandname!=null && brandname.contains("/")){
                        brandname=brandname.split("/")[1];
                 }


                String modelname = datainfo1.getProductName();;


                String str = "";
                        if(brandname!=null){
                        JSONObject jsonResult1 = new JSONObject();
                        String url1 = baseNewUrl + "getbrandlist";
                        String pic = null;
                        String brandId = null;
                        try {
                            String categoryid = request.getParameter("categoryid");
                            JSONObject requestNews1 = new JSONObject();
                            JSONObject code1 = new JSONObject();
                            code1.put("categoryid", categoryid);
                            String realCode1 = AES.Encrypt(code1.toString());  //加密
                            requestNews1.put(cipherdata, realCode1);
                            //发起请求
                            String getResult1 = AES.post(url1, requestNews1);
                            //对得到结果进行解密
                            jsonResult1 = getResult(AES.Decrypt(getResult1));
                            JSONArray array1 = jsonResult1.getJSONArray("datainfo");
                            for (int i1 = 0; i1 < array1.size(); i1++) {
                                JSONObject quote2 = (JSONObject) array1.get(i1);
                                String brandname1=quote2.getString("brandname");
                                if(brandname1.equals(brandname)){
                                    brandId = quote2.getString("brandid");
                                    break;
                                }
                            }

                            JSONObject jsonResult2 = new JSONObject();
                            String url2 = baseNewUrl + "getmodellist";
                            String seachId = "";

                                //获取请求数据

                                String pageIndex1 = "1";
                                String pageSize1 = "1000";
                                String categoryid1 = "0";

                                JSONObject requestNews2 = new JSONObject();
                                //调用接口需要加密的数据
                                JSONObject code2 = new JSONObject();
                                code2.put("pageindex", pageIndex1);
                                code2.put("pagesize", pageSize1);
                                code2.put("categoryid", categoryid1);
                                code2.put("brandid", brandId);
                                code2.put("keyword", null);
                                String realCode2 = AES.Encrypt(code2.toString());  //加密
                                requestNews2.put(cipherdata, realCode2);
                                //发起请求
                                String getResult2 = AES.post(url2, requestNews2);
                                //对得到结果进行解密
                                jsonResult2 = getResult(AES.Decrypt(getResult2));

                                //将结果中的产品id转为string类型  json解析 long类型精度会丢失
                                //防止返回机型信息为空
                                if (StringUtil.isNotBlank(jsonResult2.getString("datainfo")) && !(jsonResult2.getJSONArray("datainfo")).isEmpty()) {
                                    JSONArray jq = jsonResult2.getJSONArray("datainfo");
                                    JSONObject jqs = (JSONObject) jq.get(0);
                                    JSONArray j = jqs.getJSONArray("sublist");
                                    for (int i2 = 0; i2 < j.size(); i2++) {
                                        JSONObject js = j.getJSONObject(i2);
                                        js.put("productid", js.getString("productid"));

                                        String imgaePath = js.getString("modellogo");
                                        if((js.getString("modelname").replace(" ","")).equals(modelname.replace(" ",""))){


                                                resultTest.setModelpic(imgaePath);

//                                                JSONObject jsonResult3 = new JSONObject();
//                                                String url3 = baseNewUrl + "getchecklist";
//                                                try {
//                                                    //获取请求数据
//
//                                                    String productId = js.getString("productid");
//                                                    String modelName = js.getString("modelName");
//                                                    if (StringUtil.isBlank(productId)) {
//                                                        throw new SystemException("参数不完整");
//                                                    }
//                                                    JSONObject requestNews3 = new JSONObject();
//                                                    //调用接口需要加密的数据
//                                                   JSONObject code3 = new JSONObject();
//                                                    code3.put("productid", productId);
//                                                    String realCode3 = AES.Encrypt(code3.toString());  //加密
//                                                    requestNews3.put(cipherdata, realCode3);
//                                                    //发起请求
//                                                    String getResult3 = AES.post(url3, requestNews3);
//                                                    //对得到结果进行解密
//                                                    jsonResult3 = getResult(AES.Decrypt(getResult3));
//                                                    JSONObject object = jsonResult3.getJSONObject("datainfo");
//                                                    JSONArray array3 = object.getJSONArray("questions");
//                                                    StringBuffer stringBuffer = new StringBuffer();
//                                                    for(int b=0;b<array3.size();b++){
//                                                        JSONObject jsonObject=(JSONObject)array3.get(b);
//                                                        String question = jsonObject.getString("name");
//                                                        if(b==array3.size()-1){
//                                                            stringBuffer = stringBuffer.append(question);
//                                                        }else{
//                                                            stringBuffer = stringBuffer.append(question).append("、");
//                                                        }
//
//                                                    }
//                                                    str = new String(stringBuffer);
//                                                } catch (SystemException e) {
//                                                    sessionUserService.getSystemException(e, result);
//                                                } catch (Exception e) {
//                                                    e.printStackTrace();
//                                                    sessionUserService.getException(result);
//                                                }
                                            break;
                                        }


                                    }

                                } else {
                                    recycleOrderService.saveException(seachId, "该机型未找到");
                                    getResult(result, null, false, "3", "该机型未找到");
                                    log.info("该机型未找到");
                                }





                        } catch (SystemException e) {
                            sessionUserService.getSystemException(e, result);
                        } catch (Exception e) {
                            e.printStackTrace();
                            sessionUserService.getException(result);
                        }



//                        String imei = quote1.getString("imei");

                    }
//                }
//                  }
//                resultTest.setModelpic(datainfo1.getImagePath());
//                StringBuffer stringBuffer = new StringBuffer();
//                        if(StringUtil.isNotBlank(str)){
//                            String[] s =datainfo1.getDetail().split("、");
//                            String[] s1=str.split("、");
//                            for (int a=0;a<s.length;a++) {
//                                stringBuffer=stringBuffer.append(s1[a]).append(s[a]).append("、");
//                            }
//                        }


                resultTest.setDetail(datainfo1.getDetail());
                resultTest.setOrderStatus(datainfo1.getOrderStatus());
                resultTest.setLovemoney(datainfo1.getLovemoney());
                resultTest.setCreatetime(datainfo1.getInTime());
                resultTest.setOrderid(datainfo1.getOrderNo());
                resultTest.setModelname(datainfo1.getProductName());
                resultTest.setBrandname(datainfo1.getBrandName());
                if(datainfo1.getOrderStatus()==9){//订单完成前后价格不一
                    resultTest.setOrderprice(datainfo1.getNegotiationPrice());
                }else{
                    resultTest.setOrderprice(datainfo1.getPrice());
                }
//                String detail = new String(stringBuffer);
//                resultTest.setDetail(detail);
                datainfo.add(resultTest);
            }
            Integer totalcount = recycleOrderService.getDao().queryCountByPhone(phone);

            jsonResult.put("result","RESPONSESUCCESS");
            jsonResult.put("msg","成功");
            jsonResult.put("totalcount",totalcount);
            jsonResult.put("datainfo",datainfo);
            jsonResult.put("sessionid","");



            //结合订单号查询更多信息
            JSONArray array = jsonResult.getJSONArray("datainfo");
            if (array.size() > 0) {
                for (int i = 0; i < array.size(); i++) {
                    JSONObject quote = (JSONObject) array.get(i);
                    String orderId = quote.getString("orderid");
                    if ("202".equals(quote.getString("processstatus"))) {
                        quote.put("processstatus_name", "检测完成，请保持电话畅通");
                    }
                    if (StringUtil.isNotBlank(orderId)) {
                        RecycleOrder order = recycleOrderService.queryByOrderNo(orderId);
                        if (order != null) {
                            quote.put("raiseOrderNo", order.getIncreaseOrderNo());
                            quote.put("prepare_price", order.getPreparePrice());
                            if (StringUtil.isBlank(quote.getString("modelpic"))) {
                                //如果访问机型的图片为空 则使用默认图片
                                quote.put("modelpic", order.getImagePath());
                            }
                            if (!"204".equals(quote.getString("processstatus"))) {
                                if (StringUtils.isNotBlank(order.getCouponId())) {
                                    RecycleCoupon recycleCoupon = recycleCouponService.queryById(order.getCouponId());
                                    if (recycleCoupon != null) {
                                        JSONObject json = activityCouponService.recycleCouponDetail2Json(recycleCoupon);
                                        quote.put("coupon", json);
                                        String orderPrice = quote.getString("orderprice");
//                                        orderPrice = recycleOrderService.div095(orderPrice);//加个乘以0.95
                                        if (recycleCoupon.getPricingType() == 1) {
                                            if (recycleCoupon.getStrCouponPrice().compareTo(new BigDecimal("5")) != 0) {
                                                if (recycleCoupon.getAddPriceUpper() != null && recycleCoupon.getStrCouponPrice().intValue() > recycleCoupon.getAddPriceUpper().intValue()) {
                                                    quote.put("addCouponPrice", recycleCoupon.getAddPriceUpper());
                                                    quote.put("orderprice", new BigDecimal(orderPrice).add(recycleCoupon.getAddPriceUpper()));
                                                } else {
                                                    quote.put("addCouponPrice", recycleCoupon.getStrCouponPrice());
                                                    quote.put("orderprice", new BigDecimal(orderPrice).add(new BigDecimal(orderPrice).divide(new BigDecimal("100")).multiply(recycleCoupon.getStrCouponPrice())));
                                                }
                                            } else {
                                                String percent = recycleCoupon.getStrCouponPrice().toString();  // 加价比例
                                                Integer addCouponPrice = (recycleOrderService.getAddCouponPrice(new BigDecimal(orderPrice),percent)).intValue();
                                                if (recycleCoupon.getAddPriceUpper() != null && addCouponPrice > recycleCoupon.getAddPriceUpper().intValue()) {
                                                    quote.put("addCouponPrice", recycleCoupon.getAddPriceUpper().toString());
                                                    quote.put("orderprice", new BigDecimal(orderPrice).add(recycleCoupon.getAddPriceUpper()));
                                                } else {
                                                    quote.put("addCouponPrice", addCouponPrice.toString());
                                                    quote.put("orderprice", new BigDecimal(orderPrice).add(new BigDecimal(addCouponPrice.toString())));
                                                }
                                            }
                                        } else {
                                            quote.put("addCouponPrice", recycleCoupon.getStrCouponPrice().toString());
                                            quote.put("orderprice", new BigDecimal(orderPrice).add(new BigDecimal(recycleCoupon.getStrCouponPrice().toString())));
                                        }
                                    }
                                }
                            }
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
        return result;
//        renderJson(response, result);
    }




    /**
     * 获取订单明细
     */
    @RequestMapping(value = "recycleNew/getOrderDetail")
    public void getOrderDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        String url = baseNewUrl + "getorderdetail";
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
            System.out.println("具体返回价格："+info);
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
                    info.put("lovemoney", r.getLovemoney());
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
                    if ("202".equals(info.getString("processstatus"))) {
                        info.put("processstatus_name", "检测完成，请保持电话畅通");
                    }
                    if (!"204".equals(info.getString("processstatus"))) {
                        if (StringUtils.isNotBlank(r.getCouponId())) {
                            RecycleCoupon recycleCoupon = recycleCouponService.queryById(r.getCouponId());
                            if (recycleCoupon != null) {
                                JSONObject json = activityCouponService.recycleCouponDetail2Json(recycleCoupon);
                                info.put("coupon", json);
                                String orderPrice = info.getString("orderprice");
//                                orderPrice = recycleOrderService.div095(orderPrice);//加个乘以0.95
                                if (recycleCoupon.getPricingType() == 1) {
                                    if (recycleCoupon.getStrCouponPrice().compareTo(new BigDecimal("5")) != 0) {
                                        if (recycleCoupon.getAddPriceUpper() != null && recycleCoupon.getStrCouponPrice().intValue() > recycleCoupon.getAddPriceUpper().intValue()) {
                                            info.put("addCouponPrice", recycleCoupon.getAddPriceUpper());
                                            info.put("orderprice", new BigDecimal(orderPrice).add(recycleCoupon.getAddPriceUpper()));
                                        } else {
                                            info.put("orderprice", new BigDecimal(orderPrice).add(new BigDecimal(orderPrice).divide(new BigDecimal("100")).multiply(recycleCoupon.getStrCouponPrice())));
                                            info.put("addCouponPrice", new BigDecimal(orderPrice).divide(new BigDecimal("100")).multiply(recycleCoupon.getStrCouponPrice()));
                                        }
                                    } else {
                                        Integer addCouponPrice = (recycleOrderService.getAddCouponPrice(new BigDecimal(orderPrice),recycleCoupon.getStrCouponPrice().toString())).intValue();
                                        if (recycleCoupon.getAddPriceUpper() != null && addCouponPrice > recycleCoupon.getAddPriceUpper().intValue()) {
                                            info.put("addCouponPrice", Integer.valueOf(recycleCoupon.getAddPriceUpper().toString()));
                                            info.put("orderprice", new BigDecimal(orderPrice).add(new BigDecimal(recycleCoupon.getAddPriceUpper().toString())));
                                        } else {
                                            info.put("addCouponPrice", addCouponPrice.toString());
                                            info.put("orderprice", new BigDecimal(orderPrice).add(new BigDecimal(addCouponPrice.toString())));
                                        }
                                    }
                                } else {
                                    info.put("addCouponPrice", recycleCoupon.getStrCouponPrice().toString());
                                    info.put("orderprice", new BigDecimal(orderPrice).add(new BigDecimal(recycleCoupon.getStrCouponPrice().toString())));
                                }
                            }
                        }
                    }
                }

                try {
                    // orderPrice 如果存在捐款---增加上捐款的价格--因为回收那边传递的值是实际打款的钱，不包含捐款的钱
                    if(r.getNegotiationPrice()!=null&&r.getLovemoney()!=null){
                        BigDecimal orderPrice = (BigDecimal) info.get("orderprice");
                        info.put("orderprice",orderPrice.add(r.getLovemoney()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            getResult(result, jsonResult, true, "0", "成功");
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
     * 根据机型获取最高报价
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "recycleNew/getMaxPrice")
    public void getMaxPrice(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        String url = baseNewUrl + "getmodellist";
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
     * Description: 通过微信openId和微信小程序检测出的用户机型和品牌获取机型品牌名称 品牌id 机型名称  机型id  产品id
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "recycleNew/getProductId")
    public void getProductId(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        try {
            //获取请求数据
            JSONObject params = getPrarms(request);
            String brand = params.getString("brand");
            String modelName = params.getString("modelName");
            String openId = params.getString("openId");
            if (StringUtil.isBlank(brand) || StringUtil.isBlank(modelName) || StringUtil.isBlank(openId)) {
                throw new SystemException("参数不完整");
            }
            //将微信小程序检测的机型名称转换成回收平台的机型名称
//            Map<String, String> map = AES.getModelName(brand, modelName);
            JSONObject requestNews = new JSONObject();
            //通过转换过的机型 使用回收搜索接口得到对应机型id
            JSONObject code = new JSONObject();
            code.put("brandcode", brand);
            code.put("modelcode", modelName);
            String realCode = AES.Encrypt(code.toString());  //加密
            requestNews.put(cipherdata, realCode);
            //发起获取对应productid请求
            String getResult = AES.post(baseNewUrl + "getmodelbycode", requestNews);
            //对得到结果进行解密
            code = getResult(AES.Decrypt(getResult));
            if (StringUtils.isBlank(code.getString("datainfo"))) {
                throw new SystemException("对应机型不存在");
            }
            JSONArray product = code.getJSONArray("datainfo");

            JSONObject o = (JSONObject) product.get(0);
            JSONArray sublist = o.getJSONArray("sublist");
            String productId = ((JSONObject) sublist.get(0)).getString("productid");
            int goodPirce = (int) (((JSONObject) sublist.get(0)).getInteger("productprice") * 0.9);
            if (StringUtils.isBlank(productId)) {
                throw new SystemException("对应机型id不存在");
            }

            //获取该用户该机型上次的评估价
            Integer lastPrice = null;
            RecycleCheckItems checkItems = new RecycleCheckItems();
            checkItems.setWechatId(openId);
            checkItems.setWechatModel(modelName);
            List<RecycleCheckItems> checkList = recycleCheckItemsService.queryList(checkItems);
            if (!checkList.isEmpty() && checkList.get(0).getLastPrice() != null) {
                lastPrice = checkList.get(0).getLastPrice().intValue();
            }

            //返回数据
            jsonResult.put("brandName", o.get("brandname"));
            jsonResult.put("brandId", o.get("brandid"));
            jsonResult.put("modelName", ((JSONObject) sublist.get(0)).getString("modelname"));
            jsonResult.put("productId", productId);
            jsonResult.put("goodPrice", goodPirce);
            jsonResult.put("lastPrice", lastPrice);
            jsonResult.put("modelLogo", ((JSONObject) sublist.get(0)).getString("modellogo"));
            result.setResult(jsonResult);
            result.setResultCode("0");
            result.setSuccess(true);

            // 将用户手机品牌  品牌id  微信小程序检测的机型  以及对应的回收平台的进行名称存入数据库
            if (checkList.isEmpty()) {
                //新增
                checkItems.setBrandId(o.get("brandid").toString());
                checkItems.setRecycleModel(o.get("brandname").toString());
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
                cr.setBrandId(o.get("brandid").toString());
                cr.setRecycleModel(o.get("brandname").toString());
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
        String url = baseNewUrl + "getmodellist";
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
    @RequestMapping(value = "recycleNew/getChangePrice")
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
    @RequestMapping(value = "recycleNew/getDeclinePrice")
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
    @RequestMapping(value = "recycleNew/getPriceById")
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
        return percent + "-" + msg;
    }


}
