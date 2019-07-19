package com.kuaixiu.oldtonew.service;

import com.alibaba.fastjson.JSONObject;
import com.common.base.service.BaseService;
import com.common.exception.ApiServiceException;
import com.common.exception.SystemException;
import com.common.util.BaiduMapUtil;
import com.common.util.DateUtil;
import com.common.util.NOUtil;
import com.common.util.SmsSendUtil;
import com.kuaixiu.brand.service.BrandService;
import com.kuaixiu.brand.service.NewBrandService;
import com.kuaixiu.coupon.entity.Coupon;
import com.kuaixiu.coupon.service.CouponService;
import com.kuaixiu.customer.entity.Customer;
import com.kuaixiu.customer.service.CustomerService;
import com.kuaixiu.engineer.entity.Engineer;
import com.kuaixiu.engineer.service.EngineerService;
import com.kuaixiu.model.service.ModelService;
import com.kuaixiu.model.service.NewModelService;
import com.kuaixiu.model.service.RepairCostService;
import com.kuaixiu.oldtonew.dao.NewOrderMapper;
import com.kuaixiu.oldtonew.entity.*;
import com.kuaixiu.order.constant.OrderConstant;
import com.kuaixiu.order.entity.Order;
import com.kuaixiu.order.service.OrderDetailService;
import com.kuaixiu.shop.entity.Shop;
import com.kuaixiu.shop.service.NewShopModelService;
import com.kuaixiu.shop.service.ShopService;
import com.system.basic.user.entity.SessionUser;
import com.system.constant.ApiResultConstant;
import com.system.constant.SystemConstant;
import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author: anson
 * @CreateDate: 2017年6月15日 下午4:05:13
 * @version: V 1.0
 * 
 */
@Service("newOrderService")
public class NewOrderService extends BaseService {

	private static final Logger log = Logger.getLogger(NewOrderService.class);

	@Autowired
	private NewOrderMapper<NewOrder> mapper;
	@Autowired
	private RepairCostService costService;
	@Autowired
	private CustomerService customerService;
	@Autowired
	private OrderDetailService detailService;
	@Autowired
	private BrandService brandService;
	@Autowired
	private ModelService modelService;
	@Autowired
	private ShopService shopService;
	@Autowired
	private EngineerService engineerService;
	@Autowired
	private NewOrderPayService newOrderPayService;
	@Autowired
	private NewShopModelService newShopModelService;
	@Autowired
	private NewBrandService newBrandService;
	@Autowired
	private NewModelService newModelService;
	@Autowired
	private AgreedService agreedService;
	@Autowired
	private CouponService couponService;
	@Autowired
	private OldToNewService oldToNewService;

	public NewOrderMapper<NewOrder> getDao() {
		return mapper;
	}

	/**
	 * 查询未处理订单
	 * 
	 * @param t
	 * @return
	 * @author: lijx
	 * @CreateDate: 2016-10-10 下午11:38:30
	 */
	public List<Map<String, Object>> queryMapForPage(NewOrder o) {
		return getDao().queryMapForPage(o);
	}

	/**
	 * 根据订单号查询订单
	 * 
	 * @param t
	 */
	public NewOrder queryByOrderNo(String orderNo) {
		return getDao().queryByOrderNo(orderNo);
	}

