package com.kuaixiu.recycleCoupon.controller;

import com.common.base.controller.BaseController;
import com.common.exception.SystemException;
import com.common.paginate.Page;
import com.common.util.NOUtil;
import com.kuaixiu.recycle.entity.RecycleSystem;
import com.kuaixiu.recycle.service.RecycleSystemService;
import com.kuaixiu.recycleCoupon.entity.HsActivityCoupon;
import com.kuaixiu.recycleCoupon.entity.HsActivityCouponRole;
import com.kuaixiu.recycleCoupon.service.HsActivityCouponRoleService;
import com.kuaixiu.recycleCoupon.service.HsActivityCouponService;
import com.system.api.entity.ResultData;
import com.system.basic.sequence.util.SeqUtil;
import com.system.constant.SystemConstant;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
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
 * HsActivityCouponRole Controller
 *
 * @CreateDate: 2019-05-30 上午11:27:08
 * @version: V 1.0
 */
@Controller
public class HsActivityCouponRoleController extends BaseController {
    private static final Logger log = Logger.getLogger(HsActivityCouponRoleController.class);

    @Autowired
    private HsActivityCouponRoleService hsActivityCouponRoleService;
    @Autowired
    private HsActivityCouponService hsActivityCouponService;
    @Autowired
    private RecycleSystemService systemService;

