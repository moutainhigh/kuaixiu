package com.system.basic.user.dao;

import java.util.List;

import com.common.base.dao.BaseDao;

/**
* @author: anson
* @CreateDate: 2017年9月4日 下午5:20:31
* @version: V 1.0
* 
*/
public interface LoginUserMapper<T> extends BaseDao<T> {
     /**
      * @param accessToken
      * @return
      */
	 T queryByToken(String accessToken);
	 /**
	  * @param loginId
	  * @return
	  */
	 T queryByLoginId(String loginId);
	 /**
	  * @param openId
	  * @return
	  */
	 T queryByOpenId(String openId);
	 /**
	  * 根据sessionId 查找
	  */
	 List<T> queryBysessionId(String sessionId);
}
