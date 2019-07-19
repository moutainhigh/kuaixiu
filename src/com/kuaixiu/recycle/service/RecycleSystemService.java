package com.kuaixiu.recycle.service;


import com.common.base.service.BaseService;
import com.common.exception.SystemException;
import com.kuaixiu.recycle.dao.RecycleSystemMapper;
import com.kuaixiu.recycle.entity.RecycleSystem;
import com.system.basic.user.entity.SessionUser;
import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RecycleSystem Service
 * @CreateDate: 2017-03-11 下午10:40:00
 * @version: V 1.0
 */
@Service("RecycleSystemService")
public class RecycleSystemService extends BaseService<RecycleSystem> {
    private static final Logger log= Logger.getLogger(RecycleSystemService.class);

    @Autowired
    private RecycleSystemMapper<RecycleSystem> mapper;


    public RecycleSystemMapper<RecycleSystem> getDao() {
        return mapper;
    }

    //**********自定义方法***********
    
    /**
     * 根据名称查询来源系统
     * @param name
     * @return
     * @CreateDate: 2016-9-29 下午7:57:25
     */
    public List<RecycleSystem> queryByName(String name){
        return getDao().queryByName(name);
    }
    
    /**
     * 检查来源系统名称是否存在
     * @param name
     * @return
     * @CreateDate: 2016-9-29 下午8:00:15
     */
    public boolean checkProjectName(String name){
        List<RecycleSystem> list = getDao().queryByName(name);
        return list != null && list.size() > 0; 
    }
    
    /**
     * 保存来源系统
     * @param t
     * @CreateDate: 2016-9-29 下午8:03:11
     */
    @Transactional
    public void save(RecycleSystem t){
        //检查项目名称是否存在
        if(checkProjectName(t.getName())){
            throw new SystemException("该维修项目名称已存在");
        }
        getDao().add(t);
    }
    
    /**
     * 更新来源系统
     * @param p
     * @return
     * @CreateDate: 2016-8-31 下午7:08:33
     */
    @Transactional
    public int update(RecycleSystem f){
    	if(f == null || f.getId() == null){
            throw new SystemException("参数为空，无法更新");
        }
        RecycleSystem project = getDao().queryById(f.getId());
        if(project == null){
            throw new SystemException("来源系统未找到，无法更新");
        }
        if(!project.getName().equals(f.getName())){
            //检查项目名称是否存在
            if(checkProjectName(f.getName())){
                throw new SystemException("该来源系统名称已存在");
            }
        }
        project.setName(f.getName());
        project.setUpdateUserid(f.getUpdateUserid());
        return getDao().update(project);
    }
    
    /**
     * 删除来源系统
     * @param p
     * @return
     * @CreateDate: 2016-8-31 下午7:09:59
     */
    @Transactional
    public int delete(RecycleSystem f){
        if(f == null || f.getId() == null){
            throw new SystemException("参数为空，无法更新");
        }
        RecycleSystem t = getDao().queryById(f.getId());
        t.setIsDel(1);
        t.setUpdateUserid(f.getUpdateUserid());
        return getDao().update(t);
    }
    
    /**
     * 删除维修项目
     * @param p
     * @return
     * @CreateDate: 2016-8-31 下午7:09:59
     */
    @Transactional
    public int deleteById(String idStr, SessionUser su){
        if(StringUtils.isBlank(idStr)){
            throw new SystemException("参数为空，无法更新");
        }
        //处理批量操作
        String[] ids = idStr.split(",");
        for(String id : ids){
        	RecycleSystem t = getDao().queryById(id);
            t.setIsDel(1);
            t.setUpdateUserid(su.getUserId());
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
        String name = MapUtils.getString(params, "query_name");
        RecycleSystem t = new RecycleSystem();
        t.setName(name);
        
        String idStr = MapUtils.getString(params, "ids");
        if(StringUtils.isNotBlank(idStr)) {
        	String[] ids = StringUtils.split(idStr, ",");
        	t.setQueryIds(Arrays.asList(ids));
        }
        
        List list = queryList(t);

        Map<String,Object> map = new HashMap<String,Object>();
        map.put("list", list);
            
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