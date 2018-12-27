package com.kuaixiu.activity.controller;

import com.common.base.controller.BaseController;
import com.kuaixiu.activity.service.ActivityUserService;
import com.system.api.entity.ResultData;
import com.system.basic.user.entity.SessionUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * ActivityUser Controller
 *
 * @CreateDate: 2018-12-25 上午10:12:03
 * @version: V 1.0
 */
@Controller
public class ActivityUserController extends BaseController {

    @Autowired
    private ActivityUserService activityUserService;

    /**
     * 保存公司活动信息
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/activityUser/")
    @ResponseBody
    public ResultData addUser(HttpServletRequest request,
                                  HttpServletResponse response) throws Exception {
        ResultData result=new ResultData();
        try {
            SessionUser user=getCurrentUser(request);

        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
}
