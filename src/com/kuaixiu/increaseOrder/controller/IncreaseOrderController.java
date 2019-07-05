package com.kuaixiu.increaseOrder.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.exception.SystemException;
import com.common.util.AES;
import com.common.util.DateUtil;
import com.common.util.NOUtil;
import com.common.util.RandomUtil;
import com.common.wechat.common.util.StringUtils;
import com.kuaixiu.increaseOrder.entity.IncreaseOrder;
import com.kuaixiu.increaseOrder.entity.IncreaseRecord;
import com.kuaixiu.increaseOrder.service.IncreaseOrderService;
import com.kuaixiu.increaseOrder.service.IncreaseRecordService;
import com.kuaixiu.recycle.controller.RecycleController;
import com.kuaixiu.recycle.entity.RecycleOrder;
import com.kuaixiu.recycle.service.RecycleOrderService;
import com.kuaixiu.wechat.entity.WechatUser;
import com.kuaixiu.wechat.service.WechatUserService;
import com.system.api.entity.ResultData;
import com.system.basic.user.service.SessionUserService;
import com.system.constant.SystemConstant;
import jodd.util.StringUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Auther: anson
 * @Date: 2018/8/17
 * @Description: 回收加价订单--针对微信小程序
 */
@Controller
public class IncreaseOrderController extends BaseController {

    private static final Logger log = Logger.getLogger(IncreaseOrderController.class);

    @Autowired
    private IncreaseOrderService increaseOrderService;
    @Autowired
    private IncreaseRecordService increaseRecordService;
    @Autowired
    private SessionUserService sessionUserService;
    @Autowired
    private RecycleOrderService recycleOrderService;
    @Autowired
    private WechatUserService wechatUserService;

    /**
     * 基础访问接口地址
     */
    private static final String baseUrl = SystemConstant.RECYCLE_URL;
    /**
     * 基础访问接口地址//回收新接口
     */
    private static final String baseNewUrl = SystemConstant.RECYCLE_NEW_URL;
    /**
     * 需要加密的数据名
     */
    private static final String cipherdata = SystemConstant.RECYCLE_REQUEST;
    //回收订单状态变化时调用，更改状态  参数签名
    private static final String autograph = "HZYNKJ@SUPER2017";

