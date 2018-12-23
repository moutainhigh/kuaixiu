package com.common.importExcel;

/**
 * 导入，具体错误提示
 */
public class ImportError {
    //信息描述的位置
    private String position;
    //信息的类型
    private String msgType;
    //给客户的建议
    private String message;
    
    public String getPosition() {
        return position;
    }
    public void setPosition(String position) {
        this.position = position;
    }
    public String getMsgType() {
        return msgType;
    }
    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}