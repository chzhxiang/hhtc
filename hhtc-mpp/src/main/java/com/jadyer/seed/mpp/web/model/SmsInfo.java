package com.jadyer.seed.mpp.web.model;

import com.jadyer.seed.comm.jpa.BaseEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/17 11:28.
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name="t_sms_info")
public class SmsInfo extends BaseEntity<Long> {
    private static final long serialVersionUID = 8020680057027208315L;
    @Column(name="phone_no")
    private String phoneNo;
    @Column(name="verify_code")
    private String verifyCode;
    private int type;
    @Column(name="is_used")
    private int isUsed;
    @Column(name="used_result")
    private int usedResult;
    @Column(name="used_time")
    private Date usedTime;
    @Column(name="time_send")
    private Date timeSend;
    @Column(name="time_expire")
    private Date timeExpire;

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getIsUsed() {
        return isUsed;
    }

    public void setIsUsed(int isUsed) {
        this.isUsed = isUsed;
    }

    public int getUsedResult() {
        return usedResult;
    }

    public void setUsedResult(int usedResult) {
        this.usedResult = usedResult;
    }

    public Date getUsedTime() {
        return usedTime;
    }

    public void setUsedTime(Date usedTime) {
        this.usedTime = usedTime;
    }

    public Date getTimeSend() {
        return timeSend;
    }

    public void setTimeSend(Date timeSend) {
        this.timeSend = timeSend;
    }

    public Date getTimeExpire() {
        return timeExpire;
    }

    public void setTimeExpire(Date timeExpire) {
        this.timeExpire = timeExpire;
    }
}