package com.kuaixiu.recycle.controller;

import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.exception.SystemException;
import com.common.paginate.Page;
import com.common.util.AES;
import com.kuaixiu.recycle.entity.RecycleCheckItems;
import com.kuaixiu.recycle.service.RecycleCheckItemsService;
import com.system.constant.SystemConstant;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
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
public class RecycleTestController  extends BaseController {
    private static final Logger log = Logger.getLogger(RecycleTestController.class);


    @Autowired
    private RecycleCheckItemsService checkItemsService;
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
    @RequestMapping(value = "/recycle/testList")
    public ModelAndView list(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {

        String returnView = "recycle/testList";
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
    @RequestMapping(value = "/nbTelecomSJ/nbAreaForPage")
    public void queryListForPage(HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        //获取查询条件
        String queryStartTime = request.getParameter("queryStartTime");
        String queryEndTime = request.getParameter("queryEndTime");
        String mobile = request.getParameter("mobile");
        String brandId = request.getParameter("brandId");
        String modelId = request.getParameter("modelId");
        String channel = request.getParameter("channel");//渠道
        String isOrder = request.getParameter("isOrder");//是否成单
        String isVisit = request.getParameter("isVisit");//是否回访

        Page page = getPageByRequest(request);
        RecycleCheckItems checkItem=new RecycleCheckItems();
        checkItem.setQueryStartTime(queryStartTime);
        checkItem.setQueryEndTime(queryEndTime);
        checkItem.setLoginMobile(mobile);
        checkItem.setBrandId(brandId);
        checkItem.setModelId(modelId);
        checkItem.setIsOrder(isOrder);
        checkItem.setIsVisit(isVisit);
        checkItem.setPage(page);
        List<Map> checkItems=checkItemsService.getDao().queryTestListForPage(checkItem);
        String url = baseNewUrl + "getchecklist";
        for(Map map:checkItems){
            JSONObject jsonResult = new JSONObject();
            String productId=map.get("product_id").toString();
            JSONObject requestNews = new JSONObject();
            //调用接口需要加密的数据
            JSONObject code = new JSONObject();
            code.put("productid", productId);
            String realCode = AES.Encrypt(code.toString());  //加密
            requestNews.put(cipherdata, realCode);
            //发起请求
            String getResult = AES.post(url, requestNews);
            //对得到结果进行解密
            jsonResult=getResult(AES.Decrypt(getResult));
        }

        page.setData(checkItems);
        this.renderJson(response, page);
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
