package com.kuaixiu.balance.controller;

import com.common.base.controller.BaseController;
import com.common.paginate.Page;
import com.kuaixiu.balance.entity.BalanceProvider;
import com.kuaixiu.balance.service.BalanceProviderService;
import com.kuaixiu.balance.service.BalanceService;
import com.system.basic.user.entity.SessionUser;
import com.system.constant.SystemConstant;
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
 * Balance Controller
 *
 * @CreateDate: 2016-10-15 下午02:51:40
 * @version: V 1.0
 */
@Controller
public class BalanceProviderController extends BaseController {

    @Autowired
    private BalanceService balanceService;
    @Autowired
    private BalanceProviderService balanceProviderService;

    /**
     * 列表查询
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/balanceProvider/list")
    public ModelAndView list(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="balance/batchDetail";
        return new ModelAndView(returnView);
    }
    
    /**
     * queryListForPage
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/balanceProvider/queryListForPage")
    public void queryListForPage(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        BalanceProvider b = new BalanceProvider();
        
        String batchNo = request.getParameter("batchNo");
        b.setBatchNo(batchNo);
        //获取登录用户
        SessionUser su = getCurrentUser(request);
        //判断用户类型系统管理员可以查看所有订单
        if (su.getType() == SystemConstant.USER_TYPE_SYSTEM) {
            
        }
        else if (su.getType() == SystemConstant.USER_TYPE_PROVIDER) {
            //连锁商只能查看自己的订单
            b.setProviderCode(su.getProviderCode());
        }
        Page page = getPageByRequest(request);
        b.setPage(page);
        List<BalanceProvider> list = balanceProviderService.queryListForPage(b);
        
        page.setData(list);
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
    @RequestMapping(value = "/balanceProvider/listForProvider")
    public ModelAndView listForProvider(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        
        String returnView ="balance/listForProvider";
        return new ModelAndView(returnView);
    }
    
    /**
     * 批次详情
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/balanceProvider/balanceDetail")
    public ModelAndView batchDetail(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        String id = request.getParameter("id");
        BalanceProvider balance = balanceProviderService.queryById(id);
        
        request.setAttribute("balance", balance);
        String returnView ="balance/balanceDetail";
        return new ModelAndView(returnView);
    }
    
    private static final String isImg = ".+\\.bmp$|.+\\.jpeg$|.+\\.jpg$|.+\\.gif$|.+\\.png$";
    /**
     * 上传打款回执
     * @param myfile
     * @param request
     * @param response
     * @throws IOException
     * @author: lijx
     * @CreateDate: 2016-10-21 下午10:53:57
     */
    @RequestMapping(value = "/balanceProvider/uploadImg")
    public void uploadImg(@RequestParam("uploadFile") MultipartFile myfile,
            HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        // 返回结果，默认失败
        Map<String, Object> resultMap = new HashMap<String, Object>();
        // 校验图片格式
        String fileName = myfile.getOriginalFilename();
        // 校验文件大小
        long filezice = myfile.getSize();
        if(!fileName.matches(isImg)){
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
            resultMap.put(RESULTMAP_KEY_MSG, "请选择正确的图片格式：bmp, jpeg, jpg, gif, png！");
        }
        else if(filezice > (10 * 1024 * 1024)){
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
            resultMap.put(RESULTMAP_KEY_MSG, "图片不可大于10M！");
        } 
        else{
            String id = request.getParameter("id");
            SessionUser su = getCurrentUser(request);
            // 添加商品图片
            String fileNamePath = balanceProviderService.importImg(myfile, id , su);
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
            resultMap.put(RESULTMAP_KEY_MSG, "上传成功");
        }
        this.renderJson(response, resultMap);
    }
    
    /**
     * 删除图片
     * @param request
     * @param response
     * @throws Exception
     * @author: lijx
     * @CreateDate: 2016-10-22 上午2:04:17
     */
    @RequestMapping(value = "/balanceProvider/deleteImg")
    public void deleteImg(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String id = request.getParameter("id");
        SessionUser su = getCurrentUser(request);
        balanceProviderService.deleteImg(id, su);
        // 返回结果，默认失败
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
        resultMap.put(RESULTMAP_KEY_MSG, "删除成功");
        this.renderJson(response, resultMap);
    }
    
   
    
}
