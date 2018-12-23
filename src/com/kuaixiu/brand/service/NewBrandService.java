package com.kuaixiu.brand.service;

import java.io.IOException;
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
import com.kuaixiu.brand.dao.NewBrandMapper;
import com.kuaixiu.brand.entity.Brand;
import com.kuaixiu.brand.entity.NewBrand;
import com.system.basic.user.entity.SessionUser;

import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.XLSTransformer;

/**
* @author: anson
* @CreateDate: 2017年6月13日 下午4:15:17
* @version: V 1.0
* 
*/
@Service("newBrandService")
public class NewBrandService extends BaseService{
	private static final Logger log= Logger.getLogger(NewBrandService.class);
	
	@Autowired
	private NewBrandMapper<NewBrand> mapper;
	
	public NewBrandMapper<NewBrand> getDao() {
		// TODO Auto-generated method stub
		return mapper;
	}

	
	 /**
     * 根据名称查询品牌
     * @param name
     * @return
     * @CreateDate: 2016-9-29 下午7:57:25
     */
    public List<NewBrand> queryByName(String name){
        return getDao().queryByName(name);
    }
    
    /**
     * 检查品牌名称是否存在
     * @param name
     * @return
     * @CreateDate: 2016-9-29 下午8:00:15
     */
    public boolean checkBrandName(String name){
        List<NewBrand> list = getDao().queryByName(name);
        return list != null && list.size() > 0; 
    }
    
    /**
     * 保存品牌
     * @param t
     * @CreateDate: 2016-9-29 下午8:03:11
     */
    @Transactional
    public void save(NewBrand t){
        //检查品牌名称是否存在
        if(checkBrandName(t.getName())){
            throw new SystemException("该品牌名称已存在");
        }
        getDao().add(t);
    }
    
    /**
     * 更新维修品牌
     * @param p
     * @return
     * @CreateDate: 2016-8-31 下午7:08:33
     */
    @Transactional
    public int update(NewBrand b){
        if(b == null || StringUtils.isBlank(b.getId())){
            throw new SystemException("参数为空，无法更新");
        }
        NewBrand t = getDao().queryById(b.getId());
        if(t == null){
            throw new SystemException("品牌未找到，无法更新");
        }
        if(!t.getName().equals(b.getName())){
            //检查品牌名称是否存在
            if(checkBrandName(b.getName())){
                throw new SystemException("该品牌名称已存在");
            }
        }
        t.setName(b.getName());
        t.setSort(b.getSort());
        t.setUpdateUserid(t.getUpdateUserid());
        return getDao().update(t);
    }
    
    /**
     * 删除维修品牌
     * @param p
     * @return
     * @CreateDate: 2016-8-31 下午7:09:59
     */
    @Transactional
    public int delete(NewBrand b){
        if(b == null || StringUtils.isBlank(b.getId())){
            throw new SystemException("参数为空，无法更新");
        }
        NewBrand t = getDao().queryById(b.getId());
        t.setIsDel(1);
        t.setUpdateUserid(b.getUpdateUserid());
        return getDao().update(t);
    }
    
    /**
     * 删除维修品牌
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
        	NewBrand t = getDao().queryById(id);
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
        Brand t = new Brand();
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
