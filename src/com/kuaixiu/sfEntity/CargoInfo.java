package com.kuaixiu.sfEntity;

import com.common.base.entity.BaseEntity;

/**
* @author: anson
* @CreateDate: 2017年8月15日 上午11:30:49
* @version: V 1.0
* 货物信息
*/
public class CargoInfo extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 包裹数，一个包裹对应一个运单号，如果是大于 1 个包裹，则返回按照子母件的方式返回母运单 号和子运单号。
	 * 默认为 1
	 */
	private Integer parcelQuantity;
	/**
	 * 货物名称，如果有多个货物，以英文逗号分隔， 如：“手机,IPAD,充电器”
	 */
	private String cargo;
	/**
	 * 货物数量，多个货物时以英文逗号分隔，且与货 物名称一一对应 如：2,1,3
	 */
	private String cargoCount;
	/**
	 * 货物单位，多个货物时以英文逗号分隔，且与货物名称一一对应如：个,台,本
	 */
	private String cargoUnit;
	/**
	 * 货物重量，多个货物时以英文逗号分隔，且与货 物名称一一对应 如：1.0035,1.0,3.0
	 */
	private String cargoWeight;
	/**
	 * 货物单价，多个货物时以英文逗号分隔，且与货 物名称一一对应 如：1000,2000,1500
	 */
	private String cargoAmount;
	/**
	 * 订单货物总重量，单位 KG，如果提供此值，必须>0
	 */
	private java.math.BigDecimal cargoTotalWeight;
	public Integer getParcelQuantity() {
		return parcelQuantity;
	}
	public void setParcelQuantity(Integer parcelQuantity) {
		this.parcelQuantity = parcelQuantity;
	}
	public String getCargo() {
		return cargo;
	}
	public void setCargo(String cargo) {
		this.cargo = cargo;
	}
	public String getCargoCount() {
		return cargoCount;
	}
	public void setCargoCount(String cargoCount) {
		this.cargoCount = cargoCount;
	}
	public String getCargoUnit() {
		return cargoUnit;
	}
	public void setCargoUnit(String cargoUnit) {
		this.cargoUnit = cargoUnit;
	}
	public String getCargoWeight() {
		return cargoWeight;
	}
	public void setCargoWeight(String cargoWeight) {
		this.cargoWeight = cargoWeight;
	}
	public String getCargoAmount() {
		return cargoAmount;
	}
	public void setCargoAmount(String cargoAmount) {
		this.cargoAmount = cargoAmount;
	}
	public java.math.BigDecimal getCargoTotalWeight() {
		return cargoTotalWeight;
	}
	public void setCargoTotalWeight(java.math.BigDecimal cargoTotalWeight) {
		this.cargoTotalWeight = cargoTotalWeight;
	}
	
	
	
	
	
	
}
