package com.common.validator;

/**
 * 验证结果.
 * 
 * @author: lijx
 * @CreateDate: 2016-8-10 下午2:02:53
 * @version: V 1.0
 */
public class ValidateResult {
    /**
     * 验证是否通过
     */
    private boolean success;
    /**
     * 验证不通过时返回提示信息
     */
    private String resultMessage;
    
    public boolean getSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getResultMessage() {
        return resultMessage;
    }
    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }
}
