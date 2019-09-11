package com.common.util;

import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import com.kuaixiu.nbTelecomSJ.entity.NBArea;
import com.kuaixiu.nbTelecomSJ.entity.NBBusiness;
import com.kuaixiu.nbTelecomSJ.entity.NBManager;
import com.kuaixiu.order.entity.ReworkOrder;
import com.kuaixiu.recycle.entity.RecycleSystem;
import com.kuaixiu.recycle.service.RecycleSystemService;
import com.kuaixiu.sjBusiness.entity.SjVirtualTeam;
import com.kuaixiu.sjUser.entity.ConstructionCompany;
import com.kuaixiu.sjUser.entity.SjUser;
import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import sun.misc.BASE64Encoder;

import com.alibaba.fastjson.JSONObject;
import com.common.exception.SystemException;
import com.kuaixiu.coupon.entity.Coupon;
import com.kuaixiu.coupon.entity.CouponModel;
import com.kuaixiu.coupon.entity.CouponProject;
import com.kuaixiu.engineer.entity.Engineer;
import com.kuaixiu.oldtonew.entity.NewOrder;
import com.kuaixiu.oldtonew.entity.OldToNewUser;
import com.kuaixiu.order.entity.Order;
import com.kuaixiu.screen.entity.ScreenOrder;
import com.kuaixiu.shop.entity.Shop;
import com.system.util.SystemUtil;

/**
 * 短信发送接口.
 *
 * @CreateDate: 2016-9-13 下午7:50:26
 * @version: V 1.0
 */
public class SmsSendUtil {

    /**
     * 获取验证码
     *
     * @return
     * @CreateDate: 2016-9-13 下午8:01:55
     */
    public static String randomCode() {
        StringBuffer sb = new StringBuffer();
        sb.append(Math.random()).append("52136832");
        String code = sb.substring(2, 8);
        System.out.println("本次操作获取的验证码为：" + code);
        return code;
    }

    /**
     * 调用接口发送验证码
     *
     * @param mobile
     * @param randomCode
     * @CreateDate: 2016-9-13 下午8:15:21
     */
    public static boolean sendCheckCode(String mobile, String randomCode) {
        //获取验证码短信模板
        String content = (String) SystemUtil.getProperty("sms_checkCode");
        if (StringUtils.isBlank(content)) {
            content = "验证码：${code}，有效期30分钟，请及时验证。（如非本人操作，请忽略）";
        }
        content = content.replace("${code}", randomCode);
        //System.out.println(content);
        return sendSms2(mobile, content);
    }

    /**
     * 给以旧换新用户发送预约信息
     */
    public static boolean sendSmsToOldToNew(String mobile, OldToNewUser old) {
        StringBuffer content = new StringBuffer();
        content.append("您的预约已经完成，我们尽快联系您，");
        content.append("http://t.cn/E2y7OEC或关注m超人公众号免费估价回收，");
        content.append("凭此短信享受换机优惠100元。");
        return sendSmsThread(mobile, content.toString());
    }


    /**
     * 给碎屏险下单成功用户发送短信
     */
    public static boolean sendSmsToScreenUser(ScreenOrder o, int month) {
        StringBuffer content = new StringBuffer();
        String code = "1年期";
        if (month == 6) {
            code = "半年期";
        } else if (month == 12) {
            code = "1年期";
        } else {
            code = month + "个月期";
        }
        content.append("尊敬的顾客");
        content.append("，感谢您").append(o.getMobile());
        content.append("的手机号在M超人购买了").append(code);
        content.append("手机碎屏保障服务，请用另一部有摄像功能的手机激活需要保障的手机，激活网址为");
        content.append("http://api.linshaolong.cn/Telinfo/login?openid=tel，激活中有疑问请拨打4001013786服务热线。");
        System.out.println(content);
        return sendSmsThread(o.getMobile(), content.toString());
    }

    /**
     * 用户下单成功发送成功信息
     */
    public static boolean sendSmsToCustomer(String mobile) {
        StringBuffer content = new StringBuffer();
        content.append("您已下单成功，我们的工程师会尽快联系您的，请注意接听电话；");
        content.append("您也可以在http://t.cn/E2y7OEC或关注M超人公众号关注订单的动态。");
        content.append("客服热线：4008110299");
        return sendSmsThread(mobile, content.toString());
    }

