package com.system.wechat.handler;

import com.system.wechat.service.CoreService;
import com.common.wechat.common.exception.WxErrorException;
import com.common.wechat.common.session.WxSessionManager;
import com.common.wechat.api.WxMpConfigStorage;
import com.common.wechat.api.WxMpService;
import com.common.wechat.bean.WxMpXmlMessage;
import com.common.wechat.bean.WxMpXmlOutMessage;
import com.common.wechat.bean.WxMpXmlOutTextMessage;
import com.common.wechat.bean.result.WxMpUser;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 用户关注公众号Handler
 *
 * Created by FirenzesEagle on 2016/7/27 0027.
 * Email:liumingbo2008@gmail.com
 */
@Component
public class TextMsgHandler extends AbstractHandler {

    @Autowired
    protected WxMpConfigStorage configStorage;
    @Autowired
    protected WxMpService wxMpService;
    @Autowired
    protected CoreService coreService;


    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) throws WxErrorException {
        WxMpUser wxMpUser = coreService.getUserInfo(wxMessage.getFromUserName(), "zh_CN");

        WxMpXmlOutTextMessage m
                = WxMpXmlOutMessage.TEXT()
                .content("<a href=\"http://kuaixiu.tk/wechat/repair/index.do\">M-超人</a>")
                .fromUser(wxMessage.getToUserName())
                .toUser(wxMessage.getFromUserName())
                .build();
        logger.info("subscribeMessageHandler" + m.getContent());
        return m;
    }
};
