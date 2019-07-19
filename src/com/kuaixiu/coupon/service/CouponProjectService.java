package com.kuaixiu.coupon.service;

import com.common.base.service.BaseService;
import com.kuaixiu.coupon.dao.CouponProjectMapper;
import com.kuaixiu.coupon.entity.Coupon;
import com.kuaixiu.coupon.entity.CouponProject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * CouponProject Service
 * 
 * @CreateDate: 2017-02-21 下午11:53:44
 * @version: V 1.0
 */
@Service("couponProjectService")
public class CouponProjectService extends BaseService<CouponProject> {
	private static final Logger log = Logger.getLogger(CouponProjectService.class);

	@Autowired
	private CouponProjectMapper<CouponProject> mapper;

	public CouponProjectMapper<CouponProject> getDao() {
		return mapper;
	}

	// **********自定义方法***********

	/**
	 * 批量添加故障
	 * 
	 * @param c
	 * @return
	 */
	public int addBatch(Coupon c) {
		return getDao().addBatch(c);
	}

	/**
	 * 根据优惠券批次id批量添加故障
	 * 
	 * @param c
	 * @return
	 */
	public int addByBatchId(Coupon c) {
		return getDao().addByBatchId(c);
	}

	/**
	 * 根据优惠券ID查询
	 * 
	 * @param id
	 * @return
	 */
	public List<CouponProject> queryListByCouponId(String id) {
		CouponProject t = new CouponProject();
		t.setCouponId(id);
		return getDao().queryList2(t);
	}

	/**
	 * 根据优惠券ID删除
	 * 
	 * @param cid
	 * @return
	 */
	public int delByCouponId(String cid) {
		return getDao().delByCouponId(cid);
	}

	/**
	 * 根据优惠券批次删除优惠券支持项目
	 * 
	 * @param cid
	 * @return
	 */
	public int delByBatchId(String batchId) {
		return getDao().delByBatchId(batchId);
	}
}