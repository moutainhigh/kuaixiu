package com.kuaixiu.videoCard.controller;

import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.importExcel.ImportReport;
import com.common.paginate.Page;
import com.google.common.collect.Maps;
import com.kuaixiu.videoCard.entity.VideoCard;
import com.kuaixiu.videoCard.service.VideoCardService;
import com.kuaixiu.videoUserRel.entity.VideoUserRel;
import com.kuaixiu.videoUserRel.service.VideoUserRelService;
import com.system.util.ExcelUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * VideoCard Controller
 *
 * @CreateDate: 2019-08-15 下午03:37:26
 * @version: V 1.0
 */
@Controller
public class VideoCardController extends BaseController {

    @Autowired
    private VideoCardService videoCardService;
    @Autowired
    private VideoUserRelService videoUserRelService;


    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/videoCard/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {

        String returnView ="videoCard/videoCardList";
        return new ModelAndView(returnView);
    }




    @RequestMapping(value="/videoCard/import")
    public ModelAndView cardImport(HttpServletRequest request,HttpServletResponse response){
        return new ModelAndView("videoCard/importIndex");
    }



    @RequestMapping(value = "/videoCard/queryListForPage")
    public void cardListForPage(HttpServletRequest request,
                                HttpServletResponse response) throws Exception {
        Page page = getPageByRequest(request);
        String cardId=request.getParameter("cardId");
        String type=request.getParameter("type");
        String use=request.getParameter("isUse");
        VideoCard s=new VideoCard();
        s.setCardId(cardId);
        if(StringUtils.isNotBlank(type)){
            s.setType(Integer.parseInt(type));
        }
        if(StringUtils.isNotBlank(use)){
            s.setIsUse(Integer.parseInt(use));
        }
        s.setPage(page);
        List<VideoCard> list = videoCardService.queryListForPage(s);
        for(VideoCard videoCard:list){
            VideoUserRel userRel=videoUserRelService.getDao().queryByCardId(videoCard.getCardId());
            if(userRel!=null){
                videoCard.setOrderNo(userRel.getOrderNo());
            }
        }
        page.setData(list);
        this.renderJson(response, page);
    }


