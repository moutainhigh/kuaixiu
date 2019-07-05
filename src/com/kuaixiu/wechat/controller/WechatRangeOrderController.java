package com.kuaixiu.wechat.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.common.base.controller.BaseController;
import com.common.paginate.Page;
import com.kuaixiu.wechat.entity.WechatRangeOrder;
import com.kuaixiu.wechat.service.WechatRangeOrderService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Administrator on 2018/10/26/026.
 */
@Controller
public class WechatRangeOrderController extends BaseController {

    private static final SerializeConfig mapping = new SerializeConfig();
    private static final String DEFAULT_ENCODING = "UTF-8";

    @Autowired
    private WechatRangeOrderService wechatRangeOrderService;


    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/rangeOrder/list")
    public ModelAndView list(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {
        String returnView = "order/listForRangeOrder";
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
    @RequestMapping(value = "/rangeOrder/queryListForPage")
    public void queryListForPage(HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        WechatRangeOrder rangeOrder = new WechatRangeOrder();
        Page page = getPageByRequest(request);
        rangeOrder.setPage(page);
        List<WechatRangeOrder> rangeOrders = wechatRangeOrderService.queryListForPage(rangeOrder);
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for(WechatRangeOrder rangeOrder1:rangeOrders){
            rangeOrder1.setStrInTime(sdf.format(rangeOrder1.getInTime()));
        }
        page.setData(rangeOrders);
        this.renderJson(response, page);
    }

    /**
     * 以Json格式输出
     *
     * @param response
     * @param result
     * @throws IOException
     */
    public void renderJson(HttpServletResponse response, Object result) throws IOException {
        renderJson(response, result, null);
    }


    /**
     * 以Json格式输出
     *
     * @param response
     * @param result
     * @throws IOException
     */
    public void renderJson(HttpServletResponse response, Object result, String callback) throws IOException {
        initContentType(response, JSON_TYPE);

        // 输入流
        PrintWriter out = response.getWriter();
        String outrs = JSON.toJSONString(result, mapping, SerializerFeature.WriteMapNullValue);
        if (StringUtils.isNotEmpty(callback)) {
            outrs = callback + "(" + outrs + ")";
        }

        out.print(outrs);
        out.flush();
    }

    /**
     * 初始HTTP内容类型.
     *
     * @param response
     * @param contentType
     */
    private void initContentType(HttpServletResponse response, String contentType) {
        response.setContentType(contentType + ";charset=" + DEFAULT_ENCODING);
    }
}
