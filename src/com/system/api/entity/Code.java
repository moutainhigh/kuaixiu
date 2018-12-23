package com.system.api.entity;

import com.common.base.entity.BaseEntity;

import java.util.Date;

/**
 * @Auther: anson
 * @Date: 2018/8/6
 * @Description:短信验证码
 */
public class Code extends BaseEntity {

    private static final long serialVersionUID = 5709466959509248716L;

    private String mobile;

    private String code;

    private Date inTime;

    private Date updateTime;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getInTime() {
        return inTime;
    }

    public void setInTime(Date inTime) {
        this.inTime = inTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
