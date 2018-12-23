package com.kuaixiu.recycle.service;

import com.common.base.service.BaseService;
import com.kuaixiu.recycle.dao.RecycleOrderMapper;
import com.kuaixiu.recycle.entity.RecycleOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author: anson
* @CreateDate: 2017年11月17日 上午10:18:53
* @version: V 1.0
* 
*/
@Service("recycleOrderService")
public class RecycleOrderService extends BaseService<RecycleOrder>{

	@Autowired
	private RecycleOrderMapper<RecycleOrder> mapper;
	
	@Override
	public RecycleOrderMapper<RecycleOrder> getDao() {

		return mapper;
	}
	
	/**
	 * 根据回收流水号获取订单信息
	 * @param code
	 * @return
	 */
	public RecycleOrder queryByQuoteId(String code){
        return getDao().queryByQuoteId(code);
    }
	
	public RecycleOrder queryByOrderState(RecycleOrder recycleOrder){
		return getDao().queryByOrderState(recycleOrder);
	}

	/**
	 * 根据orderNo改订单状态
	 * @param recycleOrder
	 * @return
	 */
	public int updateByOrderStatus(RecycleOrder recycleOrder){
		return getDao().updateByOrderStatus(recycleOrder);
	}

	public int deleteCouponIdByOrderStatus(String orderNo){
		return getDao().deleteCouponIdByOrderStatus(orderNo);
	}

	/**
	 * 根据回收订单号获取订单信息
	 * @param code
	 * @return
	 */
	public RecycleOrder queryByOrderNo(String code){
        return getDao().queryByOrderNo(code);
    }

	
	/**
	 * 根据手机号查询该用户是否有正在进行信用回收的订单
	 * 芝麻订单状态值不为0(未反馈) 和2(结束)
	 */
	 public List<RecycleOrder> queryByMobile(RecycleOrder t) {
	        return getDao().queryByMobile(t);
	    }


	/**
	 * 根据抬价订单号查询
	 * @param code
	 * @return
	 */
	public RecycleOrder queryByIncreaseOrderNo(String code){
		return getDao().queryByIncreaseOrderNo(code);
	}
}
