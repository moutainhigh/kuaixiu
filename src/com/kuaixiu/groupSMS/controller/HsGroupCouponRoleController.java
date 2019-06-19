package com.kuaixiu.groupSMS.controller;

import com.common.base.controller.BaseController;
import com.common.paginate.Page;
import com.kuaixiu.groupSMS.entity.HsGroupCouponRole;
import com.kuaixiu.groupSMS.service.HsGroupCouponRoleService;
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
import java.util.List;
import java.util.UUID;

/**
 * HsGroupCouponRole Controller
 *
 * @CreateDate: 2019-06-19 上午09:25:21
 * @version: V 1.0
 */
@Controller
public class HsGroupCouponRoleController extends BaseController {

    @Autowired
    private HsGroupCouponRoleService hsGroupCouponRoleService;

    /**
     * 跳转加价券规则列表
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "groupSms/couponRoleList")
    public ModelAndView list(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {

        String returnView = "groupSms/couponRoleList";
        return new ModelAndView(returnView);
    }

    /**
     * 加价券规则列表
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "groupSms/couponRoleForPage")
    public void couponRoleForPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Page page = getPageByRequest(request);
        HsGroupCouponRole groupCouponRole = new HsGroupCouponRole();
        groupCouponRole.setPage(page);
        List<HsGroupCouponRole> groupCouponRoles = hsGroupCouponRoleService.queryListForPage(groupCouponRole);
        page.setData(groupCouponRoles);
        this.renderJson(response, page);
    }

    @RequestMapping(value = "groupSms/toAddCouponRole")
    public ModelAndView toAddCouponRole(HttpServletRequest request,
                                        HttpServletResponse response) throws Exception {

        String returnView = "groupSms/createCouponRole";
        return new ModelAndView(returnView);
    }

    /**
     * 创建加价券规则
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/groupSms/addCouponRole")
    @ResponseBody
    public ResultData getCouponIndex(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        try {
            String nameLabel = request.getParameter("nameLabel");
            String couponName = request.getParameter("couponName");//优惠券名称
            String pricingType = request.getParameter("pricingType");//加价类型 1：百分比 2:：固定加价
            String subtractionPrice = request.getParameter("subtractionPrice");//订单满减加金额
            String upperLimit = request.getParameter("upperLimit");//订单金额上限额度
            String addPriceUpper = request.getParameter("addPriceUpper");//加价金额上限
            String price = request.getParameter("price");//优惠券金额
            String description = request.getParameter("ruleDescription");//加价规则描述
            String note = request.getParameter("note");//优惠券备注
            if (StringUtils.isBlank(couponName) || StringUtils.isBlank(price) || StringUtils.isBlank(pricingType)
                    || StringUtils.isBlank(subtractionPrice) || StringUtils.isBlank(description)
                    || StringUtils.isBlank(nameLabel)) {
                return getSjResult(result, null, false, "2", null, "参数不完整");
            }
            if ("1".equals(pricingType)) {
                if (StringUtils.isBlank(addPriceUpper)) {
                    return getSjResult(result, null, false, "2", null, "参数不完整");
                }
            }
            HsGroupCouponRole groupCouponRole = hsGroupCouponRoleService.getDao().queryByNameLabel(nameLabel);
            if (groupCouponRole != null) {
                return getSjResult(result, null, false, "3", null, "规则名字重复");
            }
            HsGroupCouponRole couponRole = new HsGroupCouponRole();
            couponRole.setId(UUID.randomUUID().toString().replace("-", ""));
            couponRole.setNameLabel(nameLabel);
            couponRole.setCouponName(couponName);
            couponRole.setPricingType(Integer.valueOf(pricingType));
            couponRole.setSubtractionPrice(new BigDecimal(subtractionPrice));
            if (StringUtils.isNotBlank(upperLimit)) {
                couponRole.setUpperLimit(new BigDecimal(upperLimit));
            }
            if ("1".equals(pricingType)) {
                couponRole.setAddPriceUpper(new BigDecimal(addPriceUpper));
            } else {
                couponRole.setAddPriceUpper(null);
            }
            couponRole.setCouponPrice(new BigDecimal(price));
            couponRole.setRuleDescription(description);
            couponRole.setNote(note);
            hsGroupCouponRoleService.add(couponRole);

            getSjResult(result, null, true, "0", null, "创建成功");
        } catch (Exception e) {
            e.printStackTrace();
            getSjResult(result, null, false, "5", null, "系统异常请稍后再试");
        }
        return result;
    }

    @RequestMapping(value = "groupSms/toEditCouponRole")
    public ModelAndView toEditCouponRole(HttpServletRequest request,
                                         HttpServletResponse response) throws Exception {
        String hsCouponId = request.getParameter("id");
        HsGroupCouponRole couponRole = hsGroupCouponRoleService.queryById(hsCouponId);
        request.setAttribute("couponRole", couponRole);
        String returnView = "groupSms/editCouponRole";
        return new ModelAndView(returnView);
    }

    @RequestMapping("/groupSms/editCouponRole")
    @ResponseBody
    public ResultData editCouponRole(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        try {
            String hsCouponId = request.getParameter("hsCouponId");
            String nameLabel = request.getParameter("nameLabel");
            String couponName = request.getParameter("couponName");//优惠券名称
            String pricingType = request.getParameter("pricingType");//加价类型 1：百分比 2:：固定加价
            String subtractionPrice = request.getParameter("subtractionPrice");//订单满减加金额
            String upperLimit = request.getParameter("upperLimit");//订单金额上限额度
            String addPriceUpper = request.getParameter("addPriceUpper");//加价金额上限
            String price = request.getParameter("price");//优惠券金额
            String description = request.getParameter("ruleDescription");//加价规则描述
            String note = request.getParameter("note");//优惠券备注
            if (StringUtils.isBlank(couponName) || StringUtils.isBlank(price) || StringUtils.isBlank(pricingType)
                    || StringUtils.isBlank(hsCouponId) || StringUtils.isBlank(subtractionPrice) || StringUtils.isBlank(description)
                    || StringUtils.isBlank(nameLabel)) {
                return getSjResult(result, null, false, "2", null, "参数不完整");
            }
            if ("1".equals(pricingType)) {
                if (StringUtils.isBlank(addPriceUpper)) {
                    return getSjResult(result, null, false, "2", null, "参数不完整");
                }
            }
            HsGroupCouponRole couponRole = hsGroupCouponRoleService.queryById(hsCouponId);
            HsGroupCouponRole groupCouponRole = hsGroupCouponRoleService.getDao().queryByNameLabel(nameLabel);
            if (groupCouponRole != null) {
                if (!couponRole.getNameLabel().equals(groupCouponRole.getNameLabel())) {
                    return getSjResult(result, null, false, "3", null, "规则名字重复");
                }
            }
            couponRole.setNameLabel(nameLabel);
            couponRole.setCouponName(couponName);
            couponRole.setPricingType(Integer.valueOf(pricingType));
            couponRole.setSubtractionPrice(new BigDecimal(subtractionPrice));
            if (StringUtils.isNotBlank(upperLimit)) {
                couponRole.setUpperLimit(new BigDecimal(upperLimit));
            }
            if ("1".equals(pricingType)) {
                couponRole.setAddPriceUpper(new BigDecimal(addPriceUpper));
            } else {
                couponRole.setAddPriceUpper(null);
            }
            couponRole.setCouponPrice(new BigDecimal(price));
            couponRole.setRuleDescription(description);
            couponRole.setNote(note);
            hsGroupCouponRoleService.saveUpdate(couponRole);

            getSjResult(result, null, true, "0", null, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            getSjResult(result, null, false, "5", null, "系统异常请稍后再试");
        }
        return result;
    }

    /**
     * 删除回收加价规则
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/groupSms/delHsCouponRole")
    @ResponseBody
    public ResultData delHsCouponRole(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        try {
            String hsCouponId = request.getParameter("id");

            if (StringUtils.isBlank(hsCouponId)) {
                return getSjResult(result, null, false, "2", null, "参数为空");
            }
            HsGroupCouponRole activityCouponRole = hsGroupCouponRoleService.queryById(hsCouponId);
            if (activityCouponRole == null) {
                return getSjResult(result, null, false, "2", null, "规则不存在");
            }
            hsGroupCouponRoleService.getDao().deleteByIsDel(hsCouponId);
            getSjResult(result, null, true, "0", null, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            getSjResult(result, null, false, "5", null, "系统异常请稍后再试");
        }
        return result;
    }
}
