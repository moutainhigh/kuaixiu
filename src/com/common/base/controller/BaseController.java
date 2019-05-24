package com.common.base.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;
import com.common.base.entity.CouponLock;
import com.common.exception.SystemException;
import com.common.paginate.Page;
import com.common.util.DateUtil;
import com.common.util.SmsSendUtil;
import com.kuaixiu.coupon.entity.Coupon;
import com.kuaixiu.coupon.service.CouponService;
import com.kuaixiu.sjUser.entity.SjSessionUser;
import com.system.api.CodeService;
import com.system.api.entity.Code;
import com.system.api.entity.ResultData;
import com.system.basic.user.entity.SessionUser;
import com.system.basic.user.service.LoginUserService;
import com.system.constant.ApiResultConstant;
import com.system.constant.SystemConstant;
import jodd.util.StringUtil;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Controller基类
 */
public class BaseController {

    @Autowired
    private CouponService couponService;
    @Autowired
    private LoginUserService loginUserService;

    @Autowired
    private CodeService codeService;

    private static final String DEFAULT_ENCODING = "UTF-8";

    private static final Logger log = Logger.getLogger(BaseController.class);
    /**
     * JSON_TYPE
     */
    public static final String JSON_TYPE = "application/json";
    /**
     * HTML_TYPE
     */
    public static final String HTML_TYPE = "text/html";
    /**
     * TEXT_TYPE
     */
    public static final String TEXT_TYPE = "text/plain";

    /**
     * 返回成功失败key
     */
    public static final String RESULTMAP_KEY_SUCCESS = "success";
    /**
     * 返回描述消息key
     */
    public static final String RESULTMAP_KEY_MSG = "msg";
    /**
     * 返回数据key
     */
    public static final String RESULTMAP_KEY_DATA = "data";

    /**
     * 成功
     */
    public static final Boolean RESULTMAP_SUCCESS_TRUE = true;
    /**
     * 失败
     */
    public static final Boolean RESULTMAP_SUCCESS_FALSE = false;
    /**
     * 出现异常错误字符串提示
     */
    public static final String EXCEPTION_ERROR_STR = "加载数据失败,请稍后重试";
    // 定义允许上传的文件扩展名
    private String Ext_Name = "jpg,jpeg,png";

    /**
     * 取得参数转成Long.
     *
     * @param request
     * @param paramName
     * @return
     */
    protected Long getParameterForLong(HttpServletRequest request, String paramName) {
        if (StringUtils.isEmpty(request.getParameter(paramName))) {
            return null;
        }
        return NumberUtils.createLong(request.getParameter(paramName));
    }

    /**
     * 取得参数转成Integer.
     *
     * @param request
     * @param paramName
     * @return
     */
    protected Integer getParameterForInteger(HttpServletRequest request, String paramName) {
        if (StringUtils.isEmpty(request.getParameter(paramName))) {
            return null;
        }
        return NumberUtils.createInteger(request.getParameter(paramName));
    }

    /**
     * 取得参数转成Float.
     *
     * @param request
     * @param paramName
     * @return
     */
    protected Float getParameterForFloat(HttpServletRequest request, String paramName) {
        if (StringUtils.isEmpty(request.getParameter(paramName))) {
            return null;
        }
        return NumberUtils.createFloat(request.getParameter(paramName));
    }

    /**
     * 取得参数转成BigDecimal.
     *
     * @param request
     * @param paramName
     * @return
     */
    protected BigDecimal getParameterForBigDecimal(HttpServletRequest request, String paramName) {
        String v = request.getParameter(paramName);
        if (StringUtils.isEmpty(v)) {
            return null;
        }
        return NumberUtils.createBigDecimal(v);
    }

    /**
     * 以Json格式输出
     *
     * @param response
     * @param result
     * @throws IOException
     */
    public void renderJson(HttpServletResponse response, Object result) throws IOException {
        renderJson(response, result, null);
    }


    /**
     * 以Json格式输出
     *
     * @param response
     * @param result
     * @throws IOException
     */
    public void renderJson(HttpServletResponse response, Object result, String callback) throws IOException {
        initContentType(response, JSON_TYPE);

        // 输入流
        PrintWriter out = response.getWriter();
        String outrs = JSON.toJSONString(result, mapping, SerializerFeature.WriteMapNullValue);
        if (StringUtils.isNotEmpty(callback)) {
            outrs = callback + "(" + outrs + ")";
        }

        out.print(outrs);
        out.flush();
    }

