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
 * Created by 玄玉<http://jadyer.cn/> on 2017/8/21 18:54.
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name="t_order_inout")
public class OrderInout extends BaseEntity<Long> {
    private static final long serialVersionUID = 8020680057027208315L;
    @Column(name="community_id")
    private long communityId;
    @Column(name="max_index")
    private int maxIndex;
    @Column(name="card_no")
    private String cardNo;
    @Column(name="order_no")
    private String orderNo;
    @Column(name="car_number")
    private String carNumber;
    @Column(name="from_date")
    private int fromDate;
    @Column(name="end_date")
    private int endDate;
    @Column(name="from_time")
    private int fromTime;
    @Column(name="end_time")
    private int endTime;
    @Column(name="allow_latest_out_date")
    private Date allowLatestOutDate;
    private String openid;
    @Column(name="goods_openid")
    private String goodsOpenid;
    @Column(name="last_deduct_money")
    private BigDecimal lastDeductMoney;
    @Column(name="last_deduct_time")
    private Date lastDeductTime;
    @Column(name="next_deduct_start_time")
    private Date nextDeductStartTime;
    @Column(name="next_deduct_end_time")
    private Date nextDeductEndTime;
    @Column(name="in_time")
    private Date inTime;
    @Column(name="out_time")
    private Date outTime;

    public long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(long communityId) {
        this.communityId = communityId;
    }

    public int getMaxIndex() {
        return maxIndex;
    }

    public void setMaxIndex(int maxIndex) {
        this.maxIndex = maxIndex;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public int getFromDate() {
        return fromDate;
    }

    public void setFromDate(int fromDate) {
        this.fromDate = fromDate;
    }

    public int getEndDate() {
        return endDate;
    }

    public void setEndDate(int endDate) {
        this.endDate = endDate;
    }

    public int getFromTime() {
        return fromTime;
    }

    public void setFromTime(int fromTime) {
        this.fromTime = fromTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public Date getAllowLatestOutDate() {
        return allowLatestOutDate;
    }

    public void setAllowLatestOutDate(Date allowLatestOutDate) {
        this.allowLatestOutDate = allowLatestOutDate;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getGoodsOpenid() {
        return goodsOpenid;
    }

    public void setGoodsOpenid(String goodsOpenid) {
        this.goodsOpenid = goodsOpenid;
    }

    public BigDecimal getLastDeductMoney() {
        return lastDeductMoney;
    }

    public void setLastDeductMoney(BigDecimal lastDeductMoney) {
        this.lastDeductMoney = lastDeductMoney;
    }

    public Date getLastDeductTime() {
        return lastDeductTime;
    }

    public void setLastDeductTime(Date lastDeductTime) {
        this.lastDeductTime = lastDeductTime;
    }

    public Date getNextDeductStartTime() {
        return nextDeductStartTime;
    }

    public void setNextDeductStartTime(Date nextDeductStartTime) {
        this.nextDeductStartTime = nextDeductStartTime;
    }

    public Date getNextDeductEndTime() {
        return nextDeductEndTime;
    }

    public void setNextDeductEndTime(Date nextDeductEndTime) {
        this.nextDeductEndTime = nextDeductEndTime;
    }

    public Date getInTime() {
        return inTime;
    }

    public void setInTime(Date inTime) {
        this.inTime = inTime;
    }

    public Date getOutTime() {
        return outTime;
    }

    public void setOutTime(Date outTime) {
        this.outTime = outTime;
    }
}