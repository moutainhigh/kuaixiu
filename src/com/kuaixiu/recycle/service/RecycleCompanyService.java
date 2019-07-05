package com.kuaixiu.recycle.service;

import com.common.base.service.BaseService;
import com.common.exception.SystemException;
import com.kuaixiu.recycle.dao.RecycleCompanyMapper;
import com.kuaixiu.recycle.entity.RecycleCompany;
import com.kuaixiu.recycle.entity.RecycleCompanyNews;
import com.system.basic.user.entity.SessionUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author: anson
* @CreateDate: 2018年3月13日 下午5:21:41
* @version: V 1.0
* 
*/
@Service("recycleCompanyService")
public class RecycleCompanyService extends BaseService<RecycleCompany>{

	@Autowired
	private RecycleCompanyMapper<RecycleCompany> mapper;
	
	@Override
	public RecycleCompanyMapper<RecycleCompany> getDao() {

		return mapper;
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
        	RecycleCompany t = getDao().queryById(id);
            t.setIsDel(1);
            getDao().update(t);
        }
        return 1;
    }
    
    /**
     * 增加企业对应回收信息
     * @param t
     * @return
     */
    public int addCompanyNews(RecycleCompanyNews t){
    	return getDao().insertCompany(t);
    }
    
    /**
	 * 根据企业id查询对应回收信息 
	 * @param companyId
	 * @return
	 */
    public List<RecycleCompanyNews> queryCompanyNews(String companyId){
    	return  getDao().queryCompanyNewsList(companyId);
    }

}
