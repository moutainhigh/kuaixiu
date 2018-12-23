package com.kuaixiu.coupon.controller;

import com.common.base.controller.BaseController;
import com.common.exception.SystemException;
import com.common.paginate.Page;
import com.google.common.collect.Maps;
import com.kuaixiu.brand.entity.Brand;
import com.kuaixiu.brand.service.BrandService;
import com.kuaixiu.coupon.entity.Coupon;
import com.kuaixiu.coupon.entity.CouponModel;
import com.kuaixiu.coupon.entity.CouponProject;
import com.kuaixiu.coupon.service.CouponModelService;
import com.kuaixiu.coupon.service.CouponProjectService;
import com.kuaixiu.coupon.service.CouponService;
import com.kuaixiu.coupon.service.CreateCouponThread;
import com.kuaixiu.project.entity.Project;
import com.kuaixiu.project.service.ProjectService;
import com.system.basic.sequence.util.SeqUtil;
import com.system.basic.user.entity.SessionUser;

import jodd.util.StringUtil;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Coupon Controller
 *
 * @CreateDate: 2017-02-19 下午11:43:18
 * @version: V 1.0
 */
@Controller
public class CouponController extends BaseController {

    @Autowired
    private CouponService couponService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private CouponModelService couponModelService;
    @Autowired
    private CouponProjectService couponProjectService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/coupon/list")
    public ModelAndView list(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {

        String returnView = "coupon/list";
        return new ModelAndView(returnView);
    }

    /**
     * queryListForPage
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/coupon/queryListForPage")
    public void queryListForPage(HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        //获取查询条件
        String batchId = request.getParameter("query_batchId");
        String isUse = request.getParameter("query_isUse");
        String isReceive = request.getParameter("query_isReceive");
        String code = request.getParameter("query_code");
        String type = request.getParameter("query_type");   //优惠券类型
        Coupon t = new Coupon();
        t.setCouponCode(code);
        t.setBatchId(batchId);
        if (StringUtils.isNotBlank(isUse)) {
            t.setIsUse(Integer.parseInt(isUse));
        }
        if (StringUtils.isNotBlank(isReceive)) {
            t.setIsReceive(Integer.parseInt(isReceive));
        }
        if (StringUtils.isNoneBlank(type)) {
            t.setType(Integer.parseInt(type));
        }
        t.setIsDel(0);
        Page page = getPageByRequest(request);
        t.setPage(page);
        List<Coupon> list = couponService.queryListForPage(t);

        page.setData(list);
        this.renderJson(response, page);
    }

    /**
     * create
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/coupon/create")
    public ModelAndView add(HttpServletRequest request,
                            HttpServletResponse response) throws Exception {

        //获取登录用户
        SessionUser su = getCurrentUser(request);

        //获取品牌
        Brand b = new Brand();
        b.setIsDel(0);
        List<Brand> brandL = brandService.queryList(b);

        Project p = new Project();
        p.setIsDel(0);
        //获取省份地址
        List<Project> projectL = projectService.queryList(p);

        request.setAttribute("brandL", brandL);
        request.setAttribute("projectL", projectL);
        String returnView = "coupon/createCoupon";
        return new ModelAndView(returnView);
    }

    /**
     * index
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/coupon/save")
    public void save(HttpServletRequest request,
                     HttpServletResponse response) throws Exception {
        SessionUser su = getCurrentUser(request);
        Map<String, Object> resultMap = Maps.newHashMap();
        //优惠卷类型 0表示维修优惠卷  1表示以旧换新优惠卷 2表示换膜优惠券
        String couponType = request.getParameter("chooseCoupon");
        //优惠券名称
        String couponName = request.getParameter("couponName");
        //优惠券金额
        String couponPrice = request.getParameter("couponPrice");
        //优惠券数量
        String count = request.getParameter("count");
        //优惠券有效时间
        String validBeginTime = request.getParameter("validBeginTime");
        String validEndTime = request.getParameter("validEndTime");
        //备注
        String note = request.getParameter("note");

        String checkAllBrand=request.getParameter("checkAllBrand");//品牌是否全选
        String checkAllProject=request.getParameter("checkAllProject");//刮胡子那个是否全选
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9_\u4e00-\u9fa5]+$");
        Matcher isNum = pattern.matcher(couponName);
        if (!isNum.find()) {
            throw new SystemException("只能输入字母，数字，汉字和下划线");
        }
        //支持品牌
        String[] addBrands = request.getParameterValues("addBrand");
        //支持故障
        String[] addProjects = request.getParameterValues("addProject");

        try {
            Coupon t = new Coupon();
            //生成批次ID
            t.setBatchId(SeqUtil.getNext("C"));
            t.setCouponName(couponName);
            if (StringUtil.isNotBlank(couponPrice)) {
                t.setCouponPrice(new BigDecimal(couponPrice));
            } else {
                t.setCouponPrice(new BigDecimal(0));
            }
            t.setBeginTime(validBeginTime);
            t.setEndTime(validEndTime);
            if (addBrands != null) {
                t.setBrands(Arrays.asList(addBrands));
            }
            if (addProjects != null) {
                t.setProjects(Arrays.asList(addProjects));
            }
            t.setNote(note);
            t.setCreateUserid(su.getUserId());
            t.setIsDel(0);
            t.setStatus(1);
            t.setIsUse(0);
            t.setIsReceive(0);
            if ("0".equals(checkAllBrand)) {
                t.setIsBrandCurrency(0);
            } else if ("1".equals(checkAllBrand)) {
                t.setIsBrandCurrency(1);
            }
            if ("0".equals(checkAllProject)) {
                t.setIsProjectCurrency(0);
            } else if ("1".equals(checkAllProject)) {
                t.setIsProjectCurrency(1);
            }
            if (couponType.equals("0")) {
                t.setType(0);//0表示维修优惠卷
            }
            if (couponType.equals("1")) {
                t.setType(1);//1表示以旧换新优惠卷
            }
            if (couponType.equals("2")) {
                t.setType(2);//2表示换膜优惠券
            }

            new CreateCouponThread(Integer.parseInt(count));
            //使用多线程生成优惠码
            CreateCouponThread cct = new CreateCouponThread(t, couponService);
            //运行多线程start()
            threadRun(cct);

            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
            resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
            resultMap.put("batchId", t.getBatchId());
            resultMap.put("count", count);


        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
            resultMap.put(RESULTMAP_KEY_MSG, "保存失败");
        }
        renderJson(response, resultMap);
    }

    private void threadRun(CreateCouponThread cct){
        Thread t1=new Thread(cct);
        Thread t2=new Thread(cct);
        Thread t3=new Thread(cct);
        Thread t4=new Thread(cct);
        Thread t5=new Thread(cct);
        Thread t6=new Thread(cct);
        Thread t7=new Thread(cct);
        Thread t8=new Thread(cct);
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();
        t6.start();
        t7.start();
        t8.start();
    }

    /**
     * reflashCount
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/coupon/reflashCount")
    public void reflashCount(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {
        SessionUser su = getCurrentUser(request);

        Map<String, Object> resultMap = Maps.newHashMap();
        //批次ID
        String batchId = request.getParameter("batchId");

        try {
            Coupon t = new Coupon();
            t.setBatchId(batchId);
            int count = couponService.queryCount(t);
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
            resultMap.put(RESULTMAP_KEY_MSG, "加载成功");
            resultMap.put("count", count);
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
            resultMap.put(RESULTMAP_KEY_MSG, "加载失败");
        }
        renderJson(response, resultMap);
    }

    /**
     * detail
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/coupon/detail")
    public ModelAndView detail(HttpServletRequest request,
                               HttpServletResponse response) throws Exception {

        //获取网点id
        String id = request.getParameter("id");
        //获取网点id
        String code = request.getParameter("code");
        //查询网点信息
        Coupon t = couponService.queryById(id);
        if (t == null) {
            t = couponService.queryByCode(code);
        }

        request.setAttribute("coupon", t);
        String returnView = "coupon/detail";
        return new ModelAndView(returnView);
    }

    /**
     * edit
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/coupon/edit")
    public ModelAndView edit(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {

        //获取登录用户
        SessionUser su = getCurrentUser(request);

        String id = request.getParameter("id");
        Coupon c = couponService.queryById(id);

        List<CouponModel> couModels = couponModelService.queryListByCouponId(c.getId());
        List<CouponProject> couProjects = couponProjectService.queryListByCouponId(c.getId());
        //获取品牌
        Brand b = new Brand();
        b.setIsDel(0);
        List<Brand> brandL = brandService.queryList(b);

        Project p = new Project();
        p.setIsDel(0);
        //获取省份地址
        List<Project> projectL = projectService.queryList(p);

        request.setAttribute("brandL", brandL);
        request.setAttribute("projectL", projectL);
        request.setAttribute("couModels", couModels);
        request.setAttribute("couProjects", couProjects);
        request.setAttribute("coupon", c);

        String returnView = "coupon/editCoupon";
        return new ModelAndView(returnView);
    }

    /**
     * update
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/coupon/update")
    public void update(HttpServletRequest request,
                       HttpServletResponse response) throws Exception {

        Map<String, Object> resultMap = Maps.newHashMap();
        SessionUser su = getCurrentUser(request);
        //优惠券ID
        String id = request.getParameter("id");
        //优惠券名称
        String couponName = request.getParameter("couponName");
        //优惠券金额
        String couponPrice = request.getParameter("couponPrice");
        //优惠券有效时间
        String validBeginTime = request.getParameter("validBeginTime");
        String validEndTime = request.getParameter("validEndTime");
        //支持品牌
        String[] addBrands = request.getParameterValues("addBrand");
        //支持故障
        String[] addProjects = request.getParameterValues("addProject");
        //备注
        String note = request.getParameter("note");
        String checkAllBrand = request.getParameter("checkAllBrand");//品牌是否全选
        String checkAllProject = request.getParameter("checkAllProject");//刮胡子那个是否全选

        try {
            Coupon t = new Coupon();
            t.setId(id);
            t.setCouponName(couponName);
            t.setCouponPrice(new BigDecimal(couponPrice));
            t.setBeginTime(validBeginTime);
            t.setEndTime(validEndTime);
            if (addBrands != null) {
                t.setBrands(Arrays.asList(addBrands));
            }
            if (addProjects != null) {
                t.setProjects(Arrays.asList(addProjects));
            }
            t.setNote(note);
            t.setUpdateUserid(su.getUserId());

            if ("0".equals(checkAllBrand)) {
                t.setIsBrandCurrency(0);
            } else if ("1".equals(checkAllBrand)) {
                t.setIsBrandCurrency(1);
            }
            if ("0".equals(checkAllProject)) {
                t.setIsProjectCurrency(0);
            } else if ("1".equals(checkAllProject)) {
                t.setIsProjectCurrency(1);
            }

            couponService.update(t, su);
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
            resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
            resultMap.put(RESULTMAP_KEY_MSG, "保存失败");
        }
        renderJson(response, resultMap);
    }


    /**
     * 按批次更新
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/coupon/updateByBatchId")
    public void updateByBatchId(HttpServletRequest request,
                                HttpServletResponse response) throws Exception {

        Map<String, Object> resultMap = Maps.newHashMap();
        SessionUser su = getCurrentUser(request);
        //优惠券ID
        String id = request.getParameter("id");
        //优惠券批次
        String batchId = request.getParameter("BatchId");
        //优惠券名称
        String couponName = request.getParameter("couponName");
        //优惠券金额
        String couponPrice = request.getParameter("couponPrice");
        //优惠券有效时间
        String validBeginTime = request.getParameter("validBeginTime");
        String validEndTime = request.getParameter("validEndTime");
        //支持品牌
        String[] addBrands = request.getParameterValues("addBrand");
        //支持故障
        String[] addProjects = request.getParameterValues("addProject");
        //备注
        String note = request.getParameter("note");
        String checkAllBrand = request.getParameter("checkAllBrand");//品牌是否全选
        String checkAllProject = request.getParameter("checkAllProject");//刮胡子那个是否全选

        try {
            Coupon t = new Coupon();
            t.setId(id);
            t.setBatchId(batchId);
            t.setCouponName(couponName);
            t.setCouponPrice(new BigDecimal(couponPrice));
            t.setBeginTime(validBeginTime);
            t.setEndTime(validEndTime);
            if (addBrands != null) {
                t.setBrands(Arrays.asList(addBrands));
            }
            if (addProjects != null) {
                t.setProjects(Arrays.asList(addProjects));
            }
            t.setNote(note);
            t.setCreateUserid(su.getUserId());

            if ("0".equals(checkAllBrand)) {
                t.setIsBrandCurrency(0);
            } else if ("1".equals(checkAllBrand)) {
                t.setIsBrandCurrency(1);
            }
            if ("0".equals(checkAllProject)) {
                t.setIsProjectCurrency(0);
            } else if ("1".equals(checkAllProject)) {
                t.setIsProjectCurrency(1);
            }

            couponService.updateByBatchId(t, su);
            //使用单线程生成优惠码
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
            resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
            resultMap.put(RESULTMAP_KEY_MSG, "保存失败");
        }
        renderJson(response, resultMap);
    }

    /**
     * delete
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/coupon/delete")
    public void delete(HttpServletRequest request,
                       HttpServletResponse response) throws Exception {

        Map<String, Object> resultMap = Maps.newHashMap();
        SessionUser su = getCurrentUser(request);
        //获取优惠券id
        String batchId = request.getParameter("batchId");
        if (StringUtils.isNotBlank(batchId)) {
            couponService.deleteByBatchId(batchId, su);
        } else {
            //获取优惠券id
            String id = request.getParameter("id");
            couponService.deleteById(id, su);
        }
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
        resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
        renderJson(response, resultMap);
    }


    /**
     * 通过批次修改
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/coupon/editByBatch")
    public ModelAndView editByBatch(HttpServletRequest request,
                                    HttpServletResponse response) throws Exception {

        //获取登录用户
        SessionUser su = getCurrentUser(request);
        //获取批次id
        String batchId = request.getParameter("batchId");
        if (StringUtil.isBlank(batchId)) {
            throw new SystemException("请求参数为空");
        }
        batchId = batchId.toUpperCase();
        Coupon coupon = new Coupon();
        coupon.setBatchId(batchId);
        coupon.setIsDel(0);
        coupon.setIsUse(0);
        //判断该批次是否存在
        List<Coupon> list = couponService.queryListForPage(coupon);
        if (list.size() == 0) {
            throw new SystemException("该批次不存在");
        }
        //得到该批次中一张优惠券信息
        Coupon c = list.get(0);
        //查询该批次支持的品牌和项目
        List<CouponModel> couModels = couponModelService.queryListByCouponId(c.getId());
        List<CouponProject> couProjects = couponProjectService.queryListByCouponId(c.getId());
        //获取品牌
        Brand b = new Brand();
        b.setIsDel(0);
        List<Brand> brandL = brandService.queryList(b);

        Project p = new Project();
        p.setIsDel(0);
        List<Project> projectL = projectService.queryList(p);

        request.setAttribute("brandL", brandL);
        request.setAttribute("projectL", projectL);
        request.setAttribute("couModels", couModels);
        request.setAttribute("couProjects", couProjects);
        request.setAttribute("coupon", c);
        request.setAttribute("batchId", batchId);
        String returnView = "coupon/editCouponByBatch";
        return new ModelAndView(returnView);
    }

    /**
     * importIndex
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/coupon/importIndex")
    public ModelAndView importIndex(HttpServletRequest request,
                                    HttpServletResponse response) throws Exception {

        String returnView = "coupon/importIndex";
        return new ModelAndView(returnView);
    }

}
