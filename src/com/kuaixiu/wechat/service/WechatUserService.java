package com.kuaixiu.wechat.service;

import com.common.base.service.BaseService;
import com.common.util.NOUtil;
import com.kuaixiu.brand.entity.Brand;
import com.kuaixiu.brand.service.BrandService;
import com.kuaixiu.coupon.entity.Coupon;
import com.kuaixiu.coupon.service.CouponModelService;
import com.kuaixiu.coupon.service.CouponProjectService;
import com.kuaixiu.coupon.service.CouponService;
import com.kuaixiu.project.entity.Project;
import com.kuaixiu.project.service.ProjectService;
import com.kuaixiu.wechat.dao.WechatUserMapper;
import com.kuaixiu.wechat.entity.WechatUser;
import com.system.constant.SystemConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
* @author: anson
* @CreateDate: 2018年1月4日 上午9:29:30
* @version: V 1.0
* 
*/
@Service("wechatUserService")
public class WechatUserService extends BaseService<WechatUser>{

	@Autowired
	private WechatUserMapper<WechatUser> mapper;
	@Autowired
	private CouponService couponService;
	@Autowired
	private BrandService brandService;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private CouponModelService modelService;
	@Autowired
	private CouponProjectService couponProjectService;

	@Override
	public WechatUserMapper<WechatUser> getDao() {

		return mapper;
	}



	/**
	 * 生成一张维修通用优惠券
	 * @return
	 */
	public String createCoupon(String batchId,String price) {
		String code="";
		List<Brand> brands = brandService.queryList(null);
		List<Project> projects = projectService.queryList(null);
		// 支持品牌
		List<String> addBrands =new ArrayList<String>();
		if (!brands.isEmpty()) {
			for(Brand b : brands){
				addBrands.add(b.getId());
			}
		}
		// 支持故障
		List<String> addProjects =new ArrayList<String>();
		if (!projects.isEmpty()) {
			for(Project p : projects){
				addProjects.add(p.getId());
			}
		}
		try {
			Coupon t = new Coupon();
			// 生成批次ID
			t.setBatchId(batchId);
			t.setCouponPrice(new BigDecimal(price));
			t.setCouponName(SystemConstant.WECHAT_COMMON_NAME);
			Date date = new Date();
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = format.format(date);
			t.setBeginTime(time);

			Calendar c = Calendar.getInstance();
			c.add(Calendar.YEAR, 1);
			t.setEndTime(format.format(c.getTime()));

			t.setBrands(addBrands);
			t.setProjects(addProjects);
			t.setNote("");
			t.setCreateUserid("admin");
			t.setIsDel(0);
			t.setStatus(1);
			t.setIsUse(0);
			t.setIsReceive(1);
			t.setType(0);// 0表示维修优惠卷
			t.setIsBrandCurrency(1);
			t.setIsProjectCurrency(1);
			// 生成优惠码
			code=saveByNewCode(t);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return code;
	}


	/**
	 * 生成一张贴膜优惠券
	 * @param t
	 * @return
	 */
	public String getScreenCode(){
		String code="";
		Coupon cou = new Coupon();
		cou.setBatchId(SystemConstant.WECHAT_SCREEN_BATCHID);
		cou.setCouponName(SystemConstant.WECHAT_SCREEN_NAME);
		cou.setCouponPrice(new BigDecimal(SystemConstant.WECHAT_SCREEN_PRICE));
		cou.setStatus(1);       // 可用
		cou.setIsReceive(1);    // 已领取
		cou.setIsDel(0);        // 未删除
		cou.setType(2);         // 2表示换膜优惠券
		cou.setIsUse(0);
		cou.setCreateUserid("admin");
		try {
			Date date = new Date();
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = format.format(date);
			cou.setBeginTime(time);

			Calendar c = Calendar.getInstance();
			c.add(Calendar.YEAR, 1);
			cou.setEndTime(format.format(c.getTime()));
			code=saveByNewCode(cou);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return code;
	}



	public String saveByNewCode(Coupon t) {
		// 获取新编码
		String code =createNewCode();
		t.setId(UUID.randomUUID().toString());
		t.setCouponCode(code);
		// 保存优惠码
		couponService.add(t);
		// 保存支持品牌
		if (t.getBrands() != null && t.getBrands().size() > 0) {
			modelService.addBatch(t);
		}
		// 保存支持故障
		if (t.getProjects() != null && t.getProjects().size() > 0) {
			couponProjectService.addBatch(t);
		}
		return code;
	}

	/**
	 * 生成新优惠编码
	 *
	 * @return
	 */
	public String createNewCode() {
		String regex=".*[a-zA-Z]+.*";
		// 获取新编码
		String code = NOUtil.getRandomString(5);
		// 检查编码是否存在 且必须包含英文字母
		Coupon c =couponService.queryByCode(code);
		if (c == null&&code.matches(regex)) {
			return code;
		} else {
			return createNewCode();
		}
	}



	

}
