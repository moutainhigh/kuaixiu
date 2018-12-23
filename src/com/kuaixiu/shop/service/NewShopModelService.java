package com.kuaixiu.shop.service;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.common.base.dao.BaseDao;
import com.common.base.service.BaseService;
import com.kuaixiu.shop.dao.NewShopModelMapper;
import com.kuaixiu.shop.entity.NewShopModel;

/**
* @author: anson
* @CreateDate: 2017年6月15日 下午12:02:43
* @version: V 1.0
* 
*/
@Service("newShopModelService")
public class NewShopModelService extends BaseService{
    private static final Logger log=Logger.getLogger(NewShopModelService.class);
	
	@Autowired
	private NewShopModelMapper<NewShopModel> mapper;
	
	public NewShopModelMapper<NewShopModel> getDao() {
       
		return mapper;
	}

	
	/**
     * 批量添加品牌
     */
    public int addBatch(Map<String, Object> params){
    	return getDao().addBatch(params);
    }

    /**
     * 根据商店code删除品牌
     */
    public int deleteByShopCode(String shopCode){
    	return getDao().deleteByShopCode(shopCode);
    }
	
}
