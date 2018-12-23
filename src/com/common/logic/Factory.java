package com.common.logic;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * 工厂类.
 * 
 * @author: lijx
 * @CreateDate: 2016-8-25 上午1:41:23
 * @version: V 1.0
 */
public class Factory {
    
    private DBManager dbManager = new DBManager();
    private JdbcTemplate jt = dbManager.getTemplate();

    /**
     * @param args
     */
    public static void main(String[] args) {
        Factory factory = new Factory();
        try{
            factory.generateBack();
        } 
        catch(Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 生成后台文件
     */
    public void generateBack(){
        //获取配置信息
        Map<String,Object> root = Config.getProopertyMap();


        //获取表相关信息
        String tableName=Config.getProperty("TableName");
        String showColumnSql = " SHOW FULL COLUMNS FROM " + tableName;
        List<Map<String, Object>> columnList = jt.queryForList(showColumnSql);
        try {
            List<Map<String, Object>> list = ColumnUtil.columnAddType(columnList);
            root.put("columnList", list);
        } 
        catch (Exception e1) {
            e1.printStackTrace();
        }

        //设置模板生成时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd ahh:mm:ss");
        root.put("CreateDate", sdf.format(new Date()));
        
        //自定义字段名称格式化函数
        root.put("nameFmt", new ColumnNameFormatMethod());
        
        //模板目录
        File files = new File(Config.getProperty("TemplatePath"));
        //输出路径
        File output = new File(Config.getProperty("OutPath"));
        Configuration cfg1 = new Configuration();
        try {
            cfg1.setDirectoryForTemplateLoading(files);
        } 
        catch (IOException e1) {
            e1.printStackTrace();
        }
        cfg1.setObjectWrapper(new DefaultObjectWrapper());
        //生成模板
        generateFile(files,cfg1,files,output,root);

    }

    /**
     * ASCII a 对应数字
     */
    private static final int ASCII_a_NUMBER = 97;
    
    /**
     * ASCII z 对应数字
     */
    private static final int ASCII_z_NUMBER = 122;
    
    /**
     * 根据模板和数据生成代码
     * @param file 模板目录
     * @param cfg 配置对象
     * @param temproot 模板目录
     * @param targetroot 输出路径
     * @param root 数据库链接
     * @CreateDate: 2016-8-25 上午1:51:17
     */
    private void generateFile(File file,Configuration cfg,File temproot,File targetroot,Map<String,Object> root){
        if(file.isDirectory()){
            File[] filelist = file.listFiles();
            for(int i=0;i<filelist.length;i++){
                generateFile(filelist[i],cfg,temproot,targetroot,root);
            }
        } 
        else if(file.getName().indexOf(".ftl")>-1){
            try {
                String relatePath = file.getPath().replace(temproot.getPath(), "");
                String parentPath = file.getParent().replace(temproot.getPath(), "");
                
                File targetParent = new File((targetroot.getPath()+valueParam(parentPath,root)).toLowerCase());
                if(!targetParent.exists()){
                    targetParent.mkdirs();
                }
                String tempfilename = valueParam(file.getName(),root);

//                String fileName=tempstr.equals(file.getName())?tempstr:StringUtil.capitalize(tempstr);
//                File target = new File(targetroot.getPath()+valueParam(parentPath,root)+"\\"+ fileName.replace(".ftl", ""));
//                StringUtil.capitalize(valueParam(file.getName(),root).replace(".ftl", "")));
                //如果模板文件名首字母小写 则声称文件名全部小写
                if(file.getName().charAt(0) >= ASCII_a_NUMBER && file.getName().charAt(0) <= ASCII_z_NUMBER){
                    tempfilename = tempfilename.toLowerCase();
                }
                
                File target = new File(targetroot.getPath()+valueParam(parentPath,root)+"/"+ tempfilename.replace(".ftl", ""));
                if(target.exists()){
                    target.delete();
                }
                Template temp = cfg.getTemplate(relatePath);
                Writer out = new FileWriter(target);
                try {
                    temp.process(root, out);
                } 
                catch (TemplateException e) {
                    e.printStackTrace();
                }
                out.flush();
            } 
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 将目录中的freemaker变量进行识别和替换
     */
    private String valueParam(String str,Map<String,Object> root){
        List<String> list = new ArrayList<String>();
        Pattern pattern = Pattern.compile("\\$\\{[^\\$|\\{|\\}]*\\}");
        Matcher mather = pattern.matcher(str);
        while(mather.find()){
            list.add(mather.group(0));
        }
        for(int i=0;i<list.size();i++){
            String param = list.get(i);
            String key = param.replace("${", "").replace("}", "");
            str = str.replace(param, StringUtils.capitalize(String.valueOf(root.get(key))));
        }
        return str;
    }

}
