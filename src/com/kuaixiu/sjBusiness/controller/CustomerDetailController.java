package com.kuaixiu.sjBusiness.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.util.*;
import com.common.wechat.common.util.StringUtils;
import com.google.common.collect.Maps;
import com.kuaixiu.sjBusiness.entity.SjCode;
import com.kuaixiu.sjBusiness.service.SjCodeService;
import com.kuaixiu.sjUser.entity.CustomerDetail;
import com.kuaixiu.sjUser.entity.SjUser;
import com.kuaixiu.sjUser.service.CustomerDetailService;
import com.kuaixiu.sjUser.service.SjSessionUserService;
import com.kuaixiu.sjUser.service.SjUserService;
import com.system.api.entity.ResultData;
import com.system.constant.SystemConstant;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Map;

/**
 * CustomerDetail Controller
 *
 * @CreateDate: 2019-05-06 上午10:48:12
 * @version: V 1.0
 */
@Controller
public class CustomerDetailController extends BaseController {
    private static final Logger log = Logger.getLogger(CustomerDetailController.class);

    @Autowired
    private SjSessionUserService sessionUserService;
    @Autowired
    private SjCodeService codeService;
    @Autowired
    private SjUserService userService;
    @Autowired
    private CustomerDetailService customerDetailService;

    /**
     * 注册
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sj/wechat/register")
    @ResponseBody
    public ResultData register(HttpServletRequest request,
                               HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        try {
            JSONObject params = getPrarms(request);
            String name = params.getString("name");
            String phone = params.getString("phone");
            String code = params.getString("checkCode");
            Integer cityCompanyId = params.getInteger("cityCompanyId");//市公司
            Integer managementUnitId = params.getInteger("managementUnitId");//经营单元
            Integer branchOfficeId = params.getInteger("branchOfficeId");//支局
            Integer contractBodyId = params.getInteger("contractBodyId");//承包体
            String marketingNo = params.getString("marketingNo");

            if (StringUtils.isBlank(name) || StringUtils.isBlank(phone)
                    || StringUtils.isBlank(code) || null==cityCompanyId) {
                getSjResult(result, null, false, "2", null, "参数为空");
            }

            SjUser user = userService.checkWechatLogin(phone);
            if (!userService.checkRandomCode(phone, code)) {
                getSjResult(result, null, false, "3", null, "验证码错误");
            } else if (user != null) {
                getSjResult(result, null, false, "1", null, "该手机号已注册");
            } else {
                SjUser createUser = new SjUser();
                createUser.setLoginId(phone);
                createUser.setName(name);
                createUser.setPhone(phone);
                createUser.setType(2);
                userService.add(createUser);

                CustomerDetail customerDetail=new CustomerDetail();
                customerDetail.setLoginId(phone);
                customerDetail.setCityCompanyId(cityCompanyId);
                customerDetail.setManagementUnitId(managementUnitId);
                customerDetail.setBranchOfficeId(branchOfficeId);
                customerDetail.setContractBodyId(contractBodyId);
                customerDetail.setMarketingNo(marketingNo);
                customerDetailService.add(customerDetail);

                getSjResult(result, null, true, "0", null, "注册成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }

    /**
     * 登录
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sj/wechat/checkLogin")
    @ResponseBody
    public ResultData checkLogin(HttpServletRequest request,
                            HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        try {
            JSONObject params = getPrarms(request);
            String phone = params.getString("phone");
            String code = params.getString("checkCode");
            if (StringUtils.isBlank(phone) || StringUtils.isBlank(code)) {
                return getSjResult(result, null, false, "2", null, "参数为空");
            }
            SjUser user = userService.checkWechatLogin(phone);
            if (!userService.checkRandomCode(phone, code)) {
                getSjResult(result, null, false, "3", null, "验证码错误");
            } else if (user == null) {
                getSjResult(result, null, false, "1", null, "该手机用户不存在");
            } else {
                //初始化SessionUser
                sessionUserService.initSessionUser(user, request);

                String[] dname = request.getServerName().split("\\.");
                String phoneBase64=Base64Util.getBase64(phone);
                CookiesUtil.setCookie(response, Consts.COOKIE_SJ_PHONE, phoneBase64, CookiesUtil.prepare(dname), 999999999);

                getSjResult(result, null, true, "0", null, "登录成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }

    /**
     * 发送手机验证码
     *
     * @throws IOException
     */
    @RequestMapping("/sj/wechat/sendSmsCode")
    @ResponseBody
    public ResultData sendSmsCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResultData result = new ResultData();
        try {
            JSONObject params = getPrarms(request);
            String mobile = params.getString("phone");
            //验证手机号码
            if (ValidatorUtil.isMobile(mobile)) {
                String randomCode = getRandomCode(request, mobile);
                SmsSendUtil.sendCheckCode(mobile, randomCode);
                getSjResult(result, null, true, "0", null, "操作成功");
            } else {
                getSjResult(result, null, false, "2", null, "请输入正确手机号码");
            }
        } catch (Exception e) {
            e.printStackTrace();
            getSjResult(result, null, false, "5", null, "系统异常请稍后再试");
        }
        return result;
    }

    /**
     * 验证是否登录
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping("/sj/wechat/isLogin")
    @ResponseBody
    public ResultData isLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResultData result = new ResultData();
        try {
            Cookie cookie = CookiesUtil.getCookieByName(request, Consts.COOKIE_SJ_PHONE);
            if (cookie == null || StringUtils.isBlank(cookie.getValue())) {
                return getSjResult(result, null, false, "1", null, "未登录");
            }
            //Cookie不过期
            String phoneStr = cookie.getValue();
            try {
                phoneStr = URLDecoder.decode(phoneStr,"UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            //3.base64解密用户名
            String phone = Base64Util.getFromBase64(phoneStr);
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("mobile",phone);
            getSjResult(result, jsonObject, true, "0", null, "未登录");
        } catch (Exception e) {
            e.printStackTrace();
            getSjResult(result, null, false, "5", null, "系统异常请稍后再试");
        }
        return result;
    }

    /**
     * 获取验证码并保存到session
     *
     * @param request
     * @return
     * @CreateDate: 2016-9-13 下午8:24:41
     */
    protected String getRandomCode(HttpServletRequest request, String key) {
        String randomCode = SmsSendUtil.randomCode();
        request.getSession().setAttribute(SystemConstant.SESSION_RANDOM_CODE + key, randomCode);
        //保存验证码到数据库
        SjCode code = codeService.queryById(key);
        if (code == null) {
            code = new SjCode();
            code.setCode(randomCode);
            code.setPhone(key);
            codeService.add(code);
        } else {
            code.setCode(randomCode);
            code.setUpdateTime(new Date());
            codeService.saveUpdate(code);
        }
        return randomCode;
    }
}
