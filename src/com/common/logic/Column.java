package com.common.logic;

import java.util.Map;

/**
 * 数据表字段.
 * 
 * @CreateDate: 2016-8-25 上午1:11:08
 * @version: V 1.0
 */
public class Column {
    private String fieldName;
    private String fieldType;
    private String fieldNote;
    
    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getFieldNote() {
        return fieldNote;
    }

    public void setFieldNote(String fieldNote) {
        this.fieldNote = fieldNote;
    }

    public Column(){
        
    }
    
    public Column(Map<String,Object> map){
        
    }
}
