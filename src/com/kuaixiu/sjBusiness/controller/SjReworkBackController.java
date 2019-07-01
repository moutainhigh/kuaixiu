package com.kuaixiu.sjBusiness.controller;

import com.common.base.controller.BaseController;
import com.common.paginate.Page;
import com.common.util.NOUtil;
import com.common.wechat.common.util.StringUtils;
import com.kuaixiu.sjBusiness.entity.OrderContractPicture;
import com.kuaixiu.sjBusiness.entity.SjReworkOrder;
import com.kuaixiu.sjBusiness.service.SjOrderService;
import com.kuaixiu.sjBusiness.service.SjReworkOrderService;
import com.kuaixiu.sjUser.entity.ConstructionCompany;
import com.kuaixiu.sjUser.entity.SjSessionUser;
import com.kuaixiu.sjUser.entity.SjUser;
import com.kuaixiu.sjUser.entity.SjWorker;
import com.kuaixiu.sjUser.service.ConstructionCompanyService;
import com.kuaixiu.sjUser.service.SjUserService;
import com.kuaixiu.sjUser.service.SjWorkerService;
import com.system.api.entity.ResultData;
import com.system.constant.SystemConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2019/6/28/028.
 */
@Controller
public class SjReworkBackController extends BaseController {

    @Autowired
    private SjReworkOrderService sjReworkOrderService;
    @Autowired
    private SjOrderService orderService;
    @Autowired
    private SjUserService sjUserService;

