package com.kuaixiu.screen.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.common.base.dao.BaseDao;
import com.common.base.service.BaseService;
import com.common.exception.SystemException;
import com.kuaixiu.brand.entity.NewBrand;
import com.kuaixiu.screen.dao.ScreenBrandMapper;
import com.kuaixiu.screen.entity.ScreenBrand;

/**
* @author: anson
* @CreateDate: 2017年10月17日 下午7:37:21
* @version: V 1.0
* 
*/
@Service("screenBrandService")
public class ScreenBrandService extends BaseService<ScreenBrand>{

	@Autowired
	private ScreenBrandMapper mapper;

	@Override
	public ScreenBrandMapper<ScreenBrand> getDao() {
		// TODO Auto-generated method stub
		return mapper;
	}
	

	/**
     * 保存品牌
     * @param t
     * @CreateDate: 2016-9-29 下午8:03:11
     */
    @Transactional
    public void save(ScreenBrand t){
        //检查品牌名称是否存在
        if(checkBrandName(t.getName())){
            throw new SystemException("该品牌名称已存在");
        }
        getDao().add(t);
    }
    
    
    
    
    /**
     * 检查品牌名称是否存在
     * @param name
     * @return
     * @CreateDate: 2016-9-29 下午8:00:15
     */
    public boolean checkBrandName(String name){
        List<ScreenBrand> list = getDao().queryByName(name);
        return list != null && list.size() > 0; 
    }
}