    /**
     * 跳转加价券规则列表
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "hsActivity/couponRoleList")
    public ModelAndView list(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {

        String returnView = "hsActivity/couponRoleList";
        return new ModelAndView(returnView);
    }

    /**
     * 加价券规则列表
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "hsActivity/couponRoleForPage")
    public void couponRoleForPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Page page = getPageByRequest(request);
        HsActivityCouponRole couponRole = new HsActivityCouponRole();
        couponRole.setPage(page);
        List<HsActivityCouponRole> activityCouponRoles = hsActivityCouponRoleService.queryListForPage(couponRole);
        for (HsActivityCouponRole activityCouponRole : activityCouponRoles) {
            HsActivityCoupon activityCoupon = hsActivityCouponService.queryById(activityCouponRole.getActivityId());
            if (activityCoupon != null) {
                activityCouponRole.setActivityLabel(activityCoupon.getActivityLabel());
            }
        }
        page.setData(activityCouponRoles);
        this.renderJson(response, page);
    }

    @RequestMapping(value = "hsActivity/toAddCouponRole")
    public ModelAndView toAddCouponRole(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {

        String returnView = "hsActivity/createCouponRole";
        return new ModelAndView(returnView);
    }

    /**
     * 创建加价券规则
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/hsActivity/addCouponRole")
    @ResponseBody
    public ResultData getCouponIndex(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        try {
            String nameLabel = request.getParameter("nameLabel");
            String couponName = request.getParameter("couponName");//优惠券名称
            String pricingType = request.getParameter("pricingType");//加价类型 1：百分比 2:：固定加价
            String subtractionPrice = request.getParameter("subtractionPrice");//满减金额
            String upperLimit = request.getParameter("upperLimit");//订单金额上限额度
            String addPriceUpper = request.getParameter("addPriceUpper");//加价金额上限
            String price = request.getParameter("price");//优惠券金额
            String description = request.getParameter("ruleDescription");//加价规则描述
            String startTime = request.getParameter("validBeginTime");//优惠券开始时间
            String endTime = request.getParameter("validEndTime");//优惠券过期时间
            String activityEndTime = request.getParameter("activityEndTime");//活动结束时间
            String note = request.getParameter("note");//优惠券备注
            if (StringUtils.isBlank(couponName) || StringUtils.isBlank(price) || StringUtils.isBlank(startTime)
                    || StringUtils.isBlank(endTime) || StringUtils.isBlank(pricingType)
                    || StringUtils.isBlank(subtractionPrice) || StringUtils.isBlank(description)) {
                throw new SystemException("参数不完整");
            }
            HsActivityCouponRole couponRole = new HsActivityCouponRole();
            couponRole.setId(UUID.randomUUID().toString().replace("-", ""));
            couponRole.setNameLabel(nameLabel);
            couponRole.setCouponName(couponName);
            couponRole.setPricingType(Integer.valueOf(pricingType));
            couponRole.setSubtractionPrice(new BigDecimal(subtractionPrice));
            couponRole.setUpperLimit(new BigDecimal(upperLimit));
            couponRole.setAddPriceUpper(new BigDecimal(addPriceUpper));
            couponRole.setCouponPrice(new BigDecimal(price));
            couponRole.setRuleDescription(description);
            couponRole.setBeginTime(startTime);
            couponRole.setEndTime(endTime);
            couponRole.setActivityEndTime(activityEndTime);
            couponRole.setNote(note);
            hsActivityCouponRoleService.add(couponRole);

            getSjResult(result, null, true, "0", null, "创建成功");
        } catch (Exception e) {
            e.printStackTrace();
            getSjResult(result, null, false, "5", null, "系统异常请稍后再试");
        }
        return result;
    }

    /**
     * 跳转活动列表
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "hsActivity/activityList")
    public ModelAndView activityList(HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        String returnView = "hsActivity/activityList";
        return new ModelAndView(returnView);
    }
    /**
     * 活动列表
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "hsActivity/activityForPage")
    public void activityForPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Page page = getPageByRequest(request);
        HsActivityCoupon activityCoupon = new HsActivityCoupon();
        activityCoupon.setPage(page);
        List<HsActivityCoupon> activityCoupons = hsActivityCouponService.queryListForPage(activityCoupon);
        for(HsActivityCoupon activityCoupon1:activityCoupons){
            String sourceName= systemService.queryById(activityCoupon1.getSource()).getName();
            activityCoupon1.setSourceName(sourceName);
        }
        page.setData(activityCoupons);
        this.renderJson(response, page);
    }

    /**
     * 跳转添加活动页面
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "hsActivity/toAddActivity")
    public ModelAndView toAddActivity(HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {
        List<RecycleSystem> recycleSystems = systemService.queryList(null);
        List<HsActivityCouponRole> couponRoles = hsActivityCouponRoleService.queryList(null);
        request.setAttribute("recycleSystems", recycleSystems);
        request.setAttribute("couponRoles", couponRoles);
        String returnView = "hsActivity/createHsActivity";
        return new ModelAndView(returnView);
    }

    /**
     * 添加活动
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/hsActivity/addActivity")
    @ResponseBody
    public ResultData addActivity(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        try {
            String source = request.getParameter("source");//来源
            String headHeight = request.getParameter("headHeight");//头图片高度
            String headWide = request.getParameter("headWide");//头图片宽度
            String marginHeight = request.getParameter("marginHeight");//中心图片边框高度
            String marginWide = request.getParameter("marginWide");//中心图片边框宽度
            String centercolorValue = request.getParameter("centercolorValue");//中心图片色值
            String centerHeight = request.getParameter("centerHeight");//中心图片高度
            String centerWide = request.getParameter("centerWide");//中心图片宽度
            String[] activityRoles = request.getParameterValues("activityRoles");//活动规则描述
            String isDefault = request.getParameter("isDefault");//是否默认展示   1是   2否
            String[] couponRoles = request.getParameterValues("couponRoles");//加价券规则id

            if (StringUtils.isBlank(source) || StringUtils.isBlank(headHeight) || StringUtils.isBlank(headWide) ||
                    StringUtils.isBlank(marginHeight) || StringUtils.isBlank(marginWide) || StringUtils.isBlank(centercolorValue) ||
                    StringUtils.isBlank(centerHeight) || StringUtils.isBlank(centerWide) || activityRoles==null ||
                    couponRoles == null) {
                return getSjResult(result, null, false, "2", null, "参数为空");
            }
            //头图
            //获取图片，保存图片到webapp同级inages/activityCoupon目录
            String imageName= NOUtil.getNo("img-")+NOUtil.getRandomInteger(4);
            String savePath = serverPath(request.getServletContext().getRealPath("")) + System.getProperty("file.separator") + SystemConstant.IMAGE_PATH + System.getProperty("file.separator") + "activityCoupon" + System.getProperty("file.separator") + "hd_images";
            String logoPath = getPath(request, "headFile", savePath,imageName);             //图片路径
            String headUrl = getProjectUrl(request) + "/images/activityCoupon/hd_images" + logoPath.substring(logoPath.lastIndexOf("/") + 1);
            System.out.println("图片路径：" + savePath);
            //中心加价券图片
            String imageName2= NOUtil.getNo("img-")+NOUtil.getRandomInteger(4);
            String savePath2 = serverPath(request.getServletContext().getRealPath("")) + System.getProperty("file.separator") + SystemConstant.IMAGE_PATH + System.getProperty("file.separator") + "activityCoupon" + System.getProperty("file.separator") + "cen_images";
            String logoPath2 = getPath(request, "centerFile", savePath,imageName2);             //图片路径
            String centerUrl = getProjectUrl(request) + "/images/activityCoupon/cen_images" + logoPath.substring(logoPath2.lastIndexOf("/") + 1);
            System.out.println("图片路径：" + savePath2);
            //添加活动
            HsActivityCoupon activityCoupon = new HsActivityCoupon();
            activityCoupon.setId(UUID.randomUUID().toString().replace("-", ""));
            activityCoupon.setActivityLabel(SeqUtil.getNext("ayll"));
            activityCoupon.setSource(Integer.valueOf(source));
            activityCoupon.setHeadUrl(headUrl);
            activityCoupon.setHeadHeight(Integer.valueOf(headHeight));
            activityCoupon.setHeadWide(Integer.valueOf(headWide));
            activityCoupon.setMarginHeight(Integer.valueOf(marginHeight));
            activityCoupon.setMarginWide(Integer.valueOf(marginWide));
            activityCoupon.setCenterUrl(centerUrl);
            activityCoupon.setCentercolorValue(centercolorValue);
            activityCoupon.setCenterHeight(Integer.valueOf(centerHeight));
            activityCoupon.setCenterWide(Integer.valueOf(centerWide));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < activityRoles.length; i++) {
                sb.append(activityRoles[i]);
                if (i != activityRoles.length - 1) {
                    sb.append("|");
                }
            }
            activityCoupon.setActivityRole(sb.toString());
            hsActivityCouponService.add(activityCoupon);
            //修改活动默认展示
            if ("1".equals(isDefault)) {
                hsActivityCouponService.updateIsDefault(activityCoupon);
            }
            //活动绑定加价券
            hsActivityCouponService.activityAndCoupon(activityCoupon.getId(), couponRoles);

            getSjResult(result, null, true, "0", null, "创建成功");
        } catch (Exception e) {
            e.printStackTrace();
            getSjResult(result, null, false, "5", null, "系统异常请稍后再试");
        }
        return result;
    }

    /**
     * 修改活动为默认展示状态
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/hsActivity/updateIsDefault")
    @ResponseBody
    public ResultData updateIsDefault(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        try {
            String activityId = request.getParameter("activityId");//来源
            String isDefault = request.getParameter("isDefault");//是否默认展示   1是   2否

            if (StringUtils.isBlank(activityId)) {
                return getSjResult(result, null, false, "2", null, "参数为空");
            }
            HsActivityCoupon activityCoupon = hsActivityCouponService.queryById(activityId);

            //修改活动默认展示
            if ("1".equals(isDefault)) {
                hsActivityCouponService.updateIsDefault(activityCoupon);
            }

            getSjResult(result, null, true, "0", null, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            getSjResult(result, null, false, "5", null, "系统异常请稍后再试");
        }
        return result;
    }
}