    /**
     * 创建加价订单
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = "recycle/createIncreaseOrder")
    public ResultData createIncreaseOrder(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        try {
            //获取请求数据
            JSONObject params = getPrarms(request);
            String orderNo = get(params.getString("orderNo"));   //回收订单号

            //判断回收订单是否存在
            RecycleOrder recycleOrder = recycleOrderService.queryByOrderNo(orderNo);
            if (null == recycleOrder) {
                throw new SystemException("回收订单不存在");
            }
            //判断该回收订单是否已创建加价订单
            if (StringUtils.isNotBlank(recycleOrder.getIncreaseOrderNo())) {
                throw new SystemException("该订单已参加过分享加价活动");
            }

            //创建加价订单
            IncreaseOrder increaseOrder = new IncreaseOrder();
            String increaseOrderNo = NOUtil.getNo("PO-");
            increaseOrder.setOrderNo(increaseOrderNo);
            increaseOrder.setRecycleOrderNo(orderNo);
            increaseOrder.setPlan("0");

            //满足条件 创建加价订单
            increaseOrderService.add(increaseOrder);
            //更新回收订单状态
            recycleOrder.setIncreaseOrderNo(increaseOrderNo);
            recycleOrderService.saveUpdate(recycleOrder);

            jsonResult.put("increaseOrderNo", increaseOrderNo);
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
     * 查看抬价订单详情
     *
     * @param request
     * @param response
     */
    @ResponseBody
    @RequestMapping(value = "recycle/increaseOrderDetail")
    public ResultData increaseOrderDetail(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        try {
            //获取请求数据
            JSONObject params = getPrarms(request);
            String orderNo = get(params.getString("orderNo"));   //抬价订单订单号
            //查看加价进度
            IncreaseOrder increaseOrder = increaseOrderService.queryById(orderNo);
            if (increaseOrder == null) {
                throw new SystemException("订单不存在");
            }

            //判断订单是否过期，
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(increaseOrder.getInTime());
            calendar.add(Calendar.HOUR_OF_DAY, 24);
            if (calendar.getTime().getTime() - new Date().getTime() < 0) {
                //订单已过期
                if (increaseOrder.getIsSuccess() != 1) {
                    increaseOrder.setIsSuccess(1);
                    increaseOrderService.saveUpdate(increaseOrder);
                }
            }

            jsonResult.put("score", increaseOrder.getPlan());       //当前订单加价百分比
            Integer status = increaseOrder.getIsSuccess();
            if (status == 1) {
                status = 2;
            } else if (status == 2) {
                status = 1;
            }
            jsonResult.put("status", status);

            if (status == 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                calendar.setTime(increaseOrder.getInTime());
                calendar.add(Calendar.HOUR_OF_DAY, 24);
                long seconds = calendar.getTime().getTime() - new Date().getTime();
                if (seconds > 24 * 60 * 60 * 1000 || seconds < 0) {
                    jsonResult.put("remainTime", null);
                } else {
                    jsonResult.put("remainTime", sdf.format(new Date(seconds)));
                }
            } else {
                jsonResult.put("remainTime", null);
            }
            IncreaseRecord record = new IncreaseRecord();
            record.setOrderNo(orderNo);

            RecycleOrder recycleOrder = recycleOrderService.queryByOrderNo(increaseOrder.getRecycleOrderNo());
            if (recycleOrder.getPrice() == null || recycleOrder == null) {
                throw new SystemException("回收订单金额为空");
            }

            List<IncreaseRecord> increaseRecords = increaseRecordService.queryList(record);
            JSONArray array = new JSONArray();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (!increaseRecords.isEmpty()) {
                //加价记录
                for (IncreaseRecord r : increaseRecords) {
                    BigDecimal increasePrice = recycleOrder.getPrice().divide(new BigDecimal("100")).multiply(new BigDecimal(r.getPlan()));
                    JSONObject j = new JSONObject();
                    //对昵称字段进行解码。
                    j.put("name", URLDecoder.decode(r.getNickname(), "UTF-8"));
                    j.put("imgUrl", r.getImgUrl());
                    j.put("increasePrice", increasePrice.toString());
                    j.put("plan", r.getPlan());
                    j.put("time", sdf.format(r.getInTime()));
                    array.add(j);
                }
            }
            jsonResult.put("list", array);
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
     * 查看抬价订单状态
     *
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "recycle/IncreaseOrderStatus")
    public ResultData increaseOrderStatus(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        try {
            //获取请求数据
            JSONObject params = getPrarms(request);
            String orderNo = get(params.getString("orderNo"));    //orderNo
            IncreaseOrder increaseOrder = increaseOrderService.queryById(orderNo);
            if (increaseOrder == null) {
                throw new SystemException("订单不存在");
            }
            int orderStatus = getOrderStatus(increaseOrder);
            jsonResult.put("status", orderStatus);

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
     * 判断订单状态
     *
     * @param order 0 满足加价条件   1加价已满百分之百   2加价超出时间失败
     * @return
     */
    public int getOrderStatus(IncreaseOrder order) {
        int i = 0;
        //判断订单状态
        if (order.getIsSuccess() == 1) {
            //加价失败
            i = 2;
        } else if (order.getIsSuccess() == 2) {
            //加价成功
            i = 1;
        } else if (order.getIsSuccess() == 0) {
            //加价中 判断是否超时
            //判断该订单时间是否满足加价条件 目前允许加价时间为24小时内
            Date inTime = order.getInTime();
            // 24小时毫秒数
            long dayTime = 24 * 60 * 60 * 1000;
            if (System.currentTimeMillis() - inTime.getTime() > dayTime) {
                i = 2;
            }
        }

        return i;
    }


    /**
     * 是否关注微信公众号
     *
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "recycle/subscriptionWechat")
    public ResultData subscriptionWechat(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        try {
            //获取请求数据
            JSONObject params = getPrarms(request);
            String openId = get(params.getString("openId"));    //openId
            //查询用户是否有关注 微信公众号
            WechatUser wechatUser = wechatUserService.queryById(openId);
            if (wechatUser == null || wechatUser.getIsSubscribe() == 1) {
                //没有关注
                jsonResult.put("subscription", false);
            } else {
                //已关注
                jsonResult.put("subscription", true);
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
     * 对订单加价
     *
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "recycle/addPrice")
    public ResultData addPrice(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        String planScore = "0";   //加价百分比
        try {
            //获取请求数据  抬价订单号  微信openId  微信昵称  微信头像
            JSONObject params = getPrarms(request);
            String orderNo = get(params.getString("orderNo"));
            String openId = get(params.getString("openId"));
            String nickname = get(params.getString("nickname"));
            String imgUrl = get(params.getString("imgUrl"));
            IncreaseOrder increaseOrder = increaseOrderService.queryById(orderNo);
            if (increaseOrder == null) {
                result.setSuccess(false);
                result.setResultMessage("该订单不存在");
                return result;
            }
            //查找对应的回收订单
            RecycleOrder recycleOrder = recycleOrderService.queryByIncreaseOrderNo(orderNo);
            if (recycleOrder == null) {
                result.setSuccess(false);
                result.setResultMessage("该订单对应回收订单不存在");
                return result;
            }
            if (StringUtils.isNotBlank(recycleOrder.getWechatOpenId())) {
                if (recycleOrder.getWechatOpenId().equals(openId)) {
                    result.setSuccess(false);
                    result.setResultMessage("您不能为自己的订单抬价");
                    return result;
                }
            }

            //判断订单是否过期，
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(increaseOrder.getInTime());
            calendar.add(Calendar.HOUR_OF_DAY, 24);
            long a = calendar.getTime().getTime() - new Date().getTime();
            if (calendar.getTime().getTime() - new Date().getTime() < 0) {
                //订单已过期
                increaseOrder.setIsSuccess(1);
                increaseOrderService.saveUpdate(increaseOrder);
            }

            //判断该订单状态
            int orderStatus = getOrderStatus(increaseOrder);
            if (orderStatus != 0) {
                jsonResult.put("isSuccess", "1");
                jsonResult.put("score", null);
                jsonResult.put("name", null);
                jsonResult.put("imgUrl", null);
                result.setResult(jsonResult);
                //不满足加价条件
                if (orderStatus == 1) {
                    result.setSuccess(false);
                    result.setResultMessage("该订单加价已完成");
                    return result;
                } else if (orderStatus == 2) {
                    result.setSuccess(false);
                    result.setResultMessage("该订单活动已超过24小时");
                    return result;
                }
            }

            //判断该用户加价的次数  目前一个用户最多只能为一个订单加价1次
            IncreaseRecord r = new IncreaseRecord();
            r.setOpenId(openId);
            List<IncreaseRecord> increaseRecords = increaseRecordService.queryList(r);
            List<IncreaseRecord> increaseRecords2 = new ArrayList<IncreaseRecord>();
            for (IncreaseRecord increaseRecord : increaseRecords) {
                if (increaseRecord.getOrderNo().equals(orderNo)) {
                    jsonResult.put("isSuccess", "1");
                    jsonResult.put("score", null);
                    jsonResult.put("name", null);
                    jsonResult.put("imgUrl", null);
                    result.setResult(jsonResult);
                    result.setSuccess(false);
                    result.setResultMessage("您已为该订单抬过价了");
                    return result;
                }
                if (increaseRecord.getInTime().getTime() > DateUtil.getStartTime().getTime()) {
                    increaseRecords2.add(increaseRecord);
                }
            }
            if (!increaseRecords2.isEmpty() && increaseRecords2.size() >= 3) {
                jsonResult.put("isSuccess", "1");
                jsonResult.put("score", null);
                jsonResult.put("name", null);
                jsonResult.put("imgUrl", null);
                result.setResult(jsonResult);
                result.setSuccess(false);
                result.setResultMessage("您今天的加价次数已到上限，请明天再来！");
                return result;
            }

            //判断该订单是否已加价完成
            if (increaseOrder.getIsSuccess() == 2) {
                planScore = "0";
            } else {
                //开始抬价
                if (StringUtils.isBlank(increaseOrder.getScoreList()) && increaseOrder.getTimes() == 0) {
                    //如果是第一次抬价 则生成抬价百分比集合
                    planScore = onceRandom(recycleOrder.getPrice(), increaseOrder);
                } else {
                    //按次数给予百分数
                    List<String> strings = StringToList(increaseOrder.getScoreList());
                    planScore = strings.get(increaseOrder.getTimes()).trim();
                    increaseOrder.setTimes(increaseOrder.getTimes() + 1);
                    //小数相加 BigDecimal防止精度缺失
                    //订单已有百分比
                    BigDecimal hasScore = new BigDecimal(increaseOrder.getPlan());
                    //此次增加的百分比
                    BigDecimal nowSocre = new BigDecimal(planScore);
                    increaseOrder.setPlan((hasScore.add(nowSocre)).toString());

                    BigDecimal totalSocre = new BigDecimal(increaseOrder.getPlan());

                    if (totalSocre.subtract(new BigDecimal("100")).doubleValue() >= 0) {
                        //修改订单状态为抬价成功
                        increaseOrder.setIsSuccess(2);
                        //订单加价成功后推送后台
                        Boolean isTrue=increaseSuccess(recycleOrder.getOrderNo());
                        if(!isTrue){
                            log.trace(recycleOrder.getOrderNo()+"订单加价成功后推送后台失败");
                        }
                    }
                    increaseOrderService.saveUpdate(increaseOrder);
                }
            }

            //计算加价金额
            BigDecimal increasePrice = recycleOrder.getPrice().divide(new BigDecimal("100")).multiply(new BigDecimal(planScore));

            //返回信息
            if (increaseOrder.getIsSuccess() == 0 || increaseOrder.getIsSuccess() == 2) {
                jsonResult.put("isSuccess", "0");
                jsonResult.put("increasePrice", increasePrice.toString());
                jsonResult.put("score", planScore);
                jsonResult.put("name", nickname);
                jsonResult.put("imgUrl", imgUrl);
            } else {
                jsonResult.put("isSuccess", "1");
            }


            //新增加价记录
            IncreaseRecord record = new IncreaseRecord();
            record.setOrderNo(orderNo);
            record.setRecycleOrderNo(recycleOrder.getOrderNo());
            record.setOpenId(openId);
            record.setPlan(planScore);
            //对昵称字段进行编码存储
            record.setNickname(URLEncoder.encode(nickname, "UTF-8"));
            record.setImgUrl(imgUrl);
            increaseRecordService.add(record);


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
     * 对订单加价
     *
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "recycleNew/addPrice")
    public ResultData addPriceNewUrl(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        String planScore = "0";   //加价百分比
        try {
            //获取请求数据  抬价订单号  微信openId  微信昵称  微信头像
            JSONObject params = getPrarms(request);
            String orderNo = get(params.getString("orderNo"));
            String openId = get(params.getString("openId"));
            String nickname = get(params.getString("nickname"));
            String imgUrl = get(params.getString("imgUrl"));
            IncreaseOrder increaseOrder = increaseOrderService.queryById(orderNo);
            if (increaseOrder == null) {
                result.setSuccess(false);
                result.setResultMessage("该订单不存在");
                return result;
            }
            //查找对应的回收订单
            RecycleOrder recycleOrder = recycleOrderService.queryByIncreaseOrderNo(orderNo);
            if (recycleOrder == null) {
                result.setSuccess(false);
                result.setResultMessage("该订单对应回收订单不存在");
                return result;
            }
            if (StringUtils.isNotBlank(recycleOrder.getWechatOpenId())) {
                if (recycleOrder.getWechatOpenId().equals(openId)) {
                    result.setSuccess(false);
                    result.setResultMessage("您不能为自己的订单抬价");
                    return result;
                }
            }

            //判断订单是否过期，
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(increaseOrder.getInTime());
            calendar.add(Calendar.HOUR_OF_DAY, 24);
            long a = calendar.getTime().getTime() - new Date().getTime();
            if (calendar.getTime().getTime() - new Date().getTime() < 0) {
                //订单已过期
                increaseOrder.setIsSuccess(1);
                increaseOrderService.saveUpdate(increaseOrder);
            }

            //判断该订单状态
            int orderStatus = getOrderStatus(increaseOrder);
            if (orderStatus != 0) {
                jsonResult.put("isSuccess", "1");
                jsonResult.put("score", null);
                jsonResult.put("name", null);
                jsonResult.put("imgUrl", null);
                result.setResult(jsonResult);
                //不满足加价条件
                if (orderStatus == 1) {
                    result.setSuccess(false);
                    result.setResultMessage("该订单加价已完成");
                    return result;
                } else if (orderStatus == 2) {
                    result.setSuccess(false);
                    result.setResultMessage("该订单活动已超过24小时");
                    return result;
                }
            }

            //判断该用户加价的次数  目前一个用户最多只能为一个订单加价1次
            IncreaseRecord r = new IncreaseRecord();
            r.setOpenId(openId);
            List<IncreaseRecord> increaseRecords = increaseRecordService.queryList(r);
            List<IncreaseRecord> increaseRecords2 = new ArrayList<IncreaseRecord>();
            for (IncreaseRecord increaseRecord : increaseRecords) {
                if (increaseRecord.getOrderNo().equals(orderNo)) {
                    jsonResult.put("isSuccess", "1");
                    jsonResult.put("score", null);
                    jsonResult.put("name", null);
                    jsonResult.put("imgUrl", null);
                    result.setResult(jsonResult);
                    result.setSuccess(false);
                    result.setResultMessage("您已为该订单抬过价了");
                    return result;
                }
                if (increaseRecord.getInTime().getTime() > DateUtil.getStartTime().getTime()) {
                    increaseRecords2.add(increaseRecord);
                }
            }
            if (!increaseRecords2.isEmpty() && increaseRecords2.size() >= 3) {
                jsonResult.put("isSuccess", "1");
                jsonResult.put("score", null);
                jsonResult.put("name", null);
                jsonResult.put("imgUrl", null);
                result.setResult(jsonResult);
                result.setSuccess(false);
                result.setResultMessage("您今天的加价次数已到上限，请明天再来！");
                return result;
            }

            //判断该订单是否已加价完成
            if (increaseOrder.getIsSuccess() == 2) {
                planScore = "0";
            } else {
                //开始抬价
                if (StringUtils.isBlank(increaseOrder.getScoreList()) && increaseOrder.getTimes() == 0) {
                    //如果是第一次抬价 则生成抬价百分比集合
                    planScore = onceRandom(recycleOrder.getPrice(), increaseOrder);
                } else {
                    //按次数给予百分数
                    List<String> strings = StringToList(increaseOrder.getScoreList());
                    planScore = strings.get(increaseOrder.getTimes()).trim();
                    increaseOrder.setTimes(increaseOrder.getTimes() + 1);
                    //小数相加 BigDecimal防止精度缺失
                    //订单已有百分比
                    BigDecimal hasScore = new BigDecimal(increaseOrder.getPlan());
                    //此次增加的百分比
                    BigDecimal nowSocre = new BigDecimal(planScore);
                    increaseOrder.setPlan((hasScore.add(nowSocre)).toString());

                    BigDecimal totalSocre = new BigDecimal(increaseOrder.getPlan());

                    if (totalSocre.subtract(new BigDecimal("100")).doubleValue() >= 0) {
                        //修改订单状态为抬价成功
                        increaseOrder.setIsSuccess(2);
                        //订单加价成功后推送后台
                        Boolean isTrue=increaseNewSuccess(recycleOrder.getOrderNo());
                        if(!isTrue){
                            log.trace(recycleOrder.getOrderNo()+"订单加价成功后推送后台失败");
                        }
                    }
                    increaseOrderService.saveUpdate(increaseOrder);
                }
            }

            //计算加价金额
            BigDecimal increasePrice = recycleOrder.getPrice().divide(new BigDecimal("100")).multiply(new BigDecimal(planScore));

            //返回信息
            if (increaseOrder.getIsSuccess() == 0 || increaseOrder.getIsSuccess() == 2) {
                jsonResult.put("isSuccess", "0");
                jsonResult.put("increasePrice", increasePrice.toString());
                jsonResult.put("score", planScore);
                jsonResult.put("name", nickname);
                jsonResult.put("imgUrl", imgUrl);
            } else {
                jsonResult.put("isSuccess", "1");
            }


            //新增加价记录
            IncreaseRecord record = new IncreaseRecord();
            record.setOrderNo(orderNo);
            record.setRecycleOrderNo(recycleOrder.getOrderNo());
            record.setOpenId(openId);
            record.setPlan(planScore);
            //对昵称字段进行编码存储
            record.setNickname(URLEncoder.encode(nickname, "UTF-8"));
            record.setImgUrl(imgUrl);
            increaseRecordService.add(record);


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
     * 订单加价成功后推送后台、、回收新接口
     * @param orderNo
     * @return
     * @throws Exception
     */
    private Boolean increaseNewSuccess(String orderNo) throws Exception {
        JSONObject jsonResult = new JSONObject();
        String url = baseNewUrl + "setordercoupon";
        JSONObject requestNews = new JSONObject();
        //调用接口需要加密的数据
        JSONObject code = new JSONObject();
        code.put("orderid", orderNo);
        JSONArray jsonArray = new JSONArray();
        JSONObject json = new JSONObject();
        json.put("couponId", orderNo);
        json.put("actType", 1);
        json.put("percent", 1);
        json.put("up", 10000);
        json.put("low", 0);
        json.put("desc", "满0元加价");
        jsonArray.add(json);
        code.put("coupon_rule", jsonArray.toJSONString());
        String realCode = AES.Encrypt(code.toString());  //加密
        requestNews.put(cipherdata, realCode);
        //发起请求
        String getResult = AES.post(url, requestNews);
        //对得到结果进行解密
        jsonResult = RecycleController.getResult(AES.Decrypt(getResult));
        if (StringUtil.isNotBlank(jsonResult.getString("datainfo"))) {
            return true;
        }
        return false;
    }

    /**
     * 订单加价成功后推送后台
     * @param orderNo
     * @return
     * @throws Exception
     */
    private Boolean increaseSuccess(String orderNo) throws Exception {
        JSONObject jsonResult = new JSONObject();
        String url = baseUrl + "setordercoupon";
        JSONObject requestNews = new JSONObject();
        //调用接口需要加密的数据
        JSONObject code = new JSONObject();
        code.put("orderid", orderNo);
        JSONArray jsonArray = new JSONArray();
        JSONObject json = new JSONObject();
        json.put("couponId", orderNo);
        json.put("actType", 1);
        json.put("percent", 1);
        json.put("up", 10000);
        json.put("low", 0);
        json.put("desc", "满0元加价");
        jsonArray.add(json);
        code.put("coupon_rule", jsonArray.toJSONString());
        String realCode = AES.Encrypt(code.toString());  //加密
        requestNews.put(cipherdata, realCode);
        //发起请求
        String getResult = AES.post(url, requestNews);
        //对得到结果进行解密
        jsonResult = RecycleController.getResult(AES.Decrypt(getResult));
        if (StringUtil.isNotBlank(jsonResult.getString("datainfo"))) {
            return true;
        }
        return false;
    }


    /**
     * 生成和固定个数固定的随机数数组  返回第一个抬价数
     *
     * @param price 总金额
     * @param order 抬价订单
     */
    private String onceRandom(BigDecimal price, IncreaseOrder order) {
        List<Float> list = new ArrayList<Float>();  //总抬价随机数集合
        String result = "";                         //转换为String储存
        String dprice = "2";    //均价一次多少钱
        //判断完成百分之百需要的抬价次数  向上取整
        BigDecimal sum = price.divide(new BigDecimal(dprice), RoundingMode.UP);
        System.out.println("砍完需要的总次数：" + sum);
        if (sum.intValue() == 0) {
            throw new SystemException("您的订单价格为0,无法参与砍价活动!");
        }
        BigDecimal realSum = sum;

        //  设置五个区间
        //X代表价格   总次数 X/2
        //0%-50%     X/2 *0.1
        //50%-80%    X/2 *0.15
        //80%-90%    X/2 *0.2
        //90%-98%    X/2 *0.25
        //98%-100%   X/2 *0.3
        int first = 50;
        int second = 80;
        int third = 90;
        int fourth = 98;
        int fifth = 100;

        BigDecimal b1 = new BigDecimal("0.1");
        BigDecimal b2 = new BigDecimal("0.15");
        BigDecimal b3 = new BigDecimal("0.2");
        BigDecimal b4 = new BigDecimal("0.25");

        //1.1当抬价次数小于等于5次时  无需安按照比例分配
        //为保证砍价总数为百分只百需分5种情况分段
        if (sum.intValue() <= 5) {
            list = singleScore(first, second, third, fourth, fifth, sum.intValue());
            result = list.toString();
        } else {

            //1.2次数大于5次 则按正常方式生产抽奖随机数
            //对前两个数字区间向上取整  因为0.1*5 和0.15*5都小于1
            int sum1 = ((realSum.multiply(b1)).setScale(0, BigDecimal.ROUND_UP)).intValue();
            int sum2 = ((realSum.multiply(b2)).setScale(0, BigDecimal.ROUND_UP)).intValue();
            int sum3 = (realSum.multiply(b3)).intValue();
            int sum4 = (realSum.multiply(b4)).intValue();
            int sum5 = realSum.intValue() - sum1 - sum2 - sum3 - sum4;
            System.out.println("区间次数：" + sum1 + "  " + sum2 + "  " + sum3 + "  " + sum4 + "  " + sum5);


            //给定数组和  个数  还有最大值   最小值
            List<Float> list2 = generatorScore(second - first, sum2, false);
            List<Float> list3 = generatorScore(third - second, sum3, false);
            List<Float> list4 = generatorScore(fourth - third, sum4, false);
            List<Float> list5 = generatorScore(fifth - fourth, sum5, false);
            List<Float> list1 = new ArrayList<Float>();
            //为保证生成随机和保证为100%  留比例最大的区间最后生成随机数
            BigDecimal total2 = new BigDecimal("0");
            for (Float f : list2) {
                total2 = total2.add(new BigDecimal(String.valueOf(f)));
            }

            BigDecimal total3 = new BigDecimal("0");
            for (Float f : list3) {
                total3 = total3.add(new BigDecimal(String.valueOf(f)));
            }

            BigDecimal total4 = new BigDecimal("0");
            for (Float f : list4) {
                total4 = total4.add(new BigDecimal(String.valueOf(f)));
            }

            BigDecimal total5 = new BigDecimal("0");
            for (Float f : list5) {
                total5 = total5.add(new BigDecimal(String.valueOf(f)));
            }
            //得到剩余总数   该总数可能为很长的小数  为保证生成结果准确
            BigDecimal total1 = new BigDecimal("100").subtract(total2).subtract(total3).subtract(total4).subtract(total5);
            System.out.println("前四段各自和：" + total2.toString() + "  " + total3.toString() + "   " + total4.toString() + "   " + total5.toString());
            System.out.println("剩余总和：" + total1.toString());
            BigDecimal lastScore = new BigDecimal(0);  //最后一位的数
            if (total1.subtract(new BigDecimal(String.valueOf(first))).floatValue() != 0) {
                sum1 = sum1 - 1;
                //数值准确
                if (total1.subtract(new BigDecimal(String.valueOf(first))).floatValue() > 0) {
                    System.out.println("得到数值大于50");
                    lastScore = new BigDecimal(String.valueOf(total1)).subtract(new BigDecimal(first));
                } else {
                    System.out.println("得到数值小于50");
                    lastScore = new BigDecimal(first).subtract(new BigDecimal(String.valueOf(total1)));
                }
                System.out.println("生成的多余的数值：" + lastScore);
                list1 = generatorScore(first, sum1, true);

            } else {
                list1 = generatorScore(total1.intValue(), sum1, true);
            }
            //将得到的分批数组整合到一个总数组里

            list.addAll(getList(list1));
            list.addAll(getList(list2));
            list.addAll(getList(list3));
            list.addAll(getList(list4));
            list.addAll(getList(list5));


            //数组集合转换为String
            result = list.toString();
            if (!lastScore.toString().equals("0")) {
                System.out.println("最终数组不为0：" + lastScore.toString());
                result = result.substring(0, result.indexOf("]")) + "," + lastScore.toString() + "]";
            }
        }

        System.out.println(result);
        //将抬价数组储存到数据库
        order.setScoreList(result);
        order.setTimes(1);    //第一次抽取了
        order.setPlan(String.valueOf(list.get(0)));  //设置第一次抽奖的百分比
        order.setTotalTimes(sum.intValue());
        increaseOrderService.saveUpdate(order);


        List<String> strings = StringToList(result);
        System.out.println("得到最终转换后的String集合大小：" + strings.size());
        System.out.println(strings.get(strings.size() - 1));
        for (String s : strings) {
            System.out.print(s + ",");
        }


        return String.valueOf(list.get(0));


    }

    private List<Float> getList(List<Float> list) {
        Collections.sort(list);
        List<Float> list1 = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            list1.add(list.get(list.size() - i - 1));
        }
        return list1;
    }

    /**
     * 当砍价总次数小于等于5时 分5种情况
     * 手动指定随机数
     *
     * @param first
     * @param second
     * @param third
     * @param fourth
     * @param fifth
     * @param sum    总次数
     * @return
     */
    private List<Float> singleScore(int first, int second, int third, int fourth, int fifth, int sum) {
        List<Float> list = new ArrayList<Float>();
        if (sum == 1) {
            list.add((float) 100);
        } else if (sum == 2) {
            list.add((float) 50);
            list.add((float) 50);
        } else if (sum == 3) {
            list.add((float) 50);
            list.add((float) 30);
            list.add((float) 20);
        } else if (sum == 4) {
            list.add((float) 50);
            list.add((float) 30);
            list.add((float) 10);
            list.add((float) 10);
        } else if (sum == 5) {
            list.add((float) first);
            list.add((float) (second - first));
            list.add((float) (third - second));
            list.add((float) (fourth - third));
            list.add((float) (fifth - fourth));
        }
        return list;

    }

    /**
     * 生成和固定个数固定的随机数数组
     *
     * @param sum    数组和     50
     * @param number 数组个数   10
     * @param tip    是否一定要数据一致  true是
     */
    private static List<Float> generatorScore(float sum, int number, boolean tip) {
        List<Float> list = new ArrayList<Float>();
        float maxNumber = 0L;
        float minNumber = 0L;

        maxNumber = (float) (Math.round(((float) sum / (number / 4)) * 10000)) / 10000;    //允许生成的最大值  保留四位小数
        minNumber = (float) (Math.round(((float) sum / (number * 4)) * 10000)) / 10000;    //允许生成的最小值  保留四位小数


        java.text.DecimalFormat df = new java.text.DecimalFormat("##0.00");
        // 格式化数值

        if (maxNumber < 0.01) {
            //传入的最大最小值过小的话会报错
            maxNumber = 0.05f;
            minNumber = 0.001f;
        }


        //System.out.println("最大   最小值："+maxNumber+"  "+minNumber+"总和："+(float) sum+" 次数"+number);

        // 开始生成区间数组
        list = RandomUtil.splitRedPackets(sum, number, maxNumber, minNumber, tip);
        return list;
    }


    /**
     * List<String>格式字符串转String集合
     *
     * @param result
     */
    private static List<String> StringToList(String result) {
        List<String> list = new ArrayList<String>();
        result = result.substring(1, result.length() - 1);
        list = Arrays.asList(result.split(","));
        return list;
    }


    public static void main(String[] args) {
        IncreaseOrder order = new IncreaseOrder();
        new IncreaseOrderController().onceRandom(new BigDecimal(2000), order);


    }

}
