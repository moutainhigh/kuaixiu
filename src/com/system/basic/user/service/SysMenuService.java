package com.system.basic.user.service;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.common.base.service.BaseService;
import com.system.basic.user.entity.SysMenu;
import com.system.basic.user.dao.SysMenuMapper;

/**
 * SysMenu Service
 * @CreateDate: 2016-08-26 下午10:26:05
 * @version: V 1.0
 */
@Service("sysMenuService")
public class SysMenuService extends BaseService<SysMenu> {
    private static final Logger log= Logger.getLogger(SysMenuService.class);

    @Autowired
    private SysMenuMapper<SysMenu> mapper;


    public SysMenuMapper<SysMenu> getDao() {
        return mapper;
    }

    //**********自定义方法***********

    /**
     * 根据用户id查询用户菜单
     * @param uid
     * @return
     * @CreateDate: 2016-8-27 上午1:01:20
     */
    public List<SysMenu> queryMenusByUserId(String uid){
        return getDao().queryMenusByUserId(uid);
    }
    
    /**
     * 根据角色id查询角色菜单
     * @param uid
     * @return
     * @CreateDate: 2016-8-27 上午1:01:20
     */
    public List<SysMenu> queryMenusByRoleId(String roleId){
        return getDao().queryMenusByRoleId(roleId);
    }
    
    /**
     * 根据用权限获取用户菜单
     * @param authoritys 用户权限
     * @return
     * @CreateDate: 2016-8-27 上午1:51:00
     */
    public List<SysMenu> dealSysMenusByUserAuthoritys(List<SysMenu> authoritys){
        List<SysMenu> rootMenu = new ArrayList<SysMenu>();
        if (authoritys == null || authoritys.size() == 0) {
            return rootMenu;
        }
        for (SysMenu m : authoritys) {
            List<SysMenu> subMenuList = getSubMenuListByPcode(authoritys, m.getCode());
            if(subMenuList != null && subMenuList.size() > 0){
                m.setSubMenuList(subMenuList);
            }
            if(m.getType() == 2 && m.getIsShow() == 1){
                //将顶级菜单添加到菜单列表
                rootMenu.add(m);
            }
        }
        return rootMenu;
    }
    
    /**
     * 根据父菜单编号获取子菜单
     * @param menus
     * @param pcode
     * @return
     * @CreateDate: 2016-8-27 上午1:40:29
     */
    private List<SysMenu> getSubMenuListByPcode(List<SysMenu> menus, String pcode){
        List<SysMenu> list = new ArrayList<SysMenu>();
        for (SysMenu m : menus) {
            if (pcode.equals(m.getPcode()) && m.getIsShow() == 1) {
                list.add(m);
            }
        }
        return list;
    }
}