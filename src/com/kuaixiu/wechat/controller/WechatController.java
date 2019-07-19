package com.kuaixiu.wechat.controller;

import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.exception.SystemException;
import com.common.util.SmsSendUtil;
import com.common.util.ValidatorUtil;
import com.system.api.CodeService;
import com.system.api.entity.ResultData;
import com.system.basic.user.service.SessionUserService;
import com.system.constant.ApiResultConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * 微信controller
 *
 * @author yq
 */
@Controller
public class WechatController extends BaseController {
	@Autowired
	private SessionUserService sessionUserService;
    @Autowired
	private CodeService codeService;
    /**
     * 客服
     **/
    @RequestMapping("/wechat/service/customerService")
    public String customerService() {
        return "wechat/customerService";
    }
    
    /**
     * 发送手机验证码
     * @throws IOException 
     */
    @RequestMapping("/wechat/sendSmsCode")
    public void sendSmsCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	//返回对象
        ResultData result = new ResultData();
        //String code=null;
       try {
        	JSONObject params=getPrarms(request);
            //获取手机号
            String mobile =params.getString("mobile");
            //验证手机号码
            if(ValidatorUtil.isMobile(mobile)){
                String randomCode = getRandomCode(request, mobile);
                SmsSendUtil.sendCheckCode(mobile, randomCode);
                result.setSuccess(true);
                result.setResultCode(ApiResultConstant.resultCode_0);
            }else{
            	result.setSuccess(false);
                result.setResultMessage("请输入正确手机号码");
            } 
            }catch(SystemException e){
             sessionUserService.getSystemException(e, result);
            }catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        //result.setResult(code);
        renderJson(response, result);
    }
    
    public static void main(String[] args) {
    	UUID u=new UUID(1,2);
    	System.out.println(u.version());
		System.out.println(UUID.randomUUID());
	}
   
}
