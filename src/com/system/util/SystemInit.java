package com.system.util;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.web.context.ServletContextAware;

import com.alibaba.fastjson.JSONObject;
import com.common.util.FileUtil;
import com.system.api.service.ApiConfigService;
import com.system.basic.dict.service.DictService;
import com.system.constant.SystemConstant;

/**
 * 系统初始化执行.
 * 
 * @CreateDate: 2016-9-4 下午9:50:19
 * @version: V 1.0
 */
public class SystemInit implements ApplicationListener<ContextRefreshedEvent>,ServletContextAware{

    private ServletContext servletContext;
    
    @Autowired
    private ApiConfigService apiConfigService;
    @Autowired
    private DictService dictService;
    
    @Override
    public void setServletContext(ServletContext context) {
        this.servletContext = context;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        //防止重复执行
        if(event.getApplicationContext().getParent() == null){
            System.out.println("SystemInit init start ...");
            
            //数据字典到内存
            dictService.initDict();
            
            //初始化接口配置信息到内存
            apiConfigService.initApiConfig();
            
            //初始化Json验证规则
            initJsonConfig();
            System.out.println("SystemInit init end");
        }        
    }

    /**
     * 初始化Json配置文件
     * 
     * @author: lijx
     * @CreateDate: 2016-8-10 下午5:38:45
     */
    @SuppressWarnings("unchecked")
    private void initJsonConfig(){
        String filePath = SystemConstant.WEB_REAL_PATH + "WEB-INF/classes/";
        File file = new File(filePath + "goods.voliator.json");
        try {
            String content = FileUtil.readFileAsString(file);
            //System.out.println(content);
            //处理换行符
            content = content.replaceAll("\r\n", "\n");
            //匹配 // 注释
            Pattern reg = Pattern.compile("^\\s*//.*$", Pattern.MULTILINE);
            Matcher matcher = reg.matcher(content);
            content = matcher.replaceAll("");
            //保存配置
            JSONObject json = JSONObject.parseObject(content);
            Iterator<String> it = json.keySet().iterator();
            while(it.hasNext()){
                String key = it.next();
                SystemUtil.setProperty(key, json.getJSONArray(key));
            }
            //System.out.println(content);
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