    /**
     * 输出html json串
     *
     * @param response
     * @param result
     * @throws IOException
     */
    public void renderJsonHtml(HttpServletResponse response, Object result) throws IOException {
        initContentType(response, HTML_TYPE);

        // 输入流
        PrintWriter out = response.getWriter();
        out.write(JSON.toJSONString(result, mapping, SerializerFeature.WriteMapNullValue));
        out.flush();
    }


    /**
     * 输出html json串
     *
     * @param response
     * @param result
     * @throws IOException
     */
    public void renderJsonWithOutNull(HttpServletResponse response, Object result) throws IOException {
        initContentType(response, HTML_TYPE);

        // 输入流
        PrintWriter out = response.getWriter();
        out.write(JSON.toJSONString(result, mapping));
        out.flush();
    }

    private static final SerializeConfig mapping = new SerializeConfig();
    private static String dateFormat = "yyyy-MM-dd HH:mm:ss";

    static {
        mapping.put(Date.class, new SimpleDateFormatSerializer(dateFormat));
    }

    /**
     * 以文本格式输出
     *
     * @param response
     * @param result
     * @throws IOException
     */
    public void renderText(HttpServletResponse response, String result) throws IOException {
        initContentType(response, TEXT_TYPE);

        // 输入流
        PrintWriter out = response.getWriter();
        out.write(result);
        out.flush();
    }

    /**
     * 初始HTTP内容类型.
     *
     * @param response
     * @param contentType
     */
    private void initContentType(HttpServletResponse response, String contentType) {
        response.setContentType(contentType + ";charset=" + DEFAULT_ENCODING);
    }


    /**
     * 获取当前用户UserSession对象
     *
     * @param request
     * @return 当前用户对象
     */
    protected SessionUser getCurrentUser(HttpServletRequest request) {
        return (SessionUser) request.getSession().getAttribute(SystemConstant.SESSION_USER_KEY);
    }

    protected SjSessionUser getSjCurrentUser(HttpServletRequest request) {
        return (SjSessionUser) request.getSession().getAttribute(SystemConstant.SESSION_SJ_USER_KEY);
    }

    /**
     * 验证用户当前access_token
     */
    protected void getLoginUser(HttpServletRequest request) {
        String token = request.getParameter("access_token");
        log.info("当前用户token：" + token + "  sessionToken：" + request.getSession().getAttribute("accessToken"));
        if (token == null) {
            throw new SystemException(ApiResultConstant.resultCode_str_2003, ApiResultConstant.resultCode_2003);
        } else if (!token.equals((String) request.getSession().getAttribute("accessToken"))) {
            throw new SystemException(ApiResultConstant.resultCode_str_2005, ApiResultConstant.resultCode_2005);
        }
    }

