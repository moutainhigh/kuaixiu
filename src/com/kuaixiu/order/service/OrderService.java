package com.kuaixiu.order.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.service.BaseService;
import com.common.echarts.Option;
import com.common.echarts.axis.CategoryAxis;
import com.common.echarts.axis.ValueAxis;
import com.common.echarts.code.NameLocation;
import com.common.echarts.code.X;
import com.common.echarts.code.Y;
import com.common.echarts.series.Line;
import com.common.exception.ApiServiceException;
import com.common.exception.SystemException;
import com.common.paginate.Page;
import com.common.util.BaiduMapUtil;
import com.common.util.DateUtil;
import com.common.util.NOUtil;
import com.common.util.NumberUtils;
import com.common.util.SmsSendUtil;
import com.kuaixiu.brand.entity.Brand;
import com.kuaixiu.brand.service.BrandService;
import com.kuaixiu.clerk.entity.Clerk;
import com.kuaixiu.clerk.service.ClerkService;
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
import com.kuaixiu.engineer.service.NewEngineerService;
import com.kuaixiu.integral.service.GetIntegralService;
import com.kuaixiu.model.entity.Model;
import com.kuaixiu.model.entity.RepairCost;
import com.kuaixiu.model.service.ModelService;
import com.kuaixiu.model.service.RepairCostService;
import com.kuaixiu.order.constant.OrderConstant;
import com.kuaixiu.order.dao.OrderMapper;
import com.kuaixiu.order.entity.*;
import com.kuaixiu.provider.entity.Provider;
import com.kuaixiu.provider.service.ProviderService;
import com.kuaixiu.shop.entity.Shop;
import com.kuaixiu.shop.service.ShopService;
import com.kuaixiu.wechat.entity.WechatRangeOrder;
import com.kuaixiu.wechat.service.WechatRangeOrderService;
import com.system.constant.ApiResultConstant;
import com.system.constant.SystemConstant;
import com.system.basic.user.entity.SessionUser;

import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.XLSTransformer;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Order Service
 *
 * @CreateDate: 2016-08-26 下午10:44:08
 * @version: V 1.0
 */
@Service("orderService")
public class OrderService extends BaseService<Order> {
    private static final Logger log = Logger.getLogger(OrderService.class);

    @Autowired
    private OrderMapper<Order> mapper;
    @Autowired
    private RepairCostService costService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private OrderDetailService detailService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private ModelService modelService;
    @Autowired
    private ShopService shopService;
    @Autowired
    private EngineerService engineerService;
    @Autowired
    private OrderPayService orderPayService;
    @Autowired
    private CouponService couponService;
    @Autowired
    private CouponModelService couponModelService;
    @Autowired
    private CouponProjectService couponProjectService;
    @Autowired
    private WechatRangeOrderService wechatRangeOrderService;
    @Autowired
    private GetIntegralService getIntegralService;
    @Autowired
    private FromSystemService fromSystemService;
    @Autowired
    private UpdateOrderPriceService updateOrderPriceService;
    @Autowired
    private ReworkOrderService reworkOrderService;
    @Autowired
    private ProviderService providerService;

    public OrderMapper<Order> getDao() {
        return mapper;
    }

    //**********自定义方法***********

    /**
     * 查询未处理订单
     *
     * @param o
     * @return
     * @author: lijx
     * @CreateDate: 2016-10-10 下午11:38:30
     */
    public List<Map<String, Object>> queryMapForPage(Order o) {
        return getDao().queryMapForPage(o);
    }

    //查询未完成订单数
    public Integer orderMapCount(Order o) {
        return getDao().orderMapCount(o);
    }

    /**
     * 根据订单号查询订单
     *
     * @param orderNo
     */
    public Order queryByOrderNo(String orderNo) {
        return getDao().queryByOrderNo(orderNo);
    }

    /**
     * 保存订单
     *
     * @param o
     * @param cust
     * @return
     * @CreateDate: 2016-9-11 下午7:21:19
     * @tip true表示不判断地址可信度下单
     */
    @Transactional
    public int save(Order o, String projectIds, Customer cust, boolean tip) {
        int resultNews = 0;
        //验证品牌 一期品牌为默认值iPhone
        //品牌设置默认值
        Brand b = brandService.queryById(o.getBrandId());
        if (b == null) {
            throw new SystemException("品牌不存在");
        }
        o.setBrandName(b.getName());
        //验证机型
        Model m = modelService.queryById(o.getModelId());
        if (m == null) {
            throw new SystemException("机型不存在");
        }
        o.setModelName(m.getName());

        List<String> pIds = new ArrayList<>();
        //保存订单明细
        if (StringUtils.isBlank(projectIds)) {
            throw new SystemException("故障类型不能为空");
        }
        JSONArray pjIds = JSONArray.parseArray(projectIds);
        for (int i = 0; i < pjIds.size(); i++) {
            String projectId = pjIds.getString(i);
            if (StringUtils.isBlank(projectId)) {
                continue;
            }
            pIds.add(projectId);
        }

        //下单时的金额（含税）=实际金额+退款金额
        BigDecimal orderPrice = new BigDecimal(0);
        //保存订单明细
        for (String pid : pIds) {
            //查询维修费用
            RepairCost cost = costService.queryByMidAndPid(o.getModelId(), pid);
            if (cost == null) {
                throw new SystemException("该机型不支持该故障的维修！");
            }
            orderPrice = NumberUtils.add(orderPrice, cost.getPrice());
        }

        //判断是否使用优惠券
        boolean isUseCoupon = false;
        Coupon c = null;
        if (StringUtils.isNotBlank(o.getCouponCode())) {
            //判断优惠券是否存在
            c = couponService.queryByCode(o.getCouponCode());
            if (c == null) {
                throw new SystemException("亲，您输入的优惠券不存在。请确认优惠券是否正确。");
            }
            //判断优惠券是否使用
            if (c.getIsUse() != 0) {
                throw new SystemException("亲，您输入的优惠券已使用。请确认优惠券是否正确。");
            }
            if (c.getType() != 0) {
                throw new SystemException("亲，您输入的优惠券不支持维修订单使用");
            }
            //判断优惠券是否在有效期内
            String nowDay = DateUtil.getNowyyyyMMdd();
            if (nowDay.compareTo(c.getEndTime()) > 0) {
                throw new SystemException("亲，您输入的优惠券已超过有效期，无法使用。");
            }
            boolean isCanUse = false;
            StringBuffer msg = new StringBuffer();
            //判断优惠券是否可用
            //非品牌通用优惠券判断品牌支持
            if (c.getIsBrandCurrency() == 0) {
                List<CouponModel> couModels = couponModelService.queryListByCouponId(c.getId());
                if (couModels != null && !couModels.isEmpty()) {
                    for (CouponModel cm : couModels) {
                        if (msg.length() > 0) {
                            msg.append("、");
                        }
                        msg.append(cm.getBrandName());
                        if (o.getBrandId().equals(cm.getBrandId())) {
                            isCanUse = true;
                            break;
                        }
                    }
                    if (!isCanUse) {
                        throw new SystemException("亲，您输入的优惠券只支持：" + msg.toString() + " 手机品牌使用。");
                    }
                }
            }
            //非故障通用优惠券判断是否支持故障
            if (c.getIsProjectCurrency() == 0) {
                List<CouponProject> couProjects = couponProjectService.queryListByCouponId(c.getId());
                if (couProjects != null && !couProjects.isEmpty()) {
                    isCanUse = false;
                    msg.setLength(0);
                    for (CouponProject cp : couProjects) {
                        if (msg.length() > 0) {
                            msg.append("、");
                        }
                        msg.append(cp.getProjectName());
                        for (String pid : pIds) {
                            if (pid.equals(cp.getProjectId())) {
                                isCanUse = true;
                                break;
                            }
                        }
                        if (isCanUse) {
                            break;
                        }
                    }
                    if (!isCanUse) {
                        throw new SystemException("亲，您输入的优惠券只支持：" + msg.toString() + " 故障使用。");
                    }
                }
            }
            isUseCoupon = true;//优惠券满足下单使用
        }


        cust.setIsDel(0);
        cust.setStatus(0);
        //保存用户信息
        cust = customerService.save(cust);

        //根据客户填写的地址获取用户坐标  后台系统下单采用高德直接转化，其他的先用百度判断地址可信度
        JSONObject lal = null;
        if (tip) {
            lal = BaiduMapUtil.gaode(cust.getFullAddress());
        } else {
            lal = BaiduMapUtil.getLatAndLngByAddr(cust.getFullAddress());
        }
        System.out.println("百度转化地址:" + lal);
        //设置坐标
        cust.setLongitude(new BigDecimal(lal.getFloat("lng")));
        cust.setLatitude(new BigDecimal(lal.getFloat("lat")));
        //点对点下单跳过门店
        if (StringUtils.isBlank(o.getEngineerId())) {
            //检查附近是否有维修门店
            if (!shopService.checkShopByLonAndLat(cust.getLongitude(), cust.getLatitude())) {
                //公里范围内没有门店是保存信息
                saveRangeOrder(o, cust, c, orderPrice, isUseCoupon);
                resultNews = 10;
                return resultNews;
            }
        }
        //用户坐标
        o.setLatitude(cust.getLatitude());
        o.setLongitude(cust.getLongitude());
        //点对点下单跳过门店
        if (StringUtils.isBlank(o.getEngineerId())) {
            //检查附近的维修门店是否支持客户品牌
            if (!shopService.checkShopModelByLonAndLat(o)) {
                throw new SystemException("亲，附近维修门店赞不支持该品牌。请换个地址再试一下。");
            }
        }

        //生成订单号
        String orderNo = NOUtil.getNo("PO-");
        o.setOrderNo(orderNo);
        //生成订单id
        String orderId = UUID.randomUUID().toString().replace("-", "");
        o.setId(orderId);

        //设置客户信息
        o.setCustomerId(cust.getId());
        o.setCustomerName(cust.getName());
        o.setMobile(cust.getMobile());
        o.setEmail(cust.getEmail());
        //客户地址
        o.setProvince(cust.getProvince());
        o.setCity(cust.getCity());
        o.setCounty(cust.getCounty());
        o.setStreet(cust.getStreet());
        o.setAreas(cust.getAreas());
        o.setAddress(cust.getAddress());


        //设置初始值
        o.setIsLock(0);
        o.setSort(0);
        o.setIsDel(0);
        o.setIsDeposit(0);
        o.setDepositPrice(new BigDecimal(0));
        o.setDepositType(0);
        o.setBalanceStatus(0);
        o.setCancelType(0);
        o.setCancelStatus(0);
        o.setSendAgreedNews(0);
        //  如果该订单不是店员创建的 将clerkId设置为0
        if (o.getClerkId() == null) {
            o.setClerkId("0");
        }
        //保存订单明细
        for (String pid : pIds) {
            OrderDetail od = new OrderDetail();
            od.setOrderNo(orderNo);
            //查询维修费用
            RepairCost cost = costService.queryByMidAndPid(o.getModelId(), pid);
            if (cost == null) {
                throw new SystemException("该机型不支持该故障的维修！");
            }
            od.setProjectId(cost.getProjectId());
            od.setProjectName(cost.getProjectName());
            od.setType(0);
            od.setPrice(cost.getPrice());
            od.setRealPrice(od.getPrice());
            od.setIsDel(0);
            detailService.add(od);
        }
        if (isUseCoupon && c != null) {
            //修改优惠券改为已使用
            c.setUpdateUserid(cust.getId());
            int rest = couponService.updateForUse(c);
            if (rest == 0) {
                throw new SystemException("亲，您输入的优惠券状态异常，请稍后再试。");
            }
            o.setIsUseCoupon(1);
            o.setCouponId(c.getId());
            o.setCouponName(c.getCouponName());
            o.setCouponType(c.getType());
            o.setCouponPrice(c.getCouponPrice());
        }
        //下单完成给用户发送成功短信
        SmsSendUtil.sendSmsToCustomer(cust.getMobile());
        o.setOrderPrice(orderPrice);
        o.setRealPrice(orderPrice);
        return getDao().add(o);

    }

