package com.jadyer.seed.mpp.web.model;

import com.jadyer.seed.comm.jpa.BaseEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/16 15:51.
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name="t_order_infor")
public class OrderInfor extends BaseEntity<Long> {
    private static final long serialVersionUID = 8020680057027208315L;
    @Column(name="community_id")
    private long communityId;
    @Column(name="community_name")
    private String communityName;
    @Column(name="goods_id")
    private long goodsId;
    @Column(name="order_id")
    private String orderId;
    @Column(name="car_park_number")
    private String carParkNumber;
    @Column(name="car_park_img")
    private String carParkImg;
    @Column(name="car_number")
    private String carNumber;
    @Column(name="reservation_from_time")
    private String reservationFromTime;
    private String appid;
    private String body;
    private String attach;
    @Column(name="order_status")
    private int orderStatus;
    @Column(name="spbill_create_ip")
    private String spbillCreateIp;
    @Column(name="time_start")
    private String timeStart;
    @Column(name="time_expire")
    private String timeExpire;
    @Column(name="notify_url")
    private String notifyUrl;
    @Column(name="trade_type")
    private String tradeType;
    @Column(name="product_id")
    private long productId;
    @Column(name="post_openid")
    private String postOpenid;
    @Column(name="owners_openid")
    private String ownersOpenid;
    @Column(name="bank_type")
    private String bankType;
    @Column(name="cash_fee")
    private long cashFee;
    @Column(name="transaction_id")
    private String transactionId;
    @Column(name="time_end")
    private String timeEnd;
    @Column(name="trade_state_desc")
    private String tradeStateDesc;

    /**
     * 订单是否允许转租
     */
    @Transient
    private boolean allowRent;

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

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getReservationFromTime() {
        return reservationFromTime;
    }

    public void setReservationFromTime(String reservationFromTime) {
        this.reservationFromTime = reservationFromTime;
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

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public String getSpbillCreateIp() {
        return spbillCreateIp;
    }

    public void setSpbillCreateIp(String spbillCreateIp) {
        this.spbillCreateIp = spbillCreateIp;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getTimeExpire() {
        return timeExpire;
    }

    public void setTimeExpire(String timeExpire) {
        this.timeExpire = timeExpire;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getBankType() {
        return bankType;
    }

    public void setBankType(String bankType) {
        this.bankType = bankType;
    }

    public long getCashFee() {
        return cashFee;
    }

    public void setCashFee(long cashFee) {
        this.cashFee = cashFee;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getTradeStateDesc() {
        return tradeStateDesc;
    }

    public void setTradeStateDesc(String tradeStateDesc) {
        this.tradeStateDesc = tradeStateDesc;
    }


    public boolean isAllowRent() {
        return allowRent;
    }

    public void setAllowRent(boolean allowRent) {
        this.allowRent = allowRent;
    }
}