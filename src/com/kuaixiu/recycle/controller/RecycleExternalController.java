package com.kuaixiu.recycle.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.exception.SystemException;
import com.common.paginate.Page;
import com.common.util.AES;
import com.common.wechat.common.util.StringUtils;
import com.google.common.collect.Maps;
import com.kuaixiu.recycle.entity.RecycleExternalLogin;
import com.kuaixiu.recycle.entity.RecycleExternalSubmit;
import com.kuaixiu.recycle.entity.RecycleExternalTest;
import com.kuaixiu.recycle.service.RecycleExternalLoginService;
import com.kuaixiu.recycle.service.RecycleExternalSubmitService;
import com.kuaixiu.recycle.service.RecycleExternalTestService;
import com.system.api.entity.ResultData;
import com.system.basic.address.service.AddressService;
import com.system.basic.user.entity.SessionUser;
import com.system.basic.user.service.SessionUserService;
import com.system.constant.ApiResultConstant;
import com.system.constant.SystemConstant;
import jodd.util.StringUtil;
import org.apache.commons.collections.CollectionUtils;
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
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * RecycleExternalLogin Controller
 * 用于外部调用手机测评
 *
 * @CreateDate: 2018-09-29 上午09:09:53
 * @version: V 1.0
 */
@Controller
public class RecycleExternalController extends BaseController {

    private static final Logger log = Logger.getLogger(RecycleExternalController.class);


    @Autowired
    private RecycleExternalLoginService recycleExternalLoginService;
    @Autowired
    private RecycleExternalSubmitService recycleExternalSubmitService;
    @Autowired
    private RecycleExternalTestService recycleExternalTestService;
    @Autowired
    private SessionUserService sessionUserService;
    @Autowired
    private AddressService addressService;

    /**
     * 需要加密的数据名
     */
    private static final String cipherdata = SystemConstant.RECYCLE_REQUEST;
    /**
     * 基础访问接口地址
     */
    private static final String baseUrl = SystemConstant.RECYCLE_URL;
    /**
     * 基础访问接口地址
     */
    private static final String baseNewUrl = SystemConstant.RECYCLE_NEW_URL;
    /**
     * 外部调用地址
     */
    private static final String externalUrl = "http://test.wenboweb.cn:20001/mobile/api";