    //快修时用户公里范围内没有门店保存订单
    public void saveRangeOrder(Order o, Customer cust, Coupon c, BigDecimal orderPrice, Boolean isUseCoupon) {
        WechatRangeOrder rangeOrder = new WechatRangeOrder();
        rangeOrder.setId(UUID.randomUUID().toString().replace("-", ""));
        rangeOrder.setCustomerId(cust.getId());
        rangeOrder.setCustomerName(cust.getName());
        rangeOrder.setBrandId(o.getBrandId());
        rangeOrder.setBrandName(o.getBrandName());
        rangeOrder.setModelId(o.getModelId());
        rangeOrder.setModelName(o.getModelName());
        rangeOrder.setColor(o.getColor());
        rangeOrder.setRepairType(o.getRepairType());
        rangeOrder.setOrderPrice(orderPrice);
        if (isUseCoupon) {
            rangeOrder.setIsUseCoupon(1);
            rangeOrder.setCouponId(c.getId());
            rangeOrder.setCouponName(c.getCouponName());
            rangeOrder.setCouponType(c.getType());
            rangeOrder.setCouponPrice(c.getCouponPrice());
        }
        FromSystem fromSystem = fromSystemService.queryById(o.getFromSystem());
        rangeOrder.setFromSystem(fromSystem.getName());
        rangeOrder.setMobile(cust.getMobile());
        rangeOrder.setLatitude(cust.getLatitude());
        rangeOrder.setLongitude(cust.getLongitude());
        rangeOrder.setProvince(cust.getProvince());
        rangeOrder.setCity(cust.getCity());
        rangeOrder.setCounty(cust.getCounty());
        rangeOrder.setStreet(cust.getStreet());
        rangeOrder.setAreas(cust.getAreas());
        rangeOrder.setAddress(cust.getAddress());
        rangeOrder.setPostscript(o.getPostscript());
        wechatRangeOrderService.add(rangeOrder);
    }


    /**
     * 寄修模式下的保存订单
     *
     * @tip true表示不判断地址可信度下单
     */
    @Transactional
    public int sendSave(Order o, String projectIds, Customer cust, boolean tip) {
        //验证品牌 一期品牌为默认值iPhone
        //品牌设置默认值
        Brand b = brandService.queryById(o.getBrandId());
        if (b == null) {
            throw new SystemException("品牌不存在");
        }
        o.setBrandName(b.getName());
        //验证机型
        Model m = modelService.queryById(o.getModelId());
        if (m == null) {
            throw new SystemException("机型不存在");
        }
        o.setModelName(m.getName());

        List<String> pIds = new ArrayList<>();
        //保存订单明细
        if (StringUtils.isBlank(projectIds)) {
            throw new SystemException("故障类型不能为空");
        }
        JSONArray pjIds = JSONArray.parseArray(projectIds);
        for (int i = 0; i < pjIds.size(); i++) {
            String projectId = pjIds.getString(i);
            if (StringUtils.isBlank(projectId)) {
                continue;
            }
            pIds.add(projectId);
        }
        //判断是否使用优惠券
        boolean isUseCoupon = false;
        Coupon c = null;
        if (StringUtils.isNotBlank(o.getCouponCode())) {
            //判断优惠券是否存在
            c = couponService.queryByCode(o.getCouponCode());
            if (c == null) {
                throw new SystemException("亲，您输入的优惠券不存在。请确认优惠券是否正确。");
            }
            //判断优惠券是否使用
            if (c.getIsUse() != 0) {
                throw new SystemException("亲，您输入的优惠券已使用。请确认优惠券是否正确。");
            }
            if (c.getType() != 0) {
                throw new SystemException("亲，您输入的优惠券不支持维修订单使用");
            }
            //判断优惠券是否在有效期内
            String nowDay = DateUtil.getNowyyyyMMdd();
//    		if(nowDay.compareTo(c.getBeginTime()) < 0){
//    			throw new SystemException("亲，您输入的优惠券还未生效，无法使用。");
//    		}
            if (nowDay.compareTo(c.getEndTime()) > 0) {
                throw new SystemException("亲，您输入的优惠券已超过有效期，无法使用。");
            }
            boolean isCanUse = false;
            StringBuffer msg = new StringBuffer();
            //判断优惠券是否可用
            //非品牌通用优惠券判断品牌支持
            if (c.getIsBrandCurrency() == 0) {
                List<CouponModel> couModels = couponModelService.queryListByCouponId(c.getId());
                if (couModels != null && !couModels.isEmpty()) {
                    for (CouponModel cm : couModels) {
                        if (msg.length() > 0) {
                            msg.append("、");
                        }
                        msg.append(cm.getBrandName());
                        if (o.getBrandId().equals(cm.getBrandId())) {
                            isCanUse = true;
                            break;
                        }
                    }
                    if (!isCanUse) {
                        throw new SystemException("亲，您输入的优惠券只支持：" + msg.toString() + " 手机品牌使用。");
                    }
                }
            }
            //非故障通用优惠券判断是否支持故障
            if (c.getIsProjectCurrency() == 0) {
                List<CouponProject> couProjects = couponProjectService.queryListByCouponId(c.getId());
                if (couProjects != null && !couProjects.isEmpty()) {
                    isCanUse = false;
                    msg.setLength(0);
                    for (CouponProject cp : couProjects) {
                        if (msg.length() > 0) {
                            msg.append("、");
                        }
                        msg.append(cp.getProjectName());
                        for (String pid : pIds) {
                            if (pid.equals(cp.getProjectId())) {
                                isCanUse = true;
                                break;
                            }
                        }
                        if (isCanUse) {
                            break;
                        }
                    }
                    if (!isCanUse) {
                        throw new SystemException("亲，您输入的优惠券只支持：" + msg.toString() + " 故障使用。");
                    }
                }
            }
            isUseCoupon = true;
        }

        //根据客户填写的地址获取用户坐标  后台系统下单采用高德直接转化，其他的先用百度判断地址可信度
        JSONObject lal = null;
        if (tip) {
            lal = BaiduMapUtil.gaode(cust.getFullAddress());
        } else {
            lal = BaiduMapUtil.getLatAndLngByAddr(cust.getFullAddress());
        }
        System.out.println("百度转化地址:" + lal);
        //设置坐标
        cust.setLongitude(new BigDecimal(lal.getFloat("lng")));
        cust.setLatitude(new BigDecimal(lal.getFloat("lat")));
        //用户坐标
        o.setLatitude(cust.getLatitude());
        o.setLongitude(cust.getLongitude());
        cust.setIsDel(0);
        cust.setStatus(0);
        //保存用户信息
        cust = customerService.save(cust);
        //生成订单号
        String orderNo = NOUtil.getNo("PO-");
        o.setOrderNo(orderNo);
        //生成订单id
        String orderId = UUID.randomUUID().toString().replace("-", "");
        o.setId(orderId);

        //设置客户信息
        o.setCustomerId(cust.getId());
        o.setCustomerName(cust.getName());
        o.setMobile(cust.getMobile());
        o.setEmail(cust.getEmail());
        //客户地址
        o.setProvince(cust.getProvince());
        o.setCity(cust.getCity());
        o.setCounty(cust.getCounty());
        o.setStreet(cust.getStreet());
        o.setAreas(cust.getAreas());
        o.setAddress(cust.getAddress());


        //设置初始值
        o.setIsLock(0);
        o.setSort(0);
        o.setIsDel(0);
        o.setIsDeposit(0);
        o.setDepositPrice(new BigDecimal(0));
        o.setDepositType(0);
        o.setBalanceStatus(0);
        o.setCancelType(0);
        o.setCancelStatus(0);
        o.setSendAgreedNews(0);
        //  如果该订单不是店员创建的 将clerkId设置为0
        if (o.getClerkId() == null) {
            o.setClerkId("0");
        }
        //下单时的金额（含税）=实际金额+退款金额
        BigDecimal orderPrice = new BigDecimal(0);
        //保存订单明细
        for (String pid : pIds) {
            OrderDetail od = new OrderDetail();
            od.setOrderNo(orderNo);
            //查询维修费用
            RepairCost cost = costService.queryByMidAndPid(o.getModelId(), pid);
            if (cost == null) {
                throw new SystemException("该机型不支持该故障的维修！");
            }
            od.setProjectId(cost.getProjectId());
            od.setProjectName(cost.getProjectName());
            od.setType(0);
            od.setPrice(cost.getPrice());
            od.setRealPrice(od.getPrice());
            od.setIsDel(0);
            detailService.add(od);

            orderPrice = NumberUtils.add(orderPrice, od.getRealPrice());
        }
        if (isUseCoupon && c != null) {
            //修改优惠券改为已使用
            c.setUpdateUserid(cust.getId());
            int rest = couponService.updateForUse(c);
            if (rest == 0) {
                throw new SystemException("亲，您输入的优惠券状态异常，请稍后再试。");
            }
            o.setIsUseCoupon(1);
            o.setCouponId(c.getId());
            o.setCouponName(c.getCouponName());
            o.setCouponType(c.getType());
            o.setCouponPrice(c.getCouponPrice());
        }
        //下单完成给用户发送成功短信
        if (o.getRepairType() == OrderConstant.SEND_TO_SHOP_REPAIR) {
            //寄修订单下单成功，向客户发送下单成功信息
            SmsSendUtil.mailSendSmsToCustomer(cust.getMobile());
        } else {
            //上门维修订单
            SmsSendUtil.sendSmsToCustomer(cust.getMobile());
        }
        o.setOrderPrice(orderPrice);
        o.setRealPrice(orderPrice);
        return getDao().add(o);

    }


