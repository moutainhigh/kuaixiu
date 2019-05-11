package com.common.util;

/**
 * 常量类
 * @author yq
 */
public interface Consts {
    /**
     * PC端，Cookie存用户名密码，所用参数名 (ps:为最大限度保证不被破译，参数为毫无意义字符串)
     */
    public static final String COOKIE_LOGIN_USER_INFO = "__l";
    public static final String COOKIE_USER_INFO_SIGN = "#gyxdba#";//用户名密码分隔标识

    public static final int COOKIE_LOGIN_USER_EXPIRES_IN = 604800;//用户信息Cookie有效期,604800=一周
    public static final String AUTO_LOGIN_CHECKED = "1";//自动登录被勾选

    //H5回收列表查询手机号免登录，cookie 的key
    public static final String COOKIE_H5_PHONE = "cookie_h5_phone";
    public static final String COOKIE_NEW_H5_PHONE = "cookie_new_h5_phone";

    //商机登录手机号  key
    public static final String COOKIE_SJ_PHONE = "__sj";
}
