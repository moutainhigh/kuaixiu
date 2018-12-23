package com.kuaixiu.model.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.dao.BaseDao;
import com.common.base.service.BaseService;
import com.common.exception.SystemException;
import com.kuaixiu.model.dao.NewModelMapper;
import com.kuaixiu.model.entity.Model;
import com.kuaixiu.model.entity.NewModel;
import com.kuaixiu.model.entity.RepairCost;
import com.system.basic.user.entity.SessionUser;
;

/**
* @author: anson
* @CreateDate: 2017年6月13日 下午5:08:42
* @version: V 1.0
* 
*/
@Service("newModelService")
public class NewModelService extends BaseService<NewModel>{
    private static final Logger log=Logger.getLogger(NewModelService.class); 
	
	@Autowired
	private NewModelMapper<NewModel> mapper;
	@Override
	public NewModelMapper<NewModel> getDao() {
		return mapper;
	}
	
	
	  /**
     * 根据品牌查询机型
     */
    public List<NewModel> queryByBrandId(String brandId){
        NewModel m = new NewModel();
        m.setBrandId(brandId);
        return getDao().queryList(m);
    }
    
    /**
     *  根据品牌查询已上架的机型
     */
    public List<NewModel> queryPutawayBrandId(String brandId){
        NewModel m = new NewModel();
        m.setBrandId(brandId);
        m.setIsPutaway(1);
        return getDao().queryList(m);
    }
    
    /**
     * 验证机型名称是否存在
     */
    public boolean checkModelName(String name, String brandId){
        List<NewModel> list = getDao().queryByName(name, brandId);
        return list != null && list.size() > 0;
    }
    
    
    /**
     * 保存机型
     */
    @Transactional
    public int save(NewModel m,SessionUser su){
        //生成id
        String id = UUID.randomUUID().toString();
        m.setId(id);
        //一种机型要保证品牌,机型名称,内存,网络类型,价格唯一
        NewModel newModel=new NewModel();
        newModel.setBrandId(m.getBrandId());
        newModel.setName(m.getName());
        newModel.setMemory(m.getMemory());
        newModel.setEdition(m.getEdition());
        NewModel n=getDao().findByModel(newModel);
        if(n!=null){
        	 throw new SystemException("机型品牌，名称，内存，网络类型要对应唯一价格");
        }
        //保存机型
        getDao().add(m);
        return 1;
    }
    
    /**
     *更新机型信息
     */
    @Transactional
    public int update(NewModel m, SessionUser su){
        if(m == null || StringUtils.isBlank(m.getId())){
            throw new SystemException("参数为空，无法更新");
        }
      
        NewModel model = getDao().queryById(m.getId());
        if(model == null){
            throw new SystemException("该机型未找到，无法更新");
        }
        model.setColor(m.getColor());
        model.setUpdateUserid(m.getUpdateUserid());
        model.setSort(m.getSort());
        model.setPrice(m.getPrice());
        model.setIsPutaway(m.getIsPutaway());
        
        return getDao().update(model);
    }
    
    
    /**
     * 删除机型
     */
    @Transactional
    public int deleteById(String idStr, SessionUser su){
        if(StringUtils.isBlank(idStr)){
            throw new SystemException("参数为空，无法更新");
        }
        //处理批量操作
        String[] ids = idStr.split(",");
        for(String id : ids){
            NewModel m = getDao().queryById(id);
            m.setIsDel(1);
            m.setUpdateUserid(su.getUserId());
            getDao().update(m);
        }
        return 1;
    }
    
    
    /**
     * 精确查找机型
     */
    public NewModel findByModel(NewModel m){
     NewModel  newModel=getDao().findByModel(m);
    	return newModel;
    }
    
    
    
    public static void main(String[] args) {
		String str=",黄色,白色,金色";
		 String color=str.substring(1);
	        //将颜色数组按","划分
	        List<String> colorList=new ArrayList<String>();
	        String []strs=color.split(",");
	        for(int i=0;i<strs.length;i++){
	        	colorList.add(strs[i]);
	        }
	        System.out.println(colorList.get(2));
	}
    
    /**
     * 根据品牌id查询该品牌下最大排序数
     */
    public List<NewModel> queryMaxSort(String brandId){
    	List<NewModel> modelList=getDao().queryMaxSort(brandId);
    	return modelList;
    }
    
    
}