    /**
     * 卡密模板导入
     * @param myfile
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/videoCard/startImport")
    public void doImport(
            @RequestParam("fileInput") MultipartFile myfile,
            HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        // 返回结果，默认失败
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
        ImportReport report = new ImportReport();
        StringBuffer errorMsg = new StringBuffer();
        try{
            if(myfile != null && StringUtils.isNotBlank(myfile.getOriginalFilename())){
                String fileName=myfile.getOriginalFilename();
                //扩展名
                String extension= FilenameUtils.getExtension(fileName);
                if (!extension.equalsIgnoreCase("xls")){
                    errorMsg.append("导入文件格式错误！只能导入excel  xls文件！");
                }
                else{
                    videoCardService.importExcel(myfile,report,getCurrentUser(request));
                    resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
                    resultMap.put(RESULTMAP_KEY_MSG, "导入成功");
                }
            }
            else{
                errorMsg.append("导入文件为空");
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            errorMsg.append("导入失败");
            resultMap.put(RESULTMAP_KEY_MSG, "导入失败");
        }
        request.setAttribute("report", report);
        resultMap.put(RESULTMAP_KEY_DATA, report);
        renderJson(response, resultMap);
    }


    @RequestMapping("/videoCard/personList")
    @ResponseBody
    public void personList(HttpServletRequest request,
                            HttpServletResponse response) throws IOException {
//        SessionUser su = getCurrentUser(request);
//        String loginId = su.getUser().getLoginId();
//        List<VideoCard> list=new ArrayList<>();
//        if(!StringUtils.isBlank(loginId)){
//            VideoUserRel rel=new VideoUserRel();
//            rel.setMobile(loginId);
//            list=videoCardService.getVideoUser(rel);
//        }
        System.out.println("videoCard/personList 请求");
        Map<String, Object> resultMap = Maps.newHashMap();
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
        resultMap.put("data", getData());
        renderJson(response, resultMap);
    }







    public static void projectNews() throws Exception {
        String file = "E:\\code\\爱奇艺卡密类型.xlsx";
        Object o = ExcelUtil.testProjectList(file);
        List<VideoCard> list = (List<VideoCard>) o;   //获取excel信息
        System.out.println("卡密信息："+ JSONObject.toJSONString(list));



    }


    public static void main(String[] args) throws Exception {
//        projectNews();
        List<VideoCard> list=new ArrayList<>();
        VideoCard card=new VideoCard();
        card.setId(1L);
        card.setCardId("dadada");
        card.setType(1);
        card.setPrice(new BigDecimal("10"));
        card.setValidityTime("2019-08-14");
        card.setIsUse(1);
        card.setCreateTime(new Date());
        list.add(card);

        VideoCard card1=new VideoCard();
        card1.setId(2L);
        card1.setCardId("sssssdsa");
        card1.setType(2);
        card1.setPrice(new BigDecimal("19.8"));
        card1.setValidityTime("2020-08-14");
        card1.setIsUse(1);
        card1.setCreateTime(new Date());
        list.add(card1);


        VideoCard card2=new VideoCard();
        card2.setId(3L);
        card2.setCardId("xxxxxs");
        card2.setType(3);
        card2.setPrice(new BigDecimal("58"));
        card2.setValidityTime("2019-08-14");
        card2.setIsUse(1);
        card2.setCreateTime(new Date());
        list.add(card2);


        VideoCard card3=new VideoCard();
        card3.setId(4L);
        card3.setCardId("dadssdas");
        card3.setType(4);
        card3.setPrice(new BigDecimal("108"));
        card3.setValidityTime("2019-08-14");
        card3.setIsUse(1);
        card3.setCreateTime(new Date());
        list.add(card3);


        VideoCard card4=new VideoCard();
        card4.setId(5L);
        card4.setCardId("tesdfsdf");
        card4.setType(5);
        card4.setPrice(new BigDecimal("198"));
        card4.setValidityTime("2019-08-14");
        card4.setIsUse(1);
        card4.setCreateTime(new Date());
        list.add(card4);

        System.out.println(JSONObject.toJSONString(list));


    }


    public List<VideoCard> getData(){
        List<VideoCard> list=new ArrayList<>();
        VideoCard card=new VideoCard();
        card.setId(1L);
        card.setCardId("dadada");
        card.setType(1);
        card.setPrice(new BigDecimal("10"));
        card.setValidityTime("2019-08-14");
        card.setIsUse(1);
        card.setCreateTime(new Date());
        list.add(card);

        VideoCard card1=new VideoCard();
        card1.setId(2L);
        card1.setCardId("sssssdsa");
        card1.setType(2);
        card1.setPrice(new BigDecimal("19.8"));
        card1.setValidityTime("2020-08-14");
        card1.setIsUse(1);
        card1.setCreateTime(new Date());
        list.add(card1);


        VideoCard card2=new VideoCard();
        card2.setId(3L);
        card2.setCardId("xxxxxs");
        card2.setType(3);
        card2.setPrice(new BigDecimal("58"));
        card2.setValidityTime("2019-08-14");
        card2.setIsUse(1);
        card2.setCreateTime(new Date());
        list.add(card2);


        VideoCard card3=new VideoCard();
        card3.setId(4L);
        card3.setCardId("dadssdas");
        card3.setType(4);
        card3.setPrice(new BigDecimal("108"));
        card3.setValidityTime("2019-08-14");
        card3.setIsUse(1);
        card3.setCreateTime(new Date());
        list.add(card3);


        VideoCard card4=new VideoCard();
        card4.setId(5L);
        card4.setCardId("tesdfsdf");
        card4.setType(5);
        card4.setPrice(new BigDecimal("198"));
        card4.setValidityTime("2019-08-14");
        card4.setIsUse(1);
        card4.setCreateTime(new Date());
        list.add(card4);

        System.out.println(JSONObject.toJSONString(list));
        return list;
    }

}
