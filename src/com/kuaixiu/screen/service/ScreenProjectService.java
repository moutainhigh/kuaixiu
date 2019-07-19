package com.kuaixiu.screen.service;

import com.common.base.service.BaseService;
import com.common.exception.SystemException;
import com.kuaixiu.screen.dao.ScreenProjectMapper;
import com.kuaixiu.screen.entity.ScreenProject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author: anson
* @CreateDate: 2017年10月17日 下午7:37:47
* @version: V 1.0
* 
*/
@Service("screenProjectService")
public class ScreenProjectService extends BaseService<ScreenProject>{

	@Autowired
	private ScreenProjectMapper mapper;
	
	@Override
	public ScreenProjectMapper<ScreenProject> getDao() {
		// TODO Auto-generated method stub
		return mapper;
	}
	
	
	/**
     * 保存项目
     */
    @Transactional
    public void save(ScreenProject t){
        //检查项目名称是否存在
        if(checkProjectName(t.getName())){
            throw new SystemException("该维修项目名称已存在");
        }
        getDao().add(t);
    }
    
    
    
    /**
     * 检查项目名称是否存在
     * @param name
     * @return
     * @CreateDate: 2016-9-29 下午8:00:15
     */
    public boolean checkProjectName(String name){
        List<ScreenProject> list = getDao().queryByName(name);
        return list != null && list.size() > 0; 
    }

}
