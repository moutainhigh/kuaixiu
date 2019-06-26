package com.kuaixiu.groupShortUrl.controller;

import com.common.base.controller.BaseController;
import com.common.paginate.Page;
import com.kuaixiu.groupSMS.entity.HsGroupMobileAddress;
import com.kuaixiu.groupShortUrl.entity.HsGroupShortUrlAddress;
import com.kuaixiu.groupShortUrl.entity.HsGroupShortUrlMobile;
import com.kuaixiu.groupShortUrl.service.HsGroupShortUrlAddressService;
import com.system.api.entity.ResultData;
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
 * HsGroupShortUrlAddress Controller
 *
 * @CreateDate: 2019-06-26 上午09:26:11
 * @version: V 1.0
 */
@Controller
public class HsGroupShortUrlAddressController extends BaseController {

    @Autowired
    private HsGroupShortUrlAddressService hsGroupShortUrlAddressService;

    @RequestMapping(value = "groupShort/toAddressList")
    public ModelAndView toAddressList(HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {

        String returnView = "groupShort/addressList";
        return new ModelAndView(returnView);
    }

    @RequestMapping(value = "groupShort/addressListForPage")
    public void addressListForPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Page page = getPageByRequest(request);
        HsGroupShortUrlAddress groupMobileAddress = new HsGroupShortUrlAddress();
        groupMobileAddress.setPage(page);
        List<HsGroupShortUrlAddress> groupMobileAddresses = hsGroupShortUrlAddressService.queryListForPage(groupMobileAddress);
        page.setData(groupMobileAddresses);
        this.renderJson(response, page);
    }

    @RequestMapping(value = "groupShort/toAddAddress")
    public ModelAndView toAddAddress(HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {

        String returnView = "groupShort/createAddress";
        return new ModelAndView(returnView);
    }

    @RequestMapping("/groupShort/addAddress")
    @ResponseBody
    public ResultData addAddress(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        try {
            String nameLabel = request.getParameter("nameLabel");
            String address = request.getParameter("address");//优惠券名称
            if (StringUtils.isBlank(address) || StringUtils.isBlank(nameLabel)) {
                return getSjResult(result, null, false, "2", null, "参数不完整");
            }
            HsGroupShortUrlAddress groupMobileAddress = hsGroupShortUrlAddressService.getDao().queryByNameLabel(nameLabel);
            if (groupMobileAddress != null) {
                return getSjResult(result, null, false, "3", null, "地址名字重复");
            }
            HsGroupShortUrlAddress mobileAddress = new HsGroupShortUrlAddress();
            mobileAddress.setId(UUID.randomUUID().toString().replace("-", ""));
            mobileAddress.setNameLabel(nameLabel);
            mobileAddress.setAddress(address);
            hsGroupShortUrlAddressService.add(mobileAddress);

            getSjResult(result, null, true, "0", null, "创建成功");
        } catch (Exception e) {
            e.printStackTrace();
            getSjResult(result, null, false, "5", null, "系统异常请稍后再试");
        }
        return result;
    }

    @RequestMapping(value = "groupShort/toEditAddress")
    public ModelAndView toEditCouponRole(HttpServletRequest request,
                                         HttpServletResponse response) throws Exception {
        String id = request.getParameter("id");
        HsGroupShortUrlAddress groupMobileAddress = hsGroupShortUrlAddressService.queryById(id);
        request.setAttribute("groupMobileAddress", groupMobileAddress);
        String returnView = "groupShort/editAddress";
        return new ModelAndView(returnView);
    }

    @RequestMapping("/groupShort/editAddress")
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
            HsGroupShortUrlAddress mobileAddress = hsGroupShortUrlAddressService.queryById(id);
            HsGroupShortUrlAddress groupMobileAddress = hsGroupShortUrlAddressService.getDao().queryByNameLabel(nameLabel);
            if (groupMobileAddress != null) {
                if (!mobileAddress.getNameLabel().equals(groupMobileAddress.getNameLabel())) {
                    return getSjResult(result, null, false, "3", null, "规则名字重复");
                }
            }
            mobileAddress.setNameLabel(nameLabel);
            mobileAddress.setAddress(address);
            hsGroupShortUrlAddressService.saveUpdate(mobileAddress);

            getSjResult(result, null, true, "0", null, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            getSjResult(result, null, false, "5", null, "系统异常请稍后再试");
        }
        return result;
    }

    @RequestMapping("/groupShort/delAddress")
    @ResponseBody
    public ResultData delAddress(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        try {
            String id = request.getParameter("id");

            if (StringUtils.isBlank(id)) {
                return getSjResult(result, null, false, "2", null, "参数为空");
            }
            HsGroupShortUrlAddress mobileAddress = hsGroupShortUrlAddressService.queryById(id);
            if (mobileAddress == null) {
                return getSjResult(result, null, false, "2", null, "规则不存在");
            }
            hsGroupShortUrlAddressService.getDao().deleteByIsDel(id);
            getSjResult(result, null, true, "0", null, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            getSjResult(result, null, false, "5", null, "系统异常请稍后再试");
        }
        return result;
    }
}
