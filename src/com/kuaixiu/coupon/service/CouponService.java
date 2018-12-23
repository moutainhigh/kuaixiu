package com.kuaixiu.coupon.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.common.base.service.BaseService;
import com.common.exception.SystemException;
import com.common.util.DateUtil;
import com.common.util.NOUtil;
import com.common.util.SmsSendUtil;
import com.google.common.collect.Maps;
import com.kuaixiu.coupon.dao.CouponMapper;
import com.kuaixiu.coupon.entity.Coupon;
import com.kuaixiu.coupon.entity.CouponModel;
import com.kuaixiu.coupon.entity.CouponProject;
import com.system.basic.user.entity.SessionUser;

import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.XLSTransformer;

/**
 * Coupon Service
 *
 * @CreateDate: 2017-02-19 下午11:43:18
 * @version: V 1.0
 */
@Service("couponService")
public class CouponService extends BaseService<Coupon> {
    private static final Logger log = Logger.getLogger(CouponService.class);

    @Autowired
    private CouponMapper<Coupon> mapper;
    @Autowired
    private CouponModelService modelService;
    @Autowired
    private CouponProjectService projectService;

    public CouponMapper<Coupon> getDao() {
        return mapper;
    }

    // **********自定义方法***********

    /**
     * 根据编码查询
     *
     * @param code
     * @return
     * @CreateDate: 2016-9-6 下午11:57:44
     */
    public Coupon queryByCode(String code) {
        return getDao().queryByCode(code);
    }

    /**
     * 查询个数
     *
     * @return
     * @CreateDate: 2016-9-4 下午4:21:29
     */
    public int queryCount(Coupon t) {
        return getDao().queryCount(t);
    }

    @Transactional
    public void saveByNewCode(Coupon t) {
        // 获取新编码
        String code = this.createNewCode();
        t.setId(UUID.randomUUID().toString());
        t.setCouponCode(code);
        // 保存优惠码
        this.add(t);
        // 保存支持品牌
        if(t.getIsBrandCurrency()==0){
            if (t.getBrands() != null && t.getBrands().size() > 0) {
                modelService.addBatch(t);
            }
        }
        if(t.getIsProjectCurrency()==0) {
            // 保存支持故障
            if (t.getProjects() != null && t.getProjects().size() > 0) {
                projectService.addBatch(t);
            }
        }
    }

    /**
     * 生成新优惠编码
     *
     * @return
     */
    public String createNewCode() {
        String regex = ".*[a-zA-Z]+.*";
        // 获取新编码
        String code = NOUtil.getRandomString(5);
        // 检查编码是否存在 且必须包含英文字母
        Coupon c = this.queryByCode(code);
        if (c == null && code.matches(regex)) {
            return code;
        } else {
            return createNewCode();
        }
    }

    /**
     * 保存门店商 1、获取坐标 2、获取省份名称首字母 3、生成账号 4、创建登录用户 5、保存
     *
     * @param s
     * @return
     * @CreateDate: 2016-9-4 下午10:51:15
     */
    @Transactional
    public int save(Coupon s, SessionUser su) {

        // if(s.getBrands() != null && s.getBrands().length > 0){
        // Map<String, Object> params = new HashMap<>();
        // params.put("shopCode", code);
        // params.put("brands", s.getBrands());
        // shopModelService.addBatch(params);
        // }

        int rest = getDao().add(s);

        return rest;
    }


    /**
     * 根据批次号批量更新优惠券信息
     */
    public int updateByBatchId(Coupon c) {
        return getDao().updateByBatchId(c);
    }


