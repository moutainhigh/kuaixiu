package com.common.wechat.bean;

import com.common.wechat.util.json.WxMpGsonBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * OpenId列表群发的消息
 * 
 * @author chanjarster
 */
public class WxMpMassOpenIdsMessage implements Serializable {
  private static final long serialVersionUID = -8022910911104788999L;
  
  private List<String> toUsers = new ArrayList<>();
  private String msgType;
  private String content;
  private String mediaId;

  public WxMpMassOpenIdsMessage() {
    super();
  }
  
  public String getMsgType() {
    return this.msgType;
  }

  /**
   * <pre>
   * 请使用
   * {@link com.common.wechat.common.api.WxConsts#MASS_MSG_IMAGE}
   * {@link com.common.wechat.common.api.WxConsts#MASS_MSG_NEWS}
   * {@link com.common.wechat.common.api.WxConsts#MASS_MSG_TEXT}
   * {@link com.common.wechat.common.api.WxConsts#MASS_MSG_VIDEO}
   * {@link com.common.wechat.common.api.WxConsts#MASS_MSG_VOICE}
   * 如果msgtype和media_id不匹配的话，会返回系统繁忙的错误
   * </pre>
   * @param msgType
   */
  public void setMsgType(String msgType) {
    this.msgType = msgType;
  }

  public String getContent() {
    return this.content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getMediaId() {
    return this.mediaId;
  }

  public void setMediaId(String mediaId) {
    this.mediaId = mediaId;
  }

  public String toJson() {
    return WxMpGsonBuilder.INSTANCE.create().toJson(this);
  }

  /**
   * OpenId列表，最多支持10,000个
   */
  public List<String> getToUsers() {
    return this.toUsers;
  }

  /**
   * 添加OpenId，最多支持10,000个
   * @param openId
   */
  public void addUser(String openId) {
    this.toUsers.add(openId);
  }
}
