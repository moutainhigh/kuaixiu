package com.kuaixiu.groupShortUrl.controller;

import com.common.base.controller.BaseController;
import com.common.importExcel.ImportReport;
import com.common.paginate.Page;
import com.kuaixiu.groupShortUrl.entity.HsGroupShortUrlMobile;
import com.kuaixiu.groupShortUrl.service.HsGroupShortUrlMobileService;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HsGroupShortUrlMobile Controller
 *
 * @CreateDate: 2019-06-26 上午09:28:20
 * @version: V 1.0
 */
@Controller
public class HsGroupShortUrlMobileController extends BaseController {

    @Autowired
    private HsGroupShortUrlMobileService hsGroupShortUrlMobileService;

    /**
     * 跳转手机号列表
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "groupShort/toGroupMobile")
    public ModelAndView toGroupMobile(HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {

        String returnView = "groupShort/groupMobileList";
        return new ModelAndView(returnView);
    }

    /**
     * 手机号列表
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "groupShort/groupMobileForPage")
    public void groupMobileForPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Page page = getPageByRequest(request);
        HsGroupShortUrlMobile hsGroupMobile = new HsGroupShortUrlMobile();
        hsGroupMobile.setPage(page);
        List<HsGroupShortUrlMobile> hsGroupMobiles = hsGroupShortUrlMobileService.queryListForPage(hsGroupMobile);
        page.setData(hsGroupMobiles);
        this.renderJson(response, page);
    }

    /**
     * 跳转群发短信手机号导入
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/groupShort/importMobile")
    public ModelAndView importMobile(HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {

        String returnView ="groupShort/importMobile";
        return new ModelAndView(returnView);
    }

    /**
     * fileUpload:群发短信手机号导入.
     *
     * @param myfile   上传的文件
     * @param request  请求实体
     * @param response 返回实体
     * @throws IOException 异常信息
     * @date 2016-5-9
     * @author
     */
    @RequestMapping(value = "/groupShort/import")
    public void doImportCompany(
            @RequestParam("fileInput") MultipartFile myfile,
            HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        // 返回结果，默认失败
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
        ImportReport report = new ImportReport();
        StringBuffer errorMsg = new StringBuffer();
        try {
            if (myfile != null && StringUtils.isNotBlank(myfile.getOriginalFilename())) {
                String fileName = myfile.getOriginalFilename();
                //扩展名
                String extension = FilenameUtils.getExtension(fileName);
                if (!extension.equalsIgnoreCase("xls")) {
                    errorMsg.append("导入文件格式错误！只能导入excel文件！");
                } else {
                    hsGroupShortUrlMobileService.importExcel(myfile, report, getCurrentUser(request));
                    resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
                    resultMap.put(RESULTMAP_KEY_MSG, "导入成功");
                }
            } else {
                errorMsg.append("导入文件为空");
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMsg.append("导入失败");
            resultMap.put(RESULTMAP_KEY_MSG, "导入失败");
        }
        request.setAttribute("report", report);
        resultMap.put(RESULTMAP_KEY_DATA, report);
        renderJson(response, resultMap);
    }
}
