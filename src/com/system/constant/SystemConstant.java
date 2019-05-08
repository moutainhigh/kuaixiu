package com.system.constant;

import com.system.util.SystemUtil;

import java.math.BigDecimal;

/**
 * 系统常量.
 * 
 * @CreateDate: 2016-8-23 下午8:17:00
 * @version: V 1.0
 */
public class SystemConstant {
    /**
     * 用户sj后台登录session
     */
    public static final String SESSION_SJ_USER_KEY = "session_sj_user_key_";

    /**
     * 用户后台登录session
     */
    public static final String SESSION_USER_KEY = "session_user_key_";
    /**
     * 用户验证码
     */
    public static final String SESSION_RANDOM_CODE = "session_random_code_";
    /**
     * 微信登录用户openId
     */
    public static final String SESSION_OPENID = "wechat_openId";
    /**
     * 项目名称
     */
    public static String WEB_CONTEXT_PATH = "";
    
    /**
     * 项目真实路径名称
     */
    public static String WEB_REAL_PATH = "";

    /**
     * 超级管理员
     */
    public static final int USER_TYPE_SUPPER = 1;
    /**
     * 平台管理员
     */
    public static final int USER_TYPE_SYSTEM = 2;
    /**
     * 供应商用户
     */
    public static final int USER_TYPE_PROVIDER = 3;
    /**
     * 网点管理员
     */
    public static final int USER_TYPE_SHOP = 4;
    /**
     * 维修工程师
     */
    public static final int USER_TYPE_ENGINEER = 5;
    /**
     * 用户
     */
    public static final int USER_TYPE_CUSTOMER = 6;

    /**
     * 客服
     */
    public static final int USER_TYPE_CUSTOMER_SERVICE = 7;
    /** 
     * 特殊用户  只能查看碎屏险订单
     */
    public static final int USER_TYPE_SCREEN = 8;

    /**
     * 电渠人员
     */
    public static final int TELECOM_USER=9;

    /**
     * 站点号卡人员
     */
    public static final int STATION_USER=10;
    /**
     * 店员
     */
    public static final int USER_TYPE_CLERK=100;
    /**
     * 超级管理员
     */
    public static final String USER_ROLE_SUPPER = "BASE_ADMIN";
    /**
     * 平台管理员
     */
    public static final String USER_ROLE_SYSTEM = "SYSTEM_ADMIN";
    /**
     * 供应商用户
     */
    public static final String USER_ROLE_PROVIDER = "PROVIDER_ADMIN";
    /**
     * 网点管理员
     */
    public static final String USER_ROLE_SHOP = "SHOP_ADMIN";
    /**
     * 维修工程师
     */
    public static final String USER_ROLE_ENGINEER = "ENGINEER";
    /**
     * 用户
     */
    public static final String USER_ROLE_CUSTOMER = "CUSTOMER";
    /**
     * 用户
     */
    public static final String USER_ROLE_CUSTOMER_SERVICE = "CUSTOMER_SERVICE";
    /**
     * 碎屏险商登录用户
     */
    public static final String USER_ROLE_SPECIAL_CUSTOMER = "SPECIAL_CUSTOMER";
    /**
     * 默认每页显示条数
     */
    public static final int DEFAULT_PAGE_SIZE = 10;
    
    /**
     * 百度秘钥配置文件key
     */
    public static final String BAIDU_MAP_API_AK_KEY = "baidu.map.api.ak";
    /**
     * 高德秘钥配置文件key
     */
    public static final String GAODE_MAP_API_AK_KEY = "gaode.map.api.ak";
    /**
     * token有效时间配置文件key
     */
    public static final String ACCESS_TOKEN_EXPIRES_KEY = "access_token.expires_in";
    /**
     * 用户端登录 token有效时间  单位秒
     */
    public static final String ACCESS_TOKEN_INDATE = "access_token.indate";
    
