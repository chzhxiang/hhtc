package com.jadyer.seed.mpp.web.model;

import com.jadyer.seed.comm.jpa.BaseEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/17 10:57.
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name="t_refund_apply")
public class RefundApply extends BaseEntity<Long> {
    private static final long serialVersionUID = 8020680057027208315L;
    private String appid;
    @Column(name="client_ip")
    private String clientIp;
    private String openid;
    @Column(name="order_ids")
    private String orderIds;
    @Column(name="refund_fee")
    private long refundFee;
    @Column(name="apply_type")
    private int applyType;
    @Column(name="pay_status")
    private int payStatus;
    @Column(name="audit_status")
    private int auditStatus;
    @Column(name="audit_time")
    private Date auditTime;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getOrderIds() {
        return orderIds;
    }

    public void setOrderIds(String orderIds) {
        this.orderIds = orderIds;
    }

    public long getRefundFee() {
        return refundFee;
    }

    public void setRefundFee(long refundFee) {
        this.refundFee = refundFee;
    }

    public int getApplyType() {
        return applyType;
    }

    public void setApplyType(int applyType) {
        this.applyType = applyType;
    }

    public int getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(int payStatus) {
        this.payStatus = payStatus;
    }

    public int getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(int auditStatus) {
        this.auditStatus = auditStatus;
    }

    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }
}