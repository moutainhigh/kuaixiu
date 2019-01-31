package com.kuaixiu.recycle.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.exception.SystemException;
import com.common.paginate.Page;
import com.common.util.*;
import com.common.wechat.aes.AesCbcUtil;
import com.common.wechat.aes.YouDaoUtil;
import com.common.wechat.common.util.StringUtils;
import com.google.common.collect.Maps;
import com.kuaixiu.activity.entity.ActivityUser;
import com.kuaixiu.activity.service.ActivityUserService;
import com.kuaixiu.recycle.entity.*;
import com.kuaixiu.recycle.service.*;
import com.kuaixiu.wechat.service.WechatUserService;
import com.kuaixiu.zhuanzhuan.entity.HappyPrize;
import com.kuaixiu.zhuanzhuan.service.HappyPrizeService;
import com.system.api.entity.ResultData;
import com.system.basic.address.service.AddressService;
import com.system.basic.user.entity.SessionUser;
import com.system.basic.user.service.SessionUserService;
import com.system.constant.SystemConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author: anson
 * @CreateDate: 2018年4月26日 上午8:54:16
 * @version: V 1.0
 */
@Controller
public class RecycleWechatController extends BaseController {

    @Autowired
    private RecycleWechatService recycleWechatService;
    @Autowired
    private SessionUserService sessionUserService;
    @Autowired
    private RecycleOrderService recycleOrderService;
    @Autowired
    private TransmitRecordService transmitRecordService;
    @Autowired
    private RecyclePrizeService recyclePrizeService;
    @Autowired
    private PrizeRecordService prizeRecordService;
    @Autowired
    private WechatUserService wechatUserService;
    @Autowired
    private RecycleCheckItemsService recycleCheckItemsService;
    @Autowired
    private HappyPrizeService happyPrizeService;
    @Autowired
    private SingleLoginService singleLoginService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private ActivityUserService activityUserService;

    /**
     * 通过微信临时code获取openid和session_key
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/recycle/getOpenId")
    @ResponseBody
    public ResultData getOpenId(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        try {
            JSONObject params = getPrarms(request);
            String code = params.getString("code");
            if (StringUtils.isBlank(code)) {
                throw new SystemException("请求参数不完整");
            }
            String fromType = params.getString("fromType");
            String url = "https://api.weixin.qq.com/sns/jscode2session?";
            //翼回收
            if (StringUtils.isBlank(fromType) || "".equals(fromType) || Integer.valueOf(fromType) == 0 || Integer.valueOf(fromType) == 1) {
                url = url + "appid=" + SystemConstant.WECHAT_APPLET_APPID + "&secret=" + SystemConstant.WECHAT_APPLET_SECRET + "&js_code=" + code + "&grant_type=authorization_code";
            } else if (Integer.valueOf(fromType) == 2) {
                url = url + "appid=" + SystemConstant.WECHAT_APPLET_POSTMAN_APPID + "&secret=" + SystemConstant.WECHAT_APPLET_POSTMAN_SECRET + "&js_code=" + code + "&grant_type=authorization_code";
            } else if (Integer.valueOf(fromType) == 3) {
                url = url + "appid=" + SystemConstant.WECHAT_ACTIVITY_APPID + "&secret=" + SystemConstant.WECHAT_ACTIVITY_SECRET + "&js_code=" + code + "&grant_type=authorization_code";
            }
            String httpGet = HttpClientUtil.httpGet(url);
            JSONObject parse = (JSONObject) JSONObject.parse(httpGet);
            if (StringUtils.isBlank(parse.getString("openid"))) {
                throw new SystemException(parse.getString("errmsg"));
            }
            //获取openid和session_key并存储
            String openId = parse.getString("openid");
            String unionid = parse.getString("unionid");
            String sessionKey = parse.getString("session_key");
            if (StringUtils.isNotBlank(fromType) && Integer.valueOf(fromType) == 3) {
                ActivityUser user = new ActivityUser();
                //保存该用户
                user.setId(UUID.randomUUID().toString().replace("-", ""));
                user.setOpenId(openId);
                if (StringUtils.isNotBlank(unionid)) {
                    user.setUnionId(unionid);
                }
                user.setSessionKey(sessionKey);
                activityUserService.getDao().add(user);
            } else {
                //保存该用户
                RecycleWechat wechat = new RecycleWechat();
                wechat.setId(UUID.randomUUID().toString().replace("-", ""));
                wechat.setOpenId(openId);
                if (StringUtils.isNotBlank(unionid)) {
                    wechat.setUnionId(unionid);
                }
                wechat.setSessionKey(sessionKey);
                recycleWechatService.addByOpenId(wechat);
                //储存当前用户tokenId  后期下单确定用户
                request.getSession().setAttribute("wechat_openId", openId);
            }
            jsonResult.put("openId", openId);
            result.setResult(jsonResult);
            result.setResultCode("0");
            result.setSuccess(true);
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        return result;
    }


    /**
     * 储存用户信息  该信息需要微信算法解密
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/recycle/getUserInfo")
    @ResponseBody
    public ResultData getUserInfo(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        try {
            JSONObject params = getPrarms(request);
            String openId = params.getString("openId");
            String iv = params.getString("iv");
            String encryptedData = params.getString("encryptedData");
            if (StringUtils.isBlank(openId) || StringUtils.isBlank(iv) || StringUtils.isBlank(encryptedData)) {
                throw new SystemException("请求参数不完整");
            }
            RecycleWechat u = recycleWechatService.queryByOpenId(openId);
            String sessionKey = u.getSessionKey();
            //解密参数
            JSONObject info = AesCbcUtil.decrypt(sessionKey, encryptedData, iv);
            System.out.println("解密的用户信息：" + info);
            //将微信地址通过有道api转化为中文
            String province = YouDaoUtil.getAddress(info.getString("province"));
            String city = YouDaoUtil.getAddress(info.getString("city"));
            u.setProvince(province);
            u.setCity(city);
            u.setNick(info.getString("nickName"));
            u.setGender(info.getString("gender"));
            u.setCountry(info.getString("country"));
            u.setUrl(info.getString("avatarUrl"));
            recycleWechatService.updateByOpenId(u);
            result.setResult(jsonResult);
            result.setResultCode("0");
            result.setSuccess(true);
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        return result;
    }


    /**
     * 储存用户机型
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/recycle/getModel")
    @ResponseBody
    public ResultData getModel(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        try {
            JSONObject params = getPrarms(request);
            String openId = params.getString("openId");
            String brand = params.getString("brand");
            String model = params.getString("model");
            if (StringUtils.isBlank(openId) || StringUtils.isBlank(model)) {
                throw new SystemException("请求参数不完整");
            }
            RecycleWechat u = recycleWechatService.queryByOpenId(openId);
            u.setModel(model);
            u.setBrand(brand);
            recycleWechatService.updateByOpenId(u);
            result.setResult(jsonResult);
            result.setResultCode("0");
            result.setSuccess(true);
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        return result;
    }


    /**
     * 储存用户手机号  该信息需要微信算法解密
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/recycle/getMobile")
    @ResponseBody
    public ResultData getMobile(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        try {
            JSONObject params = getPrarms(request);
            String openId = params.getString("openId");
            String iv = params.getString("iv");
            String encryptedData = params.getString("encryptedData");
            if (StringUtils.isBlank(openId) || StringUtils.isBlank(iv) || StringUtils.isBlank(encryptedData)) {
                throw new SystemException("请求参数不完整");
            }
            RecycleWechat u = recycleWechatService.queryByOpenId(openId);
            String sessionKey = u.getSessionKey();
            //解密参数
            JSONObject info = AesCbcUtil.decrypt(sessionKey, encryptedData, iv);
            u.setMobile(info.getString("phoneNumber"));
            recycleWechatService.updateByOpenId(u);
            result.setResult(jsonResult);
            result.setResultCode("0");
            result.setSuccess(true);
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        return result;
    }


    /**
     * 储存用户详细地址 经纬度
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/recycle/getAddress")
    @ResponseBody
    public ResultData saveAddress(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        try {
            JSONObject params = getPrarms(request);
            String openId = params.getString("openId");
            String address = params.getString("address");
            BigDecimal longitude = params.getBigDecimal("longitude");
            BigDecimal latitude = params.getBigDecimal("latitude");
            if (StringUtils.isBlank(openId) || StringUtils.isBlank(address) || StringUtils.isBlank(longitude.toString()) || StringUtils.isBlank(latitude.toString())) {
                throw new SystemException("请求参数不完整");
            }
            RecycleWechat u = recycleWechatService.queryByOpenId(openId);
            u.setAddress(address);
            u.setLongitude(longitude);
            u.setLatitude(latitude);
            recycleWechatService.updateByOpenId(u);
            result.setResult(jsonResult);
            result.setResultCode("0");
            result.setSuccess(true);
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        return result;
    }


    /**
     * 微信小程序回收用户信息
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/recycle/recycleWechatList")
    public ModelAndView RecycleSystem(HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {
        List<String> province = recycleWechatService.getProvince();
        List<String> brands = recycleWechatService.getBrands();
        request.setAttribute("provinces", province);
        request.setAttribute("brands", brands);
        String returnView = "recycle/recycleWechatList";
        return new ModelAndView(returnView);
    }


    @RequestMapping("/recycle/recycleWechatList/getCitys")
    @ResponseBody
    public ResultData getCitys(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        try {
            String province = request.getParameter("province");
            if (StringUtils.isBlank(province)) {
                throw new SystemException("请选择省份");
            }
            List<String> city = recycleWechatService.getCity(province);
            jsonResult.put("citys", city);
            result.setResult(jsonResult);
            result.setResultCode("0");
            result.setSuccess(true);
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        return result;
    }

    /**
     * 获取品牌下机型
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping("/recycle/recycleWechatList/getModels")
    @ResponseBody
    public ResultData getModels(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        try {
            String brand = request.getParameter("brand");
            if (StringUtils.isBlank(brand)) {
                throw new SystemException("请选择品牌");
            }
            List<String> models = recycleWechatService.getModels(brand);
            jsonResult.put("models", models);
            result.setResult(jsonResult);
            result.setResultCode("0");
            result.setSuccess(true);
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        return result;
    }

    /**
     * 微信小程序回收用户信息刷新数据
     */
    @RequestMapping(value = "recycle/recycleWechatList/queryListForPage")
    public void systemForPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        SessionUser su = getCurrentUser(request);
        if (su.getType() != SystemConstant.USER_TYPE_SYSTEM && su.getType() != SystemConstant.USER_TYPE_CUSTOMER_SERVICE) {
            throw new SystemException("对不起，您没有操作权限!");
        }
        //获取查询条件
        String mobile = request.getParameter("query_mobile");
        String queryStartTime = request.getParameter("query_startTime");
        String queryEndTime = request.getParameter("query_endTime");
        String province = request.getParameter("query_province");
        String city = request.getParameter("query_city");
        String brand = request.getParameter("query_brand");
        String model = request.getParameter("query_model");

