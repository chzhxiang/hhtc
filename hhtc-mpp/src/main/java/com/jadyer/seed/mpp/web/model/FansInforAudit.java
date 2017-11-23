package com.jadyer.seed.mpp.web.model;


import com.jadyer.seed.comm.jpa.BaseEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "t_fans_infor_audit")
public class FansInforAudit extends BaseEntity<Long>{
    private static final long serialVersionUID = 238782664479331959L;
    private long uid;
    private int state;
    private String openid;
    private int type;
    private String content;
    private String imgurl1;
    private String imgurl2;
    private String audit_reason;
    private int audit_uid;

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImgurl1() {
        return imgurl1;
    }

    public void setImgurl1(String imgurl1) {
        this.imgurl1 = imgurl1;
    }

    public String getImgurl2() {
        return imgurl2;
    }

    public void setImgurl2(String imgurl2) {
        this.imgurl2 = imgurl2;
    }

    public String getAudit_reason() {
        return audit_reason;
    }

    public void setAudit_reason(String audit_reason) {
        this.audit_reason = audit_reason;
    }

    public int getAudit_uid() {
        return audit_uid;
    }

    public void setAudit_uid(int audit_uid) {
        this.audit_uid = audit_uid;
    }
}
