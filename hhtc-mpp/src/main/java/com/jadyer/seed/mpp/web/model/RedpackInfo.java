package com.jadyer.seed.mpp.web.model;

import com.jadyer.seed.comm.jpa.BaseEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/8/5 16:30.
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name="t_redpack_info")
public class RedpackInfo extends BaseEntity<Long> {
    private static final long serialVersionUID = 8020680057027208315L;
    @Column(name="refund_apply_id")
    private long refundApplyId;
    @Column(name="refund_apply_type")
    private int refundApplyType;
    private String appid;
    @Column(name="mch_billno")
    private String mchBillno;
    @Column(name="re_openid")
    private String reOpenid;
    @Column(name="total_amount")
    private long totalAmount;
    private int status;
    @Column(name="detail_id")
    private String detailId;
    private String reason;
    @Column(name="send_time")
    private String sendTime;
    @Column(name="refund_time")
    private String refundTime;
    @Column(name="refund_amount")
    private long refundAmount;

    public long getRefundApplyId() {
        return refundApplyId;
    }

    public void setRefundApplyId(long refundApplyId) {
        this.refundApplyId = refundApplyId;
    }

    public int getRefundApplyType() {
        return refundApplyType;
    }

    public void setRefundApplyType(int refundApplyType) {
        this.refundApplyType = refundApplyType;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getMchBillno() {
        return mchBillno;
    }

    public void setMchBillno(String mchBillno) {
        this.mchBillno = mchBillno;
    }

    public String getReOpenid() {
        return reOpenid;
    }

    public void setReOpenid(String reOpenid) {
        this.reOpenid = reOpenid;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDetailId() {
        return detailId;
    }

    public void setDetailId(String detailId) {
        this.detailId = detailId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getRefundTime() {
        return refundTime;
    }

    public void setRefundTime(String refundTime) {
        this.refundTime = refundTime;
    }

    public long getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(long refundAmount) {
        this.refundAmount = refundAmount;
    }
}