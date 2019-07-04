package com.kuaixiu.recycle.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.service.BaseService;
import com.common.exception.SystemException;
import com.common.util.NOUtil;
import com.common.wechat.common.util.StringUtils;
import com.kuaixiu.recycle.dao.RecycleCouponMapper;
import com.kuaixiu.recycle.entity.RecycleCoupon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Administrator on 2018/9/13/013.
 */
@Service("RecycleCouponService")
public class RecycleCouponService extends BaseService<RecycleCoupon>{

    @Autowired
    private RecycleCouponMapper mapper;

    @Override
    public RecycleCouponMapper<RecycleCoupon> getDao() {
        return mapper;
    }

    /**
     * 生成新优惠编码
     *
     * @return
     */
    public String createNewCode() {
        String regex=".*[a-zA-Z]+.*";
        // 获取新编码
        String code = NOUtil.getRandomString(6);
        // 检查编码是否存在 且必须包含英文字母
        RecycleCoupon c =this.queryByCode(code);
        if(c==null&&code.matches(regex)){
            return code;
        }else {
            return createNewCode();
        }
    }

    /**
     * 根据优惠券查询
     * @param code
     * @return
     */
    public RecycleCoupon queryByCode(String code){
        return getDao().queryByCode(code);
    }

    /**
     * 根据加价券编id修改记录,改为未使用-
     * @param id
     * @return
     */
    public int updateForNoUse(String id){
        return getDao().updateForNoUse(id);
    }

    /**
     * 根据批次id删除
     * @param recycleCoupon
     * @return
     */
    public int deleteByBatchId(RecycleCoupon recycleCoupon){
        return getDao().deleteByBatchId(recycleCoupon);
    }
    /**
     * 根据id删除
     * @param idStr
     * @return
     */
    public int deleteById(String idStr){
        if (StringUtils.isBlank(idStr)) {
            throw new SystemException("参数为空，无法更新");
        }
        // 处理批量操作
        String[] ids = idStr.split(",");
        for (String id : ids) {
            RecycleCoupon t = getDao().queryById(id);
            t.setIsDel(1);
            getDao().update(t);
        }
        return 1;
    }

    /**
     * 根据优惠券查询
     * @param recycleCoupon
     * @return
     */
    public Integer updateForReceive(RecycleCoupon recycleCoupon){
        return getDao().updateForReceive(recycleCoupon);
    }
    /**
     * 查询列表。可通过手机号查询
     * @param recycleCoupon
     * @return
     */
    public List<RecycleCoupon> queryUnReceive(RecycleCoupon recycleCoupon){
        return getDao().queryUnReceive(recycleCoupon);
    }

    public int updateForUse(RecycleCoupon recycleCoupon){
        return getDao().updateForUse(recycleCoupon);
    }

    /**
     * 查询所有批次
     * @return
     */
    public List<String> queryBybatch(){
        return getDao().queryBybatch();
    }

    /**
     * 查询数量
     * @param recycleCoupon
     * @return
     */
    public Integer queryCount(RecycleCoupon recycleCoupon){
        return getDao().queryCount(recycleCoupon);
    }

    public Integer updateStatusByBatchId(String batchId){
        return getDao().updateStatusByBatchId(batchId);
    }

    /**
     * 根据批次修改
     * @param recycleCoupon
     * @return
     */
    public int updateByBatchId(RecycleCoupon recycleCoupon){
        return getDao().updateByBatchId(recycleCoupon);
    }

    /**
     * 根据优惠码修改
     * @param recycleCoupon
     * @return
     */
    public int couponCodeUpdate(RecycleCoupon recycleCoupon){
        return getDao().couponCodeUpdate(recycleCoupon);
    }


    public void getCouponList(List<RecycleCoupon> recycleCoupons,JSONArray array)throws Exception{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        for (RecycleCoupon recycleCoupon1 : recycleCoupons) {
            JSONObject json = new JSONObject();
            if (sdf.parse(recycleCoupon1.getEndTime()).getTime() - new Date().getTime() < 0) {
                if (recycleCoupon1.getStatus() == 1) {
                    this.updateStatusByBatchId(recycleCoupon1.getBatchId());
                }
                continue;
            }
            json.put("couponCode", recycleCoupon1.getCouponCode());
            json.put("couponName", recycleCoupon1.getCouponName());
            if (recycleCoupon1.getPricingType() == 1) {
                json.put("couponPrice", recycleCoupon1.getStrCouponPrice().stripTrailingZeros().toPlainString() + "%");
            } else if (recycleCoupon1.getPricingType() == 2) {
                json.put("couponPrice", recycleCoupon1.getStrCouponPrice().stripTrailingZeros().toPlainString() + "元");
            }
            json.put("upperLimit", recycleCoupon1.getUpperLimit());
            json.put("subtractionPrice", recycleCoupon1.getSubtraction_price());
            json.put("ruleDescription", recycleCoupon1.getRuleDescription());
            json.put("beginTime", recycleCoupon1.getBeginTime());
            json.put("endTime", recycleCoupon1.getEndTime());
            array.add(json);
        }
    }

    public void getRecycleCoupon(JSONObject jsonResult,RecycleCoupon recycleCoupon1){
        jsonResult.put("id", recycleCoupon1.getId());
        jsonResult.put("batchId", recycleCoupon1.getBatchId());
        jsonResult.put("couponCode", recycleCoupon1.getCouponCode());
        jsonResult.put("couponName", recycleCoupon1.getCouponName());
        jsonResult.put("pricingType", String.valueOf(recycleCoupon1.getPricingType()));
        jsonResult.put("ruleDescription", recycleCoupon1.getRuleDescription());
        jsonResult.put("subtractionPrice", recycleCoupon1.getSubtraction_price().toString());
        if (recycleCoupon1.getPricingType() == 1) {
            jsonResult.put("couponPrice", recycleCoupon1.getStrCouponPrice().stripTrailingZeros().toPlainString() + "%");
        } else {
            jsonResult.put("couponPrice", recycleCoupon1.getStrCouponPrice().stripTrailingZeros().toPlainString());
        }

        jsonResult.put("beginTime", recycleCoupon1.getBeginTime());
        jsonResult.put("endTime", recycleCoupon1.getEndTime());
        jsonResult.put("receiveMobile", recycleCoupon1.getReceiveMobile());
    }
}
