package com.common.wechat.bean.outxmlbuilder;

import com.common.wechat.common.util.StringUtils;
import com.common.wechat.bean.WxMpXmlOutTransferCustomerServiceMessage;

/**
 * 客服消息builder
 * <pre>
 * 用法: WxMpCustomMessage m = WxMpXmlOutMessage.TRANSFER_CUSTOMER_SERVICE().content(...).toUser(...).build();
 * </pre>
 *
 * @author chanjarster
 */
public final class TransferCustomerServiceBuilder extends BaseBuilder<TransferCustomerServiceBuilder, WxMpXmlOutTransferCustomerServiceMessage> {
  private String kfAccount;

  public TransferCustomerServiceBuilder kfAccount(String kf) {
    this.kfAccount = kf;
    return this;
  }

  @Override
  public WxMpXmlOutTransferCustomerServiceMessage build() {
    WxMpXmlOutTransferCustomerServiceMessage m = new WxMpXmlOutTransferCustomerServiceMessage();
    setCommon(m);
    if(StringUtils.isNotBlank(this.kfAccount)){
      WxMpXmlOutTransferCustomerServiceMessage.TransInfo transInfo = new WxMpXmlOutTransferCustomerServiceMessage.TransInfo();
      transInfo.setKfAccount(this.kfAccount);
      m.setTransInfo(transInfo);
    }
    return m;
  }
}