    /**
     * 用户售后下单成功发送成功信息
     */
    public static boolean sendSmsToCustomerforRework(String mobile) {
        StringBuffer content = new StringBuffer();
        content.append("您的售后订单已生成，我们的工程师会尽快联系您的，请注意接听电话；");
        content.append("您也可以在http://t.cn/E2y7OEC或关注M超人公众号关注订单的动态。");
        content.append("客服热线：4008110299");
        return sendSmsThread(mobile, content.toString());
    }


    /**
     * 用户下寄修订单成功发送成功信息
     */
    public static boolean mailSendSmsToCustomer(String mobile) {
        StringBuffer content = new StringBuffer();
        content.append("您已下单成功，请及时寄出手机并选择邮寄到付，");
        content.append("我们会在收件后第一时间联系您，请保持联系电话的通畅；");
        content.append("您也可以在http://t.cn/E2y7OEC中查看订单的最新状态。");
        content.append("客服热线：4008110299");
        return sendSmsThread(mobile, content.toString());
    }


    /**
     * 给店员注册用户发送注册成功消息
     */
    public static boolean sendSmsToClerk(String mobile) {
        StringBuffer content = new StringBuffer();
        content.append("恭喜您注册手机超人快修店员成功，");
        content.append("了解手机超人请查看http://t.cn/E2y7OEC或关注m超人公众号.");
        return sendSmsThread(mobile, content.toString());
    }


    /**
     * 调用接口发送密码
     *
     * @param mobile
     * @param newPasswd
     * @CreateDate: 2016-9-13 下午8:15:21
     */
    public static boolean sendNewPasswd(String mobile, String newPasswd) {
        //获取验证码短信模板
        String content = (String) SystemUtil.getProperty("sms_newPasswd");
        if (StringUtils.isBlank(content)) {
            content = "新的登录密码为：${newPasswd}，登录后请及时修改密码。（如非本人操作，请忽略）";
        }
        content = content.replace("${newPasswd}", newPasswd);
        System.out.println(content);
        return sendSmsThread(mobile, content);
    }


    /**
     * 调用接口发送账号密码
     *
     * @param mobile
     * @param account
     * @CreateDate: 2016-9-13 下午8:15:21
     */
    public static boolean sendAccountAndPasswd(String mobile, String account, String passwd) {
        //获取验证码短信模板
        String content = (String) SystemUtil.getProperty("sms_newPasswd");
        if (StringUtils.isBlank(content)) {
            content = "您的登录账号为：${account}，密码：${passwd}，登录后请及时修改密码。（如非本人操作，请忽略）";
        }
        content = content.replace("${account}", account);
        content = content.replace("${passwd}", passwd);
        //System.out.println(content);
        return sendSmsThread(mobile, content);
    }

    /**
     * 给工程师发送派单提示短信
     *
     * @param eng
     * @param o
     * @CreateDate: 2016-9-15 下午11:35:01
     */
    public static boolean sendSmsToEngineer(Engineer eng, Shop s, Order o) {
        try {
            StringBuffer contentShop = new StringBuffer();
            contentShop.append("派单提醒，订单").append(o.getOrderNo());
            contentShop.append("已派单给工程师：").append(eng.getName());
            contentShop.append("，请注意关注订单处理进度。");
            if (StringUtils.isNotBlank(s.getManagerMobile1())) {
                sendSmsThread(s.getManagerMobile1(), contentShop.toString());
            }
            if (StringUtils.isNotBlank(s.getManagerMobile2())) {
                sendSmsThread(s.getManagerMobile2(), contentShop.toString());
            }
            if (StringUtils.isNotBlank(s.getManagerMobile())) {
                sendSmsThread(s.getManagerMobile(), contentShop.toString());
            }
        } catch (Exception e) {
        }
        StringBuffer content = new StringBuffer();
        content.append("工单提醒，订单").append(o.getOrderNo());
        content.append("已派单，请及时上门维修，联系电话：").append(o.getMobile());
        content.append("，联系人：").append(o.getCustomerName());
        content.append("，维修地址：").append(o.getFullAddress());
        return sendSmsThread(eng.getMobile(), content.toString());
    }

