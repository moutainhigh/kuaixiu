package com.kuaixiu.clerk.service;

import com.common.base.service.BaseService;
import com.common.exception.SystemException;
import com.common.util.MD5Util;
import com.common.util.SmsSendUtil;
import com.kuaixiu.clerk.dao.ClerkMapper;
import com.kuaixiu.clerk.entity.Clerk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author: anson
* @CreateDate: 2017年5月17日 下午3:48:38
* @version: V 1.0
* 
*/
@Service("clerkService")
public class ClerkService extends BaseService<Clerk>{
    
	@Autowired
	private ClerkMapper<Clerk> mapper;
	
	
	public ClerkMapper<Clerk> getDao() {
		return mapper;
	}
	
	/**
	 * 通过手机号查询店员
	 */
	public Clerk queryByTel(String tel){
		Clerk clerk=new Clerk();
	    clerk=getDao().queryByTel(tel);
	    if(clerk==null){
	    	return null;
	    }
		return clerk;
	}
	
	/**
	 * 验证登录用户
	 */
	public Clerk checkLogin(String tel,String password){
		
		
		return null;
	}

	/**
	 * 给店员增加积分
	 */
	public void addIntegralById(Clerk c){
		getDao().addIntegralById(c);
	}
	
	/**
	 * 店员忘记密码
	 */
	
	public Clerk resetPassword(Clerk clerk){
		  String newPasswd = SmsSendUtil.randomCode();
		  clerk.setCode(MD5Util.encodePassword(newPasswd));
		  getDao().update(clerk);
		  if(!SmsSendUtil.sendNewPasswd(clerk.getTel(), newPasswd)){
              throw new SystemException("系统异常，请稍后再试！");
          }
		  return clerk;
	}
	
	/**
	 * 删除登录用户
	 */
	public void deleteClerk(Clerk clerk,String updateUserId){
		clerk.setUpdateUserId(updateUserId);
		clerk.setIsDel(1);
		getDao().update(clerk);
		
	}
	
	/**
	 * 修改用户
	 */
	public void updateClerk(Clerk clerk,String updateUserId){
		clerk.setUpdateUserId(updateUserId);
		getDao().update(clerk);
	}
	
	
}
