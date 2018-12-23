package com.common.validator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


/**
 * 验证实现类.
 * 
 * @author: lijx
 * @CreateDate: 2016-8-10 上午11:39:17
 * @version: V 1.0
 */
public class Validator {
    /**
     * 验证属性
     */
    private List<Field> fields;
    
    public Validator(String fieldsJsonStr){
        if(null != fieldsJsonStr){
            JSONArray fieldsJson = JSONArray.parseArray(fieldsJsonStr);
            fields = buildFields(fieldsJson);
        }
    }
    
    /**
     * 通过Json对象创建验证对象
     * @param fieldsJson
     */
    public Validator(JSONArray fieldsJson){
        fields = buildFields(fieldsJson);
    }

    /**
     * 组装属性验证规则
     * @param fieldsJson
     * @author: lijx
     * @CreateDate: 2016-8-10 下午1:47:49
     */
    private List<Field> buildFields(JSONArray fieldsJson) {
        List<Field> fields = new ArrayList<Field>();
        if(null != fieldsJson){
            //属性列表
            for(int i = 0; i < fieldsJson.size(); i++){
                JSONObject fieldObj = fieldsJson.getJSONObject(i);
                Field field = new Field();
                //获取属性名称
                field.setName(fieldObj.getString("name"));
                //获取验证规则
                if (fieldObj.containsKey("validators")) {
                    JSONObject rulesJson = fieldObj.getJSONObject("validators");
                    Iterator<String> it = rulesJson.keySet().iterator();
                    
                    List<Rule> rules = new ArrayList<Rule>();
                    while(it.hasNext()){
                        String key = it.next();
                        JSONObject ruleObj = rulesJson.getJSONObject(key);
                        Rule rule = new Rule();
                        //规则名称
                        rule.setName(key);
                        //提示消息
                        rule.setMessage(ruleObj.getString("message"));
                        //正则表达式
                        rule.setRegexp(ruleObj.getString("regexp"));
                        //最小长度
                        rule.setMin(ruleObj.getInteger("min"));
                        //最大长度
                        rule.setMax(ruleObj.getInteger("max"));
                        rules.add(rule);
                    }
                    //设置属性验证规则
                    field.setRules(rules);
                }
                //判断是否有嵌套属性
                if (fieldObj.containsKey("fields")) {
                    field.setFields(buildFields(fieldObj.getJSONArray("fields")));
                }
                
                fields.add(field);
            }
        }
        
        return fields;
    }
    
    /**
     * 执行验证方法
     * @param obj
     * @return
     * @author: lijx
     * @CreateDate: 2016-8-10 下午2:06:23
     */
    public ValidateResult validate(JSONArray array){
        ValidateResult result = new ValidateResult();
        if (null == array || array.size() == 0) {
            result.setResultMessage("验证对象为空");
            return result;
        }
        for (int i = 0; i < array.size(); i++) {
            result = validate(array.getJSONObject(i));
            if (!result.getSuccess()) {
                break;
            }
        }
        return result;
    }
    
    /**
     * 执行验证方法
     * @param obj
     * @return
     * @author: lijx
     * @CreateDate: 2016-8-10 下午2:06:23
     */
    public ValidateResult validate(JSONObject obj){
        return validate(obj, fields);
    }
    
    /**
     * 执行验证方法
     * @param obj
     * @return
     * @author: lijx
     * @CreateDate: 2016-8-10 下午2:06:23
     */
    public ValidateResult validate(Object obj, List<Field> fields){
        ValidateResult result = new ValidateResult();
        result.setSuccess(true);
        if (null == obj || "".equals(obj.toString().trim())) {
            return result;
        }
        if (obj instanceof JSONObject) {
            result = validate((JSONObject)obj, fields);
        }
        else if (obj instanceof JSONArray) {
            result = validate((JSONArray)obj, fields);
        }
        return result;
    }
    
    /**
     * 执行验证方法
     * @param obj
     * @return
     * @author: lijx
     * @CreateDate: 2016-8-10 下午2:06:23
     */
    public ValidateResult validate(JSONArray array, List<Field> fields){
        ValidateResult result = new ValidateResult();
        result.setSuccess(true);
        if (null != array && array.size() > 0) {
            for (int i = 0; i < array.size(); i++) {
                result = validate(array.getJSONObject(i), fields);
                if (!result.getSuccess()) {
                    break;
                }
            }
        }
        return result;
    }
    
    /**
     * 执行验证方法
     * @param obj
     * @return
     * @author: lijx
     * @CreateDate: 2016-8-10 下午2:06:23
     */
    public ValidateResult validate(JSONObject obj, List<Field> fields){
        ValidateResult result = new ValidateResult();
        if(obj == null){
            return result;
        }
        result.setSuccess(true);
        if (null != fields) {
            //循环验证属性
            for (Field field : fields) {
                if (null != field.getRules()) {
                    String value = null;
                    if (obj.containsKey(field.getName())) {
                        value = obj.getString(field.getName());
                    }
                    //循环属性验证规则
                    for (Rule rule : field.getRules()) {
                        //非空验证
                        if ("notEmpty".equals(rule.getName())) {
                            if(null == value 
                                    || "".equals(value.trim()) 
                                    || "null".equals(value.trim())) {
                                result.setSuccess(false);
                                result.setResultMessage(rule.getMessage());
                                break;
                            }
                        }
                        //字符长度验证
                        else if ("stringLength".equals(rule.getName()) && null != value && !"".equals(value.trim())) {
                            if (null != rule.getMax() && value.length() > rule.getMax()
                                   && null != rule.getMin() && value.length() < rule.getMin()) {
                                result.setSuccess(false);
                                result.setResultMessage(rule.getMessage());
                                break;
                            }
                        }
                        //正则表达式验证
                        else if ("regexp".equals(rule.getName()) && null != value && !"".equals(value.trim())) {
                            if (!value.matches(rule.getRegexp())) {
                                result.setSuccess(false);
                                result.setResultMessage(rule.getMessage());
                                break;
                            }
                        }
                    }
                }
                
                //判断是否有嵌套属性
                if (null != field.getFields()) {
                    Object sub = obj.get(field.getName());
                    if (null != sub) {
                        result = validate(sub, field.getFields());
                    }
                }
                
                if (!result.getSuccess()) {
                    break;
                }
            }
        }
        return result;
    }
}
