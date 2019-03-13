package com.kuaixiu.recycle.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.exception.SystemException;
import com.common.paginate.Page;
import com.common.util.AES;
import com.common.wechat.common.util.StringUtils;
import com.kuaixiu.recycle.entity.RecycleCheckItems;
import com.kuaixiu.recycle.entity.RecycleOrder;
import com.kuaixiu.recycle.entity.RecycleTest;
import com.kuaixiu.recycle.service.RecycleCheckItemsService;
import com.kuaixiu.recycle.service.RecycleOrderService;
import com.kuaixiu.recycle.service.RecycleTestService;
import com.system.api.entity.ResultData;
import com.system.constant.ApiResultConstant;
import com.system.constant.SystemConstant;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @author: najy
 * @CreateDate: 2019年3月12日15:09:56
 * @version: V 1.0
 * 回收检测信息
 */
@Controller
public class RecycleTestController extends BaseController {
    private static final Logger log = Logger.getLogger(RecycleTestController.class);


    @Autowired
    private RecycleCheckItemsService checkItemsService;
    @Autowired
    private RecycleTestService testService;
    @Autowired
    private RecycleOrderService orderService;
    /**
     * 基础访问接口地址
     */
    private static final String baseNewUrl = SystemConstant.RECYCLE_NEW_URL;
    /**
     * 需要加密的数据名
     */
    private static final String cipherdata = SystemConstant.RECYCLE_REQUEST;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "recycle/recycleTestList")
    public ModelAndView list(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {
        JSONObject jsonResult = new JSONObject();
        String url = baseNewUrl + "getbrandlist";
        String categoryid = request.getParameter("categoryid");
        JSONObject requestNews = new JSONObject();
        JSONObject code = new JSONObject();
        code.put("categoryid", categoryid);
        String realCode = AES.Encrypt(code.toString());  //加密
        requestNews.put(cipherdata, realCode);
        //发起请求
        String getResult = AES.post(url, requestNews);
        //对得到结果进行解密
        jsonResult = getResult(AES.Decrypt(getResult));
        JSONArray jsonArray=jsonResult.getJSONArray("datainfo");
        request.setAttribute("brands", jsonArray);
        String returnView = "recycle/testList";
        return new ModelAndView(returnView);
    }

    /**
     * 用于外部调用登录
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/recycle/testGetModels")
    @ResponseBody
    public ResultData login(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        JSONObject jsonObject = new JSONObject();
        try {
            String categoryid = request.getParameter("categoryid");
            String brandId = request.getParameter("brandId");
            String keyword = request.getParameter("keyword");
            JSONObject jsonResult = new JSONObject();
            String url = baseNewUrl + "getmodellist";
            JSONObject requestNews = new JSONObject();
            //调用接口需要加密的数据
            JSONObject code = new JSONObject();
            code.put("pageindex", 1);
            code.put("pagesize", 500);
            code.put("categoryid", categoryid);
            code.put("brandid", brandId);
            code.put("keyword", keyword);
            String realCode = AES.Encrypt(code.toString());  //加密
            requestNews.put(cipherdata, realCode);
            //发起请求
            String getResult = AES.post(url, requestNews);
            //对得到结果进行解密
            jsonResult = getResult(AES.Decrypt(getResult));
            JSONArray jsonArray=jsonResult.getJSONArray("datainfo");
            JSONObject jsonObject1=(JSONObject) jsonArray.get(0);
            JSONArray jsonArray1=jsonObject1.getJSONArray("sublist");
            getResult(result,jsonArray1,true,"0","成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }

    /**
     * queryListForPage
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/recycle/testListForPage")
    public void queryListForPage(HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        //获取查询条件
        String queryStartTime = request.getParameter("query_startTime");
        String queryEndTime = request.getParameter("query_endTime");
        String mobile = request.getParameter("mobile");
        String brandId = request.getParameter("brandId");
        String productId = request.getParameter("productId");
        String channel = request.getParameter("channel");//渠道
        String isOrder = request.getParameter("isOrder");//是否成单
        String isVisit = request.getParameter("isVisit");//是否回访

        Page page = getPageByRequest(request);
        RecycleCheckItems checkItem = new RecycleCheckItems();
        checkItem.setQueryStartTime(queryStartTime);
        checkItem.setQueryEndTime(queryEndTime);
        checkItem.setLoginMobile(mobile);
        checkItem.setBrandId(brandId);
        checkItem.setProductId(productId);
        if(StringUtils.isNotBlank(isOrder)){
            checkItem.setIsOrder(isOrder);
        }
        if(StringUtils.isNotBlank(isVisit)){
            checkItem.setIsVisit(isVisit);
        }
        checkItem.setPage(page);
        List<Map> checkItems = checkItemsService.getDao().queryTestListForPage(checkItem);
        String url = baseNewUrl + "getchecklist";
        for (Map map : checkItems) {
            JSONObject jsonResult = new JSONObject();
            String productId1 = map.get("product_id").toString();
            JSONObject requestNews = new JSONObject();
            //调用接口需要加密的数据
            JSONObject code = new JSONObject();
            code.put("productid", productId1);
            String realCode = AES.Encrypt(code.toString());  //加密
            requestNews.put(cipherdata, realCode);
            //发起请求
            String getResult = AES.post(url, requestNews);
            //对得到结果进行解密
            jsonResult = (JSONObject) JSONObject.parse(AES.Decrypt(getResult));
            JSONObject jsonObject=jsonResult.getJSONObject("datainfo");
            JSONArray jsonArray=jsonObject.getJSONArray("questions");
        }

        page.setData(checkItems);
        this.renderJson(response, page);
    }

    /**
     * 查看
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "recycle/recycleTestDetail")
    public ModelAndView recycleTestDetail(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {
        String id = request.getParameter("id");
        String itemName = request.getParameter("itemName");//检测项
        RecycleCheckItems checkItems=checkItemsService.getDao().queryByTestId(id);

        RecycleTest recycleTest=testService.getDao().queryByCheckId(checkItems.getId());
        if(StringUtils.isNotBlank(recycleTest.getRecycleId())){
            RecycleOrder recycleOrder=orderService.queryById(recycleTest.getRecycleId());
            request.setAttribute("recycleOrderNo", recycleOrder.getOrderNo());
        }
        itemName="选项";
        request.setAttribute("itemName", itemName);
        request.setAttribute("recycleTest", recycleTest);
        request.setAttribute("checkItems", checkItems);
        String returnView = "recycle/testDetail";
        return new ModelAndView(returnView);
    }

    /**
     * 回访跳转
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "recycle/recycleTestRecord")
    public ModelAndView recycleTestRecord(HttpServletRequest request,
                                          HttpServletResponse response) throws Exception {
        String id = request.getParameter("id");
        String itemName = request.getParameter("itemName");//检测项
        RecycleCheckItems checkItems=checkItemsService.getDao().queryByTestId(id);

        itemName="选项";
        request.setAttribute("itemName", itemName);
        request.setAttribute("checkItems", checkItems);
        String returnView = "recycle/testRecord";
        return new ModelAndView(returnView);
    }

    /**
     * 返回结果解析
     *
     * @param originalString
     * @return
     */
    public static JSONObject getResult(String originalString) {
        JSONObject result = (JSONObject) JSONObject.parse(originalString);
        if (result.getString("result") != null && !result.getString("result").equals("RESPONSESUCCESS")) {
            throw new SystemException(result.getString("msg"));
        }
        return result;
    }
}