    /**
     * 用于外部调用登录
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/recycle/external/login")
    @ResponseBody
    public ResultData login(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject params = getPrarms(request);
            String loginMobile = params.getString("loginMobile");
            String source = params.getString("source");
            if (StringUtils.isBlank(loginMobile) || StringUtils.isBlank(source)) {
                result.setSuccess(false);
                result.setResultCode(ApiResultConstant.resultCode_1001);
                result.setResultMessage(ApiResultConstant.resultCode_str_1001);
                return result;
            }
            //保存登录信息,返回本次登录操作验证token
            String token = recycleExternalLoginService.addLogin(loginMobile, source);
            jsonObject.put("token", token);
            result.setResult(jsonObject);
            result.setSuccess(true);
            result.setResultCode("0");
            result.setResultMessage("登录成功");
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        return result;
    }


    /**
     * 手机测评
     */
    @RequestMapping(value = "recycle/external/getPrice")
    @ResponseBody
    public ResultData getPrice(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        try {
            //获取请求数据
            JSONObject params = getPrarms(request);
            String token = params.getString("token");
            String brandId = params.getString("brandId");
            String productId = params.getString("productId");
            String imei = params.getString("imei");
            String items = params.getString("items");
            String detail = params.getString("detail");
            if (StringUtils.isBlank(brandId) || StringUtils.isBlank(token) || StringUtils.isBlank(productId)
                    || StringUtils.isBlank(items) || StringUtils.isBlank(detail)) {
                result.setResultCode("1");
                result.setSuccess(false);
                result.setResultMessage("参数不完整");
                return result;
            }
            //通过检测项获取数据
            JSONObject jsonResult = recycleExternalTestService.getPrice(productId, imei, items);
            JSONObject jsonObject = jsonResult.getJSONObject("datainfo");

            //保存信息
            if (StringUtils.isNotBlank(jsonObject.getString("quoteid"))) {
                recycleExternalTestService.add(jsonObject, token, detail, brandId, productId);
            }
            jsonObject.put("token", token);
            result.setResult(jsonResult);
            result.setResultMessage("成功");
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
     * 手机测评
     */
    @RequestMapping(value = "recycleNew/external/getPrice")
    @ResponseBody
    public ResultData getNewUrlPrice(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        try {
            //获取请求数据
            JSONObject params = getPrarms(request);
            String token = params.getString("token");
            String brandId = params.getString("brandId");
            String productId = params.getString("productId");
            String imei = params.getString("imei");
            String items = params.getString("items");
            String detail = params.getString("detail");
            if (StringUtils.isBlank(brandId) || StringUtils.isBlank(token) || StringUtils.isBlank(productId)
                    || StringUtils.isBlank(items) || StringUtils.isBlank(detail)) {
                result.setResultCode("1");
                result.setSuccess(false);
                result.setResultMessage("参数不完整");
                return result;
            }

            //转换items格式“1,2|2,6|4,15|5,19|6,21|35,114|11,43......”-->“2,6,15,19,21,114,43......”
            StringBuilder sb=new StringBuilder();
            String[] itemses=items.split("\\|");
            for(int i=0;i<itemses.length;i++){
                String[] item=itemses[i].split(",");
                sb.append(item[1]);
                if(itemses.length-1!=i){
                    sb.append(",");
                }
            }
            items=sb.toString();

            //通过检测项获取数据
            JSONObject jsonResult = recycleExternalTestService.getNewUrlPrice(productId, imei, items);
            JSONObject jsonObject = jsonResult.getJSONObject("datainfo");

            //保存信息
            if (StringUtils.isNotBlank(jsonObject.getString("quoteid"))) {
                recycleExternalTestService.newUrlAdd(jsonObject, token, detail, brandId, productId);
            }
            jsonObject.put("token", token);
            result.setResult(jsonResult);
            result.setResultMessage("成功");
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
     * 外部调用手机测评提交订单-----然后数据推送回外部调用处
     */
    @RequestMapping(value = "recycle/external/submit")
    @ResponseBody
    public ResultData wechatCreateOrder(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        try {
            //获取请求数据
            JSONObject params = getPrarms(request);
            String token = params.getString("token");
            String quoteid = params.getString("quoteId");
            String name = params.getString("name");
            String mobile = params.getString("mobile");
            String province = params.getString("province");//省id
            String city = params.getString("city");//城id
            String area = params.getString("area");//区域id
            String street = params.getString("street");//街道
            String imagePath = params.getString("imagePath");
            String note = params.getString("note");
            if (StringUtil.isBlank(quoteid) || StringUtil.isBlank(token) ||
                    StringUtil.isBlank(name) || StringUtil.isBlank(mobile) || StringUtil.isBlank(province) ||
                    StringUtil.isBlank(city) || StringUtil.isBlank(area) || StringUtil.isBlank(street) ||
                    StringUtils.isBlank(imagePath)) {
                result.setResultMessage("参数不完整");
                result.setSuccess(false);
                result.setResultCode("1");
                return result;
            }
            //判断数据 是否符合规范
            if (name.length() > 12 || street.length() > 64) {
                result.setResultMessage("部分信息长度过长");
                result.setSuccess(false);
                result.setResultCode("2");
                return result;
            }
            Boolean isTrue = recycleExternalSubmitService.timeVerification(request);
            if (!isTrue) {
                result.setResultMessage("您下单过于频繁，请稍后重试!");
                result.setSuccess(false);
                result.setResultCode("3");
                return result;
            }
            //获取转化地址
            String areaname = recycleExternalSubmitService.getProvince(province, city, area);
            RecycleExternalSubmit submit = new RecycleExternalSubmit();
            RecycleExternalLogin login = recycleExternalLoginService.queryLogin(token);
            submit.setLoginMobile(login.getLoginMobile());
            submit.setSource(login.getSource());
            submit.setToken(token);
            submit.setName(name);
            submit.setMobile(mobile);
            submit.setNote(note);
            submit.setProvince(province);
            submit.setCity(city);
            submit.setCounty(area);
            submit.setArea(areaname);
            submit.setStreet(street);
            //保存信息并返回测评信息
            RecycleExternalTest test = recycleExternalSubmitService.add(submit, imagePath);
            //返还测试信息
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("customerName", name);
            jsonObject.put("mobile", mobile);
            jsonObject.put("province", getProvince(addressService.queryByAreaId(province).getArea()));
            jsonObject.put("city", addressService.queryByAreaId(city).getArea());
            jsonObject.put("area", addressService.queryByAreaId(area).getArea());
            jsonObject.put("address", street);
            jsonObject.put("detail", test.getDetail());
            jsonObject.put("imagePath", imagePath);
            jsonObject.put("testPrice", test.getTestPrice().toString());
            jsonObject.put("testBrandName", test.getBrandName());
            jsonObject.put("testModelName", test.getModelName());
            jsonObject.put("loginMobile", login.getLoginMobile());
            jsonObject.put("note", note);
            jsonObject.put("id", login.getSource());
            log.info(externalUrl + "的请求:" + jsonObject);
            String getResult = this.post(externalUrl, URLJsonObject(jsonObject).toString());
            submit.setTSMessage(getResult);
            recycleExternalSubmitService.getDao().updateByToken(submit);
            log.info(externalUrl + "的返回结果:" + getResult);
            result.setSuccess(true);
            result.setResultCode("0");
            result.setResultMessage("成功");
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        return result;
    }


    /**
     * 获取手机测评分页列表
     */
    @RequestMapping(value = "/recycle/getTestList")
    public ModelAndView getTest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String returnView = "recycle/external/recycleExternalTestList";
        return new ModelAndView(returnView);
    }

    /**
     * categoryId查询品牌
     */
    @RequestMapping(value = "/recycle/getTestbrandList")
    public void getTestbrandList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> resultMap = Maps.newHashMap();
        try {
            String url = baseUrl + "getbrandlist";

            String categoryId = request.getParameter("categoryId");

            JSONObject requestNews = new JSONObject();
            //调用接口需要加密的数据
            JSONObject code = new JSONObject();
            code.put("categoryid", categoryId);
            String realCode = AES.Encrypt(code.toString());  //加密
            requestNews.put(cipherdata, realCode);
            //发起请求
            String getResult = AES.post(url, requestNews);
            //对得到结果进行解密
            JSONObject jsonResult = recycleExternalTestService.getResult(AES.Decrypt(getResult));
            JSONArray jq = jsonResult.getJSONArray("datainfo");
            resultMap.put(RESULTMAP_KEY_DATA, jq);
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
            resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
        }catch (Exception e){
            e.printStackTrace();
        }
        renderJson(response, resultMap);
    }

    /**
     * categoryId查询品牌
     */
    @RequestMapping(value = "/recycleNew/getTestbrandList")
    public void getNewUrlTestbrandList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> resultMap = Maps.newHashMap();
        try {
            String url = baseNewUrl + "getbrandlist";

            String categoryId = request.getParameter("categoryId");

            JSONObject requestNews = new JSONObject();
            //调用接口需要加密的数据
            JSONObject code = new JSONObject();
            code.put("categoryid", categoryId);
            String realCode = AES.Encrypt(code.toString());  //加密
            requestNews.put(cipherdata, realCode);
            //发起请求
            String getResult = AES.post(url, requestNews);
            //对得到结果进行解密
            JSONObject jsonResult = recycleExternalTestService.getResult(AES.Decrypt(getResult));
            JSONArray jq = jsonResult.getJSONArray("datainfo");
            resultMap.put(RESULTMAP_KEY_DATA, jq);
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
            resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
        }catch (Exception e){
            e.printStackTrace();
        }
        renderJson(response, resultMap);
    }

    /**
     * 根据品牌id获取手机机型
     */
    @RequestMapping(value = "/recycle/getTestModelList")
    public void getModels(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> resultMap = Maps.newHashMap();
        try {
            String url = baseUrl + "getmodellist";
            //获取项目id
            String brandId = request.getParameter("brandId");

            JSONObject requestNews = new JSONObject();
            //调用接口需要加密的数据
            JSONObject code = new JSONObject();
            code.put("pageindex", 1);
            code.put("pagesize", 500);
            code.put("brandid", brandId);
            String realCode = AES.Encrypt(code.toString());  //加密
            requestNews.put(cipherdata, realCode);
            //发起请求
            String getResult = AES.post(url, requestNews);
            //对得到结果进行解密
            JSONObject jsonResult = recycleExternalTestService.getResult(AES.Decrypt(getResult));
            JSONArray jq = jsonResult.getJSONArray("datainfo");
            JSONObject jqs = (JSONObject) jq.get(0);
            JSONArray j = jqs.getJSONArray("sublist");
            resultMap.put(RESULTMAP_KEY_DATA, j);
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
            resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
        } catch (Exception e) {
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
            resultMap.put(RESULTMAP_KEY_MSG, "系统异常，请稍后重试");
            e.printStackTrace();
        }
        renderJson(response, resultMap);
    }

    /**
     * 根据品牌id获取手机机型
     */
    @RequestMapping(value = "/recycleNew/getTestModelList")
    public void getNewUrlModels(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> resultMap = Maps.newHashMap();
        try {
            String url = baseNewUrl + "getmodellist";
            //获取项目id
            String brandId = request.getParameter("brandId");

            JSONObject requestNews = new JSONObject();
            //调用接口需要加密的数据
            JSONObject code = new JSONObject();
            code.put("pageindex", 1);
            code.put("pagesize", 500);
            code.put("brandid", brandId);
            String realCode = AES.Encrypt(code.toString());  //加密
            requestNews.put(cipherdata, realCode);
            //发起请求
            String getResult = AES.post(url, requestNews);
            //对得到结果进行解密
            JSONObject jsonResult = recycleExternalTestService.getResult(AES.Decrypt(getResult));
            JSONArray jq = jsonResult.getJSONArray("datainfo");
            JSONObject jqs = (JSONObject) jq.get(0);
            JSONArray j = jqs.getJSONArray("sublist");
            resultMap.put(RESULTMAP_KEY_DATA, j);
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
            resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
        } catch (Exception e) {
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
            resultMap.put(RESULTMAP_KEY_MSG, "系统异常，请稍后重试");
            e.printStackTrace();
        }
        renderJson(response, resultMap);
    }

    /**
     * 获取手机测评分页列表
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "recycle/getListForPage")
    public void getTestList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Page page = getPageByRequest(request);
        try {
//            SessionUser su = getCurrentUser(request);
//            if (su.getType() != SystemConstant.USER_TYPE_SYSTEM && su.getType() != SystemConstant.USER_TYPE_SUPPER) {
//                throw new SystemException("对不起，您没有操作权限!");
//            }
            //获取查询条件
            String loginMobile = request.getParameter("loginMobile");//登录手机号
            String isOrder = request.getParameter("isOrder");//是否成单
            String startLoginTime = request.getParameter("startLoginTime");//开始登录时间
            String endLoginTime = request.getParameter("endLoginTime");//结束登录时间
            String brandId = request.getParameter("brandId");//品牌
            String modelId = request.getParameter("modelId");//机型
            String startTestTime = request.getParameter("startTestTime");//结束测评时间
            String endTestTime = request.getParameter("endTestTime");//结束测评时间
            String startSubmitTime = request.getParameter("startSubmitTime");//开始提交时间
            String endSubmitTime = request.getParameter("endSubmitTime");//结束提交时间

            RecycleExternalTest test = new RecycleExternalTest();
            if (StringUtils.isNotBlank(loginMobile)) {
                test.setLoginMobile(loginMobile);
            }
            if (StringUtils.isNotBlank(isOrder)) {
                test.setIsOrder(isOrder);
            }
            if (StringUtils.isNotBlank(startLoginTime)) {
                test.setStartLoginTime(startLoginTime);
            }
            if (StringUtils.isNotBlank(endLoginTime)) {
                test.setEndLoginTime(endLoginTime);
            }
            if (StringUtils.isNotBlank(modelId)) {
                test.setProductId(modelId);
            }
            if (StringUtils.isNotBlank(brandId)) {
                test.setBrandId(brandId);
            }
            if (StringUtils.isNotBlank(startTestTime)) {
                test.setQueryStartTime(startTestTime);
            }
            if (StringUtils.isNotBlank(endTestTime)) {
                test.setQueryEndTime(endTestTime);
            }
            if (StringUtils.isNotBlank(startSubmitTime)) {
                test.setStartSubmitTime(startSubmitTime);
                test.setIsOrder("1");
            }
            if (StringUtils.isNotBlank(endSubmitTime)) {
                test.setEndSubmitTime(endSubmitTime);
                test.setIsOrder("1");
            }
            test.setPage(page);
            List<RecycleExternalTest> tests = recycleExternalTestService.getDao().queryListForPage(test);
            for (RecycleExternalTest test1 : tests) {
                if ("0".equals(test1.getIsOrder())) {
                    test1.setUpdateTime(null);
                }
            }
            page.setData(tests);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.renderJson(response, page);
    }


    /**
     * 获取订单详情
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "recycle/testDetail")
    public ModelAndView getDetail(HttpServletRequest request, HttpServletResponse response) {
        try {
//            SessionUser su = getCurrentUser(request);
//            if (su.getType() != SystemConstant.USER_TYPE_SYSTEM && su.getType() != SystemConstant.USER_TYPE_CUSTOMER_SERVICE) {
//                throw new SystemException("对不起，您没有操作权限!");
//            }
            String id = request.getParameter("id");
            if (org.apache.commons.lang3.StringUtils.isBlank(id)) {
                throw new SystemException("参数不完整");
            }
            //获取测评信息
            RecycleExternalTest recycleExternalTest = recycleExternalTestService.queryById(id);
            RecycleExternalLogin login = recycleExternalLoginService.queryLogin(recycleExternalTest.getToken());
            request.setAttribute("source", login.getSource());
            //获取订单信息
            RecycleExternalSubmit submit = new RecycleExternalSubmit();
            submit.setToken(recycleExternalTest.getToken());
            List<RecycleExternalSubmit> submits = recycleExternalSubmitService.getDao().queryList(submit);

            request.setAttribute("testDetail", recycleExternalTest);
            if (!CollectionUtils.isEmpty(submits)) {
                request.setAttribute("submits", submits.get(0));
            } else {
                request.setAttribute("submits", submit);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String returnView = "recycle/external/detail";
        return new ModelAndView(returnView);
    }


    /**
     * 获取手机测评分页列表
     */
    @RequestMapping(value = "/recycle/totalPriceList")
    public ModelAndView totalPrice(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String returnView = "recycle/external/recycleExternalTotalPriceList";
        return new ModelAndView(returnView);
    }

    /**
     * 价格统计列表
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "recycle/totalPriceListForPage")
    @ResponseBody
    public void totalPriceList(HttpServletRequest request, HttpServletResponse response) throws Exception {
//        SessionUser su = getCurrentUser(request);
//        if (su.getType() != SystemConstant.USER_TYPE_SYSTEM && su.getType() != SystemConstant.USER_TYPE_SUPPER) {
//            throw new SystemException("对不起，您没有操作权限!");
//        }
        Page page = getPageByRequest(request);
        try {
            //获取查询条件
            String loginMobile = request.getParameter("loginMobile");//登录手机号
            String queryStartTime = request.getParameter("queryStartTime");//开始登录时间
            String queryEndTime = request.getParameter("queryEndTime");//结束登录时间

            RecycleExternalTest test = new RecycleExternalTest();
            if (StringUtils.isNotBlank(loginMobile)) {
                test.setLoginMobile(loginMobile);
            }
            if (StringUtils.isNotBlank(queryStartTime)) {
                test.setQueryStartTime(queryStartTime);
            }
            if (StringUtils.isNotBlank(queryEndTime)) {
                test.setQueryEndTime(queryEndTime);
            }
            test.setPage(page);
            List<RecycleExternalTest> tests = recycleExternalTestService.getDao().queryListTotalPriceForPage(test);
            for (RecycleExternalTest test1 : tests) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                test1.setStrCreateTime(sdf.format(test1.getCreateTime()));
            }
            page.setData(tests);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.renderJson(response, page);
    }


    /**
     * 计算总价
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "recycle/getTotalPrice")
    @ResponseBody
    public void getTotalPrice(HttpServletRequest request, HttpServletResponse response) throws Exception {
//        SessionUser su = getCurrentUser(request);
//        if (su.getType() != SystemConstant.USER_TYPE_SYSTEM && su.getType() != SystemConstant.USER_TYPE_SUPPER) {
//            throw new SystemException("对不起，您没有操作权限!");
//        }
        Map<String, Object> resultMap = Maps.newHashMap();
        try {
            //获取查询条件
            String loginMobile = request.getParameter("loginMobile");//登录手机号
            String queryStartTime = request.getParameter("queryStartTime");//开始登录时间
            String queryEndTime = request.getParameter("queryEndTime");//结束登录时间

            RecycleExternalTest test = new RecycleExternalTest();
            if (StringUtils.isNotBlank(loginMobile)) {
                test.setLoginMobile(loginMobile);
            }
            if (StringUtils.isNotBlank(queryStartTime)) {
                test.setQueryStartTime(queryStartTime);
            }
            if (StringUtils.isNotBlank(queryEndTime)) {
                test.setQueryEndTime(queryEndTime);
            }
            List<RecycleExternalTest> test0 = recycleExternalTestService.getDao().queryListTotalPriceForPage(test);
            Double price = 0d;
            for (RecycleExternalTest test1 : test0) {
                price += Double.valueOf(test1.getPrice());
            }
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
            resultMap.put(RESULTMAP_KEY_DATA, price);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.renderJson(response, resultMap);
    }


    /**
     * 超人系统地址为 上海市 黄浦区 城区    -----  浙江 杭州市 江干区
     * 回收地址规范     上海市 黄浦区            ------ 浙江省 杭州市 江干区
     * 省份区别  西藏 宁夏 新疆 广西 内蒙古
     * 北京市  天津市 上海市  重庆市 其他省后面都加省
     *
     * @param code
     * @return 超人地址转化回收地址规范
     */
    public static String getProvince(String code) throws Exception {
        List<String> s = new ArrayList<String>();
        List<String> p = new ArrayList<String>();
        String[] plist = {"西藏", "宁夏", "新疆", "广西", "内蒙古"};
        String[] slist = {"北京", "天津", "上海", "重庆"};
        s.addAll(Arrays.asList(plist));
        p.addAll(Arrays.asList(slist));
        if (s.contains(code)) {
            //不用更改
        } else if (p.contains(code)) {
            code += "市";
        } else {
            code += "省";
        }
        return code;
    }


    //json转URL
    private StringBuilder URLJsonObject(JSONObject object) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        JSONObject jsonObject = new JSONObject(object);
        Iterator<String> it = jsonObject.keySet().iterator();
        while (it.hasNext()) {
            // 获得key
            String key = it.next();
            String value = jsonObject.getString(key);
            stringBuilder.append(key + "=" + URLEncoder.encode(value, "utf-8") + "&");
        }
        return stringBuilder;
    }


    /**
     * 发送post请求 返回数据默认进行url解码
     *
     * @throws Exception
     */
    private String post(String url, String param) throws Exception {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*application/json*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
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
        result = URLDecoder.decode(result, "utf-8");
        return result;
    }

}
