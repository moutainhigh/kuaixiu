package com.kuaixiu.recycle.controller;

import com.common.base.controller.BaseController;
import com.common.paginate.Page;
import com.kuaixiu.recycle.entity.RecycleCheckItems;
import com.kuaixiu.recycle.service.RecycleCheckItemsService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

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

        RecycleCheckItems checkItems=new RecycleCheckItems();

        Page page = getPageByRequest(request);
        checkItems.setPage(page);
        List<RecycleCheckItems> checkItems1=checkItemsService.queryListForPage(checkItems);

        page.setData(checkItems1);
        this.renderJson(response, page);
    }
}