    /**
     * 报障订单列表
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sj/order/reworkList")
    public ModelAndView reworkList(HttpServletRequest request,
                                   HttpServletResponse response) throws Exception {
        String returnView = "businessRework/reworkOrderList";
        return new ModelAndView(returnView);
    }

    /**
     * 待接单列表
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sj/order/stayTakeList")
    public ModelAndView stayTakeList(HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        String returnView = "businessRework/stayTakeOrderList";
        return new ModelAndView(returnView);
    }

    /**
     * 待修复列表
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sj/order/stayRepairList")
    public ModelAndView stayRepairList(HttpServletRequest request,
                                       HttpServletResponse response) throws Exception {
        String returnView = "businessRework/stayRepairOrderList";
        return new ModelAndView(returnView);
    }

    /**
     * 订单刷新列表
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/sj/order/reworkQueryListForPage")
    public void queryListForPage(HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        Page page = getPageByRequest(request);
        try {
            //获取查询条件
            String state = request.getParameter("state");
            String queryStartTime = request.getParameter("query_startTime");
            String queryEndTime = request.getParameter("query_endTime");
            String createUser = request.getParameter("createUser");
            String companyName = request.getParameter("companyName");
            SjReworkOrder sjReworkOrder = new SjReworkOrder();
            if (StringUtils.isNotBlank(state)) {
                sjReworkOrder.setState(Integer.valueOf(state));
            }
            SjSessionUser sjSessionUser = getSjCurrentUser(request);
            if (sjSessionUser.getType() == 3) {
                sjReworkOrder.setCompanyId(sjSessionUser.getUserId());
            }else if(sjSessionUser.getType() == 8){
                sjReworkOrder.setWorkerId(sjSessionUser.getUserId());
            }
            sjReworkOrder.setQueryStartTime(queryStartTime);
            sjReworkOrder.setQueryEndTime(queryEndTime);
            sjReworkOrder.setCreateUserid(createUser);
            sjReworkOrder.setCompanyName(companyName);
            sjReworkOrder.setPage(page);
            List<SjReworkOrder> sjReworkOrders = sjReworkOrderService.queryListForPage(sjReworkOrder);
            for (SjReworkOrder sjReworkOrder1 : sjReworkOrders) {
                sjReworkOrder1.setUserType(sjSessionUser.getType());
                List<String> projects = orderService.getProject(sjReworkOrder1.getProjectIds());
                String projectName = orderService.listToString(projects);
                sjReworkOrder1.setProjectName(projectName);
            }
            page.setData(sjReworkOrders);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.renderJson(response, page);
    }


    /**
     * 取消报障订单
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sj/order/cancelReworkOrder")
    @ResponseBody
    public ResultData submitReworkOrder(HttpServletRequest request,
                                        HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        try {
            String id = request.getParameter("id");
            SjReworkOrder sjReworkOrder = sjReworkOrderService.getDao().queryById(id);
            SjSessionUser sjSessionUser = getSjCurrentUser(request);
            sjReworkOrder.setIsDel(1);
            sjReworkOrder.setUpdateUserid(sjSessionUser.getUserId());
            sjReworkOrderService.saveUpdate(sjReworkOrder);
            getSjResult(result, null, true, "0", null, "取消成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 报障订单详情
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sj/order/reworkOrderDetail")
    public ModelAndView reworkOrderDetail(HttpServletRequest request,
                                          HttpServletResponse response) throws Exception {
        try {
            String id = request.getParameter("id");
            SjReworkOrder sjReworkOrder = sjReworkOrderService.getDao().queryById(id);
            List<String> projects = orderService.getProject(sjReworkOrder.getProjectIds());
            String projectName = orderService.listToString(projects);
            sjReworkOrder.setProjectName(projectName);
            request.setAttribute("sjOrder", sjReworkOrder);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String returnView = "businessRework/reworkDetail";
        return new ModelAndView(returnView);
    }


    /**
     * 跳转查询员工
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sj/order/toSelectWorker")
    public ModelAndView toAssignReworkOrder(HttpServletRequest request,
                                            HttpServletResponse response) throws Exception {
        String reworkId = request.getParameter("reworkId");
        SjSessionUser sjSessionUser = getSjCurrentUser(request);
        SjUser sjUser = sjUserService.getDao().queryByLoginId(sjSessionUser.getUserId(), 3);
        ConstructionCompany company = companyService.getDao().queryByLoginId(sjUser.getId());
        request.setAttribute("companyId", company.getLoginId());
        request.setAttribute("reworkId", reworkId);
        String returnView = "businessRework/assignOrder";
        return new ModelAndView(returnView);
    }

    @Autowired
    private ConstructionCompanyService companyService;

    /**
     * 分配订单员工列表
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/sj/order/selectWorker")
    public void selectWorker(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {
        Page page = getPageByRequest(request);
        try {
            //获取查询条件
            String workerUserId = request.getParameter("workerUserId");
            String companyId = request.getParameter("companyId");
            String workerName = request.getParameter("workerName");
            SjUser sjUser = new SjUser();
            sjUser.setCompanyLoginId(companyId);
            sjUser.setWorkerUserId(workerUserId);
            sjUser.setWorkerName(workerName);
            sjUser.setPage(page);
            List<SjUser> sjWorkers = sjUserService.getDao().assignReworkWorker(sjUser);
            page.setData(sjWorkers);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.renderJson(response, page);
    }

    /**
     * 公司分配报障订单
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sj/order/assignReworkWorker")
    @ResponseBody
    public ResultData assignReworkWorker(HttpServletRequest request,
                                        HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        try {
            String reworkId = request.getParameter("reworkId");
            String workerId = request.getParameter("workerId");
            SjReworkOrder sjReworkOrder = sjReworkOrderService.getDao().queryById(reworkId);
            SjUser sjUser=sjUserService.queryById(workerId);
            sjReworkOrder.setWorkerId(sjUser.getLoginId());
            sjReworkOrder.setWorkerName(sjUser.getName());
            sjReworkOrder.setWorkerTakeOrderTime(new Date());
            sjReworkOrder.setState(400);
            sjReworkOrderService.saveUpdate(sjReworkOrder);
            getSjResult(result, null, true, "0", null, "分配成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 上传顾客签字照片
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sj/order/upSignImage")
    @ResponseBody
    public ResultData upContractImage(HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        try {
            String reworkId = request.getParameter("reworkId");
            //获取图片，保存图片到webapp同级inages/sj_images
            String imageName = NOUtil.getNo("img-") + NOUtil.getRandomInteger(4);
            String savePath = serverPath(request.getServletContext().getRealPath("")) + System.getProperty("file.separator") + SystemConstant.IMAGE_PATH + System.getProperty("file.separator") + "sj_images" + System.getProperty("file.separator") + "sj_sign";
            String logoPath = getPath(request, "img", savePath, imageName);             //图片路径
            String imageUrl = getProjectUrl(request) + "/images/sj_images/sj_sign/" + logoPath.substring(logoPath.lastIndexOf("/") + 1);
            System.out.println("图片路径：" + savePath);
            SjReworkOrder sjReworkOrder=sjReworkOrderService.queryById(reworkId);
            sjReworkOrder.setPictureUrl(imageUrl);
            sjReworkOrder.setState(500);
            sjReworkOrder.setEndTime(new Date());
            sjReworkOrderService.saveUpdate(sjReworkOrder);
            getSjResult(result, imageUrl, true, "0", null, "上传成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
