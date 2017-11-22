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
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/22 19:01.
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name="t_goods_publish_history")
public class GoodsPublishHistory extends BaseEntity<Long> {
    private static final long serialVersionUID = 5452996244293033218L;
    @Column(name="goods_publish_id")
    private long goodsPublishId;
    @Column(name="goods_publish_order_id")
    private long goodsPublishOrderId;
    private String openid;
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
    private BigDecimal price;
    @Column(name="publish_type")
    private int publishType;
    @Column(name="publish_from_date")
    private int publishFromDate;
    @Column(name="publish_end_date")
    private int publishEndDate;
    @Column(name="publish_from_time")
    private int publishFromTime;
    @Column(name="publish_end_time")
    private int publishEndTime;
    @Column(name="from_type")
    private int fromType;
    @Column(name="from_ids")
    private String fromIds;
    @Column(name="lock_from_date")
    private Date lockFromDate;
    @Column(name="lock_end_date")
    private Date lockEndDate;
    private int status;

    public long getGoodsPublishId() {
        return goodsPublishId;
    }

    public void setGoodsPublishId(long goodsPublishId) {
        this.goodsPublishId = goodsPublishId;
    }

    public long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(long communityId) {
        this.communityId = communityId;
    }

    public long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getCarParkImg() {
        return carParkImg;
    }

    public void setCarParkImg(String carParkImg) {
        this.carParkImg = carParkImg;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getPublishType() {
        return publishType;
    }

    public void setPublishType(int publishType) {
        this.publishType = publishType;
    }

    public int getPublishFromDate() {
        return publishFromDate;
    }

    public void setPublishFromDate(int publishFromDate) {
        this.publishFromDate = publishFromDate;
    }

    public int getPublishEndDate() {
        return publishEndDate;
    }

    public void setPublishEndDate(int publishEndDate) {
        this.publishEndDate = publishEndDate;
    }

    public int getPublishFromTime() {
        return publishFromTime;
    }

    public void setPublishFromTime(int publishFromTime) {
        this.publishFromTime = publishFromTime;
    }

    public int getPublishEndTime() {
        return publishEndTime;
    }

    public void setPublishEndTime(int publishEndTime) {
        this.publishEndTime = publishEndTime;
    }

    public int getFromType() {
        return fromType;
    }

    public void setFromType(int fromType) {
        this.fromType = fromType;
    }

    public String getFromIds() {
        return fromIds;
    }

    public void setFromIds(String fromIds) {
        this.fromIds = fromIds;
    }

    public Date getLockFromDate() {
        return lockFromDate;
    }

    public void setLockFromDate(Date lockFromDate) {
        this.lockFromDate = lockFromDate;
    }

    public Date getLockEndDate() {
        return lockEndDate;
    }

    public void setLockEndDate(Date lockEndDate) {
        this.lockEndDate = lockEndDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public String getCarParkNumber() {
        return carParkNumber;
    }

    public void setCarParkNumber(String carParkNumber) {
        this.carParkNumber = carParkNumber;
    }

    public long getGoodsPublishOrderId() {
        return goodsPublishOrderId;
    }

    public void setGoodsPublishOrderId(long goodsPublishOrderId) {
        this.goodsPublishOrderId = goodsPublishOrderId;
    }
}