    /**
     * 通过原始数据流获取浏览器的请求数据
     */
    protected String getRequestPayload(HttpServletRequest req) {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = req.getReader();
            char[] buff = new char[1024];
            int len;
            while ((len = reader.read(buff)) != -1) {
                sb.append(buff, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /**
     * 获取当前请求的params参数
     */
    protected JSONObject getPrarms(HttpServletRequest request) {
        String param = request.getParameter("params");
        log.info("request：" + param);
        if (param == null) {
            throw new SystemException(ApiResultConstant.resultCode_str_1001, ApiResultConstant.resultCode_1001);
        }
        JSONObject params = JSONObject.parseObject(param);
        params = filter(params);
        return params;
    }

    public JSONObject filter(JSONObject params) {
        for (String key : params.keySet()) {
            Object object = params.get(key);
            String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(object.toString());
            object=m.replaceAll("").trim();
            params.put(key,object);
        }
        return params;
    }

    /**
     * 判断是否非空字符串  为空返回SystemException
     *
     * @param param
     * @return
     */
    protected String getUseString(String param) {
        if (StringUtils.isBlank(param)) {
            throw new SystemException(param + "不能为空");
        }
        return param;
    }


    /**
     * 获取请求路径
     *
     * @param request
     * @return
     * @author: lijx
     * @CreateDate: 2016-10-15 下午5:46:06
     */
    protected String getRequestUrl(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + request.getRequestURI() + ";jsessionid=" + request.getSession().getId() + "?" + request.getQueryString();
    }

    /**
     * 获取协议 域名（主机+端口）  项目名称 路径
     * http://localhost:8080/kuaixiu   或
     * http://m-super.com
     */
    protected String getProjectUrl(HttpServletRequest request) {
        StringBuffer url = request.getRequestURL();
        String path = request.getServletPath();
        int a = url.indexOf(path);
        String linkUrl = url.substring(0, a);
        return linkUrl;
    }


    /**
     * 得到tomcat路径  如E:\tomcat\apache-tomcat-8.0.46
     *
     * @param path
     * @return
     */
    protected String serverPath(String path) {
        int a = path.lastIndexOf("/");
        int b = path.lastIndexOf("\\");
        String realPath = "";
        if (a > 0) {
            String tip = path.substring(0, a);
            String tips = tip.substring(0, tip.lastIndexOf("/"));
            realPath = tips.substring(0, tips.lastIndexOf("/"));
        } else if (b > 0) {
            String tip = path.substring(0, b);
            String tips = tip.substring(0, tip.lastIndexOf("\\"));
            realPath = tips.substring(0, tips.lastIndexOf("\\"));
        }
        return realPath;
    }


    /**
     * 保存文件
     */
    protected String getPath(HttpServletRequest request, String file, String URLPath) {
        String fileName = "";                   //上传的文件名
        String path = "";                       //存储路径
        try {
            //转化request
            MultipartHttpServletRequest rm = (MultipartHttpServletRequest) request;
            MultipartFile mfile = rm.getFile(file);                             //获得前端页面传来的文件
            byte[] bfile = mfile.getBytes();                                    //获得文件的字节数组
            if (bfile.length == 0) {
                log.info("未上传图片");
                return "";
            }
            fileName = mfile.getOriginalFilename();                             //获得文件名
            // 处理获取到的上传文件的文件名的路径部分，只保留文件名部分
            fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);

            // 得到上传文件的扩展名
            String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            // 检查扩展名
            if (!Ext_Name.contains(fileExt)) {
                throw new SystemException("上传文件扩展名是不允许的扩展名：" + fileExt);
            } else {
                //保存文件
                path = saveFile(bfile, fileName, request, URLPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }


    /**
     * 保存图片文件到设定的文件夹  并得到保存文件的路径  此处指tomcat目录下和webapp同级的images文件夹
     * 返回字符串为网络访问url
     */
    protected String saveFile(byte[] bfile, String fileName, HttpServletRequest request, String upload) {
        //定义文件输出流   保存图片等文件时应在tomcat server.xml配置文件中配置对应的静态路径
        //这样才能访问项目外的资源  此处我们默认在apache目录下建一个images的文件夹用来存放图片
        String path = "/" + SystemConstant.IMAGE_PATH;
        if (StringUtils.isBlank(upload)) {
            String savePath = request.getServletContext().getRealPath("");
            String serverPath = serverPath(savePath);              //得到服务器位置
            upload = serverPath + path;                       //保存图片的位置
        }
        File saveFileDir = new File(upload);
        if (!saveFileDir.exists()) {
            // 创建目录
            saveFileDir.mkdirs();
        }
        OutputStream out = null;
        try {
            out = new FileOutputStream(new File(upload, fileName));
            path = path + "/" + fileName;
            out.write(bfile);
            out.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        path = getProjectUrl(request) + path;
        return path;

    }

    /**
     * 判断文件大小
     *
     * @param len  文件长度
     * @param size 限制大小
     * @param unit 限制单位（B,K,M,G）
     * @return
     */
    protected boolean checkFileSize(Long len, int size, String unit) {
        double fileSize = 0;
        if ("B".equals(unit.toUpperCase())) {
            fileSize = (double) len;
        } else if ("K".equals(unit.toUpperCase())) {
            fileSize = (double) len / 1024;
        } else if ("M".equals(unit.toUpperCase())) {
            fileSize = (double) len / 1048576;
        } else if ("G".equals(unit.toUpperCase())) {
            fileSize = (double) len / 1073741824;
        }
        if (fileSize > size) {
            return false;
        }
        return true;
    }

    /**
     * @param arr 原图片2进制流
     * @return
     */
    public byte[] imageCompress(byte[] arr, MultipartFile mfile) {
        byte[] byteArray = null;

        String fileName = mfile.getOriginalFilename();                             //获得文件名
        // 处理获取到的上传文件的文件名的路径部分，只保留文件名部分
        fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
        // 得到上传文件的扩展名
        String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

        ByteArrayInputStream intputStream = new ByteArrayInputStream(arr);
        Thumbnails.Builder<? extends InputStream> builder = Thumbnails.of(intputStream).scale(0.9f);//0.3f图片质量压缩比例，0.1~1之间，越小图片质量越差
        try {
            BufferedImage bufferedImage = builder.asBufferedImage();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, fileExt, baos);
            byteArray = baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return byteArray;

    }

    /**
     * 获取验证码并保存到session
     *
     * @param request
     * @return
     * @CreateDate: 2016-9-13 下午8:24:41
     */
    protected String getRandomCode(HttpServletRequest request) {
        String randomCode = SmsSendUtil.randomCode();
        request.getSession().setAttribute(SystemConstant.SESSION_RANDOM_CODE, randomCode);
        return randomCode;
    }


    /**
     * 获取验证码并保存到session
     *
     * @param request
     * @return
     * @CreateDate: 2016-9-13 下午8:24:41
     */
    protected String getRandomCode(HttpServletRequest request, String key) {
        String randomCode = SmsSendUtil.randomCode();
        request.getSession().setAttribute(SystemConstant.SESSION_RANDOM_CODE + key, randomCode);
        //保存验证码到数据库
        Code code = codeService.queryById(key);
        if (code == null) {
            code = new Code();
            code.setCode(randomCode);
            code.setMobile(key);
            codeService.add(code);
        } else {
            code.setCode(randomCode);
            code.setUpdateTime(new Date());
            codeService.saveUpdate(code);
        }
        return randomCode;
    }

//    /**
//     * 获取验证码并保存到session
//     * @param request
//     * @return
//     * @CreateDate: 2016-9-13 下午8:24:41
//     */
//    protected boolean checkRandomCode(HttpServletRequest request, String key, String checkCode){
//        String randomCode = (String)request.getSession().getAttribute(SystemConstant.SESSION_RANDOM_CODE + key);
//        //System.out.println("session中的验证码："+randomCode);
//        if(StringUtils.isBlank(randomCode)){
//            return false;
//        }
//        else if(randomCode.equals(checkCode)){
//            return true;
//        }
//        return false;
//    }


    /**
     * 获取验证码
     *
     * @param request
     * @return
     * @CreateDate: 2016-9-13 下午8:24:41
     */
    protected boolean checkRandomCode(HttpServletRequest request, String key, String checkCode) {
        //万能验证码
        if ("140789".equals(checkCode)) {
            return true;
        }
        Code code = codeService.queryById(key);
        String randomCode = code.getCode();
        if (StringUtils.isBlank(randomCode)) {
            return false;
        } else if (randomCode.equals(checkCode)) {
            return true;
        }
        return false;
    }

    /**
     * 移除验证码
     *
     * @param request
     * @param key
     * @CreateDate: 2016-9-16 下午11:51:21
     */
    public void removeRandomCode(HttpServletRequest request, String key) {
        request.getSession().setAttribute(SystemConstant.SESSION_RANDOM_CODE + key, null);
    }

    /**
     * 获取所有参数
     *
     * @param request
     * @return
     */
    public Map<String, String> getRequestParameterMapStr(HttpServletRequest request) {

        Map<String, String[]> parameters = request.getParameterMap();
        Map<String, String> postMap = new HashMap<String, String>();
        if (parameters != null && parameters.size() > 0) {
            Iterator<String> iter = parameters.keySet().iterator();
            while (iter.hasNext()) {
                String key = iter.next();
                String value = request.getParameter(key);
                try {
                    value = java.net.URLDecoder.decode(value.trim(), "utf-8");
                } catch (Exception e) {

                }

                postMap.put(key, value);
            }
        }
        return postMap;
    }

    /**
     * Java模拟POST表单提交HttpClient操作.
     *
     * @param url    提交的url
     * @param params 填入表单域的值
     * @return 返回response的值
     * @throws SystemException
     */
    protected String sendPost(String url, Map<String, String> params) throws SystemException {

        // 构造HttpClient的实例
        HttpClient httpClient = new HttpClient();
        // 创建POST方法的实例
        PostMethod postMethod = new DefaultPostMethod(url);

        // 填入各个表单域的值
        if (params != null && !params.isEmpty()) {
            NameValuePair[] data = new NameValuePair[params.size()];
            int i = 0;
            for (String key : params.keySet()) {
                data[i++] = new NameValuePair(key, params.get(key));
            }

            // 将表单的值放入postMethod中
            postMethod.setRequestBody(data);
        }

        try {
            // 执行postMethod
            httpClient.executeMethod(postMethod);

            //读取内容
            String responseBody = postMethod.getResponseBodyAsString();

            return StringUtils.isEmpty(responseBody) ? "" : responseBody;
        } catch (HttpException e) {
            e.printStackTrace();
            throw new SystemException("发生致命的异常，可能是协议不对或者返回的内容有问题");
        } catch (IOException e) {
            e.printStackTrace();
            throw new SystemException("发生网络异常");
        } finally {
            //释放连接
            postMethod.releaseConnection();
        }
    }


    /**
     * 设置HttpClient POST的中文编码
     *
     * @作者 bulang
     * @创建日期 2012-8-28下午08:00:22
     * @版本 V 1.0
     */
    private static class DefaultPostMethod extends PostMethod {
        DefaultPostMethod(String url) {
            super(url);
        }

        @Override
        public String getRequestCharSet() {
            return DEFAULT_ENCODING;
        }
    }

    /**
     * @param request
     * @return
     * @throws Exception
     */
    public Page getPageByRequest(HttpServletRequest request) throws Exception {
        //记录起始索引
        String start = request.getParameter("start");
        String pageStatus = request.getParameter("pageStatus");//用来判定是否搜索查询
        if (StringUtils.isBlank(start)) {
            start = "0";
        }
        if (StringUtil.isNotBlank(pageStatus)) {
            if (pageStatus.equals("1")) {
                start = "0";
            }
        }
        //数据长度，每页记录数
        String pageSize = request.getParameter("length");

        Page dt = new Page();
        dt.setStart(Integer.valueOf(start));
        if (StringUtils.isBlank(pageSize)) {
            dt.setPageSize(SystemConstant.DEFAULT_PAGE_SIZE);
        } else {
            dt.setPageSize(Integer.valueOf(pageSize));
        }

        return dt;
    }

    /**
     * Json接受数据模式 分页
     */
    public Page getPageByRequestParams(String start, String pageSize) throws Exception {
        //记录起始索引
        if (StringUtils.isBlank(start) || start.equals("0")) {
            start = "1";
        }
        //数据长度，每页记录数
        Page dt = new Page();
        dt.setStart(Integer.valueOf(start) - 1);
        if (StringUtils.isBlank(pageSize)) {
            dt.setPageSize(SystemConstant.DEFAULT_PAGE_SIZE);
        } else {
            dt.setPageSize(Integer.valueOf(pageSize));
        }

        return dt;
    }

    /**
     * 获取用户真实IP地址，不使用request.getRemoteAddr();的原因是有可能用户使用了代理软件方式避免真实IP地址,
     * 可是，如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP值，究竟哪个才是真正的用户端的真实IP呢？
     * 答案是取X-Forwarded-For中第一个非unknown的有效IP字符串。
     * 如：X-Forwarded-For：192.168.1.110, 192.168.1.120, 192.168.1.130,
     * 192.168.1.100
     * 用户真实IP为： 192.168.1.110
     *
     * @param request
     * @return
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        //log.info("x-forwarded-for:"+ ip);
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        //log.info("Proxy-Client-IP:"+ ip);
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        //log.info("WL-Proxy-Client-IP:"+ ip);
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        //log.info("HTTP_CLIENT_IP:"+ ip);
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        //log.info("HTTP_X_FORWARDED_FOR:"+ ip);
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        //log.info("getRemoteAddr:"+ ip);
        //log.info("getRemoteAddr():"+ request.getRemoteAddr());
        if (ip != null || ip.indexOf(", ") > 0) {
            String[] ips = ip.split(", ");
            for (String i : ips) {
                if (i != null && i.length() > 0 && !"unknown".equalsIgnoreCase(i)) {
                    ip = i;
                    break;
                }
            }
        }
        //log.info("ip:"+ ip);
        return ip;
    }

    /**
     * 检查优惠码是否存在
     * 1、获取手机号码
     * 2、获取验证吗
     * 3、校验手机验证码
     * 4、校验当天优惠码输入错误次数
     * 5、如果没有超过限制则继续验证优惠码
     * 6、如果已经超过错误限制判断是否超过锁定时间
     * 7、如果已经超过锁定时间则清空锁定状态继续验证优惠码
     * 8、获取优惠码
     * 9、校验优惠码是否存在
     * 10、如果存在则返回优惠码，并清空错误次数
     * 11、如果不存在错误次数加一，累计错误次数加一
     * 12、如果错误次数是否大于限制次数，连续输错5次锁定半个小时，连续输错10次锁定一天
     * 13、如果是则判断历史错误次数，根据历史错误次数锁定优惠码查询时间，累计错误20次锁定一天
     *
     * @param request
     * @return
     * @throws ParseException
     */
    public Coupon checkCouponExisit(HttpServletRequest request) throws ParseException {
        //获取手机号
        String mobile = request.getParameter("mobile");
        //获取手机号
        String checkCode = request.getParameter("checkCode");
        //验证手机号和验证码
        if (!checkRandomCode(request, mobile, checkCode)) {
            throw new SystemException("手机号或验证码输入错误");
        }
        //获取上下文
        ServletContext sc = request.getSession().getServletContext();
        Object obj = sc.getAttribute(mobile + "_coupon");
        CouponLock cl = null;
        if (obj == null) {
            cl = new CouponLock();
            cl.setMobile(mobile);
            sc.setAttribute(mobile + "_coupon", cl);
        } else {
            cl = (CouponLock) obj;
        }
        //获取当前日期
        String nowDay = DateUtil.getNowyyyyMMdd();
        String nowTime = DateUtil.getDateyyyyMMddHHmmss(new Date());
        //判断日期如果不是当天清空错误数据
        if (!nowDay.equals(cl.getNowDay())) {
            cl.setNowDay(nowDay);
            cl.setErrCount(0);
            cl.setHisErrCount(0);
            cl.setLock(false);
            cl.setBeginLockTime(null);
            cl.setEndLockTime(null);
        }
        //是否锁定
        if (cl.isLock()) {
            if (nowTime.compareTo(cl.getEndLockTime()) < 0) {
                throw new SystemException("对不起亲，因为您输入优惠码错误次数过多，已被锁定，请稍后再试");
            } else {
                cl.setLock(false);
                cl.setBeginLockTime(null);
                cl.setEndLockTime(null);
            }
        }
        //校验当天优惠码输入错误次数
        //获取优惠码
        String couponCode = request.getParameter("couponCode");
        if (StringUtils.isBlank(couponCode)) {
            throw new SystemException("请填写优惠码");
        }
        Coupon c = couponService.queryByCode(couponCode);
        if (c == null) {
            cl.setErrCount(cl.getErrCount() + 1);
            cl.setHisErrCount(cl.getHisErrCount() + 1);
            if (cl.getErrCount() == 5) {
                //连续输错五次锁定半小时
                cl.setLock(true);
                cl.setBeginLockTime(nowTime);
                cl.setEndLockTime(DateUtil.getDateyyyyMMddHHmmss(DateUtil.getDateAddMinute(30)));
                throw new SystemException("亲，您输入优惠码错误次数过多，已被锁定，请稍后再试");
            } else if (cl.getErrCount() >= 10 || cl.getHisErrCount() > 20) {
                //锁定一天
                cl.setLock(true);
                cl.setBeginLockTime(nowTime);
                cl.setEndLockTime(DateUtil.getDateyyyyMMddHHmmss(DateUtil.getDateAddHour(24)));
                throw new SystemException("亲，您输入优惠码错误次数过多，已被锁定，请稍后再试");
            }
            throw new SystemException("优惠码不存在");
        } else if (c.getIsUse() == 1) {
            throw new SystemException("优惠码已使用");
        } else if (c.getIsDel() == 1) {
            throw new SystemException("优惠码已作废");
        } else {
            //清空错误次数
            cl.setErrCount(0);
        }
        return c;
    }


    /**
     * 用户端检测优惠码
     */
    public Coupon userCheckCouponExisit(HttpServletRequest request, JSONObject params) throws ParseException {
        String mobile = params.getString("mobile");
        String checkCode = params.getString("checkCode");
        String couponCode = params.getString("couponCode");
        if (!checkRandomCode(request, mobile, checkCode)) {
            throw new SystemException("手机号或验证码输入错误");
        }
        //获取上下文
        ServletContext sc = request.getSession().getServletContext();
        Object obj = sc.getAttribute(mobile + "_coupon");
        CouponLock cl = null;
        if (obj == null) {
            cl = new CouponLock();
            cl.setMobile(mobile);
            sc.setAttribute(mobile + "_coupon", cl);
        } else {
            cl = (CouponLock) obj;
        }
        //获取当前日期
        String nowDay = DateUtil.getNowyyyyMMdd();
        String nowTime = DateUtil.getDateyyyyMMddHHmmss(new Date());
        //判断日期如果不是当天清空错误数据
        if (!nowDay.equals(cl.getNowDay())) {
            cl.setNowDay(nowDay);
            cl.setErrCount(0);
            cl.setHisErrCount(0);
            cl.setLock(false);
            cl.setBeginLockTime(null);
            cl.setEndLockTime(null);
        }
        //是否锁定
        if (cl.isLock()) {
            if (nowTime.compareTo(cl.getEndLockTime()) < 0) {
                throw new SystemException("对不起亲，因为您输入优惠码错误次数过多，已被锁定，请稍后再试");
            } else {
                cl.setLock(false);
                cl.setBeginLockTime(null);
                cl.setEndLockTime(null);
            }
        }
        //校验当天优惠码输入错误次数
        //获取优惠码
        if (StringUtils.isBlank(couponCode)) {
            throw new SystemException("请填写优惠码");
        }
        Coupon c = couponService.queryByCode(couponCode);
        if (c == null) {
            cl.setErrCount(cl.getErrCount() + 1);
            cl.setHisErrCount(cl.getHisErrCount() + 1);
            if (cl.getErrCount() == 5) {
                //连续输错五次锁定半小时
                cl.setLock(true);
                cl.setBeginLockTime(nowTime);
                cl.setEndLockTime(DateUtil.getDateyyyyMMddHHmmss(DateUtil.getDateAddMinute(30)));
                throw new SystemException("亲，您输入优惠码错误次数过多，已被锁定，请稍后再试");
            } else if (cl.getErrCount() >= 10 || cl.getHisErrCount() > 20) {
                //锁定一天
                cl.setLock(true);
                cl.setBeginLockTime(nowTime);
                cl.setEndLockTime(DateUtil.getDateyyyyMMddHHmmss(DateUtil.getDateAddHour(24)));
                throw new SystemException("亲，您输入优惠码错误次数过多，已被锁定，请稍后再试");
            }
            throw new SystemException("优惠码不存在");
        } else if (c.getIsUse() == 1) {
            throw new SystemException("优惠码已使用");
        } else if (c.getIsDel() == 1) {
            throw new SystemException("优惠码已作废");
        } else {
            //清空错误次数
            cl.setErrCount(0);
        }
        return c;
    }


    /**
     * 验证参数是否为空
     */
    protected String get(String code) {
        if (StringUtils.isBlank(code)) {
            throw new SystemException("参数不完整！");
        }
        return code;
    }


    public ResultData getResult(ResultData result, Object object, Boolean isTrue, String code, String message) {
        result.setResultMessage(message);
        result.setResultCode(code);
        result.setSuccess(isTrue);
        result.setResult(object);
        return result;
    }

    public ResultData getSjResult(ResultData result, Object object, Boolean isTrue, String code, String msg, String message) {
        result.setMsg(msg);
        result.setResultMessage(message);
        result.setResultCode(code);
        result.setSuccess(isTrue);
        result.setResult(object);
        return result;
    }
}
