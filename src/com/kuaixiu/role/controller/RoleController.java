package com.kuaixiu.role.controller;

import com.common.base.controller.BaseController;
import com.common.paginate.Page;
import com.common.wechat.common.util.StringUtils;
import com.system.api.entity.ResultData;
import com.system.basic.user.entity.SessionUser;
import com.system.basic.user.entity.SysRole;
import com.system.basic.user.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * SysRole Controller
 *
 * @CreateDate: 2018-12-11 下午02:36:40
 * @version: V 1.0
 */
@Controller
public class RoleController extends BaseController {

    @Autowired
    private SysRoleService sysRoleService;

    /**
     * 权限列表
     * @param request
     * @param response
     */
    @RequestMapping(value = "role/sysRole/queryListForPage")
    public void getRoleListForPage(HttpServletRequest request, HttpServletResponse response){
        try {
            SysRole role=new SysRole();
            Page page=getPageByRequest(request);
            role.setPage(page);
            List<SysRole> roles=sysRoleService.queryListForPage(role);
            page.setData(roles);
            this.renderJson(response, page);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 添加权限
     */
    @RequestMapping(value = "role/sysRole/add")
    @ResponseBody
    public ResultData sysRoleAdd(HttpServletRequest request, HttpServletResponse response){
        ResultData result=new ResultData();
        try {
            SessionUser su = getCurrentUser(request);
            String roleId=request.getParameter("role_id");
            String roleName=request.getParameter("role_name");
            String roleType=request.getParameter("role_type");
            SysRole role=new SysRole();
            role.setCreateUserid(su.getUserId());
            role.setId(roleId);
            role.setName(roleName);
            if(StringUtils.isNotBlank(roleType)){
                role.setType(Integer.valueOf(roleType));
            }
            sysRoleService.add(role);
            result.setSuccess(true);
            result.setResultMessage("添加成功");
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 编辑权限
     */
    @RequestMapping(value = "role/sysRole/edit")
    @ResponseBody
    public ResultData sysRoleEdit(HttpServletRequest request, HttpServletResponse response){
        ResultData result=new ResultData();
        try {
            SessionUser su = getCurrentUser(request);
            String roleId=request.getParameter("role_id");
            String roleName=request.getParameter("role_name");
            String roleType=request.getParameter("role_type");
            String roleState=request.getParameter("role_state");
            SysRole role=new SysRole();
            role.setUpdateUserid(su.getUserId());
            role.setId(roleId);
            role.setName(roleName);
            if(StringUtils.isNotBlank(roleType)){
                role.setType(Integer.valueOf(roleType));
            }
            if(StringUtils.isNotBlank(roleState)){
                role.setState(Integer.valueOf(roleState));
            }
            sysRoleService.saveUpdate(role);
            result.setSuccess(true);
            result.setResultMessage("编辑成功");
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 编辑权限
     */
    @RequestMapping(value = "role/sysRole/delete")
    @ResponseBody
    public ResultData sysRoleDelete(HttpServletRequest request, HttpServletResponse response){
        ResultData result=new ResultData();
        try {
            String roleId=request.getParameter("role_id");
            SysRole role=new SysRole();
            role.setId(roleId);
            sysRoleService.delete(role);
            result.setSuccess(true);
            result.setResultMessage("删除成功");
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
}