	/**
	 * 保存订单
	 */
	@Transactional
	public int save(NewOrder o, Customer cust) {
		// 根据客户填写的地址获取用户坐标,精度为百分之30
		JSONObject lal = BaiduMapUtil.getOldToNewLatAndLngByAddr(cust.getFullAddress());
        System.out.println("当前经纬度："+lal);
		// 设置坐标
		cust.setLongitude(new BigDecimal(lal.getFloat("lng")));
		cust.setLatitude(new BigDecimal(lal.getFloat("lat")));

		// 检查附近是否有兑换门店且支持以旧换新功能,目前换新订单 不限制范围
		// if(!shopService.checkShopOldToNew(cust.getLongitude(),
		// cust.getLatitude())){
		// throw new
		// SystemException("亲，您填写的地址附近没有可兑换门店。请到市区下单，或联系服务热线预约0571-88803875。");
		// }

		// 判断是否使用优惠券
		if (StringUtils.isNotBlank(o.getCouponCode())) {
			// 判断优惠券是否存在
			Coupon c = couponService.queryByCode(o.getCouponCode());
			if (c == null) {
				throw new SystemException("亲，您输入的优惠券不存在。请确认优惠券是否正确。");
			}
			// 判断优惠券是否使用
			if (c.getIsUse() != 0) {
				throw new SystemException("亲，您输入的优惠券已使用。请确认优惠券是否正确。");
			}
			if (c.getType() != 1) {
				throw new SystemException("亲，您输入的优惠券不支持以旧换新订单使用");
			}
			// 判断优惠券是否在有效期内
			String nowDay = DateUtil.getNowyyyyMMdd();
//			if (nowDay.compareTo(c.getBeginTime()) < 0) {
//				throw new SystemException("亲，您输入的优惠券还未生效，无法使用。");
//			}
			if (nowDay.compareTo(c.getEndTime()) > 0) {
				throw new SystemException("亲，您输入的优惠券已超过有效期，无法使用。");
			}
			// 修改优惠券改为已使用
			c.setUpdateUserid(cust.getId());
			int rest = couponService.updateForUse(c);
			if (rest == 0) {
				throw new SystemException("亲，您输入的优惠券状态异常，请稍后再试。");
			}
			o.setIsUseCoupon(1);
			o.setCouponId(c.getId());
			o.setCouponName(c.getCouponName());
			o.setCouponType(c.getType());
			o.setCouponPrice(c.getCouponPrice());
		} else {
			o.setIsUseCoupon(0);
		}
		cust.setIsDel(0);
		cust.setStatus(0);
		// 保存用户信息
		cust = customerService.save(cust);
		// 生成订单号
		String orderNo = NOUtil.getNo("PO-");
		o.setOrderNo(orderNo);
		// 生成订单id
		String orderId = UUID.randomUUID().toString().replace("-", "");
		o.setId(orderId);

		// 设置客户信息
		o.setCustomerId(cust.getId());
		// 设置初始值
		o.setIsLock(0);
		o.setSort(0);
		o.setIsDel(1);// 待接单成功再改为0
		o.setBalanceStatus(0);
		o.setCancelType(0);
		o.setCancelStatus(0);
		o.setSendAgreedNews(0);
		// 下单时的金额（含税）=实际金额+退款金额
		BigDecimal orderPrice = new BigDecimal(0);
		o.setOrderPrice(orderPrice);
		o.setRealPrice(orderPrice);
		// 添加支付信息
		NewOrderPay orderPay = new NewOrderPay();
		orderPay.setId(UUID.randomUUID().toString());
		orderPay.setOrderNo(orderNo);
		orderPay.setIsDeposit(1);
		newOrderPayService.addOrderPay(orderPay);
		return getDao().add(o);
	}

	/**
	 * 以旧换新自动配单
	 * 
	 * @CreateDate: 2016-9-15 下午5:48:51
	 */
	@Transactional
	public void autoDispatch(NewOrder o) {
		Customer c = customerService.queryById(o.getCustomerId());
		o.setLatitude(c.getLatitude());
		o.setLongitude(c.getLongitude());
		// 通过用户下单地址坐标查询附近支持以旧换新的门店
		List<Shop> shopL = shopService.queryShopByLonAndLat(o);
		
		if (shopL.size() == 0) {
			// 用户附近没有空闲的兑换门店
			throw new SystemException("对不起，目前没有支持以旧换新的门店，敬请期待");
		}
		// 是否派单成功
		boolean isDispatched = false;
		for (Shop s : shopL) {
			// 判断商品派单模式：自动派单还是手动派单
			if (s.getDispatchType() == 0) {
				// 自动派单则查询空闲的工程师
				Engineer eng = engineerService.queryUnDispatchByShopCode(s.getCode());
				if (eng == null) {
					continue;
				}
				// 自动分配订单给空闲工程师
				haveEng(o, s, eng);
				isDispatched = true;
				break;
			} else if (s.getDispatchType() == 1) {
				// 手动模式下派单
				handMovementDispatch(o, s);
				isDispatched = true;
				break;
			}
		}

		if (!isDispatched) {
			// 如果派单失败则将订单派给最近的维修门店
			Shop s = shopL.get(0);
			o.setProviderCode(s.getProviderCode());
			o.setShopCode(s.getCode());
			o.setIsDispatch(2);
			o.setDispatchTime(new Date());
			o.setIsDel(0);
			getDao().update(o);
			// 给维修门店管理员发送短信提示
			SmsSendUtil.sendSmsToShop(s, o);
		}
	}

