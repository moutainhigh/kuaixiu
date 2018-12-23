package com.common.exception;

/**
 * 接口服务异常.
 * 
 * @CreateDate: 2016-9-5 上午12:09:58
 * @version: V 1.0
 */
public class ApiServiceException extends RuntimeException {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * 接口返回码   成功返回0
     */
    private String resultCode;
    
    /**
     * 创建接口异常对象
     * @param resultCode 接口错误码
     * @param msg 错误描述
     */
    public ApiServiceException(String resultCode, String msg){
        super(msg);
        this.resultCode = resultCode;
    }
    /**
     * 异常提醒
     */
    public ApiServiceException(String msg){
        super(msg);
    }
    
    /**
     * 
     * @return
     * @CreateDate: 2016-5-30 下午3:29:18
     */
    public String getResultCode(){
        return resultCode;
    }

    /**
     * toString
     */
    public String toString(){
        return getMessage();
    }
}
