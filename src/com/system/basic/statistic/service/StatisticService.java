package com.system.basic.statistic.service;


import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.common.base.service.BaseService;
import com.common.echarts.Option;
import com.common.echarts.axis.CategoryAxis;
import com.common.echarts.axis.ValueAxis;
import com.common.echarts.code.X;
import com.common.echarts.code.Y;
import com.common.echarts.series.Line;
import com.common.util.DateUtil;
import com.system.basic.statistic.dao.StatisticMapper;
import com.system.basic.statistic.entity.Statistic;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Statistic Service
 * @CreateDate: 2016-09-24 上午01:21:24
 * @version: V 1.0
 */
@Service("statisticService")
public class StatisticService extends BaseService<Statistic> {
    private static final Logger log= Logger.getLogger(StatisticService.class);

    @Autowired
    private StatisticMapper<Statistic> mapper;


    public StatisticMapper<Statistic> getDao() {
        return mapper;
    }

    //**********自定义方法***********

    /**
     * 查询统计数据
     * @param s
     * @return
     * @author: lijx
     * @CreateDate: 2016-9-25 下午3:10:22
     */
    public Option queryStatistic(Statistic s){
        List<Statistic> list = getDao().queryStatistic(s);
        Option option = new Option();
        //工具栏
        option.toolbox().show(false);
        //纵坐标
        ValueAxis valueAxis = new ValueAxis();
        valueAxis.axisLabel().formatter("{value}");
        valueAxis.minInterval(1);
        option.yAxis(valueAxis);
        //横坐标
        //创建类目轴  
        CategoryAxis category = new CategoryAxis();  
        //数据
        String legendName = getLegendNameByTarget(s.getTarget());
        Line line = new Line();
        line.name(legendName);
        option.legend().data(legendName).x(X.center).y(Y.bottom);
        if(list != null && list.size() > 0){
            for(Statistic sta : list){
                category.data(sta.getDayTimeFmt());
                if("orderPrice".equals(s.getTarget())){
                    line.data(sta.getDoubleValue());
                }
                else {
                    line.data(sta.getIntValue());
                }
            }
        }
        option.xAxis(category);
        option.series(line);
        return option;
    }
    
    
    
    /**
     * 根据指标
     * @param target
     * @return
     * @CreateDate: 2016-9-25 下午10:36:31
     */
    private String getLegendNameByTarget(String target){
        String name = "";
        if ("order".equals(target)) {
            name = "订单数";
        }
        else if ("provider".equals(target)) {
            name = "连锁商数";
        }
        else if ("costomer".equals(target)) {
            name = "用户数";
        }
        else if ("orderPrice".equals(target)) {
            name = "交易额";
        }
        return name;
    }
    
    
    /**
     * 统计订单数量
     * @throws ParseException 
     */
    @Transactional
    public void statisticOrderCount(String typeKey) throws ParseException{
    	Statistic s = new Statistic();
    	s.setTarget("order");
    	s.setTypeKey(typeKey);
    	//查询最后统计时间
    	String lostDay = getDao().queryLastCountDay(s);
    	Calendar cal = Calendar.getInstance();
    	//记录当前统计时间
    	String nowDate = DateUtil.getDateYYYYMMDD(cal.getTime());
    	if(StringUtils.isNotBlank(lostDay)){
    		cal.setTime(DateUtil.getYYYYMMDDDate(lostDay));
    	}
    	else{
    		//如没有查到则统计前一个月的交易量
    		cal.add(Calendar.DATE, -30);
    	}
    	
    	//日期加一
    	cal.add(Calendar.DATE, 1);
    	String queryDay = DateUtil.getDateYYYYMMDD(cal.getTime());
    	Map<String, Object> params = new HashMap<String, Object>();
    	params.put("typeKey", typeKey);
    	while(queryDay.compareTo(nowDate) < 0){
	    	params.put("queryDay", queryDay);
	    	long count = getDao().queryOrderCountByDay(params);
	    	s.setIntValue(count);
	    	s.setDayTime(cal.getTime());
	    	getDao().add(s);
	    	
	    	//日期加1
	    	cal.add(Calendar.DATE, 1);
	    	queryDay = DateUtil.getDateYYYYMMDD(cal.getTime());
    	}
    }
    
