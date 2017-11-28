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
@Table(name="t_sms_infor")
public class SmsInfor extends BaseEntity<Long> {
    private static final long serialVersionUID = 8020680057027208315L;
    @Column(name="phone_no")
    private String phoneNo;
    @Column(name="verify_code")
    private String verifyCode;
    private int type;
    @Column(name="time_expire")
    private long timeExpire;

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


    public long getTimeExpire() {
        return timeExpire;
    }

    public void setTimeExpire(long timeExpire) {
        this.timeExpire = timeExpire;
    }
}