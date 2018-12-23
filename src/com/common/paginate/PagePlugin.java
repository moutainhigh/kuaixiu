package com.common.paginate;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;

import javax.xml.bind.PropertyException;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.statement.BaseStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.ResultHandler;

import com.common.util.ReflectUtil;

/**
 * Mybatis实现物理分页的拦截器
 * 
 * @创建日期 2012-8-15下午08:58:08
 * @版本 V 1.0
 */
@Intercepts({@Signature(type = StatementHandler.class, method="prepare", args={Connection.class}),@Signature(type = StatementHandler.class, method = "query", args={Statement.class, ResultHandler.class})})
public class PagePlugin implements Interceptor {
    //数据库方言
    private static String dialect = "";    
    //mapper.xml中需要拦截的ID(正则匹配)
    private static String pageSqlId = ""; 
    
    /**
     * 拦截sql判断是否为分页查询
     */
    @SuppressWarnings("unchecked")
    public Object intercept(Invocation ivk) throws Throwable {
        
        Object result = null;
        String method = ivk.getMethod().getName();

        if(ivk.getTarget() instanceof RoutingStatementHandler){
            RoutingStatementHandler statementHandler = (RoutingStatementHandler)ivk.getTarget();
            BaseStatementHandler delegate = (BaseStatementHandler) ReflectUtil.getValueByFieldName(statementHandler, "delegate");
            MetaObject shMeta = SystemMetaObject.forObject(statementHandler);
            MappedStatement mappedStatement = (MappedStatement) ReflectUtil.getValueByFieldName(delegate, "mappedStatement");
            //拦截需要分页的SQL
            if(mappedStatement.getId().matches(pageSqlId)){ 
                BoundSql boundSql = statementHandler.getBoundSql();
                //分页SQL<select>中parameterType属性对应的实体参数，即Mapper接口中执行分页方法的参数,该参数不得为空
                Object parameterObject = boundSql.getParameterObject();
                if (parameterObject==null) {
                    throw new NullPointerException("parameterObject尚未实例化！");
                }
                else{
                    //获取分页对象
                    Page page = null;
                    //参数就是Page实体
                    if (parameterObject instanceof Page) {
                        page = (Page) parameterObject;
                    }
                    // 使用map传参
                    else if (parameterObject instanceof Map<?, ?>) {
                        page = (Page)((Map<String, Object>) parameterObject).get("page");
                    }
                    else {
                        //参数为某个实体，该实体拥有Page属性
                        Field pageField = ReflectUtil.getFieldByFieldName(parameterObject,"page");
                        if(pageField!=null){
                            page = (Page) ReflectUtil.getValueByFieldName(parameterObject,"page");
                            page = page == null ? new Page() : page;
                            //通过反射，对实体对象设置分页对象
                            ReflectUtil.setValueByFieldName(parameterObject,"page", page); 
                        }
                        else{
                            throw new NoSuchFieldException(parameterObject.getClass().getName()+"不存在 page 属性！");
                        }
                    }
                    //在 sql 预处理时修改sql
                    if (method.equals("prepare")) {
                        String sql = boundSql.getSql();
                        sql = sql.replaceFirst("SELECT", "SELECT SQL_CALC_FOUND_ROWS").replaceFirst("select", "select SQL_CALC_FOUND_ROWS");
                        //生成分页sql
                        String pageSql = generatePageSql(sql,page);
                        //将分页sql语句反射回BoundSql.
                        ReflectUtil.setValueByFieldName(boundSql, "sql", pageSql);
                        
                        shMeta.setValue("delegate.boundSql.sql", pageSql);

                        result = ivk.proceed();
                    } 
                    else if (method.equals("query")) {
                        result = ivk.proceed();
                        Statement stat = (Statement) ivk.getArgs()[0];
                        int count = this.getTotal(stat.getConnection());
                        page.setRecordsTotal(count);
                    }
                }
            }
            else {
                result = ivk.proceed();
            }
        } 
        else {
            result = ivk.proceed();
        }
        return result;
    }
    
    /**
     * 根据数据库方言，生成特定的分页sql
     * @param sql
     * @param page
     * @return
     */
    private String generatePageSql(String sql,Page page){
        if(page!=null && StringUtils.isNotEmpty(dialect)){
            StringBuffer pageSql = new StringBuffer();
            if("mysql".equals(dialect)){
                pageSql.append(sql);
                pageSql.append(" limit "+page.getStart()+","+page.getPageSize());
            }
            else if("oracle".equals(dialect)){
                pageSql.append("select * from (select tmp_tb.*,ROWNUM row_id from (");
                pageSql.append(sql);
                pageSql.append(") as tmp_tb where ROWNUM<=");
                pageSql.append(page.getStart()+page.getPageSize());
                pageSql.append(") where row_id>");
                pageSql.append(page.getStart());
            }
            return pageSql.toString();
        }
        else{
            return sql;
        }
    }
    
    /**
     * 获取总行数
     * 使用 FOUND_ROWS()函数，需要在SELECT查询中包含SQL_CALC_FOUND_ROWS选项
     * @param connection
     * @return
     */
    private int getTotal(Connection connection) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement("select found_rows()");
            ps.execute();
            rs = ps.getResultSet();
            rs.next();
            return rs.getInt(1);
        } 
        catch (SQLException e) {
            throw new RuntimeException(e);
        } 
        finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } 
            catch (SQLException e) {
            }
        }
    }
    
    /**
     * 
     */
    @Override
    public Object plugin(Object arg0) {
        return Plugin.wrap(arg0, this);
    }

    /**
     * 设置配置的参数
     */
    @Override
    public void setProperties(Properties p) {
        dialect = p.getProperty("dialect");
        if (StringUtils.isEmpty(dialect)) {
            try {
                throw new PropertyException("dialect property is not found!");
            } 
            catch (PropertyException e) {
                e.printStackTrace();
            }
        }
        pageSqlId = p.getProperty("pageSqlId");
        if (StringUtils.isEmpty(pageSqlId)) {
            try {
                throw new PropertyException("pageSqlId property is not found!");
            } 
            catch (PropertyException e) {
                e.printStackTrace();
            }
        }
    }
    
    
}