    /**
     * 售后给工程师发送派单提示短信
     *
     * @param eng
     * @param o
     * @CreateDate: 2016-9-15 下午11:35:01
     */
    public static boolean sendSmsToEngineerForRework(Engineer eng, Shop s, ReworkOrder o, Order order) {
        try {
            StringBuffer contentShop = new StringBuffer();
            contentShop.append("派单提醒，售后订单").append(o.getOrderReworkNo());
            contentShop.append("已派单给工程师：").append(eng.getName());
            contentShop.append("，请注意关注订单处理进度。");
            if (StringUtils.isNotBlank(s.getManagerMobile1())) {
                sendSmsThread(s.getManagerMobile1(), contentShop.toString());
            }
            if (StringUtils.isNotBlank(s.getManagerMobile2())) {
                sendSmsThread(s.getManagerMobile2(), contentShop.toString());
            }
            if (StringUtils.isNotBlank(s.getManagerMobile())) {
                sendSmsThread(s.getManagerMobile(), contentShop.toString());
            }
        } catch (Exception e) {
        }
        StringBuffer content = new StringBuffer();
        content.append("工单提醒，售后订单").append(o.getOrderReworkNo());
        content.append("已派单，请及时上门维修，联系电话：").append(order.getMobile());
        content.append("，联系人：").append(order.getCustomerName());
        content.append("，维修地址：").append(order.getFullAddress());
        return sendSmsThread(eng.getMobile(), content.toString());
    }

    /**
     * 给工程师发送派单提示短信(以旧换新)
     *
     * @param eng
     * @param o
     * @CreateDate: 2016-9-15 下午11:35:01
     */
    public static boolean sendSmsToEngineer(Engineer eng, Shop s, NewOrder o, OldToNewUser old) {
        try {
            StringBuffer contentShop = new StringBuffer();
            contentShop.append("派单提醒，订单").append(o.getOrderNo());
            contentShop.append("已派单给工程师：").append(eng.getName());
            contentShop.append("，请注意关注订单处理进度。");
            if (StringUtils.isNotBlank(s.getManagerMobile1())) {
                sendSmsThread(s.getManagerMobile1(), contentShop.toString());
            }
            if (StringUtils.isNotBlank(s.getManagerMobile2())) {
                sendSmsThread(s.getManagerMobile2(), contentShop.toString());
            }
            sendSmsThread(s.getManagerMobile(), contentShop.toString());
        } catch (Exception e) {
        }
        StringBuffer content = new StringBuffer();
        content.append("工单提醒，订单").append(o.getOrderNo());
        content.append("已派单，请及时上门兑换，旧机：").append(old.getOldMobile());
        content.append("，新机：").append(old.getNewMobile());
        content.append("，联系电话：").append(old.getTel());
        content.append("，联系人：").append(old.getName());
        content.append("，兑换地址：").append(old.getHomeAddress());
        return sendSmsThread(eng.getMobile(), content.toString());
    }

    /**
     * 给工程师发送派单提示短信
     *
     * @param eng
     * @param o
     * @CreateDate: 2016-9-15 下午11:35:01
     */
    public static boolean sendSmsToEngineerWithoutShop(Engineer eng, Order o) {
        StringBuffer content = new StringBuffer();
        content.append("工单提醒，订单").append(o.getOrderNo());
        content.append("已派单，请及时上门维修，联系电话：").append(o.getMobile());
        content.append("，联系人：").append(o.getCustomerName());
        content.append("，维修地址：").append(o.getFullAddress());
        return sendSmsThread(eng.getMobile(), content.toString());
    }


    /**
     * 店主将寄修订单派给工程师发送提醒短信
     */
    public static boolean sendSmsToEngineerForMailOrder(Engineer eng, Order o) {
        StringBuffer content = new StringBuffer();
        content.append("工单提醒，寄修订单").append(o.getOrderNo());
        content.append("已派单，请及时到店维修，联系电话：").append(o.getMobile());
        content.append("，联系人：").append(o.getCustomerName());
        return sendSmsThread(eng.getMobile(), content.toString());
    }


