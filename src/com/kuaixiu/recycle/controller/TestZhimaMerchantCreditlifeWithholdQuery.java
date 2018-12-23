package com.kuaixiu.recycle.controller;
/**
* @author: anson

* @CreateDate: 2017年11月23日 下午4:41:10
* @version: V 1.0
* 商户查询用户代扣协议签约情况
* https://b.zmxy.com.cn/technology/openDoc.htm?id=852
*/
import com.antgroup.zmxy.openplatform.api.DefaultZhimaClient;
import com.antgroup.zmxy.openplatform.api.ZhimaApiException;
import com.antgroup.zmxy.openplatform.api.request.ZhimaMerchantCreditlifeWithholdQueryRequest;
import com.antgroup.zmxy.openplatform.api.response.ZhimaMerchantCreditlifeWithholdQueryResponse;
import com.system.constant.SystemConstant;

public class TestZhimaMerchantCreditlifeWithholdQuery {
    //芝麻开放平台地址
    private String gatewayUrl= SystemConstant.ZHIMA_OPEN_URL;
    //商户应用 Id
    private String appId= SystemConstant.ZHIMA_APPID;
    //商户 RSA 私钥
    private String privateKey     = SystemConstant.ZHIFUBAO_PRIVATE_RSA;
    //芝麻 RSA 公钥
    private String zhimaPublicKey = SystemConstant.ZHIMA_PUBLIC_RSA;

    public void  testZhimaMerchantCreditlifeWithholdQuery() {
        ZhimaMerchantCreditlifeWithholdQueryRequest req = new ZhimaMerchantCreditlifeWithholdQueryRequest();
        req.setChannel("apppc");
        req.setPlatform("zmop");
        req.setUserId("2088022820323602");// 必要参数 
        DefaultZhimaClient client = new DefaultZhimaClient(gatewayUrl, appId, privateKey, zhimaPublicKey);
        try {
            ZhimaMerchantCreditlifeWithholdQueryResponse response = client.execute(req);
            System.out.println(response.isSuccess());
            System.out.println(response.getErrorCode());
            System.out.println(response.getErrorMessage());
            System.out.println(response.getBody());
        } catch (ZhimaApiException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        TestZhimaMerchantCreditlifeWithholdQuery result = new  TestZhimaMerchantCreditlifeWithholdQuery();
        result.testZhimaMerchantCreditlifeWithholdQuery();
    }
}
