package com.common.logic;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 * 数据库管理类.
 * 
 * @CreateDate: 2016-8-25 上午1:15:01
 * @version: V 1.0
 */
public class DBManager {

    private DriverManagerDataSource source;
    private JdbcTemplate jdbcTemplate;
    /**
     * @param args
     */
    public static void main(String[] args) {

    }
    
    /**
     * 设置数据库链接属性
     * @param className 驱动名称
     * @param url 数据库链接路径
     * @param userName 用户名
     * @param password 密码
     * @author: lijx
     * @CreateDate: 2016-8-25 上午1:15:50
     */
    public void setSource(String className,String url,String userName,String password){
        source = new DriverManagerDataSource();
        source.setDriverClassName(className);
        source.setUrl(url);
        source.setUsername(userName);
        source.setPassword(password);
    }
    
    public DriverManagerDataSource getSource(){
        return this.source;
    }
    
    /**
     * 
     * @return
     * @CreateDate: 2016-8-25 上午1:18:56
     */
    public JdbcTemplate getTemplate(){
        jdbcTemplate = new JdbcTemplate(this.source);
        return this.jdbcTemplate;
    }
    
    public DBManager(){
        this.setSource(Config.getProperty("DriverClassName"), Config.getProperty("DriverUrl"), Config.getProperty("UserName"), Config.getProperty("PassWord"));
    }

}