    /**
     * token混淆码配置文件key
     */
    public static final String ACCESS_TOKEN_SALT_KEY = "access_token.salt";
    
    /**
     * 公众号 APP_ID
     */
    public static final String APP_ID = SystemUtil.getSysCfgProperty("wechat.api.appId");
    /**
     * 公众号 APP_SECRET
     */
    public static final String APP_SECRET = SystemUtil.getSysCfgProperty("wechat.api.appSecret");
    /**
     * 公众号 TOKEN
     */
    public static final String TOKEN = SystemUtil.getSysCfgProperty("wechat.api.token");
    /**
     * 公众号 AES_KEY
     */
    public static final String AES_KEY = SystemUtil.getSysCfgProperty("wechat.api.aesKey");

    /**
     * 微信支付商户号
     */
    public static final String MCH_ID = SystemUtil.getSysCfgProperty("wechat.api.mchId");
    /**
     * 微信支付平台商户API密钥(https://pay.weixin.qq.com/index.php/core/account/api_cert)
     */
    public static final String MCH_KEY = SystemUtil.getSysCfgProperty("wechat.api.mchKey");
    
    /**
     * 接收微信支付授权域名
     */
    public static final String WECHAT_PAY_DOMAIN = SystemUtil.getSysCfgProperty("wechat.pay.domain");
    
    /**
     * 派单范围 km
     */
    public static final String ADDRESS_RANGE = SystemUtil.getSysCfgProperty("address_range");
   /**
    * 以旧换新派单范围
    */
    public static final String OLDTONEW_RANGE = SystemUtil.getSysCfgProperty("oldToNew_range"); 
    /**
     * 顺丰平台appid
     */
    public static final String SF_APP_ID = SystemUtil.getSysCfgProperty("sf.api.appid");
    /**
     * 顺丰平台appkey
     */
    public static final String SF_APP_KEY = SystemUtil.getSysCfgProperty("sf.api.appkey");
    /**
     * 顺丰平台沙盒测试环境域名
     */
    public static final String SF_TEST_DOMAIN = SystemUtil.getSysCfgProperty("sf.api.testDomain"); 
    /**
     * 顺丰平台生产环境域名
     */
    public static final String SF_DOMAIN = SystemUtil.getSysCfgProperty("sf.api.domain"); 
    /**
     * 碎屏险调用接口用户名
     */
    public static final String SCREEN_USERNAME = SystemUtil.getSysCfgProperty("screen.username"); 
    /**
     * 碎屏险调用接口密码    
     */
    public static final String SCREEN_PASSWORD = SystemUtil.getSysCfgProperty("screen.password"); 
    /**
     * 碎屏险调用接口地址
     */
    public static final String SCREEN_URL = SystemUtil.getSysCfgProperty("screen.url"); 
    /**
     * 碎屏险请求头对接值
     */
    public static final String SCREEN_HEADER = SystemUtil.getSysCfgProperty("screen.header"); 
    /**
     * 碎屏险接受请求用户名信息
     */
    public static final String SCREEN_USERNAME_RECEIVE = "hzynkj"; 
    /**
     * 碎屏险接受请求用户密码信息
     */
    public static final String SCREEN_PASSWORD_RECEIVE = "yn1234"; 
    /**
     * 碎屏险支持0元提交订单的产品编号
     */
    public static final String SCREEN_CODE = SystemUtil.getSysCfgProperty("screen.code"); 
    
