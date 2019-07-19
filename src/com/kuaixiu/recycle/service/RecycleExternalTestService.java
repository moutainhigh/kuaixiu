package com.kuaixiu.recycle.service;


import com.alibaba.fastjson.JSONObject;
import com.common.base.service.BaseService;
import com.common.exception.SystemException;
import com.common.util.AES;
import com.common.wechat.common.util.StringUtils;
import com.kuaixiu.recycle.dao.RecycleExternalTestMapper;
import com.kuaixiu.recycle.entity.RecycleExternalLogin;
import com.kuaixiu.recycle.entity.RecycleExternalTest;
import com.system.constant.SystemConstant;
import jodd.util.StringUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * RecycleExternalTest Service
 *
 * @CreateDate: 2018-09-29 上午10:24:26
 * @version: V 1.0
 */
@Service("recycleExternalTestService")
public class RecycleExternalTestService extends BaseService<RecycleExternalTest> {
    private static final Logger log = Logger.getLogger(RecycleExternalTestService.class);

    /**
     * 基础访问接口地址
     */
    public static final String baseUrl = SystemConstant.RECYCLE_URL;
    /**
     * 基础访问接口地址
     */
    public static final String baseNewUrl = SystemConstant.RECYCLE_NEW_URL;
    /**
     * 需要加密的数据名
     */
    public static final String cipherdata = SystemConstant.RECYCLE_REQUEST;

    @Autowired
    private RecycleExternalLoginService recycleExternalLoginService;
    @Autowired
    private RecycleExternalTestMapper<RecycleExternalTest> mapper;


    public RecycleExternalTestMapper<RecycleExternalTest> getDao() {
        return mapper;
    }

    //**********自定义方法***********

    /**
     * 通过检测项获取数据
     *
     * @param productId
     * @param imei
     * @param items
     * @return
     * @throws Exception
     */
    public JSONObject getPrice(String productId, String imei,
                               String items) throws Exception {
        JSONObject requestNews = new JSONObject();
        JSONObject jsonResult = new JSONObject();
        String url = baseUrl + "getprice";
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
        //将quoteid转为string
        if (StringUtil.isNotBlank(jsonResult.getString("datainfo"))) {
            JSONObject j = (JSONObject) jsonResult.get("datainfo");
            j.put("quoteid", j.getString("quoteid"));
            j.put("price", j.getString("price"));
        }
        return jsonResult;
    }

    /**
     * 通过检测项获取数据
     *
     * @param productId
     * @param imei
     * @param items
     * @return
     * @throws Exception
     */
    public JSONObject getNewUrlPrice(String productId, String imei,
                               String items) throws Exception {
        JSONObject requestNews = new JSONObject();
        JSONObject jsonResult = new JSONObject();
        String url = baseNewUrl + "getprice";
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
        //将quoteid转为string
        if (StringUtil.isNotBlank(jsonResult.getString("datainfo"))) {
            JSONObject j = (JSONObject) jsonResult.get("datainfo");
            j.put("quoteid", j.getString("quoteid"));
            j.put("price", j.getString("price"));
        }
        return jsonResult;
    }

    /**
     * 保存信息
     *
     * @param jsonObject
     * @param token
     * @param brandId
     * @param detail
     * @param productId
     * @throws Exception
     */
    public void add(JSONObject jsonObject, String token,
                    String detail, String brandId, String productId) throws Exception {
        //通过quoteid获取机型信息
        JSONObject postNews = postNews(jsonObject.getString("quoteid"));
        String selectBrandName = postNews.getString("brandname");
        String selectModelName = postNews.getString("modelname");
        String price = postNews.getString("price");
        RecycleExternalLogin login = recycleExternalLoginService.queryLogin(token);

        //获取当天初始时间
        RecycleExternalTest test = new RecycleExternalTest();
        test.setId(UUID.randomUUID().toString().replace("-", "-"));
        test.setLoginMobile(login.getLoginMobile());
        test.setLoginTime(login.getCreateTime());
        test.setBrandId(brandId);
        test.setBrandName(selectBrandName);
        test.setProductId(productId);
        test.setModelName(selectModelName);
        test.setToken(token);
        test.setTestPrice(new BigDecimal(price));
        test.setDetail(detail);
        test.setQuoteId(jsonObject.getString("quoteid"));
        getDao().add(test);
    }

    /**
     * 保存信息
     *
     * @param jsonObject
     * @param token
     * @param brandId
     * @param detail
     * @param productId
     * @throws Exception
     */
    public void newUrlAdd(JSONObject jsonObject, String token,
                    String detail, String brandId, String productId) throws Exception {
        //通过quoteid获取机型信息
        JSONObject postNews = postNewUrlNews(jsonObject.getString("quoteid"));
        String selectBrandName = postNews.getString("brandname");
        String selectModelName = postNews.getString("modelname");
        String price = postNews.getString("price");
        RecycleExternalLogin login = recycleExternalLoginService.queryLogin(token);

        //获取当天初始时间
        RecycleExternalTest test = new RecycleExternalTest();
        test.setId(UUID.randomUUID().toString().replace("-", "-"));
        test.setLoginMobile(login.getLoginMobile());
        test.setLoginTime(login.getCreateTime());
        test.setBrandId(brandId);
        test.setBrandName(selectBrandName);
        test.setProductId(productId);
        test.setModelName(selectModelName);
        test.setToken(token);
        test.setTestPrice(new BigDecimal(price));
        test.setDetail(detail);
        test.setQuoteId(jsonObject.getString("quoteid"));
        getDao().add(test);
    }

    /**
     * 通过quoteid查询 订单信息
     */
    public JSONObject postNews(String quoteId) throws Exception {
        JSONObject requestNews = new JSONObject();
        //调用接口需要加密的数据
        String url = baseUrl + "getquotedetail";
        JSONObject code = new JSONObject();
        code.put("quoteid", quoteId);
        String realCode = AES.Encrypt(code.toString());  //加密
        requestNews.put(cipherdata, realCode);
        //发起请求
        String getResult = AES.post(url, requestNews);
        //对得到结果进行解密
        JSONObject jsonResult = getResult(AES.Decrypt(getResult));
        JSONObject j = (JSONObject) jsonResult.get("datainfo");
        return j;
    }

    /**
     * 通过quoteid查询 订单信息//回收新接口
     */
    public JSONObject postNewUrlNews(String quoteId) throws Exception {
        JSONObject requestNews = new JSONObject();
        //调用接口需要加密的数据
        String url = baseNewUrl + "getquotedetail";
        JSONObject code = new JSONObject();
        code.put("quoteid", quoteId);
        String realCode = AES.Encrypt(code.toString());  //加密
        requestNews.put(cipherdata, realCode);
        //发起请求
        String getResult = AES.post(url, requestNews);
        //对得到结果进行解密
        JSONObject jsonResult = getResult(AES.Decrypt(getResult));
        JSONObject j = (JSONObject) jsonResult.get("datainfo");
        return j;
    }

    /**
     * 返回结果解析
     *
     * @param originalString
     * @return
     */
    public JSONObject getResult(String originalString) {
        JSONObject result = (JSONObject) JSONObject.parse(originalString);
        log.info("返回解密：" + result);
        if (result.getString("result") != null && !result.getString("result").equals("RESPONSESUCCESS")) {
            log.info("返回解密：" + result);
            throw new SystemException(result.getString("msg"));
        }
        return result;
    }

}