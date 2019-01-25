package com.kuaixiu.role.controller;

import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.wechat.common.util.StringUtils;
import com.system.api.entity.ResultData;
import com.system.basic.user.entity.*;
import com.system.basic.user.service.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/12/12/012.
 */
@Controller
public class SysMenuRoleController extends BaseController {

    @Autowired
    private SysMenuService sysMenuService;
    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysRoleMenuService sysRoleMenuService;
    @Autowired
    private SysUserRoleService sysUserRoleService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sysMenuRole/list")
    public ModelAndView list(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {
        try {
            SysMenu menu = new SysMenu();
            menu.setType(1);
            menu.setCode("101");
            List<SysMenu> menuses = sysMenuService.queryList(menu);
            menu.setCode(null);
            for (SysMenu menu0 : menuses) {
                menu.setPcode(menu0.getCode());
                menu.setType(2);
                List<SysMenu> menus = sysMenuService.queryList(menu);
                menu0.setSubMenuList(menus);
                for (SysMenu menu1 : menus) {
                    menu.setType(3);
                    menu.setPcode(menu1.getCode());
                    List<SysMenu> menus1 = sysMenuService.queryList(menu);
                    menu1.setSubMenuList(menus1);
                    for (SysMenu menu2 : menus1) {
                        menu.setType(4);
                        menu.setPcode(menu2.getCode());
                        List<SysMenu> menus2 = sysMenuService.queryList(menu);
                        menu2.setSubMenuList(menus2);
                    }
                }
            }
            request.setAttribute("menus", menuses);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String returnView = "role/roleList";
        return new ModelAndView(returnView);
    }

    /**
     * 权限搜索弹框
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/role/search")
    public ModelAndView roleSearch(HttpServletRequest request,
                                   HttpServletResponse response) throws Exception {
        String returnView = "role/roleSearch";
        return new ModelAndView(returnView);
    }

    /**
     * 获取树状菜单列表
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sysMenu/listTree")
    @ResponseBody
    public ResultData MenuListTree(HttpServletRequest request,
                                   HttpServletResponse response) {
        ResultData result = new ResultData();
        try {
            String roleName = request.getParameter("roleName");
            String userId = request.getParameter("userId");
            String userName = request.getParameter("userName");
            if (StringUtils.isNotBlank(roleName) && StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(userName)) {
                return getResult(result, null, false, null, "参数不能为空");
            }
            if ("".equals(roleName) && "".equals(userId) && "".equals(userName)) {
                return getResult(result, null, false, null, "参数不能为空");
            }
            SysUser user1 = new SysUser();
            SysRole role1 = new SysRole();
            List<SysMenu> menus = new ArrayList<>();
            if (StringUtils.isNotBlank(userName)) {
                SysUser user = new SysUser();
                user.setUname(userName);
                List<SysUser> users = sysUserService.queryList(user);
                if (users.size() > 1) {
                    return getResult(result, users, false, "2", "该姓名有多个账号，请用账号搜索");
                }
                if (CollectionUtils.isEmpty(users)) {
                    return getResult(result, null, false, null, "该用户不存在");
                }
                menus = sysMenuService.queryMenusByUserId(users.get(0).getLoginId());
                List<SysRole> sysRoles = sysRoleService.queryRolesByUserId(users.get(0).getLoginId());
                user1 = users.get(0);
                role1 = sysRoles.get(0);
            } else if (StringUtils.isNotBlank(userId)) {
                SysUser user = sysUserService.queryById(userId);
                if (user == null) {
                    return getResult(result, null, false, null, "该用户不存在");
                }
                menus = sysMenuService.queryMenusByUserId(userId);
                List<SysRole> sysRoles = sysRoleService.queryRolesByUserId(userId);
                user1 = user;
                role1 = sysRoles.get(0);
            } else if (StringUtils.isNotBlank(roleName)) {
                SysRole roles = sysRoleService.getDao().queryRolesByRoleName(roleName);
                if (roles == null) {
                    return getResult(result, null, false, null, "权限名称输入错误");
                }
                menus = sysMenuService.queryMenusByRoleId(roles.getId());
                role1 = roles;
            }
            JSONObject object = new JSONObject();
            String code = user1.getLoginId();//工号
            String name = user1.getUname();//姓名
            String userRole = role1.getName();//角色
            object.put("code", code);
            object.put("name", name);
            object.put("role", userRole);
            object.put("menus", menus);
            getResult(result, object, true, "0", "成功");
        } catch (Exception e) {
            e.printStackTrace();
            return getResult(result, null, false, null, "系统异常");
        }
        return result;
    }

    /**
     * 权限修改
     */
    @RequestMapping(value = "/sysMenu/update")
    @ResponseBody
    public ResultData updateRole(HttpServletRequest request,
                                 HttpServletResponse response) {
        ResultData result = new ResultData();
        try {
            String codes = request.getParameter("codes");
            String roleName = request.getParameter("roleName");
            String userId = request.getParameter("userId");
            String userName = request.getParameter("userName");
            if (StringUtils.isNotBlank(roleName) && StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(userName)) {
                return getResult(result, null, false, null, "您没有选择用户或角色");
            }
            if ("".equals(roleName) && "".equals(userId) && "".equals(userName)) {
                return getResult(result, null, false, null, "您没有选择用户或角色");
            }
            String[] menuCodes = codes.split(",");

            List<SysMenu> menus = new ArrayList<>();
            Boolean isTrue = true;
            if (StringUtils.isNotBlank(userName) || StringUtils.isNotBlank(userId)) {
                //给用户添加权限
                List<SysUser> users = new ArrayList<>();
                if (StringUtils.isNotBlank(userName)) {
                    SysUser user = new SysUser();
                    user.setUname(userName);
                    users = sysUserService.queryList(user);
                    userId = users.get(0).getLoginId();
                }
                menus = sysMenuService.queryMenusByUserId(userId);
                for (String code : menuCodes) {
                    SysMenu menu = new SysMenu();
                    menu.setCode(code);
                    menu.setUserId(userId);
                    List<SysMenu> menus1 = sysMenuService.getDao().queryMenuList(menu);
                    if (CollectionUtils.isEmpty(menus1)) {
                        //添加权限
                        SysRole role = new SysRole();
                        role.setId(userId);
                        if (StringUtils.isBlank(userName)) {
                            SysUser user = new SysUser();
                            user.setLoginId(userId);
                            users = sysUserService.queryList(user);
                            role.setName(users.get(0).getUname());
                        } else {
                            role.setName(userName);
                        }
                        //三步实现给用户添加菜单权限
                        role.setState(1);//不显示,只用于增删改查
                        List<SysRole> sysRoles = sysRoleService.queryList(role);
                        if (CollectionUtils.isEmpty(sysRoles)) {
                            sysRoleService.add(role);//1--添加新角色
                        }
                        SysUserRole userRole = new SysUserRole();
                        userRole.setUserId(userId);
                        userRole.setRoleId(role.getId());
                        List<SysUserRole> sysUserRoles = sysUserRoleService.queryList(userRole);
                        if (CollectionUtils.isEmpty(sysUserRoles)) {
                            sysUserRoleService.add(userRole);//2--用户绑定新角色
                        }
                        SysRoleMenu roleMenu = new SysRoleMenu();
                        roleMenu.setRoleId(role.getId());
                        roleMenu.setMenuCode(code);
                        sysRoleMenuService.add(roleMenu);//3--新角色赋菜单权限
                    }
                }
            } else if (StringUtils.isNotBlank(roleName)) {//给角色添加权限
                SysRole roles = sysRoleService.getDao().queryRolesByRoleName(roleName);
                menus = sysMenuService.queryMenusByRoleId(roles.getId());
                for (String code : menuCodes) {
                    SysMenu menu = new SysMenu();
                    menu.setCode(code);
                    menu.setRoleName(roleName);
                    List<SysMenu> menus1 = sysMenuService.getDao().queryMenuList(menu);
                    if (CollectionUtils.isEmpty(menus1)) {
                        //添加权限
                        SysRoleMenu roleMenu = new SysRoleMenu();
                        roleMenu.setMenuCode(code);
                        roleMenu.setRoleId(roles.getId());
                        sysRoleMenuService.add(roleMenu);
                    }
                }
            }
            //删除权限 查询前端显示的菜单
//            List<SysMenu> menuList=new ArrayList<>();
//            SysMenu menu = new SysMenu();
//            menu.setType(1);
//            menu.setCode("101");
//            List<SysMenu> menuses = sysMenuService.queryList(menu);
//            menuList.addAll(menuses);
//            menu.setCode(null);
//            for (SysMenu menu0 : menuses) {
//                menu.setPcode(menu0.getCode());
//                menu.setType(2);
//                List<SysMenu> menus1 = sysMenuService.queryList(menu);
//                menuList.addAll(menus1);
//                for (SysMenu menu1 : menus1) {
//                    menu.setType(3);
//                    menu.setPcode(menu1.getCode());
//                    List<SysMenu> menus2 = sysMenuService.queryList(menu);
//                    menuList.addAll(menus2);
//                    for (SysMenu menu2 : menus2) {
//                        menu.setType(4);
//                        menu.setPcode(menu2.getCode());
//                        List<SysMenu> menus3 = sysMenuService.queryList(menu);
//                        menuList.addAll(menus3);
//                    }
//                }
//            }
            SysMenu menu = new SysMenu();
            menu.setUserId(userId);
            List<SysMenu> menuList = sysMenuService.getDao().queryMenuList(menu);
            for (SysMenu menu1 : menuList) {
                isTrue = true;
                for (String code : menuCodes) {
                    if (code.equals(menu1.getCode())) {
                        isTrue = false;
                        break;
                    }
                }
                if (isTrue) {
                    //删除权限
                    menu1.setUserId(userId);
                    sysRoleMenuService.getDao().deleteBYCode(menu1);
                }
            }
            getResult(result, menus, true, "0", "成功");
        } catch (Exception e) {
            e.printStackTrace();
            return getResult(result, null, false, null, "系统异常");
        }
        return result;
    }

}