    /**
     * 统计订单数量
     * @throws ParseException 
     */
    @Transactional
    public void statisticProviderCount() throws ParseException{
    	Statistic s = new Statistic();
    	s.setTarget("provider");
    	//查询最后统计时间
    	String lostDay = getDao().queryLastCountDay(s);
    	Calendar cal = Calendar.getInstance();
    	//记录当前统计时间
    	String nowDate = DateUtil.getDateYYYYMMDD(cal.getTime());
    	if(StringUtils.isNotBlank(lostDay)){
    		cal.setTime(DateUtil.getYYYYMMDDDate(lostDay));
    	}
    	else{
    		//如没有查到则统计前一个月的交易量
    		cal.add(Calendar.DATE, -30);
    	}
    	
    	s.setTypeKey("0");
    	//日期加一
    	cal.add(Calendar.DATE, 1);
    	String queryDay = DateUtil.getDateYYYYMMDD(cal.getTime());
    	Map<String, Object> params = new HashMap<String, Object>();
    	while(queryDay.compareTo(nowDate) < 0){
	    	params.put("queryDay", queryDay);
	    	long count = getDao().queryProviderCountByDay(params);
	    	s.setIntValue(count);
	    	s.setDayTime(cal.getTime());
	    	getDao().add(s);
	    	
	    	//日期加1
	    	cal.add(Calendar.DATE, 1);
	    	queryDay = DateUtil.getDateYYYYMMDD(cal.getTime());
    	}
    }
    
    /**
     * 统计订单数量
     * @throws ParseException 
     */
    @Transactional
    public void statisticCustomerCount(String typeKey) throws ParseException{
    	Statistic s = new Statistic();
    	s.setTarget("costomer");
    	s.setTypeKey(typeKey);
    	//查询最后统计时间
    	String lostDay = getDao().queryLastCountDay(s);
    	Calendar cal = Calendar.getInstance();
    	//记录当前统计时间
    	String nowDate = DateUtil.getDateYYYYMMDD(cal.getTime());
    	if(StringUtils.isNotBlank(lostDay)){
    		cal.setTime(DateUtil.getYYYYMMDDDate(lostDay));
    	}
    	else{
    		//如没有查到则统计前一个月的交易量
    		cal.add(Calendar.DATE, -30);
    	}
    	
    	//日期加一
    	cal.add(Calendar.DATE, 1);
    	String queryDay = DateUtil.getDateYYYYMMDD(cal.getTime());
    	Map<String, Object> params = new HashMap<String, Object>();
    	params.put("typeKey", typeKey);
    	while(queryDay.compareTo(nowDate) < 0){
	    	params.put("queryDay", queryDay);
	    	long count = getDao().queryCustomerCountByDay(params);
	    	s.setIntValue(count);
	    	s.setDayTime(cal.getTime());
	    	getDao().add(s);
	    	
	    	//日期加1
	    	cal.add(Calendar.DATE, 1);
	    	queryDay = DateUtil.getDateYYYYMMDD(cal.getTime());
    	}
    }
    
    /**
     * 统计订单数量
     * @throws ParseException 
     */
    @Transactional
    public void statisticSumMomey(String typeKey) throws ParseException{
    	Statistic s = new Statistic();
    	s.setTarget("orderPrice");
    	s.setTypeKey(typeKey);
    	//查询最后统计时间
    	String lostDay = getDao().queryLastCountDay(s);
    	Calendar cal = Calendar.getInstance();
    	//记录当前统计时间
    	String nowDate = DateUtil.getDateYYYYMMDD(cal.getTime());
    	if(StringUtils.isNotBlank(lostDay)){
    		cal.setTime(DateUtil.getYYYYMMDDDate(lostDay));
    	}
    	else{
    		//如没有查到则统计前一个月的交易量
    		cal.add(Calendar.DATE, -30);
    	}
    	
    	//日期加一
    	cal.add(Calendar.DATE, 1);
    	String queryDay = DateUtil.getDateYYYYMMDD(cal.getTime());
    	Map<String, Object> params = new HashMap<String, Object>();
    	params.put("typeKey", typeKey);
    	while(queryDay.compareTo(nowDate) < 0){
	    	params.put("queryDay", queryDay);
	    	long count = getDao().querySumMomeyByDay(params);
	    	s.setDoubleValue(BigDecimal.valueOf(count/100.0));
	    	s.setDayTime(cal.getTime());
	    	getDao().add(s);
	    	
	    	//日期加1
	    	cal.add(Calendar.DATE, 1);
	    	queryDay = DateUtil.getDateYYYYMMDD(cal.getTime());
    	}
    }
}