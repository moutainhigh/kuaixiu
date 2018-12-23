package com.kuaixiu.wechat.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.exception.SystemException;
import com.common.paginate.Page;
import com.common.util.SmsSendUtil;
import com.kuaixiu.brand.entity.Brand;
import com.kuaixiu.brand.service.BrandService;
import com.kuaixiu.coupon.entity.Coupon;
import com.kuaixiu.coupon.entity.CouponModel;
import com.kuaixiu.coupon.entity.CouponProject;
import com.kuaixiu.coupon.service.CouponModelService;
import com.kuaixiu.coupon.service.CouponProjectService;
import com.kuaixiu.coupon.service.CouponService;
import com.kuaixiu.customer.entity.Customer;
import com.kuaixiu.engineer.entity.Engineer;
import com.kuaixiu.engineer.service.EngineerService;
import com.kuaixiu.model.entity.Model;
import com.kuaixiu.model.entity.RepairCost;
import com.kuaixiu.model.service.ModelService;
import com.kuaixiu.model.service.RepairCostService;
import com.kuaixiu.order.constant.OrderConstant;
import com.kuaixiu.order.entity.Order;
import com.kuaixiu.order.service.FromSystemService;
import com.kuaixiu.order.service.OrderService;
import com.kuaixiu.project.entity.Project;
import com.kuaixiu.project.service.ProjectService;
import com.kuaixiu.shop.entity.Shop;
import com.kuaixiu.shop.entity.ShopModel;
import com.kuaixiu.shop.service.ShopModelService;
import com.kuaixiu.shop.service.ShopService;
import com.system.api.entity.ResultData;
import com.system.basic.address.entity.Address;
import com.system.basic.address.service.AddressService;
import com.system.basic.user.entity.LoginUser;
import com.system.basic.user.entity.SysUser;
import com.system.basic.user.service.LoginUserService;
import com.system.basic.user.service.SessionUserService;
import com.system.basic.user.service.SysUserService;
import com.system.constant.ApiResultConstant;
import jodd.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

/**
 * 微信维修controller
 *
 * @author yq
 */
@Controller
public class WechatRepairController extends BaseController {
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ModelService modelService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private RepairCostService repairCostService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private SysUserService userService;
    @Autowired
    private SessionUserService sessionUserService;
    @Autowired
    private ShopService shopService;
    @Autowired
    private CouponModelService couponModelService;
    @Autowired
    private CouponProjectService couponProjectService;
    @Autowired
    private EngineerService engineerService;
    @Autowired
    private ShopModelService shopModelService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private LoginUserService loginUserService;

