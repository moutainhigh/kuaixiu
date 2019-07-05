package com.kuaixiu.wechat.controller;

import com.alibaba.fastjson.JSONObject;
import com.common.util.GlobalConstants;
import com.common.util.HttpClientUtil;
import com.common.wechat.aes.AesSignUtil;
import com.common.wechat.common.util.StringUtils;
import com.kuaixiu.brand.service.BrandService;
import com.kuaixiu.coupon.service.CouponModelService;
import com.kuaixiu.coupon.service.CouponProjectService;
import com.kuaixiu.coupon.service.CouponService;
import com.kuaixiu.project.service.ProjectService;
import com.kuaixiu.wechat.entity.WechatUser;
import com.kuaixiu.wechat.service.WechatUserService;
import com.system.constant.SystemConstant;
import com.system.wechat.util.SignUtil;
import com.system.wechat.util.TextMessage;
import com.system.wechat.util.XMLUtil;
import com.thoughtworks.xstream.XStream;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: anson
 * @CreateDate: 2018年1月4日 上午9:58:40
 * @version: V 1.0 用作微信中央服务器 处理微信订阅，文本，图像等消息的处理
 */
@Controller
public class WechatSecurity {

    private static Logger logger = Logger.getLogger(WechatSecurity.class);

    @Autowired
    private WechatUserService wechatUserService;

    @Autowired
    private CouponService couponService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private CouponModelService modelService;
    @Autowired
    private CouponProjectService couponProjectService;

    /**
     * 处理微信验证服务器的方法
     *
     * @param request
     * @param response
     * @param signature
     * @param timestamp
     * @param nonce
     * @param echostr
     */
    @RequestMapping(value = "wechat/getNews", method = RequestMethod.GET)
    public void doGet(HttpServletRequest request, HttpServletResponse response,
                      @RequestParam(value = "signature", required = true) String signature,
                      @RequestParam(value = "timestamp", required = true) String timestamp,
                      @RequestParam(value = "nonce", required = true) String nonce,
                      @RequestParam(value = "echostr", required = true) String echostr) {
        try {
            if (SignUtil.checkSignature(signature, timestamp, nonce)) {
                PrintWriter out = response.getWriter();
                out.print(echostr);
                out.close();
            } else {
                logger.info("这里存在非法请求！");
            }
        } catch (Exception e) {
            logger.error(e, e);
        }
    }

    /**
     * post方法用于接收微信服务端消息
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "wechat/getNews", method = RequestMethod.POST)
    public void DoPost(HttpServletRequest request, HttpServletResponse response) {
        String news = "";     //回复内容主题
        try {
            // 响应消息    不知道中文模式下会乱码
            OutputStreamWriter outWriter = new OutputStreamWriter(response.getOutputStream(), "utf-8");
            PrintWriter out = new PrintWriter(outWriter);

            request.setCharacterEncoding("UTF-8");
            response.setCharacterEncoding("UTF-8");
            //加密类型
            String encryptType = request.getParameter("encrypt_type");

            Map<String, String> map = null;
            System.out.println(encryptType);
            if (encryptType != null && encryptType.equals("aes")) {
                System.out.println("安全模式");
                map = AesSignUtil.parseXmlCrypt(request);
            } else {
                map = XMLUtil.parseRequestXmlToMap(request);
            }


            String msgtype = map.get("MsgType"); // 消息类型
            // 如果是订阅或者取消订阅事件则处理
            String openid = map.get("FromUserName");        // 用户openid
            String mpid = map.get("ToUserName");            // 公众号原始ID
            logger.info("openid：" + openid + " mpid：" + mpid + "消息类型：" + msgtype + " 消息内容：" + map.get("Content"));
            // 返回普通文本消息
            TextMessage txtmsg = new TextMessage();
            txtmsg.setToUserName(openid);
            txtmsg.setFromUserName(mpid);
            txtmsg.setCreateTime(new Date().getTime());
            txtmsg.setMsgType(SystemConstant.REQ_MESSAGE_TYPE_TEXT);

            if (SystemConstant.REQ_MESSAGE_TYPE_EVENT.equals(msgtype)) {
                if (map.get("Event").equals(SystemConstant.EVENT_TYPE_SUBSCRIBE)) {
                    // 查询该用户是否存在
                    WechatUser user = wechatUserService.queryById(openid);
                    logger.info("关注事件");
                    // 如果是订阅事件 第一次关注记录到数据库
                    if (user == null) {
                        logger.info("新用户关注");
                        JSONObject result = getWechatUserInfo(openid);
                        // 记录关注用户信息
                        WechatUser u = new WechatUser();
                        u.setOpenId(openid);
                        u.setSex(result.getInteger("sex"));
                        u.setNickname(result.getString("nickname"));
                        u.setHeadimgurl(result.getString("headimgurl"));
                        wechatUserService.add(u);
                        txtmsg.setContent(menuText());//回复消息文本
                    } else {
                        logger.info("有人关注了呢");
                        //更新关注人的信息
                        JSONObject result = getWechatUserInfo(openid);
                        user.setSex(result.getInteger("sex"));
                        user.setNickname(result.getString("nickname"));
                        user.setHeadimgurl(result.getString("headimgurl"));
                        wechatUserService.saveUpdate(user);
                        out.close();
                        return;
                    }
                } else if (map.get("Event").equals(SystemConstant.EVENT_TYPE_UNSUBSCRIBE)) {
                    // 如果是取消订阅事件 更新下信息
                    WechatUser user = wechatUserService.queryById(openid);
                    if (user != null) {
                        user.setIsSubscribe(1); // 已取消关注
                        wechatUserService.saveUpdate(user);
                    }
                    logger.info("有人取消关注了");
                } else {
                    if (map.get("EventKey") != null && map.get("EventKey").equals("hdyh")) {
                        // 活动优惠点击消失推送事件
                        news = "1月份的微信晒单返红包活动，完成维修后进入订单页面，把订单转发朋友圈的用户，工程师当场返利，换电池返10元，换屏返20元。";
                        txtmsg.setContent(news);
                    } else {
                        out.close();
                        return;
                    }
                }
            } else if (SystemConstant.REQ_MESSAGE_TYPE_TEXT.equals(msgtype)) {
                String content = map.get("Content");
                Pattern pattern = Pattern.compile("\\s*|\t|\r|\n");
                Matcher m = pattern.matcher(content);
                content = m.replaceAll("");
                if ("1".equals(content)) {
                    // 发送通用维修优惠券.活动已取消
                    //String commonCode = wechatUserService.createCoupon(SystemConstant.WECHAT_COMMON_BATCHID, SystemConstant.WECHAT_COMMON_PRICE);
                    // 发送换膜优惠券
                    String screenCode = wechatUserService.getScreenCode();
                    // 发放优惠券
                    WechatUser user = wechatUserService.queryById(openid);
                    JSONObject result = getWechatUserInfo(openid);
                    user.setSex(result.getInteger("sex"));
                    user.setNickname(result.getString("nickname"));
                    user.setHeadimgurl(result.getString("headimgurl"));
                    //user.setCommonCouponCode(commonCode);
                    user.setScreenCouponCode(screenCode);
                    wechatUserService.saveUpdate(user);
                    txtmsg.setContent("感谢您的关注，M超人精心为您准备了1张优惠劵，可以在公众号中“我的卡劵”中查看或点击下面链接https://m-super.com/wechat/card.html 来查看。");
                } else if ("2".equals(content)) {
                    txtmsg.setContent("https://m-super.com/wechat/repair/index.do?fm=5");
                } else if ("3".equals(content)) {
                    txtmsg.setContent("https://m-super.com/ty_wap/index.html");
                } else {
                    out.close();
                    return;
                }
            } else {
                out.close();
                return;
                //news="测试消息返回";
                //txtmsg.setContent(news);
            }
            news = textMessageToXml(txtmsg);
            logger.info("发送内容：" + news);
            out.print(news);
            out.close();
        } catch (Exception e) {
            logger.error(e, e);
        }
    }

    /**
     * 关注后发送消息文本
     */
    private static String menuText() {
        StringBuffer sb = new StringBuffer();
        sb.append("等了您好久，您终于来关注我啦，超人已经做好为您服务的准备啦！\n");
        sb.append("回复“1”即可获得1张贴膜优惠券。\n");//取消“和1张通用优惠券”
        sb.append("回复“2”即可直接进入快速维修通道。\n");
        sb.append("回复“3”即可直接进入手机回收通道。\n\n");
        sb.append("超懂手机超懂你，更多福利活动每周更新，欢迎来撩~\n");
        return sb.toString();
    }

