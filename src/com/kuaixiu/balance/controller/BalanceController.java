package com.kuaixiu.balance.controller;

import java.util.List;
import java.util.Map;

import com.common.base.controller.BaseController;
import com.common.paginate.Page;
import com.common.util.NOUtil;
import com.google.common.collect.Maps;
import com.kuaixiu.balance.entity.Balance;
import com.kuaixiu.balance.service.BalanceService;
import com.system.basic.user.entity.SessionUser;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Balance Controller
 *
 * @CreateDate: 2016-10-19 上午12:50:08
 * @version: V 1.0
 */
@Controller
public class BalanceController extends BaseController {

    @Autowired
    private BalanceService balanceService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/balance/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="balance/listForAdmin";
        return new ModelAndView(returnView);
    }

    /**
     * queryListForPage
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/balance/queryListForPage")
    public void queryListForPage(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        Balance b = new Balance();
        
        Page page = getPageByRequest(request);
        b.setPage(page);
        List<Balance> list = balanceService.queryListForPage(b);
        
        page.setData(list);
        this.renderJson(response, page);
    }
    
    /**
     * go新增结算批次
     */
    @RequestMapping("balance/orderBalance")
    public String goOrderBalance() {
        return "balance/addBalance";
    }

    /**
     *  新增结算批次 & 修改订单结算状态信息
     * @param request
     * @return
     * @throws Exception 
     */
    @RequestMapping("balance/addBalance")
    public void addBalance(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        Map<String, Object> resultMap = Maps.newHashMap();
        SessionUser sessionUser = getCurrentUser(request);
        
        //结算开始时间
        String startDate = request.getParameter("startDate");
        //结算结束时间
        String endDate = request.getParameter("endDate");
        //结算总金额，校验使用
        String amountPrice = request.getParameter("amount_price");
        
        Balance balance = new Balance();
        balance.setBatchName(startDate + " 至 " + endDate + " 连锁商结算账单");
        balance.setBalanceBeginTime(startDate);
        balance.setBalanceEndTime(endDate);

        balanceService.saveBalance(balance, amountPrice, sessionUser);
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
        resultMap.put(RESULTMAP_KEY_DATA, balance.getBatchNo());
        renderJson(response, resultMap);
    }
    
    /**
     * 批次详情
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/balance/batchDetail")
    public ModelAndView batchDetail(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        String id = request.getParameter("id");
        String batchNo = request.getParameter("batchNo");
        Balance balance = null;
        if(StringUtils.isNotBlank(id)){
            balance = balanceService.queryById(id);
        }
        if(balance == null && StringUtils.isNotBlank(batchNo)){
            balance = balanceService.queryByBatchNo(batchNo);
        }
        
        request.setAttribute("balance", balance);
        String returnView ="balance/batchDetail";
        return new ModelAndView(returnView);
    }
    
    /**
     * 删除结算单
     * @param request
     * @param response
     * @throws Exception
     * @author: lijx
     * @CreateDate: 2016-10-26 下午10:31:39
     */
    @RequestMapping(value = "/balance/delete")
    public void delete(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String id = request.getParameter("id");
        balanceService.deleteById(id);
        Map<String, Object> resultMap = Maps.newHashMap();
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
        resultMap.put(RESULTMAP_KEY_DATA, "OK");
        renderJson(response, resultMap);
    }
    
    public static void main(String[] args) {
		int a=3;
	}
}
