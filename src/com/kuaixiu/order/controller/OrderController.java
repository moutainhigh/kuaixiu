package com.kuaixiu.order.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.common.exception.ApiServiceException;
import com.kuaixiu.engineer.service.NewEngineerService;
import com.kuaixiu.order.entity.*;
import com.kuaixiu.order.service.*;
import com.system.api.entity.ResultData;
import com.kuaixiu.engineer.entity.EngineerSignIn;
import com.kuaixiu.engineer.service.EngineerSignInService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.common.base.controller.BaseController;
import com.common.echarts.Option;
import com.common.exception.SystemException;
import com.common.paginate.Page;
import com.common.util.SmsSendUtil;
import com.google.common.collect.Maps;
import com.kuaixiu.brand.entity.Brand;
import com.kuaixiu.brand.service.BrandService;
import com.kuaixiu.coupon.entity.Coupon;
import com.kuaixiu.coupon.entity.CouponModel;
import com.kuaixiu.coupon.entity.CouponProject;
import com.kuaixiu.coupon.service.CouponModelService;
import com.kuaixiu.coupon.service.CouponProjectService;
import com.kuaixiu.coupon.service.CouponService;
import com.kuaixiu.customer.entity.Customer;
import com.kuaixiu.engineer.entity.Engineer;
import com.kuaixiu.engineer.service.EngineerService;
import com.kuaixiu.model.entity.Model;
import com.kuaixiu.model.service.ModelService;
import com.kuaixiu.order.constant.OrderConstant;
import com.kuaixiu.project.entity.CancelReason;
import com.kuaixiu.project.service.CancelReasonService;
import com.kuaixiu.shop.entity.Shop;
import com.kuaixiu.shop.service.ShopService;
import com.system.basic.address.entity.Address;
import com.system.basic.address.service.AddressService;
import com.system.basic.user.entity.SessionUser;
import com.system.basic.user.service.SessionUserService;
import com.system.basic.user.service.SysUserService;
import com.system.constant.SystemConstant;

import jodd.util.StringUtil;

/**
 * Order Controller
 *
 * @CreateDate: 2016-08-26 下午10:44:08
 * @version: V 1.0
 */