    /**
     * 发起保证金
     * 1、查询订单是否存在
     * 2、校验是否有订单操作权限
     * 3、校验订单状态是否已发起支付
     * 4、生成支付订单号
     * 5、保存支付日志
     *
     * @param id
     * @param su
     * @author: lijx
     * @CreateDate: 2016-9-7 下午10:31:07
     */
    @Transactional
    public void startPayDeposit(String id, String openid, SessionUser su) {
        Order o = getDao().queryById(id);
        //这里默认订单支付完成
        o.setIsDeposit(2);
        o.setDepositTime(new Date());
        o.setOrderStatus(OrderConstant.ORDER_STATUS_DEPOSITED);
        getDao().update(o);

        //支付完成自动派单
        autoDispatch(o);
    }

    /**
     * 查未配单的订单
     *
     * @return
     * @CreateDate: 2016-9-15 下午10:26:11
     */
    public List<Order> queryUnDispatch() {
        Order o = new Order();
        //未派单
        o.setIsDispatch(0);
        //订单状态已支付保证金
        o.setOrderStatus(2);
        return getDao().queryList(o);
    }

    /**
     * 商超过15分钟没有派单的订单
     *
     * @return
     * @throws ParseException
     * @author: lijx
     * @CreateDate: 2016-10-19 上午2:34:11
     */
    public List<Order> queryUnDispatchForShop() throws ParseException {
        Order o = new Order();
        //未派单,只针对上门维修模式
        o.setRepairType(0);
        o.setIsDispatch(2);
        //订单状态已支付保证金
        o.setOrderStatus(2);
        o.setDispatchTime(DateUtil.getDateAddMinute(-15));
        return getDao().queryList(o);
    }

    /**
     * 超过多少分钟没有预约的订单
     */
    public List<Order> queryUnAgreedForShop(int waitTime, int agreedStatus) throws ParseException {
        Order o = new Order();
        // 已分配工程师，未预约
        o.setOrderStatus(OrderConstant.ORDER_STATUS_DISPATCHED);
        o.setSendAgreedNews(agreedStatus);
        o.setInTime(DateUtil.getDateAddMinute(-waitTime));
        return getDao().queryList(o);
    }

    /**
     * 查询总订单数
     *
     * @return
     * @CreateDate: 2016-9-15 下午10:26:11
     */
    public int queryAllCount(Order o) {
        //已删除的订单不计算订单量
        o.setIsDel(0);
        return getDao().queryCount(o);
    }

    /**
     * 查询日订单数
     *
     * @return
     * @CreateDate: 2016-9-15 下午10:26:11
     */
    public int queryDayCount(Order o) {
        o.setQueryStartTime(DateUtil.getNowyyyyMM000000());
        //已删除的订单不计算订单量
        o.setIsDel(0);
        return getDao().queryCount(o);
    }

    /**
     * 查询周订单数
     *
     * @return
     * @CreateDate: 2016-9-15 下午10:26:11
     */
    public int queryWeekCount(Order o) {
        o.setQueryStartTime(DateUtil.getDateyyyyMMdd000000(DateUtil.getWeekFirstDay()));
        //已删除的订单不计算订单量
        o.setIsDel(0);
        return getDao().queryCount(o);
    }

    /**
     * 查询月订单数
     *
     * @return
     * @CreateDate: 2016-9-15 下午10:26:11
     */
    public int queryMonthCount(Order o) {
        o.setQueryStartTime(DateUtil.getDateyyyyMMdd000000(DateUtil.getMonthFirstDay()));
        //已删除的订单不计算订单量
        o.setIsDel(0);
        return getDao().queryCount(o);
    }

    /**
     * 查询平台日交易金额
     *
     * @param o
     * @return
     * @CreateDate: 2016-10-18 上午12:15:02
     */
    public BigDecimal queryDayAmount(Order o) {
        if (o == null) {
            o = new Order();
        }
        o.setQueryStartTime(DateUtil.getNowyyyyMM000000());
        //已删除的订单不计算订单量
        o.setIsDel(0);
        return getDao().queryAmount(o);
    }

    /**
     * 查询平台周交易金额
     *
     * @param o
     * @return
     * @CreateDate: 2016-10-18 上午12:15:02
     */
    public BigDecimal queryWeekAmount(Order o) {
        if (o == null) {
            o = new Order();
        }
        o.setQueryStartTime(DateUtil.getDateyyyyMMdd000000(DateUtil.getWeekFirstDay()));
        //已删除的订单不计算订单量
        o.setIsDel(0);
        return getDao().queryAmount(o);
    }

    /**
     * 查询平台月交易金额
     *
     * @param o
     * @return
     * @CreateDate: 2016-10-18 上午12:15:02
     */
    public BigDecimal queryMonthAmount(Order o) {
        if (o == null) {
            o = new Order();
        }
        o.setQueryStartTime(DateUtil.getDateyyyyMMdd000000(DateUtil.getMonthFirstDay()));
        //已删除的订单不计算订单量
        o.setIsDel(0);
        return getDao().queryAmount(o);
    }

    /**
     * 查询平台总交易金额
     *
     * @param o
     * @return
     * @CreateDate: 2016-10-18 上午12:15:02
     */
    public BigDecimal queryAllAmount(Order o) {
        if (o == null) {
            o = new Order();
        }
        //已删除的订单不计算订单量
        o.setIsDel(0);
        return getDao().queryAmount(o);
    }

    /**
     * 统计工程师本月订单数
     *
     * @return
     * @CreateDate: 2016-9-15 下午10:26:11
     */
    public Option queryStatisticByEngineer(String engineerId) {
        Order t = new Order();
        t.setEngineerId(engineerId);
        t.setQueryStartTime(DateUtil.getDateyyyyMMdd000000(DateUtil.getMonthFirstDay()));
        //已删除的订单不计算订单量
        t.setIsDel(0);
        List<Order> list = getDao().queryStatisticByEngineer(t);

        Option option = new Option();
        option.title("本月订单统计：");
        //工具栏
        option.toolbox().show(false);
        //纵坐标
        ValueAxis valueAxis = new ValueAxis();
        valueAxis.name("订单数").axisLabel().formatter("{value}");
        valueAxis.minInterval(1);
        option.yAxis(valueAxis);
        //横坐标
        //创建类目轴  
        CategoryAxis category = new CategoryAxis();
        category.name("日期");
        //数据
        String legendName = "订单数";
        Line line = new Line();
        line.name(legendName);
        option.legend().data(legendName).x(X.center).y(Y.bottom);
        if (list != null && list.size() > 0) {
            for (Order o : list) {
                category.data(DateUtil.getDateYYYYMMDD(o.getEndTime()));
                line.data(o.getSort());
            }
        }
        option.xAxis(category);
        option.series(line);
        return option;
    }

