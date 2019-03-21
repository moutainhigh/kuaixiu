package com.kuaixiu.recycle.service;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.service.BaseService;
import com.common.exception.SystemException;
import com.common.util.AES;
import com.kuaixiu.recycle.dao.RecycleTestMapper;
import com.kuaixiu.recycle.entity.RecycleTest;

import com.system.constant.SystemConstant;
import jodd.util.StringUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * RecycleTest Service
 * @CreateDate: 2019-03-12 下午04:08:20
 * @version: V 1.0
 */
@Service("recycleTestService")
public class RecycleTestService extends BaseService<RecycleTest> {
    private static final Logger log= Logger.getLogger(RecycleTestService.class);

    @Autowired
    private RecycleTestMapper<RecycleTest> mapper;


    public RecycleTestMapper<RecycleTest> getDao() {
        return mapper;
    }

    //**********自定义方法***********
    private static final String baseNewUrl = SystemConstant.RECYCLE_NEW_URL;
    private static final String cipherdata = SystemConstant.RECYCLE_REQUEST;
    private static final String baseUrl = SystemConstant.RECYCLE_URL;

    public Map getBrandAndModelByProductId(String productId)throws Exception{
        Map map=new HashMap();
        JSONObject requestNews2 = new JSONObject();
        //调用接口需要加密的数据
        JSONObject code2 = new JSONObject();
        code2.put("productid", productId);
        String realCode2 = AES.Encrypt(code2.toString());  //加密
        requestNews2.put(cipherdata, realCode2);
        //发起请求
        String url = baseNewUrl + "getmodelbyid";
        String getResult2 = AES.post(url, requestNews2);
        //对得到结果进行解密
        JSONObject jsonResult2 = getResult(AES.Decrypt(getResult2));
        //将结果中的产品id转为string类型  json解析 long类型精度会丢失
        //防止返回机型信息为空
        if (StringUtil.isNotBlank(jsonResult2.getString("datainfo")) && !(jsonResult2.getJSONObject("datainfo")).isEmpty()) {
            JSONObject jq = jsonResult2.getJSONObject("datainfo");
            map.put("brandname",jq.getString("brandname"));
            map.put("modelname",jq.getString("modelname"));
            map.put("modellogo",jq.getString("modellogo"));
        }
        return map;
    }

    public Map getProductName(String items,String productId1)throws Exception{
        Map map=new HashMap();
        JSONObject jsonResult = new JSONObject();
        String url = baseNewUrl + "getcheckitems";
        List<Map<String, String>> lists = new ArrayList<>();
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
        code.put("productid", productId1);
        code.put("items", items);
        String realCode = AES.Encrypt(code.toString());  //加密
        requestNews.put(cipherdata, realCode);
        //发起请求
        String getResult = AES.post(url, requestNews);
        //对得到结果进行解密
        jsonResult = (JSONObject) JSONObject.parse(AES.Decrypt(getResult));
        if (StringUtil.isBlank(jsonResult.getString("datainfo"))) {
            map.put("product_name", "");
            return map;
        }
        StringBuilder sb1=new StringBuilder();
        JSONArray jsonArray = jsonResult.getJSONArray("datainfo");
        for(int i=0;i<jsonArray.size();i++){
            JSONObject jqs = (JSONObject) jsonArray.get(i);
            sb1.append(jqs.getString("itemname"));
            sb1.append("、");
        }
        if(sb1.length()>1){
            sb1.deleteCharAt(sb1.length() - 1);
            map.put("product_name", sb1.toString());
        }else{
            map.put("product_name", "");
        }
        return map;
    }

    public void getImgaePath(String brandId,String ProductId,HttpServletRequest request)throws Exception{
        JSONObject requestNews2 = new JSONObject();
        //调用接口需要加密的数据
        JSONObject code2 = new JSONObject();
        code2.put("pageindex", 1);
        code2.put("pagesize", 500);
        code2.put("categoryid", null);
        code2.put("brandid", brandId);
        code2.put("keyword", null);
        String realCode2 = AES.Encrypt(code2.toString());  //加密
        requestNews2.put(cipherdata, realCode2);
        //发起请求
        String url = baseNewUrl + "getmodellist";
        String getResult2 = AES.post(url, requestNews2);
        //对得到结果进行解密
        JSONObject jsonResult2 = getResult(AES.Decrypt(getResult2));
        //将结果中的产品id转为string类型  json解析 long类型精度会丢失
        //防止返回机型信息为空
        if (StringUtil.isNotBlank(jsonResult2.getString("datainfo")) && !(jsonResult2.getJSONArray("datainfo")).isEmpty()) {
            JSONArray jq = jsonResult2.getJSONArray("datainfo");
            JSONObject jqs = (JSONObject) jq.get(0);
            JSONArray j = jqs.getJSONArray("sublist");
            for (int i = 0; i < j.size(); i++) {
                JSONObject js = j.getJSONObject(i);
                if(ProductId.equals(js.getString("productid"))){
                    String imgaePath = js.getString("modellogo");
                    //如果该机型的图片地址为空 则使用默认图片地址
                    if (StringUtil.isBlank(imgaePath)) {
                        String realUrl = request.getRequestURL().toString();
                        String domain = realUrl.replace(request.getRequestURI(), "");
                        String path = domain + "/" + SystemConstant.DEFAULTIMAGE;
                        imgaePath = path;
                    }
                    request.setAttribute("imagePath", imgaePath);
                    break;
                }
            }
        }
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
}