    /**
     * 芝麻开放平台地址 
     */
    public static final String ZHIMA_OPEN_URL = "https://zmopenapi.zmxy.com.cn/openapi.do"; 
    /**
     * 芝麻商户appid
     */
    public static final String ZHIMA_APPID = SystemUtil.getSysCfgProperty("zhima_appid"); 
    /**
     * 支付宝RSA私钥
     */
    public static final String ZHIFUBAO_PRIVATE_RSA = SystemUtil.getSysCfgProperty("zhifubao_private_rsa"); 
    /**
     * 支付宝RSA公钥
     */
    public static final String ZHIFUBAO_PUBLIC_RSA = SystemUtil.getSysCfgProperty("zhifubao_public_rsa"); 
    /**
     * 芝麻信用RSA公钥
     */
    public static final String ZHIMA_PUBLIC_RSA = SystemUtil.getSysCfgProperty("zhima_public_rsa"); 
    /**
     * 芝麻信用获取订单号的回调地址
     */
    public static final String ZHIMA_CALLBACKURL = SystemUtil.getSysCfgProperty("zhima_callBackUrl"); 
    /**
     * 调用回收接口AES 16位加密密钥
     */
    public static final String RECYCLE_CODE = SystemUtil.getSysCfgProperty("recycle_code"); 
    /**
     * 偏移量  除ecb模式  其他模式都需要其进行加解密
     */
    public static final String RECYCLE_VECTOR=SystemUtil.getSysCfgProperty("recycle_vector");     
    /**
     * 调用回收接口必须基础参数名称
     */
    public static final String RECYCLE_ID = SystemUtil.getSysCfgProperty("recycle_id");
    /**
     * 调用回收接口必须基础参数值
     */
    public static final String RECYCLE_ID_VALUE= SystemUtil.getSysCfgProperty("recycle_id_value");
    /**
     * 调用回收接口所需请求参数名
     */
    public static final String RECYCLE_REQUEST= SystemUtil.getSysCfgProperty("recycle_request");

    /**
     * 调用每单金额
     */
    public static final String RECYCLE_BASEPRICE = SystemUtil.getSysCfgProperty("recycle_base_price");
    /**
     * 多少单一次计费
     */
    public static final String RECYCLE_BASENUMBER = SystemUtil.getSysCfgProperty("recycle_base_number");
    /**
     * 默认回收机型图片地址
     */
    public static final String DEFAULTIMAGE="resource/brandLogo/default.png";
    /**
     * 默认图片上传保存位置  
     */
    public static final String IMAGE_PATH=SystemUtil.getSysCfgProperty("image_path");
    /**
     * 微信中央服务器获取token的基础url
     */
    public static final String WECHAT_TOKENURL = SystemUtil.getSysCfgProperty("wechat.tokenUrl"); 
    /**
     * 微信中央服务器通过用户openid获取用户信息的基础url
     */
    public static final String WECHAT_OPENIDUSERINFOURL = SystemUtil.getSysCfgProperty("wechat.openidUserinfoUrl"); 
    /**
     * 微信请求消息类型：文本 text
     */
    public static final String REQ_MESSAGE_TYPE_TEXT = "text";
    /**
     * 微信请求消息类型：推送 事件 event
     */
    public static final String REQ_MESSAGE_TYPE_EVENT = "event";
    /**
     * 微信事件类型：subscribe(订阅)
     */
    public static final String EVENT_TYPE_SUBSCRIBE = "subscribe";
    /**
     * 微信事件类型：unsubscribe(取消订阅)
     */
    public static final String EVENT_TYPE_UNSUBSCRIBE = "unsubscribe";
    
    
    /**
     * 微信关注发送的维修通用优惠卷批次号 
     */
    public static final String WECHAT_COMMON_BATCHID =SystemUtil.getSysCfgProperty("wechat.common.batchId"); 
    /**
     * 微信关注发送的维修通用优惠卷名称
     */
    public static final String WECHAT_COMMON_NAME ="通用优惠券"; 
    /**
     * 微信关注发送的维修通用优惠卷价格
     */
    public static final String WECHAT_COMMON_PRICE =SystemUtil.getSysCfgProperty("wechat.common.price");
    /**
     * 回访的维修通用优惠卷批次号
     */
    public static final String RECORD_COMMON_BATCHID =SystemUtil.getSysCfgProperty("record.common.batchId");
    /**
     * 回访的20屏幕维修通用优惠卷批次号
     */
    public static final String RECORD_SCREEN30_BATCHID =SystemUtil.getSysCfgProperty("record.screen30.batchId");
    /**
     * 回访的50屏幕维修通用优惠卷批次号
     */
    public static final String RECORD_SCREEN50_BATCHID =SystemUtil.getSysCfgProperty("record.screen50.batchId");
    /**
     * 回访的维修通用优惠卷价格
     */
    public static final String RECORD_COMMON_PRICE =SystemUtil.getSysCfgProperty("record.common.price");
    /**
     * 回访的50屏幕维修通用优惠卷价格
     */
    public static final String RECORD_SCREEN30_PRICE =SystemUtil.getSysCfgProperty("record.screen30.price");

