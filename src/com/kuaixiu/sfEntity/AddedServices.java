package com.kuaixiu.sfEntity;

import com.common.base.entity.BaseEntity;

/**
* @author: anson
* @CreateDate: 2017年8月15日 下午2:42:07
* @version: V 1.0
* 顺丰快递增值服务（注意字段名称必须为英文字母大写）
*/
public class AddedServices extends BaseEntity{

	private static final long serialVersionUID = 1L;
    /**
     * 代收货款，value代收货款值，上限为20000，以 原寄地所在区域币种为准，
     * 如中国大陆为人民 币，香港为港币，保留1位小数，如 99.9 。
     *  value1为代收货款协议卡号（可能与月结卡号相 同）， 如选择此服务，须增加CUSTID字段
     */
	private String COD;
	/**
	 * 代收货款月结卡号，如果选择 COD 增值服务，必 填
	 */
	private String CUSTID;
	/**
	 * 保价，value为声明价值(即该包裹的价值)
	 */
	private String INSURE;
	/**
	 * 签收短信通知，value 为手机号码
	 */
	private String MSG;
	/**
	 * 包装费，value 为包装费费用
	 */
	private String PKFREE;
	/**
	 * 特殊保价，value 为服务费.
	 */
	private String SINSURE;
	/**
	 * 特殊配送，value为服务特殊配送服务费
	 */
	private String SDELIVERY;
	/**
	 * 特殊增值服务，value 特殊增值服务费
	 */
	private String SADDSERVICE;
	public String getCOD() {
		return COD;
	}
	public void setCOD(String cOD) {
		COD = cOD;
	}
	public String getCUSTID() {
		return CUSTID;
	}
	public void setCUSTID(String cUSTID) {
		CUSTID = cUSTID;
	}
	public String getINSURE() {
		return INSURE;
	}
	public void setINSURE(String iNSURE) {
		INSURE = iNSURE;
	}
	public String getMSG() {
		return MSG;
	}
	public void setMSG(String mSG) {
		MSG = mSG;
	}
	public String getPKFREE() {
		return PKFREE;
	}
	public void setPKFREE(String pKFREE) {
		PKFREE = pKFREE;
	}
	public String getSINSURE() {
		return SINSURE;
	}
	public void setSINSURE(String sINSURE) {
		SINSURE = sINSURE;
	}
	public String getSDELIVERY() {
		return SDELIVERY;
	}
	public void setSDELIVERY(String sDELIVERY) {
		SDELIVERY = sDELIVERY;
	}
	public String getSADDSERVICE() {
		return SADDSERVICE;
	}
	public void setSADDSERVICE(String sADDSERVICE) {
		SADDSERVICE = sADDSERVICE;
	}
	
	
	
}
