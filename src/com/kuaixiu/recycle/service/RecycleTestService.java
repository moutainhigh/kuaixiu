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
    public Map getBrandAndModel(String brandId,String productId)throws Exception{
        Map map=new HashMap();
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
            map.put("brandname",jqs.getString("brandname"));
            JSONArray j = jqs.getJSONArray("sublist");
            for (int i = 0; i < j.size(); i++) {
                JSONObject js = j.getJSONObject(i);
                if(productId.equals(js.getString("productid"))){
                    map.put("modelname",js.getString("modelname"));
                    break;
                }
                //如果该机型的图片地址为空 则使用默认图片地址
            }
        }
        return map;
    }

    public Map getProductName(String items,String productId1)throws Exception{
        Map map=new HashMap();
        JSONObject jsonResult = new JSONObject();
        String url = baseNewUrl + "getchecklist";
        List<Map<String, String>> lists = new ArrayList<>();
        List<String> list1 = new ArrayList();
        List<String> lists2 = new ArrayList<>();
        if(items.contains("|")){
            List<String> list = Arrays.asList(items.split("\\|"));
            for (int q = 0; q < list.size(); q++) {
                String[] a = list.get(q).split(",");
                Map<String, String> maps = new HashMap<>();
                if ("".equals(a[0])) {
                    list1.add(a[1]);
                } else {
                    maps.put(a[0], a[1]);
                    lists.add(maps);
                }
            }
        }else{
            lists2 = Arrays.asList(items.split(","));
        }
        JSONObject requestNews = new JSONObject();
        //调用接口需要加密的数据
        JSONObject code = new JSONObject();
        code.put("productid", productId1);
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
        JSONObject jsonObject = jsonResult.getJSONObject("datainfo");
        JSONArray questions = jsonObject.getJSONArray("questions");
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        for (int i = 0; i < questions.size(); i++) {
            if(items.contains("|")) {
                if ("9999".equals(((JSONObject) questions.get(i)).getString("id")) && list1 != null) {
                    JSONArray answers = ((JSONObject) questions.get(i)).getJSONArray("answers");
                    for (int j = 0; j < answers.size(); j++) {
                        for (String str : list1) {
                            if (str.equals(((JSONObject) answers.get(j)).getString("id"))) {
                                sb2.append(((JSONObject) answers.get(j)).getString("name"));
                                sb2.append(" 、 ");
                            }
                        }

                    }
                }else {
                    for (Map<String, String> map1 : lists) {
                        Set<String> set = map1.keySet();
                        for (String key : set) {
                            if (key.equals(((JSONObject) questions.get(i)).getString("id"))) {
                                JSONArray answers = ((JSONObject) questions.get(i)).getJSONArray("answers");
                                for (int j = 0; j < answers.size(); j++) {
                                    if (map1.get(key).equals(((JSONObject) answers.get(j)).getString("id"))) {
                                        sb.append(((JSONObject) answers.get(j)).getString("name"));
                                        sb.append(" 、 ");
                                    }
                                }
                            }
                        }
                    }
                }
            }else{
                JSONArray answers = ((JSONObject) questions.get(i)).getJSONArray("answers");
                if(i<questions.size()-1){
                    for (int j = 0; j < answers.size(); j++) {
                        if (lists2.get(i).equals(((JSONObject) answers.get(j)).getString("id"))) {
                            sb.append(((JSONObject) answers.get(j)).getString("name"));
                            sb.append(" 、 ");
                        }
                    }
                }else{
                    for(int a=questions.size()-1;a<lists2.size();a++){
                        for (int j = 0; j < answers.size(); j++) {
                            if (lists2.get(a).equals(((JSONObject) answers.get(j)).getString("id"))) {
                                sb2.append(((JSONObject) answers.get(j)).getString("name"));
                                sb2.append(" 、 ");
                            }
                        }
                    }
                }
            }
        }
        sb.append(sb2.toString());
        if(sb.length()>1){
            sb.deleteCharAt(sb.length() - 1);
            map.put("product_name", sb.toString());
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