    /**
     * 寄修订单派单给门店后给门店负责人发送信息
     */
    public static boolean mailSendSmsToShop(Shop s, Order o) {
        StringBuffer content = new StringBuffer();
        content.append("工单提醒，您有新的寄修订单").append(o.getOrderNo());
        content.append("，机型型号：").append(o.getModelName());
        content.append("，机型颜色：").append(o.getColor());
        content.append("，请注意查收邮寄包裹！");
        return sendSmsThread(s.getTel(), content.toString());
    }

    /**
     * 商机提醒给包区人发送短信
     */
    public static boolean mailSendSmsTobusiness(NBManager manager, NBBusiness business, NBArea area) {
        String StrDemand = "";
        switch (business.getDemand()) {
            case 1:
                StrDemand = "无需求";
                break;
            case 2:
                StrDemand = "宽带体验";
                break;
            case 3:
                StrDemand = "专线体验";
                break;
            case 4:
                StrDemand = "战狼办理";
                break;
            case 5:
                StrDemand = "其他需求";
                break;
        }
        StringBuffer content = new StringBuffer();
        content.append("商机提醒：").append(business.getCompanyName());
        content.append("的通信需求：").append(StrDemand);
        content.append("，电话：").append(business.getTelephone());
        content.append("，地址：").append(business.getAddress());
        content.append("，走访人：").append(manager.getManagerName());
        content.append("/").append(manager.getManagerTel());
        return sendSmsThread(area.getPersonTel(), content.toString());
    }


    /**
     * 寄修订单工程师确认发货后给客户发送确认信息
     */
    public static boolean finishSendSmsToCustomer(String mobile) {
        StringBuffer content = new StringBuffer();
        content.append("您的手机已完成维修，现已发货，请注意查收！");
        return sendSmsThread(mobile, content.toString());
    }


    /**
     * 给超过30分钟还未预约的维修订单对应门店商发送提醒消息
     *
     * @param eng
     * @param o
     * @CreateDate: 2016-9-15 下午11:35:01
     */
    public static boolean sendAgreedSmsToShop(Shop s, Engineer eng, Order o) {
        StringBuffer content = new StringBuffer();
        content.append("工单提醒，维修订单").append(o.getOrderNo());
        content.append("已超过30分钟未处理，请提醒工程师及时提交预约信息");
        content.append("，接单工程师：").append(eng.getName());
        content.append("，联系电话：").append(eng.getMobile());
        return sendSmsThread(s.getTel(), content.toString());
    }

    /**
     * 给超过30分钟还未预约的维修订单对应工程师发送提醒消息
     *
     * @param eng
     * @param o
     * @CreateDate: 2016-9-15 下午11:35:01
     */
    public static boolean sendAgreedSmsToEngineer(Engineer eng, Order o) {
        StringBuffer content = new StringBuffer();
        content.append("工单提醒，维修订单").append(o.getOrderNo());
        content.append("已超过30分钟未处理，请及时提交预约信息");
        return sendSmsThread(eng.getMobile(), content.toString());
    }

    /**
     * 给超过两个小时还未预约的维修订单给客服发送提醒消息
     *
     * @param eng
     * @param o
     * @CreateDate: 2016-9-15 下午11:35:01
     */
    public static boolean sendAgreedSmsToCustomerService(String mobile, Shop s, Engineer eng, Order o) {
        StringBuffer content = new StringBuffer();
        content.append("工单提醒，维修订单").append(o.getOrderNo());
        content.append("，已超过两小时未处理，请提醒工程师及时提交预约信息");
        content.append("，接单工程师：").append(eng.getName());
        content.append("，联系电话：").append(eng.getMobile());
        content.append("，所属门店：").append(s.getName());
        content.append("，门店电话：").append(s.getTel());
        return sendSmsThread(mobile, content.toString());
    }


    /**
     * 给超过30分钟还未预约的换新订单对应门店商发送提醒消息
     *
     * @param eng
     * @param o
     * @CreateDate: 2016-9-15 下午11:35:01
     */
    public static boolean sendOldToNewSmsToShop(Shop s, Engineer eng, NewOrder o) {
        StringBuffer content = new StringBuffer();
        content.append("工单提醒，以旧换新订单").append(o.getOrderNo());
        content.append("已超过30分钟未处理，请提醒工程师及时提交预约信息");
        content.append("，接单工程师：").append(eng.getName());
        content.append("，联系电话：").append(eng.getMobile());
        return sendSmsThread(s.getTel(), content.toString());
    }

