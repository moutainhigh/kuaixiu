package com.kuaixiu.groupSMS.controller;

import com.common.base.controller.BaseController;
import com.common.paginate.Page;
import com.kuaixiu.groupSMS.entity.*;
import com.kuaixiu.groupSMS.service.*;
import com.kuaixiu.recycle.entity.RecycleCoupon;
import com.kuaixiu.recycle.service.MyExecutor;
import com.kuaixiu.recycle.service.RecycleCouponService;
import com.kuaixiu.recycleCoupon.entity.HsActivityCouponRole;
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
 * HsGroupMobileRecord Controller
 *
 * @CreateDate: 2019-06-19 下午02:38:21
 * @version: V 1.0
 */
@Controller
public class HsGroupMobileRecordController extends BaseController {

    @Autowired
    private HsGroupMobileRecordService hsGroupMobileRecordService;
    @Autowired
    private HsGroupCouponRoleService hsGroupCouponRoleService;
    @Autowired
    private HsGroupMobileAddressService hsGroupMobileAddressService;
    @Autowired
    private HsGroupMobileService hsGroupMobileService;
    @Autowired
    private RecycleCouponService recycleCouponService;
    @Autowired
    private HsGroupMobileSmsService hsGroupMobileSmsService;
    @Autowired
    private HsGroupMobileBatchRecordService hsGroupMobileBatchRecordService;

    /**
     * 跳转群发短信记录详情
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "groupSms/toGroupMobileRecord")
    public ModelAndView toGroupMobileRecords(HttpServletRequest request,
                                             HttpServletResponse response) throws Exception {
        String batchId = request.getParameter("batchId");
        HsGroupMobileBatchRecord batchRecord=hsGroupMobileBatchRecordService.queryById(batchId);
        request.setAttribute("batchRecord", batchRecord);
        String returnView = "groupSms/groupSmsRecordDetail";
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
        String batchId = request.getParameter("batchId");
        Page page = getPageByRequest(request);
        HsGroupMobileRecord groupMobileRecord = new HsGroupMobileRecord();
        groupMobileRecord.setBatchId(batchId);
        groupMobileRecord.setPage(page);
        List<HsGroupMobileRecord> groupMobileRecords = hsGroupMobileRecordService.queryListForPage(groupMobileRecord);
        for(HsGroupMobileRecord mobileRecord:groupMobileRecords){
            HsGroupMobileAddress groupMobileAddress=hsGroupMobileAddressService.queryById(mobileRecord.getAddressId());
            mobileRecord.setAddress(groupMobileAddress.getAddress());
            RecycleCoupon recycleCoupon=recycleCouponService.queryById(mobileRecord.getCouponId());
            mobileRecord.setCouponCode(recycleCoupon.getCouponCode());
            HsGroupMobileSms hsGroupMobileSms=hsGroupMobileSmsService.queryById(mobileRecord.getSmsId());
            mobileRecord.setSmsTemplate(hsGroupMobileSms.getNameLabel());
        }
        page.setData(groupMobileRecords);
        this.renderJson(response, page);
    }

    @RequestMapping(value = "groupSms/toGroupSendSms")
    public ModelAndView toGroupSendSms(HttpServletRequest request,
                                             HttpServletResponse response) throws Exception {
        List<HsGroupCouponRole> groupCouponRoles = hsGroupCouponRoleService.queryList(null);
        request.setAttribute("groupCouponRoles", groupCouponRoles);
        List<HsGroupMobileAddress> groupMobileAddresses = hsGroupMobileAddressService.queryList(null);
        request.setAttribute("groupMobileAddresses", groupMobileAddresses);
        List<HsGroupMobileSms> hsGroupMobileSms=hsGroupMobileSmsService.queryList(null);
        request.setAttribute("hsGroupMobileSms", hsGroupMobileSms);
        String returnView = "groupSms/createGroupSendSms";
        return new ModelAndView(returnView);
    }

    @RequestMapping(value = "groupSms/groupSendSms")
    @ResponseBody
    public ResultData groupSendSms(HttpServletRequest request, HttpServletResponse response)throws Exception{
        ResultData result = new ResultData();
        try {
            String couponRoleId = request.getParameter("couponRoleId");
            String addressId = request.getParameter("addressId");//优惠券名称
            String smsId = request.getParameter("smsId");//模板id
            if (StringUtils.isBlank(couponRoleId) || StringUtils.isBlank(addressId)|| StringUtils.isBlank(smsId)) {
                return getSjResult(result, null, false, "2", null, "参数不完整");
            }
            SessionUser su=getCurrentUser(request);
            List<HsGroupMobile> groupMobiles=hsGroupMobileService.queryList(null);
            HsGroupCouponRole hsGroupCouponRole=hsGroupCouponRoleService.queryById(couponRoleId);
            HsGroupMobileAddress groupMobileAddress=hsGroupMobileAddressService.queryById(addressId);
            HsGroupMobileSms hsGroupMobileSms=hsGroupMobileSmsService.queryById(smsId);

            HsGroupMobileBatchRecord groupMobileBatchRecord=new HsGroupMobileBatchRecord();
            groupMobileBatchRecord.setId(UUID.randomUUID().toString().replace("-", ""));
            groupMobileBatchRecord.setAddressId(groupMobileAddress.getId());
            groupMobileBatchRecord.setSmsId(hsGroupMobileSms.getId());
            groupMobileBatchRecord.setBatchId(SeqUtil.getNext("gs"));
            groupMobileBatchRecord.setCreateUserid(su.getUserId());
            hsGroupMobileBatchRecordService.add(groupMobileBatchRecord);

            GroupMobileExecutor myExecutor = new GroupMobileExecutor();
            myExecutor.fun(su, groupMobiles, hsGroupCouponRole,hsGroupMobileRecordService,groupMobileAddress,
                    hsGroupMobileSms,hsGroupMobileService,groupMobileBatchRecord);

            getSjResult(result, null, true, "0", null, "后台创建中");
        } catch (Exception e) {
            e.printStackTrace();
            getSjResult(result, null, false, "5", null, "系统异常请稍后再试");
        }
        return result;

    }
}
