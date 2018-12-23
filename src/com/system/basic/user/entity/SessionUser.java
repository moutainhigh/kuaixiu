package com.system.basic.user.entity;

import java.util.List;

/**
 * Session中保存用户对象.
 * 
 * @CreateDate: 2016-8-23 下午8:11:57
 * @version: V 1.0
 */
public class SessionUser {

    //用户id
    private String userId;
    //员工的名称
    private String userName;
    //网点账号
    private String shopCode;
    //网点名称
    private String shopName;
    //连锁商账号
    private String providerCode;
    //连锁商吗
    private String providerName;
    //用户类型
    private int type;
    //登录用户
    private SysUser user;
    
    /**
     * 用户角色
     */
    private List<SysRole> sysRoleList;
    
    /**
     * 用户权限用于权限认证判断
     */
    private List<SysMenu> userAuthoritys;
    
    /**
     * 用户菜单用于前台页面菜单展示
     */
    private List<SysMenu> sysMenuList;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getProviderCode() {
        return providerCode;
    }

    public void setProviderCode(String providerCode) {
        this.providerCode = providerCode;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }
    /**
     * 给店员单独添加的一个用于在session判断中区别用户权限，该值默认为100 
     */
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<SysRole> getSysRoleList() {
        return sysRoleList;
    }

    public void setSysUserRoleList(List<SysRole> sysRoleList) {
        this.sysRoleList = sysRoleList;
    }

    /**
     * 用户菜单用于前台页面菜单展示
     * @return
     * @CreateDate: 2016-8-27 上午1:25:29
     */
    public List<SysMenu> getSysMenuList() {
        return sysMenuList;
    }

    public void setSysMenuList(List<SysMenu> sysMenuList) {
        this.sysMenuList = sysMenuList;
    }

    /**
     * 用户权限用于权限认证判断
     * @return
     * @CreateDate: 2016-8-27 上午1:25:11
     */
    public List<SysMenu> getUserAuthoritys() {
        return userAuthoritys;
    }

    public void setUserAuthoritys(List<SysMenu> userAuthoritys) {
        this.userAuthoritys = userAuthoritys;
    }

    public SysUser getUser() {
        return user;
    }

    public void setUser(SysUser user) {
        this.user = user;
    }

    
}