    /**
     * 回访的50屏幕维修通用优惠卷价格
     */
    public static final String RECORD_SCREEN50_PRICE =SystemUtil.getSysCfgProperty("record.screen50.price");


    /**
     * 微信关注发送的贴膜优惠卷批次号 
     */
    public static final String WECHAT_SCREEN_BATCHID =SystemUtil.getSysCfgProperty("wechat.screen.batchId"); 
    /**
     * 微信关注发送的贴膜优惠卷名称
     */
    public static final String WECHAT_SCREEN_NAME ="贴膜优惠券"; 
    /**
     * 微信关注发送的贴膜优惠卷价格
     */
    public static final String WECHAT_SCREEN_PRICE =SystemUtil.getSysCfgProperty("wechat.screen.price");
    
    
    /**
     * 支付宝商户号appid
     */
    public static final String ZHIFUMCH_APPID=SystemUtil.getSysCfgProperty("zhifumch_appid"); 
    /**
     * 支付宝商户号公钥
     */
    public static final String ZHIFUMCH_PUBLIC_RSA=SystemUtil.getSysCfgProperty("zhifumch_public_rsa"); 
    /**
     * 支付宝商户号私钥
     */
    public static final String ZHIFUMCH_PRIVATE_RSA=SystemUtil.getSysCfgProperty("zhifumch_private_rsa"); 
    /**
     * 支付宝支付回调地址
     */
    public static final String ZHIFUMCH_CALLBACHURL=SystemUtil.getSysCfgProperty("zhifumch_callBackUrl"); 
    /**
     * 允许跨域请求的域名
     */
    public static final String ALLOW_DOMAIN=SystemUtil.getSysCfgProperty("allow.domain");

    /**
     * 翼回收微信小程序appid
     */
    public static final String WECHAT_APPLET_APPID =SystemUtil.getSysCfgProperty("wechat.applet.appid");
    /**
     * 翼回收微信小程序secret
     */
    public static final String WECHAT_APPLET_SECRET=SystemUtil.getSysCfgProperty("wechat.applet.secret");
    /**
     * 企业活动微信小程序appid
     */
    public static final String WECHAT_ACTIVITY_APPID =SystemUtil.getSysCfgProperty("wechat.activity.appid");
    /**
     * 企业活动微信小程序secret
     */
    public static final String WECHAT_ACTIVITY_SECRET=SystemUtil.getSysCfgProperty("wechat.activity.secret");
    /**
     * 超人回收微信小程序appid
     */
    public static final String WECHAT_APPLET_POSTMAN_APPID =SystemUtil.getSysCfgProperty("wechat.applet.postMan.appid");
    /**
     * 超人回收微信小程序secret
     */
    public static final String WECHAT_APPLET_POSTMAN_SECRET=SystemUtil.getSysCfgProperty("wechat.applet.postMan.secret");


