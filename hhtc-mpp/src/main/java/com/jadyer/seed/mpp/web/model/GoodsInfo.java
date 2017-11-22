package com.jadyer.seed.mpp.web.model;

import com.jadyer.seed.comm.jpa.BaseEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/16 14:57.
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name="t_goods_info")
public class GoodsInfo extends BaseEntity<Long> {
    private static final long serialVersionUID = 5452996244293033218L;
    @Column(name="community_id")
    private long communityId;
    @Column(name="community_name")
    private String communityName;
    private String appid;
    private String openid;
    @Column(name="car_park_number")
    private String carParkNumber;
    @Column(name="car_park_img")
    private String carParkImg;
    @Column(name="car_equity_img")
    private String carEquityImg;
    @Column(name="car_useful_from_date")
    private int carUsefulFromDate;
    @Column(name="car_useful_end_date")
    private int carUsefulEndDate;
    @Column(name="is_used")
    private int isUsed;
    @Column(name="is_repetition")
    private int isRepetition;
    @Column(name="car_audit_status")
    private int carAuditStatus;
    @Column(name="car_audit_time")
    private Date carAuditTime;
    @Column(name="car_audit_uid")
    private long carAuditUid;
    @Column(name="car_audit_remark")
    private String carAuditRemark;
    @Transient
    private String nickname;
    @Transient
    private String headimgurl;

    public int getIsRepetition() {
        return isRepetition;
    }

    public void setIsRepetition(int isRepetition) {
        this.isRepetition = isRepetition;
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

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getCarParkNumber() {
        return carParkNumber;
    }

    public void setCarParkNumber(String carParkNumber) {
        this.carParkNumber = carParkNumber;
    }

    public String getCarParkImg() {
        return carParkImg;
    }

    public void setCarParkImg(String carParkImg) {
        this.carParkImg = carParkImg;
    }

    public String getCarEquityImg() {
        return carEquityImg;
    }

    public void setCarEquityImg(String carEquityImg) {
        this.carEquityImg = carEquityImg;
    }

    public int getCarUsefulFromDate() {
        return carUsefulFromDate;
    }

    public void setCarUsefulFromDate(int carUsefulFromDate) {
        this.carUsefulFromDate = carUsefulFromDate;
    }

    public int getCarUsefulEndDate() {
        return carUsefulEndDate;
    }

    public void setCarUsefulEndDate(int carUsefulEndDate) {
        this.carUsefulEndDate = carUsefulEndDate;
    }

    public int getIsUsed() {
        return isUsed;
    }

    public void setIsUsed(int isUsed) {
        this.isUsed = isUsed;
    }

    public int getCarAuditStatus() {
        return carAuditStatus;
    }

    public void setCarAuditStatus(int carAuditStatus) {
        this.carAuditStatus = carAuditStatus;
    }

    public Date getCarAuditTime() {
        return carAuditTime;
    }

    public void setCarAuditTime(Date carAuditTime) {
        this.carAuditTime = carAuditTime;
    }

    public long getCarAuditUid() {
        return carAuditUid;
    }

    public void setCarAuditUid(long carAuditUid) {
        this.carAuditUid = carAuditUid;
    }

    public String getCarAuditRemark() {
        return carAuditRemark;
    }

    public void setCarAuditRemark(String carAuditRemark) {
        this.carAuditRemark = carAuditRemark;
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