package com.kuaixiu.engineer.controller;

import java.math.BigDecimal;
import java.util.*;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.exception.SystemException;
import com.common.paginate.Page;
import com.google.common.collect.Maps;
import com.kuaixiu.engineer.entity.Engineer;
import com.kuaixiu.engineer.service.EngineerService;
import com.kuaixiu.engineer.service.NewEngineerService;
import com.kuaixiu.order.entity.Order;
import com.kuaixiu.order.entity.OrderDetail;
import com.kuaixiu.order.service.OrderDetailService;
import com.kuaixiu.order.service.OrderService;
import com.kuaixiu.provider.entity.Provider;
import com.kuaixiu.provider.service.ProviderService;
import com.kuaixiu.shop.entity.Shop;
import com.kuaixiu.shop.service.ShopService;
import com.system.constant.SystemConstant;
import com.system.basic.user.entity.SessionUser;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Engineer Controller
 *
 * @CreateDate: 2016-08-26 上午03:13:05
 * @version: V 1.0
 */
@Controller
public class EngineerController extends BaseController {

    @Autowired
    private EngineerService engineerService;
    @Autowired
    private ProviderService providerService;
    @Autowired
    private ShopService shopService;
    @Autowired
    private NewEngineerService newEngineerService;


    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/engineer/list")
    public ModelAndView list(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {

        String returnView = "engineer/list";
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
    @RequestMapping(value = "/engineer/queryAchievementForPage")
    public void queryAchievementListForPage(HttpServletRequest request,
                                            HttpServletResponse response) throws Exception {
        //获取查询条件
        String name = request.getParameter("query_name");
        String number = request.getParameter("query_number");
        String mobile = request.getParameter("query_mobile");
        String idcard = request.getParameter("query_idcard");
        String orderStatus = request.getParameter("order_status");
        String queryStartTime = request.getParameter("query_startTime");
        String queryEndTime = request.getParameter("query_endTime");
        String isPatch = request.getParameter("isPatch");
        String orderType = request.getParameter("orderType");

        Engineer eng = new Engineer();
        eng.setName(name);
        eng.setNumber(number);
        eng.setMobile(mobile);
        eng.setIdcard(idcard);
        if (StringUtils.isNotBlank(isPatch)) {
            eng.setIsPatch(Integer.valueOf(isPatch));
        }
        if (StringUtils.isNotBlank(queryStartTime) || StringUtils.isNotBlank(queryEndTime)) {
            if (StringUtils.isNotBlank(orderStatus)) {
                if (Integer.valueOf(orderStatus) == 40) {
                    throw new SystemException("对不起，进行中状态不可与时间同时筛选！");
                } else if (Integer.valueOf(orderStatus) == 50 || Integer.valueOf(orderStatus) == 60) {
                    eng.setOrderStatus(String.valueOf(orderStatus));
                    eng.setQueryStartTime(queryStartTime);
                    eng.setQueryEndTime(queryEndTime);
                }
            } else if (StringUtils.isBlank(orderStatus)) {
                eng.setOrderStatus("70");
                eng.setQueryStartTime(queryStartTime);
                eng.setQueryEndTime(queryEndTime);
            }
        } else if (StringUtils.isBlank(queryStartTime) && StringUtils.isBlank(queryEndTime)) {
            eng.setOrderStatus(String.valueOf(orderStatus));
        }

        //获取登录用户
        SessionUser su = getCurrentUser(request);
        //判断用户类型系统管理员可以查看所有工程师
        if (su.getType() == SystemConstant.USER_TYPE_SYSTEM
                || su.getType() == SystemConstant.USER_TYPE_CUSTOMER_SERVICE) {

        } else if (su.getType() == SystemConstant.USER_TYPE_PROVIDER) {
            //连锁商只能查看自己的工程师
            eng.setProviderCode(su.getProviderCode());
        } else if (su.getType() == SystemConstant.USER_TYPE_SHOP) {
            //门店商只能查看自己的工程师
            eng.setShopCode(su.getShopCode());
        } else {
            throw new SystemException("对不起，您无权查看此信息！");
        }
        //获取隐藏域搜索条件，门店查询工程师
        String shopCode = request.getParameter("hide_shopCode");
        if (StringUtils.isNotBlank(shopCode)) {
            eng.setShopCode(shopCode);
        }

        Page page = getPageByRequest(request);
        eng.setPage(page);
        List<Engineer> endList = newEngineerService.getDao().queryAchievementForPage(eng);
        for (Engineer engineer : endList) {
            engineer.setOrderType(orderType);
            //如果是多个门店，就便利查找名称
            newEngineerService.engineerShopCode(engineer);
        }
        page.setData(endList);
        this.renderJson(response, page);
    }

    /**
     * queryListForPage
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/engineer/queryListForPage")
    public void queryListForPage(HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        //获取查询条件
        String name = request.getParameter("query_name");
        String number = request.getParameter("query_number");
        String mobile = request.getParameter("query_mobile");
        String idcard = request.getParameter("query_idcard");
        Engineer eng = new Engineer();

        eng.setName(name);
        eng.setNumber(number);
        eng.setMobile(mobile);
        eng.setIdcard(idcard);

        //获取登录用户
        SessionUser su = getCurrentUser(request);
        //判断用户类型系统管理员可以查看所有工程师
        if (su.getType() == SystemConstant.USER_TYPE_SYSTEM
                || su.getType() == SystemConstant.USER_TYPE_CUSTOMER_SERVICE) {

        } else if (su.getType() == SystemConstant.USER_TYPE_PROVIDER) {
            //连锁商只能查看自己的工程师
            eng.setProviderCode(su.getProviderCode());
        } else if (su.getType() == SystemConstant.USER_TYPE_SHOP) {
            //门店商只能查看自己的工程师
            eng.setShopCode(su.getShopCode());
        } else {
            throw new SystemException("对不起，您无权查看此信息！");
        }
        //获取隐藏域搜索条件，门店查询工程师
        String shopCode = request.getParameter("hide_shopCode");
        if (StringUtils.isNotBlank(shopCode)) {
            eng.setShopCode(shopCode);
        }

        Page page = getPageByRequest(request);
        eng.setPage(page);
        List<Engineer> endList = newEngineerService.queryListForPage(eng);
        for (Engineer engineer : endList) {
            //如果是多个门店，就便利查找名称
            newEngineerService.engineerShopCode(engineer);
        }
        page.setData(endList);
        this.renderJson(response, page);
    }

    /**
     * add
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/engineer/add")
    public ModelAndView add(HttpServletRequest request,
                            HttpServletResponse response) throws Exception {
        //获取登录用户
        SessionUser su = getCurrentUser(request);
        //判断用户类型系统管理员可以查看所有工程师
        if (su.getType() == SystemConstant.USER_TYPE_SYSTEM
                || su.getType() == SystemConstant.USER_TYPE_CUSTOMER_SERVICE) {
            //获取连锁商
            List<Provider> providerL = providerService.queryList(null);
            request.setAttribute("providerL", providerL);
        } else if (su.getType() == SystemConstant.USER_TYPE_PROVIDER
                || su.getType() == SystemConstant.USER_TYPE_CUSTOMER_SERVICE) {
            //获取维修门店
            List<Shop> shopL = shopService.queryByProviderCode(su.getProviderCode());
            request.setAttribute("shopL", shopL);
        }

        String returnView = "engineer/addEngineer";
        return new ModelAndView(returnView);
    }

    @Autowired
    private OrderDetailService detailService;

    /**
     * 计算订单总价
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/engineer/getTotalPrice")
    public void getTotalPrice(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        Map<String, Object> resultMap = Maps.newHashMap();
        //获取查询条件
        String queryStartRepairTime = request.getParameter("query_startRepairTime");//完成时间
        String queryEndRepairTime = request.getParameter("query_endRepairTime");
        String orderState = request.getParameter("query_orderState");
        String engNumber = request.getParameter("query_engNumber");
        String isPatch = request.getParameter("isPatch");//是否去掉贴膜优惠券
        Order o = new Order();
        o.setEngineerNumber(engNumber);
        if (StringUtils.isNotBlank(queryStartRepairTime)) {
            o.setQueryRepairStartTime(queryStartRepairTime);
        }
        if (StringUtils.isNotBlank(isPatch)) {
            o.setIsPatch(Integer.valueOf(isPatch));
        }
        if (StringUtils.isNotBlank(queryEndRepairTime)) {
            o.setQueryRepairEndTime(queryEndRepairTime);
        }

        if (StringUtils.isNotBlank(orderState)) {
            o.setOrderStatus(Integer.parseInt(orderState));
        }
        BigDecimal sumPrice = new BigDecimal("0");
        List<Order> list = orderService.queryList(o);
        Iterator<Order> it = list.iterator();
        while (it.hasNext()) {
            Order order = it.next();
            if(order.getOrderStatus()<50||order.getOrderStatus()==60){
                sumPrice = sumPrice.add(new BigDecimal("0"));
            }else {
                sumPrice = sumPrice.add(order.getRealPriceSubCoupon());
            }
        }
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
        resultMap.put(RESULTMAP_KEY_DATA, sumPrice);
        this.renderJson(response, resultMap);
    }

    /**
     * index
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/engineer/save")
    public void save(HttpServletRequest request,
                     HttpServletResponse response) throws Exception {

        Map<String, Object> resultMap = Maps.newHashMap();
        //获取名称
        String name = request.getParameter("addName");
        String gender = request.getParameter("addGender");
        String mobile = request.getParameter("addMobile");
        String idcard = request.getParameter("addIdcard");
        String providerCode = request.getParameter("addProviderCode");
        String[] shopCodes = request.getParameterValues("addShopCode");

        if (shopCodes == null || shopCodes.length == 0 || shopCodes[0].trim().equals("")) {
            throw new SystemException("至少选择一个门店");
        }

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < shopCodes.length; i++) {
            sb.append(shopCodes[i]);
            if ((i + 1) != shopCodes.length) {
                sb.append(",");
            }
        }
        String shopCode = sb.toString();

        SessionUser su = getCurrentUser(request);

        if (su.getType() == SystemConstant.USER_TYPE_PROVIDER) {
            //如果是连锁商
            providerCode = su.getProviderCode();
        } else if (su.getType() == SystemConstant.USER_TYPE_SHOP) {
            //如果是门店商
            providerCode = su.getProviderCode();
            shopCode = su.getShopCode();
        }

        Engineer eng = new Engineer();
        eng.setProviderCode(providerCode);
        eng.setShopCode(shopCode);
        eng.setName(name);
        if ("1".equals(gender)) {
            eng.setGender("男");
        } else {
            eng.setGender("女");
        }
        eng.setMobile(mobile);
        eng.setIdcard(idcard);
        eng.setIsDel(0);
        eng.setIsDispatch(0);
        eng.setCreateUserid(su.getUserId());

        engineerService.save(eng, su);
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
        resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
        renderJson(response, resultMap);
    }

    @Autowired
    private OrderService orderService;

    /**
     * detail
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/engineer/detail")
    public ModelAndView detail(HttpServletRequest request,
                               HttpServletResponse response) throws Exception {

        //获取工程师id
        String id = request.getParameter("id");
        //查询工程师内容
        Engineer eng = engineerService.queryById(id);

        request.setAttribute("eng", eng);
        //订单状态筛选列表
        Map<Integer, Object> m = orderService.getSelectOrderStatus();
        request.setAttribute("selectOrderStatus", m);

        String returnView = "engineer/detail";
        return new ModelAndView(returnView);
    }

    /**
     * edit
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/engineer/edit")
    public ModelAndView edit(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {

        //获取工程师id
        String id = request.getParameter("id");
        //查询工程师内容
        Engineer eng = engineerService.queryById(id);
        //获取连锁商
        List<Provider> providerL = providerService.queryList(null);
        //获取维修门店
        List<Shop> shopL = shopService.queryByProviderCode(eng.getProviderCode());
        JSONArray jsonArray = new JSONArray();
        //如果是多个门店，就便利查找名称
        if (eng.getShopCode().contains(",")) {
            List<String> shopCodeList = Arrays.asList(eng.getShopCode().split(","));


            for (String shopCodeList1 : shopCodeList) {
                JSONObject jsonObject = new JSONObject();
                Shop shop = shopService.queryByCode(shopCodeList1);
                jsonObject.put("jsonCode", shop.getCode());
                jsonArray.add(jsonObject);
            }
        } else {
            JSONObject jsonObject = new JSONObject();
            Shop shop = shopService.queryByCode(eng.getShopCode());
            jsonObject.put("jsonCode", shop.getCode());
            jsonArray.add(jsonObject);
        }
        eng.setShopCodes(jsonArray);
        request.setAttribute("engineer", eng);
        request.setAttribute("providerL", providerL);
        request.setAttribute("shopL", shopL);
        String returnView = "engineer/editEngineer";
        return new ModelAndView(returnView);
    }

    /**
     * update
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/engineer/update")
    public void update(HttpServletRequest request,
                       HttpServletResponse response) throws Exception {

        Map<String, Object> resultMap = Maps.newHashMap();
        SessionUser su = getCurrentUser(request);

        //获取工程师id
        String id = request.getParameter("id");

        //获取名称
        String name = request.getParameter("addName");
        String gender = request.getParameter("addGender");
        String mobile = request.getParameter("addMobile");
        String idcard = request.getParameter("addIdcard");
        String addDispatch = request.getParameter("addDispatch");
        String providerCode = request.getParameter("addProviderCode");
        String[] shopCodes = request.getParameterValues("addShopCode");

        if (shopCodes == null || shopCodes.length <= 0 || shopCodes[0].trim().equals("")) {
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
            resultMap.put(RESULTMAP_KEY_MSG, "至少选择一个门店");
            renderJson(response, resultMap);
        }

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < shopCodes.length; i++) {
            sb.append(shopCodes[i]);
            if ((i + 1) != shopCodes.length) {
                sb.append(",");
            }
        }
        String shopCode = sb.toString();

        if (su.getType() == SystemConstant.USER_TYPE_PROVIDER) {
            //如果是连锁商
            providerCode = su.getProviderCode();
        } else if (su.getType() == SystemConstant.USER_TYPE_SHOP) {
            //如果是门店商
            providerCode = su.getProviderCode();
            shopCode = su.getShopCode();
        }
        Engineer eng = engineerService.queryById(id);
        eng.setId(id);
        eng.setProviderCode(providerCode);
        eng.setShopCode(shopCode);
        eng.setName(name);
        if ("1".equals(gender)) {
            eng.setGender("男");
        } else {
            eng.setGender("女");
        }
        eng.setMobile(mobile);
        eng.setIdcard(idcard);
        eng.setIsDel(0);
        eng.setUpdateUserid(su.getUserId());
        if ("2".equals(addDispatch)) {//下线
            if (eng.getIsDispatch() == 1) {
                throw new SystemException("该工程师还有订单未完成");
            } else {
                eng.setIsDispatch(2);
            }
        } else {//上线
            if (eng.getIsDispatch() == 2) {
                eng.setIsDispatch(0);
            }
        }

        engineerService.update(eng);

        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
        resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
        renderJson(response, resultMap);
    }

    /**
     * delete
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/engineer/delete")
    public void delete(HttpServletRequest request,
                       HttpServletResponse response) throws Exception {

        Map<String, Object> resultMap = Maps.newHashMap();
        //获取工程师id
        String id = request.getParameter("id");
        SessionUser su = getCurrentUser(request);

        engineerService.deleteById(id, su);

        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
        resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
        renderJson(response, resultMap);
    }
}