    /**
     * 查未配单的订单
     *
     * @return
     * @CreateDate: 2016-9-15 下午10:26:11
     */
    public int queryUnDispatchCount(String shopCode) {
        Order o = new Order();
        //未派单
        o.setIsDispatch(2);
        //订单状态已支付保证金
        o.setOrderStatus(2);
        o.setShopCode(shopCode);
        return getDao().queryCount(o);
    }

    @Autowired
    private NewEngineerService newEngineerService;

    /**
     * 自动配单
     *
     * @CreateDate: 2016-9-15 下午5:48:51
     */
    @Transactional
    public void autoDispatch(Order o) {
        List<Shop> shopL = shopService.queryIdleShopByLonAndLat(o);
        if (shopL == null || shopL.size() == 0) {
            //用户附近没有空闲的维修门店
            return;
        }
        //是否派单成功
        boolean isDispatched = false;
        for (Shop s : shopL) {
            //判断商品派单模式：自动派单还是手动派单
            if (s.getDispatchType() == 0) {
                //自动派单则查询空闲的工程师
                Engineer eng = newEngineerService.queryUnDispatchByShopCode(s.getCode());
                if (eng == null) {
                    continue;
                }
                //自动分配订单给空闲工程师
                o.setProviderCode(s.getProviderCode());
                o.setProviderName(s.getProviderName());
                o.setShopCode(s.getCode());
                o.setShopName(s.getName());
                o.setEngineerId(eng.getId());
                o.setEngineerName(eng.getName());
                o.setEngineerNumber(eng.getNumber());
                o.setEngineerMobile(eng.getMobile());
                o.setIsDispatch(1);
                o.setDispatchTime(new Date());
                o.setOrderStatus(OrderConstant.ORDER_STATUS_DISPATCHED);
                getDao().update(o);
                //更改工程师状态
                eng.setIsDispatch(1);
                engineerService.saveUpdate(eng);
                //给工程师发送短信提示
                SmsSendUtil.sendSmsToEngineer(eng, s, o);
                isDispatched = true;
                break;
            } else if (s.getDispatchType() == 1) {
                //如果是手动派单则判断空闲工程师数量是否大于已分配的订单数
                int unDispatchEngCt = newEngineerService.queryUnDispatchCount(s.getCode());
                int unDispatchOrderCt = queryUnDispatchCount(s.getCode());
                if (unDispatchEngCt > unDispatchOrderCt) {
                    //存在多余的工程师
                    //自动分配订单给维修门店
                    o.setProviderCode(s.getProviderCode());
                    o.setProviderName(s.getProviderName());
                    o.setShopCode(s.getCode());
                    o.setShopName(s.getName());
                    o.setIsDispatch(2);
                    o.setDispatchTime(new Date());
                    getDao().update(o);
                    //给维修门店管理员发送短信提示
                    SmsSendUtil.sendSmsToShop(s, o);
                    isDispatched = true;
                    break;
                }
            }
        }

        if (!isDispatched) {
            for (Shop s : shopL) {
                //判断商品派单模式：自动派单还是手动派单
                if (s.getDispatchType() == 0) {
                    //自动派单则查询忙碌的工程师
                    Engineer eng = newEngineerService.queryDispatchByShopCode(s.getCode(), null);
                    if (eng == null) {
                        continue;
                    }
                    //自动分配订单给忙碌工程师
                    o.setProviderCode(s.getProviderCode());
                    o.setProviderName(s.getProviderName());
                    o.setShopCode(s.getCode());
                    o.setShopName(s.getName());
                    o.setEngineerId(eng.getId());
                    o.setEngineerName(eng.getName());
                    o.setEngineerNumber(eng.getNumber());
                    o.setEngineerMobile(eng.getMobile());
                    o.setDispatchTime(new Date());
                    o.setOrderStatus(OrderConstant.ORDER_STATUS_DISPATCHED);
                    getDao().update(o);
                    //更改工程师状态
                    eng.setIsDispatch(1);
                    engineerService.saveUpdate(eng);
                    //给工程师发送短信提示
                    SmsSendUtil.sendSmsToEngineer(eng, s, o);
                    isDispatched = true;
                    break;

                }
            }
        }

        if (!isDispatched) {
            //如果派单失败则将订单派给最近的维修门店
            Shop s = shopL.get(0);
            o.setProviderCode(s.getProviderCode());
            o.setProviderName(s.getProviderName());
            o.setShopCode(s.getCode());
            o.setShopName(s.getName());
            o.setIsDispatch(2);
            o.setDispatchTime(new Date());
            getDao().update(o);
            //给维修门店管理员发送短信提示
            SmsSendUtil.sendSmsToShop(s, o);
        }
    }

    /**
     * 重新派单
     *
     * @CreateDate: 2016-9-15 下午5:48:51
     */
    @Transactional
    public void againOrder(String orderNo, SessionUser su, Engineer eng) {
        //检查订单状态
        Order o = getDao().queryByOrderNo(orderNo);
        ReworkOrder reworkOrder = reworkOrderService.getDao().queryByReworkNo(orderNo);
        if (o == null && reworkOrder == null) {
            throw new SystemException("订单不存在！");
        }
        String oldEngineerId = "";
        if (o != null) {
            //记录原来的维修门店及维修工程师
            oldEngineerId = o.getEngineerId();
            if (eng.getId().equals(oldEngineerId)) {
                throw new SystemException("该订单已派单给此工程师，无需重复操作！");
            }
            //获取工程师姓名
            eng = newEngineerService.engineerShopCode(eng);
            Provider provider=providerService.queryByCode(eng.getProviderCode());
            //派单给工程师
            o.setProviderCode(eng.getProviderCode());
            o.setProviderName(provider.getName());
            o.setShopCode(eng.getShopCode());
            o.setShopName(eng.getShopName());
            o.setEngineerId(eng.getId());
            o.setEngineerName(eng.getName());
            o.setEngineerNumber(eng.getNumber());
            o.setEngineerMobile(eng.getMobile());
            o.setIsDispatch(1);
            o.setDispatchTime(new Date());
            o.setOrderStatus(OrderConstant.ORDER_STATUS_DISPATCHED);
            getDao().update(o);
            //更改工程师状态
            eng.setIsDispatch(1);
            engineerService.saveUpdate(eng);
            //给工程师发送短信提示
            SmsSendUtil.sendSmsToEngineer(eng, null, o);
        } else if (reworkOrder != null) {
            Order order = getDao().queryByOrderNo(reworkOrder.getParentOrder());
            //记录原来的维修门店及维修工程师
            oldEngineerId = reworkOrder.getEngineerId();
            if (eng.getId().equals(oldEngineerId)) {
                throw new SystemException("该订单已派单给此工程师，无需重复操作！");
            }
            //获取工程师姓名
            eng = newEngineerService.engineerShopCode(eng);
            Provider provider=providerService.queryByCode(eng.getProviderCode());
            //派单给工程师
            reworkOrder.setProviderCode(eng.getProviderCode());
            reworkOrder.setProviderName(provider.getName());
            reworkOrder.setShopCode(eng.getShopCode());
            reworkOrder.setShopName(eng.getShopName());
            reworkOrder.setEngineerId(eng.getId());
            reworkOrder.setEngineerName(eng.getName());
            reworkOrder.setEngineerNumber(eng.getNumber());
            reworkOrder.setEngineerMobile(eng.getMobile());
            reworkOrder.setIsDispatch(1);
            reworkOrder.setDispatchTime(new Date());
            reworkOrder.setOrderStatus(OrderConstant.ORDER_STATUS_DISPATCHED);
            reworkOrderService.saveUpdate(reworkOrder);
            //更改工程师状态
            eng.setIsDispatch(1);
            engineerService.saveUpdate(eng);
            //给工程师发送短信提示
            SmsSendUtil.sendSmsToEngineerForRework(eng, null, reworkOrder, order);
        }

        //将原工程师状态改为空闲
        engineerService.checkDispatchState(oldEngineerId);
    }

