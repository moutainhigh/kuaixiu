package com.system.basic.statistic.controller;

import java.util.Map;

import com.common.base.controller.BaseController;
import com.common.echarts.Option;
import com.google.common.collect.Maps;
import com.system.basic.statistic.entity.Statistic;
import com.system.basic.statistic.service.StatisticService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Statistic Controller
 *
 * @CreateDate: 2016-09-24 上午01:21:24
 * @version: V 1.0
 */
@Controller
public class StatisticController extends BaseController {

    @Autowired
    private StatisticService statisticService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/statistic/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="statistic/list";
        return new ModelAndView(returnView);
    }
    
    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/statistic/queryStatistic")
    public void queryStatistic(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        Map<String, Object> resultMap = Maps.newHashMap();

        //获取请求参数
        String startTime = request.getParameter("query_startTime");
        String endTime = request.getParameter("query_endTime");
        String target = request.getParameter("target");
        String queryType = request.getParameter("query_type");
        
        Statistic s = new Statistic();
        s.setQueryStartTime(startTime);
        s.setQueryEndTime(endTime);
        s.setTarget(target);
        s.setTypeKey(queryType);
        Option option = statisticService.queryStatistic(s);
        
        resultMap.put(RESULTMAP_KEY_DATA, option);
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
        resultMap.put(RESULTMAP_KEY_MSG, "查询成功");
        renderJsonWithOutNull(response, resultMap);
    }
    
    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/statistic/test")
    public void test(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
    	statisticService.statisticOrderCount("0");
    	statisticService.statisticOrderCount("1");
    	statisticService.statisticProviderCount();
    	statisticService.statisticCustomerCount("0");
    	statisticService.statisticCustomerCount("1");
    	statisticService.statisticSumMomey("0");
    	statisticService.statisticSumMomey("1");
    	renderText(response, "统计完成！");
    }
        
}
