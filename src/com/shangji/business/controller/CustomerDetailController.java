package com.shangji.business.controller;

import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.exception.SystemException;
import com.common.util.SmsSendUtil;
import com.common.util.ValidatorUtil;
import com.common.wechat.common.util.StringUtils;
import com.google.common.collect.Maps;
import com.shangji.business.entity.Code;
import com.shangji.business.service.CodeService;
import com.shangji.user.entity.User;
import com.shangji.user.service.CustomerDetailService;
import com.shangji.user.service.SjSessionUserService;
import com.shangji.user.service.UserService;
import com.system.api.entity.ResultData;
import com.system.basic.user.entity.SysUser;
import com.system.constant.SystemConstant;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
    private static final Logger log= Logger.getLogger(CustomerDetailController.class);

    @Autowired
    private SjSessionUserService sessionUserService;
    @Autowired
    private CodeService codeService;
    @Autowired
    private UserService userService;

    /**
     * 注册
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/customer/register")
    @ResponseBody
    public ResultData register(HttpServletRequest request,
                            HttpServletResponse response) throws Exception {
        ResultData result=new ResultData();
        try {
            JSONObject params = getPrarms(request);
            String name = params.getString("name");
            String phone = params.getString("phone");
            String code = params.getString("code");
            String cityCompanyId = params.getString("cityCompanyId");
            String managementUnitId = params.getString("managementUnitId");
            String branchOfficeId = params.getString("branchOfficeId");
            String contractBodyId = params.getString("contractBodyId");
            String marketingNo = params.getString("marketingNo");

            if(StringUtils.isBlank(name)||StringUtils.isBlank(phone)||StringUtils.isBlank(code)||
                    StringUtils.isBlank(cityCompanyId)||StringUtils.isBlank(marketingNo)){
                getResult(result,null,false,"2","参数为空");
            }

            User user = userService.checkWechatLogin(phone);
            if (!userService.checkRandomCode(phone, code)) {
                getResult(result,null,false,"3","验证码错误");
            }else if (user != null) {
                getResult(result,null,false,"1","该手机号已注册");
            } else {
                User createUser=new User();
                createUser.setLoginId(phone);
                createUser.setName(name);
                createUser.setPhone(phone);
                createUser.setPhone(phone);
                createUser.setPhone(phone);
                createUser.setPhone(phone);
                createUser.setPhone(phone);

                getResult(result,null,false,"0","登录成功");
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }

    /**
     * 登录
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/customer/login")
    @ResponseBody
    public ResultData login(HttpServletRequest request,
                           HttpServletResponse response) throws Exception {
        ResultData result=new ResultData();
        try {
            JSONObject params = getPrarms(request);
            String phone = params.getString("phone");
            String code = params.getString("code");

            User user = userService.checkWechatLogin(phone);
            if (!userService.checkRandomCode(phone, code)) {
                getResult(result,null,false,"2","验证码错误");
            }else if (user == null) {
                getResult(result,null,false,"1","该手机用户不存在");
            } else {
                //初始化SessionUser
                sessionUserService.initSessionUser(user, request);
                getResult(result,null,false,"0","登录成功");
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }

    /**
     * 发送手机验证码
     * @throws IOException
     */
    @RequestMapping("/customer/sendSmsCode")
    @ResponseBody
    public void sendSmsCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> resultMap = Maps.newHashMap();
        try {
            //获取手机号
            String mobile = request.getParameter("mobile");
            //验证手机号码
            if(ValidatorUtil.isMobile(mobile)){
                String randomCode = getRandomCode(request, mobile);
                SmsSendUtil.sendCheckCode(mobile, randomCode);
                resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
                //resultMap.put(RESULTMAP_KEY_DATA, randomCode);
            }
            else{
                resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
                resultMap.put(RESULTMAP_KEY_MSG, "请输入正确手机号码");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
            resultMap.put(RESULTMAP_KEY_MSG, "系统异常请稍后再试");
        }
        renderJson(response, resultMap);
    }

    /**
     * 获取验证码并保存到session
     * @param request
     * @return
     * @CreateDate: 2016-9-13 下午8:24:41
     */
    protected String getRandomCode(HttpServletRequest request, String key){
        String randomCode = SmsSendUtil.randomCode();
        request.getSession().setAttribute(SystemConstant.SESSION_RANDOM_CODE + key, randomCode);
        //保存验证码到数据库
        Code code = codeService.queryById(key);
        if(code==null){
            code=new Code();
            code.setCode(randomCode);
            code.setPhone(key);
            codeService.add(code);
        }else{
            code.setCode(randomCode);
            code.setUpdateTime(new Date());
            codeService.saveUpdate(code);
        }
        return randomCode;
    }
}
