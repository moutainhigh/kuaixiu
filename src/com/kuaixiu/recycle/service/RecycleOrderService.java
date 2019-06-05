package com.kuaixiu.recycle.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.service.BaseService;
import com.common.exception.SystemException;
import com.common.paginate.Page;
import com.common.util.AES;
import com.common.util.DateUtil;
import com.common.wechat.common.util.StringUtils;
import com.kuaixiu.recycle.dao.RecycleOrderMapper;
import com.kuaixiu.recycle.entity.*;
import com.system.basic.address.entity.Address;
import com.system.basic.address.service.AddressService;
import com.system.basic.user.entity.SessionUser;
import com.system.constant.SystemConstant;
import jodd.util.StringUtil;
import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author: anson
 * @CreateDate: 2017年11月17日 上午10:18:53
 * @version: V 1.0
 */
@Service("recycleOrderService")
public class RecycleOrderService extends BaseService<RecycleOrder> {

    @Autowired
    private RecycleOrderMapper<RecycleOrder> mapper;
    @Autowired
    private RecycleCouponService recycleCouponService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private PushsfExceptionService pushsfExceptionService;

    private static final String baseNewUrl = SystemConstant.RECYCLE_NEW_URL;
    private static final String cipherdata = SystemConstant.RECYCLE_REQUEST;


    @Override
    public RecycleOrderMapper<RecycleOrder> getDao() {

        return mapper;
    }

    /**
     * 根据回收流水号获取订单信息
     *
     * @param code
     * @return
     */
    public RecycleOrder queryByQuoteId(String code) {
        return getDao().queryByQuoteId(code);
    }

    public RecycleOrder queryByOrderState(RecycleOrder recycleOrder) {
        return getDao().queryByOrderState(recycleOrder);
    }

    /**
     * 根据orderNo改订单状态
     *
     * @param recycleOrder
     * @return
     */
    public int updateByOrderStatus(RecycleOrder recycleOrder) {
        return getDao().updateByOrderStatus(recycleOrder);
    }

    public int deleteCouponIdByOrderStatus(String orderNo) {
        return getDao().deleteCouponIdByOrderStatus(orderNo);
    }

