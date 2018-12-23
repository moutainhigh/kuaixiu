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
public class SubscribeHandler extends AbstractHandler {

    @Autowired
    protected WxMpConfigStorage configStorage;
    @Autowired
    protected WxMpService wxMpService;
    @Autowired
    protected CoreService coreService;


    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) throws WxErrorException {
        WxMpUser wxMpUser = coreService.getUserInfo(wxMessage.getFromUserName(), "zh_CN");
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("openId", wxMpUser.getOpenId()));
        params.add(new BasicNameValuePair("nickname", wxMpUser.getNickname()));
        params.add(new BasicNameValuePair("headImgUrl", wxMpUser.getHeadImgUrl()));

        //TODO 在这里可以进行用户关注时对业务系统的相关操作（比如新增用户）

        WxMpXmlOutTextMessage m
                = WxMpXmlOutMessage.TEXT()
                .content("尊敬的" + wxMpUser.getNickname() + "，您好！")
                .fromUser(wxMessage.getToUserName())
                .toUser(wxMessage.getFromUserName())
                .build();
        logger.info("subscribeMessageHandler" + m.getContent());
        return m;
    }
};