        RecycleWechat r = new RecycleWechat();
        r.setLotteryMobile(mobile);
        r.setIsDel(0);
        r.setQueryStartTime(queryStartTime);
        r.setQueryEndTime(queryEndTime);
        r.setProvince(province);
        r.setCity(city);
        r.setBrand(brand);
        r.setModel(model);
        Page page = getPageByRequest(request);
        r.setPage(page);
        List<RecycleWechat> list = recycleWechatService.queryListForPage(r);
        RecycleCheckItems recycleCheckItems = new RecycleCheckItems();
        for (RecycleWechat rw : list) {
            recycleCheckItems.setWechatId(rw.getOpenId());
            //根据openId查询机型和品牌:(机型，品牌，openid三者一起确定唯一数据)
            List<RecycleCheckItems> checkItems = recycleCheckItemsService.queryList(recycleCheckItems);
            for (RecycleCheckItems recycleCheckItems1 : checkItems) {
                if (recycleCheckItems1 != null && recycleCheckItems1.getBrand() != null
                        && recycleCheckItems1.getRecycleModel() != null) {
                    rw.setBrand(recycleCheckItems.getBrand());
                    rw.setModel(recycleCheckItems.getRecycleModel());
                    break;
                }
            }


        }
        page.setData(list);
        this.renderJson(response, page);
    }


    /**
     * 微信用户信息详情
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "recycle/wechat/detail")
    public String detail(HttpServletRequest request, HttpServletResponse response) {
        SessionUser su = getCurrentUser(request);
        if (su.getType() != SystemConstant.USER_TYPE_SYSTEM && su.getType() != SystemConstant.USER_TYPE_CUSTOMER_SERVICE) {
            throw new SystemException("对不起，您没有操作权限!");
        }
        String id = request.getParameter("id");
        if (StringUtils.isBlank(id)) {
            throw new SystemException("参数不完整");
        }
        RecycleWechat recycleWechat = recycleWechatService.queryById(id);
        if (recycleWechat == null) {
            throw new SystemException("用户信息不存在");
        }
        RecycleCheckItems recycleCheckItems = new RecycleCheckItems();
        recycleCheckItems.setWechatId(recycleWechat.getOpenId());
        //根据openId查询机型和品牌:(机型，品牌，openid三者一起确定唯一数据)
        List<RecycleCheckItems> checkItems = recycleCheckItemsService.queryList(recycleCheckItems);
        for (RecycleCheckItems recycleCheckItems1 : checkItems) {
            if (recycleCheckItems1 != null && recycleCheckItems1.getBrand() != null
                    && recycleCheckItems1.getRecycleModel() != null) {
                recycleWechat.setBrand(recycleCheckItems.getBrand());
                recycleWechat.setModel(recycleCheckItems.getRecycleModel());
                break;
            }
        }
        request.setAttribute("wechat", recycleWechat);
        return "recycle/wechatDetail";
    }


    /**
     * delete
     *
     * @param request
     * @param response
     * @return 删除微信用户
     * @throws Exception
     */
    @RequestMapping(value = "recycle/recycleWechatList/delete")
    @ResponseBody
    public Map<String, Object> delete(HttpServletRequest request,
                                      HttpServletResponse response) {
        SessionUser su = getCurrentUser(request);
        if (su.getType() != SystemConstant.USER_TYPE_SYSTEM) {
            throw new SystemException("对不起，您没有操作权限!");
        }
        Map<String, Object> resultMap = Maps.newHashMap();
        //获取项目id
        String id = request.getParameter("id");
        recycleWechatService.deleteById(id, su);
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
        resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
        return resultMap;
    }


    /**
     * 判断是否需要授权获取手机号
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/recycle/isGetMobile")
    @ResponseBody
    public ResultData isGetMobile(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        try {
            JSONObject params = getPrarms(request);
            String openId = params.getString("openId");
            if (StringUtils.isBlank(openId)) {
                throw new SystemException("请求参数不完整");
            }
            RecycleWechat u = recycleWechatService.queryByOpenId(openId);
            if (u == null) {
                throw new SystemException("该用户不存在");
            }
            if (StringUtils.isBlank(u.getMobile())) {
                //需要授权
                jsonResult.put("mobile", true);
            } else {
                jsonResult.put("mobile", false);
            }
            recycleWechatService.updateByOpenId(u);
            result.setResult(jsonResult);
            result.setResultCode("0");
            result.setSuccess(true);
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        return result;
    }


    /**
     * 获取微信小程序 实时订单
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/recycle/getWechatOrder")
    @ResponseBody
    public ResultData getWechatOrder(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        System.out.println("微信用户订单");
        try {
            RecycleOrder order = new RecycleOrder();
            order.setWechatOpenId("");
            order.setIsDel(0);
            List<RecycleOrder> list = recycleOrderService.queryListForPage(order);
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd hh:MM:ss");
            //关联订单对应微信用户
            if (!list.isEmpty()) {
                JSONArray array = new JSONArray();
                for (RecycleOrder o : list) {
                    RecycleWechat wechat = recycleWechatService.queryByOpenId(o.getWechatOpenId());
                    JSONObject json = new JSONObject();
                    if (wechat != null) {
                        json.put("wechatName", wechat.getName());
                        json.put("price", o.getPrice());
                        json.put("time", sf.format(o.getInTime()));
                        json.put("model", o.getProductName());
                        array.add(json);
                    }

                }
                jsonResult.put("info", array);
            }
            result.setResult(jsonResult);
            result.setResultCode("0");
            result.setSuccess(true);
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        return result;
    }


    /**
     * 获取微信小程序用户上次评估的机型和价格
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/recycle/recycleNews")
    @ResponseBody
    public ResultData recycleNews(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        try {
            JSONObject params = getPrarms(request);
            //request.getSession().setAttribute("wechat_openId", openId);
            //String wechatOpenId=(String) request.getSession().getAttribute("wechat_openId");

            //机型名称和微信openId
            String openId = params.getString("openId");
            String model = params.getString("model");
            if (StringUtils.isBlank(openId) || StringUtils.isBlank(model)) {
                throw new SystemException("参数不完整");
            }
            RecycleOrder order = new RecycleOrder();
            order.setWechatOpenId(openId);
            order.setProductName(model);
            List<RecycleOrder> list = recycleOrderService.queryList(order);
            if (!list.isEmpty()) {
                jsonResult.put("modelName", list.get(0).getProductName());
                jsonResult.put("price", list.get(0).getPrice());
            } else {
                throw new SystemException("请先进行检测！");
            }
            result.setResult(jsonResult);
            result.setResultCode("0");
            result.setSuccess(true);
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        return result;
    }

    /**
     * 微信用户分享活动   成功分享一次增加一次抽奖机会  目前次数不限制
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/recycle/recycleShare")
    @ResponseBody
    public ResultData recycleShare(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        boolean tip = true;
        try {
            JSONObject params = getPrarms(request);

            //微信openId和分享的群id
            String openId = params.getString("openId");
            String loginMobile = params.getString("loginMobile");
            String groupId = params.getString("groupId");
            //  status值如果为true则代表分享成功
            String status = params.getString("status");
            if (StringUtils.isBlank(status)) {
                throw new SystemException("参数不完整");
            }
            if (!status.equals("true")) {
                result.setSuccess(false);
                throw new SystemException("分享失败");
            }
            RecycleWechat wechat;
            if (StringUtils.isBlank(loginMobile)) {
                wechat = recycleWechatService.queryByOpenId(openId);
            } else {
                wechat = recycleWechatService.queryLoginMobile(loginMobile);
            }
            if (wechat == null) {
                result.setSuccess(false);
                throw new SystemException("用户不存在");
            }

            //新增分享记录
            TransmitRecord record = new TransmitRecord();
            record.setMobile(wechat.getLoginMobile());
            record.setWechatGroupId(groupId);
            record.setWechatId(wechat.getOpenId());
            transmitRecordService.add(record);

            //每次分享 将用户抽奖次数设为1
            if (wechat.getTotalUse() < 2) {
                wechat.setTotalUse(1);
                recycleWechatService.saveUpdate(wechat);
            }

            result.setResult(jsonResult);
            result.setResultCode("0");
            result.setSuccess(true);
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        return result;
    }


    /**
     * 保存小程序用户抽奖手机号
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/recycle/saveLotteryMobile")
    @ResponseBody
    public ResultData saveLotteryMobile(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        try {
            JSONObject params = getPrarms(request);
            //手机号和验证码
            String mobile = params.getString("mobile");
            String code = params.getString("code");
            String openId = params.getString("openId");
            if (StringUtils.isBlank(mobile) || StringUtils.isBlank(code)) {
                throw new SystemException("手机号或验证码为空");
            }
            if (!checkRandomCode(request, mobile, code)) { // 验证手机号和验证码
                throw new SystemException("手机号或验证码输入错误");
            }
            //保存用户抽奖手机号  一个微信用户的openId只会有一次中奖记录
            RecycleWechat recycleWechat = recycleWechatService.queryByOpenId(openId);
            if (recycleWechat == null) {
                throw new SystemException("用户信息不存在");
            }
            recycleWechat.setLotteryMobile(mobile);
            recycleWechatService.saveUpdate(recycleWechat);

            result.setResult(jsonResult);
            result.setResultCode("0");
            result.setSuccess(true);
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        return result;
    }


    /**
     * 判断是否需要重新获取收货地址
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/recycle/isSaveAddress")
    @ResponseBody
    public ResultData isSaveAddress(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        boolean tip = true;
        try {
            JSONObject params = getPrarms(request);
            String openId = params.getString("openId");
            String loginMobile = params.getString("mobile");  //集团欢购登录手机号
            if (StringUtils.isBlank(openId) && StringUtils.isBlank(loginMobile)) {
                throw new SystemException("参数不完整");
            }
            //判断是通过openId还是手机号 获取信息
            RecycleWechat recycleWechat = new RecycleWechat();
            if (StringUtils.isNotBlank(openId)) {
                recycleWechat = recycleWechatService.queryByOpenId(openId);
            } else if (StringUtils.isNotBlank(loginMobile)) {
                recycleWechat = recycleWechatService.queryLoginMobile(loginMobile);
            }

            if (recycleWechat == null || StringUtils.isBlank(recycleWechat.getPrizeMobile())
                    || StringUtils.isBlank(recycleWechat.getPrizeProvince()) || StringUtils.isBlank(recycleWechat.getPrizeCity())
                    || StringUtils.isBlank(recycleWechat.getPrizeArea()) || StringUtils.isBlank(recycleWechat.getPrizeStreet())) {
                tip = false;
            }

            jsonResult.put("isSave", tip);
            result.setResult(jsonResult);
            result.setResultCode("0");
            result.setSuccess(true);
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        return result;
    }


    /**
     * 保存用户抽奖信息   一个用户对应一个微信openid最多允许中一次奖
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/recycle/savePrizeNews")
    @ResponseBody
    public ResultData savePrizeNews(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        try {
            JSONObject params = getPrarms(request);

            String mobile = params.getString("mobile");
            //地址信息
            String name = get(params.getString("name"));
            String province = get(params.getString("province"));
            String city = get(params.getString("city"));
            String area = get(params.getString("area"));
            String street = get(params.getString("street"));
            String openId = get(params.getString("openId"));

            //保存用户收货手机号  一个微信用户的openId只会有一次中奖记录
            RecycleWechat recycleWechat = recycleWechatService.queryByOpenId(openId);
            if (recycleWechat == null) {
                throw new SystemException("用户信息不存在");
            }
            recycleWechat.setPrizeName(name);
            recycleWechat.setPrizeMobile(mobile);
            recycleWechat.setPrizeProvince(province);
            recycleWechat.setPrizeCity(city);
            recycleWechat.setPrizeArea(area);
            recycleWechat.setPrizeStreet(street);
            recycleWechatService.saveUpdate(recycleWechat);

            result.setResult(jsonResult);
            result.setResultCode("0");
            result.setSuccess(true);
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        return result;
    }


    /**
     * 保存用户地址信息
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/recycle/saveAddressNews")
    @ResponseBody
    public ResultData saveAddressNews(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        try {
            JSONObject params = getPrarms(request);
            String mobile = params.getString("mobile");
            //地址信息
            String name = get(params.getString("name"));
            String province = get(params.getString("province"));
            String city = get(params.getString("city"));
            String area = get(params.getString("area"));
            String street = get(params.getString("street"));
            String openId = params.getString("openId");
            String loginMobile = params.getString("loginMobile");

            RecycleWechat recycleWechat1;
            if (StringUtils.isNotEmpty(openId)) {
                recycleWechat1 = recycleWechatService.queryByOpenId(openId);
            } else {
                recycleWechat1 = recycleWechatService.queryLoginMobile(loginMobile);
            }
            RecycleWechat recycleWechat = new RecycleWechat();
            recycleWechat.setId(UUID.randomUUID().toString().replace("-", ""));
            recycleWechat.setPrizeName(name);
            recycleWechat.setPrizeMobile(mobile);
            recycleWechat.setPrizeProvince(addressService.queryByAreaId(province).getArea());
            recycleWechat.setPrizeCity(addressService.queryByAreaId(city).getArea());
            recycleWechat.setPrizeArea(addressService.queryByAreaId(area).getArea());
            recycleWechat.setPrizeStreet(street);
            recycleWechat.setOpenId(openId);
            recycleWechat.setLoginMobile(loginMobile);
            if (recycleWechat1 == null) {
                recycleWechatService.add(recycleWechat);
            } else {
                recycleWechatService.updateByLoginMobile(recycleWechat);
            }

            result.setResult(jsonResult);
            result.setResultCode("0");
            result.setSuccess(true);
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        return result;
    }


    /**
     * 微信小程序抽奖并发请求测试
     * @param request
     * @param response
     * @return
     */
