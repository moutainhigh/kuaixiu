package com.kuaixiu.join.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.common.base.dao.BaseDao;
import com.common.base.service.BaseService;
import com.common.exception.SystemException;
import com.kuaixiu.brand.entity.Brand;
import com.kuaixiu.join.dao.JoinNewsMapper;
import com.kuaixiu.join.entity.JoinNews;
import com.system.basic.user.entity.SessionUser;

/**
 * @author: anson
 * @CreateDate: 2018年3月1日 下午2:57:12
 * @version: V 1.0
 * 
 */
@Service("joinService")
public class JoinNewsService extends BaseService<JoinNews> {

	@Autowired
	private JoinNewsMapper<JoinNews> mapper;

	@Override
	public JoinNewsMapper<JoinNews> getDao() {
		// TODO Auto-generated method stub
		return mapper;
	}

	/**
	 * 通过手机号查询
	 * 
	 * @return
	 */
	public JoinNews queryByMobile(String mobile) {
		return getDao().queryByMobile(mobile);
	}

	/**
	 * 删除
	 * @param idStr
	 * @param su
	 * @return
	 */
	@Transactional
	public int deleteById(String idStr, SessionUser su) {
		if (StringUtils.isBlank(idStr)) {
			throw new SystemException("参数为空，无法更新");
		}
		// 处理批量操作
		String[] ids = idStr.split(",");
		for (String id : ids) {
			JoinNews t = getDao().queryById(id);
			t.setIsDel(1);
			t.setUpdateUserid(su.getUserId());
			getDao().update(t);
		}
		return 1;
	}

}
