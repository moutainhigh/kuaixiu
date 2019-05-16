package com.system.basic.download.controller;

import com.common.base.controller.BaseController;
import com.common.exception.SystemException;
import com.common.util.DateUtil;
import com.system.basic.download.entity.SysReport;
import com.system.basic.download.service.FileDownloadService;
import com.system.basic.user.entity.SessionUser;
import com.system.constant.SystemConstant;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


/**
 * 文件下载
 * 
 * @author: wugl
 * @CreateDate: 2016-09-11 00:11
 * @version: V 1.0
 * 
 */
@Controller
public class FileDownloadController extends BaseController{
    private final Logger logger = Logger.getLogger(FileDownloadController.class);
    @Autowired
    private FileDownloadService fileDownloadService;
    /**
     * 文件下载
     * 
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/file/download")
    public String download(HttpServletRequest request, HttpServletResponse response) throws Exception{

        try {
            request.setCharacterEncoding("UTF-8");
            response.setCharacterEncoding("UTF-8");
            String fileId = request.getParameter("fileId");
            SysReport sysReport = fileDownloadService.queryById(fileId);
            String nowDate = DateUtil.getSerialFullDate();
            
            Map<String, String> requestMap = getRequestParameterMapStr(request);
            
            String outFilePath = File.separator + "output" + File.separator + sysReport.getFileOutName().replace("{date}", nowDate);
            String path = WebUtils.getRealPath(request.getServletContext(), outFilePath);
            
            //根据模板导出数据模式
            if(sysReport.getType() == 1){
                //获取登录用户
                SessionUser su = getCurrentUser(request);
                if(fileId.equals("15")){
                   //碎屏险导出 只有管理员和碎屏险商有权限
                	if(su.getType() != SystemConstant.USER_TYPE_SYSTEM&&su.getType()!=SystemConstant.USER_TYPE_SCREEN){
                		throw new SystemException("对不起您没有权限操作！");
                	}
                }
                if(fileId.equals("17")){
                	//碎屏险免激活用户导出 只有管理员有权限
                	if(su.getType() != SystemConstant.USER_TYPE_SYSTEM){
                		throw new SystemException("对不起您没有权限操作！");
                	}
                }
                if(fileId.equals("21")){
                    //站点信息导出 只有管理员,客服,号卡站点人员有权限
                    if(su.getType() != SystemConstant.USER_TYPE_SYSTEM&&su.getType()!=SystemConstant.USER_TYPE_CUSTOMER_SERVICE&&su.getType()!=SystemConstant.STATION_USER){
                        throw new SystemException("对不起您没有权限操作！");
                    }
                }
                
                //获取上下文
                WebApplicationContext context = (WebApplicationContext) request
                        .getSession()
                        .getServletContext()
                        .getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
               
                //获取service对象
                Object value = context.getBean(sysReport.getClassPath());
                
                //反射方法名
                Method process = value.getClass().getMethod(sysReport.getMethodName(), new Class[]{Map.class});
                
                String tempFilePath = File.separator + "templates" + File.separator + sysReport.getFileTempName();
                String tempPath = WebUtils.getRealPath(request.getServletContext(), tempFilePath);
                //拼接参数
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("tempFileName", tempPath);
                params.put("outFileName", path);
                params.put("response", response);
                params.put("user", su);
                params.putAll(requestMap);
                
                //执行service方法
                process.invoke(value, new Object[]{params});
            }

            String userAgent = request.getHeader("USER-AGENT").toLowerCase();
            try {
                response.reset();
            }catch (IllegalStateException e){

            }
            response.setContentType("application/x-msdownload");

            if (userAgent.indexOf("firefox") >= 0) {
                response.addHeader("Content-Disposition", "attachment; filename=\"" + new String(sysReport.getFileName().getBytes("UTF-8"), "ISO-8859-1")
                        + "\"");
            } 
            else {
                response.addHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode(sysReport.getFileName(), "UTF-8") + "\"");
            }

            InputStream inputStream = new FileInputStream(path);

            OutputStream os = response.getOutputStream();
            byte[] b = new byte[2048];
            int length;
            while ((length = inputStream.read(b)) > 0) {
                os.write(b, 0, length);
            }

            os.close();
            inputStream.close();
        } 
        catch (FileNotFoundException e) {
            logger.error("FileDownload_FileNotFoundException", e);
        } 
        catch (IOException e) {
            logger.error("FileDownload_IOException", e);
        }
        return null;
    }

    /**
     * 文件下载
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/sj/file/download")
    public String sjDownload(HttpServletRequest request, HttpServletResponse response) throws Exception{

        try {
            request.setCharacterEncoding("UTF-8");
            response.setCharacterEncoding("UTF-8");
            String fileId = request.getParameter("fileId");
            SysReport sysReport = fileDownloadService.queryById(fileId);
            String nowDate = DateUtil.getSerialFullDate();

            Map<String, String> requestMap = getRequestParameterMapStr(request);

            String outFilePath = File.separator + "output" + File.separator + sysReport.getFileOutName().replace("{date}", nowDate);
            String path = WebUtils.getRealPath(request.getServletContext(), outFilePath);

            //根据模板导出数据模式
            if(sysReport.getType() == 1){
                //获取登录用户
                SessionUser su = getCurrentUser(request);
                if(fileId.equals("15")){
                    //碎屏险导出 只有管理员和碎屏险商有权限
                    if(su.getType() != SystemConstant.USER_TYPE_SYSTEM&&su.getType()!=SystemConstant.USER_TYPE_SCREEN){
                        throw new SystemException("对不起您没有权限操作！");
                    }
                }
                if(fileId.equals("17")){
                    //碎屏险免激活用户导出 只有管理员有权限
                    if(su.getType() != SystemConstant.USER_TYPE_SYSTEM){
                        throw new SystemException("对不起您没有权限操作！");
                    }
                }
                if(fileId.equals("21")){
                    //站点信息导出 只有管理员,客服,号卡站点人员有权限
                    if(su.getType() != SystemConstant.USER_TYPE_SYSTEM&&su.getType()!=SystemConstant.USER_TYPE_CUSTOMER_SERVICE&&su.getType()!=SystemConstant.STATION_USER){
                        throw new SystemException("对不起您没有权限操作！");
                    }
                }

                //获取上下文
                WebApplicationContext context = (WebApplicationContext) request
                        .getSession()
                        .getServletContext()
                        .getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);

                //获取service对象
                Object value = context.getBean(sysReport.getClassPath());

                //反射方法名
                Method process = value.getClass().getMethod(sysReport.getMethodName(), new Class[]{Map.class});

                String tempFilePath = File.separator + "templates" + File.separator + sysReport.getFileTempName();
                String tempPath = WebUtils.getRealPath(request.getServletContext(), tempFilePath);
                //拼接参数
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("tempFileName", tempPath);
                params.put("outFileName", path);
                params.put("response", response);
                params.put("user", su);
                params.putAll(requestMap);

                //执行service方法
                process.invoke(value, new Object[]{params});
            }

            String userAgent = request.getHeader("USER-AGENT").toLowerCase();
            try {
                response.reset();
            }catch (IllegalStateException e){

            }
            response.setContentType("application/x-msdownload");

            if (userAgent.indexOf("firefox") >= 0) {
                response.addHeader("Content-Disposition", "attachment; filename=\"" + new String(sysReport.getFileName().getBytes("UTF-8"), "ISO-8859-1")
                        + "\"");
            }
            else {
                response.addHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode(sysReport.getFileName(), "UTF-8") + "\"");
            }

            InputStream inputStream = new FileInputStream(path);

            OutputStream os = response.getOutputStream();
            byte[] b = new byte[2048];
            int length;
            while ((length = inputStream.read(b)) > 0) {
                os.write(b, 0, length);
            }

            os.close();
            inputStream.close();
        }
        catch (FileNotFoundException e) {
            logger.error("FileDownload_FileNotFoundException", e);
        }
        catch (IOException e) {
            logger.error("FileDownload_IOException", e);
        }
        return null;
    }
}
