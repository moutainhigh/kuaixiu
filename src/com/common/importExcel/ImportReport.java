package com.common.importExcel;

import java.util.ArrayList;
import java.util.List;

/**
 * 导入错误提示
 */
public class ImportReport {
    //是否继续分析
    private boolean continueNext = true;
    //整个的分析的结果是否通过
    private boolean pass = true;
    //错误信息
    private String error;
    //商品导入错误集合    
    private List<ImportError> errorList=new ArrayList<ImportError>();
    
    public boolean isContinueNext() {
        return continueNext;
    }
    public void setContinueNext(boolean continueNext) {
        this.continueNext = continueNext;
    }
    public boolean isPass() {
        return pass;
    }
    public void setPass(boolean pass) {
        this.pass = pass;
    }
    public String getError() {
        return error;
    }
    public void setError(String error) {
        this.error = error;
    }
    public List<ImportError> getErrorList() {
        return errorList;
    }
    public void setErrorList(List<ImportError> errorList) {
        this.errorList = errorList;
    }
    
}
