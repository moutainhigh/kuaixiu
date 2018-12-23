package com.kuaixiu.recycle.service;

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
import org.springframework.transaction.annotation.Transactional;

import com.common.base.dao.BaseDao;
import com.common.base.service.BaseService;
import com.common.exception.SystemException;
import com.kuaixiu.oldtonew.entity.OldToNewUser;
import com.kuaixiu.oldtonew.service.OldToNewService;
import com.kuaixiu.recycle.dao.RecycleWechatMapper;
import com.kuaixiu.recycle.entity.RecycleCompany;
import com.kuaixiu.recycle.entity.RecycleWechat;
import com.system.basic.user.entity.SessionUser;

import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.XLSTransformer;

/**
* @author: anson
* @CreateDate: 2018年4月25日 下午5:32:26
* @version: V 1.0
* 
*/
@Service("recycleWechatService")
public class RecycleWechatService extends BaseService<RecycleWechat>{

	private static final Logger log=Logger.getLogger(RecycleWechatService.class);
	
	@Autowired
	private RecycleWechatMapper<RecycleWechat> mapper;
	
	@Override
	public RecycleWechatMapper<RecycleWechat> getDao() {
		return mapper;
	}

	
	/**
	 * 通过openId查询
	 * @param openId
	 * @return
	 */
	public RecycleWechat queryByOpenId(String openId){
		return mapper.queryByOpenId(openId);
	}
	
	/**
	 * 通过手机号查询
	 * @param mobile
	 * @return
	 */
	public RecycleWechat queryByMobile(String mobile){
		return mapper.queryByMobie(mobile);
	}

	/**
	 * 通过登录手机号查询
	 * @param mobile
	 * @return
	 */
	public RecycleWechat queryLoginMobile(String mobile){
		return mapper.queryLoginMobie(mobile);
	}

	/**
	 * 通过openId修改
	 * @param wechat
	 * @return
	 */
	@Transactional
	public int updateByOpenId(RecycleWechat wechat){
		return mapper.updateByOpenId(wechat);
	}

	public int updateByLoginMobile(RecycleWechat wechat){
		return mapper.updateByLoginMobile(wechat);
	}
	
	/**
	 * 通过openId新增一条记录  保证其唯一
	 * @param wechat
	 * @return
	 */
	@Transactional
	public void addByOpenId(RecycleWechat wechat){
		RecycleWechat w = mapper.queryByOpenId(wechat.getOpenId());
		if(w==null){
			//记录不存在 则新增
			mapper.add(wechat);
		}else{
			//存在则更新sessionKey
			w.setSessionKey(wechat.getSessionKey());
			w.setUnionId(wechat.getUnionId());
			this.updateByOpenId(w);
		}
	}
	
	/**
	 * 获取省份
	 * @return
	 */
	public List<String> getProvince(){
		return mapper.queryProvince();
	}
	
	/**
	 * 获取省份下城市
	 * @param city
	 * @return
	 */
	public List<String> getCity(String city){
		return mapper.queryCity(city);
	}
	
	
	/**
	 * 获取品牌
	 * @return
	 */
	public List<String> getBrands(){
		return mapper.queryBrand();
	}
	
	/**
	 * 获取品牌下机型
	 * @param brand
	 * @return
	 */
	public List<String> getModels(String brand){
		return mapper.queryModel(brand);
	}
	
	
	
	
	/**
	 * 删除
	 * @param idStr
	 * @param su
	 * @return
	 */
    @Transactional
    public int deleteById(String idStr, SessionUser su){
        if(StringUtils.isBlank(idStr)){
            throw new SystemException("参数为空，无法更新");
        }
        //处理批量操作
        System.out.println("批量操作："+idStr);
        String[] ids = idStr.split(",");
        for(String id : ids){
        	RecycleWechat t = getDao().queryById(id);
            t.setIsDel(1);
            getDao().update(t);
        }
        return 1;
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
        String mobile = MapUtils.getString(params, "query_mobile");
    	String queryStartTime = MapUtils.getString(params,"query_startTime");
        String queryEndTime = MapUtils.getString(params,"query_endTime");
        String province = MapUtils.getString(params,"query_province");
        String city = MapUtils.getString(params,"query_city");
        String brand = MapUtils.getString(params,"query_brand");
		String model = MapUtils.getString(params,"query_model");
        RecycleWechat wechat=new RecycleWechat();
        wechat.setMobile(mobile);
        wechat.setProvince(province);
        wechat.setCity(city);
        wechat.setBrand(brand);
        wechat.setModel(model);
        wechat.setQueryStartTime(queryStartTime);
        wechat.setQueryEndTime(queryEndTime);
        String idStr = MapUtils.getString(params, "ids");
        if(StringUtils.isNotBlank(idStr)) {
        	String[] ids = StringUtils.split(idStr, ",");
        	wechat.setQueryIds(Arrays.asList(ids));
        }
        
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<RecycleWechat> list =this.queryList(wechat);
        for(RecycleWechat o:list){
    		String startTime = sdf.format(o.getInTime());
    		o.setStringInTime(startTime);
    	}
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("wechatList", list);
            
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
