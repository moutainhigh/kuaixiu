package com.kuaixiu.sjUser.service;


import com.common.base.service.BaseService;
import com.kuaixiu.sjUser.dao.MenuMapper;
import com.kuaixiu.sjUser.entity.Menu;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Menu Service
 * @CreateDate: 2019-05-06 上午10:47:20
 * @version: V 1.0
 */
@Service("menuService")
public class MenuService extends BaseService<Menu> {
    private static final Logger log= Logger.getLogger(MenuService.class);

    @Autowired
    private MenuMapper<Menu> mapper;


    public MenuMapper<Menu> getDao() {
        return mapper;
    }

    //**********自定义方法***********
    /**
     * 根据用户id查询用户菜单
     * @param uid
     * @return
     * @CreateDate: 2016-8-27 上午1:01:20
     */
    public List<Menu> queryMenusByUserId(String uid){
        return getDao().queryMenusByUserId(uid);
    }

    /**
     * 根据用权限获取用户菜单
     * @param authoritys 用户权限
     * @return
     * @CreateDate: 2016-8-27 上午1:51:00
     */
    public List<Menu> dealSysMenusByUserAuthoritys(List<Menu> authoritys){
        List<Menu> Menu = new ArrayList<Menu>();
        if (authoritys == null || authoritys.size() == 0) {
            return Menu;
        }
        for (Menu m : authoritys) {
            List<Menu> subMenuList = getSubMenuListByPcode(authoritys, m.getCode());
            if(subMenuList != null && subMenuList.size() > 0){
                m.setSubMenuList(subMenuList);
            }
            if(m.getType() == 2 && m.getIsShow() == 1){
                //将顶级菜单添加到菜单列表
                Menu.add(m);
            }
        }
        return Menu;
    }

    /**
     * 根据父菜单编号获取子菜单
     * @param menus
     * @param pcode
     * @return
     * @CreateDate: 2016-8-27 上午1:40:29
     */
    private List<Menu> getSubMenuListByPcode(List<Menu> menus, String pcode){
        List<Menu> list = new ArrayList<Menu>();
        for (Menu m : menus) {
            if (pcode.equals(m.getPcode()) && m.getIsShow() == 1) {
                list.add(m);
            }
        }
        return list;
    }
}