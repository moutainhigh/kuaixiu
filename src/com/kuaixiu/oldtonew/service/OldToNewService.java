package com.kuaixiu.oldtonew.service;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.common.base.dao.BaseDao;
import com.common.base.service.BaseService;
import com.common.exception.SystemException;
import com.kuaixiu.customer.entity.Customer;
import com.kuaixiu.oldtonew.dao.OldToNewMapper;
import com.kuaixiu.oldtonew.entity.OldToNewUser;
import com.kuaixiu.provider.entity.Provider;
import com.system.basic.user.entity.SessionUser;
import com.system.constant.SystemConstant;

import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.XLSTransformer;
@Service("oldToNewService")
public class OldToNewService extends BaseService<OldToNewUser> {
	
	private static final Logger log=Logger.getLogger(OldToNewService.class);
	
	@Autowired
	private OldToNewMapper<OldToNewUser> mapper;

	public OldToNewMapper<OldToNewUser> getDao() {
		return mapper;
	}
	
	/**
	 * 增加以旧换新用户
	 */
    public void addUser(OldToNewUser user){
    	getDao().addNews(user);
    }
    
    
    /**
     * 删除用户信息
     */
    public int deleteById(String idStr, SessionUser su){
        if(StringUtils.isBlank(idStr)){
            throw new SystemException("参数为空，无法更新");
        }
        //处理批量操作
        String[] ids = idStr.split(",");
        for(String id : ids){
            OldToNewUser t = getDao().queryById(id);
            t.setIsDel(1);
            t.setUpdateUserid(su.getUserId());
            getDao().update(t);
        }
        return 1;
    }
    
    /**
     * 更新用户信息
     */
    public int update(OldToNewUser p, SessionUser su){
    	p.setIsDel(0);
    	p.setUpdateUserid(su.getUserId());
    	return getDao().update(p);
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
        String name = MapUtils.getString(params, "query_name");
        String tel=MapUtils.getString(params,"query_tel");
    	String queryStartTime = MapUtils.getString(params,"query_startTime");
        String queryEndTime = MapUtils.getString(params,"query_endTime");
        String province = MapUtils.getString(params,"queryProvince");
        String city = MapUtils.getString(params,"queryCity");
        String county = MapUtils.getString(params,"queryCounty");
        OldToNewUser old=new OldToNewUser();
        old.setName(name);
        old.setQueryStartTime(queryStartTime);
    	old.setQueryEndTime(queryEndTime);
    	old.setProvince(province);
    	old.setCity(city);
    	old.setCounty(county);
    	
        String idStr = MapUtils.getString(params, "ids");
        if(StringUtils.isNotBlank(idStr)) {
        	String[] ids = StringUtils.split(idStr, ",");
        	old.setQueryIds(Arrays.asList(ids));
        }
        
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<OldToNewUser> project = queryList(old);
        for(OldToNewUser o:project){
    		String startTime = sdf.format(o.getInTime());
    		o.setStringDate(startTime);
    	}
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("oldToNewList", project);
            
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
}
