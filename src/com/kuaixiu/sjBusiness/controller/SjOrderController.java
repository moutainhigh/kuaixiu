package com.kuaixiu.sjBusiness.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.exception.SystemException;
import com.common.paginate.Page;
import com.common.util.NOUtil;
import com.common.util.UrlUtil;
import com.common.wechat.common.util.StringUtils;
import com.kuaixiu.recycle.service.RecycleOrderService;
import com.kuaixiu.sjBusiness.entity.*;
import com.kuaixiu.sjBusiness.service.*;
import com.kuaixiu.sjUser.entity.CustomerDetail;
import com.kuaixiu.sjUser.entity.SjUser;
import com.kuaixiu.sjUser.service.CustomerDetailService;
import com.kuaixiu.sjUser.service.SjUserService;
import com.system.api.entity.ResultData;
import com.system.constant.SystemConstant;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Order Controller
 *
 * @CreateDate: 2019-05-06 上午10:44:49
 * @version: V 1.0
 */
@Controller
public class SjOrderController extends BaseController {
    private static final Logger log = Logger.getLogger(SjOrderController.class);

    @Autowired
    private SjOrderService orderService;
    @Autowired
    private SjProjectService projectService;
    @Autowired
    private OrderCompanyPictureService orderCompanyPictureService;
    @Autowired
    private SjUserService userService;
    @Autowired
    private RecycleOrderService recycleOrderService;
    @Autowired
    private CustomerDetailService customerDetailService;
    @Autowired
    private AreaCityCompanyService cityCompanyService;
    @Autowired
    private AreaManagementUnitService managementUnitService;
    @Autowired
    private AreaBranchOfficeService branchOfficeService;
    @Autowired
    private AreaContractBodyService contractBodyService;

    private String Ext_Name = "jpg,jpeg,png";
    private static final String poilceUrl = "https://sh.keepwan.com/api/ningbo/merchants";

