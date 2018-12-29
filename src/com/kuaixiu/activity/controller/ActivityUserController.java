package com.kuaixiu.activity.controller;

import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.exception.SystemException;
import com.common.paginate.Page;
import com.common.wechat.aes.AesCbcUtil;
import com.common.wechat.common.util.StringUtils;
import com.kuaixiu.activity.entity.ActivityProject;
import com.kuaixiu.activity.entity.ActivityUser;
import com.kuaixiu.activity.service.ActivityProjectService;
import com.kuaixiu.activity.service.ActivityUserService;
import com.system.api.entity.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    @Autowired
    private ActivityUserService activityUserService;
    @Autowired
    private ActivityProjectService activityProjectService;

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
                user.setCompanyName(companyName);
                user.setPerson(person);
                user.setQueryStartTime(queryStartTime);
                user.setQueryEndTime(queryEndTime);
                user.setPage(page);
                List<Map<String, Object>> users=new ArrayList<Map<String,Object>>();
                users = activityUserService.getDao().queryEstimateForPage(user);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                for(Map<String,Object> map:users){
                    for(String s:map.keySet()){
                        Long createTime = sdf.parse(map.get("updateTime").toString()).getTime();
                        Long endTime = sdf.parse(map.get("endTime").toString()).getTime();
                        if (createTime > endTime) {
                            if("1".equals(estimateResult)){
                                continue;
                            }
                            map.put("estimateResult", 1);
                        }else{
                            if("2".equals(estimateResult)){
                                continue;
                            }

                            map.put("estimateResult", 2);
                        }
                        map.put("businessType", 1);
                        objects.add(map);
                    }

                }
            }
            if (StringUtils.isBlank(businessType) || "2".equals(businessType)) {
                //电信业务
            }

            page.setData(objects);
        } catch (Exception e) {
            e.printStackTrace();
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
    @RequestMapping(value = "/activityCompany/login")
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

            result.setResultCode("0");
            result.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
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
            if (StringUtils.isBlank(type) || "0".equals(type)) {
                throw new SystemException("请求参数不完整");
            }
            ActivityProject project = new ActivityProject();
            project.setType(Integer.valueOf(type));
            List<ActivityProject> projects = activityProjectService.queryList(project);

            result.setResult(projects);
            result.setResultCode("0");
            result.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
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
            String person = params.getString("person");
            String number = params.getString("number");
            String model = params.getString("model");
            String project = params.getString("project");//1,5,9,6,3,4,
            String fault = params.getString("fault");//故障现象

            if (StringUtils.isNotBlank(project)) {
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
            }
            ActivityUser user = activityUserService.getDao().queryByIdent(activityIdent);
            user.setPerson(person);
            user.setNumber(number);
            user.setModel(model);
            user.setProject(project);
            user.setFault(fault);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date newTime = new Date();
            user.setUpdateTime(sdf.format(newTime));
            activityUserService.getDao().updateByIden(user);

            result.setResultCode("0");
            result.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