    /**
     * 通过access_token获取用户基本信息
     */
    private static JSONObject getWechatUserInfo(String openid) {
        JSONObject result = new JSONObject();
        logger.info("开始获取微信用户信息");
        StringBuffer sb = new StringBuffer();
        //判定定时器获取的access_token
        if (StringUtils.isBlank(GlobalConstants.getInterfaceUrl("access_token"))) {
            //token为空 则主动再次请求微信服务器获取
            getAccessToken();
        }
        sb.append(SystemConstant.WECHAT_OPENIDUSERINFOURL);
        sb.append("?access_token=").append(GlobalConstants.getInterfaceUrl("access_token"));
        sb.append("&openid=").append(openid);
        sb.append("&lang=zh_CN");

        //请求该URL
        String httpGet = HttpClientUtil.httpGet(sb.toString());
        if (StringUtils.isNotBlank(httpGet)) {
            result = JSONObject.parseObject(httpGet);
            if (result.getInteger("errcode") != null) {
                if (40001 == result.getInteger("errcode") || 42001 == result.getInteger("errcode")) {
                    GlobalConstants.interfaceUrlProperties.remove("access_token");
                    result = getWechatUserInfo(openid);
                }
            }
        } else {
            logger.info("微信用户信息返回异常");
        }
        return result;

    }

    /**
     * 主动获取微信access_token
     */
    public static void getAccessToken() {
        logger.info("主动获取微信access_token");
        StringBuffer sb = new StringBuffer();
        sb.append(SystemConstant.WECHAT_TOKENURL);
        sb.append("?grant_type=client_credential").append("&appid=").append(SystemConstant.APP_ID);
        sb.append("&secret=").append(SystemConstant.APP_SECRET);
        //请求该URL
        String httpGet = HttpClientUtil.httpGet(sb.toString());
        //得到access_token写入指定properties文件
        if (StringUtils.isNotBlank(httpGet)) {
            JSONObject fromObject = JSONObject.parseObject(httpGet);
            String token = fromObject.getString("access_token");
            GlobalConstants.interfaceUrlProperties.put("access_token", token);
        } else {
            logger.info("获取微信access_token返回为空");
        }

    }


    /**
     * 文本消息对象转换成xml
     *
     * @param textMessage
     * @return
     */
    private static String textMessageToXml(TextMessage textMessage) {
        XStream xstreams = new XStream();
        xstreams.alias("xml", textMessage.getClass());
        return xstreams.toXML(textMessage);
    }

}
