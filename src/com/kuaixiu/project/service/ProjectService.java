package com.kuaixiu.project.service;

import com.common.base.service.BaseService;
import com.common.exception.SystemException;
import com.kuaixiu.project.dao.ProjectMapper;
import com.kuaixiu.project.entity.Project;
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
* Project Service
* @author: lijx
*/
@Service("projectService")
public class ProjectService extends BaseService<Project> {
    private static final Logger log= Logger.getLogger(ProjectService.class);

    @Autowired
    private ProjectMapper<Project> mapper;


    public ProjectMapper<Project> getDao() {
        return mapper;
    }

    //**********自定义方法***********
 
    /**
     * 根据名称查询项目
     * @param name
     * @return
     * @CreateDate: 2016-9-29 下午7:57:25
     */
    public List<Project> queryByName(String name){
        return getDao().queryByName(name);
    }
    
    /**
     * 检查项目名称是否存在
     * @param name
     * @return
     * @CreateDate: 2016-9-29 下午8:00:15
     */
    public boolean checkProjectName(String name){
        List<Project> list = getDao().queryByName(name);
        return list != null && list.size() > 0; 
    }
    
    /**
     * 保存项目
     * @param t
     * @CreateDate: 2016-9-29 下午8:03:11
     */
    @Transactional
    public void save(Project t){
        //检查项目名称是否存在
        if(checkProjectName(t.getName())){
            throw new SystemException("该维修项目名称已存在");
        }
        getDao().add(t);
    }
    
    /**
     * 更新维修项目
     * @param p
     * @return
     * @CreateDate: 2016-8-31 下午7:08:33
     */
    @Transactional
    public int update(Project p){
        if(p == null || StringUtils.isBlank(p.getId())){
            throw new SystemException("参数为空，无法更新");
        }
        Project project = getDao().queryById(p.getId());
        if(project == null){
            throw new SystemException("维修项目未找到，无法更新");
        }
        if(!project.getName().equals(p.getName())){
            //检查项目名称是否存在
            if(checkProjectName(p.getName())){
                throw new SystemException("该维修项目名称已存在");
            }
        }
        project.setName(p.getName());
        project.setUpdateUserid(p.getUpdateUserid());
        return getDao().update(project);
    }
    
    /**
     * 删除维修项目
     * @param p
     * @return
     * @CreateDate: 2016-8-31 下午7:09:59
     */
    @Transactional
    public int delete(Project p){
        if(p == null || StringUtils.isBlank(p.getId())){
            throw new SystemException("参数为空，无法更新");
        }
        Project project = getDao().queryById(p.getId());
        project.setIsDel(1);
        project.setUpdateUserid(p.getUpdateUserid());
        return getDao().update(project);
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
            Project project = getDao().queryById(id);
            project.setIsDel(1);
            project.setUpdateUserid(su.getUserId());
            getDao().update(project);
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
        Project project = new Project();
        project.setName(name);
        
        String idStr = MapUtils.getString(params, "ids");
        if(StringUtils.isNotBlank(idStr)) {
        	String[] ids = StringUtils.split(idStr, ",");
        	project.setQueryIds(Arrays.asList(ids));
        }
        
        List list = queryList(project);

        Map<String,Object> map = new HashMap<String,Object>();
        map.put("projectList", list);
            
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