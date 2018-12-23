package com.common.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.exception.SystemException;
import com.system.constant.SystemConstant;
import com.system.util.SystemUtil;

/**
 * 百度地图工具类.
 * 
 * @CreateDate: 2016-9-4 下午10:05:10
 * @version: V 1.0
 */
public class BaiduMapUtil {
    
    private static final String baiduMapApiUrl = "http://api.map.baidu.com";
    
    private static final String baiduGeocoderUrl = "/geocoder/v2/";
    
    private static final String gaodeMapApiUrl = "http://restapi.amap.com";
    
    private static final String gaodeGeocoderUrl = "/v3/geocode/geo";
    
    private static final String gaodeTobaidu="http://api.map.baidu.com/geoconv/v1/?coords=";
    
    
    /**
     * 高精度IP定位API
     */
    private static final String baiduHighacciplocUrl = "/highacciploc/v1/";
    /**
     * location定位
     */
    private static final String baiduLocationiplocUrl = "/location/ip";
    
    
    /**
     * ！！！因为百度api转化地址不准确 但是可以判断地址的可信度
     * 所以先根据地址得到百度经纬度  如果坐标可信则继续采用高德转化坐标后 再转化为百度坐标
     * 如果百度返回结果显示不可信则直接返回 地址可信度低
     * @param addr
     */
    public static JSONObject getLatAndLngByAddr(String addr){
    	JSONObject news=null;   //返回的json格式坐标数据
        //拼接路径
        StringBuffer url = new StringBuffer();
        url.append(baiduMapApiUrl).append(baiduGeocoderUrl);
        url.append("?output=json&ak=").append(SystemUtil.getSysCfgProperty(SystemConstant.BAIDU_MAP_API_AK_KEY));
        // 对URL进行编码
        try {
            url.append("&address=").append(URLEncoder.encode(addr, "UTF-8"));
        } 
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String response = HttpClientUtil.httpGet(url.toString());
        if (StringUtils.isBlank(response)) {
            throw new SystemException("接口未返回任何数据");
        }
        //解析返回数据
        JSONObject json = JSONObject.parseObject(response);
        if (json.getInteger("status") == 0) {
            json = json.getJSONObject("result");
            if (json.getInteger("confidence") < 30) {
                throw new SystemException("查询到的坐标可信度过低");
            }else{   //如果地址可信 则改用高德地图api转化后再转化为百度坐标
                news=gaode(addr);
            }
                return news;
        }
        else {
            throw new SystemException("查询坐标失败");
        }
    }
    
    
    
    
    
    /**
     * 通过高德api转化坐标再转化为百度坐标
     * @param address
     * @return
     */
    public static JSONObject gaode(String address){
    	JSONObject news=new JSONObject();  //返回封装好经纬度的json
    	  //拼接路径
        StringBuffer url = new StringBuffer();
        url.append(gaodeMapApiUrl).append(gaodeGeocoderUrl);
        // 对URL进行编码
        try {
            url.append("?address=").append(URLEncoder.encode(address, "UTF-8"));
            url.append("&output=JSON&key=").append(SystemUtil.getSysCfgProperty(SystemConstant.GAODE_MAP_API_AK_KEY));
        } 
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String response = HttpClientUtil.httpGet(url.toString());
        if (StringUtils.isBlank(response)) {
            throw new SystemException("接口未返回任何数据");
        }
        //解析返回数据
        JSONObject json = JSONObject.parseObject(response);
        if(json.getString("status").equals("0")||json.getString("count").equals("0")){
        	throw new SystemException("查询到的坐标可信度过低");
        }
        JSONArray array=(JSONArray) json.get("geocodes");
        String location=((JSONObject) array.get(0)).getString("location");
        
        //将高德坐标转化为百度坐标
        StringBuffer baiduUrl = new StringBuffer();
        baiduUrl.append(gaodeTobaidu);
        baiduUrl.append(location).append("&from=3&to=5&ak=").append(SystemUtil.getSysCfgProperty(SystemConstant.BAIDU_MAP_API_AK_KEY));
        String result= HttpClientUtil.httpGet(baiduUrl.toString());
        if (StringUtils.isBlank(result)) {
            throw new SystemException("接口未返回任何数据");
        }
        //解析返回数据
        JSONObject jsonResult=JSONObject.parseObject(result);
        if(jsonResult.getString("status").equals("0")){
        	//转换成功
        	JSONObject code=(jsonResult.getJSONArray("result")).getJSONObject(0);
        	news.put("lng", code.getString("x"));
            news.put("lat", code.getString("y"));
        }else{
        	//如果高德转百度失败则选择用高德
        	news.put("lng", location.substring(0,location.indexOf(",")));
    		news.put("lat", location.substring(location.indexOf(",")+1));
        }
    	return news;
    }
    
    
    /**
     * 根据
     * 以旧换新地址精确度上调至百分之30来测试
     */
    public static JSONObject getOldToNewLatAndLngByAddr(String addr){
        //拼接路径
        StringBuffer url = new StringBuffer();
        url.append(baiduMapApiUrl).append(baiduGeocoderUrl);
        url.append("?output=json&ak=").append(SystemUtil.getSysCfgProperty(SystemConstant.BAIDU_MAP_API_AK_KEY));
        // 对URL进行编码
        try {
            url.append("&address=").append(URLEncoder.encode(addr, "UTF-8"));
        } 
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String response = HttpClientUtil.httpGet(url.toString());
        if (StringUtils.isBlank(response)) {
            throw new SystemException("接口未返回任何数据");
        }
        //解析返回数据
        JSONObject json = JSONObject.parseObject(response);
        if (json.getInteger("status") == 0) {
            json = json.getJSONObject("result");
            if (json.getInteger("confidence") < 30) {
                throw new SystemException("查询到的坐标可信度过低");
            }
            else {
                return json.getJSONObject("location");
            }
        }
        else {
            throw new SystemException("查询坐标失败");
        }
    }
    
