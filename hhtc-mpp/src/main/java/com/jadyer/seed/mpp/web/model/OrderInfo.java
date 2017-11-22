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
@Table(name="t_order_info")
public class OrderInfo extends BaseEntity<Long> {
    private static final long serialVersionUID = 8020680057027208315L;
    @Column(name="community_id")
    private long communityId;
    @Column(name="community_name")
    private String communityName;
    @Column(name="goods_id")
    private long goodsId;
    @Column(name="goods_publish_order_ids")
    private String goodsPublishOrderIds;
    @Column(name="goods_need_id")
    private long goodsNeedId;
    @Column(name="car_park_number")
    private String carParkNumber;
    @Column(name="car_park_img")
    private String carParkImg;
    @Column(name="car_number")
    private String carNumber;
    @Column(name="open_type")
    private int openType;
    @Column(name="open_from_time")
    private int openFromTime;
    @Column(name="open_end_time")
    private int openEndTime;
    @Column(name="open_from_dates")
    private String openFromDates;
    private String appid;
    private String body;
    private String attach;
    @Column(name="out_trade_no")
    private String outTradeNo;
    @Column(name="total_fee")
    private long totalFee;
    @Column(name="deposit_money")
    private BigDecimal depositMoney;
    @Column(name="can_refund_money")
    private BigDecimal canRefundMoney;
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
    private String openid;
    @Column(name="is_subscribe")
    private String isSubscribe;
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
    @Column(name="notify_time")
    private Date notifyTime;
    @Column(name="is_notify")
    private int isNotify;
    @Column(name="order_type")
    private int orderType;
    @Column(name="order_status")
    private int orderStatus;

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

    public String getGoodsPublishOrderIds() {
        return goodsPublishOrderIds;
    }

    public void setGoodsPublishOrderIds(String goodsPublishOrderIds) {
        this.goodsPublishOrderIds = goodsPublishOrderIds;
    }

    public long getGoodsNeedId() {
        return goodsNeedId;
    }

    public void setGoodsNeedId(long goodsNeedId) {
        this.goodsNeedId = goodsNeedId;
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

    public int getOpenType() {
        return openType;
    }

    public void setOpenType(int openType) {
        this.openType = openType;
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

    public String getOpenFromDates() {
        return openFromDates;
    }

    public void setOpenFromDates(String openFromDates) {
        this.openFromDates = openFromDates;
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

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public long getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(long totalFee) {
        this.totalFee = totalFee;
    }

    public BigDecimal getDepositMoney() {
        return depositMoney;
    }

    public void setDepositMoney(BigDecimal depositMoney) {
        this.depositMoney = depositMoney;
    }

    public BigDecimal getCanRefundMoney() {
        return canRefundMoney;
    }

    public void setCanRefundMoney(BigDecimal canRefundMoney) {
        this.canRefundMoney = canRefundMoney;
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

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getIsSubscribe() {
        return isSubscribe;
    }

    public void setIsSubscribe(String isSubscribe) {
        this.isSubscribe = isSubscribe;
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

    public Date getNotifyTime() {
        return notifyTime;
    }

    public void setNotifyTime(Date notifyTime) {
        this.notifyTime = notifyTime;
    }

    public int getIsNotify() {
        return isNotify;
    }

    public void setIsNotify(int isNotify) {
        this.isNotify = isNotify;
    }

    public int getOrderType() {
        return orderType;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public boolean isAllowRent() {
        return allowRent;
    }

    public void setAllowRent(boolean allowRent) {
        this.allowRent = allowRent;
    }
}