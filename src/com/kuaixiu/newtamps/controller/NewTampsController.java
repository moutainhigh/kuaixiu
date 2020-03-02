package com.kuaixiu.newtamps.controller;

import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.exception.SystemException;
import com.common.paginate.Page;
import com.common.wechat.common.util.StringUtils;
import com.google.common.collect.Maps;
import com.kuaixiu.coupon.entity.Coupon;
import com.kuaixiu.newtamps.entity.NewTamps;
import com.kuaixiu.newtamps.service.NewTampsService;
import com.kuaixiu.recycle.controller.RecycleCouponController;
import com.kuaixiu.recycle.entity.RecycleCoupon;
import com.system.api.entity.ResultData;
import com.system.basic.sequence.util.SeqUtil;
import com.system.basic.user.entity.SessionUser;
import com.system.basic.user.service.SessionUserService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * NewTamps Controller
 *
 * @CreateDate: 2019-11-08 上午09:39:09
 * @version: V 1.0
 */
@Controller
public class NewTampsController extends BaseController {
    private static final Logger log = Logger.getLogger(NewTampsController.class);

    @Autowired
    private NewTampsService newTampsService;
    @Autowired
    private SessionUserService sessionUserService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/newTamps/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="newTamps/list";
        return new ModelAndView(returnView);
    }
    /**
     * queryListForPage
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/newTamps/queryList")
    @ResponseBody
    public ResultData queryList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        try {
            //获取查询条件
            JSONObject params = getPrarms(request);
            String userMobile = params.getString("userMobile");
            if (StringUtils.isBlank(userMobile)|| StringUtils.isBlank(userMobile)) {
                return getSjResult(result, null, false, "2", null, "手机号为空");
            }
            List<NewTamps> list = newTampsService.queryListByUserMobile(userMobile);
            jsonResult.put("list", list);
            getResult(result, jsonResult, true, "0", "成功");
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        return result;
    }


    /**
     * 添加
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/newTamps/add")
    @ResponseBody
    public void addCoupon(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> resultMap = Maps.newHashMap();
        try {
            SessionUser su = getCurrentUser(request);
            String couponId = request.getParameter("couponId");
            String batchId = request.getParameter("batchId");
            String grade = request.getParameter("grade");
            String couponName = request.getParameter("couponName");
            String userMobile = request.getParameter("userMobile");
            NewTamps t = new NewTamps();
            t.setId(UUID.randomUUID().toString());
            t.setCouponId(couponId);
            t.setBatchId(batchId);
            t.setGrade(grade);
            t.setCouponName(couponName);
            t.setUserMobile(userMobile);
            t.setIsDel(0);
            t.setCreator(su.getUserId());
            newTampsService.getDao().add(t);
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
            resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
        } catch (Exception e) {
           log.error("NewTampsController----122行,保存新机券失败");
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
            resultMap.put(RESULTMAP_KEY_MSG, "保存失败");
        }
        renderJson(response, resultMap);
    }


}
