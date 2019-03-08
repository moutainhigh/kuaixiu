package com.kuaixiu.activity.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.paginate.Page;
import com.common.util.FileUtil;
import com.common.util.HttpClientUtil;
import com.common.util.NOUtil;
import com.common.util.UrlUtil;
import com.common.wechat.common.util.StringUtils;
import com.kuaixiu.activity.entity.ActivityCompany;
import com.kuaixiu.activity.service.ActivityCompanyService;
import com.system.api.entity.ResultData;
import com.system.basic.user.entity.SessionUser;
import com.system.constant.SystemConstant;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HTTP;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * ActivityCompany Controller
 *
 * @CreateDate: 2018-12-27 上午11:24:16
 * @version: V 1.0
 */
@Controller
public class ActivityCompanyController extends BaseController {
    private static final Logger log = Logger.getLogger(ActivityCompanyController.class);
    @Autowired
    private ActivityCompanyService activityCompanyService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/activityCompany/list")
    public ModelAndView list(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {

        String returnView = "activity/activityCompany";
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
    @RequestMapping(value = "/activityCompany/getActivityForPage")
    @ResponseBody
    public void getActivityForPage(HttpServletRequest request,
                                   HttpServletResponse response) throws Exception {
        Page page = getPageByRequest(request);
        try {
            String activityIdentification = request.getParameter("activityIdentification");
            String companyName = request.getParameter("companyName");
            String queryStartTime = request.getParameter("queryStartTime");
            String queryEndTime = request.getParameter("queryEndTime");
            String startTime = request.getParameter("startTime");
            String endTime = request.getParameter("endTime");
            String isEnd = request.getParameter("isEnd");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            ActivityCompany activityCompany = new ActivityCompany();
            activityCompany.setCompanyName(companyName.replace(" ", ""));
            activityCompany.setActivityIdentification(activityIdentification);
            activityCompany.setQueryStartTime(queryStartTime);
            activityCompany.setQueryEndTime(queryEndTime);
            if (StringUtils.isNotBlank(startTime)) {
                activityCompany.setStartTime(startTime);
            }
            if (StringUtils.isNotBlank(endTime)) {
                activityCompany.setEndTime(endTime);
            }
            activityCompany.setPage(page);
            List<ActivityCompany> activityCompanies = activityCompanyService.getDao().queryListForPage(activityCompany);

            Long newTime = new Date().getTime();
            Iterator<ActivityCompany> it = activityCompanies.iterator();
            while (it.hasNext()) {
                ActivityCompany x = it.next();
                if (StringUtils.isNotBlank(isEnd)) {
                    if ("0".equals(isEnd)) {
                        if (sdf.parse(x.getStartTime()).getTime() >= newTime
                                || newTime >= sdf.parse(x.getEndTime()).getTime()) {
                            it.remove();
                            continue;
                        }
                    }
                    if ("1".equals(isEnd)) {
                        if (sdf.parse(x.getStartTime()).getTime() <= newTime) {
                            it.remove();
                            continue;
                        }
                    }
                    if ("2".equals(isEnd)) {
                        if (sdf.parse(x.getEndTime()).getTime() >= newTime) {
                            it.remove();
                            continue;
                        }
                    }
                }
                if (sdf.parse(x.getStartTime()).getTime() <= newTime
                        && newTime <= sdf.parse(x.getEndTime()).getTime()) {
                    x.setIsEnd(0);
                } else if (sdf.parse(x.getStartTime()).getTime() >= newTime) {
                    x.setIsEnd(1);
                } else if (newTime >= sdf.parse(x.getEndTime()).getTime()) {
                    x.setIsEnd(2);
                }
            }

            page.setData(activityCompanies);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        this.renderJson(response, page);
    }

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/activityCompany/create")
    public ModelAndView add(HttpServletRequest request,
                            HttpServletResponse response) throws Exception {

        String returnView = "activity/createActivity";
        return new ModelAndView(returnView);
    }

    /**
     * 保存公司活动信息
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/activityCompany/add")
    @ResponseBody
    public ResultData addActivity(HttpServletRequest request,
                                  HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        try {
            SessionUser user = getCurrentUser(request);
            String companyName = request.getParameter("companyName");
            String kxBusinessTitle = request.getParameter("kxBusinessTitle");
            String kxBusiness = request.getParameter("kxBusiness");
            String kxBusinessDetail = request.getParameter("kxBusinessDetail");
            String dxIncrementBusinessTitle = request.getParameter("dxIncrementBusinessTitle");
            String dxIncrementBusiness = request.getParameter("dxIncrementBusiness");
            String dxIncrementBusinessDetail = request.getParameter("dxIncrementBusinessDetail");
            String dxBusinessPerson = request.getParameter("dxBusinessPerson");
            String dxBusinessPersonNumber = request.getParameter("dxBusinessPersonNumber");
            String startTime = request.getParameter("startTime");
            String endTime = request.getParameter("endTime");
            String activityTime = request.getParameter("activityTime");

            if (StringUtils.isBlank(companyName) || StringUtils.isBlank(kxBusiness) || StringUtils.isBlank(kxBusinessDetail)
                    || StringUtils.isBlank(dxIncrementBusiness) || StringUtils.isBlank(dxIncrementBusinessDetail)
                    || StringUtils.isBlank(startTime) || StringUtils.isBlank(endTime) || StringUtils.isBlank(kxBusinessTitle)
                    || StringUtils.isBlank(dxIncrementBusinessTitle)) {
                return getResult(result, null, false, "2", "参数不能为空");
            }

            //获取图片，保存图片到webapp同级inages/activityCompany目录
            String savePath = serverPath(request.getServletContext().getRealPath("")) + System.getProperty("file.separator") + SystemConstant.IMAGE_PATH + System.getProperty("file.separator") + "activityCompany";
            String logoPath = getPath(request, "file", savePath);             //图片路径
            String imageUrl = getProjectUrl(request) + "/images/activityCompany/" + logoPath.substring(logoPath.lastIndexOf("/") + 1);
            System.out.println("图片路径：" + savePath);

            ActivityCompany company = new ActivityCompany();
            company.setActivityTime(activityTime);
            company.setId(UUID.randomUUID().toString().replace("-", "").substring(0, 16));
            company.setCompanyId(UUID.randomUUID().toString().replace("-", "").substring(0, 16));
            company.setCompanyName(companyName);
            company.setActivityImgUrl(imageUrl);
            company.setKxBusinessId(UUID.randomUUID().toString().replace("-", "").substring(0, 16));
            company.setDxIncrementBusinessId(UUID.randomUUID().toString().replace("-", "").substring(0, 16));
            company.setKxBusiness(kxBusiness);
            company.setKxBusinessDetail(kxBusinessDetail);
            company.setDxIncrementBusiness(dxIncrementBusiness);
            company.setDxIncrementBusinessDetail(dxIncrementBusinessDetail);
            company.setCreateUser(user.getUserId());
            company.setKxBusinessTitle(kxBusinessTitle);
            company.setDxIncrementBusinessTitle(dxIncrementBusinessTitle);
            company.setDxBusinessPerson(dxBusinessPerson);
            company.setDxBusinessPersonNumber(dxBusinessPersonNumber);
            company.setActivityIdentification(createNewHd());
            company.setStartTime(startTime);
            company.setEndTime(endTime);
            activityCompanyService.add(company);

            result.setSuccess(true);
            result.setResultMessage("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }

    /**
     * 修改公司活动信息
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/activityCompany/update")
    @ResponseBody
    public ResultData updateActivity(HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        try {
            SessionUser user = getCurrentUser(request);
            String activityIdentification = request.getParameter("activityIdentification");
            String companyName = request.getParameter("companyName");
            String kxBusinessTitle = request.getParameter("kxBusinessTitle");
            String kxBusiness = request.getParameter("kxBusiness");
            String kxBusinessDetail = request.getParameter("kxBusinessDetail");
            String dxIncrementBusinessTitle = request.getParameter("dxIncrementBusinessTitle");
            String dxIncrementBusiness = request.getParameter("dxIncrementBusiness");
            String dxIncrementBusinessDetail = request.getParameter("dxIncrementBusinessDetail");
            String dxBusinessPerson = request.getParameter("dxBusinessPerson");
            String dxBusinessPersonNumber = request.getParameter("dxBusinessPersonNumber");
            String startTime = request.getParameter("startTime");
            String endTime = request.getParameter("endTime");
            String fileURL = request.getParameter("fileURL");
            String activityTime = request.getParameter("activityTime");

            if (StringUtils.isBlank(companyName) || StringUtils.isBlank(kxBusiness) || StringUtils.isBlank(kxBusinessDetail)
                    || StringUtils.isBlank(dxIncrementBusiness) || StringUtils.isBlank(dxIncrementBusinessDetail)
                    || StringUtils.isBlank(startTime) || StringUtils.isBlank(endTime) || StringUtils.isBlank(kxBusinessTitle)
                    || StringUtils.isBlank(dxIncrementBusinessTitle)) {
                return getResult(result, null, false, "2", "参数不能为空");
            }
            String imageUrl = "";
            //获取图片，保存图片到webapp同级inages/activityCompany目录
            String savePath = serverPath(request.getServletContext().getRealPath("")) + System.getProperty("file.separator") + SystemConstant.IMAGE_PATH + System.getProperty("file.separator") + "activityCompany";
            //转化request
            MultipartHttpServletRequest rm = (MultipartHttpServletRequest) request;
            MultipartFile mfile = rm.getFile("file");                             //获得前端页面传来的文件
            byte[] bfile = mfile.getBytes();//获得文件的字节数组
            if (bfile.length == 0) {
                if (StringUtils.isBlank(fileURL)) {
                    return getResult(result, null, false, "2", "图片不能为空");
                }
                imageUrl = fileURL;
            } else {
                String logoPath = getPath(request, "file", savePath);             //图片路径
                imageUrl = getProjectUrl(request) + "/images/activityCompany/" + logoPath.substring(logoPath.lastIndexOf("/") + 1);
            }
            System.out.println("图片路径：" + savePath);

            ActivityCompany company = activityCompanyService.getDao().queryByIdentification(activityIdentification);
            company.setCompanyName(companyName);
            company.setActivityTime(activityTime);
            company.setActivityIdentification(activityIdentification);
            company.setActivityImgUrl(imageUrl);
            company.setKxBusiness(kxBusiness);
            company.setKxBusinessDetail(kxBusinessDetail);
            company.setDxIncrementBusiness(dxIncrementBusiness);
            company.setDxIncrementBusinessDetail(dxIncrementBusinessDetail);
            company.setKxBusinessTitle(kxBusinessTitle);
            company.setDxIncrementBusinessTitle(dxIncrementBusinessTitle);
            company.setDxBusinessPerson(dxBusinessPerson);
            company.setDxBusinessPersonNumber(dxBusinessPersonNumber);
            company.setStartTime(startTime);
            company.setEndTime(endTime);
            activityCompanyService.getDao().updateByIdentification(company);

            result.setSuccess(true);
            result.setResultMessage("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }

    /**
     * 生成新优惠编码
     *
     * @return
     */
    public String createNewHd() {
        String regex = ".*[a-zA-Z]+.*";
        // 获取新编码
        String code = NOUtil.getRandomString(6);
        // 检查编码是否存在 且必须包含英文字母
        ActivityCompany c = activityCompanyService.getDao().queryByIdentification(code);
        if (c == null && code.matches(regex)) {
            return code;
        } else {
            return createNewHd();
        }
    }

    /**
     * 根据活动标识查询活动信息
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/activityCompany/getActivity")
    @ResponseBody
    public ModelAndView getActivity(HttpServletRequest request,
                                    HttpServletResponse response) throws Exception {
        try {
            String activityIdentification = request.getParameter("activityIdentification");

            ActivityCompany activityCompany = activityCompanyService.getDao().queryByIdentification(activityIdentification);

            request.setAttribute("activity", activityCompany);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return new ModelAndView("activity/editActivity");
    }


    /**
     * 根据活动标识查询活动信息
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/activityCompany/findActivity")
    @ResponseBody
    public ResultData findActivity(HttpServletRequest request,
                                   HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        try {
            JSONObject params = getPrarms(request);
            String activityIdentification = params.getString("iden");
            if (StringUtils.isBlank(activityIdentification)) {
                return getResult(result, null, false, "2", "参数不能为空");
            }
            ActivityCompany activityCompany = activityCompanyService.getDao().queryByIdentification(activityIdentification);
            if (activityCompany == null) {
                return getResult(result, null, false, "2", "该活动为空");
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Long newTime = new Date().getTime();
            if (sdf.parse(activityCompany.getStartTime()).getTime() <= newTime
                    && newTime <= sdf.parse(activityCompany.getEndTime()).getTime()) {
                activityCompany.setIsEnd(0);
            } else if (sdf.parse(activityCompany.getStartTime()).getTime() >= newTime) {
                activityCompany.setIsEnd(1);
            } else if (newTime >= sdf.parse(activityCompany.getEndTime()).getTime()) {
                activityCompany.setIsEnd(2);
            }
            if (StringUtils.isBlank(activityCompany.getActivityImgUrl())) {
                String imageUrl = getProjectUrl(request) + "/images/activityCompany/default.png";
                activityCompany.setActivityImgUrl(imageUrl);
            }

            Map<String,Object> map=activityCompanyService.objectToMap(activityCompany);

            result.setSuccess(true);
            result.setResultMessage("成功");
            result.setResult(activityCompany);
            result.setResultCode("0");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }


    /*
         * 获取二维码
    　　　* 这里的 post 方法 为 json post【重点】
         */
    @RequestMapping(value = "/activityCompany/getCode")
    @ResponseBody
    public ResultData getCodeM(HttpServletRequest request) throws Exception {
        ResultData result = new ResultData();
        JSONObject object = new JSONObject();
        String imei = request.getParameter("Identification");
        String name = imei + ".png";
        String savePath = serverPath(request.getServletContext().getRealPath("")) + System.getProperty("file.separator") + SystemConstant.IMAGE_PATH + System.getProperty("file.separator") + "activityCompany";
        File f = new File(savePath + System.getProperty("file.separator") + name);
        if (f.exists()) {
            log.info("图片已存在");
            object.put("path", getProjectUrl(request) + System.getProperty("file.separator") + SystemConstant.IMAGE_PATH + System.getProperty("file.separator") + "activityCompany" + System.getProperty("file.separator") + name);
            getResult(result, object, true, "0", "成功");
            return result;
        }
        //String page="page/msg_waist/msg_waist";
        String token = getToken();   // 得到token

        Map<String, Object> params = new HashMap<>();
        params.put("scene", imei);  //参数
        params.put("page", "pages/index/index"); //位置
        params.put("width", 430);

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        HttpPost httpPost = new HttpPost("https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=" + token);  // 接口
        httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json");
        String body = JSON.toJSONString(params);           //必须是json模式的 post
        StringEntity entity;
        entity = new StringEntity(body);
        entity.setContentType("image/png");
        httpPost.setEntity(entity);
        HttpResponse response;

        response = httpClient.execute(httpPost);
        InputStream inputStream = response.getEntity().getContent();

        Integer stateInt = saveToImgByInputStream(inputStream, savePath, name);  //保存图片
        if (stateInt == 1) {
            object.put("path", getProjectUrl(request) + System.getProperty("file.separator") + SystemConstant.IMAGE_PATH + System.getProperty("file.separator") + "activityCompany" + System.getProperty("file.separator") + name);
            getResult(result, object, true, "0", "成功");
        } else {
            getResult(result, object, false, "2", "失败");
        }
        return result;
    }

    /*
     * 获取 token
　　　* 普通的 get 可获 token
     */
    public static String getToken() {
        try {

            Map<String, String> map = new LinkedHashMap<String, String>();
            map.put("grant_type", "client_credential");
            map.put("appid", SystemConstant.WECHAT_APPLET_APPID);//改成自己的appid
            map.put("secret", SystemConstant.WECHAT_APPLET_SECRET);

            //String rt = UrlUtil.sendPost("https://api.weixin.qq.com/cgi-bin/token", map);
            String rt = UrlUtil.sendPost("https://api.weixin.qq.com/cgi-bin/token", map);
            System.out.println(rt);
            JSONObject json = JSONObject.parseObject(rt);

            if (json.getString("access_token") != null || json.getString("access_token") != "") {
                return json.getString("access_token");
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("# 获取 token 出错... e:" + e);
            e.printStackTrace();
            log.error(e.getMessage());
            return null;
        }

    }

    /**
     * 将二进制转换成文件保存
     *
     * @param instreams 二进制流
     * @param imgPath   图片的保存路径
     * @param imgName   图片的名称
     * @return 1：保存正常
     * 0：保存失败
     */
    public static int saveToImgByInputStream(InputStream instreams, String imgPath, String imgName) {
        int stateInt = 1;
        if (instreams != null) {
            try {
                File file = new File(imgPath, imgName);//可以是任何图片格式.jpg,.png等
                FileOutputStream out = new FileOutputStream(imgPath + System.getProperty("file.separator") + imgName);
                byte[] b = new byte[1024];
                int nRead = 0;
                while ((nRead = instreams.read(b)) != -1) {
                    log.info("二维码开始下载" + b);
                    out.write(b, 0, nRead);
                }
                out.flush();
                out.close();
            } catch (Exception e) {
                stateInt = 0;
                e.printStackTrace();
                log.error(e.getMessage());
            } finally {
            }
        }
        return stateInt;
    }

}
