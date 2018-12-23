package com.system.wechat.handler;

import com.common.wechat.common.session.WxSessionManager;
import com.common.wechat.api.WxMpService;
import com.common.wechat.bean.WxMpXmlMessage;
import com.common.wechat.bean.WxMpXmlOutMessage;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 对所有接收到的消息输出日志，也可进行持久化处理
 *
 * Created by FirenzesEagle on 2016/7/27 0027.
 * Email:liumingbo2008@gmail.com
 */
@Component
public class LogHandler extends AbstractHandler {
    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService wxMpService,
                                    WxSessionManager sessionManager) {
        this.logger.info("\n接收到请求消息，内容：【{}】", wxMessage.toString());
        return null;
    }
}