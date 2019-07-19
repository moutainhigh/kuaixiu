package com.kuaixiu.recycleUser.controller;

import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.util.SmsSendUtil;
import com.common.util.ValidatorUtil;
import com.common.wechat.common.util.StringUtils;
import com.system.api.entity.ResultData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * HsUser Controller
 *
 * @CreateDate: 2019-05-30 上午10:24:26
 * @version: V 1.0
 */
@Controller
public class HsUserController extends BaseController {


    @RequestMapping("/recycle/sendSmsCode")
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
}
