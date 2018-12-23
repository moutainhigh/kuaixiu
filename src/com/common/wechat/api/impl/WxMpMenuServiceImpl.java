package com.common.wechat.api.impl;

import com.common.wechat.common.bean.menu.WxMenu;
import com.common.wechat.common.exception.WxErrorException;
import com.common.wechat.common.util.http.SimpleGetRequestExecutor;
import com.common.wechat.common.util.http.SimplePostRequestExecutor;
import com.common.wechat.api.WxMpMenuService;
import com.common.wechat.api.WxMpService;

/**
 * Created by Binary Wang on 2016/7/21.
 */
public class WxMpMenuServiceImpl implements WxMpMenuService {
  private static final String API_URL_PREFIX = "https://api.weixin.qq.com/cgi-bin/menu";

  private WxMpService wxMpService;

  public WxMpMenuServiceImpl(WxMpService wxMpService) {
    this.wxMpService = wxMpService;
  }

  @Override
  public void menuCreate(WxMenu menu) throws WxErrorException {
    if (menu.getMatchRule() != null) {
      String url = API_URL_PREFIX + "/addconditional";
      this.wxMpService.execute(new SimplePostRequestExecutor(), url, menu.toJson());
    } else {
      String url = API_URL_PREFIX + "/create";
      this.wxMpService.execute(new SimplePostRequestExecutor(), url, menu.toJson());
    }
  }

  @Override
  public void menuDelete() throws WxErrorException {
    String url = API_URL_PREFIX + "/delete";
    this.wxMpService.execute(new SimpleGetRequestExecutor(), url, null);
  }

  @Override
  public void menuDelete(String menuid) throws WxErrorException {
    String url = API_URL_PREFIX + "/delconditional";
    this.wxMpService.execute(new SimpleGetRequestExecutor(), url, "menuid=" + menuid);
  }

  @Override
  public WxMenu menuGet() throws WxErrorException {
    String url = API_URL_PREFIX + "/get";
    try {
      String resultContent = this.wxMpService.execute(new SimpleGetRequestExecutor(), url, null);
      return WxMenu.fromJson(resultContent);
    } catch (WxErrorException e) {
      // 46003 不存在的菜单数据
      if (e.getError().getErrorCode() == 46003) {
        return null;
      }
      throw e;
    }
  }

  @Override
  public WxMenu menuTryMatch(String userid) throws WxErrorException {
    String url = API_URL_PREFIX + "/trymatch";
    try {
      String resultContent = this.wxMpService.execute(new SimpleGetRequestExecutor(), url, "user_id=" + userid);
      return WxMenu.fromJson(resultContent);
    } catch (WxErrorException e) {
      // 46003 不存在的菜单数据     46002 不存在的菜单版本
      if (e.getError().getErrorCode() == 46003 || e.getError().getErrorCode() == 46002) {
        return null;
      }
      throw e;
    }
  }
}
