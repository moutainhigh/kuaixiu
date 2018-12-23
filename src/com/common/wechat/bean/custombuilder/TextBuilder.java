package com.common.wechat.bean.custombuilder;

import com.common.wechat.common.api.WxConsts;
import com.common.wechat.bean.WxMpCustomMessage;

/**
 * 文本消息builder
 * <pre>
 * 用法: WxMpCustomMessage m = WxMpCustomMessage.TEXT().content(...).toUser(...).build();
 * </pre>
 * @author chanjarster
 *
 */
public final class TextBuilder extends BaseBuilder<TextBuilder> {
  private String content;

  public TextBuilder() {
    this.msgType = WxConsts.CUSTOM_MSG_TEXT;
  }

  public TextBuilder content(String content) {
    this.content = content;
    return this;
  }

  @Override
  public WxMpCustomMessage build() {
    WxMpCustomMessage m = super.build();
    m.setContent(this.content);
    return m;
  }
}