    /**
     * 根据
     * @param addr
     * @return
     * @author: lijx
     * @CreateDate: 2016-9-4 下午10:14:02
     */
    public static JSONObject getLatAndLngByIP(String ip){
        //拼接路径
        StringBuffer url = new StringBuffer();
        url.append(baiduMapApiUrl).append(baiduLocationiplocUrl);
        url.append("?ak=").append(SystemUtil.getSysCfgProperty(SystemConstant.BAIDU_MAP_API_AK_KEY));
        // 对URL进行编码
        url.append("&ip=").append(ip);
        url.append("&coor=bd09ll");
        String response = HttpClientUtil.httpGet(url.toString());
        if (StringUtils.isBlank(response)) {
            throw new SystemException("哎呦！网络好像不太稳定，请稍等片刻");
        }
        //解析返回数据
        JSONObject json = JSONObject.parseObject(response);
        if (json.getInteger("status") != 0) {
        	 throw new SystemException("亲，您是火星来的吗？为什么查不到您的位置。");
        }
        JSONObject result = json.getJSONObject("content");
        JSONObject address=result.getJSONObject("point");
        	return address;
    }
    
    /**
     * 获取百度坐标两点之间的距离
     * @param lat_a 坐标a维度
     * @param lng_a 坐标a经度
     * @param lat_b 坐标b维度
     * @param lng_b 坐标b经度
     * @return 返回两个坐标之间的距离，单位米
     * @CreateDate: 2016-9-15 下午4:23:07
     */
    public static double getDistanceFromXtoY(double lat_a,double lng_a,double lat_b,double lng_b){
        double pk = 180 / 3.14169  ;
        double a1 = lat_a / pk  ;
        double a2 = lng_a / pk  ;
        double b1 = lat_b / pk  ;
        double b2 = lng_b / pk  ;
        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2)  ;
        double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2)  ;
        double t3 = Math.sin(a1) * Math.sin(b1)  ;
        double tt = Math.acos(t1 + t2 + t3)  ;
        return 6366000 * tt;
    }
    
    /**
     * 
     * @param args
     * @CreateDate: 2016-9-15 下午4:25:31
     */
//    public static void main(String[] args) {
//        System.out.println(BaiduMapUtil.getDistanceFromXtoY(37.480563, 121.467113, 37.480591, 121.467926));
//        //117.086393,36.686631
//        //117.087017,36.678158
//        System.out.println(BaiduMapUtil.getDistanceFromXtoY(36.67954200000000, 117.10230300000000
//                ,36.648971, 117.014916));
//        System.out.println(BaiduMapUtil.getDistanceFromXtoY(36.61653400000000, 116.96892200000000
//                ,36.648971, 117.014916));
//        System.out.println(BaiduMapUtil.getDistanceFromXtoY(36.66842600000000, 117.01721500000000
//                ,36.648971, 117.014916));
//        System.out.println(BaiduMapUtil.getDistanceFromXtoY(36.67120500000000, 116.95742400000000
//                ,36.648971, 117.014916));
//        
//    }
}
