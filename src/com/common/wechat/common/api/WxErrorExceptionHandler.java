package com.common.wechat.common.api;

import com.common.wechat.common.exception.WxErrorException;

/**
 * WxErrorException处理器
 */
public interface WxErrorExceptionHandler {

  void handle(WxErrorException e);

}