    /**
     * 移动端首页
     */
    @RequestMapping("/wechat/repair/index")
    public String goIndex(HttpServletRequest request, HttpServletResponse response) {
        request.getRequestedSessionId();
        String path = request.getContextPath();
        String basePath = request.getScheme() + "://"
                + request.getServerName() + ":" + request.getServerPort()
                + path + "/";
        String fm = request.getParameter("fm");   //系统来源标示
        if (StringUtil.isBlank(fm)) {
            fm = "5";   //公众号客户端
        }
        String index = "wechat/index.html?fm=" + fm;
        String realPath = basePath + index;
        try {
            response.sendRedirect(realPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
        //return "wechat/index";
    }


    /**
     * 获得手机品牌
     *
     * @throws IOException
     */
    @RequestMapping("/wechat/repair/brandList")
    @ResponseBody
    public void getBrandList(HttpServletResponse response) throws IOException {
        ResultData result = new ResultData();
        try {
            //查询所有品牌
            List<Brand> list = brandService.queryList(null);
            if (list != null && list.size() != 0) {
                JSONObject jsonResult = new JSONObject();
                JSONArray array = new JSONArray();
                for (Brand b : list) {
                    JSONObject j = new JSONObject();
                    j.put("brandId", b.getId());
                    j.put("brandName", b.getName());
                    array.add(j);
                }
                jsonResult.put("data", array);
                result.setResult(jsonResult);
                sessionUserService.getSuccessResult(result, jsonResult);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        renderJson(response, result);
    }


    /**
     * 获得手机型号
     *
     * @throws IOException
     */
    @RequestMapping("/wechat/repair/modelList")
    @ResponseBody
    public void getModelList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResultData result = new ResultData();
        try {
            JSONObject params = getPrarms(request);
            String brandId = params.getString("brandId");
            if (StringUtils.isBlank(brandId)) {
                throw new SystemException(ApiResultConstant.resultCode_str_1001, ApiResultConstant.resultCode_1001);
            }
            List<Model> modelList = modelService.queryByBrandId(brandId);
            if (modelList != null && modelList.size() != 0) {
                JSONObject jsonResult = new JSONObject();
                JSONArray array = new JSONArray();
                for (Model model : modelList) {
                    JSONObject j = new JSONObject();
                    j.put("modelId", model.getId());
                    j.put("modelName", model.getName());
                    array.add(j);
                }
                jsonResult.put("data", array);
                result.setResult(jsonResult);
                sessionUserService.getSuccessResult(result, jsonResult);
            } else {
                result.setResultMessage("该品牌不存在");
            }
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        renderJson(response, result);
    }


    /**
     * 根据手机型号得到该机型的颜色和可维修项目
     *
     * @throws IOException
     */
    @RequestMapping("/wechat/repair/modelInfo")
    @ResponseBody
    public void getModelInfo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResultData result = new ResultData();
        try {
            JSONObject params = getPrarms(request);
            String pageIndex = params.getString("pageIndex");//页码数
            String pageSize = params.getString("pageSize");//每页加
            String modelId = params.getString("modelId");//手机型号
            if (StringUtils.isBlank(modelId)) {
                throw new SystemException(ApiResultConstant.resultCode_str_1001, ApiResultConstant.resultCode_1001);
            }
            Page page = new Page();
            Integer Index = Math.abs(Integer.valueOf(pageIndex));
            Integer Size = Math.abs(Integer.valueOf(pageSize));
            page.setStart((Index-1)*Size);
            page.setPageSize(Size);
            Model model = modelService.queryById(modelId);//得到所选机型信息
            RepairCost repairCost = new RepairCost();
            repairCost.setModelId(modelId);
            repairCost.setPage(page);
            List<RepairCost> repairCostList = repairCostService.getDao().queryProjectForPage(repairCost);//得到所选机型可维修项目
            if (model == null || repairCostList == null || repairCostList.size() == 0) {
                throw new SystemException("该机型不存在");
            } else {
                int orderSize = page.getRecordsTotal();//总记录数
                JSONObject jsonResult = new JSONObject();
                JSONArray array = new JSONArray();
                for (RepairCost r : repairCostList) {
                    JSONObject j = new JSONObject();
                    j.put("projectId", r.getProjectId());
                    j.put("projectName", r.getProjectName());
                    j.put("price", r.getPrice());
                    array.add(j);
                }
                jsonResult.put("modelColor", model.getColor());
                jsonResult.put("data", array);
                jsonResult.put("recordsTotal", orderSize);
                result.setResult(jsonResult);
                sessionUserService.getSuccessResult(result, jsonResult);
            }
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        renderJson(response, result);
    }


    /**
     * /**
     * 保存订单
     *
     * @throws IOException
     */
    @RequestMapping("/wechat/repair/saveOrder")
    @ResponseBody
    public void saveOrder(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        try {
            JSONObject params = getPrarms(request);
            String brandId = params.getString("brandId");                    //获取品牌
            String modelId = params.getString("modelId");                    //获取机型
            String color = params.getString("color");                       //获取机型颜色
            String repairType = params.getString("repairType");              //获取维修方式
            String customerName = params.getString("customerName");          //获取用户名
            String mobile = params.getString("mobile");                      //获取手机号
            String checkCode = params.getString("checkCode");                //获取验证码
            String province = params.getString("province");                  //获取省份id
            String city = params.getString("city");                          //获取城市id
            String area = params.getString("area");                          //获取区id
            String county = params.getString("county");                      //获取县,镇id  可能没有
            String street = params.getString("street");                      //获取街道
            String note = params.getString("note");                          //获取备注
            String couponCode = params.getString("couponCode");              //优惠券
            String projects = params.getString("projects");                   //获取维修故障
            String fm = params.getString("fm");                              //系统来源
            String engineerNo = params.getString("engineerId");                //工程师工号
            if (!checkRandomCode(request, mobile, checkCode)) {                 //验证手机号和验证码
                throw new SystemException("手机号或验证码输入错误");
            }
            if (StringUtils.isNotBlank(repairType)) {
                if (StringUtils.isNotBlank(engineerNo) && Integer.valueOf(repairType) == 3) {
                    throw new SystemException("扫描二维码下单不能寄修");
                }
            }
            if (StringUtils.isBlank(fm)) {
                //如果为空 默认订单来源m超人H5
                fm = "9";
            }
            removeRandomCode(request, mobile);                              //验证成功移除验证码
            Customer cust = new Customer();                                        //用户信息
            cust.setName(customerName);
            cust.setMobile(mobile);
            cust.setProvince(province);
            cust.setCity(city);
            cust.setCounty(area);
            cust.setStreet("0");
            Address provinceName = addressService.queryByAreaId(province);
            Address cityName = addressService.queryByAreaId(city);
            Address areaName = addressService.queryByAreaId(area);
            String countyName = "";//县，镇信息
            if (StringUtils.isNotBlank(county)) {
                Address a = addressService.queryByAreaId(county);
                countyName = " " + a.getArea();
            }
            if (provinceName == null || cityName == null || areaName == null) {
                throw new SystemException("请确认地址信息是否无误");
            }
            String areas = provinceName.getArea() + " " + cityName.getArea() + " " + areaName.getArea() + countyName;
            cust.setAreas(areas);                                                 // 省市区县地址拼接
            cust.setAddress(street);                                              //街道具体信息

            Order o = new Order();                                                //订单信息
            o.setBrandId(brandId);
            o.setModelId(modelId);
            o.setColor(color);
            o.setIsMobile(1);
            if (StringUtils.isNotBlank(repairType) && StringUtils.isBlank(engineerNo)) {
                o.setRepairType(Integer.parseInt(repairType));
            } else if (StringUtils.isNotBlank(engineerNo) && !"".equals(engineerNo)) {
                o.setRepairType(4);
            } else {
                o.setRepairType(0);
            }
            o.setPostscript(note);
            o.setCouponCode(couponCode);
            o.setOrderStatus(OrderConstant.ORDER_STATUS_DEPOSITED);
            o.setFromSystem(fm);
            //为了接口方法统一化 此处修改projects的形式
            if (StringUtils.isBlank(projects)) {
                throw new SystemException("故障类型不能为空");
            }
            JSONArray json = JSONArray.parseArray(projects);
            JSONArray pIds = new JSONArray();
            for (int i = 0; i < json.size(); i++) {
                JSONObject j = (JSONObject) json.get(i);
                String projectId = j.getString("projectId");
                if (StringUtils.isBlank(projectId)) {
                    continue;
                }
                pIds.add(projectId);
            }
            projects = pIds + "";//处理后转化为字符串格式

            int resultNews = 0;//判断上门维修是否有满足条件的门店
            if (o.getRepairType() == 3) {
                //如果是寄修
                Shop s = shopService.selectShop(province, city);
                if (s.getCode() != null) {
                    //满足寄修条件的门店存在  保存订单
                    orderService.sendSave(o, projects, cust, false);
                    //订单保存完成后派单给门店
                    o.setProviderCode(s.getProviderCode());
                    o.setProviderName(s.getProviderName());

                    o.setShopCode(s.getCode());
                    o.setShopName(s.getName());
                    o.setIsDispatch(2);
                    o.setDispatchTime(new Date());
                    o.setOrderStatus(OrderConstant.ORDER_STATUS_WAIT_SHOP_SEND_RECEIVE);
                    orderService.saveUpdate(o);
                    //向对应门店店主发送下单信息
                    SmsSendUtil.mailSendSmsToShop(s, o);
                }
            } else {
                Engineer eng = new Engineer();
                if (StringUtils.isNotBlank(engineerNo)) {
                    eng = engineerService.queryByEngineerNumber(engineerNo);
                    o.setProviderCode(eng.getProviderCode());
                    o.setProviderName(eng.getProviderName());
                    Shop shop = getEngCodeName(eng);
                    o.setShopCode(shop.getCode());
                    o.setShopName(shop.getName());
                    o.setEngineerName(eng.getName());
                    o.setEngineerNumber(engineerNo);
                    o.setEngineerMobile(eng.getMobile());
                    o.setEngineerId(eng.getId());
                    o.setDispatchTime(new Date());
                    o.setRepairType(4);
                    o.setIsDispatch(1);
                    o.setOrderStatus(OrderConstant.ORDER_STATUS_DISPATCHED);
                }
                //上门维修保存订单
                resultNews = orderService.save(o, projects, cust, false);
                if (resultNews == 10) {
                    //提示是否选择寄修门店
                    throw new SystemException("亲，您填写的地址附近没有维修门店。是否选择寄修？", ApiResultConstant.resultCode_3004);
                } else {
                    if (StringUtils.isBlank(engineerNo)) {
                        orderService.autoDispatch(o);                    //支付完成自动派单
                    } else {
                        //更改工程师状态
                        eng.setIsDispatch(1);
                        engineerService.saveUpdate(eng);
                        //给工程师发送短信提示
                        SmsSendUtil.sendSmsToEngineer(eng, new Shop(), o);
                    }
                }
            }
            SysUser user = userService.checkWechatLogin(mobile);                             //初始化session
            String sessionId = (String) request.getSession().getId();                           //当前sesionId
            LoginUser login = loginUserService.pcInitLoginUser(user, sessionId);                //得到登录用户
            sessionUserService.customerInitSessionUser(user, request, login.getAccessToken());//初始化session
            jsonResult.put("access_token", login.getAccessToken());                          //返还access_token 和订单号给用户端
            jsonResult.put("id", o.getId());
            sessionUserService.getSuccessResult(result, jsonResult);
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        renderJson(response, result);
    }


    public Shop getEngCodeName(Engineer engineer) {
        Shop shop = new Shop();
        if (engineer.getShopCode().contains(",")) {
            List<String> shopCodeList = Arrays.asList(engineer.getShopCode().split(","));
            StringBuilder sb = new StringBuilder();
            for (String shopCodeList1 : shopCodeList) {
                shop = shopService.queryByCode(shopCodeList1);
                break;
            }
        } else {
            shop = shopService.queryByCode(engineer.getShopCode());
        }
        return shop;
    }

    /**
     * 查询邮寄地址
     */
    @RequestMapping("/wechat/repair/sendAddress")
    public void sendAddress(HttpServletRequest request
            , HttpServletResponse response) throws IOException, ParseException {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        try {
            JSONObject params = getPrarms(request);
            String province = params.getString("province");
            String city = params.getString("city");
            if (StringUtils.isBlank(province) || StringUtils.isBlank(city)) {
                throw new SystemException(ApiResultConstant.resultCode_str_1007, ApiResultConstant.resultCode_1007);
            }
            Shop s = shopService.selectShop(province, city);//得到满足条件的门店
            JSONObject j = new JSONObject();
            j.put("fullAddress", s.getFullAddress());
            j.put("managerName", s.getManagerName());
            j.put("managerMobile", s.getManagerMobile());
            jsonResult.put("shop", j);
            sessionUserService.getSuccessResult(result, jsonResult);
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        renderJson(response, result);
    }


    /**
     * 加载商店列表
     *
     * @param request
     * @throws IOException
     */
    @RequestMapping("/wechat/repair/loadShopList")
    @ResponseBody
    public void loadShopList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResultData result = new ResultData();
        try {
            JSONObject params = getPrarms(request);
            String pageIndex = params.getString("pageIndex");//页码数
            String pageSize = params.getString("pageSize");//每页加载数
            String longitude = params.getString("longitude");//用户当前位置经度
            String latitude = params.getString("latitude");//用户当前位置纬度
            Page p = getPageByRequestParams(pageIndex, pageSize);//获取分页
            if (StringUtils.isBlank(longitude) || StringUtils.isBlank(latitude)) {
                throw new SystemException(ApiResultConstant.resultCode_str_1007, ApiResultConstant.resultCode_1007);
            }
            List<Shop> shopL = shopService.queryByLonAndLatForPage(new BigDecimal(longitude), new BigDecimal(latitude), p);
            JSONObject jsonResult = new JSONObject();
            if (shopL.size() == 0 || shopL == null) {
                throw new SystemException("附近暂时没有维修门店,敬请期待");
            } else {
                JSONArray array = new JSONArray();
                for (Shop s : shopL) {
                    ShopModel model = new ShopModel();
                    model.setShopCode(s.getCode());
                    List<ShopModel> shopList = shopModelService.queryList(model);
                    //只显示有维修能力的门店
                    if (shopList.isEmpty()) {
                        continue;
                    }
                    JSONObject j = new JSONObject();
                    j.put("shopFullAddress", s.getFullAddress());
                    j.put("tel", s.getTel());
                    j.put("distance", s.getDistance());
                    j.put("name", s.getName());
                    array.add(j);
                }
                jsonResult.put("data", array);
                jsonResult.put("shopSize", shopL.size());
                result.setResult(jsonResult);
            }
            sessionUserService.getSuccessResult(result, jsonResult);
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        renderJson(response, result);
    }

    /**
     * 优惠券信息
     *
     * @throws ParseException
     */
    @RequestMapping("/wechat/repair/couponInfo")
    public void couponInfo(HttpServletRequest request
            , HttpServletResponse response) throws IOException, ParseException {
        ResultData result = new ResultData();
        try {
            JSONObject params = getPrarms(request);
            Coupon c = userCheckCouponExisit(request, params);//检测优惠券
            List<CouponModel> couModels = couponModelService.queryListByCouponId(c.getId());
            List<CouponProject> couProjects = couponProjectService.queryListByCouponId(c.getId());
            JSONObject jsonResult = new JSONObject();
            JSONObject coupon = new JSONObject();//优惠券信息
            coupon.put("id", c.getId());
            coupon.put("couponName", c.getCouponName());
            coupon.put("couponPrice", c.getCouponPrice());
            coupon.put("beginTime", c.getBeginTime());
            coupon.put("note", c.getNote());
            coupon.put("endTime", c.getEndTime());
            JSONArray array = new JSONArray();//支持品牌
            for (CouponModel model : couModels) {
                JSONObject j = new JSONObject();
                j.put("brandName", model.getBrandName());
                array.add(j);
            }
            JSONArray projects = new JSONArray();//支持维修项目
            //如果该优惠券支持所有项目则返回  通用
            List<Project> projectList = projectService.queryList(null);
            if (projectList.size() == couProjects.size()) {
                JSONObject js = new JSONObject();
                js.put("projectName", "通用");
                projects.add(js);
            } else {
                for (CouponProject project : couProjects) {
                    JSONObject js = new JSONObject();
                    js.put("projectName", project.getProjectName());
                    projects.add(js);
                }

            }
            jsonResult.put("data", coupon);
            jsonResult.put("models", array);
            jsonResult.put("projects", projects);
            result.setResult(jsonResult);
            sessionUserService.getSuccessResult(result, jsonResult);
        } catch (SystemException e) {
            sessionUserService.getSystemException(e, result);
        } catch (Exception e) {
            e.printStackTrace();
            sessionUserService.getException(result);
        }
        renderJson(response, result);
    }
}
