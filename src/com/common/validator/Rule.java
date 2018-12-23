package com.common.validator;

/**
 * 验证规则.
 * 后期改用继承
 * @author: lijx
 * @CreateDate: 2016-8-10 上午11:19:29
 * @version: V 1.0
 */
public class Rule {
    /**
     * 验证不通过时提示消息
     */
    private String message;
    /**
     * 规则名称
     */
    private String name;
    /**
     * 规则名称为：regexp 时使用
     * 正则表达式
     */
    private String regexp;
    /**
     * 最小长度
     */
    private Integer min;
    /**
     * 最大长度
     */
    private Integer max;
    
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getRegexp() {
        return regexp;
    }
    public void setRegexp(String regexp) {
        this.regexp = regexp;
    }
    public Integer getMin() {
        return min;
    }
    public void setMin(Integer min) {
        this.min = min;
    }
    public Integer getMax() {
        return max;
    }
    public void setMax(Integer max) {
        this.max = max;
    }
}
