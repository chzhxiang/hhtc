package com.jadyer.seed.mpp.web.model;

import com.jadyer.seed.comm.jpa.BaseEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/30 21:34.
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name="t_order_history")
public class OrderHistory extends BaseEntity<Long> {
    private static final long serialVersionUID = 8020680057027208315L;
    @Column(name="order_id")
    private long orderId;
    @Column(name="post_openid")
    private String postOpenid;
    @Column(name="owners_openid")
    private String ownersOpenid;
    @Column(name="community_id")
    private long communityId;
    @Column(name="community_name")
    private String communityName;
    @Column(name="goods_id")
    private long goodsId;
    @Column(name="car_park_number")
    private String carParkNumber;
    @Column(name="car_park_img")
    private String carParkImg;
    @Column(name="car_number")
    private String carNumber;
    private int openFromTime;
    @Column(name="open_end_time")
    private int openEndTime;
    private String appid;
    @Column(name="price")
    private BigDecimal price;

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getPostOpenid() {
        return postOpenid;
    }

    public void setPostOpenid(String postOpenid) {
        this.postOpenid = postOpenid;
    }

    public String getOwnersOpenid() {
        return ownersOpenid;
    }

    public void setOwnersOpenid(String ownersOpenid) {
        this.ownersOpenid = ownersOpenid;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
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

    public long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
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

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public int getOpenFromTime() {
        return openFromTime;
    }

    public void setOpenFromTime(int openFromTime) {
        this.openFromTime = openFromTime;
    }

    public int getOpenEndTime() {
        return openEndTime;
    }

    public void setOpenEndTime(int openEndTime) {
        this.openEndTime = openEndTime;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }


}