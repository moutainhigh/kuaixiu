package com.kuaixiu.brand.service;


import com.common.base.service.BaseService;
import com.common.exception.SystemException;
import com.kuaixiu.brand.dao.BrandMapper;
import com.kuaixiu.brand.entity.Brand;
import com.system.basic.user.entity.SessionUser;

import jodd.util.StringUtil;
import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.XLSTransformer;

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

/**
 * Brand Service
 * @CreateDate: 2016-08-25 上午03:57:21
 * @version: V 1.0
 */
@Service("brandService")
public class BrandService extends BaseService<Brand> {
    private static final Logger log= Logger.getLogger(BrandService.class);

    @Autowired
    private BrandMapper<Brand> mapper;


    public BrandMapper<Brand> getDao() {
        return mapper;
    }

    //**********自定义方法***********

    /**
     * 根据名称查询品牌
     * @param name
     * @return
     * @CreateDate: 2016-9-29 下午7:57:25
     */
    public List<Brand> queryByName(String name){
        return getDao().queryByName(name);
    }
    
    /**
     * 检查品牌名称是否存在
     * @param name
     * @return
     * @CreateDate: 2016-9-29 下午8:00:15
     */
    public boolean checkBrandName(String name){
        List<Brand> list = getDao().queryByName(name);
        return list != null && list.size() > 0; 
    }
    
    /**
     * 保存品牌
     * @param t
     * @CreateDate: 2016-9-29 下午8:03:11
     */
    @Transactional
    public void save(Brand t){
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
    public int update(Brand b){
        if(b == null || StringUtils.isBlank(b.getId())){
            throw new SystemException("参数为空，无法更新");
        }
        Brand t = getDao().queryById(b.getId());
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
        if(StringUtil.isNotBlank(b.getLogo())){
        	t.setLogo(b.getLogo());
        }
        return getDao().update(t);
    }
    
    /**
     * 删除维修品牌
     * @param p
     * @return
     * @CreateDate: 2016-8-31 下午7:09:59
     */
    @Transactional
    public int delete(Brand b){
        if(b == null || StringUtils.isBlank(b.getId())){
            throw new SystemException("参数为空，无法更新");
        }
        Brand t = getDao().queryById(b.getId());
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
        	Brand t = getDao().queryById(id);
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