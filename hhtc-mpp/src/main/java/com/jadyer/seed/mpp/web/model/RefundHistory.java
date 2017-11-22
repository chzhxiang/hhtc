package com.jadyer.seed.mpp.web.model;

import com.jadyer.seed.comm.jpa.BaseEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/8/5 16:29.
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name="t_refund_history")
public class RefundHistory extends BaseEntity<Long> {
    private static final long serialVersionUID = 8020680057027208315L;
    @Column(name="refund_info_id")
    private long refundInfoId;
    @Column(name="refund_apply_id")
    private long refundApplyId;
    @Column(name="refund_apply_type")
    private int refundApplyType;
    private String openid;
    @Column(name="order_id")
    private long orderId;
    private String appid;
    @Column(name="out_refund_no")
    private String outRefundNo;
    @Column(name="total_fee")
    private long totalFee;
    @Column(name="refund_fee")
    private long refundFee;
    @Column(name="refund_desc")
    private String refundDesc;
    @Column(name="refund_accout")
    private String refundAccout;
    @Column(name="refund_status")
    private int refundStatus;
    @Column(name="refund_id")
    private String refundId;
    @Column(name="refund_channel")
    private String refundChannel;
    @Column(name="refund_recv_accout")
    private String refundRecvAccout;
    @Column(name="refund_success_time")
    private String refundSuccessTime;

    public long getRefundApplyId() {
        return refundApplyId;
    }

    public void setRefundApplyId(long refundApplyId) {
        this.refundApplyId = refundApplyId;
    }

    public long getRefundInfoId() {
        return refundInfoId;
    }

    public void setRefundInfoId(long refundInfoId) {
        this.refundInfoId = refundInfoId;
    }

    public int getRefundApplyType() {
        return refundApplyType;
    }

    public void setRefundApplyType(int refundApplyType) {
        this.refundApplyType = refundApplyType;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getOutRefundNo() {
        return outRefundNo;
    }

    public void setOutRefundNo(String outRefundNo) {
        this.outRefundNo = outRefundNo;
    }

    public long getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(long totalFee) {
        this.totalFee = totalFee;
    }

    public long getRefundFee() {
        return refundFee;
    }

    public void setRefundFee(long refundFee) {
        this.refundFee = refundFee;
    }

    public String getRefundDesc() {
        return refundDesc;
    }

    public void setRefundDesc(String refundDesc) {
        this.refundDesc = refundDesc;
    }

    public String getRefundAccout() {
        return refundAccout;
    }

    public void setRefundAccout(String refundAccout) {
        this.refundAccout = refundAccout;
    }

    public int getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(int refundStatus) {
        this.refundStatus = refundStatus;
    }

    public String getRefundId() {
        return refundId;
    }

    public void setRefundId(String refundId) {
        this.refundId = refundId;
    }

    public String getRefundChannel() {
        return refundChannel;
    }

    public void setRefundChannel(String refundChannel) {
        this.refundChannel = refundChannel;
    }

    public String getRefundRecvAccout() {
        return refundRecvAccout;
    }

    public void setRefundRecvAccout(String refundRecvAccout) {
        this.refundRecvAccout = refundRecvAccout;
    }

    public String getRefundSuccessTime() {
        return refundSuccessTime;
    }

    public void setRefundSuccessTime(String refundSuccessTime) {
        this.refundSuccessTime = refundSuccessTime;
    }
}