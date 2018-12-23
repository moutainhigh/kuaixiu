package com.system.basic.user.entity;import java.util.Date;import com.common.base.entity.BaseEntity;/** * . * * @CreateDate: 2016-08-26 下午10:26:44 * @version: V 1.0 */public class SysRole extends BaseEntity {        /**     * 序列化Id     */    private static final long serialVersionUID = 1L;    /**     *      */    private String id ;    /**     * 角色名称     */    private String name ;    /**     * 角色类型 0 系统角色     */    private Integer type ;    /**     * 状态 0 启用 1 禁用     */    private Integer state ;    /**     * 是否删除 0：否；1：是     */    private Integer isDel ;    /**     *      */    private Date createTime ;    /**     *      */    private String createUserid ;    /**     *      */    private Date updateTime ;    /**     *      */    private String updateUserid ;    /**     * get:     */    public String getId(){        return this.id;    }    /**     * set：     */    public void setId(String id){        this.id=id;    }    /**     * get:角色名称     */    public String getName(){        return this.name;    }    /**     * set：角色名称     */    public void setName(String name){        this.name=name;    }    /**     * get:角色类型 0 系统角色     */    public Integer getType(){        return this.type;    }    /**     * set：角色类型 0 系统角色     */    public void setType(Integer type){        this.type=type;    }    /**     * get:状态 0 启用 1 禁用     */    public Integer getState(){        return this.state;    }    /**     * set：状态 0 启用 1 禁用     */    public void setState(Integer state){        this.state=state;    }    /**     * get:是否删除 0：否；1：是     */    public Integer getIsDel(){        return this.isDel;    }    /**     * set：是否删除 0：否；1：是     */    public void setIsDel(Integer isDel){        this.isDel=isDel;    }    /**     * get:     */    public Date getCreateTime(){        return this.createTime;    }    /**     * set：     */    public void setCreateTime(Date createTime){        this.createTime=createTime;    }    /**     * get:     */    public String getCreateUserid(){        return this.createUserid;    }    /**     * set：     */    public void setCreateUserid(String createUserid){        this.createUserid=createUserid;    }    /**     * get:     */    public Date getUpdateTime(){        return this.updateTime;    }    /**     * set：     */    public void setUpdateTime(Date updateTime){        this.updateTime=updateTime;    }    /**     * get:     */    public String getUpdateUserid(){        return this.updateUserid;    }    /**     * set：     */    public void setUpdateUserid(String updateUserid){        this.updateUserid=updateUserid;    }}