    /**
     * 重新派单
     *
     * @CreateDate: 2016-9-15 下午5:48:51
     */
    @Transactional
    public void reDispatch(String id, SessionUser su) {
        //检查订单状态
        Order o = getDao().queryById(id);
        if (o == null) {
            throw new SystemException("订单不存在！");
        }
        List<Shop> shopL = shopService.queryIdleShopByLonAndLat(o);
        if (shopL == null) {
            //用户附近没有空闲的维修门店
            throw new SystemException("附近没有维修门店请及时联系客户！");
        }
        //记录原来的维修门店及维修工程师
        String oldShopCode = o.getShopCode();
        String oldEngineerId = o.getEngineerId();
        Shop oldShop = null;
        //是否派单成功
        boolean isDispatched = false;
        for (Shop s : shopL) {
            if (s.getCode().equals(oldShopCode)) {
                //第一次优先考虑其它维修门店
                oldShop = s;
                continue;
            }
            //判断商品派单模式：自动派单还是手动派单
            if (s.getDispatchType() == 0) {
                //自动派单则查询空闲的工程师
                Engineer eng = engineerService.queryUnDispatchByShopCode(s.getCode());
                if (eng == null) {
                    continue;
                }
                //自动分配订单给空闲工程师
                o.setProviderCode(s.getProviderCode());
                o.setProviderName(s.getProviderName());
                o.setShopCode(s.getCode());
                o.setShopName(s.getName());
                o.setEngineerId(eng.getId());
                o.setEngineerName(eng.getName());
                o.setEngineerNumber(eng.getNumber());
                o.setEngineerMobile(eng.getMobile());
                o.setIsDispatch(1);
                o.setDispatchTime(new Date());
                o.setOrderStatus(OrderConstant.ORDER_STATUS_DISPATCHED);
                getDao().update(o);
                //更改工程师状态
                eng.setIsDispatch(1);
                engineerService.saveUpdate(eng);
                //给工程师发送短信提示
                SmsSendUtil.sendSmsToEngineer(eng, s, o);
                isDispatched = true;
                break;
            } else if (s.getDispatchType() == 1) {
                //如果是手动派单则判断空闲工程师数量是否大于已分配的订单数
                int unDispatchEngCt = engineerService.queryUnDispatchCount(s.getCode());
                int unDispatchOrderCt = queryUnDispatchCount(s.getCode());
                if (unDispatchEngCt > unDispatchOrderCt) {
                    //存在多余的工程师
                    //自动分配订单给维修门店
                    o.setProviderCode(s.getProviderCode());
                    o.setProviderName(s.getProviderName());
                    o.setShopCode(s.getCode());
                    o.setShopName(s.getName());
                    o.setEngineerId("");
                    o.setEngineerName("");
                    o.setEngineerNumber("");
                    o.setEngineerMobile("");
                    o.setIsDispatch(2);
                    o.setDispatchTime(new Date());
                    o.setOrderStatus(OrderConstant.ORDER_STATUS_DEPOSITED);
                    getDao().update(o);
                    //给维修门店管理员发送短信提示
                    SmsSendUtil.sendSmsToShop(s, o);
                    isDispatched = true;
                    break;
                }
            }//end else
        }//end for
        if (!isDispatched) {
            for (Shop s : shopL) {
                if (s.getCode().equals(oldShopCode)) {
                    //第一次优先考虑其它维修门店
                    oldShop = s;
                    continue;
                }
                //判断商品派单模式：自动派单还是手动派单
                if (s.getDispatchType() == 0) {
                    //自动派单则查询忙碌的工程师
                    Engineer eng = engineerService.queryDispatchByShopCode(s.getCode(), null);
                    if (eng == null) {
                        continue;
                    }
                    //自动分配订单给忙碌工程师
                    o.setProviderCode(s.getProviderCode());
                    o.setProviderName(s.getProviderName());
                    o.setShopCode(s.getCode());
                    o.setShopName(s.getName());
                    o.setEngineerId(eng.getId());
                    o.setEngineerName(eng.getName());
                    o.setEngineerNumber(eng.getNumber());
                    o.setEngineerMobile(eng.getMobile());
                    o.setDispatchTime(new Date());
                    o.setOrderStatus(OrderConstant.ORDER_STATUS_DISPATCHED);
                    getDao().update(o);
                    //更改工程师状态
                    eng.setIsDispatch(1);
                    engineerService.saveUpdate(eng);
                    //给工程师发送短信提示
                    SmsSendUtil.sendSmsToEngineer(eng, s, o);
                    isDispatched = true;
                    break;
                }//end else
            }//end for
        }
        //判断是否派单成功
        if (!isDispatched && oldShop != null) {
            //如果没有派单成功则派单给原维修门店其它维修工程师
            //自动派单则查询空闲的工程师,
            //这里目前不用添加其它条件，因为是查询的空闲工程师，原维修工程师状态为非空闲状态
            //如果改成一人多单则需要排除原维修工程师
            Engineer eng = engineerService.queryUnDispatchByShopCode(oldShop.getCode(), oldEngineerId);
            if (eng != null) {
                //自动分配订单给空闲工程师
                o.setProviderCode(oldShop.getProviderCode());
                o.setProviderName(oldShop.getProviderName());
                o.setShopCode(oldShop.getCode());
                o.setShopName(oldShop.getName());
                o.setEngineerId(eng.getId());
                o.setEngineerName(eng.getName());
                o.setEngineerNumber(eng.getNumber());
                o.setEngineerMobile(eng.getMobile());
                o.setIsDispatch(1);
                o.setOrderStatus(OrderConstant.ORDER_STATUS_DISPATCHED);
                o.setDispatchTime(new Date());
                getDao().update(o);
                //更改工程师状态
                eng.setIsDispatch(1);
                engineerService.saveUpdate(eng);
                //给工程师发送短信提示
                SmsSendUtil.sendSmsToEngineer(eng, oldShop, o);
                isDispatched = true;
            }//end if(eng != null)
        }// end if(!isDispatched)

        //判断是否派单成功
        if (!isDispatched && oldShop != null) {
            //如果没有派单成功则派单给原维修门店其它维修工程师
            //自动派单则查询忙碌的工程师,
            //这里目前不用添加其它条件，因为是查询的空闲工程师，原维修工程师状态为非空闲状态
            //如果改成一人多单则需要排除原维修工程师
            Engineer eng = engineerService.queryDispatchByShopCode(oldShop.getCode(), oldEngineerId);
            if (eng != null) {
                //自动分配订单给忙碌工程师
                o.setProviderCode(oldShop.getProviderCode());
                o.setProviderName(oldShop.getProviderName());
                o.setShopCode(oldShop.getCode());
                o.setShopName(oldShop.getName());
                o.setEngineerId(eng.getId());
                o.setEngineerName(eng.getName());
                o.setEngineerNumber(eng.getNumber());
                o.setEngineerMobile(eng.getMobile());
                o.setOrderStatus(OrderConstant.ORDER_STATUS_DISPATCHED);
                o.setDispatchTime(new Date());
                getDao().update(o);
                //更改工程师状态
                eng.setIsDispatch(1);
                engineerService.saveUpdate(eng);
                //给工程师发送短信提示
                SmsSendUtil.sendSmsToEngineer(eng, oldShop, o);
                isDispatched = true;
            }//end if(eng != null)
        }// end if(!isDispatched)
        //再次判断是否派单成功
        if (isDispatched) {
            //如果派单成功则将原工程师状态改为空闲
//            Engineer eng = engineerService.queryById(oldEngineerId);
//            if (eng != null && eng.getIsDispatch() == 1) {
//                eng.setIsDispatch(0);
//                engineerService.saveUpdate(eng);
//            }
            engineerService.checkDispatchState(oldEngineerId);
        } else {
            log.info("附近没有维修工程师！");
        }
    }

    /**
     * 派单给维修门店
     *
     * @param o
     * @param su
     * @CreateDate: 2016-9-7 下午10:33:09
     */
    public void dispatchToShop(Order o, SessionUser su) {

    }

    /**
     * 派单给工程师
     *
     * @param id
     * @param su
     * @CreateDate: 2016-9-7 下午10:33:09
     */
    public void dispatchToEngineer(String id, String engId, SessionUser su) {
        //检查订单状态
        Order o = getDao().queryById(id);
        if (o == null) {
            throw new SystemException("订单不存在！");
        }
//        if(!su.getShopCode().equals(o.getShopCode())){
//            throw new SystemException("对不起，您无权操作该订单！原因可能是已转派给其它维修门店。");
//        }
        if (engId.equals(o.getEngineerId())) {
            throw new SystemException("该订单已派单给此工程师，无需重复操作！");
        }
        //检查工程师状态
        Engineer eng = engineerService.queryById(engId);
        if (eng.getIsDispatch() == 2) {
            throw new SystemException("派单失败，工程师：" + eng.getName() + ", 处于离线状态");
        }
        String oldEngineerId = o.getEngineerId();
        o.setEngineerId(eng.getId());
        o.setEngineerName(eng.getName());
        o.setEngineerNumber(eng.getNumber());
        o.setEngineerMobile(eng.getMobile());
        o.setIsDispatch(1);
        o.setDispatchTime(new Date());
        //如果是寄修订单  则派单直接改状态为待检测
        if (o.getRepairType() == OrderConstant.SEND_TO_SHOP_REPAIR) {
            o.setOrderStatus(OrderConstant.ORDER_STATUS_START_CHECK);
        } else {
            o.setOrderStatus(OrderConstant.ORDER_STATUS_DISPATCHED);
        }
        getDao().update(o);
        //更改工程师状态
        eng.setIsDispatch(1);
        engineerService.saveUpdate(eng);

        //更新原工程师状态
//        Engineer engOld = engineerService.queryById(oldEngineerId);
//        if (engOld != null && engOld.getIsDispatch() == 1) {
//        	engOld.setIsDispatch(0);
//            engineerService.saveUpdate(engOld);
//        }
        engineerService.checkDispatchState(oldEngineerId);
        //给工程师发送短信提示

        if (o.getRepairType() == OrderConstant.SEND_TO_SHOP_REPAIR) {
            //寄修订单发送短信
            SmsSendUtil.sendSmsToEngineerForMailOrder(eng, o);
        } else {
            //上门维修发送短信
            SmsSendUtil.sendSmsToEngineerWithoutShop(eng, o);
        }

    }

    /**
     * 开始检测
     *
     * @param orderNo
     * @param su
     * @CreateDate: 2016-9-7 下午10:41:04
     */
    public void startCheck(String orderNo, SessionUser su) {

    }