	/**
	 * 查该门店未配单的订单
	 * 
	 * @return
	 * @CreateDate: 2016-9-15 下午10:26:11
	 */
	public int queryUnDispatchCount(String shopCode) {
		NewOrder o = new NewOrder();
		// 未派单
		o.setIsDispatch(2);
		// 订单状态已支付保证金
		o.setOrderStatus(2);
		o.setShopCode(shopCode);
		return getDao().queryCount(o);
	}

	/**
	 * 查未配单的订单
	 * 
	 * @return
	 * @CreateDate: 2016-9-15 下午10:26:11
	 */
	public List<NewOrder> queryUnDispatch() {
		NewOrder o = new NewOrder();
		// 未派单
		o.setIsDispatch(0);
		// 订单状态已支付保证金
		o.setOrderStatus(2);
		return getDao().queryList(o);
	}

	/**
	 * 以旧换新重新派单
	 * 
	 * @CreateDate: 2016-9-15 下午5:48:51
	 */
	@Transactional
	public void reDispatch(String id, SessionUser su) {
		// 检查订单状态
		NewOrder o = getDao().queryById(id);
		if (o == null) {
			throw new SystemException("订单不存在！");
		}
		Customer c = customerService.queryById(o.getCustomerId());
		o.setLatitude(c.getLatitude());
		o.setLongitude(c.getLongitude());
		// 查看附近所有支持以旧换新的门店
		List<Shop> shopL = shopService.queryShopByLonAndLat(o);
		if (shopL.size() == 0) {
			throw new SystemException("附近没有维修门店请及时联系客户！");
		}
		// 记录原来的维修门店及维修工程师
		String oldShopCode = o.getShopCode();
		String oldEngineerId = o.getEngineerId();
		Shop oldShop = null;
		// 是否派单成功
		boolean isDispatched = false;
		for (Shop s : shopL) {
			if (s.getCode().equals(oldShopCode)) {
				// 第一次优先考虑其它维修门店
				oldShop = s;
				continue;
			}
			// 判断商品派单模式：自动派单还是手动派单
			if (s.getDispatchType() == 0) {
				// 自动派单则查询空闲的工程师
				Engineer eng = engineerService.queryUnDispatchByShopCode(s.getCode());
				if (eng == null) {
					continue;
				}
				// 自动分配订单给空闲工程师
				haveEng(o, s, eng);
				isDispatched = true;
				break;
			} else if (s.getDispatchType() == 1) {
				// 手动模式下派单
				handMovementDispatch(o, s);
				isDispatched = true;
				break;
			}
		}
		// 如果满足重新派单的门店都没有工程师,则派给除原先的门店外最近的门店
		if (!isDispatched) {
			for (Shop s : shopL) {
				if (s.getCode().equals(oldShopCode)) {
					// 优先考虑其它维修门店
					oldShop = s;
					continue;
				}
				// 判断商品派单模式：自动派单还是手动派单
				if (s.getDispatchType() == 0) {
					// 订单分配给门店(没有工程师分配)
					o.setProviderCode(s.getProviderCode());
					o.setShopCode(s.getCode());
					o.setDispatchTime(new Date());
					o.setEngineerId(null);
					o.setIsDispatch(2);
					o.setOrderStatus(OrderConstant.ORDER_STATUS_DEPOSITED);
					getDao().update(o);
					isDispatched = true;
					break;
				} else if (s.getDispatchType() == 1) {
					handMovementDispatch(o, s);
					isDispatched = true;
					break;
				}
			}
		}
		// 如果满足派单的门店只有原先的那一家
		if (!isDispatched && oldShop != null) {
			// 如果没有派单成功则派单给原维修门店其它维修工程师
			// 自动派单则查询空闲的工程师,
			// 这里目前不用添加其它条件，因为是查询的空闲工程师，原维修工程师状态为非空闲状态
			// 如果改成一人多单则需要排除原维修工程师
			Engineer eng = engineerService.queryUnDispatchByShopCode(oldShop.getCode(), oldEngineerId);
			if (eng != null) {
				// 自动分配订单给空闲工程师
				o.setProviderCode(oldShop.getProviderCode());
				o.setShopCode(oldShop.getCode());
				o.setEngineerId(eng.getId());
				o.setIsDispatch(1);
				o.setOrderStatus(OrderConstant.ORDER_STATUS_DISPATCHED);
				o.setDispatchTime(new Date());
				getDao().update(o);
				// 更改工程师状态
				eng.setIsDispatch(1);
				engineerService.saveUpdate(eng);
				OldToNewUser old = oldToNewService.queryById(o.getUserId());
				// 给工程师发送短信提示
				SmsSendUtil.sendSmsToEngineer(eng, oldShop, o, old);
				isDispatched = true;
			}
		}
		// 再次判断是否派单成功
		if (isDispatched) {
			// 重新派单，如果该顶单已有预约信息则删除该订单预约信息
			Agreed agreed = agreedService.queryByOrderNo(o.getOrderNo());
			if (agreed != null) {
				agreed.setIsDel(1);
				agreedService.updateAgreed(agreed);
			}
			engineerService.checkDispatchState(oldEngineerId);
		} else {
			log.info("附近没有空闲的维修工程师!");
		}
	}

