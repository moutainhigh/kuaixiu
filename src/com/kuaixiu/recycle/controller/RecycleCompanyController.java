package com.kuaixiu.recycle.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.kuaixiu.recycle.entity.RecyclePrize;
import com.kuaixiu.recycle.service.RecyclePrizeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.exception.SystemException;
import com.common.paginate.Page;
import com.google.common.collect.Maps;
import com.kuaixiu.recycle.entity.RecycleCompany;
import com.kuaixiu.recycle.entity.RecycleCompanyNews;
import com.kuaixiu.recycle.service.RecycleCompanyService;
import com.system.api.entity.ResultData;
import com.system.basic.user.entity.SessionUser;
import com.system.basic.user.service.SessionUserService;
import com.system.constant.SystemConstant;

/**
 * @author: anson
 * @CreateDate: 2018年3月13日 下午5:23:02
 * @version: V 1.0
 * 企业回收信息录入
 */
@Controller
public class RecycleCompanyController extends BaseController {

    @Autowired
    private RecycleCompanyService recycleCompanyService;
    @Autowired
    private SessionUserService sessionUserService;
    @Autowired
    private RecyclePrizeService recyclePrizeService;

    /**
     * 添加企业回收信息
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "recycle/addCompany")
    public void getBrandList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        try {
            JSONObject params = getPrarms(request);
            String name = params.getString("name");
            String recycleTime = params.getString("recycleTime");
            String customerManager = params.getString("customerManager");
            String mobile = params.getString("mobile");
            //回收机型的集合
            String info = params.getString("info");
            if (StringUtils.isBlank(name) || StringUtils.isBlank(info)
                    || StringUtils.isBlank(recycleTime) || StringUtils.isBlank(customerManager) || StringUtils.isBlank(mobile)) {
                throw new SystemException("请求参数不完整");
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            if (recycleTime.contains(" ")) {
                //转化时间
                recycleTime = recycleTime.substring(0, recycleTime.indexOf(" "));
            }
            JSONArray array = JSONArray.parseArray(info);
            JSONObject json = null;
            String productId, price;
            String allModel = "";
            //添加企业信息
            RecycleCompany rc = new RecycleCompany();
            String id = UUID.randomUUID().toString().replace("-", "");
            rc.setId(id);
            rc.setName(name);
            rc.setRecycleTime(sdf.parse(recycleTime));
            rc.setCustomerManager(customerManager);
            rc.setMobile(mobile);
            //添加企业id对应回收的机型信息
            RecycleCompanyNews n = new RecycleCompanyNews();
            for (int i = 0; i < array.size(); i++) {
                json = (JSONObject) array.get(i);
                productId = json.getString("productId");
                price = (String) request.getSession().getAttribute(productId);
                //验证机型价格
                if (StringUtils.isBlank(price)) {
                    System.out.println("重新获取价格");
                    String maxItem = RecycleController.getMaxItem(productId);
                    String realPrice = RecycleController.getPrice(productId, maxItem);
                    price = realPrice;
                }
                Integer allPrice = (Integer.parseInt(price)) * (json.getInteger("sum"));
                if (!(json.getInteger("price")).equals(allPrice)) {
                    throw new SystemException("请求机型与价格不匹配！");
                }
                allModel += json.getString("model") + ", ";  //将回收的所有机型添加到一个字段
                n.setCompanyId(id);
                n.setModel(json.getString("model"));
                n.setSum(json.getInteger("sum"));
                n.setProductId(productId);
                n.setBrand(json.getString("brand"));
                n.setPrice(new BigDecimal(allPrice));
                recycleCompanyService.addCompanyNews(n);
            }
            rc.setModel(allModel.substring(0, allModel.length() - 2));
            recycleCompanyService.add(rc);
            result.setResult(jsonResult);
            result.setResultCode("0");
            result.setSuccess(true);
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        renderJson(response, result);
    }

    /**
     * 企业回收
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/recycle/companyList")
    public ModelAndView RecycleSystem(HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {

        String returnView = "recycle/companyList";
        return new ModelAndView(returnView);
    }


    /**
     * 企业回收刷新数据
     */
    @RequestMapping(value = "recycle/companyList/queryListForPage")
    public void systemForPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
//        SessionUser su = getCurrentUser(request);
//        if (su.getType() != SystemConstant.USER_TYPE_SYSTEM && su.getType() != SystemConstant.USER_TYPE_CUSTOMER_SERVICE) {
//            throw new SystemException("对不起，您没有操作权限!");
//        }
        //获取查询条件
        String mobile = request.getParameter("query_mobile");
        String name = request.getParameter("query_name");
        String queryStartTime = request.getParameter("query_startTime");
        String queryEndTime = request.getParameter("query_endTime");
        String recycleStartTime = request.getParameter("recycle_startTime");
        String recycleEndTime = request.getParameter("recycle_endTime");
        //订单是否处理   0否  1是
        String orderStatus = request.getParameter("query_orderStatus");
        RecycleCompany r = new RecycleCompany();
        r.setMobile(mobile);
        r.setName(name);
        r.setQueryStartTime(queryStartTime);
        r.setQueryEndTime(queryEndTime);
        r.setRecycleStartTime(recycleStartTime);
        r.setRecycleEndTime(recycleEndTime);
        r.setIsDel(0);
        if (!StringUtils.isBlank(orderStatus)) {
            r.setOrderStatus(Integer.parseInt(orderStatus));
        }
        Page page = getPageByRequest(request);
        r.setPage(page);
        r.setIsDel(0);
        List<RecycleCompany> list = recycleCompanyService.queryListForPage(r);
        page.setData(list);
        this.renderJson(response, page);
    }


    /**
     * delete
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "recycle/company/delete")
    public void delete(HttpServletRequest request,
                       HttpServletResponse response) throws Exception {
//        SessionUser su = getCurrentUser(request);
//        if (su.getType() != SystemConstant.USER_TYPE_SYSTEM) {
//            throw new SystemException("对不起，您没有操作权限!");
//        }
        Map<String, Object> resultMap = Maps.newHashMap();
        //获取项目id
        String id = request.getParameter("id");
        String[] ids = id.split(",");
        for (String id1 : ids) {
            RecyclePrize t = recyclePrizeService.getDao().queryById(id1);
            t.setIsDel(1);
            recyclePrizeService.getDao().update(t);
        }
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
        resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
        renderJson(response, resultMap);
    }

    /**
     * 企业回收详情
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "recycle/company/detail")
    public String detail(HttpServletRequest request, HttpServletResponse response) {
//        SessionUser su = getCurrentUser(request);
//        if (su.getType() != SystemConstant.USER_TYPE_SYSTEM && su.getType() != SystemConstant.USER_TYPE_CUSTOMER_SERVICE) {
//            throw new SystemException("对不起，您没有操作权限!");
//        }
        String id = request.getParameter("id");
        if (StringUtils.isBlank(id)) {
            throw new SystemException("参数不完整");
        }
        BigDecimal price = new BigDecimal(0);
        RecycleCompany company = recycleCompanyService.queryById(id);
        List<RecycleCompanyNews> list = recycleCompanyService.queryCompanyNews(company.getId());
        for (RecycleCompanyNews c : list) {
            price = price.add(c.getPrice());
        }
        request.setAttribute("company", company);
        request.setAttribute("list", list);
        request.setAttribute("price", price);
        return "recycle/companyDetail";
    }


    /**
     * 企业回收用户查询登录
     *
     * @throws IOException
     */
    @RequestMapping(value = "recycle/login")
    public void login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResultData result = new ResultData();
        try {
            JSONObject params = getPrarms(request);
            String mobile = params.getString("mobile");
            String code = params.getString("code");
            if (StringUtils.isBlank(mobile)) {
                throw new SystemException("手机号不能为空");
            }
            if (!checkRandomCode(request, mobile, code)) { // 验证手机号和验证码
                throw new SystemException("手机号或验证码输入错误");
            }
            String tokenId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
            request.getSession().setAttribute("recycelt_tokenId", tokenId);
            getResult(result,tokenId,true,"0","成功");
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        renderJson(response, result);
    }


    /**
     * 页面提供企业信息查询
     *
     * @throws IOException
     */
    @RequestMapping(value = "recycle/getRecycleNews")
    public void getNews(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResultData result = new ResultData();
        JSONArray info = new JSONArray();
        JSONObject json = new JSONObject();
        try {
            String token = (String) request.getSession().getAttribute("recycelt_tokenId");
            JSONObject params = getPrarms(request);
            String mobile = params.getString("mobile");
            String tokenId = params.getString("tokenId");
            String pageIndex = params.getString("pageIndex");
            String pageSize = params.getString("pageSize");
            if (StringUtils.isBlank(token) || !token.equals(tokenId)) {
                throw new SystemException("token错误，请重新登录");
            }
            if (StringUtils.isBlank(mobile)) {
                throw new SystemException("手机号不能为空");
            }
            RecycleCompany rc = new RecycleCompany();
            rc.setMobile(mobile);

            Page dt = new Page();
            if (StringUtils.isBlank(pageIndex) || pageIndex.equals(1)) {
                //默认第一页,第一条
                dt.setStart(1);
            } else {
                dt.setStart((Integer.parseInt(pageIndex) - 1) * Integer.parseInt(pageSize));
            }

            if (StringUtils.isBlank(pageSize)) {
                //默认一页5条
                dt.setPageSize(5);
            } else {
                dt.setPageSize(Integer.parseInt(pageSize));
            }
            rc.setPage(dt);
            rc.setIsDel(0);
            List<RecycleCompany> list = recycleCompanyService.queryListForPage(rc);
            if (list.isEmpty()) {
                throw new SystemException("暂无回收信息");
            }
            for (RecycleCompany c : list) {
                JSONObject j = getRealNews(c, recycleCompanyService);
                info.add(j);
            }
            json.put("totalPage", dt.getTotalPage());
            json.put("totalRecord", dt.getRecordsTotal());
            json.put("info", info);
            getResult(result,json,true,"0","成功");
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        renderJson(response, result);
    }


    /**
     * 页面提供企业信息订单详情
     *
     * @throws IOException
     */
    @RequestMapping(value = "recycle/getDetailNews")
    public void getDetailNews(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        try {
            //判断是有过登录
            String token = (String) request.getSession().getAttribute("recycelt_tokenId");
            JSONObject params = getPrarms(request);
            String id = params.getString("id");
            String tokenId = params.getString("tokenId");
            if (StringUtils.isBlank(token) || !token.equals(tokenId)) {
                throw new SystemException("token错误，请重新登录");
            }
            if (StringUtils.isBlank(id)) {
                throw new SystemException("订单id不能为空");
            }
            List<RecycleCompanyNews> list = recycleCompanyService.queryCompanyNews(id);
            RecycleCompany company = recycleCompanyService.queryById(id);
            JSONObject j = getRealNews(company, recycleCompanyService);
            jsonResult.put("company", j);

            JSONArray info = new JSONArray();
            for (RecycleCompanyNews n : list) {
                JSONObject json = new JSONObject();
                json.put("brand", n.getBrand());
                json.put("model", n.getModel());
                json.put("price", (n.getPrice()).divide(new BigDecimal(n.getSum())));
                json.put("sum", n.getSum());
                json.put("allPrice", n.getPrice());
                info.add(json);
            }
            jsonResult.put("list", info);
            getResult(result,jsonResult,true,"0","成功");
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        renderJson(response, result);
    }


    /**
     * 封装实际返回信息
     *
     * @param c
     * @return
     */
    public static JSONObject getRealNews(RecycleCompany c, RecycleCompanyService recycleCompanyService) {
        JSONObject j = new JSONObject();
        j.put("customerManager", c.getCustomerManager());
        j.put("id", c.getId());
        j.put("inTime", c.getInTime());
        j.put("mobile", c.getMobile());
        j.put("model", c.getModel());
        j.put("name", c.getName());
        j.put("recycleTime", c.getRecycleTime());
        j.put("note", c.getNote());
        List<RecycleCompanyNews> list = recycleCompanyService.queryCompanyNews(c.getId());
        BigDecimal price = new BigDecimal(0);
        for (RecycleCompanyNews r : list) {
            price = price.add(r.getPrice());
        }
        j.put("totalPrice", price);
        return j;
    }

    /**
     * 更新订单状态 增加备注
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "recycle/company/updateNote")
    public void updateNote(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResultData result = new ResultData();
        JSONArray jsonResult = new JSONArray();
        try {
//            SessionUser su = getCurrentUser(request);
//            if (su.getType() != SystemConstant.USER_TYPE_SYSTEM) {
//                throw new SystemException("对不起，您没有操作权限!");
//            }
            String id = request.getParameter("id");
            String note = request.getParameter("note");
            RecycleCompany company = recycleCompanyService.queryById(id);
            if (company == null) {
                throw new SystemException("订单不存在");
            }
            company.setNote(note);
            company.setOrderStatus(1);
            recycleCompanyService.saveUpdate(company);
            result.setResult(jsonResult);
            result.setResultCode("0");
            result.setSuccess(true);
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        renderJson(response, result);
    }


}
