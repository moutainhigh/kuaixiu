package com.common.util;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


/**
 * Http请求工具类.
 * 
 * @CreateDate: 2016-9-4 下午8:21:59
 * @version: V 1.0
 */
public class HttpClientUtil{
    
    /** 
     * 最大连接数 
     */  
    public static final int MAX_TOTAL_CONNECTIONS = 800;  
    /** 
     * 获取连接的最大等待时间 
     */  
    public static final int WAIT_TIMEOUT = 90000;  
    /** 
     * 每个路由最大连接数 
     */  
    public static final int MAX_ROUTE_CONNECTIONS = 400;  
    /** 
     * 连接超时时间 
     */  
    public static final int CONNECT_TIMEOUT = 90000;  
    /** 
     * 读取超时时间 
     */  
    public static final int READ_TIMEOUT = 90000;  

    private static final int HTTPS_PORT = 443;
    private static final int HTTP_PORT = 80;

    private static String charset = "UTF-8";

    /**
     * 
     * @param args
     * @CreateDate: 2016-9-4 下午9:35:27
     */
    public static void main(String[] args){
    	String c=".无拆无修\n手机后盖螺丝没拆开过，并且没有进行过拆机或维修\n2.有拆修	\n有拆修就是拆过机，维修或拆开过主板，或多处部件维修过\n所谓拆过机，就是拧开过螺丝；原厂标或背板标签变形；更换过小部件等。例如：\n（1）. 内部3个或3个以上螺丝变形或拧痕\n（2）. 1个或1个以上螺丝缺失\n（3）. 电池被更换或松动有撬痕\n（4）. 维修过尾插或扬声器\n（5）. 组装后壳缺少导电纸\n（6）. 原厂标缺失或破损\n（7）. 机身内部贴有店标\n（8）. 内部（非主板处）有盖章\n";
        System.out.println(c.replace("\n", ""));
    }
    
