package com.common.logic;

import java.util.List;
import java.util.Map;

/**
 * 数据表字段工具类.
 * 
 * @CreateDate: 2016-8-25 上午1:25:04
 * @version: V 1.0
 */
public class ColumnUtil {
    
    /**
     * 
     */
    public static final int DB_MYSQL = 1;
    
    
    public ColumnUtil(){
        
    }
    
    /**
     * 获取数据表字段类型
     * @param column
     * @param db_type
     * @return
     * @throws Exception
     * @CreateDate: 2016-8-25 上午1:26:14
     */
    public static String getColumnType(Map<String,Object> column,int db_type) throws Exception{
        String type = "";
        switch(db_type){
            case DB_MYSQL:
                type=getColumnFromDB_Mysql(column);
                break;
            default:
                break;
        }
        return type;
    }
    
    /**
     * 
     * @param column
     * @param db_type
     * @return
     * @throws Exception
     * @CreateDate: 2016-8-25 上午1:32:17
     */
    public static String getColumnType_Android(Map<String,Object> column,int db_type) throws Exception{
        String type = "";
        switch(db_type){
            case DB_MYSQL:
                type=getColumnFromDB_Mysql_For_Android(column);
                break;
            default:
                break;
        }
        return type;
    }
    
    /**
     * Integer 类型最大位数
     */
    private static final int INTEGER_MAX_LENGTH = 10;
    
    /**
     * Long 类型最大位数
     */
    private static final int LONG_MAX_LENGTH = 21;
    
    /**
     * 
     * @param column
     * @return
     * @throws Exception
     * @CreateDate: 2016-8-25 上午1:33:03
     */
    private static String getColumnFromDB_Mysql(Map<String,Object> column) throws Exception{
        String type=String.valueOf(column.get("Type"));
        if (type.indexOf("varchar") > -1 || type.indexOf("text") > -1) {
            type = "String";
        }
        else if (type.indexOf("date") > -1 || type.indexOf("time") > -1){
            type = "java.util.Date";
        }
        else if (type.indexOf("decimal") > -1 || type.indexOf("numeric") > -1) {
            type = "java.math.BigDecimal";
        }
        else if (type.indexOf("int") > -1) {
            int start = type.indexOf("(")+1;
            int end = type.indexOf(")");
            int size = Integer.valueOf(String.valueOf(type.substring(start,end)));
            if (size < INTEGER_MAX_LENGTH) {
                type = "Integer";
            } 
            else if (size < LONG_MAX_LENGTH) {
                type = "Long";
            } 
            else {
                throw new Exception("\""+column.get("Field")+"\"位数过大,程序可能出错,请不要超过18位");
            }
        }
        else if (type.indexOf("double") > -1 || type.indexOf("float") > -1) {
            type = "Double";
        }
        else {
            type = "String";
        }
        return type;
    }
    
    /**
     * 
     * @param column
     * @return
     * @throws Exception
     * @CreateDate: 2016-8-25 上午1:28:05
     */
    private static String getColumnFromDB_Mysql_For_Android(Map<String,Object> column) throws Exception{
        String type=String.valueOf(column.get("Type"));
        if (type.indexOf("varchar") > -1) {
            type = "varchar";
        }
        else if (type.indexOf("text") > -1) {
            type = "text";
        }
        else if(type.indexOf("datetime") > -1){
            type = "datetime";
        }
        else if (type.indexOf("decimal") > -1){
            type = "decimal";
        }
        else if(type.indexOf("int") > -1){
            type = "int";
        }
        else if(type.indexOf("bigint") > -1){
            type = "bigint";
        }
        else if (type.indexOf("binary") > -1) {
            type = "binary";
        }
        else if (type.indexOf("float") > -1) {
            type = "float";
        }
        else {
            type = "String";
        }
        
        return type;
    }
    
    /**
     * 获取数据库字段类型
     * @param columnList
     * @return
     * @throws Exception
     * @CreateDate: 2016-8-25 上午1:40:15
     */
    public static List<Map<String, Object>> columnAddType(List<Map<String, Object>> columnList) throws Exception{
        for (int i=0; i < columnList.size(); i++) {
            Map<String, Object> column = columnList.get(i);
            column.put("Jtype", getColumnType(column,DB_MYSQL));
        }
        return columnList;
    }
    
    /**
     * 获取数据库字段类型
     * @param columnList
     * @return
     * @throws Exception
     * @CreateDate: 2016-8-25 上午1:47:52
     */
    public static List<Map<String, Object>> columnAddType_Android(List<Map<String, Object>> columnList) throws Exception{
        for(int i=0;i<columnList.size();i++){
            Map<String, Object> column = columnList.get(i);
            column.put("Jtype", getColumnType_Android(column,DB_MYSQL));
        }
        return columnList;
    }
}