    /**
     * 更新商店
     *
     * @return
     * @CreateDate: 2016-8-31 下午7:08:33
     */
    @Transactional
    public int update(Coupon s, SessionUser su) {
        if (s == null || StringUtils.isBlank(s.getId())) {
            throw new SystemException("参数为空，无法更新");
        }
        Coupon t = getDao().queryById(s.getId());
        if (t.getIsUse() == 1) {
            throw new SystemException("优惠券已使用，无法修改");
        }
        modelService.delByCouponId(s.getId());
        // 保存支持品牌
        if (s.getBrands() != null && s.getBrands().size() > 0) {
            modelService.addBatch(s);
        }
        projectService.delByCouponId(s.getId());
        // 保存支持故障
        if (s.getProjects() != null && s.getProjects().size() > 0) {
            projectService.addBatch(s);
        }
        t.setCouponName(s.getCouponName());
        t.setCouponPrice(s.getCouponPrice());
        t.setBeginTime(s.getBeginTime());
        t.setEndTime(s.getEndTime());
        t.setNote(s.getNote());
        t.setIsProjectCurrency(s.getIsProjectCurrency());
        t.setIsBrandCurrency(s.getIsBrandCurrency());
        return getDao().update(t);
    }

    /**
     * 优惠券批量更新
     *
     * @param s
     * @param su
     * @return
     */
    @Transactional
    public int updateByBatchId(Coupon s, SessionUser su) {

        long now = System.currentTimeMillis();
        if (s == null || StringUtils.isBlank(s.getBatchId())) {
            throw new SystemException("参数为空，无法更新");
        }
        //查询该批次优惠券信息
        Coupon t = getDao().queryById(s.getId());
        if (t == null) {
            throw new SystemException("参数为空，无法更新");
        }
        if (s.getIsBrandCurrency() == 0) {
            CouponModel model = new CouponModel();
            model.setCouponId(t.getId());
            List<CouponModel> modelList = modelService.queryList(model);
            //判断支持的品牌是否有变动 如果有变动则更改
            if (s.getBrands() != null && modelList.size() == s.getBrands().size()) {
                List<String> modelCoupons = new ArrayList<String>();
                if (modelList.size() > 0) {
                    for (CouponModel m : modelList) {
                        modelCoupons.add(m.getBrandId());
                    }
                }
                boolean tip = ListIsEqual(modelCoupons, s.getBrands());
                if (!tip) {
                    // 根据批次删除原先支持的品牌
                    modelService.delByCouponBatchId(s.getBatchId());
                    // 保存该批次支持品牌
                    if (s.getBrands() != null && s.getBrands().size() > 0) {
                        for (String bd : s.getBrands()) {
                            s.setId(bd);
                            modelService.addByBatchId(s);
                        }
                    }
                }
                System.out.println("品牌未更改");
            } else {
                // 根据批次删除原先支持的品牌
                modelService.delByCouponBatchId(s.getBatchId());
                // 保存该批次支持品牌
                if (s.getBrands() != null && s.getBrands().size() > 0) {
                    for (String bd : s.getBrands()) {
                        s.setId(bd);
                        modelService.addByBatchId(s);
                    }
                }
            }
        }


        if (s.getIsProjectCurrency() == 0) {
            //判断支持的项目是否有变动
            CouponProject j = new CouponProject();
            j.setCouponId(t.getId());
            List<CouponProject> projects = projectService.queryList(j);
            if (s.getProjects() != null && projects.size() == s.getProjects().size()) {
                List<String> projectCoupons = new ArrayList<String>();
                if (projects.size() > 0) {
                    for (CouponProject m : projects) {
                        projectCoupons.add(m.getProjectId());
                    }
                }
                boolean tip = ListIsEqual(projectCoupons, s.getProjects());
                if (!tip) {
                    //根据批次id删除原先支持的项目
                    projectService.delByBatchId(s.getBatchId());
                    // 保存该批次支持故障
                    if (s.getProjects() != null && s.getProjects().size() > 0) {
                        for (String pj : s.getProjects()) {
                            s.setId(pj);
                            projectService.addByBatchId(s);
                        }
                    }
                }
                System.out.println("项目未更改");
            } else {
                //根据批次id删除原先支持的项目
                projectService.delByBatchId(s.getBatchId());
                // 保存该批次支持故障
                if (s.getProjects() != null && s.getProjects().size() > 0) {
                    for (String pj : s.getProjects()) {
                        s.setId(pj);
                        projectService.addByBatchId(s);
                    }
                }
            }
        }

        // 根据批次更新原先优惠卷信息
        // 判断以下信息是否有更改 如果更改则更新优惠券的信息
        if (t.getCouponName().equals(s.getCouponName()) && t.getCouponPrice().equals(s.getCouponPrice())
                && t.getBeginTime().equals(s.getBeginTime()) && t.getEndTime().equals(s.getEndTime())
                && t.getNote().equals(s.getNote()) && t.getIsProjectCurrency().equals(s.getIsBrandCurrency())
                && t.getIsBrandCurrency().equals(s.getIsBrandCurrency())) {
            System.out.println("其他信息无需更新");
        } else {
            s.setCouponName(s.getCouponName());
            s.setCouponPrice(s.getCouponPrice());
            s.setBeginTime(s.getBeginTime());
            s.setEndTime(s.getEndTime());
            s.setNote(s.getNote());
            s.setUpdateUserid(su.getUserId());
            return getDao().updateByBatchId(s);
        }
        System.out.println("更新耗时：" + (System.currentTimeMillis() - now));
        return 1;
    }

