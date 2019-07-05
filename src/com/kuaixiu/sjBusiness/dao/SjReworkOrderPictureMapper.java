package com.kuaixiu.sjBusiness.dao;

import com.common.base.dao.BaseDao;

import java.util.List;

/**
 * SjReworkOrderPicture Mapper
 *
 * @param <T>
 * @CreateDate: 2019-07-02 上午10:33:28
 * @version: V 1.0
 */
public interface SjReworkOrderPictureMapper<T> extends BaseDao<T> {

    List<T> queryByReworkNo(String reworkNo);
}


