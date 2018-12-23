package com.common.wechat.util.http;

import com.common.wechat.common.bean.result.WxError;
import com.common.wechat.common.exception.WxErrorException;
import com.common.wechat.common.util.http.RequestExecutor;
import com.common.wechat.common.util.http.Utf8ResponseHandler;
import com.common.wechat.common.util.json.WxGsonBuilder;
import com.common.wechat.bean.WxMpMaterialNews;
import com.common.wechat.util.json.WxMpGsonBuilder;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MaterialNewsInfoRequestExecutor implements RequestExecutor<WxMpMaterialNews, String> {

  public MaterialNewsInfoRequestExecutor() {
    super();
  }

  @Override
  public WxMpMaterialNews execute(CloseableHttpClient httpclient, HttpHost httpProxy, String uri, String materialId) throws WxErrorException, IOException {
    HttpPost httpPost = new HttpPost(uri);
    if (httpProxy != null) {
      RequestConfig config = RequestConfig.custom().setProxy(httpProxy).build();
      httpPost.setConfig(config);
    }

    Map<String, String> params = new HashMap<>();
    params.put("media_id", materialId);
    httpPost.setEntity(new StringEntity(WxGsonBuilder.create().toJson(params)));
    try(CloseableHttpResponse response = httpclient.execute(httpPost)){
      String responseContent = Utf8ResponseHandler.INSTANCE.handleResponse(response);
      WxError error = WxError.fromJson(responseContent);
      if (error.getErrorCode() != 0) {
        throw new WxErrorException(error);
      } else {
        return WxMpGsonBuilder.create().fromJson(responseContent, WxMpMaterialNews.class);
      }
    }finally {
      httpPost.releaseConnection();
    }

  }

}