    /**
     * 根据回收订单号获取订单信息
     *
     * @param code
     * @return
     */
    public RecycleOrder queryByOrderNo(String code) {
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
     *
     * @param code
     * @return
     */
    public RecycleOrder queryByIncreaseOrderNo(String code) {
        return getDao().queryByIncreaseOrderNo(code);
    }


    //确定来源，没有则默认微信公众号来源
    public String isHaveSource(RecycleOrder order, String source) {
        if (!source.equals("null") && StringUtils.isNotBlank(source)) {
            String sourceType = source;
            if (source.contains("?")) {
                sourceType = org.apache.commons.lang3.StringUtils.substringBefore(source, "?");
            }
            order.setSourceType(Integer.parseInt(sourceType));
        } else {
            source = "1";
            order.setSourceType(Integer.valueOf(source));        //订单来源  默认微信公众号来源
        }
        return source;
    }


    //判断该订单来源确定是否使用加价券
    public RecycleCoupon getCouponCode(String mobile, HttpServletRequest request, List<CouponAddValue> addValues, BigDecimal price) {
        CouponAddValue addValue = new CouponAddValue();
        for (CouponAddValue addValue1 : addValues) {
            if (price.intValue() > 4000) {
                if (addValue1.getPricingType() == 2) {
                    addValue = addValue1;
                }
            } else {
                if (addValue1.getPricingType() == 1) {
                    addValue = addValue1;
                }
            }
        }
        RecycleCoupon recycleCoupon = new RecycleCoupon();
        if (new Date().getTime() < addValue.getActivityEndTime().getTime()) {
            SessionUser su = (SessionUser) request.getSession().getAttribute(SystemConstant.SESSION_USER_KEY);
            recycleCoupon = this.addCoupon(addValue, su, mobile);
        }
        return recycleCoupon;
    }
//
//    //判断该订单来源确定是否使用加价券
//    public RecycleCoupon getCouponCodeTest(String mobile, List<CouponAddValue> addValues,BigDecimal price) {
//        CouponAddValue addValue = new CouponAddValue();
//        for (CouponAddValue addValue1 : addValues) {
//            if(price.intValue()>4000){
//                if (addValue1.getPricingType() == 2) {
//                    addValue = addValue1;
//                }
//            }else{
//                if (addValue1.getPricingType() == 1) {
//                    addValue = addValue1;
//                }
//            }
//        }
//        RecycleCoupon recycleCoupon = new RecycleCoupon();
//        if (new Date().getTime() < addValue.getActivityEndTime().getTime()) {
//            SessionUser su = new SessionUser();
//            recycleCoupon = this.addCoupon(addValue, su, mobile);
//        }
//        return recycleCoupon;
//    }

    //创建加价券
    private RecycleCoupon addCoupon(CouponAddValue addValue, SessionUser su, String mobile) {
        RecycleCoupon recycleCoupon = new RecycleCoupon();
        recycleCoupon.setBatchId(addValue.getBatchId());
        if (su != null) {
            recycleCoupon.setCreateUserid(su.getUserId());
            recycleCoupon.setUpdateUserid(su.getUserId());
        }
        recycleCoupon.setId(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16));
        recycleCoupon.setCouponName(addValue.getCouponName());
        recycleCoupon.setPricingType(addValue.getPricingType());
        recycleCoupon.setRuleDescription(addValue.getRuleDescription());
        recycleCoupon.setUpperLimit(addValue.getUpperLimit());
        recycleCoupon.setSubtraction_price(addValue.getSubtractionPrice());
        recycleCoupon.setStrCouponPrice(addValue.getCouponPrice());
        recycleCoupon.setBeginTime(addValue.getBeginTime());
        recycleCoupon.setEndTime(addValue.getEndTime());
        recycleCoupon.setReceiveMobile(mobile);
        recycleCoupon.setAddPriceUpper(addValue.getAddPriceUpper());
        recycleCoupon.setIsDel(0);
        recycleCoupon.setIsUse(0);
        recycleCoupon.setIsReceive(1);
        recycleCoupon.setStatus(1);
        recycleCoupon.setNote(addValue.getNote());
        recycleCoupon.setCouponCode(recycleCouponService.createNewCode());
        recycleCouponService.add(recycleCoupon);
        return recycleCoupon;
    }


    //转化地址
    public String getAreaname(String province, String city, String area) {
        Address provinceName = addressService.queryByAreaId(province);
        Address cityName = addressService.queryByAreaId(city);
        Address areaName = addressService.queryByAreaId(area);
        if (provinceName == null || cityName == null || areaName == null) {
            throw new SystemException("请确认地址信息是否无误");
        }
        return getProvince(provinceName.getArea()) + cityName.getArea() + " " + areaName.getArea();
    }