    /**
     * 抽奖区间划分的份数section_copies
     */
    public static final String SECTION_COPIES=SystemUtil.getSysCfgProperty("section_copies");
    /**
     * 最多可设置到的奖品等级
     */
    public static final String MAX_PRIZE=SystemUtil.getSysCfgProperty("max_prize");
    /**
     * 安卓机型下周降价比
     */
    public static final String ANDROID_NEXTPRICE=SystemUtil.getSysCfgProperty("android_nextPrice");
    /**
     * ios机型下周降价比
     */
    public static final String IOS_NEXTPRICE=SystemUtil.getSysCfgProperty("ios_nextPrice");
    /**
     * 当前抽奖的奖品批次
     */
    public static final String NOW_PRIZE_BATCH=SystemUtil.getSysCfgProperty("now_prize_batch");
    /**
     * 当前抽奖的奖品批次
     */
    public static final String NOW_PRIZE_PRICE=SystemUtil.getSysCfgProperty("now_prize_price");


    //-------------------------------电渠参数-----------------------------------------
    /**
     * 请求url
     */
    public static final String TELECOM_URL=SystemUtil.getSysCfgProperty("telecom_url");
    /**
     * 请求方标示
     */
    public static final String TELECOM_ID=SystemUtil.getSysCfgProperty("srcsysid");
    /**
     * 加密数据字段
     */
    public static final String TELECOM_KEY=SystemUtil.getSysCfgProperty("telecom_key");

    /**
     *每次推送给电渠号卡的最大数
     */
    public static final String PUSH_MAXSUM=SystemUtil.getSysCfgProperty("push_maxsum");



    //-------------------------------转转参数-----------------------------------------

    /**
     * 转转请求实际地址
     */
    public static final String ZHUANG_URL=SystemUtil.getSysCfgProperty("zhuang_url");
    /**
     * 调用方ID
     */
    public static final String ZHUANG_PARTNEW_ID=SystemUtil.getSysCfgProperty("partnew_id");
    /**
     * 转转加解密key
     */
    public static final String ZHUANG_APP_KEY=SystemUtil.getSysCfgProperty("zhuang_app_key");


    /**
     * 欢GO单点登录websevice请求地址
     */
    public static final String SINGLE_LOGIN_URL=SystemUtil.getSysCfgProperty("single_login_url");

    /**
     * String精确转int
     * @param prize
     * @return
     */
    public static Integer getInt(String prize){
        BigDecimal b=(new BigDecimal(prize)).multiply(new BigDecimal(SECTION_COPIES));
        Integer i = b.intValue();
        return i;
    }

    public static final String REWORK_ENGINEER_NUMBER=SystemUtil.getSysCfgProperty("rework_order_engineer_number");

    public static final String ALIPAY_APP_ID=SystemUtil.getSysCfgProperty("alipay_app_id");
    public static final String ALIPAY_APP_URL=SystemUtil.getSysCfgProperty("alipay_url");
    public static final String ALIPAY_APP_PAIVATE_KEY=SystemUtil.getSysCfgProperty("alipay_private_key");
    public static final String ALIPAY_APP_PUBLIC_KEY=SystemUtil.getSysCfgProperty("alipay_public_key");
    /**
     * 调用回收接口的基础的地址如 http://114.215.210.238/super_webapi/
     * 回收新接口：http://116.62.233.149/super_webapi/
     */
    public static final String RECYCLE_URL = SystemUtil.getSysCfgProperty("recycle_url");
    public static final String RECYCLE_NEW_URL = SystemUtil.getSysCfgProperty("recycle_new_url");


    //以下是生产环境
    public static final String ALIPAY_NOTIFY_URL=SystemUtil.getSysCfgProperty("alipay_notify_url");
    /**
     * 生产接收微信支付通知回调地址
     */
    public static final String WECHAT_PAY_NOTIFY_URL = SystemUtil.getSysCfgProperty("wechat.pay.notify_url");


//    //以下是测试环境
//    public static final String ALIPAY_NOTIFY_URL=SystemUtil.getSysCfgProperty("alipay_ceshi_notify_url");
//    /**
//     * 测试环境接收微信支付通知回调地址
//     */
//    public static final String WECHAT_PAY_NOTIFY_URL = SystemUtil.getSysCfgProperty("wechat.ceshi.pay.notify_url");

}
