package com.kuaixiu.recycle.entity;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Administrator on 2018/9/18/018.
 */
public class WechatTemplate {

    private String touser;

    private String template_id;

    private String url;

    private Map<String, TemplateData> data;

    public static JSONObject item(String value, String color) {
        JSONObject params = new JSONObject();
        params.put("value", value);
        params.put("color", color);
        return params;
    }

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public String getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(String template_id) {
        this.template_id = template_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, TemplateData> getData() {
        return data;
    }

    public void setData(Map<String, TemplateData> data) {
        this.data = data;
    }
}
