package com.common.exception;
/**
 * session失效异常类.
 * 
 * @CreateDate: 2016-8-23 下午8:35:01
 * @version: V 1.0
 */
public class SessionInvalidateException extends RuntimeException{

    /**
     * 序列化id
     */
    private static final long serialVersionUID = 1L;

    /**
     * 保存出错时用户操作的url地址，方便用户返回刚才的操作页面
     */
    private String backUrl=null;
    
    public SessionInvalidateException(String msg){
        super(msg);
    }
    
    /**
     * @param msg 出错提示信息
     * @param backUrl 可以传入出错时操作的url地址，方便用户返回刚才的操作页面。
     */
    public SessionInvalidateException(String msg,String backUrl){
        super(msg);
        this.backUrl = backUrl;
    }
    
    /**
     * 返回回调路径
     * @return
     * @author: lijx
     * @CreateDate: 2016-6-3 上午11:16:18
     */
    public String getBackUrl() {
        return backUrl;
    }

    public void setBackUrl(String backUrl) {
        this.backUrl = backUrl;
    }
}