    /**
     * 发起保证金
     * 1、查询订单是否存在
     * 2、校验是否有订单操作权限
     * 3、校验订单状态是否已发起支付
     * 4、生成支付订单号
     * 5、保存支付日志
     *
     * @param id
     * @param su
     * @author: lijx
     * @CreateDate: 2016-9-7 下午10:31:07
     */
    @Transactional
    public void startPayBalance(String id, String openid, SessionUser su) {
        Order o = getDao().queryById(id);
        //这里默认订单支付完成
        o.setPayStatus(1);
        o.setPayTime(new Date());
        //o.setOrderStatus(OrderConstant.ORDER_STATUS_END_PAY);
        getDao().update(o);
    }

    /**
     * 开始维修
     *
     * @param orderNo
     * @param su
     * @author: lijx
     * @CreateDate: 2016-9-7 下午10:41:04
     */
    public void startRepair(String orderNo, SessionUser su) {

    }

    /**
     * 完成维修
     *
     * @param orderNo
     * @param su
     * @author: lijx
     * @CreateDate: 2016-9-7 下午10:41:04
     */
    public void endRepair(String orderNo, SessionUser su) {

    }

    /**
     * 重置故障
     *
     * @param order_no
     * @param su
     * @author: lijx
     * @CreateDate: 2016-9-7 下午10:41:04
     */
    public void resetRepair(String order_no, SessionUser su) {
        Order o = new Order();
        if (order_no.contains("PO")) {
            o = getDao().queryByOrderNo(order_no);
        } else {
            ReworkOrder reworkOrder = reworkOrderService.getDao().queryByReworkNo(order_no);
            if (reworkOrder == null) {
                throw new SystemException("订单不存在！");
            }
            o = getDao().queryByOrderNo(reworkOrder.getParentOrder());
        }
        if (o == null) {
            throw new SystemException("订单不存在！");
        }
        if (su.getType() != SystemConstant.USER_TYPE_SYSTEM
                && su.getType() != SystemConstant.USER_TYPE_CUSTOMER_SERVICE) {
            throw new SystemException("对不起，您无权操作该订单！");
        }
        if (o.getOrderStatus() == OrderConstant.ORDER_STATUS_CANCEL) {
            throw new SystemException("该订单已取消！");
        }

        if (o.getOrderStatus() == OrderConstant.ORDER_STATUS_FINISHED) {
            throw new SystemException("该订单已完成！");
        }
        if (o.getOrderStatus() < OrderConstant.ORDER_STATUS_END_REPAIR) {
            throw new ApiServiceException(ApiResultConstant.resultCode_3002, "订单未完成维修");
        }
        if (o.getOrderStatus() != OrderConstant.ORDER_STATUS_END_REPAIR) {
            throw new SystemException("该订单无法重置故障！");
        } else {
            //根据订单号删除维修记录
            detailService.delRepairByOrderNo(o.getOrderNo());
            List<UpdateOrderPrice> orderPrices = updateOrderPriceService.getDao().queryByUpOrderNo(o.getOrderNo());
            //未经管理员修改过金额的订单客服才可以操作
            if (CollectionUtils.isEmpty(orderPrices)) {
                o.setRealPrice(o.getOrderPrice());
            }
            o.setOrderStatus(OrderConstant.ORDER_STATUS_START_CHECK);
            o.setEndCheckTime(null);
            o.setRepairType(0);
            o.setUpdateUserid(su.getUserId());
            getDao().update(o);
        }

    }


    /**
     * 预约维修时间
     *
     * @param id
     * @param su
     * @author: lijx
     * @CreateDate: 2016-9-7 下午10:41:04
     */
    public void agreedTime(String id, String agreedTime, String agreedNote, SessionUser su) {
        Order o = getDao().queryById(id);
        if (o == null) {
            throw new SystemException("订单不存在！");
        }
        if (su.getType() != SystemConstant.USER_TYPE_SYSTEM
                && su.getType() != SystemConstant.USER_TYPE_CUSTOMER_SERVICE
                && su.getType() != SystemConstant.USER_TYPE_SHOP) {
            throw new SystemException("对不起，您无权操作该订单！");
        }
        if (o.getOrderStatus() == OrderConstant.ORDER_STATUS_CANCEL) {
            throw new SystemException("该订单已取消！");
        }

        if (o.getOrderStatus() == OrderConstant.ORDER_STATUS_FINISHED) {
            throw new SystemException("该订单已完成！");
        }
        if (o.getOrderStatus() < OrderConstant.ORDER_STATUS_DISPATCHED) {
            throw new ApiServiceException(ApiResultConstant.resultCode_3002, "订单未派单");
        }
        if (o.getOrderStatus() == OrderConstant.ORDER_STATUS_DISPATCHED) {
            o.setAgreedTime(agreedTime);
            o.setEngNote(agreedNote);
            o.setOrderStatus(OrderConstant.ORDER_STATUS_RECEIVED);
            o.setUpdateUserid(su.getUserId());
            getDao().update(o);
        } else {
            throw new SystemException("该订单无法预约维修时间！");
        }

    }

    /**
     * 完成订单
     *
     * @param id
     * @param su
     * @author: lijx
     * @CreateDate: 2016-9-7 下午10:41:04
     */
    public void orderConfirmToFinish(String id, SessionUser su) {
        Order o = getDao().queryById(id);
        if (o == null) {
            throw new SystemException("订单不存在！");
        }
        if (!su.getUserId().equals(o.getCustomerId())) {
            throw new SystemException("对不起，您无权操作该订单！");
        }
        if (o.getOrderStatus() == OrderConstant.ORDER_STATUS_CANCEL) {
            throw new SystemException("该订单已取消！");
        }

        if (o.getOrderStatus() == OrderConstant.ORDER_STATUS_FINISHED) {
            throw new SystemException("该订单已完成！无需重复操作");
        }

//        if(o.getOrderStatus() != OrderConstant.ORDER_STATUS_END_PAY){
//            throw new SystemException("该订单未支付，不能执行完成操作！");
//        }

        //执行完成订单
        o.setOrderStatus(OrderConstant.ORDER_STATUS_FINISHED);
        o.setUpdateUserid(su.getUserId());
        o.setEndTime(new Date());
        getDao().update(o);

        //如果该订单是店员创建，完成订单后则为该店员增加对应积分
        getIntegralService.addIntegral(o);

        //维修工程师改为空闲状态
//        Engineer eng = engineerService.queryByEngineerNumber(o.getEngineerNumber());
//        if(eng.getIsDispatch() == 1){
//	        eng.setIsDispatch(0);
//	        engineerService.saveUpdate(eng);
//        }
        engineerService.checkDispatchState(o.getEngineerId());
    }

