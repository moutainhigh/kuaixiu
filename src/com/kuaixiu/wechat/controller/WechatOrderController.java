package com.kuaixiu.wechat.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.kuaixiu.order.entity.*;
import com.kuaixiu.order.service.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.exception.SystemException;
import com.common.paginate.Page;
import com.common.util.CookiesUtil;
import com.common.util.DateUtil;
import com.common.util.MD5Util;
import com.common.wechat.api.WxMpService;
import com.common.wechat.bean.result.WxMpOAuth2AccessToken;
import com.common.wechat.common.bean.WxJsapiSignature;
import com.common.wechat.common.exception.WxErrorException;
import com.common.wechat.common.util.StringUtils;
import com.kuaixiu.brand.entity.Brand;
import com.kuaixiu.brand.service.BrandService;
import com.kuaixiu.coupon.entity.Coupon;
import com.kuaixiu.coupon.entity.CouponModel;
import com.kuaixiu.coupon.entity.CouponProject;
import com.kuaixiu.coupon.service.CouponModelService;
import com.kuaixiu.coupon.service.CouponProjectService;
import com.kuaixiu.coupon.service.CouponService;
import com.kuaixiu.customer.entity.Customer;
import com.kuaixiu.customer.service.CustomerService;
import com.kuaixiu.engineer.entity.Engineer;
import com.kuaixiu.engineer.service.EngineerService;
import com.kuaixiu.oldtonew.entity.Agreed;
import com.kuaixiu.oldtonew.entity.NewOrder;
import com.kuaixiu.oldtonew.entity.OldToNewUser;
import com.kuaixiu.oldtonew.entity.OrderShow;
import com.kuaixiu.oldtonew.entity.UpdateTimeCompare;
import com.kuaixiu.oldtonew.service.AgreedService;
import com.kuaixiu.oldtonew.service.NewOrderService;
import com.kuaixiu.oldtonew.service.OldToNewService;
import com.kuaixiu.order.constant.OrderConstant;
import com.kuaixiu.project.entity.CancelReason;
import com.kuaixiu.project.entity.Project;
import com.kuaixiu.project.service.CancelReasonService;
import com.kuaixiu.project.service.ProjectService;
import com.kuaixiu.screen.entity.ScreenOrder;
import com.kuaixiu.screen.service.ScreenOrderService;
import com.kuaixiu.shop.entity.Shop;
import com.kuaixiu.shop.service.ShopService;
import com.kuaixiu.wechat.entity.WechatUser;
import com.kuaixiu.wechat.service.WechatUserService;
import com.system.api.entity.ResultData;
import com.system.basic.user.entity.LoginUser;
import com.system.basic.user.entity.SessionUser;
import com.system.basic.user.entity.SysUser;
import com.system.basic.user.service.LoginUserService;
import com.system.basic.user.service.SessionUserService;
import com.system.basic.user.service.SysUserService;
import com.system.constant.ApiResultConstant;
import com.system.constant.SystemConstant;
import com.system.util.SystemUtil;

/**
 * 微信订单controller
 *
 * @author yq
 */