    /**
     * 发送Get请求
     * @param url
     * @return
     * @CreateDate: 2016-9-4 下午8:59:20
     */
    public static String httpGet(String url){
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String response = "";
        HttpGet httpGet = null;  
        try {
            // 创建httppost    
            httpGet = new HttpGet(url); 
            
            System.out.println("executing request " + httpGet.getURI());
            CloseableHttpResponse httpResponse = httpclient.execute(httpGet);
            try {
                HttpEntity entity = httpResponse.getEntity();
                if (entity != null) {
                    System.out.println("--------------------------------------"); 
                    response = EntityUtils.toString(entity, charset);
                    System.out.println("Response content: " + response);  
                    System.out.println("--------------------------------------");  
                }
            } 
            finally {
                httpResponse.close();
            }
        } 
        catch (ClientProtocolException e) {
            e.printStackTrace();
        } 
        catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } 
        catch (IOException e) {
            e.printStackTrace();
        } 
        finally {
            // 关闭连接,释放资源 
            if (httpGet != null) {
                try{
                    httpGet.abort();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
            if (httpclient != null) {
                try {
                    httpclient.close();
                } 
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response;
    }
    
    /**
     * 发送Post请求
     * @param url
     * @param params
     * @return
     * @CreateDate: 2016-9-4 下午8:59:20
     */
    public static String sendPost(String url, Map<String,String> params){
        String response = "";
        HttpPost httpPost = null;  
        try {
            // 创建httppost    
            httpPost = new HttpPost(url); 
            // 设置参数
            List<NameValuePair> list = getParamsList(params);
            if (list != null && list.size() > 0) {
                httpPost.setEntity(new UrlEncodedFormEntity(list, charset));
            }
            System.out.println("executing request " + httpPost.getURI());
            response = sendPost(httpPost);
        } 
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            // 关闭连接,释放资源 
            if (httpPost != null) {
                try{
                    httpPost.abort();
                }
                catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        }
        return response;
    }
    
    
    /**
     * 发送Post请求
     * @param url
     * @return
     * @CreateDate: 2016-9-4 下午8:59:20
     */
    public static String sendPostContent(String url, String content){
        // 创建httppost    
        HttpPost httpPost = new HttpPost(url); 
        // 设置参数
        StringEntity strEntity = new StringEntity(content, Consts.UTF_8);
        httpPost.setEntity(strEntity);
        return sendPost(httpPost);
    }
    
    /**
     * 发送Post请求
     * @return
     * @CreateDate: 2016-9-4 下午8:59:20
     */
    public static String sendPost(HttpPost httpPost){
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String response = "";
        try {
            System.out.println("executing request " + httpPost.getURI());
            RequestConfig config = RequestConfig.custom()
                    .setConnectTimeout(CONNECT_TIMEOUT)
                    .setConnectionRequestTimeout(WAIT_TIMEOUT)
                    .setSocketTimeout(READ_TIMEOUT).build();
            httpPost.setConfig(config);
            CloseableHttpResponse httpResponse = httpclient.execute(httpPost);
            try {
                HttpEntity entity = httpResponse.getEntity();
                if (entity != null) {
                    System.out.println("--------------------------------------"); 
                    response = EntityUtils.toString(entity, charset);
                    System.out.println("Response content: " + response);  
                    System.out.println("--------------------------------------");  
                }
            } 
            finally {
                httpResponse.close();
            }
        } 
        catch (ClientProtocolException e) {
            e.printStackTrace();
        } 
        catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } 
        catch (IOException e) {
            e.printStackTrace();
        } 
        finally {
            // 关闭连接,释放资源 
            if (httpPost != null) {
                try{
                    httpPost.abort();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
            if (httpclient != null) {
                try {
                    httpclient.close();
                } 
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response;
    }
    
    /**
     * 将包含参数的MAP转换为提交的参数类型
     * 
     * @param paramsMap
     * @return
     */
    public static List<NameValuePair> getParamsList(Map<String,String> paramsMap) {
        if (paramsMap == null || paramsMap.size() == 0) {
            return null;
        }
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        Set<Entry<String, String>> sets = paramsMap.entrySet();
        for (Entry<String, String> entry : sets) {
            String valueString = entry.getValue() == null ? "" : entry.getValue().toString();
            list.add(new BasicNameValuePair(entry.getKey(), valueString));
        }
        return list;
    }




    /**
     *  发送post请求   application/json
     * @param url
     * @param param
     * @return
     */
    public static String sendJsonPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*application/json*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("发送 POST 请求出现异常"+e.getMessage());
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }


    /**
     *
     * @param postUrl  请求地址
     * @param soapXML  请求数据
     */
    public static String webService(String postUrl,String soapXML){
        String result="";
        InputStream is=null;
        InputStreamReader isr=null;
        BufferedReader br=null;
        OutputStream os=null;
        try {
        //第一步：创建服务地址，不是WSDL地址
        URL url = new URL(postUrl);
        //第二步：打开一个通向服务地址的连接
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        //第三步：设置参数
        //3.1发送方式设置：POST必须大写
        connection.setRequestMethod("POST");
        //3.2设置数据格式：content-type
        connection.setRequestProperty("content-type", "text/xml;charset=utf-8");
        //3.3设置输入输出，因为默认新创建的connection没有读写权限，
        connection.setDoInput(true);
        connection.setDoOutput(true);
        //第四步：组织SOAP数据，发送请求
        os = connection.getOutputStream();

            os.write(soapXML.getBytes());
            //第五步：接收服务端响应，打印
            int responseCode = connection.getResponseCode();
            System.out.println("返回编码："+responseCode);
            if(200 == responseCode){//表示服务端响应成功
                is = connection.getInputStream();
                isr = new InputStreamReader(is);
                br = new BufferedReader(isr);

                StringBuilder sb = new StringBuilder();
                String temp = null;
                while(null != (temp = br.readLine())){
                    sb.append(temp);
                }
                result=sb.toString();
                System.out.println("返回参数："+result);


            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(isr!=null){
                try {
                    isr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(br!=null){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(os!=null){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return result;

    }
    
}
