package com.common.validator;

import java.util.List;

/**
 * 验证属性.
 * 
 * @author: lijx
 * @CreateDate: 2016-8-10 上午11:34:55
 * @version: V 1.0
 */
public class Field {
    
    /**
     * 属性名称
     */
    private String name;
    
    /**
     * 验证规则
     */
    private List<Rule> rules;
    
    /**
     * 嵌套属性
     */
    private List<Field> fields;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    public List<Rule> getRules() {
        return rules;
    }
    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }
    
    public List<Field> getFields() {
        return fields;
    }
    public void setFields(List<Field> fields) {
        this.fields = fields;
    }
    
}
