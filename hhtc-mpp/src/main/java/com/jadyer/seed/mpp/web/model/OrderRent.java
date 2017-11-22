package com.jadyer.seed.mpp.web.model;

import com.jadyer.seed.comm.jpa.BaseEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/8/24 20:05.
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name="t_order_rent")
public class OrderRent extends BaseEntity<Long> {
    private static final long serialVersionUID = 8020680057027208315L;
    @Column(name="community_id")
    private long communityId;
    @Column(name="order_id")
    private long orderId;
    @Column(name="order_type")
    private int orderType;
    @Column(name="order_from_date")
    private int orderFromDate;
    @Column(name="goods_openid")
    private String goodsOpenid;
    @Column(name="order_money")
    private BigDecimal orderMoney;
    @Column(name="per_money")
    private BigDecimal perMoney;
    @Column(name="other_money")
    private BigDecimal otherMoney;

    public long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(long communityId) {
        this.communityId = communityId;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public int getOrderType() {
        return orderType;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    public int getOrderFromDate() {
        return orderFromDate;
    }

    public void setOrderFromDate(int orderFromDate) {
        this.orderFromDate = orderFromDate;
    }

    public String getGoodsOpenid() {
        return goodsOpenid;
    }

    public void setGoodsOpenid(String goodsOpenid) {
        this.goodsOpenid = goodsOpenid;
    }

    public BigDecimal getOrderMoney() {
        return orderMoney;
    }

    public void setOrderMoney(BigDecimal orderMoney) {
        this.orderMoney = orderMoney;
    }

    public BigDecimal getPerMoney() {
        return perMoney;
    }

    public void setPerMoney(BigDecimal perMoney) {
        this.perMoney = perMoney;
    }

    public BigDecimal getOtherMoney() {
        return otherMoney;
    }

    public void setOtherMoney(BigDecimal otherMoney) {
        this.otherMoney = otherMoney;
    }
}