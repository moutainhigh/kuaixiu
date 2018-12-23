package com.kuaixiu.oldtonew.dao;

import java.util.List;

import com.common.base.dao.BaseDao;
import com.kuaixiu.oldtonew.entity.OldToNewUser;

public interface OldToNewMapper<T> extends BaseDao<T> {
    /**
     * 添加以旧换新信息
     */
	public void addNews(OldToNewUser user);
}
