package com.kuaixiu.sjBusiness.controller;

import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.util.*;
import com.common.wechat.common.util.StringUtils;
import com.kuaixiu.sjUser.entity.CustomerDetail;
import com.kuaixiu.sjUser.entity.SjUser;
import com.kuaixiu.sjUser.service.CustomerDetailService;
import com.kuaixiu.sjUser.service.SjSessionUserService;
import com.kuaixiu.sjUser.service.SjUserService;
import com.system.api.entity.ResultData;
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

            if (StringUtils.isBlank(name)) {
                return getSjResult(result, null, false, "2", null, "姓名为空");
            }
            if (StringUtils.isBlank(phone)) {
                return getSjResult(result, null, false, "2", null, "手机号为空");
            }
            if (StringUtils.isBlank(code)) {
                return getSjResult(result, null, false, "2", null, "验证码为空");
            }
            if (null == cityCompanyId) {
                return getSjResult(result, null, false, "2", null, "市公司为空");
            }

            SjUser user = userService.checkWechatLogin(phone, 2);
            if (!userService.checkRandomCode(phone, code)) {
                return getSjResult(result, null, false, "3", null, "验证码错误");
            } else if (user != null) {
                return getSjResult(result, null, false, "1", null, "该手机号已注册");
            } else {
                SjUser createUser = new SjUser();
                createUser.setLoginId(phone);
                createUser.setName(name);
                createUser.setPhone(phone);
                createUser.setType(2);
                userService.add(createUser);

                SjUser sjUser = userService.getDao().queryByLoginId(phone, 2);

                CustomerDetail customerDetail = new CustomerDetail();
                customerDetail.setLoginId(sjUser.getId());
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
            Integer isLogin = params.getInteger("isRememberLogin");//是否记住登录
            if (StringUtils.isBlank(phone) || StringUtils.isBlank(code)) {
                return getSjResult(result, null, false, "2", null, "参数为空");
            }
            SjUser user = userService.checkWechatLogin(phone, 2);
            if (!userService.checkRandomCode(phone, code)) {
                return getSjResult(result, null, false, "3", null, "验证码错误");
            } else if (user == null) {
                return getSjResult(result, null, false, "1", null, "该手机用户不存在");
            }
            if (isLogin != null && isLogin == 1) {
                String[] dname = request.getServerName().split("\\.");
                String phoneBase64 = Base64Util.getBase64(phone);
                CookiesUtil.setCookie(response, Consts.COOKIE_SJ_PHONE, phoneBase64, CookiesUtil.prepare(dname), 999999999);
            }
            //初始化SessionUser
            sessionUserService.customerInitSessionUser(user, request);
            getSjResult(result, null, true, "0", null, "登录成功");
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
            if (StringUtils.isBlank(mobile)) {
                return getSjResult(result, null, false, "2", null, "手机号不能为空");
            }
            //验证手机号码
            if (ValidatorUtil.isMobile(mobile)) {
                String randomCode = customerDetailService.getRandomCode(request, mobile);
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
     *
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
                phoneStr = URLDecoder.decode(phoneStr, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            //3.base64解密用户名
            String phone = Base64Util.getFromBase64(phoneStr);
            SjUser user = userService.checkWechatLogin(phone, 2);
            if (user == null) {
                return getSjResult(result, null, false, "1", null, "该手机用户不存在");
            }
            sessionUserService.customerInitSessionUser(user, request);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("mobile", phone);
            getSjResult(result, jsonObject, true, "0", null, "已登录");
        } catch (Exception e) {
            e.printStackTrace();
            getSjResult(result, null, false, "5", null, "系统异常请稍后再试");
        }
        return result;
    }

    /**
     * 查看个人信息
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping("/sj/wechat/getUserDetail")
    @ResponseBody
    public ResultData getUserDetail(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResultData result = new ResultData();
        try {
            JSONObject params = getPrarms(request);
            String phone = params.getString("phone");
            if (StringUtils.isBlank(phone)) {
                return getSjResult(result, null, false, "2", null, "参数为空");
            }
            SjUser sjUser = userService.getDao().queryByLoginId(phone, 2);
            CustomerDetail customerDetail = customerDetailService.getDao().queryByLoginId(sjUser.getId());
            JSONObject jsonObject = customerDetailService.getUserToJsonObject(sjUser, customerDetail);

            getSjResult(result, jsonObject, true, "0", null, "操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            getSjResult(result, null, false, "1", null, "操作失败");
        }
        return result;
    }

    /**
     * 编辑个人信息
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping("/sj/wechat/updateUser")
    @ResponseBody
    public ResultData updateUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResultData result = new ResultData();
        try {
            JSONObject params = getPrarms(request);
            String phone = params.getString("phone");
            String name = params.getString("name");
            Integer cityCompanyId = params.getInteger("cityCompanyId");
            Integer managementUnitId = params.getInteger("managementUnitId");
            Integer branchOfficeId = params.getInteger("branchOfficeId");
            Integer contractBodyId = params.getInteger("contractBodyId");
            String marketingNo = params.getString("marketingNo");
            if (StringUtils.isBlank(phone) || StringUtils.isBlank(name) || null == cityCompanyId) {
                return getSjResult(result, null, false, "2", null, "参数为空");
            }
            SjUser sjUser = userService.getDao().queryByLoginId(phone, 2);
            if (sjUser == null) {
                return getSjResult(result, null, false, "3", null, "参数错误");
            }
            CustomerDetail customerDetail = customerDetailService.getDao().queryByLoginId(sjUser.getId());
            if (customerDetail == null) {
                return getSjResult(result, null, false, "3", null, "参数错误");
            }
            sjUser.setName(name);
            userService.saveUpdate(sjUser);

            customerDetail.setCityCompanyId(cityCompanyId);
            customerDetail.setManagementUnitId(managementUnitId);
            customerDetail.setBranchOfficeId(branchOfficeId);
            customerDetail.setContractBodyId(contractBodyId);
            customerDetail.setMarketingNo(marketingNo);
            customerDetailService.saveUpdate(customerDetail);

            getSjResult(result, null, true, "0", null, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            getSjResult(result, null, false, "1", null, "操作失败");
        }
        return result;
    }

    /**
     * 退出登录
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping("/sj/wechat/loginOut")
    @ResponseBody
    public ResultData loginOut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResultData result = new ResultData();
        try {
            JSONObject params = getPrarms(request);
            String phone = params.getString("phone");
            if (StringUtils.isBlank(phone)) {
                return getSjResult(result, null, false, "2", null, "参数为空");
            }
            sessionUserService.removeSessionUser(request);

            String[] dname = request.getServerName().split("\\.");
//            String phoneBase64 = Base64Util.getBase64(phone);
            CookiesUtil.setCookie(response, Consts.COOKIE_SJ_PHONE, null, CookiesUtil.prepare(dname), 0);

            getSjResult(result, null, true, "0", null, "操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            getSjResult(result, null, false, "1", null, "操作失败");
        }
        return result;
    }


}