//	@RequestMapping("/recycle/getManyPrize")
//	@ResponseBody
//	public ResultData mangyGetPrize(HttpServletRequest request, HttpServletResponse response) {
//		ResultData result = new ResultData();
//		JSONObject jsonResult = new JSONObject();
//		JSONObject j = new JSONObject();
//		boolean tip=true;
//		try {
//			PrizeRecord record = new PrizeRecord();
//			record.setMobile("15356152347");
//			record.setWechatId("123456");
//			record.setBatch(SystemConstant.NOW_PRIZE_BATCH);
//			record.setType(0);
//
//			//3 查询奖品剩余情况
//			RecyclePrize rPrize = new RecyclePrize();
//			rPrize.setBatch(SystemConstant.NOW_PRIZE_BATCH);
//			List<RecyclePrize> rList = recyclePrizeService.queryListByGrade(rPrize);
//			//4 开始抽奖
//			int prize = lottery(rList);
//			PrizeRecord user = new PrizeRecord();
//			user.setMobile("15356152347");
//			user.setWechatId("123456");
//			user.setBatch(SystemConstant.NOW_PRIZE_BATCH);
//			user.setIsGet(1);
//			List<PrizeRecord> prizeRecords = prizeRecordService.queryList(user);
//			if (prize != 0) {
//				//中奖了  判断奖品是否还有
//				RecyclePrize p = new RecyclePrize();
//				p.setGrade(prize);
//				p.setBatch(SystemConstant.NOW_PRIZE_BATCH);
//				List<RecyclePrize> recyclePrizeList = recyclePrizeService.queryList(p);
//				if (recyclePrizeList.isEmpty()) {
//					throw new SystemException("系统繁忙，请稍后重试");
//				}
//				RecyclePrize prize1 = recyclePrizeList.get(0);
//				if (prize1.getTotalSum() <= prize1.getUseSum()) {
//					//奖品没有了  此次中奖当作未中奖处理
//					record.setIsGet(0);
//				} else {
//					//System.out.println("中了几等奖：" + prize);
//					System.out.println("当前"+prize+"等奖目前抽中次数"+prize1.getUseSum());
//					//6 抽中了则将奖品更新为最新状态
//					if((prize1.getUseSum()+1)<=prize1.getTotalSum()) {
//						int i = recyclePrizeService.updateById(prize1.getPrizeId());
//						if(i!=1){
//							tip=false;
//						}
//					}
//					//防止多并发 引起脏数据  tip为true最终才为获奖
//					if(tip) {
//						//如果抽中四等奖 则生成一张微信通用优惠券
//						String coupon = "";  //优惠码
//						if (prize == 4) {
//							//System.out.println("四等奖");
//						}
//						record.setCouponCode(coupon);
//						j.put("couponCode", coupon);
//						j.put("prizeName", prize1.getPrizeName());
//						j.put("grade", prize1.getGrade());
//						j.put("inTime", prize1.getInTime());
//						String detail = prize1.getDetails();
//						if (detail.contains(";")) {
//							//分段显示
//							j.put("firstDetails", detail.substring(0, detail.indexOf(";")));
//							j.put("secondDetails", detail.substring(detail.indexOf(";") + 1));
//						} else {
//							j.put("firstDetails", detail);
//							j.put("secondDetails", "");
//						}
//						record.setIsGet(1);
//						record.setGrade(prize);
//						record.setPrizeId(prize1.getPrizeId());
//					}
//				}
//			} else {
//				//未中奖
//				record.setIsGet(0);
//			}
//			//7 新增一条抽奖记录
//			prizeRecordService.add(record);
//
//			jsonResult.put("prizeInfo", j);
//			result.setResult(jsonResult);
//			result.setResultCode("0");
//			result.setSuccess(true);
//		} catch (SystemException e) {
//			sessionUserService.getSystemException(e, result);
//		} catch (Exception e) {
//			e.printStackTrace();
//			sessionUserService.getException(result);
//		}
//
//
//
//
//		return result;
//	}


    /**
     * 欢GO抽奖  每个手机号每天可抽三次  目前先关闭
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/recycle/getRecyclePrize")
    @ResponseBody
    public ResultData getRecyclePrize(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        int surplusLotteryNumber = 0;   //剩余抽奖次数0,1,2
        int currentPrizeState = 0;      //如果currentPrizeState=1才有奖品信息
        int status = 0;                 //0表示抽奖信息已保存   1表示抽奖手机号未保存
        boolean tip = true;             //是否满足抽奖条件
        //返回给前端的抽奖信息
        JSONObject j = new JSONObject();
        try {
            JSONObject params = getPrarms(request);
            String mobile = params.getString("mobile");
            if (StringUtils.isBlank(mobile)) {
                status = 1;
                tip = false;
            }
            //判断该手机号是否满足抽奖条件
            HappyPrize happyPrize = happyPrizeService.queryByMobile(mobile);
            if (happyPrize == null) {
                status = 1;
                tip = false;
            } else {
                // 3判断用户剩余抽奖次数   每天刷新3次抽奖次数
                if (happyPrize.getLastPrizeTime() != null) {
                    //判断上一次抽奖时间 是否为当日
                    if (happyPrize.getLastPrizeTime().getTime() < DateUtil.getStartTime().getTime()) {
                        //刷新三次抽奖次数
                        happyPrize.setTotalUse(3);
                        happyPrize.setAlreadyUse(0);
                    } else {
                        if (happyPrize.getTotalUse() == 0 || happyPrize.getAlreadyUse() >= 3) {
                            tip = false;
                        }
                    }
                }

            }


            // 满足条件才给予抽奖
            if (tip) {
                PrizeRecord record = new PrizeRecord();
                record.setMobile(happyPrize.getLotteryMobile());
                record.setBatch("S002");
                record.setType(1);   //欢GO来源
                //3 查询奖品剩余情况
                RecyclePrize rPrize = new RecyclePrize();
                rPrize.setBatch("S002");
                List<RecyclePrize> rList = recyclePrizeService.queryListByGrade(rPrize);
                //4 开始抽奖
                int prize = lottery(rList);
                PrizeRecord user = new PrizeRecord();
                user.setMobile(mobile);
                user.setBatch("S002");
                user.setIsGet(1);
                List<PrizeRecord> prizeRecords = prizeRecordService.queryList(user);
                //5 如果用户数据库中没有中奖记录，而且抽中了奖，则为成立，否则当未中奖处理
                if (prize != 0 && prizeRecords.isEmpty()) {
                    //中奖了  判断奖品是否还有
                    RecyclePrize p = new RecyclePrize();
                    p.setGrade(prize);
                    p.setBatch("S002");
                    List<RecyclePrize> recyclePrizeList = recyclePrizeService.queryList(p);
                    if (recyclePrizeList.isEmpty()) {
                        throw new SystemException("系统繁忙，请稍后重试");
                    }
                    RecyclePrize prize1 = recyclePrizeList.get(0);
                    if (prize1.getTotalSum() <= prize1.getUseSum()) {
                        //奖品没有了  此次中奖当作未中奖处理
                        record.setIsGet(0);
                    } else {
                        //6 抽中了则将奖品更新为最新状态
                        if ((prize1.getUseSum() + 1) <= prize1.getTotalSum()) {
                            int i = recyclePrizeService.updateById(prize1.getPrizeId());
                            if (i != 1) {
                                tip = false;
                            }
                        }

                        currentPrizeState = 1;
                        if (tip) {
                            j.put("prizeName", prize1.getPrizeName());
                            j.put("grade", prize1.getGrade());
                            j.put("inTime", prize1.getInTime());
                            String detail = prize1.getDetails();
                            if (detail.contains(";")) {
                                //分段显示
                                j.put("firstDetails", detail.substring(0, detail.indexOf(";")));
                                j.put("secondDetails", detail.substring(detail.indexOf(";") + 1));
                            } else {
                                j.put("firstDetails", detail);
                                j.put("secondDetails", "");
                            }
                            record.setIsGet(1);
                            record.setGrade(prize);
                            record.setPrizeId(prize1.getPrizeId());
                        }
                    }
                } else {
                    //未中奖
                    record.setIsGet(0);
                }
                //7 新增一条抽奖记录
                prizeRecordService.add(record);
                //抽奖了 返回可抽奖次数减1
                surplusLotteryNumber = happyPrize.getTotalUse() - 1;
                //8 修改用户抽奖记录信息
                happyPrize.setTotalUse(happyPrize.getTotalUse() - 1);
                happyPrize.setLastPrizeTime(new Date());
                happyPrize.setAlreadyUse(happyPrize.getAlreadyUse() + 1);
                happyPrizeService.saveUpdate(happyPrize);

            }
            jsonResult.put("status", status);
            jsonResult.put("surplusLotteryNumber", surplusLotteryNumber);
            jsonResult.put("tip", tip);
            jsonResult.put("prizeMobile", mobile);
            jsonResult.put("currentPrizeState", currentPrizeState);
            jsonResult.put("prizeInfo", j);
            result.setResult(jsonResult);
            result.setResultCode("0");
            result.setSuccess(true);
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        return result;
    }


    /**
     * 针对小程序调用
     * 微信用户抽奖  用户只能中一次奖  以后无论抽多少次都不会中奖   目前用户每天抽奖次数不限制
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/recycle/getPrize")
    @ResponseBody
    public ResultData getPrize(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        int surplusLotteryNumber = 2;   //剩余抽奖次数0,1,2
        int shareState = 1;             //分享状态 0未分享 1已分享
        int currentPrizeState = 0;      //如果currentPrizeState=1才有奖品信息
        int status = 0;                 //0表示用抽奖手机号和收货地址都已保存   1表示抽奖手机号未保存  2表示收货信息未保存   3抽奖手机号和收货信息都未保存
        boolean tip = true;             //是否满足抽奖条件
        //返回给前端的抽奖信息
        JSONObject j = new JSONObject();
        try {
            JSONObject params = getPrarms(request);
            String openId = params.getString("openId");
            if (StringUtils.isBlank(openId)) {
                throw new SystemException("参数不完整");
            }

            RecycleWechat r = new RecycleWechat();
            r.setOpenId(openId);
            List<RecycleWechat> list = recycleWechatService.queryList(r);
            if (list.isEmpty()) {
                throw new SystemException("用户信息不存在");
            }
            RecycleWechat rw = list.get(0);
            //2 查询该用户的抽奖手机号  和抽奖信息是否存在
            if (StringUtils.isBlank(rw.getLotteryMobile())) {
                status = 1;
                tip = false;
            }
            if (StringUtils.isBlank(rw.getPrizeMobile()) || StringUtils.isBlank(rw.getPrizeProvince())) {
                status = 2;
                // tip = false;
            }
            if (StringUtils.isBlank(rw.getPrizeMobile()) && StringUtils.isBlank(rw.getLotteryMobile())) {
                status = 3;
                //  tip=false;
            }


            //判断用户是否为当天第一次检测来参与抽奖  此种状况不需要分享就可以抽奖
            if (rw.getLastPrizeTime() == null) {
                //不用分享直接抽
            } else {
                RecycleCheckItems rcheck = new RecycleCheckItems();
                rcheck.setWechatId(openId);
                List<RecycleCheckItems> recycleCheckItems = recycleCheckItemsService.queryList(rcheck);
                if (!recycleCheckItems.isEmpty()) {
                    RecycleCheckItems check = recycleCheckItems.get(0);
                    boolean timeTip = check.getUpdateTime().getTime() > DateUtil.getStartTime().getTime() && rw.getLastPrizeTime().getTime() < DateUtil.getStartTime().getTime();
                    if (!timeTip) {
                        //1 先查询用户当天是否有分享记录
                        TransmitRecord t = new TransmitRecord();
                        t.setWechatId(openId);
                        List<TransmitRecord> transmitRecords = transmitRecordService.queryList(t);
                        if (transmitRecords.isEmpty()) {
                            tip = false;
                            shareState = 0;
                        } else {
                            TransmitRecord record = transmitRecords.get(0);
                            //获取当日的起始值
                            long start = DateUtil.getStartTime().getTime();
                            if (record.getInTime().getTime() <= start) {
                                //当日未分享
                                tip = false;
                                shareState = 0;
                            }
                        }
                    }

                }
            }

            // 3判断用户剩余抽奖次数
            if (rw.getTotalUse() < 1) {
                surplusLotteryNumber = 0;
                tip = false;
            } else {
                //得到剩余抽奖次数
                surplusLotteryNumber = rw.getTotalUse();

            }

            // 满足条件才给予抽奖
            if (tip) {
                PrizeRecord record = new PrizeRecord();
                record.setMobile(rw.getLotteryMobile());
                record.setWechatId(openId);
                record.setBatch(SystemConstant.NOW_PRIZE_BATCH);
                record.setType(0);   //微信小程序来源
                //3 查询奖品剩余情况
                RecyclePrize rPrize = new RecyclePrize();
                rPrize.setBatch(SystemConstant.NOW_PRIZE_BATCH);
                List<RecyclePrize> rList = recyclePrizeService.queryListByGrade(rPrize);
                //4 开始抽奖
                int prize = lottery(rList);
                PrizeRecord user = new PrizeRecord();
                user.setMobile(rw.getLotteryMobile());
                user.setWechatId(openId);
                user.setBatch(SystemConstant.NOW_PRIZE_BATCH);
                user.setIsGet(1);
                List<PrizeRecord> prizeRecords = prizeRecordService.queryList(user);
                //5 如果用户数据库中没有中奖记录，而且抽中了奖，则为成立，否则当未中奖处理
                if (prize != 0 && prizeRecords.isEmpty()) {
                    //中奖了  判断奖品是否还有
                    RecyclePrize p = new RecyclePrize();
                    p.setGrade(prize);
                    p.setBatch(SystemConstant.NOW_PRIZE_BATCH);
                    List<RecyclePrize> recyclePrizeList = recyclePrizeService.queryList(p);
                    if (recyclePrizeList.isEmpty()) {
                        throw new SystemException("系统繁忙，请稍后重试");
                    }
                    RecyclePrize prize1 = recyclePrizeList.get(0);
                    if (prize1.getTotalSum() <= prize1.getUseSum()) {
                        //奖品没有了  此次中奖当作未中奖处理
                        record.setIsGet(0);
                    } else {
                        //6 抽中了则将奖品更新为最新状态
                        if ((prize1.getUseSum() + 1) <= prize1.getTotalSum()) {
                            int i = recyclePrizeService.updateById(prize1.getPrizeId());
                            if (i != 1) {
                                tip = false;
                            }
                        }

                        currentPrizeState = 1;
                        if (tip) {
                            if (prize == 3) {
                                String commonCode = wechatUserService.createCoupon(SystemConstant.NOW_PRIZE_BATCH
                                        , SystemConstant.NOW_PRIZE_PRICE);
                                record.setCouponCode(commonCode);
                                SmsSendUtil.prizeSendToUser(rw.getLotteryMobile(), commonCode);
                                j.put("couponCode", commonCode);
                            }
                            j.put("prizeName", prize1.getPrizeName());
                            j.put("grade", prize1.getGrade());
                            j.put("inTime", prize1.getInTime());
                            String detail = prize1.getDetails();
                            if (detail.contains(";")) {
                                //分段显示
                                j.put("firstDetails", detail.substring(0, detail.indexOf(";")));
                                j.put("secondDetails", detail.substring(detail.indexOf(";") + 1));
                            } else {
                                j.put("firstDetails", detail);
                                j.put("secondDetails", "");
                            }
                            record.setIsGet(1);
                            record.setGrade(prize);
                            record.setPrizeId(prize1.getPrizeId());
                        }
                    }
                } else {
                    //未中奖
                    record.setIsGet(0);
                }
                //7 新增一条抽奖记录
                prizeRecordService.add(record);

                //8 修改用户抽奖记录信息
                rw.setTotalUse(rw.getTotalUse() - 1);
                rw.setLastPrizeTime(new Date());
                rw.setAlreadyUse(rw.getAlreadyUse() + 1);
                recycleWechatService.saveUpdate(rw);
                //抽奖了 返回可抽奖次数减1
                surplusLotteryNumber = surplusLotteryNumber - 1;
            }
            jsonResult.put("status", status);
            jsonResult.put("surplusLotteryNumber", surplusLotteryNumber);
            jsonResult.put("tip", tip);
            jsonResult.put("prizeMobile", rw.getLotteryMobile());
            jsonResult.put("shareState", shareState);
            jsonResult.put("currentPrizeState", currentPrizeState);
            jsonResult.put("prizeInfo", j);
            result.setResult(jsonResult);
            result.setResultCode("0");
            result.setSuccess(true);
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        return result;
    }


    /**
     * 奖品查询   若传参为手机号和微信openId可直接查询
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/recycle/queryPrize")
    @ResponseBody
    public ResultData queryPrize(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        boolean tip = false;    //判断是否需要验证码
        try {
            JSONObject params = getPrarms(request);
            String openId = params.getString("openId");
            if (StringUtils.isNotBlank(openId)) {
                tip = true;
            }
            //手机号和验证码
            String mobile = params.getString("mobile");
            String code = params.getString("code");
            if (!tip) {
                if (StringUtils.isBlank(mobile) || StringUtils.isBlank(code)) {
                    return getResult(result,null,false,"2","手机号或验证码为空");
                }
                if (!checkRandomCode(request, mobile, code)) { // 验证手机号和验证码
                    return getResult(result,null,false,"2","手机号或验证码输入错误");
                }
            }
            PrizeRecord r = new PrizeRecord();
            r.setMobile(mobile);
            if (tip) {
                r.setWechatId(openId);
            }
            r.setIsGet(1);   //1表示中奖的记录  一个手机号只会有一条中奖记录
            List<PrizeRecord> list = prizeRecordService.queryList(r);
            if (list.isEmpty()) {
                return getResult(result,null,false,"2","您当前没有中奖记录");
            }
            for (PrizeRecord t : list) {
                //活动的奖品信息  一个用户只会有一次中奖记录
                RecyclePrize prize = recyclePrizeService.queryById(t.getPrizeId());
                JSONObject j = new JSONObject();
                j.put("couponCode", t.getCouponCode());
                j.put("prizeName", prize.getPrizeName());
                j.put("grade", prize.getGrade());
                j.put("inTime", t.getInTime());
                String detail = prize.getDetails();
                if (detail.contains(";")) {
                    //分段显示
                    j.put("firstDetails", detail.substring(0, detail.indexOf(";")));
                    j.put("secondDetails", detail.substring(detail.indexOf(";") + 1));
                } else {
                    j.put("firstDetails", detail);
                    j.put("secondDetails", "");
                }
                jsonResult.put("prizeInfo", j);
            }
            result.setResult(jsonResult);
            result.setResultCode("0");
            result.setSuccess(true);
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        return result;
    }


    /**
     * 奖品查询   更具手机号和批次可直接查询
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/recycle/queryPrizeBatch")
    @ResponseBody
    public ResultData queryPrizeBatch(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        boolean tip = false;    //判断是否需要验证码
        try {
            JSONObject params = getPrarms(request);
            String batch = params.getString("batch");//批次
            //手机号和验证码
            String mobile = get(params.getString("mobile"));
            String code = get(params.getString("code"));
            if (!checkRandomCode(request, mobile, code)) { // 验证手机号和验证码
                result.setSuccess(false);
                throw new SystemException("手机号或验证码输入错误");
            }
            PrizeRecord r = new PrizeRecord();
            r.setMobile(mobile);
            r.setIsGet(1);   //1表示中奖的记录  一个手机号只会有一条中奖记录
            if (StringUtils.isNotBlank(batch)) {
                r.setBatch(batch);
            } else {
                r.setBatch("S001");
            }
            List<PrizeRecord> list = prizeRecordService.queryList(r);
            if (list.isEmpty()) {
                return getResult(result,null,false,"2","您当前没有中奖记录");
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            for (PrizeRecord t : list) {
                //活动的奖品信息  一个用户只会有一次中奖记录
                RecyclePrize prize = recyclePrizeService.queryById(t.getPrizeId());
                JSONObject j = new JSONObject();
                j.put("couponCode", t.getCouponCode());
                j.put("prizeName", prize.getPrizeName());
                j.put("grade", String.valueOf(prize.getGrade()));
                j.put("inTime", sdf.format(t.getInTime()));
                String detail = prize.getDetails();
                if (detail.contains(";")) {
                    //分段显示
                    j.put("firstDetails", detail.substring(0, detail.indexOf(";")));
                    j.put("secondDetails", detail.substring(detail.indexOf(";") + 1));
                } else {
                    j.put("firstDetails", detail);
                    j.put("secondDetails", "");
                }
                jsonResult.put("prizeInfo", j);
            }
            result.setResult(jsonResult);
            result.setResultCode("0");
            result.setSuccess(true);
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        return result;
    }


    /**
     * 保存欢GO用户抽奖手机号  通过集团欢GO的单点登录 通过ticket换取用户手机号
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/recycle/saveHappyMobile")
    @ResponseBody
    public ResultData saveHappyMobile(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        try {
            JSONObject params = getPrarms(request);
            //单点登录ticket
            String ticket = params.getString("ticket");
            if (StringUtils.isBlank(ticket)) {
                throw new SystemException("参数不完整");
            }
            String mobile = "";
            //通过集团webservice接口获取信息
            String data = HttpClientUtil.webService(SystemConstant.SINGLE_LOGIN_URL, getXMLTicket(ticket));
            //String data=getFailNews();   测试数据
            //String data=getSuccess();
            if (StringUtils.isBlank(data)) {
                throw new SystemException("单点请求获取信息失败");
            }
            //解析返回的xml文件
            XmlUtil xmlUtil = new XmlUtil();
            //先得到response
            Map parse = xmlUtil.parse(data);
            String repsonseData = parse.get("response").toString();
            if (StringUtils.isBlank(repsonseData)) {
                throw new SystemException("单点请求返回xml错误");
            }
            //得到 请求状态 RspType  0为成功 1失败
            Map parse1 = xmlUtil.parse(repsonseData);
            String rspType = parse1.get("RspType").toString();
            if (StringUtils.isBlank(rspType)) {
                throw new SystemException("单点请求返回xml错误");
            }
            //判断请求是否成功
            if (!rspType.equals("0")) {
                //失败
                String rspDesc = parse1.get("RspDesc").toString();
                throw new SystemException(rspDesc);
            }
            //获取手机号
            mobile = parse1.get("AccountID").toString();
            //获取重定向地址  流水号id
            String returnURL = parse1.get("ReturnURL").toString();
            String transactionId = parse1.get("TransactionID").toString();
            //存储登录信息
            SingleLogin singleLogin = singleLoginService.queryById(mobile);
            if (singleLogin == null) {
                SingleLogin s = new SingleLogin();
                s.setMobile(mobile);
                s.setTicket(ticket);
                s.setUrl(returnURL);
                s.setTransactionId(transactionId);
                singleLoginService.add(s);
            } else {
                singleLogin.setTransactionId(transactionId);
                singleLogin.setTicket(ticket);
                singleLoginService.saveUpdate(singleLogin);
            }


            //保存用户抽奖手机号
            HappyPrize happyPrize = happyPrizeService.queryByMobile(mobile);
            if (happyPrize == null) {
                //新增一个
                HappyPrize p = new HappyPrize();
                p.setLotteryMobile(mobile);
                p.setTotalUse(3);   //目前欢GO用户 一天最多只能抽3次奖
                happyPrizeService.add(p);
            }
            jsonResult.put("mobile", mobile);
            result.setResult(jsonResult);
            result.setResultCode("0");
            result.setSuccess(true);
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        return result;
    }

    /**
     * 单点返回错误样本
     *
     * @return
     */
    public String getFailNews() {
        String soap = "<?xml version=\"1.0\"?>\n" +
                "<soap:Envelope\n" +
                "    xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
                "    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
                "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "    <soap:Body>\n" +
                "        <ns1:accountInfoQueryResponse\n" +
                "            xmlns:ns1=\"http://www.chinatelecom.hub.com\">\n" +
                "            <ns1:response>\n" +
                "                <![CDATA[<CAPRoot><SessionHeader><TransactionID>13000201805183044198370</TransactionID><ActionCode>1</ActionCode><RspTime>20180822070820</RspTime><Response><RspType>1</RspType><RspCode>1992</RspCode><RspDesc>Ticket已超时</RspDesc></Response></SessionHeader></CAPRoot>]]>\n" +
                "            </ns1:response>\n" +
                "        </ns1:accountInfoQueryResponse>\n" +
                "    </soap:Body>\n" +
                "</soap:Envelope>";

        return soap;
    }

    /**
     * 单点返回正确样本
     *
     * @return
     */
    public String getSuccess() {
        String soap1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "\n" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">  \n" +
                "  <soap:Body> \n" +
                "    <ns1:accountInfoQueryResponse xmlns:ns1=\"http://www.chinatelecom.hub.com\">  \n" +
                "      <ns1:response> <![CDATA[<CAPRoot><SessionHeader><TransactionID>13000201805183044198370</TransactionID><ActionCode>1</ActionCode><RspTime>20180813032539</RspTime><Response><RspType>0</RspType><RspCode>0000</RspCode><RspDesc>Success</RspDesc></Response></SessionHeader><SessionBody><AssertionQueryResp><AccountType>2000004</AccountType><AccountID>18106538281</AccountID><PWDType>03</PWDType><ProvinceID>12</ProvinceID><ReturnURL>http://www.189.cn/tymh/address_plus.do</ReturnURL><PWDAttrList/><TrustedAccList/></AssertionQueryResp></SessionBody></CAPRoot>]]> </ns1:response> \n" +
                "    </ns1:accountInfoQueryResponse> \n" +
                "  </soap:Body> \n" +
                "</soap:Envelope>";

        return soap1;
    }


    /**
     * 生成请求xml
     *
     * @param ticket 单点登录ticket
     * @return 具体文档说明见集团客户端单点协议文档
     */
    public static String getXMLTicket(String ticket) {
        //请求时 SrcSysID（发起方(系统/平台)编码）   DstSysID（落地方(系统/平台)编码）
        //      ReqTime（请求时间 YYYYMMDDHH24MMSS） TransactionID（交易流水号） 会变化
        String SrcSysId = "12000";  //代表浙江
        String DstSysID = "35111";  //代表集团统一门户
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date now = new Date();
        String ReqTime = DateUtil.getSerialFullDate(now);
        // 发起方请求时生成并填写，该流水号全局唯一。
        //【5位系统编码（2位省份编码（附录3.3）+000）】+【8位日期编码YYYYMMDD】+【10位不重复的流水号】。
        String TransactionID = SrcSysId + sdf.format(now) + NOUtil.getNo("").substring(NOUtil.getNo("").length() - 10);

        String data = "<?xml version='1.0' encoding='utf-8'?>\n" +
                "<CAPRoot>\n" +
                "    <SessionHeader>\n" +
                "        <ServiceCode>CAP42003</ServiceCode>\n" +
                "        <Version>CAP4200320120518</Version>\n" +
                "        <ActionCode>0</ActionCode>\n" +
                "        <TransactionID>" + TransactionID + "</TransactionID>\n" +
                "        <SrcSysID>" + SrcSysId + "</SrcSysID>\n" +
                "        <DstSysID>" + DstSysID + "</DstSysID>\n" +
                "        <ReqTime>" + ReqTime + "</ReqTime>\n" +
                "        <DigitalSign />\n" +
                "    </SessionHeader>\n" +
                "    <SessionBody>\n" +
                "        <AssertionQueryReq>\n" +
                "            <Ticket>" + ticket + "</Ticket>\n" +
                "        </AssertionQueryReq>\n" +
                "    </SessionBody>\n" +
                "</CAPRoot>";
        return data;
    }


    /**
     * 抽奖算法  返回 1234分别代表一二三四等奖 0为未中奖
     */
    public static int lottery(List<RecyclePrize> list) {
        int tip = (int) (Math.random() * Integer.parseInt(SystemConstant.SECTION_COPIES));
        //System.out.println("当前抽奖随机数："+tip);
        int prize = 0;  //记录抽中的为几等奖

        //先算出所有奖品总概率区间值
        int sum = 0;
        for (RecyclePrize p : list) {
            int s = SystemConstant.getInt(p.getPrizeProbability());
            //System.out.println(p.getGrade()+"等级概率："+s);
            sum += s;
        }
        //System.out.println("总区间："+sum);
        //目前设置最多奖项为六等奖
        int first = SystemConstant.getInt(list.get(list.size() - 1).getPrizeProbability());    //    10
        int second = 0;
        int third = 0;
        int fourth = 0;
        int fifth = 0;
        int sixth = 0;
        if (list.size() > 1) {
            second = first + SystemConstant.getInt(list.get(list.size() - 2).getPrizeProbability());
        }
        if (list.size() > 2) {
            third = second + SystemConstant.getInt(list.get(list.size() - 3).getPrizeProbability());
        }
        if (list.size() > 3) {
            fourth = third + SystemConstant.getInt(list.get(list.size() - 4).getPrizeProbability());
        }
        if (list.size() > 4) {
            fifth = fourth + SystemConstant.getInt(list.get(list.size() - 5).getPrizeProbability());
        }
        if (list.size() > 5) {
            sixth = fifth + SystemConstant.getInt(list.get(list.size() - 6).getPrizeProbability());
        }
        //System.out.println(first+"  "+second+" "+third+"  "+fourth+"  "+fifth+"  "+sixth);

        //中奖了  判断为几等奖
        if (tip <= sum) {
            //判断一等奖
            if (tip <= first) {
                prize = 1;
                return prize;
            }
            //判断二等奖
            if (second != 0 && (tip > first && tip <= second)) {
                prize = 2;
                return prize;
            }
            //判断三等奖
            if (third != 0 && (tip > second && tip <= third)) {
                prize = 3;
                return prize;
            }
            //判断四等奖
            if (fourth != 0 && (tip > third && tip <= fourth)) {
                prize = 4;
                return prize;
            }
            //判断五等奖
            if (fifth != 0 && (tip > fourth && tip <= fifth)) {
                prize = 5;
                return prize;
            }
            //判断六等奖
            if (sixth != 0 && (tip > fifth && tip <= sixth)) {
                prize = 6;
                return prize;
            }
        }


        return prize;
    }

    public static void test() {
        List<RecyclePrize> list = new ArrayList<RecyclePrize>();
        list.add(new RecyclePrize(5, "0.3"));
        list.add(new RecyclePrize(4, "0.2"));
        list.add(new RecyclePrize(3, "0.2"));
        list.add(new RecyclePrize(2, "0.005"));
        list.add(new RecyclePrize(1, "0"));
        int sum = 0;
        int first = 0;
        int second = 0;
        int third = 0;
        int fourth = 0;
        for (int i = 0; i < 1000; i++) {
            sum = lottery(list);
            if (sum == 1) {
                first += 1;
            } else if (sum == 2) {
                second += 1;
            } else if (sum == 3) {
                third += 1;
            } else if (sum == 4) {
                fourth += 1;
            }
        }
        System.out.println("一等奖：" + first + " 二等奖：" + second + " 三等奖：" + third + " 四等奖：" + fourth);
    }


    public static void main(String[] args) {

        System.out.println(getXMLTicket("3213213"));

    }
}
