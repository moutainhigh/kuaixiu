package com.kuaixiu.sjBusiness.entity;import com.common.base.entity.BaseEntity;import com.common.util.DateUtil;/** * . * * @CreateDate: 2019-06-28 下午04:31:50 * @version: V 1.0 */public class SjReworkOrder extends BaseEntity {        /**     * 序列化Id     */    private static final long serialVersionUID = 1L;    /**     *      */    private String id ;    /**     * 报障单号     */    private String reworkOrderNo ;    /**     * 原订单id     */    private Integer orderId ;    /**     * 原订单号     */    private String orderNo ;    /**     * 状态 已创建：100，待分配:200，待施工:300，待完成:400，已完成:500，已取消:600     */    private Integer state ;    /**     * 报障产品id   ","隔开     */    private String projectIds ;    /**     * 报障备注     */    private String note ;    /**     * 接单企业id     */    private String companyId ;    /**     * 接单企业名字     */    private String companyName ;    /**     * 接单员工id     */    private String workerId ;    /**     * 接单员工名字     */    private String workerName ;    /**     * 员工接单时间     */    private java.util.Date workerTakeOrderTime ;    /**     * 顾客签字图片url     */    private String pictureUrl ;    /**     *      */    private java.util.Date endTime ;    /**     * 是否删除    1是   0否     */    private Integer isDel ;    /**     *      */    private java.util.Date createTime ;    /**     *      */    private String createUserid ;    /**     *      */    private java.util.Date updateTime ;    /**     *      */    private String updateUserid ;    private String projectName;    private String strCreateTime;    private String strUpdateTime;    private String strWorkerTakeOrderTime;    private String strEndTime;    private String nowTime;    private Integer userType;    public Integer getUserType() {        return userType;    }    public void setUserType(Integer userType) {        this.userType = userType;    }    public String getNowTime() {        return DateUtil.getNowyyyyMMddHHmmss();    }    public void setNowTime(String nowTime) {        this.nowTime = nowTime;    }    public String getStrEndTime() {        if(endTime!=null){            return DateUtil.getDateyyyyMMddHHmmss(endTime);        }else{            return "";        }    }    public void setStrEndTime(String strEndTime) {        this.strEndTime = strEndTime;    }    public String getStrWorkerTakeOrderTime() {        if(workerTakeOrderTime!=null){            return DateUtil.getDateyyyyMMddHHmmss(workerTakeOrderTime);        }else{            return "";        }    }    public void setStrWorkerTakeOrderTime(String strWorkerTakeOrderTime) {        this.strWorkerTakeOrderTime = strWorkerTakeOrderTime;    }    public String getStrCreateTime() {        return DateUtil.getDateyyyyMMddHHmmss(createTime);    }    public void setStrCreateTime(String strCreateTime) {        this.strCreateTime = strCreateTime;    }    public String getStrUpdateTime() {        if(updateTime!=null){            return DateUtil.getDateyyyyMMddHHmmss(updateTime);        }else{            return "";        }    }    public void setStrUpdateTime(String strUpdateTime) {        this.strUpdateTime = strUpdateTime;    }    public String getProjectName() {        return projectName;    }    public void setProjectName(String projectName) {        this.projectName = projectName;    }    public String getId(){        return this.id;    }    public void setId(String id){        this.id=id;    }    public String getReworkOrderNo(){        return this.reworkOrderNo;    }    public void setReworkOrderNo(String reworkOrderNo){        this.reworkOrderNo=reworkOrderNo;    }    public Integer getOrderId(){        return this.orderId;    }    public void setOrderId(Integer orderId){        this.orderId=orderId;    }    public String getOrderNo(){        return this.orderNo;    }    public void setOrderNo(String orderNo){        this.orderNo=orderNo;    }    public Integer getState(){        return this.state;    }    public void setState(Integer state){        this.state=state;    }    public String getProjectIds(){        return this.projectIds;    }    public void setProjectIds(String projectIds){        this.projectIds=projectIds;    }    public String getNote(){        return this.note;    }    public void setNote(String note){        this.note=note;    }    public String getCompanyId(){        return this.companyId;    }    public void setCompanyId(String companyId){        this.companyId=companyId;    }    public String getCompanyName(){        return this.companyName;    }    public void setCompanyName(String companyName){        this.companyName=companyName;    }    public String getWorkerId(){        return this.workerId;    }    public void setWorkerId(String workerId){        this.workerId=workerId;    }    public String getWorkerName(){        return this.workerName;    }    public void setWorkerName(String workerName){        this.workerName=workerName;    }    public java.util.Date getWorkerTakeOrderTime(){        return this.workerTakeOrderTime;    }    public void setWorkerTakeOrderTime(java.util.Date workerTakeOrderTime){        this.workerTakeOrderTime=workerTakeOrderTime;    }    public String getPictureUrl(){        return this.pictureUrl;    }    public void setPictureUrl(String pictureUrl){        this.pictureUrl=pictureUrl;    }    public java.util.Date getEndTime(){        return this.endTime;    }    public void setEndTime(java.util.Date endTime){        this.endTime=endTime;    }    public Integer getIsDel(){        return this.isDel;    }    public void setIsDel(Integer isDel){        this.isDel=isDel;    }    public java.util.Date getCreateTime(){        return this.createTime;    }    public void setCreateTime(java.util.Date createTime){        this.createTime=createTime;    }    public String getCreateUserid(){        return this.createUserid;    }    public void setCreateUserid(String createUserid){        this.createUserid=createUserid;    }    public java.util.Date getUpdateTime(){        return this.updateTime;    }    public void setUpdateTime(java.util.Date updateTime){        this.updateTime=updateTime;    }    public String getUpdateUserid(){        return this.updateUserid;    }    public void setUpdateUserid(String updateUserid){        this.updateUserid=updateUserid;    }}