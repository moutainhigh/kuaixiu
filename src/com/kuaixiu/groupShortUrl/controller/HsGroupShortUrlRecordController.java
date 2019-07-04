package com.kuaixiu.groupShortUrl.controller;

import com.common.base.controller.BaseController;
import com.common.paginate.Page;
import com.kuaixiu.groupSMS.entity.*;
import com.kuaixiu.groupSMS.service.*;
import com.kuaixiu.groupShortUrl.entity.*;
import com.kuaixiu.groupShortUrl.service.*;
import com.kuaixiu.recycle.service.RecycleSystemService;
import com.system.api.entity.ResultData;
import com.system.basic.sequence.util.SeqUtil;
import com.system.basic.user.entity.SessionUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;

/**
 * HsGroupShortUrlRecord Controller
 *
 * @CreateDate: 2019-06-26 上午09:28:49
 * @version: V 1.0
 */
@Controller
public class HsGroupShortUrlRecordController extends BaseController {

    @Autowired
    private HsGroupShortUrlRecordService hsGroupShortUrlRecordService;
    @Autowired
    private HsGroupShortUrlMobileService hsGroupShortUrlMobileService;
    @Autowired
    private HsGroupShortUrlSmsService hsGroupShortUrlSmsService;
    @Autowired
    private HsGroupShortUrlBatchRecordService hsGroupShortUrlBatchRecordService;

    /**
     * 跳转群发短信记录详情
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "groupShort/toGroupMobileRecord")
    public ModelAndView toGroupMobileRecords(HttpServletRequest request,
                                             HttpServletResponse response) throws Exception {
        String batchId = request.getParameter("batchId");
        HsGroupShortUrlBatchRecord batchRecord=hsGroupShortUrlBatchRecordService.queryById(batchId);
        request.setAttribute("batchRecord", batchRecord);
        String returnView = "groupShort/groupSmsRecordDetail";
        return new ModelAndView(returnView);
    }

    /**
     * 群发短信记录列表
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "groupShort/groupMobileRecordForPage")
    public void groupMobileRecordForPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String batchId = request.getParameter("batchId");
        String mobile = request.getParameter("mobile");
        String queryStartTime = request.getParameter("queryStartTime");
        String queryEndTime = request.getParameter("queryEndTime");
        Page page = getPageByRequest(request);
        HsGroupShortUrlRecord groupMobileRecord = new HsGroupShortUrlRecord();
        groupMobileRecord.setBatchId(batchId);
        groupMobileRecord.setMobile(mobile);
        groupMobileRecord.setQueryStartTime(queryStartTime);
        groupMobileRecord.setQueryEndTime(queryEndTime);
        groupMobileRecord.setPage(page);
        List<HsGroupShortUrlRecord> groupMobileRecords = hsGroupShortUrlRecordService.queryListForPage(groupMobileRecord);
        for(HsGroupShortUrlRecord mobileRecord:groupMobileRecords){
            HsGroupShortUrlSms hsGroupMobileSms=hsGroupShortUrlSmsService.queryById(mobileRecord.getSmsId());
            mobileRecord.setSmsTemplate(hsGroupMobileSms.getNameLabel());
        }
        page.setData(groupMobileRecords);
        this.renderJson(response, page);
    }

    @RequestMapping(value = "groupShort/toGroupSendSms")
    public ModelAndView toGroupSendSms(HttpServletRequest request,
                                       HttpServletResponse response) throws Exception {
        List<HsGroupShortUrlSms> hsGroupMobileSms=hsGroupShortUrlSmsService.queryList(null);
        request.setAttribute("hsGroupMobileSms", hsGroupMobileSms);
        String returnView = "groupShort/createGroupSendSms";
        return new ModelAndView(returnView);
    }

    @RequestMapping(value = "groupShort/groupSendSms")
    @ResponseBody
    public ResultData groupSendSms(HttpServletRequest request, HttpServletResponse response)throws Exception{
        ResultData result = new ResultData();
        try {
            String smsId = request.getParameter("smsId");//模板id
            if (StringUtils.isBlank(smsId)) {
                return getSjResult(result, null, false, "2", null, "参数不完整");
            }
            SessionUser su=getCurrentUser(request);
            List<HsGroupShortUrlMobile> groupMobiles=hsGroupShortUrlMobileService.queryList(null);
            HsGroupShortUrlSms hsGroupMobileSms=hsGroupShortUrlSmsService.queryById(smsId);

            HsGroupShortUrlBatchRecord groupMobileBatchRecord=new HsGroupShortUrlBatchRecord();
            groupMobileBatchRecord.setId(UUID.randomUUID().toString().replace("-", ""));
            groupMobileBatchRecord.setSmsId(hsGroupMobileSms.getId());
            groupMobileBatchRecord.setBatchId(SeqUtil.getNext("gss"));
            groupMobileBatchRecord.setCreateUserid(su.getUserId());
            hsGroupShortUrlBatchRecordService.add(groupMobileBatchRecord);

            GroupShortUrlExecutor myExecutor = new GroupShortUrlExecutor();
            myExecutor.fun(su, groupMobiles,hsGroupShortUrlRecordService,
                    hsGroupMobileSms,hsGroupShortUrlMobileService,
                    groupMobileBatchRecord,recycleSystemService);

            getSjResult(result, null, true, "0", null, "后台创建中");
        } catch (Exception e) {
            e.printStackTrace();
            getSjResult(result, null, false, "5", null, "系统异常请稍后再试");
        }
        return result;

    }
    @Autowired
    private RecycleSystemService recycleSystemService;
}