    /**
     * 给超过30分钟还未预约的换新订单对应工程师发送提醒消息
     *
     * @param eng
     * @param o
     * @CreateDate: 2016-9-15 下午11:35:01
     */
    public static boolean sendOldToNewSmsToEngineer(Engineer eng, NewOrder o) {
        StringBuffer content = new StringBuffer();
        content.append("工单提醒，以旧换新订单").append(o.getOrderNo());
        content.append("已超过30分钟未处理，请及时提交预约信息");
        return sendSmsThread(eng.getMobile(), content.toString());
    }

    /**
     * 给超过两个小时还未预约的换新订单给客服发送提醒消息
     *
     * @param eng
     * @param o
     * @CreateDate: 2016-9-15 下午11:35:01
     */
    public static boolean sendOldToNewSmsToCustomerService(String mobile, Shop s, Engineer eng, NewOrder o) {
        StringBuffer content = new StringBuffer();
        content.append("工单提醒，以旧换新订单").append(o.getOrderNo());
        content.append("，已超过两小时未处理，请提醒及时填写预约信息");
        content.append("，接单工程师：").append(eng.getName());
        content.append("，联系电话：").append(eng.getMobile());
        content.append("，所属门店：").append(s.getName());
        content.append("，门店电话：").append(s.getTel());
        return sendSmsThread(mobile, content.toString());
    }


    /**
     * 给工程师发送派单提示短信(以旧换新)
     *
     * @param eng
     * @param o
     * @CreateDate: 2016-9-15 下午11:35:01
     */
    public static boolean sendSmsToEngineerWithoutShop(Engineer eng, NewOrder o, OldToNewUser old) {
        StringBuffer content = new StringBuffer();
        content.append("工单提醒，订单").append(o.getOrderNo());
        content.append("已派单，请及时上门维修，联系电话：").append(old.getTel());
        content.append("，联系人：").append(old.getName());
        content.append("，维修地址：").append(old.getHomeAddress());
        return sendSmsThread(eng.getMobile(), content.toString());
    }

    /**
     * 给维修门店管理员发送短信提示
     *
     * @param s
     * @param o
     * @CreateDate: 2016-9-15 下午11:34:45
     */
    public static boolean sendSmsToShop(Shop s, Order o) {
        StringBuffer content = new StringBuffer();
        content.append("派单提醒，订单").append(o.getOrderNo());
        content.append("已接收，请及时派单处理，派单过期时间15分钟");
        if (StringUtils.isNotBlank(s.getManagerMobile1())) {
            sendSmsThread(s.getManagerMobile1(), content.toString());
        }
        if (StringUtils.isNotBlank(s.getManagerMobile2())) {
            sendSmsThread(s.getManagerMobile2(), content.toString());
        }
        return sendSmsThread(s.getManagerMobile(), content.toString());
    }

    /**
     * 给维修门店管理员发送短信提示(以旧换新)
     *
     * @param s
     * @param o
     * @CreateDate: 2016-9-15 下午11:34:45
     */
    public static boolean sendSmsToShop(Shop s, NewOrder o) {
        StringBuffer content = new StringBuffer();
        content.append("派单提醒，订单").append(o.getOrderNo());
        content.append("已接收，请及时派单处理，派单过期时间15分钟");
        if (StringUtils.isNotBlank(s.getManagerMobile1())) {
            sendSmsThread(s.getManagerMobile1(), content.toString());
        }
        if (StringUtils.isNotBlank(s.getManagerMobile2())) {
            sendSmsThread(s.getManagerMobile2(), content.toString());
        }
        return sendSmsThread(s.getManagerMobile(), content.toString());
    }

    /**
     * 给维客户发送订单取消短信提示
     *
     * @param mobile
     * @CreateDate: 2016-9-15 下午11:34:45
     */
    public static boolean sendSmsToCustomerForCancel(String mobile, String orderNo) {
        StringBuffer content = new StringBuffer();
        content.append("订单取消通知，客户您好，您的订单：").append(orderNo);
        content.append(" 已被取消，如有疑问请联系客服");
        return sendSmsThread(mobile, content.toString());
    }