    /**
     * 修改优惠券已经使用
     *
     * @param c
     * @return
     */
    public int updateForUse(Coupon c) {
        return getDao().updateForUse(c);
    }

    /**
     * 修改优惠券已经领用
     *
     * @param batchId
     * @return
     */
    @Transactional
    public Coupon checkReceiveCoupon(String batchId, String mobile) {
        Coupon t = new Coupon();
        t.setBatchId(batchId);
        t.setEndTime(DateUtil.getNowyyyyMMdd());
        t.setIsUse(0);
        t.setIsDel(0);
        // 检查是否有未使用的优惠券
        t.setIsReceive(1);
        t.setReceiveMobile(mobile);
        return getDao().queryUnReceive(t);
    }

    /**
     * 修改优惠券已经领用
     *
     * @param batchId
     * @return
     */
    @Transactional
    public Coupon updateForReceive(String batchId, String mobile) {
        Coupon t = new Coupon();
        t.setBatchId(batchId);
        t.setEndTime(DateUtil.getNowyyyyMMdd());
        t.setIsUse(0);
        t.setIsDel(0);
        // 根据批次获取优惠券
        t.setIsReceive(0);
        Coupon c = getDao().queryUnReceive(t);
        if (c == null) {
            throw new SystemException("亲，您来晚了，优惠券已全部领用完毕，敬请关注其它活动！");
        }
        // 更新优惠券领用状态
        c.setReceiveMobile(mobile);
        int rest = getDao().updateForReceive(c);
        if (rest == 0) {
            // 优惠券领用失败重新获取优惠券
            c = getDao().queryUnReceive(t);
            if (c == null) {
                throw new SystemException("亲，您来晚了，优惠券已全部领用完毕，敬请关注其它活动！");
            }
            // 更新优惠券领用状态
            c.setReceiveMobile(mobile);
            rest = getDao().updateForReceive(c);
        }
        if (rest == 0) {
            // 优惠券领用失败重新获取优惠券
            c = getDao().queryUnReceive(t);
            if (c == null) {
                throw new SystemException("亲，您来晚了，优惠券已全部领用完毕，敬请关注其它活动！");
            }
            // 更新优惠券领用状态
            c.setReceiveMobile(mobile);
            rest = getDao().updateForReceive(c);
        }
        if (rest == 0) {
            throw new SystemException("亲，由于此优惠券过于火热，领用失败，请稍后重试！");
        }
        return c;
    }

