package com.kuaixiu.groupShortUrl.controller;

import com.common.base.controller.BaseController;
import com.common.paginate.Page;
import com.kuaixiu.groupShortUrl.entity.HsGroupShortUrlBatchRecord;
import com.kuaixiu.groupShortUrl.entity.HsGroupShortUrlSms;
import com.kuaixiu.groupShortUrl.service.HsGroupShortUrlBatchRecordService;
import com.kuaixiu.groupShortUrl.service.HsGroupShortUrlSmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * HsGroupShortUrlBatchRecord Controller
 *
 * @CreateDate: 2019-06-26 上午09:27:50
 * @version: V 1.0
 */
@Controller
public class HsGroupShortUrlBatchRecordController extends BaseController {

    @Autowired
    private HsGroupShortUrlBatchRecordService hsGroupShortUrlBatchRecordService;
    @Autowired
    private HsGroupShortUrlSmsService hsGroupShortUrlSmsService;

    /**
     * 跳转群发短信批次记录列表
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "groupShort/toGroupMobileBatchRecord")
    public ModelAndView toGroupMobileBatchRecord(HttpServletRequest request,
                                                 HttpServletResponse response) throws Exception {

        String returnView = "groupShort/groupSmsBatchRecordList";
        return new ModelAndView(returnView);
    }

    /**
     * 群发短信批次记录列表
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "groupShort/groupMobileBatchRecordForPage")
    public void groupMobileBatchRecordForPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String mobile = request.getParameter("mobile");
        String queryStartTime = request.getParameter("queryStartTime");
        String queryEndTime = request.getParameter("queryEndTime");

        Page page = getPageByRequest(request);
        HsGroupShortUrlBatchRecord batchRecord = new HsGroupShortUrlBatchRecord();
        batchRecord.setMobile(mobile);
        batchRecord.setQueryStartTime(queryStartTime);
        batchRecord.setQueryEndTime(queryEndTime);
        batchRecord.setPage(page);
        List<HsGroupShortUrlBatchRecord> groupMobileBatchRecords = hsGroupShortUrlBatchRecordService.queryListForPage(batchRecord);
        for(HsGroupShortUrlBatchRecord mobileBatchRecord:groupMobileBatchRecords){
            HsGroupShortUrlSms hsGroupMobileSms=hsGroupShortUrlSmsService.queryById(mobileBatchRecord.getSmsId());
            mobileBatchRecord.setSmsTemplate(hsGroupMobileSms.getNameLabel());
        }
        page.setData(groupMobileBatchRecords);
        this.renderJson(response, page);
    }
}
