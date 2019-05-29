package com.kuaixiu.sjUser.entity;import com.common.base.entity.BaseEntity;import java.util.List;/** * . * * @CreateDate: 2019-05-09 下午03:44:00 * @version: V 1.0 */public class ConstructionCompany extends BaseEntity {        /**     * 序列化Id     */    private static final long serialVersionUID = 1L;    /**     *      */    private Long id ;    /**     * 登录id     */    private Integer loginId ;    /**     * 省     */    private String province ;    /**     * 市     */    private String city ;    /**     * 区域     */    private String area ;    /**     * 街道     */    private String addressDetail ;    /**     * 对接人     */    private String person ;    /**     * 对接电话     */    private String phone ;    /**     * 施工项目     */    private String project ;    /**     * 单位人数     */    private Integer personNum ;    /**     * 完成订单数     */    private Integer endOrderNum ;    /**     * 服务区域     */    private String serviceArea;    private String companyName;    private String address;    private String projectNames;    private String password;    public String getCompanyName() {        return companyName;    }    public void setCompanyName(String companyName) {        this.companyName = companyName;    }    public String getAddress() {        return address;    }    public void setAddress(String address) {        this.address = address;    }    public String getProjectNames() {        return projectNames;    }    public void setProjectNames(String projectNames) {        this.projectNames = projectNames;    }    public Long getId(){        return this.id;    }    public void setId(Long id){        this.id=id;    }    public Integer getLoginId(){        return this.loginId;    }    public void setLoginId(Integer loginId){        this.loginId=loginId;    }    public String getProvince(){        return this.province;    }    public void setProvince(String province){        this.province=province;    }    public String getCity(){        return this.city;    }    public void setCity(String city){        this.city=city;    }    public String getArea(){        return this.area;    }    public void setArea(String area){        this.area=area;    }    public String getAddressDetail() {        return addressDetail;    }    public void setAddressDetail(String addressDetail) {        this.addressDetail = addressDetail;    }    public String getPerson(){        return this.person;    }    public void setPerson(String person){        this.person=person;    }    public String getPhone(){        return this.phone;    }    public void setPhone(String phone){        this.phone=phone;    }    public String getProject(){        return this.project;    }    public void setProject(String project){        this.project=project;    }    public Integer getPersonNum(){        return this.personNum;    }    public void setPersonNum(Integer personNum){        this.personNum=personNum;    }    public Integer getEndOrderNum(){        return this.endOrderNum;    }    public void setEndOrderNum(Integer endOrderNum){        this.endOrderNum=endOrderNum;    }    public String getPassword() {        return password;    }    public void setPassword(String password) {        this.password = password;    }    public String getServiceArea() {        return serviceArea;    }    public void setServiceArea(String serviceArea) {        this.serviceArea = serviceArea;    }}