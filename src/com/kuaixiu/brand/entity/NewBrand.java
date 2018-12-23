package com.kuaixiu.brand.entity;

import com.common.base.entity.BaseEntity;

/**
* @author: anson
* @CreateDate: 2017年6月13日 下午4:13:13
* @version: V 1.0
* 以旧换新支持品牌
*/
public class NewBrand extends BaseEntity{

	private static final long serialVersionUID = 6913488728937302984L;

	    private String id ;
	    /**
	     * 品牌名称
	     */
	    private String name ;
	    /**
	     * 品牌名称-英文
	     */
	    private String nameEn ;
	    /**
	     * logo图片
	     */
	    private String logo ;
	    /**
	     * 品牌首字母
	     */
	    private String initial ;
	    /**
	     * 排序
	     */
	    private Integer sort ;
	    /**
	     * 是否删除 0：否；1：是
	     */
	    private Integer isDel ;
	    /**
	     * 
	     */
	    private java.util.Date createTime ;
	    /**
	     * 
	     */
	    private String createUserid ;
	    /**
	     * 
	     */
	    private java.util.Date updateTime ;
	    /**
	     * 
	     */
	    private String updateUserid ;

	    /**
	     * get:
	     */
	    public String getId(){
	        return this.id;
	    }

	    /**
	     * set：
	     */
	    public void setId(String id){
	        this.id=id;
	    }
	    /**
	     * get:品牌名称
	     */
	    public String getName(){
	        return this.name;
	    }

	    /**
	     * set：品牌名称
	     */
	    public void setName(String name){
	        this.name=name;
	    }
	    /**
	     * get:品牌名称-英文
	     */
	    public String getNameEn(){
	        return this.nameEn;
	    }

	    /**
	     * set：品牌名称-英文
	     */
	    public void setNameEn(String nameEn){
	        this.nameEn=nameEn;
	    }
	    /**
	     * get:logo图片
	     */
	    public String getLogo(){
	        return this.logo;
	    }

	    /**
	     * set：logo图片
	     */
	    public void setLogo(String logo){
	        this.logo=logo;
	    }
	    /**
	     * get:品牌首字母
	     */
	    public String getInitial(){
	        return this.initial;
	    }

	    /**
	     * set：品牌首字母
	     */
	    public void setInitial(String initial){
	        this.initial=initial;
	    }
	    
	    public Integer getSort() {
			return sort;
		}

		public void setSort(Integer sort) {
			this.sort = sort;
		}

		/**
	     * get:是否删除 0：否；1：是
	     */
	    public Integer getIsDel(){
	        return this.isDel;
	    }

	    /**
	     * set：是否删除 0：否；1：是
	     */
	    public void setIsDel(Integer isDel){
	        this.isDel=isDel;
	    }
	    /**
	     * get:
	     */
	    public java.util.Date getCreateTime(){
	        return this.createTime;
	    }

	    /**
	     * set：
	     */
	    public void setCreateTime(java.util.Date createTime){
	        this.createTime=createTime;
	    }
	    /**
	     * get:
	     */
	    public String getCreateUserid(){
	        return this.createUserid;
	    }

	    /**
	     * set：
	     */
	    public void setCreateUserid(String createUserid){
	        this.createUserid=createUserid;
	    }
	    /**
	     * get:
	     */
	    public java.util.Date getUpdateTime(){
	        return this.updateTime;
	    }

	    /**
	     * set：
	     */
	    public void setUpdateTime(java.util.Date updateTime){
	        this.updateTime=updateTime;
	    }
	    /**
	     * get:
	     */
	    public String getUpdateUserid(){
	        return this.updateUserid;
	    }

	    /**
	     * set：
	     */
	    public void setUpdateUserid(String updateUserid){
	        this.updateUserid=updateUserid;
	    }
	
	
	
}
