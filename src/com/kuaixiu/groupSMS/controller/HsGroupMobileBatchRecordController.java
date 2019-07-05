package com.kuaixiu.groupSMS.controller;

import com.common.base.controller.BaseController;
import com.common.paginate.Page;
import com.kuaixiu.groupSMS.entity.HsGroupMobileBatchRecord;
import com.kuaixiu.groupSMS.entity.HsGroupMobileSms;
import com.kuaixiu.groupSMS.service.HsGroupMobileBatchRecordService;
import com.kuaixiu.groupSMS.service.HsGroupMobileSmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * HsGroupMobileBatchRecord Controller
 *
 * @CreateDate: 2019-06-21 上午11:07:52
 * @version: V 1.0
 */
@Controller
public class HsGroupMobileBatchRecordController extends BaseController {

    @Autowired
    private HsGroupMobileBatchRecordService hsGroupMobileBatchRecordService;
    @Autowired
    private HsGroupMobileSmsService hsGroupMobileSmsService;

    /**
     * 跳转群发短信批次记录列表
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "groupSms/toGroupMobileBatchRecord")
    public ModelAndView toGroupMobileBatchRecord(HttpServletRequest request,
                                             HttpServletResponse response) throws Exception {

        String returnView = "groupSms/groupSmsBatchRecordList";
        return new ModelAndView(returnView);
    }

    /**
     * 群发短信批次记录列表
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "groupSms/groupMobileBatchRecordForPage")
    public void groupMobileBatchRecordForPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String mobile = request.getParameter("mobile");
        String queryStartTime = request.getParameter("queryStartTime");
        String queryEndTime = request.getParameter("queryEndTime");

        Page page = getPageByRequest(request);
        HsGroupMobileBatchRecord batchRecord = new HsGroupMobileBatchRecord();
        batchRecord.setMobile(mobile);
        batchRecord.setQueryStartTime(queryStartTime);
        batchRecord.setQueryEndTime(queryEndTime);
        batchRecord.setPage(page);
        List<HsGroupMobileBatchRecord> groupMobileBatchRecords = hsGroupMobileBatchRecordService.queryListForPage(batchRecord);
        for(HsGroupMobileBatchRecord mobileBatchRecord:groupMobileBatchRecords){
            HsGroupMobileSms hsGroupMobileSms=hsGroupMobileSmsService.queryById(mobileBatchRecord.getSmsId());
            mobileBatchRecord.setSmsTemplate(hsGroupMobileSms.getNameLabel());
        }
        page.setData(groupMobileBatchRecords);
        this.renderJson(response, page);
    }
}
