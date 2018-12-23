package com.kuaixiu.sfEntity;

import com.common.base.entity.BaseEntity;

/**
* @author: anson
* @CreateDate: 2017年8月15日 上午11:25:35
* @version: V 1.0
* 快速下单 到件方信息
*/
public class ConsigneerInfo extends BaseEntity{

	private static final long serialVersionUID = 1L;

	/**
	 * 到件方公司名称
	 */
	private String company;
	/**
	 * 到件方联系人 
	 */
	private String contact;
	/**
	 * 到件方联系电话 
	 */
	private String tel;
	/**
	 * 到件方所在省份，必须是标准的省名称称谓
	 *  如：广东省（省字不要省略） 如果是直辖市，请直接传北京市、上海市等 
	 */
	private String province;
	/**
	 * 到件方所属城市名称，必须是标准的城市称谓 
	 * 如：深圳市（市字不要省略） 如果是直辖市，请直接传北京市、上海市等 
	 */
	private String city;
	/**
	 * 到件人所在县/区，必须是标准的县/区称谓 如：福田区（区字不要省略）
	 */
	private String county;
	/**
	 * 寄件方详细地址 如：“福田区新洲十一街万基商务大厦 10 楼” 
	 */
	private String address;
	/**
	 * 寄件方手机
	 */
	private String mobile;
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCounty() {
		return county;
	}
	public void setCounty(String county) {
		this.county = county;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	
	
}
