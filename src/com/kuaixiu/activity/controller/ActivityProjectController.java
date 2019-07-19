package com.kuaixiu.activity.controller;

import com.common.base.controller.BaseController;
import com.common.paginate.Page;
import com.kuaixiu.activity.entity.ActivityProject;
import com.kuaixiu.activity.service.ActivityCompanyService;
import com.kuaixiu.activity.service.ActivityProjectService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Administrator on 2019/1/23/023.
 */
public class ActivityProjectController extends BaseController{
    private static final Logger log = Logger.getLogger(ActivityProjectController.class);

    @Autowired
    private ActivityCompanyService activityCompanyService;
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
    @RequestMapping(value = "/activityCompany/projectList")
    public ModelAndView list(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {

        String returnView = "activity/project";
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
    @RequestMapping(value = "/activityCompany/getProjectForPage")
    @ResponseBody
    public void getProjectForPage(HttpServletRequest request,
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
            ActivityProject activityProject=new ActivityProject();

            activityProject.setPage(page);
            List<ActivityProject> activityProjects=activityProjectService.getDao().queryListForPage(activityProject);


            page.setData(activityProjects);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        this.renderJson(response, page);
    }
}