    /**
     * 给维修工程师发送订单取消短信提示
     *
     * @param mobile
     * @CreateDate: 2016-9-15 下午11:34:45
     */
    public static boolean sendSmsToEngineerForCancel(String mobile, String orderNo) {
        StringBuffer content = new StringBuffer();
        content.append("订单取消通知，订单:").append(orderNo);
        content.append(" 已被取消，请及时处理下一个订单。");
        return sendSmsThread(mobile, content.toString());
    }

    /**
     * 领用优惠券发送短信
     *
     * @CreateDate: 2016-9-15 下午11:35:01
     */
    public static boolean sendSmsForCoupon(Coupon c, List<CouponModel> couModels, List<CouponProject> couProjects) {
        StringBuffer contentShop = new StringBuffer();
        contentShop.append("领用优惠券成功，优惠码：").append(c.getCouponCode());
        contentShop.append("，优惠券金额：").append(c.getCouponPrice());
        contentShop.append("（元），有效时间：").append(c.getEndTime());
        if (couModels != null && couModels.size() > 0) {
            contentShop.append("，支持品牌：");
            for (int i = 0; i < couModels.size(); i++) {
                if (i > 0) {
                    contentShop.append("、");
                }
                contentShop.append(couModels.get(i).getBrandName());
            }
        }
        if (couProjects != null && couProjects.size() > 0) {
            contentShop.append("，支持故障：");
            for (int i = 0; i < couProjects.size(); i++) {
                if (i > 0) {
                    contentShop.append("、");
                }
                contentShop.append(couProjects.get(i).getProjectName());
            }
        }
        contentShop.append("。http://t.cn/E2y7OEC");
        return sendSmsThread(c.getReceiveMobile(), contentShop.toString());
    }

    /**
     * 快修完成订单回访优惠券发送短信
     *
     * @CreateDate: 2016-9-15 下午11:35:01
     */
    public static boolean sendSmsForkxCoupon(Coupon c, List<CouponProject> couProjects) {
        StringBuffer contentShop = new StringBuffer();
        contentShop.append("领用优惠券成功，优惠码：").append(c.getCouponCode());
        contentShop.append("，优惠券金额：").append(c.getCouponPrice());
        contentShop.append("（元），有效时间：").append(c.getEndTime());
        contentShop.append("，支持品牌：通用");
        if (couProjects != null && couProjects.size() > 0) {
            contentShop.append("，支持故障：");
            for (int i = 0; i < couProjects.size(); i++) {
                if (i > 0) {
                    contentShop.append("、");
                }
                contentShop.append(couProjects.get(i).getProjectName());
            }
        } else {
            contentShop.append("，支持故障：通用");
        }
        contentShop.append("。http://t.cn/E2y7OEC");
        return sendSmsThread(c.getReceiveMobile(), contentShop.toString());
    }

    /**
     * 微信用户抽中维修优惠劵 发送短信提醒
     *
     * @param mobile
     * @return
     */
    public static boolean prizeSendToUser(String mobile, String couponCode) {
        StringBuffer content = new StringBuffer();
        content.append("恭喜您抽到了M-超人手机维修劵，您的优惠码为").append(couponCode);
        content.append("使用方法是在打开https://m-super.com/wechat/index.html?fm=17 后在订单中填写优惠码。");
        // System.out.println(content);
        return sendSmsThread(mobile, content.toString());
    }


    /**
     * 商机用户注册发送短信
     *
     * @param user
     * @return
     */
    public static boolean sjRegisterUserSend(SjUser user, String pwd) {
        StringBuffer content = new StringBuffer();
        content.append("商机后台注册:用户：").append(user.getName());
        content.append("账号为").append(user.getLoginId());
        content.append("密码为").append(pwd);
        return sendSmsThread(user.getPhone(), content.toString());
    }

    public static boolean sjApprovalSend(SjVirtualTeam virtualTeam, String ordelNo) {
        StringBuffer content = new StringBuffer();
        content.append("商机后台审核:订单：").append(ordelNo);
        content.append("审核已通过");
        return sendSmsThread(virtualTeam.getPhone(), content.toString());
    }

