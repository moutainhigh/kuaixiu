package com.kuaixiu.videoCard.controller;

import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.google.common.collect.Maps;
import com.kuaixiu.order.constant.OrderConstant;
import com.kuaixiu.videoCard.entity.VideoCard;
import com.kuaixiu.videoCard.service.VideoCardService;
import com.kuaixiu.videoUserRel.entity.VideoUserRel;
import com.kuaixiu.videoUserRel.service.VideoUserRelService;
import com.system.basic.user.entity.SessionUser;
import com.system.constant.SystemConstant;
import com.system.util.ExcelUtil;
import jodd.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * VideoCard Controller
 *
 * @CreateDate: 2019-08-15 下午03:37:26
 * @version: V 1.0
 */
@Controller
public class VideoCardController extends BaseController {

    @Autowired
    private VideoCardService videoCardService;


    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/videoCard/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {

        String returnView ="videoCard/videoCardList";
        return new ModelAndView(returnView);
    }




    @RequestMapping(value="/videoCard/import")
    public ModelAndView cardImport(HttpServletRequest request,HttpServletResponse response){
        return new ModelAndView("videoCard/importIndex");
    }




    @RequestMapping("/videoCard/personList")
    @ResponseBody
    public void personList(HttpServletRequest request,
                            HttpServletResponse response) throws IOException {
        SessionUser su = getCurrentUser(request);
        String loginId = su.getUser().getLoginId();
        List<VideoCard> list=new ArrayList<>();
        if(!StringUtils.isBlank(loginId)){
            VideoUserRel rel=new VideoUserRel();
            rel.setMobile(loginId);
            list=videoCardService.getVideoUser(rel);
        }
        Map<String, Object> resultMap = Maps.newHashMap();
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
        resultMap.put("data", list);
        renderJson(response, resultMap);
    }





    public static void projectNews() throws Exception {
        String file = "E:\\code\\爱奇艺卡密类型.xlsx";
        Object o = ExcelUtil.testProjectList(file);
        List<VideoCard> list = (List<VideoCard>) o;   //获取excel信息
        System.out.println("卡密信息："+ JSONObject.toJSONString(list));



    }


    public static void main(String[] args) throws Exception {
        projectNews();
    }


}
