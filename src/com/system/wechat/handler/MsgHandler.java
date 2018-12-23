package com.system.wechat.handler;

import com.common.wechat.common.exception.WxErrorException;
import com.common.wechat.common.session.WxSessionManager;
import com.common.wechat.api.WxMpService;
import com.common.wechat.bean.WxMpXmlMessage;
import com.common.wechat.bean.WxMpXmlOutMessage;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 转发客户消息Handler
 *
 * Created by FirenzesEagle on 2016/7/27 0027.
 * Email:liumingbo2008@gmail.com
 */
@Component
public class MsgHandler extends AbstractHandler {
    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) throws WxErrorException {
        return WxMpXmlOutMessage
                .TRANSFER_CUSTOMER_SERVICE().fromUser(wxMessage.getToUserName())
                .toUser(wxMessage.getFromUserName()).build();
    }
}
