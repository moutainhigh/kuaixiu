package com.kuaixiu.activity.controller;

import com.common.base.controller.BaseController;
import com.common.wechat.common.util.StringUtils;
import com.kuaixiu.activity.entity.ActivityCompany;
import com.kuaixiu.activity.service.ActivityCompanyService;
import com.system.api.entity.ResultData;
import com.system.basic.user.entity.SessionUser;
import com.system.constant.SystemConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * ActivityCompany Controller
 *
 * @CreateDate: 2018-12-25 上午10:13:01
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
        
        String returnView ="activityCompany/list";
        return new ModelAndView(returnView);
    }

    /**
     * 保存公司活动信息
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/activityCompany/add")
    @ResponseBody
    public ResultData addActivity(HttpServletRequest request,
                                  HttpServletResponse response) throws Exception {
        ResultData result=new ResultData();
        try {
            SessionUser user=getCurrentUser(request);
            String companyName = request.getParameter("companyName");
            String kxBusiness = request.getParameter("kxBusiness");
            String kxBusinessDetail = request.getParameter("kxBusinessDetail");
            String dxIncrementBusiness = request.getParameter("dxIncrementBusiness");
            String dxIncrementBusinessDetail = request.getParameter("dxIncrementBusinessDetail");

            if(StringUtils.isBlank(companyName)||StringUtils.isBlank(kxBusiness)||StringUtils.isBlank(kxBusinessDetail)
                    ||StringUtils.isBlank(dxIncrementBusiness)||StringUtils.isBlank(dxIncrementBusinessDetail)){
                return getResult(result,null,false,"2","参数不能为空");
            }

            //获取图片，保存图片到webapp同级inages/activityCompany目录
            String savePath = serverPath(request.getServletContext().getRealPath("")) + System.getProperty("file.separator") + SystemConstant.IMAGE_PATH + System.getProperty("file.separator") + "activityCompany";
            String logoPath=getPath(request,"file",savePath);             //图片路径
            String imageUrl = getProjectUrl(request) + "/images/activityCompany/" + logoPath.substring(logoPath.lastIndexOf("/") + 1);
            System.out.println("图片路径："+savePath);

            ActivityCompany company=new ActivityCompany();
            company.setCompanyId(UUID.randomUUID().toString().replace("-","").substring(0,16));
            company.setCompanyName(companyName);
            company.setActivityIdentification(UUID.randomUUID().toString().replace("",""));
            company.setActivityImgUrl(imageUrl);
            company.setKxBusinessId(UUID.randomUUID().toString().replace("-","").substring(0,16));
            company.setDxIncrementBusinessId(UUID.randomUUID().toString().replace("-","").substring(0,16));
            company.setKxBusiness(kxBusiness);
            company.setKxBusinessDetail(kxBusinessDetail);
            company.setDxIncrementBusiness(dxIncrementBusiness);
            company.setDxIncrementBusinessDetail(dxIncrementBusinessDetail);
            company.setCreateUser(user.getUserId());

            activityCompanyService.add(company);

            result.setSuccess(true);
            result.setResultMessage("保存成功");
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 保存公司活动信息
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/activityCompany/getActivity")
    @ResponseBody
    public ResultData getActivity(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        ResultData result=new ResultData();
        try {
            SessionUser user=getCurrentUser(request);

        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }


}