	/**
	 * 门店存在工程师时的订单分配
	 */
	public void haveEng(NewOrder o, Shop s, Engineer eng) {
		// 自动分配订单给空闲工程师
		o.setProviderCode(s.getProviderCode());
		o.setShopCode(s.getCode());
		o.setEngineerId(eng.getId());
		o.setIsDispatch(1);
		o.setDispatchTime(new Date());
		o.setIsDel(0);
		o.setOrderStatus(OrderConstant.ORDER_STATUS_DISPATCHED);
		getDao().update(o);
		// 更改工程师状态
		eng.setIsDispatch(1);
		engineerService.saveUpdate(eng);
		OldToNewUser old = oldToNewService.queryById(o.getUserId());
		// 给工程师发送短信提示
		SmsSendUtil.sendSmsToEngineer(eng, s, o, old);
	}

	/**
	 * 手动派单模式下分配订单
	 */
	public void handMovementDispatch(NewOrder o, Shop s) {
		// 如果是手动派单则判断空闲工程师数量是否大于已分配的订单数
		int unDispatchEngCt = engineerService.queryUnDispatchCount(s.getCode());
		// 查未配单的订单
		int unDispatchOrderCt = queryUnDispatchCount(s.getCode());
		if (unDispatchEngCt > unDispatchOrderCt) {
			// 存在多余的工程师
			// 自动分配订单给维修门店
			o.setProviderCode(s.getProviderCode());
			o.setShopCode(s.getCode());
			o.setIsDispatch(2);
			o.setIsDel(0);
			o.setDispatchTime(new Date());
			getDao().update(o);
			// 给维修门店管理员发送短信提示
			SmsSendUtil.sendSmsToShop(s, o);
		}
	}

	/**
	 * 商超过15分钟没有派单的以旧换新订单
	 */
	public List<NewOrder> queryUnDispatchForShop() throws ParseException {
		NewOrder o = new NewOrder();
		// 未派单
		o.setIsDispatch(2);
		// 订单状态已支付保证金
		o.setOrderStatus(2);
		o.setDispatchTime(DateUtil.getDateAddMinute(-15));
		return getDao().queryList(o);
	}

	/**
	 * 超过多少分钟没有预约的订单
	 */
	public List<NewOrder> queryUnAgreedForShop(int waitTime, int agreedStatus) throws ParseException {
		NewOrder o = new NewOrder();
		// 已分配工程师，未预约
		o.setOrderStatus(OrderConstant.ORDER_STATUS_DISPATCHED);
		o.setSendAgreedNews(agreedStatus);
		o.setDispatchTime(DateUtil.getDateAddMinute(-waitTime));
		return getDao().queryList(o);
	}

