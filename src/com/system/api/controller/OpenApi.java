package com.system.api.controller;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;

import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.exception.ApiServiceException;
import com.system.api.entity.ApiConfig;
import com.system.api.entity.OauthToken;
import com.system.api.entity.ResultData;
import com.system.api.service.OpenApiService;
import com.system.constant.ApiResultConstant;
import com.system.util.SystemUtil;

/**
 * 开放接口.
 * 
 * @CreateDate: 2016-9-4 下午11:49:58
 * @version: V 1.0
 */
@Controller
public class OpenApi extends BaseController {
    private static final Logger log = Logger.getLogger(OpenApi.class);
    @Autowired
    private OpenApiService openApiService;
    
    /**
     * 获取access_token
     * @param request
     * @param response
     * @throws Exception
     * @CreateDate: 2016-9-4 下午11:53:15
     */
    @RequestMapping(value = "/open/oauth2/access_token")
    public void getAccessToken(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
    //	response.setHeader("Access-Control-Allow-Origin", "*");
        ResultData result = new ResultData();
        //表示授权类型，此处的值固定为"password"
        String grantType = request.getParameter("grant_type");
        //表示权限范围，可选项
        String scope = request.getParameter("scope");
        //获取用户请求的主机IP地址
        String ip=getIpAddress(request);
        
        try{
            //获取token
            if ("password".equals(grantType)) {
                //表示工程师账号(由系统管理员提供)
                String username = request.getParameter("username");
                //表示工程师密码(由系统管理员提供，调用时采用MD5加密传输)
                String password = request.getParameter("password");
                if(StringUtils.isNotBlank(username)
                        && StringUtils.isNotBlank(password)) {
                    //生成token
                    OauthToken token = openApiService.getAccessToken(grantType, username, password, scope, ip);
                    JSONObject json = new JSONObject();
                    json.put("access_token", token.getAccessToken());
                    json.put("expires_in", token.getExpiresIn());
                    json.put("refresh_token", token.getRefreshToken());
                    json.put("time", token.getTime());
                    result.setResult(json);
                    result.setResultCode("0");
                    result.setSuccess(true);
                }
                else {
                    result.setResultMessage(ApiResultConstant.resultCode_1001);
                    result.setResultCode(ApiResultConstant.resultCode_str_1001);
                    result.setSuccess(false);
                }
            }
            else if("refresh_token".equals(grantType)){
                //表示工程师账号(由系统管理员提供)
                String refreshToken = request.getParameter("refresh_token");
                if(StringUtils.isNotBlank(refreshToken)){
                    //刷新token
                    OauthToken token = openApiService.refreshToken(refreshToken, scope, ip);
                    JSONObject json = new JSONObject();
                    json.put("access_token", token.getAccessToken());
                    json.put("expires_in", token.getExpiresIn());
                    json.put("refresh_token", token.getRefreshToken());
                    json.put("time", token.getTime());
                    result.setResult(json);
                    result.setResultCode("0");
                    result.setSuccess(true);
                }
                else {
                    result.setResultMessage(ApiResultConstant.resultCode_1001);
                    result.setResultCode(ApiResultConstant.resultCode_str_1001);
                    result.setSuccess(false);
                }
                
            }
            else{
                result.setResultMessage(ApiResultConstant.resultCode_1003);
                result.setResultCode("grant_type 参数值不正确");
                result.setSuccess(false);
            }
        }
        catch(ApiServiceException e){
            e.printStackTrace();
            result.setResultMessage(e.getMessage());
            result.setResultCode(e.getResultCode());
            result.setSuccess(false);
        }
        catch (Exception e) {
            e.printStackTrace();
            result.setResultCode(ApiResultConstant.resultCode_5001);
            result.setResultMessage(ApiResultConstant.resultCode_str_5001);
            result.setSuccess(false);
        }
        renderJson(response, result);
    }
    