@Controller
public class WechatOrderController extends BaseController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDetailService detailService;
    @Autowired
    private OrderCommentService commentService;
    @Autowired
    private SysUserService userService;
    @Autowired
    private SessionUserService sessionUserService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private ShopService shopService;
    @Autowired
    private OrderPayService orderPayService;
    @Autowired
    private WxMpService wxMpService;
    @Autowired
    private NewOrderService newOrderService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private OldToNewService oldToNewService;
    @Autowired
    private EngineerService engineerService;
    @Autowired
    private AgreedService agreedService;
    @Autowired
    private CouponService couponService;
    @Autowired
    private CancelReasonService cancelReasonService;
    @Autowired
    private LoginUserService loginUserService;
    @Autowired
    private ScreenOrderService screenOrderService;
    @Autowired
    private WechatUserService wechatUserService;
    @Autowired
    private CouponModelService couponModelService;
    @Autowired
    private CouponProjectService couponProjectService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private BrandService brandService;


    private static final Logger log = Logger.getLogger(WechatOrderController.class);
    //手机识别
    private final static String[] agent = {"android", "iphone", "ipod", "ipad", "windows phone", "mqqbrowser"};

    private static boolean checkAgentIsMobile(String ua) {
        boolean flag = false;
        if (!ua.contains("windows nt")
                || (ua.contains("windows nt")
                && ua.contains("compatible; msie 9.0;"))) {
            // 排除 苹果桌面系统
            if (!ua.contains("windows nt") && !ua.contains("macintosh")) {
                for (String item : agent) {
                    if (ua.contains(item)) {
                        flag = true;
                        break;
                    }
                }
            }
        }
        return flag;
    }


    /**
     * 检测该微信openId下access_token是否有效
     *
     * @throws IOException
     * @throws WxErrorException
     **/
    @RequestMapping("/wechat/order/wechatLogin")
    public String userLogin(HttpServletRequest request, HttpServletResponse response) throws IOException, WxErrorException {
        ResultData result = new ResultData();
        JSONObject j = new JSONObject();
        String ua = request.getHeader("user-agent").toLowerCase();
        SessionUser sessionUser = getCurrentUser(request);
        //没有登录
        if (sessionUser == null || sessionUser.getType() != SystemConstant.USER_TYPE_CUSTOMER) {
            if (ua.indexOf("micromessenger") > 0) {
                String code = request.getParameter("code");//微信端验证
                //判断是否因服务器的断开而导致的登录记录还存在
                Cookie cookie = CookiesUtil.getCookieByName(request, "is_login");
                if (cookie != null && !StringUtils.isBlank(cookie.getValue()) && StringUtils.isBlank(code)) {
                    j.put("is_login", 3);//服务器的重启重新登录
                    result.setResult(j);
                } else {
                    if (StringUtils.isBlank(code)) {
                        String url = request.getHeader("Referer");//微信回调地址
                        url = URLEncoder.encode(url, "UTF-8");
                        url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + SystemConstant.APP_ID
                                + "&redirect_uri=" + url + "&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect";
                        return "redirect:" + url;
                    }
                    WxMpOAuth2AccessToken token = this.wxMpService.oauth2getAccessToken(code);      //通过code获取用户的openId
                    LoginUser loginUser = loginUserService.findLoginUserByOpenId(token.getOpenId()); //查找该openId下的用户
                    Boolean tip = loginUserService.findLoginUserInDate(loginUser);//判断该用户accessToken是否在有效期内
                    request.getSession().setAttribute(SystemConstant.SESSION_OPENID, token.getOpenId());//存下openId
                    if (tip) {
                        //token在有效期内
                        SysUser user = userService.checkWechatLogin(loginUser.getLoginId());
                        //如果该记录sessionId和现在的不一样则更新
                        if (!loginUser.getSessionId().equals(request.getSession().getId())) {
                            loginUser.setSessionId(request.getSession().getId());
                            loginUserService.updateLoginUser(loginUser);
                        }
                        sessionUserService.customerInitSessionUser(user, request, loginUser.getAccessToken());//初始化session
                        j.put("is_login", 1);//token有效
                        j = getLoginNews(request, j);//得到已登录用户信息
                        result.setResult(j);
                    } else {
                        j.put("is_login", 0);//token失效
                        result.setResult(j);
                    }
                }


            } else {
                j.put("is_login", 0); //其他端
                result.setResult(j);
            }
        } else {
            j.put("is_login", 1); //session存在
            j = getLoginNews(request, j);
            result.setResult(j);
        }
        result.setResultCode("0");
        result.setSuccess(true);
        renderJson(response, result);
        return null;
    }


    /**
     * 得到已登录用户信息
     */
    public JSONObject getLoginNews(HttpServletRequest request, JSONObject jsonResult) {
        SessionUser sessionUser = getCurrentUser(request); //得到当前用户
        Customer cust = customerService.queryById(sessionUser.getUserId());
        jsonResult.put("mobile", cust.getMobile());
        jsonResult.put("access_token", request.getSession().getAttribute("accessToken"));
        return jsonResult;
    }


    /**
     * 微信登录验证
     *
     * @param request
     */
    @RequestMapping("/wechat/order/checkLogin")
    @ResponseBody
    public void checkLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResultData result = new ResultData();
        String ua = request.getHeader("user-agent").toLowerCase();
        JSONObject j = new JSONObject();
        SysUser user = null;
        LoginUser login = null;
        try {
            JSONObject params = getPrarms(request);
            String mobile = params.getString("mobile");
            String validateCode = params.getString("checkCode");
            user = userService.checkWechatLogin(mobile);
            String sessionId = (String) request.getSession().getId();//当前sesionId
            if (!checkRandomCode(request, mobile, validateCode)) {
                result.setSuccess(false);
                result.setResultMessage("验证码错误");
            } else if (user == null) {
                result.setSuccess(false);
                result.setResultMessage("该手机用户不存在");
            } else {
                if (ua.indexOf("micromessenger") > 0) {//微信端登录会默认保存7天登录
                    String openId = (String) request.getSession().getAttribute(SystemConstant.SESSION_OPENID);
                    login = loginUserService.wechatInitLoginUser(user, openId, sessionId); //更新用户信息
                } else if (checkAgentIsMobile(ua)) {//手机端（不包括微信端）的登录会限制一处登录
                    login = loginUserService.pcInitLoginUser(user, sessionId);
                } else {
                    //pc端  不用保存
                    String token = MD5Util.md5Encode(mobile + DateUtil.getNowyyyyMMddHHmmssSSS(), SystemUtil.getSysCfgProperty(SystemConstant.ACCESS_TOKEN_SALT_KEY));
                    login = new LoginUser();
                    login.setAccessToken(token);
                }
                if (login != null) {
                    j.put("access_token", login.getAccessToken());
                    result.setResult(j);
                }
                result.setSuccess(true);
                result.setResultMessage("登录成功");
                result.setResultCode("0");
                sessionUserService.customerInitSessionUser(user, request, login.getAccessToken());
            }
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        renderJson(response, result);
    }


    /**
     * 支付所需参数   生成当前网页微信支付有关参数
     * 包含appid公众号id   timestamp当前时间戳    nonceStr前面随机字符串
     * signature微信签名     code微信网页授权code
     *
     * @throws WxErrorException
     * @throws IOException
     **/
    @RequestMapping("/wechat/order/payBalance")
    public String payBalance(HttpServletRequest request, HttpServletResponse response) throws WxErrorException, IOException {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        try {
            JSONObject params = getPrarms(request);
            String id = params.getString("id");
            Order order = orderService.queryById(id);                         //维修订单
            NewOrder newOrder = (NewOrder) newOrderService.queryById(id);       //以旧换新订单
            ScreenOrder screen = screenOrderService.queryById(id);              //碎屏险订单
            //通过id判断订单只会得到唯一的订单 或者不存在
            if (order == null && newOrder == null & screen == null) {
                throw new SystemException("订单不存在");
            }
            if (screen != null) {
                //当订单为碎屏险订单时，因未涉及到登录 故不考虑验证当前用户的判断
            } else {
                getLoginUser(request);//验证token
                SessionUser sessionUser = getCurrentUser(request); //得到当前用户
            }

            String code = params.getString("code");//获取微信授权code
            if (StringUtils.isBlank(code)) {
                String url = request.getHeader("Referer");
                url = URLEncoder.encode(url, "UTF-8");
                url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + SystemConstant.APP_ID
                        + "&redirect_uri=" + url + "&response_type=code&scope=snsapi_base&state=STATE&connect_redirect=1#wechat_redirect";
                return "redirect:" + url;
            }
            String url = getRequestUrl(request);
            WxJsapiSignature wxJsapiSign = wxMpService.createJsapiSignature(url);  //得到微信签名等参数
            JSONObject j = new JSONObject();
            j.put("appId", wxJsapiSign.getAppid());
            j.put("timestamp", wxJsapiSign.getTimestamp());
            j.put("noncestr", wxJsapiSign.getNoncestr());
            j.put("signature", wxJsapiSign.getSignature());
            jsonResult.put("code", code);
            jsonResult.put("wxJsapiSign", j);
            sessionUserService.getSuccessResult(result, jsonResult);
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        renderJson(response, result);
        return null;
    }

    /**
     * 发起支付余款
     *
     * @param request
     * @param response
     * @throws IOException
     * @throws WxErrorException
     * @CreateDate: 2016-9-17 上午12:13:53
     */
    @RequestMapping("/wechat/order/startPayBalance")
    public void startPayBalance(HttpServletRequest request, HttpServletResponse response) throws IOException, WxErrorException {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        try {
            JSONObject params = getPrarms(request);  //获取请求参数
            String id = params.getString("id");
            String code = params.getString("code");
            if (StringUtils.isBlank(code)) {
                throw new SystemException("获取微信支付参数失败!");
            }
            Order order = orderService.queryById(id);                         //维修订单
            NewOrder newOrder = (NewOrder) newOrderService.queryById(id);       //以旧换新订单
            ScreenOrder screen = screenOrderService.queryById(id);              //碎屏险订单
            //通过id判断订单只会得到唯一的订单 或者不存在
            if (order == null && newOrder == null & screen == null) {
                throw new SystemException("订单不存在");
            }
            SessionUser su = null;
            if (screen != null) {
                //当订单为碎屏险订单时，因未涉及到登录 故不考虑验证当前用户的判断
            } else {
                getLoginUser(request);//验证token
                su = getCurrentUser(request); //得到当前用户
            }

            WxMpOAuth2AccessToken accessToken = getAccessToken(request, code);
            String openid = accessToken.getOpenId();
            if (StringUtils.isBlank(openid)) {
                throw new SystemException(ApiResultConstant.resultCode_str_1008, ApiResultConstant.resultCode_1008);
            }
            String ip = getIpAddress(request); //获取用户请求的主机IP地址
            System.out.println(ip);
            OrderPayLog payLog = new OrderPayLog();
            payLog.setTradeType("JSAPI");
            payLog.setSpbillCreateIp(ip);
            payLog.setBody("M-超人-支付余款");
            payLog.setAttach("balance");
            payLog.setOpenid(openid);
            payLog.setExpenseType(OrderConstant.ORDER_EXPENSE_TYPE_BALANCE);

            if (order != null) {                       //维修订单支付
                payLog = orderPayService.startWechatPay(id, payLog, su);
            } else if (newOrder != null) {              //以旧换新支付
                payLog = orderPayService.newOrderStartWechatPay(id, payLog, su);
            } else {                                 //碎屏险订单支付
                payLog = orderPayService.screenWechatPay(id, payLog);
            }

            if (payLog.getPayStatus() == OrderConstant.ORDER_PAY_STATUS_SUBMITED) {
                try {     //提交成功
                    //获取JsApi签名
                    Map<String, String> paySign = wxMpService.getPayService().getJsapiPaySign(payLog.getPrepayId());
                    jsonResult.put("paySign", paySign);
                    jsonResult.put("pay_status", payLog.getPayStatus());
                    sessionUserService.getSuccessResult(result, jsonResult);
                } catch (Exception e) {
                    e.printStackTrace();
                    result.setSuccess(false);
                    result.setResultCode(ApiResultConstant.resultCode_5001);
                    result.setResultMessage("生成支付签名错误，请稍后再试.");
                }
            } else if (payLog.getPayStatus() == OrderConstant.ORDER_PAY_STATUS_SUCCESS) {
                result.setSuccess(true);
                result.setResultMessage("订单已支付");
                jsonResult.put("pay_status", payLog.getPayStatus());
                result.setResult(jsonResult);
            } else {
                result.setSuccess(false);
                result.setResultMessage(payLog.getErrCodeDes());
            }
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (WxErrorException e) {
            e.printStackTrace();  //一般为code过期
            result.setResultCode(ApiResultConstant.resultCode_1008);
            result.setSuccess(false);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        renderJson(response, result);
    }

    /**
     * code如果与之前存入session的code一致  则根据情况 直接调用或调用refreshToken向微信服务器更新accessToken
     * code如果与之前存入session的code不一致 则向微信服务器发起获取accessToken的请求
     *
     * @param request
     * @param code
     * @return
     * @throws WxErrorException
     */
    public WxMpOAuth2AccessToken getAccessToken(HttpServletRequest request, String code) throws WxErrorException {
        WxMpOAuth2AccessToken accessToken = null;
        if (code.equals(request.getSession().getAttribute("code"))) {
            //之前存入的token对象
            WxMpOAuth2AccessToken token = (WxMpOAuth2AccessToken) request.getSession().getAttribute("WX_CODE");
            //查看accessToken是否需要更新新
            long time = System.currentTimeMillis() - (long) request.getSession().getAttribute("tokenInTime");
            if (time >= 2 * 3600 * 1000) {
                //使用refreshToken刷新获取  refreshToken有效期为30天
                accessToken = this.wxMpService.oauth2refreshAccessToken(token.getRefreshToken());
                request.getSession().setAttribute("code", code);
                request.getSession().setAttribute("tokenInTime", System.currentTimeMillis());//存入当前时间戳
                request.getSession().setAttribute("WX_CODE", accessToken);
            } else {
                accessToken = token;
            }
        } else {
            accessToken = this.wxMpService.oauth2getAccessToken(code);
            //换取成功  存入session   再次请求该方法时 直接调用存入的数据
            request.getSession().setAttribute("code", code);
            request.getSession().setAttribute("tokenInTime", System.currentTimeMillis());//存入当前时间戳
            request.getSession().setAttribute("WX_CODE", accessToken);
        }
        return accessToken;
    }


    /**
     * 得到订单列表
     *
     * @throws IOException
     */
    @RequestMapping("/wechat/order/getOrderList")
    @ResponseBody
    public void getOrderList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResultData result = new ResultData();
        try {
            JSONObject params = getPrarms(request);
            String pageIndex = params.getString("pageIndex");//页码数
            String pageSize = params.getString("pageSize");//每页加载数
            String orderStatus = params.getString("status");
            String newOrderStatus = params.getString("newStatus");
            getLoginUser(request);//验证token
            SessionUser sessionUser = getCurrentUser(request); //得到当前用户
//            String currentUserId = sessionUser.getUserId();//当前用户业务ID
            Order order = new Order();
            List<Object> statusL = new ArrayList<Object>();
            if (StringUtils.isNotBlank(orderStatus)) {
                String[] statusArray = orderStatus.split(",");
                for (String s : statusArray) {
                    statusL.add(s);
                }
            }
            order.setQueryStatusArray(statusL);
            Page p = getPageByRequestParams(pageIndex, pageSize);
            order.setPage(p);
            List<Order> orderPage = orderService.getDao().queryListApiForPage(order);
//            List<OrderShow> orderPage = getList(currentUserId, request, p, orderStatus, newOrderStatus); //得到实际返回的数据
            int orderSize = p.getRecordsTotal();//总记录数
            JSONArray jsonArray = getReworkJsonResult(orderPage);//封装成json数组返回
            JSONObject jsonResult = new JSONObject();
            jsonResult.put("orderList", jsonArray);
            jsonResult.put("orderSize", orderSize);
            sessionUserService.getSuccessResult(result, jsonResult);
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        renderJson(response, result);
    }


    /**
     * 加载实际返回数据
     *
     * @throws Exception
     */
    public List<OrderShow> getList(String currentUserId, HttpServletRequest request, Page p, String orderStatus, String newOrderStatus) throws Exception {
        //根据前端传送的订单状态值 返回满足条件的订单  如果值为空则返回所有状态
        Order obj_order = new Order();
        obj_order.setCustomerId(currentUserId);
        List<Object> statusL = new ArrayList<Object>();
        if (StringUtils.isNotBlank(orderStatus)) {
            String[] statusArray = orderStatus.split(",");
            for (String s : statusArray) {
                statusL.add(s);
            }
        }
        obj_order.setQueryStatusArray(statusL);
        obj_order.setOrderBy(" t.order_status asc, t.in_time desc ");
        List<Order> orderList = orderService.queryList(obj_order);            //得到维修订单


        NewOrder newOrder = new NewOrder();
        newOrder.setCustomerId(currentUserId);
        List<Object> newStatusL = new ArrayList<Object>();
        if (StringUtils.isNotBlank(newOrderStatus)) {
            String[] statusArray = newOrderStatus.split(",");
            for (String s : statusArray) {
                newStatusL.add(s);
            }
        }
        newOrder.setQueryStatusArray(newStatusL);
        newOrder.setOrderBy(" t.order_status asc, t.in_time desc ");
        newOrder.setOrderStatus(0);//未删除的订单
        List<NewOrder> newOrderList = newOrderService.queryList(newOrder);      //得到以旧换新订单


        List<OrderShow> orderShow = sortOrder(orderList, newOrderList);          //排序两种订单方便显示
        p.setRecordsTotal(orderShow.size());                                  //用户名下订单总数
        List<OrderShow> orderPage = new ArrayList<OrderShow>();                 //分页显示,用于分页显示的集合
        int start = p.getStart() * p.getPageSize();
        int end = (p.getStart() + 1) * p.getPageSize();
        //如果纪录数少于每页显示数      
        if (orderShow.size() <= p.getPageSize() && start == 1) {
            for (int i = start; i < end; i++) {
                orderPage.add(orderShow.get(i));
            }
        } else if (orderShow.size() > end) {
            for (int i = start; i < end; i++) {
                orderPage.add(orderShow.get(i));
            }
        } else if (orderShow.size() >= start && orderShow.size() <= end) {
            for (int i = start; i < orderShow.size(); i++) {
                orderPage.add(orderShow.get(i));
            }
        }
        return orderPage;
    }


    /**
     * 包装两种订单用于排序显示
     */
    public List<OrderShow> sortOrder(List<Order> orderList, List<NewOrder> newOrderList) {
        //创建一个共类集合
        List<OrderShow> orderShow = new ArrayList<OrderShow>();
        if (newOrderList != null && newOrderList.size() > 0) {
            for (NewOrder order : newOrderList) {
                OrderShow show = new OrderShow();
                show.setOrderNo(order.getOrderNo());
                show.setUserId(order.getUserId());
                show.setOrderType(1);//表以旧换新订单
                show.setUpdateTime(order.getUpdateTime());
                orderShow.add(show);
            }
        }
        if (orderList != null && orderList.size() > 0) {
            for (Order order : orderList) {
                OrderShow show = new OrderShow();
                show.setOrderType(0);//0表示维修订单
                show.setOrderNo(order.getOrderNo());
                show.setUpdateTime(order.getUpdateTime());
                orderShow.add(show);
            }
        }
        //将得到的集合按更新时间递减排序
        Collections.sort(orderShow, new UpdateTimeCompare());
        return orderShow;
    }

    /**
     * 将得到集合包装成json数组返回
     */
    public JSONArray getReworkJsonResult(List<Order> orders) {
        JSONArray json = new JSONArray();
        for (Order order : orders) {
            JSONObject j = new JSONObject();
            //维修订单
            j.put("id", order.getId());
            j.put("orderNo", order.getOrderNo());
            j.put("inTime", order.getInTime());
            j.put("isComment", order.getIsComment());
            j.put("mobile", order.getMobile());
            j.put("customerName", order.getCustomerName());
            j.put("modelName", order.getModelName());
            j.put("orderStatus", order.getOrderStatus());
            j.put("color", order.getColor());
            j.put("realPrice", order.getRealPrice());
            j.put("shopName", order.getShopName());
            j.put("is_rework", order.getIsRework());
            List<OrderDetail> detail = detailService.queryByOrderNo(order.getOrderNo());
            JSONArray jsonDetails = new JSONArray();
            for (OrderDetail od : detail) {
                JSONObject item = new JSONObject();
                item.put("type", od.getType());
                item.put("projectId", od.getProjectId());
                item.put("projectName", od.getProjectName());
                item.put("price", od.getRealPrice());
                jsonDetails.add(item);
            }
            j.put("projects", jsonDetails);
            json.add(j);
        }
        return json;
    }

    /**
     * 将得到集合包装成json数组返回
     */
    public JSONArray getJsonResult(List<OrderShow> orderShow) {
        JSONArray json = new JSONArray();
        for (OrderShow o : orderShow) {
            JSONObject j = new JSONObject();
            if (o.getOrderType() == 0) {
                //维修订单
                Order order = orderService.queryByOrderNo(o.getOrderNo());
                j.put("id", order.getId());
                j.put("orderNo", order.getOrderNo());
                j.put("inTime", order.getInTime());
                j.put("isComment", order.getIsComment());
                j.put("mobile", order.getMobile());
                j.put("customerName", order.getCustomerName());
                j.put("modelName", order.getModelName());
                j.put("orderStatus", order.getOrderStatus());
                j.put("orderType", o.getOrderType());
                j.put("color", order.getColor());
                j.put("realPrice", order.getRealPrice());
                j.put("isUpdatePrice", order.getIsUpdatePrice());
                j.put("shopName", order.getShopName());
                List<OrderDetail> detail = detailService.queryByOrderNo(order.getOrderNo());
                JSONArray jsonDetails = new JSONArray();
                for (OrderDetail od : detail) {
                    JSONObject item = new JSONObject();
                    item.put("type", od.getType());
                    item.put("projectId", od.getProjectId());
                    item.put("projectName", od.getProjectName());
                    item.put("price", od.getRealPrice());
                    jsonDetails.add(item);
                }
                j.put("projects", jsonDetails);
            } else {
                //换新订单
                NewOrder newOrder = newOrderService.queryByOrderNo(o.getOrderNo());
                j.put("id", newOrder.getId());
                j.put("orderNo", newOrder.getOrderNo());
                j.put("inTime", newOrder.getInTime());
                j.put("orderStatus", newOrder.getOrderStatus());
                j.put("orderType", o.getOrderType());
                j.put("realPrice", newOrder.getRealPrice());
                j.put("isComment", newOrder.getIsComment());
                j.put("selectType", newOrder.getSelectType());
                Shop shop = shopService.queryByCode(newOrder.getShopCode());
                if (shop != null) {
                    j.put("shopName", shop.getName());
                }
                OldToNewUser old = oldToNewService.queryById(o.getUserId());
                if (old != null) {
                    j.put("oldModel", old.getOldMobile());
                    j.put("newModel", old.getNewMobile());
                    j.put("mobile", old.getTel());
                    j.put("customerName", old.getName());
                }
                Agreed agreed = agreedService.queryByOrderNo(o.getOrderNo());
                if (agreed != null) {
                    j.put("isAgreed", 0);
                    JSONObject item = new JSONObject();
                    item.put("agreedModel", agreed.getNewModelName());
                    item.put("agreedPrice", agreed.getAgreedOrderPrice());
                    item.put("color", agreed.getColor());
                    item.put("edition", agreed.getEdition());
                    item.put("memory", agreed.getMemory());
                    j.put("agreedNews", item);
                } else {
                    j.put("isAgreed", 1);
                }
            }
            json.add(j);
        }
        return json;
    }


    /**
     * 订单详情
     *
     * @throws IOException
     **/
    @RequestMapping("/wechat/order/orderDetail")
    public void orderDetail(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResultData result = new ResultData();
        String attribute = (String) request.getSession().getAttribute(SystemConstant.SESSION_OPENID);
        System.out.println("当前登录用户openid：" + attribute);
        try {
            getLoginUser(request);//验证token
            SessionUser sessionUser = getCurrentUser(request); //得到当前用户
            JSONObject params = getPrarms(request);
            String id = params.getString("id");
            Integer isRework = params.getInteger("isRework");
            JSONObject jsonResult = new JSONObject();
            JSONObject j = new JSONObject();
            if (1 == isRework) {
                ReworkOrder reworkOrder = reworkOrderService.queryById(id);
                if (reworkOrder == null) {
                    throw new SystemException(ApiResultConstant.resultCode_str_3003, ApiResultConstant.resultCode_3003);
                }
                Order order = orderService.queryByOrderNo(reworkOrder.getParentOrder());
                getReworkDetailJson(order, reworkOrder, j);
            } else {
                Order order = orderService.queryById(id);
                NewOrder newOrder = (NewOrder) newOrderService.queryById(id);
                if (order == null && newOrder == null) {
                    throw new SystemException(ApiResultConstant.resultCode_str_3003, ApiResultConstant.resultCode_3003);
                }
                //只能查看用户自己名下的订单
                if (order != null && order.getCustomerId().equals(sessionUser.getUserId())) {                                                       //维修订单
                    j = getDetailJson(order, j);
                } else if (newOrder != null && newOrder.getCustomerId().equals(sessionUser.getUserId())) {                                                                 //换新订单
                    j = getNewDetailJson(newOrder, j);
                } else {
                    throw new SystemException("对不起，您无权操作该订单");
                }

            }
            jsonResult.put("order", j);
            sessionUserService.getSuccessResult(result, jsonResult);
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        renderJson(response, result);
    }


    /**
     * 返修订单详情Json格式包装
     */
    public JSONObject getReworkDetailJson(Order order, ReworkOrder reworkOrder, JSONObject j) {
        j.put("id", reworkOrder.getId());
        j.put("orderNo", reworkOrder.getOrderReworkNo());
        j.put("inTime", reworkOrder.getInTime());
        j.put("isComment", reworkOrder.getIsComment());
        j.put("customerName", order.getCustomerName());
        j.put("fullAddress", order.getFullAddress());
        j.put("mobile", order.getMobile());
        j.put("modelName", order.getModelName());
        j.put("orderStatus", reworkOrder.getOrderStatus());
        j.put("orderType", 0);
        j.put("color", order.getColor());
        j.put("realPrice", reworkOrder.getRealPrice());
        j.put("shopName", order.getShopName());
        j.put("endTime", reworkOrder.getEndTime());
        j.put("repairType", order.getRepairType());
        Shop shop = new Shop();
        if (order.getShopCode().contains(",")) {
            List<String> shopCodeList = Arrays.asList(order.getShopCode().split(","));
            shop = shopService.queryByCode(shopCodeList.get(0));
        } else {
            shop = shopService.queryByCode(order.getShopCode());
        }
        if (shop != null) {
            j.put("shopName", shop.getName());
            j.put("shopFullAddress", shop.getFullAddress());
            j.put("shopTel", shop.getTel());
            j.put("shopManagerName", shop.getManagerName());
            j.put("shopManagerMobile", shop.getManagerMobile());
        }
        List<OrderDetail> detail = detailService.queryByOrderNo(order.getOrderNo());
        JSONArray jsonDetails = new JSONArray();
        for (OrderDetail od : detail) {
            JSONObject item = new JSONObject();
            item.put("id", od.getId());
            item.put("orderNo", od.getOrderNo());
            item.put("type", od.getType());
            item.put("projectId", od.getProjectId());
            item.put("projectName", od.getProjectName());
            item.put("price", od.getRealPrice());
            jsonDetails.add(item);
        }
        j.put("details", jsonDetails);


        return j;
    }

    /**
     * 维修订单详情Json格式包装
     */
    public JSONObject getDetailJson(Order order, JSONObject j) {
        j.put("id", order.getId());
        j.put("orderNo", order.getOrderNo());
        j.put("inTime", order.getInTime());
        j.put("isComment", order.getIsComment());
        j.put("customerName", order.getCustomerName());
        j.put("fullAddress", order.getFullAddress());
        j.put("mobile", order.getMobile());
        j.put("modelName", order.getModelName());
        j.put("orderStatus", order.getOrderStatus());
        j.put("orderType", 0);
        j.put("color", order.getColor());
        j.put("realPrice", order.getRealPrice());
        j.put("shopName", order.getShopName());
        j.put("endTime", order.getEndTime());
        j.put("repairType", order.getRepairType());
        Shop shop = new Shop();
        if (order.getShopCode().contains(",")) {
            List<String> shopCodeList = Arrays.asList(order.getShopCode().split(","));
            shop = shopService.queryByCode(shopCodeList.get(0));
        } else {
            shop = shopService.queryByCode(order.getShopCode());
        }
        if (shop != null) {
            j.put("shopName", shop.getName());
            j.put("shopFullAddress", shop.getFullAddress());
            j.put("shopTel", shop.getTel());
            j.put("shopManagerName", shop.getManagerName());
            j.put("shopManagerMobile", shop.getManagerMobile());
        }
        //优惠券信息
        j.put("isUseCoupon", order.getIsUseCoupon());
        if (order.getIsUseCoupon() == 1) {
            JSONObject c = new JSONObject();
            c.put("couponCode", order.getCouponCode());
            c.put("couponPrice", order.getCouponPrice());
            c.put("beginTime", order.getBalanceTime());
            c.put("endTime", order.getEndTime());
            c.put("note", order.getNote());
            j.put("coupon", c);
        }
        List<OrderDetail> detail = detailService.queryByOrderNo(order.getOrderNo());
        JSONArray jsonDetails = new JSONArray();
        for (OrderDetail od : detail) {
            JSONObject item = new JSONObject();
            item.put("id", od.getId());
            item.put("orderNo", od.getOrderNo());
            item.put("type", od.getType());
            item.put("projectId", od.getProjectId());
            item.put("projectName", od.getProjectName());
            item.put("price", od.getRealPrice());
            jsonDetails.add(item);
        }
        j.put("details", jsonDetails);


        return j;
    }

    /**
     * 以旧换新订单详情Json包装格式
     */
    public JSONObject getNewDetailJson(NewOrder newOrder, JSONObject j) {
        j.put("id", newOrder.getId());
        j.put("orderNo", newOrder.getOrderNo());
        j.put("inTime", newOrder.getInTime());
        j.put("orderStatus", newOrder.getOrderStatus());
        j.put("orderType", 1);
        j.put("realPrice", newOrder.getRealPrice());
        j.put("isComment", newOrder.getIsComment());
        j.put("endTime", newOrder.getEndTime());
        j.put("selectType", newOrder.getSelectType());
        Shop shop = shopService.queryByCode(newOrder.getShopCode());
        if (shop != null) {
            j.put("shopName", shop.getName());
            j.put("shopFullAddress", shop.getFullAddress());
            j.put("shopTel", shop.getTel());
            j.put("shopManagerName", shop.getManagerName());
            j.put("shopManagerMobile", shop.getManagerMobile());
        }
        OldToNewUser old = oldToNewService.queryById(newOrder.getUserId());
        if (old != null) {
            j.put("oldModel", old.getOldMobile());
            j.put("newModel", old.getNewMobile());
            j.put("mobile", old.getTel());
            j.put("customerName", old.getName());
            j.put("fullAddress", old.getHomeAddress());
        }
        Agreed agreed = agreedService.queryByOrderNo(newOrder.getOrderNo());
        if (agreed != null) {
            j.put("isAgreed", 0);
            JSONObject item = new JSONObject();
            item.put("agreedModel", agreed.getNewModelName());
            item.put("agreedPrice", agreed.getAgreedOrderPrice());
            item.put("color", agreed.getColor());
            item.put("edition", agreed.getEdition());
            item.put("memory", agreed.getMemory());
            j.put("agreedNews", item);
        } else {
            j.put("isAgreed", 1);
        }
        return j;
    }


    /**
     * 关闭微信订单
     *
     * @throws IOException
     **/
    @RequestMapping("/wechat/order/cancelWechatOrder")
    public void cancelWechatOrder(HttpServletRequest request,
                                  HttpServletResponse response) throws IOException {
        ResultData result = new ResultData();
        try {
            SessionUser su = getCurrentUser(request);
            if (su == null) {
                throw new SystemException("系统异常请稍后重试");
            }
            JSONObject params = getPrarms(request);
            String id = params.getString("orderId");
            Order order = orderService.queryById(id);
            NewOrder newOrder = (NewOrder) newOrderService.queryById(id);
            if (order == null && newOrder == null) {
                throw new SystemException("订单不存在");
            }
            if (order != null) {
                orderPayService.cancelWechatOrder(order, su);//修改维修支付订单
            } else {
                orderPayService.cancelWechatNewOrder(newOrder, su);//修改以旧换新支付订单
            }
            result.setSuccess(true);
            result.setResultCode("0");
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }

        renderJson(response, result);
    }


    /**
     * 取消原因列表
     **/
    @RequestMapping("/wechat/order/cancelReason")
    public void cancleReason(HttpServletRequest request,
                             HttpServletResponse response) throws IOException {
        ResultData result = new ResultData();
        CancelReason c = new CancelReason();
        c.setIsDel(0);
        try {
            List<CancelReason> list = cancelReasonService.queryListForPage(c);
            JSONObject jsonResult = new JSONObject();
            JSONArray array = new JSONArray();
            for (CancelReason reason : list) {
                JSONObject j = new JSONObject();
                j.put("reason", reason.getReason());
                array.add(j);
            }
            jsonResult.put("data", array);
            result.setResult(jsonResult);
            sessionUserService.getSuccessResult(result, jsonResult);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }

        renderJson(response, result);
    }


    @Autowired
    private ReworkOrderService reworkOrderService;

    /**
     * 取消订单
     *
     * @param request
     * @param response
     * @throws IOException
     * @CreateDate: 2016-9-17 上午12:13:53
     */
    @RequestMapping("/wechat/order/orderCancel")
    public void orderCancel(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResultData result = new ResultData();
        try {
            getLoginUser(request);//验证token
            SessionUser su = getCurrentUser(request);//得到当前用户
            JSONObject params = getPrarms(request);
            String id = params.getString("id");//订单id
            Integer isRework = params.getInteger("isRework");//是否返修单
            String reason = params.getString("reason");//选择的原因
            String selectReason = params.getString("selectReason");//填写的原因
            String cancelReason = null;
            if (!StringUtils.isBlank(selectReason)) {
                cancelReason = selectReason;
            }
            if (!StringUtils.isBlank(reason) && !StringUtils.isBlank(selectReason)) {
                cancelReason = cancelReason + "；" + reason;
            }
            if (!StringUtils.isBlank(reason) && StringUtils.isBlank(selectReason)) {
                cancelReason = reason;
            }
            if (1 == isRework) {
                ReworkOrder reworkOrder = reworkOrderService.queryById(id);
                if (reworkOrder == null) {
                    throw new SystemException("订单不存在");
                }
                reworkOrderService.orderCancel(id, OrderConstant.ORDER_CANCEL_TYPE_CUSTOMER, cancelReason, su);
            } else {
                Order o = orderService.queryById(id);
                NewOrder newOrder = (NewOrder) newOrderService.queryById(id);
                if (o == null && newOrder == null) {
                    throw new SystemException("订单不存在");
                }
                if (o != null) {
                    orderService.orderCancel(id, OrderConstant.ORDER_CANCEL_TYPE_CUSTOMER, cancelReason, su);
                } else if (newOrder != null) {
                    newOrderService.orderCancel(id, OrderConstant.ORDER_CANCEL_TYPE_CUSTOMER, cancelReason, su);
                }
            }
            result.setSuccess(true);
            result.setResultCode("0");
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        renderJson(response, result);
    }

    /**
     * 确认订单
     *
     * @param request
     * @param response
     * @throws IOException
     * @CreateDate: 2016-9-17 上午12:13:53
     */
    @RequestMapping("/wechat/order/orderConfirmToFinish")
    public void orderConfirmToFinish(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResultData result = new ResultData();
        try {
            getLoginUser(request);//验证token
            SessionUser su = getCurrentUser(request);//得到当前用户
            JSONObject params = getPrarms(request);
            String id = params.getString("id");
            orderService.orderConfirmToFinish(id, su);//确认操作
            result.setSuccess(true);
            result.setResultCode("0");
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        renderJson(response, result);
    }


    /**
     * 保存评价
     *
     * @param request
     * @param response
     * @throws IOException
     * @CreateDate: 2016-9-17 上午12:13:53
     */
    @RequestMapping("/wechat/order/saveComment")
    public void saveComment(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResultData result = new ResultData();
        try {
            getLoginUser(request);//验证token
            SessionUser su = getCurrentUser(request);//得到当前用户
            JSONObject params = getPrarms(request);
            String id = params.getString("id");
            String overallRate = params.getString("overallRate");//星评数
            String content = params.getString("content");//评价内容
            Integer isRework = params.getInteger("isRework");//是否返修   1：返修
            OrderComment comm = new OrderComment();
            comm.setOverallRate(new BigDecimal(overallRate));
            comm.setContent(content);
            if (1 == isRework) {
                ReworkOrder reworkOrder = reworkOrderService.queryById(id);
                if (reworkOrder == null) {
                    throw new SystemException("订单不存在，不能进行评价");
                }
                commentService.reSave(id, comm, su);
            } else {
                //判断是维修订单评价还是以旧换新订单评价
                Order o = orderService.queryById(id);
                NewOrder newOrder = (NewOrder) newOrderService.queryById(id);

                if (o == null && newOrder == null) {
                    throw new SystemException("订单不存在，不能进行评价");
                }
                if (o != null) {
                    commentService.save(id, comm, su);
                } else if (newOrder != null) {
                    commentService.orderSave(id, comm, su);
                }
            }
            result.setSuccess(true);
            result.setResultCode("0");
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        renderJson(response, result);
    }


    /**
     * 微信分享页面数据获取
     */
    @RequestMapping("/wechat/repair/shareData")
    public void shareData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        try {
            JSONObject params = getPrarms(request);
            String id = params.getString("id");
            Order o = orderService.queryById(id);
            if (o == null) {
                throw new SystemException(ApiResultConstant.resultCode_str_3003, ApiResultConstant.resultCode_3003);
            }
            List<OrderDetail> details = detailService.queryByOrderNo(o.getOrderNo());
            JSONObject j = new JSONObject();
            j.put("modelName", o.getModelName());
            j.put("realPrice", o.getRealPrice());
            j.put("isUseCoupon", o.getIsUseCoupon());
            j.put("couponPrice", o.getCouponPrice());
            JSONArray array = new JSONArray();
            for (OrderDetail detail : details) {
                JSONObject s = new JSONObject();
                s.put("type", detail.getType());
                s.put("projectName", detail.getProjectName());
                s.put("price", detail.getPrice());
                array.add(s);
            }
            j.put("details", array);
            jsonResult.put("order", j);
            sessionUserService.getSuccessResult(result, jsonResult);
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        renderJson(response, result);
    }

    /**
     * 获取调用微信js-sdk接口所需config配置数据
     */
    @RequestMapping("/wechat/repair/shareForData")
    public void shareForData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        try {
            getLoginUser(request);//验证token
            SessionUser su = getCurrentUser(request);//得到当前用户
            String url = request.getHeader("Referer");//发起请求的地址
            WxJsapiSignature wxJsapiSign = wxMpService.createJsapiSignature(url);
            JSONObject j = new JSONObject();
            j.put("appId", wxJsapiSign.getAppid());
            j.put("timestamp", wxJsapiSign.getTimestamp());
            j.put("noncestr", wxJsapiSign.getNoncestr());
            j.put("signature", wxJsapiSign.getSignature());
            jsonResult.put("wxJsapiSign", j);
            sessionUserService.getSuccessResult(result, jsonResult);
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        renderJson(response, result);
    }


    /**
     * 获取微信用户领取的优惠券信息
     */
    @RequestMapping("/wechat/repair/getCouponNews")
    public void getCouponNews(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        JSONArray array = new JSONArray();
        try {
            String code = request.getParameter("code");
            //测试
            String openid = (String) request.getSession().getAttribute("couponOpenid");
            if (StringUtils.isBlank(code) && StringUtils.isBlank(openid)) {
                log.info("微信授权code为空 ");
                String url = getProjectUrl(request);
                response.sendRedirect(url + "/wechat/card.html");
                return;
            } else {
                //调用微信服务接口获取openid
                if (StringUtils.isBlank(openid)) {
                    WxMpOAuth2AccessToken token = this.wxMpService.oauth2getAccessToken(code);
                    openid = token.getOpenId();
                    //为防止code 获取openid无效 先存起来
                    request.getSession().setAttribute("couponOpenid", token.getOpenId());
                }
                WechatUser wechatUser = wechatUserService.queryById(openid);
                if (wechatUser == null) {
                    throw new SystemException("您还未领取过微信关注优惠券");
                }
                // 查询领取的贴膜优惠券
                Coupon screenCoupon = couponService.queryByCode(wechatUser.getScreenCouponCode());
                // 查询领取的通用优惠券
                Coupon commonCoupon = couponService.queryByCode(wechatUser.getCommonCouponCode());

                if (screenCoupon != null) {
                    JSONObject j = getCouponNews(screenCoupon);
                    array.add(j);
                }
                if (commonCoupon != null) {
                    JSONObject j = getCouponNews(commonCoupon);
                    array.add(j);
                }

            }
            jsonResult.put("couponList", array);
            sessionUserService.getSuccessResult(result, jsonResult);
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        renderJson(response, result);

    }

    /**
     * 优惠券信息转化为json格式输出
     *
     * @param coupon
     * @return
     */
    public JSONObject getCouponNews(Coupon coupon) {
        JSONObject json = new JSONObject();
        //返回优惠券信息json字符串
        json.put("coupon_code", coupon.getCouponCode());
        json.put("begin_time", coupon.getBeginTime().replace("-", "."));
        json.put("end_time", coupon.getEndTime().replace("-", "."));
        json.put("coupon_price", coupon.getCouponPrice());
        json.put("coupon_name", coupon.getCouponName());
        json.put("is_use", coupon.getIsUse());
        if (coupon.getIsUse() == 1) {
            json.put("use_time", coupon.getUseTime());
            if (coupon.getType() == 2) {
                //贴膜优惠券
                Engineer eng = engineerService.queryByEngineerNumber(coupon.getUpdateUserid());
                if (eng != null) {
                    Shop shop = shopService.queryByCode(eng.getShopCode());
                    if (shop != null) {
                        json.put("shop_name", shop.getName());
                    }
                }
            } else if (coupon.getType() == 0) {
                //维修优惠券
                Order o = new Order();
                o.setCouponCode(coupon.getCouponCode());
                List<Order> order = orderService.queryListForPage(o);
                if (!order.isEmpty()) {
                    json.put("shop_name", order.get(0).getShopName());
                }
            }
        }
        if (coupon.getType() == 2) {
            //贴膜优惠券
            json.put("type", 2);
        } else {
            //维修优惠券
            json.put("type", 0);
        }
        //判断是否过期
        //判断是否过期
        String nowDay = DateUtil.getNowyyyyMMdd();
        if (nowDay.compareTo(coupon.getEndTime()) > 0) {
            json.put("is_overdue", 1);//过期
        } else {
            json.put("is_overdue", 0);//未过期
        }


        //判断支持的品牌和项目
        List<CouponModel> couModels = couponModelService.queryListByCouponId(coupon.getId());

        JSONArray array = new JSONArray();//支持品牌
        if (coupon.getType() == 2) {
            // 贴膜优惠券 品牌限制 返回 无
            JSONObject j = new JSONObject();
            j.put("brandName", "无");
            array.add(j);
        } else {
            //如果该优惠券支持所有品牌则返回 无 表示品牌无限制
            List<Brand> brandList = brandService.queryList(null);
            if (brandList.size() == couModels.size()) {
                JSONObject j = new JSONObject();
                j.put("brandName", "无");
                array.add(j);
            } else {
                for (CouponModel model : couModels) {
                    JSONObject j = new JSONObject();
                    j.put("brandName", model.getBrandName());
                    j.put("brandId", model.getBrandId());
                    array.add(j);
                }
            }

        }
        json.put("models", array);


        JSONArray projects = new JSONArray();//支持维修项目
        if (coupon.getType() == 2) {
            //贴膜优惠券 返回项目信息  钢化膜
            JSONObject js = new JSONObject();
            js.put("projectName", "钢化膜");
            projects.add(js);
        } else {
            List<CouponProject> couProjects = couponProjectService.queryListByCouponId(coupon.getId());
            //如果维修优惠券支持所有项目则返回  无  表示品牌无限制
            List<Project> projectList = projectService.queryList(null);
            if (projectList.size() == couProjects.size()) {
                JSONObject js = new JSONObject();
                js.put("projectName", "无");
                projects.add(js);
            } else {
                for (CouponProject project : couProjects) {
                    JSONObject js = new JSONObject();
                    js.put("projectName", project.getProjectName());
                    js.put("projectId", project.getId());
                    projects.add(js);
                }
            }
        }
        json.put("projects", projects);


        return json;
    }


}


