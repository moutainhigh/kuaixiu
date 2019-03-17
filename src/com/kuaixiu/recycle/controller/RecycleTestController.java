package com.kuaixiu.recycle.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.exception.SystemException;
import com.common.paginate.Page;
import com.common.util.AES;
import com.common.wechat.common.util.StringUtils;
import com.kuaixiu.brand.entity.Brand;
import com.kuaixiu.brand.entity.NewBrand;
import com.kuaixiu.provider.entity.Provider;
import com.kuaixiu.provider.service.ProviderService;
import com.kuaixiu.recycle.entity.*;
import com.kuaixiu.recycle.service.*;
import com.kuaixiu.shop.entity.NewShopModel;
import com.kuaixiu.shop.entity.Shop;
import com.kuaixiu.shop.entity.ShopModel;
import com.system.api.entity.ResultData;
import com.system.basic.address.entity.Address;
import com.system.basic.address.service.AddressService;
import com.system.basic.user.entity.SessionUser;
import com.system.constant.ApiResultConstant;
import com.system.constant.SystemConstant;
import jodd.util.StringUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author: najy
 * @CreateDate: 2019年3月12日15:09:56
 * @version: V 1.0
 * 回收检测信息
 */
@Controller
public class RecycleTestController extends BaseController {
    private static final Logger log = Logger.getLogger(RecycleTestController.class);


    @Autowired
    private RecycleCheckItemsService checkItemsService;
    @Autowired
    private RecycleTestService testService;
    @Autowired
    private RecycleOrderService orderService;
    @Autowired
    private RecycleCouponService recycleCouponService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private RecycleOrderService recycleOrderService;
    @Autowired
    private RecycleCustomerService recycleCustomerService;
    @Autowired
    private PushsfExceptionService pushsfExceptionService;
    @Autowired
    private RecycleTestService recycleTestService;
    @Autowired
    private RecycleCheckItemsService recycleCheckItemsService;

