package com.kuaixiu.couponcodepool.controller;

import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.exception.SystemException;
import com.common.paginate.Page;
import com.kuaixiu.couponcodepool.entity.CouponCodePool;
import com.kuaixiu.couponcodepool.service.CouponCodePoolService;
import com.kuaixiu.newtamps.dao.NewTampsMapper;
import com.kuaixiu.newtamps.entity.NewTamps;
import com.kuaixiu.prizechance.service.PrizeChanceService;
import com.kuaixiu.recycle.dao.RecycleCouponMapper;
import com.kuaixiu.recycle.entity.RecycleSystem;
import com.kuaixiu.recycle.service.RecycleSystemService;
import com.system.api.entity.ResultData;
import com.system.basic.user.service.SessionUserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * CouponCodePool Controller
 *
 * @CreateDate: 2019-11-07 上午10:00:26
 * @version: V 1.0
 */
@Controller
public class CouponCodePoolController extends BaseController {

    private static final Logger log = Logger.getLogger(CouponCodePoolController.class);
    @Autowired
    private SessionUserService sessionUserService;

    @Autowired
    private CouponCodePoolService couponCodePoolService;

    @Autowired
    private RecycleCouponMapper recycleCouponMapper;

    @Autowired
    private NewTampsMapper newTampsMapper;

    @Autowired
    private PrizeChanceService prizeChanceService;

    @Autowired
    private RecycleSystemService recycleSystemService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "couponCodePool/getCouponCodePoolList")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="prize/getCouponCodePoolList";
        return new ModelAndView(returnView);
    }

    /**
     * 奖池列表
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/couponCodePool/queryListForPage")
    public void prizeRecordForPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
//        SessionUser su = getCurrentUser(request);
//		if (su.getType() != SystemConstant.USER_TYPE_SYSTEM&&su.getType() != SystemConstant.USER_TYPE_CUSTOMER_SERVICE) {
//			throw new SystemException("对不起，您没有操作权限!");
//		}
        // 获取查询条件
        String mobile=request.getParameter("query_mobile");
        String isDel = request.getParameter("query_isGet");
        String grade = request.getParameter("query_grade");
        String batch = request.getParameter("query_batch");
        CouponCodePool couponCodePool = new CouponCodePool();
        if(!StringUtils.isBlank(isDel)){
            couponCodePool.setIsDel(Integer.parseInt(isDel));
        }
        if(!StringUtils.isBlank(grade)){
            couponCodePool.setGrade(Integer.parseInt(grade));
        }
        if(StringUtils.isNotBlank(batch)){
            couponCodePool.setBatch(batch);
        }
        if(StringUtils.isNotBlank(mobile)){
            couponCodePool.setUserMobile(mobile);
        }
        Page page = getPageByRequest(request);
        couponCodePool.setPage(page);
        List<CouponCodePool> list = couponCodePoolService.queryListForPage(couponCodePool);
        for (CouponCodePool couponCodePool1:  list) {
            if(1==couponCodePool1.getGrade()){
                couponCodePool1.setName("iPhone 11 Pro(256G) 400元直降优惠券");
            }
            if(2==couponCodePool1.getGrade()){
                couponCodePool1.setName("华为p30,500元直降优惠券");
            }
            if(3==couponCodePool1.getGrade()){
                couponCodePool1.setName("三星 A90 5G,400元直降优惠券");
            }
            if(4==couponCodePool1.getGrade()){
                couponCodePool1.setName("20元鱼卡");
            }
            if(5==couponCodePool1.getGrade()){
                couponCodePool1.setName("60元加价券,订单满1000元使用");
            }
            if(6==couponCodePool1.getGrade()){
                couponCodePool1.setName("20元加价券,订单满500元使用");
            }
        }
        page.setData(list);
        this.renderJson(response, page);
    }

    /**
     * 双11活动抽奖
     */
    @RequestMapping(value = "/tmallActivities/getPrize")
    @ResponseBody
    public ResultData getPrize(HttpServletRequest request, HttpServletResponse response ) throws Exception {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        try {
            //获取请求数据
            JSONObject params = getPrarms(request);
            String userMobile = params.getString("userMobile");
            String fm = params.getString("fm");
            //先根据手机号查询抽奖机会
            Integer count = prizeChanceService.queryByMobile(userMobile);
            if(count<1 || Objects.isNull(count)){
                //throw new SystemException("抽奖次数不足");
                getResult(result, jsonResult, false, "1", "请参加活动,增加刮奖机会");
                return result;
            }
            //抽奖
            CouponCodePool couponCodePool = couponCodePoolService.queryByRandom();
            if(Objects.isNull(couponCodePool)|| Objects.isNull(couponCodePool.getGrade())){
                getResult(result, jsonResult, false, "1", "失败");
                return result;
            }
            Integer grede = couponCodePool.getGrade();
            //修改奖池已抽中数据
            String fmName;
            RecycleSystem r = recycleSystemService.queryById(fm);
            if(null == r || StringUtils.isEmpty(r.getName())){
                fmName= "微信公众号";
            }else {
                fmName=r.getName();
            }
            int i = couponCodePoolService.updateDel(couponCodePool.getId(),userMobile,fmName);
            if(i < 1){
                log.error(String.format("修改奖池数据is_del为1时失败,奖券码=%s",couponCodePool.getCouponId()));
                throw new SystemException("修改奖池数据失败");
            }
            //如果抽中的是加价券
            if(4 < grede){
                //给用户添加优惠券(加价券)
                recycleCouponMapper.updateReceiveMobile(userMobile, couponCodePool.getCouponId());
            }else{
                //添加奖品到新卡包
                NewTamps t = new NewTamps();
                t.setId(UUID.randomUUID().toString());
                t.setCouponId(couponCodePool.getCouponId());
                t.setBatchId(couponCodePool.getBatch());
                //奖品等级,用作区分新机券和鱼卡券
                t.setGrade(String.valueOf(grede));
                t.setUserMobile(userMobile);
                t.setIsDel(0);
                newTampsMapper.add(t);
            }
            jsonResult.put("grede", grede);
            jsonResult.put("couponcodepool", couponCodePool);
            getResult(result, jsonResult, true, "0", "成功");
            //逻辑执行完,抽奖机会减1
            prizeChanceService.updateCountByReduce(userMobile);
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        return result;
    }

    /**
     * 中奖信息滚动栏,显示最新的50条
     */
    @RequestMapping(value = "/tmallActivities/getScrollBarPrize")
    @ResponseBody
    public ResultData getScrollBarPrize(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        try {
            List<CouponCodePool> list = couponCodePoolService.queryPrizeByUserMobile();
            for (CouponCodePool oj : list) {
                oj.setUserMobile(oj.getUserMobile().replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2"));
            }
            jsonResult.put("list", list);
            getResult(result, jsonResult, true, "0", "成功");

        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        return result;
    }

}
