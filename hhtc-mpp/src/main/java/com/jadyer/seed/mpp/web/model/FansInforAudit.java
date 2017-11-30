package com.jadyer.seed.mpp.web.model;


import com.jadyer.seed.comm.jpa.BaseEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "t_fans_infor_audit")
public class FansInforAudit extends BaseEntity<Long>{
    private static final long serialVersionUID = 238782664479331959L;
    private long uid;
    @Column(name="community_id")
    private long communityId;
    @Column(name="community_name")
    private String communityName;
    private String openid;
    private int type;
    private int state;
    private String content;
    private String imgurl1;
    private String imgurl2;

    @Transient
    private String nickname;
    @Transient
    private String headimgurl;
    @Transient
    private String phone;
    @Transient
    private String community;


    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
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
