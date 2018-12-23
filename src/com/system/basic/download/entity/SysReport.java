package com.system.basic.download.entity;

import java.util.Date;

import com.common.base.entity.BaseEntity;

public class SysReport extends BaseEntity{
    /**
     * 序列化Id
     */
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Integer id ;
    /**
     * 文件名称
     */
    private String fileName ;
    /**
     * 文件模板名称
     */
    private String fileTempName ;
    /**
     * 导出文件名称
     */
    private String fileOutName ;
    /**
     * 导出文件名称
     */
    private String classPath ;
    /**
     * 导出文件名称
     */
    private String methodName ;
    /**
     * 文件下载模式
     */
    private Integer type ;
    /**
     * 是否删除 0：否；1：是
     */
    private Integer isDel ;
    /**
     * 
     */
    private Date createTime ;
    /**
     * 
     */
    private String createUserid ;
    /**
     * 
     */
    private Date updateTime ;
    /**
     * 
     */
    private String updateUserid ;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileTempName() {
		return fileTempName;
	}
	public void setFileTempName(String fileTempName) {
		this.fileTempName = fileTempName;
	}
	public String getFileOutName() {
		return fileOutName;
	}
	public void setFileOutName(String fileOutName) {
		this.fileOutName = fileOutName;
	}
	public String getClassPath() {
		return classPath;
	}
	public void setClassPath(String classPath) {
		this.classPath = classPath;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Integer getIsDel() {
		return isDel;
	}
	public void setIsDel(Integer isDel) {
		this.isDel = isDel;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getCreateUserid() {
		return createUserid;
	}
	public void setCreateUserid(String createUserid) {
		this.createUserid = createUserid;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public String getUpdateUserid() {
		return updateUserid;
	}
	public void setUpdateUserid(String updateUserid) {
		this.updateUserid = updateUserid;
	}
}
