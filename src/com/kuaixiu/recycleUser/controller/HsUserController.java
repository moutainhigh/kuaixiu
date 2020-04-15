package com.kuaixiu.recycleUser.controller;

import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.util.SmsSendUtil;
import com.common.util.SmsSendUtil2;
import com.common.util.ValidatorUtil;
import com.common.wechat.common.util.StringUtils;
import com.system.api.CodeService;
import com.system.api.entity.Code;
import com.system.api.entity.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * HsUser Controller
 *
 * @CreateDate: 2019-05-30 上午10:24:26
 * @version: V 1.0
 */
@Controller
public class HsUserController extends BaseController {

    @Autowired
    private CodeService codeService;

    @RequestMapping("/recycle/sendSmsCode")
    @ResponseBody
    public ResultData sendSmsCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResultData result = new ResultData();
        try {
            JSONObject params = getPrarms(request);
            String mobile = params.getString("phone");
            String fm=params.getString("fm");  //如果fm存在则发送超人验证码，否则发送天翼回收
            System.out.println("渠道："+fm);
            if (StringUtils.isBlank(mobile)) {
                return getSjResult(result, null, false, "2", null, "手机号不能为空");
            }
            //验证手机号码
            if (!ValidatorUtil.isMobile(mobile)) {
                getSjResult(result, null, false, "2", null, "请输入正确手机号码");
            }


                //验证上次获取验证码时间间隔,30分内只能获取一次
                Code code = codeService.queryById(mobile);
            if(code!=null){
                long diff = System.currentTimeMillis() - code.getUpdateTime().getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date old = new Date();
                old.setTime(code.getUpdateTime().getTime());
                System.out.println("oldTime:"+sdf.format(old));

                Date now = new Date();
                now.setTime(System.currentTimeMillis());
                System.out.println("nowTime:"+sdf.format(now));
                long min = diff / (1000 * 60);
                if(min < 30){
                    getSjResult(result, null, false, "2", null, "验证码时效为30分钟,请勿重复获取");
                }else{
                    String randomCode = getRandomCode(request, mobile);
                    if(StringUtils.isBlank(fm)){
                        System.out.println("天翼短信");
                        SmsSendUtil.sendCheckCode(mobile, randomCode);  //天翼
                    }else{
                        System.out.println("超人短信");
                        SmsSendUtil2.sendCheckCode(mobile, randomCode); //超人
                    }
                    getSjResult(result, null, true, "0", null, "操作成功");
                }
            }else {
                String randomCode = getRandomCode(request, mobile);
                if(StringUtils.isBlank(fm)){
                    SmsSendUtil.sendCheckCode(mobile, randomCode);  //天翼
                }else{
                    SmsSendUtil2.sendCheckCode(mobile, randomCode); //超人
                }
                getSjResult(result, null, true, "0", null, "操作成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            getSjResult(result, null, false, "5", null, "系统异常请稍后再试");
        }
        return result;
    }
//    public ResultData sendSmsCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        ResultData result = new ResultData();
//        try {
//            JSONObject params = getPrarms(request);
//            String mobile = params.getString("phone");
//            if (StringUtils.isBlank(mobile)) {
//                return getSjResult(result, null, false, "2", null, "手机号不能为空");
//            }
//            //验证手机号码
//            if (ValidatorUtil.isMobile(mobile)) {
//                String randomCode = getRandomCode(request, mobile);
//                SmsSendUtil.sendCheckCode(mobile, randomCode);
//                getSjResult(result, null, true, "0", null, "操作成功");
//            } else {
//                getSjResult(result, null, false, "2", null, "请输入正确手机号码");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            getSjResult(result, null, false, "5", null, "系统异常请稍后再试");
//        }
//        return result;
//    }
}
