package com.system.basic.user.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.common.base.dao.BaseDao;
import com.common.base.service.BaseService;
import com.common.util.DateUtil;
import com.common.util.MD5Util;
import com.system.basic.user.dao.LoginUserMapper;
import com.system.basic.user.entity.LoginUser;
import com.system.basic.user.entity.SysUser;
import com.system.constant.SystemConstant;
import com.system.util.SystemUtil;

/**
* @author: anson
* @CreateDate: 2017年9月5日 上午9:07:08
* @version: V 1.0
* 
*/
@Service("loginUserService")
public class LoginUserService extends BaseService{
	private static final Logger log= Logger.getLogger(LoginUserService.class);
	@Autowired
	private LoginUserMapper<LoginUser> mapper;
	@Autowired
	private SessionUserService sessionUserService;
	@Override
	public LoginUserMapper<LoginUser> getDao() {
		return mapper;
	}
	
	
	/**
	 * 新添用户
	 */
    public void addLoginUser(String mobile){
    	
    }
    
    /**
	 * 手机其他端（排除微信端）初始化用户
	 */
    public LoginUser pcInitLoginUser(SysUser user,String sessionId){
    	//查看该用户是否有登录记录
    	LoginUser login=getDao().queryByLoginId(user.getLoginId());
    	if(login!=null){
    		//更新用户
       		login.setLoginType(1);
       		login.setAccessToken(createAccessToken(login.getLoginId()));
   			login.setUid(user.getUid());
   			login.setLoginId(user.getLoginId());
   			login.setOpenId(null);
   			login.setSessionId(sessionId);
       		getDao().update(login);
    	}else{
    		 LoginUser l=new LoginUser();
    		//新增用户
    		 l.setId(UUID.randomUUID().toString());
 		     l.setUid(user.getUid());//用户业务id  
    		 l.setLoginId(user.getLoginId());
    	     l.setAccessToken(createAccessToken(user.getLoginId()));   
    	     l.setIndate(Long.parseLong(SystemUtil.getSysCfgProperty(SystemConstant.ACCESS_TOKEN_INDATE)));   
    	     l.setUserType(user.getType());
    	     l.setLoginType(1);
    	     l.setSessionId(sessionId);
    	     getDao().add(l);
    	}
    	return getDao().queryByLoginId(user.getLoginId());
    }
    
    /**
   	 * 微信端初始化
   	 */
       public LoginUser wechatInitLoginUser(SysUser user,String openId,String sessionId){
       	//查看该用户是否有登录记录  只要该openId有记录或者用户名有记录即判断存在
        LoginUser login=getDao().queryByLoginId(user.getLoginId());
        LoginUser loginByOpenId=getDao().queryByOpenId(openId);
        if(login!=null&&loginByOpenId!=null){
        	 if(!loginByOpenId.getLoginId().equals(user.getLoginId())){
    		    	//不是同一个用户则删除   loginByOpenId
    		    	getDao().delete(loginByOpenId.getId());
    		    }
        	    //更新记录
        	   login.setAccessToken(createAccessToken(user.getLoginId()));
  			   login.setUid(user.getUid());
  			   login.setLoginType(0);
  			   login.setLoginId(user.getLoginId());
  			   login.setOpenId(openId);
  			   login.setSessionId(sessionId);
  			   getDao().update(login);
        }
        //如果根据openId查找的用户存在 但目前登录用户未曾有过登录记录 
        else if(loginByOpenId!=null){
       		    loginByOpenId.setAccessToken(createAccessToken(user.getLoginId()));
       		    loginByOpenId.setUid(user.getUid());
       		    loginByOpenId.setLoginType(0);
       		    loginByOpenId.setLoginId(user.getLoginId());
       		    loginByOpenId.setOpenId(openId);
       		    loginByOpenId.setSessionId(sessionId);
       			getDao().update(loginByOpenId);
       	}else if(login!=null){//更新用户
       		   login.setAccessToken(createAccessToken(user.getLoginId()));
   			   login.setUid(user.getUid());
   			   login.setLoginType(0);
   			   login.setLoginId(user.getLoginId());
   			   login.setOpenId(openId);
   			   login.setSessionId(sessionId);
   			   getDao().update(login);
       	}else{
       		 //新增用户
         	 LoginUser l=new LoginUser();
         	 l.setId(UUID.randomUUID().toString());
 		     l.setUid(user.getUid());//用户业务id  
 		     l.setLoginId(user.getLoginId());
 	         l.setAccessToken(createAccessToken(user.getLoginId()));   
 	         l.setIndate(Long.parseLong(SystemUtil.getSysCfgProperty(SystemConstant.ACCESS_TOKEN_INDATE)));   
 	         l.setUserType(user.getType());
 	         l.setLoginType(0);
 	         l.setOpenId(openId);	
 	         l.setSessionId(sessionId);
       	     getDao().add(l);
       	}
       	return getDao().queryByLoginId(user.getLoginId());
       }
       
