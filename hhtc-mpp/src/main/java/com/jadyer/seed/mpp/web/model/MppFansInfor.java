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
public class MppFansInfor extends BaseEntity<Long> {
    private static final long serialVersionUID = 7585092842503110991L;
    private long uid;
    private String infor_state;
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
    @Column(name="community_id")
    private long CommunityId;
    @Column(name="community_name")
    private String CommunityName;
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

    public String getInfor_state() {
        return infor_state;
    }

    public void setInfor_state(String infor_state) {
        this.infor_state = infor_state;
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

    public void setCommunityId(long CommunityId) {
        this.CommunityId = CommunityId;
    }

    public long getCommunityId() {
        return CommunityId;
    }


    public String getCommunityName() {
        return CommunityName;
    }

    public void setCommunityName(String CommunityName) {
        this.CommunityName = CommunityName;
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

}