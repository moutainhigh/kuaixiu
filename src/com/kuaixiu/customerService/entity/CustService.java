package com.kuaixiu.customerService.entity;import com.common.base.entity.BaseEntity;/** * . * * @CreateDate: 2016-12-18 下午10:13:47 * @version: V 1.0 */public class CustService extends BaseEntity {        /**     * 序列化Id     */    private static final long serialVersionUID = 1L;    /**     *      */    private String id ;    /**     * 链接商账号     */    private String providerCode ;    /**     * 维修门店账号     */    private String shopCode ;    /**     * 客服工号     */    private String number ;    /**     * 客服名称     */    private String name ;    /**     * 客服号码     */    private String mobile ;    /**     * 邮箱     */    private String email ;    /**     * 性别     */    private String gender ;    /**     * 是否删除 0：否；1：是     */    private Integer isDel ;    /**     *      */    private java.util.Date createTime ;    /**     *      */    private String createUserid ;    /**     *       */    private java.util.Date updateTime ;    /**     *      */    private String updateUserid ;    /**     * get:     */    public String getId(){        return this.id;    }    /**     * set：     */    public void setId(String id){        this.id=id;    }    /**     * get:链接商账号     */    public String getProviderCode(){        return this.providerCode;    }    /**     * set：链接商账号     */    public void setProviderCode(String providerCode){        this.providerCode=providerCode;    }    /**     * get:维修门店账号     */    public String getShopCode(){        return this.shopCode;    }    /**     * set：维修门店账号     */    public void setShopCode(String shopCode){        this.shopCode=shopCode;    }    /**     * get:客服工号     */    public String getNumber(){        return this.number;    }    /**     * set：客服工号     */    public void setNumber(String number){        this.number=number;    }    /**     * get:客服名称     */    public String getName(){        return this.name;    }    /**     * set：客服名称     */    public void setName(String name){        this.name=name;    }    /**     * get:客服号码     */    public String getMobile(){        return this.mobile;    }    /**     * set：客服号码     */    public void setMobile(String mobile){        this.mobile=mobile;    }    /**     * get:邮箱     */    public String getEmail(){        return this.email;    }    /**     * set：邮箱     */    public void setEmail(String email){        this.email=email;    }    /**     * get:性别     */    public String getGender(){        return this.gender;    }    /**     * set：性别     */    public void setGender(String gender){        this.gender=gender;    }    /**     * get:是否删除 0：否；1：是     */    public Integer getIsDel(){        return this.isDel;    }    /**     * set：是否删除 0：否；1：是     */    public void setIsDel(Integer isDel){        this.isDel=isDel;    }    /**     * get:     */    public java.util.Date getCreateTime(){        return this.createTime;    }    /**     * set：     */    public void setCreateTime(java.util.Date createTime){        this.createTime=createTime;    }    /**     * get:     */    public String getCreateUserid(){        return this.createUserid;    }    /**     * set：     */    public void setCreateUserid(String createUserid){        this.createUserid=createUserid;    }    /**     * get:      */    public java.util.Date getUpdateTime(){        return this.updateTime;    }    /**     * set：      */    public void setUpdateTime(java.util.Date updateTime){        this.updateTime=updateTime;    }    /**     * get:     */    public String getUpdateUserid(){        return this.updateUserid;    }    /**     * set：     */    public void setUpdateUserid(String updateUserid){        this.updateUserid=updateUserid;    }}