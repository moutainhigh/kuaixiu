package com.kuaixiu.version.entity;import com.common.base.entity.BaseEntity;/** * . * * @CreateDate: 2017-05-02 下午09:55:22 * @version: V 1.0 */public class Version extends BaseEntity {        /**     * 序列化Id     */    private static final long serialVersionUID = 1L;    /**     *      */    private Long id ;    /**     *      */    private String name ;    /**     * 版本     */    private String version ;    /**     * 级别     */    private Long level ;    /**     * 下载路径     */    private String path ;    /**     * 更新日期     */    private String updateTime ;    /**     *      */    private java.util.Date createTime ;    /**     * get:     */    public Long getId(){        return this.id;    }    /**     * set：     */    public void setId(Long id){        this.id=id;    }    /**     * get:     */    public String getName(){        return this.name;    }    /**     * set：     */    public void setName(String name){        this.name=name;    }    /**     * get:版本     */    public String getVersion(){        return this.version;    }    /**     * set：版本     */    public void setVersion(String version){        this.version=version;    }    /**     * get:级别     */    public Long getLevel(){        return this.level;    }    /**     * set：级别     */    public void setLevel(Long level){        this.level=level;    }    /**     * get:下载路径     */    public String getPath(){        return this.path;    }    /**     * set：下载路径     */    public void setPath(String path){        this.path=path;    }    /**     * get:更新日期     */    public String getUpdateTime(){        return this.updateTime;    }    /**     * set：更新日期     */    public void setUpdateTime(String updateTime){        this.updateTime=updateTime;    }    /**     * get:     */    public java.util.Date getCreateTime(){        return this.createTime;    }    /**     * set：     */    public void setCreateTime(java.util.Date createTime){        this.createTime=createTime;    }}