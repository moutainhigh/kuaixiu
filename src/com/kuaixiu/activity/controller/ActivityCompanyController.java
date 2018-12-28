package com.kuaixiu.activity.controller;

import com.common.base.controller.BaseController;
import com.common.paginate.Page;
import com.common.util.NOUtil;
import com.common.wechat.common.util.StringUtils;
import com.kuaixiu.activity.entity.ActivityCompany;
import com.kuaixiu.activity.service.ActivityCompanyService;
import com.kuaixiu.coupon.entity.Coupon;
import com.system.api.entity.ResultData;
import com.system.basic.user.entity.SessionUser;
import com.system.constant.SystemConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * ActivityCompany Controller
 *
 * @CreateDate: 2018-12-27 上午11:24:16
 * @version: V 1.0
 */
@Controller
public class ActivityCompanyController extends BaseController {

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
            activityCompany.setCompanyName(companyName);
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
        }
        return result;
    }

    /**
     * 保存公司活动信息
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
            String imageUrl ="";
            //获取图片，保存图片到webapp同级inages/activityCompany目录
            String savePath = serverPath(request.getServletContext().getRealPath("")) + System.getProperty("file.separator") + SystemConstant.IMAGE_PATH + System.getProperty("file.separator") + "activityCompany";
            //转化request
            MultipartHttpServletRequest rm=(MultipartHttpServletRequest) request;
            MultipartFile mfile=rm.getFile("file");                             //获得前端页面传来的文件
            byte[] bfile=mfile.getBytes();//获得文件的字节数组
            if(bfile.length==0){
                if(StringUtils.isBlank(fileURL)){
                    return getResult(result, null, false, "2", "图片不能为空");
                }
                imageUrl=fileURL;
            }else {
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
        ResultData result = new ResultData();
        try {
            SessionUser user = getCurrentUser(request);
            String activityIdentification = request.getParameter("activityIdentification");

            ActivityCompany activityCompany = activityCompanyService.getDao().queryByIdentification(activityIdentification);

            request.setAttribute("activity", activityCompany);
        } catch (Exception e) {
            e.printStackTrace();
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
            String activityIdentification = request.getParameter("identification");
            if (StringUtils.isBlank(activityIdentification)) {
                return getResult(result, null, false, "2", "参数不能为空");
            }
            ActivityCompany activityCompany = activityCompanyService.getDao().queryByIdentification(activityIdentification);
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
            result.setSuccess(true);
            result.setResultMessage("成功");
            result.setResult(activityCompany);
            result.setResultCode("0");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