    /**
     * 自定义accessToken 
     */
    public String createAccessToken(String mobile){
    	    String nowDate = DateUtil.getNowyyyyMMddHHmmssSSS();
	        String plainText = mobile + nowDate ;
	        String accessToken = MD5Util.md5Encode(plainText, SystemUtil.getSysCfgProperty(SystemConstant.ACCESS_TOKEN_SALT_KEY));
       return accessToken;
    }

    /**
     * 修改用户
     */
	public int updateLoginUser(LoginUser l){
		 return getDao().update(l);
	}
	
	/**
	 * 根据id查找用户
	 */
    public LoginUser findLoginUserById(String id){
        return getDao().queryById(id);    	
    }
    
    /**
     * 根据accessToken查找 不考虑该记录是否已删除
     */
    public LoginUser findLoginUserByToken(String token){
        return getDao().queryByToken(token);    	
    }
    
    /**
     * 根据loginId查找  不考虑该记录是否已删除
     */
    public LoginUser findLoginUserByLoginId(String loginId){
        return getDao().queryByLoginId(loginId);    	
    }
    
    
    
    /**
     * 根据openId查找  不考虑该记录是否已删除
     */
    public LoginUser findLoginUserByOpenId(String openId){
        return getDao().queryByOpenId(openId);    	
    }
    
    /**
     *  根据openId查找token在有效期内的用户
     */
    public LoginUser findIndateLoginUserByOpenId(LoginUser login){
         
    	return null;
    }
    
    /**
     *  根据sessionId查找用户
     */
    public List<LoginUser> findLoginUserBysessionId(String sessionId){
        List<LoginUser> login=getDao().queryBysessionId(sessionId);
    	return login;
    }
    
    /**
     * 根据查询记录判断用户的accessToken是否已过期  且该用户记录登录方式为微信登录 
     */
	public Boolean findLoginUserInDate(LoginUser l){
		if(l==null||l.getLoginType()==1){
			return false;
		}
		//自定义的token有效期  单位秒
		long inDate=Long.parseLong(SystemUtil.getSysCfgProperty(SystemConstant.ACCESS_TOKEN_INDATE));
		long upDate=l.getUpdateTime().getTime();
		long time=System.currentTimeMillis()-upDate;  //当前时间和用户token更新时间的差
        System.out.println("判断token是否有效："+upDate+"  当前时间："+time);
		if(time<inDate*1000||time==inDate*1000){
        	return true;
        }else{
        	return false;
        }
	}
	
	public static void main(String[] args) {
		long inDate=Long.parseLong(SystemUtil.getSysCfgProperty(SystemConstant.ACCESS_TOKEN_INDATE));
		Date d=new Date();
		System.out.println(d);
		System.out.println(d.getTime());
		System.out.println(System.currentTimeMillis());
	}
	
    
}
