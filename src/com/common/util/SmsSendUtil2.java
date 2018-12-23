package com.common.util;

import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.commons.lang3.StringUtils;

import sun.misc.BASE64Encoder;

import com.alibaba.fastjson.JSONObject;
import com.common.exception.SystemException;
import com.kuaixiu.engineer.entity.Engineer;
import com.kuaixiu.order.entity.Order;
import com.kuaixiu.shop.entity.Shop;
import com.system.util.SystemUtil;

/**
 * 短信发送接口.
 * 
 * @CreateDate: 2016-9-13 下午7:50:26
 * @version: V 1.0
 */
public class SmsSendUtil2 {
    
    /**
     * 获取验证码
     * @return
     * @CreateDate: 2016-9-13 下午8:01:55
     */
    public static String randomCode(){
        StringBuffer sb = new StringBuffer();
        sb.append(Math.random()).append("52136832");
        String code = sb.substring(2, 8);
        System.out.println("本次操作获取的验证码为："+code);
        return code;
    }
    
    /**
     * 调用接口发送验证码
     * @param mobile
     * @param randomCode
     * @CreateDate: 2016-9-13 下午8:15:21
     */
    public static boolean sendCheckCode(String mobile, String randomCode){
        //获取验证码短信模板
        String content = (String)SystemUtil.getProperty("sms_checkCode");
        if(StringUtils.isBlank(content)){
            content = "【M-超人】验证码：${code}，有效期30分钟，请及时验证。（如非本人操作，请忽略）";
        }
        content = content.replace("${code}", randomCode);
        //System.out.println(content);
        return sendSms(mobile, content);
    }
    
    /**
     * 调用接口发送密码
     * @param mobile
     * @param randomCode
     * @CreateDate: 2016-9-13 下午8:15:21
     */
    public static boolean sendNewPasswd(String mobile, String newPasswd){
        //获取验证码短信模板
        String content = (String)SystemUtil.getProperty("sms_newPasswd");
        if(StringUtils.isBlank(content)){
            content = "【M-超人】新的登录密码为：${newPasswd}，登录后请及时修改密码。（如非本人操作，请忽略）";
        }
        content = content.replace("${newPasswd}", newPasswd);
        //System.out.println(content);
        return sendSms(mobile, content);
    }
    

    /**
     * 调用接口发送账号密码
     * @param mobile
     * @param randomCode
     * @CreateDate: 2016-9-13 下午8:15:21
     */
    public static boolean sendAccountAndPasswd(String mobile, String account, String passwd){
        //获取验证码短信模板
        String content = (String)SystemUtil.getProperty("sms_newPasswd");
        if(StringUtils.isBlank(content)){
            content = "【M-超人】您的登录账号为：${account}，密码：${passwd}，登录后请及时修改密码。（如非本人操作，请忽略）";
        }
        content = content.replace("${account}", account);
        content = content.replace("${passwd}", passwd);
        //System.out.println(content);
        return sendSmsThread(mobile, content);
    }
    
    /**
     * 给工程师发送派单提示短信
     * @param eng
     * @param o
     * @CreateDate: 2016-9-15 下午11:35:01
     */
    public static boolean sendSmsToEngineer(Engineer eng, Order o){
        StringBuffer content = new StringBuffer();
        content.append("【M-超人】工单提醒，订单").append(o.getOrderNo());
        content.append("已派单，请及时上门维修，联系电话：").append(o.getMobile());
        content.append(",联系人：").append(o.getCustomerName());
        content.append(",维修地址：").append(o.getFullAddress());
        return sendSmsThread(eng.getMobile(), content.toString());
    }

    /**
     * 给维修门店管理员发送短信提示
     * @param s
     * @param o
     * @CreateDate: 2016-9-15 下午11:34:45
     */
    public static boolean sendSmsToShop(Shop s, Order o){
        StringBuffer content = new StringBuffer();
        content.append("【M-超人】派单提醒，订单").append(o.getOrderNo());
        content.append("已接收，请及时派单处理，派单过期时间15分钟");
        return sendSmsThread(s.getManagerMobile(), content.toString());
    }
    

    /**
     * 给维客户发送订单取消短信提示
     * @param s
     * @param o
     * @CreateDate: 2016-9-15 下午11:34:45
     */
    public static boolean sendSmsToCustomerForCancel(String mobile, String orderNo){
        StringBuffer content = new StringBuffer();
        content.append("【M-超人】订单取消通知，客户您好，您的订单：").append(orderNo);
        content.append(" 已被取消，如有疑问请联系客服");
        return sendSmsThread(mobile, content.toString());
    }
    
    /**
     * 给维工程师发送订单取消短信提示
     * @param s
     * @param o
     * @CreateDate: 2016-9-15 下午11:34:45
     */
    public static boolean sendSmsToEngineerForCancel(String mobile, String orderNo){
        StringBuffer content = new StringBuffer();
        content.append("【M-超人】订单取消通知，订单:").append(orderNo);
        content.append(" 已被取消，请及时处理下一个订单。");
        return sendSmsThread(mobile, content.toString());
    }
    
    private static DateFormat formater = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    private static String URL = SystemUtil.getSysCfgProperty("smsUtil.url");
    private static String SIID = SystemUtil.getSysCfgProperty("smsUtil.siid");
    private static String SPSECRET = SystemUtil.getSysCfgProperty("smsUtil.spSecret");
    
    /**
     * 发送短信
     * @param mobile 手机号码
     * @param content 短信内容
     * @CreateDate: 2016-9-26 下午8:10:21
     */
    public static Boolean sendSmsThread(String mobile, String content){
        SmsSendThread sst = new SmsSendThread(mobile, content);
        new Thread(sst).start();
        System.out.println("started ...");
        return true;
    }
    
    /**
     * 发送短信
     * @param mobile 手机号码
     * @param content 短信内容
     * @CreateDate: 2016-9-26 下午8:10:21
     */
    public static Boolean sendSms(String mobile, String content){
        
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
        sendData.append("{\"StreamingNo\":").append("\""+streamingNo+"\",")
        .append("\"TimeStamp\":").append("\""+timeStamp+"\",")
        .append("\"TransactionID\":").append("\""+transactionID+"\",")
        .append("\"Authenticator\":").append("\""+authenticator+"\",")
        .append("\"SessionToken\":").append("\" \",")
        .append("\"SIID\":").append("\""+SIID+"\",")
        .append("\"ParamItems\":[{\"paramID\":").append("\"DestNumber\",")
        .append("\"paramName\":null,")
        .append("\"paramValue\":").append("\""+ mobile +"\"").append("},")
//        .append("{\"paramID\":").append("\"ExpandNumber\",")
//        .append("\"paramName\":null,")
//        .append("\"paramValue\":").append("\"01\"").append("},")
        .append("{\"paramID\":").append("\"Message\",")
        .append("\"paramName\":null,")
        .append("\"paramValue\":").append("\""+ content +"\"").append("}]}");
        
        String response = HttpClientUtil.sendPostContent(URL, sendData.toString());
        if(response == null){
            throw new SystemException("调用短信接口失败");
        }
        //解析返回数据
        JSONObject json = JSONObject.parseObject(response);
        if ("0000".equals(json.getString("retCode"))) {
            return true;
        }
        else {
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
        sendSmsThread("15006513637","123212");
    }

}

class SmsSendThread1 implements Runnable{
    private String mobile;
    private String content;
    public SmsSendThread1(String mobile, String content){
        this.mobile = mobile;
        this.content = content;
    }
    @Override
    public void run() {
        SmsSendUtil2.sendSms(mobile, content);
    }
    
}
