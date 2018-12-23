package com.common.exception;

/**
 * Response异常.
 * 
 * @CreateDate: 2016-9-4 下午8:18:26
 * @version: V 1.0
 */
public class ResponseException extends RuntimeException{
    /**
     * 序列化id
     */
    private static final long serialVersionUID = 1L;

    public ResponseException(String message){
        super(message);
    }

    public ResponseException(Throwable cause) {
        super(cause);
    }

    public ResponseException(String message, Throwable cause) {
        super(message, cause);
    }
}