    //发送给回收平台加价券
    public void sendRecycleCoupon(JSONObject code, RecycleCoupon recycleCoupon, List<CouponAddValue> addValues) {
        JSONArray jsonArray = new JSONArray();
        for (CouponAddValue addValue : addValues) {
            JSONObject json = new JSONObject();
            json.put("couponId", recycleCoupon.getCouponCode());
            json.put("actType", addValue.getPricingType());
            if (addValue.getPricingType() == 1) {
                json.put("addFee", 0);
                json.put("percent", addValue.getCouponPrice().divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_DOWN));
            } else {
                json.put("addFee", addValue.getCouponPrice().intValue());
                json.put("percent", 0);
            }
            json.put("up", addValue.getUpperLimit());
            json.put("low", addValue.getSubtractionPrice());
            json.put("desc", recycleCoupon.getRuleDescription());
            jsonArray.add(json);
        }
        code.put("coupon_rule", jsonArray.toJSONString());
    }


    //订单提交成功 当用户选择超人系统推送时  调用顺丰取件接口
    public void getPostSF(RecycleOrder order, Long time, HttpServletRequest request) {
        try {
            String mailNo = postSfOrder(order);
            order.setSfOrderNo(mailNo);
            order.setOrderStatus(2);
//			this.saveUpdate(order);
//			log.info("顺丰推送成功");
        } catch (Exception e) {
            //记录顺丰推送异常信息
            PushsfException exception = new PushsfException();
            exception.setShNo(UUID.randomUUID().toString().replace("-", ""));
            exception.setOrderNo(order.getOrderNo());
            exception.setShExceptin(e.getMessage());
            pushsfExceptionService.add(exception);
            request.getSession().setAttribute("newTimes", time);
            throw new SystemException("推送顺丰快递失败");
        }
    }

    /**
     * 发起订单物流请求
     */
    private String postSfOrder(RecycleOrder order) throws Exception {
        String mailNo = null;
        String sfUrl = baseNewUrl + "pushsforder";
        JSONObject requestNews = new JSONObject();
        JSONObject code = new JSONObject();
        code.put("orderid", order.getOrderNo());
        code.put("sendtime", order.getTakeTime());
        String realCode = AES.Encrypt(code.toString());  //加密
        requestNews.put(cipherdata, realCode);
        //发起请求
        String getResult = AES.post(sfUrl, requestNews);
        //对得到结果进行解密  //得到运单号
        JSONObject jsonResult = getResult(AES.Decrypt(getResult));
        JSONObject j = (JSONObject) jsonResult.get("datainfo");
        if (j != null) {
            mailNo = j.getString("mailno");
        } else {
            throw new SystemException("参数值不正确");
        }
        return mailNo;
    }

    //根据订单价计算加价金额
    public BigDecimal getAddCouponPrice(BigDecimal orderPrice) {
//        //计算计价5%之前的原价
//        BigDecimal price = orderPrice.divide(new BigDecimal("105"), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"));
        //计算原价的10%额度
        BigDecimal addCouponPrice = orderPrice.divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("10"));
        return addCouponPrice.setScale(0, BigDecimal.ROUND_HALF_UP);
    }

    public static String div095(String orderPrice){
        BigDecimal addCouponPrice = new BigDecimal(orderPrice).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("95"));
        return addCouponPrice.setScale(0, BigDecimal.ROUND_HALF_UP).toString();
    }

    /**
     * 返回结果解析
     */
    public static JSONObject getResult(String originalString) {
        JSONObject result = (JSONObject) JSONObject.parse(originalString);
        if (result.getString("result") != null && !result.getString("result").equals("RESPONSESUCCESS")) {
            throw new SystemException(result.getString("msg"));
        }
        return result;
    }



    /**
     * 超人系统地址为 上海市 黄浦区 城区    -----  浙江 杭州市 江干区
     * 回收地址规范     上海市 黄浦区            ------ 浙江省 杭州市 江干区
     * 省份区别  西藏 宁夏 新疆 广西 内蒙古
     * 北京市  天津市 上海市  重庆市 其他省后面都加省
     *
     * @param code
     * @return 超人地址转化回收地址规范
     */
    public static String getProvince(String code) {
        List<String> s = new ArrayList<String>();
        List<String> p = new ArrayList<String>();
        String[] plist = {"西藏", "宁夏", "新疆", "广西", "内蒙古"};
        String[] slist = {"北京", "天津", "上海", "重庆"};
        s.addAll(Arrays.asList(plist));
        p.addAll(Arrays.asList(slist));
        if (s.contains(code)) {
            //不用更改
        } else if (p.contains(code)) {
            code += "市";
        } else {
            code += "省";
        }
        return code;
    }

    /**
     * 通过quoteid查询 订单信息
     */
    public JSONObject postNews(String quoteId) throws Exception {
        JSONObject requestNews = new JSONObject();
        //调用接口需要加密的数据
        String url = baseNewUrl + "getquotedetail";
        JSONObject code = new JSONObject();
        code.put("quoteid", quoteId);
        String realCode = AES.Encrypt(code.toString());  //加密
        requestNews.put(cipherdata, realCode);
        //发起请求
        String getResult = AES.post(url, requestNews);
        //对得到结果进行解密
        JSONObject jsonResult = getResult(AES.Decrypt(getResult));
        JSONObject j = (JSONObject) jsonResult.get("datainfo");
        return j;
    }

    /**
     * 更新字符串格式
     */
    public String getDate(String time) {
        String realTime = "";
        try {
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            String month = c.get(Calendar.YEAR) + "/" + (time.substring(0, time.indexOf("月"))) + "/"
                    + (time.substring((time.indexOf("月") + 1), time.indexOf("日")));
            String hour = time.substring((time.lastIndexOf(" ") + 1), time.indexOf(":"));
            realTime = month + " " + hour + ":30:00";
        } catch (Exception e) {
            realTime = time;
        }
        return realTime;
    }

    @Autowired
    private RecycleSystemService recycleSystemService;

    /**
     * 已Excel形式导出列表数据
     *
     * @param params
     */
    @SuppressWarnings("rawtypes")
    public void expDataExcel(Map<String, Object> params) {
        HttpServletResponse response = (HttpServletResponse) params.get("response");
        //获取登录用户
        SessionUser su = (SessionUser) params.get("user");
        //获取查询条件
        String orderNo = MapUtils.getString(params, "query_orderNo");
        String status = MapUtils.getString(params, "query_orderStates");
        String mobile = MapUtils.getString(params, "query_customerMobile");
        String queryStartTime = MapUtils.getString(params, "query_startTime");
        String queryEndTime = MapUtils.getString(params, "query_endTime");
        String fromSystem = MapUtils.getString(params, "fromSystem");
        String isCoupon = MapUtils.getString(params, "isCoupon");

        RecycleCheckItems checkItem = new RecycleCheckItems();
        String idStr = MapUtils.getString(params, "ids");
        if (org.apache.commons.lang3.StringUtils.isNotBlank(idStr)) {
            String[] ids = org.apache.commons.lang3.StringUtils.split(idStr, ",");
            checkItem.setQueryIds(Arrays.asList(ids));
        }
        RecycleOrder r = new RecycleOrder();
        r.setOrderNo(orderNo);
        if (StringUtils.isNotBlank(fromSystem)) {
            r.setSourceType(Integer.valueOf(fromSystem));
        }
        if (StringUtil.isNotBlank(status)) {
            r.setOrderStatus(Integer.parseInt(status));
        }
        r.setMobile(mobile);
        r.setQueryStartTime(queryStartTime);
        r.setQueryEndTime(queryEndTime);
        r.setIsCoupon(isCoupon);
        List<RecycleOrder> recycleOrders = this.getDao().queryImportList(r);
        List<RecycleSystem> flist = recycleSystemService.queryList(null);
        List<Map<String, Object>> maps = new ArrayList<>();
        for (RecycleOrder o : recycleOrders) {
            for (RecycleSystem system : flist) {
                if (o.getSourceType() == system.getId()) {
                    o.setFm(system.getName());  //系统来源
                    break;
                }
            }
            Map<String, Object> map = new BeanMap(o);
            maps.add(map);
        }


        List<List<Map<String, Object>>> lists = new ArrayList<>();
        int size = maps.size() / 10000;
        List<String> listAgent = new ArrayList<>();
        if (maps.size() < 10000) {
            lists.add(maps);
            listAgent.add("集合");
        } else {
            for (int i = 0; i < size; i++) {
                List<Map<String, Object>> list = maps.subList(i * 10000, (i + 1) * 10000);
                lists.add(list);
                listAgent.add("集合" + i);
            }
            //余数
            int lastSize = maps.size() - size * 10000;
            List<Map<String, Object>> list = maps.subList(size * 10000, size * 10000 + lastSize);
            lists.add(list);
            listAgent.add("集合" + size);
        }

        //excel标题
        String[] header = new String[]{"订单号", "订单状态", "联系人/手机号", "支付类型", "机型名字","回收价格",
                "订单来源", "使用加价券", "下单时间"};

// 导出到多个sheet中--------------------------------------------------------------------------------开始
        // 创建一个EXCEL
        HSSFWorkbook wb = new HSSFWorkbook();
        // 循环经销商，每个经销商一个sheet
        for (int i = 0; i < listAgent.size(); i++) {
            // 第 i 个sheet,以经销商命名
            HSSFSheet sheet = wb.createSheet(listAgent.get(i).toString());
            // 第i个sheet第二行为列名
            HSSFRow rowFirst = sheet.createRow(0);
            // 写标题
            for (int j = 0; j < header.length; j++) {
                // 获取第一行的每一个单元格
                HSSFCell cell = rowFirst.createCell(j);
                // 往单元格里面写入值
                cell.setCellValue(header[j]);
            }
            for (int j = 0; j < lists.get(i).size(); j++) {
                Map<?, ?> map = (Map<?, ?>) lists.get(i).get(j);
                // 创建数据行，从第三行开始
                HSSFRow row = sheet.createRow(j + 1);
                // 设置对应单元格的值
                getRow(row, map);
            }
        }
        // 写出文件（path为文件路径含文件名）
        OutputStream os = null;
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/x-download");
            String filedisplay = "回收列表导出.xls";
            //防止文件名含有中文乱码
            filedisplay = new String(filedisplay.getBytes("gb2312"), "ISO8859-1");
            response.setHeader("Content-Disposition", "attachment;filename=" + filedisplay);

            os = response.getOutputStream();
            wb.write(os);
        } catch (Exception e) {
            System.out.println("导出文件出错了.....\n" + e.getMessage());
        } finally {
            try {
                os.flush();
                os.close();
            } catch (IOException e) {
                System.out.println("导出文件出错了.....\n" + e.getMessage());
            }
        }
        // 导出到多个sheet中---------------------------------------------------------------------------------结束
    }


    private HSSFRow getRow(HSSFRow row, Map<?, ?> map) {
        // 设置对应单元格的值
        if (map.get("orderNo") == null) {
            row.createCell(0).setCellValue("");
        } else {
            row.createCell(0).setCellValue(map.get("orderNo").toString());
        }
        if (map.get("orderStatus") == null) {
            row.createCell(1).setCellValue("");
        } else {
            row.createCell(1).setCellValue(getState(Integer.valueOf(map.get("orderStatus").toString())));
        }
        if (map.get("name") == null && map.get("mobile") == null) {
            row.createCell(2).setCellValue("");
        } else if (map.get("name") != null && map.get("mobile") != null) {
            row.createCell(2).setCellValue(map.get("name").toString() + "/" + map.get("mobile").toString());
        } else if (map.get("name") != null) {
            row.createCell(2).setCellValue(map.get("name").toString());
        } else if (map.get("mobile") != null) {
            row.createCell(2).setCellValue(map.get("mobile").toString());
        }
        if (map.get("exchangeType") == null) {
            row.createCell(3).setCellValue("");
        } else {
            if ("1".equals(map.get("exchangeType").toString())) {
                row.createCell(3).setCellValue("支付宝收款");
            } else {
                row.createCell(3).setCellValue("话费充值");
            }
        }
        if (map.get("productName") == null) {
            row.createCell(4).setCellValue("");
        } else {
            row.createCell(4).setCellValue(map.get("productName").toString());
        }
        if (map.get("price") == null) {
            row.createCell(5).setCellValue("");
        } else {
            row.createCell(5).setCellValue(map.get("price").toString());
        }
        if (map.get("fm") == null) {
            row.createCell(6).setCellValue("");
        } else {
            row.createCell(6).setCellValue(map.get("fm").toString());
        }
        if (map.get("couponId") == null) {
            row.createCell(7).setCellValue("否");
        } else {
            row.createCell(7).setCellValue("是");
        }
        if (map.get("inTime") == null) {
            row.createCell(8).setCellValue("");
        } else {
            row.createCell(8).setCellValue(DateUtil.getDateyyyyMMddHHmmss((Date) map.get("inTime")));
        }
        return row;
    }

    private String getState(Integer state) {
        if (state == 0) {
            return "订单已取消";
        } else if (state == 1) {
            return "创建订单";
        } else if (state == 2) {
            return "待客户发件";
        } else if (state == 3) {
            return "已发货待收件";
        } else if (state == 4) {
            return "门店收件";
        } else if (state == 5) {
            return "提交质检报告";
        } else if (state == 6) {
            return "需议价订单";
        } else if (state == 7) {
            return "议价结束";
        } else if (state == 8) {
            return "待支付订单";
        } else if (state == 9) {
            return "支付完成（结束）";
        } else if (state == 10) {
            return "预支付转账成功";
        } else if (state == 11) {
            return "预支付转账失败";
        } else if (state == 12) {
            return "支付尾款失败";
        } else if (state == 13) {
            return "扣款失败";
        } else {
            return " ";
        }
    }
}
