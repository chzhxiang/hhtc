package com.jadyer.seed.mpp.web.model;

import com.jadyer.seed.comm.jpa.BaseEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/16 14:57.
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name="t_owners_infor")
public class OwnersInfor extends BaseEntity<Long> {
    private static final long serialVersionUID = 5452996244293033218L;
    @Column(name="community_id")
    private long communityId;
    @Column(name="community_name")
    private String communityName;
    private String openid;
    @Column(name="car_number")
    private String caNumber;
    @Column(name="car_number_img")
    private String carNumberImg;
    @Column(name="car_audit_uid")
    private long carAuditUid;
    @Transient
    private String nickname;
    @Transient
    private String headimgurl;


    public String getCaNumber() {
        return caNumber;
    }

    public void setCaNumber(String caNumber) {
        this.caNumber = caNumber;
    }

    public String getCarNumberImg() {
        return carNumberImg;
    }

    public void setCarNumberImg(String carNumberImg) {
        this.carNumberImg = carNumberImg;
    }

    public long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(long communityId) {
        this.communityId = communityId;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public long getCarAuditUid() {
        return carAuditUid;
    }

    public void setCarAuditUid(long carAuditUid) {
        this.carAuditUid = carAuditUid;
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