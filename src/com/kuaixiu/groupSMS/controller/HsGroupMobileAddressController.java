package com.kuaixiu.groupSMS.controller;

import com.common.base.controller.BaseController;
import com.common.paginate.Page;
import com.kuaixiu.groupSMS.entity.HsGroupCouponRole;
import com.kuaixiu.groupSMS.entity.HsGroupMobileAddress;
import com.kuaixiu.groupSMS.service.HsGroupMobileAddressService;
import com.system.api.entity.ResultData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * HsGroupMobileAddress Controller
 *
 * @CreateDate: 2019-06-19 下午04:31:51
 * @version: V 1.0
 */
@Controller
public class HsGroupMobileAddressController extends BaseController {

    @Autowired
    private HsGroupMobileAddressService hsGroupMobileAddressService;

    @RequestMapping(value = "groupSms/toAddressList")
    public ModelAndView toAddressList(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {

        String returnView = "groupSms/addressList";
        return new ModelAndView(returnView);
    }

    @RequestMapping(value = "groupSms/addressListForPage")
    public void addressListForPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Page page = getPageByRequest(request);
        HsGroupMobileAddress groupMobileAddress = new HsGroupMobileAddress();
        groupMobileAddress.setPage(page);
        List<HsGroupMobileAddress> groupMobileAddresses = hsGroupMobileAddressService.queryListForPage(groupMobileAddress);
        page.setData(groupMobileAddresses);
        this.renderJson(response, page);
    }

    @RequestMapping(value = "groupSms/toAddAddress")
    public ModelAndView toAddAddress(HttpServletRequest request,
                                        HttpServletResponse response) throws Exception {

        String returnView = "groupSms/createAddress";
        return new ModelAndView(returnView);
    }

    @RequestMapping("/groupSms/addAddress")
    @ResponseBody
    public ResultData addAddress(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        try {
            String nameLabel = request.getParameter("nameLabel");
            String address = request.getParameter("address");//优惠券名称
            if (StringUtils.isBlank(address) || StringUtils.isBlank(nameLabel)) {
                return getSjResult(result, null, false, "2", null, "参数不完整");
            }
            HsGroupMobileAddress groupMobileAddress = hsGroupMobileAddressService.getDao().queryByNameLabel(nameLabel);
            if (groupMobileAddress != null) {
                return getSjResult(result, null, false, "3", null, "地址名字重复");
            }
            HsGroupMobileAddress mobileAddress = new HsGroupMobileAddress();
            mobileAddress.setId(UUID.randomUUID().toString().replace("-", ""));
            mobileAddress.setNameLabel(nameLabel);
            mobileAddress.setAddress(address);
            hsGroupMobileAddressService.add(mobileAddress);

            getSjResult(result, null, true, "0", null, "创建成功");
        } catch (Exception e) {
            e.printStackTrace();
            getSjResult(result, null, false, "5", null, "系统异常请稍后再试");
        }
        return result;
    }

    @RequestMapping(value = "groupSms/toEditAddress")
    public ModelAndView toEditCouponRole(HttpServletRequest request,
                                         HttpServletResponse response) throws Exception {
        String id = request.getParameter("id");
        HsGroupMobileAddress groupMobileAddress = hsGroupMobileAddressService.queryById(id);
        request.setAttribute("groupMobileAddress", groupMobileAddress);
        String returnView = "groupSms/editAddress";
        return new ModelAndView(returnView);
    }

    @RequestMapping("/groupSms/editAddress")
    @ResponseBody
    public ResultData editCouponRole(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        try {
            String id = request.getParameter("id");
            String nameLabel = request.getParameter("nameLabel");
            String address = request.getParameter("address");//优惠券名称
            if (StringUtils.isBlank(id) || StringUtils.isBlank(address) || StringUtils.isBlank(nameLabel)) {
                return getSjResult(result, null, false, "2", null, "参数不完整");
            }
            HsGroupMobileAddress mobileAddress = hsGroupMobileAddressService.queryById(id);
            HsGroupMobileAddress groupMobileAddress = hsGroupMobileAddressService.getDao().queryByNameLabel(nameLabel);
            if (groupMobileAddress != null) {
                if (!mobileAddress.getNameLabel().equals(groupMobileAddress.getNameLabel())) {
                    return getSjResult(result, null, false, "3", null, "规则名字重复");
                }
            }
            mobileAddress.setNameLabel(nameLabel);
            mobileAddress.setAddress(address);
            hsGroupMobileAddressService.saveUpdate(mobileAddress);

            getSjResult(result, null, true, "0", null, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            getSjResult(result, null, false, "5", null, "系统异常请稍后再试");
        }
        return result;
    }

    @RequestMapping("/groupSms/delAddress")
    @ResponseBody
    public ResultData delAddress(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        try {
            String id = request.getParameter("id");

            if (StringUtils.isBlank(id)) {
                return getSjResult(result, null, false, "2", null, "参数为空");
            }
            HsGroupMobileAddress mobileAddress = hsGroupMobileAddressService.queryById(id);
            if (mobileAddress == null) {
                return getSjResult(result, null, false, "2", null, "规则不存在");
            }
            hsGroupMobileAddressService.getDao().deleteByIsDel(id);
            getSjResult(result, null, true, "0", null, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            getSjResult(result, null, false, "5", null, "系统异常请稍后再试");
        }
        return result;
    }
}
