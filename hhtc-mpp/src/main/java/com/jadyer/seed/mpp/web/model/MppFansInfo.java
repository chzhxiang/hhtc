package com.jadyer.seed.mpp.web.model;

import com.jadyer.seed.comm.jpa.BaseEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name="t_mpp_fans_info")
public class MppFansInfo extends BaseEntity<Long> {
    private static final long serialVersionUID = 7585092842503110991L;
    private long uid;
    private String wxid;
    private String openid;
    private String name;
    @Column(name="id_card")
    private String idCard;
    @Column(name="phone_no")
    private String phoneNo;
    private String subscribe;
    private String nickname;
    private int sex;
    private String city;
    private String country;
    private String province;
    private String language;
    private String headimgurl;
    @Column(name="subscribe_time")
    private String subscribeTime;
    private String unionid;
    private String remark;
    private String groupid;
    @Column(name="car_owner_status")
    private int carOwnerStatus;
    @Column(name="car_owner_audit_status")
    private int carOwnerAuditStatus;
    @Column(name="car_owner_reg_time")
    private Date carOwnerRegTime;
    @Column(name="car_owner_audit_time")
    private Date carOwnerAuditTime;
    @Column(name="car_owner_audit_uid")
    private long carOwnerAuditUid;
    @Column(name="car_owner_audit_remark")
    private String carOwnerAuditRemark;
    @Column(name="car_park_status")
    private int carParkStatus;
    @Column(name="car_park_audit_status")
    private int carParkAuditStatus;
    @Column(name="car_park_reg_time")
    private Date carParkRegTime;
    @Column(name="car_park_audit_time")
    private Date carParkAuditTime;
    @Column(name="car_park_audit_uid")
    private long carParkAuditUid;
    @Column(name="car_park_audit_remark")
    private String carParkAuditRemark;
    @Column(name="car_owner_community_id")
    private long carOwnerCommunityId;
    @Column(name="car_park_community_id")
    private long carParkCommunityId;
    @Column(name="car_owner_community_name")
    private String carOwnerCommunityName;
    @Column(name="car_park_community_name")
    private String carParkCommunityName;
    @Column(name="house_number")
    private String houseNumber;
    @Column(name="car_number")
    private String carNumber;

    public long getUid() {
        return uid;
    }
    public void setUid(long uid) {
        this.uid = uid;
    }
    public String getWxid() {
        return wxid;
    }
    public void setWxid(String wxid) {
        this.wxid = wxid;
    }
    public String getOpenid() {
        return openid;
    }
    public void setOpenid(String openid) {
        this.openid = openid;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getIdCard() {
        return idCard;
    }
    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }
    public String getPhoneNo() {
        return phoneNo;
    }
    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }
    public String getSubscribe() {
        return subscribe;
    }
    public void setSubscribe(String subscribe) {
        this.subscribe = subscribe;
    }
    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public int getSex() {
        return sex;
    }
    public void setSex(int sex) {
        this.sex = sex;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public String getProvince() {
        return province;
    }
    public void setProvince(String province) {
        this.province = province;
    }
    public String getLanguage() {
        return language;
    }
    public void setLanguage(String language) {
        this.language = language;
    }
    public String getHeadimgurl() {
        return headimgurl;
    }
    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }
    public String getSubscribeTime() {
        return subscribeTime;
    }
    public void setSubscribeTime(String subscribeTime) {
        this.subscribeTime = subscribeTime;
    }
    public String getUnionid() {
        return unionid;
    }
    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }
    public String getRemark() {
        return remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }
    public String getGroupid() {
        return groupid;
    }
    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public int getCarOwnerStatus() {
        return carOwnerStatus;
    }

    public void setCarOwnerStatus(int carOwnerStatus) {
        this.carOwnerStatus = carOwnerStatus;
    }

    public int getCarOwnerAuditStatus() {
        return carOwnerAuditStatus;
    }

    public void setCarOwnerAuditStatus(int carOwnerAuditStatus) {
        this.carOwnerAuditStatus = carOwnerAuditStatus;
    }

    public Date getCarOwnerAuditTime() {
        return carOwnerAuditTime;
    }

    public void setCarOwnerAuditTime(Date carOwnerAuditTime) {
        this.carOwnerAuditTime = carOwnerAuditTime;
    }

    public int getCarParkStatus() {
        return carParkStatus;
    }

    public void setCarParkStatus(int carParkStatus) {
        this.carParkStatus = carParkStatus;
    }

    public int getCarParkAuditStatus() {
        return carParkAuditStatus;
    }

    public void setCarParkAuditStatus(int carParkAuditStatus) {
        this.carParkAuditStatus = carParkAuditStatus;
    }

    public Date getCarParkAuditTime() {
        return carParkAuditTime;
    }

    public void setCarParkAuditTime(Date carParkAuditTime) {
        this.carParkAuditTime = carParkAuditTime;
    }

    public long getCarOwnerCommunityId() {
        return carOwnerCommunityId;
    }

    public void setCarOwnerCommunityId(long carOwnerCommunityId) {
        this.carOwnerCommunityId = carOwnerCommunityId;
    }

    public long getCarParkCommunityId() {
        return carParkCommunityId;
    }

    public void setCarParkCommunityId(long carParkCommunityId) {
        this.carParkCommunityId = carParkCommunityId;
    }

    public String getCarOwnerCommunityName() {
        return carOwnerCommunityName;
    }

    public void setCarOwnerCommunityName(String carOwnerCommunityName) {
        this.carOwnerCommunityName = carOwnerCommunityName;
    }

    public String getCarParkCommunityName() {
        return carParkCommunityName;
    }

    public void setCarParkCommunityName(String carParkCommunityName) {
        this.carParkCommunityName = carParkCommunityName;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public long getCarOwnerAuditUid() {
        return carOwnerAuditUid;
    }

    public void setCarOwnerAuditUid(long carOwnerAuditUid) {
        this.carOwnerAuditUid = carOwnerAuditUid;
    }

    public long getCarParkAuditUid() {
        return carParkAuditUid;
    }

    public void setCarParkAuditUid(long carParkAuditUid) {
        this.carParkAuditUid = carParkAuditUid;
    }

    public Date getCarOwnerRegTime() {
        return carOwnerRegTime;
    }

    public void setCarOwnerRegTime(Date carOwnerRegTime) {
        this.carOwnerRegTime = carOwnerRegTime;
    }

    public Date getCarParkRegTime() {
        return carParkRegTime;
    }

    public void setCarParkRegTime(Date carParkRegTime) {
        this.carParkRegTime = carParkRegTime;
    }

    public String getCarOwnerAuditRemark() {
        return carOwnerAuditRemark;
    }

    public void setCarOwnerAuditRemark(String carOwnerAuditRemark) {
        this.carOwnerAuditRemark = carOwnerAuditRemark;
    }

    public String getCarParkAuditRemark() {
        return carParkAuditRemark;
    }

    public void setCarParkAuditRemark(String carParkAuditRemark) {
        this.carParkAuditRemark = carParkAuditRemark;
    }
}