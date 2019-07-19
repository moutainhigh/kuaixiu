package com.kuaixiu.timer;

import com.system.basic.statistic.service.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 定时统计.
 * 
 * @CreateDate: 2016-9-15 下午5:38:21
 * @version: V 1.0
 */
public class Job4Statistic {

	@Autowired
    private StatisticService statisticService;
    /**
     * 定时统计
     * 
     * @CreateDate: 2016-9-15 下午5:48:51
     */
    public void dealAutoStatistic(){
        System.out.println("定时统计开始...");
        try{
	        statisticService.statisticOrderCount("0");
	    	statisticService.statisticOrderCount("1");
	    	statisticService.statisticProviderCount();
	    	statisticService.statisticCustomerCount("0");
	    	statisticService.statisticCustomerCount("1");
	    	statisticService.statisticSumMomey("0");
	    	statisticService.statisticSumMomey("1");
        }
        catch(Exception e){
        	e.printStackTrace();
        }
        System.out.println("定时统计结束...");
    }
    
   
    /**
     * @param args
     * @CreateDate: 2016-9-15 下午5:38:03
     */
    public static void main(String[] args) {

    }

}
