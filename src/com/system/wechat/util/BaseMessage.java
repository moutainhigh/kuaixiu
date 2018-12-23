package com.system.wechat.util;
/**
* @author: anson
* @CreateDate: 2018年1月4日 下午12:37:31
* @version: V 1.0
* 返回消息体基本信息
*/
public class BaseMessage {  
    // 接收方帐号（收到的OpenID）   
    private String ToUserName;  
    // 开发者微信号   
    private String FromUserName;  
    // 消息创建时间 （整型）   
    private long CreateTime;  
    // 消息类型（text/music/news）   
    private String MsgType;  
      
   
    public String getToUserName() {  
        return ToUserName;  
    }  
   
    public void setToUserName(String toUserName) {  
        ToUserName = toUserName;  
    }  
   
    public String getFromUserName() {  
        return FromUserName;  
    }  
   
    public void setFromUserName(String fromUserName) {  
        FromUserName = fromUserName;  
    }  
   
    public long getCreateTime() {  
        return CreateTime;  
    }  
   
    public void setCreateTime(long createTime) {  
        CreateTime = createTime;  
    }  
   
    public String getMsgType() {  
        return MsgType;  
    }  
   
    public void setMsgType(String msgType) {  
        MsgType = msgType;  
    }  
   
   
}
