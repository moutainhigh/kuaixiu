package com.kuaixiu.sjUser.entity;

import java.util.List;

/**
 * Session中保存用户对象.
 * 
 * @CreateDate: 2016-8-23 下午8:11:57
 * @version: V 1.0
 */
public class SjSessionUser {

    //用户id
    private String userId;
    //员工的名称
    private String userName;
    //公司id
    private String companyId;
    //公司名字
    private String companyName;
    //用户类型
    private int type;
    /**
     * 用户角色
     */
    private List<Role> roleList;
    /**
     * 用户权限用于权限认证判断
     */
    private List<Menu> userAuthoritys;
    /**
     * 用户菜单用于前台页面菜单展示
     */
    private List<Menu> menuList;
    /**
     * 用户
     */
    private SjUser user;

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

    /**
     * 给店员单独添加的一个用于在session判断中区别用户权限，该值默认为100 
     */
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }

    public List<Menu> getUserAuthoritys() {
        return userAuthoritys;
    }

    public void setUserAuthoritys(List<Menu> userAuthoritys) {
        this.userAuthoritys = userAuthoritys;
    }

    public List<Menu> getMenuList() {
        return menuList;
    }

    public void setMenuList(List<Menu> menuList) {
        this.menuList = menuList;
    }

    public SjUser getUser() {
        return user;
    }

    public void setUser(SjUser user) {
        this.user = user;
    }
}
