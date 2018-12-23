package com.kuaixiu.brand.entity;

import com.common.base.entity.BaseEntity;

/**
* @author: anson
* @CreateDate: 2017年6月29日 下午4:55:04
* @version: V 1.0
* 简写版用于传送数据给安卓端
*/
public class AppBrand extends BaseEntity{

	private String id;
	
	private String name;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