    /**
     * 领用优惠券发送短信
     *
     * @param c
     * @return
     */
    public void receiveSendSms(Coupon c) {
        try {
            List<CouponModel> couModels = modelService.queryListByCouponId(c.getId());
            List<CouponProject> couProjects = projectService.queryListByCouponId(c.getId());
            SmsSendUtil.sendSmsForCoupon(c, couModels, couProjects);
        } catch (SystemException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除连接商
     *
     * @param s
     * @return
     * @CreateDate: 2016-8-31 下午7:09:59
     */
    public int delete(Coupon s) {
        if (s == null || StringUtils.isBlank(s.getId())) {
            throw new SystemException("参数为空，无法更新");
        }
        Coupon t = getDao().queryById(s.getId());
        if (t.getIsUse() == 1) {
            throw new SystemException("优惠券已使用，无法删除");
        }
        t.setIsDel(1);
        t.setUpdateUserid(s.getUpdateUserid());
        return getDao().update(t);
    }

    /**
     * 删除优惠券
     *
     * @param idStr
     * @return
     * @CreateDate: 2016-8-31 下午7:09:59
     */
    public int deleteById(String idStr, SessionUser su) {
        if (StringUtils.isBlank(idStr)) {
            throw new SystemException("参数为空，无法更新");
        }
        // 处理批量操作
        String[] ids = idStr.split(",");
        for (String id : ids) {
            Coupon t = getDao().queryById(id);
            t.setIsDel(1);
            t.setUpdateUserid(su.getUserId());
            getDao().update(t);
        }
        return 1;
    }

    /**
     * 删除优惠券
     *
     * @param batchId
     * @return
     * @CreateDate: 2016-8-31 下午7:09:59
     */
    public int deleteByBatchId(String batchId, SessionUser su) {
        if (StringUtils.isBlank(batchId)) {
            throw new SystemException("参数为空，无法更新");
        }
        // 处理批量操作
        Coupon t = new Coupon();
        t.setBatchId(batchId);
        t.setIsDel(1);
        t.setUpdateUserid(su.getUserId());
        return getDao().deleteByBatchId(t);
    }

    /**
     * 已Excel形式导出列表数据
     *
     * @param params
     */
    @SuppressWarnings("rawtypes")
    public void expDataExcel(Map<String, Object> params) {
        String templateFileName = params.get("tempFileName") + "";
        String destFileName = params.get("outFileName") + "";

        String batchId = MapUtils.getString(params, "query_batchId");
        String isUse = MapUtils.getString(params, "query_isUse");
        String isReceive = MapUtils.getString(params, "query_isReceive");
        String code = MapUtils.getString(params, "query_code");
        Coupon t = new Coupon();
        t.setCouponCode(code);
        t.setBatchId(batchId);

        String idStr = MapUtils.getString(params, "ids");
        if (StringUtils.isNotBlank(idStr)) {
            String[] ids = StringUtils.split(idStr, ",");
            t.setQueryIds(Arrays.asList(ids));
        }
        if (StringUtils.isNotBlank(isUse)) {
            t.setIsUse(Integer.parseInt(isUse));
        }
        if (StringUtils.isNotBlank(isReceive)) {
            t.setIsReceive(Integer.parseInt(isReceive));
        }
        t.setIsDel(0);
        List project = queryList(t);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("dataList", project);

        XLSTransformer transformer = new XLSTransformer();
        try {
            transformer.transformXLS(templateFileName, map, destFileName);
        } catch (ParsePropertyException e) {
            log.error("文件导出--ParsePropertyException", e);
        } catch (InvalidFormatException e) {
            log.error("文件导出--InvalidFormatException", e);
        } catch (IOException e) {
            log.error("文件导出--IOException", e);
        }
    }

    /**
     * 判断两个集合内的元素是否完全相同
     *
     * @return
     */
    public static boolean ListIsEqual(List<String> list1, List<String> list2) {
        boolean tip = false;
        Map<String, Integer> maps = Maps.newHashMap();
        if (list1.size() != list2.size()) {
            tip = false;
        } else {
            for (String str : list1) {
                maps.put(str, 1);
            }
            for (String s : list2) {
                maps.put(s, 1);
            }
            if (maps.size() == list1.size()) {
                tip = true;
            } else {
                tip = false;
            }
        }
        return tip;
    }

}