	/**
	 * 取消订单
	 */
	@Transactional
	public void orderCancel(String id, int cancelType, String cancelReason, SessionUser su) {
		NewOrder o = getDao().queryById(id);
		if (o == null) {
			throw new SystemException("订单不存在！");
		}
		if (su.getType() != SystemConstant.USER_TYPE_SYSTEM && su.getType() != SystemConstant.USER_TYPE_CUSTOMER_SERVICE
				&& !su.getUserId().equals(o.getCustomerId()) && !su.getUserId().equals(o.getEngineerId())) {
			throw new SystemException("对不起，您无权操作该订单！");
		}
		if (o.getOrderStatus() == OrderConstant.ORDER_STATUS_CANCEL) {
			throw new SystemException("该订单已取消，无需重复操作！");
		}

		if (o.getOrderStatus() == OrderConstant.ORDER_STATUS_FINISHED) {
			throw new SystemException("该订单已完成，不能取消！");
		}

		// 执行取消订单
		// 保存修改前订单状态
		o.setCancelStatus(o.getOrderStatus());
		o.setOrderStatus(OrderConstant.ORDER_STATUS_CANCEL);
		o.setCancelType(cancelType);
		o.setUpdateUserid(su.getUserId());
		o.setEndTime(new Date());
		if (cancelReason != null) {
			o.setCancelReason(cancelReason);
		}
		getDao().update(o);
		// 如果使用了优惠券将优惠券改为可用
		if (StringUtils.isNotBlank(o.getCouponId())) {
			Coupon coupon = couponService.queryById(o.getCouponId());
			coupon.setIsUse(0);
			coupon.setUseTime(null);
			couponService.saveUpdate(coupon);
		}
		Engineer engineer = engineerService.queryById(o.getEngineerId());
		OldToNewUser old = oldToNewService.queryById(o.getUserId());
		// 如果订单已派单则将工程师改为空闲状态
		if (o.getCancelStatus() >= OrderConstant.ORDER_STATUS_DISPATCHED) {
			engineerService.checkDispatchState(o.getEngineerId());
			if (cancelType != OrderConstant.ORDER_CANCEL_TYPE_ENGINEER) {
				// 如果取消人不是工程师取消发送短信
				try {
					SmsSendUtil.sendSmsToEngineerForCancel(engineer.getMobile(), o.getOrderNo());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if (cancelType != OrderConstant.ORDER_CANCEL_TYPE_CUSTOMER) {
			// 如果取消人不是客户取消发送短信
			try {
				SmsSendUtil.sendSmsToCustomerForCancel(old.getTel(), o.getOrderNo());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 安卓端以旧换新取消订单
	 */
	@Transactional
	public void newOrderCancel(String id, int cancelType, String cancelReason, SessionUser su) {
		NewOrder o = getDao().queryById(id);
		if (o == null) {
			throw new SystemException("订单不存在！");
		}
		// 执行取消订单
		// 保存修改前订单状态
		o.setCancelStatus(o.getOrderStatus());
		o.setOrderStatus(OrderConstant.ORDER_STATUS_CANCEL);
		o.setCancelType(cancelType);
		o.setUpdateUserid(su.getUserId());
		o.setEndTime(new Date());
		if (cancelReason != null) {
			o.setCancelReason(cancelReason);
		}
		getDao().update(o);
		Engineer engineer = engineerService.queryById(o.getEngineerId());
		OldToNewUser old = oldToNewService.queryById(o.getUserId());
		// 如果订单已派单则将工程师改为空闲状态
		if (o.getCancelStatus() >= OrderConstant.ORDER_STATUS_DISPATCHED) {
			engineerService.checkDispatchState(o.getEngineerId());
			if (cancelType != OrderConstant.ORDER_CANCEL_TYPE_ENGINEER) {
				// 如果取消人不是工程师取消发送短信
				try {
					SmsSendUtil.sendSmsToEngineerForCancel(engineer.getMobile(), o.getOrderNo());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if (cancelType != OrderConstant.ORDER_CANCEL_TYPE_CUSTOMER) {
			// 如果取消人不是客户取消发送短信
			try {
				SmsSendUtil.sendSmsToCustomerForCancel(old.getTel(), o.getOrderNo());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 预约兑换时间
	 */
	public void agreedTime(Agreed agreed, String agreedTime, SessionUser su) {
		NewOrder o = getDao().queryByOrderNo(agreed.getOrderNo());
		if (o == null) {
			throw new SystemException("订单不存在！");
		}
		if (su.getType() != SystemConstant.USER_TYPE_SYSTEM && su.getType() != SystemConstant.USER_TYPE_CUSTOMER_SERVICE
				&& su.getType() != SystemConstant.USER_TYPE_SHOP) {
			throw new SystemException("对不起，您无权操作该订单！");
		}
		if (o.getOrderStatus() == OrderConstant.ORDER_STATUS_CANCEL) {
			throw new SystemException("该订单已取消！");
		}

		if (o.getOrderStatus() == OrderConstant.ORDER_STATUS_FINISHED) {
			throw new SystemException("该订单已完成！");
		}
		if (o.getOrderStatus() < OrderConstant.WAIT_AGREED) {
			throw new ApiServiceException(ApiResultConstant.resultCode_3002, "订单未派单");
		}
		if (o.getOrderStatus() == OrderConstant.WAIT_AGREED) {
			o.setOrderStatus(OrderConstant.ALREADY_AGREED);
			o.setUpdateUserid(su.getUserId());
			// 用户支付金额按照工程师手动输入的价格为准
			o.setRealPrice(agreed.getAgreedOrderPrice());
			getDao().update(o);
		} else {
			throw new SystemException("该订单无法预约维修时间！");
		}
		// 保存预约信息
		agreedService.saveAgreedNews(agreed);

	}

	/**
	 * 派单给工程师
	 */
	public void dispatchToEngineer(String id, String engId, SessionUser su) {
		// 检查订单状态
		NewOrder o = getDao().queryById(id);
		if (o == null) {
			throw new SystemException("订单不存在！");
		}
		if (!su.getShopCode().equals(o.getShopCode())) {
			throw new SystemException("对不起，您无权操作该订单！原因可能是已转派给其它维修门店。");
		}
		if (engId.equals(o.getEngineerId())) {
			throw new SystemException("该订单已派单给此工程师，无需重复操作！");
		}
		// 检查工程师状态
		Engineer eng = engineerService.queryById(engId);
		if (eng.getIsDispatch() == 2) {
			throw new SystemException("派单失败，工程师：" + eng.getName() + ", 处于离线状态");
		}
		String oldEngineerId = o.getEngineerId();
		o.setEngineerId(eng.getId());
		o.setIsDispatch(1);
		o.setDispatchTime(new Date());
		o.setOrderStatus(OrderConstant.ORDER_STATUS_DISPATCHED);
		getDao().update(o);
		// 更改工程师状态
		eng.setIsDispatch(1);
		engineerService.saveUpdate(eng);
		engineerService.checkDispatchState(oldEngineerId);
		OldToNewUser old = oldToNewService.queryById(o.getUserId());
		// 给工程师发送短信提示
		SmsSendUtil.sendSmsToEngineerWithoutShop(eng, o, old);
	}

	/**
	 * 以旧换新完成订单
	 */
	public void orderConfirmToFinish(String id, SessionUser su) {
		NewOrder o = getDao().queryById(id);
		if (o == null) {
			throw new SystemException("订单不存在！");
		}
		if (!su.getUserId().equals(o.getCustomerId())) {
			throw new SystemException("对不起，您无权操作该订单！");
		}
		if (o.getOrderStatus() == OrderConstant.ORDER_STATUS_CANCEL) {
			throw new SystemException("该订单已取消！");
		}

		if (o.getOrderStatus() == OrderConstant.ORDER_STATUS_FINISHED) {
			throw new SystemException("该订单已完成！无需重复操作");
		}

		// 执行完成订单
		o.setOrderStatus(OrderConstant.ORDER_STATUS_FINISHED);
		o.setUpdateUserid(su.getUserId());
		o.setEndTime(new Date());
		getDao().update(o);
		engineerService.checkDispatchState(o.getEngineerId());
	}

	/**
	 * 更新订单
	 */
	public void saveUpdate(NewOrder o) {
		getDao().update(o);
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
		SessionUser su = (SessionUser) params.get("user");
		// 获取查询条件

		String queryStartTime = MapUtils.getString(params, "query_startTime");
		String queryEndTime = MapUtils.getString(params, "query_endTime");
		String province = MapUtils.getString(params, "queryProvince");
		String city = MapUtils.getString(params, "queryCity");
		String county = MapUtils.getString(params, "queryCounty");
		NewOrder old = new NewOrder();
		old.setQueryStartTime(queryStartTime);
		old.setQueryEndTime(queryEndTime);
		old.setProvince(province);
		old.setCity(city);
		old.setCounty(county);

		// 判断用户类型系统管理员可以查看所有订单
		if (su.getType() == SystemConstant.USER_TYPE_SYSTEM
				|| su.getType() == SystemConstant.USER_TYPE_CUSTOMER_SERVICE) {

		} else if (su.getType() == SystemConstant.USER_TYPE_PROVIDER) {
			// 连锁商只能查看自己的订单
			old.setProviderCode(su.getProviderCode());
		} else if (su.getType() == SystemConstant.USER_TYPE_SHOP) {
			// 门店商只能查看自己的订单
			old.setShopCode(su.getShopCode());
		}

		String idStr = MapUtils.getString(params, "ids");
		if (StringUtils.isNotBlank(idStr)) {
			String[] ids = StringUtils.split(idStr, ",");
			old.setQueryIds(Arrays.asList(ids));
		}
		List<NewOrder> list = queryList(old);
		List<NewOrderList> project = createList(list);
		System.out.println("导出大小:" + project.size());
		System.out.println("身份:" + su.getType());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("newOrderList", project);
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
	 * 导出模板包装订单类
	 */
	public List<NewOrderList> createList(List<NewOrder> lists) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<NewOrderList> list = new ArrayList<NewOrderList>();
		for (NewOrder order : lists) {
			NewOrderList l = new NewOrderList();
			l.setId(order.getId());
			l.setOrderNo(order.getOrderNo());
			l.setExportInTime(sdf.format(order.getInTime()));
			l.setOrderPrice(order.getRealPrice());
			if (order.getOrderStatus() == 0) {
				l.setExportOrderStatus("待提交");
			} else if (order.getOrderStatus() == 2) {
				l.setExportOrderStatus("待派单");
			} else if (order.getOrderStatus() == 11) {
				l.setExportOrderStatus("待预约");
			} else if (order.getOrderStatus() == 12) {
				l.setExportOrderStatus("已预约");
			} else if (order.getOrderStatus() == 50) {
				l.setExportOrderStatus("已完成");
			} else if (order.getOrderStatus() == 55) {
				l.setExportOrderStatus("用户已评价");
			} else if (order.getOrderStatus() == 60) {
				l.setExportOrderStatus("已取消");
			} else {
				l.setExportOrderStatus("状态异常");
			}
			l.setDispatchTime(order.getDispatchTime());
			l.setEngineerId(order.getEngineerId());

			if (order.getEndTime() != null) {
				l.setExportEndTime(sdf.format(order.getEndTime()));
			} else {
				l.setExportEndTime("");
			}

			l.setCancelType(order.getCancelType());
			l.setBalanceStatus(order.getBalanceStatus());
			OldToNewUser u = oldToNewService.queryById(order.getUserId());
			if (u != null) {
				l.setCustomerName(u.getName());
				l.setCustomerMobile(u.getTel());
				l.setOldMobile(u.getOldMobile());
				l.setNewMobile(u.getNewMobile());
				l.setAddress(u.getHomeAddress());
			}
			Engineer e = engineerService.queryById(order.getEngineerId());
			if (e != null) {
				l.setEngineerNumber(e.getNumber());
				l.setEngineerName(e.getName());
			}
			Shop shop = shopService.queryByCode(order.getShopCode());
			if (shop != null) {
				l.setShopName(shop.getName());
			}
			list.add(l);
		}
		return list;
	}

	/**
	 * 以旧换新包装类，用于前端展示订单详情
	 */
	public NewOrderList createOrderDetail(NewOrder order) {
		NewOrderList l = new NewOrderList();
		l.setId(order.getId());
		l.setOrderNo(order.getOrderNo());
		l.setInTime(order.getInTime());
		l.setOrderPrice(order.getOrderPrice());
		l.setOrderStatus(order.getOrderStatus());
		l.setRealPrice(order.getRealPrice());
		l.setIsUseCoupon(order.getIsUseCoupon());
		l.setCouponId(order.getCouponId());
		l.setIsDispatch(order.getIsDispatch());
		l.setCouponCode(order.getCouponCode());
		l.setCouponName(order.getCouponName());
		l.setCancelType(order.getConvertType());
		l.setCouponPrice(order.getCouponPrice());
		l.setCancelReason(order.getCancelReason());
		l.setSelectType(order.getSelectType());
		l.setIsComment(order.getIsComment());
		Coupon c = couponService.queryById(order.getCouponId());
		if (c != null) {
			l.setCouponBeginTime(c.getBeginTime());
			l.setCouponEndTime(c.getEndTime());
		}
		l.setDispatchTime(order.getDispatchTime());
		l.setEngineerId(order.getEngineerId());
		l.setEndTime(order.getEndTime());
		OldToNewUser u = oldToNewService.queryById(order.getUserId());
		if (u != null) {
			l.setCustomerName(u.getName());
			l.setCustomerMobile(u.getTel());
			l.setOldMobile(u.getOldMobile());
			l.setNewMobile(u.getNewMobile());
			l.setFullAddress(u.getHomeAddress());
			l.setPostScript(u.getPostscript());
		}
		Engineer e = engineerService.queryById(order.getEngineerId());
		if (e != null) {
			l.setEngineerNumber(e.getNumber());
			l.setEngineerName(e.getName());
		}
		Shop shop = shopService.queryByCode(order.getShopCode());
		if (shop != null) {
			l.setShopName(shop.getName());
			l.setShopCode(shop.getCode());
			l.setShopManagerName(shop.getManagerName());
			l.setShopMobile(shop.getTel());
			l.setShopManagerMobile(shop.getManagerMobile());
		}
		Agreed agreed = agreedService.queryByOrderNo(order.getOrderNo());
		if (agreed != null) {
			l.setIsAgreed(0);
			l.setAgreedTime(agreed.getAgreedTime());
			l.setAgreedModel(agreed.getNewModelName());
			l.setColor(agreed.getColor());
			l.setMemory(agreed.getMemory());
			l.setEdition(agreed.getEdition());
			l.setAgreedPrice(agreed.getAgreedOrderPrice());
			l.setAgreedOther(agreed.getOther());
		} else {
			l.setIsAgreed(1);
		}
		return l;
	}

	/**
	 * 用于pc端和安卓端用户查询订单列表显示的公共包装类
	 */
	public List<OrderShow> commonOrderList(List<Order> orderL, List<NewOrder> newOrderL) {
		List<OrderShow> orderShow = new ArrayList<OrderShow>();
		if (newOrderL != null && newOrderL.size() > 0) {
			for (NewOrder order : newOrderL) {
				OldToNewUser old = oldToNewService.queryById(order.getUserId());
				OrderShow show = new OrderShow();
				Agreed agreed = agreedService.queryByOrderNo(order.getOrderNo());
				if (agreed != null) {
					// 表示已预约
					show.setIsAgreed(0);
					show.setAgreedModel(agreed.getNewModelName());
					show.setColor(agreed.getColor());
					show.setMemory(agreed.getMemory());
					show.setEdition(agreed.getEdition());
					show.setAgreedPrice(agreed.getAgreedOrderPrice());
					show.setAgreedOther(agreed.getOther());
				} else {
					// 未预约，则只显示用户填写的 兑换信息
					show.setIsAgreed(1);
				}
				Shop shop = shopService.queryByCode(order.getShopCode());
				if (shop != null) {
					show.setShopName(shop.getName());
				}
				show.setId(order.getId());
				show.setOrderType(1);// 1表示以旧换新订单
				show.setOrderNo(order.getOrderNo());
				show.setInTime(order.getInTime());
				show.setUpdateTime(order.getUpdateTime());
				show.setOrderStatus(order.getOrderStatus());
				show.setCustomerName(old.getName());
				show.setMobile(old.getTel());
				show.setIsComment(order.getIsComment());
				show.setEndCheckTime(order.getEndCheckTime());
				show.setRealPrice(order.getRealPrice());
				show.setOldModel(old.getOldMobile());
				show.setNewModel(old.getNewMobile());
				orderShow.add(show);
			}
		}
		if (orderL != null && orderL.size() > 0) {
			for (Order order : orderL) {
				order.setOrderDetails(detailService.queryByOrderNo(order.getOrderNo()));
				OrderShow show = new OrderShow();
				show.setId(order.getId());
				show.setOrderType(0);// 0表示维修订单
				show.setOrderNo(order.getOrderNo());
				show.setInTime(order.getInTime());
				show.setUpdateTime(order.getUpdateTime());
				show.setShopName(order.getShopName());
				show.setOrderStatus(order.getOrderStatus());
				show.setCustomerName(order.getCustomerName());
				show.setMobile(order.getMobile());
				show.setIsComment(order.getIsComment());
				show.setEndCheckTime(order.getEndCheckTime());
				show.setRepairColor(order.getColor());
				show.setModelName(order.getModelName());
				show.setRealPrice(order.getRealPrice());
				show.setOrderDetails(order.getOrderDetails());
				orderShow.add(show);
			}
		}
		return orderShow;
	}

}