    /**
     * 忘记密码
     * @param request
     * @param response
     * @throws Exception
     * @CreateDate: 2016-9-4 下午11:53:15
     */
    @RequestMapping(value = "/open/oauth2/forgot_password")
    public void forgotPassword(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
  //  	response.setHeader("Access-Control-Allow-Origin", "*");
        ResultData result = new ResultData();
        //工程师工号
        String number = request.getParameter("number");
        //手机号码
        String mobile = request.getParameter("mobile");
        //身份证
        String idcard = request.getParameter("idcard");
        //获取用户请求的主机IP地址
        String ip=getIpAddress(request);
        
        try{
            if(StringUtils.isNotBlank(number)
                    && StringUtils.isNotBlank(mobile)
                    && StringUtils.isNotBlank(idcard)) {
                //生成token
                String msg = openApiService.forgotPassword(number, mobile, idcard);
                result.setResult("OK");
                result.setResultMessage("密码已重置，请注意查收短信");
                result.setResultCode("0");
                result.setSuccess(true);
            }
            else {
                result.setResultMessage(ApiResultConstant.resultCode_1001);
                result.setResultCode(ApiResultConstant.resultCode_str_1001);
                result.setSuccess(false);
            }
        }
        catch(ApiServiceException e){
            e.printStackTrace();
            result.setResultMessage(e.getMessage());
            result.setResultCode(e.getResultCode());
            result.setSuccess(false);
        }
        catch (Exception e) {
            e.printStackTrace();
            result.setResultCode(ApiResultConstant.resultCode_5001);
            result.setResultMessage(ApiResultConstant.resultCode_str_5001);
            result.setSuccess(false);
        }
        renderJson(response, result);
    }
    
    /**
     * 获取access_token
     * @param request
     * @param response
     * @throws Exception
     * @CreateDate: 2016-9-4 下午11:53:15
     */
    @RequestMapping(value = "/open/oauth2/api")
    public void api(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
   // 	response.setHeader("Access-Control-Allow-Origin", "*");
        ResultData result = new ResultData();	
        //  授权token
        String token = request.getParameter("token");
        //接口请求方法
        String method = request.getParameter("method");
        //获取用户请求的主机IP地址
        String ip=getIpAddress(request);
        try{
            if (StringUtils.isNotBlank(token)
                    && StringUtils.isNotBlank(method)) {
                //获取接口请求参数
                Map<String, String> params=getRequestParameterMapStr(request);
                //验证token
                openApiService.checkToken(token, params);
                params.put("request_ip",ip);//添加ip地址
                //获取接口对应的真实方法数据
                ApiConfig apiConfig = SystemUtil.getApiConfig(method);
                if(apiConfig != null){
                    //获取上下文
                    WebApplicationContext context = (WebApplicationContext) request
                            .getSession()
                            .getServletContext()
                            .getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
                    //获取service对象
                    Object value = context.getBean(apiConfig.getService());
                    //反射方法名
                    Method process = value.getClass().getMethod(apiConfig.getMethod(), new Class[]{Map.class});
                    //执行service方法
                    Object resultObj = process.invoke(value, new Object[]{params});
                    result.setResult(resultObj);
                    result.setSuccess(true);
                    result.setResultCode(ApiResultConstant.resultCode_0);
                }
                else {
                    result.setResultCode(ApiResultConstant.resultCode_2004);
                    result.setResultMessage(ApiResultConstant.resultCode_str_2004);
                }
            }
            else{
                result.setResultCode(ApiResultConstant.resultCode_1001);
                result.setResultMessage(ApiResultConstant.resultCode_str_1001);
            }
        }
        //通过反射调用方法如果抛出异常会被转成InvocationTargetException
        catch (InvocationTargetException e) {
            e.printStackTrace();
            Throwable throwable = e.getTargetException();
            //System.out.println("throwable:"+throwable.getMessage()+throwable.getClass());
            //判断被调用方法抛出的异常是否为 ApiServiceException
            if (throwable instanceof ApiServiceException) {
                ApiServiceException apiEx = (ApiServiceException)throwable;
                result.setResultMessage(apiEx.getMessage());
                result.setResultCode(apiEx.getResultCode());
            }
            else {
                result.setResultCode(ApiResultConstant.resultCode_5001);
                result.setResultMessage(ApiResultConstant.resultCode_str_5001);
            }
        }
        catch(ApiServiceException e){
            e.printStackTrace();
            result.setResultMessage(e.getMessage());
            result.setResultCode(e.getResultCode());
        }
        catch (Exception e) {
            e.printStackTrace();
            result.setResultCode(ApiResultConstant.resultCode_5001);
            result.setResultMessage(ApiResultConstant.resultCode_str_5001);
        }
        renderJson(response, result);
    }
}
