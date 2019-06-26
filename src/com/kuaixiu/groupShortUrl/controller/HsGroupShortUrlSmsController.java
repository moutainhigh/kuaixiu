package com.kuaixiu.groupShortUrl.controller;

import com.common.base.controller.BaseController;
import com.common.paginate.Page;
import com.kuaixiu.groupSMS.entity.HsGroupMobileSms;
import com.kuaixiu.groupShortUrl.entity.HsGroupShortUrlSms;
import com.kuaixiu.groupShortUrl.service.HsGroupShortUrlSmsService;
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
 * HsGroupShortUrlSms Controller
 *
 * @CreateDate: 2019-06-26 上午09:29:12
 * @version: V 1.0
 */
@Controller
public class HsGroupShortUrlSmsController extends BaseController {

    @Autowired
    private HsGroupShortUrlSmsService hsGroupShortUrlSmsService;

    @RequestMapping(value = "groupShort/toSmsList")
    public ModelAndView toAddressList(HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {

        String returnView = "groupShort/smsList";
        return new ModelAndView(returnView);
    }

    @RequestMapping(value = "groupShort/smsListForPage")
    public void smsListForPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Page page = getPageByRequest(request);
        HsGroupShortUrlSms groupMobileSms = new HsGroupShortUrlSms();
        groupMobileSms.setPage(page);
        List<HsGroupShortUrlSms> hsGroupMobileSms = hsGroupShortUrlSmsService.queryListForPage(groupMobileSms);
        page.setData(hsGroupMobileSms);
        this.renderJson(response, page);
    }

    @RequestMapping(value = "groupShort/toAddSms")
    public ModelAndView toAddAddress(HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {

        String returnView = "groupShort/createSms";
        return new ModelAndView(returnView);
    }

    @RequestMapping("/groupShort/addSms")
    @ResponseBody
    public ResultData addAddress(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        try {
            String nameLabel = request.getParameter("nameLabel");
            String smsTemplate = request.getParameter("smsTemplate");//优惠券名称
            if (StringUtils.isBlank(smsTemplate) || StringUtils.isBlank(nameLabel)) {
                return getSjResult(result, null, false, "2", null, "参数不完整");
            }
            HsGroupShortUrlSms groupMobileSms = hsGroupShortUrlSmsService.getDao().queryByNameLabel(nameLabel);
            if (groupMobileSms != null) {
                return getSjResult(result, null, false, "3", null, "地址名字重复");
            }
            HsGroupShortUrlSms groupMobileSms1 = new HsGroupShortUrlSms();
            groupMobileSms1.setId(UUID.randomUUID().toString().replace("-", ""));
            groupMobileSms1.setNameLabel(nameLabel);
            groupMobileSms1.setSmsTemplate(smsTemplate);
            hsGroupShortUrlSmsService.add(groupMobileSms1);

            getSjResult(result, null, true, "0", null, "创建成功");
        } catch (Exception e) {
            e.printStackTrace();
            getSjResult(result, null, false, "5", null, "系统异常请稍后再试");
        }
        return result;
    }

    @RequestMapping(value = "groupShort/toEditSms")
    public ModelAndView toEditCouponRole(HttpServletRequest request,
                                         HttpServletResponse response) throws Exception {
        String id = request.getParameter("id");
        HsGroupShortUrlSms groupMobileSms = hsGroupShortUrlSmsService.queryById(id);
        request.setAttribute("groupMobileSms", groupMobileSms);
        String returnView = "groupShort/editSms";
        return new ModelAndView(returnView);
    }

    @RequestMapping("/groupShort/editSms")
    @ResponseBody
    public ResultData editCouponRole(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        try {
            String id = request.getParameter("id");
            String nameLabel = request.getParameter("nameLabel");
            String smsTemplate = request.getParameter("smsTemplate");//优惠券名称
            if (StringUtils.isBlank(id) || StringUtils.isBlank(smsTemplate) || StringUtils.isBlank(nameLabel)) {
                return getSjResult(result, null, false, "2", null, "参数不完整");
            }
            HsGroupShortUrlSms groupMobileSms = hsGroupShortUrlSmsService.queryById(id);
            HsGroupShortUrlSms hsGroupMobileSms = hsGroupShortUrlSmsService.getDao().queryByNameLabel(nameLabel);
            if (hsGroupMobileSms != null) {
                if (!groupMobileSms.getNameLabel().equals(hsGroupMobileSms.getNameLabel())) {
                    return getSjResult(result, null, false, "3", null, "规则名字重复");
                }
            }
            SessionUser su=getCurrentUser(request);
            groupMobileSms.setNameLabel(nameLabel);
            groupMobileSms.setSmsTemplate(smsTemplate);
            groupMobileSms.setUpdateUserid(su.getUserId());
            hsGroupShortUrlSmsService.saveUpdate(groupMobileSms);

            getSjResult(result, null, true, "0", null, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            getSjResult(result, null, false, "5", null, "系统异常请稍后再试");
        }
        return result;
    }

    @RequestMapping("/groupShort/delSms")
    @ResponseBody
    public ResultData delAddress(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        try {
            String id = request.getParameter("id");

            if (StringUtils.isBlank(id)) {
                return getSjResult(result, null, false, "2", null, "参数为空");
            }
            HsGroupShortUrlSms groupMobileSms = hsGroupShortUrlSmsService.queryById(id);
            if (groupMobileSms == null) {
                return getSjResult(result, null, false, "2", null, "规则不存在");
            }
            SessionUser su=getCurrentUser(request);
            hsGroupShortUrlSmsService.getDao().deleteByIsDel(id,su.getUserId());
            getSjResult(result, null, true, "0", null, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            getSjResult(result, null, false, "5", null, "系统异常请稍后再试");
        }
        return result;
    }
}
