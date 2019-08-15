package com.system.basic.user.service;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.common.base.service.BaseService;
import com.system.basic.user.entity.SysMenu;
import com.system.basic.user.dao.SysMenuMapper;

/**
 * SysMenu Service
 *
 * @CreateDate: 2016-08-26 下午10:26:05
 * @version: V 1.0
 */
@Service("sysMenuService")
public class SysMenuService extends BaseService<SysMenu> {
    private static final Logger log = Logger.getLogger(SysMenuService.class);

    @Autowired
    private SysMenuMapper<SysMenu> mapper;


    public SysMenuMapper<SysMenu> getDao() {
        return mapper;
    }

    //**********自定义方法***********

    /**
     * 根据用户id查询用户菜单
     *
     * @param uid
     * @return
     * @CreateDate: 2016-8-27 上午1:01:20
     */
    public List<SysMenu> queryMenusByUserId(String uid) {
        return getDao().queryMenusByUserId(uid);
    }

    /**
     * 根据角色id查询角色菜单
     *
     * @param roleId
     * @return
     * @CreateDate: 2016-8-27 上午1:01:20
     */
    public List<SysMenu> queryMenusByRoleId(String roleId) {
        return getDao().queryMenusByRoleId(roleId);
    }

    /**
     * 根据用权限获取用户菜单
     *
     * @param authoritys 用户权限
     * @return
     * @CreateDate: 2016-8-27 上午1:51:00
     */
    public List<SysMenu> dealSysMenusByUserAuthoritys(List<SysMenu> authoritys) {
        List<SysMenu> rootMenu = new ArrayList<SysMenu>();
        if (authoritys == null || authoritys.size() == 0) {
            return rootMenu;
        }
        for (SysMenu m : authoritys) {
            List<SysMenu> subMenuList = getSubMenuListByPcode(authoritys, m.getCode());
            if (subMenuList != null && subMenuList.size() > 0) {
                m.setSubMenuList(subMenuList);
            }
            if (m.getType() == 2 && m.getIsShow() == 1) {
                //降三级菜单添加到二级菜单列表
                rootMenu.add(m);
            }
        }

        SysMenu menu = new SysMenu();
        menu.setType(1);
        menu.setIsShow(1);
        menu.setPcode("0");
        List<SysMenu> menuses = this.getDao().queryList(menu);
        Iterator<SysMenu> it = menuses.iterator();
        while (it.hasNext()) {
            SysMenu str = it.next();
            List<SysMenu> SysMenus=new ArrayList<>();
            for (SysMenu sysMenu : rootMenu) {
                if (str.getCode().equals(sysMenu.getPcode()) && sysMenu.getIsShow() == 1) {
                    SysMenus.add(sysMenu);
                }
            }
            //降二级菜单添加到一级菜单列表
            str.setSubMenuList(SysMenus);
            if (CollectionUtils.isEmpty(str.getSubMenuList())) {
                it.remove();
            }
        }
        System.out.println("返回菜单："+ JSONObject.toJSONString(menuses));
        return menuses;
    }

    public List<SysMenu> getSysMenu(String userId) {
        SysMenu menu = new SysMenu();
        menu.setType(1);
        menu.setIsShow(1);
        menu.setPcode("101");
        menu.setUserId(userId);
        List<SysMenu> menuses = this.getDao().queryMenusByUserIdType(menu);
        for (SysMenu menu0 : menuses) {
            menu.setPcode(menu0.getCode());
            menu.setType(2);
            List<SysMenu> menus = this.getDao().queryMenusByUserIdType(menu);
            menu0.setSubMenuList(menus);
            for (SysMenu menu1 : menus) {
                menu.setType(3);
                menu.setPcode(menu1.getCode());
                List<SysMenu> menus1 = this.getDao().queryMenusByUserIdType(menu);
                menu1.setSubMenuList(menus1);
            }
        }
        return menuses;
    }

    /**
     * 根据父菜单编号获取子菜单
     *
     * @param menus
     * @param pcode
     * @return
     * @CreateDate: 2016-8-27 上午1:40:29
     */
    private List<SysMenu> getSubMenuListByPcode(List<SysMenu> menus, String pcode) {
        List<SysMenu> list = new ArrayList<SysMenu>();
        for (SysMenu m : menus) {
            if (pcode.equals(m.getPcode()) && m.getIsShow() == 1) {
                list.add(m);
            }
        }
        return list;
    }
}