    public static boolean submitRecycleOrder(String mobile, String source,RecycleSystemService recycleSystemService) {
        StringBuffer content = new StringBuffer();
        content.append("订单提交成功，价格有效期10天，等待顺丰快递上门取件，" +
                "收货地址 浙江省杭州市下城区武林广场电信营业厅三楼售后维修中心0571-87162535  ，" +
                "寄出前请解除机器所有账号和密码（flyme，iCloud）等并取出手机卡和内存卡。");
        return sendSmsThread(mobile, content.toString(), source,recycleSystemService);
    }


    /**
     * 商机指派订单发送短信
     *
     * @param phone
     * @param orderNo
     * @return
     */
    public static boolean sjAssignOrderSend(String phone, String orderNo, Integer type) {
        StringBuffer content = new StringBuffer();
        content.append("商机后台订单指派:订单号：").append(orderNo);
        if (type == 1) {
            content.append("该订单已指派给您企业下员工");
        } else {
            content.append("该订单已指派给您");
        }
        return sendSmsThread(phone, content.toString());
    }

    public static boolean groupMobileSendCoupon(String contentText,String mobile,RecycleSystemService recycleSystemService) {
        StringBuffer content = new StringBuffer();
        content.append(contentText);
        return sendSmsThread(mobile, content.toString(),"2",recycleSystemService);
    }

    /**
     * 赠送爱奇艺短信
     * @param mobile
     * @param recycleSystemService
     * @return
     */
    public static boolean VideoCardExecutorSendMobile(String mobile,RecycleSystemService recycleSystemService) {
        StringBuffer content = new StringBuffer();
        content.append("感谢您参与手机回收活动，赠送的爱奇艺兑换码已发至您的天翼回收-个人中心-我的其他券中，");
        content.append("请戳dwz.cn/iOQlLZb2前往查看");
        return sendSmsThread(mobile, content.toString(),"2",recycleSystemService);
    }

    private static DateFormat formater = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    private static String URL = SystemUtil.getSysCfgProperty("smsUtil.url");
    private static String SIID = SystemUtil.getSysCfgProperty("smsUtil.siid");
    private static String USER = SystemUtil.getSysCfgProperty("smsUtil.user");
    private static String SPSECRET = SystemUtil.getSysCfgProperty("smsUtil.spSecret");
    private static String USER2 = SystemUtil.getSysCfgProperty("smsUtil.user2");
    private static String SPSECRET2 = SystemUtil.getSysCfgProperty("smsUtil.spSecret2");

    /**
     * 发送短信
     *
     * @param mobile  手机号码
     * @param content 短信内容
     * @CreateDate: 2016-9-26 下午8:10:21
     */
    public static Boolean sendSmsThread(String mobile, String content, String source,RecycleSystemService recycleSystemService) {
        SmsSendThread sst = new SmsSendThread(mobile, content, source,recycleSystemService);
        new Thread(sst).start();
        System.out.println("started ...");
        return true;
    }

    public static Boolean sendSmsThread(String mobile, String content) {
        SmsSendThread sst = new SmsSendThread(mobile, content, null,null);
        new Thread(sst).start();
        System.out.println("started ...");
        return true;
    }

    /**
     * 发送短信
     *
     * @param mobile  手机号码
     * @param content 短信内容
     * @CreateDate: 2016-9-26 下午8:10:21
     */
    public static Boolean sendSms(String mobile, String content) {

        long currenttime = System.currentTimeMillis();
        //获取当前时间戳
        String timeStamp = formater.format(currenttime);
        //事务号
        String transactionID = timeStamp;
        //流水号，标识操作唯一性，只能使用一次
        String streamingNo = SIID + transactionID;
        //认证码，参见产品接入规范
        String authenticator = MD5Util.md5Encode(timeStamp + transactionID + streamingNo + SPSECRET);
        authenticator = Base64Util.getBase64(authenticator.toUpperCase());
        authenticator = encoderByMd5(timeStamp + transactionID + streamingNo + SPSECRET);
        //拼接
        StringBuilder sendData = new StringBuilder();
        sendData.append("{\"streamingNo\":").append("\"" + streamingNo + "\",")
                .append("\"timeStamp\":").append("\"" + timeStamp + "\",")
                .append("\"transactionID\":").append("\"" + transactionID + "\",")
                .append("\"authenticator\":").append("\"" + authenticator + "\",")
                .append("\"siid\":").append("\"" + SIID + "\",")
                .append("\"user\":").append("\"" + USER + "\",")
                .append("\"mobile\":").append("\"" + mobile + "\",")
                .append("\"content\":").append("\"" + content + "\"").append("}");

        String response = HttpClientUtil.sendPostContent(URL, sendData.toString());
        if (response == null) {
            throw new SystemException("调用短信接口失败");
        }
        //解析返回数据
        JSONObject json = JSONObject.parseObject(response);
        if ("0000".equals(json.getString("retCode"))) {
            return true;
        } else {
            throw new SystemException(json.getString("msg"));
        }
    }

