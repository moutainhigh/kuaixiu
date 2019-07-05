package com.kuaixiu.engineer.service;


import com.common.base.service.BaseService;
import com.common.exception.SystemException;
import com.common.util.ConverterUtil;
import com.common.util.SmsSendUtil;
import com.kuaixiu.engineer.dao.EngineerMapper;
import com.kuaixiu.engineer.entity.Engineer;
import com.kuaixiu.shop.entity.Shop;
import com.kuaixiu.shop.service.ShopService;
import com.system.basic.address.entity.Address;
import com.system.basic.address.service.AddressService;
import com.system.basic.sequence.util.SeqUtil;
import com.system.basic.user.entity.SessionUser;
import com.system.basic.user.service.SysUserService;
import com.system.constant.SystemConstant;
import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Engineer Service
 *
 * @CreateDate: 2016-08-26 上午03:13:05
 * @version: V 1.0
 */
@Service("engineerService")
public class EngineerService extends BaseService<Engineer> {
    private static final Logger log = Logger.getLogger(EngineerService.class);

    @Autowired
    private EngineerMapper<Engineer> mapper;
    @Autowired
    private ShopService shopService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private NewEngineerService newEngineerService;

    public EngineerMapper<Engineer> getDao() {
        return mapper;
    }

    //**********自定义方法***********

    /**
     * 根据工程师工号，获取工程师基本信息
     *
     * @param number 工号
     * @return 工程师对象
     */
    public Engineer queryByEngineerNumber(String number) {
        return getDao().queryByEngineerNumber(number);
    }