@Controller
public class OrderController extends BaseController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private OrderDetailService detailService;
    @Autowired
    private EngineerService engineerService;
    @Autowired
    private ShopService shopService;
    @Autowired
    private OrderPayService orderPayService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private ModelService modelService;
    @Autowired
    private SessionUserService sessionUserService;
    @Autowired
    private CouponService couponService;
    @Autowired
    private CouponModelService couponModelService;
    @Autowired
    private CouponProjectService couponProjectService;
    @Autowired
    private CancelReasonService cancelReasonService;
    @Autowired
    private OrderCommentService commentService;
    @Autowired
    private UpdateOrderPriceService updateOrderPriceService;
    @Autowired
    private NewEngineerService newEngineerService;
    @Autowired
    private FromSystemService fromSystemService;

    /**
     * 列表查询 -- 后台管理员
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/order/list")
    public ModelAndView list(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {
        //获取登录用户
        SessionUser su = getCurrentUser(request);
        //订单状态筛选列表
        Map<Integer, Object> m = orderService.getSelectOrderStatus();
        request.setAttribute("selectOrderStatus", m);
        String returnView = "";
        //判断用户类型系统管理员可以查看所有工程师
        if (su.getType() == SystemConstant.USER_TYPE_SYSTEM || su.getType() == SystemConstant.USER_TYPE_CUSTOMER_SERVICE) {
            //获取省份地址
            FromSystem fromSystem=new FromSystem();
            fromSystem.setIsDel(0);
            List<FromSystem> fromSystems = fromSystemService.queryList(fromSystem);
            request.setAttribute("fromSystems", fromSystems);
            List<Address> provinceL = addressService.queryByPid("0");
            request.setAttribute("provinceL", provinceL);
            List<Brand> brands = brandService.queryList(null);
            request.setAttribute("brands", brands);
            returnView = "order/listForAdmin";
        } else if (su.getType() == SystemConstant.USER_TYPE_PROVIDER) {
            //连锁商只能查看自己的订单
            returnView = "order/listForProvider";
        } else if (su.getType() == SystemConstant.USER_TYPE_SHOP) {
            //门店商只能查看自己的订单
            returnView = "order/listForShop";
        } else {
            throw new SystemException("对不起，您无权查看此信息！");
        }
        return new ModelAndView(returnView);
    }


    /**
     * 寄修调控列表
     */
    @RequestMapping(value = "/sendOrder/list")
    public ModelAndView sendOrder(HttpServletRequest request,
                                  HttpServletResponse response) throws Exception {
        //获取登录用户
        SessionUser su = getCurrentUser(request);
        String returnView = null;
        //判断用户类型系统管理员可以查看所有订单
        if (su.getType() == SystemConstant.USER_TYPE_SYSTEM || su.getType() == SystemConstant.USER_TYPE_CUSTOMER_SERVICE) {
            returnView = "order/adminSendControl";
        } else if (su.getType() == SystemConstant.USER_TYPE_SHOP) {
            //门店商只能查看自己的订单
            Shop s = shopService.queryByCode(su.getShopCode());
            List<Engineer> engList = engineerService.queryListUnDispatchByShopCode(su.getShopCode());
            request.setAttribute("shop", s);
            request.setAttribute("engList", engList);
            returnView = "order/shopSendControl";
        } else {
            throw new SystemException("对不起，您无权查看此信息！");
        }
        return new ModelAndView(returnView);
    }


    /**
     * 未派单列表查询 -- 门店商管理员
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/order/unDispatchList")
    public ModelAndView unDispatchList(HttpServletRequest request,
                                       HttpServletResponse response) throws Exception {
        //获取登录用户
        SessionUser su = getCurrentUser(request);

        Shop s = shopService.queryByCode(su.getShopCode());
        List<Engineer> engList = engineerService.queryListUnDispatchByShopCode(su.getShopCode());
        request.setAttribute("shop", s);
        request.setAttribute("engList", engList);

        //门店商只能查看自己的订单
        String returnView = "order/unDispatchForShop";

        return new ModelAndView(returnView);
    }

    /**
     * 未派单列表查询 -- 后台管理员
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/order/unDispatchForAdmin")
    public ModelAndView unDispatchForAdmin(HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        //获取登录用户
        SessionUser su = getCurrentUser(request);

        String returnView = "order/unDispatchForAdmin";
        return new ModelAndView(returnView);
    }

    /**
     * 未检修列表查询 -- 后台管理员
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/order/unCheckedList")
    public ModelAndView unCheckedList(HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {
        //获取登录用户
        SessionUser su = getCurrentUser(request);

        String returnView = "order/unCheckedList";
        return new ModelAndView(returnView);
    }

    /**
     * queryListForPage
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/order/queryListForPage")
    public void queryListForPage(HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        //获取查询条件
        String orderNo = request.getParameter("query_orderNo");
        String customerMobile = request.getParameter("query_customerMobile");
        String queryStartTime = request.getParameter("query_startTime");
        String queryEndTime = request.getParameter("query_endTime");
        String queryStartRepairTime = request.getParameter("query_startRepairTime");//完成时间
        String queryEndRepairTime = request.getParameter("query_endRepairTime");
        String model = request.getParameter("query_model");
        String brand = request.getParameter("query_brand");
        String engNumber = request.getParameter("query_engNumber");
        String engName = request.getParameter("query_engName");
        String orderState = request.getParameter("query_orderState");
        String orderStates = request.getParameter("query_orderStates");
        String balanceStatus = request.getParameter("query_balanceStatus");
        String shopName = request.getParameter("query_shopName");
        String shopCode = request.getParameter("query_shopCode");
        String shopManagerMobile = request.getParameter("query_shopManagerMobile");
        String province = request.getParameter("queryProvince");
        String city = request.getParameter("queryCity");
        String county = request.getParameter("queryCounty");
        String balanceNo = request.getParameter("balanceNo");
        String fromSystem = request.getParameter("fromSystem");
        //维修方式
        String repairType = request.getParameter("query_repairType");
        String isPatch = request.getParameter("isPatch");//是否去掉贴膜优惠券
        Order o = new Order();
        if (StringUtils.isNotBlank(fromSystem)) {
            o.setFromSystem(fromSystem);
        }
        if (StringUtils.isNotBlank(isPatch)) {
            o.setIsPatch(Integer.valueOf(isPatch));
        }
        if (StringUtils.isNotBlank(orderNo)) {
            o.setOrderNo(orderNo);
        }
        if (StringUtils.isNotBlank(customerMobile)) {
            o.setMobile(customerMobile);
        }
        if (StringUtils.isNotBlank(queryStartTime)) {
            o.setQueryStartTime(queryStartTime);
        }
        if (StringUtils.isNotBlank(queryEndTime)) {
            o.setQueryEndTime(queryEndTime);
        }
        if (StringUtils.isNotBlank(queryStartRepairTime)) {
            o.setQueryRepairStartTime(queryStartRepairTime);
        }
        if (StringUtils.isNotBlank(queryEndRepairTime)) {
            o.setQueryRepairEndTime(queryEndRepairTime);
        }
        if (StringUtils.isNotBlank(model)) {
            o.setModelId(model);
        }
        if (StringUtils.isNotBlank(brand)) {
            o.setBrandId(brand);
        }
        if (StringUtils.isNotBlank(engNumber)) {
            o.setEngineerNumber(engNumber);
        }
        if (StringUtils.isNotBlank(engName)) {
            o.setEngineerName(engName);
        }
        if (StringUtils.isNotBlank(orderState)) {
            o.setOrderStatus(Integer.parseInt(orderState));
        }
        if (StringUtils.isNotBlank(orderStates)) {
            o.setQueryStatusArray(Arrays.asList(StringUtils.split(orderStates, ",")));
        }
        if (StringUtils.isNotBlank(balanceStatus)) {
            o.setBalanceStatus(Integer.parseInt(balanceStatus));
        }
        if (StringUtils.isNotBlank(repairType)) {
            o.setRepairType(Integer.parseInt(repairType));
        }
        if (StringUtils.isNotBlank(shopName)) {
            o.setShopName(shopName);
        }
        if (StringUtils.isNotBlank(shopCode)) {
            o.setShopCode(shopCode);
        }
        if (StringUtils.isNotBlank(shopManagerMobile)) {
            o.setShopManagerMobile(shopManagerMobile);
        }
        if (StringUtils.isNotBlank(province)) {
            o.setProvince(province);
        }
        if (StringUtils.isNotBlank(city)) {
            o.setCity(city);
        }
        if (StringUtils.isNotBlank(county)) {
            o.setCounty(county);
        }
        if (StringUtils.isNotBlank(balanceNo)) {
            o.setBalanceNo(balanceNo);
        }
        //获取登录用户
        SessionUser su = getCurrentUser(request);
        boolean tip = false;
        //判断用户类型系统管理员可以查看所有订单
        if (su.getType() == SystemConstant.USER_TYPE_SYSTEM || su.getType() == SystemConstant.USER_TYPE_CUSTOMER_SERVICE) {
            tip = true;
        } else if (su.getType() == SystemConstant.USER_TYPE_PROVIDER) {
            //连锁商只能查看自己的订单
            o.setProviderCode(su.getProviderCode());
        } else if (su.getType() == SystemConstant.USER_TYPE_SHOP) {
            //门店商只能查看自己的订单
            o.setShopCode(su.getShopCode());
        } else {
            throw new SystemException("对不起，您无权查看此信息！");
        }

        Page page = getPageByRequest(request);
        o.setPage(page);
        List<Order> list = orderService.queryListForPage(o);
        if (tip) {
            Iterator<Order> it = list.iterator();
            while (it.hasNext()) {
                Order order = it.next();
                //对每条订单显示对应可接单的工程师列表
                List<Engineer> englist = engineerService.queryByShopCode(order.getShopCode());
                if (englist.size() > 0) {
                    order.setEngineerList(englist);
                }
                //查询订单明细
                List<OrderDetail> details = detailService.queryByOrderNo(order.getOrderNo());
//                if ("1".equals(isPatch)) {
//                    if (details.size() == 1) {
//                        if (details.get(0).getProjectName().contains("贴膜")) {
//                            it.remove();
//                            continue;
//                        }
//                    }
//                }
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
        }
        page.setData(list);
        this.renderJson(response, page);
    }


    /**
     * 修改订单价格
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "order/updateOrderPrice")
    @ResponseBody
    public ResultData updateOrderPrice(HttpServletRequest request, HttpServletResponse response) {
        ResultData resultData = new ResultData();
        try {
            SessionUser su = getCurrentUser(request);
            String id = request.getParameter("orderPriceId");//订单id
            String orderUpdatePrice = request.getParameter("orderUpdatePrice");//修改金额
            String updateReason = request.getParameter("updateReason");//修改原因
            if ("0".equals(orderUpdatePrice)) {
                return SystemException(false, "对不起，金额不能改为零元");
            }
            Order order = orderService.getDao().queryByIdFromUpdatePrice(id);
            if (order == null) {
                return SystemException(false, "订单不存在");
            }
            if (order.getOrderStatus() == OrderConstant.ORDER_STATUS_CANCEL) {
                return SystemException(false, "该订单已取消");
            }
            if (order.getOrderStatus() == OrderConstant.ORDER_STATUS_FINISHED) {
                return SystemException(false, "该订单已完成");
            }
            if (order.getOrderStatus() < OrderConstant.ORDER_STATUS_DISPATCHED) {
                return SystemException(false, "订单未派单");
            }
            //保存修改信息
            UpdateOrderPrice orderPrice = new UpdateOrderPrice();
            orderPrice.setUpBeginPrice(order.getRealPrice());//保存修改前金额
            orderPrice.setId(UUID.randomUUID().toString().replace("-", ""));
            orderPrice.setUpNumberNo(su.getUserId());
            orderPrice.setUpOrderNo(order.getOrderNo());
            orderPrice.setUpEndPrice(new BigDecimal(orderUpdatePrice));
            orderPrice.setUpReason(updateReason);
            orderPrice.setCreateUserid(su.getUserId());
            updateOrderPriceService.add(orderPrice);

            order.setIsUpdatePrice(1);
            order.setRealPrice(new BigDecimal(orderUpdatePrice));
            orderService.saveUpdate(order);

            resultData.setResultMessage("修改成功");
            resultData.setSuccess(true);
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, resultData);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(resultData);
        }
        return resultData;
    }

    private ResultData SystemException(Boolean isTrue, String msg) {
        ResultData resultData = new ResultData();
        resultData.setSuccess(isTrue);
        resultData.setResultMessage(msg);
        return resultData;
    }


    /**
     * queryListForPage
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/order/queryListMapForPage")
    public void queryListMapForPage(HttpServletRequest request,
                                    HttpServletResponse response) throws Exception {
        ResultData resultData = new ResultData();
        Page page = new Page();
        try {
            //获取查询条件
            String orderNo = request.getParameter("query_orderNo");
            String customerMobile = request.getParameter("query_customerMobile");
            String queryStartTime = request.getParameter("query_startTime");
            String queryEndTime = request.getParameter("query_endTime");
            String model = request.getParameter("query_model");
            String brand = request.getParameter("query_brand");
            String engNumber = request.getParameter("query_engNumber");
            String orderStates = request.getParameter("query_orderStates");
            String isDispatch = request.getParameter("query_isDispatch");
            String shopName = request.getParameter("query_shopName");
            String shopCode = request.getParameter("query_shopCode");
            String shopManagerMobile = request.getParameter("query_shopManagerMobile");
            String province = request.getParameter("queryProvince");
            String city = request.getParameter("queryCity");
            String county = request.getParameter("queryCounty");
            String repairType = request.getParameter("query_repairType");
            String orderStatus = request.getParameter("query_orderStatus");
            Order o = new Order();
            o.setOrderNo(orderNo);
            o.setMobile(customerMobile);
            o.setQueryStartTime(queryStartTime);
            o.setQueryEndTime(queryEndTime);
            o.setModelId(model);
            o.setBrandId(brand);
            o.setEngineerNumber(engNumber);
            if (StringUtils.isNotBlank(repairType)) {
                o.setRepairType(Integer.parseInt(repairType));
            }
            if (StringUtils.isNotBlank(orderStatus)) {
                o.setOrderStatus(Integer.parseInt(orderStatus));
            }
            if (StringUtils.isNotBlank(orderStates)) {
                o.setQueryStatusArray(Arrays.asList(StringUtils.split(orderStates, ",")));
            }
            if (StringUtils.isNotBlank(isDispatch)) {
                o.setIsDispatch(Integer.parseInt(isDispatch));
            }
            o.setShopName(shopName);
            o.setShopCode(shopCode);
            o.setShopManagerMobile(shopManagerMobile);
            o.setProvince(province);
            o.setCity(city);
            o.setCounty(county);

            //获取登录用户
            SessionUser su = getCurrentUser(request);
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

            page = getPageByRequest(request);
            o.setPage(page);
            o.setIsDel(0);
            List<Map<String, Object>> list = orderService.queryMapForPage(o);
            page.setData(list);
            resultData.setResult(page);
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, resultData);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(resultData);
        }
        this.renderJson(response, page);
    }


    /**
     * 重新派单
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "order/againOrder")
    @ResponseBody
    public void againOrder(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        Page page = getPageByRequest(request);
        try {
            String engName = request.getParameter("engName");
            String engCode = request.getParameter("engCode");
            if (StringUtils.isBlank(engCode) && StringUtils.isBlank(engName)) {
                throw new SystemException("参数不完整");
            }
            Engineer eng = new Engineer();
            eng.setName(engName);
            eng.setNumber(engCode);

            eng.setPage(page);
            List<Engineer> engineers = newEngineerService.getDao().queryAgainOrderForPage(eng);
            if (!CollectionUtils.isEmpty(engineers)) {
                Iterator<Engineer> it = engineers.iterator();
                while (it.hasNext()) {
                    Engineer isEng = it.next();
                    if (StringUtils.isBlank(isEng.getShopCode()) || StringUtils.isBlank(isEng.getProviderCode())) {
                        it.remove();
                    } else {
                        //如果是多个门店，就遍历查找名称
                        newEngineerService.engineerShopCode(isEng);
                        //查询未完成订单数
                        Order order = new Order();
                        order.setEngineerId(isEng.getId());
                        order.setQueryStatusArray(Arrays.asList(StringUtils.split("11,12,20,30", ",")));
                        Integer orderNumber = orderService.orderMapCount(order);
                        isEng.setOrderDayNum(orderNumber);
                    }
                }
            }
            page.setData(engineers);
            result.setResult(page);
            result.setSuccess(true);
            result.setResultMessage("成功");
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        this.renderJson(response, page);
    }


    /**
     * 添加订单
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/order/add")
    public ModelAndView add(HttpServletRequest request,
                            HttpServletResponse response) throws Exception {
        Brand b = new Brand();
        b.setIsDel(0);
        //查询品牌
        List<Brand> brands = brandService.queryList(null);
        if (brands != null && brands.size() > 0) {
            b = brands.get(0);
            Model m = new Model();
            m.setBrandId(b.getId());
            m.setIsDel(0);
            List<Model> models = modelService.queryList(m);
            request.setAttribute("models", models);
        }
        request.setAttribute("brands", brands);
        //获取省份地址
        List<Address> provinceL = addressService.queryByPid("0");
        request.setAttribute("provinceL", provinceL);
        String returnView = "order/addOrder";
        return new ModelAndView(returnView);
    }

    /**
     * 保存订单   后台快速下单 采用高德转换地址坐标
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/order/save")
    public void save(HttpServletRequest request,
                     HttpServletResponse response) throws Exception {
        Map<String, Object> resultMap = Maps.newHashMap();
        //获取品牌id
        String brandId = request.getParameter("brandId");
        //获取机型id
        String modelId = request.getParameter("modelId");
        //获取颜色id
        String color = request.getParameter("colorId");
        //获取选择故障项目，维修方式
        String projectIds = request.getParameter("projectIds");
        String repairType = request.getParameter("repairType");
        //获取用户姓名
        String username = request.getParameter("customerName");
        //获取用户手机号码
        String mobile = request.getParameter("customerMobile");
        //获取省份
        String province = request.getParameter("addProvince");
        //获取城市
        String city = request.getParameter("addCity");
        //获取县，镇信息
        String county = request.getParameter("addCounty");
        //获取街道
        String street = request.getParameter("addStreet");
        //省市信息
        String areas = request.getParameter("addAreas");
        //详细地址
        String address = request.getParameter("addAddress");
        //备注
        String note = request.getParameter("note");
        //优惠码
        String couponCode = request.getParameter("couponCode");
        //用户信息
        Customer cust = new Customer();
        cust.setName(username);
        cust.setMobile(mobile);
        cust.setProvince(province);
        cust.setCity(city);
        cust.setCounty(county);
        cust.setStreet(street);
        cust.setAreas(areas);
        cust.setAddress(address);
        //订单信息
        Order o = new Order();
        if (StringUtils.isNotBlank(repairType)) {
            o.setRepairType(Integer.parseInt(repairType));
        }
        o.setBrandId(brandId);
        o.setModelId(modelId);
        o.setColor(color);
        o.setIsMobile(1);
        o.setPostscript(note);
        o.setCouponCode(couponCode);
        o.setOrderStatus(OrderConstant.ORDER_STATUS_DEPOSITED);

        int result = 0;//用以判定是否需要从上门维修转至寄修
        if (o.getRepairType() == 3) {
            //如果是寄修
            Shop s = shopService.selectShop(province, city);
            if (s.getCode() != null) {
                //满足寄修条件的门店存在  保存订单
                orderService.sendSave(o, projectIds, cust, true);
                try {
                    //订单保存完成后派单给门店
                    o.setProviderCode(s.getProviderCode());
                    o.setProviderName(s.getProviderName());
                    o.setShopCode(s.getCode());
                    o.setShopName(s.getName());
                    o.setIsDispatch(2);
                    o.setDispatchTime(new Date());
                    o.setOrderStatus(OrderConstant.ORDER_STATUS_WAIT_SHOP_SEND_RECEIVE);
                    orderService.saveUpdate(o);
                    //寄修订单下单成功，向客户发送下单成功信息
                    SmsSendUtil.mailSendSmsToCustomer(cust.getMobile());
                    //向对应门店店主发送下单信息
                    SmsSendUtil.mailSendSmsToShop(s, o);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            //上门维修方式保存订单
            result = orderService.save(o, projectIds, cust, true);
            if (result == 10) {
                resultMap.put(RESULTMAP_KEY_MSG, "亲，您填写的地址附近没有维修门店。是否选择寄修？");
                resultMap.put("sendType", 1);
            } else {
                try {
                    //支付完成自动派单
                    orderService.autoDispatch(o);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (result == 10) {
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
        } else {
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
            resultMap.put(RESULTMAP_KEY_DATA, o.getId());
        }
        renderJson(response, resultMap);
    }

    @Autowired
    private EngineerSignInService engineerSignInService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/order/detail")
    public ModelAndView detail(HttpServletRequest request,
                               HttpServletResponse response) throws Exception {

        String id = request.getParameter("id");
        Order o = orderService.queryById(id);
        if (o == null) {
            throw new SystemException("订单未找到！！");
        }
        //查询订单明细
        List<OrderDetail> details = detailService.queryByOrderNo(o.getOrderNo());
        //取消原因标签列表
        List<CancelReason> reasonList = cancelReasonService.queryListForPage(new CancelReason());
        //用户评价信息
        OrderComment comm = commentService.queryByOrderNo(o.getOrderNo());
        //修改金额记录
        List<UpdateOrderPrice> upOrderPrice = updateOrderPriceService.getDao().queryByUpOrderNo(o.getOrderNo());
        UpdateOrderPrice updateOrderPrice = new UpdateOrderPrice();
        if (!CollectionUtils.isEmpty(upOrderPrice)) {
            updateOrderPrice = upOrderPrice.get(0);
            updateOrderPrice.setUpBeginPrice(upOrderPrice.get(upOrderPrice.size() - 1).getUpBeginPrice());
        }
        EngineerSignIn signIn = new EngineerSignIn();
        signIn.setOrderNo(o.getOrderNo());
        List<EngineerSignIn> signIns = engineerSignInService.getDao().queryList(signIn);
        if (!CollectionUtils.isEmpty(signIns)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            signIns.get(0).setStrCreateTime(sdf.format(signIns.get(0).getCreateTime()));
            request.setAttribute("engineerSignIn", signIns.get(0));
        }
        request.setAttribute("upOrderPrice", updateOrderPrice);
        request.setAttribute("comment", comm);
        request.setAttribute("reasonList", reasonList);
        request.setAttribute("order", o);
        request.setAttribute("details", details);
        String returnView = "order/detail";
        return new ModelAndView(returnView);
    }

    /**
     * 取消订单
     *
     * @param request
     * @param response
     * @throws IOException
     * @CreateDate: 2016-9-17 上午12:13:53
     */
    @RequestMapping("/order/orderCancel")
    public void orderCancel(HttpServletRequest request,
                            HttpServletResponse response) throws IOException {
        SessionUser su = getCurrentUser(request);
        //订单id
        String id = request.getParameter("id");
        String selectReason = request.getParameter("selectReason");
        String reason = request.getParameter("reason");
        String cancelReason = null;
        if (!StringUtil.isBlank(selectReason)) {
            cancelReason = selectReason;
        }
        if (!StringUtil.isBlank(reason) && !StringUtil.isBlank(selectReason)) {
            cancelReason = cancelReason + "；" + reason;
        }
        if (!StringUtil.isBlank(reason) && StringUtil.isBlank(selectReason)) {
            cancelReason = reason;
        }

        if (su.getType() == SystemConstant.USER_TYPE_SYSTEM) {
            orderService.orderCancel(id, OrderConstant.ORDER_CANCEL_TYPE_ADMIN, cancelReason, su);
        } else if (su.getType() == SystemConstant.USER_TYPE_CUSTOMER_SERVICE) {
            orderService.orderCancel(id, OrderConstant.ORDER_CANCEL_TYPE_CUSTOMER_SERVICE, cancelReason, su);
        }

        Map<String, Object> resultMap = Maps.newHashMap();
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
        renderJson(response, resultMap);
    }

    /**
     * 优惠券信息
     */
    @RequestMapping("/order/couponInfo")
    public void couponInfo(HttpServletRequest request
            , HttpServletResponse response) throws IOException {
        Map<String, Object> resultMap = Maps.newHashMap();
        //获取优惠码
        String couponCode = request.getParameter("couponCode");
        if (StringUtils.isBlank(couponCode)) {
            throw new SystemException("请填写优惠码");
        }
        Coupon c = couponService.queryByCode(couponCode);
        if (c == null) {
            throw new SystemException("优惠码不存在");
        }
        List<CouponModel> couModels = couponModelService.queryListByCouponId(c.getId());
        List<CouponProject> couProjects = couponProjectService.queryListByCouponId(c.getId());
        resultMap.put(RESULTMAP_KEY_DATA, c);
        resultMap.put("models", couModels);
        resultMap.put("projects", couProjects);
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
        renderJson(response, resultMap);
    }

    /**
     * dispatch
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/order/dispatch")
    public void dispatchToEngineer(HttpServletRequest request,
                                   HttpServletResponse response) throws Exception {

        Map<String, Object> resultMap = Maps.newHashMap();
        SessionUser su = getCurrentUser(request);

        //获取网点id
        String id = request.getParameter("id");
        String engId = request.getParameter("engId");
        if (StringUtils.isNotBlank(id) && StringUtils.isNotBlank(engId)) {
            orderService.dispatchToEngineer(id, engId, su);
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
            resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
        } else {
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
            resultMap.put(RESULTMAP_KEY_MSG, "参数为空");
        }

        renderJson(response, resultMap);
    }

    /**
     * 重新派单
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/order/reDispatch")
    public void reDispatch(HttpServletRequest request,
                           HttpServletResponse response) throws Exception {

        Map<String, Object> resultMap = Maps.newHashMap();
        SessionUser su = getCurrentUser(request);

        //获取网点id
        String orderId = request.getParameter("orderId");
        String engineerId = request.getParameter("engineerId");
        if (StringUtils.isNotBlank(orderId) && StringUtils.isNotBlank(engineerId)) {
            Engineer eng = newEngineerService.queryById(engineerId);
            if (eng == null) {
                throw new SystemException("该工程师不存在！");
            }
            //检查工程师状态
            if (eng.getIsDispatch() == 2) {
                throw new SystemException("派单失败，工程师：" + eng.getName() + ", 处于离线状态");
            }
            orderService.againOrder(orderId, su, eng);
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
            resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
        } else {
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
            resultMap.put(RESULTMAP_KEY_MSG, "参数为空");
        }

        renderJson(response, resultMap);
    }

    /**
     * 寄修订单门店确认收货
     */
    @RequestMapping(value = "/webpc/activity/affirm")
    public void alreadyReceive(HttpServletRequest request,
                               HttpServletResponse response) throws Exception {

        Map<String, Object> resultMap = Maps.newHashMap();
        SessionUser su = getCurrentUser(request);
        if (su.getType() != SystemConstant.USER_TYPE_SYSTEM && su.getType() != SystemConstant.USER_TYPE_CUSTOMER_SERVICE
                && su.getType() != SystemConstant.USER_TYPE_SHOP && su.getType() != SystemConstant.USER_TYPE_SUPPER) {
            throw new SystemException("对不起您没有权限");
        }
        //获取网点id
        String id = request.getParameter("id");
        if (StringUtils.isNotBlank(id)) {
            Order o = orderService.queryById(id);
            if (o == null) {
                throw new SystemException("订单不存在！");
            }
            o.setOrderStatus(OrderConstant.ORDER_STATUS_SHOP_ALERADY_RECEIVE);
            orderService.saveUpdate(o);
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
            resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
        } else {
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
            resultMap.put(RESULTMAP_KEY_MSG, "参数为空");
        }

        renderJson(response, resultMap);
    }


    /**
     * 重置故障
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/order/resetRepair")
    public void resetRepair(HttpServletRequest request,
                            HttpServletResponse response) throws Exception {

        Map<String, Object> resultMap = Maps.newHashMap();
        SessionUser su = getCurrentUser(request);

        //获取网点id
        String id = request.getParameter("id");
        if (StringUtils.isNotBlank(id)) {
            orderService.resetRepair(id, su);
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
            resultMap.put(RESULTMAP_KEY_MSG, "操作成功");
        } else {
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
            resultMap.put(RESULTMAP_KEY_MSG, "参数为空");
        }

        renderJson(response, resultMap);
    }

    /**
     * 退款
     *
     * @param request
     * @param response
     * @throws Exception
     * @author: lijx
     * @CreateDate: 2016-10-23 上午3:00:37
     */
    @RequestMapping(value = "/order/payRefund")
    public void payRefund(HttpServletRequest request,
                          HttpServletResponse response) throws Exception {

        Map<String, Object> resultMap = Maps.newHashMap();
        SessionUser su = getCurrentUser(request);

        //获取网点id
        String id = request.getParameter("id");
        if (StringUtils.isNotBlank(id)) {
            orderPayService.payRefund(id, 1, su);
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
            resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
        } else {
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
            resultMap.put(RESULTMAP_KEY_MSG, "参数为空");
        }

        renderJson(response, resultMap);
    }

    /**
     * 预约维修时间
     *
     * @param request
     * @param response
     * @throws Exception
     * @author: lijx
     * @CreateDate: 2016-10-23 上午3:00:37
     */
    @RequestMapping(value = "/order/agreedTime")
    public void agreedTime(HttpServletRequest request,
                           HttpServletResponse response) throws Exception {

        Map<String, Object> resultMap = Maps.newHashMap();
        SessionUser su = getCurrentUser(request);

        //获取网点id
        String id = request.getParameter("orderId");
        String agreedTime = request.getParameter("agreedTime");
        String agreedNote = request.getParameter("agreedNote");
        if (StringUtils.isNotBlank(id) && agreedNote.length() <= 250) {
            orderService.agreedTime(id, agreedTime, agreedNote, su);
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
            resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
        } else if (agreedNote.length() > 250) {
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
            resultMap.put(RESULTMAP_KEY_MSG, "备注仅限250字");
        } else {
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
            resultMap.put(RESULTMAP_KEY_MSG, "参数为空");
        }

        renderJson(response, resultMap);
    }


    /**
     * 统计工程师本月订单数
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/order/queryStatisticByEngineer")
    public void queryStatisticByEngineer(HttpServletRequest request,
                                         HttpServletResponse response) throws Exception {

        Map<String, Object> resultMap = Maps.newHashMap();

        //获取请求参数
        String engineerId = request.getParameter("engineerId");

        Option option = orderService.queryStatisticByEngineer(engineerId);

        resultMap.put(RESULTMAP_KEY_DATA, option);
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
        resultMap.put(RESULTMAP_KEY_MSG, "查询成功");
        renderJsonWithOutNull(response, resultMap);
    }

    /**
     * 查询待结算订单
     *
     * @param request
     * @param response
     * @throws Exception
     * @author: lijx
     * @CreateDate: 2016-10-18 下午10:10:37
     */
    @RequestMapping("/order/getSummaryOrder")
    public void getSummaryOrder(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String startDate = request.getParameter("query_startTime");
        String endDate = request.getParameter("query_endTime");

        Page page = getPageByRequest(request);
        if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)) {
            Order order = new Order();
            order.setQueryStartTime(startDate);
            order.setQueryEndTime(endDate);
            order.setPage(page);
            List<Map<String, Object>> list = orderService.getSummaryOrderListForPage(order);
            System.out.println("详情：" + list);
            page.setData(list);
        } else {
            page.setData(new ArrayList<Map<String, Object>>());
        }
        this.renderJson(response, page);
    }

    /**
     * 得到指定日期内各连锁商结算总金额
     *
     * @param request
     * @param response
     * @throws Exception
     * @author: lijx
     * @CreateDate: 2016-10-18 下午11:28:31
     */
    @RequestMapping("/order/queryBalanceAmount")
    public void queryBalanceAmount(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        Map<String, Object> resultMap = Maps.newHashMap();
        if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)) {
            Order order = new Order();
            order.setQueryStartTime(startDate);
            order.setQueryEndTime(endDate);
            Map<String, Object> amount = orderService.queryBalanceAmount(order);
            System.out.println("结算：" + amount);
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
            resultMap.put(RESULTMAP_KEY_DATA, amount);
        } else {
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
            resultMap.put(RESULTMAP_KEY_MSG, "查询时间为空");
        }
        this.renderJson(response, resultMap);
    }
}
