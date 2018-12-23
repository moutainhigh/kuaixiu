package com.kuaixiu.screen.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.common.base.dao.BaseDao;
import com.common.base.service.BaseService;
import com.kuaixiu.oldtonew.entity.OldToNewUser;
import com.kuaixiu.oldtonew.service.OldToNewService;
import com.kuaixiu.screen.dao.ScreenOrderMapper;
import com.kuaixiu.screen.entity.ScreenCustomer;
import com.kuaixiu.screen.entity.ScreenOrder;

import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.XLSTransformer;

/**
* @author: anson
* @CreateDate: 2017年10月17日 下午5:06:12
* @version: V 1.0
* 
*/
@Service("screenOrderService")
public class ScreenOrderService extends BaseService<ScreenOrder>{
    
	private static final Logger log=Logger.getLogger(ScreenOrderService.class);
	
	@Autowired
	private ScreenOrderMapper mapper;
	@Autowired
	private ScreenCustomerService screenCustomerService;
	@Override
	public ScreenOrderMapper<ScreenOrder> getDao() {
		// TODO Auto-generated method stub
		return mapper;
	}
	
	/**
	 * 保存订单
	 */
	public int save(ScreenOrder o){
         return getDao().add(o);		
	}
	
	
	/**
	 * 根据订单号查找碎屏险订单
	 * @return
	 */
	public ScreenOrder queryByOrderNo(String orderNo){
		return getDao().queryByOrderNo(orderNo);
	}
	
	
	/**
     * 已Excel形式导出列表数据
     * @param params
     */
    @SuppressWarnings("rawtypes")
    public void expDataExcel(Map<String, Object> params){
        String templateFileName = params.get("tempFileName")+"";
        String destFileName =params.get("outFileName")+"";
        //获取查询条件
        String orderNo=MapUtils.getString(params,"query_orderNo");
        String states=MapUtils.getString(params,"query_orderStates");
        String mobile= MapUtils.getString(params,"query_customerMobile");
    	String queryStartTime = MapUtils.getString(params,"query_startTime");
        String queryEndTime = MapUtils.getString(params,"query_endTime");
    	String isActive = MapUtils.getString(params,"query_isActive");
        ScreenOrder sc=new ScreenOrder();
        if(StringUtils.isNotBlank(states)){
        	  sc.setOrderStatus(Integer.parseInt(states));
        }
        if(StringUtils.isNotBlank(isActive)){
      	  sc.setIsActive(Integer.parseInt(isActive));
        } 
        sc.setOrderNo(orderNo);
        sc.setMobile(mobile);
        sc.setQueryStartTime(queryStartTime);
        sc.setQueryEndTime(queryEndTime);
        String idStr = MapUtils.getString(params, "ids");
        if(StringUtils.isNotBlank(idStr)) {
        	String[] ids = StringUtils.split(idStr, ",");
        	sc.setQueryIds(Arrays.asList(ids));
        }
        
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<ScreenOrder> list =queryList(sc);
        
        for(ScreenOrder o:list){
        	//格式化时间
    		String startTime = sdf.format(o.getInTime());
    		if(o.getEndTime()!=null){
    			String endTime=sdf.format(o.getEndTime());
    			o.setStringEndTime(endTime);
    		}
    		if(o.getIsActive()==1){
    			//免激活  要提供 机型 手机串号
    			ScreenCustomer s=new ScreenCustomer();
    			s.setMobile(o.getMobile());
    			s.setIsActive(1);
    			List<ScreenCustomer> slist=screenCustomerService.queryList(s);
    			if(!slist.isEmpty()){
    				o.setProjectBrand(slist.get(0).getBrand());
    				o.setMobileModel(slist.get(0).getModel());
    				o.setMobileCode(slist.get(0).getImei());
    			}else{
    				o.setMobileCode("无");
    				o.setMobileModel(o.getProjectBrand());
    			}
    		}
    		o.setStringInTime(startTime);
    		status(o);
    	}
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("orderList",list);
            
        XLSTransformer transformer = new XLSTransformer();
        try {
            transformer.transformXLS(templateFileName, map, destFileName);
        } 
        catch (ParsePropertyException e) {
            log.error("文件导出--ParsePropertyException", e);
        } 
        catch (InvalidFormatException e) {
            log.error("文件导出--InvalidFormatException", e);
        } 
        catch (IOException e) {
            log.error("文件导出--IOException", e);
        }
    }
    
    /**
     * 解释订单状态
     * @param orderStatus
     * @param o
     * 0未付款   1已付款   2退款中   3已退款  4提交失败   5提交成功  6已激活 10已取消
     */
    public static void status(ScreenOrder o){
    	switch(o.getOrderStatus()){
		case 0:
			o.setFromSystem("未付款");
			break;
		case 1:
			o.setFromSystem("已付款");
			break;	
		case 2:
			o.setFromSystem("退款中");
			break;
		case 3:
			o.setFromSystem("已退款");
			break;
		case 4:
			o.setFromSystem("提交失败");
			break;
		case 5:
			o.setFromSystem("提交成功");
			break;
		case 6:
			o.setFromSystem("已激活");
			break;
		case 10:
			o.setFromSystem("已取消");
			break;
		}
    }
    

}
