package com.kuaixiu.groupSMS.controller;

import com.common.base.controller.BaseController;
import com.common.paginate.Page;
import com.kuaixiu.groupSMS.entity.HsGroupCouponRole;
import com.kuaixiu.groupSMS.entity.HsGroupMobile;
import com.kuaixiu.groupSMS.entity.HsGroupMobileAddress;
import com.kuaixiu.groupSMS.entity.HsGroupMobileRecord;
import com.kuaixiu.groupSMS.service.*;
import com.kuaixiu.recycle.entity.RecycleCoupon;
import com.kuaixiu.recycle.service.MyExecutor;
import com.kuaixiu.recycleCoupon.entity.HsActivityCouponRole;
import com.system.api.entity.ResultData;
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

    @RequestMapping(value = "groupSms/toGroupSendSms")
    public ModelAndView toGroupSendSms(HttpServletRequest request,
                                             HttpServletResponse response) throws Exception {
        List<HsGroupCouponRole> groupCouponRoles = hsGroupCouponRoleService.queryList(null);
        request.setAttribute("groupCouponRoles", groupCouponRoles);
        List<HsGroupMobileAddress> groupMobileAddresses = hsGroupMobileAddressService.queryList(null);
        request.setAttribute("groupMobileAddresses", groupMobileAddresses);
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
            if (StringUtils.isBlank(couponRoleId) || StringUtils.isBlank(addressId)) {
                return getSjResult(result, null, false, "2", null, "参数不完整");
            }
            SessionUser su=getCurrentUser(request);
            List<HsGroupMobile> groupMobiles=hsGroupMobileService.queryList(null);
            HsGroupCouponRole hsGroupCouponRole=hsGroupCouponRoleService.queryById(couponRoleId);

            GroupMobileExecutor myExecutor = new GroupMobileExecutor();
            myExecutor.fun(su, groupMobiles, hsGroupCouponRole);
            
            getSjResult(result, null, true, "0", null, "后台创建中");
        } catch (Exception e) {
            e.printStackTrace();
            getSjResult(result, null, false, "5", null, "系统异常请稍后再试");
        }
        return result;

    }
}
