package com.system.api.entity;
/**
 * 接口统一返回数据对象
 */
public class ResultData {
    /**
     * 是否成功（true or false）
     */
    private boolean success;
    /**
     * 返回数据描述
     */
    private String resultMessage;
    /**
     * 业务代码标记
     */
    private String resultCode;
    /**
     * 返回业务数据
     */
    private Object result;
    
    
    public boolean isSuccess() {
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
    public String getResultCode() {
        return resultCode;
    }
    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }
    public Object getResult() {
        return result;
    }
    public void setResult(Object result) {
        this.result = result;
    }
}
