package com.kuaixiu.activity.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.exception.SystemException;
import com.common.paginate.Page;
import com.common.util.AES;
import com.common.util.FileUtil;
import com.common.wechat.aes.AesCbcUtil;
import com.common.wechat.common.util.StringUtils;
import com.kuaixiu.activity.entity.ActivityProject;
import com.kuaixiu.activity.entity.ActivityUser;
import com.kuaixiu.activity.service.ActivityProjectService;
import com.kuaixiu.activity.service.ActivityUserService;
import com.system.api.entity.ResultData;
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
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * ActivityUser Controller
 *
 * @CreateDate: 2018-12-28 下午04:18:04
 * @version: V 1.0
 */
@Controller
public class ActivityUserController extends BaseController {
    private static final Logger log = Logger.getLogger(ActivityUserController.class);
    @Autowired
    private ActivityUserService activityUserService;
    @Autowired
    private ActivityProjectService activityProjectService;
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
    @RequestMapping(value = "/activityUser/list")
    public ModelAndView list(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {

        String returnView = "activity/activityUserList";
        return new ModelAndView(returnView);
    }

    /**
     * 根据活动标识查询活动信息
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/activityCompany/getActivityUserForPage")
    @ResponseBody
    public void getActivityUserForPage(HttpServletRequest request,
                                       HttpServletResponse response) throws Exception {
        Page page = getPageByRequest(request);
        try {
            String companyName = request.getParameter("companyName");
            String queryStartTime = request.getParameter("queryStartTime");
            String queryEndTime = request.getParameter("queryEndTime");
            String businessType = request.getParameter("businessType");//业务类型 1：快修  2电信
            String person = request.getParameter("person");
            String estimateResult = request.getParameter("estimateResult");//预约结果  1：有效  2：已结束
            List<Object> objects = new ArrayList<>();
            if (StringUtils.isBlank(businessType) || "1".equals(businessType)) {
                ActivityUser user = new ActivityUser();
                user.setCompanyName(companyName.replace(" ", ""));
                user.setPerson(person.replace(" ", ""));
                user.setQueryStartTime(queryStartTime);
                user.setQueryEndTime(queryEndTime);
                user.setPage(page);
                List<Map<String, Object>> users = new ArrayList<Map<String, Object>>();
                users = activityUserService.getDao().queryEstimateForPage(user);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                for (Map<String, Object> map : users) {
                    Long createTime = sdf.parse(map.get("saveTime").toString()).getTime();
                    Long endTime = sdf.parse(map.get("endTime").toString()).getTime();
                    if (createTime > endTime) {
                        if ("1".equals(estimateResult)) {
                            continue;
                        }
                        map.put("estimateResult", 2);
                    } else {
                        if ("2".equals(estimateResult)) {
                            continue;
                        }

                        map.put("estimateResult", 1);
                    }
                    map.put("businessType", 1);
                    objects.add(map);
                }
            }
            if (StringUtils.isBlank(businessType) || "2".equals(businessType)) {
                //电信业务
            }
            page.setData(objects);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        this.renderJson(response, page);
    }


    /**
     * 根据活动标识登录
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/activityCompany/saveLogin")
    @ResponseBody
    public ResultData activityLogin(HttpServletRequest request,
                                    HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        try {
            JSONObject params = getPrarms(request);
            String openId = params.getString("openId");
            String iv = params.getString("iv");
            String encryptedData = params.getString("encryptedData");
            String activityIdentification = params.getString("iden");

            if (StringUtils.isBlank(openId) || StringUtils.isBlank(iv) || StringUtils.isBlank(encryptedData)
                    || StringUtils.isBlank(activityIdentification)) {
                throw new SystemException("请求参数不完整");
            }
            ActivityUser a = activityUserService.getDao().queryByOpenId(openId);
            String sessionKey = a.getSessionKey();
            //解密参数
            JSONObject info = AesCbcUtil.decrypt(sessionKey, encryptedData, iv);
            a.setLoginNumber(info.getString("phoneNumber"));
            a.setActivityIdent(activityIdentification);
            activityUserService.getDao().updateByOpenId(a);

            result.setResult(a);
            result.setResultCode("0");
            result.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }


    /**
     * 获取活动项目
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/activityCompany/getKxProject")
    @ResponseBody
    public ResultData getKxProject(HttpServletRequest request,
                                   HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        try {
            JSONObject params = getPrarms(request);
            String type = params.getString("type");
            String activityIdent = params.getString("activityIdent");
            if (StringUtils.isBlank(activityIdent) ||StringUtils.isBlank(type) || "0".equals(type)) {
                throw new SystemException("请求参数不完整");
            }
            ActivityProject project = new ActivityProject();
            project.setActivityIdent(activityIdent);
            project.setType(Integer.valueOf(type));
            List<ActivityProject> projects = activityProjectService.queryList(project);

            result.setResult(projects);
            result.setResultCode("0");
            result.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }

    /**
     * 保存信息
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/activityCompany/kxSave")
    @ResponseBody
    public ResultData kxSave(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        try {
            JSONObject params = getPrarms(request);
            String activityIdent = params.getString("iden");
            String person = params.getString("userName");
            String number = params.getString("number");
//            String model = params.getString("model");
            String project = params.getString("project");//1,5,9,6,3,4,
            String fault = params.getString("fault");//故障现象
            String brand = params.getString("brandName");
            String modelName = params.getString("modelName");
            String productId = params.getString("productId");
            if (StringUtil.isBlank(activityIdent) || StringUtil.isBlank(person) || StringUtil.isBlank(number)
                    || StringUtil.isBlank(project) || StringUtil.isBlank(brand) || StringUtil.isBlank(modelName)
                    || StringUtil.isBlank(productId)) {
                throw new SystemException("参数不完整");
            }
            StringBuilder sb = new StringBuilder();
            List<String> list = new ArrayList<String>();
            ActivityProject project1 = new ActivityProject();
            project1.setType(1);
            list = Arrays.asList(project.split(","));
            for (int i = 0; i < list.size(); i++) {
                String pro = list.get(i);
                project1.setProjectNo(pro);
                ActivityProject project2 = activityProjectService.getDao().queryByProjectNo(project1);
                sb.append(project2.getProjectName());
                if (i != list.size() - 1) {
                    sb.append(",");
                }
            }
            project = sb.toString();

            ActivityUser user = activityUserService.getDao().queryByIdent(activityIdent);
            user.setPerson(person);
            user.setNumber(number);
//            user.setModel(model);
            user.setProject(project);
            user.setFault(fault);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date newTime = new Date();
            user.setSaveTime(sdf.format(newTime));
            user.setRecycleModel(modelName);
            user.setBrand(brand);
            user.setProductId(productId);
            activityUserService.getDao().update(user);
            activityUserService.getDao().updateByIden(user);

            result.setResultCode("0");
            result.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }

    /**
     * 通过微信openId和微信小程序检测出的用户机型和品牌获取机型品牌名称 品牌id 机型名称  机型id  产品id
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/activityCompany/getProductId")
    @ResponseBody
    public ResultData getProductId(HttpServletRequest request,
                                   HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        JSONObject jsonResult = new JSONObject();
        String productId = null;   //通过回收结果返回的最终机型id
        try {
            //获取请求数据
            JSONObject params = getPrarms(request);
            String brand = params.getString("brand");
            String modelName = params.getString("modelName");
            String categoryid = params.getString("categoryid");
            if (StringUtil.isBlank(brand) || StringUtil.isBlank(modelName)) {
                throw new SystemException("参数不完整");
            }
            //将微信小程序检测的机型名称转换成回收平台的机型名称
            Map<String, String> map = AES.getModelName(brand, modelName);
            JSONObject requestNews = new JSONObject();
            if (map.isEmpty()) {
                throw new SystemException("对应机型未找到!");
            }
            //通过转换过的机型 使用回收搜索接口得到对应机型id
            JSONObject code = new JSONObject();
            code.put("pageindex", "1");
            code.put("pagesize", "50");
            code.put("categoryid", categoryid);
            code.put("keyword", map.get("modelName").replaceAll(" ", ""));
            String realCode = AES.Encrypt(code.toString());  //加密
            requestNews.put(cipherdata, realCode);
            //发起获取对应productid请求
            String getResult = AES.post(baseUrl + "getmodellist", requestNews);
            //对得到结果进行解密
            code = getResult(AES.Decrypt(getResult));
            JSONArray product = code.getJSONArray("datainfo");
            if (product.isEmpty()) {
                throw new SystemException("对应机型不存在");
            }
            //判断返回的机型是否有多个
            if (product.size() > 1) {
                //如果有多个机型 则通过机型名称匹配唯一
                for (int i = 0; i < product.size(); i++) {
                    JSONObject object = (JSONObject) product.get(i);
                    JSONArray sublist = object.getJSONArray("sublist");
                    if (((JSONObject) sublist.get(0)).getString("modelname").equals(map.get("modelName"))) {
                        productId = object.getString("productid");
                    }
                }
            } else {
                JSONObject o = (JSONObject) product.get(0);
                JSONArray sublist = o.getJSONArray("sublist");
                productId = ((JSONObject) sublist.get(0)).getString("productid");
            }
            if (StringUtils.isBlank(productId)) {
                throw new SystemException("对应机型id不存在");
            }

            //将图片的http的链接转换为https返回  图片保存位置   项目根目录的 resource/brandLogo下
            String imageUrl = map.get("modelLogo");
            String savePath = serverPath(request.getServletContext().getRealPath("")) + System.getProperty("file.separator") + SystemConstant.IMAGE_PATH + System.getProperty("file.separator") + "recycleModel";
            log.info("图片保存路径:" + savePath);
            String modelUrl = getProjectUrl(request) + "/images/recycleModel/" + imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            //先判断文件是否存在  不存在则下载
            File f = new File(savePath + System.getProperty("file.separator") + (imageUrl.substring(imageUrl.lastIndexOf("/") + 1)));
            if (!f.exists()) {
                log.info("图片不存在，开始下载");
                FileUtil.download(imageUrl, imageUrl.substring(imageUrl.lastIndexOf("/") + 1), savePath);
            }

            //返回数据
            jsonResult.put("brandName", map.get("brandName"));
            jsonResult.put("brandId", map.get("brandId"));
            jsonResult.put("modelName", map.get("modelName"));
            jsonResult.put("productId", productId);
            jsonResult.put("modelId", map.get("modelId"));
            jsonResult.put("modelLogo", modelUrl);
            result.setResult(jsonResult);

            result.setResultCode("0");
            result.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
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
