package com.jadyer.seed.mpp.web.model;

import com.jadyer.seed.comm.jpa.BaseEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/17 10:35.
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name="t_goods_need_info")
public class GoodsNeedInfo extends BaseEntity<Long> {
    private static final long serialVersionUID = 5452996244293033218L;
    private String appid;
    private String openid;
    @Column(name="community_id")
    private long communityId;
    @Column(name="car_number")
    private String carNumber;
    @Column(name="need_type")
    private int needType;
    @Column(name="need_from_time")
    private int needFromTime;
    @Column(name="need_end_time")
    private int needEndTime;
    @Column(name="need_from_date")
    private int needFromDate;
    @Column(name="need_end_date")
    private int needEndDate;
    @Column(name="money_rent")
    private BigDecimal moneyRent;
    private int status;
    @Column(name="goods_publish_order_id")
    private long goodsPublishOrderId;

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

    public long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(long communityId) {
        this.communityId = communityId;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public int getNeedType() {
        return needType;
    }

    public void setNeedType(int needType) {
        this.needType = needType;
    }

    public int getNeedFromTime() {
        return needFromTime;
    }

    public void setNeedFromTime(int needFromTime) {
        this.needFromTime = needFromTime;
    }

    public int getNeedEndTime() {
        return needEndTime;
    }

    public void setNeedEndTime(int needEndTime) {
        this.needEndTime = needEndTime;
    }

    public int getNeedFromDate() {
        return needFromDate;
    }

    public void setNeedFromDate(int needFromDate) {
        this.needFromDate = needFromDate;
    }

    public int getNeedEndDate() {
        return needEndDate;
    }

    public void setNeedEndDate(int needEndDate) {
        this.needEndDate = needEndDate;
    }

    public BigDecimal getMoneyRent() {
        return moneyRent;
    }

    public void setMoneyRent(BigDecimal moneyRent) {
        this.moneyRent = moneyRent;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getGoodsPublishOrderId() {
        return goodsPublishOrderId;
    }

    public void setGoodsPublishOrderId(long goodsPublishOrderId) {
        this.goodsPublishOrderId = goodsPublishOrderId;
    }
}