    /**
     * 取消订单
     *
     * @param id
     * @param su
     * @author: lijx
     * @CreateDate: 2016-9-7 下午10:41:04
     */
    @Transactional
    public void orderCancel(String id, int cancelType, String cancelReason, SessionUser su) {
        Order o = getDao().queryById(id);
        if (o == null) {
            throw new SystemException("订单不存在！");
        }
        if (su.getType() != SystemConstant.USER_TYPE_SYSTEM
                && su.getType() != SystemConstant.USER_TYPE_CUSTOMER_SERVICE
                && !su.getUserId().equals(o.getCustomerId())
                && !su.getUserId().equals(o.getEngineerId())) {
            throw new SystemException("对不起，您无权操作该订单！");
        }
        if (o.getOrderStatus() == OrderConstant.ORDER_STATUS_CANCEL) {
            throw new SystemException("该订单已取消，无需重复操作！");
        }

        if (o.getOrderStatus() == OrderConstant.ORDER_STATUS_FINISHED) {
            throw new SystemException("该订单已完成，不能取消！");
        }

        //执行取消订单
        //保存修改前订单状态
        o.setCancelStatus(o.getOrderStatus());
        o.setOrderStatus(OrderConstant.ORDER_STATUS_CANCEL);
        o.setCancelType(cancelType);
        o.setUpdateUserid(su.getUserId());
        o.setEndTime(new Date());
        if (cancelReason != null) {
            o.setCancelReason(cancelReason);
        }
        //判断是否需要退款
        if (o.getCancelStatus() >= OrderConstant.ORDER_STATUS_DEPOSITED
                || o.getPayStatus() == OrderConstant.ORDER_PAY_STATUS_SUBMITED) {
            o.setIsDrawback(OrderConstant.ORDER_REFUND_STATUS_WAIT);
        }
        getDao().update(o);
        //如果用户使用了优惠卷，将优惠卷状态改为可用
        if (StringUtils.isNotBlank(o.getCouponId())) {
            Coupon coupon = couponService.queryById(o.getCouponId());
            coupon.setIsUse(0);
            coupon.setUseTime(null);
            couponService.saveUpdate(coupon);
        }


        //如果订单已派单则将工程师改为空闲状态
        if (o.getCancelStatus() >= OrderConstant.ORDER_STATUS_DISPATCHED) {
//            Engineer eng = engineerService.queryByEngineerNumber(o.getEngineerNumber());
//            if(eng.getIsDispatch() == 1){
//	            eng.setIsDispatch(0);
//	            engineerService.saveUpdate(eng);
//            }
            engineerService.checkDispatchState(o.getEngineerId());

            if (cancelType != OrderConstant.ORDER_CANCEL_TYPE_ENGINEER) {
                //如果取消人不是工程师取消发送短信
                try {
                    SmsSendUtil.sendSmsToEngineerForCancel(o.getEngineerMobile(), o.getOrderNo());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (cancelType != OrderConstant.ORDER_CANCEL_TYPE_CUSTOMER) {
            //如果取消人不是客户取消发送短信
            try {
                SmsSendUtil.sendSmsToCustomerForCancel(o.getMobile(), o.getOrderNo());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //通知退款
        if (o.getCancelStatus() >= OrderConstant.ORDER_STATUS_DEPOSITED
                || o.getPayStatus() == OrderConstant.ORDER_PAY_STATUS_SUBMITED) {
            //如果订单取消前已支付保证金，通知商店管理员退款
            try {
                orderPayService.payRefund(o.getId(), cancelType, su);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 取消订单
     *
     * @param orderNo
     * @param su
     * @author: lijx
     * @CreateDate: 2016-9-7 下午10:41:04
     */
    public void orderComment(String orderNo, SessionUser su) {

    }

    /**
     * 已Excel形式导出列表数据
     *
     * @param params
     */
    @SuppressWarnings("rawtypes")
    public void expDataExcel(Map<String, Object> params) {
        String templateFileName = params.get("tempFileName") + "";
        String destFileName = params.get("outFileName") + "";

        //获取登录用户
        SessionUser su = (SessionUser) params.get("user");

        //获取查询条件
        String orderNo = MapUtils.getString(params, "query_orderNo");
        String customerMobile = MapUtils.getString(params, "query_customerMobile");
        String queryStartTime = MapUtils.getString(params, "query_startTime");
        String queryEndTime = MapUtils.getString(params, "query_endTime");
        String model = MapUtils.getString(params, "query_model");
        String brand = MapUtils.getString(params, "query_brand");
        String engNumber = MapUtils.getString(params, "query_engNumber");
        String engName = MapUtils.getString(params, "query_engName");
        String orderState = MapUtils.getString(params, "query_orderState");
        String balanceStatus = MapUtils.getString(params, "query_balanceStatus");
        String shopName = MapUtils.getString(params, "query_shopName");
        String shopCode = MapUtils.getString(params, "query_shopCode");
        String shopManagerMobile = MapUtils.getString(params, "query_shopManagerMobile");
        String province = MapUtils.getString(params, "queryProvince");
        String city = MapUtils.getString(params, "queryCity");
        String county = MapUtils.getString(params, "queryCounty");
        String queryStartRepairTime = MapUtils.getString(params, "query_startRepairTime");//完成时间
        String queryEndRepairTime = MapUtils.getString(params, "query_endRepairTime");

        String balanceNo = MapUtils.getString(params, "balanceNo");
        String repairType = MapUtils.getString(params, "query_repairType");
        String isPatch = MapUtils.getString(params, "isPatch");

        Order o = new Order();
        o.setOrderNo(orderNo);
        o.setMobile(customerMobile);
        o.setQueryStartTime(queryStartTime);
        o.setQueryEndTime(queryEndTime);
        o.setModelId(model);
        o.setBrandId(brand);
        o.setEngineerNumber(engNumber);
        if (StringUtils.isNotBlank(isPatch)) {
            o.setIsPatch(Integer.valueOf(isPatch));
        }
        if (StringUtils.isNotBlank(balanceNo)) {
            o.setBalanceNo(balanceNo);
        }
        if (StringUtils.isNotBlank(repairType)) {
            o.setRepairType(Integer.parseInt(repairType));
        }
        if (StringUtils.isNotBlank(engName)) {
            o.setEngineerName(engName);
        }
        if (StringUtils.isNotBlank(orderState)) {
            o.setOrderStatus(Integer.parseInt(orderState));
        }
        if (StringUtils.isNotBlank(balanceStatus)) {
            o.setBalanceStatus(Integer.parseInt(balanceStatus));
        }
        if (StringUtils.isNotBlank(queryStartRepairTime)) {
            o.setQueryRepairStartTime(queryStartRepairTime);
        }
        if (StringUtils.isNotBlank(queryEndRepairTime)) {
            o.setQueryRepairEndTime(queryEndRepairTime);
        }
        o.setShopName(shopName);
        o.setShopCode(shopCode);
        o.setShopManagerMobile(shopManagerMobile);
        o.setProvince(province);
        o.setCity(city);
        o.setCounty(county);

        //判断用户类型系统管理员可以查看所有订单
        if (su.getType() == SystemConstant.USER_TYPE_SYSTEM || su.getType() == SystemConstant.USER_TYPE_CUSTOMER_SERVICE) {

        } else if (su.getType() == SystemConstant.USER_TYPE_PROVIDER) {
            //连锁商只能查看自己的订单
            o.setProviderCode(su.getProviderCode());
        } else if (su.getType() == SystemConstant.USER_TYPE_SHOP) {
            //门店商只能查看自己的订单
            o.setShopCode(su.getShopCode());
        }

        String idStr = MapUtils.getString(params, "ids");
        if (StringUtils.isNotBlank(idStr)) {
            String[] ids = StringUtils.split(idStr, ",");
            o.setQueryIds(Arrays.asList(ids));
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        List<Order> project = queryList(o);
        Iterator<Order> it = project.iterator();
        while (it.hasNext()) {
            Order order = it.next();
            //查询订单明细
            List<OrderDetail> details = detailService.queryByOrderNo(order.getOrderNo());
            if (order.getEndTime() != null) {
                order.setStrEndTime(sdf.format(order.getEndTime()));
            }

            //对每条订单显示对应可接单的工程师列表
            List<Engineer> englist = engineerService.queryByShopCode(order.getShopCode());
            if (englist.size() > 0) {
                order.setEngineerList(englist);
            }

            List<String> projectNames = new ArrayList<>();
            Boolean isTrue = false;//判断是否有工程师确认的维修项目
            for (OrderDetail detail : details) {
                if (detail.getType() == 1) {
                    isTrue = true;
                    break;
                }
            }
            for (OrderDetail detail : details) {
                if (isTrue) {
                    if (detail.getType() == 1) {//工程师确认的维修项目
                        projectNames.add(detail.getProjectName());
                    }
                } else {
                    if (detail.getType() == 0) {//客户确认的维修项目
                        projectNames.add(detail.getProjectName());
                    }
                }
            }
            String a = StringUtils.join(projectNames, ",");
            order.setProjectName(a);
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("orderList", project);

        XLSTransformer transformer = new XLSTransformer();
        try {
            transformer.transformXLS(templateFileName, map, destFileName);
        } catch (ParsePropertyException e) {
            log.error("文件导出--ParsePropertyException", e);
        } catch (InvalidFormatException e) {
            log.error("文件导出--InvalidFormatException", e);
        } catch (IOException e) {
            log.error("文件导出--IOException", e);
        }
    }

    /**
     * 已Excel形式导出列表数据
     *
     * @param params
     */
    @SuppressWarnings("rawtypes")
    public void expDataExcelByEng(Map<String, Object> params) {
        String templateFileName = params.get("tempFileName") + "";
        String destFileName = params.get("outFileName") + "";

        //获取登录用户
        SessionUser su = (SessionUser) params.get("user");

        //获取查询条件
        String queryStartRepairTime = MapUtils.getString(params, "query_startRepairTime");//完成时间
        String queryEndRepairTime = MapUtils.getString(params, "query_endRepairTime");
        String engNumber = MapUtils.getString(params, "query_engNumber");
        String isRework = MapUtils.getString(params, "isRework");
        //维修方式
        String repairType = MapUtils.getString(params, "query_repairType");
        String isPatch = MapUtils.getString(params, "isPatch");//是否去掉贴膜优惠券

        Order o = new Order();
        o.setEngineerNumber(engNumber);
        if (StringUtils.isNotBlank(isPatch)) {
            o.setIsPatch(Integer.valueOf(isPatch));
        }
        if (StringUtils.isNotBlank(repairType)) {
            o.setRepairType(Integer.parseInt(repairType));
        }
        if (StringUtils.isNotBlank(queryStartRepairTime)) {
            o.setQueryRepairStartTime(queryStartRepairTime);
        }
        if (StringUtils.isNotBlank(queryEndRepairTime)) {
            o.setQueryRepairEndTime(queryEndRepairTime);
        }
        //判断用户类型系统管理员可以查看所有订单
        if (su.getType() == SystemConstant.USER_TYPE_SYSTEM || su.getType() == SystemConstant.USER_TYPE_CUSTOMER_SERVICE) {

        } else if (su.getType() == SystemConstant.USER_TYPE_PROVIDER) {
            //连锁商只能查看自己的订单
            o.setProviderCode(su.getProviderCode());
        } else if (su.getType() == SystemConstant.USER_TYPE_SHOP) {
            //门店商只能查看自己的订单
            o.setShopCode(su.getShopCode());
        }

        String idStr = MapUtils.getString(params, "ids");
        if (StringUtils.isNotBlank(idStr)) {
            String[] ids = StringUtils.split(idStr, ",");
            o.setQueryIds(Arrays.asList(ids));
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        List<Order> list = new ArrayList<Order>();
        if (StringUtils.isBlank(isRework)) {
            list = getDao().queryEngineerList(o);
        } else if (Integer.valueOf(isRework) == 0) {
            list = getDao().queryList(o);
        } else if (Integer.valueOf(isRework) == 1) {
            list = getDao().queryReworkList(o);
        }
        Iterator<Order> it = list.iterator();
        while (it.hasNext()) {
            Order order = it.next();
            //查询订单明细
            List<OrderDetail> details = new ArrayList<OrderDetail>();
            if (!order.getOrderNo().contains("PO")) {
                ReworkOrder reworkOrder = reworkOrderService.getDao().queryByReworkNo(order.getOrderNo());
                details = detailService.queryByOrderNo(reworkOrder.getParentOrder());
            } else {
                details = detailService.queryByOrderNo(order.getOrderNo());
            }
            switch (order.getRepairType()) {
                case 0:
                    order.setRepairStrType("上门维修");
                    break;
                case 1:
                    order.setRepairStrType("到店维修");
                    break;
                case 2:
                    order.setRepairStrType("返店维修");
                    break;
                case 3:
                    order.setRepairStrType("寄修");
                    break;
                case 4:
                    order.setRepairStrType("点对点二维码");
                    break;
            }
            if (order.getEndTime() != null) {
                order.setStrEndTime(sdf.format(order.getEndTime()));
            }

            List<String> projectNames = new ArrayList<>();
            Boolean isTrue = false;//判断是否有工程师确认的维修项目
            for (OrderDetail detail : details) {
                if (detail.getType() == 1) {
                    isTrue = true;
                    break;
                }
            }
            for (OrderDetail detail : details) {
                if (isTrue) {
                    if (detail.getType() == 1) {//工程师确认的维修项目
                        projectNames.add(detail.getProjectName());
                    }
                } else {
                    if (detail.getType() == 0) {//客户确认的维修项目
                        projectNames.add(detail.getProjectName());
                    }
                }
            }
            String a = StringUtils.join(projectNames, ",");
            order.setProjectName(a);
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("orderList", list);

        XLSTransformer transformer = new XLSTransformer();
        try {
            transformer.transformXLS(templateFileName, map, destFileName);
        } catch (ParsePropertyException e) {
            log.error("文件导出--ParsePropertyException", e);
        } catch (InvalidFormatException e) {
            log.error("文件导出--InvalidFormatException", e);
        } catch (IOException e) {
            log.error("文件导出--IOException", e);
        }
    }

    /**
     * 已Excel形式导出列表数据
     *
     * @param params
     */
    @SuppressWarnings("rawtypes")
    public void expUncheckedDataExcel(Map<String, Object> params) {
        String templateFileName = params.get("tempFileName") + "";
        String destFileName = params.get("outFileName") + "";

        //获取登录用户
        SessionUser su = (SessionUser) params.get("user");

        //获取查询条件
        String orderNo = MapUtils.getString(params, "query_orderNo");
        String customerMobile = MapUtils.getString(params, "query_customerMobile");
        String queryStartTime = MapUtils.getString(params, "query_startTime");
        String queryEndTime = MapUtils.getString(params, "query_endTime");
        String orderState = MapUtils.getString(params, "query_orderState");
        Order o = new Order();
        o.setOrderNo(orderNo);
        o.setMobile(customerMobile);
        o.setQueryStartTime(queryStartTime);
        o.setQueryEndTime(queryEndTime);

        if (StringUtils.isNotBlank(orderState)) {
            o.setOrderStatus(Integer.parseInt(orderState));
        }

        //判断用户类型系统管理员可以查看所有订单
        if (su.getType() == SystemConstant.USER_TYPE_SYSTEM || su.getType() == SystemConstant.USER_TYPE_CUSTOMER_SERVICE) {

        } else if (su.getType() == SystemConstant.USER_TYPE_PROVIDER) {
            //连锁商只能查看自己的订单
            o.setProviderCode(su.getProviderCode());
        } else if (su.getType() == SystemConstant.USER_TYPE_SHOP) {
            //门店商只能查看自己的订单
            o.setShopCode(su.getShopCode());
        } else {
            throw new SystemException("对不起，您无权查看此信息！");
        }

        String idStr = MapUtils.getString(params, "ids");
        if (StringUtils.isNotBlank(idStr)) {
            String[] ids = StringUtils.split(idStr, ",");
            o.setQueryIds(Arrays.asList(ids));
        }

        List<Map<String, Object>> list = getDao().queryListMap(o);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("orderList", list);

        XLSTransformer transformer = new XLSTransformer();
        try {
            transformer.transformXLS(templateFileName, map, destFileName);
        } catch (ParsePropertyException e) {
            log.error("文件导出--ParsePropertyException", e);
        } catch (InvalidFormatException e) {
            log.error("文件导出--InvalidFormatException", e);
        } catch (IOException e) {
            log.error("文件导出--IOException", e);
        }
    }

    /**
     * 查询待结算的订单
     *
     * @param startTime
     * @param endTime
     * @param providerCode
     * @return
     * @author: lijx
     * @CreateDate: 2016-10-20 下午7:37:14
     */
    public List<Order> queryOrderForBalance(String startTime, String endTime, String providerCode) {
        Order t = new Order();
        t.setQueryStartTime(startTime);
        t.setQueryEndTime(endTime);
        t.setProviderCode(providerCode);
        return getDao().queryOrderForBalance(t);
    }

    /**
     * 得到指定日期内各连锁商未结算订单信息
     */
    public List<Map<String, Object>> getSummaryOrderListForPage(Order order) {
        return mapper.getSummaryOrderListForPage(order);
    }

    /**
     * 得到指定日期内各连锁商结算总金额
     */
    public Map<String, Object> queryBalanceAmount(Order order) {
        return mapper.queryBalanceAmount(order);
    }

    /**
     * 更改订单的结算状态信息
     *
     * @param order
     * @return
     */
    public int updateBalanceStatus(Order order) {
        return mapper.updateBalanceStatus(order);
    }

    /**
     * 清楚balance状态
     *
     * @param balanceNo
     * @return
     * @author: lijx
     * @CreateDate: 2016-10-26 下午10:47:16
     */
    public int deleteBalanceStatus(String balanceNo) {
        Order t = new Order();
        t.setBalanceNo(balanceNo);
        return getDao().deleteBalanceStatus(t);
    }

    /**
     * 已Excel形式导出列表数据
     *
     * @param params
     */
    @SuppressWarnings("rawtypes")
    public void expSummaryOrderDataExcel(Map<String, Object> params) {
        String templateFileName = params.get("tempFileName") + "";
        String destFileName = params.get("outFileName") + "";

        //获取登录用户
        SessionUser su = (SessionUser) params.get("user");

        //获取查询条件
        String queryStartTime = MapUtils.getString(params, "query_startTime");
        String queryEndTime = MapUtils.getString(params, "query_endTime");
        Order o = new Order();
        o.setQueryStartTime(queryStartTime);
        o.setQueryEndTime(queryEndTime);

        //使用分页查询
        Page page = new Page();
        page.setPageSize(1000000);
        page.setStart(0);
        o.setPage(page);

        //判断用户类型系统管理员可以查看所有订单
        if (su.getType() == SystemConstant.USER_TYPE_SYSTEM || su.getType() == SystemConstant.USER_TYPE_CUSTOMER_SERVICE) {

        } else if (su.getType() == SystemConstant.USER_TYPE_PROVIDER) {
            //连锁商只能查看自己的订单
            o.setProviderCode(su.getProviderCode());
        } else if (su.getType() == SystemConstant.USER_TYPE_SHOP) {
            //门店商只能查看自己的订单
            o.setShopCode(su.getShopCode());
        } else {
            throw new SystemException("对不起，您无权查看此信息！");
        }

        List<Map<String, Object>> list = getDao().getSummaryOrderListForPage(o);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("list", list);
        map.putAll(mapper.queryBalanceAmount(o));
        map.put("queryStartTime", queryStartTime);
        map.put("queryEndTime", queryEndTime);
        XLSTransformer transformer = new XLSTransformer();
        try {
            transformer.transformXLS(templateFileName, map, destFileName);
        } catch (ParsePropertyException e) {
            log.error("文件导出--ParsePropertyException", e);
        } catch (InvalidFormatException e) {
            log.error("文件导出--InvalidFormatException", e);
        } catch (IOException e) {
            log.error("文件导出--IOException", e);
        }
    }


    /**
     * 用于通过订单状态进行筛选满足条件的订单状态对应名称列表
     * 订单状态:
     * 2  待派单           3  待门店收件      5  门店已收件     11 待预约
     * 12 已预约           20 定位故障         30 待用户付款
     * 35 正在维修        40 待客户收件     50 已完成             60 已取消
     */
    public Map<Integer, Object> getSelectOrderStatus() {
        Map<Integer, Object> m = new HashMap();
        m.put(OrderConstant.ORDER_STATUS_DEPOSITED, "待派单");
        m.put(OrderConstant.ORDER_STATUS_WAIT_SHOP_SEND_RECEIVE, "待门店收件");
        m.put(OrderConstant.ORDER_STATUS_SHOP_ALERADY_RECEIVE, "门店已收件");
        m.put(OrderConstant.ORDER_STATUS_DISPATCHED, "待预约");
        m.put(OrderConstant.ORDER_STATUS_RECEIVED, "已预约");
        m.put(OrderConstant.ORDER_STATUS_START_CHECK, "定位故障");
        m.put(OrderConstant.ORDER_STATUS_END_REPAIR, "待用户付款");
        m.put(OrderConstant.ORDER_STATUS_REPAIRING, "正在维修");
        m.put(OrderConstant.ORDER_STATUS_WAIT_CUSTOMER_RECEIVE, "待客户收件");
        m.put(OrderConstant.ORDER_STATUS_FINISHED, "已完成");
        m.put(OrderConstant.ORDER_STATUS_CANCEL, "已取消");
        return m;
    }

}