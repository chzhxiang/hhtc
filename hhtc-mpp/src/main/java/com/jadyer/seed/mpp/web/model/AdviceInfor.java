package com.jadyer.seed.mpp.web.model;

import com.jadyer.seed.comm.jpa.BaseEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/17 10:30.
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name="t_advice_infor")
public class AdviceInfor extends BaseEntity<Long> {
    private static final long serialVersionUID = 7485548095171662418L;
    private String openid;
    private String content;
    private String contentImg;
    private String result;
    @Column(name="audit_uid")
    private long auditUid;
    @Transient
    private String nickname;
    @Transient
    private String headimgurl;

    public String getContentImg() {
        return contentImg;
    }

    public void setContentImg(String contentImg) {
        this.contentImg = contentImg;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public long getAuditUid() {
        return auditUid;
    }

    public void setAuditUid(long auditUid) {
        this.auditUid = auditUid;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }
}