    /**
     * 1、获取省份名称首字母
     * 2、生成账号
     * 3、创建登录用户
     * 4、保存
     *
     * @param t
     * @param su
     * @return
     * @CreateDate: 2016-9-7 上午1:18:20
     */
    public int save(Engineer t, SessionUser su) {
        String shopCode = Arrays.asList(t.getShopCode().split(",")).get(0);
        Shop s = shopService.queryByCode(shopCode);
        //获取省份名称首字母
        Address province = addressService.queryByAreaId(s.getProvince());
        String fristSpell = ConverterUtil.getFirstSpellForAreaName(province.getArea());
        //获取市名称首字母
        Address city = addressService.queryByAreaId(s.getCity());
        fristSpell += ConverterUtil.getFirstSpellForAreaName(city.getArea());
        //获取账号
        fristSpell += "e";
        String code = SeqUtil.getNext(fristSpell);
        t.setNumber(code);

        //创建登录用户
        String m = t.getMobile();
        sysUserService.createUser(code, m.substring(m.length() - 6), code, t.getName(),
                SystemConstant.USER_TYPE_ENGINEER, su.getUserId());
        int rest = getDao().add(t);

        //发送短信
        try {
            SmsSendUtil.sendAccountAndPasswd(m, code, m.substring(m.length() - 6));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rest;
    }

    /**
     * 查询工程师总数
     *
     * @return
     * @CreateDate: 2016-9-4 下午4:21:29
     */
    public int queryAllCount() {
        return getDao().queryCount(null);
    }

    /**
     * 根据连锁商查询工程师
     *
     * @param proCode
     * @return
     * @CreateDate: 2016-9-4 下午4:21:29
     */
    public int queryCountByProviderCode(String proCode) {
        Engineer t = new Engineer();
        t.setProviderCode(proCode);
        return getDao().queryCount(t);
    }

    /**
     * 根据门店查询工程师
     *
     * @return
     * @CreateDate: 2016-9-4 下午4:21:29
     */
    public int queryCountByShopCode(String shopCode) {
        Engineer t = new Engineer();
        t.setShopCode(shopCode);
        return getDao().queryCount(t);
    }

    /**
     * 根据连锁商查询工程师
     *
     * @param proCode
     * @return
     * @CreateDate: 2016-9-4 下午4:21:29
     */
    public List<Engineer> queryByProviderCode(String proCode) {
        Engineer t = new Engineer();
        t.setProviderCode(proCode);
        return getDao().queryList(t);
    }

    /**
     * 根据门店查询工程师
     *
     * @return
     * @CreateDate: 2016-9-4 下午4:21:29
     */
    public List<Engineer> queryByShopCode(String shopCode) {
        Engineer t = new Engineer();
        t.setShopCode(shopCode);
        return getDao().queryList(t);
    }

    /**
     * 根据门店查询空闲工程师
     *
     * @return
     * @CreateDate: 2016-9-4 下午4:21:29
     */
    public Engineer queryUnDispatchByShopCode(String shopCode) {
        return queryUnDispatchByShopCode(shopCode, null);
    }


    /**
     * 根据门店查询空闲工程师
     *
     * @return
     * @CreateDate: 2016-9-4 下午4:21:29
     */
    public Engineer queryUnDispatchByShopCode(String shopCode, String notEngId) {
        List<Engineer> list = getDao().queryUnDispatchByShopCode(shopCode, notEngId);
        return list != null && list.size() > 0 ? list.get(0) : null;
    }

    /**
     * 根据门店查询忙碌工程师
     *
     * @return
     * @CreateDate: 2016-9-4 下午4:21:29
     */
    public Engineer queryDispatchByShopCode(String shopCode, String notEngId) {
        List<Engineer> list = getDao().queryDispatchByShopCode(shopCode, notEngId);
        return list != null && list.size() > 0 ? list.get(0) : null;
    }

    /**
     * 根据门店查询空闲工程师
     *
     * @return
     * @CreateDate: 2016-9-4 下午4:21:29
     */
    public List<Engineer> queryListUnDispatchByShopCode(String shopCode) {
        return getDao().queryUnDispatchByShopCode(shopCode, null);
    }

    /**
     * 根据门店查询空闲工程师个数
     *
     * @return
     * @CreateDate: 2016-9-4 下午4:21:29
     */
    public int queryUnDispatchCount(String shopCode) {
        Engineer t = new Engineer();
        t.setShopCode(shopCode);
        t.setIsDispatch(0);
        return getDao().queryCount(t);
    }

    /**
     * 更新工程师
     *
     * @return
     * @CreateDate: 2016-8-31 下午7:08:33
     */
    public int update(Engineer eng) {
        if (eng == null || StringUtils.isBlank(eng.getId())) {
            throw new SystemException("参数为空，无法更新");
        }
        Engineer t = getDao().queryById(eng.getId());
        //如果名称发送改变则修改登录用户名称
        sysUserService.resetUserName(t.getNumber(), eng.getName(), eng.getUpdateUserid());
        eng.setNumber(t.getNumber());
        eng.setIsDispatch(eng.getIsDispatch());
        eng.setIsDel(0);
        return getDao().update(eng);
    }

    /**
     * 删除连接商
     *
     * @return
     * @CreateDate: 2016-8-31 下午7:09:59
     */
    public int delete(Engineer eng) {
        if (eng == null || StringUtils.isBlank(eng.getId())) {
            throw new SystemException("参数为空，无法更新");
        }
        Engineer t = getDao().queryById(eng.getId());
        if (t.getIsDispatch() == 1) {
            throw new SystemException("工程师已派单无法删除");
        }
        t.setIsDel(1);
        t.setUpdateUserid(eng.getUpdateUserid());
        sysUserService.deleteUser(t.getNumber(), eng.getUpdateUserid());
        return getDao().update(t);
    }

    /**
     * 检查工程师是否存在未完成的工单
     */
    public void checkDispatchState(String id) {
        //查询是否有未完成的订单,包括维修和换新订单
        if (getDao().queryUnFinishedOrderByEngId(id) == 0 && getDao().queryUnFinishedNewOrderByEngId(id) == 0) {
            Engineer eng = this.queryById(id);
            if (eng != null && eng.getIsDispatch() == 1) {
                eng.setIsDispatch(0);
                getDao().update(eng);
            }
        }
    }

    /**
     * 删除连接商
     *
     * @return
     * @CreateDate: 2016-8-31 下午7:09:59
     */
    public int deleteById(String idStr, SessionUser su) {
        if (StringUtils.isBlank(idStr)) {
            throw new SystemException("参数为空，无法更新");
        }
        //处理批量操作
        String[] ids = idStr.split(",");
        for (String id : ids) {
            Engineer t = getDao().queryById(id);
            if (t.getIsDispatch() == 1) {
                throw new SystemException("工程师已派单无法删除");
            }
            t.setIsDel(1);
            t.setUpdateUserid(su.getUserId());
            sysUserService.deleteUser(t.getNumber(), su.getUserId());
            getDao().update(t);
        }
        return 1;
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

        //获取查询条件
        String name = MapUtils.getString(params, "query_name");
        String number = MapUtils.getString(params, "query_number");
        String mobile = MapUtils.getString(params, "query_mobile");
        String idcard = MapUtils.getString(params, "query_idcard");
        String orderStatus = MapUtils.getString(params, "order_status");
        String queryStartTime = MapUtils.getString(params, "query_startTime");
        String queryEndTime = MapUtils.getString(params, "query_endTime");
        String isPatch = MapUtils.getString(params, "isPatch");
        String orderType = MapUtils.getString(params, "orderType");
        Engineer eng = new Engineer();

        eng.setName(name);
        eng.setNumber(number);
        eng.setMobile(mobile);
        eng.setIdcard(idcard);
        eng.setOrderStatus(String.valueOf(orderStatus));
        eng.setQueryStartTime(queryStartTime);
        eng.setQueryEndTime(queryEndTime);
        if (StringUtils.isNotBlank(isPatch)) {
            eng.setIsPatch(Integer.valueOf(isPatch));
        }
        String idStr = MapUtils.getString(params, "ids");
        if (StringUtils.isNotBlank(idStr)) {
            String[] ids = StringUtils.split(idStr, ",");
            eng.setQueryIds(Arrays.asList(ids));
        }

        List<Engineer> project = newEngineerService.getDao().queryListAchievement(eng);
        for (Engineer engineer : project) {
            if (StringUtils.isBlank(orderType)) {
                engineer.setOrderDayNum(engineer.getOrderDayNum() + Integer.valueOf(engineer.getReworkOrderNum()));
            } else if (Integer.valueOf(orderType) == 2) {
                if(StringUtils.isBlank(engineer.getReworkOrderNum())) {
                    engineer.setOrderDayNum(0);
                }else{
                    engineer.setOrderDayNum(Integer.valueOf(engineer.getReworkOrderNum()));
                }
            }
            //如果是多个门店，就便利查找名称
            newEngineerService.engineerShopCode(engineer);
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("engineerList", project);

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
}