    /**
     * 基础访问接口地址
     */
    private static final String baseNewUrl = SystemConstant.RECYCLE_NEW_URL;
    /**
     * 基础访问接口地址
     */
    private static final String baseUrl = SystemConstant.RECYCLE_URL;
    /**
     * 需要加密的数据名
     */
    private static final String cipherdata = SystemConstant.RECYCLE_REQUEST;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "recycle/recycleTestList")
    public ModelAndView list(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {
        JSONObject jsonResult = new JSONObject();
        String url = baseNewUrl + "getbrandlist";
        String categoryid = request.getParameter("categoryid");
        JSONObject requestNews = new JSONObject();
        JSONObject code = new JSONObject();
        code.put("categoryid", categoryid);
        String realCode = AES.Encrypt(code.toString());  //加密
        requestNews.put(cipherdata, realCode);
        //发起请求
        String getResult = AES.post(url, requestNews);
        //对得到结果进行解密
        jsonResult = getResult(AES.Decrypt(getResult));
        JSONArray jsonArray = jsonResult.getJSONArray("datainfo");
        request.setAttribute("brands", jsonArray);
        String returnView = "recycle/testList";
        return new ModelAndView(returnView);
    }

    /**
     * 用于外部调用登录
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/recycle/testGetModels")
    @ResponseBody
    public ResultData login(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        JSONObject jsonObject = new JSONObject();
        try {
            String categoryid = request.getParameter("categoryid");
            String brandId = request.getParameter("brandId");
            String keyword = request.getParameter("keyword");
            JSONObject jsonResult = new JSONObject();
            String url = baseNewUrl + "getmodellist";
            JSONObject requestNews = new JSONObject();
            //调用接口需要加密的数据
            JSONObject code = new JSONObject();
            code.put("pageindex", 1);
            code.put("pagesize", 500);
            code.put("categoryid", categoryid);
            code.put("brandid", brandId);
            code.put("keyword", keyword);
            String realCode = AES.Encrypt(code.toString());  //加密
            requestNews.put(cipherdata, realCode);
            //发起请求
            String getResult = AES.post(url, requestNews);
            //对得到结果进行解密
            jsonResult = getResult(AES.Decrypt(getResult));
            JSONArray jsonArray = jsonResult.getJSONArray("datainfo");
            JSONObject jsonObject1 = (JSONObject) jsonArray.get(0);
            JSONArray jsonArray1 = jsonObject1.getJSONArray("sublist");
            getResult(result, jsonArray1, true, "0", "成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }

    /**
     * queryListForPage
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/recycle/testListForPage")
    public void queryListForPage(HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        //获取查询条件
        String queryStartTime = request.getParameter("query_startTime");
        String queryEndTime = request.getParameter("query_endTime");
        String mobile = request.getParameter("mobile");
        String brandId = request.getParameter("brandId");
        String productId = request.getParameter("productId");
        String channel = request.getParameter("channel");//渠道
        String isOrder = request.getParameter("isOrder");//是否成单
        String isVisit = request.getParameter("isVisit");//是否回访

        Page page = getPageByRequest(request);
        RecycleCheckItems checkItem = new RecycleCheckItems();
        checkItem.setQueryStartTime(queryStartTime);
        checkItem.setQueryEndTime(queryEndTime);
        checkItem.setLoginMobile(mobile);
        checkItem.setBrandId(brandId);
        checkItem.setProductId(productId);
        if (StringUtils.isNotBlank(isOrder)) {
            checkItem.setIsOrder(isOrder);
        }
        if (StringUtils.isNotBlank(isVisit)) {
            checkItem.setIsVisit(isVisit);
        }
        checkItem.setPage(page);
        List<Map> checkItems = checkItemsService.getDao().queryTestListForPage(checkItem);
        String url = "";
        for (Map map : checkItems) {
            JSONObject jsonResult = new JSONObject();
            String productId1 = map.get("product_id").toString();
            String items = map.get("items").toString();
            List<Map<String, String>> lists = new ArrayList<>();
            List<String> list1 = new ArrayList();
            List<String> lists2 = new ArrayList<>();
            if(items.contains("|")){
                url = baseUrl + "getchecklist";
                List<String> list = Arrays.asList(items.split("\\|"));
                for (int q = 0; q < list.size(); q++) {
                    String[] a = list.get(q).split(",");
                    Map<String, String> maps = new HashMap<>();
                    if ("".equals(a[0])) {
                        list1.add(a[1]);
                    } else {
                        maps.put(a[0], a[1]);
                        lists.add(maps);
                    }
                }
            }else{
                url = baseNewUrl + "getchecklist";
                lists2 = Arrays.asList(items.split(","));
            }
            JSONObject requestNews = new JSONObject();
            //调用接口需要加密的数据
            JSONObject code = new JSONObject();
            code.put("productid", productId1);
            String realCode = AES.Encrypt(code.toString());  //加密
            requestNews.put(cipherdata, realCode);
            //发起请求
            String getResult = AES.post(url, requestNews);
            //对得到结果进行解密
            jsonResult = (JSONObject) JSONObject.parse(AES.Decrypt(getResult));
            JSONObject jsonObject = jsonResult.getJSONObject("datainfo");
            JSONArray questions = jsonObject.getJSONArray("questions");
            StringBuilder sb = new StringBuilder();
            StringBuilder sb2 = new StringBuilder();
            for (int i = 0; i < questions.size(); i++) {
                if(items.contains("|")) {
                    if ("9999".equals(((JSONObject) questions.get(i)).getString("id")) && list1 != null) {
                        JSONArray answers = ((JSONObject) questions.get(i)).getJSONArray("answers");
                        for (int j = 0; j < answers.size(); j++) {
                            for (String str : list1) {
                                if (str.equals(((JSONObject) answers.get(j)).getString("id"))) {
                                    sb2.append(((JSONObject) answers.get(j)).getString("name"));
                                    sb2.append(" 、 ");
                                }
                            }

                        }
                    }else {
                        for (Map<String, String> map1 : lists) {
                            Set<String> set = map1.keySet();
                            for (String key : set) {
                                if (key.equals(((JSONObject) questions.get(i)).getString("id"))) {
                                    JSONArray answers = ((JSONObject) questions.get(i)).getJSONArray("answers");
                                    for (int j = 0; j < answers.size(); j++) {
                                        if (map1.get(key).equals(((JSONObject) answers.get(j)).getString("id"))) {
                                            sb.append(((JSONObject) answers.get(j)).getString("name"));
                                            sb.append(" 、 ");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }else{
                    int sum=questions.size();
                    if(i<sum-1){
                            JSONArray answers = ((JSONObject) questions.get(i)).getJSONArray("answers");
                            for (int j = 0; j < answers.size(); j++) {
                                if (lists2.get(i).equals(((JSONObject) answers.get(j)).getString("id"))) {
                                    sb.append(((JSONObject) answers.get(j)).getString("name"));
                                    sb.append(" 、 ");
                                }
                            }
                    }else{
                        for(int a=lists2.size();a>sum-2;a--){
                            JSONArray answers = ((JSONObject) questions.get(i)).getJSONArray("answers");
                            for (int j = 0; j < answers.size(); j++) {
                                    if (lists2.get(a-1).equals(((JSONObject) answers.get(j)).getString("id"))) {
                                        sb2.append(((JSONObject) answers.get(j)).getString("name"));
                                        sb2.append(" 、 ");
                                    }
                            }
                        }
                    }
                }
            }
            sb.append(sb2.toString());
            if(sb.length()>1){
                sb.deleteCharAt(sb.length() - 1);
                map.put("product_id", sb.toString());
            }else{
                map.put("product_id", "");
            }

        }
        page.setData(checkItems);
        this.renderJson(response, page);
    }

    /**
     * 查看
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "recycle/recycleTestDetail")
    public ModelAndView recycleTestDetail(HttpServletRequest request,
                                          HttpServletResponse response) throws Exception {
        String id = request.getParameter("id");
        String itemName = request.getParameter("product");//检测项
        RecycleCheckItems checkItems = checkItemsService.getDao().queryByTestId(id);

        RecycleTest recycleTest = testService.getDao().queryByCheckId(checkItems.getId());
        if (StringUtils.isNotBlank(recycleTest.getRecycleId())) {
            RecycleOrder recycleOrder = orderService.queryById(recycleTest.getRecycleId());
            request.setAttribute("recycleOrderNo", recycleOrder.getOrderNo());
            request.setAttribute("recycleOrderId", recycleOrder.getId());
        }
        request.setAttribute("itemName", itemName);
        request.setAttribute("recycleTest", recycleTest);
        request.setAttribute("checkItems", checkItems);
        String returnView = "recycle/testDetail";
        return new ModelAndView(returnView);
    }

    /**
     * 回访跳转
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "recycle/recycleTestRecord")
    public ModelAndView recycleTestRecord(HttpServletRequest request,
                                          HttpServletResponse response) throws Exception {
        String id = request.getParameter("id");
        String itemName = request.getParameter("product");//检测项
        RecycleCheckItems checkItems = checkItemsService.getDao().queryByTestId(id);

        request.setAttribute("itemName", itemName);
        request.setAttribute("checkItems", checkItems);
        String returnView = "recycle/testRecord";
        return new ModelAndView(returnView);
    }


    /**
     * 保存测试备注
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/recycle/saveTestNote")
    @ResponseBody
    public ResultData saveTestNote(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        try {
            String checkItemsId = request.getParameter("id");
            String note = request.getParameter("note");
            SessionUser su = getCurrentUser(request);
            if (StringUtils.isBlank(checkItemsId) || StringUtils.isBlank(note)) {
                return getResult(result, null, false, "1", "参数为空");
            }
            RecycleTest recycleTest = new RecycleTest();
            recycleTest.setCheckItemsId(checkItemsId);
            recycleTest.setNote(note);
            recycleTest.setRecordName(su.getUserId());
            recycleTestService.add(recycleTest);

            getResult(result, null, true, "0", "成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }


    /**
     * edit
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/recycle/goTestSubmit")
    public ModelAndView goTestSubmit(HttpServletRequest request,
                                     HttpServletResponse response) {
        try {
            String id = request.getParameter("id");

            RecycleCheckItems checkItems = recycleCheckItemsService.getDao().queryByTestId(id);
            JSONObject jsonResult = new JSONObject();
            String url = baseNewUrl + "getchecklist";
            String productId1 = checkItems.getProductId();
            String items = checkItems.getItems();
            List<Map<String, String>> lists = new ArrayList<>();
            List<String> list1 = new ArrayList();
            List<String> lists2 = new ArrayList<>();
            if(items.contains("|")){
                url = baseUrl + "getchecklist";
                List<String> list = Arrays.asList(items.split("\\|"));
                for (int q = 0; q < list.size(); q++) {
                    String[] a = list.get(q).split(",");
                    Map<String, String> maps = new HashMap<>();
                    if ("".equals(a[0])) {
                        list1.add(a[1]);
                    } else {
                        maps.put(a[0], a[1]);
                        lists.add(maps);
                    }
                }
            }else{
                url = baseNewUrl + "getchecklist";
                lists2 = Arrays.asList(items.split(","));
            }
            JSONObject requestNews = new JSONObject();
            //调用接口需要加密的数据
            JSONObject code = new JSONObject();
            code.put("productid", productId1);
            String realCode = AES.Encrypt(code.toString());  //加密
            requestNews.put(cipherdata, realCode);
            //发起请求
            String getResult = AES.post(url, requestNews);
            //对得到结果进行解密
            jsonResult = (JSONObject) JSONObject.parse(AES.Decrypt(getResult));
            JSONObject jsonObject = jsonResult.getJSONObject("datainfo");
            JSONArray questions = jsonObject.getJSONArray("questions");
            StringBuilder sb = new StringBuilder();
            StringBuilder sb2 = new StringBuilder();
            for (int i = 0; i < questions.size(); i++) {
                if(items.contains("|")) {
                    if ("9999".equals(((JSONObject) questions.get(i)).getString("id")) && list1 != null) {
                        JSONArray answers = ((JSONObject) questions.get(i)).getJSONArray("answers");
                        for (int j = 0; j < answers.size(); j++) {
                            for (String str : list1) {
                                if (str.equals(((JSONObject) answers.get(j)).getString("id"))) {
                                    sb2.append(((JSONObject) answers.get(j)).getString("name"));
                                    sb2.append(" 、 ");
                                }
                            }

                        }
                    }else {
                        for (Map<String, String> map1 : lists) {
                            Set<String> set = map1.keySet();
                            for (String key : set) {
                                if (key.equals(((JSONObject) questions.get(i)).getString("id"))) {
                                    JSONArray answers = ((JSONObject) questions.get(i)).getJSONArray("answers");
                                    for (int j = 0; j < answers.size(); j++) {
                                        if (map1.get(key).equals(((JSONObject) answers.get(j)).getString("id"))) {
                                            sb.append(((JSONObject) answers.get(j)).getString("name"));
                                            sb.append(" 、 ");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }else{
                    int sum=questions.size();
                    if(i<sum-1){
                        JSONArray answers = ((JSONObject) questions.get(i)).getJSONArray("answers");
                        for (int j = 0; j < answers.size(); j++) {
                            if (lists2.get(i).equals(((JSONObject) answers.get(j)).getString("id"))) {
                                sb.append(((JSONObject) answers.get(j)).getString("name"));
                                sb.append(" 、 ");
                            }
                        }
                    }else{
                        for(int a=lists2.size();a>sum-2;a--){
                            JSONArray answers = ((JSONObject) questions.get(i)).getJSONArray("answers");
                            for (int j = 0; j < answers.size(); j++) {
                                if (lists2.get(a-1).equals(((JSONObject) answers.get(j)).getString("id"))) {
                                    sb2.append(((JSONObject) answers.get(j)).getString("name"));
                                    sb2.append(" 、 ");
                                }
                            }
                        }
                    }
                }
            }
            sb.append(sb2.toString());
            sb.deleteCharAt(sb.length() - 1);
            //获取省份地址
            List<Address> provinceL = addressService.queryByPid("0");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            checkItems.setIsVisit(sdf.format(checkItems.getInTime()));

            if (StringUtils.isBlank(checkItems.getBrand()) && StringUtils.isNotBlank(checkItems.getRecycleModel())) {
                checkItems.setRecycleModel(checkItems.getRecycleModel());
            } else if (StringUtils.isNotBlank(checkItems.getBrand()) && StringUtils.isBlank(checkItems.getRecycleModel())) {
                checkItems.setRecycleModel(checkItems.getBrand());
            } else if (StringUtils.isNotBlank(checkItems.getBrand()) && StringUtils.isNotBlank(checkItems.getRecycleModel())) {
                checkItems.setRecycleModel(checkItems.getBrand() + "/" + checkItems.getRecycleModel());
            }

            JSONObject requestNews2 = new JSONObject();
            //调用接口需要加密的数据
            JSONObject code2 = new JSONObject();
            code2.put("pageindex", 1);
            code2.put("pagesize", 500);
            code2.put("categoryid", 1);
            code2.put("brandid", checkItems.getBrandId());
            code2.put("keyword", null);
            String realCode2 = AES.Encrypt(code2.toString());  //加密
            requestNews2.put(cipherdata, realCode2);
            //发起请求
            url = baseNewUrl + "getmodellist";
            String getResult2 = AES.post(url, requestNews2);
            //对得到结果进行解密
            JSONObject jsonResult2 = getResult(AES.Decrypt(getResult2));
            //将结果中的产品id转为string类型  json解析 long类型精度会丢失
            //防止返回机型信息为空
            if (StringUtil.isNotBlank(jsonResult2.getString("datainfo")) && !(jsonResult2.getJSONArray("datainfo")).isEmpty()) {
                JSONArray jq = jsonResult2.getJSONArray("datainfo");
                JSONObject jqs = (JSONObject) jq.get(0);
                JSONArray j = jqs.getJSONArray("sublist");
                for (int i = 0; i < j.size(); i++) {
                    JSONObject js = j.getJSONObject(i);
                    if(productId1.equals(js.getString("productid"))){
                        String imgaePath = js.getString("modellogo");
                        if (StringUtil.isBlank(imgaePath)) {
                            String realUrl = request.getRequestURL().toString();
                            String domain = realUrl.replace(request.getRequestURI(), "");
                            String path = domain + "/" + SystemConstant.DEFAULTIMAGE;
                            imgaePath = path;
                        }
                        request.setAttribute("imagePath", imgaePath);
                        break;
                    }
                    //如果该机型的图片地址为空 则使用默认图片地址
                }
            }

            request.setAttribute("itemName", sb.toString());
            request.setAttribute("provinceL", provinceL);
            request.setAttribute("checkItems", checkItems);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String returnView = "recycle/testSubmitOrder";
        return new ModelAndView(returnView);
    }

    /**
     * 测试提交订单
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/recycle/testSubmitOrder")
    @ResponseBody
    public ResultData testSubmitOrder(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        String url = baseNewUrl + "createorder";
        try {
            String checkItemsId = request.getParameter("submitCheckId");
            String name = request.getParameter("name");
            String mobile = request.getParameter("mobile");
            String province = request.getParameter("addProvince");
            String city = request.getParameter("addCity");
            String area = request.getParameter("addCounty");
            String address = request.getParameter("addAddress");
            String recycleType = request.getParameter("payType");   //支付类型 1支付宝收款  2话费充值
            String payMobile = request.getParameter("payMobile");
            String imagePath = request.getParameter("imagePath");
            String detail = request.getParameter("itemName");
            String takeTime = request.getParameter("takeTime");
            String source = request.getParameter("source");     //  1公众号   2欢GO
            String mailType = request.getParameter("mailType"); //  1超人系统推送  2用户自行邮寄
            String note = request.getParameter("note");
            String couponCode = request.getParameter("couponCode");//加价券编码recordNote
            String recordNote = request.getParameter("recordNote");//回访备注
            RecycleOrder order = new RecycleOrder();
            if (StringUtil.isBlank(checkItemsId) || StringUtil.isBlank(name) || StringUtil.isBlank(mobile) ||
                    StringUtil.isBlank(province) || StringUtil.isBlank(city) || StringUtil.isBlank(area) ||
                    StringUtil.isBlank(address) || StringUtil.isBlank(recycleType)|| StringUtil.isBlank(payMobile)) {
                return getResult(result, null, false, "2", "请填写完整信息");
            }
            RecycleCheckItems checkItems = checkItemsService.getDao().queryByTestId(checkItemsId);
            String quoteid = checkItems.getQuoteId();
            if(StringUtils.isBlank(quoteid)){
                JSONObject requestNews = new JSONObject();
                JSONObject quoteidjsonResult = new JSONObject();
                String quoteidurl = "";
                //调用接口需要加密的数据
                JSONObject code = new JSONObject();
                code.put("productid", checkItems.getProductId());
                String items=checkItems.getItems();
                if(items.contains("|")){
                    quoteidurl = baseUrl + "getprice";
                }else{
                    quoteidurl = baseNewUrl + "getprice";
                }
                code.put("items", items);
                String realCode = AES.Encrypt(code.toString());  //加密
                requestNews.put(cipherdata, realCode);
                //发起请求
                String getResult = AES.post(quoteidurl, requestNews);
                //对得到结果进行解密
                quoteidjsonResult = getResult(AES.Decrypt(getResult));
                //将quoteid转为string
                if (StringUtil.isNotBlank(quoteidjsonResult.getString("datainfo"))) {
                    JSONObject j = (JSONObject) quoteidjsonResult.get("datainfo");
                    quoteid=j.getString("quoteid");
                    checkItems.setQuoteId(quoteid);
                    checkItemsService.saveUpdate(checkItems);
                }
            }
            String openId = checkItems.getWechatId();
            //对重要信息作详细判断
            if (Integer.valueOf(recycleType) == 2) {
                if (payMobile.length() != 11 || mobile.length() != 11) {
                    return getResult(result, null, false, "2", "请输入正确的手机号码");
                }
            }
            //判断数据 是否符合规范
            if (name.length() > 12 || address.length() > 64) {
                return getResult(result, null, false, "2", "部分信息长度过长");
            }
            RecycleCoupon recycleCoupon = new RecycleCoupon();
            if (StringUtils.isNotBlank(couponCode)) {
                recycleCoupon = recycleCouponService.queryByCode(couponCode);
                if (recycleCoupon == null) {
                    return getResult(result, null, false, "2", "加价码输入错误");
                } else if (1 == recycleCoupon.getIsUse()) {
                    return getResult(result, null, false, "2", "加价码已被使用");
                }
            }

            //设置下单间隔时间,最少3秒
            Long time = System.currentTimeMillis();
            Object requestTimes = request.getSession().getAttribute("times");
            if (requestTimes == null) {
                request.getSession().setAttribute("times", time);
            } else {
                long realTime = (long) (requestTimes);
                if ((time - realTime) < 5000) {
                    return getResult(result, null, false, "2", "您下单过于频繁，请稍后重试!");
                }
            }

            //转化时间格式
            if (StringUtil.isNotBlank(takeTime)) {
                takeTime = RecycleController.getDate(takeTime);
            }
            //转化地址
            Address provinceName = addressService.queryByAreaId(province);
            Address cityName = addressService.queryByAreaId(city);
            Address areaName = addressService.queryByAreaId(area);
            if (provinceName == null || cityName == null || areaName == null) {
                return getResult(result, null, false, "2", "请确认地址信息是否无误");
            }
            String areaname = RecycleController.getProvince(provinceName.getArea()) + cityName.getArea() + " " + areaName.getArea();


            //先保存订单到超人平台再调用回收平台下单接口  返回成功则更新订单状态
            String id = UUID.randomUUID().toString().replace("-", "");
            String customerId = UUID.randomUUID().toString().replace("-", "");
            order.setId(id);
            order.setOrderNo("");
            order.setOrderType(1);             //正式订单
            order.setRecycleType(1);           //普通回收
            order.setExchangeType(Integer.parseInt(recycleType));
            order.setPercent(0);
            order.setPreparePrice(new BigDecimal(0));
            order.setImagePath(imagePath);
            order.setDetail(detail);
            order.setMobile(mobile);
            order.setCustomerId(customerId);
            order.setPayMobile(payMobile);
            order.setTakeTime(takeTime);
            order.setOrderStatus(1);           //订单已生成
            order.setNote(note);
            order.setCheckId(quoteid);
            if (StringUtils.isNotBlank(mailType)) {
                order.setMailType(Integer.parseInt(mailType));
            } else {
                order.setMailType(1);        //快递类型   1超人系统推送
            }
            if (!source.equals("null") && StringUtils.isNotBlank(source)) {
                String sourceType = source;
                if (source.contains("?")) {
                    sourceType = org.apache.commons.lang3.StringUtils.substringBefore(source, "?");
                }
                order.setSourceType(Integer.parseInt(sourceType));
            } else {
                order.setSourceType(1);        //订单来源  默认微信公众号来源
            }
            //通过quoteid获取机型信息
            JSONObject postNews = new JSONObject();
            if(checkItems.getItems().contains("|")){
                postNews = post(quoteid);
            }else {
                postNews = postNews(quoteid);
            }
            order.setProductName(postNews.getString("modelname"));
            order.setPrice(new BigDecimal(postNews.getString("price")));
            //判断该订单是否来源于微信小程序
            if (StringUtils.isNotBlank(openId)) {
                order.setWechatOpenId(openId);
            }
            if (StringUtils.isNotBlank(couponCode)) {
                if (order.getPrice().intValue() < recycleCoupon.getSubtraction_price().intValue()) {
                    return getResult(result, null, false, "2", "加价券的使用不满足条件");
                }
                order.setCouponId(recycleCoupon.getId());
                recycleCouponService.updateForUse(recycleCoupon);
            }
            recycleOrderService.add(order);     //添加预付订单记录

            SessionUser su = getCurrentUser(request);
            RecycleTest recycleTest = new RecycleTest();
            recycleTest.setNote(recordNote);
            recycleTest.setRecordName(su.getUserId());
            recycleTest.setRecycleId(order.getId());
            recycleTest.setCheckItemsId(checkItemsId);
            recycleTestService.add(recycleTest);//添加回访记录

            //保存用户信息
            RecycleCustomer cust = new RecycleCustomer();
            cust.setId(customerId);
            cust.setName(name);
            cust.setMobile(mobile);
            cust.setProvince(province);
            cust.setCity(city);
            cust.setCounty(area);
            cust.setAddressType(2);  //系统地址
            cust.setArea(areaname);
            cust.setAddress(address);
            recycleCustomerService.add(cust);  //添加用户信息


            //请求回收平台调用下单接口
            JSONObject requestNews = new JSONObject();
            //调用接口需要加密的数据
            JSONObject code = new JSONObject();
            code.put("quoteid", quoteid);                                      //流水号
            code.put("pricetype", Integer.parseInt(recycleType));              //付款类型
            code.put("payment_name", name);                                    //结款户名
            code.put("payment_account", payMobile);                            //结款账户
            code.put("contactname", name);                                     //联系人
            code.put("contactphone", mobile);                                  //联系电话
            code.put("areaname", areaname);                                     //省市区
            code.put("address", address);                                      //详细地址
            if (StringUtils.isNotBlank(couponCode)) {
                JSONArray jsonArray = new JSONArray();
                JSONObject json = new JSONObject();
                json.put("couponId", recycleCoupon.getCouponCode());
                json.put("actType", recycleCoupon.getPricingType());
                if (1 == (recycleCoupon.getPricingType())) {
                    json.put("percent", recycleCoupon.getStrCouponPrice().divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_DOWN));
                    json.put("addFee", 0);
                } else if (2 == (recycleCoupon.getPricingType())) {
                    json.put("percent", 0);
                    json.put("addFee", recycleCoupon.getStrCouponPrice().intValue());
                }
                json.put("up", recycleCoupon.getUpperLimit());
                json.put("low", recycleCoupon.getSubtraction_price());
                json.put("desc", recycleCoupon.getRuleDescription());
                jsonArray.add(json);
                code.put("coupon_rule", jsonArray.toJSONString());
            }
            String realCode = AES.Encrypt(code.toString());  //加密
            requestNews.put(cipherdata, realCode);
            //发起请求
            String getResult = AES.post(url, requestNews);
            //对得到结果进行解密
            jsonResult = getResult(AES.Decrypt(getResult));

            //订单号传过来为long型转为string防止精度丢失
            if (StringUtil.isNotBlank(jsonResult.getString("datainfo"))) {
                JSONObject j = (JSONObject) jsonResult.get("datainfo");
                j.put("orderid", j.getString("orderid"));
                //订单提交回收平台成功 更新订单状态
                order = recycleOrderService.queryById(id);
                order.setOrderNo(j.getString("orderid"));
                //更新回收订单：订单号
                recycleOrderService.saveUpdate(order);
                //订单提交成功 当用户选择超人系统推送时  调用顺丰取件接口
                if (mailType.equals("1")) {
                    try {
                        String mailNo = postSfOrder(order);
                        order.setSfOrderNo(mailNo);
                        order.setOrderStatus(2);
                        recycleOrderService.saveUpdate(order);
                        log.info("顺丰推送成功");
                    } catch (Exception e) {
                        //记录顺丰推送异常信息
                        PushsfException exception = new PushsfException();
                        exception.setShNo(UUID.randomUUID().toString().replace("-", ""));
                        exception.setOrderNo(order.getOrderNo());
                        exception.setShExceptin(e.getMessage());
                        pushsfExceptionService.add(exception);
                        request.getSession().setAttribute("times", time);
                        return getResult(result, null, false, "2", "推送顺丰快递失败，请检查收件地址是否正确");
                    }
                }

            } else {
                order.setIsDel(1);
            }
            //更新回收订单
            recycleOrderService.saveUpdate(order);

            result.setResult(jsonResult);
            result.setResultCode("0");
            result.setSuccess(true);
            //下单成功后更新下单间隔时间
            request.getSession().setAttribute("times", time);
            getResult(result, null, true, "0", "成功");
        } catch (SystemException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            getResult(result, null, false, "2", "系统异常");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            getResult(result, null, false, "2", "系统异常");
        }
        return result;
    }

    /**
     * 发起订单物流请求
     *
     * @param order
     * @throws Exception
     */
    public String postSfOrder(RecycleOrder order) throws Exception {
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
    /**
     * 通过quoteid查询 订单信息
     */
    public JSONObject post(String quoteId) throws Exception {
        JSONObject requestNews = new JSONObject();
        //调用接口需要加密的数据
        String url = baseUrl + "getquotedetail";
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
     * 返回结果解析
     *
     * @param originalString
     * @return
     */
    public static JSONObject getResult(String originalString) {
        JSONObject result = (JSONObject) JSONObject.parse(originalString);
        if (result.getString("result") != null && !result.getString("result").equals("RESPONSESUCCESS")) {
            throw new SystemException(result.getString("msg"));
        }
        return result;
    }
}
