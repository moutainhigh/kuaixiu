//package com.kuaixiu.videoCard.controller;
//
//import com.alibaba.fastjson.JSONObject;
//import com.common.base.controller.BaseController;
//import com.common.importExcel.ImportReport;
//import com.google.common.collect.Maps;
//import com.kuaixiu.order.constant.OrderConstant;
//import com.kuaixiu.videoCard.entity.VideoCard;
//import com.kuaixiu.videoCard.service.VideoCardService;
//import com.kuaixiu.videoUserRel.entity.VideoUserRel;
//import com.kuaixiu.videoUserRel.service.VideoUserRelService;
//import com.system.basic.user.entity.SessionUser;
//import com.system.constant.SystemConstant;
//import com.system.util.ExcelUtil;
//import jodd.util.StringUtil;
//import org.apache.commons.io.FilenameUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.servlet.ModelAndView;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.math.BigDecimal;
//import java.text.SimpleDateFormat;
//import java.util.*;
//
///**
// * VideoCard Controller
// *
// * @CreateDate: 2019-08-15 下午03:37:26
// * @version: V 1.0
// */
//@Controller
//public class VideoCardController extends BaseController {
//
//    @Autowired
//    private VideoCardService videoCardService;
//
//
//    /**
//     * 列表查询
//     *
//     * @param request
//     * @param response
//     * @return
//     * @throws Exception
//     */
//    @RequestMapping(value = "/videoCard/list")
//    public ModelAndView list(HttpServletRequest request,
//                              HttpServletResponse response) throws Exception {
//
//        String returnView ="videoCard/videoCardList";
//        return new ModelAndView(returnView);
//    }
//
//
//
//
//    @RequestMapping(value="/videoCard/import")
//    public ModelAndView cardImport(HttpServletRequest request,HttpServletResponse response){
//        return new ModelAndView("videoCard/importIndex");
//    }
//
//
//
//    /**
//     * 卡密模板导入
//     * @param myfile
//     * @param request
//     * @param response
//     * @throws IOException
//     */
//    @RequestMapping(value = "/videoCard/startImport")
//    public void doImport(
//            @RequestParam("fileInput") MultipartFile myfile,
//            HttpServletRequest request, HttpServletResponse response)
//            throws IOException {
//        // 返回结果，默认失败
//        Map<String, Object> resultMap = new HashMap<String, Object>();
//        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
//        ImportReport report = new ImportReport();
//        StringBuffer errorMsg = new StringBuffer();
//        try{
//            if(myfile != null && StringUtils.isNotBlank(myfile.getOriginalFilename())){
//                String fileName=myfile.getOriginalFilename();
//                //扩展名
//                String extension= FilenameUtils.getExtension(fileName);
//                if (!extension.equalsIgnoreCase("xls")){
//                    errorMsg.append("导入文件格式错误！只能导入excel  xls文件！");
//                }
//                else{
////                    stationService.importExcel(myfile,report,getCurrentUser(request));
//                    resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
//                    resultMap.put(RESULTMAP_KEY_MSG, "导入成功");
//                }
//            }
//            else{
//                errorMsg.append("导入文件为空");
//            }
//        }
//        catch(Exception e) {
//            e.printStackTrace();
//            errorMsg.append("导入失败");
//            resultMap.put(RESULTMAP_KEY_MSG, "导入失败");
//        }
//        request.setAttribute("report", report);
//        resultMap.put(RESULTMAP_KEY_DATA, report);
//        renderJson(response, resultMap);
//    }
//
//
//    @RequestMapping("/videoCard/personList")
//    @ResponseBody
//    public void personList(HttpServletRequest request,
//                            HttpServletResponse response) throws IOException {
//        SessionUser su = getCurrentUser(request);
//        String loginId = su.getUser().getLoginId();
//        List<VideoCard> list=new ArrayList<>();
//        if(!StringUtils.isBlank(loginId)){
//            VideoUserRel rel=new VideoUserRel();
//            rel.setMobile(loginId);
//            list=videoCardService.getVideoUser(rel);
//        }
//        Map<String, Object> resultMap = Maps.newHashMap();
//        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
//        resultMap.put("data", list);
//        renderJson(response, resultMap);
//    }
//
//
//
//
//
//    public static void projectNews() throws Exception {
//        String file = "E:\\code\\爱奇艺卡密类型.xlsx";
//        Object o = ExcelUtil.testProjectList(file);
//        List<VideoCard> list = (List<VideoCard>) o;   //获取excel信息
//        System.out.println("卡密信息："+ JSONObject.toJSONString(list));
//
//
//
//    }
//
//
//    public static void main(String[] args) throws Exception {
//        projectNews();
//    }
//
//
//}
