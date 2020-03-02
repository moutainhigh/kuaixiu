package com.kuaixiu.prizechance.controller;

import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.exception.SystemException;
import com.common.wechat.common.util.StringUtils;
import com.google.common.collect.Maps;
import com.kuaixiu.couponcodepool.controller.CouponCodePoolController;
import com.kuaixiu.couponcodepool.entity.CouponCodePool;
import com.kuaixiu.newtamps.entity.NewTamps;
import com.kuaixiu.prizechance.entity.PrizeChance;
import com.kuaixiu.prizechance.service.PrizeChanceService;
import com.system.api.entity.ResultData;
import com.system.basic.user.service.SessionUserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * PrizeChance Controller
 *
 * @CreateDate: 2019-11-07 下午05:21:23
 * @version: V 1.0
 */
@Controller
public class PrizeChanceController extends BaseController {

    private static final Logger log = Logger.getLogger(PrizeChanceController.class);
    @Autowired
    private PrizeChanceService prizeChanceService;

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
    @RequestMapping(value = "/prizeChance/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="prizeChance/list";
        return new ModelAndView(returnView);
    }
    /**
     * 添加次数
     */
    @RequestMapping(value = "/prizeChance/addCount")
    @ResponseBody
    public ResultData addCount(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        try {
            JSONObject params = getPrarms(request);
            String userMobile = params.getString("userMobile");
            if (StringUtils.isBlank(userMobile)) {
                return getSjResult(result, null, false, "2", null, "参数为空");
            }
            Integer count = prizeChanceService.queryByMobile(userMobile);
            int i;
            if(Objects.isNull(count)){
                PrizeChance t =new PrizeChance();
                t.setId(UUID.randomUUID().toString());
                t.setUserMobile(userMobile);
                t.setCount(1);
                t.setIsDel(0);
                i = prizeChanceService.insertCountByMobile(t);
            }else{
                i = prizeChanceService.updateCountByMobile(userMobile);
            }
            if(i<1){
                throw new SystemException("添加失败");
            }
            getSjResult(result, null, true, "0", null, "操作成功");
        } catch (Exception e) {
            getSjResult(result, null, true, "0", null, "操作失败");
        }
        return result;
    }
    /**
     * 添加次数(通过分享只能加一次)
     */
    @RequestMapping(value = "/prizeChance/addCountByType")
    @ResponseBody
    public ResultData addCountByType(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        try {
            JSONObject params = getPrarms(request);
            String userMobile = params.getString("userMobile");
            String type = params.getString("type");
            if (StringUtils.isBlank(userMobile)||StringUtils.isBlank(type)) {
                return getSjResult(result, null, false, "2", null, "参数为空");
            }
            PrizeChance prc = prizeChanceService.queryIDByMobile(userMobile);
            //Integer count = prizeChanceService.queryByMobileAndType(userMobile,type);
            int i;
            if(StringUtils.isNotEmpty(prc.getType())){
                getSjResult(result, null, false, "0", null, "分享多次只能增加一次抽奖机会,试试去做其他任务吧~");
                return result;
            }
            if(Objects.nonNull(prc.getCount())){
                PrizeChance t =new PrizeChance();
                t.setId(prc.getId());
                t.setUserMobile(userMobile);
                t.setType(type);
                t.setIsDel(0);
                i = prizeChanceService.updateBytype(t);
                if(i<1){
                    throw new SystemException("添加失败");
                }
            } else{
                PrizeChance t =new PrizeChance();
                t.setId(UUID.randomUUID().toString());
                t.setUserMobile(userMobile);
                t.setType(type);
                t.setIsDel(0);
                i = prizeChanceService.addBytype(t);
                if(i<1){
                    throw new SystemException("添加失败");
                }
            }
            getSjResult(result, null, true, "0", null, "操作成功");
        } catch (Exception e) {
            getSjResult(result, null, false, "0", null, "操作失败");
        }
        return result;
    }
    /**
     * 查询抽奖次数
     */
    @RequestMapping(value = "/prizeChance/getCount")
    @ResponseBody
    public ResultData getCount(HttpServletRequest request,HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
     try {
        //获取请求数据
        JSONObject params = getPrarms(request);
        String userMobile = params.getString("userMobile");
        //抽奖
         Integer count = prizeChanceService.queryByMobile(userMobile);
        jsonResult.put("count", count);
        getResult(result, jsonResult, true, "0", "成功");
    } catch (SystemException e) {
        sessionUserService.getSystemException(e, result);
    } catch (Exception e) {
        e.printStackTrace();
        sessionUserService.getException(result);
    }
        return result;
}
}
