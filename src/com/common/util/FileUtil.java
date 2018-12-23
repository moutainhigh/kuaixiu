package com.common.util;

import com.system.constant.SystemConstant;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/**
 * 文件操作工具类
 * 
 * @作者 wangym
 * @创建日期 Aug 18, 2012 10:06:10 AM
 * @版本 V 1.0
 */
public class FileUtil {

    /**
     * 输入缓存大小
     */
    public static final int BUFFERSIZE = 1024;
    
    public static void createDir(String dir, boolean ignoreIfExitst)
            throws IOException{
        File file = new File(dir);

        if(ignoreIfExitst && file.exists()){
            return;
        }

        if(file.mkdir() == false){
            throw new IOException("不能读取该目录 = " + dir);
        }
    }

    public static void createDirs(String dir, boolean ignoreIfExitst)
        throws IOException{
        File file = new File(dir);

        if(ignoreIfExitst && file.exists()){
            return;
        }

        if(file.mkdirs() == false){
            throw new IOException("不能读取该目录 = " + dir);
        }
    }

    public static boolean creatFile(String filePath, byte[] content){
        File file = new File(filePath);
        
        if(file.isDirectory()){
            return false;
        }
        else if(file.isFile()){
            return false;
        }
        else{
            try{
                FileOutputStream fos = new FileOutputStream(filePath);
                fos.write(content);
                fos.close();
            }
            catch(Exception e){
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
    
    
    /**
     * 新建一个文件,如果存在可以覆盖
     * @param filePath
     * @param content
     * @param overIfExitst
     * @return
     */
    public static boolean creatFile(String filePath, byte[] content, boolean overIfExitst){
        File file = new File(filePath);
        
        if(file.isDirectory()){
            return false;
        }
        else if(file.isFile()){
            if(overIfExitst){
                if(!file.delete()){
                    return false;
                }
            }
            else{
                return false;
            }
        }

        try{
            FileOutputStream fos = new FileOutputStream(filePath);
            fos.write(content);
            fos.close();
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
        
        return true;
    }
        
    
    /**
     * 文件删除
     * 
     * @date Aug 18, 2012 10:16:13 AM
     * @param path
     */
    public static void deleteFile(String path) {
        // path不为空
        if (StringUtils.isNotBlank(path)) {
            path = SystemConstant.WEB_REAL_PATH + path;
            File file = new File(path);
            if (file.isFile() && file.exists()) {  
                file.delete();
            }  
        }
    }

    /**
     * 根据输入流获取输出缓存流
     * @param is 输入流
     * @return 输出缓存流
     * @throws IOException
     */
    public static ByteArrayOutputStream getBaosByInputStream(InputStream is)
            throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[BUFFERSIZE];
        int len;
        while ((len = is.read(buffer)) > -1) {
            baos.write(buffer, 0, len);
        }
        baos.flush();
        is.close();
        return baos;
    }
    
    /**
     * 读取文件内容到String
     * 文件默认以UTF-8的编码进行读取 
     * @param file
     * @return
     * @throws IOException
     * @author: lijx
     * @CreateDate: 2016-8-10 下午6:17:16
     * @UpdateDate: 2016-09-17 22:02 
     */
    public static String readFileAsString(File file) throws IOException {  
        return readFileAsString(file, "UTF-8");  
    }  
  
    /**
     * 读取文件内容到String
     * @param file 文件
     * @param charsetName 编码
     * @return
     * @throws IOException
     * @author: lijx
     * @CreateDate: 2016-8-10 下午6:16:27
     */
    public static String readFileAsString(File file, String charsetName) throws IOException {  
        if (!file.exists() || !file.isFile()) {  
            throw new IOException("File to be readed not exist, file path : " + file.getAbsolutePath());  
        }  
  
        FileInputStream fileIn = null;  
        InputStreamReader inReader = null;  
        BufferedReader bReader = null;  
        try {  
            fileIn = new FileInputStream(file);  
            inReader = charsetName == null ? new InputStreamReader(fileIn) : new InputStreamReader(fileIn, charsetName);  
            bReader = new BufferedReader(inReader);  
            StringBuffer content = new StringBuffer();  
            char[] chBuffer = new char[1024];  
            int readedNum = -1;  
            while ((readedNum = bReader.read(chBuffer)) != -1) {  
                content.append(chBuffer, 0, readedNum);  
            }  
  
            return content.toString();  
        } finally {  
            if (fileIn != null) {  
                try {  
                    fileIn.close();  
                } catch (IOException e) {  
                }  
            }  
  
            if (bReader != null) {  
                try {  
                    bReader.close();  
                } catch (IOException e) {  
                }  
            }  
        }  
    }
    
    /**
     * 将输入流写入到指定文件
     * @param is
     * @author: lijx
     * @CreateDate: 2016-10-22 上午1:15:19
     */
    public static void writeFromInputStream(InputStream is, String outFileName){
        File file = new File(getPhysicalPath() + outFileName);
        FileOutputStream out = null; 
        try {
            out = new FileOutputStream(file);
            byte[] buffer = new byte[BUFFERSIZE];
            int len;
            while ((len = is.read(buffer)) > -1) {
                out.write(buffer, 0, len);
            }
            out.close();
            is.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally{
            try {     
                is.close();       
                out.close();     
            } 
            catch (Exception e) {     
                e.printStackTrace();     
            }
        }
    }
    
    /**
     * 获取项目路径
     * @return
     * @author: lijx
     * @CreateDate: 2016-10-22 上午1:39:15
     */
    public static String getPhysicalPath(){
        String filePath=new FileUtil().getClass().getResource("/").getPath();
        if(filePath.indexOf("/")==0){           
            filePath=filePath.substring(1, filePath.lastIndexOf("WEB-INF"));
            //非windows
            if(filePath.indexOf(":")==-1){
                filePath="/"+filePath;
            }
        }
        else {
            filePath=filePath.substring(0, filePath.lastIndexOf("WEB-INF"));
        }
        return filePath;
    }

    /**
     * 下载网络资源到指定本地目录
     * @param urlString   路径
     * @param filename    保存的文件名
     * @param savePath    保存的位置
     * @throws Exception
     */
    public static void download(String urlString, String filename,String savePath){
        InputStream is=null;
        OutputStream os = null;
        try {
            // 构造URL
            URL url = new URL(urlString);
            // 打开连接
            URLConnection  con = url.openConnection();
            //设置请求超时为5s
            con.setConnectTimeout(5*1000);
            // 输入流
            is = con.getInputStream();
            // 1K的数据缓冲
            byte[] bs = new byte[1024];
            // 读取到的数据长度
            int len;
            // 输出的文件流
            File sf=new File(savePath);
            if(!sf.exists()){
                sf.mkdirs();
            }
            os = new FileOutputStream(sf.getPath()+System.getProperty("file.separator")+filename);
            // 开始读取
            while ((len = is.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(os!=null){
                    os.close();
                }
                if(is!=null){
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    /**
     * 
     * @param args
     * @author: lijx
     * @CreateDate: 2016-10-22 上午1:40:00
     */
    public static void main(String[] args) throws Exception {
        String url="http://114.215.210.238/resources/Mobile/hw-NxT-AL10.png";
        String path=getPhysicalPath();
        String saveName=url.substring(url.lastIndexOf("/")+1);
        System.out.println("保存名："+saveName);
        //图片保存位置
        path=path+"resource"+System.getProperty("file.separator")+"brandLogo";
        System.out.println(path);
        download(url,"hw-NxT-AL10.png",path);
        System.out.println("下载完毕");
    }
}