    /**
     * 获取所有产品需求
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sj/wechat/getProject")
    @ResponseBody
    public ResultData getProject(HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        try {
            List<SjProject> projects = projectService.queryList(null);
            List<JSONObject> objects1 = new ArrayList<>();
            for (SjProject project : projects) {
                JSONObject object = new JSONObject();
                object.put("projectId", project.getId());
                object.put("project", project.getProject());
                objects1.add(object);
            }
            getSjResult(result, objects1, true, "0", null, "获取成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }


    /**
     * 提交商机/派单
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sj/wechat/submitOrder")
    @ResponseBody
    public ResultData register(HttpServletRequest request,
                               HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        try {
            JSONObject params = getPrarms(request);
            Integer type = params.getInteger("type");//
            String phone = params.getString("phone");//登录手机号
            String companyName = params.getString("companyName");//企业名字
            String provinceId = params.getString("provinceId");
            String cityId = params.getString("cityId");
            String areaId = params.getString("areaId");
            String streetId = params.getString("streetId");
            String addressDetail = params.getString("addressDetail");
            String person = params.getString("person");//姓名
            String personPhone = params.getString("personPhone");//手机号
            String responsibleName = params.getString("responsibleName");//负责人姓名
            String responsibleIdNumber = params.getString("responsibleIdNumber");//负责人身份证号
            JSONArray imagesList = params.getJSONArray("imagesList");
            String projectId = params.getString("projectId");//需求id  ","隔开
            Integer single = params.getInteger("ap");//AP
            Integer group = params.getInteger("monitor");//监控
            String crmNo = params.getString("crmNo");//CRM编号

            if (StringUtils.isBlank(phone) || StringUtils.isBlank(companyName)
                    || StringUtils.isBlank(addressDetail) || StringUtils.isBlank(person)
                    || StringUtils.isBlank(personPhone) || StringUtils.isBlank(projectId)
                    || StringUtils.isBlank(provinceId) || StringUtils.isBlank(cityId)
                    || StringUtils.isBlank(areaId)
                    || null == type || null == imagesList) {
                return getSjResult(result, null, false, "2", null, "参数为空");
            }
            SjUser user = userService.getDao().queryByLoginId(phone, null);
            if (user == null) {
                return getSjResult(result, null, false, "2", null, "账号错误，请重新登录");
            }
            if (type == 2) {
                if (projectId.contains("1")) {
                    if (null == single) {
                        return getSjResult(result, null, false, "2", null, "AP为空");
                    }
//                    if (StringUtils.isBlank(responsibleIdNumber)) {
//                        return getSjResult(result, null, false, "2", null, "负责人身份证号为空");
//                    }
//                    if (StringUtils.isBlank(responsibleName)) {
//                        return getSjResult(result, null, false, "2", null, "负责人姓名为空");
//                    }
                }
                if (projectId.contains("2")) {
                    if (null == group) {
                        return getSjResult(result, null, false, "2", null, "监控为空");
                    }
                }
                if (StringUtils.isBlank(crmNo)) {
                    return getSjResult(result, null, false, "2", null, "参数为空");
                }
                if (crmNo.length() != 18) {
                    return getSjResult(result, null, false, "2", null, "CRM错误");
                }
            }
            SjOrder sjOrder = new SjOrder();
            sjOrder.setType(type);
            sjOrder.setCrmNo(crmNo);
            sjOrder.setState(100);
            sjOrder.setCompanyName(companyName);
            sjOrder.setProvinceId(provinceId);
            sjOrder.setCityId(cityId);
            sjOrder.setAreaId(areaId);
            sjOrder.setStreetId(streetId);
            sjOrder.setAddressDetail(addressDetail);
            sjOrder.setPerson(person);
            sjOrder.setResponsibleIdNumber(responsibleIdNumber);
            sjOrder.setResponsibleName(responsibleName);
            sjOrder.setPhone(personPhone);
            sjOrder.setProjectId(projectId);
            if (type == 2) {
                sjOrder.setSingle(single);
                sjOrder.setGroupNet(group);
            }
            sjOrder.setCreateUserid(phone);
            sjOrder.setCreateName(user.getName());
            sjOrder.setStayPerson("admin");
            if (type == 2) {
                orderService.createOrder(projectId, sjOrder);
            } else {
                sjOrder.setOrderNo(NOUtil.getNo("NB-"));
                orderService.add(sjOrder);
            }

            for (int i = 0; i < imagesList.size(); i++) {
                String image = imagesList.get(i).toString();
                OrderCompanyPicture companyPicture = new OrderCompanyPicture();
                companyPicture.setOrderNo(sjOrder.getOrderNo());
                companyPicture.setCompanyPictureUrl(image);
                orderCompanyPictureService.add(companyPicture);
            }

            if (type == 2) {
                if (projectId.contains("1")) {
                    postNews(sjOrder);
                }
            }

            getSjResult(result, null, true, "0", null, "提交成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }


    //推送数据给公安
    public String postNews(SjOrder sjOrder) throws Exception {
        String areaname = recycleOrderService.getAreaname(sjOrder.getProvinceId(), sjOrder.getCityId(), sjOrder.getAreaId());
        StringBuilder ascription = new StringBuilder();
        SjUser sjUser = userService.getDao().queryByLoginId(sjOrder.getCreateUserid(), 2);
        CustomerDetail customerDetail = customerDetailService.getDao().queryByLoginId(sjUser.getId());
        AreaCityCompany areaCityCompany = cityCompanyService.queryById(customerDetail.getCityCompanyId());
        String cityCompany = areaCityCompany.getCityCompany();
        ascription.append(cityCompany);
        if (customerDetail.getManagementUnitId() != null) {
            AreaManagementUnit managementUnit1 = managementUnitService.queryById(customerDetail.getManagementUnitId());
            if (managementUnit1 != null) {
                String managementUnit = managementUnit1.getManagementUnit();
                ascription.append("/" + managementUnit);
            }
        }
        if (customerDetail.getBranchOfficeId() != null) {
            AreaBranchOffice branchOffice1 = branchOfficeService.queryById(customerDetail.getBranchOfficeId());
            if (branchOffice1 != null) {
                String branchOffice = branchOffice1.getBranchOffice();
                ascription.append("/" + branchOffice);
            }
        }
        if (customerDetail.getContractBodyId() != null) {
            AreaContractBody contractBody1 = contractBodyService.queryById(customerDetail.getContractBodyId());
            if (contractBody1 != null) {
                String contractBody = contractBody1.getContractBody();
                ascription.append("/" + contractBody);
            }
        }
        //调用接口需要加密的数据
        JSONObject code = new JSONObject();
        code.put("name", sjOrder.getCompanyName());
        code.put("address", areaname + sjOrder.getAddressDetail());
        code.put("linkman_name", sjOrder.getPerson());
        code.put("linkman_phone", sjOrder.getPhone());
        code.put("corporation_name", sjOrder.getResponsibleName());
        code.put("corporation_id_no", sjOrder.getResponsibleIdNumber());
        code.put("order_no", sjOrder.getOrderNo());
        code.put("cm_name", sjOrder.getCreateName());
        code.put("cm_phone", sjOrder.getCreateUserid());
        code.put("cm_unit", ascription.toString());
        //发起请求
        return UrlUtil.sendPost(poilceUrl, code);
    }

    /**
     * 订单列表
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sj/wechat/getOrderList")
    @ResponseBody
    public ResultData getOrderList(HttpServletRequest request,
                                   HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        try {
            JSONObject params = getPrarms(request);
            Integer type = params.getInteger("type");
            Integer state = params.getInteger("state");
            String phone = params.getString("phone");
            Integer pageIndex = params.getInteger("pageIndex");
            Integer pageSize = params.getInteger("pageSize");

            if (StringUtils.isBlank(phone)) {
                return getSjResult(result, null, false, "2", null, "手机号不能为空");
            }
            SjOrder sjOrder = new SjOrder();
            Page page = new Page();
            //将值转化为绝对值
            pageIndex = Math.abs(pageIndex);
            pageSize = Math.abs(pageSize);
            page.setPageSize(pageSize);
            page.setCurrentPage(pageIndex);
            sjOrder.setPage(page);
            sjOrder.setCreateUserid(phone);
            sjOrder.setType(type);
            if (state != 1) {
                Integer orderState = orderService.setOrderType(state, type);
                sjOrder.setState(orderState);
            }
            List<SjOrder> sjOrders = orderService.getDao().queryWebListForPage(sjOrder);
            List<JSONObject> jsonObjects = orderService.sjListOrderToObejct(sjOrders);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("pageSize", page.getPageSize());
            jsonObject.put("pageIndex", page.getCurrentPage());
            jsonObject.put("recordsTotal", page.getRecordsTotal());
            jsonObject.put("totalPage", page.getTotalPage());
            jsonObject.put("sjOrders", jsonObjects);
            getSjResult(result, jsonObject, true, "0", null, "查询成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }

    /**
     * 订单详情
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sj/wechat/getOrderDetail")
    @ResponseBody
    public ResultData getOrderDetail(HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        try {
            JSONObject params = getPrarms(request);
            String phone = params.getString("phone");
            String orderNo = params.getString("orderNo");
            if (StringUtils.isBlank(phone) || StringUtils.isBlank(orderNo)) {
                return getSjResult(result, null, false, "0", null, "参数为空");
            }
            SjOrder sjOrder = orderService.getDao().queryByOrderNo(orderNo, phone);
            if (sjOrder == null) {
                return getSjResult(result, null, false, "0", null, "订单号错误");
            }
            JSONObject jsonObject = orderService.sjOrderToObejct(sjOrder);

            getSjResult(result, jsonObject, true, "0", null, "查询成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }

    /**
     * 详情重新提交商机/派单
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sj/wechat/reworkSubmitOrder")
    @ResponseBody
    public ResultData reworkSubmitOrder(HttpServletRequest request,
                                        HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        try {
            JSONObject params = getPrarms(request);
            Integer type = params.getInteger("type");
            String phone = params.getString("phone");
            String orderNo = params.getString("orderNo");
            String companyName = params.getString("companyName");
            String provinceId = params.getString("provinceId");
            String cityId = params.getString("cityId");
            String areaId = params.getString("areaId");
            String streetId = params.getString("streetId");
            String addressDetail = params.getString("addressDetail");
            String person = params.getString("person");
            String personPhone = params.getString("personPhone");
            String responsibleName = params.getString("responsibleName");//负责人姓名
            String responsibleIdNumber = params.getString("responsibleIdNumber");//负责人身份证号
            JSONArray imagesList = params.getJSONArray("imagesList");
            String projectId = params.getString("projectId");
            Integer single = params.getInteger("ap");
            Integer group = params.getInteger("monitor");
            String crmNo = params.getString("crmNo");

            if (StringUtils.isBlank(phone) || StringUtils.isBlank(companyName) || StringUtils.isBlank(orderNo)
                    || StringUtils.isBlank(addressDetail) || StringUtils.isBlank(person)
                    || StringUtils.isBlank(personPhone) || StringUtils.isBlank(projectId)
                    || StringUtils.isBlank(provinceId) || StringUtils.isBlank(cityId)
                    || StringUtils.isBlank(areaId) || null == type || null == imagesList) {
                return getSjResult(result, null, false, "2", null, "参数为空");
            }
            if (type == 2) {
                if (projectId.contains("1")) {
                    if (null == single) {
                        return getSjResult(result, null, false, "2", null, "AP为空");
                    }
//                    if (StringUtils.isBlank(responsibleIdNumber)) {
//                        return getSjResult(result, null, false, "2", null, "负责人身份证号为空");
//                    }
//                    if (StringUtils.isBlank(responsibleName)) {
//                        return getSjResult(result, null, false, "2", null, "负责人姓名为空");
//                    }
                }
                if (projectId.contains("2")) {
                    if (null == group) {
                        return getSjResult(result, null, false, "2", null, "监控为空");
                    }
                }
                if (StringUtils.isBlank(crmNo)) {
                    return getSjResult(result, null, false, "2", null, "参数为空");
                }
                if (crmNo.length() != 18) {
                    return getSjResult(result, null, false, "2", null, "CRM错误");
                }
            }

            SjUser user = userService.getDao().queryByLoginId(phone, null);
            if (user == null) {
                return getSjResult(result, null, false, "2", null, "账号错误，请重新登录");
            }

            SjOrder sjOrder = orderService.getDao().queryByOrderNo(orderNo, phone);
            sjOrder.setType(type);
            sjOrder.setCrmNo(crmNo);
            sjOrder.setState(100);
            sjOrder.setCompanyName(companyName);
            sjOrder.setProvinceId(provinceId);
            sjOrder.setCityId(cityId);
            sjOrder.setAreaId(areaId);
            sjOrder.setStreetId(streetId);
            sjOrder.setAddressDetail(addressDetail);
            sjOrder.setPerson(person);
            sjOrder.setPhone(personPhone);
            sjOrder.setResponsibleIdNumber(responsibleIdNumber);
            sjOrder.setResponsibleName(responsibleName);
            sjOrder.setProjectId(projectId);
            if (type == 2) {
                sjOrder.setSingle(single);
                sjOrder.setGroupNet(group);
            }
            sjOrder.setCreateUserid(phone);
            sjOrder.setCreateName(user.getName());
            sjOrder.setStayPerson(orderService.setStayPerson(1));
            orderService.saveUpdate(sjOrder);

            orderCompanyPictureService.getDao().deleteByOrderNo(orderNo);

            for (int i = 0; i < imagesList.size(); i++) {
                String image = imagesList.get(i).toString();
                OrderCompanyPicture companyPicture = new OrderCompanyPicture();
                companyPicture.setOrderNo(sjOrder.getOrderNo());
                companyPicture.setCompanyPictureUrl(image);
                orderCompanyPictureService.add(companyPicture);
            }

            getSjResult(result, null, true, "0", null, "提交成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }

    /**
     * 上传照片
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sj/wechat/uplocadImage")
    @ResponseBody
    public ResultData uplocadImage(HttpServletRequest request,
                                   HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        try {
            //获取图片，保存图片到webapp同级inages/sj_images
            String savePath = serverPath(request.getServletContext().getRealPath("")) + System.getProperty("file.separator") + SystemConstant.IMAGE_PATH + System.getProperty("file.separator") + "sj_images" + System.getProperty("file.separator") + "sj_company";
            String logoPath = getImagePath(request, "image", savePath);             //图片路径
            String image = "";
            if (StringUtils.isNotBlank(logoPath)) {
                image = getProjectUrl(request) + "/images/sj_images/sj_company/" + logoPath.substring(logoPath.lastIndexOf("/") + 1);
            }
            getSjResult(result, image, true, "0", null, "上传成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }

    /**
     * 保存文件
     */
    private String getImagePath(HttpServletRequest request, String file, String URLPath) {
        String fileName = "";                   //上传的文件名
        String path = "";                       //存储路径
        try {
            //转化request
            MultipartHttpServletRequest rm = (MultipartHttpServletRequest) request;
            MultipartFile mfile = rm.getFile(file);                             //获得前端页面传来的文件
            byte[] bfile = mfile.getBytes();                                    //获得文件的字节数组
            if (!checkFileSize(mfile.getSize(), 300, "k")) {
                log.info("图片大小：" + mfile.getSize() / 1024 + "KB");
                bfile = imageCompress(bfile, mfile);
            }
            if (bfile.length == 0) {
                log.info("未上传图片");
                return "";
            }
            fileName = mfile.getOriginalFilename();                             //获得文件名
            // 处理获取到的上传文件的文件名的路径部分，只保留文件名部分
            fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);

            // 得到上传文件的扩展名
            String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            // 检查扩展名
            if (!Ext_Name.contains(fileExt)) {
                throw new SystemException("上传文件扩展名是不允许的扩展名：" + fileExt);
            } else {
                //保存文件
                path = saveFile(bfile, fileName, request, URLPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }
}