    /**
     * 发送短信  天翼回收
     *
     * @param mobile  手机号码
     * @param content 短信内容
     * @CreateDate: 2016-9-26 下午8:10:21
     */
    public static Boolean sendSms2(String mobile, String content) {

        long currenttime = System.currentTimeMillis();
        //获取当前时间戳
        String timeStamp = formater.format(currenttime);
        //事务号
        String transactionID = timeStamp;
        //流水号，标识操作唯一性，只能使用一次
        String streamingNo = SIID + transactionID;
        //认证码，参见产品接入规范
        String authenticator = MD5Util.md5Encode(timeStamp + transactionID + streamingNo + SPSECRET2);
        authenticator = Base64Util.getBase64(authenticator.toUpperCase());
        authenticator = encoderByMd5(timeStamp + transactionID + streamingNo + SPSECRET2);
        //拼接
        StringBuilder sendData = new StringBuilder();
        sendData.append("{\"streamingNo\":").append("\"" + streamingNo + "\",")
                .append("\"timeStamp\":").append("\"" + timeStamp + "\",")
                .append("\"transactionID\":").append("\"" + transactionID + "\",")
                .append("\"authenticator\":").append("\"" + authenticator + "\",")
                .append("\"siid\":").append("\"" + SIID + "\",")
                .append("\"user\":").append("\"" + USER2 + "\",")
                .append("\"mobile\":").append("\"" + mobile + "\",")
                .append("\"content\":").append("\"" + content + "\"").append("}");

        String response = HttpClientUtil.sendPostContent(URL, sendData.toString());
        if (response == null) {
            throw new SystemException("调用短信接口失败");
        }
        //解析返回数据
        JSONObject json = JSONObject.parseObject(response);
        if ("0000".equals(json.getString("retCode"))) {
            return true;
        } else {
            throw new SystemException(json.getString("msg"));
        }
    }

    private static String encoderByMd5(String str) {
        try {
            // 确定计算方法
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            BASE64Encoder base64en = new BASE64Encoder();
            // 加密后的字符串
            return base64en.encode(md5.digest(str.getBytes("UTF-8")));
        } catch (Exception e) {
            return "";
        }
    }


    /**
     * @param args
     * @CreateDate: 2016-9-13 下午7:50:20
     */
    public static void main(String[] args) {
        //sendSmsBySp("15006513637", "test");
        //sendSms("18106538281", "test");
        prizeSendToUser("15006513637", "123212");
    }

}

class SmsSendThread implements Runnable {

    private RecycleSystemService recycleSystemService;
    private String mobile;
    private String content;
    private String source;

    public SmsSendThread(String mobile, String content, String source,RecycleSystemService recycleSystemService) {
        this.mobile = mobile;
        this.content = content;
        this.source = source;
        this.recycleSystemService=recycleSystemService;
    }

    @Override
    public void run() {
        if(recycleSystemService!=null){
            List<String> sources=recycleSystemService.getDao().queryIdBySmsType(2);
//        List<String> sources= Arrays.asList("2","4","9","10","15");
            if (sources.contains(source) && StringUtils.isNotBlank(source)) {
                SmsSendUtil.sendSms2(mobile, content);
            } else {
                SmsSendUtil.sendSms(mobile, content);
            }
        }else{
            SmsSendUtil.sendSms(mobile, content);
        }
    }
}
