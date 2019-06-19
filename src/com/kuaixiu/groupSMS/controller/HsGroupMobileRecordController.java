package com.kuaixiu.groupSMS.controller;

import com.common.base.controller.BaseController;
import com.common.paginate.Page;
import com.kuaixiu.groupSMS.entity.HsGroupMobileRecord;
import com.kuaixiu.groupSMS.service.HsGroupMobileRecordService;
import com.system.api.entity.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * HsGroupMobileRecord Controller
 *
 * @CreateDate: 2019-06-19 下午02:38:21
 * @version: V 1.0
 */
@Controller
public class HsGroupMobileRecordController extends BaseController {

    @Autowired
    private HsGroupMobileRecordService hsGroupMobileRecordService;

    /**
     * 跳转群发短信记录列表
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "groupSms/toGroupMobileRecord")
    public ModelAndView toGroupMobileRecords(HttpServletRequest request,
                                             HttpServletResponse response) throws Exception {

        String returnView = "groupSms/groupMobileRecordList";
        return new ModelAndView(returnView);
    }

    /**
     * 群发短信记录列表
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "groupSms/groupMobileRecordForPage")
    public void groupMobileRecordForPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Page page = getPageByRequest(request);
        HsGroupMobileRecord groupMobileRecord = new HsGroupMobileRecord();
        groupMobileRecord.setPage(page);
        List<HsGroupMobileRecord> groupMobileRecords = hsGroupMobileRecordService.queryListForPage(groupMobileRecord);
        page.setData(groupMobileRecords);
        this.renderJson(response, page);
    }

    @RequestMapping(value = "groupSms/groupSendSms")
    @ResponseBody
    public ResultData groupSendSms(HttpServletRequest request, HttpServletResponse response)throws Exception{

        try {

        }catch (Exception e){

        }

    }
}
